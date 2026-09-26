package com.muslim.browser.pro.browser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/**
 * Ultra-lightweight Online Bengali Live Translator for Muslim Browser Pro.
 * Communicates directly with the LibreTranslate online endpoint.
 * Zero external translation SDKs or offline models required.
 */
object BengaliTranslator {

    private const val TRANSLATE_URL = "https://translate.terraprint.co/translate"
    const val TARGET_LANGUAGE = "bn"

    // Simple session cache to avoid repeated network calls for identical texts
    private val memoryCache = ConcurrentHashMap<String, String>()

    // Testing override for offline unit testing without network dependency
    internal var testTranslatorOverride: ((String) -> String)? = null

    fun clearCache() {
        memoryCache.clear()
    }

    /**
     * Translates a single text string to Bengali using LibreTranslate API.
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

        var connection: HttpURLConnection? = null
        try {
            val url = URL(TRANSLATE_URL)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 7000
                readTimeout = 7000
                doOutput = true
            }

            val payload = JSONObject().apply {
                put("q", trimmed)
                put("source", "auto")
                put("target", TARGET_LANGUAGE)
                put("format", "text")
            }

            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(payload.toString())
                writer.flush()
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseStr = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val jsonObj = JSONObject(responseStr)
                val translated = jsonObj.optString("translatedText", "")
                if (translated.isNotEmpty()) {
                    memoryCache[trimmed] = translated
                    Result.success(translated)
                } else {
                    Result.failure(IllegalStateException("Translation unavailable. Check your internet connection."))
                }
            } else {
                Result.failure(IllegalStateException("Translation unavailable. Check your internet connection."))
            }
        } catch (e: Exception) {
            Result.failure(IllegalStateException("Translation unavailable. Check your internet connection.", e))
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
                    while ((n = walker.nextNode()) && count < 400) {
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
