package com.muslim.browser.pro.browser

import android.net.Uri
import java.net.URLDecoder
import java.util.Locale

/**
 * Download rules:
 * 1) Video: blocked from every server.
 * 2) APK: allowed only from GitHub.
 * 3) Everything else (ZIP, PDF, audio, documents): allowed.
 */
object DownloadPolicy {

    sealed class Result {
        data object Allowed : Result()
        data class Blocked(val reason: String) : Result()
    }

    val VIDEO_EXTS: HashSet<String> = hashSetOf(
        "mp4", "mkv", "webm", "avi", "mov", "m4v", "wmv", "flv", "3gp",
        "ts", "mpg", "mpeg", "ogv", "vob", "m2ts"
    )

    val APK_EXTS: HashSet<String> = hashSetOf("apk", "xapk", "apks")

    val AUDIO_EXTS: HashSet<String> = hashSetOf("mp3", "wav", "ogg", "m4a", "aac", "flac")

    val ZIP_EXTS: HashSet<String> = hashSetOf("zip")

    val DOWNLOADABLE_EXTS: HashSet<String> = hashSetOf(
        "apk", "xapk", "apks", "zip", "tar", "gz", "gzip", "tgz", "bz2", "xz",
        "rar", "7z", "iso", "dmg", "pdf", "epub", "doc", "docx", "xls", "xlsx",
        "ppt", "pptx", "csv", "txt", "rtf", "odt", "ods", "odp"
    )

    // ---------- file name / extension helpers ----------

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
                if (raw.isNotEmpty()) return decodeFileName(raw)
            }
            if (disp.contains("filename=", ignoreCase = true)) {
                val raw = disp.substringAfter("filename=").substringBefore(';').trim('"', '\'', ' ')
                if (raw.isNotEmpty()) return decodeFileName(raw)
            }
        }
        val clean = url.substringBefore('?').substringBefore('#').trim()
        val candidate = clean.substringAfterLast('/')
        if (candidate.contains('.')) return decodeFileName(candidate)
        val query = url.substringAfter('?', "").substringBefore('#')
        if (query.isNotEmpty()) {
            for (param in query.split('&')) {
                val value = param.substringAfter('=', "")
                if (value.isNotEmpty()) {
                    val decoded = decodeFileName(value).substringAfterLast('/')
                    if (decoded.contains('.')) return decoded
                }
            }
        }
        return decodeFileName(candidate)
    }

    fun extractExtension(url: String, contentDisposition: String? = null): String {
        val fileName = extractFileName(url, contentDisposition)
        val ext = fileName.substringAfterLast('.', "")
        if (ext.isNotEmpty() && ext != fileName) return ext.lowercase(Locale.ROOT)
        return url.substringBefore('?').substringBefore('#').substringAfterLast('/')
            .substringAfterLast('.').lowercase(Locale.ROOT)
    }

    // One shared check used by isVideo / isApk / isAudio / isZip
    private fun hasExt(url: String, disposition: String?, exts: Set<String>): Boolean {
        if (extractExtension(url, disposition) in exts) return true
        if (extractExtension(url, null) in exts) return true
        val path = try {
            Uri.parse(url.trim()).path?.lowercase(Locale.ROOT) ?: ""
        } catch (_: Exception) {
            ""
        }
        return exts.any { path.endsWith(".$it") || path.contains(".$it/") }
    }

    private fun lowerMime(mimeType: String?): String = mimeType?.trim()?.lowercase(Locale.ROOT) ?: ""

    // ---------- file type checks ----------

    fun isVideo(url: String, mimeType: String? = null, contentDisposition: String? = null): Boolean {
        if (lowerMime(mimeType).startsWith("video/")) return true
        return hasExt(url, contentDisposition, VIDEO_EXTS)
    }

    fun isApk(url: String, mimeType: String? = null, contentDisposition: String? = null): Boolean {
        if (lowerMime(mimeType) == "application/vnd.android.package-archive") return true
        return hasExt(url, contentDisposition, APK_EXTS)
    }

    fun isAudio(url: String, mimeType: String? = null, contentDisposition: String? = null): Boolean {
        val mime = lowerMime(mimeType)
        if (mime.startsWith("audio/") || mime == "application/ogg") return true
        return hasExt(url, contentDisposition, AUDIO_EXTS)
    }

    fun isZip(url: String, mimeType: String? = null, contentDisposition: String? = null): Boolean {
        val mime = lowerMime(mimeType)
        if (mime == "application/zip" || mime == "application/x-zip-compressed" || mime == "application/x-zip") return true
        return hasExt(url, contentDisposition, ZIP_EXTS)
    }

    fun isDownloadableFileUrl(
        url: String,
        mimeType: String? = null,
        contentDisposition: String? = null
    ): Boolean {
        if (url.isBlank()) return false
        if (isVideo(url, mimeType, contentDisposition)) return false

        val mime = lowerMime(mimeType)
        if (mime == "application/pdf" || mime == "application/octet-stream" ||
            isZip(url, mime, contentDisposition)
        ) return true

        val ext = extractExtension(url, contentDisposition)
        if (ext in DOWNLOADABLE_EXTS || ext in AUDIO_EXTS) return true

        val lowerUrl = url.lowercase(Locale.ROOT)
        return DOWNLOADABLE_EXTS.any {
            lowerUrl.contains(".$it") || lowerUrl.contains("=$it") || lowerUrl.contains("%2e$it")
        }
    }

    // ---------- GitHub check ----------

    fun isGitHubHost(host: String?): Boolean {
        val h = host?.lowercase(Locale.ROOT) ?: return false
        return h == "github.com" ||
            h == "www.github.com" ||
            h == "githubusercontent.com" ||
            h.endsWith(".githubusercontent.com") ||
            (h.startsWith("github-production-release-asset") && h.endsWith(".amazonaws.com"))
    }

    fun isAllowedGitHubApkUrl(url: String): Boolean {
        if (url.isBlank()) return false
        return try {
            isGitHubHost(Uri.parse(url.trim()).host)
        } catch (_: Exception) {
            false
        }
    }

    // ---------- decisions ----------

    fun evaluate(url: String, mimeType: String? = null, contentDisposition: String? = null): Result {
        if (isVideo(url, mimeType, contentDisposition)) {
            return Result.Blocked("Video downloads are blocked.")
        }
        if (isApk(url, mimeType, contentDisposition) && !isAllowedGitHubApkUrl(url)) {
            return Result.Blocked("APK downloads are allowed only from GitHub.")
        }
        return Result.Allowed
    }

    fun shouldBlockUrlNavigation(url: String): Boolean {
        if (isVideo(url)) return true
        return isApk(url) && !isAllowedGitHubApkUrl(url)
    }
}
