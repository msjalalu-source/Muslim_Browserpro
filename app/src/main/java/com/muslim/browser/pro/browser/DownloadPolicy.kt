package com.muslim.browser.pro.browser

import android.net.Uri
import java.net.URLDecoder
import java.util.Locale

object DownloadPolicy {

    sealed class Result {
        data object Allowed : Result()
        data class Blocked(val reason: String) : Result()
    }

    data class TrustedApkOrigin(
        val originalUrl: String,
        val fileName: String,
        val timestamp: Long
    )

    private const val TRUSTED_ORIGIN_TIMEOUT_MS = 20000L

    @Volatile
    private var activeTrustedOrigin: TrustedApkOrigin? = null

    val VIDEO_EXTS: HashSet<String> = hashSetOf(
        "mp4", "mkv", "webm", "avi", "mov", "m4v", "wmv", "flv", "3gp",
        "ts", "mpg", "mpeg", "ogv", "vob", "m2ts"
    )

    val APK_EXTS: HashSet<String> = hashSetOf(
        "apk", "xapk", "apks"
    )

    val AUDIO_EXTS: HashSet<String> = hashSetOf(
        "mp3", "wav", "ogg", "m4a", "aac", "flac"
    )

    val DOWNLOADABLE_EXTS: HashSet<String> = hashSetOf(
        "apk", "xapk", "apks", "zip", "tar", "gz", "gzip", "tgz", "bz2", "xz",
        "rar", "7z", "iso", "dmg", "pdf", "epub", "doc", "docx", "xls", "xlsx",
        "ppt", "pptx", "csv", "txt", "rtf", "odt", "ods", "odp"
    )

    private fun decodeFileName(name: String): String {
        val decoded = try {
            URLDecoder.decode(name, "UTF-8")
        } catch (_: Exception) {
            name
        }
        return decoded.trim()
    }

    private fun extractDispositionFromQuery(url: String): String? {
        val lower = url.lowercase(Locale.ROOT)
        val disp = if (lower.contains("response-content-disposition=")) {
            url.substringAfter("response-content-disposition=").substringBefore('&')
        } else if (lower.contains("rscd=")) {
            url.substringAfter("rscd=").substringBefore('&')
        } else {
            null
        }
        return if (disp != null) {
            try {
                URLDecoder.decode(disp, "UTF-8")
            } catch (_: Exception) {
                disp
            }
        } else null
    }

    fun extractFileName(url: String, contentDisposition: String? = null): String {
        val disp = contentDisposition ?: extractDispositionFromQuery(url)
        if (!disp.isNullOrBlank()) {
            if (disp.contains("filename*=", ignoreCase = true)) {
                val raw = disp.substringAfter("filename*=").substringAfter("''").substringBefore(';').trim('"', '\'', ' ')
                if (raw.isNotEmpty()) {
                    return decodeFileName(raw)
                }
            }
            if (disp.contains("filename=", ignoreCase = true)) {
                val raw = disp.substringAfter("filename=").substringBefore(';').trim('"', '\'', ' ')
                if (raw.isNotEmpty()) {
                    return decodeFileName(raw)
                }
            }
        }
        val clean = url.substringBefore('?').substringBefore('#').trim()
        val candidate = clean.substringAfterLast('/')
        if (candidate.contains('.')) {
            return decodeFileName(candidate)
        }
        val query = url.substringAfter('?', "").substringBefore('#')
        if (query.isNotEmpty()) {
            for (param in query.split('&')) {
                val value = param.substringAfter('=', "")
                if (value.isNotEmpty()) {
                    val decoded = decodeFileName(value).substringAfterLast('/')
                    if (decoded.contains('.')) {
                        return decoded
                    }
                }
            }
        }
        return decodeFileName(candidate)
    }

    fun extractExtension(url: String, contentDisposition: String? = null): String {
        val fileName = extractFileName(url, contentDisposition)
        val ext = fileName.substringAfterLast('.', "")
        if (ext.isNotEmpty() && ext != fileName) {
            return ext.lowercase(Locale.ROOT)
        }
        val clean = url.substringBefore('?').substringBefore('#').substringAfterLast('/').substringAfterLast('.')
        return clean.lowercase(Locale.ROOT)
    }

    fun isGitHubCdnHost(host: String?): Boolean {
        if (host.isNullOrBlank()) return false
        val h = host.lowercase(Locale.ROOT)
        return h == "release-assets.githubusercontent.com" ||
            h == "objects.githubusercontent.com" ||
            h == "github-production-release-asset-2e65be.s3.amazonaws.com" ||
            (h.endsWith(".githubusercontent.com") && h.contains("")) ||
            h.contains("github-production-release-asset")
    }

    const val TRUSTED_GITHUB_ACCOUNT = "msjalalu-source"

    fun isAllowedGitHubApkUrl(url: String): Boolean {
        if (url.isBlank()) return false
        return try {
            val uri = Uri.parse(url.trim())
            val host = uri.host?.lowercase(Locale.ROOT)
            if (host != "github.com") return false
            val segments = uri.pathSegments
            segments?.firstOrNull() == TRUSTED_GITHUB_ACCOUNT
        } catch (_: Exception) {
            false
        }
    }

    private fun matchesExtensionOrPath(
        url: String,
        contentDisposition: String?,
        extensions: Set<String>
    ): Boolean {
        if (!contentDisposition.isNullOrBlank()) {
            val ext = extractExtension("", contentDisposition)
            if (extensions.contains(ext)) return true
        }
        val extFromUrl = extractExtension(url, null)
        if (extensions.contains(extFromUrl)) return true
        val extFromDisp = extractFileName(url, contentDisposition).substringAfterLast('.')
        if (extensions.contains(extFromDisp)) return true

        val cleanPath = try {
            Uri.parse(url.trim()).path?.lowercase(Locale.ROOT) ?: ""
        } catch (_: Exception) { "" }

        for (ext in extensions) {
            if (cleanPath.endsWith(".$ext") || cleanPath.contains(".$ext/") || cleanPath.contains(".$ext?")) {
                return true
            }
        }
        return false
    }

    fun isVideo(url: String, mimeType: String? = null, contentDisposition: String? = null): Boolean {
        val mime = mimeType?.trim()?.lowercase(Locale.ROOT) ?: ""
        if (mime.startsWith("video/")) return true
        return matchesExtensionOrPath(url, contentDisposition, VIDEO_EXTS)
    }

    fun isApk(url: String, mimeType: String? = null, contentDisposition: String? = null): Boolean {
        val mime = mimeType?.trim()?.lowercase(Locale.ROOT) ?: ""
        if (mime == "application/vnd.android.package-archive") return true
        return matchesExtensionOrPath(url, contentDisposition, APK_EXTS)
    }

    fun isAudio(url: String, mimeType: String? = null, contentDisposition: String? = null): Boolean {
        val mime = mimeType?.trim()?.lowercase(Locale.ROOT) ?: ""
        if (mime.startsWith("audio/") || mime == "application/ogg") return true
        return matchesExtensionOrPath(url, contentDisposition, AUDIO_EXTS)
    }

    val ZIP_EXTS: HashSet<String> = hashSetOf(
        "zip"
    )

    fun isZip(url: String, mimeType: String? = null, contentDisposition: String? = null): Boolean {
        val mime = mimeType?.trim()?.lowercase(Locale.ROOT) ?: ""
        if (mime == "application/zip" ||
            mime == "application/x-zip-compressed" ||
            mime == "application/x-zip"
        ) return true
        return matchesExtensionOrPath(url, contentDisposition, ZIP_EXTS)
    }

    fun isDownloadableFileUrl(
        url: String,
        mimeType: String? = null,
        contentDisposition: String? = null
    ): Boolean {
        if (url.isBlank()) return false
        if (isVideo(url, mimeType, contentDisposition)) return false

        val mime = mimeType?.trim()?.lowercase(Locale.ROOT) ?: ""
        if (mime == "application/zip" ||
            mime == "application/x-zip-compressed" ||
            mime == "application/x-zip" ||
            mime == "application/pdf" ||
            mime == "application/octet-stream"
        ) {
            return true
        }

        if (!contentDisposition.isNullOrBlank()) {
            val dispExt = extractExtension("", contentDisposition)
            if (DOWNLOADABLE_EXTS.contains(dispExt) || AUDIO_EXTS.contains(dispExt)) {
                return true
            }
        }

        val cleanUrl = url.substringBefore('?').substringBefore('#').lowercase(Locale.ROOT)
        val fileName = cleanUrl.substringAfterLast('/')
        if (fileName.contains('.')) {
            val ext = fileName.substringAfterLast('.')
            if (DOWNLOADABLE_EXTS.contains(ext) || AUDIO_EXTS.contains(ext)) {
                return true
            }
        }

        val lowerUrl = url.lowercase(Locale.ROOT)
        for (ext in DOWNLOADABLE_EXTS) {
            if (lowerUrl.contains(".$ext") || lowerUrl.contains("=$ext") || lowerUrl.contains("%2e$ext")) {
                return true
            }
        }

        return false
    }

    fun recordTrustedOrigin(url: String) {
        if (!isAllowedGitHubApkUrl(url)) return
        val fileName = extractFileName(url, null)
        activeTrustedOrigin = TrustedApkOrigin(url, fileName, System.currentTimeMillis())
    }

    fun clearTrustedOrigin() {
        activeTrustedOrigin = null
    }

    fun validateAndConsumeTrustedOrigin(url: String, contentDisposition: String? = null): Boolean {
        val origin = activeTrustedOrigin ?: return false
        if (System.currentTimeMillis() - origin.timestamp > TRUSTED_ORIGIN_TIMEOUT_MS) {
            clearTrustedOrigin()
            return false
        }
        return try {
            val host = Uri.parse(url.trim()).host?.lowercase(Locale.ROOT)
            if (!isGitHubCdnHost(host)) {
                clearTrustedOrigin()
                return false
            }
            val fileName = extractFileName(url, contentDisposition)
            if (origin.fileName.isNotEmpty() && fileName.isNotEmpty() && !origin.fileName.equals(fileName, ignoreCase = true)) {
                clearTrustedOrigin()
                return false
            }
            clearTrustedOrigin()
            true
        } catch (_: Exception) {
            clearTrustedOrigin()
            false
        }
    }

    fun evaluate(url: String, mimeType: String? = null, contentDisposition: String? = null): Result {
        if (isApk(url, mimeType, contentDisposition)) {
            val isAllowedGitHub = isAllowedGitHubApkUrl(url)
            val isTrusted = if (!isAllowedGitHub) {
                validateAndConsumeTrustedOrigin(url, contentDisposition)
            } else {
                clearTrustedOrigin()
                true
            }
            return if (isAllowedGitHub || isTrusted) {
                Result.Allowed
            } else {
                clearTrustedOrigin()
                Result.Blocked("APK downloads are restricted to verified sources.")
            }
        }
        if (isVideo(url, mimeType, contentDisposition)) {
            return Result.Blocked("Video downloads are blocked.")
        }
        return Result.Allowed
    }

    fun shouldBlockUrlNavigation(url: String): Boolean {
        if (isVideo(url)) return true
        if (isApk(url)) {
            if (isAllowedGitHubApkUrl(url)) {
                recordTrustedOrigin(url)
                return false
            }
            val origin = activeTrustedOrigin ?: return true
            if (System.currentTimeMillis() - origin.timestamp > TRUSTED_ORIGIN_TIMEOUT_MS) {
                return true
            }
            return try {
                val host = Uri.parse(url).host?.lowercase(Locale.ROOT)
                if (isGitHubCdnHost(host)) {
                    val fileName = extractFileName(url, null)
                    if (origin.fileName.isNotEmpty() && fileName.isNotEmpty() && !origin.fileName.equals(fileName, ignoreCase = true)) {
                        return true
                    }
                    false
                } else {
                    true
                }
            } catch (_: Exception) {
                true
            }
        }
        return false
    }
}
