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
    val name: String,
    val url: String,
    val category: String,
    val iconLetter: String,
    val badgeColor: Long
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
    val loadingProgress: Int = 0
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
    val blockedInfo: BlockedInfo? = null,
    val toastMessage: String? = null,
    val selectedCategory: String = "All",
    val customKeywords: Set<String> = emptySet(),
    val isPopupBlockingEnabled: Boolean = true,
    val isAdBlockingEnabled: Boolean = true,
    val isDesktopModeEnabled: Boolean = false
)

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    private val _uiState = MutableStateFlow(
        BrowserUiState(
            customKeywords = repository.getCustomKeywords(),
            isPopupBlockingEnabled = repository.isPopupBlockingEnabled,
            isAdBlockingEnabled = repository.isAdBlockingEnabled,
            isDesktopModeEnabled = repository.isDesktopModeEnabled
        )
    )
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    val favoriteSites = listOf(
        FavoriteSite("Google", "https://www.google.com", "Tools", "G", 0xFF4285F4),
        FavoriteSite("Wikipedia", "https://www.wikipedia.org", "Study", "W", 0xFF333333),
        FavoriteSite("DuckDuckGo", "https://duckduckgo.com", "Tools", "D", 0xFFDE5833),
        FavoriteSite("GitHub", "https://www.github.com", "Tools", "GH", 0xFF24292E),
        FavoriteSite("BBC News", "https://www.bbc.com/news", "News", "B", 0xFFBB1919),
        FavoriteSite("Reddit", "https://www.reddit.com", "Social", "R", 0xFFFF4500),
        FavoriteSite("YouTube", "https://www.youtube.com", "Social", "Y", 0xFFFF0000),
        FavoriteSite("Stack Overflow", "https://stackoverflow.com", "Study", "SO", 0xFFF48024)
    )

    fun onSearchInputChange(query: String) {
        _uiState.update { it.copy(searchInput = query) }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun openMenu() {
        _uiState.update { it.copy(isMenuOpen = true) }
    }

    fun closeMenu() {
        _uiState.update { it.copy(isMenuOpen = false) }
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
                blockedInfo = null
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
                blockedInfo = tab.blockedInfo
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
                        blockedInfo = null
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
                blockedInfo = null
            )
        }
    }

    /**
     * Handles search input submission or link click.
     * Evaluates against Adult Protection and Custom Keywords before navigation.
     */
    fun submitQueryOrUrl(input: String): Boolean {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return false

        // Check query or URL with ProtectionEngine
        val checkResult = ProtectionEngine.checkUrlOrQuery(trimmed, _uiState.value.customKeywords)
        if (checkResult is ProtectionEngine.FilterResult.Blocked) {
            val info = BlockedInfo(
                reason = checkResult.reason,
                detail = checkResult.detail,
                targetUrl = trimmed
            )
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
            return false
        }

        // Convert query to URL if not a standard URL format
        val targetUrl = resolveUrl(trimmed)

        // Check the resolved target URL as well (skip duplicate evaluation if targetUrl == trimmed)
        val resolvedCheck = if (targetUrl == trimmed) {
            checkResult
        } else {
            ProtectionEngine.checkUrlOrQuery(targetUrl, _uiState.value.customKeywords)
        }
        if (resolvedCheck is ProtectionEngine.FilterResult.Blocked) {
            val info = BlockedInfo(
                reason = resolvedCheck.reason,
                detail = resolvedCheck.detail,
                targetUrl = targetUrl
            )
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
            return false
        }

        // Also check if URL is a direct download of blocked file types
        val downloadCheck = ProtectionEngine.checkDownloadType(targetUrl, null, null)
        if (downloadCheck == ProtectionEngine.DownloadStatus.BLOCKED_VIDEO ||
            downloadCheck == ProtectionEngine.DownloadStatus.BLOCKED_AUDIO ||
            downloadCheck == ProtectionEngine.DownloadStatus.BLOCKED_APK
        ) {
            showToast("This file type is blocked.")
            return false
        }

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
        return true
    }

    private fun resolveUrl(input: String): String {
        return if (input.startsWith("http://", ignoreCase = true) ||
            input.startsWith("https://", ignoreCase = true)
        ) {
            input
        } else if (input.contains(".") && !input.contains(" ") && input.length >= 4) {
            "https://$input"
        } else {
            val encodedQuery = URLEncoder.encode(input, StandardCharsets.UTF_8.name())
            "https://duckduckgo.com/?q=$encodedQuery"
        }
    }

    /**
     * Intercepts URL navigation inside WebView.
     * Returns true if blocked, false if navigation is allowed.
     */
    fun checkAndFilterUrl(url: String): Boolean {
        val check = ProtectionEngine.checkUrlOrQuery(url, _uiState.value.customKeywords)
        if (check is ProtectionEngine.FilterResult.Blocked) {
            val info = BlockedInfo(
                reason = check.reason,
                detail = check.detail,
                targetUrl = url
            )
            _uiState.update { state ->
                val updatedTabs = state.tabs.map { tab ->
                    if (tab.id == state.currentTabId) {
                        tab.copy(blockedInfo = info, isLoading = false)
                    } else tab
                }
                state.copy(
                    tabs = updatedTabs,
                    blockedInfo = info,
                    isLoading = false
                )
            }
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
