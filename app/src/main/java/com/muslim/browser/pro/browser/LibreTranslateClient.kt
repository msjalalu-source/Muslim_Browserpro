package com.muslim.browser.pro.browser

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

object LibreTranslateClient {

    const val DEFAULT_SERVER_URL: String = "https://libretranslate.com"
    private const val PREFS_NAME: String = "libre_translate_prefs"
    private const val KEY_SERVER_URL: String = "key_libre_server_url"
    private const val KEY_API_KEY: String = "key_libre_api_key"

    fun getServerUrl(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SERVER_URL, null)?.takeIf { it.isNotBlank() } ?: DEFAULT_SERVER_URL
    }

    fun setServerUrl(context: Context, url: String) {
        val cleanUrl = url.trim().ifBlank { DEFAULT_SERVER_URL }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SERVER_URL, cleanUrl)
            .apply()
    }

    fun getApiKey(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_API_KEY, "") ?: ""
    }

    fun setApiKey(context: Context, apiKey: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_API_KEY, apiKey.trim())
            .apply()
    }

    private const val TAG: String = "LibreTranslateClient"

    sealed class TranslationResult {
        data class Success(val translations: List<String>) : TranslationResult()
        data class Error(val messageBn: String) : TranslationResult()
    }

    fun buildJsonRequestBody(
        texts: List<String>,
        sourceLang: String = "auto",
        targetLang: String = "bn",
        apiKey: String = ""
    ): JSONObject {
        return JSONObject().apply {
            if (texts.size == 1) {
                put("q", texts[0])
            } else {
                val qArray = JSONArray()
                texts.forEach { qArray.put(it) }
                put("q", qArray)
            }
            put("source", sourceLang)
            put("target", targetLang)
            put("format", "text")
            if (apiKey.isNotBlank()) {
                put("api_key", apiKey.trim())
            }
        }
    }

    suspend fun translateBatch(
        serverUrl: String,
        apiKey: String,
        texts: List<String>
    ): TranslationResult = withContext(Dispatchers.IO) {
        if (texts.isEmpty()) {
            return@withContext TranslationResult.Success(emptyList())
        }

        val base = serverUrl.trim().trimEnd('/')
        if (!base.startsWith("http://", ignoreCase = true) && !base.startsWith("https://", ignoreCase = true)) {
            android.util.Log.w(TAG, "Invalid server URL: $serverUrl")
            return@withContext TranslationResult.Error("সার্ভার URL সঠিক নয়")
        }

        val endpoint = if (base.endsWith("/translate", ignoreCase = true)) base else "$base/translate"

        // Primary translation attempt with automatic language detection (source = "auto")
        val result = executeTranslateRequest(endpoint, apiKey, texts, sourceLang = "auto")
        if (result is TranslationResult.Success) {
            return@withContext result
        }

        val err = result as TranslationResult.Error

        // Fast-fail: Never perform retries on authentication, rate-limit, or network reachability errors
        if (err.messageBn.contains("API Key") ||
            err.messageBn.contains("সীমা") ||
            err.messageBn.contains("নেটওয়ার্ক") ||
            err.messageBn.contains("সার্ভার পাওয়া যাচ্ছে না")
        ) {
            return@withContext result
        }

        // Only retry with source='en' if the failure was specifically due to language detection unavailability
        if (err.messageBn.contains("শনাক্ত")) {
            android.util.Log.i(TAG, "Language detection unavailable; attempting fallback to source language 'en'")
            val fallbackResult = executeTranslateRequest(endpoint, apiKey, texts, sourceLang = "en")
            if (fallbackResult is TranslationResult.Success) {
                return@withContext fallbackResult
            }
        }

        result
    }

    private fun executeTranslateRequest(
        endpoint: String,
        apiKey: String,
        texts: List<String>,
        sourceLang: String
    ): TranslationResult {
        var conn: HttpURLConnection? = null
        try {
            val url = URL(endpoint)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 12000
                readTimeout = 15000
                doInput = true
                doOutput = true
                instanceFollowRedirects = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                )
            }

            val jsonBody = buildJsonRequestBody(texts, sourceLang = sourceLang, targetLang = "bn", apiKey = apiKey)

            conn.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
                os.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                val responseBody = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val parsed = parseLibreTranslateResponse(responseBody, texts.size)
                if (parsed.isNotEmpty()) {
                    return TranslationResult.Success(parsed)
                } else {
                    return TranslationResult.Error("ফলাফল পার্স করা যায়নি")
                }
            } else {
                val errorBody = try {
                    conn.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                } catch (_: Exception) { "" }
                val parsedError = parseLibreTranslateError(errorBody)
                android.util.Log.w(TAG, "HTTP $responseCode from $endpoint: $parsedError")

                val isApiKeyErr = responseCode == 403 ||
                        (responseCode == 400 && (
                                parsedError.contains("API key", ignoreCase = true) ||
                                errorBody.contains("API key", ignoreCase = true) ||
                                errorBody.contains("portal.libretranslate.com", ignoreCase = true)
                        ))
                val isDetectionErr = responseCode == 400 && (
                        parsedError.contains("detect", ignoreCase = true) ||
                        errorBody.contains("detect", ignoreCase = true)
                )

                val errorMsg = when {
                    isApiKeyErr -> "API Key প্রয়োজন বা অননুমোদিত"
                    isDetectionErr -> "ভাষা শনাক্ত করা যায়নি"
                    responseCode == 400 -> "অনুরোধে ত্রুটি (Bad Request)"
                    responseCode == 429 -> "অনুরোধের সীমা অতিক্রম করেছে"
                    responseCode in 500..599 -> "সার্ভারে সমস্যা হয়েছে (HTTP $responseCode)"
                    else -> "অনুবাদ ব্যর্থ হয়েছে (HTTP $responseCode)"
                }
                return TranslationResult.Error(errorMsg)
            }
        } catch (e: SocketTimeoutException) {
            android.util.Log.w(TAG, "Timeout connecting to $endpoint: ${e.message}")
            return TranslationResult.Error("অনুরোধের সময় শেষ")
        } catch (e: UnknownHostException) {
            android.util.Log.w(TAG, "Unknown host: $endpoint: ${e.message}")
            return TranslationResult.Error("সার্ভার পাওয়া যাচ্ছে না")
        } catch (e: IOException) {
            android.util.Log.w(TAG, "Network error: ${e.message}")
            return TranslationResult.Error("নেটওয়ার্ক ত্রুটি")
        } catch (e: Exception) {
            android.util.Log.w(TAG, "Translation error: ${e.message}")
            return TranslationResult.Error("অনুবাদে ত্রুটি")
        } finally {
            try {
                conn?.disconnect()
            } catch (_: Exception) {}
        }
    }

    suspend fun testConnection(serverUrl: String, apiKey: String): TranslationResult = withContext(Dispatchers.IO) {
        val base = serverUrl.trim().trimEnd('/')
        if (!base.startsWith("http://", ignoreCase = true) && !base.startsWith("https://", ignoreCase = true)) {
            return@withContext TranslationResult.Error("সার্ভার URL সঠিক নয় (http:// বা https:// দিয়ে শুরু হতে হবে)")
        }

        val endpoint = if (base.endsWith("/translate", ignoreCase = true)) base else "$base/translate"
        var conn: HttpURLConnection? = null

        try {
            val url = URL(endpoint)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10000
                readTimeout = 10000
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                )
            }

            val jsonBody = buildJsonRequestBody(listOf("Hello"), sourceLang = "en", targetLang = "bn", apiKey = apiKey)

            conn.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
                os.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                val responseBody = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val parsed = parseLibreTranslateResponse(responseBody, 1)
                if (parsed.isNotEmpty()) {
                    TranslationResult.Success(parsed)
                } else {
                    TranslationResult.Error("ফলাফল পার্স করা যায়নি")
                }
            } else {
                val errorBody = try {
                    conn.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                } catch (_: Exception) { "" }
                val parsedError = parseLibreTranslateError(errorBody)
                val isApiKeyErr = responseCode == 403 ||
                        (responseCode == 400 && (
                                parsedError.contains("API key", ignoreCase = true) ||
                                errorBody.contains("API key", ignoreCase = true) ||
                                errorBody.contains("portal.libretranslate.com", ignoreCase = true)
                        ))
                val errorMsg = when {
                    isApiKeyErr -> "API Key প্রয়োজন বা ভুল API Key"
                    responseCode == 400 -> "অনুরোধে ত্রুটি (Bad Request)"
                    responseCode == 429 -> "অনুরোধের সীমা অতিক্রম করেছে (Rate limit)"
                    responseCode in 500..599 -> "সার্ভারে সমস্যা হয়েছে (HTTP $responseCode)"
                    else -> "সংযোগ ব্যর্থ (HTTP $responseCode)"
                }
                TranslationResult.Error(errorMsg)
            }
        } catch (e: SocketTimeoutException) {
            TranslationResult.Error("অনুরোধের সময় শেষ (Timeout)। সার্ভার সাড়া দেয়নি।")
        } catch (e: UnknownHostException) {
            TranslationResult.Error("ইন্টারনেট সংযোগ নেই অথবা সার্ভার পাওয়া যাচ্ছে না।")
        } catch (e: IOException) {
            TranslationResult.Error("নেটওয়ার্ক ত্রুটি: ইন্টারনেট সংযোগ পরীক্ষা করুন।")
        } catch (e: Exception) {
            TranslationResult.Error("সংযোগ ত্রুটি: ${e.message ?: "অজানা সমস্যা"}")
        } finally {
            try {
                conn?.disconnect()
            } catch (_: Exception) {}
        }
    }

    fun parseLibreTranslateResponse(jsonStr: String, expectedCount: Int): List<String> {
        val trimmed = jsonStr.trim()
        if (trimmed.startsWith("{")) {
            val obj = JSONObject(trimmed)
            if (obj.has("translatedText")) {
                val field = obj.get("translatedText")
                if (field is JSONArray) {
                    val list = mutableListOf<String>()
                    for (i in 0 until field.length()) {
                        list.add(field.optString(i, ""))
                    }
                    return list
                } else if (field is String) {
                    return listOf(field)
                }
            }
        } else if (trimmed.startsWith("[")) {
            val arr = JSONArray(trimmed)
            val list = mutableListOf<String>()
            for (i in 0 until arr.length()) {
                val item = arr.get(i)
                if (item is JSONObject && item.has("translatedText")) {
                    list.add(item.optString("translatedText", ""))
                } else if (item is String) {
                    list.add(item)
                }
            }
            return list
        }
        return emptyList()
    }

    fun parseLibreTranslateError(jsonStr: String): String {
        if (jsonStr.isBlank()) return ""
        return try {
            val trimmed = jsonStr.trim()
            val raw = if (trimmed.startsWith("{")) {
                val obj = JSONObject(trimmed)
                obj.optString("error", "").ifBlank { obj.optString("message", "") }
            } else {
                jsonStr.take(120)
            }
            if (raw.contains("portal.libretranslate.com", ignoreCase = true) || raw.contains("API key", ignoreCase = true)) {
                "API key required"
            } else {
                raw.take(120)
            }
        } catch (_: Exception) {
            "Unknown error"
        }
    }
}
