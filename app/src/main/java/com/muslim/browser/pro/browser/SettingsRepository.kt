package com.muslim.browser.pro.browser

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
    // Pre-normalized lowercased keywords cache for O(1) string checks without repeated allocation
    private val inMemoryNormalizedKeywords = ArrayList<String>()

    // In-memory cache of favorite websites to avoid repeated JSON deserialization on UI renders
    private val inMemoryFavorites = ArrayList<FavoriteSite>()

    // In-memory cache of browsing history entries
    private val inMemoryHistory = ArrayList<HistoryEntry>()

    // Cached immutable snapshots to eliminate repeated .toSet() and .toList() allocations
    private var cachedKeywordsSet: Set<String> = emptySet()
    private var cachedFavoritesList: List<FavoriteSite> = emptyList()
    private var cachedHistoryList: List<HistoryEntry> = emptyList()

    init {
        val savedKeywords = prefs.getStringSet(KEY_CUSTOM_KEYWORDS, emptySet()) ?: emptySet()
        inMemoryKeywords.addAll(savedKeywords)
        cachedKeywordsSet = inMemoryKeywords.toSet()
        rebuildNormalizedKeywords()

        // Load favorites once from disk into memory
        loadFavoritesFromDisk()

        // Load browsing history once from disk into memory
        loadHistoryFromDisk()
    }

    private fun rebuildNormalizedKeywords() {
        inMemoryNormalizedKeywords.clear()
        for (kw in inMemoryKeywords) {
            val normalized = kw.trim().lowercase(Locale.ROOT)
            if (normalized.isNotEmpty() && !inMemoryNormalizedKeywords.contains(normalized)) {
                inMemoryNormalizedKeywords.add(normalized)
            }
        }
    }

    private fun loadFavoritesFromDisk() {
        inMemoryFavorites.clear()
        val rawJson = prefs.getString(KEY_FAVORITES, null)
        if (rawJson == null) {
            inMemoryFavorites.addAll(DEFAULT_FAVORITES)
            cachedFavoritesList = inMemoryFavorites.toList()
            return
        }
        try {
            val legacyRemovedDomains = setOf(
                "google.com", "wikipedia.org", "duckduckgo.com",
                "bbc.com", "reddit.com", "youtube.com", "stackoverflow.com"
            )
            val jsonArray = JSONArray(rawJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val url = obj.getString("url")
                val domain = FaviconManager.extractDomain(url)
                if (domain in legacyRemovedDomains) {
                    continue
                }
                inMemoryFavorites.add(
                    FavoriteSite(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        name = obj.getString("name"),
                        url = url,
                        iconLetter = obj.optString("iconLetter", obj.getString("name").take(2).uppercase()),
                        badgeColor = obj.optLong("badgeColor", 0xFF4285F4)
                    )
                )
            }
            // Ensure the 7 required default sites are present in the list
            for (featured in DEFAULT_FAVORITES.reversed()) {
                val existingIndex = inMemoryFavorites.indexOfFirst {
                    it.url.equals(featured.url, ignoreCase = true) || it.name.equals(featured.name, ignoreCase = true)
                }
                if (existingIndex == -1) {
                    inMemoryFavorites.add(0, featured)
                }
            }
            if (inMemoryFavorites.isEmpty()) {
                inMemoryFavorites.addAll(DEFAULT_FAVORITES)
            }
        } catch (_: Exception) {
            inMemoryFavorites.addAll(DEFAULT_FAVORITES)
        }
        cachedFavoritesList = inMemoryFavorites.toList()
    }

    private fun loadHistoryFromDisk() {
        inMemoryHistory.clear()
        val rawJson = prefs.getString(KEY_HISTORY, null)
        if (rawJson == null) {
            cachedHistoryList = emptyList()
            return
        }
        try {
            val jsonArray = JSONArray(rawJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                inMemoryHistory.add(
                    HistoryEntry(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        title = obj.optString("title", ""),
                        url = obj.getString("url"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        cachedHistoryList = inMemoryHistory.toList()
    }

    /**
     * Gets unmodifiable view of currently active custom keywords.
     * Returns cached immutable set snapshot to eliminate per-call allocations.
     */
    fun getCustomKeywords(): Set<String> {
        return cachedKeywordsSet
    }

    /**
     * Gets cached pre-normalized (lowercase, trimmed) keywords to avoid per-request allocations.
     */
    fun getNormalizedKeywords(): List<String> {
        return inMemoryNormalizedKeywords
    }

    /**
     * Adds a new protected keyword.
     * Prevents empty entries and duplicate entries (case-insensitive).
     * Returns true if successfully added, false if duplicate or blank.
     */
    fun addCustomKeyword(keyword: String): Boolean {
        val trimmed = keyword.trim()
        if (trimmed.isEmpty()) return false

        // Check duplicates (case-insensitive) using pre-normalized cache
        val normalized = trimmed.lowercase(Locale.ROOT)
        if (inMemoryNormalizedKeywords.contains(normalized)) {
            return false
        }

        inMemoryKeywords.add(trimmed)
        inMemoryNormalizedKeywords.add(normalized)
        cachedKeywordsSet = inMemoryKeywords.toSet()
        prefs.edit()
            .putStringSet(KEY_CUSTOM_KEYWORDS, cachedKeywordsSet)
            .apply()
        return true
    }

    /**
     * Retrieves the list of favorite websites from fast in-memory cache.
     * Returns cached immutable list to eliminate allocations.
     */
    fun getFavoriteSites(): List<FavoriteSite> {
        return cachedFavoritesList
    }

    /**
     * Saves the updated list of favorite websites to SharedPreferences asynchronously.
     */
    fun saveFavoriteSites(sites: List<FavoriteSite>) {
        inMemoryFavorites.clear()
        inMemoryFavorites.addAll(sites)
        cachedFavoritesList = inMemoryFavorites.toList()

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
        val current = inMemoryFavorites.toMutableList()
        current.add(newSite)
        saveFavoriteSites(current)
        return newSite
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

    var translationMode: TranslationMode
        get() {
            val saved = prefs.getString(KEY_TRANSLATION_MODE_SELECTION, TranslationMode.MT.name)
            return try {
                TranslationMode.valueOf(saved ?: TranslationMode.MT.name)
            } catch (_: Exception) {
                TranslationMode.MT
            }
        }
        set(value) {
            prefs.edit().putString(KEY_TRANSLATION_MODE_SELECTION, value.name).apply()
        }

    // ==========================================
    // BROWSING HISTORY PERSISTENCE
    // ==========================================

    /**
     * Returns unmodifiable list of browsing history entries (most recent first).
     * Returns cached immutable list to eliminate allocations.
     */
    fun getHistory(): List<HistoryEntry> {
        return cachedHistoryList
    }

    /**
     * Adds an entry to browsing history.
     * Prevents empty/about: URLs and duplicate entries on consecutive page loads.
     */
    fun addHistoryEntry(title: String, url: String): HistoryEntry? {
        val trimmedUrl = url.trim()
        if (trimmedUrl.isBlank() || trimmedUrl == "about:blank" || trimmedUrl.startsWith("about:")) {
            return null
        }
        val effectiveTitle = if (title.isNotBlank()) title.trim() else trimmedUrl

        // Deduplication: if the newest entry has the exact same URL, update its title/timestamp
        val first = inMemoryHistory.firstOrNull()
        if (first != null && first.url == trimmedUrl) {
            val updated = first.copy(
                title = if (effectiveTitle != trimmedUrl) effectiveTitle else first.title,
                timestamp = System.currentTimeMillis()
            )
            inMemoryHistory[0] = updated
            saveHistoryToDisk()
            return updated
        }

        val newEntry = HistoryEntry(
            title = effectiveTitle,
            url = trimmedUrl,
            timestamp = System.currentTimeMillis()
        )
        inMemoryHistory.add(0, newEntry)
        // Keep history lightweight: cap at 500 entries
        if (inMemoryHistory.size > 500) {
            inMemoryHistory.removeAt(inMemoryHistory.size - 1)
        }
        saveHistoryToDisk()
        return newEntry
    }

    /**
     * Deletes a specific browsing history item.
     */
    fun deleteHistoryEntry(id: String): Boolean {
        val removed = inMemoryHistory.removeAll { it.id == id }
        if (removed) {
            saveHistoryToDisk()
        }
        return removed
    }

    /**
     * Clears all browsing history.
     */
    fun clearHistory() {
        inMemoryHistory.clear()
        cachedHistoryList = emptyList()
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveHistoryToDisk() {
        cachedHistoryList = inMemoryHistory.toList()
        val jsonArray = JSONArray()
        for (item in inMemoryHistory) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("url", item.url)
                put("timestamp", item.timestamp)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }

    // ==========================================
    // WINDOW / TAB STATE PERSISTENCE
    // ==========================================

    /**
     * Persists all open browser windows/tabs and the currently active tab ID.
     * Preserves tab order, URLs, titles, and active window state across app restarts
     * and Recent Apps swipe-away.
     */
    fun saveTabs(tabs: List<BrowserTab>, activeTabId: String) {
        val jsonArray = JSONArray()
        for (tab in tabs) {
            val obj = JSONObject().apply {
                put("id", tab.id)
                put("url", tab.url)
                put("pageTitle", tab.pageTitle)
                put("isHomePage", tab.isHomePage)
                put("searchInput", tab.searchInput)
            }
            jsonArray.put(obj)
        }
        prefs.edit()
            .putString(KEY_SAVED_TABS, jsonArray.toString())
            .putString(KEY_ACTIVE_TAB_ID, activeTabId)
            .apply()
    }

    /**
     * Restores saved browser windows/tabs and the active tab ID.
     * Returns null if no saved state exists.
     */
    fun getSavedTabs(): Pair<List<BrowserTab>, String>? {
        val rawJson = prefs.getString(KEY_SAVED_TABS, null) ?: return null
        val activeTabId = prefs.getString(KEY_ACTIVE_TAB_ID, null)
        return try {
            val jsonArray = JSONArray(rawJson)
            if (jsonArray.length() == 0) return null
            val list = mutableListOf<BrowserTab>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("id")
                val url = obj.optString("url", "")
                val pageTitle = obj.optString("pageTitle", if (url.isNotEmpty()) url else "Home")
                val isHomePage = obj.optBoolean("isHomePage", url.isEmpty())
                val searchInput = obj.optString("searchInput", url)
                list.add(
                    BrowserTab(
                        id = id,
                        url = url,
                        pageTitle = pageTitle,
                        isHomePage = isHomePage,
                        searchInput = searchInput
                    )
                )
            }
            if (list.isEmpty()) return null
            val effectiveActiveId = if (list.any { it.id == activeTabId }) {
                activeTabId!!
            } else {
                list.first().id
            }
            Pair(list, effectiveActiveId)
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private const val PREFS_NAME = "focus_shield_prefs"
        private const val KEY_CUSTOM_KEYWORDS = "key_custom_keywords"
        private const val KEY_POPUP_BLOCKING = "key_popup_blocking"
        private const val KEY_AD_BLOCKING = "key_ad_blocking"
        private const val KEY_DESKTOP_MODE = "key_desktop_mode"
        private const val KEY_TRANSLATION_MODE_SELECTION = "key_translation_mode_selection"
        private const val KEY_FAVORITES = "key_favorite_sites"
        private const val KEY_SAVED_TABS = "key_saved_tabs"
        private const val KEY_ACTIVE_TAB_ID = "key_active_tab_id"
        private const val KEY_HISTORY = "key_browsing_history"

        val DEFAULT_FAVORITES = listOf(
            FavoriteSite(id = "fav_moldovalive", name = "MoldovaLive", url = "https://moldovalive.md", iconLetter = "ML", badgeColor = 0xFF00796B),
            FavoriteSite(id = "fav_moldova1", name = "Moldova1", url = "https://moldova1.md/i/en", iconLetter = "M1", badgeColor = 0xFF1565C0),
            FavoriteSite(id = "fav_aistudio", name = "Google AI Studio", url = "https://aistudio.google.com", iconLetter = "AI", badgeColor = 0xFF1A73E8),
            FavoriteSite(id = "fav_github", name = "GitHub", url = "https://github.com/", iconLetter = "GH", badgeColor = 0xFF24292E),
            FavoriteSite(id = "fav_prothomalo", name = "Prothom Alo ePaper", url = "https://epaper.prothomalo.com/Home", iconLetter = "PA", badgeColor = 0xFFD32F2F),
            FavoriteSite(id = "fav_dailystar", name = "The Daily Star Bangla", url = "https://bangla.thedailystar.net", iconLetter = "DS", badgeColor = 0xFF283593),
            FavoriteSite(id = "fav_ittefaq", name = "Ittefaq", url = "https://www.ittefaq.com.bd", iconLetter = "IT", badgeColor = 0xFFE65100)
        )
    }
}

data class HistoryEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)
