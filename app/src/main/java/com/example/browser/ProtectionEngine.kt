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
        val normalized = input.trim().lowercase(Locale.ROOT)
        if (normalized.isEmpty()) return FilterResult.Allowed

        // 1. Adult Content Protection (Permanently Enabled)
        // Check known adult domains
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
            if (containsWord(normalized, kw)) {
                return FilterResult.Blocked(
                    reason = "Adult Content Protection",
                    detail = "Content containing explicit or adult terms is blocked."
                )
            }
        }

        // 2. Custom Keyword Protection
        for (customKw in customKeywords) {
            val kwNormalized = customKw.trim().lowercase(Locale.ROOT)
            if (kwNormalized.isNotEmpty() && normalized.contains(kwNormalized)) {
                return FilterResult.Blocked(
                    reason = "Custom Keyword Protection",
                    detail = "Blocked due to protected keyword: \"$customKw\""
                )
            }
        }

        return FilterResult.Allowed
    }

    /**
     * Case-insensitive substring/word check helper.
     */
    private fun containsWord(target: String, word: String): Boolean {
        return target.contains(word)
    }

    /**
     * Checks if a network request is directed at a known advertisement network.
     */
    fun isAdRequest(url: String): Boolean {
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
