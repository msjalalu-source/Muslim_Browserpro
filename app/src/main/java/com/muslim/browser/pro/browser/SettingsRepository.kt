package com.muslim.browser.pro.browser

import android.content.Context
import android.content.SharedPreferences
import com.muslim.browser.pro.ui.theme.AppTheme
import org.json.JSONArray
import org.json.JSONObject

class SettingsRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Volatile
    private var cachedHistory: MutableList<HistoryEntry>? = null

    @Volatile
    private var cachedDownloads: MutableList<DownloadEntry>? = null

    @Volatile
    private var cachedFavorites: List<FavoriteSite>? = null

    @Volatile
    private var cachedKeywords: MutableSet<String>? = null

    companion object {
        const val PREFS_NAME = "focus_shield_prefs"
        const val KEY_APP_THEME = "key_app_theme"
        const val KEY_AD_BLOCKING = "key_ad_blocking"
        const val KEY_POPUP_BLOCKING = "key_popup_blocking"
        const val KEY_DESKTOP_ARCHITECTURE = "key_desktop_architecture"
        const val KEY_FAVORITES = "key_favorites"
        const val KEY_HISTORY = "key_history"
        const val KEY_ACTIVE_TAB_ID = "key_active_tab_id"
        const val KEY_CUSTOM_KEYWORDS = "key_custom_keywords"
        const val KEY_DOWNLOAD_HISTORY = "key_download_history"
        const val KEY_SEARCH_ENGINE = "key_search_engine"

        val DEFAULT_PROTECTED_KEYWORDS = listOf(
            "Aashiq Banaya",
            "hot",
            "intimate",
            "adult",
            "kiss",
            "video",
            "anonymous",
            "anonymity",
            "18+",
            "download",
            "apk"
        )

        val LEGACY_PROTECTED_KEYWORDS = listOf("vpn", "proxy", "bypass", "tor")

        val ALL_PROTECTED_KEYWORDS = DEFAULT_PROTECTED_KEYWORDS + LEGACY_PROTECTED_KEYWORDS

        fun normalizeKeyword(kw: String): String {
            return kw.replace(Regex("\\s+"), " ").trim().lowercase(java.util.Locale.ROOT)
        }

        fun isProtected(keyword: String): Boolean {
            val norm = normalizeKeyword(keyword)
            if (norm.isEmpty()) return false
            return ALL_PROTECTED_KEYWORDS.any { normalizeKeyword(it) == norm }
        }

        val DEFAULT_FAVORITES = listOf(
            FavoriteSite(id = "1", name = "MoldovaLive", url = "https://moldovalive.md", iconLetter = "MD", badgeColor = 0xFFFFFFFF),
            FavoriteSite(id = "2", name = "Moldova1", url = "https://moldova1.md", iconLetter = "M1", badgeColor = 0xFFFFFFFF),
            FavoriteSite(id = "3", name = "Google AI Studio", url = "https://aistudio.google.com", iconLetter = "AI", badgeColor = 0xFFFFFFFF),
            FavoriteSite(id = "4", name = "GitHub", url = "https://github.com", iconLetter = "GH", badgeColor = 0xFFFFFFFF),
            FavoriteSite(id = "5", name = "Prothom Alo", url = "https://www.prothomalo.com", iconLetter = "PA", badgeColor = 0xFFD32F2F),
            FavoriteSite(id = "6", name = "The Daily Star", url = "https://www.thedailystar.net", iconLetter = "Star", badgeColor = 0xFFFFFFFF),
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
        val raw = prefs.getString(KEY_APP_THEME, AppTheme.BLACK_WHITE.name)
        return try {
            AppTheme.valueOf(raw ?: AppTheme.BLACK_WHITE.name)
        } catch (_: Exception) {
            AppTheme.BLACK_WHITE
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

    fun isProtectedKeyword(keyword: String): Boolean = isProtected(keyword)

    fun getCustomKeywords(): Set<String> {
        cachedKeywords?.let { return it.toSet() }
        val saved = prefs.getStringSet(KEY_CUSTOM_KEYWORDS, null)
        val result = LinkedHashSet<String>()
        if (saved == null) {
            // Fresh installation: include all 11 required built-in keywords
            result.addAll(DEFAULT_PROTECTED_KEYWORDS)
            prefs.edit().putStringSet(KEY_CUSTOM_KEYWORDS, result).apply()
        } else {
            // Existing installation: preserve all existing keywords, safely add any missing built-in keywords
            result.addAll(saved)
            var modified = false
            for (defaultKw in DEFAULT_PROTECTED_KEYWORDS) {
                val exists = result.any { normalizeKeyword(it) == normalizeKeyword(defaultKw) }
                if (!exists) {
                    result.add(defaultKw)
                    modified = true
                }
            }
            if (modified) {
                prefs.edit().putStringSet(KEY_CUSTOM_KEYWORDS, result).apply()
            }
        }
        cachedKeywords = result
        return result.toSet()
    }

    fun addCustomKeyword(keyword: String): Boolean {
        val norm = normalizeKeyword(keyword)
        if (norm.isEmpty()) return false
        val current = getCustomKeywords().toMutableSet()
        // Prevent duplicate entries using case-insensitive comparison and normalized whitespace
        if (current.any { normalizeKeyword(it) == norm }) {
            return false
        }
        val cleaned = keyword.trim().replace(Regex("\\s+"), " ")
        current.add(cleaned)
        cachedKeywords = current
        prefs.edit().putStringSet(KEY_CUSTOM_KEYWORDS, current).apply()
        return true
    }

    fun removeCustomKeyword(keyword: String): Boolean {
        val norm = normalizeKeyword(keyword)
        if (norm.isEmpty()) return false
        // Built-in protected keywords cannot be removed
        if (isProtectedKeyword(keyword)) {
            return false
        }
        val current = getCustomKeywords().toMutableSet()
        val match = current.firstOrNull { normalizeKeyword(it) == norm } ?: return false
        val removed = current.remove(match)
        if (removed) {
            cachedKeywords = current
            prefs.edit().putStringSet(KEY_CUSTOM_KEYWORDS, current).apply()
        }
        return removed
    }

    fun getFavoriteSites(): List<FavoriteSite> {
        cachedFavorites?.let { return it }
        val json = prefs.getString(KEY_FAVORITES, null)
        val loaded = if (json == null) {
            DEFAULT_FAVORITES
        } else {
            try {
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
        cachedFavorites = loaded
        return loaded
    }

    fun addFavoriteSite(name: String, url: String): FavoriteSite {
        val current = getFavoriteSites().toMutableList()
        val site = FavoriteSite(name = name, url = url)
        current.add(site)
        cachedFavorites = current
        saveFavorites(current)
        return site
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
        cachedHistory?.let { return ArrayList(it) }
        val json = prefs.getString(KEY_HISTORY, null)
        val loaded = if (json == null) {
            mutableListOf()
        } else {
            try {
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
                mutableListOf()
            }
        }
        cachedHistory = loaded
        return ArrayList(loaded)
    }

    fun addHistoryEntry(title: String, url: String): HistoryEntry {
        val current = if (cachedHistory != null) cachedHistory!! else getHistory().toMutableList()
        val entry = HistoryEntry(title = title, url = url)
        current.add(0, entry)
        if (current.size > 200) current.removeAt(current.size - 1)
        cachedHistory = current
        saveHistory(current)
        return entry
    }

    fun deleteHistoryEntry(id: String) {
        val current = if (cachedHistory != null) cachedHistory!! else getHistory().toMutableList()
        current.removeAll { it.id == id }
        cachedHistory = current
        saveHistory(current)
    }

    fun clearHistory() {
        cachedHistory = mutableListOf()
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveHistory(history: List<HistoryEntry>) {
        val sb = StringBuilder(history.size * 128 + 16)
        sb.append('[')
        for (i in history.indices) {
            val h = history[i]
            if (i > 0) sb.append(',')
            sb.append("{\"id\":").append(JSONObject.quote(h.id))
                .append(",\"title\":").append(JSONObject.quote(h.title))
                .append(",\"url\":").append(JSONObject.quote(h.url))
                .append(",\"timestamp\":").append(h.timestamp)
                .append('}')
        }
        sb.append(']')
        prefs.edit().putString(KEY_HISTORY, sb.toString()).apply()
    }

    fun getDownloads(): List<DownloadEntry> {
        cachedDownloads?.let { return ArrayList(it) }
        val json = prefs.getString(KEY_DOWNLOAD_HISTORY, null)
        val loaded = if (json == null) {
            mutableListOf()
        } else {
            try {
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
                mutableListOf()
            }
        }
        cachedDownloads = loaded
        return ArrayList(loaded)
    }

    fun addDownloadEntry(entry: DownloadEntry): DownloadEntry {
        val current = if (cachedDownloads != null) cachedDownloads!! else getDownloads().toMutableList()
        current.removeAll { it.downloadId == entry.downloadId }
        current.add(0, entry)
        cachedDownloads = current
        saveDownloads(current)
        return entry
    }

    fun deleteDownloadEntry(idOrName: String) {
        val current = if (cachedDownloads != null) cachedDownloads!! else getDownloads().toMutableList()
        current.removeAll {
            it.downloadId.toString() == idOrName || it.fileName == idOrName
        }
        cachedDownloads = current
        saveDownloads(current)
    }

    fun clearAllDownloadHistory() {
        cachedDownloads = mutableListOf()
        prefs.edit().remove(KEY_DOWNLOAD_HISTORY).apply()
    }

    private fun saveDownloads(downloads: List<DownloadEntry>) {
        val sb = StringBuilder(downloads.size * 160 + 16)
        sb.append('[')
        for (i in downloads.indices) {
            val d = downloads[i]
            if (i > 0) sb.append(',')
            sb.append("{\"downloadId\":").append(d.downloadId)
                .append(",\"fileName\":").append(JSONObject.quote(d.fileName))
                .append(",\"url\":").append(JSONObject.quote(d.url))
                .append(",\"filePath\":").append(JSONObject.quote(d.filePath))
                .append(",\"totalBytes\":").append(d.totalBytes)
                .append(",\"downloadedBytes\":").append(d.downloadedBytes)
                .append(",\"status\":").append(JSONObject.quote(d.status.name))
                .append(",\"timestamp\":").append(d.timestamp)
                .append('}')
        }
        sb.append(']')
        prefs.edit().putString(KEY_DOWNLOAD_HISTORY, sb.toString()).apply()
    }
}
