package com.muslim.browser.pro

import android.app.Application
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.BrowserUiState
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.DesktopArchitecture
import com.muslim.browser.pro.browser.DesktopCore
import com.muslim.browser.pro.browser.SettingsRepository
import com.muslim.browser.pro.browser.TabWebViewManager
import com.muslim.browser.pro.browser.WebViewConfigurator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DesktopCoreConsolidationTest {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.desktopArchitecture = DesktopArchitecture.NONE
    }

    @Test
    fun test1_desktopCoreAppliesCommonDesktopWebViewSettings() {
        val webView = WebView(context)
        // Reset settings
        webView.settings.useWideViewPort = false
        webView.settings.loadWithOverviewMode = false
        webView.settings.textZoom = 80
        webView.settings.builtInZoomControls = false
        webView.settings.displayZoomControls = true

        DesktopCore.applyCommonDesktopWebViewSettings(webView)

        assertTrue("useWideViewPort must be true", webView.settings.useWideViewPort)
        assertTrue("loadWithOverviewMode must be true", webView.settings.loadWithOverviewMode)
        assertEquals("textZoom must be 100", 100, webView.settings.textZoom)
        assertTrue("builtInZoomControls must be true", webView.settings.builtInZoomControls)
        assertFalse("displayZoomControls must be false", webView.settings.displayZoomControls)
    }

    @Test
    fun test2_standardAndWindows10TouchShareCommonDesktopCoreSettings() {
        val webViewStandard = WebView(context)
        val webViewWin10 = WebView(context)

        DesktopCore.synchronizeDesktopWebView(webViewStandard, "https://example.com", DesktopArchitecture.STANDARD)
        DesktopCore.synchronizeDesktopWebView(webViewWin10, "https://example.com", DesktopArchitecture.WINDOWS_10_TOUCH)

        // Both must have the identical shared desktop core native configuration
        assertTrue(webViewStandard.settings.useWideViewPort)
        assertTrue(webViewWin10.settings.useWideViewPort)
        assertTrue(webViewStandard.settings.loadWithOverviewMode)
        assertTrue(webViewWin10.settings.loadWithOverviewMode)
        assertEquals(100, webViewStandard.settings.textZoom)
        assertEquals(100, webViewWin10.settings.textZoom)
        assertTrue(webViewStandard.settings.builtInZoomControls)
        assertTrue(webViewWin10.settings.builtInZoomControls)
        assertFalse(webViewStandard.settings.displayZoomControls)
        assertFalse(webViewWin10.settings.displayZoomControls)

        // But distinct User-Agents
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webViewStandard.settings.userAgentString)
        assertEquals(WebViewConfigurator.WINDOWS_10_TOUCH_USER_AGENT, webViewWin10.settings.userAgentString)
    }

    @Test
    fun test3_authUrlRevertsToMobileUserAgentUnderBothProfiles() {
        val webViewStandard = WebView(context)
        val webViewWin10 = WebView(context)
        val defaultUa = webViewStandard.settings.userAgentString

        val authUrl = "https://accounts.google.com/signin/v2/identifier"
        DesktopCore.synchronizeDesktopWebView(webViewStandard, authUrl, DesktopArchitecture.STANDARD)
        DesktopCore.synchronizeDesktopWebView(webViewWin10, authUrl, DesktopArchitecture.WINDOWS_10_TOUCH)

        assertEquals("Standard profile must use mobile UA on auth endpoint", defaultUa, webViewStandard.settings.userAgentString)
        assertEquals("Windows 10 Touch profile must use mobile UA on auth endpoint", defaultUa, webViewWin10.settings.userAgentString)
        assertFalse(webViewStandard.settings.userAgentString.contains("Windows NT"))
        assertFalse(webViewWin10.settings.userAgentString.contains("Windows NT"))
    }

    @Test
    fun test4_singleSourceOfTruthDerivedUiStateProperties() {
        val standardState = BrowserUiState(desktopArchitecture = DesktopArchitecture.STANDARD)
        assertTrue("STANDARD state must have isDesktopModeEnabled = true", standardState.isDesktopModeEnabled)
        assertFalse("STANDARD state must have isWindows10TouchEnabled = false", standardState.isWindows10TouchEnabled)
        assertFalse(standardState.isDesktopMode4Enabled)

        val win10State = BrowserUiState(desktopArchitecture = DesktopArchitecture.WINDOWS_10_TOUCH)
        assertFalse("WINDOWS_10_TOUCH state must have isDesktopModeEnabled = false", win10State.isDesktopModeEnabled)
        assertTrue("WINDOWS_10_TOUCH state must have isWindows10TouchEnabled = true", win10State.isWindows10TouchEnabled)
        assertFalse(win10State.isDesktopMode4Enabled)

        val mobileState = BrowserUiState(desktopArchitecture = DesktopArchitecture.NONE)
        assertFalse(mobileState.isDesktopModeEnabled)
        assertFalse(mobileState.isWindows10TouchEnabled)
    }

    @Test
    fun test5_viewModelSelectDesktopArchitectureAtomicallyUpdatesDerivedState() {
        val viewModel = BrowserViewModel(context)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)

        // Select STANDARD
        viewModel.selectDesktopArchitecture(DesktopArchitecture.STANDARD)
        assertEquals(DesktopArchitecture.STANDARD, viewModel.uiState.value.desktopArchitecture)
        assertTrue(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)

        // Select WINDOWS_10_TOUCH
        viewModel.selectDesktopArchitecture(DesktopArchitecture.WINDOWS_10_TOUCH)
        assertEquals(DesktopArchitecture.WINDOWS_10_TOUCH, viewModel.uiState.value.desktopArchitecture)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        assertTrue(viewModel.uiState.value.isWindows10TouchEnabled)

        // Select NONE
        viewModel.selectDesktopArchitecture(DesktopArchitecture.NONE)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)
    }

    @Test
    fun test6_tabWebViewManagerHasLiveWebView() {
        val webView = WebView(context)
        val tabManager = TabWebViewManager(
            context = context,
            maxLiveWebViews = 2,
            webViewFactory = { webView }
        )

        assertFalse(tabManager.hasLiveWebView(webView))

        tabManager.getOrCreateWebView("tab_1")
        assertTrue(tabManager.hasLiveWebView(webView))

        val otherWebView = WebView(context)
        assertFalse(tabManager.hasLiveWebView(otherWebView))
    }

    @Test
    fun test7_desktopCoreHandlePageLifecycleExecution() {
        val webView = WebView(context)

        // Lifecycle calls should execute deterministically without throwing exceptions
        DesktopCore.handlePageLifecycle(webView, DesktopArchitecture.STANDARD, "https://example.com")
        DesktopCore.handlePageLifecycle(webView, DesktopArchitecture.WINDOWS_10_TOUCH, "https://example.com")
        DesktopCore.handlePageLifecycle(webView, DesktopArchitecture.NONE, "https://example.com")
    }

    @Test
    fun test8_switchingBetweenStandardAndWindows10Touch() {
        val webView = WebView(context)

        // 1. Switch to STANDARD
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.STANDARD)
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertTrue(webView.settings.useWideViewPort)

        // 2. Switch to WINDOWS_10_TOUCH
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.WINDOWS_10_TOUCH)
        assertEquals(WebViewConfigurator.WINDOWS_10_TOUCH_USER_AGENT, webView.settings.userAgentString)
        assertTrue(webView.settings.useWideViewPort)

        // 3. Switch to NONE
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.NONE)
        assertFalse(webView.settings.userAgentString.contains("Windows NT"))
        assertFalse(webView.settings.userAgentString.contains("X11; Linux x86_64"))
    }
}
