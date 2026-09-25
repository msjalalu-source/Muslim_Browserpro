package com.muslim.browser.pro.browser

import android.net.Uri
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale

/**
 * Lightweight two-tier protection engine for Focus Shield Browser:
 * 1. Primary Search Protection: Enforces Google SafeSearch (safe=active) for all search requests
 *    and normalizes external search engine queries into Google SafeSearch.
 * 2. Secondary Custom Keyword Protection: Fast, user-defined keyword filter evaluated before queries
 *    reach search engines and on direct URL navigations.
 * 3. Direct URL Protection: Lightweight domain-level matching for high-impact adult domains
 *    and download type restrictions (Video, MP3/Audio, APK).
 * 4. Lightweight Ad Request Protection: Direct host matching against known ad networks.
 */
object ProtectionEngine {

    // Compact high-confidence adult domains for lightweight direct URL protection
    val KNOWN_ADULT_DOMAINS = hashSetOf(
        "pornhub.com",
        "xvideos.com",
        "xnxx.com",
        "xhamster.com",
        "redtube.com",
        "youporn.com",
        "chaturbate.com",
        "stripchat.com",
        "livejasmin.com",
        "onlyfans.com",
        "brazzers.com",
        "spankbang.com",
        "daftsex.com",
        "porn.com",
        "xxx.com"
    )

    // Common ad network domains for lightweight request blocking
    private val KNOWN_AD_DOMAINS = hashSetOf(
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        "adnxs.com",
        "adsystem.amazon.com",
        "amazon-adsystem.com",
        "criteo.com",
        "criteo.net",
        "taboola.com",
        "outbrain.com",
        "popads.net",
        "adsterra.com",
        "propellerads.com",
        "inmobi.com",
        "applovin.com",
        "unityads.unity3d.com",
        "scorecardresearch.com",
        "advertising.com",
        "adroll.com",
        "revcontent.com"
    )

    // Blocked download extensions
    private val BLOCKED_VIDEO_EXTS = hashSetOf(
        "mp4", "mkv", "webm", "avi", "mov", "m4v", "wmv", "flv", "3gp"
    )
    private val BLOCKED_AUDIO_EXTS = hashSetOf(
        "mp3", "m4a", "wav", "flac", "ogg", "aac", "wma"
    )
    private val BLOCKED_APK_EXTS = hashSetOf(
        "apk", "xapk", "apks"
    )

    // Allowed download extensions
    private val ALLOWED_IMAGE_EXTS = hashSetOf(
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "ico"
    )
    private val ALLOWED_PDF_EXTS = hashSetOf(
        "pdf"
    )

    sealed class FilterResult {
        object Allowed : FilterResult()
        data class Blocked(val reason: String, val detail: String) : FilterResult()
    }

    enum class DownloadStatus {
        ALLOWED_IMAGE,
        ALLOWED_PDF,
        BLOCKED_VIDEO,
        BLOCKED_AUDIO,
        BLOCKED_APK,
        BLOCKED_OTHER
    }

    /**
     * Builds a standardized Google SafeSearch URL with safe=active guaranteed.
     * Centralized helper to avoid duplicated URL-building logic.
     */
    fun buildGoogleSafeSearchUrl(query: String): String {
        val trimmed = query.trim()
        val encoded = URLEncoder.encode(trimmed, StandardCharsets.UTF_8.name())
        return "https://www.google.com/search?q=$encoded&safe=active"
    }

    /**
     * Checks if a URL is already a Google Search URL with safe=active enforced.
     */
    fun isGoogleSafeSearchUrl(url: String): Boolean {
        if (url.isBlank()) return false
        val uri = try {
            Uri.parse(url)
        } catch (_: Exception) {
            return false
        }
        val host = uri.host?.lowercase(Locale.ROOT) ?: return false
        if (!isGoogleHost(host)) return false
        val path = uri.path?.lowercase(Locale.ROOT) ?: ""
        if (!path.contains("/search") && !path.contains("/webhp")) return false
        val safeParam = uri.getQueryParameter("safe")?.lowercase(Locale.ROOT)
        return safeParam == "active"
    }

    /**
     * Checks whether the host represents a Google search domain.
     */
    fun isGoogleHost(host: String): Boolean {
        val clean = host.lowercase(Locale.ROOT)
        return clean == "google.com" ||
                clean.endsWith(".google.com") ||
                clean.startsWith("google.") ||
                clean.contains(".google.")
    }

    /**
     * Checks whether the host represents a Google Translate domain.
     */
    fun isTranslationHost(host: String?): Boolean {
        if (host.isNullOrBlank()) return false
        val clean = host.lowercase(Locale.ROOT)
        return clean == "translate.google.com" ||
                clean.endsWith(".translate.google.com") ||
                clean == "translate.goog" ||
                clean.endsWith(".translate.goog") ||
                clean == "translate.googleapis.com" ||
                clean.endsWith(".translate.googleapis.com")
    }

    /**
     * Checks whether the URL is a Google Translate service URL.
     */
    fun isTranslationUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val host = extractHost(url) ?: return false
        return isTranslationHost(host)
    }

    /**
     * Converts an original web hostname into Google Translate proxy hostname (.translate.goog).
     * Hyphens are escaped as double-hyphens '--', and dots '.' are replaced with '-'.
     */
    fun encodeTranslateHost(originalHost: String): String {
        val clean = originalHost.trim().lowercase(Locale.ROOT)
        return clean
            .replace("-", "--")
            .replace(".", "-") + ".translate.goog"
    }

    /**
     * Decodes a Google Translate proxy hostname back to the original domain name.
     * Removes the .translate.goog suffix, restores dots, and unescapes double-hyphens.
     */
    fun decodeTranslateHost(translateHost: String): String {
        val clean = translateHost.trim().lowercase(Locale.ROOT)
        val withoutSuffix = if (clean.endsWith(".translate.goog")) {
            clean.removeSuffix(".translate.goog")
        } else {
            clean
        }
        return withoutSuffix
            .replace("--", "\u0000")
            .replace("-", ".")
            .replace("\u0000", "-")
    }

    /**
     * Builds a direct un-framed Google Translate URL (.translate.goog) for a given original webpage URL.
     * Query parameters and fragments are preserved.
     */
    fun buildDirectTranslateUrl(originalUrl: String): String? {
        if (originalUrl.isBlank() || originalUrl.startsWith("about:")) return null
        val trimmed = originalUrl.trim()
        val uri = try {
            Uri.parse(trimmed)
        } catch (_: Exception) {
            return null
        }
        val host = uri.host ?: return null
        if (host.isBlank()) return null

        // If already translated, return trimmed
        if (host.lowercase(Locale.ROOT).endsWith(".translate.goog")) {
            return trimmed
        }

        val googHost = encodeTranslateHost(host)
        val path = if (uri.encodedPath.isNullOrEmpty()) "/" else uri.encodedPath
        val trParams = "_x_tr_sl=auto&_x_tr_tl=bn&_x_tr_hl=bn"
        val query = uri.encodedQuery
        val finalQuery = if (query.isNullOrEmpty()) trParams else "$query&$trParams"
        val fragment = if (uri.encodedFragment.isNullOrEmpty()) "" else "#${uri.encodedFragment}"
        return "https://$googHost$path?$finalQuery$fragment"
    }

    /**
     * Extracts the original URL from a translated URL (.translate.goog or legacy translate.google.com).
     */
    fun getOriginalUrlFromTranslation(url: String): String? {
        if (url.isBlank() || url.startsWith("about:")) return null
        val trimmed = url.trim()
        try {
            val uri = Uri.parse(trimmed)
            val host = uri.host?.lowercase(Locale.ROOT) ?: return null

            // Direct .translate.goog format
            if (host.endsWith(".translate.goog")) {
                val originalHost = decodeTranslateHost(host)
                val path = uri.encodedPath ?: ""
                val query = uri.query?.split("&")
                    ?.filterNot { it.startsWith("_x_tr_") }
                    ?.joinToString("&")
                    ?.takeIf { it.isNotBlank() }
                    ?.let { "?$it" } ?: ""
                val fragment = if (uri.encodedFragment.isNullOrBlank()) "" else "#${uri.encodedFragment}"
                return "https://$originalHost$path$query$fragment"
            }

            // Fallback for translate.google.com/translate?u=...
            if (host == "translate.google.com" || host.endsWith(".translate.google.com")) {
                val paramU = uri.getQueryParameter("u")
                if (!paramU.isNullOrBlank()) return paramU
            }
        } catch (_: Exception) {}
        return null
    }

    /**
     * Extracts original target URL from Google Translate proxy URL if present.
     */
    fun extractUnderlyingTargetUrl(url: String): String? {
        return getOriginalUrlFromTranslation(url)
    }

    /**
     * Detects if a URL is a search request from known search providers
     * (Google, Bing, DuckDuckGo, Yahoo, Yandex, Baidu, Ecosia, Startpage, Ask)
     * and extracts the clean search query string.
     *
     * Returns null if the URL is a regular web page, search engine homepage, or translation URL.
     */
    fun extractSearchEngineQuery(url: String): String? {
        if (url.isBlank()) return null
        val uri = try {
            Uri.parse(url)
        } catch (_: Exception) {
            return null
        }
        val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: return null
        if (scheme != "http" && scheme != "https") return null
        val host = uri.host?.lowercase(Locale.ROOT) ?: return null
        if (isTranslationHost(host)) return null
        val path = uri.path?.lowercase(Locale.ROOT) ?: ""

        return when {
            // Google Search (search results or web search home with query, excluding outbound /url redirects)
            isGoogleHost(host) && !path.startsWith("/url") && (path.contains("/search") || path.contains("/webhp") || ((path == "/" || path.isEmpty()) && uri.getQueryParameter("q") != null)) -> {
                uri.getQueryParameter("q")?.takeIf { it.isNotBlank() }
            }
            // Bing Search
            host.contains("bing.com") && (path.contains("/search") || uri.getQueryParameter("q") != null) -> {
                uri.getQueryParameter("q")?.takeIf { it.isNotBlank() }
            }
            // DuckDuckGo Search
            host.contains("duckduckgo.com") && uri.getQueryParameter("q") != null -> {
                uri.getQueryParameter("q")?.takeIf { it.isNotBlank() }
            }
            // Yahoo Search
            (host.contains("search.yahoo.com") || (host.contains("yahoo.com") && path.contains("/search"))) && uri.getQueryParameter("p") != null -> {
                uri.getQueryParameter("p")?.takeIf { it.isNotBlank() }
            }
            // Yandex Search
            host.contains("yandex.") && (path.contains("/search") || uri.getQueryParameter("text") != null) -> {
                (uri.getQueryParameter("text") ?: uri.getQueryParameter("query"))?.takeIf { it.isNotBlank() }
            }
            // Baidu Search
            host.contains("baidu.com") && (uri.getQueryParameter("wd") != null || uri.getQueryParameter("word") != null) -> {
                (uri.getQueryParameter("wd") ?: uri.getQueryParameter("word"))?.takeIf { it.isNotBlank() }
            }
            // Ecosia Search
            host.contains("ecosia.org") && path.contains("/search") && uri.getQueryParameter("q") != null -> {
                uri.getQueryParameter("q")?.takeIf { it.isNotBlank() }
            }
            // Startpage Search
            host.contains("startpage.com") && (path.contains("/search") || uri.getQueryParameter("query") != null || uri.getQueryParameter("q") != null) -> {
                (uri.getQueryParameter("query") ?: uri.getQueryParameter("q"))?.takeIf { it.isNotBlank() }
            }
            // Ask.com Search
            host.contains("ask.com") && (path.contains("/web") || path.contains("/search")) && uri.getQueryParameter("q") != null -> {
                uri.getQueryParameter("q")?.takeIf { it.isNotBlank() }
            }
            else -> null
        }
    }

    /**
     * Checks if the text (search query or URL) contains any user-defined custom keyword.
     * Utilizes pre-normalized cached keywords if provided to eliminate per-request allocations.
     * Returns the matched keyword name, or null if allowed.
     */
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

        if (normalizedKeywords != null && normalizedKeywords.isNotEmpty()) {
            for (kwNorm in normalizedKeywords) {
                if (kwNorm.isNotEmpty() && normalized.contains(kwNorm)) {
                    // Match original casing for display/reporting
                    return customKeywords.find { it.trim().equals(kwNorm, ignoreCase = true) } ?: kwNorm
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

    /**
     * Lightweight direct URL protection.
     * Evaluates custom keywords and high-confidence adult domains with fast host-first lookup.
     */
    fun checkDirectUrl(
        url: String,
        customKeywords: Set<String>,
        normalizedKeywords: Collection<String>? = null
    ): FilterResult {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return FilterResult.Allowed

        android.util.Log.d("DIAGNOSTIC", "DIRECT_URL_CHECK=checkDirectUrl")
        android.util.Log.d("DIAGNOSTIC", "URL=$trimmed")

        // 1. Custom Keyword Protection
        val blockedKw = isBlockedByCustomKeywords(trimmed, customKeywords, normalizedKeywords)
        if (blockedKw != null) {
            android.util.Log.e("DIAGNOSTIC", "BLOCK_FUNCTION=ProtectionEngine.isBlockedByCustomKeywords")
            android.util.Log.e("DIAGNOSTIC", "BLOCK_REASON=Custom Keyword: \"$blockedKw\"")
            android.util.Log.e("DIAGNOSTIC", "REQUEST_URL=$trimmed")
            android.util.Log.e("DIAGNOSTIC", "PROTECTION_RESULT=Blocked")
            return FilterResult.Blocked(
                reason = "Custom Keyword Protection",
                detail = "Blocked due to protected keyword: \"$blockedKw\""
            )
        }

        // 2. Direct Adult Domain Matching (O(1) host lookup first)
        val host = extractHost(trimmed)
        if (host != null && isTranslationHost(host)) {
            val underlyingUrl = extractUnderlyingTargetUrl(trimmed)
            android.util.Log.d("DIAGNOSTIC", "TRANSLATION_URL=$trimmed")
            android.util.Log.d("DIAGNOSTIC", "TRANSLATION_HOST=$host")
            android.util.Log.d("DIAGNOSTIC", "ORIGINAL_URL=$underlyingUrl")
            android.util.Log.d("DIAGNOSTIC", "DECODED_URL=$underlyingUrl")
            if (underlyingUrl != null) {
                return checkDirectUrl(underlyingUrl, customKeywords, normalizedKeywords)
            }
            android.util.Log.d("DIAGNOSTIC", "PROTECTION_RESULT=Allowed")
            android.util.Log.d("DIAGNOSTIC", "RESULT=Allowed")
            return FilterResult.Allowed
        }

        if (host != null && host.isNotEmpty()) {
            if (matchesDomainOrSubdomain(host, KNOWN_ADULT_DOMAINS)) {
                android.util.Log.e("DIAGNOSTIC", "BLOCK_FUNCTION=ProtectionEngine.checkDirectUrl (Adult Domain)")
                android.util.Log.e("DIAGNOSTIC", "BLOCK_REASON=Adult Content Protection")
                android.util.Log.e("DIAGNOSTIC", "REQUEST_URL=$trimmed")
                android.util.Log.e("DIAGNOSTIC", "PROTECTION_RESULT=Blocked")
                return FilterResult.Blocked(
                    reason = "Adult Content Protection",
                    detail = "Access to adult entertainment domains is permanently restricted."
                )
            }
        } else {
            // Fallback only for raw domain inputs where host couldn't be parsed
            val normalized = trimmed.lowercase(Locale.ROOT)
            for (domain in KNOWN_ADULT_DOMAINS) {
                if (normalized.contains(domain)) {
                    android.util.Log.e("DIAGNOSTIC", "BLOCK_FUNCTION=ProtectionEngine.checkDirectUrl (Adult Domain Fallback)")
                    android.util.Log.e("DIAGNOSTIC", "BLOCK_REASON=Adult Content Protection")
                    android.util.Log.e("DIAGNOSTIC", "REQUEST_URL=$trimmed")
                    android.util.Log.e("DIAGNOSTIC", "PROTECTION_RESULT=Blocked")
                    return FilterResult.Blocked(
                        reason = "Adult Content Protection",
                        detail = "Access to adult entertainment domains is permanently restricted."
                    )
                }
            }
        }

        android.util.Log.d("DIAGNOSTIC", "PROTECTION_RESULT=Allowed")
        android.util.Log.d("DIAGNOSTIC", "RESULT=Allowed")
        return FilterResult.Allowed
    }

    /**
     * Lightweight compatibility evaluator for input queries and direct URLs.
     */
    fun checkUrlOrQuery(
        input: String,
        customKeywords: Set<String>,
        normalizedKeywords: Collection<String>? = null
    ): FilterResult {
        return checkDirectUrl(input, customKeywords, normalizedKeywords)
    }

    /**
     * Extracts lowercase host name from URL or web address.
     * Avoids heavy Uri object creation and extra string allocations.
     */
    fun extractHost(url: String): String? {
        if (url.isEmpty()) return null
        var start = 0
        val schemeEnd = url.indexOf("://")
        if (schemeEnd != -1) {
            start = schemeEnd + 3
        } else if (url.startsWith("//")) {
            start = 2
        }

        // Skip userinfo if present (e.g. user:pass@host)
        val atIndex = url.indexOf('@', start)
        val nextSlash = url.indexOf('/', start)
        if (atIndex != -1 && (nextSlash == -1 || atIndex < nextSlash)) {
            start = atIndex + 1
        }

        var end = start
        while (end < url.length) {
            val c = url[end]
            if (c == '/' || c == '?' || c == '#' || c == ':') {
                break
            }
            end++
        }
        if (start >= end) return null
        return url.substring(start, end).lowercase(Locale.ROOT)
    }

    /**
     * Checks if a host matches a domain or any of its parent subdomains against a HashSet.
     */
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

    /**
     * Checks if a network request is directed at a known advertisement network.
     * Fast path: Checks host and subdomains against HashSet first.
     * Slow path: Only checks query if host did not match and uri has query parameters.
     */
    fun isAdRequest(uri: Uri): Boolean {
        val host = uri.host?.lowercase(Locale.ROOT)
        if (host != null && host.isNotEmpty()) {
            if (matchesDomainOrSubdomain(host, KNOWN_AD_DOMAINS)) {
                return true
            }
        }
        // Only inspect the full URL if query parameters exist (potential ad redirect/tracking URLs)
        if (uri.query != null) {
            val url = uri.toString()
            val normalized = url.lowercase(Locale.ROOT)
            for (adDomain in KNOWN_AD_DOMAINS) {
                if (normalized.contains(adDomain)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Checks if a network request is directed at a known advertisement network.
     * Fast path: Checks host and subdomains against HashSet first.
     */
    fun isAdRequest(url: String): Boolean {
        val host = extractHost(url)
        if (host != null && host.isNotEmpty()) {
            if (matchesDomainOrSubdomain(host, KNOWN_AD_DOMAINS)) {
                return true
            }
        }
        // Only inspect full URL string if host was not extracted or query exists
        if (host == null || url.indexOf('?') != -1) {
            val normalized = url.lowercase(Locale.ROOT)
            for (adDomain in KNOWN_AD_DOMAINS) {
                if (normalized.contains(adDomain)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Determines whether a download should be allowed or blocked.
     * Allowed: Images, PDF
     * Blocked: Video, MP3/Audio, APK, and any other non-allowed formats.
     */
    fun checkDownloadType(
        url: String,
        mimeType: String?,
        contentDisposition: String?
    ): DownloadStatus {
        val cleanMime = mimeType?.trim()?.lowercase(Locale.ROOT) ?: ""
        val extension = extractExtension(url, contentDisposition)

        // Check Videos
        if (cleanMime.startsWith("video/") || BLOCKED_VIDEO_EXTS.contains(extension)) {
            return DownloadStatus.BLOCKED_VIDEO
        }

        // Check Audio/MP3
        if (cleanMime.startsWith("audio/") || cleanMime == "application/ogg" || BLOCKED_AUDIO_EXTS.contains(extension)) {
            return DownloadStatus.BLOCKED_AUDIO
        }

        // Check APK
        if (cleanMime == "application/vnd.android.package-archive" || BLOCKED_APK_EXTS.contains(extension)) {
            return DownloadStatus.BLOCKED_APK
        }

        // Check Allowed Images
        if (cleanMime.startsWith("image/") || ALLOWED_IMAGE_EXTS.contains(extension)) {
            return DownloadStatus.ALLOWED_IMAGE
        }

        // Check Allowed PDF
        if (cleanMime == "application/pdf" || ALLOWED_PDF_EXTS.contains(extension)) {
            return DownloadStatus.ALLOWED_PDF
        }

        // Any other type not explicitly allowed (Images/PDF) is blocked
        return DownloadStatus.BLOCKED_OTHER
    }

    /**
     * Extracts extension from URL or Content-Disposition.
     */
    fun extractExtension(url: String, contentDisposition: String?): String {
        if (contentDisposition != null && contentDisposition.contains("filename=", ignoreCase = true)) {
            val filenamePart = contentDisposition.substringAfter("filename=", "")
                .replace("\"", "").trim()
            val ext = filenamePart.substringAfterLast('.', "")
            if (ext.isNotEmpty()) {
                return ext.lowercase(Locale.ROOT)
            }
        }

        val cleanUrl = url.substringBefore('?').substringBefore('#')
        val lastSegment = cleanUrl.substringAfterLast('/', "")
        val ext = lastSegment.substringAfterLast('.', "")
        return ext.lowercase(Locale.ROOT)
    }
}
