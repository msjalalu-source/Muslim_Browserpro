package com.muslim.browser.pro.browser

import android.net.Uri
import android.util.Log
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale

object ProtectionEngine {

    enum class DownloadStatus {
        ALLOWED_IMAGE,
        ALLOWED_PDF,
        BLOCKED_VIDEO,
        BLOCKED_AUDIO,
        BLOCKED_APK,
        BLOCKED_OTHER
    }

    sealed class FilterResult {
        data object Allowed : FilterResult()
        data class Blocked(val reason: String, val detail: String) : FilterResult()
    }

    val KNOWN_ADULT_DOMAINS: HashSet<String> = hashSetOf(
        "pornhub.com", "xvideos.com", "xnxx.com", "xhamster.com", "redtube.com",
        "youporn.com", "chaturbate.com", "stripchat.com", "livejasmin.com", "onlyfans.com",
        "brazzers.com", "spankbang.com", "daftsex.com", "porn.com", "xxx.com"
    )

    val KNOWN_AD_DOMAINS: HashSet<String> = hashSetOf(
        "doubleclick.net", "googlesyndication.com", "googleadservices.com", "adservice.google.com",
        "pagead2.googlesyndication.com", "adnxs.com", "adsystem.amazon.com", "amazon-adsystem.com",
        "criteo.com", "criteo.net", "taboola.com", "outbrain.com", "popads.net", "adsterra.com",
        "propellerads.com", "inmobi.com", "applovin.com", "unityads.unity3d.com",
        "scorecardresearch.com", "advertising.com", "adroll.com", "revcontent.com"
    )

    val BLOCKED_VIDEO_EXTS: HashSet<String> = hashSetOf(
        "mp4", "mkv", "webm", "avi", "mov", "m4v", "wmv", "flv", "3gp"
    )

    val BLOCKED_AUDIO_EXTS: HashSet<String> = hashSetOf(
        "mp3", "m4a", "wav", "flac", "ogg", "aac", "wma"
    )

    val BLOCKED_APK_EXTS: HashSet<String> = hashSetOf(
        "apk", "xapk", "apks"
    )

    val ALLOWED_IMAGE_EXTS: HashSet<String> = hashSetOf(
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "ico"
    )

    val ALLOWED_PDF_EXTS: HashSet<String> = hashSetOf(
        "pdf"
    )

    fun buildGoogleSafeSearchUrl(query: String): String {
        val trimmed = query.trim()
        val encoded = URLEncoder.encode(trimmed, StandardCharsets.UTF_8.name())
        val url = "https://www.google.com/search?q=$encoded&safe=active"
        NavigationController.markSearchUrlValidated(url)
        return url
    }

    fun buildDuckDuckGoSafeSearchUrl(query: String): String {
        val trimmed = query.trim()
        val encoded = URLEncoder.encode(trimmed, StandardCharsets.UTF_8.name())
        return "https://safe.duckduckgo.com/?q=$encoded"
    }

    fun buildSafeSearchUrl(query: String, searchEngine: SearchEngine = SearchEngine.DUCKDUCKGO): String {
        return when (searchEngine) {
            SearchEngine.DUCKDUCKGO -> buildDuckDuckGoSafeSearchUrl(query)
            SearchEngine.GOOGLE -> buildGoogleSafeSearchUrl(query)
        }
    }

    fun isGoogleSafeSearchUrl(url: String): Boolean {
        if (url.isBlank() || !url.contains("safe=active", ignoreCase = true)) return false
        return try {
            val uri = Uri.parse(url)
            val host = uri.host?.lowercase(Locale.ROOT) ?: return false
            if (!isGoogleHost(host)) return false
            val path = uri.path?.lowercase(Locale.ROOT) ?: ""
            if (!path.contains("/search") && !path.contains("/webhp") && path != "/") return false
            uri.getQueryParameter("safe")?.equals("active", ignoreCase = true) == true
        } catch (_: Exception) {
            false
        }
    }

    fun isDuckDuckGoSafeSearchUrl(url: String): Boolean {
        if (url.isBlank()) return false
        return try {
            val uri = Uri.parse(url)
            val host = uri.host?.lowercase(Locale.ROOT) ?: return false
            host == "safe.duckduckgo.com" || (host.contains("duckduckgo.com") && uri.getQueryParameter("kp") == "1")
        } catch (_: Exception) {
            false
        }
    }

    fun isSafeSearchUrl(url: String): Boolean {
        return isGoogleSafeSearchUrl(url) || isDuckDuckGoSafeSearchUrl(url)
    }

    fun isGoogleHost(host: String): Boolean {
        val clean = host.lowercase(Locale.ROOT)
        return clean == "google.com" ||
            clean.endsWith(".google.com") ||
            clean.startsWith("google.") ||
            clean.contains(".google.")
    }

    fun extractSearchEngineQuery(url: String): String? {
        if (url.isBlank()) return null
        return try {
            val uri = Uri.parse(url)
            val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: return null
            if (scheme != "http" && scheme != "https") return null
            val host = uri.host?.lowercase(Locale.ROOT) ?: return null
            val path = uri.path?.lowercase(Locale.ROOT) ?: ""

            if (isGoogleHost(host) && !path.startsWith("/url")) {
                if (path.contains("/search") || path.contains("/webhp") || path == "/" || path.isEmpty()) {
                    val q = uri.getQueryParameter("q")
                    if (!q.isNullOrBlank()) return q
                }
            }

            if (host.contains("bing.com") && (path.contains("/search") || uri.getQueryParameter("q") != null)) {
                val q = uri.getQueryParameter("q")
                if (!q.isNullOrBlank()) return q
            }

            if (host.contains("duckduckgo.com") && uri.getQueryParameter("q") != null) {
                val q = uri.getQueryParameter("q")
                if (!q.isNullOrBlank()) return q
            }

            if (host.contains("search.yahoo.com") || (host.contains("yahoo.com") && path.contains("/search"))) {
                val p = uri.getQueryParameter("p")
                if (!p.isNullOrBlank()) return p
            }

            null
        } catch (_: Exception) {
            null
        }
    }

    fun isBlockedByCustomKeywords(
        text: String,
        customKeywords: Set<String>,
        normalizedKeywords: Collection<String>? = null
    ): String? {
        if (customKeywords.isEmpty() || text.isBlank()) return null
        val lowerText = text.trim().lowercase(Locale.ROOT)
        val normalized = if (lowerText.contains("%20")) {
            lowerText.replace("%20", " ")
        } else {
            lowerText
        }

        if (!normalizedKeywords.isNullOrEmpty()) {
            for (kwNorm in normalizedKeywords) {
                if (kwNorm.isNotEmpty() && normalized.contains(kwNorm)) {
                    val original = customKeywords.firstOrNull { it.trim().equals(kwNorm, ignoreCase = true) }
                    return original ?: kwNorm
                }
            }
        } else {
            for (kw in customKeywords) {
                val kwNormalized = kw.trim().lowercase(Locale.ROOT)
                if (kwNormalized.isNotEmpty() && normalized.contains(kwNormalized)) {
                    return kw
                }
            }
        }
        return null
    }

    fun checkDirectUrl(
        url: String,
        customKeywords: Set<String>,
        normalizedKeywords: Collection<String>? = null
    ): FilterResult {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return FilterResult.Allowed

        Log.d("DIAGNOSTIC", "DIRECT_URL_CHECK=checkDirectUrl")
        Log.d("DIAGNOSTIC", "URL=$trimmed")

        val blockedKw = isBlockedByCustomKeywords(trimmed, customKeywords, normalizedKeywords)
        if (blockedKw != null) {
            return FilterResult.Blocked(
                "Custom Keyword Protection",
                "Direct navigation blocked due to protected keyword: \"$blockedKw\""
            )
        }

        val host = extractHost(trimmed)
        if (host != null && matchesDomainOrSubdomain(host, KNOWN_ADULT_DOMAINS)) {
            return FilterResult.Blocked(
                "Adult Content Protection",
                "Direct access to adult domains is restricted: \"$host\""
            )
        }

        if (isAdRequest(trimmed)) {
            return FilterResult.Blocked(
                "Ad Protection",
                "Ad network URL blocked: \"${host ?: trimmed}\""
            )
        }

        return FilterResult.Allowed
    }

    fun checkUrlOrQuery(
        input: String,
        customKeywords: Set<String>,
        normalizedKeywords: Collection<String>? = null
    ): FilterResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return FilterResult.Allowed
        val blockedKw = isBlockedByCustomKeywords(trimmed, customKeywords, normalizedKeywords)
        if (blockedKw != null) {
            return FilterResult.Blocked(
                "Custom Keyword Protection",
                "Input blocked due to protected keyword: \"$blockedKw\""
            )
        }
        return checkDirectUrl(trimmed, customKeywords, normalizedKeywords)
    }

    fun extractHost(url: String): String? {
        val candidate = if (!url.startsWith("http://", ignoreCase = true) && !url.startsWith("https://", ignoreCase = true)) {
            "https://$url"
        } else {
            url
        }
        return try {
            Uri.parse(candidate).host?.lowercase(Locale.ROOT)
        } catch (_: Exception) {
            null
        }
    }

    fun matchesDomainOrSubdomain(host: String, domainSet: Set<String>): Boolean {
        if (domainSet.contains(host)) return true
        var dotIndex = host.indexOf('.')
        while (dotIndex != -1) {
            val parent = host.substring(dotIndex + 1)
            if (domainSet.contains(parent)) return true
            dotIndex = host.indexOf('.', dotIndex + 1)
        }
        return false
    }

    fun isAdRequest(uri: Uri): Boolean {
        val host = uri.host?.lowercase(Locale.ROOT)
        if (host != null && host.isNotEmpty() && matchesDomainOrSubdomain(host, KNOWN_AD_DOMAINS)) {
            return true
        }
        if (uri.query != null) {
            val normalized = uri.toString().lowercase(Locale.ROOT)
            for (adDomain in KNOWN_AD_DOMAINS) {
                if (normalized.contains(adDomain)) return true
            }
        }
        return false
    }

    fun isAdRequest(url: String): Boolean {
        val host = extractHost(url)
        if (host != null && host.isNotEmpty() && matchesDomainOrSubdomain(host, KNOWN_AD_DOMAINS)) {
            return true
        }
        if (host == null || url.indexOf('?') != -1) {
            val normalized = url.lowercase(Locale.ROOT)
            for (adDomain in KNOWN_AD_DOMAINS) {
                if (normalized.contains(adDomain)) return true
            }
        }
        return false
    }

    fun checkDownloadType(url: String, mimeType: String?, contentDisposition: String?): DownloadStatus {
        val mime = mimeType?.trim()?.lowercase(Locale.ROOT) ?: ""
        val ext = extractExtension(url, contentDisposition)

        if (mime.startsWith("video/") || BLOCKED_VIDEO_EXTS.contains(ext)) {
            return DownloadStatus.BLOCKED_VIDEO
        }
        if (mime.startsWith("audio/") || mime == "application/ogg" || BLOCKED_AUDIO_EXTS.contains(ext)) {
            return DownloadStatus.BLOCKED_AUDIO
        }
        if (mime == "application/vnd.android.package-archive" || BLOCKED_APK_EXTS.contains(ext)) {
            return DownloadStatus.BLOCKED_APK
        }
        if (mime.startsWith("image/") || ALLOWED_IMAGE_EXTS.contains(ext)) {
            return DownloadStatus.ALLOWED_IMAGE
        }
        if (mime == "application/pdf" || ALLOWED_PDF_EXTS.contains(ext)) {
            return DownloadStatus.ALLOWED_PDF
        }
        return DownloadStatus.BLOCKED_OTHER
    }

    fun extractExtension(url: String, contentDisposition: String?): String {
        return DownloadPolicy.extractExtension(url, contentDisposition)
    }
}
