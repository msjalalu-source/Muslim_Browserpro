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

    sealed class Result {
        object Allowed : Result()
        data class Blocked(val reason: String) : Result()
    }

    /**
     * Evaluates a download request (from DownloadListener or explicit file download).
     * Follows the strict priority rules:
     * 1. APK -> GitHub allowlist rule (https://github.com/msjalalu-source/...)
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

        // 1. APK -> GitHub allowlist rule
        val isApk = cleanMime == "application/vnd.android.package-archive" || APK_EXTS.contains(extension)
        if (isApk) {
            return if (isAllowedGitHubApkUrl(url)) {
                Result.Allowed
            } else {
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
            return !isAllowedGitHubApkUrl(url)
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
     * - hostname exactly "github.com"
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
            if (host != "github.com") return false
            val firstSegment = uri.pathSegments?.firstOrNull()
            firstSegment == "msjalalu-source"
        } catch (_: Exception) {
            false
        }
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
