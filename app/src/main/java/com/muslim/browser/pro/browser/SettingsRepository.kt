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

    // In-memory cache of user-added keywords persisted in SharedPreferences
    private val inMemoryUserKeywords = LinkedHashSet<String>()
    // In-memory cache of all effective keywords (built-in protected defaults + user-added keywords)
    private val inMemoryKeywords = LinkedHashSet<String>()
    // Pre-normalized lowercased keywords cache for O(1) string checks without repeated allocation
    private val inMemoryNormalizedKeywords = ArrayList<String>()

    // In-memory cache of favorite websites to avoid repeated JSON deserialization on UI renders
    private val inMemoryFavorites = ArrayList<FavoriteSite>()

    // In-memory cache of browsing history entries
    private val inMemoryHistory = ArrayList<HistoryEntry>()

    // In-memory cache of downloads initiated by this browser
    private val inMemoryDownloads = ArrayList<DownloadEntry>()

    // Cached immutable snapshots to eliminate repeated .toSet() and .toList() allocations
    private var cachedKeywordsSet: Set<String> = emptySet()
    private var cachedFavoritesList: List<FavoriteSite> = emptyList()
    private var cachedHistoryList: List<HistoryEntry> = emptyList()
    private var cachedDownloadList: List<DownloadEntry> = emptyList()

    init {
        val savedKeywords = prefs.getStringSet(KEY_CUSTOM_KEYWORDS, emptySet()) ?: emptySet()
        inMemoryUserKeywords.addAll(savedKeywords)
        rebuildEffectiveKeywords()

        // Load favorites once from disk into memory
        loadFavoritesFromDisk()

        // Load browsing history once from disk into memory
        loadHistoryFromDisk()

        // Load download history once from disk into memory
        loadDownloadHistoryFromDisk()
    }

    private fun rebuildEffectiveKeywords() {
        inMemoryKeywords.clear()
        inMemoryKeywords.addAll(DEFAULT_PROTECTED_KEYWORDS)
        inMemoryKeywords.addAll(inMemoryUserKeywords)
        cachedKeywordsSet = inMemoryKeywords.toSet()
        rebuildNormalizedKeywords()
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
            // Ensure the required default sites are present in the list in their defined order
            for (featured in DEFAULT_FAVORITES) {
                val existingIndex = inMemoryFavorites.indexOfFirst {
                    it.url.equals(featured.url, ignoreCase = true) || it.name.equals(featured.name, ignoreCase = true)
                }
                if (existingIndex == -1) {
                    val defaultIndex = DEFAULT_FAVORITES.indexOf(featured)
                    if (defaultIndex in 0..inMemoryFavorites.size) {
                        inMemoryFavorites.add(defaultIndex, featured)
                    } else {
                        inMemoryFavorites.add(featured)
                    }
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

    private fun loadDownloadHistoryFromDisk() {
        inMemoryDownloads.clear()
        val rawJson = prefs.getString(KEY_DOWNLOAD_HISTORY, null)
        if (rawJson == null) {
            cachedDownloadList = emptyList()
            return
        }
        try {
            val jsonArray = JSONArray(rawJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val statusStr = obj.optString("status", DownloadStatus.COMPLETED.name)
                val status = try {
                    DownloadStatus.valueOf(statusStr)
                } catch (_: Exception) {
                    DownloadStatus.COMPLETED
                }
                inMemoryDownloads.add(
                    DownloadEntry(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        downloadId = obj.optLong("downloadId", -1L),
                        fileName = obj.optString("fileName", "download"),
                        url = obj.optString("url", ""),
                        mimeType = obj.optString("mimeType", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        status = status,
                        downloadedBytes = obj.optLong("downloadedBytes", 0L),
                        totalBytes = obj.optLong("totalBytes", -1L),
                        localUri = obj.optString("localUri", "").ifEmpty { null }
                    )
                )
            }
        } catch (_: Exception) {}
        cachedDownloadList = inMemoryDownloads.toList()
    }

    /**
     * Gets unmodifiable view of currently active custom keywords (built-in protected defaults + user-added).
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
     * Checks if a keyword is one of the built-in protected defaults that cannot be deleted, removed, or overwritten.
     */
    fun isDefaultProtectedKeyword(keyword: String): Boolean {
        val normalized = keyword.trim().lowercase(Locale.ROOT)
        return DEFAULT_PROTECTED_KEYWORDS.any { it.trim().lowercase(Locale.ROOT) == normalized }
    }

    /**
     * Adds a new user protected keyword.
     * Prevents empty entries and duplicate entries (case-insensitive) against all effective keywords.
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

        inMemoryUserKeywords.add(trimmed)
        inMemoryKeywords.add(trimmed)
        inMemoryNormalizedKeywords.add(normalized)
        cachedKeywordsSet = inMemoryKeywords.toSet()
        prefs.edit()
            .putStringSet(KEY_CUSTOM_KEYWORDS, inMemoryUserKeywords)
            .apply()
        return true
    }

    /**
     * Removes a user-added keyword. Built-in protected defaults CANNOT be removed or deleted.
     * Returns true if removed, false if not found or is a built-in protected default.
     */
    fun removeCustomKeyword(keyword: String): Boolean {
        if (isDefaultProtectedKeyword(keyword)) {
            return false
        }
        val removed = inMemoryUserKeywords.remove(keyword)
        if (removed) {
            rebuildEffectiveKeywords()
            prefs.edit()
                .putStringSet(KEY_CUSTOM_KEYWORDS, inMemoryUserKeywords)
                .apply()
        }
        return removed
    }

    /**
     * Clears user-added keywords. Built-in protected defaults are always retained.
     */
    fun clearUserKeywords() {
        inMemoryUserKeywords.clear()
        rebuildEffectiveKeywords()
        prefs.edit()
            .remove(KEY_CUSTOM_KEYWORDS)
            .apply()
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

    var desktopArchitecture: DesktopArchitecture
        get() {
            val raw = prefs.getString(KEY_DESKTOP_ARCHITECTURE, null)
            if (raw != null) {
                return try {
                    DesktopArchitecture.valueOf(raw)
                } catch (_: Exception) {
                    DesktopArchitecture.NONE
                }
            }
            val isWin10 = prefs.getBoolean(KEY_WINDOWS_10_TOUCH, false)
            val isDesktop = prefs.getBoolean(KEY_DESKTOP_MODE, false)
            val migrated = when {
                isWin10 -> DesktopArchitecture.WINDOWS_10_TOUCH
                isDesktop -> DesktopArchitecture.STANDARD
                else -> DesktopArchitecture.NONE
            }
            prefs.edit().putString(KEY_DESKTOP_ARCHITECTURE, migrated.name).apply()
            return migrated
        }
        set(value) {
            prefs.edit()
                .putString(KEY_DESKTOP_ARCHITECTURE, value.name)
                .putBoolean(KEY_DESKTOP_MODE, value == DesktopArchitecture.STANDARD)
                .putBoolean(KEY_WINDOWS_10_TOUCH, value == DesktopArchitecture.WINDOWS_10_TOUCH)
                .apply()
        }

    var isDesktopModeEnabled: Boolean
        get() = desktopArchitecture == DesktopArchitecture.STANDARD
        set(value) {
            desktopArchitecture = if (value) DesktopArchitecture.STANDARD else DesktopArchitecture.NONE
        }

    var isWindows10TouchEnabled: Boolean
        get() = desktopArchitecture == DesktopArchitecture.WINDOWS_10_TOUCH
        set(value) {
            desktopArchitecture = if (value) DesktopArchitecture.WINDOWS_10_TOUCH else DesktopArchitecture.NONE
        }

    var isDesktopMode4Enabled: Boolean
        get() = desktopArchitecture == DesktopArchitecture.DESKTOP_MODE_4
        set(value) {
            desktopArchitecture = if (value) DesktopArchitecture.DESKTOP_MODE_4 else DesktopArchitecture.NONE
        }

    var appTheme: com.muslim.browser.pro.ui.theme.AppTheme
        get() {
            val rawName = prefs.getString(KEY_APP_THEME, null)
                ?: return com.muslim.browser.pro.ui.theme.AppTheme.BLACK_WHITE
            return try {
                com.muslim.browser.pro.ui.theme.AppTheme.valueOf(rawName)
            } catch (_: Exception) {
                // Automatically migrate legacy "BLACK" or invalid theme preference to default (BLACK_WHITE)
                prefs.edit().putString(KEY_APP_THEME, com.muslim.browser.pro.ui.theme.AppTheme.BLACK_WHITE.name).commit()
                com.muslim.browser.pro.ui.theme.AppTheme.BLACK_WHITE
            }
        }
        set(value) {
            prefs.edit().putString(KEY_APP_THEME, value.name).apply()
        }

    var selectedTranslationEngine: TranslationEngine
        get() {
            val name = prefs.getString(KEY_SELECTED_TRANSLATION_ENGINE, TranslationEngine.LIBRE_TRANSLATE.name)
                ?: TranslationEngine.LIBRE_TRANSLATE.name
            return try {
                TranslationEngine.valueOf(name)
            } catch (_: Exception) {
                // Safely migrate legacy LINGVA or invalid engine preference to default without crashing
                prefs.edit().putString(KEY_SELECTED_TRANSLATION_ENGINE, TranslationEngine.LIBRE_TRANSLATE.name).commit()
                TranslationEngine.LIBRE_TRANSLATE
            }
        }
        set(value) {
            prefs.edit().putString(KEY_SELECTED_TRANSLATION_ENGINE, value.name).apply()
        }

    var selectedSearchEngine: SearchEngine
        get() {
            val name = prefs.getString(KEY_SELECTED_SEARCH_ENGINE, SearchEngine.DUCKDUCKGO.name)
                ?: SearchEngine.DUCKDUCKGO.name
            return try {
                SearchEngine.valueOf(name)
            } catch (_: Exception) {
                SearchEngine.DUCKDUCKGO
            }
        }
        set(value) {
            prefs.edit().putString(KEY_SELECTED_SEARCH_ENGINE, value.name).apply()
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
    // DOWNLOAD HISTORY PERSISTENCE
    // ==========================================

    /**
     * Returns an unmodifiable list of downloads initiated by Muslim Browser Pro.
     */
    fun getDownloadHistory(): List<DownloadEntry> {
        return cachedDownloadList
    }

    /**
     * Adds a newly initiated download to the persistent history.
     */
    fun addDownloadEntry(entry: DownloadEntry): DownloadEntry {
        inMemoryDownloads.add(0, entry)
        // Cap at 500 download entries to preserve memory
        if (inMemoryDownloads.size > 500) {
            inMemoryDownloads.removeAt(inMemoryDownloads.size - 1)
        }
        saveDownloadHistoryToDisk()
        return entry
    }

    /**
     * Updates download progress (bytes and status) in memory, with optional disk persistence.
     * Prevents excessive disk I/O during high-frequency polling while keeping UI state fresh.
     */
    fun updateDownloadProgress(
        downloadId: Long,
        status: DownloadStatus,
        downloadedBytes: Long = -1L,
        totalBytes: Long = -1L,
        localUri: String? = null,
        persistToDisk: Boolean = false
    ): Boolean {
        if (downloadId == -1L) return false
        var updated = false
        for (i in inMemoryDownloads.indices) {
            val item = inMemoryDownloads[i]
            if (item.downloadId == downloadId) {
                inMemoryDownloads[i] = item.copy(
                    status = status,
                    localUri = localUri ?: item.localUri,
                    downloadedBytes = if (downloadedBytes >= 0L) downloadedBytes else item.downloadedBytes,
                    totalBytes = if (totalBytes > 0L) totalBytes else item.totalBytes
                )
                updated = true
                break
            }
        }
        if (updated) {
            cachedDownloadList = inMemoryDownloads.toList()
            if (persistToDisk) {
                saveDownloadHistoryToDisk()
            }
        }
        return updated
    }

    /**
     * Updates status, local URI, downloaded bytes, and size for an existing download and persists to disk.
     */
    fun updateDownloadStatus(
        downloadId: Long,
        status: DownloadStatus,
        localUri: String? = null,
        downloadedBytes: Long = -1L,
        totalBytes: Long = -1L
    ): Boolean {
        return updateDownloadProgress(
            downloadId = downloadId,
            status = status,
            downloadedBytes = downloadedBytes,
            totalBytes = totalBytes,
            localUri = localUri,
            persistToDisk = true
        )
    }

    /**
     * Deletes a specific download record from history.
     */
    fun deleteDownloadEntry(id: String): Boolean {
        val removed = inMemoryDownloads.removeAll { it.id == id }
        if (removed) {
            saveDownloadHistoryToDisk()
        }
        return removed
    }

    /**
     * Clears all download history records.
     */
    fun clearAllDownloadHistory() {
        inMemoryDownloads.clear()
        cachedDownloadList = emptyList()
        prefs.edit().remove(KEY_DOWNLOAD_HISTORY).apply()
    }

    private fun saveDownloadHistoryToDisk() {
        cachedDownloadList = inMemoryDownloads.toList()
        val jsonArray = JSONArray()
        for (item in inMemoryDownloads) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("downloadId", item.downloadId)
                put("fileName", item.fileName)
                put("url", item.url)
                put("mimeType", item.mimeType)
                put("timestamp", item.timestamp)
                put("status", item.status.name)
                put("downloadedBytes", item.downloadedBytes)
                put("totalBytes", item.totalBytes)
                if (item.localUri != null) {
                    put("localUri", item.localUri)
                }
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_DOWNLOAD_HISTORY, jsonArray.toString()).apply()
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
        private const val KEY_WINDOWS_10_TOUCH = "key_windows_10_touch"
        private const val KEY_DESKTOP_ARCHITECTURE = "key_desktop_architecture"
        private const val KEY_APP_THEME = "key_app_theme"
        private const val KEY_SELECTED_TRANSLATION_ENGINE = "key_selected_translation_engine"
        private const val KEY_SELECTED_SEARCH_ENGINE = "key_selected_search_engine"
        private const val KEY_FAVORITES = "key_favorite_sites"
        private const val KEY_SAVED_TABS = "key_saved_tabs"
        private const val KEY_ACTIVE_TAB_ID = "key_active_tab_id"
        private const val KEY_HISTORY = "key_browsing_history"
        private const val KEY_DOWNLOAD_HISTORY = "key_download_history"

        val DEFAULT_PROTECTED_KEYWORDS: List<String> = listOf(
            "Aashiq Banaya",
            "hot",
            "intimate",
            "adult",
            "kiss",
            "online",
            "video",
            "anonymous",
            "anonymity",
            "18+",
            "download",
            "downloaded",
            "downloading",
            "downl"
        )

        val DEFAULT_FAVORITES = listOf(
            FavoriteSite(id = "fav_moldovalive", name = "MoldovaLive", url = "https://moldovalive.md", iconLetter = "ML", badgeColor = 0xFF00796B),
            FavoriteSite(id = "fav_moldova1", name = "Moldova1", url = "https://moldova1.md/i/en", iconLetter = "M1", badgeColor = 0xFF1565C0),
            FavoriteSite(id = "fav_aistudio", name = "Google AI Studio", url = "https://aistudio.google.com", iconLetter = "AI", badgeColor = 0xFF1A73E8),
            FavoriteSite(id = "fav_github", name = "GitHub", url = "https://github.com/", iconLetter = "GH", badgeColor = 0xFF24292E),
            FavoriteSite(id = "fav_prothomalo", name = "Prothom Alo ePaper", url = "https://epaper.prothomalo.com/Home", iconLetter = "PA", badgeColor = 0xFFD32F2F),
            FavoriteSite(id = "fav_dailystar", name = "The Daily Star Bangla", url = "https://bangla.thedailystar.net", iconLetter = "DS", badgeColor = 0xFF283593),
            FavoriteSite(id = "fav_ittefaq", name = "Ittefaq", url = "https://www.ittefaq.com.bd", iconLetter = "IT", badgeColor = 0xFFE65100),
            FavoriteSite(id = "fav_banginews", name = "BangiNews", url = "https://banginews.com", iconLetter = "BN", badgeColor = 0xFF00838F)
        )
    }
}

data class HistoryEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)
