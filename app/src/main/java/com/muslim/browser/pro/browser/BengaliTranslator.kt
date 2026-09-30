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
 * Supports two distinct, independent translation engines:
 * 1. LibreTranslate (Default)
 * 2. MyMemory Translate
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
    var myMemoryBaseUrl: String = "https://api.mymemory.translated.net"

    private const val CONNECT_TIMEOUT_MS = 5000
    private const val READ_TIMEOUT_MS = 7000

    private const val MAX_CONCURRENT_REQUESTS = 4
    private const val MAX_BATCH_CHARS = 2500
    private const val MAX_BATCH_ITEMS = 25

    // Request pacing to prevent rapid bursting on rate-limited engines (LibreTranslate)
    private const val MIN_DISPATCH_INTERVAL_MS = 600L
    private val dispatchMutex = Mutex()
    private var lastDispatchTimeMs = 0L

    /**
     * Bounded, thread-safe LRU cache for recently translated text entries.
     * Prevents unbounded memory growth while offering instant 0ms retrieval for repeated translations.
     */
    class TranslationLruCache(private val maxSize: Int = 500) {
        private val map = object : LinkedHashMap<String, String>(maxSize, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?): Boolean {
                return size > maxSize
            }
        }

        @Synchronized
        fun get(key: String): String? = map[key]

        @Synchronized
        fun put(key: String, value: String) {
            map[key] = value
        }

        @Synchronized
        fun clear() {
            map.clear()
        }

        @Synchronized
        fun size(): Int = map.size
    }

    private val lruCache = TranslationLruCache(500)

    // In-flight request deduplication map: coalesces concurrent identical translations into a single network call
    private val inFlightRequests = ConcurrentHashMap<String, kotlinx.coroutines.CompletableDeferred<Result<String>>>()

    init {
        try {
            System.setProperty("http.keepAlive", "true")
            System.setProperty("http.maxConnections", "10")
        } catch (_: Exception) {}
    }

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
        lruCache.clear()
        inFlightRequests.clear()
        TranslationStats.reset()
    }

    fun getCacheSize(): Int = lruCache.size()

    fun putInCache(key: String, value: String) {
        lruCache.put(key, value)
    }

    fun getFromCache(key: String): String? = lruCache.get(key)

    fun cacheKey(
        engine: TranslationEngine,
        text: String,
        sourceLang: String = "auto",
        targetLang: String = TARGET_LANGUAGE
    ): String {
        val normalized = text.trim()
        return "${engine.name}:$sourceLang:$targetLang:$normalized"
    }

    // Common English indicator words (stopwords and high-frequency UI/web terms)
    private val ENGLISH_INDICATORS = hashSetOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "it", "for", "not", "on", "with",
        "he", "as", "you", "do", "at", "this", "but", "his", "by", "from", "they", "we", "say", "her",
        "she", "or", "an", "will", "my", "one", "all", "would", "there", "their", "what", "so", "up",
        "out", "if", "about", "who", "get", "which", "go", "me", "when", "make", "can", "like", "time",
        "no", "just", "him", "know", "take", "people", "into", "year", "your", "good", "some", "could",
        "them", "see", "other", "than", "then", "now", "look", "only", "come", "its", "over", "think",
        "also", "back", "after", "use", "two", "how", "our", "work", "first", "well", "way", "even",
        "new", "want", "because", "any", "these", "give", "day", "most", "us", "is", "are", "was",
        "were", "been", "has", "had", "am", "welcome", "news", "read", "more", "share", "home",
        "search", "settings", "menu", "page", "next", "previous", "view", "click", "here", "today",
        "world", "privacy", "terms", "policy", "login", "sign", "in", "up", "download", "save", "cancel",
        "ok", "yes", "close", "open", "help", "about", "contact", "article", "post", "comment"
    )

    /**
     * Determines with high confidence whether text is English.
     * Disqualifies non-ASCII (Bengali, Arabic, Hindi, CJK, accented European text),
     * code/syntax artifacts, URLs, and unaccented non-English phrases.
     * Falls back to server-side autodetection when uncertain.
     */
    internal fun isLikelyEnglish(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.length < 2) return false

        // 1. Any non-ASCII character immediately disqualifies (Bengali, Arabic, Hindi, CJK, accented chars)
        var hasLetter = false
        for (ch in trimmed) {
            if (ch.code >= 128) return false
            if (ch in 'a'..'z' || ch in 'A'..'Z') hasLetter = true
        }
        if (!hasLetter) return false

        // 2. Reject URLs and code/markup indicators
        if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.startsWith("www.", ignoreCase = true) ||
            trimmed.contains("://") ||
            trimmed.contains("{") || trimmed.contains("}") ||
            trimmed.contains("();") || trimmed.contains("</") ||
            trimmed.contains("var ") || trimmed.contains("function(")
        ) {
            return false
        }

        // 3. Extract word tokens
        val words = trimmed.split(Regex("[^a-zA-Z]+")).filter { it.isNotBlank() }
        if (words.isEmpty()) return false

        // 4. Check if at least one token is a known English word/indicator
        for (w in words) {
            if (ENGLISH_INDICATORS.contains(w.lowercase())) {
                return true
            }
        }

        // 5. Check typical English grammar contractions ('s, 't, 're, 've, 'll, 'd)
        if (trimmed.contains("'s", ignoreCase = true) ||
            trimmed.contains("'t", ignoreCase = true) ||
            trimmed.contains("'re", ignoreCase = true) ||
            trimmed.contains("'ve", ignoreCase = true) ||
            trimmed.contains("'ll", ignoreCase = true)
        ) {
            return true
        }

        return false
    }

    /**
     * Fast check to detect ASCII English phrases, kept for backward compatibility.
     */
    internal fun isAsciiEnglish(text: String): Boolean = isLikelyEnglish(text)

    /**
     * Resolves the language pair for MyMemory requests.
     * Uses "en|bn" for confident English text for lowest server latency; defaults to "autodetect|bn" otherwise.
     */
    internal fun resolveLanguagePair(text: String, sourceLang: String? = null): String {
        if (!sourceLang.isNullOrBlank() && sourceLang != "auto") {
            return "$sourceLang|$TARGET_LANGUAGE"
        }
        return if (isLikelyEnglish(text)) "en|$TARGET_LANGUAGE" else "autodetect|$TARGET_LANGUAGE"
    }

    /**
     * Ensures new HTTP requests respect minimum pacing interval for rate-limited engines.
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
     * Takes advantage of in-memory LRU cache and in-flight request deduplication.
     */
    suspend fun translate(
        text: String,
        engine: TranslationEngine = TranslationEngine.LIBRE_TRANSLATE,
        sourceLang: String = "auto"
    ): Result<String> = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || shouldSkipText(trimmed)) return@withContext Result.success(text)
        translateSingleTextCoalesced(trimmed, engine, sourceLang)
    }

    /**
     * Translates a single text using bounded LRU cache and concurrent in-flight request deduplication.
     * If the identical translation is already executing, concurrent callers share the single network response.
     */
    suspend fun translateSingleTextCoalesced(
        text: String,
        engine: TranslationEngine,
        sourceLang: String = "auto"
    ): Result<String> {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || shouldSkipText(trimmed)) return Result.success(text)

        val key = cacheKey(engine, trimmed, sourceLang)
        // 1. Fast path: return immediately from LRU cache if already translated
        lruCache.get(key)?.let { return Result.success(it) }

        // 2. Coalesce concurrent identical requests lock-free
        val newDeferred = kotlinx.coroutines.CompletableDeferred<Result<String>>()
        val existing = inFlightRequests.putIfAbsent(key, newDeferred)
        if (existing != null) {
            // Re-check cache in case the existing request completed just before putIfAbsent
            lruCache.get(key)?.let { return Result.success(it) }
            return existing.await()
        }

        // We are the initiator of this in-flight request
        try {
            val result = translateSingleText(trimmed, engine, sourceLang)
            if (result.isSuccess) {
                val translated = result.getOrThrow()
                if (translated.isNotBlank()) {
                    lruCache.put(key, translated)
                }
            }
            newDeferred.complete(result)
            return result
        } catch (e: Throwable) {
            val failure = Result.failure<String>(e)
            newDeferred.complete(failure)
            if (e is kotlinx.coroutines.CancellationException) {
                throw e
            }
            return failure
        } finally {
            inFlightRequests.remove(key, newDeferred)
        }
    }

    /**
     * Unified translation pipeline:
     * 1. Filters non-translatable texts (numbers, punctuation, URLs, emails, existing Bengali).
     * 2. Deduplicates texts.
     * 3. Checks engine-specific LRU session cache.
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
            val cached = lruCache.get(key)
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
                    lruCache.put(key, res)
                    translationMap[needed] = res
                }
                Result.success(mapFinalResults())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        // For LibreTranslate, utilize native array batch translation (up to MAX_BATCH_ITEMS per HTTP request)
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
                    // Gracefully fallback to translating individual items within this batch using LibreTranslate only
                    for (item in batch) {
                        val singleRes = translateSingleTextCoalesced(item, TranslationEngine.LIBRE_TRANSLATE)
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

        // For MyMemory, translate with controlled concurrency (MAX_CONCURRENT_REQUESTS = 4)
        val semaphore = Semaphore(MAX_CONCURRENT_REQUESTS)
        var successCount = 0
        var lastError: Throwable? = null

        coroutineScope {
            val jobs = neededTexts.map { needed ->
                async {
                    semaphore.withPermit {
                        val singleRes = translateSingleTextCoalesced(needed, engine)
                        if (singleRes.isSuccess) {
                            val translated = singleRes.getOrThrow()
                            if (translated.isNotBlank()) {
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
    private suspend fun translateSingleText(
        text: String,
        engine: TranslationEngine,
        sourceLang: String = "auto"
    ): Result<String> {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || shouldSkipText(trimmed)) return Result.success(text)

        testTranslatorOverride?.let { override ->
            return try {
                Result.success(override(trimmed))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        // Only rate-limited engines (LibreTranslate) require artificial pacing
        if (engine == TranslationEngine.LIBRE_TRANSLATE) {
            awaitDispatchSlot()
        }
        val startMs = System.currentTimeMillis()
        TranslationStats.totalRequests++

        val result = when (engine) {
            TranslationEngine.LIBRE_TRANSLATE -> translateWithLibreTranslate(trimmed)
            TranslationEngine.MYMEMORY -> translateWithMyMemory(trimmed, sourceLang)
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
     * Provider 2: MyMemory Translate
     * Direct GET request (/get?q=...&langpair=...).
     * Employs HTTP Keep-Alive connection reuse, language pair optimization, and lightweight parsing.
     * No fallback to other engines.
     */
    internal fun translateWithMyMemory(text: String, sourceLang: String? = null): Result<String> {
        var connection: HttpURLConnection? = null
        try {
            val encodedQuery = URLEncoder.encode(text, "UTF-8")
            val langPair = resolveLanguagePair(text, sourceLang)
            val endpointUrl = "$myMemoryBaseUrl/get?q=$encodedQuery&langpair=$langPair"
            val url = URL(endpointUrl)

            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "MuslimBrowser-App/1.0 (Android)")
                setRequestProperty("Connection", "keep-alive")
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                doInput = true
            }

            val statusCode = connection.responseCode
            val isSuccess = statusCode == HttpURLConnection.HTTP_OK
            val stream = if (isSuccess) connection.inputStream else connection.errorStream
            val responseStr = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""

            if (isSuccess) {
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
                    connection.disconnect()
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
                connection.disconnect()
                return Result.failure(IllegalStateException("MyMemory Translate rate limit (HTTP 429)"))
            } else {
                connection.disconnect()
                return Result.failure(IllegalStateException("MyMemory Translate HTTP $statusCode: $responseStr"))
            }
        } catch (e: Exception) {
            connection?.disconnect()
            return Result.failure(e)
        }
        // Connection is left open for HTTP Keep-Alive connection pooling on subsequent requests
    }

    /**
     * Decodes basic HTML entities commonly returned by MyMemory API.
     * Skips processing if no '&' entity marker exists to eliminate allocations.
     */
    fun decodeHtmlEntities(input: String): String {
        if (!input.contains('&')) return input
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
