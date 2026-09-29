package com.muslim.browser.pro.browser

import android.net.Uri
import java.util.Locale

/**
 * Centralized Download Policy for Muslim Browser Pro.
 * Evaluates all download requests against a single, unified priority ruleset:
 * 1. APK -> Allowed ONLY from https://github.com/msjalalu-source/...
 * 2. Audio -> ALLOW (MP3, WAV, OGG, M4A, AAC, FLAC, and other common audio)
 * 3. Video -> BLOCK (mp4, mkv, webm, avi, mov, etc.)
 * 4. Other -> Images & PDFs allowed, other unrecognized file types blocked.
 */
object DownloadPolicy {

    private val VIDEO_EXTS = hashSetOf(
        "mp4", "mkv", "webm", "avi", "mov", "m4v", "wmv", "flv", "3gp"
    )

    private val AUDIO_EXTS = hashSetOf(
        "mp3", "m4a", "wav", "flac", "ogg", "aac", "wma", "opus", "mid", "midi"
    )

    private val APK_EXTS = hashSetOf(
        "apk", "xapk", "apks"
    )

    private val ALLOWED_IMAGE_EXTS = hashSetOf(
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "ico"
    )

    private val ALLOWED_PDF_EXTS = hashSetOf(
        "pdf"
    )

    data class TrustedApkOrigin(
        val originalUrl: String,
        val fileName: String,
        val timestamp: Long
    )

    private const val TRUSTED_ORIGIN_TIMEOUT_MS = 20_000L // 20 seconds validity window for GitHub redirect

    @Volatile
    private var activeTrustedOrigin: TrustedApkOrigin? = null

    sealed class Result {
        object Allowed : Result()
        data class Blocked(val reason: String) : Result()
    }

    /**
     * Records a temporary trusted APK origin when a navigation or download starts
     * from a verified "github.com/msjalalu-source" APK URL.
     */
    fun recordTrustedOrigin(url: String) {
        if (!isAllowedGitHubApkUrl(url)) return
        val fileName = extractFileName(url, null)
        activeTrustedOrigin = TrustedApkOrigin(
            originalUrl = url,
            fileName = fileName,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Clears any active trusted APK origin.
     */
    fun clearTrustedOrigin() {
        activeTrustedOrigin = null
    }

    /**
     * Verifies if an incoming download URL is a legitimate GitHub CDN redirect
     * originating from a previously validated "msjalalu-source" GitHub origin.
     * A CDN URL by itself is NEVER sufficient to allow an APK.
     * Consumes (clears) the trusted origin so it cannot be reused.
     */
    fun validateAndConsumeTrustedOrigin(
        url: String,
        contentDisposition: String? = null
    ): Boolean {
        val origin = activeTrustedOrigin ?: return false
        val now = System.currentTimeMillis()

        // 1. Check expiration (TTL)
        if (now - origin.timestamp > TRUSTED_ORIGIN_TIMEOUT_MS) {
            clearTrustedOrigin()
            return false
        }

        // 2. A CDN URL by itself is never allowed; verify host is a recognized GitHub release storage CDN
        val uri = try {
            Uri.parse(url.trim())
        } catch (_: Exception) {
            clearTrustedOrigin()
            return false
        }
        val host = uri.host?.lowercase(Locale.ROOT)
        if (!isGitHubCdnHost(host)) {
            clearTrustedOrigin()
            return false
        }

        // 3. Verify file name matching between original request and incoming download
        val incomingFileName = extractFileName(url, contentDisposition)
        if (origin.fileName.isNotEmpty() && incomingFileName.isNotEmpty()) {
            if (!incomingFileName.equals(origin.fileName, ignoreCase = true)) {
                clearTrustedOrigin()
                return false
            }
        }

        // Successfully validated! Clear the single-use trusted origin immediately
        clearTrustedOrigin()
        return true
    }

    /**
     * Checks if a host belongs to GitHub's release asset CDN infrastructure.
     */
    fun isGitHubCdnHost(host: String?): Boolean {
        if (host.isNullOrBlank()) return false
        val clean = host.lowercase(Locale.ROOT)
        return clean == "release-assets.githubusercontent.com" ||
                clean == "objects.githubusercontent.com" ||
                clean == "github-production-release-asset-2e65be.s3.amazonaws.com" ||
                (clean.endsWith(".githubusercontent.com") && !clean.contains(" ")) ||
                clean.contains("github-production-release-asset")
    }

    /**
     * Evaluates a download request (from DownloadListener or explicit file download).
     * Follows the strict priority rules:
     * 1. APK -> GitHub allowlist rule (msjalalu-source only, direct or legitimate validated redirect)
     * 2. Audio -> ALLOW (MP3, WAV, OGG, M4A, AAC, FLAC, etc.)
     * 3. Video -> BLOCK (existing video download rule)
     * 4. Other files -> Existing download behavior (Images & PDFs allowed, others blocked)
     */
    fun evaluate(
        url: String,
        mimeType: String? = null,
        contentDisposition: String? = null
    ): Result {
        val cleanMime = mimeType?.trim()?.lowercase(Locale.ROOT) ?: ""
        val extension = extractExtension(url, contentDisposition)

        // 1. APK -> GitHub allowlist rule (msjalalu-source only)
        val isApk = cleanMime == "application/vnd.android.package-archive" || APK_EXTS.contains(extension)
        if (isApk) {
            val isDirectAllowed = isAllowedGitHubApkUrl(url)
            val isRedirectAllowed = if (!isDirectAllowed) {
                validateAndConsumeTrustedOrigin(url, contentDisposition)
            } else {
                clearTrustedOrigin()
                true
            }

            return if (isDirectAllowed || isRedirectAllowed) {
                Result.Allowed
            } else {
                clearTrustedOrigin()
                Result.Blocked("APK downloads are restricted to verified sources.")
            }
        }

        // 2. Audio -> ALLOW
        val isAudio = cleanMime.startsWith("audio/") || cleanMime == "application/ogg" || AUDIO_EXTS.contains(extension)
        if (isAudio) {
            return Result.Allowed
        }

        // 3. Video -> BLOCK (existing video download rule)
        val isVideo = cleanMime.startsWith("video/") || VIDEO_EXTS.contains(extension)
        if (isVideo) {
            return Result.Blocked("Video downloads are blocked.")
        }

        // 4. Other files -> Existing download behavior (Images & PDFs allowed, others blocked)
        val isImage = cleanMime.startsWith("image/") || ALLOWED_IMAGE_EXTS.contains(extension)
        if (isImage) {
            return Result.Allowed
        }

        val isPdf = cleanMime == "application/pdf" || ALLOWED_PDF_EXTS.contains(extension)
        if (isPdf) {
            return Result.Allowed
        }

        return Result.Blocked("This file type is blocked.")
    }

    /**
     * Checks if a direct navigation URL targets a blocked downloadable file
     * (e.g. direct link to a video file, or an unauthorized APK).
     * Returns true if it should be blocked from loading in the browser.
     */
    fun shouldBlockUrlNavigation(url: String): Boolean {
        val extension = extractExtension(url, null)
        if (extension.isEmpty()) return false

        // 1. Direct APK navigation
        if (APK_EXTS.contains(extension)) {
            if (isAllowedGitHubApkUrl(url)) {
                recordTrustedOrigin(url)
                return false
            }
            // If it's a redirect matching the active trusted origin, allow WebView navigation to proceed to DownloadListener
            val origin = activeTrustedOrigin
            if (origin != null && System.currentTimeMillis() - origin.timestamp <= TRUSTED_ORIGIN_TIMEOUT_MS) {
                val host = try { Uri.parse(url).host?.lowercase(Locale.ROOT) } catch (_: Exception) { null }
                if (isGitHubCdnHost(host)) {
                    val incomingFileName = extractFileName(url, null)
                    if (origin.fileName.isEmpty() || incomingFileName.isEmpty() || incomingFileName.equals(origin.fileName, ignoreCase = true)) {
                        return false
                    }
                }
            }
            return true
        }

        // 2. Audio is ALLOWED
        if (AUDIO_EXTS.contains(extension)) {
            return false
        }

        // 3. Direct Video navigation is BLOCKED
        if (VIDEO_EXTS.contains(extension)) {
            return true
        }

        return false
    }

    /**
     * Validates whether an APK URL is strictly from:
     * - hostname exactly "github.com" or "www.github.com"
     * - URL path's first owner segment exactly "msjalalu-source"
     *
     * ALLOW: https://github.com/msjalalu-source/...
     * BLOCK: https://github.com/, https://github.com/other-user/..., https://github.com/another-account/...
     */
    fun isAllowedGitHubApkUrl(url: String): Boolean {
        if (url.isBlank()) return false
        return try {
            val uri = Uri.parse(url.trim())
            val host = uri.host?.lowercase(Locale.ROOT)
            if (host != "github.com" && host != "www.github.com") return false
            val firstSegment = uri.pathSegments?.firstOrNull()
            firstSegment == "msjalalu-source"
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Extracts filename from Content-Disposition header, query parameters, or URL path.
     */
    fun extractFileName(url: String, contentDisposition: String? = null): String {
        val header = contentDisposition ?: extractDispositionFromQuery(url)
        if (!header.isNullOrBlank()) {
            if (header.contains("filename*=", ignoreCase = true)) {
                val raw = header.substringAfter("filename*=", "")
                    .substringAfter("''", "")
                    .substringBefore(';')
                    .trim('"', '\'', ' ')
                if (raw.isNotEmpty()) {
                    return decodeFileName(raw)
                }
            }
            if (header.contains("filename=", ignoreCase = true)) {
                val raw = header.substringAfter("filename=", "")
                    .substringBefore(';')
                    .trim('"', '\'', ' ')
                if (raw.isNotEmpty()) {
                    return decodeFileName(raw)
                }
            }
        }

        val cleanUrl = url.substringBefore('?').substringBefore('#')
        val lastSegment = cleanUrl.substringAfterLast('/', "").trim('"', '\'', ' ')
        if (lastSegment.isNotEmpty()) {
            return decodeFileName(lastSegment)
        }
        return ""
    }

    private fun extractDispositionFromQuery(url: String): String? {
        val lower = url.lowercase(Locale.ROOT)
        return when {
            lower.contains("response-content-disposition=") -> {
                url.substringAfter("response-content-disposition=", "")
                    .substringBefore('&')
            }
            lower.contains("rscd=") -> {
                url.substringAfter("rscd=", "")
                    .substringBefore('&')
            }
            else -> null
        }?.let {
            try {
                java.net.URLDecoder.decode(it, "UTF-8")
            } catch (_: Exception) {
                it
            }
        }
    }

    private fun decodeFileName(name: String): String {
        val decoded = try {
            java.net.URLDecoder.decode(name, "UTF-8")
        } catch (_: Exception) {
            name
        }
        return decoded.trim().lowercase(Locale.ROOT)
    }

    /**
     * Extracts extension from Content-Disposition filename or URL path.
     */
    fun extractExtension(url: String, contentDisposition: String? = null): String {
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
