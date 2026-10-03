package com.muslim.browser.pro

import android.app.Application
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DesktopModeTest {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.isDesktopModeEnabled = false
    }

    @Test
    fun test1_desktopModePersistenceInRepository() {
        // Initial state is false
        assertFalse(repository.isDesktopModeEnabled)

        // Enable desktop mode
        repository.isDesktopModeEnabled = true
        assertTrue(repository.isDesktopModeEnabled)

        // Disable desktop mode
        repository.isDesktopModeEnabled = false
        assertFalse(repository.isDesktopModeEnabled)
    }

    @Test
    fun test2_browserViewModelDesktopModeToggle() {
        val viewModel = BrowserViewModel(context)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)

        // Toggle ON
        viewModel.toggleDesktopMode(true)
        assertTrue(viewModel.uiState.value.isDesktopModeEnabled)
        assertTrue(repository.isDesktopModeEnabled)

        // Toggle OFF
        viewModel.toggleDesktopMode(false)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(repository.isDesktopModeEnabled)
    }

    @Test
    fun test3_baseWebViewConfigurationDoesNotInjectObsoleteScripts() {
        val webView = WebView(context)
        MainActivity.configureBaseSettings(webView, isDarkTheme = false)

        assertTrue("JavaScript must be enabled", webView.settings.javaScriptEnabled)
        assertTrue("DOM storage must be enabled", webView.settings.domStorageEnabled)
        assertTrue("Wide viewport must be enabled", webView.settings.useWideViewPort)
        assertTrue("Load with overview mode must be enabled", webView.settings.loadWithOverviewMode)
    }

    @Test
    fun test4_multipleTogglesDoNotCorruptRepositoryState() {
        val viewModel = BrowserViewModel(context)
        for (i in 1..10) {
            viewModel.toggleDesktopMode(true)
            assertTrue("Cycle $i ON failed", viewModel.uiState.value.isDesktopModeEnabled)
            assertTrue("Cycle $i repo ON failed", repository.isDesktopModeEnabled)

            viewModel.toggleDesktopMode(false)
            assertFalse("Cycle $i OFF failed", viewModel.uiState.value.isDesktopModeEnabled)
            assertFalse("Cycle $i repo OFF failed", repository.isDesktopModeEnabled)
        }
    }

    @Test
    fun test5_applyDesktopModeTogglesUserAgentAndMaintainsViewport() {
        val webView = WebView(context)
        MainActivity.configureBaseSettings(webView, isDarkTheme = false, isDesktopEnabled = false)

        val defaultUa = webView.settings.userAgentString
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)

        // Enable Desktop Mode
        MainActivity.applyDesktopMode(webView, isDesktopEnabled = true)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertFalse(webView.settings.userAgentString.contains("Mobile"))
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
        assertEquals(100, webView.settings.textZoom)

        // Disable Desktop Mode
        MainActivity.applyDesktopMode(webView, isDesktopEnabled = false)
        assertEquals(defaultUa, webView.settings.userAgentString)
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
        assertEquals(100, webView.settings.textZoom)
    }

    @Test
    fun test6_fullDesktopModeRuntimeLifecycleSequence() {
        // A. Start with Desktop Mode OFF.
        repository.isDesktopModeEnabled = false
        val viewModel = BrowserViewModel(context)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)

        val tabManager = com.muslim.browser.pro.browser.TabWebViewManager(
            context = context,
            webViewFactory = { _ ->
                WebView(context).apply {
                    MainActivity.configureBaseSettings(
                        webView = this,
                        isDarkTheme = false,
                        isDesktopEnabled = viewModel.uiState.value.isDesktopModeEnabled
                    )
                }
            }
        )

        // B. Create a WebView.
        val (webView1, _) = tabManager.getOrCreateWebView("tab1")

        // C. Verify its normal/mobile configuration.
        assertFalse(webView1.settings.userAgentString.contains("Linux x86_64"))
        assertTrue(webView1.settings.useWideViewPort)
        assertTrue(webView1.settings.loadWithOverviewMode)
        assertEquals(100, webView1.settings.textZoom)
        assertTrue(webView1.settings.supportMultipleWindows())
        assertTrue(webView1.settings.builtInZoomControls)
        assertFalse(webView1.settings.displayZoomControls)

        // D. Turn Desktop Mode ON.
        viewModel.toggleDesktopMode(true)
        assertTrue(viewModel.uiState.value.isDesktopModeEnabled)
        tabManager.forEachLiveWebView { MainActivity.applyDesktopMode(it, true) }

        // E. Verify the SAME existing WebView now has the desktop User-Agent and desktop settings.
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView1.settings.userAgentString)
        assertTrue(webView1.settings.useWideViewPort)
        assertTrue(webView1.settings.loadWithOverviewMode)
        assertEquals(100, webView1.settings.textZoom)
        assertTrue(webView1.settings.supportMultipleWindows())
        assertTrue(webView1.settings.builtInZoomControls)
        assertFalse(webView1.settings.displayZoomControls)

        // F. Create a NEW tab while Desktop Mode is ON.
        val (webView2, _) = tabManager.getOrCreateWebView("tab2")

        // G. Verify the new WebView ALSO has the desktop configuration.
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView2.settings.userAgentString)
        assertTrue(webView2.settings.useWideViewPort)
        assertTrue(webView2.settings.loadWithOverviewMode)
        assertEquals(100, webView2.settings.textZoom)
        assertTrue(webView2.settings.supportMultipleWindows())
        assertTrue(webView2.settings.builtInZoomControls)
        assertFalse(webView2.settings.displayZoomControls)

        // H. Navigate/reload both tabs.
        webView1.loadUrl("https://example.com/page1")
        webView1.reload()
        webView2.loadUrl("https://example.com/page2")
        webView2.reload()

        // I. Verify the desktop configuration remains.
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView1.settings.userAgentString)
        assertTrue(webView1.settings.useWideViewPort)
        assertTrue(webView1.settings.loadWithOverviewMode)
        assertEquals(100, webView1.settings.textZoom)

        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView2.settings.userAgentString)
        assertTrue(webView2.settings.useWideViewPort)
        assertTrue(webView2.settings.loadWithOverviewMode)
        assertEquals(100, webView2.settings.textZoom)

        // J. Turn Desktop Mode OFF.
        viewModel.toggleDesktopMode(false)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        tabManager.forEachLiveWebView { MainActivity.applyDesktopMode(it, false) }

        // K. Verify both WebViews return to normal configuration.
        assertFalse(webView1.settings.userAgentString.contains("Linux x86_64"))
        assertTrue(webView1.settings.useWideViewPort)
        assertTrue(webView1.settings.loadWithOverviewMode)
        assertEquals(100, webView1.settings.textZoom)

        assertFalse(webView2.settings.userAgentString.contains("Linux x86_64"))
        assertTrue(webView2.settings.useWideViewPort)
        assertTrue(webView2.settings.loadWithOverviewMode)
        assertEquals(100, webView2.settings.textZoom)
    }

    @Test
    fun test7_configureBaseSettingsDirectlyAppliesDesktopModeWhenEnabled() {
        val webView = WebView(context)
        MainActivity.configureBaseSettings(webView, isDarkTheme = false, isDesktopEnabled = true)

        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
        assertEquals(100, webView.settings.textZoom)
        assertTrue(webView.settings.supportMultipleWindows())
        assertTrue(webView.settings.builtInZoomControls)
        assertFalse(webView.settings.displayZoomControls)
    }

    @Test
    fun test8_authenticationEndpointCompatibilityDetection() {
        // Known major IdP hosts
        assertTrue(MainActivity.isAuthenticationUrl("https://accounts.google.com/signin/v2/identifier"))
        assertTrue(MainActivity.isAuthenticationUrl("https://accounts.google.com/o/oauth2/v2/auth?client_id=123"))
        assertTrue(MainActivity.isAuthenticationUrl("https://sub.accounts.google.com/login"))
        assertTrue(MainActivity.isAuthenticationUrl("https://appleid.apple.com/auth/authorize"))
        assertTrue(MainActivity.isAuthenticationUrl("https://login.microsoftonline.com/common/oauth2/v2.0/authorize"))
        assertTrue(MainActivity.isAuthenticationUrl("https://auth.account.sony.com/login"))
        assertTrue(MainActivity.isAuthenticationUrl("https://auth.example.org/session"))
        assertTrue(MainActivity.isAuthenticationUrl("https://id.example.com/oauth/token"))

        // OAuth 2.0 and OpenID Connect paths
        assertTrue(MainActivity.isAuthenticationUrl("https://mywebsite.com/api/oauth2/authorize"))
        assertTrue(MainActivity.isAuthenticationUrl("https://mywebsite.com/oauth/authorize?response_type=code"))
        assertTrue(MainActivity.isAuthenticationUrl("https://sso.company.com/openid-connect/auth"))

        // Normal browsing URLs must NOT be flagged as authentication endpoints
        assertFalse(MainActivity.isAuthenticationUrl("https://www.google.com/"))
        assertFalse(MainActivity.isAuthenticationUrl("https://www.google.com/search?q=kotlin"))
        assertFalse(MainActivity.isAuthenticationUrl("https://en.wikipedia.org/wiki/Android"))
        assertFalse(MainActivity.isAuthenticationUrl("https://news.ycombinator.com/"))
        assertFalse(MainActivity.isAuthenticationUrl("https://github.com/torvalds/linux"))
        assertFalse(MainActivity.isAuthenticationUrl(""))
        assertFalse(MainActivity.isAuthenticationUrl(null))
    }

    @Test
    fun test9_applyDesktopModeWithAuthEndpointPreservesMobileUA() {
        val webView = WebView(context)
        val defaultUa = webView.settings.userAgentString

        // Normal page with Desktop Mode enabled -> Desktop UA
        webView.loadUrl("https://www.example.com")
        MainActivity.applyDesktopMode(webView, isDesktopEnabled = true)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Auth endpoint with Desktop Mode enabled -> compatible Mobile UA (null/default)
        webView.loadUrl("https://accounts.google.com/signin")
        MainActivity.applyDesktopMode(webView, isDesktopEnabled = true)
        assertEquals(defaultUa, webView.settings.userAgentString)

        // Switching back to regular page -> Desktop UA
        webView.loadUrl("https://www.example.com/dashboard")
        MainActivity.applyDesktopMode(webView, isDesktopEnabled = true)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test10_webViewConfiguratorBuild48ArchitectureDirectVerification() {
        val webView = WebView(context)
        val defaultUa = webView.settings.userAgentString

        // 1. Base configuration with Desktop Mode disabled
        com.muslim.browser.pro.browser.WebViewConfigurator.configureBaseSettings(
            webView = webView,
            isDarkTheme = false,
            isDesktopEnabled = false
        )
        assertTrue(webView.settings.javaScriptEnabled)
        assertTrue(webView.settings.domStorageEnabled)
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
        assertEquals(100, webView.settings.textZoom)
        assertEquals(defaultUa, webView.settings.userAgentString)

        // 2. Enable Desktop Mode via WebViewConfigurator
        com.muslim.browser.pro.browser.WebViewConfigurator.applyDesktopMode(webView, isDesktopEnabled = true)
        assertEquals(com.muslim.browser.pro.browser.WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)

        // 3. Navigate to auth endpoint -> compatible mobile UA is applied
        webView.loadUrl("https://accounts.google.com/o/oauth2/v2/auth")
        com.muslim.browser.pro.browser.WebViewConfigurator.applyDesktopMode(webView, isDesktopEnabled = true)
        assertEquals(defaultUa, webView.settings.userAgentString)

        // 4. Navigate back to destination page -> Desktop UA restored
        webView.loadUrl("https://mywebsite.org/home")
        com.muslim.browser.pro.browser.WebViewConfigurator.applyDesktopMode(webView, isDesktopEnabled = true)
        assertEquals(com.muslim.browser.pro.browser.WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // 5. Disable Desktop Mode
        com.muslim.browser.pro.browser.WebViewConfigurator.applyDesktopMode(webView, isDesktopEnabled = false)
        assertEquals(defaultUa, webView.settings.userAgentString)
    }
}
