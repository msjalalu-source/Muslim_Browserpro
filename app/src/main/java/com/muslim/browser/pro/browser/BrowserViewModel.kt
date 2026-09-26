package com.muslim.browser.pro.browser

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class BlockedInfo(
    val reason: String,
    val detail: String,
    val targetUrl: String
)

data class FavoriteSite(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val url: String,
    val iconLetter: String = if (name.isNotBlank()) name.trim().take(2).uppercase() else "W",
    val badgeColor: Long = 0xFF4285F4
)

data class BrowserTab(
    val id: String = java.util.UUID.randomUUID().toString(),
    val url: String = "",
    val searchInput: String = "",
    val pageTitle: String = "Home",
    val isHomePage: Boolean = true,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val blockedInfo: BlockedInfo? = null,
    val isPageTranslated: Boolean = false,
    val isLoading: Boolean = false,
    val loadingProgress: Int = 0,
    val isPageContentVisible: Boolean = false,
    val bundle: android.os.Bundle? = null
)

data class BrowserUiState(
    val tabs: List<BrowserTab> = listOf(BrowserTab(id = "default_tab")),
    val currentTabId: String = "default_tab",
    val isHomePage: Boolean = true,
    val currentUrl: String = "",
    val searchInput: String = "",
    val pageTitle: String = "Home",
    val isLoading: Boolean = false,
    val loadingProgress: Int = 0,
    val isPageContentVisible: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isMenuOpen: Boolean = false,
    val isTabsDialogOpen: Boolean = false,
    val isHistoryOpen: Boolean = false,
    val browsingHistory: List<HistoryEntry> = emptyList(),
    val blockedInfo: BlockedInfo? = null,
    val toastMessage: String? = null,
    val favoriteSites: List<FavoriteSite> = emptyList(),
    val customKeywords: Set<String> = emptySet(),
    val isPopupBlockingEnabled: Boolean = true,
    val isAdBlockingEnabled: Boolean = true,
    val isDesktopModeEnabled: Boolean = false,
    val isPageTranslated: Boolean = false,
    val isTranslating: Boolean = false
)

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    private val _uiState: MutableStateFlow<BrowserUiState>

    init {
        // Restore open browser windows/tabs and active tab from persistent storage
        val savedData = repository.getSavedTabs()
        val initialTabs = savedData?.first ?: listOf(BrowserTab(id = "default_tab"))
        val initialActiveTabId = savedData?.second ?: initialTabs.first().id
        val initialActiveTab = initialTabs.find { it.id == initialActiveTabId } ?: initialTabs.first()

        _uiState = MutableStateFlow(
            BrowserUiState(
                tabs = initialTabs,
                currentTabId = initialActiveTab.id,
                isHomePage = initialActiveTab.isHomePage,
                currentUrl = initialActiveTab.url,
                searchInput = initialActiveTab.searchInput,
                pageTitle = initialActiveTab.pageTitle,
                favoriteSites = repository.getFavoriteSites(),
                customKeywords = repository.getCustomKeywords(),
                isPopupBlockingEnabled = repository.isPopupBlockingEnabled,
                isAdBlockingEnabled = repository.isAdBlockingEnabled,
                isDesktopModeEnabled = repository.isDesktopModeEnabled,
                browsingHistory = repository.getHistory()
            )
        )
    }

    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    private fun persistTabs() {
        val state = _uiState.value
        repository.saveTabs(state.tabs, state.currentTabId)
    }

    val favoriteSites: List<FavoriteSite>
        get() = _uiState.value.favoriteSites.ifEmpty { repository.getFavoriteSites() }

    fun getNormalizedKeywords(): List<String> = repository.getNormalizedKeywords()

    fun addFavoriteSite(name: String, url: String) {
        val trimmedName = name.trim()
        val trimmedUrl = url.trim()
        if (trimmedName.isEmpty() || trimmedUrl.isEmpty()) {
            showToast("Website name and URL cannot be empty.")
            return
        }
        repository.addFavoriteSite(trimmedName, trimmedUrl)
        _uiState.update { it.copy(favoriteSites = repository.getFavoriteSites()) }
        showToast("Favorite added.")
    }

    fun onSearchInputChange(query: String) {
        _uiState.update { it.copy(searchInput = query) }
    }

    fun openMenu() {
        _uiState.update { it.copy(isMenuOpen = true, isTabsDialogOpen = false) }
    }

    fun closeMenu() {
        _uiState.update { it.copy(isMenuOpen = false) }
    }

    fun openTabsDialog() {
        _uiState.update { it.copy(isTabsDialogOpen = true, isMenuOpen = false) }
    }

    fun closeTabsDialog() {
        _uiState.update { it.copy(isTabsDialogOpen = false) }
    }

    fun saveCurrentTabState(bundle: android.os.Bundle?) {
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == state.currentTabId) {
                    tab.copy(bundle = bundle)
                } else tab
            }
            state.copy(tabs = updatedTabs)
        }
    }

    fun openNewTab(url: String = "") {
        val newTab = BrowserTab(
            url = url,
            searchInput = url,
            pageTitle = if (url.isNotEmpty()) url else "Home",
            isHomePage = url.isEmpty()
        )
        _uiState.update { state ->
            val updatedTabs = state.tabs + newTab
            state.copy(
                tabs = updatedTabs,
                currentTabId = newTab.id,
                isHomePage = newTab.isHomePage,
                currentUrl = newTab.url,
                searchInput = newTab.searchInput,
                pageTitle = newTab.pageTitle,
                isLoading = false,
                loadingProgress = 0,
                canGoBack = false,
                canGoForward = false,
                blockedInfo = null,
                isTabsDialogOpen = false,
                isMenuOpen = false
            )
        }
        persistTabs()
    }

    fun selectTab(tabId: String) {
        val tab = _uiState.value.tabs.find { it.id == tabId } ?: return
        _uiState.update { state ->
            state.copy(
                currentTabId = tab.id,
                isHomePage = tab.isHomePage,
                currentUrl = tab.url,
                searchInput = tab.searchInput,
                pageTitle = tab.pageTitle,
                isLoading = tab.isLoading,
                loadingProgress = tab.loadingProgress,
                isPageContentVisible = tab.isPageContentVisible,
                canGoBack = tab.canGoBack,
                canGoForward = tab.canGoForward,
                blockedInfo = tab.blockedInfo,
                isPageTranslated = tab.isPageTranslated,
                isTabsDialogOpen = false
            )
        }
        persistTabs()
    }

    fun closeTab(tabId: String) {
        val currentTabs = _uiState.value.tabs
        if (currentTabs.size <= 1) {
            goHome()
            return
        }
        val indexToRemove = currentTabs.indexOfFirst { it.id == tabId }
        if (indexToRemove == -1) return

        val newTabs = currentTabs.filter { it.id != tabId }
        val newCurrentTab = if (_uiState.value.currentTabId == tabId) {
            val nextIndex = (indexToRemove - 1).coerceAtLeast(0)
            newTabs[nextIndex]
        } else {
            newTabs.find { it.id == _uiState.value.currentTabId } ?: newTabs.first()
        }

        _uiState.update { state ->
            state.copy(
                tabs = newTabs,
                currentTabId = newCurrentTab.id,
                isHomePage = newCurrentTab.isHomePage,
                currentUrl = newCurrentTab.url,
                searchInput = newCurrentTab.searchInput,
                pageTitle = newCurrentTab.pageTitle,
                isLoading = newCurrentTab.isLoading,
                loadingProgress = newCurrentTab.loadingProgress,
                isPageContentVisible = newCurrentTab.isPageContentVisible,
                canGoBack = newCurrentTab.canGoBack,
                canGoForward = newCurrentTab.canGoForward,
                blockedInfo = newCurrentTab.blockedInfo,
                isPageTranslated = newCurrentTab.isPageTranslated
            )
        }
        persistTabs()
    }

    fun goHome() {
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == state.currentTabId) {
                    tab.copy(
                        isHomePage = true,
                        url = "",
                        searchInput = "",
                        pageTitle = "Home",
                        isLoading = false,
                        blockedInfo = null,
                        isPageTranslated = false,
                        isPageContentVisible = false,
                        bundle = null
                    )
                } else tab
            }
            state.copy(
                tabs = updatedTabs,
                isHomePage = true,
                currentUrl = "",
                searchInput = "",
                pageTitle = "Home",
                isLoading = false,
                blockedInfo = null,
                isPageTranslated = false,
                isTranslating = false,
                isPageContentVisible = false,
                isTabsDialogOpen = false
            )
        }
        persistTabs()
    }

    /**
     * Handles search input submission or link click.
     * Query-first filtering:
     * 1. If search engine URL -> extract query -> custom keyword check -> Google SafeSearch URL
     * 2. If raw query -> custom keyword check -> Google SafeSearch URL
     * 3. If direct URL -> custom keyword + adult domain check -> allowed navigation
     */
    fun submitQueryOrUrl(input: String): Boolean {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return false

        val normalizedKws = repository.getNormalizedKeywords()
        val customKws = _uiState.value.customKeywords

        // Check if input is a search engine URL with a query parameter
        val queryFromUrl = ProtectionEngine.extractSearchEngineQuery(trimmed)
        if (queryFromUrl != null) {
            val blockedKw = ProtectionEngine.isBlockedByCustomKeywords(queryFromUrl, customKws, normalizedKws)
            if (blockedKw != null) {
                setBlockedUrl(
                    url = trimmed,
                    reason = "Custom Keyword Protection",
                    detail = "Search query blocked due to protected keyword: \"$blockedKw\""
                )
                return false
            }
            val safeUrl = ProtectionEngine.buildGoogleSafeSearchUrl(queryFromUrl)
            loadTargetUrl(safeUrl)
            return true
        }

        // Check if input is a direct URL or search query
        val isDirectUrl = isWebUrl(trimmed)
        if (!isDirectUrl) {
            // Raw search query -> SafeSearch with safe=active
            val blockedKw = ProtectionEngine.isBlockedByCustomKeywords(trimmed, customKws, normalizedKws)
            if (blockedKw != null) {
                setBlockedUrl(
                    url = trimmed,
                    reason = "Custom Keyword Protection",
                    detail = "Search query blocked due to protected keyword: \"$blockedKw\""
                )
                return false
            }
            val safeUrl = ProtectionEngine.buildGoogleSafeSearchUrl(trimmed)
            loadTargetUrl(safeUrl)
            return true
        }

        // Direct URL Navigation
        val formattedUrl = formatDirectUrl(trimmed)
        val check = ProtectionEngine.checkDirectUrl(formattedUrl, customKws, normalizedKws)
        if (check is ProtectionEngine.FilterResult.Blocked) {
            setBlockedUrl(
                url = formattedUrl,
                reason = check.reason,
                detail = check.detail
            )
            return false
        }

        // Check if direct download of blocked file types
        val downloadCheck = ProtectionEngine.checkDownloadType(formattedUrl, null, null)
        if (downloadCheck == ProtectionEngine.DownloadStatus.BLOCKED_VIDEO ||
            downloadCheck == ProtectionEngine.DownloadStatus.BLOCKED_AUDIO ||
            downloadCheck == ProtectionEngine.DownloadStatus.BLOCKED_APK
        ) {
            showToast("This file type is blocked.")
            return false
        }

        loadTargetUrl(formattedUrl)
        return true
    }



    private fun isWebUrl(input: String): Boolean {
        if (input.startsWith("http://", ignoreCase = true) || input.startsWith("https://", ignoreCase = true)) {
            return true
        }
        return input.contains(".") && !input.contains(" ") && input.length >= 4
    }

    private fun formatDirectUrl(input: String): String {
        return if (input.startsWith("http://", ignoreCase = true) || input.startsWith("https://", ignoreCase = true)) {
            input
        } else {
            "https://$input"
        }
    }

    private fun loadTargetUrl(targetUrl: String) {
        val wasOnHomePage = _uiState.value.isHomePage
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == state.currentTabId) {
                    tab.copy(
                        isHomePage = false,
                        url = targetUrl,
                        searchInput = targetUrl,
                        blockedInfo = null,
                        isPageTranslated = false,
                        isLoading = true,
                        // Reset page content visibility if coming from home page or if never rendered yet
                        isPageContentVisible = if (wasOnHomePage) false else tab.isPageContentVisible
                    )
                } else tab
            }
            state.copy(
                tabs = updatedTabs,
                isHomePage = false,
                currentUrl = targetUrl,
                searchInput = targetUrl,
                blockedInfo = null,
                isPageTranslated = false,
                isTranslating = false,
                isLoading = true,
                isPageContentVisible = if (wasOnHomePage) false else state.isPageContentVisible
            )
        }
        persistTabs()
    }

    fun setBlockedUrl(url: String, reason: String, detail: String) {
        android.util.Log.e("DIAGNOSTIC", "BLOCK_FUNCTION=BrowserViewModel.setBlockedUrl")
        android.util.Log.e("DIAGNOSTIC", "BLOCK_REASON=$reason: $detail")
        android.util.Log.e("DIAGNOSTIC", "REQUEST_URL=$url")
        android.util.Log.e("DIAGNOSTIC", "PROTECTION_RESULT=Blocked")
        val info = BlockedInfo(reason = reason, detail = detail, targetUrl = url)
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == state.currentTabId) {
                    tab.copy(isHomePage = false, blockedInfo = info, isLoading = false, isPageContentVisible = true)
                } else tab
            }
            state.copy(
                tabs = updatedTabs,
                isHomePage = false,
                blockedInfo = info,
                isLoading = false,
                isPageContentVisible = true
            )
        }
    }

    /**
     * Intercepts URL navigation inside WebView.
     * Returns true if blocked, false if navigation is allowed.
     */
    fun checkAndFilterUrl(url: String): Boolean {
        android.util.Log.d("DIAGNOSTIC", "checkAndFilterUrl: URL=$url")
        val check = ProtectionEngine.checkDirectUrl(
            url = url,
            customKeywords = _uiState.value.customKeywords,
            normalizedKeywords = repository.getNormalizedKeywords()
        )
        if (check is ProtectionEngine.FilterResult.Blocked) {
            setBlockedUrl(url, check.reason, check.detail)
            return true
        }

        // Check if direct download of blocked file types
        val downloadStatus = ProtectionEngine.checkDownloadType(url, null, null)
        if (downloadStatus == ProtectionEngine.DownloadStatus.BLOCKED_VIDEO ||
            downloadStatus == ProtectionEngine.DownloadStatus.BLOCKED_AUDIO ||
            downloadStatus == ProtectionEngine.DownloadStatus.BLOCKED_APK
        ) {
            showToast("This file type is blocked.")
            return true
        }

        return false
    }

    fun onPageStarted(url: String) {
        if (url.isBlank() || url == "about:blank") return
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == state.currentTabId) {
                    tab.copy(
                        isLoading = true,
                        url = url,
                        searchInput = url,
                        blockedInfo = null,
                        isPageTranslated = false,
                        isHomePage = false
                    )
                } else tab
            }
            state.copy(
                tabs = updatedTabs,
                isLoading = true,
                currentUrl = url,
                searchInput = url,
                blockedInfo = null,
                isPageTranslated = false,
                isTranslating = false,
                isHomePage = false
            )
        }
    }

    fun onPageCommitVisible() {
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == state.currentTabId) {
                    tab.copy(isPageContentVisible = true)
                } else tab
            }
            state.copy(
                tabs = updatedTabs,
                isPageContentVisible = true
            )
        }
    }

    fun onPageFinished(url: String, title: String?, canBack: Boolean, canForward: Boolean) {
        if (url.isBlank() || url == "about:blank") return
        val effectiveTitle = if (!title.isNullOrBlank()) title else url
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == state.currentTabId) {
                    tab.copy(
                        isLoading = false,
                        url = url,
                        searchInput = url,
                        pageTitle = effectiveTitle,
                        canGoBack = canBack,
                        canGoForward = canForward,
                        isHomePage = false,
                        isPageContentVisible = true
                    )
                } else tab
            }
            state.copy(
                tabs = updatedTabs,
                isLoading = false,
                currentUrl = url,
                searchInput = url,
                pageTitle = effectiveTitle,
                canGoBack = canBack,
                canGoForward = canForward,
                isPageContentVisible = true
            )
        }
        persistTabs()

        // Record successful navigation into history (skip about:blank and blocked sites)
        if (url.isNotBlank() && url != "about:blank" && !url.startsWith("about:") && _uiState.value.blockedInfo == null) {
            repository.addHistoryEntry(effectiveTitle, url)
            _uiState.update { it.copy(browsingHistory = repository.getHistory()) }
        }
    }

    fun onProgressChanged(progress: Int) {
        _uiState.update { state ->
            if (state.loadingProgress == progress && state.isLoading == (progress < 100)) {
                return@update state
            }
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == state.currentTabId) {
                    tab.copy(
                        loadingProgress = progress,
                        isLoading = progress < 100
                    )
                } else tab
            }
            state.copy(
                tabs = updatedTabs,
                loadingProgress = progress,
                isLoading = progress < 100
            )
        }
    }

    /**
     * Adds custom keyword.
     * Permanently protected: cannot be edited, deleted, or disabled.
     */
    fun addCustomKeyword(keyword: String): Boolean {
        val added = repository.addCustomKeyword(keyword)
        if (added) {
            _uiState.update {
                it.copy(
                    customKeywords = repository.getCustomKeywords(),
                    toastMessage = "Keyword added and permanently protected."
                )
            }
        } else {
            showToast("Keyword is empty or already in the protected list.")
        }
        return added
    }

    fun togglePopupBlocking(enabled: Boolean) {
        repository.isPopupBlockingEnabled = enabled
        _uiState.update { it.copy(isPopupBlockingEnabled = enabled) }
    }

    fun toggleAdBlocking(enabled: Boolean) {
        repository.isAdBlockingEnabled = enabled
        _uiState.update { it.copy(isAdBlockingEnabled = enabled) }
    }

    fun toggleDesktopMode(enabled: Boolean) {
        repository.isDesktopModeEnabled = enabled
        _uiState.update { it.copy(isDesktopModeEnabled = enabled) }
    }

    /**
     * Translates the current webpage DOM to Bengali using the online BengaliTranslator.
     * When already translated, triggers page reload to restore original state.
     */
    fun translateCurrentPage(
        evaluateJs: (script: String, callback: ((String?) -> Unit)?) -> Unit,
        reloadPage: () -> Unit
    ) {
        closeMenu()
        val state = _uiState.value
        if (state.isHomePage || state.currentUrl.isBlank() || state.currentUrl.startsWith("about:")) {
            showToast("Translate works on active webpages")
            return
        }

        if (state.isTranslating) {
            showToast("Translation in progress...")
            return
        }

        if (state.isPageTranslated) {
            reloadPage()
            _uiState.update { s ->
                val updatedTabs = s.tabs.map { tab ->
                    if (tab.id == s.currentTabId) tab.copy(isPageTranslated = false) else tab
                }
                s.copy(
                    tabs = updatedTabs,
                    isPageTranslated = false,
                    isTranslating = false
                )
            }
            showToast("Original page restored")
            return
        }

        _uiState.update { it.copy(isTranslating = true) }
        showToast("Translating to বাংলা...")

        val extractScript = BengaliTranslator.buildExtractScript()
        evaluateJs(extractScript) { jsonResult ->
            if (jsonResult.isNullOrBlank() || jsonResult == "null") {
                _uiState.update { it.copy(isTranslating = false) }
                showToast("Cannot extract webpage content")
                return@evaluateJs
            }

            viewModelScope.launch {
                try {
                    val cleanJson = if (jsonResult.startsWith("\"") && jsonResult.endsWith("\"")) {
                        try {
                            org.json.JSONTokener(jsonResult).nextValue() as String
                        } catch (_: Exception) {
                            jsonResult
                        }
                    } else {
                        jsonResult
                    }

                    val jsonObj = org.json.JSONObject(cleanJson)
                    val textsArray = jsonObj.optJSONArray("texts")
                    if (textsArray == null || textsArray.length() == 0) {
                        _uiState.update { it.copy(isTranslating = false) }
                        showToast("No translatable text found")
                        return@launch
                    }

                    val texts = ArrayList<String>(textsArray.length())
                    for (i in 0 until textsArray.length()) {
                        texts.add(textsArray.getString(i))
                    }

                    val translationResult = BengaliTranslator.translateBatch(texts)

                    if (translationResult.isSuccess) {
                        val translatedList = translationResult.getOrThrow()
                        val replaceScript = BengaliTranslator.buildReplaceScript(translatedList)
                        withContext(Dispatchers.Main) {
                            evaluateJs(replaceScript) {
                                _uiState.update { s ->
                                    val updatedTabs = s.tabs.map { tab ->
                                        if (tab.id == s.currentTabId) tab.copy(isPageTranslated = true) else tab
                                    }
                                    s.copy(
                                        tabs = updatedTabs,
                                        isPageTranslated = true,
                                        isTranslating = false
                                    )
                                }
                                showToast("বাংলায় অনুবাদ সম্পন্ন হয়েছে")
                            }
                        }
                    } else {
                        val error = translationResult.exceptionOrNull()
                        android.util.Log.e("BrowserViewModel", "Translation failed: ${error?.message}", error)
                        withContext(Dispatchers.Main) {
                            _uiState.update { it.copy(isTranslating = false) }
                            showToast("Translation unavailable. Please try again.")
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("BrowserViewModel", "Translation exception: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(isTranslating = false) }
                        showToast("Translation unavailable. Please try again.")
                    }
                }
            }
        }
    }

    fun onHistoryCleared() {
        _uiState.update {
            it.copy(
                canGoBack = false,
                canGoForward = false
            )
        }
    }

    // ==========================================
    // HISTORY NAVIGATION & MANAGEMENT
    // ==========================================

    fun openHistory() {
        _uiState.update {
            it.copy(
                isHistoryOpen = true,
                isMenuOpen = false,
                isTabsDialogOpen = false,
                browsingHistory = repository.getHistory()
            )
        }
    }

    fun closeHistory() {
        _uiState.update { it.copy(isHistoryOpen = false) }
    }

    fun deleteHistoryEntry(id: String) {
        repository.deleteHistoryEntry(id)
        _uiState.update { it.copy(browsingHistory = repository.getHistory()) }
    }

    fun clearAllHistory() {
        repository.clearHistory()
        _uiState.update { it.copy(browsingHistory = emptyList()) }
        showToast("Browsing history cleared.")
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
