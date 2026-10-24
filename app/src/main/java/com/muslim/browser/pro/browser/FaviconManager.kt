package com.muslim.browser.pro.browser

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/**
 * High-performance, lightweight favicon manager with memory (LruCache)
 * and disk caching to asynchronously load and cache website favicons.
 */
object FaviconManager {

    private val memoryCache = object : LruCache<String, Bitmap>(64) {
        override fun sizeOf(key: String, value: Bitmap): Int = 1
    }

    fun getMemoryCached(url: String): Bitmap? {
        val host = extractHost(url)
        if (host.isBlank()) return null
        return memoryCache.get(host)
    }

    fun putInMemory(url: String, bitmap: Bitmap) {
        val host = extractHost(url)
        if (host.isNotBlank()) {
            memoryCache.put(host, bitmap)
        }
    }

    fun clearCache() {
        memoryCache.evictAll()
    }

    private fun extractHost(url: String): String {
        return try {
            val trimmed = url.trim()
            val uri = Uri.parse(if (!trimmed.contains("://")) "https://$trimmed" else trimmed)
            uri.host?.lowercase() ?: trimmed
        } catch (_: Exception) {
            url.trim()
        }
    }

    private fun hashKey(host: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val bytes = md.digest(host.toByteArray())
            bytes.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            host.replace(Regex("[^a-zA-Z0-9]"), "_")
        }
    }

    suspend fun loadFavicon(context: Context, url: String): Bitmap? = withContext(Dispatchers.IO) {
        val host = extractHost(url)
        if (host.isBlank()) return@withContext null

        // 1. Check in-memory cache
        memoryCache.get(host)?.let { return@withContext it }

        // 2. Check disk cache
        val cacheDir = File(context.cacheDir, "favicons")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val file = File(cacheDir, "${hashKey(host)}.png")
        if (file.exists() && file.length() > 0) {
            try {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    memoryCache.put(host, bitmap)
                    return@withContext bitmap
                }
            } catch (_: Exception) {}
        }

        // 3. Asynchronous network fetch with strict timeout
        withTimeoutOrNull(2500L) {
            val endpoints = listOf(
                "https://www.google.com/s2/favicons?domain=$host&sz=128",
                "https://$host/favicon.ico"
            )

            for (endpoint in endpoints) {
                var conn: HttpURLConnection? = null
                try {
                    conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                        connectTimeout = 2000
                        readTimeout = 2000
                        instanceFollowRedirects = true
                        setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                    }
                    if (conn.responseCode == 200) {
                        conn.inputStream.use { stream ->
                            val bytes = stream.readBytes()
                            if (bytes.isNotEmpty()) {
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                if (bitmap != null) {
                                    memoryCache.put(host, bitmap)
                                    try {
                                        FileOutputStream(file).use { fos ->
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                                        }
                                    } catch (_: Exception) {}
                                    return@withTimeoutOrNull bitmap
                                }
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Fallthrough to next endpoint
                } finally {
                    conn?.disconnect()
                }
            }
            null
        }
    }
}
