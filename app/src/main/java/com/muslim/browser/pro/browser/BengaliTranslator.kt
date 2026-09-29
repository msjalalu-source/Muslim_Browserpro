package com.muslim.browser.pro.browser

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Ultra-lightweight Online Bengali Live Translator for Muslim Browser Pro.
 * Supports three distinct, independent translation engines:
 * 1. LibreTranslate (Default)
 * 2. Lingva Translate
 * 3. MyMemory Translate
 *
 * Strict Architectural Rule:
 * Only the currently selected engine performs network requests.
 * There is NO automatic fallback between providers.
 * Each engine has its own isolated cache namespace.
 */
object BengaliTranslator {

    private const val TAG = "BengaliTranslator"
    const val TARGET_LANGUAGE = "bn"

    // Engine endpoint configurations (easily configurable constants)
    const val LIBRE_TRANSLATE_URL = "https://translate.disroot.org/translate"

    @Volatile
    var lingvaBaseUrl: String = "https://lingva.ml"

    @Volatile
    var myMemoryBaseUrl: String = "https://api.mymemory.translated.net"

    private const val CONNECT_TIMEOUT_MS = 7000
    private const val READ_TIMEOUT_MS = 9000

    private const val MAX_CONCURRENT_REQUESTS = 3
    private const val MAX_BATCH_CHARS = 2500
    private const val MAX_BATCH_ITEMS = 25

    // Request pacing to prevent rapid bursting
    private const val MIN_DISPATCH_INTERVAL_MS = 600L
    private val dispatchMutex = Mutex()
    private var lastDispatchTimeMs = 0L

    // In-memory engine-specific session cache: key is "${engine.name}:auto:bn:$text"
    private val memoryCache = ConcurrentHashMap<String, String>()

    // Testing override for offline unit testing without network dependency
    internal var testTranslatorOverride: ((String) -> String)? = null

    internal object TranslationStats {
        @Volatile var totalRequests = 0
        @Volatile var successfulRequests = 0
        @Volatile var failedRequests = 0
        @Volatile var http429Count = 0
        @Volatile var retryCount = 0
        val requestDurations = CopyOnWriteArrayList<Long>()

        fun reset() {
            totalRequests = 0
            successfulRequests = 0
            failedRequests = 0
            http429Count = 0
            retryCount = 0
            requestDurations.clear()
        }
    }

    fun clearCache() {
        memoryCache.clear()
        TranslationStats.reset()
    }

    private fun putInCache(key: String, value: String) {
        if (memoryCache.size > 1000) {
            val iterator = memoryCache.keys().iterator()
            var count = 0
            while (iterator.hasNext() && count < 200) {
                memoryCache.remove(iterator.next())
                count++
            }
        }
        memoryCache[key] = value
    }

    fun cacheKey(engine: TranslationEngine, text: String): String {
        return "${engine.name}:auto:$TARGET_LANGUAGE:$text"
    }

    /**
     * Ensures new HTTP requests respect minimum pacing interval.
     */
    private suspend fun awaitDispatchSlot() {
        dispatchMutex.withLock {
            val now = System.currentTimeMillis()
            val elapsed = now - lastDispatchTimeMs
            if (elapsed < MIN_DISPATCH_INTERVAL_MS) {
                val waitMs = MIN_DISPATCH_INTERVAL_MS - elapsed
                delay(waitMs)
            }
            lastDispatchTimeMs = System.currentTimeMillis()
        }
    }

    /**
     * Determines if a text should be skipped from translation.
     * Skips: empty/whitespace, digits/dates/currency/punctuation only, URLs, email addresses, single chars,
     * or text already predominantly written in Bengali script.
     */
    fun shouldSkipText(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.length < 2) return true
        if (trimmed.all { it.isDigit() || it.isWhitespace() || it in ".,$%-+/:#()[]{}|\\@!?'\"" }) return true
        if (trimmed.none { it.isLetter() }) return true
        if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.startsWith("www.", ignoreCase = true) ||
            trimmed.contains("://")
        ) return true
        if (trimmed.contains("@") && trimmed.contains(".") && !trimmed.contains(" ")) return true
        val letters = trimmed.filter { it.isLetter() }
        if (letters.isNotEmpty()) {
            val bnCount = letters.count { it in '\u0980'..'\u09FF' }
            if (bnCount.toDouble() / letters.length > 0.6) return true
        }
        return false
    }

    /**
     * Chunks unique texts into batches respecting character and item limits.
     */
    fun chunkIntoBatches(texts: List<String>): List<List<String>> {
        val batches = ArrayList<List<String>>()
        var currentBatch = ArrayList<String>()
        var currentChars = 0

        for (text in texts) {
            val len = text.length
            if (currentBatch.isNotEmpty() && (currentChars + len > MAX_BATCH_CHARS || currentBatch.size >= MAX_BATCH_ITEMS)) {
                batches.add(currentBatch)
                currentBatch = ArrayList()
                currentChars = 0
            }
            currentBatch.add(text)
            currentChars += len
        }
        if (currentBatch.isNotEmpty()) {
            batches.add(currentBatch)
        }
        return batches
    }

    /**
     * Translates a single text string to Bengali using the specified translation engine.
     */
    suspend fun translate(
        text: String,
        engine: TranslationEngine = TranslationEngine.LIBRE_TRANSLATE
    ): Result<String> = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || shouldSkipText(trimmed)) return@withContext Result.success(text)
        translateBatch(listOf(trimmed), engine).map { it.firstOrNull() ?: trimmed }
    }

    /**
     * Unified translation pipeline:
     * 1. Filters non-translatable texts (numbers, punctuation, URLs, emails, existing Bengali).
     * 2. Deduplicates texts.
     * 3. Checks engine-specific session cache.
     * 4. Dispatches untranslated phrases using ONLY the selected engine with controlled concurrency.
     * 5. No fallback across providers: if the selected engine fails, it returns failure.
     * 6. Maps results back to corresponding original DOM nodes.
     */
    suspend fun translateBatch(
        texts: List<String>,
        engine: TranslationEngine = TranslationEngine.LIBRE_TRANSLATE
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        if (texts.isEmpty()) return@withContext Result.success(emptyList())

        val translationMap = ConcurrentHashMap<String, String>()
        val neededTexts = LinkedHashSet<String>()

        for (text in texts) {
            val trimmed = text.trim()
            if (shouldSkipText(trimmed)) continue
            val key = cacheKey(engine, trimmed)
            val cached = memoryCache[key]
            if (cached != null) {
                translationMap[trimmed] = cached
            } else {
                neededTexts.add(trimmed)
            }
        }

        fun mapFinalResults(): List<String> = texts.map { text ->
            val trimmed = text.trim()
            if (shouldSkipText(trimmed)) text else translationMap[trimmed] ?: text
        }

        // If all texts were skipped or already cached, return immediately
        if (neededTexts.isEmpty()) {
            return@withContext Result.success(mapFinalResults())
        }

        // Test override for unit testing
        testTranslatorOverride?.let { override ->
            return@withContext try {
                for (needed in neededTexts) {
                    val res = override(needed)
                    val key = cacheKey(engine, needed)
                    memoryCache[key] = res
                    translationMap[needed] = res
                }
                Result.success(mapFinalResults())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        // For LibreTranslate, utilize native array batch translation (up to MAX_BATCH_ITEMS per HTTP request)
        // This drops network requests from ~50 individual calls to 2-3 batch calls, eliminating HTTP 429 rate limits.
        if (engine == TranslationEngine.LIBRE_TRANSLATE) {
            val batches = chunkIntoBatches(neededTexts.toList())
            var batchSuccessCount = 0
            var lastBatchError: Throwable? = null

            for (batch in batches) {
                val batchResult = translateBatchWithLibreTranslate(batch)
                if (batchResult.isSuccess) {
                    val translatedBatch = batchResult.getOrThrow()
                    for (i in batch.indices) {
                        val original = batch[i]
                        val translated = translatedBatch.getOrNull(i)?.trim() ?: original
                        if (translated.isNotBlank() && translated != original) {
                            val key = cacheKey(engine, original)
                            putInCache(key, translated)
                            translationMap[original] = translated
                            batchSuccessCount++
                        }
                    }
                } else {
                    lastBatchError = batchResult.exceptionOrNull()
                    Log.w(TAG, "LibreTranslate batch error: ${lastBatchError?.message}")
                    // Gracefully fallback to translating individual items within this batch using LibreTranslate only (no cross-engine fallback)
                    for (item in batch) {
                        val singleRes = translateSingleText(item, TranslationEngine.LIBRE_TRANSLATE)
                        if (singleRes.isSuccess) {
                            val translated = singleRes.getOrThrow()
                            if (translated.isNotBlank()) {
                                putInCache(cacheKey(engine, item), translated)
                                translationMap[item] = translated
                                batchSuccessCount++
                            }
                        }
                    }
                }
            }

            val totalTranslated = translationMap.count { (k, v) -> k != v }
            if (neededTexts.isNotEmpty() && batchSuccessCount == 0 && totalTranslated == 0) {
                return@withContext Result.failure(
                    IllegalStateException("Translation unavailable with LibreTranslate. Please try again.", lastBatchError)
                )
            }
            return@withContext Result.success(mapFinalResults())
        }

        // For Lingva and MyMemory, translate with controlled concurrency (MAX_CONCURRENT_REQUESTS = 3)
        val semaphore = Semaphore(MAX_CONCURRENT_REQUESTS)
        var successCount = 0
        var lastError: Throwable? = null

        coroutineScope {
            val jobs = neededTexts.map { needed ->
                async {
                    semaphore.withPermit {
                        val singleRes = translateSingleText(needed, engine)
                        if (singleRes.isSuccess) {
                            val translated = singleRes.getOrThrow()
                            if (translated.isNotBlank()) {
                                val key = cacheKey(engine, needed)
                                putInCache(key, translated)
                                translationMap[needed] = translated
                                synchronized(neededTexts) { successCount++ }
                            }
                        } else {
                            translationMap[needed] = needed
                            lastError = singleRes.exceptionOrNull()
                            Log.w(TAG, "Translation error ($engine) for '$needed': ${singleRes.exceptionOrNull()?.message}")
                        }
                    }
                }
            }
            jobs.awaitAll()
        }

        // Trigger failure if requests were needed and none succeeded and cache had nothing
        val totalTranslated = translationMap.count { (k, v) -> k != v }
        if (neededTexts.isNotEmpty() && successCount == 0 && totalTranslated == 0) {
            return@withContext Result.failure(
                IllegalStateException("Translation unavailable with ${engine.displayName}. Please try again.", lastError)
            )
        }

        Result.success(mapFinalResults())
    }

    /**
     * Dispatches text to the selected provider only. No automatic fallback between engines.
     */
    private suspend fun translateSingleText(text: String, engine: TranslationEngine): Result<String> {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || shouldSkipText(trimmed)) return Result.success(text)

        awaitDispatchSlot()
        val startMs = System.currentTimeMillis()
        TranslationStats.totalRequests++

        val result = when (engine) {
            TranslationEngine.LIBRE_TRANSLATE -> translateWithLibreTranslate(trimmed)
            TranslationEngine.LINGVA -> translateWithLingva(trimmed)
            TranslationEngine.MYMEMORY -> translateWithMyMemory(trimmed)
        }

        val dur = System.currentTimeMillis() - startMs
        TranslationStats.requestDurations.add(dur)

        if (result.isSuccess) {
            TranslationStats.successfulRequests++
        } else {
            TranslationStats.failedRequests++
        }

        return result
    }

    /**
     * Provider 1: LibreTranslate
     * Simple, direct POST request. At most 1 short retry on rate limit (429).
     * No fallback to other engines or secondary endpoints.
     */
    internal suspend fun translateWithLibreTranslate(text: String): Result<String> {
        var attempts = 0
        val maxAttempts = 2

        while (attempts < maxAttempts) {
            attempts++
            var connection: HttpURLConnection? = null
            try {
                val url = URL(LIBRE_TRANSLATE_URL)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("User-Agent", "MuslimBrowser-App/1.0 (Android)")
                    connectTimeout = CONNECT_TIMEOUT_MS
                    readTimeout = READ_TIMEOUT_MS
                    doOutput = true
                    doInput = true
                }

                val payload = JSONObject().apply {
                    put("q", text)
                    put("source", "auto")
                    put("target", TARGET_LANGUAGE)
                    put("format", "text")
                }

                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(payload.toString())
                    writer.flush()
                }

                val statusCode = connection.responseCode
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    val responseStr = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                    if (!responseStr.trimStart().startsWith("{")) {
                        return Result.failure(IllegalStateException("Non-JSON response from LibreTranslate"))
                    }
                    val jsonObj = JSONObject(responseStr)
                    val translated = jsonObj.optString("translatedText", "").trim()
                    if (translated.isNotEmpty()) {
                        return Result.success(translated)
                    } else {
                        return Result.failure(IllegalStateException("Empty translatedText from LibreTranslate"))
                    }
                } else if (statusCode == 429) {
                    TranslationStats.http429Count++
                    if (attempts < maxAttempts) {
                        TranslationStats.retryCount++
                        delay(1000L) // Short single retry pause
                        continue
                    }
                    return Result.failure(IllegalStateException("LibreTranslate rate limit (HTTP 429)"))
                } else {
                    val errorBody = connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                    return Result.failure(IllegalStateException("LibreTranslate HTTP $statusCode: $errorBody"))
                }
            } catch (e: Exception) {
                if (attempts >= maxAttempts) {
                    return Result.failure(e)
                }
                delay(600L)
            } finally {
                connection?.disconnect()
            }
        }
        return Result.failure(IllegalStateException("LibreTranslate request failed"))
    }

    /**
     * Provider 1 Batch: LibreTranslate
     * Sends native JSON array request {"q": ["...", "..."], ...} in a single HTTP POST.
     * Completes batch in ~1s instead of dozens of sequential requests, eliminating 429 rate limit.
     */
    internal suspend fun translateBatchWithLibreTranslate(texts: List<String>): Result<List<String>> {
        if (texts.isEmpty()) return Result.success(emptyList())
        if (texts.size == 1) {
            return translateWithLibreTranslate(texts.first()).map { listOf(it) }
        }

        var attempts = 0
        val maxAttempts = 2

        while (attempts < maxAttempts) {
            attempts++
            var connection: HttpURLConnection? = null
            try {
                awaitDispatchSlot()
                val url = URL(LIBRE_TRANSLATE_URL)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("User-Agent", "MuslimBrowser-App/1.0 (Android)")
                    connectTimeout = CONNECT_TIMEOUT_MS
                    readTimeout = READ_TIMEOUT_MS
                    doOutput = true
                    doInput = true
                }

                val jsonArray = JSONArray()
                for (t in texts) {
                    jsonArray.put(t)
                }

                val payload = JSONObject().apply {
                    put("q", jsonArray)
                    put("source", "auto")
                    put("target", TARGET_LANGUAGE)
                    put("format", "text")
                }

                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(payload.toString())
                    writer.flush()
                }

                val statusCode = connection.responseCode
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    val responseStr = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                    if (!responseStr.trimStart().startsWith("{")) {
                        return Result.failure(IllegalStateException("Non-JSON response from LibreTranslate"))
                    }
                    val jsonObj = JSONObject(responseStr)
                    val transArray = jsonObj.optJSONArray("translatedText")
                    if (transArray != null) {
                        val resultList = ArrayList<String>(transArray.length())
                        for (i in 0 until transArray.length()) {
                            resultList.add(transArray.getString(i))
                        }
                        return Result.success(resultList)
                    }
                    val singleTrans = jsonObj.optString("translatedText", "")
                    if (singleTrans.isNotEmpty()) {
                        return Result.success(listOf(singleTrans))
                    }
                    return Result.failure(IllegalStateException("Empty translatedText array from LibreTranslate"))
                } else if (statusCode == 429) {
                    TranslationStats.http429Count++
                    if (attempts < maxAttempts) {
                        TranslationStats.retryCount++
                        delay(1000L)
                        continue
                    }
                    return Result.failure(IllegalStateException("LibreTranslate rate limit (HTTP 429)"))
                } else {
                    val errorBody = connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                    return Result.failure(IllegalStateException("LibreTranslate HTTP $statusCode: $errorBody"))
                }
            } catch (e: Exception) {
                if (attempts >= maxAttempts) {
                    return Result.failure(e)
                }
                delay(600L)
            } finally {
                connection?.disconnect()
            }
        }
        return Result.failure(IllegalStateException("LibreTranslate batch request failed"))
    }

    /**
     * Provider 2: Lingva Translate
     * Direct GET request to REST API v1 endpoint (/api/v1/auto/bn/:query).
     * No fallback to other engines.
     */
    internal fun translateWithLingva(text: String): Result<String> {
        var connection: HttpURLConnection? = null
        try {
            val encodedQuery = URLEncoder.encode(text, "UTF-8")
            val endpointUrl = "$lingvaBaseUrl/api/v1/auto/$TARGET_LANGUAGE/$encodedQuery"
            val url = URL(endpointUrl)

            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "MuslimBrowser-App/1.0 (Android)")
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                doInput = true
            }

            val statusCode = connection.responseCode
            if (statusCode == HttpURLConnection.HTTP_OK) {
                val responseStr = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                if (!responseStr.trimStart().startsWith("{")) {
                    return Result.failure(IllegalStateException("Non-JSON response from Lingva"))
                }
                val jsonObj = JSONObject(responseStr)
                val translated = jsonObj.optString("translation", "").trim()
                if (translated.isNotEmpty()) {
                    return Result.success(translated)
                } else {
                    return Result.failure(IllegalStateException("Empty translation received from Lingva"))
                }
            } else if (statusCode == 429) {
                TranslationStats.http429Count++
                return Result.failure(IllegalStateException("Lingva Translate rate limit (HTTP 429)"))
            } else {
                val errorBody = connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                return Result.failure(IllegalStateException("Lingva Translate HTTP $statusCode: $errorBody"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Provider 3: MyMemory Translate
     * Direct GET request (/get?q=...&langpair=autodetect|bn).
     * Unescapes HTML entities and checks quota errors.
     * No fallback to other engines.
     */
    internal fun translateWithMyMemory(text: String): Result<String> {
        var connection: HttpURLConnection? = null
        try {
            val encodedQuery = URLEncoder.encode(text, "UTF-8")
            val endpointUrl = "$myMemoryBaseUrl/get?q=$encodedQuery&langpair=autodetect|$TARGET_LANGUAGE"
            val url = URL(endpointUrl)

            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "MuslimBrowser-App/1.0 (Android)")
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                doInput = true
            }

            val statusCode = connection.responseCode
            if (statusCode == HttpURLConnection.HTTP_OK) {
                val responseStr = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                if (!responseStr.trimStart().startsWith("{")) {
                    return Result.failure(IllegalStateException("Non-JSON response from MyMemory"))
                }
                val jsonObj = JSONObject(responseStr)
                val quotaFinished = jsonObj.optBoolean("quotaFinished", false)
                if (quotaFinished) {
                    return Result.failure(IllegalStateException("MyMemory quota limit exceeded"))
                }
                val responseStatus = jsonObj.optInt("responseStatus", 200)
                if (responseStatus == 403 || responseStatus == 429) {
                    return Result.failure(IllegalStateException("MyMemory quota limit exceeded (status $responseStatus)"))
                }

                val responseData = jsonObj.optJSONObject("responseData")
                val rawText = responseData?.optString("translatedText", "")?.trim() ?: ""

                // Check for MyMemory quota warning or error strings
                if (rawText.startsWith("MYMEMORY WARNING:", ignoreCase = true)) {
                    return Result.failure(IllegalStateException("MyMemory quota limit exceeded"))
                }

                if (rawText.isNotEmpty()) {
                    val cleanText = decodeHtmlEntities(rawText)
                    return Result.success(cleanText)
                } else {
                    return Result.failure(IllegalStateException("Empty translatedText from MyMemory"))
                }
            } else if (statusCode == 429) {
                TranslationStats.http429Count++
                return Result.failure(IllegalStateException("MyMemory Translate rate limit (HTTP 429)"))
            } else {
                val errorBody = connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                return Result.failure(IllegalStateException("MyMemory Translate HTTP $statusCode: $errorBody"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Decodes basic HTML entities commonly returned by MyMemory API.
     */
    fun decodeHtmlEntities(input: String): String {
        return input.replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&nbsp;", " ")
    }

    /**
     * Builds lightweight TreeWalker script to extract visible text nodes.
     */
    fun buildExtractScript(): String {
        return """
            (function() {
                try {
                    var root = document.body || document.documentElement;
                    if (!root) return JSON.stringify({error: "No body"});
                    var walker = document.createTreeWalker(
                        root,
                        NodeFilter.SHOW_TEXT,
                        {
                            acceptNode: function(node) {
                                if (!node || !node.nodeValue) return NodeFilter.FILTER_REJECT;
                                var val = node.nodeValue.trim();
                                if (val.length < 2) return NodeFilter.FILTER_REJECT;
                                var p = node.parentElement;
                                if (!p) return NodeFilter.FILTER_REJECT;
                                var t = p.tagName ? p.tagName.toUpperCase() : "";
                                if (t === 'SCRIPT' || t === 'STYLE' || t === 'NOSCRIPT' || 
                                    t === 'CODE' || t === 'PRE' || t === 'INPUT' || 
                                    t === 'TEXTAREA' || t === 'SELECT' || t === 'SVG' || 
                                    t === 'CANVAS' || t === 'AUDIO' || t === 'VIDEO') {
                                    return NodeFilter.FILTER_REJECT;
                                }
                                if (p.offsetParent === null && t !== 'BODY' && t !== 'HTML') {
                                    return NodeFilter.FILTER_REJECT;
                                }
                                return NodeFilter.FILTER_ACCEPT;
                            }
                        },
                        false
                    );
                    window.__bn_nodes = [];
                    var texts = [];
                    var n;
                    var count = 0;
                    while ((n = walker.nextNode()) && count < 250) {
                        var orig = n.nodeValue;
                        var trimmed = orig.trim();
                        if (trimmed.length > 1) {
                            window.__bn_nodes.push({node: n, orig: orig});
                            texts.push(trimmed);
                            count++;
                        }
                    }
                    return JSON.stringify({texts: texts});
                } catch(e) {
                    return JSON.stringify({error: e.toString()});
                }
            })();
        """.trimIndent()
    }

    /**
     * Builds lightweight script to replace extracted DOM text nodes with Bengali translations.
     */
    fun buildReplaceScript(translations: List<String>): String {
        val jsonArray = JSONArray(translations).toString()
        return """
            (function(trans) {
                try {
                    if (!window.__bn_nodes || !trans) return "no_nodes";
                    for (var i = 0; i < window.__bn_nodes.length && i < trans.length; i++) {
                        var item = window.__bn_nodes[i];
                        var t = trans[i];
                        if (t && item.node && item.node.parentNode) {
                            var orig = item.orig;
                            var leading = (orig.match(/^\s*/) || [""])[0];
                            var trailing = (orig.match(/\s*$/) || [""])[0];
                            item.node.nodeValue = leading + t + trailing;
                        }
                    }
                    return "success";
                } catch(e) {
                    return "error: " + e.toString();
                }
            })($jsonArray);
        """.trimIndent()
    }
}
