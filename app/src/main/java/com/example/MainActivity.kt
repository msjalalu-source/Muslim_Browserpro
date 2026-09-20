package com.example

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.browser.BrowserViewModel
import com.example.browser.ProtectionEngine
import com.example.browser.ui.BlockedScreen
import com.example.browser.ui.BottomNavBar
import com.example.browser.ui.BrowserMenuSheet
import com.example.browser.ui.BrowserWebView
import com.example.browser.ui.HomePage
import com.example.ui.theme.MyApplicationTheme
import java.io.ByteArrayInputStream

class MainActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModels()
    private var webViewInstance: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create single WebView instance with optimized memory settings
        val webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                setSupportMultipleWindows(true)
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                if (viewModel.uiState.value.isDesktopModeEnabled) {
                    userAgentString = DESKTOP_USER_AGENT
                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    val isBlocked = viewModel.checkAndFilterUrl(url)
                    if (isBlocked) {
                        return true // Block navigation
                    }
                    return false
                }

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    val url = request?.url?.toString() ?: return null
                    if (viewModel.uiState.value.isAdBlockingEnabled && ProtectionEngine.isAdRequest(url)) {
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

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let {
                        viewModel.onPageFinished(
                            url = it,
                            title = view?.title,
                            canBack = view?.canGoBack() ?: false,
                            canForward = view?.canGoForward() ?: false
                        )
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
                                setDescription("Downloading with Focus Shield...")
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

    private fun clearAllData() {
        val webView = webViewInstance ?: return
        try {
            // 1. Clear browsing history
            webView.clearHistory()
            viewModel.onHistoryCleared()

            // 2. Clear cache
            webView.clearCache(true)

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

    override fun onDestroy() {
        webViewInstance?.apply {
            stopLoading()
            loadUrl("about:blank")
            clearHistory()
            removeAllViews()
            destroy()
        }
        webViewInstance = null
        super.onDestroy()
    }

    companion object {
        const val DESKTOP_USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
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
                    viewModel.openNewTab()
                    webView.loadUrl("about:blank")
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
            when {
                uiState.blockedInfo != null -> {
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
                        canGoBack = webView.canGoBack()
                    )
                }

                uiState.isHomePage -> {
                    HomePage(
                        uiState = uiState,
                        favoriteSites = viewModel.favoriteSites,
                        onQueryChange = { viewModel.onSearchInputChange(it) },
                        onSubmitQuery = { query ->
                            val success = viewModel.submitQueryOrUrl(query)
                            if (success) {
                                webView.loadUrl(viewModel.uiState.value.currentUrl)
                            }
                        },
                        onSelectCategory = { viewModel.selectCategory(it) }
                    )
                }

                else -> {
                    BrowserWebView(
                        uiState = uiState,
                        webView = webView,
                        onUrlSubmit = { url ->
                            val success = viewModel.submitQueryOrUrl(url)
                            if (success) {
                                webView.loadUrl(viewModel.uiState.value.currentUrl)
                            }
                        },
                        onReload = { webView.reload() }
                    )
                }
            }

            // Three-line Menu Sheet
            if (uiState.isMenuOpen) {
                BrowserMenuSheet(
                    uiState = uiState,
                    onDismiss = { viewModel.closeMenu() },
                    onAddKeyword = { kw -> viewModel.addCustomKeyword(kw) },
                    onTogglePopupBlocking = { enabled -> viewModel.togglePopupBlocking(enabled) },
                    onToggleAdBlocking = { enabled -> viewModel.toggleAdBlocking(enabled) },
                    onClearAllData = onClearAllData,
                    onClearCacheAndCookies = onClearCacheAndCookies,
                    onToggleDesktopMode = onToggleDesktopMode
                )
            }
        }
    }
}
