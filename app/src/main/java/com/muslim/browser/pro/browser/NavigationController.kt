package com.muslim.browser.pro.browser

import android.net.Uri
import java.util.Locale

object NavigationController {

    fun evaluate(
        url: String,
        customKeywords: Set<String>,
        normalizedKeywords: Collection<String>? = null,
        searchEngine: SearchEngine = SearchEngine.DUCKDUCKGO,
        isAdBlockingEnabled: Boolean = true,
        timeLimitedDomains: Set<String> = emptySet(),
        domainUsageSeconds: Map<String, Long> = emptyMap()
    ): NavigationDecision {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) {
            return NavigationDecision.Allowed
        }

        // 0. Website Time Limit enforcement for registered domains
        if (timeLimitedDomains.isNotEmpty()) {
            val matchedDomain = WebsiteTimeLimitPolicy.findMatchingDomain(trimmed, timeLimitedDomains)
            if (matchedDomain != null) {
                val used = domainUsageSeconds[matchedDomain] ?: 0L
                if (used >= WebsiteTimeLimitPolicy.DAILY_LIMIT_SECONDS) {
                    return NavigationDecision.Blocked(
                        "Website Time Limit",
                        "The daily 20-minute limit for \"$matchedDomain\" has been reached."
                    )
                }
            }
        }

        // 1. Search engine evaluation for supported search engines
        val searchEngineQuery = ProtectionEngine.extractSearchEngineQuery(trimmed)
        if (searchEngineQuery != null) {
            // First: keyword check on search query
            val blockedKw = ProtectionEngine.isBlockedByCustomKeywords(searchEngineQuery, customKeywords, normalizedKeywords)
            if (blockedKw != null) {
                return NavigationDecision.Blocked(
                    "Custom Keyword Protection",
                    "Search query blocked due to protected keyword: \"$blockedKw\""
                )
            }

            val parsedUri = try { Uri.parse(trimmed) } catch (_: Exception) { null }
            val host = parsedUri?.host?.lowercase(Locale.ROOT) ?: ""

            val isSelectedEngine = when (searchEngine) {
                SearchEngine.DUCKDUCKGO -> host.contains("duckduckgo.com")
                SearchEngine.GOOGLE -> ProtectionEngine.isGoogleHost(host)
            }

            if (!isSelectedEngine) {
                // a) URL belongs to a DIFFERENT engine than the selected searchEngine:
                return NavigationDecision.Redirect(searchEngine.buildSearchUrl(searchEngineQuery))
            } else {
                // b) URL belongs to the selected engine:
                when (searchEngine) {
                    SearchEngine.DUCKDUCKGO -> {
                        if (ProtectionEngine.isDuckDuckGoSafeSearchUrl(trimmed)) {
                            return NavigationDecision.Allowed
                        }
                        val safeUrl = SearchEngine.DUCKDUCKGO.buildSearchUrl(searchEngineQuery)
                        return NavigationDecision.Redirect(safeUrl)
                    }
                    SearchEngine.GOOGLE -> {
                        val safeParam = parsedUri?.getQueryParameter("safe")
                        if (safeParam.equals("active", ignoreCase = true)) {
                            return NavigationDecision.Allowed
                        }
                        val path = parsedUri?.path?.lowercase(Locale.ROOT) ?: ""
                        if (path.contains("/search") && parsedUri != null) {
                            val builder = parsedUri.buildUpon().clearQuery()
                            for (paramName in parsedUri.queryParameterNames) {
                                if (!paramName.equals("safe", ignoreCase = true)) {
                                    for (paramValue in parsedUri.getQueryParameters(paramName)) {
                                        builder.appendQueryParameter(paramName, paramValue)
                                    }
                                }
                            }
                            builder.appendQueryParameter("safe", "active")
                            return NavigationDecision.Redirect(builder.build().toString())
                        } else {
                            return NavigationDecision.Redirect(searchEngine.buildSearchUrl(searchEngineQuery))
                        }
                    }
                }
            }
        }

        // 2. Generic website search URLs (e.g. Wikipedia/GitHub search) - keep only keyword check
        val genericQuery = ProtectionEngine.extractGenericSearchQuery(trimmed)
        if (genericQuery != null) {
            val blockedKw = ProtectionEngine.isBlockedByCustomKeywords(genericQuery, customKeywords, normalizedKeywords)
            if (blockedKw != null) {
                return NavigationDecision.Blocked(
                    "Custom Keyword Protection",
                    "Search query blocked due to protected keyword: \"$blockedKw\""
                )
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
        val check = ProtectionEngine.checkDirectUrl(trimmed, customKeywords, normalizedKeywords, isAdBlockingEnabled)
        if (check is ProtectionEngine.FilterResult.Blocked) {
            return NavigationDecision.Blocked(check.reason, check.detail)
        }

        if (DownloadPolicy.shouldBlockUrlNavigation(trimmed)) {
            return NavigationDecision.Blocked("File Type Blocked", "This file type is blocked.")
        }

        return NavigationDecision.Allowed
    }
}
