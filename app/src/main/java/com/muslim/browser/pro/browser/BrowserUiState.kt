package com.muslim.browser.pro.browser

import com.muslim.browser.pro.ui.theme.AppTheme

data class BrowserUiState(
    val tabs: List<BrowserTab> = emptyList(),
    val currentTabId: String = "",
    val isHomePage: Boolean = true,
    val currentUrl: String = "",
    val pageTitle: String = "",
    val searchInput: String = "",
    val isLoading: Boolean = false,
    val loadingProgress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isMenuOpen: Boolean = false,
    val isTabsDialogOpen: Boolean = false,
    val isDownloadsOpen: Boolean = false,
    val isHistoryOpen: Boolean = false,
    val browsingHistory: List<HistoryEntry> = emptyList(),
    val downloadHistory: List<DownloadEntry> = emptyList(),
    val blockedInfo: BlockedInfo = BlockedInfo(),
    val toastMessage: String? = null,
    val favoriteSites: List<FavoriteSite> = emptyList(),
    val customKeywords: Set<String> = emptySet(),
    val isAdBlockingEnabled: Boolean = true,
    val isPopupBlockingEnabled: Boolean = true,
    val desktopArchitecture: DesktopArchitecture = DesktopArchitecture.NONE,
    val isPageTranslated: Boolean = false,
    val isTranslating: Boolean = false,
    val appTheme: AppTheme = AppTheme.BLACK_WHITE,
    val selectedSearchEngine: SearchEngine = SearchEngine.DUCKDUCKGO,
    val sslWarningState: SslWarningState? = null,
    val isPageContentVisible: Boolean = true,
    val timeLimitedDomains: Set<String> = emptySet(),
    val timeLimitUsageSeconds: Map<String, Long> = emptyMap()
) {
    val isWindows10TouchEnabled: Boolean
        get() = desktopArchitecture.isWindows10Touch()
}
