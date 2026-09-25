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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.FaviconManager
import com.muslim.browser.pro.browser.ProtectionEngine
import com.muslim.browser.pro.browser.ui.BlockedScreen
import com.muslim.browser.pro.browser.ui.BottomNavBar
import com.muslim.browser.pro.browser.ui.BrowserMenuSheet
import com.muslim.browser.pro.browser.ui.BrowserWebView
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        // Create single WebView instance with optimized memory settings
        val webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            applyWebViewTheme(this, isDarkTheme)

            // Ensure cookies and third-party cookies are accepted for cross-origin assets (e.g. translation)
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
                if (viewModel.uiState.value.isDesktopModeEnabled) {
                    userAgentString = DESKTOP_USER_AGENT
                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    return handleUrlNavigation(view, url)
                }

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    val uri = request?.url ?: return null
                    // Never intercept or block Google Translate scripts, styles, or proxy chunks
                    val host = uri.host?.lowercase(Locale.ROOT)
                    if (host != null && (
                        ProtectionEngine.isTranslationHost(host) ||
                        host == "google.com" || host.endsWith(".google.com") ||
                        host == "gstatic.com" || host.endsWith(".gstatic.com") ||
                        host == "googleapis.com" || host.endsWith(".googleapis.com")
                    )) {
                        return null
                    }
                    if (viewModel.uiState.value.isAdBlockingEnabled && ProtectionEngine.isAdRequest(uri)) {
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
                    url?.let { viewModel.onPageStarted(it) }
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                    applyWebPageDarkTheme(view, isDarkThemeActive)
                    viewModel.onPageCommitVisible()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
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
                val status = ProtectionEngine.checkDownloadType(url, mimetype, contentDisposition)
                when (status) {
                    ProtectionEngine.DownloadStatus.BLOCKED_VIDEO,
                    ProtectionEngine.DownloadStatus.BLOCKED_AUDIO,
                    ProtectionEngine.DownloadStatus.BLOCKED_APK,
                    ProtectionEngine.DownloadStatus.BLOCKED_OTHER -> {
                        viewModel.showToast("This file type is blocked.")
                    }
                    ProtectionEngine.DownloadStatus.ALLOWED_IMAGE,
                    ProtectionEngine.DownloadStatus.ALLOWED_PDF -> {
                        try {
                            val request = DownloadManager.Request(Uri.parse(url)).apply {
                                setMimeType(mimetype)
                                val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
                                setTitle(fileName)
                                setDescription("Downloading with Muslim Browser Pro...")
                                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                            }
                            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                            dm?.enqueue(request)
                            viewModel.showToast("Downloading file...")
                        } catch (e: Exception) {
                            viewModel.showToast("Download started.")
                        }
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
            MyApplicationTheme {
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
        // Translation URLs should not be intercepted as search engine requests
        if (ProtectionEngine.isTranslationUrl(url)) {
            val isBlocked = viewModel.checkAndFilterUrl(url)
            if (isBlocked) {
                return true // Block navigation if underlying content is blocked
            }
            return false // Allow WebView to proceed with translation
        }

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
        webView.settings.userAgentString = if (enabled) DESKTOP_USER_AGENT else null
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
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
                .background(Color(0xFF0F172A))
        ) {
            // 1. BrowserWebView is persistently kept in the Box layout so the WebView instance
            // remains warm and attached to the window, preventing teardown, re-attaching, and blank flashes.
            BrowserWebView(
                uiState = uiState,
                webView = webView,
                onUrlSubmit = { url ->
                    val success = viewModel.submitQueryOrUrl(url)
                    if (success) {
                        webView.loadUrl(viewModel.uiState.value.currentUrl)
                    }
                },
                onReload = { webView.reload() },
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
                    onAddKeyword = { kw -> viewModel.addCustomKeyword(kw) },
                    onTogglePopupBlocking = { enabled -> viewModel.togglePopupBlocking(enabled) },
                    onToggleAdBlocking = { enabled -> viewModel.toggleAdBlocking(enabled) },
                    onClearAllData = onClearAllData,
                    onClearCacheAndCookies = onClearCacheAndCookies,
                    onToggleDesktopMode = onToggleDesktopMode,
                    onTranslateToBangla = {
                        val target = viewModel.translateToBangla(webView.url)
                        if (target.isNotBlank()) {
                            webView.loadUrl(target)
                        }
                    }
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
                            webView.loadUrl(viewModel.uiState.value.currentUrl)
                        }
                    },
                    onDeleteEntry = { id -> viewModel.deleteHistoryEntry(id) },
                    onClearAll = { viewModel.clearAllHistory() },
                    onDismiss = { viewModel.closeHistory() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
