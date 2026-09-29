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
import android.webkit.CookieManager
import android.webkit.MimeTypeMap
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.DownloadEntry
import com.muslim.browser.pro.browser.DownloadPolicy
import com.muslim.browser.pro.browser.DownloadStatus
import com.muslim.browser.pro.browser.FaviconManager
import com.muslim.browser.pro.browser.ProtectionEngine
import com.muslim.browser.pro.browser.ui.BlockedScreen
import com.muslim.browser.pro.browser.ui.BottomNavBar
import com.muslim.browser.pro.browser.ui.BrowserMenuSheet
import com.muslim.browser.pro.browser.ui.BrowserWebView
import com.muslim.browser.pro.browser.ui.DownloadHistoryScreen
import com.muslim.browser.pro.browser.ui.HomePage
import com.muslim.browser.pro.browser.ui.HistoryScreen
import com.muslim.browser.pro.browser.ui.OpenWindowsDialog
import com.muslim.browser.pro.ui.theme.MyApplicationTheme
import java.io.ByteArrayInputStream
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModels()
    private var webViewInstance: WebView? = null
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val callback = fileChooserCallback
        fileChooserCallback = null
        if (callback == null) return@registerForActivityResult

        val uris = parseFileChooserResult(result.resultCode, result.data, contentResolver)
        callback.onReceiveValue(uris)
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
                    val bytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    val totalBytes = if (bytesIndex != -1) cursor.getLong(bytesIndex) else -1L

                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            viewModel.updateDownloadStatus(
                                downloadId = downloadId,
                                status = DownloadStatus.COMPLETED,
                                localUri = localUri,
                                totalBytes = totalBytes
                            )
                        }
                        DownloadManager.STATUS_FAILED -> {
                            viewModel.updateDownloadStatus(
                                downloadId = downloadId,
                                status = DownloadStatus.FAILED,
                                localUri = localUri,
                                totalBytes = totalBytes
                            )
                        }
                    }
                }
            }
        } catch (_: Exception) {}
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

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    android.util.Log.d("DIAGNOSTIC", "shouldOverrideUrlLoading: URL=$url")
                    if (view != null) {
                        applyDesktopModeToWebView(view, viewModel.uiState.value.isDesktopModeEnabled, url)
                    }
                    return handleUrlNavigation(view, url)
                }

                @Deprecated("Deprecated in Java", ReplaceWith("shouldOverrideUrlLoading(view, request)"))
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url == null) return false
                    android.util.Log.d("DIAGNOSTIC", "shouldOverrideUrlLoading(String): URL=$url")
                    if (view != null) {
                        applyDesktopModeToWebView(view, viewModel.uiState.value.isDesktopModeEnabled, url)
                    }
                    return handleUrlNavigation(view, url)
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
                        applyDesktopModeToWebView(view, viewModel.uiState.value.isDesktopModeEnabled, url)
                    }
                    url?.let { viewModel.onPageStarted(it) }
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                    applyWebPageDarkTheme(view, isDarkThemeActive)
                    viewModel.onPageCommitVisible()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    android.util.Log.d("DIAGNOSTIC", "onPageFinished: URL=$url")
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
                    android.util.Log.e("DIAGNOSTIC", "onReceivedHttpError: URL=${request?.url}, statusCode=${errorResponse?.statusCode}, reason=${errorResponse?.reasonPhrase}")
                    if (request?.isForMainFrame == true) {
                        viewModel.onPageCommitVisible()
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
                    // Pop-up Blocking
                    if (viewModel.uiState.value.isPopupBlockingEnabled) {
                        return false // Blocks pop-up
                    }
                    if (resultMsg != null) {
                        val transport = resultMsg.obj as? WebView.WebViewTransport
                        transport?.webView = view
                        if (view != null) {
                            applyDesktopModeToWebView(view, viewModel.uiState.value.isDesktopModeEnabled, view.url)
                        }
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
                val decision = DownloadPolicy.evaluate(url, mimetype, contentDisposition)
                when (decision) {
                    is DownloadPolicy.Result.Blocked -> {
                        viewModel.showToast(decision.reason)
                        viewModel.onPageCommitVisible()
                    }
                    is DownloadPolicy.Result.Allowed -> {
                        try {
                            val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
                            val request = DownloadManager.Request(Uri.parse(url)).apply {
                                if (!mimetype.isNullOrBlank()) {
                                    setMimeType(mimetype)
                                }
                                if (!userAgent.isNullOrBlank()) {
                                    addRequestHeader("User-Agent", userAgent)
                                }
                                setTitle(fileName)
                                setDescription("Downloading with Muslim Browser Pro...")
                                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                            }
                            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                            val downloadId = dm?.enqueue(request) ?: -1L
                            val entry = DownloadEntry(
                                downloadId = downloadId,
                                fileName = fileName,
                                url = url,
                                mimeType = mimetype ?: "",
                                timestamp = System.currentTimeMillis(),
                                status = DownloadStatus.DOWNLOADING
                            )
                            viewModel.recordDownload(entry)
                            viewModel.showToast("Downloading $fileName...")
                        } catch (e: Exception) {
                            viewModel.showToast("Download started.")
                        }
                        viewModel.onPageCommitVisible()
                    }
                }
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
                    onToggleDesktopMode = { enabled -> setDesktopMode(enabled) }
                )
            }
        }
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

        // 2. Direct URL navigation check
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
        viewModel.toggleDesktopMode(enabled)
        val webView = webViewInstance ?: return
        applyDesktopModeToWebView(webView, enabled, viewModel.uiState.value.currentUrl)
        if (!viewModel.uiState.value.isHomePage && viewModel.uiState.value.currentUrl.isNotEmpty()) {
            webView.reload()
        }
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

    companion object {
        const val DESKTOP_USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

        @Volatile
        var isDarkThemeActive: Boolean = true

        /**
         * Checks whether a given URL targets Google account authentication endpoints.
         * Used to ensure standards-compliant authentication without user-agent spoofing.
         */
        fun isGoogleAuthUrl(url: String?): Boolean {
            if (url.isNullOrBlank()) return false
            return try {
                val uri = Uri.parse(url)
                val host = uri.host?.lowercase(Locale.ROOT) ?: return false
                val path = uri.path?.lowercase(Locale.ROOT) ?: ""

                // 1. Dedicated Google auth and account management hosts
                if (host == "accounts.google.com" ||
                    host.endsWith(".accounts.google.com") ||
                    host == "accounts.youtube.com" ||
                    host == "myaccount.google.com" ||
                    host == "oauth2.googleapis.com"
                ) {
                    return true
                }

                // 2. Google / YouTube / Gmail login and authentication endpoints
                val isGoogleDomain = host == "google.com" || host.endsWith(".google.com") ||
                        host == "youtube.com" || host.endsWith(".youtube.com") ||
                        host == "gmail.com" || host.endsWith(".gmail.com")

                if (isGoogleDomain) {
                    if (path.startsWith("/servicelogin") ||
                        path.startsWith("/signin") ||
                        path.startsWith("/signup") ||
                        path.startsWith("/o/oauth2") ||
                        path.contains("/signin/") ||
                        (uri.getQueryParameter("service") != null && path.contains("login"))
                    ) {
                        return true
                    }
                }

                // 3. Direct Gmail entry points that redirect into Google authentication
                if (host == "mail.google.com" || host == "gmail.com") {
                    if (path.isEmpty() || path == "/" || path.contains("signin") || path.contains("login")) {
                        return true
                    }
                }

                false
            } catch (_: Exception) {
                false
            }
        }

        /**
         * Centralized function to configure Desktop Mode on any WebView instance.
         * Enforces browser-level Desktop Mode preference across all creations, navigations,
         * tab restorations, and window transfers.
         *
         * When Desktop Mode is ON:
         * - Applies DESKTOP_USER_AGENT to all standard browsing and search pages.
         * - Uses standard supported mobile configuration on Google account authentication
         *   endpoints (accounts.google.com) to comply with Google security policies and prevent
         *   unsupported browser / insecure app security warnings.
         * - Sets useWideViewPort = true and loadWithOverviewMode = true.
         *
         * When Desktop Mode is OFF:
         * - Reverts userAgentString to null (system default mobile UA).
         */
        fun applyDesktopModeToWebView(
            webView: WebView,
            enabled: Boolean,
            url: String? = null
        ) {
            try {
                val isGoogleAuth = enabled && isGoogleAuthUrl(url)
                val targetUserAgent = if (enabled && !isGoogleAuth) {
                    DESKTOP_USER_AGENT
                } else {
                    null
                }

                if (targetUserAgent == null) {
                    if (webView.settings.userAgentString == DESKTOP_USER_AGENT) {
                        webView.settings.userAgentString = null
                    }
                } else {
                    if (webView.settings.userAgentString != targetUserAgent) {
                        webView.settings.userAgentString = targetUserAgent
                    }
                }
                webView.settings.useWideViewPort = true
                webView.settings.loadWithOverviewMode = true
            } catch (_: Exception) {}
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
    onToggleDesktopMode: (Boolean) -> Unit
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
                    onSelectTranslationEngine = { engine -> viewModel.selectTranslationEngine(engine) }
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
        }
    }
}

/**
 * Safely opens a completed downloaded file with the appropriate application using ACTION_VIEW.
 * Handles DownloadManager content URIs, FileProvider content URIs, and mime-type detection.
 */
private fun openDownloadedFile(context: Context, entry: DownloadEntry) {
    try {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        var uri: Uri? = null

        // 1. Try DownloadManager content URI
        if (entry.downloadId != -1L) {
            try {
                uri = dm?.getUriForDownloadedFile(entry.downloadId)
            } catch (_: Exception) {}
        }

        // 2. Try parsing localUri
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

        // 3. Try finding in Public Downloads directory
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
            Toast.makeText(context, "File not found.", Toast.LENGTH_SHORT).show()
            return
        }

        val effectiveMime = if (entry.mimeType.isNotBlank() && entry.mimeType != "*/*") {
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
        Toast.makeText(context, "No app available to open ${entry.fileName}", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
