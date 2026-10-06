package com.muslim.browser.pro.browser

import android.net.Uri
import java.util.Locale

object NavigationController {

    @Volatile
    private var lastValidatedGoogleUrl: String? = null

    fun markSearchUrlValidated(url: String) {
        lastValidatedGoogleUrl = url.trim()
    }

    fun isSearchUrlValidated(url: String): Boolean {
        val validated = lastValidatedGoogleUrl
        return validated != null && validated == url.trim()
    }

    fun clearSearchUrlValidation() {
        lastValidatedGoogleUrl = null
    }

    fun evaluate(
        url: String,
        customKeywords: Set<String>,
        normalizedKeywords: Collection<String>? = null,
        searchEngine: SearchEngine = SearchEngine.DUCKDUCKGO
    ): NavigationDecision {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) {
            return NavigationDecision.Allowed
        }

        // SMART FAST-PATH for application-generated validated Google Search
        if (isSearchUrlValidated(trimmed)) {
            clearSearchUrlValidation()
            // Verify safe=active is present on the Google URL before fast-pathing
            if (trimmed.contains("safe=active")) {
                return NavigationDecision.Allowed
            }
        }

        // Focused Google SafeSearch evaluation (single-pass, zero duplicate processing):
        val uri = try { Uri.parse(trimmed) } catch (_: Exception) { null }
        val host = uri?.host?.lowercase(Locale.ROOT)
        val isGoogle = host != null && ProtectionEngine.isGoogleHost(host)

        if (isGoogle && uri != null) {
            val path = uri.path?.lowercase(Locale.ROOT) ?: ""
            if (path.contains("/search") || path.contains("/webhp") || path == "/" || path.isEmpty()) {
                val query = uri.getQueryParameter("q")
                if (!query.isNullOrBlank()) {
                    // Check custom keywords on untrusted Google search
                    val blockedKw = ProtectionEngine.isBlockedByCustomKeywords(query, customKeywords, normalizedKeywords)
                    if (blockedKw != null) {
                        return NavigationDecision.Blocked(
                            "Custom Keyword Protection",
                            "Search query blocked due to protected keyword: \"$blockedKw\""
                        )
                    }
                    // Enforce SafeSearch parameter (safe=active)
                    val isSafeActive = uri.getQueryParameter("safe")?.equals("active", ignoreCase = true) == true
                    return if (isSafeActive) {
                        NavigationDecision.Allowed
                    } else {
                        val safeUrl = ProtectionEngine.buildGoogleSafeSearchUrl(query)
                        NavigationDecision.Redirect(safeUrl)
                    }
                }
            }
            // Non-search Google navigation proceeds to destination checks without re-querying
        } else {
            // Search engine evaluation for non-Google engines (e.g. DuckDuckGo - untouched as required by scope)
            val searchEngineQuery = ProtectionEngine.extractSearchEngineQuery(trimmed)
            if (searchEngineQuery != null) {
                val blockedKw = ProtectionEngine.isBlockedByCustomKeywords(searchEngineQuery, customKeywords, normalizedKeywords)
                if (blockedKw != null) {
                    return NavigationDecision.Blocked(
                        "Custom Keyword Protection",
                        "Search query blocked due to protected keyword: \"$blockedKw\""
                    )
                }
                if (ProtectionEngine.isSafeSearchUrl(trimmed)) {
                    return NavigationDecision.Allowed
                }
                val safeUrl = ProtectionEngine.buildSafeSearchUrl(searchEngineQuery, searchEngine)
                return NavigationDecision.Redirect(safeUrl)
            }
        }

        // Audio & Download policies
        if (DownloadPolicy.isAudio(trimmed)) {
            return NavigationDecision.Download(trimmed, "audio/mpeg")
        }

        if (DownloadPolicy.isDownloadableFileUrl(trimmed)) {
            val decision = DownloadPolicy.evaluate(trimmed)
            return when (decision) {
                is DownloadPolicy.Result.Allowed -> NavigationDecision.Download(trimmed, null)
                is DownloadPolicy.Result.Blocked -> NavigationDecision.Blocked("Download Blocked", decision.reason)
            }
        }

        // Destination protection: Direct URL check against adult content, ad networks, and keywords
        val check = ProtectionEngine.checkDirectUrl(trimmed, customKeywords, normalizedKeywords)
        if (check is ProtectionEngine.FilterResult.Blocked) {
            return NavigationDecision.Blocked(check.reason, check.detail)
        }

        if (DownloadPolicy.shouldBlockUrlNavigation(trimmed)) {
            return NavigationDecision.Blocked("File Type Blocked", "This file type is blocked.")
        }

        return NavigationDecision.Allowed
    }
}
