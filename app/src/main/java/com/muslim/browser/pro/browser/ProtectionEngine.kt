package com.muslim.browser.pro.browser

import android.net.Uri
import android.util.Log
import org.json.JSONArray
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale

private val WHITESPACE_REGEX = Regex("\\s+")

object ProtectionEngine {

    private var lastKeywordsForScript: Set<String>? = null
    private var lastBuiltScript: String? = null

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
        // Google Ad & Tracking Services
        "doubleclick.net", "googlesyndication.com", "googleadservices.com", "adservice.google.com",
        "pagead2.googlesyndication.com", "google-analytics.com", "analytics.google.com",
        // Amazon & Microsoft
        "adsystem.amazon.com", "amazon-adsystem.com", "aax.amazon-adsystem.com",
        "clarity.ms", "bingads.microsoft.com",
        // Ad Exchanges, SSPs, DSPs & Native Ads
        "adnxs.com", "criteo.com", "criteo.net", "taboola.com", "outbrain.com",
        "rubiconproject.com", "pubmatic.com", "openx.net", "casalemedia.com",
        "smartadserver.com", "bidswitch.net", "triplelift.com", "indexexchange.com",
        "sharethrough.com", "sovrn.com", "yieldmo.com", "media.net", "exponential.com",
        // Mobile & In-App Ad Networks
        "inmobi.com", "applovin.com", "unityads.unity3d.com", "vungle.com", "ironsrc.com",
        "chartboost.com", "mopub.com", "adcolony.com", "fyber.com",
        // Aggressive Pop-up, Pop-under & Redirect Networks
        "popads.net", "popcash.net", "adsterra.com", "propellerads.com", "hilltopads.com",
        "clickadu.com", "exoclick.com", "trafficjunky.com", "juicyads.com", "ero-advertising.com",
        "yllix.com", "adcash.com", "richads.com", "monetag.com",
        // Telemetry & Tracking
        "scorecardresearch.com", "advertising.com", "adroll.com", "revcontent.com",
        "hotjar.com", "mouseflow.com", "segment.io", "mixpanel.com", "quantserve.com",
        "moatads.com", "adsrvr.org", "zemanta.com", "teads.tv"
    )


    fun isDuckDuckGoHost(host: String): Boolean {
        if (host.isBlank()) return false
        var clean = host.trim().lowercase(Locale.ROOT)
        if (clean.endsWith(".")) {
            clean = clean.dropLast(1)
        }
        if (clean.isEmpty()) return false
        for (ch in clean) {
            if (!(ch in 'a'..'z' || ch in '0'..'9' || ch == '.' || ch == '-')) {
                return false
            }
        }
        if (clean.startsWith(".") || clean.endsWith(".") || clean.contains("..")) return false
        val labels = clean.split('.')
        for (label in labels) {
            if (label.isEmpty() || label.startsWith("-") || label.endsWith("-")) {
                return false
            }
        }
        return clean == "duckduckgo.com" || clean.endsWith(".duckduckgo.com")
    }

    fun isDuckDuckGoSearchPath(path: String?): Boolean {
        if (path == null) return true
        val cleanPath = path.lowercase(Locale.ROOT).trimEnd('/')
        return cleanPath.isEmpty() ||
                cleanPath == "/html" ||
                cleanPath == "/lite" ||
                cleanPath == "/search"
    }

    fun isDuckDuckGoSafeSearchUrl(url: String): Boolean {
        if (url.isBlank()) return false
        return try {
            val uri = Uri.parse(url)
            val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: return false
            if (scheme != "https") return false
            val host = uri.host?.lowercase(Locale.ROOT) ?: return false
            if (host != "safe.duckduckgo.com") return false
            if (!isDuckDuckGoSearchPath(uri.path)) return false
            val q = uri.getQueryParameter("q")
            if (q.isNullOrBlank()) return false
            val kpNames = uri.queryParameterNames.filter { it.equals("kp", ignoreCase = true) }
            if (kpNames.isEmpty()) return false
            val allKpValues = kpNames.flatMap { uri.getQueryParameters(it) }
            allKpValues.size == 1 && allKpValues[0] == "1"
        } catch (_: Exception) {
            false
        }
    }

    fun buildDuckDuckGoStrictSearchUrl(url: String): String {
        val uri = try { Uri.parse(url) } catch (_: Exception) { null }
        if (uri == null) {
            val q = extractSearchEngineQuery(url) ?: ""
            return SearchEngine.DUCKDUCKGO.buildSearchUrl(q)
        }

        val host = uri.host?.lowercase(Locale.ROOT)
        if (host == null || !isDuckDuckGoHost(host)) {
            return url
        }
        if (!isDuckDuckGoSearchPath(uri.path)) {
            return url
        }

        val builder = uri.buildUpon()
            .scheme("https")
            .authority("safe.duckduckgo.com")

        val path = uri.path
        if (path.isNullOrEmpty()) {
            builder.path("/")
        }

        val rawQuery = uri.encodedQuery
        val keptPairs = mutableListOf<String>()
        if (!rawQuery.isNullOrBlank()) {
            val pairs = rawQuery.split('&')
            for (pair in pairs) {
                if (pair.isBlank()) continue
                val key = pair.substringBefore('=')
                val decodedKey = try { Uri.decode(key) } catch (_: Exception) { key }
                if (!decodedKey.equals("kp", ignoreCase = true)) {
                    keptPairs.add(pair)
                }
            }
        }
        keptPairs.add("kp=1")
        builder.encodedQuery(keptPairs.joinToString("&"))

        return builder.build().toString()
    }

    fun isGoogleSearchPath(path: String?): Boolean {
        if (path == null) return false
        val clean = path.lowercase(Locale.ROOT).trimEnd('/')
        return clean == "/search" || clean.startsWith("/search/") || clean == "/webhp"
    }

    fun isGoogleSafeSearchUrl(url: String): Boolean {
        if (url.isBlank()) return false
        return try {
            val uri = Uri.parse(url)
            val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: return false
            if (scheme != "http" && scheme != "https") return false
            val host = uri.host?.lowercase(Locale.ROOT) ?: return false
            if (!isGoogleHost(host)) return false
            if (!isGoogleSearchPath(uri.path)) return false
            val q = uri.getQueryParameter("q")
            if (q.isNullOrBlank()) return false
            val safeNames = uri.queryParameterNames.filter { it.equals("safe", ignoreCase = true) }
            if (safeNames.isEmpty()) return false
            val allSafeValues = safeNames.flatMap { uri.getQueryParameters(it) }
            allSafeValues.size == 1 && allSafeValues[0].equals("active", ignoreCase = true)
        } catch (_: Exception) {
            false
        }
    }

    fun buildGoogleStrictSearchUrl(url: String): String {
        val uri = try { Uri.parse(url) } catch (_: Exception) { null }
        if (uri == null) {
            val q = extractSearchEngineQuery(url) ?: ""
            return SearchEngine.GOOGLE.buildSearchUrl(q)
        }

        val host = uri.host?.lowercase(Locale.ROOT)
        if (host == null || !isGoogleHost(host)) {
            return url
        }

        val path = uri.path?.lowercase(Locale.ROOT) ?: ""
        if (!isGoogleSearchPath(path)) {
            return url
        }

        val builder = uri.buildUpon()
            .scheme("https")
            .clearQuery()

        for (paramName in uri.queryParameterNames) {
            if (!paramName.equals("safe", ignoreCase = true)) {
                for (paramValue in uri.getQueryParameters(paramName)) {
                    builder.appendQueryParameter(paramName, paramValue)
                }
            }
        }
        builder.appendQueryParameter("safe", "active")

        return builder.build().toString()
    }

    fun enforceSafeSearch(url: String, searchEngine: SearchEngine): String {
        return when (searchEngine) {
            SearchEngine.GOOGLE -> buildGoogleStrictSearchUrl(url)
            SearchEngine.DUCKDUCKGO -> buildDuckDuckGoStrictSearchUrl(url)
        }
    }

    private val GOOGLE_HOST_REGEX = Regex("^google\\.(com|co\\.[a-z]{2,3}|com\\.[a-z]{2,3}|[a-z]{2,3})$")

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

        return GOOGLE_HOST_REGEX.matches(baseDomain)
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
            if (isDuckDuckGoHost(host) && isDuckDuckGoSearchPath(path)) {
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

    /**
     * Extracts search query parameters from general website search URLs
     * (e.g. https://en.wikipedia.org/w/index.php?search=..., https://github.com/search?q=...)
     * when the domain is not one of the explicitly hardcoded global search engines.
     */
    fun extractGenericSearchQuery(url: String): String? {
        if (url.isBlank()) return null
        return try {
            val uri = Uri.parse(url)
            val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: return null
            if (scheme != "http" && scheme != "https") return null
            if (!uri.userInfo.isNullOrEmpty()) return null
            val port = uri.port
            if (port != -1 && port != 80 && port != 443) return null

            val queryParamNames = listOf(
                "q", "search", "query", "search_query", "k", "keyword",
                "term", "text", "s", "wd", "searchTerm", "search_term"
            )
            for (param in queryParamNames) {
                val value = uri.getQueryParameter(param)
                if (!value.isNullOrBlank()) {
                    return value
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun normalizeKeyword(kw: String): String {
        return kw.replace(WHITESPACE_REGEX, " ").trim().lowercase(Locale.ROOT)
    }

    private fun findKeywordMatch(
        normalizedText: String,
        customKeywords: Set<String>,
        normalizedKeywords: Collection<String>?
    ): String? {
        if (!normalizedKeywords.isNullOrEmpty()) {
            for (kwNorm in normalizedKeywords) {
                val cleanKwNorm = normalizeKeyword(kwNorm)
                if (cleanKwNorm.isNotEmpty() && normalizedText.contains(cleanKwNorm)) {
                    val original = customKeywords.firstOrNull { normalizeKeyword(it) == cleanKwNorm }
                    return original ?: kwNorm
                }
            }
        } else {
            for (kw in customKeywords) {
                val kwNormalized = normalizeKeyword(kw)
                if (kwNormalized.isNotEmpty() && normalizedText.contains(kwNormalized)) {
                    return kw
                }
            }
        }
        return null
    }

    fun isBlockedByCustomKeywords(
        text: String,
        customKeywords: Set<String>,
        normalizedKeywords: Collection<String>? = null
    ): String? {
        if (customKeywords.isEmpty() || text.isBlank()) return null

        // 1. Check with URL-decoded text preserving '+' (crucial for phrases and symbols like "18+")
        val decoded = try {
            Uri.decode(text) ?: text
        } catch (_: Exception) {
            text
        }
        val normalizedRaw = decoded.replace(WHITESPACE_REGEX, " ").trim().lowercase(Locale.ROOT)
        if (normalizedRaw.isNotEmpty()) {
            val match = findKeywordMatch(normalizedRaw, customKeywords, normalizedKeywords)
            if (match != null) return match
        }

        // 2. Also check with '+' treated as space (for URL search query parameters like "hot+scenes")
        val plusReplaced = decoded.replace('+', ' ')
        val normalizedPlus = plusReplaced.replace(WHITESPACE_REGEX, " ").trim().lowercase(Locale.ROOT)
        if (normalizedPlus.isNotEmpty() && normalizedPlus != normalizedRaw) {
            val match = findKeywordMatch(normalizedPlus, customKeywords, normalizedKeywords)
            if (match != null) return match
        }

        return null
    }

    /**
     * Builds JavaScript that intercepts webpage search boxes (Enter key, search buttons, form submissions)
     * across all websites and SPAs before navigation occurs.
     */
    fun buildKeywordBlockingScript(customKeywords: Set<String>): String {
        if (customKeywords == lastKeywordsForScript && lastBuiltScript != null) {
            return lastBuiltScript!!
        }

        val jsonArray = JSONArray()
        for (kw in customKeywords) {
            val trimmed = kw.trim()
            if (trimmed.isNotEmpty()) {
                jsonArray.put(trimmed)
            }
        }
        val keywordsJson = jsonArray.toString()

        val script = """
(function() {
    window.__mbp_keywords = $keywordsJson;
    if (window.__mbp_kw_installed) return;
    window.__mbp_kw_installed = true;

    function checkBlocked(query) {
        if (!query) return null;
        var norm = query.replace(/\s+/g, ' ').trim().toLowerCase();
        if (!norm) return null;
        var kws = window.__mbp_keywords || [];
        for (var i = 0; i < kws.length; i++) {
            var kw = (kws[i] || '').replace(/\s+/g, ' ').trim().toLowerCase();
            if (kw.length > 0 && norm.indexOf(kw) !== -1) {
                return kws[i];
            }
        }
        return null;
    }

    function isSearchInput(el) {
        if (!el || !el.tagName) return false;
        var tag = el.tagName.toUpperCase();
        if (tag === 'TEXTAREA') {
            var r = (el.getAttribute('role') || '').toLowerCase();
            var a = (el.getAttribute('aria-label') || '').toLowerCase();
            var n = (el.name || '').toLowerCase();
            var id = (el.id || '').toLowerCase();
            var ph = (el.placeholder || '').toLowerCase();
            return r === 'searchbox' || a.indexOf('search') !== -1 || ph.indexOf('search') !== -1 ||
                   n === 'q' || n === 'search' || n === 'search_query' || id.indexOf('search') !== -1;
        }
        if (tag !== 'INPUT') return false;
        var type = (el.type || 'text').toLowerCase();
        if (type === 'search') return true;
        if (type !== 'text' && type !== '' && type !== 'url') return false;

        var role = (el.getAttribute('role') || '').toLowerCase();
        if (role === 'searchbox') return true;

        var n = (el.name || '').toLowerCase();
        var id = (el.id || '').toLowerCase();
        var ph = (el.placeholder || '').toLowerCase();
        var aria = (el.getAttribute('aria-label') || '').toLowerCase();
        var cl = (el.className || '').toLowerCase();

        if (role === 'combobox') {
            if (aria.indexOf('search') !== -1 || ph.indexOf('search') !== -1 || ph.indexOf('খুঁজ') !== -1 ||
                n.indexOf('search') !== -1 || n === 'q' || n === 'query' || n === 'k' || n === 'keyword' ||
                n === 's' || id.indexOf('search') !== -1 || cl.indexOf('search') !== -1) {
                return true;
            }
        }

        if (n === 'q' || n === 'query' || n === 'search' || n === 'search_query' || n === 'k' || n === 'keyword' ||
            n === 'term' || n === 'text' || n === 's' || n === 'searchterm' || n === 'search_term' || n === 'wd') return true;
        if (n.indexOf('search') !== -1 || id.indexOf('search') !== -1 || ph.indexOf('search') !== -1 || aria.indexOf('search') !== -1 || cl.indexOf('search') !== -1) return true;
        if (ph.indexOf('খুঁজ') !== -1 || aria.indexOf('খুঁজ') !== -1) return true;

        var form = el.form || (el.closest ? el.closest('form') : null);
        if (form) {
            var fRole = (form.getAttribute('role') || '').toLowerCase();
            var fAction = (form.action || '').toLowerCase();
            var fClass = (form.className || '').toLowerCase();
            var fId = (form.id || '').toLowerCase();
            if (fRole === 'search' || fAction.indexOf('search') !== -1 || fClass.indexOf('search') !== -1 || fId.indexOf('search') !== -1) {
                return true;
            }
        }
        return false;
    }

    function blockSearch(e, matchedKw) {
        if (e) {
            try { e.preventDefault(); } catch(_) {}
            try { e.stopPropagation(); } catch(_) {}
            try { e.stopImmediatePropagation(); } catch(_) {}
        }
        window.location.href = 'mbp-blocked-search://block?kw=' + encodeURIComponent(matchedKw);
    }

    // 1. Enter key in search input (SPAs & regular forms)
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' || e.keyCode === 13) {
            var target = e.target;
            if (isSearchInput(target)) {
                var kw = checkBlocked(target.value);
                if (kw) {
                    blockSearch(e, kw);
                }
            }
        }
    }, true);

    // 2. Form submit event
    document.addEventListener('submit', function(e) {
        var form = e.target;
        if (form && form.querySelectorAll) {
            var inputs = form.querySelectorAll('input, textarea');
            for (var i = 0; i < inputs.length; i++) {
                if (isSearchInput(inputs[i])) {
                    var kw = checkBlocked(inputs[i].value);
                    if (kw) {
                        blockSearch(e, kw);
                        return;
                    }
                }
            }
        }
    }, true);

    // 3. Search button click (for buttons or anchors triggering JS searches)
    document.addEventListener('click', function(e) {
        var btn = e.target && e.target.closest ? e.target.closest('button, input[type="submit"], [role="button"], a') : null;
        if (btn) {
            var bText = (btn.innerText || btn.textContent || btn.value || '').toLowerCase();
            var bAria = (btn.getAttribute('aria-label') || '').toLowerCase();
            var bClass = (btn.className || '').toLowerCase();
            var bId = (btn.id || '').toLowerCase();
            var isSearchBtn = bAria.indexOf('search') !== -1 || bText.indexOf('search') !== -1 || bText.indexOf('খুঁজ') !== -1 ||
                              bClass.indexOf('search') !== -1 || bId.indexOf('search') !== -1;
            var form = btn.form || (btn.closest ? btn.closest('form') : null);
            if (!isSearchBtn && form) {
                var fRole = (form.getAttribute('role') || '').toLowerCase();
                var fAction = (form.action || '').toLowerCase();
                if (fRole === 'search' || fAction.indexOf('search') !== -1) {
                    isSearchBtn = true;
                }
            }
            if (isSearchBtn) {
                var scope = form || (btn.closest ? btn.closest('header, nav, [role="search"], form') : null);
                var inputs = scope && scope.querySelectorAll ? Array.prototype.slice.call(scope.querySelectorAll('input, textarea')) : [];
                if (inputs.length === 0 && btn.parentElement) {
                    inputs = Array.prototype.slice.call(btn.parentElement.querySelectorAll('input, textarea'));
                    if (inputs.length === 0 && btn.parentElement.parentElement) {
                        inputs = Array.prototype.slice.call(btn.parentElement.parentElement.querySelectorAll('input, textarea'));
                    }
                }
                for (var i = 0; i < inputs.length; i++) {
                    if (isSearchInput(inputs[i])) {
                        var kw = checkBlocked(inputs[i].value);
                        if (kw) {
                            blockSearch(e, kw);
                            return;
                        }
                    }
                }
            }
        }
    }, true);

    // 4. Programmatic form.submit() call by site JavaScript
    try {
        var origSubmit = HTMLFormElement.prototype.submit;
        HTMLFormElement.prototype.submit = function() {
            var inputs = this.querySelectorAll ? this.querySelectorAll('input, textarea') : [];
            for (var i = 0; i < inputs.length; i++) {
                if (isSearchInput(inputs[i])) {
                    var kw = checkBlocked(inputs[i].value);
                    if (kw) {
                        blockSearch(null, kw);
                        return;
                    }
                }
            }
            return origSubmit.apply(this, arguments);
        };
    } catch(_) {}
})();
        """.trimIndent()
        lastKeywordsForScript = HashSet(customKeywords)
        lastBuiltScript = script
        return script
    }

    fun checkDirectUrl(
        url: String,
        customKeywords: Set<String>,
        normalizedKeywords: Collection<String>? = null,
        isAdBlockingEnabled: Boolean = true
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

        if (isAdBlockingEnabled && isAdOrTracker(trimmed)) {
            return FilterResult.Blocked(
                "Ad Protection",
                "Ad network URL blocked: \"${host ?: trimmed}\""
            )
        }

        return FilterResult.Allowed
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

    fun isAdOrTracker(host: String?, path: String? = null): Boolean {
        if (host.isNullOrBlank()) return false
        val cleanHost = host.trim().lowercase(Locale.ROOT)
        if (matchesDomainOrSubdomain(cleanHost, KNOWN_AD_DOMAINS)) {
            return true
        }
        if (path != null && isGoogleHost(cleanHost)) {
            val lowerPath = path.lowercase(Locale.ROOT)
            if (lowerPath.startsWith("/pagead/") || lowerPath.startsWith("/adservice/")) {
                return true
            }
        }
        return false
    }

    fun isAdOrTracker(uri: Uri): Boolean {
        return isAdOrTracker(uri.host, uri.path)
    }

    fun isAdOrTracker(url: String): Boolean {
        return try {
            val candidate = if (!url.startsWith("http://", ignoreCase = true) && !url.startsWith("https://", ignoreCase = true)) {
                "https://$url"
            } else {
                url
            }
            val uri = Uri.parse(candidate)
            isAdOrTracker(uri)
        } catch (_: Exception) {
            false
        }
    }

    fun isHarmfulUrl(url: String, customKeywords: Set<String> = emptySet()): Boolean {
        return checkDirectUrl(url, customKeywords) is FilterResult.Blocked
    }
}
