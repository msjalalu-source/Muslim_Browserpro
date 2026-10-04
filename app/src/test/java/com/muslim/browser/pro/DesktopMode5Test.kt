package com.muslim.browser.pro

import android.app.Application
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.DesktopArchitecture
import com.muslim.browser.pro.browser.SettingsRepository
import com.muslim.browser.pro.browser.TabWebViewManager
import com.muslim.browser.pro.browser.WebViewConfigurator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit test suite for Desktop Mode 5 (Lifecycle Hybrid).
 *
 * Architecture Characteristics:
 * - Desktop User-Agent: Linux x86_64 Chrome
 * - Native WebSettings: useWideViewPort = true, loadWithOverviewMode = true, textZoom = 100
 * - Native lifecycle-driven enforcement: WebViewClient onPageFinished / onPageCommitVisible
 * - Retained SPA navigation event listeners: turbo:load, turbo:render, pjax:end, pageshow, popstate
 * - Strictly ONE-SHOT fallback stabilization observer: disconnects immediately once viewport is enforced
 * - NO permanent MutationObserver (zero continuous DOM tree observation)
 * - NO setInterval, NO polling loops, NO continuous DOM scanning
 * - NO history.pushState / history.replaceState monkey-patching
 * - Retained UA Client Hints spoofing: mobile: false, platform: 'Linux'
 * - Mutual Exclusion: strictly mutually exclusive with Standard, Mode 1, Mode 4, Windows 10 Touch
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DesktopMode5Test {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.desktopArchitecture = DesktopArchitecture.NONE
    }

    @Test
    fun test1_desktopMode5PersistenceInRepository() {
        assertFalse(repository.isDesktopMode5Enabled)
        assertEquals(DesktopArchitecture.NONE, repository.desktopArchitecture)

        // Enable Mode 5
        repository.isDesktopMode5Enabled = true
        assertTrue(repository.isDesktopMode5Enabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_5, repository.desktopArchitecture)

        // Disable Mode 5
        repository.isDesktopMode5Enabled = false
        assertFalse(repository.isDesktopMode5Enabled)
        assertEquals(DesktopArchitecture.NONE, repository.desktopArchitecture)
    }

    @Test
    fun test2_browserViewModelDesktopMode5ToggleAndMutualExclusion() {
        val viewModel = BrowserViewModel(context)
        assertFalse(viewModel.uiState.value.isDesktopMode5Enabled)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)

        // Toggle Mode 5 ON
        viewModel.toggleDesktopMode5(true)
        assertTrue(viewModel.uiState.value.isDesktopMode5Enabled)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode1Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode4Enabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_5, viewModel.uiState.value.desktopArchitecture)

        // Toggle Standard Desktop ON -> Mode 5 must turn OFF
        viewModel.toggleDesktopMode(true)
        assertTrue(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode5Enabled)
        assertEquals(DesktopArchitecture.STANDARD, viewModel.uiState.value.desktopArchitecture)

        // Toggle Mode 5 ON again -> Standard Desktop must turn OFF
        viewModel.toggleDesktopMode5(true)
        assertTrue(viewModel.uiState.value.isDesktopMode5Enabled)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_5, viewModel.uiState.value.desktopArchitecture)

        // Toggle Mode 1 ON -> Mode 5 must turn OFF
        viewModel.toggleDesktopMode1(true)
        assertTrue(viewModel.uiState.value.isDesktopMode1Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode5Enabled)

        // Toggle Mode 5 ON again -> Mode 1 must turn OFF
        viewModel.toggleDesktopMode5(true)
        assertTrue(viewModel.uiState.value.isDesktopMode5Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode1Enabled)

        // Toggle Mode 4 ON -> Mode 5 must turn OFF
        viewModel.toggleDesktopMode4(true)
        assertTrue(viewModel.uiState.value.isDesktopMode4Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode5Enabled)

        // Toggle Mode 5 ON again -> Mode 4 must turn OFF
        viewModel.toggleDesktopMode5(true)
        assertTrue(viewModel.uiState.value.isDesktopMode5Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode4Enabled)

        // Toggle Windows 10 Touch ON -> Mode 5 must turn OFF
        viewModel.toggleWindows10Touch(true)
        assertTrue(viewModel.uiState.value.isWindows10TouchEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode5Enabled)

        // Toggle Mode 5 ON again
        viewModel.toggleDesktopMode5(true)
        assertTrue(viewModel.uiState.value.isDesktopMode5Enabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)

        // Toggle Mode 5 OFF -> NONE
        viewModel.toggleDesktopMode5(false)
        assertFalse(viewModel.uiState.value.isDesktopMode5Enabled)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)
    }

    @Test
    fun test3_desktopMode5AppliesDesktopUserAgentAndNativeWebSettings() {
        val webView = WebView(context)
        val defaultMobileUa = webView.settings.userAgentString

        WebViewConfigurator.configureBaseSettings(
            webView = webView,
            isDarkTheme = false,
            desktopArchitecture = DesktopArchitecture.DESKTOP_MODE_5
        )

        // UA must be Desktop
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertFalse(webView.settings.userAgentString.contains("Mobile"))

        // Native desktop settings
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
        assertEquals(100, webView.settings.textZoom)
        assertTrue(webView.settings.builtInZoomControls)
        assertFalse(webView.settings.displayZoomControls)
        assertTrue(webView.settings.javaScriptEnabled)
        assertTrue(webView.settings.domStorageEnabled)

        // Turn OFF via syncDesktopArchitecture(NONE) -> restore mobile UA
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = null,
            architecture = DesktopArchitecture.NONE,
            updateUserAgent = true
        )
        assertEquals(defaultMobileUa, webView.settings.userAgentString)
    }

    @Test
    fun test4_desktopMode5ScriptStructureAndLifecycleHybridVerification() {
        val script = WebViewConfigurator.DESKTOP_MODE_5_HYBRID_SCRIPT

        // 1. Must use the unique guard key
        assertTrue("Mode 5 must use __mb_desktop_mode5__ key", script.contains("__mb_desktop_mode5__"))

        // 2. Must enforce width=1280
        assertTrue("Mode 5 must set width=1280", script.contains("width=1280"))

        // 3. Must use a strictly ONE-SHOT fallback observer that disconnects upon enforcement
        assertTrue("Mode 5 must contain fallback observer", script.contains("fallbackObserver"))
        assertTrue("Mode 5 must disconnect fallback immediately upon enforcement", script.contains("disconnectFallback()"))
        assertTrue("Mode 5 fallback observer must observe head with subtree: false", script.contains("subtree: false"))

        // 4. Must NOT have permanent head or document observers
        assertFalse("Mode 5 must NOT have permanent head observer", script.contains("var headObserver"))
        assertFalse("Mode 5 must NOT have permanent doc observer", script.contains("var docObserver"))
        assertFalse("Mode 5 must NOT have subtree: true", script.contains("subtree: true"))

        // 5. Must NOT use setInterval or setTimeout polling
        assertFalse("Mode 5 must NOT use setInterval", script.contains("setInterval"))

        // 6. Must retain navigation event listeners for SPA/PJAX
        assertTrue("Mode 5 must listen to turbo:load", script.contains("turbo:load"))
        assertTrue("Mode 5 must listen to turbo:render", script.contains("turbo:render"))
        assertTrue("Mode 5 must listen to pjax:end", script.contains("pjax:end"))
        assertTrue("Mode 5 must listen to pageshow", script.contains("pageshow"))
        assertTrue("Mode 5 must listen to popstate", script.contains("popstate"))

        // 7. Must NOT monkey-patch history.pushState or replaceState
        assertFalse("Mode 5 MUST NOT modify history.pushState", script.contains("history.pushState ="))
        assertFalse("Mode 5 MUST NOT modify history.replaceState", script.contains("history.replaceState ="))

        // 8. Must retain desktop client hints override
        assertTrue("Mode 5 must spoof mobile: false", script.contains("mobile: false"))
        assertTrue("Mode 5 must spoof platform: 'Linux'", script.contains("platform: 'Linux'"))

        // 9. Cleanup must be defined and disconnect fallback observer
        assertTrue("Mode 5 must include cleanup function", script.contains("cleanup: function()"))
        assertTrue("Mode 5 cleanup must call disconnectFallback", script.contains("disconnectFallback()"))
        assertTrue("Mode 5 cleanup must restore original viewport", script.contains("data-mb5-orig"))
    }

    @Test
    fun test5_desktopMode5CleanupInUnifiedCleanupScript() {
        val cleanupScript = WebViewConfigurator.CLEANUP_ALL_DESKTOP_SCRIPTS
        assertTrue("Unified cleanup must check __mb_desktop_mode5__", cleanupScript.contains("__mb_desktop_mode5__"))
        assertTrue("Unified cleanup must clean data-mb5-created", cleanupScript.contains("data-mb5-created"))
        assertTrue("Unified cleanup must clean data-mb5-orig", cleanupScript.contains("data-mb5-orig"))
    }

    @Test
    fun test6_desktopMode5PreservesMobileUaOnAuthenticationUrls() {
        val webView = WebView(context)
        val defaultMobileUa = webView.settings.userAgentString

        // Normal page -> Desktop UA
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://github.com/torvalds/linux",
            architecture = DesktopArchitecture.DESKTOP_MODE_5,
            updateUserAgent = true
        )
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Authentication page -> Mobile UA
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://accounts.google.com/signin/v2",
            architecture = DesktopArchitecture.DESKTOP_MODE_5,
            updateUserAgent = true
        )
        assertEquals(defaultMobileUa, webView.settings.userAgentString)

        // Return to normal page -> Desktop UA restored
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://www.example.com",
            architecture = DesktopArchitecture.DESKTOP_MODE_5,
            updateUserAgent = true
        )
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test7_tabWebViewManagerSynchronizesDesktopMode5() {
        var activeArchitecture = DesktopArchitecture.DESKTOP_MODE_5
        var syncCount = 0

        val tabManager = TabWebViewManager(
            context = context,
            webViewFactory = { _ ->
                WebView(context).apply {
                    WebViewConfigurator.configureBaseSettings(
                        webView = this,
                        isDarkTheme = false,
                        desktopArchitecture = activeArchitecture
                    )
                }
            },
            onSyncArchitecture = { wv, arch ->
                syncCount++
                WebViewConfigurator.syncDesktopArchitecture(wv, wv.url, arch, true)
            },
            architectureProvider = { activeArchitecture }
        )

        // 1. Create tab -> Desktop Mode 5 UA applied
        val (wv1, _) = tabManager.getOrCreateWebView("tab1")
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, wv1.settings.userAgentString)
        assertTrue("onSyncArchitecture should have been called", syncCount >= 1)

        // 2. Fetch existing tab -> re-synchronizes
        val prevSync = syncCount
        tabManager.getOrCreateWebView("tab1")
        assertTrue("onSyncArchitecture should be called when accessing existing tab", syncCount > prevSync)

        // 3. Switch architecture to NONE
        activeArchitecture = DesktopArchitecture.NONE
        tabManager.syncAllLiveWebViewsArchitecture(DesktopArchitecture.NONE)
        assertFalse(wv1.settings.userAgentString.contains("Linux x86_64"))

        // 4. Switch back to Mode 5
        activeArchitecture = DesktopArchitecture.DESKTOP_MODE_5
        tabManager.syncAllLiveWebViewsArchitecture(DesktopArchitecture.DESKTOP_MODE_5)
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, wv1.settings.userAgentString)
    }
}
