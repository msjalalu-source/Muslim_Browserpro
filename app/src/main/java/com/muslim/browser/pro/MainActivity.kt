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
import android.webkit.SafeBrowsingResponse
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
import android.content.ContentValues
import android.provider.MediaStore
import android.util.Base64
import com.muslim.browser.pro.browser.BlobDownloadBridge
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder
import java.util.Locale
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
import androidx.compose.runtime.LaunchedEffect
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
    val blobDownloadBridge = BlobDownloadBridge()
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

                LaunchedEffect(uiState.currentTabId) {
                    tabWebViewManager.pauseAll(exceptTabId = uiState.currentTabId)
                    tabWebViewManager.resumeTab(uiState.currentTabId)
                }

                LaunchedEffect(Unit) {
                    viewModel.blockedKeywordToast.collect { kw ->
                        val msg = "Search blocked due to protected keyword: \"$kw\""
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                LaunchedEffect(uiState.customKeywords, uiState.currentTabId) {
                    if (uiState.customKeywords.isNotEmpty()) {
                        val script = ProtectionEngine.buildKeywordBlockingScript(uiState.customKeywords)
                        currentWebView.evaluateJavascript(script, null)
                    }
                }

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
                                    if (viewModel.submitQueryOrUrl(url)) {
                                        currentWebView.loadUrl(viewModel.uiState.value.currentUrl)
                                    }
                                },
                                onSearch = { query ->
                                    if (viewModel.submitQueryOrUrl(query)) {
                                        currentWebView.loadUrl(viewModel.uiState.value.currentUrl)
                                    }
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
                                    if (viewModel.submitQueryOrUrl(url)) {
                                        currentWebView.loadUrl(viewModel.uiState.value.currentUrl)
                                    }
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
                                onRemoveKeyword = { keyword -> viewModel.removeCustomKeyword(keyword) },
                                onTogglePopupBlocking = { enabled ->
                                    viewModel.togglePopupBlocking(enabled)
                                    tabWebViewManager.forEachLiveWebView { webView ->
                                        webView.settings.javaScriptCanOpenWindowsAutomatically = !enabled
                                    }
                                },
                                onToggleAdBlocking = { enabled -> viewModel.toggleAdBlocking(enabled) },
                                onClearAllData = {
                                    viewModel.closeMenu()
                                    viewModel.clearAllBrowsingData(
                                        liveWebViews = tabWebViewManager.getLiveWebViews(),
                                        onResetWebViews = {
                                            tabWebViewManager.destroyAll(saveState = false)
                                        }
                                    )
                                },
                                onClearCacheAndCookies = {
                                    viewModel.closeMenu()
                                    viewModel.clearCacheAndCookies(
                                        liveWebViews = tabWebViewManager.getLiveWebViews()
                                    )
                                },
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
                                onSelectTheme = { theme -> viewModel.setAppTheme(theme) },
                                onAddTimeLimitedDomain = { domain -> viewModel.addTimeLimitedDomain(domain) }
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
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, false)

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true
            }

            addJavascriptInterface(blobDownloadBridge, "AndroidBlobBridge")

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
                    if (targetUrl.startsWith("mbp-blocked-search://")) {
                        val kw = try {
                            Uri.parse(targetUrl).getQueryParameter("kw") ?: ""
                        } catch (_: Exception) { "" }
                        val msg = if (kw.isNotBlank()) {
                            "Search blocked due to protected keyword: \"$kw\""
                        } else {
                            "Search blocked due to protected keyword"
                        }
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                        return true
                    }
                    val decision = NavigationController.evaluate(
                        url = targetUrl,
                        customKeywords = viewModel.uiState.value.customKeywords,
                        searchEngine = viewModel.uiState.value.selectedSearchEngine,
                        isAdBlockingEnabled = viewModel.uiState.value.isAdBlockingEnabled,
                        timeLimitedDomains = viewModel.uiState.value.timeLimitedDomains,
                        domainUsageSeconds = viewModel.uiState.value.timeLimitUsageSeconds
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
                        if (viewModel.uiState.value.customKeywords.isNotEmpty()) {
                            it.evaluateJavascript(ProtectionEngine.buildKeywordBlockingScript(viewModel.uiState.value.customKeywords), null)
                        }
                    }
                    viewModel.onPageCommitVisible()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    val arch = viewModel.uiState.value.desktopArchitecture
                    view?.let {
                        DesktopCore.handlePageLifecycle(it, arch, url ?: "")
                        if (viewModel.uiState.value.customKeywords.isNotEmpty()) {
                            it.evaluateJavascript(ProtectionEngine.buildKeywordBlockingScript(viewModel.uiState.value.customKeywords), null)
                        }
                    }
                    val current = url ?: ""
                    viewModel.onPageFinished(
                        url = current,
                        title = view?.title ?: current,
                        canGoBack = view?.canGoBack() ?: false,
                        canGoForward = view?.canGoForward() ?: false
                    )
                }

                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    val newUrl = url ?: return
                    val decision = NavigationController.evaluate(
                        url = newUrl,
                        customKeywords = viewModel.uiState.value.customKeywords,
                        searchEngine = viewModel.uiState.value.selectedSearchEngine,
                        isAdBlockingEnabled = viewModel.uiState.value.isAdBlockingEnabled,
                        timeLimitedDomains = viewModel.uiState.value.timeLimitedDomains,
                        domainUsageSeconds = viewModel.uiState.value.timeLimitUsageSeconds
                    )
                    if (decision is com.muslim.browser.pro.browser.NavigationDecision.Blocked) {
                        view?.stopLoading()
                        viewModel.setBlockedUrl(newUrl, decision.reason, decision.detail)
                        return
                    }
                    if (decision is com.muslim.browser.pro.browser.NavigationDecision.Redirect && decision.url != newUrl) {
                        view?.loadUrl(decision.url)
                    }
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

                override fun onSafeBrowsingHit(
                    view: WebView?,
                    request: WebResourceRequest?,
                    threatType: Int,
                    callback: SafeBrowsingResponse?
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                        callback?.backToSafety(true)
                    }
                    val url = request?.url?.toString() ?: view?.url ?: ""
                    viewModel.setBlockedUrl(
                        targetUrl = url,
                        reason = "Security Protection",
                        detail = "Dangerous website blocked (phishing or malware)"
                    )
                    Toast.makeText(
                        this@MainActivity,
                        "Dangerous website blocked (phishing or malware)",
                        Toast.LENGTH_SHORT
                    ).show()
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
                if (url.startsWith("blob:", ignoreCase = true)) {
                    downloadBlob(this, url, contentDisposition, mimetype)
                    return@setDownloadListener
                }
                if (url.startsWith("data:", ignoreCase = true)) {
                    handleDataDownload(url, contentDisposition, mimetype)
                    return@setDownloadListener
                }
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

    fun resolveDownloadFileName(url: String, contentDisposition: String?, mimetype: String?): String {
        var fileName = DownloadPolicy.extractFileName(url, contentDisposition).ifBlank {
            URLUtil.guessFileName(url, contentDisposition, mimetype)
        }
        if (fileName.isBlank() || fileName.equals("download", ignoreCase = true) || !fileName.contains('.')) {
            val guessed = URLUtil.guessFileName(url, contentDisposition, mimetype)
            if (guessed.isNotBlank()) {
                if (fileName.isBlank() || fileName.equals("download", ignoreCase = true)) {
                    fileName = guessed
                } else if (!fileName.contains('.') && guessed.contains('.')) {
                    fileName = "$fileName.${guessed.substringAfterLast('.')}"
                }
            }
        }
        val lowerMime = mimetype?.trim()?.lowercase(Locale.ROOT) ?: ""
        val isZipMime = lowerMime == "application/zip" ||
            lowerMime == "application/x-zip-compressed" ||
            lowerMime == "application/x-zip"
        if (isZipMime && !fileName.endsWith(".zip", ignoreCase = true)) {
            fileName = "$fileName.zip"
        }
        return fileName
    }

    fun resolveMimeType(fileName: String, mimetype: String?): String {
        return mimetype?.takeIf { it.isNotBlank() }
            ?: if (fileName.endsWith(".zip", ignoreCase = true)) "application/zip"
            else if (fileName.endsWith(".pdf", ignoreCase = true)) "application/pdf"
            else "application/octet-stream"
    }

    fun getUniqueFileName(baseName: String, checkExists: (String) -> Boolean): String {
        if (!checkExists(baseName)) return baseName
        val dotIndex = baseName.lastIndexOf('.')
        val nameWithoutExt = if (dotIndex > 0) baseName.substring(0, dotIndex) else baseName
        val ext = if (dotIndex > 0) baseName.substring(dotIndex) else ""
        var counter = 1
        while (true) {
            val candidate = "$nameWithoutExt($counter)$ext"
            if (!checkExists(candidate)) return candidate
            counter++
        }
    }

    fun saveDownloadedBytes(
        rawBytes: ByteArray,
        initialFileName: String,
        mimeType: String,
        sourceUrl: String
    ) {
        try {
            var finalFileName = initialFileName
            var savedPath = ""

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                finalFileName = getUniqueFileName(initialFileName) { candidate ->
                    try {
                        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
                        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
                        val selectionArgs = arrayOf(candidate)
                        contentResolver.query(
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                            projection,
                            selection,
                            selectionArgs,
                            null
                        )?.use { it.count > 0 } ?: false
                    } catch (_: Exception) {
                        false
                    }
                }

                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, finalFileName)
                    put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IOException("MediaStore এ ফাইল তৈরি করা যায়নি")

                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(rawBytes)
                    outputStream.flush()
                } ?: throw IOException("ফাইলে লিখতে ব্যর্থ হয়েছে")

                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)

                savedPath = uri.toString()
            } else {
                val targetDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: filesDir
                if (!targetDir.exists()) targetDir.mkdirs()

                finalFileName = getUniqueFileName(initialFileName) { candidate ->
                    File(targetDir, candidate).exists()
                }

                val targetFile = File(targetDir, finalFileName)
                FileOutputStream(targetFile).use { fos ->
                    fos.write(rawBytes)
                    fos.flush()
                }
                savedPath = targetFile.absolutePath
            }

            val uniqueDownloadId = -System.currentTimeMillis()
            viewModel.recordDownload(
                DownloadEntry(
                    downloadId = uniqueDownloadId,
                    fileName = finalFileName,
                    url = sourceUrl,
                    filePath = savedPath,
                    totalBytes = rawBytes.size.toLong(),
                    downloadedBytes = rawBytes.size.toLong(),
                    status = DownloadStatus.COMPLETED
                )
            )

            runOnUiThread {
                Toast.makeText(this, "ডাউনলোড সম্পন্ন: $finalFileName", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            val reason = e.message ?: "অজানা ত্রুটি"
            runOnUiThread {
                Toast.makeText(this, "ডাউনলোড ব্যর্থ: $reason", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun downloadBlob(
        webView: WebView,
        url: String,
        contentDisposition: String?,
        mimetype: String?
    ) {
        val fileName = resolveDownloadFileName(url, contentDisposition, mimetype)
        val resolvedMime = resolveMimeType(fileName, mimetype)

        // Security: Video is blocked everywhere
        if (DownloadPolicy.isVideo(fileName, resolvedMime, contentDisposition) ||
            resolvedMime.startsWith("video/", ignoreCase = true)
        ) {
            Toast.makeText(this, "ভিডিও ডাউনলোড বন্ধ", Toast.LENGTH_SHORT).show()
            return
        }

        // Security: APK is allowed only if origin is GitHub
        if (DownloadPolicy.isApk(fileName, resolvedMime, contentDisposition) ||
            resolvedMime == "application/vnd.android.package-archive"
        ) {
            val origin = url.removePrefix("blob:").removePrefix("BLOB:")
            if (!DownloadPolicy.isAllowedGitHubApkUrl(origin)) {
                Toast.makeText(this, "APK ডাউনলোড শুধু GitHub থেকে অনুমোদিত", Toast.LENGTH_SHORT).show()
                return
            }
        }

        blobDownloadBridge.onReadyCallback = { base64Data, detectedMime ->
            try {
                val cleanBase64 = if (base64Data.contains(",")) {
                    base64Data.substringAfter(",")
                } else {
                    base64Data
                }
                val rawBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                val finalMime = detectedMime.ifBlank { resolvedMime }
                saveDownloadedBytes(rawBytes, fileName, finalMime, url)
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "ডাউনলোড ব্যর্থ: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        blobDownloadBridge.onErrorCallback = { errorMsg ->
            runOnUiThread {
                if (errorMsg == "TOO_LARGE") {
                    Toast.makeText(this, "ফাইল অনেক বড়, এই পদ্ধতিতে নামানো যাচ্ছে না", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "ডাউনলোড ব্যর্থ: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val escapedUrl = JSONObject.quote(url)
        val script = """
            (function() {
                try {
                    var blobUrl = $escapedUrl;
                    fetch(blobUrl)
                        .then(function(res) {
                            if (!res.ok) throw new Error('HTTP ' + res.status);
                            return res.blob();
                        })
                        .then(function(blob) {
                            var MAX_SIZE = 30 * 1024 * 1024;
                            if (blob.size > MAX_SIZE) {
                                if (window.AndroidBlobBridge && window.AndroidBlobBridge.onBlobError) {
                                    window.AndroidBlobBridge.onBlobError('TOO_LARGE');
                                }
                                return;
                            }
                            var reader = new FileReader();
                            reader.onloadend = function() {
                                var base64 = reader.result;
                                if (window.AndroidBlobBridge && window.AndroidBlobBridge.onBlobReady) {
                                    window.AndroidBlobBridge.onBlobReady(base64, blob.type || '');
                                }
                            };
                            reader.onerror = function(err) {
                                if (window.AndroidBlobBridge && window.AndroidBlobBridge.onBlobError) {
                                    window.AndroidBlobBridge.onBlobError('FileReader error');
                                }
                            };
                            reader.readAsDataURL(blob);
                        })
                        .catch(function(err) {
                            if (window.AndroidBlobBridge && window.AndroidBlobBridge.onBlobError) {
                                window.AndroidBlobBridge.onBlobError(err && err.message ? err.message : 'Fetch failed');
                            }
                        });
                } catch(e) {
                    if (window.AndroidBlobBridge && window.AndroidBlobBridge.onBlobError) {
                        window.AndroidBlobBridge.onBlobError(e && e.message ? e.message : 'Unknown error');
                    }
                }
            })();
        """.trimIndent()

        runOnUiThread {
            webView.evaluateJavascript(script, null)
        }
    }

    fun handleDataDownload(url: String, contentDisposition: String?, mimetype: String?) {
        try {
            val commaIndex = url.indexOf(',')
            if (commaIndex == -1) {
                Toast.makeText(this, "ডাউনলোড ব্যর্থ: অবৈধ data URI", Toast.LENGTH_SHORT).show()
                return
            }
            val meta = url.substring(5, commaIndex)
            val dataPart = url.substring(commaIndex + 1)
            val isBase64 = meta.contains(";base64", ignoreCase = true)
            val extractedMime = if (meta.contains(';')) {
                meta.substringBefore(';')
            } else {
                meta.ifBlank { mimetype ?: "application/octet-stream" }
            }

            val fileName = resolveDownloadFileName(url, contentDisposition, extractedMime)
            val resolvedMime = resolveMimeType(fileName, extractedMime)

            if (DownloadPolicy.isVideo(fileName, resolvedMime, contentDisposition) ||
                resolvedMime.startsWith("video/", ignoreCase = true)
            ) {
                Toast.makeText(this, "ভিডিও ডাউনলোড বন্ধ", Toast.LENGTH_SHORT).show()
                return
            }
            if (DownloadPolicy.isApk(fileName, resolvedMime, contentDisposition) ||
                resolvedMime == "application/vnd.android.package-archive"
            ) {
                Toast.makeText(this, "APK ডাউনলোড শুধু GitHub থেকে অনুমোদিত", Toast.LENGTH_SHORT).show()
                return
            }

            val rawBytes = if (isBase64) {
                Base64.decode(dataPart, Base64.DEFAULT)
            } else {
                URLDecoder.decode(dataPart, "UTF-8").toByteArray(Charsets.UTF_8)
            }

            saveDownloadedBytes(rawBytes, fileName, resolvedMime, url)
        } catch (e: Exception) {
            Toast.makeText(this, "ডাউনলোড ব্যর্থ: ${e.message}", Toast.LENGTH_SHORT).show()
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
            val fileName = resolveDownloadFileName(url, contentDisposition, mimetype)
            val resolvedMime = resolveMimeType(fileName, mimetype)

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

    override fun onPause() {
        super.onPause()
        viewModel.onAppForegroundStateChanged(isForeground = false)
        if (::tabWebViewManager.isInitialized) {
            tabWebViewManager.pauseAll()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onAppForegroundStateChanged(isForeground = true)
        if (::tabWebViewManager.isInitialized) {
            tabWebViewManager.resumeTab(viewModel.uiState.value.currentTabId)
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
