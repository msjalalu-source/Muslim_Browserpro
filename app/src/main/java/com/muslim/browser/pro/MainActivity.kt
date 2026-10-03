package com.muslim.browser.pro

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.ViewGroup
import android.net.http.SslError
import android.webkit.CookieManager
import android.webkit.MimeTypeMap
import android.webkit.SslErrorHandler
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.DownloadEntry
import com.muslim.browser.pro.browser.DownloadPolicy
import com.muslim.browser.pro.browser.DownloadProgressPoller
import com.muslim.browser.pro.browser.DownloadStatus
import com.muslim.browser.pro.browser.FaviconManager
import com.muslim.browser.pro.browser.ProtectionEngine
import com.muslim.browser.pro.browser.SslSecurityPolicy
import com.muslim.browser.pro.browser.ui.BlockedScreen
import com.muslim.browser.pro.browser.ui.BottomNavBar
import com.muslim.browser.pro.browser.ui.BrowserMenuSheet
import com.muslim.browser.pro.browser.ui.BrowserWebView
import com.muslim.browser.pro.browser.ui.DiagnosticScreen
import com.muslim.browser.pro.browser.ui.DownloadHistoryScreen
import com.muslim.browser.pro.browser.ui.HistoryScreen
import com.muslim.browser.pro.browser.ui.HomePage
import com.muslim.browser.pro.browser.NavigationController
import com.muslim.browser.pro.browser.NavigationDecision
import com.muslim.browser.pro.browser.TabWebViewManager
import com.muslim.browser.pro.browser.WebViewConfigurator
import com.muslim.browser.pro.browser.ui.OpenWindowsDialog
import com.muslim.browser.pro.ui.theme.MyApplicationTheme
import java.io.ByteArrayInputStream
import java.util.Collections
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModels()
    private var webViewInstance: WebView? = null
    lateinit var tabWebViewManager: TabWebViewManager
        internal set
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private val pendingSslHandlers = Collections.synchronizedMap(mutableMapOf<String, MutableList<SslErrorHandler>>())

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val callback = fileChooserCallback
        fileChooserCallback = null
        if (callback == null) return@registerForActivityResult

        val uris = parseFileChooserResult(result.resultCode, result.data, contentResolver)
        callback.onReceiveValue(uris)
    }

    private var progressPollingJob: Job? = null

    private fun startProgressPolling() {
        if (progressPollingJob?.isActive == true) return

        progressPollingJob = lifecycleScope.launch {
            while (isActive) {
                val hasActive = queryActiveDownloads()
                if (!hasActive) {
                    break
                }
                delay(1000L)
            }
        }
    }

    internal suspend fun queryActiveDownloads(): Boolean {
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager ?: return false
        val currentDownloads = viewModel.uiState.value.downloadHistory
        val activeEntries = currentDownloads.filter {
            it.downloadId > 0L && (it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PAUSED)
        }

        if (activeEntries.isEmpty()) {
            return false
        }

        // Run cursor query and reading strictly off the main thread on Dispatchers.IO
        val (updates, stillActive) = DownloadProgressPoller.queryActiveDownloadsProgress(dm, activeEntries)

        // Return the resulting lightweight progress data to the existing UI state update path on Main context
        for (update in updates) {
            when (update.status) {
                DownloadStatus.DOWNLOADING, DownloadStatus.PAUSED -> {
                    viewModel.updateDownloadProgress(
                        downloadId = update.downloadId,
                        status = update.status,
                        downloadedBytes = update.downloadedBytes,
                        totalBytes = update.totalBytes,
                        localUri = update.localUri
                    )
                }
                DownloadStatus.COMPLETED, DownloadStatus.FAILED, DownloadStatus.CANCELLED -> {
                    viewModel.updateDownloadStatus(
                        downloadId = update.downloadId,
                        status = update.status,
                        localUri = update.localUri,
                        downloadedBytes = update.downloadedBytes,
                        totalBytes = update.totalBytes
                    )
                }
            }
        }

        return stillActive
    }

    private val downloadReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (downloadId != -1L) {
                    checkDownloadStatus(downloadId)
                }
            }
        }
    }

    internal fun checkDownloadStatus(downloadId: Long) {
        lifecycleScope.launch {
            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            if (dm != null) {
                val update = DownloadProgressPoller.querySingleDownloadProgress(dm, downloadId)
                if (update != null) {
                    when (update.status) {
                        DownloadStatus.COMPLETED, DownloadStatus.FAILED, DownloadStatus.CANCELLED -> {
                            viewModel.updateDownloadStatus(
                                downloadId = update.downloadId,
                                status = update.status,
                                localUri = update.localUri,
                                downloadedBytes = update.downloadedBytes,
                                totalBytes = update.totalBytes
                            )
                        }
                        DownloadStatus.PAUSED, DownloadStatus.DOWNLOADING -> {
                            viewModel.updateDownloadProgress(
                                downloadId = update.downloadId,
                                status = update.status,
                                downloadedBytes = update.downloadedBytes,
                                totalBytes = update.totalBytes,
                                localUri = update.localUri
                            )
                        }
                    }
                }
            }
            val hasActive = queryActiveDownloads()
            if (!hasActive) {
                progressPollingJob?.cancel()
                progressPollingJob = null
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val downloadFilter = android.content.IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        androidx.core.content.ContextCompat.registerReceiver(
            this,
            downloadReceiver,
            downloadFilter,
            androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
        )

        // Check if any ongoing downloads need active progress polling
        startProgressPolling()

        tabWebViewManager = TabWebViewManager(
            context = this,
            maxLiveWebViews = TabWebViewManager.MAX_LIVE_WEBVIEWS,
            webViewFactory = { id -> createConfiguredWebView(id) },
            onSaveTabBundle = { id, bundle -> viewModel.saveTabState(id, bundle) },
            onSyncTheme = { wv, isDark ->
                applyWebViewTheme(wv, isDark)
            },
            onSyncDesktopMode = { wv, isDesktop ->
                if (viewModel.uiState.value.isSimpleDesktopModeEnabled) {
                    SimpleDesktopMode.apply(wv, true)
                } else {
                    val isWin10Touch = viewModel.uiState.value.isWindows10TouchEnabled
                    WebViewConfigurator.syncDesktopMode(
                        webView = wv,
                        url = wv.url,
                        isDesktopEnabled = isDesktop,
                        updateUserAgent = true,
                        isWindows10TouchEnabled = isWin10Touch
                    )
                }
            },
            isDesktopModeProvider = {
                viewModel.uiState.value.isDesktopModeEnabled || viewModel.uiState.value.isSimpleDesktopModeEnabled
            }
        )

        val activeTab = viewModel.uiState.value.tabs.find { it.id == viewModel.uiState.value.currentTabId }
        val (initialWebView, _) = tabWebViewManager.getOrCreateWebView(
            tabId = viewModel.uiState.value.currentTabId,
            url = if (activeTab?.isHomePage == false) activeTab.url else null,
            bundle = activeTab?.bundle
        )
        webViewInstance = initialWebView

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val isDark = uiState.appTheme != com.muslim.browser.pro.ui.theme.AppTheme.WHITE

            LaunchedEffect(uiState.appTheme) {
                tabWebViewManager.syncAllLiveWebViewsTheme(isDark)
                webViewInstance?.let {
                    applyWebViewTheme(it, isDarkTheme = isDark)
                }
            }

            val view = androidx.compose.ui.platform.LocalView.current
            if (!view.isInEditMode) {
                androidx.compose.runtime.SideEffect {
                    val window = (view.context as? android.app.Activity)?.window
                    if (window != null) {
                        val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, view)
                        insetsController.isAppearanceLightStatusBars = !isDark
                        insetsController.isAppearanceLightNavigationBars = !isDark
                        window.decorView.setBackgroundColor(if (isDark) android.graphics.Color.BLACK else android.graphics.Color.parseColor("#F4F6F9"))
                    }
                }
            }

            MyApplicationTheme(appTheme = uiState.appTheme) {
                BrowserApp(
                    viewModel = viewModel,
                    tabWebViewManager = tabWebViewManager,
                    onClearAllData = { clearAllData() },
                    onClearCacheAndCookies = { clearCacheAndCookies() },
                    onToggleDesktopMode = { enabled -> setDesktopMode(enabled) },
                    onToggleSimpleDesktopMode = { enabled -> setSimpleDesktopMode(enabled) },
                    onToggleWindows10Touch = { enabled -> setWindows10Touch(enabled) },
                    onSslProceed = { host -> onSslPromptProceed(host) },
                    onSslCancel = { host -> onSslPromptCancel(host) },
                    onHandleUrlNavigation = { url ->
                        webViewInstance?.let { handleUrlNavigation(it, url) } ?: false
                    },
                    onActiveWebViewChanged = { newWv ->
                        webViewInstance = newWv
                    }
                )
            }
        }
    }

    internal fun createConfiguredWebView(tabId: String): WebView {
        val isDarkTheme = viewModel.uiState.value.appTheme != com.muslim.browser.pro.ui.theme.AppTheme.WHITE
        val isDesktop = viewModel.uiState.value.isDesktopModeEnabled
        val isWin10Touch = viewModel.uiState.value.isWindows10TouchEnabled
        val webView = WebView(this).apply {
            android.util.Log.d("DESKTOP_DEBUG", "createConfiguredWebView: tabId=$tabId, instance=${System.identityHashCode(this)}, isDesktopEnabled=$isDesktop, isWin10Touch=$isWin10Touch")
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            WebViewConfigurator.configureBaseSettings(this, isDarkTheme, isDesktop, isWin10Touch)

            webViewClient = object : WebViewClient() {
                private fun processUrlLoading(view: WebView?, url: String): Boolean {
                    android.util.Log.d("DIAGNOSTIC", "processUrlLoading: URL=$url")
                    return handleUrlNavigation(view, url)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    return processUrlLoading(view, url)
                }

                @Deprecated("Deprecated in Java", ReplaceWith("shouldOverrideUrlLoading(view, request)"))
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url == null) return false
                    return processUrlLoading(view, url)
                }

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    val uri = request?.url ?: return null
                    if (viewModel.uiState.value.isAdBlockingEnabled && ProtectionEngine.isAdRequest(uri)) {
                        val reqUrl = uri.toString()
                        val resType = request.requestHeaders?.get("Accept") ?: "subresource"
                        android.util.Log.e("DIAGNOSTIC", "INTERCEPT_BLOCK=shouldInterceptRequest")
                        android.util.Log.e("DIAGNOSTIC", "REQUEST_URL=$reqUrl")
                        android.util.Log.e("DIAGNOSTIC", "RESOURCE_TYPE=$resType")
                        android.util.Log.e("DIAGNOSTIC", "BLOCK_REASON=Ad Request Blocked")
                        return WebResourceResponse(
                            "text/plain",
                            "UTF-8",
                            ByteArrayInputStream(EMPTY_BLOCKED_BYTES)
                        )
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    val isDesktopMode = viewModel.uiState.value.isDesktopModeEnabled
                    val isWin10Touch = viewModel.uiState.value.isWindows10TouchEnabled
                    val mode = WebViewConfigurator.getActiveIdentityMode(isDesktopMode, isWin10Touch)

                    if (view != null) {
                        when (mode) {
                            WebViewConfigurator.BrowserIdentityMode.WINDOWS_10_TOUCH -> {
                                if (WebViewConfigurator.isAuthenticationUrl(url)) {
                                    android.util.Log.d("DESKTOP_DEBUG", "onPageStarted: Auth endpoint detected ($url). Temporarily using compatible mobile UA.")
                                    if (view.settings.userAgentString != null) {
                                        view.settings.userAgentString = null
                                    }
                                } else {
                                    if (view.settings.userAgentString != WebViewConfigurator.WINDOWS_10_TOUCH_USER_AGENT) {
                                        android.util.Log.d("DESKTOP_DEBUG", "onPageStarted: Windows 10 Touch UA set.")
                                        view.settings.userAgentString = WebViewConfigurator.WINDOWS_10_TOUCH_USER_AGENT
                                    }
                                    WebViewConfigurator.injectWindows10TouchProfileIfEnabled(view, true)
                                }
                            }
                            WebViewConfigurator.BrowserIdentityMode.DESKTOP_LINUX -> {
                                if (WebViewConfigurator.isAuthenticationUrl(url)) {
                                    android.util.Log.d("DESKTOP_DEBUG", "onPageStarted: Auth endpoint detected ($url). Temporarily using compatible mobile UA.")
                                    if (view.settings.userAgentString != null) {
                                        view.settings.userAgentString = null
                                    }
                                } else {
                                    if (view.settings.userAgentString != WebViewConfigurator.DESKTOP_USER_AGENT) {
                                        android.util.Log.d("DESKTOP_DEBUG", "onPageStarted: Non-auth page ($url). Restoring desktop UA.")
                                        view.settings.userAgentString = WebViewConfigurator.DESKTOP_USER_AGENT
                                    }
                                }
                            }
                            WebViewConfigurator.BrowserIdentityMode.MOBILE -> {
                                if (view.settings.userAgentString != null) {
                                    view.settings.userAgentString = null
                                }
                            }
                        }
                    }
                    android.util.Log.d(
                        "DESKTOP_DEBUG",
                        "onPageStarted: instance=${System.identityHashCode(view)}, URL=$url, UA=${view?.settings?.userAgentString}, mode=$mode"
                    )
                    url?.let { viewModel.onPageStarted(it) }
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                    view?.settings?.cacheMode = WebSettings.LOAD_DEFAULT
                    val isDesktop = viewModel.uiState.value.isDesktopModeEnabled
                    val isWin10Touch = viewModel.uiState.value.isWindows10TouchEnabled
                    if (view != null) {
                        WebViewConfigurator.applyDesktopViewport(view, isDesktop || isWin10Touch)
                    }
                    if (isWin10Touch && !WebViewConfigurator.isAuthenticationUrl(url)) {
                        WebViewConfigurator.injectWindows10TouchProfileIfEnabled(view, true)
                    }
                    viewModel.onPageCommitVisible()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    val isDesktop = viewModel.uiState.value.isDesktopModeEnabled
                    val isWin10Touch = viewModel.uiState.value.isWindows10TouchEnabled
                    if (view != null) {
                        WebViewConfigurator.applyDesktopViewport(view, isDesktop || isWin10Touch)
                    }
                    if (isWin10Touch && !WebViewConfigurator.isAuthenticationUrl(url)) {
                        WebViewConfigurator.injectWindows10TouchProfileIfEnabled(view, true)
                    }
                    android.util.Log.d(
                        "DESKTOP_DEBUG",
                        "onPageFinished: instance=${System.identityHashCode(view)}, URL=$url, UA=${view?.settings?.userAgentString}"
                    )
                    url?.let {
                        viewModel.onPageFinished(
                            url = it,
                            title = view?.title,
                            canBack = view?.canGoBack() ?: false,
                            canForward = view?.canGoForward() ?: false
                        )
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    view?.settings?.cacheMode = WebSettings.LOAD_DEFAULT
                    android.util.Log.e("DIAGNOSTIC", "onReceivedError: URL=${request?.url}, errorCode=${error?.errorCode}, description=${error?.description}, isMainFrame=${request?.isForMainFrame}")
                    if (request?.isForMainFrame == true) {
                        viewModel.onPageCommitVisible()
                    }
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    view?.settings?.cacheMode = WebSettings.LOAD_DEFAULT
                    android.util.Log.e("DIAGNOSTIC", "onReceivedHttpError: URL=${request?.url}, statusCode=${errorResponse?.statusCode}, reason=${errorResponse?.reasonPhrase}")
                    if (request?.isForMainFrame == true) {
                        viewModel.onPageCommitVisible()
                    }
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    if (handler == null || error == null) {
                        handler?.cancel()
                        return
                    }

                    val currentHost = view?.url?.let { SslSecurityPolicy.extractHostFromUrl(it) }
                    val prelimDecision = SslSecurityPolicy.preliminaryCheck(error, currentHost)

                    when (prelimDecision) {
                        is SslSecurityPolicy.Decision.Reject -> {
                            android.util.Log.e("SSL", "Preliminary reject: ${prelimDecision.reason} for URL=${error.url}")
                            handler.cancel()
                            viewModel.showToast("Connection blocked: Certificate untrusted (${prelimDecision.reason})")
                            viewModel.onPageCommitVisible()
                            return
                        }
                        is SslSecurityPolicy.Decision.ProceedSessionApproved -> {
                            android.util.Log.d("SSL", "Proceeding with session-approved host for URL=${error.url}")
                            handler.proceed()
                            return
                        }
                        else -> {
                            // Proceed to asynchronous cryptographic AIA validation
                        }
                    }

                    val host = (currentHost ?: SslSecurityPolicy.extractHostFromUrl(error.url ?: ""))?.let {
                        SslSecurityPolicy.normalizeHost(it)
                    } ?: ""

                    if (host.isBlank()) {
                        handler.cancel()
                        viewModel.onPageCommitVisible()
                        return
                    }

                    // Synchronize on pendingSslHandlers to prevent duplicate parallel AIA fetches for the same host
                    val isFirstRequest = synchronized(pendingSslHandlers) {
                        val existing = pendingSslHandlers[host]
                        if (existing != null) {
                            existing.add(handler)
                            false
                        } else {
                            pendingSslHandlers[host] = mutableListOf(handler)
                            true
                        }
                    }

                    if (!isFirstRequest) {
                        return
                    }

                    lifecycleScope.launch {
                        val decision = withContext(Dispatchers.IO) {
                            SslSecurityPolicy.validateIncompleteChain(error, host)
                        }

                        when (decision) {
                            is SslSecurityPolicy.Decision.PromptUser -> {
                                android.util.Log.w("SSL", "AIA validated for host: ${decision.host}. Showing user warning.")
                                viewModel.showSslWarning(decision.host, decision.url, decision.certDetails)
                            }
                            is SslSecurityPolicy.Decision.Reject -> {
                                android.util.Log.e("SSL", "AIA validation rejected: ${decision.reason} for host: $host")
                                val handlers = synchronized(pendingSslHandlers) {
                                    pendingSslHandlers.remove(host) ?: emptyList()
                                }
                                handlers.forEach {
                                    try { it?.cancel() } catch (_: Exception) {}
                                }
                                viewModel.showToast("Connection blocked: Certificate untrusted (${decision.reason})")
                                viewModel.onPageCommitVisible()
                            }
                            is SslSecurityPolicy.Decision.ProceedSessionApproved -> {
                                val handlers = synchronized(pendingSslHandlers) {
                                    pendingSslHandlers.remove(host) ?: emptyList()
                                }
                                handlers.forEach {
                                    try { it?.proceed() } catch (_: Exception) {}
                                }
                            }
                        }
                    }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    viewModel.onProgressChanged(newProgress)
                }

                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: android.os.Message?
                ): Boolean {
                    // Pop-up Blocking: only block unsolicited popups without user interaction
                    if (viewModel.uiState.value.isPopupBlockingEnabled && !isUserGesture) {
                        android.util.Log.d("POPUP", "Blocked unsolicited popup window (no user gesture)")
                        return false // Blocks unsolicited pop-up
                    }
                    if (resultMsg != null) {
                        val tempWebView = WebView(this@MainActivity)
                        tempWebView.settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            setSupportMultipleWindows(false)
                        }
                        CookieManager.getInstance().setAcceptThirdPartyCookies(tempWebView, true)
                        WebViewConfigurator.applyIdentityMode(
                            tempWebView,
                            viewModel.uiState.value.isDesktopModeEnabled,
                            viewModel.uiState.value.isWindows10TouchEnabled
                        )

                        var isHandled = false
                        fun forwardToParent(destUrl: String) {
                            if (isHandled || destUrl.isBlank() || destUrl == "about:blank") return
                            isHandled = true
                            view?.post {
                                try {
                                    tempWebView.stopLoading()
                                    tempWebView.destroy()
                                } catch (_: Exception) {}
                                view.loadUrl(destUrl)
                            }
                        }

                        tempWebView.webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(v: WebView?, request: WebResourceRequest?): Boolean {
                                val destUrl = request?.url?.toString() ?: return false
                                forwardToParent(destUrl)
                                return true
                            }

                            @Deprecated("Deprecated in Java")
                            override fun shouldOverrideUrlLoading(v: WebView?, destUrl: String?): Boolean {
                                if (destUrl == null) return false
                                forwardToParent(destUrl)
                                return true
                            }

                            override fun onPageStarted(v: WebView?, destUrl: String?, favicon: Bitmap?) {
                                super.onPageStarted(v, destUrl, favicon)
                                if (!destUrl.isNullOrBlank() && destUrl != "about:blank") {
                                    forwardToParent(destUrl)
                                }
                            }
                        }
                        val transport = resultMsg.obj as? WebView.WebViewTransport
                        transport?.webView = tempWebView
                        resultMsg.sendToTarget()
                        return true
                    }
                    return false
                }

                override fun onShowFileChooser(
                    view: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    // Safely clear any previous pending callback to prevent leaks / frozen chooser
                    fileChooserCallback?.onReceiveValue(null)
                    fileChooserCallback = filePathCallback

                    val isMultiple = fileChooserParams?.mode == FileChooserParams.MODE_OPEN_MULTIPLE
                    val acceptTypes = fileChooserParams?.acceptTypes
                    val chooserIntent = createFileChooserIntent(acceptTypes, isMultiple)

                    return try {
                        fileChooserLauncher.launch(chooserIntent)
                        true
                    } catch (e: ActivityNotFoundException) {
                        try {
                            val fallbackIntent = createGetContentIntent(acceptTypes, isMultiple)
                            fileChooserLauncher.launch(fallbackIntent)
                            true
                        } catch (e2: Exception) {
                            fileChooserCallback?.onReceiveValue(null)
                            fileChooserCallback = null
                            false
                        }
                    } catch (e: Exception) {
                        fileChooserCallback?.onReceiveValue(null)
                        fileChooserCallback = null
                        false
                    }
                }
            }

            setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
                startDownload(
                    url = url,
                    userAgent = userAgent,
                    contentDisposition = contentDisposition,
                    mimetype = mimetype
                )
            }
        }
        return webView
    }

    private fun onSslPromptProceed(host: String) {
        SslSecurityPolicy.approveHostForSession(host)
        viewModel.dismissSslWarning()
        val handlers = synchronized(pendingSslHandlers) {
            pendingSslHandlers.remove(host) ?: emptyList()
        }
        handlers.forEach {
            try {
                it?.proceed()
            } catch (e: Exception) {
                android.util.Log.e("SSL", "Error executing handler.proceed()", e)
            }
        }
        webViewInstance?.reload()
    }

    private fun onSslPromptCancel(host: String) {
        viewModel.dismissSslWarning()
        val handlers = synchronized(pendingSslHandlers) {
            pendingSslHandlers.remove(host) ?: emptyList()
        }
        handlers.forEach {
            try {
                it?.cancel()
            } catch (e: Exception) {
                android.util.Log.e("SSL", "Error executing handler.cancel()", e)
            }
        }
        viewModel.onPageCommitVisible()
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            viewModel.goHome()
        }
        viewModel.showToast("Connection cancelled.")
    }

    /**
     * Centralized download initiator using Android's DownloadManager as the single source of truth.
     * Enforces policy checks, validates URI schemes, supplies authentication/session cookies and referer,
     * sanitizes filenames, verifies valid enqueue IDs, and triggers real-time progress polling.
     */
    fun startDownload(
        url: String,
        userAgent: String? = null,
        contentDisposition: String? = null,
        mimetype: String? = null
    ) {
        val decision = DownloadPolicy.evaluate(url, mimetype, contentDisposition)
        if (decision is DownloadPolicy.Result.Blocked) {
            viewModel.showToast(decision.reason)
            viewModel.onPageCommitVisible()
            return
        }

        val parsedUri = try {
            Uri.parse(url.trim())
        } catch (e: Exception) {
            null
        }

        val scheme = parsedUri?.scheme?.lowercase(Locale.ROOT)
        if (scheme != "http" && scheme != "https") {
            android.util.Log.e("DownloadManager", "Unsupported URI scheme for download: $url")
            viewModel.showToast("Cannot download: unsupported link scheme.")
            viewModel.onPageCommitVisible()
            return
        }

        var fileName = DownloadPolicy.extractFileName(url, contentDisposition)
        if (fileName.isBlank()) {
            fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
        }
        fileName = fileName.replace(Regex("[/\\\\:*?\"<>|]"), "_").trim()
        if (fileName.isBlank()) {
            fileName = "download_${System.currentTimeMillis()}"
        }

        val isMp3 = mimetype?.equals("audio/mpeg", ignoreCase = true) == true ||
                mimetype?.equals("audio/mp3", ignoreCase = true) == true ||
                url.substringBefore('?').lowercase(Locale.ROOT).endsWith(".mp3") ||
                fileName.lowercase(Locale.ROOT).endsWith(".mp3")

        if (isMp3 && !fileName.lowercase(Locale.ROOT).endsWith(".mp3")) {
            fileName = if (fileName.contains('.')) {
                fileName.substringBeforeLast('.') + ".mp3"
            } else {
                "$fileName.mp3"
            }
        }

        val effectiveMime = if (isMp3) {
            "audio/mpeg"
        } else if (!mimetype.isNullOrBlank() && mimetype != "*/*") {
            mimetype
        } else {
            val ext = fileName.substringAfterLast('.', "").lowercase(Locale.ROOT)
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: "*/*"
        }

        val effectiveUserAgent = if (!userAgent.isNullOrBlank()) {
            userAgent
        } else {
            webViewInstance?.settings?.userAgentString
        }

        var downloadId: Long = -1L
        try {
            val request = DownloadManager.Request(parsedUri).apply {
                setMimeType(effectiveMime)
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
                if (!effectiveUserAgent.isNullOrBlank()) {
                    addRequestHeader("User-Agent", effectiveUserAgent)
                }
                val cookie = try {
                    CookieManager.getInstance().getCookie(url)
                } catch (_: Exception) {
                    null
                }
                if (!cookie.isNullOrBlank()) {
                    addRequestHeader("Cookie", cookie)
                }
                val currentWebUrl = webViewInstance?.url
                if (!currentWebUrl.isNullOrBlank() && !currentWebUrl.startsWith("data:") && !currentWebUrl.startsWith("about:")) {
                    addRequestHeader("Referer", currentWebUrl)
                }

                setTitle(fileName)
                setDescription("Downloading with Muslim Browser Pro...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            }

            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            if (dm != null) {
                downloadId = dm.enqueue(request)
            }
        } catch (e: Exception) {
            android.util.Log.e("DownloadManager", "Failed to enqueue download for $url", e)
            downloadId = -1L
        }

        if (downloadId <= 0L) {
            val failedEntry = DownloadEntry(
                downloadId = -1L,
                fileName = fileName,
                url = url,
                mimeType = effectiveMime,
                timestamp = System.currentTimeMillis(),
                status = DownloadStatus.FAILED
            )
            viewModel.recordDownload(failedEntry)
            viewModel.showToast("Download failed to start.")
            viewModel.onPageCommitVisible()
            return
        }

        val entry = DownloadEntry(
            downloadId = downloadId,
            fileName = fileName,
            url = url,
            mimeType = effectiveMime,
            timestamp = System.currentTimeMillis(),
            status = DownloadStatus.DOWNLOADING
        )
        viewModel.recordDownload(entry)
        viewModel.showToast("Download started.")
        viewModel.onPageCommitVisible()
        startProgressPolling()
    }

    internal fun handleUrlNavigation(view: WebView?, url: String): Boolean {
        android.util.Log.d("DIAGNOSTIC", "handleUrlNavigation: URL=$url")
        val decision = NavigationController.evaluate(
            url = url,
            customKeywords = viewModel.uiState.value.customKeywords,
            normalizedKeywords = viewModel.getNormalizedKeywords()
        )
        return when (decision) {
            is NavigationDecision.Blocked -> {
                if (decision.reason == "Download Blocked") {
                    viewModel.showToast(decision.detail)
                    viewModel.onPageCommitVisible()
                } else {
                    viewModel.setBlockedUrl(
                        url = url,
                        reason = decision.reason,
                        detail = decision.detail
                    )
                }
                true
            }
            is NavigationDecision.Redirect -> {
                view?.loadUrl(decision.url)
                true
            }
            is NavigationDecision.Download -> {
                startDownload(
                    url = decision.url,
                    userAgent = view?.settings?.userAgentString,
                    contentDisposition = null,
                    mimetype = decision.mimeType
                )
                true
            }
            is NavigationDecision.Allowed -> false
        }
    }

    private fun clearAllData() {
        try {
            try {
                tabWebViewManager.clearAllData()
            } catch (_: Exception) {}
            viewModel.clearAllHistory()
            viewModel.onHistoryCleared()

            FaviconManager.clearCache(this)

            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()

            WebStorage.getInstance().deleteAllData()

            DownloadPolicy.clearTrustedOrigin()

            viewModel.showToast("All browsing data cleared.")
        } catch (e: Exception) {
            viewModel.showToast("Browsing data cleared.")
        }
    }

    private fun clearCacheAndCookies() {
        try {
            try {
                tabWebViewManager.clearAllData()
            } catch (_: Exception) {}

            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()

            WebStorage.getInstance().deleteAllData()

            viewModel.showToast("Cache and cookies cleared.")
        } catch (e: Exception) {
            viewModel.showToast("Cache and cookies cleared.")
        }
    }

    internal fun setDesktopMode(enabled: Boolean) {
        android.util.Log.d("DESKTOP_DEBUG", "setDesktopMode toggle: enabled=$enabled")
        viewModel.toggleDesktopMode(enabled)
        val isWin10Touch = viewModel.uiState.value.isWindows10TouchEnabled
        tabWebViewManager.syncAllLiveWebViews(enabled)
        val webView = webViewInstance ?: return
        WebViewConfigurator.syncDesktopMode(
            webView = webView,
            url = webView.url,
            isDesktopEnabled = enabled,
            updateUserAgent = true,
            isWindows10TouchEnabled = isWin10Touch
        )
        val currentUrl = webView.url?.takeIf { it.isNotBlank() && it != "about:blank" }
            ?: viewModel.uiState.value.currentUrl.takeIf { it.isNotBlank() && it != "about:blank" }

        android.util.Log.d(
            "DESKTOP_DEBUG",
            "setDesktopMode applied: instance=${System.identityHashCode(webView)}, currentUrl=$currentUrl, UA=${webView.settings.userAgentString}, isDesktopModeEnabled=$enabled"
        )

        if (currentUrl == null || viewModel.uiState.value.isHomePage) {
            return
        }

        android.util.Log.d(
            "DESKTOP_DEBUG",
            "setDesktopMode triggering reload: instance=${System.identityHashCode(webView)}, UA_before_reload=${webView.settings.userAgentString}"
        )
        webView.reload()
    }

    internal fun setWindows10Touch(enabled: Boolean) {
        android.util.Log.d("DESKTOP_DEBUG", "setWindows10Touch toggle: enabled=$enabled")
        viewModel.toggleWindows10Touch(enabled)
        val isDesktop = viewModel.uiState.value.isDesktopModeEnabled
        tabWebViewManager.forEachLiveWebView {
            WebViewConfigurator.syncDesktopMode(
                webView = it,
                url = it.url,
                isDesktopEnabled = isDesktop,
                updateUserAgent = true,
                isWindows10TouchEnabled = enabled
            )
        }
        val webView = webViewInstance ?: return
        WebViewConfigurator.syncDesktopMode(
            webView = webView,
            url = webView.url,
            isDesktopEnabled = isDesktop,
            updateUserAgent = true,
            isWindows10TouchEnabled = enabled
        )
        val currentUrl = webView.url?.takeIf { it.isNotBlank() && it != "about:blank" }
            ?: viewModel.uiState.value.currentUrl.takeIf { it.isNotBlank() && it != "about:blank" }

        android.util.Log.d(
            "DESKTOP_DEBUG",
            "setWindows10Touch applied: instance=${System.identityHashCode(webView)}, currentUrl=$currentUrl, UA=${webView.settings.userAgentString}, isWindows10TouchEnabled=$enabled"
        )

        if (currentUrl == null || viewModel.uiState.value.isHomePage) {
            return
        }

        android.util.Log.d(
            "DESKTOP_DEBUG",
            "setWindows10Touch triggering reload: instance=${System.identityHashCode(webView)}, UA_before_reload=${webView.settings.userAgentString}"
        )
        webView.reload()
    }

    override fun onPause() {
        super.onPause()
        try {
            tabWebViewManager.pauseAll()
        } catch (_: Exception) {}
    }

    override fun onResume() {
        super.onResume()
        try {
            tabWebViewManager.resumeTab(viewModel.uiState.value.currentTabId)
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        SslSecurityPolicy.clearSessionApprovals()
        synchronized(pendingSslHandlers) {
            pendingSslHandlers.values.forEach { list ->
                list.forEach { try { it.cancel() } catch (_: Exception) {} }
            }
            pendingSslHandlers.clear()
        }
        progressPollingJob?.cancel()
        progressPollingJob = null
        try {
            unregisterReceiver(downloadReceiver)
        } catch (_: Exception) {}
        fileChooserCallback?.onReceiveValue(null)
        fileChooserCallback = null
        try {
            tabWebViewManager.destroyAll()
        } catch (_: Exception) {}
        webViewInstance = null
        super.onDestroy()
    }

    companion object {
        private val EMPTY_BLOCKED_BYTES = ByteArray(0)

        const val DESKTOP_USER_AGENT = WebViewConfigurator.DESKTOP_USER_AGENT
        const val WINDOWS_10_TOUCH_USER_AGENT = WebViewConfigurator.WINDOWS_10_TOUCH_USER_AGENT

        var isDarkThemeActive: Boolean
            get() = WebViewConfigurator.isDarkThemeActive
            set(value) { WebViewConfigurator.isDarkThemeActive = value }

        fun isAuthenticationUrl(url: String?): Boolean = WebViewConfigurator.isAuthenticationUrl(url)

        fun applyDesktopMode(webView: WebView, isDesktopEnabled: Boolean) {
            WebViewConfigurator.applyDesktopMode(webView, isDesktopEnabled)
        }

        fun applyWindows10Touch(webView: WebView, isWindows10TouchEnabled: Boolean) {
            WebViewConfigurator.applyIdentityMode(webView, isDesktopEnabled = false, isWindows10TouchEnabled = isWindows10TouchEnabled)
        }

        fun applyIdentityMode(webView: WebView, isDesktopEnabled: Boolean, isWindows10TouchEnabled: Boolean) {
            WebViewConfigurator.applyIdentityMode(webView, isDesktopEnabled, isWindows10TouchEnabled)
        }

        fun configureBaseSettings(
            webView: WebView,
            isDarkTheme: Boolean,
            isDesktopEnabled: Boolean = false,
            isWindows10TouchEnabled: Boolean = false
        ) {
            WebViewConfigurator.configureBaseSettings(webView, isDarkTheme, isDesktopEnabled, isWindows10TouchEnabled)
        }

        fun applyWebViewTheme(webView: WebView, isDarkTheme: Boolean) {
            WebViewConfigurator.applyWebViewTheme(webView, isDarkTheme)
        }

        fun applyWebPageDarkTheme(webView: WebView?, isDarkTheme: Boolean) {
            WebViewConfigurator.applyWebPageDarkTheme(webView, isDarkTheme)
        }

        // Normalizes and sanitizes MIME types requested by websites via accept attributes.
        // Handles comma-separated values, extensions (.pdf, .png, etc.), and defaults to all types.
        fun normalizeMimeTypes(acceptTypes: Array<String>?): Array<String> {
            if (acceptTypes == null || acceptTypes.isEmpty()) {
                return arrayOf("*/*")
            }
            val mimeList = mutableListOf<String>()
            for (raw in acceptTypes) {
                if (raw.isBlank()) continue
                val parts = raw.split(",")
                for (part in parts) {
                    val trimmed = part.trim()
                    if (trimmed.isEmpty()) continue
                    if (trimmed.startsWith(".")) {
                        val ext = trimmed.substring(1).lowercase(java.util.Locale.ROOT)
                        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
                        if (mime != null) {
                            mimeList.add(mime)
                        } else {
                            mimeList.add("*/*")
                        }
                    } else if (trimmed.contains("/")) {
                        mimeList.add(trimmed)
                    }
                }
            }
            return if (mimeList.isEmpty()) arrayOf("*/*") else mimeList.distinct().toTypedArray()
        }

        /**
         * Creates standard Android System Document Picker Intent (ACTION_OPEN_DOCUMENT).
         */
        fun createFileChooserIntent(
            acceptTypes: Array<String>?,
            isMultiple: Boolean
        ): Intent {
            val mimeTypes = normalizeMimeTypes(acceptTypes)
            return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (isMultiple) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
                if (mimeTypes.size == 1) {
                    type = mimeTypes[0]
                } else {
                    type = "*/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }
            }
        }

        /**
         * Fallback intent using ACTION_GET_CONTENT if ACTION_OPEN_DOCUMENT is unsupported.
         */
        fun createGetContentIntent(
            acceptTypes: Array<String>?,
            isMultiple: Boolean
        ): Intent {
            val mimeTypes = normalizeMimeTypes(acceptTypes)
            return Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (isMultiple) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
                if (mimeTypes.size == 1) {
                    type = mimeTypes[0]
                } else {
                    type = "*/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }
            }
        }

        /**
         * Parses Activity result from system file picker into Array<Uri>? for WebView callback.
         * Handles single file, multiple files (ClipData), and cancellation/back press.
         */
        fun parseFileChooserResult(
            resultCode: Int,
            data: Intent?,
            resolver: android.content.ContentResolver? = null
        ): Array<Uri>? {
            if (resultCode != android.app.Activity.RESULT_OK || data == null) {
                return null
            }
            val clipData = data.clipData
            val singleUri = data.data

            return when {
                clipData != null && clipData.itemCount > 0 -> {
                    Array(clipData.itemCount) { index ->
                        val uri = clipData.getItemAt(index).uri
                        try {
                            resolver?.takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        } catch (_: Exception) {}
                        uri
                    }
                }
                singleUri != null -> {
                    try {
                        resolver?.takePersistableUriPermission(
                            singleUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: Exception) {}
                    arrayOf(singleUri)
                }
                else -> null
            }
        }
    }
}

@Composable
fun BrowserApp(
    viewModel: BrowserViewModel,
    tabWebViewManager: TabWebViewManager,
    onClearAllData: () -> Unit,
    onClearCacheAndCookies: () -> Unit,
    onToggleDesktopMode: (Boolean) -> Unit,
    onToggleWindows10Touch: (Boolean) -> Unit = {},
    onSslProceed: (String) -> Unit = {},
    onSslCancel: (String) -> Unit = {},
    onHandleUrlNavigation: (String) -> Boolean = { false },
    onActiveWebViewChanged: (WebView) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val colors = com.muslim.browser.pro.ui.theme.LocalAppColors.current

    val activeTab = uiState.tabs.find { it.id == uiState.currentTabId }
    val (activeWebView, _) = remember(uiState.currentTabId) {
        tabWebViewManager.getOrCreateWebView(
            tabId = uiState.currentTabId,
            url = if (activeTab?.isHomePage == false) activeTab.url else null,
            bundle = activeTab?.bundle
        )
    }

    LaunchedEffect(activeWebView) {
        onActiveWebViewChanged(activeWebView)
        tabWebViewManager.pauseAll(exceptTabId = uiState.currentTabId)
        tabWebViewManager.resumeTab(uiState.currentTabId)
        val isWin10Touch = uiState.isWindows10TouchEnabled
        WebViewConfigurator.syncDesktopMode(
            webView = activeWebView,
            url = activeWebView.url,
            isDesktopEnabled = uiState.isDesktopModeEnabled,
            updateUserAgent = true,
            isWindows10TouchEnabled = isWin10Touch
        )
    }

    // Toast / Snackbar feedback
    LaunchedEffect(uiState.toastMessage) {
        val msg = uiState.toastMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // Handle Hardware/Gesture Back
    BackHandler(enabled = true) {
        when {
            uiState.isDiagnosticOpen -> viewModel.closeDiagnostic()
            uiState.sslWarningState != null -> onSslCancel(uiState.sslWarningState!!.host)
            uiState.isDownloadsOpen -> viewModel.closeDownloads()
            uiState.isHistoryOpen -> viewModel.closeHistory()
            uiState.isTabsDialogOpen -> viewModel.closeTabsDialog()
            uiState.isMenuOpen -> viewModel.closeMenu()
            uiState.blockedInfo != null -> viewModel.goHome()
            !uiState.isHomePage -> {
                if (activeWebView.canGoBack()) {
                    activeWebView.goBack()
                } else {
                    viewModel.goHome()
                }
            }
            else -> {
                // Let system exit
            }
        }
    }

    val navigateToInput: (String) -> Unit = { input ->
        val trimmed = input.trim()
        if (DownloadPolicy.isAudio(trimmed) || DownloadPolicy.isDownloadableFileUrl(trimmed)) {
            onHandleUrlNavigation(trimmed)
        } else {
            val success = viewModel.submitQueryOrUrl(input)
            if (success) {
                activeWebView.loadUrl(viewModel.uiState.value.currentUrl)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        contentColor = colors.textPrimary,
        bottomBar = {
            BottomNavBar(
                canGoBack = uiState.canGoBack && !uiState.isHomePage,
                canGoForward = uiState.canGoForward && !uiState.isHomePage,
                isDesktopModeEnabled = uiState.isDesktopModeEnabled,
                onGoBack = {
                    if (activeWebView.canGoBack()) activeWebView.goBack() else viewModel.goHome()
                },
                onGoForward = {
                    if (activeWebView.canGoForward()) activeWebView.goForward()
                },
                onNewTab = {
                    val currentTab = viewModel.uiState.value.tabs.find { it.id == viewModel.uiState.value.currentTabId }
                    if (currentTab != null && !currentTab.isHomePage) {
                        val bundle = Bundle()
                        activeWebView.saveState(bundle)
                        viewModel.saveTabState(currentTab.id, bundle)
                    }
                    viewModel.openNewTab()
                },
                onShowTabs = {
                    viewModel.openTabsDialog()
                },
                onToggleDesktopMode = {
                    onToggleDesktopMode(!uiState.isDesktopModeEnabled)
                },
                onOpenMenu = {
                    viewModel.openMenu()
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
        ) {
            // 1. BrowserWebView is persistently kept in the Box layout so the active WebView instance
            // remains warm and attached to the window, preventing teardown, re-attaching, and blank flashes.
            BrowserWebView(
                uiState = uiState,
                webView = activeWebView,
                onUrlSubmit = navigateToInput,
                onReload = {
                    activeWebView.reload()
                },
                modifier = Modifier.fillMaxSize()
            )

            // 2. HomePage overlay when on home page and not blocked
            if (uiState.isHomePage && uiState.blockedInfo == null) {
                HomePage(
                    uiState = uiState,
                    favoriteSites = uiState.favoriteSites,
                    onQueryChange = { viewModel.onSearchInputChange(it) },
                    onSubmitQuery = navigateToInput,
                    onAddFavorite = { name, url -> viewModel.addFavoriteSite(name, url) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 3. BlockedScreen overlay when content is blocked
            if (uiState.blockedInfo != null) {
                BlockedScreen(
                    blockedInfo = uiState.blockedInfo!!,
                    onGoHome = { viewModel.goHome() },
                    onGoBack = {
                        if (activeWebView.canGoBack()) {
                            activeWebView.goBack()
                            viewModel.onPageStarted(activeWebView.url ?: "")
                        } else {
                            viewModel.goHome()
                        }
                    },
                    canGoBack = activeWebView.canGoBack(),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Open Windows Dialog (Triggered by Long-Press on + Button)
            if (uiState.isTabsDialogOpen) {
                OpenWindowsDialog(
                    uiState = uiState,
                    onSelectTab = { selectedTabId ->
                        if (selectedTabId != viewModel.uiState.value.currentTabId) {
                            val currentTab = viewModel.uiState.value.tabs.find { it.id == viewModel.uiState.value.currentTabId }
                            if (currentTab != null && !currentTab.isHomePage) {
                                val bundle = Bundle()
                                activeWebView.saveState(bundle)
                                viewModel.saveTabState(currentTab.id, bundle)
                            }
                            viewModel.selectTab(selectedTabId)
                        } else {
                            viewModel.closeTabsDialog()
                        }
                    },
                    onCloseTab = { tabId ->
                        tabWebViewManager.destroyWebView(tabId)
                        viewModel.closeTab(tabId)
                    },
                    onNewTab = {
                        val currentTab = viewModel.uiState.value.tabs.find { it.id == viewModel.uiState.value.currentTabId }
                        if (currentTab != null && !currentTab.isHomePage) {
                            val bundle = Bundle()
                            activeWebView.saveState(bundle)
                            viewModel.saveTabState(currentTab.id, bundle)
                        }
                        viewModel.openNewTab()
                    },
                    onDismiss = { viewModel.closeTabsDialog() }
                )
            }

            // Three-line Menu Sheet (Compact Floating Window)
            if (uiState.isMenuOpen) {
                BrowserMenuSheet(
                    uiState = uiState,
                    onDismiss = { viewModel.closeMenu() },
                    onOpenHistory = { viewModel.openHistory() },
                    onOpenDownloads = {
                        viewModel.closeMenu()
                        viewModel.openDownloads()
                    },
                    onAddKeyword = { kw -> viewModel.addCustomKeyword(kw) },
                    onTogglePopupBlocking = { enabled -> viewModel.togglePopupBlocking(enabled) },
                    onToggleAdBlocking = { enabled -> viewModel.toggleAdBlocking(enabled) },
                    onClearAllData = onClearAllData,
                    onClearCacheAndCookies = onClearCacheAndCookies,
                    onToggleDesktopMode = onToggleDesktopMode,
                    onToggleWindows10Touch = onToggleWindows10Touch,
                    onTranslateToBengali = {
                        viewModel.translateCurrentPage(
                            evaluateJs = { script, cb -> activeWebView.evaluateJavascript(script, cb) },
                            reloadPage = { activeWebView.reload() }
                        )
                    },
                    onSelectTheme = { theme -> viewModel.setAppTheme(theme) },
                    onSelectTranslationEngine = { engine -> viewModel.selectTranslationEngine(engine) },
                    onOpenDiagnostics = { viewModel.openDiagnostic() }
                )
            }

            // Temporary Diagnostic Viewport & Environment Inspector Overlay
            if (uiState.isDiagnosticOpen) {
                DiagnosticScreen(
                    webView = activeWebView,
                    isDesktopModeEnabled = uiState.isDesktopModeEnabled,
                    isWindows10TouchEnabled = uiState.isWindows10TouchEnabled,
                    onDismiss = { viewModel.closeDiagnostic() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Browsing History Screen Overlay
            if (uiState.isHistoryOpen) {
                HistoryScreen(
                    history = uiState.browsingHistory,
                    onSelectUrl = { url ->
                        viewModel.closeHistory()
                        navigateToInput(url)
                    },
                    onDeleteEntry = { id -> viewModel.deleteHistoryEntry(id) },
                    onClearAll = { viewModel.clearAllHistory() },
                    onDismiss = { viewModel.closeHistory() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // In-App Download History Screen Overlay
            if (uiState.isDownloadsOpen) {
                DownloadHistoryScreen(
                    downloads = uiState.downloadHistory,
                    onOpenFile = { entry -> openDownloadedFile(context, entry) },
                    onDeleteEntry = { id -> viewModel.deleteDownloadEntry(id) },
                    onClearAll = { viewModel.clearAllDownloadHistory() },
                    onDismiss = { viewModel.closeDownloads() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // SSL Certificate Warning Dialog
            uiState.sslWarningState?.let { sslState ->
                AlertDialog(
                    onDismissRequest = { onSslCancel(sslState.host) },
                    containerColor = colors.surface,
                    titleContentColor = colors.textPrimary,
                    textContentColor = colors.textSecondary,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    title = {
                        Text(
                            text = "Security Certificate Warning",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "The security certificate for \"${sslState.host}\" cannot be fully verified because the server did not provide its intermediate certificate chain.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Host: ${sslState.host}",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.textSecondary
                            )
                            if (sslState.details.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = sslState.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary.copy(alpha = 0.8f)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Do you want to proceed to this website anyway?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { onSslProceed(sslState.host) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Proceed")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { onSslCancel(sslState.host) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colors.textPrimary
                            ),
                            border = BorderStroke(1.dp, colors.border)
                        ) {
                            Text("Cancel / Go Back")
                        }
                    }
                )
            }
        }
    }
}

/**
 * Safely opens a completed downloaded file with the appropriate application using ACTION_VIEW.
 * Uses DownloadManager.getUriForDownloadedFile(downloadId) as single source of truth,
 * with audio/mpeg MIME type for MP3 files and FLAG_GRANT_READ_URI_PERMISSION.
 */
private fun openDownloadedFile(context: Context, entry: DownloadEntry) {
    try {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        var uri: Uri? = null

        // 1. Retrieve the actual downloaded URI using DownloadManager.getUriForDownloadedFile(downloadId)
        if (entry.downloadId > 0L) {
            try {
                uri = dm?.getUriForDownloadedFile(entry.downloadId)
            } catch (e: Exception) {
                android.util.Log.w("DownloadManager", "Could not get URI from DownloadManager for ${entry.downloadId}", e)
            }
        }

        // 2. Fallback to saved localUri if available
        if (uri == null && !entry.localUri.isNullOrBlank()) {
            val rawUri = Uri.parse(entry.localUri)
            uri = if (rawUri.scheme == "file") {
                val file = java.io.File(rawUri.path ?: "")
                if (file.exists()) {
                    androidx.core.content.FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                } else rawUri
            } else {
                rawUri
            }
        }

        // 3. Fallback to Public Downloads directory file
        if (uri == null) {
            val publicFile = java.io.File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                entry.fileName
            )
            if (publicFile.exists()) {
                uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    publicFile
                )
            }
        }

        if (uri == null) {
            Toast.makeText(context, "Downloaded file not found.", Toast.LENGTH_SHORT).show()
            return
        }

        val isMp3 = entry.fileName.endsWith(".mp3", ignoreCase = true) ||
                entry.mimeType.equals("audio/mpeg", ignoreCase = true) ||
                entry.mimeType.equals("audio/mp3", ignoreCase = true)

        val effectiveMime = if (isMp3) {
            "audio/mpeg"
        } else if (entry.mimeType.isNotBlank() && entry.mimeType != "*/*") {
            entry.mimeType
        } else {
            val ext = entry.fileName.substringAfterLast('.', "").lowercase(Locale.ROOT)
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: "*/*"
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, effectiveMime)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val isMp3 = entry.fileName.endsWith(".mp3", ignoreCase = true) || entry.mimeType.contains("audio")
        if (isMp3) {
            Toast.makeText(context, "No compatible app found to open this MP3.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No compatible app found to open ${entry.fileName}", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
