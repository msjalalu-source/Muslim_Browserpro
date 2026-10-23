package com.muslim.browser.pro.browser

import android.app.Application
import android.os.Bundle
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.muslim.browser.pro.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            selectedSearchEngine = repository.getSearchEngine(),
            isPageContentVisible = true
        )
    )
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    private val _blockedKeywordToast = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val blockedKeywordToast: SharedFlow<String> = _blockedKeywordToast.asSharedFlow()

    fun openMenu() { _uiState.update { it.copy(isMenuOpen = true) } }
    fun closeMenu() { _uiState.update { it.copy(isMenuOpen = false) } }

    fun openTabsDialog() { _uiState.update { it.copy(isTabsDialogOpen = true) } }
    fun closeTabsDialog() { _uiState.update { it.copy(isTabsDialogOpen = false) } }

    fun openDownloads() {
        _uiState.update {
            it.copy(
                isDownloadsOpen = true,
                isMenuOpen = false,
                downloadHistory = repository.getDownloads()
            )
        }
    }
    fun closeDownloads() { _uiState.update { it.copy(isDownloadsOpen = false) } }

    fun openHistory() {
        _uiState.update {
            it.copy(
                isHistoryOpen = true,
                isMenuOpen = false,
                browsingHistory = repository.getHistory()
            )
        }
    }
    fun closeHistory() { _uiState.update { it.copy(isHistoryOpen = false) } }


    fun submitQueryOrUrl(input: String): Boolean {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return false

        // 1. Direct check against saved custom keywords
        val blockedKw = ProtectionEngine.isBlockedByCustomKeywords(trimmed, _uiState.value.customKeywords)
        if (blockedKw != null) {
            _blockedKeywordToast.tryEmit(blockedKw)
            return false
        }

        // 2. Evaluate target URL against navigation policies and keyword protection
        val targetUrl = formatDirectUrl(trimmed)
        val decision = NavigationController.evaluate(
            url = targetUrl,
            customKeywords = _uiState.value.customKeywords,
            isAdBlockingEnabled = _uiState.value.isAdBlockingEnabled
        )
        if (decision is NavigationDecision.Blocked) {
            if (decision.reason == "Custom Keyword Protection") {
                val kw = ProtectionEngine.isBlockedByCustomKeywords(targetUrl, _uiState.value.customKeywords) ?: trimmed
                _blockedKeywordToast.tryEmit(kw)
            } else {
                setBlockedUrl(targetUrl, decision.reason, decision.detail)
            }
            return false
        }

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

    fun openNewTab(url: String = ""): BrowserTab {
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
        return newTab
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

    fun setAppTheme(theme: AppTheme) {
        repository.setAppTheme(theme)
        _uiState.update { it.copy(appTheme = theme) }
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

    /**
     * Resets open tabs and session states to a single clean Home tab,
     * ensuring no stale navigation bundles or form snapshots remain in memory.
     */
    fun resetAllTabsToCleanHome() {
        val initialTab = BrowserTab(
            id = UUID.randomUUID().toString(),
            url = "",
            title = "Home",
            canGoBack = false,
            canGoForward = false
        )
        _uiState.update { state ->
            state.copy(
                tabs = listOf(initialTab),
                currentTabId = initialTab.id,
                currentUrl = "",
                pageTitle = "Home",
                isHomePage = true,
                isLoading = false,
                loadingProgress = 0,
                canGoBack = false,
                canGoForward = false,
                isPageContentVisible = true,
                blockedInfo = BlockedInfo(),
                searchInput = "",
                browsingHistory = emptyList(),
                isTabsDialogOpen = false
            )
        }
    }

    /**
     * Clears only HTTP cache and cookies, guarding against concurrent runs.
     * Preserves browsing history, bookmarks, downloads, and app settings.
     */
    fun clearCacheAndCookies(
        liveWebViews: Collection<WebView>,
        onComplete: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            val success = BrowserDataClearingManager.clearCacheAndCookies(
                context = getApplication(),
                liveWebViews = liveWebViews
            )
            val msg = if (success) "Cache and cookies cleared" else "Failed to clear cache and cookies"
            showToast(msg)
            onComplete(success, msg)
        }
    }

    /**
     * Clears complete browsing data: cache, cookies, DOM storage, form data, SSL preferences,
     * navigation stacks, and browsing history.
     * Preserves app settings, favorite bookmarks, download records, and Autofill profile.
     */
    fun clearAllBrowsingData(
        liveWebViews: Collection<WebView>,
        onResetWebViews: () -> Unit = {},
        onComplete: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            val success = BrowserDataClearingManager.clearAllBrowsingData(
                context = getApplication(),
                liveWebViews = liveWebViews,
                onClearHistory = {
                    repository.clearHistory()
                },
                onResetTabs = {
                    onResetWebViews()
                    resetAllTabsToCleanHome()
                }
            )
            val msg = if (success) "All browsing data cleared" else "Failed to clear browsing data"
            showToast(msg)
            onComplete(success, msg)
        }
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

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun addCustomKeyword(keyword: String): Boolean {
        val added = repository.addCustomKeyword(keyword)
        if (added) {
            _uiState.update { it.copy(customKeywords = repository.getCustomKeywords()) }
        }
        return added
    }

    fun removeCustomKeyword(keyword: String): Boolean {
        val removed = repository.removeCustomKeyword(keyword)
        if (removed) {
            _uiState.update { it.copy(customKeywords = repository.getCustomKeywords()) }
        }
        return removed
    }

    fun isProtectedKeyword(keyword: String): Boolean {
        return repository.isProtectedKeyword(keyword)
    }

    fun translateCurrentPage(
        evaluateJs: ((script: String, callback: ((String?) -> Unit)?) -> Unit)? = null,
        reloadPage: () -> Unit = {},
        evalScript: ((String, (String?) -> Unit) -> Unit)? = null,
        onComplete: () -> Unit = {}
    ) {
        val execJs: (String, ((String?) -> Unit)?) -> Unit = evaluateJs ?: { script, cb ->
            evalScript?.invoke(script) { res -> cb?.invoke(res) }
        }

        closeMenu()
        val state = _uiState.value
        if (state.isHomePage || state.currentUrl.isBlank() || state.currentUrl.startsWith("about:", ignoreCase = false)) {
            showToast("Translate works on active webpages")
            return
        }
        if (state.isTranslating) {
            showToast("Translation in progress...")
            return
        }
        if (state.isPageTranslated) {
            reloadPage()
            _uiState.update { it.copy(isPageTranslated = false, isTranslating = false) }
            showToast("Original page restored")
            onComplete()
            return
        }

        _uiState.update { it.copy(isTranslating = true) }
        showToast("Translating to বাংলা...")

        execJs(BengaliTranslator.buildExtractScript()) { jsonResult ->
            if (jsonResult.isNullOrBlank() || jsonResult == "null") {
                _uiState.update { it.copy(isTranslating = false) }
                return@execJs
            }

            activeTranslationJob?.cancel()
            activeTranslationJob = viewModelScope.launch(Dispatchers.IO) {
                try {
                    var cleanJson = jsonResult.trim()
                    if (cleanJson.startsWith("\"") && cleanJson.endsWith("\"")) {
                        try {
                            cleanJson = org.json.JSONTokener(cleanJson).nextValue() as String
                        } catch (_: Exception) {
                            cleanJson = jsonResult
                        }
                    }
                    val jsonObject = org.json.JSONObject(cleanJson)
                    val textsJsonArray = jsonObject.optJSONArray("texts")
                    if (textsJsonArray == null || textsJsonArray.length() == 0) {
                        withContext(Dispatchers.Main) {
                            _uiState.update { it.copy(isTranslating = false, isPageTranslated = false) }
                            showToast("No translatable text found")
                        }
                        return@launch
                    }

                    val texts = ArrayList<String>(textsJsonArray.length())
                    for (i in 0 until textsJsonArray.length()) {
                        texts.add(textsJsonArray.getString(i))
                    }

                    val result = BengaliTranslator.translateBatch(texts) { partialList ->
                        val progressiveScript = BengaliTranslator.buildReplaceScript(partialList)
                        withContext(Dispatchers.Main) {
                            execJs(progressiveScript, null)
                        }
                    }
                    if (result.isSuccess) {
                        val translatedList = result.getOrThrow()
                        val replaceScript = BengaliTranslator.buildReplaceScript(translatedList)
                        withContext(Dispatchers.Main) {
                            execJs(replaceScript) {
                                _uiState.update { current ->
                                    current.copy(
                                        isPageTranslated = true,
                                        isTranslating = false
                                    )
                                }
                                showToast("বাংলায় অনুবাদ সম্পন্ন হয়েছে")
                                onComplete()
                            }
                        }
                    } else {
                        val err = result.exceptionOrNull()
                        android.util.Log.e("BrowserViewModel", "Translation failed: ${err?.message}", err)
                        withContext(Dispatchers.Main) {
                            _uiState.update { it.copy(isTranslating = false, isPageTranslated = false) }
                            showToast("Translation unavailable. Please try again.")
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("BrowserViewModel", "Translation exception: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(isTranslating = false, isPageTranslated = false) }
                        showToast("Translation unavailable. Please try again.")
                    }
                }
            }
        }
    }
}
