package com.muslim.browser.pro.browser

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

enum class TranslationMode {
    MT,    // MT Translation (Google ML Kit On-device)
    LIVE   // Live Translation (LibreTranslate Online)
}

/**
 * Lightweight In-Place Translation Engine for Muslim Browser Pro.
 * Translates DOM text nodes directly to Bengali ("bn") without modifying webpage URLs.
 * Strict Mode Separation:
 * - MT selected -> Uses only ML Kit On-device translation; never calls Live API.
 * - Live selected -> Uses only LibreTranslate API; never calls ML Kit.
 */
object TranslationManager {

    const val TARGET_LANGUAGE_CODE = "bn"
    const val TARGET_LANGUAGE_NAME = "বাংলা"

    // LibreTranslate public endpoint (can be configured or changed)
    var libreTranslateEndpoint: String = "https://translate.terraprint.co/translate"

    // In-memory cache to prevent redundant translations
    private val memoryCache = ConcurrentHashMap<String, String>()

    // Optional override translators for unit testing / decoupling
    var mtTranslatorOverride: ((String) -> String)? = null
    var liveTranslatorOverride: ((String) -> String)? = null

    // Tracking for test verification
    var lastUsedEngine: TranslationMode? = null
    var mtCallCount: Int = 0
    var liveCallCount: Int = 0

    private var mlKitTranslator: Translator? = null

    @Synchronized
    private fun getOrCreateMlKitTranslator(): Translator {
        val current = mlKitTranslator
        if (current != null) return current
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.BENGALI)
            .build()
        val created = Translation.getClient(options)
        mlKitTranslator = created
        return created
    }

    fun clearCache() {
        memoryCache.clear()
        lastUsedEngine = null
        mtCallCount = 0
        liveCallCount = 0
    }

    /**
     * Translates a single text chunk to Bengali based strictly on the selected mode.
     */
    suspend fun translateText(text: String, mode: TranslationMode, context: Context? = null): Result<String> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return Result.success(text)

        // Check in-memory cache
        val cached = memoryCache[trimmed]
        if (cached != null) {
            return Result.success(cached)
        }

        return when (mode) {
            TranslationMode.MT -> translateWithMt(trimmed, context)
            TranslationMode.LIVE -> translateWithLive(trimmed)
        }
    }

    /**
     * Translates a batch of text nodes into Bengali according to selected mode.
     */
    suspend fun translateBatch(
        texts: List<String>,
        mode: TranslationMode,
        context: Context? = null
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        if (texts.isEmpty()) return@withContext Result.success(emptyList())

        lastUsedEngine = mode
        val results = ArrayList<String>(texts.size)

        // Pre-check for MT model if using MT mode without test override
        if (mode == TranslationMode.MT && mtTranslatorOverride == null) {
            try {
                val translator = getOrCreateMlKitTranslator()
                val conditions = DownloadConditions.Builder().build()
                Tasks.await(translator.downloadModelIfNeeded(conditions))
            } catch (e: Exception) {
                return@withContext Result.failure(
                    IllegalStateException("Translation unavailable\nPlease download the Bengali translation model.")
                )
            }
        }

        for (text in texts) {
            val trimmed = text.trim()
            if (trimmed.isEmpty()) {
                results.add(text)
                continue
            }

            val cached = memoryCache[trimmed]
            if (cached != null) {
                results.add(cached)
                continue
            }

            val singleResult = when (mode) {
                TranslationMode.MT -> translateWithMt(trimmed, context)
                TranslationMode.LIVE -> translateWithLive(trimmed)
            }

            if (singleResult.isSuccess) {
                val translated = singleResult.getOrThrow()
                memoryCache[trimmed] = translated
                results.add(translated)
            } else {
                // Return failure on first critical error with proper user-facing message
                return@withContext Result.failure(singleResult.exceptionOrNull() ?: Exception("Translation failed"))
            }
        }

        Result.success(results)
    }

    private suspend fun translateWithMt(text: String, context: Context?): Result<String> = withContext(Dispatchers.IO) {
        lastUsedEngine = TranslationMode.MT
        mtCallCount++

        val override = mtTranslatorOverride
        if (override != null) {
            return@withContext try {
                Result.success(override(text))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        return@withContext try {
            val translator = getOrCreateMlKitTranslator()
            val translated = Tasks.await(translator.translate(text))
            Result.success(translated)
        } catch (e: Exception) {
            Result.failure(
                IllegalStateException("Translation unavailable\nPlease download the Bengali translation model.", e)
            )
        }
    }

    private suspend fun translateWithLive(text: String): Result<String> = withContext(Dispatchers.IO) {
        lastUsedEngine = TranslationMode.LIVE
        liveCallCount++

        val override = liveTranslatorOverride
        if (override != null) {
            return@withContext try {
                Result.success(override(text))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        var connection: HttpURLConnection? = null
        try {
            val url = URL(libreTranslateEndpoint)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 7000
                readTimeout = 7000
                doOutput = true
            }

            val jsonPayload = JSONObject().apply {
                put("q", text)
                put("source", "auto")
                put("target", TARGET_LANGUAGE_CODE)
                put("format", "text")
            }

            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(jsonPayload.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseStr = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val jsonObj = JSONObject(responseStr)
                val translatedText = jsonObj.optString("translatedText", "")
                if (translatedText.isNotEmpty()) {
                    Result.success(translatedText)
                } else {
                    Result.failure(IllegalStateException("Live translation unavailable.\nCheck your internet connection."))
                }
            } else {
                Result.failure(IllegalStateException("Live translation unavailable.\nCheck your internet connection."))
            }
        } catch (e: Exception) {
            Result.failure(IllegalStateException("Live translation unavailable.\nCheck your internet connection.", e))
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Builds JavaScript script to extract visible text nodes from the current DOM.
     * Guaranteed not to modify page URL, links, scripts, or images.
     */
    fun buildDomExtractionScript(): String {
        return """
            (function() {
                try {
                    if (window.__fs_isTranslated) {
                        return JSON.stringify({action: "already_translated"});
                    }
                    var root = document.body || document.documentElement;
                    if (!root) return JSON.stringify({action: "error", message: "No body"});
                    
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
                    
                    window.__fs_nodes = [];
                    var texts = [];
                    var n;
                    var count = 0;
                    while ((n = walker.nextNode()) && count < 500) {
                        var orig = n.nodeValue;
                        var trimmed = orig.trim();
                        if (trimmed.length > 1) {
                            window.__fs_nodes.push({node: n, orig: orig});
                            texts.push(trimmed);
                            count++;
                        }
                    }
                    return JSON.stringify({action: "extracted", texts: texts});
                } catch(e) {
                    return JSON.stringify({action: "error", message: e.toString()});
                }
            })();
        """.trimIndent()
    }

    /**
     * Builds JavaScript script to apply translated text segments to the corresponding text nodes.
     * Stores original text in-memory for instant revert.
     */
    fun buildDomReplacementScript(translations: List<String>): String {
        val jsonArray = JSONArray(translations).toString()
        return """
            (function(trans) {
                try {
                    if (!window.__fs_nodes || !trans) return "no_nodes";
                    for (var i = 0; i < window.__fs_nodes.length && i < trans.length; i++) {
                        var item = window.__fs_nodes[i];
                        var t = trans[i];
                        if (t && item.node && item.node.parentNode) {
                            var orig = item.orig;
                            var leading = (orig.match(/^\s*/) || [""])[0];
                            var trailing = (orig.match(/\s*$/) || [""])[0];
                            item.node.nodeValue = leading + t + trailing;
                        }
                    }
                    window.__fs_isTranslated = true;
                    return "success";
                } catch(e) {
                    return "error: " + e.toString();
                }
            })($jsonArray);
        """.trimIndent()
    }

    /**
     * Builds JavaScript script to revert translated text nodes back to their original text.
     */
    fun buildDomRevertScript(): String {
        return """
            (function() {
                try {
                    if (!window.__fs_nodes) return "no_nodes";
                    for (var i = 0; i < window.__fs_nodes.length; i++) {
                        var item = window.__fs_nodes[i];
                        if (item.node && item.node.parentNode && item.orig !== undefined) {
                            item.node.nodeValue = item.orig;
                        }
                    }
                    window.__fs_isTranslated = false;
                    return "reverted";
                } catch(e) {
                    return "error: " + e.toString();
                }
            })();
        """.trimIndent()
    }
}
