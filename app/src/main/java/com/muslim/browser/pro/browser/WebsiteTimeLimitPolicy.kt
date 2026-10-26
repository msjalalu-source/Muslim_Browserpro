package com.muslim.browser.pro.browser

import android.net.Uri
import java.util.Calendar
import java.util.Locale

object WebsiteTimeLimitPolicy {

    /**
     * Fixed daily time limit per registered domain: exactly 20 minutes.
     * Hardcoded in application logic.
     */
    const val DAILY_LIMIT_SECONDS: Long = 20 * 60L // 1200 seconds

    /**
     * Normalizes a domain string before saving and validates domain structure:
     * - Trims whitespace
     * - Lowercases
     * - Strips protocol (http://, https://)
     * - Strips path, query, and port
     * - Strips leading "www."
     * - Rejects invalid domain names, empty strings, spaces
     */
    fun normalizeDomain(input: String): String? {
        var clean = input.trim().lowercase(Locale.ROOT)
        if (clean.isBlank()) return null

        if (clean.startsWith("http://")) clean = clean.removePrefix("http://")
        if (clean.startsWith("https://")) clean = clean.removePrefix("https://")

        val slashIdx = clean.indexOfAny(charArrayOf('/', '?', '#'))
        if (slashIdx != -1) {
            clean = clean.substring(0, slashIdx)
        }

        val colonIdx = clean.indexOf(':')
        if (colonIdx != -1) {
            clean = clean.substring(0, colonIdx)
        }

        while (clean.startsWith("www.")) {
            clean = clean.removePrefix("www.")
        }
        clean = clean.trimEnd('.')

        if (!clean.contains('.') || clean.contains(' ') || clean.length < 3) {
            return null
        }

        // Basic domain validation: letters, digits, hyphens, dots, valid TLD
        val domainRegex = Regex("^[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,}$")
        if (!domainRegex.matches(clean)) {
            return null
        }

        return clean
    }

    /**
     * Safely extracts the lowercase host from any URL.
     */
    fun extractHost(url: String): String? {
        val trimmed = url.trim()
        if (trimmed.isBlank() || trimmed.startsWith("about:") || trimmed.startsWith("data:")) return null
        return try {
            val candidate = if (!trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true)) {
                "https://$trimmed"
            } else {
                trimmed
            }
            Uri.parse(candidate).host?.lowercase(Locale.ROOT)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Finds which registered domain matches the given URL according to safe domain-matching rules:
     * - Exact host match: host == domain
     * - Subdomain match: host.endsWith(".$domain")
     * Prevents partial/unrelated matches (e.g. "myfacebook.com" will NOT match "facebook.com").
     */
    fun findMatchingDomain(url: String, registeredDomains: Collection<String>): String? {
        val host = extractHost(url) ?: return null
        for (domain in registeredDomains) {
            val d = domain.lowercase(Locale.ROOT).removePrefix("www.")
            if (d.isNotEmpty() && (host == d || host.endsWith(".$d"))) {
                return domain
            }
        }
        return null
    }

    /**
     * Determines whether the daily 20-minute limit for the given URL's domain has been reached.
     */
    fun isTimeLimitReached(
        url: String,
        registeredDomains: Collection<String>,
        domainUsageSeconds: Map<String, Long>
    ): Boolean {
        val matched = findMatchingDomain(url, registeredDomains) ?: return false
        val used = domainUsageSeconds[matched] ?: 0L
        return used >= DAILY_LIMIT_SECONDS
    }

    /**
     * Returns the local device calendar date in "yyyy-MM-dd" format.
     */
    fun getTodayDateString(): String {
        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.ROOT, "%04d-%02d-%02d", y, m, d)
    }

    /**
     * Formats seconds into a human-readable usage string (e.g. "12m 30s / 20m" or "Limit reached (20m / 20m)").
     */
    fun formatUsageStatus(usedSeconds: Long): String {
        if (usedSeconds >= DAILY_LIMIT_SECONDS) {
            return "Limit reached (20m / 20m)"
        }
        val mins = usedSeconds / 60L
        val secs = usedSeconds % 60L
        return "${mins}m ${secs}s / 20m"
    }
}
