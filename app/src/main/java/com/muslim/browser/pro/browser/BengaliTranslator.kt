package com.muslim.browser.pro.browser

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap

/**
 * Ultra-lightweight Online Bengali Live Translator for Muslim Browser Pro.
 * Communicates directly with the LibreTranslate online endpoint.
 * Zero external translation SDKs or offline models required.
 */
object BengaliTranslator {

    private const val TAG = "BengaliTranslator"
    const val PRIMARY_TRANSLATE_URL = "https://translate.terraprint.co/translate"
    private const val FALLBACK_TRANSLATE_URL = "https://api.mymemory.translated.net/get"
    const val TARGET_LANGUAGE = "bn"

    // Timeouts: 15s connect, 30s read
    private const val CONNECT_TIMEOUT_MS = 15000
    private const val READ_TIMEOUT_MS = 30000

    // Simple in-memory session cache to avoid repeated network calls for identical texts
    private val memoryCache = ConcurrentHashMap<String, String>()

    // Testing override for offline unit testing without network dependency
    internal var testTranslatorOverride: ((String) -> String)? = null

    fun clearCache() {
        memoryCache.clear()
    }

    /**
     * Translates a single text string to Bengali.
     * Primary: LibreTranslate HTTP POST at https://translate.terraprint.co/translate
     * Fallback: High-availability online translation endpoint if primary returns 5xx/4xx
     */
    suspend fun translate(text: String): Result<String> = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return@withContext Result.success(text)

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

        // 1. Primary: LibreTranslate API request
        val primaryResult = executeLibreTranslateRequest(PRIMARY_TRANSLATE_URL, trimmed)
        if (primaryResult.isSuccess) {
            val translated = primaryResult.getOrThrow()
            memoryCache[trimmed] = translated
            return@withContext Result.success(translated)
        }

        val primaryError = primaryResult.exceptionOrNull()
        Log.w(TAG, "Primary LibreTranslate request failed: ${primaryError?.message}. Attempting fallback...")

        // 2. High-availability Fallback if primary endpoint is temporarily unavailable (e.g. 502 Bad Gateway)
        val fallbackResult = executeFallbackRequest(trimmed)
        if (fallbackResult.isSuccess) {
            val translated = fallbackResult.getOrThrow()
            memoryCache[trimmed] = translated
            return@withContext Result.success(translated)
        }

        val fallbackError = fallbackResult.exceptionOrNull()
        Log.e(TAG, "All translation attempts failed. Primary error: ${primaryError?.message}, Fallback error: ${fallbackError?.message}")

        Result.failure(IllegalStateException("Translation unavailable. Check your internet connection.", primaryError ?: fallbackError))
    }

    /**
     * Executes standard LibreTranslate POST request to the specified endpoint.
     */
    private fun executeLibreTranslateRequest(endpointUrl: String, text: String): Result<String> {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(endpointUrl)
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
            Log.d(TAG, "LibreTranslate response code: $statusCode from $endpointUrl")

            if (statusCode == HttpURLConnection.HTTP_OK) {
                val responseStr = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                Log.d(TAG, "LibreTranslate response body: $responseStr")
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
                Log.e(TAG, "LibreTranslate HTTP error $statusCode from $endpointUrl: $errorBody")
                return Result.failure(IllegalStateException("HTTP $statusCode: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "LibreTranslate exception connecting to $endpointUrl: ${e.message}", e)
            return Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Fallback online translation when primary mirror is down/502.
     */
    private fun executeFallbackRequest(text: String): Result<String> {
        var connection: HttpURLConnection? = null
        try {
            val encodedQuery = URLEncoder.encode(text, "UTF-8")
            val urlString = "$FALLBACK_TRANSLATE_URL?q=$encodedQuery&langpair=en|$TARGET_LANGUAGE"
            val url = URL(urlString)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile) MuslimBrowser/1.0")
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
            }

            val statusCode = connection.responseCode
            Log.d(TAG, "Fallback translation response code: $statusCode")

            if (statusCode == HttpURLConnection.HTTP_OK) {
                val responseStr = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val jsonObj = JSONObject(responseStr)
                val responseData = jsonObj.optJSONObject("responseData")
                val translated = responseData?.optString("translatedText", "")?.trim()
                    ?: jsonObj.optString("translatedText", "").trim()
                if (translated.isNotEmpty()) {
                    return Result.success(translated)
                } else {
                    Log.e(TAG, "Fallback translation returned empty text: $responseStr")
                    return Result.failure(IllegalStateException("Empty translatedText from fallback"))
                }
            } else {
                val errorStream = connection.errorStream ?: connection.inputStream
                val errorBody = errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                Log.e(TAG, "Fallback HTTP error $statusCode: $errorBody")
                return Result.failure(IllegalStateException("HTTP $statusCode: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fallback translation exception: ${e.message}", e)
            return Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Translates a list of DOM texts into Bengali sequentially.
     */
    suspend fun translateBatch(texts: List<String>): Result<List<String>> = withContext(Dispatchers.IO) {
        if (texts.isEmpty()) return@withContext Result.success(emptyList())

        val results = ArrayList<String>(texts.size)
        for (text in texts) {
            val trimmed = text.trim()
            if (trimmed.isEmpty()) {
                results.add(text)
                continue
            }
            val res = translate(trimmed)
            if (res.isSuccess) {
                results.add(res.getOrThrow())
            } else {
                return@withContext Result.failure(res.exceptionOrNull() ?: IllegalStateException("Translation unavailable. Check your internet connection."))
            }
        }
        Result.success(results)
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
                                    t === 'CANVAS') {
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
                    while ((n = walker.nextNode()) && count < 300) {
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
