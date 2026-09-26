package com.muslim.browser.pro.browser

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/**
 * Ultra-lightweight, High-speed Online Bengali Live Translator for Muslim Browser Pro.
 * Uses ONLY LibreTranslate at https://translate.terraprint.co/translate.
 * Features smart DOM text filtering, deduplication, and batch processing for maximum speed.
 * Zero external translation SDKs or offline models required.
 */
object BengaliTranslator {

    private const val TAG = "BengaliTranslator"
    const val PRIMARY_TRANSLATE_URL = "https://translate.terraprint.co/translate"
    const val TARGET_LANGUAGE = "bn"

    // Optimized network timeouts to avoid long waiting times
    private const val CONNECT_TIMEOUT_MS = 10000
    private const val READ_TIMEOUT_MS = 15000

    // Batching configuration: reasonable character & item limits per request
    private const val MAX_BATCH_CHARS = 2500
    private const val MAX_BATCH_ITEMS = 25

    // In-memory cache for the session to prevent duplicate network calls
    private val memoryCache = ConcurrentHashMap<String, String>()

    // Testing override for offline unit testing without network dependency
    internal var testTranslatorOverride: ((String) -> String)? = null

    fun clearCache() {
        memoryCache.clear()
    }

    /**
     * Determines if a text should be skipped from translation.
     * Skips: empty/whitespace, digits only, punctuation only, URLs, email addresses, single chars.
     */
    fun shouldSkipText(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.length < 2) return true

        // Digits, dates, numbers, currency or punctuation only (e.g., "123", "2026", "$100", "99.9%")
        if (trimmed.all { it.isDigit() || it.isWhitespace() || it in ".,$%-+/:#()[]{}|\\@!?'\"" }) return true

        // No letters present (pure symbols/formatting)
        if (trimmed.none { it.isLetter() }) return true

        // URLs
        if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.startsWith("www.", ignoreCase = true) ||
            trimmed.contains("://")
        ) return true

        // Email addresses
        if (trimmed.contains("@") && trimmed.contains(".") && !trimmed.contains(" ")) return true

        return false
    }

    /**
     * Chunks a list of unique texts into batches respecting character and item limits.
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
     * Translates a single text string to Bengali using LibreTranslate.
     */
    suspend fun translate(text: String): Result<String> = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || shouldSkipText(trimmed)) return@withContext Result.success(text)

        val cached = memoryCache[trimmed]
        if (cached != null) {
            return@withContext Result.success(cached)
        }

        testTranslatorOverride?.let { override ->
            return@withContext try {
                val res = override(trimmed)
                memoryCache[trimmed] = res
                Result.success(res)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        val result = executeLibreTranslateRequest(trimmed)
        if (result.isSuccess) {
            val translated = result.getOrThrow()
            memoryCache[trimmed] = translated
            Result.success(translated)
        } else {
            val error = result.exceptionOrNull()
            Log.e(TAG, "LibreTranslate translation failed for '$trimmed': ${error?.message}")
            Result.failure(IllegalStateException("Translation unavailable. Please try again.", error))
        }
    }

    /**
     * Highly-optimized batch translation:
     * 1. Filters out non-translatable texts (numbers, punctuation, URLs, emails).
     * 2. Deduplicates texts (each unique phrase translated only once).
     * 3. Combines unique phrases into batches (2000-2500 chars per batch).
     * 4. Sends a minimal number of network requests to LibreTranslate.
     * 5. Splits and maps results back to all corresponding DOM nodes.
     */
    suspend fun translateBatch(texts: List<String>): Result<List<String>> = withContext(Dispatchers.IO) {
        if (texts.isEmpty()) return@withContext Result.success(emptyList())

        val translationMap = HashMap<String, String>()
        val neededTexts = LinkedHashSet<String>()

        for (text in texts) {
            val trimmed = text.trim()
            if (shouldSkipText(trimmed)) continue
            val cached = memoryCache[trimmed]
            if (cached != null) {
                translationMap[trimmed] = cached
            } else {
                neededTexts.add(trimmed)
            }
        }

        // If all texts were skipped or already cached, return immediately with zero network requests
        if (neededTexts.isEmpty()) {
            val instantResults = texts.map { text ->
                val trimmed = text.trim()
                if (shouldSkipText(trimmed)) text else translationMap[trimmed] ?: text
            }
            return@withContext Result.success(instantResults)
        }

        // Test override for local unit testing
        testTranslatorOverride?.let { override ->
            try {
                for (needed in neededTexts) {
                    val res = override(needed)
                    memoryCache[needed] = res
                    translationMap[needed] = res
                }
                val results = texts.map { text ->
                    val trimmed = text.trim()
                    if (shouldSkipText(trimmed)) text else translationMap[trimmed] ?: text
                }
                return@withContext Result.success(results)
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

        // Chunk needed unique texts into batches
        val batches = chunkIntoBatches(neededTexts.toList())
        Log.d(TAG, "Translating ${texts.size} DOM nodes: ${neededTexts.size} unique texts in ${batches.size} batch(es)")

        for (batch in batches) {
            val chunkResult = translateBatchChunk(batch)
            if (chunkResult.isSuccess) {
                val batchMap = chunkResult.getOrThrow()
                for ((orig, trans) in batchMap) {
                    translationMap[orig] = trans
                    memoryCache[orig] = trans
                }
            } else {
                val err = chunkResult.exceptionOrNull()
                Log.e(TAG, "Batch chunk translation failed: ${err?.message}")
                return@withContext Result.failure(
                    IllegalStateException("Translation unavailable. Please try again.", err)
                )
            }
        }

        // Map translated results back to every original text node
        val finalResults = texts.map { text ->
            val trimmed = text.trim()
            if (shouldSkipText(trimmed)) {
                text
            } else {
                translationMap[trimmed] ?: memoryCache[trimmed] ?: text
            }
        }

        Result.success(finalResults)
    }

    /**
     * Translates a single batch of unique texts in a single HTTP request.
     */
    private fun translateBatchChunk(batch: List<String>): Result<Map<String, String>> {
        if (batch.isEmpty()) return Result.success(emptyMap())

        if (batch.size == 1) {
            val single = batch[0]
            val singleRes = executeLibreTranslateRequest(single)
            return if (singleRes.isSuccess) {
                Result.success(mapOf(single to singleRes.getOrThrow()))
            } else {
                Result.failure(singleRes.exceptionOrNull() ?: IllegalStateException("Single translation failed"))
            }
        }

        // Combine texts with newline delimiter
        val combinedText = batch.joinToString("\n") { it.replace('\n', ' ').trim() }
        val reqResult = executeLibreTranslateRequest(combinedText)

        if (reqResult.isSuccess) {
            val translatedResponse = reqResult.getOrThrow()
            val lines = translatedResponse.split("\n")
            val resultMap = HashMap<String, String>()

            if (lines.size == batch.size) {
                for (i in batch.indices) {
                    resultMap[batch[i]] = lines[i].trim()
                }
            } else {
                // If line counts slightly diverge, map what we can safely
                for (i in batch.indices) {
                    val line = if (i < lines.size) lines[i].trim() else batch[i]
                    resultMap[batch[i]] = if (line.isNotBlank()) line else batch[i]
                }
            }
            return Result.success(resultMap)
        } else {
            return Result.failure(reqResult.exceptionOrNull() ?: IllegalStateException("Batch request failed"))
        }
    }

    /**
     * Executes LibreTranslate POST request to https://translate.terraprint.co/translate.
     */
    private fun executeLibreTranslateRequest(text: String): Result<String> {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(PRIMARY_TRANSLATE_URL)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile) MuslimBrowser/1.0")
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
            Log.d(TAG, "LibreTranslate response code: $statusCode from $PRIMARY_TRANSLATE_URL")

            if (statusCode == HttpURLConnection.HTTP_OK) {
                val responseStr = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val jsonObj = JSONObject(responseStr)
                val translated = jsonObj.optString("translatedText", "").trim()
                if (translated.isNotEmpty()) {
                    return Result.success(translated)
                } else {
                    Log.e(TAG, "LibreTranslate returned empty translatedText in JSON: $responseStr")
                    return Result.failure(IllegalStateException("Empty translatedText received from LibreTranslate"))
                }
            } else {
                val errorStream = connection.errorStream ?: connection.inputStream
                val errorBody = errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                Log.e(TAG, "LibreTranslate HTTP error $statusCode from $PRIMARY_TRANSLATE_URL: $errorBody")
                return Result.failure(IllegalStateException("HTTP $statusCode: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "LibreTranslate exception connecting to $PRIMARY_TRANSLATE_URL: ${e.message}", e)
            return Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Builds lightweight TreeWalker script to extract visible text nodes.
     * Skips non-content elements and hidden DOM elements.
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
