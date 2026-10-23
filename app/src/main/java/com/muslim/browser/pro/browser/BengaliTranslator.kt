package com.muslim.browser.pro.browser

import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.LinkedHashMap
import java.util.Locale
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * High-performance, memory-conscious translation engine for Bengali translations
 * using LibreTranslate (https://translate.disroot.org/translate).
 * Ported faithfully from build 117's prebuilt jar.
 */
object BengaliTranslator {
    const val TARGET_LANGUAGE: String = "bn"
    const val LIBRE_TRANSLATE_URL: String = "https://translate.disroot.org/translate"

    private const val CONNECT_TIMEOUT_MS: Int = 5000
    private const val READ_TIMEOUT_MS: Int = 7000
    private const val MAX_BATCH_CHARS: Int = 2500
    private const val MAX_BATCH_ITEMS: Int = 25
    private const val MIN_DISPATCH_INTERVAL_MS: Long = 600L
    private const val TAG: String = "BengaliTranslator"

    private val dispatchMutex = Mutex()
    private var lastDispatchTimeMs: Long = 0L
    private val lruCache = TranslationLruCache(500)
    private val inFlightRequests = ConcurrentHashMap<String, CompletableDeferred<Result<String>>>()

    var testTranslatorOverride: ((String) -> String)? = null

    init {
        try {
            System.setProperty("http.keepAlive", "true")
            System.setProperty("http.maxConnections", "10")
        } catch (_: Exception) {}
    }

    private val NON_ASCII_WORD_REGEX = Regex("[^a-zA-Z]+")
    private val PUNCTUATION_CHARS: HashSet<Char> = hashSetOf(
        '.', ',', '$', '%', '-', '+', '/', ':', '#', '(', ')', '[', ']', '{', '}', '|', '\\', '@', '!', '?', '\'', '"'
    )

    private val ENGLISH_INDICATORS = hashSetOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "it", "for", "not", "on", "with", "he",
        "as", "you", "do", "at", "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what", "so", "up", "out",
        "if", "about", "who", "get", "which", "go", "me", "when", "make", "can", "like", "time", "no",
        "just", "him", "know", "take", "people", "into", "year", "your", "good", "some", "could", "them",
        "see", "other", "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way", "even", "new",
        "want", "because", "any", "these", "give", "day", "most", "us", "is", "are", "was", "were",
        "been", "has", "had", "am", "welcome", "news", "read", "more", "share", "home", "search",
        "settings", "menu", "page", "next", "previous", "view", "click", "here", "today", "world",
        "privacy", "terms", "policy", "login", "sign", "in", "up", "download", "save", "cancel",
        "ok", "yes", "close", "open", "help", "about", "contact", "article", "post", "comment"
    )

    class TranslationLruCache(private val maxSize: Int = 500) {
        private val map: LinkedHashMap<String, String> = object : LinkedHashMap<String, String>(maxSize, 0.75f, true) {
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

    object TranslationStats {
        @Volatile
        var totalRequests: Int = 0
        @Volatile
        var successfulRequests: Int = 0
        @Volatile
        var failedRequests: Int = 0
        @Volatile
        var http429Count: Int = 0
        @Volatile
        var retryCount: Int = 0
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
        text: String,
        sourceLang: String = "auto",
        targetLang: String = TARGET_LANGUAGE
    ): String {
        return "LIBRE_TRANSLATE:$sourceLang:$targetLang:${text.trim()}"
    }

    fun cacheKey(
        engine: TranslationEngine,
        text: String,
        sourceLang: String = "auto",
        targetLang: String = TARGET_LANGUAGE
    ): String {
        return "${engine.name}:$sourceLang:$targetLang:${text.trim()}"
    }

    fun isLikelyEnglish(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.length < 2) return false
        var hasAsciiLetter = false
        for (i in 0 until trimmed.length) {
            val c = trimmed[i]
            if (c >= '\u0080') return false
            if (c in 'a'..'z' || c in 'A'..'Z') {
                hasAsciiLetter = true
            }
        }
        if (!hasAsciiLetter) return false

        if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.startsWith("www.", ignoreCase = true) ||
            trimmed.contains("://") ||
            trimmed.contains("{") ||
            trimmed.contains("}") ||
            trimmed.contains("();") ||
            trimmed.contains("</") ||
            trimmed.contains("var ") ||
            trimmed.contains("function(")
        ) {
            return false
        }

        val words = NON_ASCII_WORD_REGEX.split(trimmed).filter { it.isNotBlank() }
        if (words.isEmpty()) return false

        for (word in words) {
            if (ENGLISH_INDICATORS.contains(word.lowercase(Locale.ROOT))) {
                return true
            }
        }

        return trimmed.contains("'s", ignoreCase = true) ||
                trimmed.contains("'t", ignoreCase = true) ||
                trimmed.contains("'re", ignoreCase = true) ||
                trimmed.contains("'ve", ignoreCase = true) ||
                trimmed.contains("'ll", ignoreCase = true)
    }

    fun isAsciiEnglish(text: String): Boolean = isLikelyEnglish(text)

    private suspend fun awaitDispatchSlot() {
        dispatchMutex.withLock {
            val now = System.currentTimeMillis()
            val elapsed = now - lastDispatchTimeMs
            if (elapsed < MIN_DISPATCH_INTERVAL_MS) {
                delay(MIN_DISPATCH_INTERVAL_MS - elapsed)
            }
            lastDispatchTimeMs = System.currentTimeMillis()
        }
    }

    fun shouldSkipText(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.length < 2) return true

        var allDigitsPunct = true
        for (i in 0 until trimmed.length) {
            val c = trimmed[i]
            if (!(c.isDigit() || c.isWhitespace() || PUNCTUATION_CHARS.contains(c))) {
                allDigitsPunct = false
                break
            }
        }
        if (allDigitsPunct) return true

        var hasLetter = false
        for (i in 0 until trimmed.length) {
            if (trimmed[i].isLetter()) {
                hasLetter = true
                break
            }
        }
        if (!hasLetter) return true

        if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.startsWith("www.", ignoreCase = true) ||
            trimmed.contains("://")
        ) {
            return true
        }

        if (trimmed.contains("@") && trimmed.contains(".") && !trimmed.contains(" ")) {
            return true
        }

        val lettersOnly = StringBuilder()
        for (i in 0 until trimmed.length) {
            val c = trimmed[i]
            if (c.isLetter()) lettersOnly.append(c)
        }
        val lettersStr = lettersOnly.toString()
        if (lettersStr.isNotEmpty()) {
            var bengaliChars = 0
            for (i in 0 until lettersStr.length) {
                val code = lettersStr[i].code
                if (code in 2432 until 2560) {
                    bengaliChars++
                }
            }
            if (bengaliChars.toDouble() / lettersStr.length.toDouble() > 0.6) {
                return true
            }
        }

        return false
    }

    fun chunkIntoBatches(texts: List<String>): List<List<String>> {
        val result = ArrayList<List<String>>()
        var currentBatch = ArrayList<String>()
        var currentChars = 0

        for (text in texts) {
            val len = text.length
            if (currentBatch.isNotEmpty() && (currentChars + len > MAX_BATCH_CHARS || currentBatch.size >= MAX_BATCH_ITEMS)) {
                result.add(currentBatch)
                currentBatch = ArrayList()
                currentChars = 0
            }
            currentBatch.add(text)
            currentChars += len
        }
        if (currentBatch.isNotEmpty()) {
            result.add(currentBatch)
        }
        return result
    }

    suspend fun translate(
        text: String,
        engine: TranslationEngine = TranslationEngine.LIBRE_TRANSLATE,
        sourceLang: String = "auto"
    ): Result<String> = translate(text, sourceLang)

    suspend fun translate(
        text: String,
        sourceLang: String = "auto"
    ): Result<String> = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || shouldSkipText(trimmed)) {
            return@withContext Result.success(text)
        }
        translateSingleTextCoalesced(trimmed, sourceLang)
    }

    suspend fun translateSingleTextCoalesced(
        text: String,
        engine: TranslationEngine,
        sourceLang: String = "auto"
    ): Result<String> = translateSingleTextCoalesced(text, sourceLang)

    suspend fun translateSingleTextCoalesced(
        text: String,
        sourceLang: String = "auto"
    ): Result<String> {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || shouldSkipText(trimmed)) {
            return Result.success(text)
        }
        val key = cacheKey(trimmed, sourceLang)
        val cached = lruCache.get(key)
        if (cached != null) {
            return Result.success(cached)
        }

        val deferred = CompletableDeferred<Result<String>>()
        val existing = inFlightRequests.putIfAbsent(key, deferred)
        if (existing != null) {
            val fastCache = lruCache.get(key)
            if (fastCache != null) return Result.success(fastCache)
            return existing.await()
        }

        val result: Result<String> = try {
            val res = translateSingleText(trimmed, sourceLang)
            if (res.isSuccess) {
                val trans = res.getOrNull()
                if (!trans.isNullOrBlank()) {
                    lruCache.put(key, trans)
                }
            }
            deferred.complete(res)
            res
        } catch (t: Throwable) {
            val failure = Result.failure<String>(t)
            deferred.complete(failure)
            if (t is CancellationException) {
                inFlightRequests.remove(key, deferred)
                throw t
            }
            failure
        } finally {
            inFlightRequests.remove(key, deferred)
        }
        return result
    }

    private suspend fun translateSingleText(
        text: String,
        sourceLang: String = "auto"
    ): Result<String> {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || shouldSkipText(trimmed)) {
            return Result.success(text)
        }

        testTranslatorOverride?.let { override ->
            return try {
                Result.success(override(trimmed))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        awaitDispatchSlot()
        val startTime = System.currentTimeMillis()
        TranslationStats.totalRequests++

        val result = translateWithLibreTranslate(trimmed)

        val duration = System.currentTimeMillis() - startTime
        if (TranslationStats.requestDurations.size < 100) {
            TranslationStats.requestDurations.add(duration)
        }

        if (result.isSuccess) {
            TranslationStats.successfulRequests++
        } else {
            TranslationStats.failedRequests++
        }

        return result
    }

    suspend fun translateBatch(
        texts: List<String>,
        engine: TranslationEngine
    ): Result<List<String>> = translateBatch(texts)

    suspend fun translateBatch(
        texts: List<String>
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        if (texts.isEmpty()) {
            return@withContext Result.success(emptyList())
        }

        val translationMap = ConcurrentHashMap<String, String>()
        val neededTexts = LinkedHashSet<String>()

        for (item in texts) {
            val trimmed = item.trim()
            if (shouldSkipText(trimmed)) continue
            val key = cacheKey(trimmed)
            val cached = lruCache.get(key)
            if (cached != null) {
                translationMap[trimmed] = cached
            } else {
                neededTexts.add(trimmed)
            }
        }

        if (neededTexts.isEmpty()) {
            return@withContext Result.success(mapFinalResults(texts, translationMap))
        }

        testTranslatorOverride?.let { override ->
            return@withContext try {
                for (needed in neededTexts) {
                    val translated = override(needed)
                    lruCache.put(cacheKey(needed), translated)
                    translationMap[needed] = translated
                }
                Result.success(mapFinalResults(texts, translationMap))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        val batches = chunkIntoBatches(neededTexts.toList())
        var successCount = 0
        var lastError: Throwable? = null

        for (batch in batches) {
            val batchRes = translateBatchWithLibreTranslate(batch)
            if (batchRes.isSuccess) {
                val transList = batchRes.getOrNull() ?: emptyList()
                for (i in batch.indices) {
                    val original = batch[i]
                    val trans = transList.getOrNull(i)?.trim() ?: original
                    if (trans.isNotBlank() && trans != original) {
                        putInCache(cacheKey(original), trans)
                        translationMap[original] = trans
                        successCount++
                    }
                }
            } else {
                lastError = batchRes.exceptionOrNull()
                Log.w(TAG, "LibreTranslate batch error: ${lastError?.message}")
                for (needed in batch) {
                    val singleRes = translateSingleTextCoalesced(needed)
                    if (singleRes.isSuccess) {
                        val singleTrans = singleRes.getOrNull()
                        if (!singleTrans.isNullOrBlank()) {
                            putInCache(cacheKey(needed), singleTrans)
                            translationMap[needed] = singleTrans
                            successCount++
                        }
                    }
                }
            }
        }

        var modifiedCount = 0
        for ((k, v) in translationMap) {
            if (k != v) modifiedCount++
        }

        if (neededTexts.isNotEmpty() && successCount == 0 && modifiedCount == 0) {
            return@withContext Result.failure(
                IllegalStateException("Translation unavailable with LibreTranslate. Please try again.", lastError)
            )
        }

        return@withContext Result.success(mapFinalResults(texts, translationMap))
    }

    private fun mapFinalResults(
        texts: List<String>,
        translationMap: ConcurrentHashMap<String, String>
    ): List<String> {
        return texts.map { text ->
            val trimmed = text.trim()
            if (!shouldSkipText(trimmed)) {
                translationMap[trimmed] ?: text
            } else {
                text
            }
        }
    }

    suspend fun translateBatchWithLibreTranslate(texts: List<String>): Result<List<String>> {
        var attempts = 0
        val maxAttempts = 2
        while (attempts < maxAttempts) {
            attempts++
            awaitDispatchSlot()
            var conn: HttpURLConnection? = null
            try {
                val url = URL(LIBRE_TRANSLATE_URL)
                conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("User-Agent", "MuslimBrowser-App/1.0 (Android)")
                    connectTimeout = CONNECT_TIMEOUT_MS
                    readTimeout = READ_TIMEOUT_MS
                    doOutput = true
                    doInput = true
                }

                val qArray = JSONArray()
                for (t in texts) {
                    qArray.put(t)
                }

                val jsonBody = JSONObject().apply {
                    put("q", qArray)
                    put("source", "auto")
                    put("target", TARGET_LANGUAGE)
                    put("format", "text")
                }

                OutputStreamWriter(conn.outputStream, StandardCharsets.UTF_8).use { writer ->
                    writer.write(jsonBody.toString())
                    writer.flush()
                }

                val code = conn.responseCode
                when (code) {
                    200 -> {
                        val responseText = BufferedReader(InputStreamReader(conn.inputStream, StandardCharsets.UTF_8)).use {
                            it.readText()
                        }
                        val trimmed = responseText.trimStart()
                        if (!trimmed.startsWith("{")) {
                            return Result.failure(IllegalStateException("Non-JSON response from LibreTranslate"))
                        }
                        val json = JSONObject(responseText)
                        val transArray = json.optJSONArray("translatedText")
                        if (transArray != null) {
                            val list = ArrayList<String>(transArray.length())
                            for (i in 0 until transArray.length()) {
                                list.add(transArray.getString(i))
                            }
                            return Result.success(list)
                        } else {
                            val single = json.optString("translatedText", "")
                            if (single.isNotEmpty()) {
                                return Result.success(listOf(single))
                            } else {
                                return Result.failure(IllegalStateException("Empty translatedText array from LibreTranslate"))
                            }
                        }
                    }
                    429 -> {
                        TranslationStats.http429Count++
                        if (attempts < maxAttempts) {
                            TranslationStats.retryCount++
                            delay(1000L)
                            continue
                        } else {
                            return Result.failure(IllegalStateException("LibreTranslate rate limit (HTTP 429)"))
                        }
                    }
                    else -> {
                        val errStream = conn.errorStream
                        val errText = errStream?.let {
                            BufferedReader(InputStreamReader(it, StandardCharsets.UTF_8)).use { r -> r.readText() }
                        } ?: ""
                        return Result.failure(IllegalStateException("LibreTranslate HTTP $code: $errText"))
                    }
                }
            } catch (e: Exception) {
                if (attempts >= maxAttempts) {
                    return Result.failure(e)
                }
                delay(1000L)
            } finally {
                conn?.disconnect()
            }
        }
        return Result.failure(IllegalStateException("LibreTranslate batch request failed"))
    }

    suspend fun translateWithLibreTranslate(text: String): Result<String> {
        var attempts = 0
        val maxAttempts = 2
        while (attempts < maxAttempts) {
            attempts++
            awaitDispatchSlot()
            var conn: HttpURLConnection? = null
            try {
                val url = URL(LIBRE_TRANSLATE_URL)
                conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("User-Agent", "MuslimBrowser-App/1.0 (Android)")
                    connectTimeout = CONNECT_TIMEOUT_MS
                    readTimeout = READ_TIMEOUT_MS
                    doOutput = true
                    doInput = true
                }

                val jsonBody = JSONObject().apply {
                    put("q", text)
                    put("source", "auto")
                    put("target", TARGET_LANGUAGE)
                    put("format", "text")
                }

                OutputStreamWriter(conn.outputStream, StandardCharsets.UTF_8).use { writer ->
                    writer.write(jsonBody.toString())
                    writer.flush()
                }

                val code = conn.responseCode
                when (code) {
                    200 -> {
                        val responseText = BufferedReader(InputStreamReader(conn.inputStream, StandardCharsets.UTF_8)).use {
                            it.readText()
                        }
                        val trimmed = responseText.trimStart()
                        if (!trimmed.startsWith("{")) {
                            return Result.failure(IllegalStateException("Non-JSON response from LibreTranslate"))
                        }
                        val json = JSONObject(responseText)
                        val trans = json.optString("translatedText", "")
                        if (trans.isNotEmpty()) {
                            return Result.success(trans)
                        } else {
                            return Result.failure(IllegalStateException("Empty translatedText from LibreTranslate"))
                        }
                    }
                    429 -> {
                        TranslationStats.http429Count++
                        if (attempts < maxAttempts) {
                            TranslationStats.retryCount++
                            delay(1000L)
                            continue
                        } else {
                            return Result.failure(IllegalStateException("LibreTranslate rate limit (HTTP 429)"))
                        }
                    }
                    else -> {
                        val errStream = conn.errorStream
                        val errText = errStream?.let {
                            BufferedReader(InputStreamReader(it, StandardCharsets.UTF_8)).use { r -> r.readText() }
                        } ?: ""
                        return Result.failure(IllegalStateException("LibreTranslate HTTP $code: $errText"))
                    }
                }
            } catch (e: Exception) {
                if (attempts >= maxAttempts) {
                    return Result.failure(e)
                }
                delay(1000L)
            } finally {
                conn?.disconnect()
            }
        }
        return Result.failure(IllegalStateException("LibreTranslate request failed"))
    }

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

    fun buildExtractScript(): String {
        return """(function() {
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
})();"""
    }

    fun buildReplaceScript(translations: List<String>): String {
        val json = JSONArray(translations).toString()
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
            })($json);
        """.trimIndent()
    }
}
