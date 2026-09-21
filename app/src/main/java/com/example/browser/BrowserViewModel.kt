package com.example.browser

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    val isLoading: Boolean = false,
    val loadingProgress: Int = 0,
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
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isMenuOpen: Boolean = false,
    val isTabsDialogOpen: Boolean = false,
    val blockedInfo: BlockedInfo? = null,
    val toastMessage: String? = null,
    val favoriteSites: List<FavoriteSite> = emptyList(),
    val customKeywords: Set<String> = emptySet(),
    val isPopupBlockingEnabled: Boolean = true,
    val isAdBlockingEnabled: Boolean = true,
    val isDesktopModeEnabled: Boolean = false
)

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    private val _uiState = MutableStateFlow(
        BrowserUiState(
            favoriteSites = repository.getFavoriteSites(),
            customKeywords = repository.getCustomKeywords(),
            isPopupBlockingEnabled = repository.isPopupBlockingEnabled,
            isAdBlockingEnabled = repository.isAdBlockingEnabled,
            isDesktopModeEnabled = repository.isDesktopModeEnabled
        )
    )
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    val favoriteSites: List<FavoriteSite>
        get() = _uiState.value.favoriteSites.ifEmpty { repository.getFavoriteSites() }

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

    fun updateFavoriteSite(id: String, name: String, url: String) {
        val trimmedName = name.trim()
        val trimmedUrl = url.trim()
        if (trimmedName.isEmpty() || trimmedUrl.isEmpty()) {
            showToast("Website name and URL cannot be empty.")
            return
        }
        val updated = repository.updateFavoriteSite(id, trimmedName, trimmedUrl)
        if (updated) {
            _uiState.update { it.copy(favoriteSites = repository.getFavoriteSites()) }
            showToast("Favorite updated.")
        }
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
                canGoBack = tab.canGoBack,
                canGoForward = tab.canGoForward,
                blockedInfo = tab.blockedInfo,
                isTabsDialogOpen = false
            )
        }
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
                canGoBack = newCurrentTab.canGoBack,
                canGoForward = newCurrentTab.canGoForward,
                blockedInfo = newCurrentTab.blockedInfo
            )
        }
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
                isTabsDialogOpen = false
            )
        }
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

        // Check if input is a search engine URL with a query parameter
        val queryFromUrl = ProtectionEngine.extractSearchEngineQuery(trimmed)
        if (queryFromUrl != null) {
            val blockedKw = ProtectionEngine.isBlockedByCustomKeywords(queryFromUrl, _uiState.value.customKeywords)
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
            val blockedKw = ProtectionEngine.isBlockedByCustomKeywords(trimmed, _uiState.value.customKeywords)
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
        val check = ProtectionEngine.checkDirectUrl(formattedUrl, _uiState.value.customKeywords)
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

    /**
     * Fast Bangla Translation:
     * If browsing a webpage, translates the entire page to Bangla via Google Translate.
     * If on home or search input, translates text or opens Google Translate Bangla.
     */
    fun translateToBangla(): String {
        val current = _uiState.value.currentUrl
        val targetUrl = if (!_uiState.value.isHomePage && current.isNotBlank() && !current.startsWith("about:")) {
            val encodedUrl = URLEncoder.encode(current, StandardCharsets.UTF_8.name())
            "https://translate.google.com/translate?sl=auto&tl=bn&u=$encodedUrl"
        } else {
            val query = _uiState.value.searchInput.trim()
            if (query.isNotEmpty() && !query.startsWith("http://") && !query.startsWith("https://")) {
                val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
                "https://translate.google.com/?sl=auto&tl=bn&text=$encodedQuery&op=translate"
            } else {
                "https://translate.google.com/?sl=auto&tl=bn&op=translate"
            }
        }
        loadTargetUrl(targetUrl)
        closeMenu()
        showToast("বাংলায় অনুবাদ করা হচ্ছে...")
        return targetUrl
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
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == state.currentTabId) {
                    tab.copy(
                        isHomePage = false,
                        url = targetUrl,
                        searchInput = targetUrl,
                        blockedInfo = null,
                        isLoading = true
                    )
                } else tab
            }
            state.copy(
                tabs = updatedTabs,
                isHomePage = false,
                currentUrl = targetUrl,
                searchInput = targetUrl,
                blockedInfo = null,
                isLoading = true
            )
        }
    }

    fun setBlockedUrl(url: String, reason: String, detail: String) {
        val info = BlockedInfo(reason = reason, detail = detail, targetUrl = url)
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == state.currentTabId) {
                    tab.copy(isHomePage = false, blockedInfo = info, isLoading = false)
                } else tab
            }
            state.copy(
                tabs = updatedTabs,
                isHomePage = false,
                blockedInfo = info,
                isLoading = false
            )
        }
    }

    /**
     * Intercepts URL navigation inside WebView.
     * Returns true if blocked, false if navigation is allowed.
     */
    fun checkAndFilterUrl(url: String): Boolean {
        val check = ProtectionEngine.checkDirectUrl(url, _uiState.value.customKeywords)
        if (check is ProtectionEngine.FilterResult.Blocked) {
            setBlockedUrl(url, check.reason, check.detail)
            return true
        }

        // Check if direct download URL
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
                isHomePage = false
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
                        isHomePage = false
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
                isHomePage = false
            )
        }
    }

    fun onProgressChanged(progress: Int) {
        _uiState.update { state ->
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

    fun onHistoryCleared() {
        _uiState.update {
            it.copy(
                canGoBack = false,
                canGoForward = false
            )
        }
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
