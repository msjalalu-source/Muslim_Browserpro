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
import com.muslim.browser.pro.browser.ui.HomePage
import com.muslim.browser.pro.browser.ui.HistoryScreen
import com.muslim.browser.pro.browser.ui.OpenWindowsDialog
import com.muslim.browser.pro.ui.theme.MyApplicationTheme
import java.io.ByteArrayInputStream
import java.util.Collections
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModels()
    private var webViewInstance: WebView? = null
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

    private fun queryActiveDownloads(): Boolean {
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager ?: return false
        val currentDownloads = viewModel.uiState.value.downloadHistory
        val activeEntries = currentDownloads.filter {
            it.downloadId > 0L && (it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PAUSED)
        }

        if (activeEntries.isEmpty()) {
            return false
        }

        var stillActive = false

        for (entry in activeEntries) {
            val query = DownloadManager.Query().setFilterById(entry.downloadId)
            try {
                dm.query(query)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status = if (statusIndex != -1) cursor.getInt(statusIndex) else -1
                        val bytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        val downloadedBytes = if (bytesIndex != -1) cursor.getLong(bytesIndex) else 0L
                        val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                        val totalBytes = if (totalBytesIndex != -1) cursor.getLong(totalBytesIndex) else -1L
                        val localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                        val localUri = if (localUriIndex != -1) cursor.getString(localUriIndex) else null

                        when (status) {
                            DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING -> {
                                stillActive = true
                                viewModel.updateDownloadProgress(
                                    downloadId = entry.downloadId,
                                    status = DownloadStatus.DOWNLOADING,
                                    downloadedBytes = downloadedBytes,
                                    totalBytes = totalBytes,
                                    localUri = localUri
                                )
                            }
                            DownloadManager.STATUS_PAUSED -> {
                                stillActive = true
                                viewModel.updateDownloadProgress(
                                    downloadId = entry.downloadId,
                                    status = DownloadStatus.PAUSED,
                                    downloadedBytes = downloadedBytes,
                                    totalBytes = totalBytes,
                                    localUri = localUri
                                )
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                viewModel.updateDownloadStatus(
                                    downloadId = entry.downloadId,
                                    status = DownloadStatus.COMPLETED,
                                    localUri = localUri,
                                    downloadedBytes = downloadedBytes,
                                    totalBytes = totalBytes
                                )
                            }
                            DownloadManager.STATUS_FAILED -> {
                                val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                                val reason = if (reasonIndex != -1) cursor.getInt(reasonIndex) else -1
                                android.util.Log.d("DownloadManager", "Download ${entry.downloadId} failed: reason=$reason")
                                viewModel.updateDownloadStatus(
                                    downloadId = entry.downloadId,
                                    status = DownloadStatus.FAILED,
                                    localUri = localUri,
                                    downloadedBytes = downloadedBytes,
                                    totalBytes = totalBytes
                                )
                            }
                        }
                    } else {
                        viewModel.updateDownloadStatus(
                            downloadId = entry.downloadId,
                            status = DownloadStatus.FAILED
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DownloadManager", "Error querying downloadId ${entry.downloadId}", e)
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

    private fun checkDownloadStatus(downloadId: Long) {
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager ?: return
        val query = DownloadManager.Query().setFilterById(downloadId)
        try {
            dm.query(query)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = if (statusIndex != -1) cursor.getInt(statusIndex) else -1
                    val localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    val localUri = if (localUriIndex != -1) cursor.getString(localUriIndex) else null
                    val bytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                    val downloadedBytes = if (bytesIndex != -1) cursor.getLong(bytesIndex) else 0L
                    val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    val totalBytes = if (totalBytesIndex != -1) cursor.getLong(totalBytesIndex) else -1L

                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            viewModel.updateDownloadStatus(
                                downloadId = downloadId,
                                status = DownloadStatus.COMPLETED,
                                localUri = localUri,
                                downloadedBytes = downloadedBytes,
                                totalBytes = totalBytes
                            )
                        }
                        DownloadManager.STATUS_FAILED -> {
                            viewModel.updateDownloadStatus(
                                downloadId = downloadId,
                                status = DownloadStatus.FAILED,
                                localUri = localUri,
                                downloadedBytes = downloadedBytes,
                                totalBytes = totalBytes
                            )
                        }
                        DownloadManager.STATUS_PAUSED -> {
                            viewModel.updateDownloadProgress(
                                downloadId = downloadId,
                                status = DownloadStatus.PAUSED,
                                downloadedBytes = downloadedBytes,
                                totalBytes = totalBytes,
                                localUri = localUri
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DownloadManager", "Error checking download status for $downloadId", e)
        }
        val hasActive = queryActiveDownloads()
        if (!hasActive) {
            progressPollingJob?.cancel()
            progressPollingJob = null
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val downloadFilter = android.content.IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(downloadReceiver, downloadFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(downloadReceiver, downloadFilter)
        }

        // Check if any ongoing downloads need active progress polling
        startProgressPolling()

        val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        // Create single WebView instance with optimized memory settings
        val webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            applyWebViewTheme(this, isDarkTheme)

            // Ensure cookies and third-party cookies are accepted for cross-origin assets
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
                setSupportMultipleWindows(true)
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                // Keep offscreenPreRaster false to reduce RAM and GPU rasterization load
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    offscreenPreRaster = false
                }
                mediaPlaybackRequiresUserGesture = true
                saveFormData = false
            }
            applyDesktopModeToWebView(this, viewModel.uiState.value.isDesktopModeEnabled)
            DesktopModeDiagnostics.webViewRecreationCount++

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    android.util.Log.d("DIAGNOSTIC", "shouldOverrideUrlLoading: URL=$url")
                    DesktopModeDiagnostics.redirectChain.add(url)
                    if (view != null) {
                        val uaChanged = applyDesktopModeToWebView(view, viewModel.uiState.value.isDesktopModeEnabled, url)
                        if (uaChanged) {
                            android.util.Log.d("DESKTOP_DIAG", "UA updated for auth/desktop transition in shouldOverrideUrlLoading, loading destination URL: $url")
                            DesktopModeDiagnostics.loadUrlCount++
                            view.loadUrl(url)
                            return true
                        }
                    }
                    val handled = handleUrlNavigation(view, url)
                    DesktopModeDiagnostics.lastShouldOverrideResult = handled
                    return handled
                }

                @Deprecated("Deprecated in Java", ReplaceWith("shouldOverrideUrlLoading(view, request)"))
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url == null) return false
                    android.util.Log.d("DIAGNOSTIC", "shouldOverrideUrlLoading(String): URL=$url")
                    DesktopModeDiagnostics.redirectChain.add(url)
                    if (view != null) {
                        val uaChanged = applyDesktopModeToWebView(view, viewModel.uiState.value.isDesktopModeEnabled, url)
                        if (uaChanged) {
                            android.util.Log.d("DESKTOP_DIAG", "UA updated for auth/desktop transition in shouldOverrideUrlLoading(String), loading destination URL: $url")
                            DesktopModeDiagnostics.loadUrlCount++
                            view.loadUrl(url)
                            return true
                        }
                    }
                    val handled = handleUrlNavigation(view, url)
                    DesktopModeDiagnostics.lastShouldOverrideResult = handled
                    return handled
                }

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    val uri = request?.url ?: return null
                    val reqUrl = uri.toString()
                    val host = uri.host?.lowercase(Locale.ROOT)
                    if (viewModel.uiState.value.isAdBlockingEnabled && ProtectionEngine.isAdRequest(uri)) {
                        val resType = request.requestHeaders?.get("Accept") ?: "subresource"
                        android.util.Log.e("DIAGNOSTIC", "INTERCEPT_BLOCK=shouldInterceptRequest")
                        android.util.Log.e("DIAGNOSTIC", "REQUEST_URL=$reqUrl")
                        android.util.Log.e("DIAGNOSTIC", "RESOURCE_TYPE=$resType")
                        android.util.Log.e("DIAGNOSTIC", "BLOCK_REASON=Ad Request Blocked")
                        return WebResourceResponse(
                            "text/plain",
                            "UTF-8",
                            ByteArrayInputStream(ByteArray(0))
                        )
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    android.util.Log.d("DIAGNOSTIC", "onPageStarted: URL=$url")
                    if (view != null && url != null) {
                        // Track auth state but do NOT mutate userAgentString mid-load to avoid disrupting in-flight requests
                        applyDesktopModeToWebView(view, viewModel.uiState.value.isDesktopModeEnabled, url, updateUserAgent = false)
                    }
                    url?.let { viewModel.onPageStarted(it) }
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                    view?.settings?.cacheMode = WebSettings.LOAD_DEFAULT
                    DesktopModeDiagnostics.currentCacheMode = WebSettings.LOAD_DEFAULT
                    applyDesktopViewport(view, viewModel.uiState.value.isDesktopModeEnabled)
                    applyWebPageDarkTheme(view, isDarkThemeActive)
                    viewModel.onPageCommitVisible()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.settings?.cacheMode = WebSettings.LOAD_DEFAULT
                    DesktopModeDiagnostics.currentCacheMode = WebSettings.LOAD_DEFAULT
                    android.util.Log.d("DIAGNOSTIC", "onPageFinished: URL=$url")
                    applyDesktopViewport(view, viewModel.uiState.value.isDesktopModeEnabled)
                    applyWebPageDarkTheme(view, isDarkThemeActive)
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
                        tempWebView.settings.javaScriptEnabled = true
                        tempWebView.webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(v: WebView?, request: WebResourceRequest?): Boolean {
                                val destUrl = request?.url?.toString() ?: return false
                                tempWebView.destroy()
                                if (view != null) {
                                    applyDesktopModeToWebView(view, viewModel.uiState.value.isDesktopModeEnabled, destUrl)
                                    view.loadUrl(destUrl)
                                }
                                return true
                            }

                            @Deprecated("Deprecated in Java")
                            override fun shouldOverrideUrlLoading(v: WebView?, destUrl: String?): Boolean {
                                if (destUrl == null) return false
                                tempWebView.destroy()
                                if (view != null) {
                                    applyDesktopModeToWebView(view, viewModel.uiState.value.isDesktopModeEnabled, destUrl)
                                    view.loadUrl(destUrl)
                                }
                                return true
                            }

                            override fun onPageStarted(v: WebView?, destUrl: String?, favicon: Bitmap?) {
                                super.onPageStarted(v, destUrl, favicon)
                                if (!destUrl.isNullOrBlank() && destUrl != "about:blank") {
                                    tempWebView.destroy()
                                    if (view != null) {
                                        applyDesktopModeToWebView(view, viewModel.uiState.value.isDesktopModeEnabled, destUrl)
                                        view.loadUrl(destUrl)
                                    }
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
        webViewInstance = webView

        // Restore active tab webpage on cold start / process recreation if not on home page
        val activeTab = viewModel.uiState.value.tabs.find { it.id == viewModel.uiState.value.currentTabId }
        if (activeTab != null && !activeTab.isHomePage && activeTab.url.isNotEmpty()) {
            webView.loadUrl(activeTab.url)
        }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.appTheme) {
                applyWebViewTheme(webView, isDarkTheme = uiState.appTheme != com.muslim.browser.pro.ui.theme.AppTheme.WHITE)
            }

            MyApplicationTheme(appTheme = uiState.appTheme) {
                BrowserApp(
                    viewModel = viewModel,
                    webView = webView,
                    onClearAllData = { clearAllData() },
                    onClearCacheAndCookies = { clearCacheAndCookies() },
                    onToggleDesktopMode = { enabled -> setDesktopMode(enabled) },
                    onSslProceed = { host -> onSslPromptProceed(host) },
                    onSslCancel = { host -> onSslPromptCancel(host) }
                )
            }
        }
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

    private fun isDirectAudioUrl(url: String): Boolean {
        val clean = url.substringBefore('?').substringBefore('#').lowercase(Locale.ROOT)
        return clean.endsWith(".mp3") || clean.endsWith(".wav") || clean.endsWith(".ogg") ||
                clean.endsWith(".m4a") || clean.endsWith(".aac") || clean.endsWith(".flac")
    }

    private fun handleUrlNavigation(view: WebView?, url: String): Boolean {
        android.util.Log.d("DIAGNOSTIC", "handleUrlNavigation: URL=$url")
        // 1. Detect if this is a search engine request
        val searchEngineQuery = ProtectionEngine.extractSearchEngineQuery(url)
        if (searchEngineQuery != null) {
            // Check custom keyword on the extracted search query
            val blockedKw = ProtectionEngine.isBlockedByCustomKeywords(
                searchEngineQuery,
                viewModel.uiState.value.customKeywords,
                viewModel.getNormalizedKeywords()
            )
            if (blockedKw != null) {
                viewModel.setBlockedUrl(
                    url = url,
                    reason = "Custom Keyword Protection",
                    detail = "Search query blocked due to protected keyword: \"$blockedKw\""
                )
                return true // Block navigation
            }

            // If allowed, check if already Google SafeSearch
            if (ProtectionEngine.isGoogleSafeSearchUrl(url)) {
                return false // Let WebView proceed with Google SafeSearch
            }

            // Normalize to Google SafeSearch and load
            val safeUrl = ProtectionEngine.buildGoogleSafeSearchUrl(searchEngineQuery)
            if (view != null) {
                applyDesktopModeToWebView(view, viewModel.uiState.value.isDesktopModeEnabled, safeUrl)
            }
            view?.loadUrl(safeUrl)
            return true // Intercepted and redirected
        }

        // 2. Direct audio / MP3 link check
        if (isDirectAudioUrl(url)) {
            startDownload(
                url = url,
                userAgent = view?.settings?.userAgentString,
                contentDisposition = null,
                mimetype = "audio/mpeg"
            )
            return true
        }

        // 3. Direct URL navigation check
        val isBlocked = viewModel.checkAndFilterUrl(url)
        if (isBlocked) {
            return true // Block navigation
        }

        return false
    }

    private fun clearAllData() {
        val webView = webViewInstance ?: return
        try {
            // 1. Clear browsing history from WebView, ViewModel, and persistent storage
            webView.clearHistory()
            viewModel.clearAllHistory()
            viewModel.onHistoryCleared()

            // 2. Clear cache
            webView.clearCache(true)
            FaviconManager.clearCache(this)

            // 3. Clear cookies
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()

            // 4. Clear WebStorage (DOM storage / localStorage)
            WebStorage.getInstance().deleteAllData()

            // 5. Clear Form data & SSL preferences
            webView.clearFormData()
            webView.clearSslPreferences()

            // 6. Clear pending trusted download origins
            DownloadPolicy.clearTrustedOrigin()

            viewModel.showToast("All browsing data cleared.")
        } catch (e: Exception) {
            viewModel.showToast("Browsing data cleared.")
        }
    }

    private fun clearCacheAndCookies() {
        val webView = webViewInstance ?: return
        try {
            // 1. Clear cache
            webView.clearCache(true)

            // 2. Clear cookies
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()

            // 3. Clear WebStorage
            WebStorage.getInstance().deleteAllData()

            viewModel.showToast("Cache and cookies cleared.")
        } catch (e: Exception) {
            viewModel.showToast("Cache and cookies cleared.")
        }
    }

    private fun setDesktopMode(enabled: Boolean) {
        DesktopModeDiagnostics.urlBeforeToggle = webViewInstance?.url
        DesktopModeDiagnostics.userAgentBeforeToggle = webViewInstance?.settings?.userAgentString

        viewModel.toggleDesktopMode(enabled)
        val webView = webViewInstance ?: return

        // 1. Authoritative current URL: read directly from the active WebView instance
        val currentUrl = webView.url?.takeIf { it.isNotBlank() && it != "about:blank" }
            ?: viewModel.uiState.value.currentUrl.takeIf { it.isNotBlank() && it != "about:blank" }

        // If on browser home page with no web page open, configure setting for future navigation
        if (currentUrl == null || viewModel.uiState.value.isHomePage) {
            applyDesktopModeToWebView(webView, enabled, null)
            DesktopModeDiagnostics.urlAfterToggle = currentUrl
            DesktopModeDiagnostics.userAgentAfterToggle = webView.settings.userAgentString
            return
        }

        // 2. Apply the desktop User-Agent to the active WebView settings and adjust viewport
        applyDesktopModeToWebView(webView, enabled, currentUrl)
        applyDesktopViewport(webView, enabled)

        // 3. Synchronize ViewModel state with the authoritative active URL
        viewModel.onPageStarted(currentUrl)

        // 4. Record diagnostics
        DesktopModeDiagnostics.urlAfterToggle = currentUrl
        DesktopModeDiagnostics.userAgentAfterToggle = webView.settings.userAgentString
        DesktopModeDiagnostics.reloadCount++
        DesktopModeDiagnostics.lastTriggerSource = "setDesktopMode_user_toggle"

        android.util.Log.d(
            "DESKTOP_DIAG",
            "setDesktopMode toggle: enabled=$enabled, url=$currentUrl, UA=${webView.settings.userAgentString}"
        )

        // 5. Temporarily bypass HTTP cache so server re-evaluates the new User-Agent instead of returning 304 Not Modified
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        DesktopModeDiagnostics.currentCacheMode = WebSettings.LOAD_NO_CACHE

        // 6. Reload exactly ONE time
        webView.reload()
    }

    override fun onPause() {
        super.onPause()
        webViewInstance?.apply {
            onPause()
            pauseTimers()
        }
    }

    override fun onResume() {
        super.onResume()
        webViewInstance?.apply {
            onResume()
            resumeTimers()
        }
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
        webViewInstance?.apply {
            stopLoading()
            pauseTimers()
            onPause()
            removeAllViews()
            destroy()
        }
        webViewInstance = null
        super.onDestroy()
    }

    object DesktopModeDiagnostics {
        var urlBeforeToggle: String? = null
        var urlAfterToggle: String? = null
        var userAgentBeforeToggle: String? = null
        var userAgentAfterToggle: String? = null
        var reloadCount: Int = 0
        var loadUrlCount: Int = 0
        var lastTriggerSource: String? = null
        val redirectChain: MutableList<String> = mutableListOf()
        var lastShouldOverrideResult: Boolean = false
        var currentDesktopMode: Boolean = false
        var currentCacheMode: Int = WebSettings.LOAD_DEFAULT
        var webViewRecreationCount: Int = 0
        var isAuthFlowActive: Boolean = false

        fun reset() {
            urlBeforeToggle = null
            urlAfterToggle = null
            userAgentBeforeToggle = null
            userAgentAfterToggle = null
            reloadCount = 0
            loadUrlCount = 0
            lastTriggerSource = null
            redirectChain.clear()
            lastShouldOverrideResult = false
            currentDesktopMode = false
            currentCacheMode = WebSettings.LOAD_DEFAULT
            webViewRecreationCount = 0
            isAuthFlowActive = false
        }
    }

    companion object {
        const val DESKTOP_USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"

        @Volatile
        var isDarkThemeActive: Boolean = true

        @Volatile
        var defaultMobileUserAgent: String? = null

        @Volatile
        var isAuthFlowActive: Boolean = false

        /**
         * Resolves the desktop User-Agent string, dynamically incorporating the actual Chrome
         * version installed on the device for optimal compatibility and bot-detection avoidance.
         */
        fun resolveDesktopUserAgent(context: Context): String {
            val baseMobile = defaultMobileUserAgent ?: try {
                WebSettings.getDefaultUserAgent(context)
            } catch (_: Exception) {
                null
            }
            return if (!baseMobile.isNullOrBlank()) {
                val chromeVersionRegex = Regex("Chrome/([0-9.]+)")
                val match = chromeVersionRegex.find(baseMobile)
                val chromeVer = match?.value ?: "Chrome/134.0.0.0"
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) $chromeVer Safari/537.36"
            } else {
                DESKTOP_USER_AGENT
            }
        }

        /**
         * Detects whether a URL represents an authentication, login, or OAuth identity provider endpoint.
         * Used to temporarily supply compatible mobile configuration during authentication exchanges.
         */
        fun isAuthenticationEndpoint(url: String?): Boolean {
            if (url.isNullOrBlank()) return false
            return try {
                val uri = Uri.parse(url)
                val host = uri.host?.lowercase(Locale.ROOT) ?: return false
                val path = uri.path?.lowercase(Locale.ROOT) ?: ""

                // 1. Google Account & OAuth hosts
                if (host == "accounts.google.com" ||
                    host.endsWith(".accounts.google.com") ||
                    host == "oauth2.googleapis.com" ||
                    host == "myaccount.google.com" ||
                    host == "accounts.youtube.com"
                ) {
                    return true
                }

                // 2. Apple ID Auth
                if (host == "appleid.apple.com") {
                    return true
                }

                // 3. Generic OAuth authorization endpoints
                if (path.contains("/oauth2/v2/auth") ||
                    path.contains("/o/oauth2/auth") ||
                    path.contains("/oauth/authorize") ||
                    path.contains("/login/oauth/authorize") ||
                    path.contains("/oauth2/authorize") ||
                    path.contains("/oauth/v2/auth")
                ) {
                    return true
                }

                // 4. Google services sign-in, account addition, and sign-out endpoints
                val isGoogle = host == "google.com" || host.endsWith(".google.com")
                if (isGoogle && (
                    path.startsWith("/servicelogin") ||
                    path.startsWith("/signin") ||
                    path.startsWith("/interactive_login") ||
                    path.contains("/addsession") ||
                    path.contains("/accountchooser") ||
                    path.contains("/logout") ||
                    path.contains("/signout") ||
                    path.startsWith("/accounts/") ||
                    (uri.getQueryParameter("service") != null && path.contains("login"))
                )) {
                    return true
                }

                // 5. Common identity providers / login endpoints
                if (path.contains("/login") || path.contains("/signin") || path.contains("/auth")) {
                    if (host.contains("login.") || host.contains("auth.") || host.contains("identity.") || host.contains("sso.")) {
                        return true
                    }
                }

                false
            } catch (_: Exception) {
                false
            }
        }

        /**
         * Backward-compatible alias for authentication endpoint detection.
         */
        fun isGoogleAuthUrl(url: String?): Boolean = isAuthenticationEndpoint(url)

        /**
         * Pure configuration function to apply Desktop or Mobile User-Agent and viewport settings.
         * Returns true if userAgentString was updated, false otherwise.
         * CRITICAL: Must NEVER trigger reload() or loadUrl().
         */
        fun applyDesktopModeToWebView(
            webView: WebView,
            enabled: Boolean,
            url: String? = null,
            updateUserAgent: Boolean = true
        ): Boolean {
            var uaChanged = false
            try {
                if (defaultMobileUserAgent == null) {
                    defaultMobileUserAgent = try {
                        WebSettings.getDefaultUserAgent(webView.context)
                    } catch (_: Exception) {
                        webView.settings.userAgentString
                    }
                }

                if (enabled && url != null) {
                    if (isAuthenticationEndpoint(url)) {
                        isAuthFlowActive = true
                        DesktopModeDiagnostics.isAuthFlowActive = true
                        android.util.Log.d("DESKTOP_DIAG", "Auth flow activated for URL: $url")
                    } else if (isAuthFlowActive) {
                        val uri = try { Uri.parse(url) } catch (_: Exception) { null }
                        val host = uri?.host?.lowercase(Locale.ROOT) ?: ""
                        val path = uri?.path?.lowercase(Locale.ROOT) ?: ""
                        val isIntermediate = path.contains("/callback") ||
                                path.contains("/redirect") ||
                                path.contains("/signin") ||
                                path.contains("/login") ||
                                path.contains("/auth") ||
                                path.contains("/oauth") ||
                                path.contains("/consent") ||
                                path.contains("/challenge") ||
                                path.contains("/checkpoint") ||
                                path.contains("/speedbump") ||
                                path.contains("/saml") ||
                                path.contains("/federation") ||
                                path.contains("/logout") ||
                                path.contains("/signout") ||
                                path.contains("/addsession") ||
                                path.contains("/accountchooser") ||
                                path.contains("/checkcookie") ||
                                path.contains("/setosid") ||
                                path.contains("/embedded") ||
                                host.contains("accounts.") ||
                                host.contains("login.") ||
                                host.contains("auth.") ||
                                host.contains("identity.") ||
                                host.contains("sso.")
                        if (!isIntermediate) {
                            isAuthFlowActive = false
                            DesktopModeDiagnostics.isAuthFlowActive = false
                            android.util.Log.d("DESKTOP_DIAG", "Auth flow completed on URL: $url")
                        }
                    }
                } else if (!enabled) {
                    isAuthFlowActive = false
                    DesktopModeDiagnostics.isAuthFlowActive = false
                }

                val shouldUseMobile = !enabled || isAuthFlowActive
                val targetUserAgent = if (shouldUseMobile) {
                    defaultMobileUserAgent ?: webView.settings.userAgentString
                } else {
                    resolveDesktopUserAgent(webView.context)
                }

                if (updateUserAgent && webView.settings.userAgentString != targetUserAgent) {
                    android.util.Log.d(
                        "DESKTOP_DIAG",
                        "Setting userAgentString: $targetUserAgent (isAuthFlowActive=$isAuthFlowActive)"
                    )
                    webView.settings.userAgentString = targetUserAgent
                    uaChanged = true
                }

                webView.settings.useWideViewPort = true
                webView.settings.loadWithOverviewMode = true
                webView.settings.builtInZoomControls = true
                webView.settings.displayZoomControls = false

                DesktopModeDiagnostics.currentDesktopMode = enabled
            } catch (_: Exception) {}
            return uaChanged
        }

        /**
         * Configures viewport settings and dynamically ensures the viewport meta tag
         * presents a full desktop layout (width=1024) in Desktop Mode, and restores standard
         * device-width in Mobile Mode or during active authentication flows.
         * CRITICAL: Must NEVER trigger reload() or loadUrl().
         */
        fun applyDesktopViewport(webView: WebView?, enabled: Boolean) {
            if (webView == null) return
            try {
                webView.settings.useWideViewPort = true
                webView.settings.loadWithOverviewMode = true
                webView.settings.builtInZoomControls = true
                webView.settings.displayZoomControls = false

                // In active authentication flows, maintain responsive mobile viewport for usability and bot-detection avoidance
                val effectiveDesktopMode = enabled && !isAuthFlowActive

                val script = if (effectiveDesktopMode) {
                    """
                        (function() {
                            try {
                                var metas = document.querySelectorAll('meta[name="viewport"]');
                                if (metas.length > 0) {
                                    metas.forEach(function(m) {
                                        m.setAttribute('content', 'width=1024');
                                    });
                                } else {
                                    var meta = document.createElement('meta');
                                    meta.name = 'viewport';
                                    meta.content = 'width=1024';
                                    (document.head || document.documentElement).appendChild(meta);
                                }
                            } catch(e) {}
                        })();
                    """.trimIndent()
                } else {
                    """
                        (function() {
                            try {
                                var metas = document.querySelectorAll('meta[name="viewport"]');
                                if (metas.length > 0) {
                                    metas.forEach(function(m) {
                                        m.setAttribute('content', 'width=device-width, initial-scale=1.0');
                                    });
                                }
                            } catch(e) {}
                        })();
                    """.trimIndent()
                }
                webView.evaluateJavascript(script, null)
            } catch (_: Throwable) {}
        }

        /**
         * Applies the native dark or light theme settings to the WebView.
         * Leverages native Android WebView algorithmic darkening and force dark capabilities,
         * and applies clean, lightweight dark theme rendering to web page content.
         */
        fun applyWebViewTheme(webView: WebView, isDarkTheme: Boolean) {
            try {
                isDarkThemeActive = isDarkTheme
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    webView.settings.isAlgorithmicDarkeningAllowed = isDarkTheme
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    @Suppress("DEPRECATION")
                    webView.settings.forceDark = if (isDarkTheme) {
                        WebSettings.FORCE_DARK_ON
                    } else {
                        WebSettings.FORCE_DARK_OFF
                    }
                }
                val bgColor = if (isDarkTheme) android.graphics.Color.parseColor("#0F172A") else android.graphics.Color.WHITE
                webView.setBackgroundColor(bgColor)
                applyWebPageDarkTheme(webView, isDarkTheme)
            } catch (_: Exception) {}
        }

        /**
         * Applies or removes lightweight dark rendering on web content.
         * Preserves true image, video, and media colors while darkening light backgrounds and text.
         */
        fun applyWebPageDarkTheme(webView: WebView?, isDarkTheme: Boolean) {
            if (webView == null) return
            try {
                if (isDarkTheme) {
                    val script = """
                        (function() {
                            try {
                                var id = '__mb_dark_theme__';
                                if (document.getElementById(id)) return;
                                var target = document.body || document.documentElement;
                                if (!target) return;
                                var bg = window.getComputedStyle(target).backgroundColor;
                                var m = bg ? bg.match(/rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*([\d.]+))?\)/) : null;
                                if (m) {
                                    var alpha = m[4] !== undefined ? parseFloat(m[4]) : 1;
                                    if (alpha > 0.1) {
                                        var r = parseInt(m[1]), g = parseInt(m[2]), b = parseInt(m[3]);
                                        var lum = 0.299 * r + 0.587 * g + 0.114 * b;
                                        if (lum < 65) return;
                                    }
                                }
                                var style = document.createElement('style');
                                style.id = id;
                                style.textContent = 'html { filter: invert(100%) hue-rotate(180deg) !important; background-color: #0F172A !important; } img, video, canvas, svg, picture, iframe, [style*="background-image"] { filter: invert(100%) hue-rotate(180deg) !important; }';
                                (document.head || document.documentElement).appendChild(style);
                            } catch(e) {}
                        })();
                    """.trimIndent()
                    webView.evaluateJavascript(script, null)
                } else {
                    val script = """
                        (function() {
                            try {
                                var el = document.getElementById('__mb_dark_theme__');
                                if (el) el.remove();
                            } catch(e) {}
                        })();
                    """.trimIndent()
                    webView.evaluateJavascript(script, null)
                }
            } catch (_: Throwable) {}
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
    webView: WebView,
    onClearAllData: () -> Unit,
    onClearCacheAndCookies: () -> Unit,
    onToggleDesktopMode: (Boolean) -> Unit,
    onSslProceed: (String) -> Unit = {},
    onSslCancel: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val colors = com.muslim.browser.pro.ui.theme.LocalAppColors.current

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
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    viewModel.goHome()
                }
            }
            else -> {
                // Let system exit
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                canGoBack = uiState.canGoBack && !uiState.isHomePage,
                canGoForward = uiState.canGoForward && !uiState.isHomePage,
                isDesktopModeEnabled = uiState.isDesktopModeEnabled,
                onGoBack = {
                    if (webView.canGoBack()) webView.goBack() else viewModel.goHome()
                },
                onGoForward = {
                    if (webView.canGoForward()) webView.goForward()
                },
                onNewTab = {
                    val currentTab = viewModel.uiState.value.tabs.find { it.id == viewModel.uiState.value.currentTabId }
                    if (currentTab != null && !currentTab.isHomePage) {
                        val bundle = Bundle()
                        webView.saveState(bundle)
                        viewModel.saveCurrentTabState(bundle)
                    }
                    viewModel.openNewTab()
                    MainActivity.applyDesktopModeToWebView(webView, viewModel.uiState.value.isDesktopModeEnabled)
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
            // 1. BrowserWebView is persistently kept in the Box layout so the WebView instance
            // remains warm and attached to the window, preventing teardown, re-attaching, and blank flashes.
            BrowserWebView(
                uiState = uiState,
                webView = webView,
                onUrlSubmit = { url ->
                    val success = viewModel.submitQueryOrUrl(url)
                    if (success) {
                        MainActivity.applyDesktopModeToWebView(webView, viewModel.uiState.value.isDesktopModeEnabled, viewModel.uiState.value.currentUrl)
                        webView.loadUrl(viewModel.uiState.value.currentUrl)
                    }
                },
                onReload = {
                    MainActivity.applyDesktopModeToWebView(webView, viewModel.uiState.value.isDesktopModeEnabled, viewModel.uiState.value.currentUrl)
                    webView.reload()
                },
                modifier = Modifier.fillMaxSize()
            )

            // 2. HomePage overlay when on home page and not blocked
            if (uiState.isHomePage && uiState.blockedInfo == null) {
                HomePage(
                    uiState = uiState,
                    favoriteSites = uiState.favoriteSites,
                    onQueryChange = { viewModel.onSearchInputChange(it) },
                    onSubmitQuery = { query ->
                        val success = viewModel.submitQueryOrUrl(query)
                        if (success) {
                            MainActivity.applyDesktopModeToWebView(webView, viewModel.uiState.value.isDesktopModeEnabled, viewModel.uiState.value.currentUrl)
                            webView.loadUrl(viewModel.uiState.value.currentUrl)
                        }
                    },
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
                        if (webView.canGoBack()) {
                            webView.goBack()
                            viewModel.onPageStarted(webView.url ?: "")
                        } else {
                            viewModel.goHome()
                        }
                    },
                    canGoBack = webView.canGoBack(),
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
                                webView.saveState(bundle)
                                viewModel.saveCurrentTabState(bundle)
                            }
                            val targetTab = viewModel.uiState.value.tabs.find { it.id == selectedTabId }
                            viewModel.selectTab(selectedTabId)
                            if (targetTab != null) {
                                if (targetTab.isHomePage) {
                                    // Composable HomePage will be displayed
                                } else {
                                    MainActivity.applyDesktopModeToWebView(webView, viewModel.uiState.value.isDesktopModeEnabled, targetTab.url)
                                    if (targetTab.bundle != null) {
                                        webView.restoreState(targetTab.bundle)
                                    } else if (targetTab.url.isNotEmpty()) {
                                        webView.loadUrl(targetTab.url)
                                    }
                                }
                            }
                        } else {
                            viewModel.closeTabsDialog()
                        }
                    },
                    onCloseTab = { tabId ->
                        val wasActive = (tabId == viewModel.uiState.value.currentTabId)
                        viewModel.closeTab(tabId)
                        if (wasActive) {
                            val newActive = viewModel.uiState.value.tabs.find { it.id == viewModel.uiState.value.currentTabId }
                            if (newActive != null && !newActive.isHomePage) {
                                MainActivity.applyDesktopModeToWebView(webView, viewModel.uiState.value.isDesktopModeEnabled, newActive.url)
                                if (newActive.bundle != null) {
                                    webView.restoreState(newActive.bundle)
                                } else if (newActive.url.isNotEmpty()) {
                                    webView.loadUrl(newActive.url)
                                }
                            }
                        }
                    },
                    onNewTab = {
                        val currentTab = viewModel.uiState.value.tabs.find { it.id == viewModel.uiState.value.currentTabId }
                        if (currentTab != null && !currentTab.isHomePage) {
                            val bundle = Bundle()
                            webView.saveState(bundle)
                            viewModel.saveCurrentTabState(bundle)
                        }
                        viewModel.openNewTab()
                        MainActivity.applyDesktopModeToWebView(webView, viewModel.uiState.value.isDesktopModeEnabled)
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
                    onTranslateToBengali = {
                        viewModel.translateCurrentPage(
                            evaluateJs = { script, cb -> webView.evaluateJavascript(script, cb) },
                            reloadPage = {
                                MainActivity.applyDesktopModeToWebView(webView, viewModel.uiState.value.isDesktopModeEnabled, viewModel.uiState.value.currentUrl)
                                webView.reload()
                            }
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
                    webView = webView,
                    isDesktopModeEnabled = uiState.isDesktopModeEnabled,
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
                        val success = viewModel.submitQueryOrUrl(url)
                        if (success) {
                            MainActivity.applyDesktopModeToWebView(webView, viewModel.uiState.value.isDesktopModeEnabled, viewModel.uiState.value.currentUrl)
                            webView.loadUrl(viewModel.uiState.value.currentUrl)
                        }
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
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "The security certificate for \"${sslState.host}\" cannot be fully verified because the server did not provide its intermediate certificate chain.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Host: ${sslState.host}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (sslState.details.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = sslState.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Do you want to proceed to this website anyway?",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { onSslProceed(sslState.host) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Proceed")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { onSslCancel(sslState.host) }
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
