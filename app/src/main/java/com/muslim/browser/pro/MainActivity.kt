package com.muslim.browser.pro

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.app.DownloadManager
import android.os.Environment
import android.view.View
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.os.Message
import android.widget.Toast
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.ByteArrayInputStream
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.DesktopArchitecture
import com.muslim.browser.pro.browser.DesktopCore
import com.muslim.browser.pro.browser.DownloadEntry
import com.muslim.browser.pro.browser.DownloadPolicy
import com.muslim.browser.pro.browser.DownloadStatus
import com.muslim.browser.pro.browser.NavigationController
import com.muslim.browser.pro.browser.ProtectionEngine
import com.muslim.browser.pro.browser.SslSecurityPolicy
import com.muslim.browser.pro.browser.TabWebViewManager
import com.muslim.browser.pro.browser.ui.BottomNavBar
import com.muslim.browser.pro.browser.ui.BrowserMenuSheet
import com.muslim.browser.pro.browser.ui.BrowserWebView
import com.muslim.browser.pro.browser.ui.DownloadsDialog
import com.muslim.browser.pro.browser.ui.HistoryDialog
import com.muslim.browser.pro.browser.ui.HomePage
import com.muslim.browser.pro.browser.ui.TabsSelectionDialog
import com.muslim.browser.pro.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    val viewModel: BrowserViewModel by viewModels()
    lateinit var tabWebViewManager: TabWebViewManager

    private var lastPopupCreatedTimestamp: Long = 0L
    private val emptyAdBytes = ByteArray(0)

    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private val fileChooserLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val results: Array<Uri>? = if (result.resultCode == RESULT_OK && data != null) {
                val dataString = data.dataString
                val clipData = data.clipData
                if (clipData != null) {
                    Array(clipData.itemCount) { i -> clipData.getItemAt(i).uri }
                } else if (dataString != null) {
                    arrayOf(Uri.parse(dataString))
                } else null
            } else null
            fileChooserCallback?.onReceiveValue(results)
            fileChooserCallback = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DesktopCore.migrateSavedArchitecture(this)

        tabWebViewManager = TabWebViewManager(
            context = this,
            webViewFactory = { tabId -> createConfiguredWebView(tabId) },
            onSyncDesktopMode = { webView, isDesktop ->
                val arch = if (isDesktop) DesktopArchitecture.WINDOWS_10_TOUCH else DesktopArchitecture.NONE
                DesktopCore.applyCommonDesktopWebViewSettings(webView)
                DesktopCore.synchronizeDesktopWebView(webView, webView.url ?: "", arch, true)
            },
            onSyncArchitecture = { webView, arch ->
                DesktopCore.applyCommonDesktopWebViewSettings(webView)
                DesktopCore.synchronizeDesktopWebView(
                    webView,
                    webView.url ?: "",
                    arch,
                    true
                )
            }
        )

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            MyApplicationTheme(appTheme = uiState.appTheme) {
                val currentWebView = tabWebViewManager.getOrCreateWebView(
                    tabId = uiState.currentTabId,
                    url = uiState.currentUrl
                ).first

                BackHandler(enabled = true) {
                    when {
                        uiState.isMenuOpen -> viewModel.closeMenu()
                        uiState.isTabsDialogOpen -> viewModel.closeTabsDialog()
                        uiState.isDownloadsOpen -> viewModel.closeDownloads()
                        uiState.isHistoryOpen -> viewModel.closeHistory()
                        currentWebView.canGoBack() -> currentWebView.goBack()
                        !uiState.isHomePage -> viewModel.goHome()
                        else -> finish()
                    }
                }

                Scaffold(
                    bottomBar = {
                        BottomNavBar(
                            canGoBack = uiState.canGoBack,
                            canGoForward = uiState.canGoForward,
                            isDesktopModeEnabled = uiState.desktopArchitecture.isAnyDesktop(),
                            onGoBack = { if (currentWebView.canGoBack()) currentWebView.goBack() },
                            onGoForward = { if (currentWebView.canGoForward()) currentWebView.goForward() },
                            onNewTab = { viewModel.openNewTab() },
                            onShowTabs = { viewModel.openTabsDialog() },
                            onToggleDesktopMode = {
                                val newEnabled = !uiState.desktopArchitecture.isAnyDesktop()
                                val target = if (newEnabled) DesktopArchitecture.WINDOWS_10_TOUCH else DesktopArchitecture.NONE
                                applyDesktopArchitecture(target, currentWebView)
                            },
                            onOpenMenu = { viewModel.openMenu() }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        if (uiState.isHomePage) {
                            HomePage(
                                uiState = uiState,
                                favoriteSites = uiState.favoriteSites,
                                onNavigate = { url ->
                                    viewModel.submitQueryOrUrl(url)
                                    currentWebView.loadUrl(viewModel.formatDirectUrl(url))
                                },
                                onSearch = { query ->
                                    viewModel.submitQueryOrUrl(query)
                                    currentWebView.loadUrl(viewModel.formatDirectUrl(query))
                                },
                                onAddFavorite = { name, url ->
                                    viewModel.addFavoriteSite(name, url)
                                },
                                onSelectSearchEngine = { engine ->
                                    viewModel.selectSearchEngine(engine)
                                }
                            )
                        } else {
                            BrowserWebView(
                                uiState = uiState,
                                webView = currentWebView,
                                onUrlSubmit = { url ->
                                    viewModel.submitQueryOrUrl(url)
                                    currentWebView.loadUrl(viewModel.formatDirectUrl(url))
                                },
                                onReload = { currentWebView.reload() }
                            )
                        }

                        if (uiState.isMenuOpen) {
                            BrowserMenuSheet(
                                uiState = uiState,
                                currentWebView = currentWebView,
                                onDismiss = { viewModel.closeMenu() },
                                onOpenHistory = {
                                    viewModel.closeMenu()
                                    viewModel.openHistory()
                                },
                                onOpenDownloads = {
                                    viewModel.closeMenu()
                                    viewModel.openDownloads()
                                },
                                onAddKeyword = { keyword -> viewModel.addCustomKeyword(keyword) },
                                onTogglePopupBlocking = { enabled ->
                                    viewModel.togglePopupBlocking(enabled)
                                    tabWebViewManager.forEachLiveWebView { webView ->
                                        webView.settings.javaScriptCanOpenWindowsAutomatically = !enabled
                                    }
                                },
                                onToggleAdBlocking = { enabled -> viewModel.toggleAdBlocking(enabled) },
                                onClearAllData = {
                                    tabWebViewManager.clearAllData()
                                    viewModel.clearAllHistory()
                                },
                                onClearCacheAndCookies = { tabWebViewManager.clearAllData() },
                                onToggleDesktopMode = { enabled ->
                                    val target = if (enabled) DesktopArchitecture.WINDOWS_10_TOUCH else DesktopArchitecture.NONE
                                    applyDesktopArchitecture(target, currentWebView)
                                },
                                onToggleDesktopMode4 = { enabled ->
                                    val target = if (enabled) DesktopArchitecture.WINDOWS_10_TOUCH else DesktopArchitecture.NONE
                                    applyDesktopArchitecture(target, currentWebView)
                                },
                                onToggleWindows10Touch = { enabled ->
                                    val target = if (enabled) DesktopArchitecture.WINDOWS_10_TOUCH else DesktopArchitecture.NONE
                                    applyDesktopArchitecture(target, currentWebView)
                                },
                                onTranslateToBengali = {
                                    viewModel.translateCurrentPage(
                                        evaluateJs = { script, cb ->
                                            currentWebView.evaluateJavascript(script) { res -> cb?.invoke(res) }
                                        },
                                        reloadPage = {
                                            currentWebView.reload()
                                        }
                                    )
                                },
                                onSelectTheme = { theme -> viewModel.setAppTheme(theme) }
                            )
                        }

                        if (uiState.isTabsDialogOpen) {
                            TabsSelectionDialog(
                                uiState = uiState,
                                onSelectTab = { tabId -> viewModel.selectTab(tabId) },
                                onCloseTab = { tabId ->
                                    tabWebViewManager.destroyWebView(tabId)
                                    viewModel.closeTab(tabId)
                                },
                                onNewTab = { viewModel.openNewTab() },
                                onDismiss = { viewModel.closeTabsDialog() }
                            )
                        }

                        if (uiState.isHistoryOpen) {
                            HistoryDialog(
                                uiState = uiState,
                                onSelectUrl = { url ->
                                    viewModel.closeHistory()
                                    viewModel.submitQueryOrUrl(url)
                                    currentWebView.loadUrl(viewModel.formatDirectUrl(url))
                                },
                                onDeleteEntry = { id -> viewModel.deleteHistoryEntry(id) },
                                onClearAll = { viewModel.clearAllHistory() },
                                onDismiss = { viewModel.closeHistory() }
                            )
                        }

                        if (uiState.isDownloadsOpen) {
                            DownloadsDialog(
                                uiState = uiState,
                                onSelectUrl = { url ->
                                    viewModel.closeDownloads()
                                    viewModel.submitQueryOrUrl(url)
                                    currentWebView.loadUrl(viewModel.formatDirectUrl(url))
                                },
                                onDeleteEntry = { idOrName -> viewModel.deleteDownloadEntry(idOrName) },
                                onClearAll = { viewModel.clearAllDownloadHistory() },
                                onDismiss = { viewModel.closeDownloads() }
                            )
                        }
                    }
                }
            }
        }
    }

    fun applyDesktopArchitecture(architecture: DesktopArchitecture, currentWebView: WebView?) {
        viewModel.selectDesktopArchitecture(architecture)
        tabWebViewManager.syncAllLiveWebViewsArchitecture(architecture)
        if (!viewModel.uiState.value.isHomePage && currentWebView != null && !currentWebView.url.isNullOrBlank()) {
            currentWebView.reload()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createConfiguredWebView(tabId: String): WebView {
        return WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.setSupportMultipleWindows(true)
            settings.javaScriptCanOpenWindowsAutomatically = !viewModel.uiState.value.isPopupBlockingEnabled

            // Test to find out whether system autofill causes the window focus loss and keyboard closing
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
            }

            DesktopCore.applyCommonDesktopWebViewSettings(this)
            DesktopCore.synchronizeDesktopWebView(
                this,
                null,
                viewModel.uiState.value.desktopArchitecture,
                true
            )

            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    if (!viewModel.uiState.value.isAdBlockingEnabled) {
                        return super.shouldInterceptRequest(view, request)
                    }
                    val uri = request?.url ?: return super.shouldInterceptRequest(view, request)
                    if (ProtectionEngine.isAdOrTracker(uri)) {
                        return WebResourceResponse(
                            "text/plain",
                            "UTF-8",
                            ByteArrayInputStream(emptyAdBytes)
                        )
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val targetUrl = request?.url?.toString() ?: return false
                    val decision = NavigationController.evaluate(
                        url = targetUrl,
                        customKeywords = viewModel.uiState.value.customKeywords,
                        searchEngine = viewModel.uiState.value.selectedSearchEngine,
                        isAdBlockingEnabled = viewModel.uiState.value.isAdBlockingEnabled
                    )
                    if (decision is com.muslim.browser.pro.browser.NavigationDecision.Blocked) {
                        viewModel.setBlockedUrl(targetUrl, decision.reason, decision.detail)
                        return true
                    }
                    if (decision is com.muslim.browser.pro.browser.NavigationDecision.Redirect) {
                        view?.loadUrl(decision.url)
                        return true
                    }
                    if (decision is com.muslim.browser.pro.browser.NavigationDecision.Download) {
                        startDownload(
                            url = decision.url,
                            userAgent = view?.settings?.userAgentString,
                            contentDisposition = null,
                            mimetype = decision.mimeType,
                            contentLength = -1L
                        )
                        return true
                    }
                    if (viewModel.uiState.value.isAdBlockingEnabled && ProtectionEngine.isHarmfulUrl(targetUrl)) {
                        viewModel.setBlockedUrl(targetUrl, "Blocked Content", "Safety filter blocked this URL.")
                        return true
                    }
                    return false
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    url?.let { safeUrl: String ->
                        viewModel.onPageStarted(safeUrl)
                        DesktopCore.handlePageLifecycle(
                            this@apply,
                            viewModel.uiState.value.desktopArchitecture,
                            safeUrl
                        )
                    }
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                    view?.settings?.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                    val arch = viewModel.uiState.value.desktopArchitecture
                    view?.let {
                        DesktopCore.handlePageLifecycle(it, arch, url ?: "")
                    }
                    viewModel.onPageCommitVisible()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    val arch = viewModel.uiState.value.desktopArchitecture
                    view?.let {
                        DesktopCore.handlePageLifecycle(it, arch, url ?: "")
                    }
                    val current = url ?: ""
                    viewModel.onPageFinished(
                        url = current,
                        title = view?.title ?: current,
                        canGoBack = view?.canGoBack() ?: false,
                        canGoForward = view?.canGoForward() ?: false
                    )
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    val url = error?.url ?: view?.url ?: ""
                    val host = SslSecurityPolicy.extractHostFromUrl(url)
                    if (SslSecurityPolicy.isHostApproved(host)) {
                        handler?.proceed()
                    } else {
                        viewModel.showSslWarning(
                            url = url,
                            host = host,
                            details = error?.toString() ?: "SSL Certificate Error"
                        )
                        handler?.cancel()
                    }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    viewModel.onProgressChanged(newProgress)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                }

                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message?
                ): Boolean {
                    val isBlocking = viewModel.uiState.value.isPopupBlockingEnabled
                    val now = System.currentTimeMillis()

                    if (isBlocking && !isUserGesture) {
                        return false
                    }

                    if (isBlocking && (now - lastPopupCreatedTimestamp < 500L)) {
                        return false
                    }

                    if (resultMsg != null) {
                        lastPopupCreatedTimestamp = now
                        val newTab = viewModel.openNewTab()
                        val newWebView = tabWebViewManager.getOrCreateWebView(newTab.id, "").first
                        val transport = resultMsg.obj as? WebView.WebViewTransport
                        if (transport != null) {
                            transport.webView = newWebView
                            resultMsg.sendToTarget()
                            return true
                        }
                    }
                    return false
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    fileChooserCallback?.onReceiveValue(null)
                    fileChooserCallback = filePathCallback
                    val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                    }
                    try {
                        fileChooserLauncher.launch(intent)
                    } catch (_: Exception) {
                        fileChooserCallback = null
                        return false
                    }
                    return true
                }
            }

            setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                val decision = DownloadPolicy.evaluate(url, mimetype, contentDisposition)
                if (decision is DownloadPolicy.Result.Blocked) {
                    viewModel.setBlockedUrl(url, "Download Blocked", decision.reason)
                    return@setDownloadListener
                }
                if (ProtectionEngine.isHarmfulUrl(url)) {
                    viewModel.setBlockedUrl(url, "Blocked Content", "Safety filter blocked this URL.")
                    return@setDownloadListener
                }
                startDownload(url, userAgent, contentDisposition, mimetype, contentLength)
            }
        }
    }

    fun startDownload(
        url: String,
        userAgent: String?,
        contentDisposition: String?,
        mimetype: String?,
        contentLength: Long
    ) {
        try {
            val fileName = DownloadPolicy.extractFileName(url, contentDisposition).ifBlank {
                URLUtil.guessFileName(url, contentDisposition, mimetype)
            }
            val resolvedMime = mimetype?.takeIf { it.isNotBlank() }
                ?: if (fileName.endsWith(".zip", ignoreCase = true)) "application/zip"
                else if (fileName.endsWith(".pdf", ignoreCase = true)) "application/pdf"
                else "application/octet-stream"

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as? DownloadManager
            if (downloadManager == null) {
                Toast.makeText(this, "Download service unavailable", Toast.LENGTH_SHORT).show()
                return
            }

            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setMimeType(resolvedMime)
                val cookie = CookieManager.getInstance().getCookie(url)
                if (!cookie.isNullOrBlank()) {
                    addRequestHeader("Cookie", cookie)
                }
                if (!userAgent.isNullOrBlank()) {
                    addRequestHeader("User-Agent", userAgent)
                }
                setTitle(fileName)
                setDescription("Downloading $fileName")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            }

            val downloadId = downloadManager.enqueue(request)

            viewModel.recordDownload(
                DownloadEntry(
                    downloadId = downloadId,
                    fileName = fileName,
                    url = url,
                    totalBytes = if (contentLength > 0) contentLength else 0L,
                    status = DownloadStatus.DOWNLOADING
                )
            )

            Toast.makeText(this, "Downloading $fileName...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tabWebViewManager.isInitialized) {
            tabWebViewManager.destroyAll()
        }
    }

    companion object {
        var isDarkThemeActive: Boolean = false

        @JvmStatic
        fun applyWebViewTheme(webView: WebView, isDark: Boolean) {
            isDarkThemeActive = isDark
            if (androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.FORCE_DARK)) {
                androidx.webkit.WebSettingsCompat.setForceDark(
                    webView.settings,
                    if (isDark) androidx.webkit.WebSettingsCompat.FORCE_DARK_ON
                    else androidx.webkit.WebSettingsCompat.FORCE_DARK_OFF
                )
            }
        }
    }
}
