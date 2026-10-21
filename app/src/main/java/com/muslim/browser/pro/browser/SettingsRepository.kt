package com.muslim.browser.pro.browser

import android.content.Context
import android.content.SharedPreferences
import com.muslim.browser.pro.ui.theme.AppTheme
import org.json.JSONArray
import org.json.JSONObject

class SettingsRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREFS_NAME = "focus_shield_prefs"
        const val KEY_APP_THEME = "key_app_theme"
        const val KEY_AD_BLOCKING = "key_ad_blocking"
        const val KEY_POPUP_BLOCKING = "key_popup_blocking"
        const val KEY_DESKTOP_ARCHITECTURE = "key_desktop_architecture"
        const val KEY_DESKTOP_MODE = "key_desktop_mode"
        const val KEY_FAVORITES = "key_favorites"
        const val KEY_HISTORY = "key_history"
        const val KEY_SAVED_TABS = "key_saved_tabs"
        const val KEY_ACTIVE_TAB_ID = "key_active_tab_id"
        const val KEY_CUSTOM_KEYWORDS = "key_custom_keywords"
        const val KEY_DOWNLOAD_HISTORY = "key_download_history"
        const val KEY_SEARCH_ENGINE = "key_search_engine"

        val DEFAULT_PROTECTED_KEYWORDS = listOf("vpn", "proxy", "bypass", "tor")

        val DEFAULT_FAVORITES = listOf(
            FavoriteSite(id = "1", name = "MoldovaLive", url = "https://moldovalive.md", iconLetter = "MD", badgeColor = 0xFFFFFFFF),
            FavoriteSite(id = "2", name = "Moldova1", url = "https://moldova1.md", iconLetter = "M1", badgeColor = 0xFFFFFFFF),
            FavoriteSite(id = "3", name = "Google AI Studio", url = "https://aistudio.google.com", iconLetter = "AI", badgeColor = 0xFFFFFFFF),
            FavoriteSite(id = "4", name = "GitHub", url = "https://github.com", iconLetter = "GH", badgeColor = 0xFFFFFFFF),
            FavoriteSite(id = "5", name = "Prothom Alo", url = "https://www.prothomalo.com", iconLetter = "PA", badgeColor = 0xFFD32F2F),
            FavoriteSite(id = "6", name = "The Daily Star", url = "https://www.thedailystar.net", iconLetter = "DS", badgeColor = 0xFFFFFFFF),
            FavoriteSite(id = "7", name = "Ittefaq", url = "https://www.ittefaq.com.bd", iconLetter = "IT", badgeColor = 0xFFFFFFFF),
            FavoriteSite(id = "8", name = "BangiNews", url = "https://banginews.com", iconLetter = "BN", badgeColor = 0xFFD32F2F)
        )
    }

    fun getSearchEngine(): SearchEngine {
        val raw = prefs.getString(KEY_SEARCH_ENGINE, null) ?: return SearchEngine.DUCKDUCKGO
        return try {
            SearchEngine.valueOf(raw)
        } catch (_: Exception) {
            SearchEngine.DUCKDUCKGO
        }
    }

    fun setSearchEngine(engine: SearchEngine) {
        prefs.edit().putString(KEY_SEARCH_ENGINE, engine.name).apply()
    }

    fun getAppTheme(): AppTheme {
        val raw = prefs.getString(KEY_APP_THEME, AppTheme.WHITE.name)
        return try {
            AppTheme.valueOf(raw ?: AppTheme.WHITE.name)
        } catch (_: Exception) {
            AppTheme.WHITE
        }
    }

    fun setAppTheme(theme: AppTheme) {
        prefs.edit().putString(KEY_APP_THEME, theme.name).apply()
    }

    fun isAdBlockingEnabled(): Boolean = prefs.getBoolean(KEY_AD_BLOCKING, true)

    fun setAdBlockingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AD_BLOCKING, enabled).apply()
    }

    fun isPopupBlockingEnabled(): Boolean = prefs.getBoolean(KEY_POPUP_BLOCKING, true)

    fun setPopupBlockingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_POPUP_BLOCKING, enabled).apply()
    }

    fun getDesktopArchitecture(): DesktopArchitecture {
        val raw = prefs.getString(KEY_DESKTOP_ARCHITECTURE, DesktopArchitecture.NONE.name)
        return try {
            DesktopArchitecture.valueOf(raw ?: DesktopArchitecture.NONE.name)
        } catch (_: Exception) {
            DesktopArchitecture.NONE
        }
    }

    fun setDesktopArchitecture(arch: DesktopArchitecture) {
        prefs.edit().putString(KEY_DESKTOP_ARCHITECTURE, arch.name).apply()
    }

    fun getCustomKeywords(): Set<String> {
        return prefs.getStringSet(KEY_CUSTOM_KEYWORDS, emptySet()) ?: emptySet()
    }

    fun addCustomKeyword(keyword: String): Boolean {
        val current = getCustomKeywords().toMutableSet()
        val trimmed = keyword.trim().lowercase()
        if (trimmed.isEmpty() || current.contains(trimmed)) return false
        current.add(trimmed)
        prefs.edit().putStringSet(KEY_CUSTOM_KEYWORDS, current).apply()
        return true
    }

    fun getFavoriteSites(): List<FavoriteSite> {
        val json = prefs.getString(KEY_FAVORITES, null) ?: return DEFAULT_FAVORITES
        return try {
            val arr = JSONArray(json)
            val list = mutableListOf<FavoriteSite>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    FavoriteSite(
                        id = obj.optString("id"),
                        name = obj.optString("name"),
                        url = obj.optString("url"),
                        iconLetter = obj.optString("iconLetter"),
                        badgeColor = obj.optLong("badgeColor", 0xFF2E7D32)
                    )
                )
            }
            if (list.isEmpty()) DEFAULT_FAVORITES else list
        } catch (_: Exception) {
            DEFAULT_FAVORITES
        }
    }

    fun addFavoriteSite(name: String, url: String): FavoriteSite {
        val current = getFavoriteSites().toMutableList()
        val site = FavoriteSite(name = name, url = url)
        current.add(site)
        saveFavorites(current)
        return site
    }

    fun removeFavoriteSite(id: String) {
        val current = getFavoriteSites().filterNot { it.id == id }
        saveFavorites(current)
    }

    private fun saveFavorites(sites: List<FavoriteSite>) {
        val arr = JSONArray()
        for (s in sites) {
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("name", s.name)
            obj.put("url", s.url)
            obj.put("iconLetter", s.iconLetter)
            obj.put("badgeColor", s.badgeColor)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_FAVORITES, arr.toString()).apply()
    }

    fun getHistory(): List<HistoryEntry> {
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            val list = mutableListOf<HistoryEntry>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    HistoryEntry(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        url = obj.optString("url"),
                        timestamp = obj.optLong("timestamp")
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addHistoryEntry(title: String, url: String): HistoryEntry {
        val current = getHistory().toMutableList()
        val entry = HistoryEntry(title = title, url = url)
        current.add(0, entry)
        if (current.size > 200) current.removeAt(current.size - 1)
        saveHistory(current)
        return entry
    }

    fun deleteHistoryEntry(id: String) {
        val current = getHistory().filterNot { it.id == id }
        saveHistory(current)
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveHistory(history: List<HistoryEntry>) {
        val arr = JSONArray()
        for (h in history) {
            val obj = JSONObject()
            obj.put("id", h.id)
            obj.put("title", h.title)
            obj.put("url", h.url)
            obj.put("timestamp", h.timestamp)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, arr.toString()).apply()
    }

    fun getDownloads(): List<DownloadEntry> {
        val json = prefs.getString(KEY_DOWNLOAD_HISTORY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            val list = mutableListOf<DownloadEntry>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    DownloadEntry(
                        downloadId = obj.optLong("downloadId"),
                        fileName = obj.optString("fileName"),
                        url = obj.optString("url"),
                        filePath = obj.optString("filePath"),
                        totalBytes = obj.optLong("totalBytes"),
                        downloadedBytes = obj.optLong("downloadedBytes"),
                        status = try {
                            DownloadStatus.valueOf(obj.optString("status", DownloadStatus.COMPLETED.name))
                        } catch (_: Exception) {
                            DownloadStatus.COMPLETED
                        },
                        timestamp = obj.optLong("timestamp")
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addDownloadEntry(entry: DownloadEntry): DownloadEntry {
        val current = getDownloads().toMutableList()
        current.removeAll { it.downloadId == entry.downloadId }
        current.add(0, entry)
        saveDownloads(current)
        return entry
    }

    fun deleteDownloadEntry(idOrName: String) {
        val current = getDownloads().filterNot {
            it.downloadId.toString() == idOrName || it.fileName == idOrName
        }
        saveDownloads(current)
    }

    fun clearAllDownloadHistory() {
        prefs.edit().remove(KEY_DOWNLOAD_HISTORY).apply()
    }

    private fun saveDownloads(downloads: List<DownloadEntry>) {
        val arr = JSONArray()
        for (d in downloads) {
            val obj = JSONObject()
            obj.put("downloadId", d.downloadId)
            obj.put("fileName", d.fileName)
            obj.put("url", d.url)
            obj.put("filePath", d.filePath)
            obj.put("totalBytes", d.totalBytes)
            obj.put("downloadedBytes", d.downloadedBytes)
            obj.put("status", d.status.name)
            obj.put("timestamp", d.timestamp)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_DOWNLOAD_HISTORY, arr.toString()).apply()
    }
}
