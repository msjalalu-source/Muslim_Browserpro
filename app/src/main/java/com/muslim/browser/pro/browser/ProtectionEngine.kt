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
        ALLOWED_ZIP,
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

    val BLOCKED_VIDEO_EXTS: HashSet<String> get() = DownloadPolicy.VIDEO_EXTS

    val BLOCKED_AUDIO_EXTS: HashSet<String> get() = DownloadPolicy.AUDIO_EXTS

    val BLOCKED_APK_EXTS: HashSet<String> get() = DownloadPolicy.APK_EXTS

    val ALLOWED_IMAGE_EXTS: HashSet<String> = hashSetOf(
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "ico"
    )

    val ALLOWED_PDF_EXTS: HashSet<String> = hashSetOf(
        "pdf"
    )

    val ALLOWED_ZIP_EXTS: HashSet<String> = hashSetOf(
        "zip"
    )

    fun buildDuckDuckGoSafeSearchUrl(query: String): String {
        val trimmed = query.trim()
        val encoded = URLEncoder.encode(trimmed, StandardCharsets.UTF_8.name())
        return "https://safe.duckduckgo.com/?q=$encoded"
    }

    fun buildSafeSearchUrl(query: String, searchEngine: SearchEngine = SearchEngine.DUCKDUCKGO): String {
        return buildDuckDuckGoSafeSearchUrl(query)
    }

    val SUPPORTED_GOOGLE_DOMAINS: Set<String> = hashSetOf(
        "google.com",
        "google.ad", "google.ae", "google.al", "google.am", "google.as", "google.at", "google.az",
        "google.ba", "google.be", "google.bf", "google.bg", "google.bi", "google.bj", "google.bs", "google.bt", "google.by",
        "google.ca", "google.cat", "google.cd", "google.cf", "google.cg", "google.ch", "google.ci", "google.cl", "google.cm", "google.cn",
        "google.co.ao", "google.co.bw", "google.co.ck", "google.co.cr", "google.co.id", "google.co.il", "google.co.in", "google.co.jp",
        "google.co.ke", "google.co.kr", "google.co.ls", "google.co.ma", "google.co.mz", "google.co.nz", "google.co.th", "google.co.tz",
        "google.co.ug", "google.co.uk", "google.co.uz", "google.co.ve", "google.co.vi", "google.co.za", "google.co.zm", "google.co.zw",
        "google.com.af", "google.com.ag", "google.com.ai", "google.com.ar", "google.com.au", "google.com.bd", "google.com.bh", "google.com.bn",
        "google.com.bo", "google.com.br", "google.com.bz", "google.com.co", "google.com.cu", "google.com.cy", "google.com.do", "google.com.ec",
        "google.com.eg", "google.com.et", "google.com.fj", "google.com.gh", "google.com.gi", "google.com.gt", "google.com.gy", "google.com.hk",
        "google.com.jm", "google.com.jo", "google.com.kh", "google.com.kw", "google.com.lb", "google.com.lc", "google.com.ly", "google.com.mm",
        "google.com.mt", "google.com.mx", "google.com.my", "google.com.na", "google.com.ng", "google.com.ni", "google.com.np", "google.com.om",
        "google.com.pa", "google.com.pe", "google.com.pg", "google.com.ph", "google.com.pk", "google.com.pr", "google.com.py", "google.com.qa",
        "google.com.sa", "google.com.sb", "google.com.sg", "google.com.sl", "google.com.sv", "google.com.tj", "google.com.tn", "google.com.tr",
        "google.com.tw", "google.com.ua", "google.com.uy", "google.com.vc", "google.com.vn",
        "google.cv", "google.cz", "google.de", "google.dj", "google.dk", "google.dm", "google.dz",
        "google.ee", "google.es", "google.fi", "google.fm", "google.fr", "google.ga", "google.ge", "google.gg", "google.gl", "google.gm", "google.gp", "google.gr", "google.gy",
        "google.hn", "google.hr", "google.ht", "google.hu", "google.ie", "google.im", "google.iq", "google.is", "google.it",
        "google.je", "google.jo", "google.kg", "google.ki", "google.kz", "google.la", "google.li", "google.lk", "google.lt", "google.lu", "google.lv",
        "google.md", "google.me", "google.mg", "google.mk", "google.ml", "google.mn", "google.ms", "google.mu", "google.mv", "google.mw",
        "google.ne", "google.net", "google.nf", "google.nl", "google.no", "google.nr", "google.nu",
        "google.pl", "google.pn", "google.ps", "google.pt", "google.ro", "google.rs", "google.ru", "google.rw",
        "google.sc", "google.se", "google.sh", "google.si", "google.sk", "google.sm", "google.sn", "google.so", "google.sr", "google.st",
        "google.td", "google.tg", "google.tk", "google.tl", "google.tm", "google.tn", "google.to", "google.tt",
        "google.vg", "google.vu", "google.ws"
    )

    fun isDuckDuckGoSafeSearchUrl(url: String): Boolean {
        if (url.isBlank()) return false
        return try {
            val uri = Uri.parse(url)
            val host = uri.host?.lowercase(Locale.ROOT) ?: return false
            host == "safe.duckduckgo.com"
        } catch (_: Exception) {
            false
        }
    }

    fun isSafeSearchUrl(url: String): Boolean {
        return isDuckDuckGoSafeSearchUrl(url)
    }

    fun isGoogleHost(host: String): Boolean {
        if (host.isBlank()) return false
        var clean = host.trim().lowercase(Locale.ROOT)
        // Handle trailing dot (DNS root)
        if (clean.endsWith(".")) {
            clean = clean.dropLast(1)
        }
        if (clean.isEmpty()) return false

        // Hostname syntax validation: only alphanumeric, dots, and hyphens
        // Rejects user-info (@), port (:), path (/ or \), query (?), fragment (#), whitespace
        for (ch in clean) {
            if (!(ch in 'a'..'z' || ch in '0'..'9' || ch == '.' || ch == '-')) {
                return false
            }
        }
        if (clean.startsWith(".") || clean.endsWith(".") || clean.contains("..")) return false

        // Validate each label (RFC 1034 / 1123)
        val labels = clean.split('.')
        for (label in labels) {
            if (label.isEmpty() || label.startsWith("-") || label.endsWith("-")) {
                return false
            }
        }

        // Only allow naked Google search domain (e.g. google.com) or www subdomain (e.g. www.google.com).
        // Disallow arbitrary subdomains or unrelated Google services (e.g. drive.google.com, docs.google.com).
        val baseDomain = if (clean.startsWith("www.")) {
            clean.removePrefix("www.")
        } else {
            clean
        }

        return SUPPORTED_GOOGLE_DOMAINS.contains(baseDomain)
    }

    fun extractSearchEngineQuery(url: String): String? {
        if (url.isBlank()) return null
        return try {
            val uri = Uri.parse(url)
            val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: return null
            if (scheme != "http" && scheme != "https") return null
            if (!uri.userInfo.isNullOrEmpty()) return null
            val port = uri.port
            if (port != -1 && port != 80 && port != 443) return null
            val host = uri.host ?: return null
            val path = uri.path?.lowercase(Locale.ROOT) ?: ""

            // 1. Google (only /search or /webhp with query param q, preserve translate, accounts, etc.)
            if (isGoogleHost(host) && !path.startsWith("/url")) {
                if (path.contains("/search") || path.contains("/webhp")) {
                    val q = uri.getQueryParameter("q")
                    if (!q.isNullOrBlank()) return q
                }
            }

            // 2. DuckDuckGo
            if (host.contains("duckduckgo.com")) {
                val q = uri.getQueryParameter("q")
                if (!q.isNullOrBlank()) return q
            }

            // 3. Bing
            if (host.contains("bing.com") && (path.contains("/search") || uri.getQueryParameter("q") != null)) {
                val q = uri.getQueryParameter("q")
                if (!q.isNullOrBlank()) return q
            }

            // 4. Yahoo
            if (host.contains("search.yahoo.com") || (host.contains("yahoo.com") && path.contains("/search"))) {
                val p = uri.getQueryParameter("p") ?: uri.getQueryParameter("q")
                if (!p.isNullOrBlank()) return p
            }

            // 5. Yandex
            if (host.contains("yandex.") || host.contains("ya.ru")) {
                val text = uri.getQueryParameter("text") ?: uri.getQueryParameter("q")
                if (!text.isNullOrBlank()) return text
            }

            // 6. Brave
            if (host.contains("brave.com") || host.contains("search.brave.com")) {
                val q = uri.getQueryParameter("q")
                if (!q.isNullOrBlank()) return q
            }

            // 7. Ecosia
            if (host.contains("ecosia.org")) {
                val q = uri.getQueryParameter("q")
                if (!q.isNullOrBlank()) return q
            }

            // 8. Startpage
            if (host.contains("startpage.com")) {
                val q = uri.getQueryParameter("query") ?: uri.getQueryParameter("q")
                if (!q.isNullOrBlank()) return q
            }

            // 9. Qwant
            if (host.contains("qwant.com")) {
                val q = uri.getQueryParameter("q")
                if (!q.isNullOrBlank()) return q
            }

            // 10. Ask
            if (host.contains("ask.com")) {
                val q = uri.getQueryParameter("q")
                if (!q.isNullOrBlank()) return q
            }

            // 11. Baidu
            if (host.contains("baidu.com")) {
                val wd = uri.getQueryParameter("wd") ?: uri.getQueryParameter("word")
                if (!wd.isNullOrBlank()) return wd
            }

            // 12. Naver
            if (host.contains("naver.com")) {
                val query = uri.getQueryParameter("query") ?: uri.getQueryParameter("q")
                if (!query.isNullOrBlank()) return query
            }

            // 13. AOL
            if (host.contains("search.aol.") || host.contains("aol.com")) {
                val q = uri.getQueryParameter("q")
                if (!q.isNullOrBlank()) return q
            }

            // 14. Mojeek
            if (host.contains("mojeek.com")) {
                val q = uri.getQueryParameter("q")
                if (!q.isNullOrBlank()) return q
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
        val plusReplaced = text.replace('+', ' ')
        val decoded = try {
            Uri.decode(plusReplaced) ?: plusReplaced
        } catch (_: Exception) {
            plusReplaced
        }
        val normalized = decoded.trim().lowercase(Locale.ROOT)

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

        if (Log.isLoggable("DIAGNOSTIC", Log.DEBUG)) {
            Log.d("DIAGNOSTIC", "DIRECT_URL_CHECK=checkDirectUrl")
        }

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
        if (DownloadPolicy.isVideo(url, mimeType, contentDisposition)) {
            return DownloadStatus.BLOCKED_VIDEO
        }
        if (DownloadPolicy.isAudio(url, mimeType, contentDisposition)) {
            return DownloadStatus.BLOCKED_AUDIO
        }
        if (DownloadPolicy.isApk(url, mimeType, contentDisposition)) {
            return DownloadStatus.BLOCKED_APK
        }

        val mime = mimeType?.trim()?.lowercase(Locale.ROOT) ?: ""
        val ext = extractExtension(url, contentDisposition)

        if (mime.startsWith("image/") || ALLOWED_IMAGE_EXTS.contains(ext)) {
            return DownloadStatus.ALLOWED_IMAGE
        }
        if (mime == "application/pdf" || ALLOWED_PDF_EXTS.contains(ext)) {
            return DownloadStatus.ALLOWED_PDF
        }
        val isZipMime = mime == "application/zip" ||
            mime == "application/x-zip-compressed" ||
            mime == "application/x-zip"
        if (isZipMime || ALLOWED_ZIP_EXTS.contains(ext) || DownloadPolicy.isZip(url, mimeType, contentDisposition)) {
            return DownloadStatus.ALLOWED_ZIP
        }
        return DownloadStatus.BLOCKED_OTHER
    }

    fun extractExtension(url: String, contentDisposition: String?): String {
        return DownloadPolicy.extractExtension(url, contentDisposition)
    }

    fun isHarmfulUrl(url: String, customKeywords: Set<String> = emptySet()): Boolean {
        return checkDirectUrl(url, customKeywords) is FilterResult.Blocked
    }
}
