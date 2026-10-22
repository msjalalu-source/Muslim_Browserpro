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

    sealed class TranslationResult {
        data class Success(val translations: List<String>) : TranslationResult()
        data class Error(val messageBn: String) : TranslationResult()
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
            return@withContext TranslationResult.Error("সার্ভার URL সঠিক নয় (http:// বা https:// দিয়ে শুরু হতে হবে)")
        }

        val endpoint = "$base/translate"
        var conn: HttpURLConnection? = null

        try {
            val url = URL(endpoint)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15000
                readTimeout = 20000
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }

            val jsonBody = JSONObject().apply {
                val qArray = JSONArray()
                texts.forEach { qArray.put(it) }
                put("q", qArray)
                put("source", "auto")
                put("target", "bn")
                put("format", "text")
                if (apiKey.isNotBlank()) {
                    put("api_key", apiKey.trim())
                }
            }

            conn.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
                os.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                val responseBody = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val parsed = parseLibreTranslateResponse(responseBody, texts.size)
                TranslationResult.Success(parsed)
            } else {
                val errorBody = try {
                    conn.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                } catch (_: Exception) { "" }
                val parsedError = parseLibreTranslateError(errorBody)
                val errorMsg = when (responseCode) {
                    400 -> "অনুরোধে ত্রুটি (Bad Request${if (parsedError.isNotBlank()) ": $parsedError" else ""})"
                    403 -> "অনুবাদ ব্যর্থ: API Key প্রয়োজন বা ভুল API Key (HTTP 403)"
                    429 -> "অনুরোধের সীমা অতিক্রম করেছে (Rate limit - HTTP 429)"
                    in 500..599 -> "সার্ভারে সমস্যা হয়েছে (Server Error: HTTP $responseCode)"
                    else -> "অনুবাদ ব্যর্থ হয়েছে (HTTP $responseCode${if (parsedError.isNotBlank()) ": $parsedError" else ""})"
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
            TranslationResult.Error("অনুবাদে ত্রুটি: ${e.message ?: "অজানা সমস্যা"}")
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

        val endpoint = "$base/translate"
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
            }

            val jsonBody = JSONObject().apply {
                put("q", "Hello")
                put("source", "en")
                put("target", "bn")
                put("format", "text")
                if (apiKey.isNotBlank()) {
                    put("api_key", apiKey.trim())
                }
            }

            conn.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
                os.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                val responseBody = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val parsed = parseLibreTranslateResponse(responseBody, 1)
                TranslationResult.Success(parsed)
            } else {
                val errorBody = try {
                    conn.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                } catch (_: Exception) { "" }
                val parsedError = parseLibreTranslateError(errorBody)
                val errorMsg = when (responseCode) {
                    400 -> "অনুরোধে ত্রুটি (Bad Request${if (parsedError.isNotBlank()) ": $parsedError" else ""})"
                    403 -> "API Key প্রয়োজন বা ভুল API Key (HTTP 403)"
                    429 -> "অনুরোধের সীমা অতিক্রম করেছে (Rate limit - HTTP 429)"
                    in 500..599 -> "সার্ভারে সমস্যা হয়েছে (Server Error: HTTP $responseCode)"
                    else -> "সংযোগ ব্যর্থ (HTTP $responseCode${if (parsedError.isNotBlank()) ": $parsedError" else ""})"
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
            if (trimmed.startsWith("{")) {
                val obj = JSONObject(trimmed)
                obj.optString("error", "").ifBlank { obj.optString("message", "") }
            } else {
                jsonStr.take(120)
            }
        } catch (_: Exception) {
            jsonStr.take(120)
        }
    }
}
