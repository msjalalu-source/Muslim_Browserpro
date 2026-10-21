package com.muslim.browser.pro.browser

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.muslim.browser.pro.ui.theme.AppTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)
    private var activeTranslationJob: Job? = null

    private val initialTab = BrowserTab(
        id = UUID.randomUUID().toString(),
        url = "",
        title = "Home",
        canGoBack = false,
        canGoForward = false
    )

    private val _uiState = MutableStateFlow(
        BrowserUiState(
            tabs = listOf(initialTab),
            currentTabId = initialTab.id,
            isHomePage = true,
            currentUrl = "",
            pageTitle = "Home",
            searchInput = "",
            isLoading = false,
            loadingProgress = 0,
            canGoBack = false,
            canGoForward = false,
            isMenuOpen = false,
            isTabsDialogOpen = false,
            isDownloadsOpen = false,
            isHistoryOpen = false,
            isDiagnosticOpen = false,
            browsingHistory = repository.getHistory(),
            downloadHistory = repository.getDownloads(),
            blockedInfo = BlockedInfo(),
            toastMessage = null,
            favoriteSites = repository.getFavoriteSites(),
            customKeywords = repository.getCustomKeywords(),
            isAdBlockingEnabled = repository.isAdBlockingEnabled(),
            isPopupBlockingEnabled = repository.isPopupBlockingEnabled(),
            desktopArchitecture = repository.getDesktopArchitecture(),
            appTheme = repository.getAppTheme(),
            selectedTranslationEngine = TranslationEngine.LIBRE_TRANSLATE,
            selectedSearchEngine = repository.getSearchEngine(),
            isPageContentVisible = true
        )
    )
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    fun openMenu() { _uiState.update { it.copy(isMenuOpen = true) } }
    fun closeMenu() { _uiState.update { it.copy(isMenuOpen = false) } }

    fun openTabsDialog() { _uiState.update { it.copy(isTabsDialogOpen = true) } }
    fun closeTabsDialog() { _uiState.update { it.copy(isTabsDialogOpen = false) } }

    fun openDownloads() { _uiState.update { it.copy(isDownloadsOpen = true) } }
    fun closeDownloads() { _uiState.update { it.copy(isDownloadsOpen = false) } }

    fun openHistory() { _uiState.update { it.copy(isHistoryOpen = true) } }
    fun closeHistory() { _uiState.update { it.copy(isHistoryOpen = false) } }

    fun openDiagnostic() { _uiState.update { it.copy(isDiagnosticOpen = true) } }
    fun closeDiagnostic() { _uiState.update { it.copy(isDiagnosticOpen = false) } }

    fun onSearchInputChange(input: String) {
        _uiState.update { it.copy(searchInput = input) }
    }

    fun submitQueryOrUrl(input: String): Boolean {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return false

        val targetUrl = formatDirectUrl(trimmed)
        loadTargetUrl(targetUrl)
        return true
    }

    fun isWebUrl(input: String): Boolean {
        val trimmed = input.trim()
        return trimmed.startsWith("http://") ||
                trimmed.startsWith("https://") ||
                (trimmed.contains(".") && !trimmed.contains(" ") && trimmed.length > 3)
    }

    fun formatDirectUrl(input: String): String {
        val trimmed = input.trim()
        return if (isWebUrl(trimmed)) {
            if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                "https://$trimmed"
            } else {
                trimmed
            }
        } else {
            _uiState.value.selectedSearchEngine.buildSearchUrl(trimmed)
        }
    }

    fun loadTargetUrl(url: String) {
        val currentId = _uiState.value.currentTabId
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == currentId) {
                    tab.copy(url = url, blockedInfo = null)
                } else tab
            }
            state.copy(
                tabs = updatedTabs,
                currentUrl = url,
                isHomePage = false,
                isLoading = true,
                loadingProgress = 10,
                isPageContentVisible = false,
                blockedInfo = BlockedInfo(),
                searchInput = url
            )
        }
    }

    fun goHome() {
        val currentId = _uiState.value.currentTabId
        _uiState.update { state ->
            val updatedTabs = state.tabs.map { tab ->
                if (tab.id == currentId) {
                    tab.copy(url = "", title = "Home", blockedInfo = null)
                } else tab
            }
            state.copy(
                tabs = updatedTabs,
                currentUrl = "",
                pageTitle = "Home",
                isHomePage = true,
                isLoading = false,
                loadingProgress = 0,
                isPageContentVisible = true,
                blockedInfo = BlockedInfo(),
                searchInput = ""
            )
        }
    }

    fun openNewTab(url: String = "") {
        val newTab = BrowserTab(
            id = UUID.randomUUID().toString(),
            url = url,
            title = if (url.isBlank()) "Home" else url,
            canGoBack = false,
            canGoForward = false
        )
        _uiState.update { state ->
            state.copy(
                tabs = state.tabs + newTab,
                currentTabId = newTab.id,
                currentUrl = url,
                pageTitle = newTab.title,
                isHomePage = url.isBlank(),
                isLoading = url.isNotBlank(),
                loadingProgress = if (url.isNotBlank()) 10 else 0,
                searchInput = url,
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
        val remaining = currentTabs.filterNot { it.id == tabId }
        val nextTab = if (tabId == _uiState.value.currentTabId) {
            remaining.last()
        } else {
            remaining.first { it.id == _uiState.value.currentTabId }
        }
        _uiState.update { state ->
            state.copy(
                tabs = remaining,
                currentTabId = nextTab.id,
                currentUrl = nextTab.url,
                pageTitle = nextTab.title,
                isHomePage = nextTab.url.isBlank(),
                canGoBack = nextTab.canGoBack,
                canGoForward = nextTab.canGoForward,
                searchInput = nextTab.url
            )
        }
    }

    fun selectTab(tabId: String) {
        val target = _uiState.value.tabs.firstOrNull { it.id == tabId } ?: return
        _uiState.update { state ->
            state.copy(
                currentTabId = target.id,
                currentUrl = target.url,
                pageTitle = target.title,
                isHomePage = target.url.isBlank(),
                canGoBack = target.canGoBack,
                canGoForward = target.canGoForward,
                searchInput = target.url,
                isTabsDialogOpen = false
            )
        }
    }

    fun saveTabState(tabId: String, bundle: Bundle) {
        _uiState.update { state ->
            state.copy(tabs = state.tabs.map { if (it.id == tabId) it.copy(bundle = bundle) else it })
        }
    }

    fun saveCurrentTabState(bundle: Bundle) {
        saveTabState(_uiState.value.currentTabId, bundle)
    }

    fun onPageStarted(url: String) {
        val currentId = _uiState.value.currentTabId
        _uiState.update { state ->
            val updatedTabs = state.tabs.map {
                if (it.id == currentId) it.copy(url = url) else it
            }
            state.copy(
                tabs = updatedTabs,
                currentUrl = url,
                isHomePage = false,
                isLoading = true,
                loadingProgress = 15,
                searchInput = url
            )
        }
    }

    fun onPageFinished(url: String, title: String, canGoBack: Boolean, canGoForward: Boolean) {
        val currentId = _uiState.value.currentTabId
        val displayTitle = if (title.isNotBlank()) title else url
        if (url.isNotBlank() && !url.startsWith("data:") && !url.startsWith("about:")) {
            repository.addHistoryEntry(displayTitle, url)
        }
        _uiState.update { state ->
            val updatedTabs = state.tabs.map {
                if (it.id == currentId) it.copy(
                    url = url,
                    title = displayTitle,
                    canGoBack = canGoBack,
                    canGoForward = canGoForward
                ) else it
            }
            state.copy(
                tabs = updatedTabs,
                currentUrl = url,
                pageTitle = displayTitle,
                isLoading = false,
                loadingProgress = 100,
                canGoBack = canGoBack,
                canGoForward = canGoForward,
                browsingHistory = repository.getHistory(),
                isPageContentVisible = true
            )
        }
    }

    fun onProgressChanged(progress: Int) {
        _uiState.update { it.copy(loadingProgress = progress, isLoading = progress < 100) }
    }

    fun onPageCommitVisible() {
        _uiState.update { it.copy(isPageContentVisible = true) }
    }

    fun setBlockedUrl(targetUrl: String, reason: String, detail: String) {
        val blocked = BlockedInfo(targetUrl = targetUrl, reason = reason, detail = detail)
        _uiState.update { state ->
            state.copy(
                blockedInfo = blocked,
                isLoading = false,
                loadingProgress = 0,
                isPageContentVisible = true
            )
        }
    }

    fun showSslWarning(url: String, host: String, details: String) {
        _uiState.update { it.copy(sslWarningState = SslWarningState(url, host, details)) }
    }

    fun dismissSslWarning() {
        _uiState.update { it.copy(sslWarningState = null) }
    }

    fun toggleAdBlocking(enabled: Boolean) {
        repository.setAdBlockingEnabled(enabled)
        _uiState.update { it.copy(isAdBlockingEnabled = enabled) }
    }

    fun togglePopupBlocking(enabled: Boolean) {
        repository.setPopupBlockingEnabled(enabled)
        _uiState.update { it.copy(isPopupBlockingEnabled = enabled) }
    }

    fun selectDesktopArchitecture(architecture: DesktopArchitecture) {
        repository.setDesktopArchitecture(architecture)
        _uiState.update { it.copy(desktopArchitecture = architecture) }
    }

    fun toggleWindows10Touch(enabled: Boolean) {
        val target = if (enabled) DesktopArchitecture.WINDOWS_10_TOUCH else DesktopArchitecture.NONE
        selectDesktopArchitecture(target)
    }

    fun toggleDesktopMode(enabled: Boolean) {
        toggleWindows10Touch(enabled)
    }

    fun toggleDesktopMode4(enabled: Boolean) {
        toggleWindows10Touch(enabled)
    }

    fun setAppTheme(theme: AppTheme) {
        repository.setAppTheme(theme)
        _uiState.update { it.copy(appTheme = theme) }
    }

    fun selectTranslationEngine(engine: TranslationEngine) {
        _uiState.update { it.copy(selectedTranslationEngine = engine) }
    }

    fun selectSearchEngine(engine: SearchEngine) {
        repository.setSearchEngine(engine)
        _uiState.update { it.copy(selectedSearchEngine = engine) }
    }

    fun addFavoriteSite(name: String, url: String) {
        repository.addFavoriteSite(name, url)
        _uiState.update { it.copy(favoriteSites = repository.getFavoriteSites()) }
    }

    fun deleteHistoryEntry(id: String) {
        repository.deleteHistoryEntry(id)
        _uiState.update { it.copy(browsingHistory = repository.getHistory()) }
    }

    fun clearAllHistory() {
        repository.clearHistory()
        _uiState.update { it.copy(browsingHistory = emptyList()) }
    }

    fun onHistoryCleared() {
        clearAllHistory()
    }

    fun deleteDownloadEntry(idOrName: String) {
        repository.deleteDownloadEntry(idOrName)
        _uiState.update { it.copy(downloadHistory = repository.getDownloads()) }
    }

    fun clearAllDownloadHistory() {
        repository.clearAllDownloadHistory()
        _uiState.update { it.copy(downloadHistory = emptyList()) }
    }

    fun recordDownload(entry: DownloadEntry) {
        repository.addDownloadEntry(entry)
        _uiState.update { it.copy(downloadHistory = repository.getDownloads()) }
    }

    fun updateDownloadProgress(
        downloadId: Long,
        status: DownloadStatus,
        downloadedBytes: Long,
        totalBytes: Long,
        localUri: String
    ) {
        val existing = repository.getDownloads().firstOrNull { it.downloadId == downloadId }
        val updated = (existing ?: DownloadEntry(downloadId = downloadId)).copy(
            status = status,
            downloadedBytes = downloadedBytes,
            totalBytes = totalBytes,
            filePath = localUri
        )
        recordDownload(updated)
    }

    fun updateDownloadStatus(
        downloadId: Long,
        status: DownloadStatus,
        localUri: String,
        downloadedBytes: Long,
        totalBytes: Long
    ) {
        updateDownloadProgress(downloadId, status, downloadedBytes, totalBytes, localUri)
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun checkAndFilterUrl(url: String): Boolean {
        return ProtectionEngine.isHarmfulUrl(url)
    }

    fun addCustomKeyword(keyword: String): Boolean {
        val added = repository.addCustomKeyword(keyword)
        if (added) {
            _uiState.update { it.copy(customKeywords = repository.getCustomKeywords()) }
        }
        return added
    }

    fun getNormalizedKeywords(): List<String> {
        val defaultKeywords = SettingsRepository.DEFAULT_PROTECTED_KEYWORDS
        val custom = repository.getCustomKeywords()
        return (defaultKeywords + custom).distinct().sorted()
    }

    fun translateCurrentPage(
        evalScript: (String, (String?) -> Unit) -> Unit,
        onComplete: () -> Unit = {}
    ) {
        if (_uiState.value.isTranslating) return
        _uiState.update { it.copy(isTranslating = true) }
        activeTranslationJob = viewModelScope.launch {
            try {
                val script = """
                    (function() {
                        const translateElement = document.createElement('script');
                        translateElement.src = 'https://translate.google.com/translate_a/element.js?cb=googleTranslateElementInit';
                        document.head.appendChild(translateElement);
                    })();
                """.trimIndent()
                evalScript(script) {
                    _uiState.update { it.copy(isTranslating = false, isPageTranslated = true) }
                    onComplete()
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isTranslating = false) }
            }
        }
    }
}
