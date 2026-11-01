package com.muslim.browser.pro.browser

import android.util.Log
import kotlinx.coroutines.CompletableDeferred
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
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.LinkedHashMap
import java.util.Locale
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * High-performance, memory-conscious translation engine for Bengali translations
 * using the Google Gemini API.
 */
object BengaliTranslator {
    const val TARGET_LANGUAGE: String = "bn"
    const val DEFAULT_MODEL: String = "gemini-flash-latest"

    private const val CONNECT_TIMEOUT_MS: Int = 5000
    private const val READ_TIMEOUT_MS: Int = 15000
    private const val MAX_BATCH_CHARS: Int = 4000
    private const val MAX_BATCH_ITEMS: Int = 40
    private const val MIN_DISPATCH_INTERVAL_MS: Long = 200L
    private const val TAG: String = "BengaliTranslator"

    open class TranslationApiException(
        val userMessage: String,
        cause: Throwable? = null
    ) : IllegalStateException(userMessage, cause)

    class SplittablePayloadException(
        message: String,
        cause: Throwable? = null
    ) : IllegalStateException(message, cause)

    internal class RateLimitException(message: String) : Exception(message)

    fun isSplittableError(error: Throwable?): Boolean {
        return error is SplittablePayloadException
    }

    private fun isServerDownError(error: Throwable?): Boolean {
        if (error is TranslationApiException) return true
        if (error is java.io.IOException) return true
        val msg = error?.message?.lowercase(Locale.ROOT) ?: ""
        return msg.contains("timeout") || msg.contains("connect") || msg.contains("unknownhost") || msg.contains("http 5") || msg.contains("502") || msg.contains("bad gateway")
    }

    private val dispatchMutex = Mutex()
    private var lastDispatchTimeMs: Long = 0L
    private val lruCache = TranslationLruCache(1000)
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

    fun putInCache(key: String, value: String) {
        lruCache.put(key, value)
    }

    fun cacheKey(
        text: String,
        sourceLang: String = "auto",
        targetLang: String = TARGET_LANGUAGE
    ): String {
        return "GEMINI:$sourceLang:$targetLang:${text.trim()}"
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

        val words = trimmed.split(NON_ASCII_WORD_REGEX).filter { it.length >= 2 }
        if (words.isEmpty()) return false
        val matchCount = words.count { ENGLISH_INDICATORS.contains(it.lowercase(Locale.ROOT)) }
        return matchCount > 0 || words.size <= 2
    }

    fun shouldSkipText(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.length <= 1) return true

        var allDigitsOrPunct = true
        for (i in 0 until trimmed.length) {
            val c = trimmed[i]
            if (!c.isDigit() && !PUNCTUATION_CHARS.contains(c) && !c.isWhitespace()) {
                allDigitsOrPunct = false
                break
            }
        }
        if (allDigitsOrPunct) return true

        if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.contains("@") && trimmed.contains(".")
        ) {
            return true
        }

        var bengaliCharCount = 0
        for (i in 0 until trimmed.length) {
            val code = trimmed[i].code
            if (code in 0x0980..0x09FF) {
                bengaliCharCount++
            }
        }
        if (bengaliCharCount.toDouble() / trimmed.length.toDouble() > 0.60) {
            return true
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
        apiKey: String = "",
        model: String = DEFAULT_MODEL,
        sourceLang: String = "auto"
    ): Result<String> = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || shouldSkipText(trimmed)) {
            return@withContext Result.success(text)
        }
        translateSingleTextCoalesced(trimmed, apiKey, model, sourceLang)
    }

    suspend fun translateSingleTextCoalesced(
        text: String,
        apiKey: String = "",
        model: String = DEFAULT_MODEL,
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
            val res = translateSingleText(trimmed, apiKey, model, sourceLang)
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
        apiKey: String = "",
        model: String = DEFAULT_MODEL,
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

        val startTime = System.currentTimeMillis()
        TranslationStats.totalRequests++

        val batchRes = translateBatchWithGemini(listOf(trimmed), apiKey, model)

        val duration = System.currentTimeMillis() - startTime
        if (TranslationStats.requestDurations.size < 100) {
            TranslationStats.requestDurations.add(duration)
        }

        if (batchRes.isSuccess) {
            TranslationStats.successfulRequests++
            val translated = batchRes.getOrNull()?.firstOrNull() ?: trimmed
            return Result.success(translated)
        } else {
            TranslationStats.failedRequests++
            return Result.failure(batchRes.exceptionOrNull() ?: TranslationApiException("ইন্টারনেট বা সার্ভার সমস্যা"))
        }
    }

    suspend fun translateBatch(
        texts: List<String>,
        apiKey: String = "",
        model: String = DEFAULT_MODEL,
        onProgress: (suspend (List<String>) -> Unit)? = null
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        if (texts.isEmpty()) {
            return@withContext Result.success(emptyList())
        }

        val translationMap = ConcurrentHashMap<String, String>()
        val neededTexts = LinkedHashSet<String>()
        var cachedCount = 0

        for (item in texts) {
            val trimmed = item.trim()
            if (shouldSkipText(trimmed)) continue
            val key = cacheKey(trimmed)
            val cached = lruCache.get(key)
            if (cached != null) {
                translationMap[trimmed] = cached
                cachedCount++
            } else {
                neededTexts.add(trimmed)
            }
        }

        if (cachedCount > 0 && onProgress != null) {
            onProgress.invoke(mapFinalResults(texts, translationMap))
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
                val finalRes = mapFinalResults(texts, translationMap)
                onProgress?.invoke(finalRes)
                Result.success(finalRes)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        val batches = chunkIntoBatches(neededTexts.toList())
        val semaphore = Semaphore(3)
        val serverDownFlag = AtomicBoolean(false)

        val batchResults = withTimeoutOrNull(18000L) {
            coroutineScope {
                batches.map { batch ->
                    async {
                        semaphore.withPermit {
                            if (serverDownFlag.get()) {
                                return@withPermit Pair(0, TranslationApiException("ইন্টারনেট বা সার্ভার সমস্যা"))
                            }
                            val res = processBatchWithFallback(batch, translationMap, apiKey, model, serverDownFlag)
                            if (res.first > 0 && onProgress != null) {
                                onProgress.invoke(mapFinalResults(texts, translationMap))
                            }
                            res
                        }
                    }
                }.awaitAll()
            }
        }

        var successCount = 0
        var lastError: Throwable? = null
        if (batchResults != null) {
            for ((s, err) in batchResults) {
                successCount += s
                if (err != null) {
                    lastError = err
                }
            }
        }

        var modifiedCount = 0
        for ((k, v) in translationMap) {
            if (k != v) modifiedCount++
        }

        if (neededTexts.isNotEmpty() && successCount == 0 && modifiedCount == 0) {
            val failureException = if (lastError is TranslationApiException) {
                lastError
            } else {
                TranslationApiException("ইন্টারনেট বা সার্ভার সমস্যা", lastError)
            }
            return@withContext Result.failure(failureException)
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

    private suspend fun processBatchWithFallback(
        batch: List<String>,
        translationMap: ConcurrentHashMap<String, String>,
        apiKey: String,
        model: String,
        serverDownFlag: AtomicBoolean? = null
    ): Pair<Int, Throwable?> {
        if (batch.isEmpty()) return Pair(0, null)
        if (serverDownFlag?.get() == true) {
            return Pair(0, TranslationApiException("ইন্টারনেট বা সার্ভার সমস্যা"))
        }

        val batchRes = translateBatchWithGemini(batch, apiKey, model)
        if (batchRes.isSuccess) {
            val transList = batchRes.getOrNull() ?: emptyList()
            var successes = 0
            for (i in batch.indices) {
                val original = batch[i]
                val trans = transList.getOrNull(i)?.trim() ?: original
                if (trans.isNotBlank() && trans != original) {
                    putInCache(cacheKey(original), trans)
                    translationMap[original] = trans
                    successes++
                }
            }
            return Pair(successes, null)
        }

        val error = batchRes.exceptionOrNull()

        if (isServerDownError(error)) {
            serverDownFlag?.set(true)
            return Pair(0, error)
        }

        if (!isSplittableError(error)) {
            return Pair(0, error)
        }

        if (batch.size > 1) {
            val mid = batch.size / 2
            val (s1, e1) = processBatchWithFallback(batch.subList(0, mid), translationMap, apiKey, model, serverDownFlag)
            val (s2, e2) = processBatchWithFallback(batch.subList(mid, batch.size), translationMap, apiKey, model, serverDownFlag)
            return Pair(s1 + s2, e1 ?: e2 ?: error)
        } else {
            return Pair(0, error)
        }
    }

    suspend fun translateBatchWithGemini(
        texts: List<String>,
        apiKey: String = "",
        model: String = DEFAULT_MODEL
    ): Result<List<String>> {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isEmpty()) {
            return Result.failure(TranslationApiException("API Key ভুল বা অনুমতি নেই"))
        }

        val effectiveModel = model.trim().ifEmpty { DEFAULT_MODEL }
        val targetUrl = "https://generativelanguage.googleapis.com/v1beta/models/$effectiveModel:generateContent"

        var attempts = 0
        val maxAttempts = 2

        while (attempts < maxAttempts) {
            attempts++
            awaitDispatchSlot()
            val result = executeGeminiRequest(
                endpointUrl = targetUrl,
                apiKey = trimmedKey,
                texts = texts,
                includeThinkingConfig = true
            )

            if (result.isSuccess) {
                return result
            }

            val err = result.exceptionOrNull()
            if (err is RateLimitException && attempts < maxAttempts) {
                TranslationStats.http429Count++
                TranslationStats.retryCount++
                delay(700L)
                continue
            }

            return result
        }
        return Result.failure(TranslationApiException("ব্যবহারের সীমা শেষ, পরে চেষ্টা করুন"))
    }

    private fun executeGeminiRequest(
        endpointUrl: String,
        apiKey: String,
        texts: List<String>,
        includeThinkingConfig: Boolean
    ): Result<List<String>> {
        var conn: HttpURLConnection? = null
        try {
            val url = URL(endpointUrl)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("x-goog-api-key", apiKey)
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "MuslimBrowser-App/1.0 (Android)")
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                doOutput = true
                doInput = true
            }

            val requestBody = buildRequestBody(texts, includeThinkingConfig)
            val postBytes = requestBody.toString().toByteArray(StandardCharsets.UTF_8)
            conn.setFixedLengthStreamingMode(postBytes.size)

            conn.outputStream.use { os ->
                os.write(postBytes)
                os.flush()
            }

            val code = conn.responseCode
            when (code) {
                200 -> {
                    val responseText = BufferedReader(InputStreamReader(conn.inputStream, StandardCharsets.UTF_8)).use {
                        it.readText()
                    }
                    return parseGeminiResponse(responseText, texts.size)
                }
                400 -> {
                    val errStream = conn.errorStream
                    val errText = errStream?.let {
                        BufferedReader(InputStreamReader(it, StandardCharsets.UTF_8)).use { r -> r.readText() }
                    } ?: ""
                    val errLower = errText.lowercase(Locale.ROOT)
                    if (includeThinkingConfig && errLower.contains("thinking")) {
                        return executeGeminiRequest(
                            endpointUrl = endpointUrl,
                            apiKey = apiKey,
                            texts = texts,
                            includeThinkingConfig = false
                        )
                    }
                    if (errLower.contains("api_key") || errLower.contains("api key") || errLower.contains("key not valid") || errLower.contains("invalid api key")) {
                        return Result.failure(TranslationApiException("API Key ভুল বা অনুমতি নেই"))
                    }
                    return Result.failure(TranslationApiException("API Key ভুল বা অনুমতি নেই"))
                }
                401, 403 -> {
                    return Result.failure(TranslationApiException("API Key ভুল বা অনুমতি নেই"))
                }
                404 -> {
                    return Result.failure(TranslationApiException("মডেলের নাম ভুল"))
                }
                429 -> {
                    return Result.failure(RateLimitException("Rate limit 429"))
                }
                in 500..599 -> {
                    return Result.failure(TranslationApiException("ইন্টারনেট বা সার্ভার সমস্যা"))
                }
                else -> {
                    return Result.failure(TranslationApiException("ইন্টারনেট বা সার্ভার সমস্যা"))
                }
            }
        } catch (e: Exception) {
            if (e is TranslationApiException || e is SplittablePayloadException || e is RateLimitException) {
                return Result.failure(e)
            }
            return Result.failure(TranslationApiException("ইন্টারনেট বা সার্ভার সমস্যা", e))
        } finally {
            conn?.disconnect()
        }
    }

    fun buildRequestBody(texts: List<String>, includeThinkingConfig: Boolean): JSONObject {
        val sysPart = JSONObject().put(
            "text",
            "Translate every string in the given JSON array from English to Bengali (bn). Return ONLY a JSON array of strings with exactly the same length and order. Keep numbers, URLs, email addresses, code, brand names and HTML entities unchanged."
        )
        val sysInstruction = JSONObject().put("parts", JSONArray().put(sysPart))

        val textsArray = JSONArray()
        for (t in texts) {
            textsArray.put(t)
        }
        val contentPart = JSONObject().put("text", textsArray.toString())
        val contentObj = JSONObject().put("parts", JSONArray().put(contentPart))
        val contentsArray = JSONArray().put(contentObj)

        val genConfig = JSONObject().apply {
            put("temperature", 0)
            put("responseMimeType", "application/json")
            put("responseSchema", JSONObject().apply {
                put("type", "ARRAY")
                put("items", JSONObject().apply {
                    put("type", "STRING")
                })
            })
            if (includeThinkingConfig) {
                put("thinkingConfig", JSONObject().apply {
                    put("thinkingBudget", 0)
                })
            }
        }

        return JSONObject().apply {
            put("systemInstruction", sysInstruction)
            put("contents", contentsArray)
            put("generationConfig", genConfig)
        }
    }

    fun parseGeminiResponse(responseText: String, expectedCount: Int): Result<List<String>> {
        try {
            val json = JSONObject(responseText)
            val candidates = json.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return Result.failure(TranslationApiException("ইন্টারনেট বা সার্ভার সমস্যা"))
            }
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.optJSONObject(0)?.optString("text") ?: ""

            var cleanedText = rawText.trim()
            if (cleanedText.startsWith("```json")) {
                cleanedText = cleanedText.removePrefix("```json").removeSuffix("```").trim()
            } else if (cleanedText.startsWith("```")) {
                cleanedText = cleanedText.removePrefix("```").removeSuffix("```").trim()
            }

            val parsedArray = JSONArray(cleanedText)
            val list = ArrayList<String>(parsedArray.length())
            for (i in 0 until parsedArray.length()) {
                list.add(parsedArray.getString(i))
            }

            if (list.size != expectedCount) {
                return Result.failure(
                    SplittablePayloadException("Response length (${list.size}) does not match input length ($expectedCount)")
                )
            }
            return Result.success(list)
        } catch (e: Exception) {
            return Result.failure(
                SplittablePayloadException("Failed to parse Gemini translation array: ${e.message}", e)
            )
        }
    }

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
