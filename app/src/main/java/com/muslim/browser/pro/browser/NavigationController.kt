package com.muslim.browser.pro.browser

/**
 * Consolidated, authoritative decision pipeline for URL navigation.
 * Evaluates:
 * 1. Search engine queries -> custom keyword check -> SafeSearch redirection
 * 2. Audio/MP3 direct downloads -> Download decision
 * 3. Unified downloadable files (documents, archives, APKs) -> Download decision or Blocked
 * 4. Direct URL protection (adult content, custom keyword in URL) -> Blocked
 * 5. Blocked file type direct navigation -> Blocked
 * 6. Normal safe web navigation -> Allowed
 */
sealed class NavigationDecision {
    object Allowed : NavigationDecision()
    data class Blocked(val reason: String, val detail: String) : NavigationDecision()
    data class Redirect(val url: String) : NavigationDecision()
    data class Download(
        val url: String,
        val mimeType: String? = null
    ) : NavigationDecision()
}

object NavigationController {

    fun evaluate(
        url: String,
        customKeywords: Set<String>,
        normalizedKeywords: Collection<String>? = null,
        searchEngine: SearchEngine = SearchEngine.DUCKDUCKGO
    ): NavigationDecision {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return NavigationDecision.Allowed

        // 1. Search engine request check
        val searchEngineQuery = ProtectionEngine.extractSearchEngineQuery(trimmed)
        if (searchEngineQuery != null) {
            val blockedKw = ProtectionEngine.isBlockedByCustomKeywords(
                searchEngineQuery,
                customKeywords,
                normalizedKeywords
            )
            if (blockedKw != null) {
                return NavigationDecision.Blocked(
                    reason = "Custom Keyword Protection",
                    detail = "Search query blocked due to protected keyword: \"$blockedKw\""
                )
            }

            if (ProtectionEngine.isSafeSearchUrl(trimmed)) {
                return NavigationDecision.Allowed
            }

            val safeUrl = ProtectionEngine.buildSafeSearchUrl(searchEngineQuery, searchEngine)
            return NavigationDecision.Redirect(safeUrl)
        }

        // 2. Direct audio link check
        if (DownloadPolicy.isAudio(trimmed)) {
            return NavigationDecision.Download(trimmed, "audio/mpeg")
        }

        // 3. Early download interception for unified downloadable files (documents, archives, APKs)
        if (DownloadPolicy.isDownloadableFileUrl(trimmed)) {
            val decision = DownloadPolicy.evaluate(trimmed)
            return when (decision) {
                is DownloadPolicy.Result.Allowed -> NavigationDecision.Download(trimmed, null)
                is DownloadPolicy.Result.Blocked -> NavigationDecision.Blocked(
                    reason = "Download Blocked",
                    detail = decision.reason
                )
            }
        }

        // 4. Direct URL Protection Check (Adult content, custom keywords in URL)
        val check = ProtectionEngine.checkDirectUrl(trimmed, customKeywords, normalizedKeywords)
        if (check is ProtectionEngine.FilterResult.Blocked) {
            return NavigationDecision.Blocked(check.reason, check.detail)
        }

        // 5. Direct navigation check for blocked file types (e.g. video links, unauthorized APKs)
        if (DownloadPolicy.shouldBlockUrlNavigation(trimmed)) {
            return NavigationDecision.Blocked(
                reason = "File Type Blocked",
                detail = "This file type is blocked."
            )
        }

        return NavigationDecision.Allowed
    }
}
