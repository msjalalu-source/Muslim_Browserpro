package com.example.browser

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale

/**
 * Ultra-lightweight repository backed by Android SharedPreferences.
 * Stores Pop-up Blocking state, Ad Blocking state, and permanently protected Custom Keywords.
 *
 * NOTE: As per strict security requirements:
 * - No delete, edit, or clear functions are provided for custom keywords.
 * - Once added, keywords cannot be removed or disabled.
 */
class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // In-memory cache of keywords to avoid disk reads on every URL evaluation
    private val inMemoryKeywords = LinkedHashSet<String>()

    init {
        val saved = prefs.getStringSet(KEY_CUSTOM_KEYWORDS, emptySet()) ?: emptySet()
        inMemoryKeywords.addAll(saved)
    }

    /**
     * Gets unmodifiable view of currently active custom keywords.
     */
    fun getCustomKeywords(): Set<String> {
        return inMemoryKeywords.toSet()
    }

    /**
     * Adds a new protected keyword.
     * Prevents empty entries and duplicate entries (case-insensitive).
     * Returns true if successfully added, false if duplicate or blank.
     */
    fun addCustomKeyword(keyword: String): Boolean {
        val trimmed = keyword.trim()
        if (trimmed.isEmpty()) return false

        // Check duplicates (case-insensitive)
        for (existing in inMemoryKeywords) {
            if (existing.equals(trimmed, ignoreCase = true)) {
                return false
            }
        }

        inMemoryKeywords.add(trimmed)
        prefs.edit()
            .putStringSet(KEY_CUSTOM_KEYWORDS, inMemoryKeywords.toSet())
            .apply()
        return true
    }

    var isPopupBlockingEnabled: Boolean
        get() = prefs.getBoolean(KEY_POPUP_BLOCKING, true)
        set(value) {
            prefs.edit().putBoolean(KEY_POPUP_BLOCKING, value).apply()
        }

    var isAdBlockingEnabled: Boolean
        get() = prefs.getBoolean(KEY_AD_BLOCKING, true)
        set(value) {
            prefs.edit().putBoolean(KEY_AD_BLOCKING, value).apply()
        }

    var isDesktopModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_DESKTOP_MODE, false)
        set(value) {
            prefs.edit().putBoolean(KEY_DESKTOP_MODE, value).apply()
        }

    companion object {
        private const val PREFS_NAME = "focus_shield_prefs"
        private const val KEY_CUSTOM_KEYWORDS = "key_custom_keywords"
        private const val KEY_POPUP_BLOCKING = "key_popup_blocking"
        private const val KEY_AD_BLOCKING = "key_ad_blocking"
        private const val KEY_DESKTOP_MODE = "key_desktop_mode"
    }
}
