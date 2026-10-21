package com.muslim.browser.pro

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.DesktopCore
import com.muslim.browser.pro.browser.NavigationController
import com.muslim.browser.pro.browser.ProtectionEngine
import com.muslim.browser.pro.browser.SslSecurityPolicy
import com.muslim.browser.pro.browser.TabWebViewManager
import com.muslim.browser.pro.browser.ui.BottomNavBar
import com.muslim.browser.pro.browser.ui.BrowserMenuSheet
import com.muslim.browser.pro.browser.ui.BrowserWebView
import com.muslim.browser.pro.browser.ui.HomePage
import com.muslim.browser.pro.browser.ui.TabsSelectionDialog
import com.muslim.browser.pro.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    val viewModel: BrowserViewModel by viewModels()
    lateinit var tabWebViewManager: TabWebViewManager

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

        tabWebViewManager = TabWebViewManager(
            context = this,
            webViewFactory = { tabId -> createConfiguredWebView(tabId) },
            onSyncDesktopMode = { webView, isDesktop ->
                DesktopCore.applyCommonDesktopWebViewSettings(webView)
            },
            onSyncArchitecture = { webView, arch ->
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
                                viewModel.toggleDesktopMode(!uiState.desktopArchitecture.isAnyDesktop())
                            },
                            onOpenMenu = { viewModel.openMenu() }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
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
                                onDismiss = { viewModel.closeMenu() },
                                onOpenHistory = { viewModel.openHistory() },
                                onOpenDownloads = { viewModel.openDownloads() },
                                onAddKeyword = { keyword -> viewModel.addCustomKeyword(keyword) },
                                onTogglePopupBlocking = { enabled -> viewModel.togglePopupBlocking(enabled) },
                                onToggleAdBlocking = { enabled -> viewModel.toggleAdBlocking(enabled) },
                                onClearAllData = {
                                    tabWebViewManager.clearAllData()
                                    viewModel.clearAllHistory()
                                },
                                onClearCacheAndCookies = { tabWebViewManager.clearAllData() },
                                onToggleDesktopMode = { enabled -> viewModel.toggleDesktopMode(enabled) },
                                onToggleDesktopMode4 = { enabled -> viewModel.toggleDesktopMode4(enabled) },
                                onToggleWindows10Touch = { enabled -> viewModel.toggleWindows10Touch(enabled) },
                                onTranslateToBengali = {
                                    viewModel.translateCurrentPage(
                                        evalScript = { script, cb ->
                                            currentWebView.evaluateJavascript(script) { res -> cb(res) }
                                        }
                                    )
                                },
                                onSelectTheme = { theme -> viewModel.setAppTheme(theme) },
                                onSelectTranslationEngine = { engine -> viewModel.selectTranslationEngine(engine) }
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
                    }
                }
            }
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

            DesktopCore.applyCommonDesktopWebViewSettings(this)

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val targetUrl = request?.url?.toString() ?: return false
                    val decision = NavigationController.evaluate(
                        url = targetUrl,
                        customKeywords = viewModel.uiState.value.customKeywords,
                        searchEngine = viewModel.uiState.value.selectedSearchEngine
                    )
                    if (decision is com.muslim.browser.pro.browser.NavigationDecision.Blocked) {
                        viewModel.setBlockedUrl(targetUrl, decision.reason, decision.detail)
                        return true
                    }
                    if (decision is com.muslim.browser.pro.browser.NavigationDecision.Redirect) {
                        view?.loadUrl(decision.url)
                        return true
                    }
                    if (ProtectionEngine.isHarmfulUrl(targetUrl)) {
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

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
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
