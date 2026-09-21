package com.example.browser

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

/**
 * Ultra-lightweight repository backed by Android SharedPreferences.
 * Stores Pop-up Blocking state, Ad Blocking state, permanently protected Custom Keywords,
 * and persistent Favorite Websites.
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

    /**
     * Retrieves the persistent list of favorite websites.
     * Falls back to default favorites on initial launch.
     */
    fun getFavoriteSites(): List<FavoriteSite> {
        val rawJson = prefs.getString(KEY_FAVORITES, null) ?: return DEFAULT_FAVORITES
        return try {
            val jsonArray = JSONArray(rawJson)
            val list = mutableListOf<FavoriteSite>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    FavoriteSite(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        name = obj.getString("name"),
                        url = obj.getString("url"),
                        iconLetter = obj.optString("iconLetter", obj.getString("name").take(2).uppercase()),
                        badgeColor = obj.optLong("badgeColor", 0xFF4285F4)
                    )
                )
            }
            if (list.isEmpty()) DEFAULT_FAVORITES else list
        } catch (e: Exception) {
            DEFAULT_FAVORITES
        }
    }

    /**
     * Saves the updated list of favorite websites to SharedPreferences.
     */
    fun saveFavoriteSites(sites: List<FavoriteSite>) {
        val jsonArray = JSONArray()
        for (site in sites) {
            val obj = JSONObject().apply {
                put("id", site.id)
                put("name", site.name)
                put("url", site.url)
                put("iconLetter", site.iconLetter)
                put("badgeColor", site.badgeColor)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_FAVORITES, jsonArray.toString()).apply()
    }

    /**
     * Adds a new favorite website.
     * Formats URL with https:// if scheme is missing, computes iconLetter and badge color.
     */
    fun addFavoriteSite(name: String, url: String): FavoriteSite {
        val current = getFavoriteSites().toMutableList()
        val trimmedName = name.trim()
        val trimmedUrl = url.trim()
        val formattedUrl = if (!trimmedUrl.startsWith("http://", ignoreCase = true) && !trimmedUrl.startsWith("https://", ignoreCase = true)) {
            "https://$trimmedUrl"
        } else trimmedUrl
        val letter = if (trimmedName.isNotBlank()) trimmedName.take(2).uppercase() else "W"
        val palette = listOf(
            0xFF4285F4, 0xFF00897B, 0xFF5E35B1, 0xFF00ACC1,
            0xFFD81B60, 0xFF3949AB, 0xFFFB8C00, 0xFF43A047, 0xFF333333
        )
        val color = palette[Math.abs(trimmedName.hashCode() % palette.size)]
        val newSite = FavoriteSite(
            id = java.util.UUID.randomUUID().toString(),
            name = trimmedName,
            url = formattedUrl,
            iconLetter = letter,
            badgeColor = color
        )
        current.add(newSite)
        saveFavoriteSites(current)
        return newSite
    }

    /**
     * Updates an existing favorite website.
     */
    fun updateFavoriteSite(id: String, name: String, url: String): Boolean {
        val current = getFavoriteSites().toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index == -1) return false
        val existing = current[index]
        val trimmedName = name.trim()
        val trimmedUrl = url.trim()
        val formattedUrl = if (!trimmedUrl.startsWith("http://", ignoreCase = true) && !trimmedUrl.startsWith("https://", ignoreCase = true)) {
            "https://$trimmedUrl"
        } else trimmedUrl
        val letter = if (trimmedName.isNotBlank()) trimmedName.take(2).uppercase() else existing.iconLetter
        val updated = existing.copy(
            name = trimmedName,
            url = formattedUrl,
            iconLetter = letter
        )
        current[index] = updated
        saveFavoriteSites(current)
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
        private const val KEY_FAVORITES = "key_favorite_sites"

        val DEFAULT_FAVORITES = listOf(
            FavoriteSite(id = "fav_google", name = "Google", url = "https://www.google.com", iconLetter = "G", badgeColor = 0xFF4285F4),
            FavoriteSite(id = "fav_wikipedia", name = "Wikipedia", url = "https://www.wikipedia.org", iconLetter = "W", badgeColor = 0xFF333333),
            FavoriteSite(id = "fav_duckduckgo", name = "DuckDuckGo", url = "https://duckduckgo.com", iconLetter = "D", badgeColor = 0xFFDE5833),
            FavoriteSite(id = "fav_github", name = "GitHub", url = "https://www.github.com", iconLetter = "GH", badgeColor = 0xFF24292E),
            FavoriteSite(id = "fav_bbc", name = "BBC News", url = "https://www.bbc.com/news", iconLetter = "B", badgeColor = 0xFFBB1919),
            FavoriteSite(id = "fav_reddit", name = "Reddit", url = "https://www.reddit.com", iconLetter = "R", badgeColor = 0xFFFF4500),
            FavoriteSite(id = "fav_youtube", name = "YouTube", url = "https://www.youtube.com", iconLetter = "Y", badgeColor = 0xFFFF0000),
            FavoriteSite(id = "fav_stackoverflow", name = "Stack Overflow", url = "https://stackoverflow.com", iconLetter = "SO", badgeColor = 0xFFF48024)
        )
    }
}
