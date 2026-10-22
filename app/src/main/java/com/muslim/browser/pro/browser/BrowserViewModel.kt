package com.muslim.browser.pro.browser

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.muslim.browser.pro.ui.theme.AppTheme
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
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

        // If the page is already translated, restore original page text
        if (_uiState.value.isPageTranslated) {
            val restoreScript = """
                (function() {
                    try {
                        if (window.__mbNodes) {
                            for (var id in window.__mbNodes) {
                                var item = window.__mbNodes[id];
                                if (item && item.node && item.original !== undefined) {
                                    item.node.nodeValue = item.original;
                                }
                            }
                        }
                        window.__mbTranslated = false;
                    } catch(e) {}
                })();
            """.trimIndent()
            evalScript(restoreScript) {
                _uiState.update { it.copy(isPageTranslated = false) }
                Toast.makeText(getApplication(), "আসল পেজ পুনরুদ্ধার করা হয়েছে", Toast.LENGTH_SHORT).show()
                onComplete()
            }
            return
        }

        _uiState.update { it.copy(isTranslating = true) }

        // Collect visible text nodes, excluding script, style, code, inputs, and visible password containers
        val extractScript = """
            (function() {
                try {
                    window.__mbNodes = {};
                    var nextId = 0;

                    function hasVisiblePasswordChild(container) {
                        if (!container || !container.querySelector) return false;
                        var pass = container.querySelector('input[type=password]');
                        if (!pass) return false;
                        var s = window.getComputedStyle(pass);
                        return (pass.offsetParent !== null || s.position === 'fixed') && 
                               s.display !== 'none' && 
                               s.visibility !== 'hidden' && 
                               (pass.offsetWidth > 0 || pass.offsetHeight > 0);
                    }

                    function isExcluded(el) {
                        var curr = el;
                        while (curr && curr !== document.body && curr !== document.documentElement) {
                            var tag = (curr.tagName || '').toLowerCase();
                            if (tag === 'script' || tag === 'style' || tag === 'noscript' || tag === 'code' || tag === 'textarea' || tag === 'input') {
                                return true;
                            }
                            if (curr.getAttribute && curr.getAttribute('translate') === 'no') {
                                return true;
                            }
                            if (curr.className && typeof curr.className === 'string' && curr.className.indexOf('notranslate') !== -1) {
                                return true;
                            }
                            if ((tag === 'form' || curr.parentElement === document.body) && hasVisiblePasswordChild(curr)) {
                                return true;
                            }
                            curr = curr.parentElement;
                        }
                        return false;
                    }

                    var walker = document.createTreeWalker(
                        document.body,
                        NodeFilter.SHOW_TEXT,
                        {
                            acceptNode: function(node) {
                                if (!node.nodeValue || !node.nodeValue.trim()) return NodeFilter.FILTER_REJECT;
                                var parent = node.parentElement;
                                if (!parent) return NodeFilter.FILTER_REJECT;
                                var s = window.getComputedStyle(parent);
                                if (s.display === 'none' || s.visibility === 'hidden' || parseFloat(s.opacity) === 0) {
                                    return NodeFilter.FILTER_REJECT;
                                }
                                if (isExcluded(parent)) return NodeFilter.FILTER_REJECT;
                                return NodeFilter.FILTER_ACCEPT;
                            }
                        },
                        false
                    );

                    var items = [];
                    var node;
                    while (node = walker.nextNode()) {
                        var text = node.nodeValue.trim();
                        if (text.length > 0) {
                            var id = 'mb_t_' + (++nextId);
                            window.__mbNodes[id] = { node: node, original: node.nodeValue };
                            items.push({ id: id, text: text });
                        }
                    }
                    return JSON.stringify(items);
                } catch(e) {
                    return JSON.stringify([]);
                }
            })();
        """.trimIndent()

        evalScript(extractScript) { rawResult ->
            if (rawResult == null) {
                _uiState.update { it.copy(isTranslating = false) }
                return@evalScript
            }

            val unquoted = unescapeJsJson(rawResult)
            val items = try {
                val jsonArr = org.json.JSONArray(unquoted)
                val list = mutableListOf<TextNodeItem>()
                for (i in 0 until jsonArr.length()) {
                    val obj = jsonArr.getJSONObject(i)
                    list.add(TextNodeItem(id = obj.getString("id"), text = obj.getString("text")))
                }
                list
            } catch (_: Exception) {
                emptyList()
            }

            if (items.isEmpty()) {
                _uiState.update { it.copy(isTranslating = false) }
                return@evalScript
            }

            val serverUrl = LibreTranslateClient.getServerUrl(getApplication())
            val apiKey = LibreTranslateClient.getApiKey(getApplication())

            activeTranslationJob?.cancel()
            activeTranslationJob = viewModelScope.launch(Dispatchers.IO) {
                val batches = items.chunked(15)
                val semaphore = Semaphore(2)
                var hasFailed = false
                var hasModifiedDom = false

                val jobs = batches.map { batch ->
                    launch {
                        semaphore.withPermit {
                            if (hasFailed) return@withPermit
                            val texts = batch.map { it.text }
                            val res = LibreTranslateClient.translateBatch(serverUrl, apiKey, texts)
                            if (hasFailed) return@withPermit

                            when (res) {
                                is LibreTranslateClient.TranslationResult.Success -> {
                                    hasModifiedDom = true
                                    val updatesJson = org.json.JSONArray().apply {
                                        for (i in batch.indices) {
                                            val translated = res.translations.getOrNull(i) ?: batch[i].text
                                            put(org.json.JSONObject().apply {
                                                put("id", batch[i].id)
                                                put("text", translated)
                                            })
                                        }
                                    }.toString()

                                    val updateScript = "(function(){ try { var u = $updatesJson; if (!window.__mbNodes) return; for (var i = 0; i < u.length; i++) { var item = window.__mbNodes[u[i].id]; if (item && item.node) { item.node.nodeValue = u[i].text; } } window.__mbTranslated = true; } catch(e){} })();"

                                    withContext(Dispatchers.Main) {
                                        evalScript(updateScript) {}
                                    }
                                }
                                is LibreTranslateClient.TranslationResult.Error -> {
                                    hasFailed = true
                                    android.util.Log.w("BrowserViewModel", "LibreTranslate translation failed: ${res.messageBn}")
                                    withContext(Dispatchers.Main) {
                                        _uiState.update { it.copy(isTranslating = false) }
                                    }
                                }
                            }
                        }
                    }
                }

                jobs.joinAll()

                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isTranslating = false,
                            isPageTranslated = if (hasModifiedDom) true else it.isPageTranslated
                        )
                    }
                    if (!hasFailed && hasModifiedDom) {
                        Toast.makeText(getApplication(), "পেজটি বাংলায় অনুবাদ করা হয়েছে", Toast.LENGTH_SHORT).show()
                        onComplete()
                    }
                }
            }
        }
    }

    private data class TextNodeItem(val id: String, val text: String)

    private fun unescapeJsJson(raw: String): String {
        val trimmed = raw.trim()
        return try {
            val tokener = org.json.JSONTokener(trimmed)
            val value = tokener.nextValue()
            value.toString()
        } catch (_: Exception) {
            if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length >= 2) {
                trimmed.substring(1, trimmed.length - 1)
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\")
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\t", "\t")
            } else {
                trimmed
            }
        }
    }
}
