package com.muslim.browser.pro.browser

import android.content.Intent
import android.net.Uri

/**
 * Handles incoming Android Sharesheet (ACTION_SEND) and deep link (ACTION_VIEW) intents.
 * Safely parses, sanitizes, and extracts URLs or search queries for Muslim Browser Pro.
 */
object ShareIntentHandler {

    const val EXTRA_PROCESSED = "com.muslim.browser.pro.SHARE_INTENT_PROCESSED"
    const val MAX_INPUT_LENGTH = 8192

    // Matches standard HTTP and HTTPS URLs within text
    private val HTTP_URL_PATTERN = Regex(
        "https?://[^\\s<>\"'{}|\\\\^`]+",
        RegexOption.IGNORE_CASE
    )

    /**
     * Extracts a sanitized URL or search query from an incoming intent.
     * Returns null if the intent is not a supported share/view action or has no usable content.
     */
    fun extractContent(intent: Intent?): String? {
        if (intent == null) return null
        val action = intent.action ?: return null

        return when (action) {
            Intent.ACTION_SEND -> handleSendIntent(intent)
            Intent.ACTION_VIEW -> handleViewIntent(intent)
            else -> null
        }
    }

    /**
     * Processes ACTION_SEND intents with text/plain or compatible mimeTypes.
     */
    private fun handleSendIntent(intent: Intent): String? {
        val type = intent.type ?: ""
        // Validate that mimeType is text-compatible
        if (!type.startsWith("text/") && type != "*/*") {
            return null
        }

        val rawText = intent.getStringExtra(Intent.EXTRA_TEXT)
            ?: intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
            ?: intent.dataString

        return extractFromText(rawText)
    }

    /**
     * Processes ACTION_VIEW intents containing a web URI.
     */
    private fun handleViewIntent(intent: Intent): String? {
        val dataString = intent.dataString ?: intent.data?.toString()
        if (dataString.isNullOrBlank()) return null

        val trimmed = dataString.trim()
        val lower = trimmed.lowercase()
        return if (lower.startsWith("http://") || lower.startsWith("https://")) {
            if (isValidWebUri(trimmed)) trimmed else null
        } else {
            null
        }
    }

    /**
     * Analyzes raw text and returns either an extracted URL or search query.
     */
    fun extractFromText(rawText: String?): String? {
        if (rawText.isNullOrBlank()) return null

        // Cap length to prevent DoS from excessively long input
        val boundedText = if (rawText.length > MAX_INPUT_LENGTH) {
            rawText.substring(0, MAX_INPUT_LENGTH)
        } else {
            rawText
        }.trim()

        if (boundedText.isEmpty()) return null

        // 1. Direct web URL check
        if (isDirectHttpUrl(boundedText)) {
            return boundedText
        }

        // 2. Search for embedded URL within text (e.g. from Notes, email, or social apps)
        val match = HTTP_URL_PATTERN.find(boundedText)
        if (match != null) {
            val candidate = cleanTrailingPunctuation(match.value)
            if (isDirectHttpUrl(candidate)) {
                return candidate
            }
        }

        // 3. Check for standalone domain-like string (e.g. "example.com", "www.islamweb.net")
        if (!boundedText.contains(" ") && boundedText.contains(".") && boundedText.length >= 4) {
            val formatted = if (!boundedText.startsWith("http://", ignoreCase = true) &&
                !boundedText.startsWith("https://", ignoreCase = true)
            ) {
                "https://$boundedText"
            } else {
                boundedText
            }
            if (isValidWebUri(formatted)) {
                return boundedText
            }
        }

        // 4. Fallback to clean plain text for SafeSearch query
        return boundedText
    }

    /**
     * Checks if a string is a valid direct HTTP/HTTPS URL.
     */
    fun isDirectHttpUrl(url: String): Boolean {
        val trimmed = url.trim()
        val lower = trimmed.lowercase()
        if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
            return false
        }
        return isValidWebUri(trimmed)
    }

    /**
     * Validates that a string parses as a valid web URI with a non-empty host.
     */
    fun isValidWebUri(uriString: String): Boolean {
        return try {
            val parsed = Uri.parse(uriString)
            val scheme = parsed.scheme?.lowercase()
            val host = parsed.host
            (scheme == "http" || scheme == "https") && !host.isNullOrBlank()
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Strips common trailing punctuation that might follow a URL in natural text.
     */
    private fun cleanTrailingPunctuation(url: String): String {
        return url.trimEnd('.', ',', ';', ':', '!', '?', ')', ']', '}', '>', '"', '\'')
    }

    /**
     * Checks if an intent has already been processed to prevent duplicate navigation.
     */
    fun isAlreadyProcessed(intent: Intent?): Boolean {
        return intent?.getBooleanExtra(EXTRA_PROCESSED, false) == true
    }

    /**
     * Marks an intent as processed.
     */
    fun markAsProcessed(intent: Intent?) {
        intent?.putExtra(EXTRA_PROCESSED, true)
    }
}
