package com.muslim.browser.pro.browser

import android.net.Uri
import java.util.Locale

object NavigationController {

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

        // Unified search engine evaluation for all supported search engines
        // (Google, Bing, Yahoo, Yandex, Brave, Ecosia, Startpage, Qwant, Ask, Baidu, Naver, AOL, Mojeek, DuckDuckGo)
        val searchEngineQuery = ProtectionEngine.extractSearchEngineQuery(trimmed)
        if (searchEngineQuery != null) {
            // 1. Check custom protected keywords on search query
            val blockedKw = ProtectionEngine.isBlockedByCustomKeywords(searchEngineQuery, customKeywords, normalizedKeywords)
            if (blockedKw != null) {
                return NavigationDecision.Blocked(
                    "Custom Keyword Protection",
                    "Search query blocked due to protected keyword: \"$blockedKw\""
                )
            }

            // 2. If already on safe.duckduckgo.com, do NOT redirect (prevent loop)
            if (ProtectionEngine.isDuckDuckGoSafeSearchUrl(trimmed)) {
                return NavigationDecision.Allowed
            }

            // 3. Enforce Strict Safe Search on DuckDuckGo searches without hijacking other search engines
            val isDdg = try {
                val host = Uri.parse(trimmed).host?.lowercase(Locale.ROOT) ?: ""
                host.contains("duckduckgo.com")
            } catch (_: Exception) { false }

            if (isDdg) {
                val safeUrl = ProtectionEngine.buildDuckDuckGoSafeSearchUrl(searchEngineQuery)
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
