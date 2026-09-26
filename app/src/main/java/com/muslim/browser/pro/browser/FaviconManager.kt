package com.muslim.browser.pro.browser

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Lightweight, non-blocking Favicon Manager.
 * Fetches, caches (in-memory LRU and on-disk), and provides website icons for Home Page tiles.
 * Does not use any heavy external libraries.
 */
object FaviconManager {

    private const val CACHE_DIR_NAME = "favicons"
    private const val CONNECT_TIMEOUT_MS = 3500
    private const val READ_TIMEOUT_MS = 4000
    private val SAFE_FILENAME_REGEX = Regex("[^a-zA-Z0-9.-]")

    // In-memory LRU cache for 64 icons to prevent disk reads and network requests on recomposition
    private val memoryCache = LruCache<String, Bitmap>(64)

    /**
     * Extracts a clean base domain from a URL (e.g., "https://www.google.com/search" -> "google.com").
     */
    fun extractDomain(url: String): String {
        return try {
            val trimmed = url.trim()
            if (trimmed.isBlank() || trimmed.startsWith("about:")) return ""
            val candidate = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                "https://$trimmed"
            } else {
                trimmed
            }
            val uri = Uri.parse(candidate)
            var host = uri.host ?: ""
            if (host.startsWith("www.")) {
                host = host.substring(4)
            }
            host.lowercase()
        } catch (_: Exception) {
            ""
        }
    }

    /**
     * Synchronously returns cached bitmap from memory if available.
     */
    fun getFromMemory(domain: String): Bitmap? {
        if (domain.isBlank()) return null
        return memoryCache.get(domain)
    }

    /**
     * Retrieves the favicon:
     * 1. Checks memory cache
     * 2. Checks disk cache on Dispatchers.IO
     * 3. Fetches from Google's official s2 Favicon service if not cached
     * Never crashes on failure, returns null as fallback.
     */
    suspend fun loadFavicon(context: Context, domain: String): Bitmap? = withContext(Dispatchers.IO) {
        if (domain.isBlank()) return@withContext null

        // 1. Check memory cache
        val memBitmap = memoryCache.get(domain)
        if (memBitmap != null) return@withContext memBitmap

        val diskFile = getDiskCacheFile(context, domain)

        // 2. Check disk cache
        if (diskFile != null && diskFile.exists() && diskFile.length() > 0) {
            try {
                val diskBitmap = BitmapFactory.decodeFile(diskFile.absolutePath)
                if (diskBitmap != null) {
                    memoryCache.put(domain, diskBitmap)
                    return@withContext diskBitmap
                }
            } catch (_: Exception) {
                diskFile.delete()
            }
        }

        // 3. Fetch from Google S2 Favicon service
        try {
            val iconUrl = "https://www.google.com/s2/favicons?domain=$domain&sz=128"
            val connection = (URL(iconUrl).openConnection() as? HttpURLConnection) ?: return@withContext null
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.instanceFollowRedirects = true
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)")

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val bytes = connection.inputStream.use { it.readBytes() }
                connection.disconnect()

                if (bytes.isNotEmpty()) {
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        memoryCache.put(domain, bitmap)

                        // Save to disk cache asynchronously
                        if (diskFile != null) {
                            try {
                                diskFile.parentFile?.mkdirs()
                                FileOutputStream(diskFile).use { fos ->
                                    fos.write(bytes)
                                    fos.flush()
                                }
                            } catch (_: Exception) {}
                        }

                        return@withContext bitmap
                    }
                }
            } else {
                connection.disconnect()
            }
        } catch (_: Exception) {
            // Network failure or offline: gracefully fallback to null
        }

        null
    }

    /**
     * Clears in-memory and on-disk favicon cache (e.g. on Clear All Data).
     */
    fun clearCache(context: Context) {
        memoryCache.evictAll()
        try {
            val cacheDir = File(context.cacheDir, CACHE_DIR_NAME)
            if (cacheDir.exists()) {
                cacheDir.deleteRecursively()
            }
        } catch (_: Exception) {}
    }

    private fun getDiskCacheFile(context: Context, domain: String): File? {
        return try {
            val dir = File(context.cacheDir, CACHE_DIR_NAME)
            val safeName = domain.replace(SAFE_FILENAME_REGEX, "_") + ".png"
            File(dir, safeName)
        } catch (_: Exception) {
            null
        }
    }
}

/**
 * Compose helper to observe website favicon with memory + disk caching.
 * Returns null while loading or if unavailable (triggers clean fallback).
 */
@Composable
fun rememberFavicon(url: String): Bitmap? {
    val context = LocalContext.current.applicationContext
    val domain = remember(url) { FaviconManager.extractDomain(url) }
    var bitmap by remember(domain) {
        mutableStateOf(FaviconManager.getFromMemory(domain))
    }

    LaunchedEffect(domain) {
        if (bitmap == null && domain.isNotBlank()) {
            val loaded = FaviconManager.loadFavicon(context, domain)
            if (loaded != null) {
                bitmap = loaded
            }
        }
    }

    return bitmap
}
