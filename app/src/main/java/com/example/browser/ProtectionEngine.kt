package com.example.browser

import java.util.Locale

/**
 * Ultra-lightweight, event-driven protection engine for Focus Shield Browser.
 * Handles adult content filtering, custom keyword matching, download type restrictions,
 * and lightweight ad request detection.
 *
 * Uses direct hash-sets and simple string checks without heavy dependencies or background services.
 */
object ProtectionEngine {

    // Common adult domains (sample high-impact list, normalized lowercase)
    private val KNOWN_ADULT_DOMAINS = hashSetOf(
        "pornhub.com",
        "xvideos.com",
        "xnxx.com",
        "xhamster.com",
        "redtube.com",
        "youporn.com",
        "chaturbate.com",
        "stripchat.com",
        "livejasmin.com",
        "bongacams.com",
        "onlyfans.com",
        "cam4.com",
        "camsoda.com",
        "adultfriendfinder.com",
        "brazzers.com",
        "beeg.com",
        "spankbang.com",
        "tubegalore.com",
        "eporner.com",
        "daftsex.com",
        "fuq.com",
        "heavy-r.com",
        "motherless.com",
        "hqporner.com",
        "porn.com",
        "xxx.com",
        "hentaihaven.xxx",
        "nhentai.net",
        "gelbooru.com",
        "rule34.xxx",
        "e-hentai.org"
    )

    // Keywords that indicate adult content in URLs or search queries
    private val ADULT_KEYWORDS = arrayOf(
        "porn",
        "xxx",
        "nsfw",
        "erotic",
        "hentai",
        "nude",
        "nudity",
        "sexcam",
        "camgirl",
        "blowjob",
        "hardcore",
        "gangbang",
        "milf",
        "fetish",
        "shemale",
        "anal",
        "cumshot",
        "masturbat"
    )

    // Built-in keywords requiring whole-word/token boundary matching
    private val BUILTIN_WHOLE_WORD_KEYWORDS = arrayOf(
        "x",
        "browser",
        "browsers",
        "browsering",
        "browsered"
    )

    // Built-in blocked keywords (matched via case-insensitive contains)
    private val BUILTIN_BLOCKED_KEYWORDS = arrayOf(
        "aashiq banaya",
        "hot",
        "adult",
        "porn",
        "sex",
        "xxx",
        "18+",
        "intimate",
        "kiss"
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
     * Checks whether a navigation URL or search query violates Adult Protection
     * or any user-defined Custom Keywords.
     */
    fun checkUrlOrQuery(
        input: String,
        customKeywords: Set<String>
    ): FilterResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return FilterResult.Allowed
        val normalized = trimmed.lowercase(Locale.ROOT).replace("%20", " ")

        // 1. Adult Content Protection (Permanently Enabled)
        // Fast O(1) host & subdomain check if input contains a host
        val host = extractHost(normalized)
        if (host != null && host.isNotEmpty() && matchesDomainOrSubdomain(host, KNOWN_ADULT_DOMAINS)) {
            return FilterResult.Blocked(
                reason = "Adult Content Protection",
                detail = "Access to adult entertainment domains is permanently restricted."
            )
        }

        // Fallback domain check in search query or raw input
        for (domain in KNOWN_ADULT_DOMAINS) {
            if (normalized.contains(domain)) {
                return FilterResult.Blocked(
                    reason = "Adult Content Protection",
                    detail = "Access to adult entertainment domains is permanently restricted."
                )
            }
        }

        // Check adult keywords in search query or URL
        for (kw in ADULT_KEYWORDS) {
            if (normalized.contains(kw)) {
                return FilterResult.Blocked(
                    reason = "Adult Content Protection",
                    detail = "Content containing explicit or adult terms is blocked."
                )
            }
        }

        // 2. Built-in Whole-Word Keywords Protection ("x", "browser", and variants)
        for (word in BUILTIN_WHOLE_WORD_KEYWORDS) {
            if (containsWholeWord(normalized, word)) {
                return FilterResult.Blocked(
                    reason = "Protected Content Policy",
                    detail = "Blocked due to protected keyword: \"$word\""
                )
            }
        }

        // 3. Built-in Blocked Keywords Protection
        for (kw in BUILTIN_BLOCKED_KEYWORDS) {
            if (normalized.contains(kw)) {
                return FilterResult.Blocked(
                    reason = "Protected Content Policy",
                    detail = "Blocked due to protected keyword: \"$kw\""
                )
            }
        }

        // 4. Custom Keyword Protection
        if (customKeywords.isNotEmpty()) {
            for (customKw in customKeywords) {
                val kwNormalized = customKw.trim().lowercase(Locale.ROOT)
                if (kwNormalized.isNotEmpty() && !isBuiltInKeyword(kwNormalized) && normalized.contains(kwNormalized)) {
                    return FilterResult.Blocked(
                        reason = "Custom Keyword Protection",
                        detail = "Blocked due to protected keyword: \"$customKw\""
                    )
                }
            }
        }

        return FilterResult.Allowed
    }

    /**
     * Checks if [word] appears in [text] as a standalone whole word (token boundary).
     * Non-alphanumeric characters (including start/end of string, spaces, punctuation, slashes)
     * serve as word boundaries.
     */
    fun containsWholeWord(text: String, word: String): Boolean {
        val wordLen = word.length
        if (wordLen == 0 || text.isEmpty()) return false
        var startIndex = 0
        while (true) {
            val index = text.indexOf(word, startIndex)
            if (index == -1) return false
            val prevCharOk = (index == 0) || !Character.isLetterOrDigit(text[index - 1])
            val nextIndex = index + wordLen
            val nextCharOk = (nextIndex == text.length) || !Character.isLetterOrDigit(text[nextIndex])
            if (prevCharOk && nextCharOk) {
                return true
            }
            startIndex = index + 1
        }
    }

    /**
     * Checks whether a keyword is already part of the built-in protected keywords.
     */
    fun isBuiltInKeyword(keyword: String): Boolean {
        val lower = keyword.trim().lowercase(Locale.ROOT)
        if (lower.isEmpty()) return false
        for (w in BUILTIN_WHOLE_WORD_KEYWORDS) {
            if (w == lower) return true
        }
        for (w in BUILTIN_BLOCKED_KEYWORDS) {
            if (w == lower) return true
        }
        for (w in ADULT_KEYWORDS) {
            if (w == lower) return true
        }
        return false
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
     * Takes O(1) set lookups proportional to subdomain depth rather than O(N) string searches.
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
     * Uses fast host matching first for maximum performance on frequent subresource requests.
     */
    fun isAdRequest(url: String): Boolean {
        val host = extractHost(url)
        if (host != null && host.isNotEmpty()) {
            if (matchesDomainOrSubdomain(host, KNOWN_AD_DOMAINS)) {
                return true
            }
        }
        // Fallback for relative or malformed URLs
        val normalized = url.lowercase(Locale.ROOT)
        for (adDomain in KNOWN_AD_DOMAINS) {
            if (normalized.contains(adDomain)) {
                return true
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
        // Try from content-disposition filename first
        if (contentDisposition != null && contentDisposition.contains("filename=", ignoreCase = true)) {
            val filenamePart = contentDisposition.substringAfter("filename=", "")
                .replace("\"", "").trim()
            val ext = filenamePart.substringAfterLast('.', "")
            if (ext.isNotEmpty()) {
                return ext.lowercase(Locale.ROOT)
            }
        }

        // Extract from URL query-free path
        val cleanUrl = url.substringBefore('?').substringBefore('#')
        val lastSegment = cleanUrl.substringAfterLast('/', "")
        val ext = lastSegment.substringAfterLast('.', "")
        return ext.lowercase(Locale.ROOT)
    }
}
