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
 * Unit test suite for Desktop Mode 3 (Maximum Simplification).
 *
 * Architecture Characteristics:
 * - Desktop User-Agent: Linux x86_64 Chrome
 * - Native WebSettings: useWideViewPort = true, loadWithOverviewMode = true, textZoom = 100
 * - ZERO JavaScript injection:
 *   - No MutationObserver
 *   - No event listeners (turbo, pjax, popstate, etc.)
 *   - No viewport rewriting script
 *   - No DOM manipulation
 *   - No client hints override script
 * - Complexity Boundary: strictly simpler than Mode 2 (zero JS overhead)
 * - Mutual Exclusion: strictly mutually exclusive with Standard, Mode 1, Mode 2, Windows 10 Touch
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DesktopMode3Test {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.desktopArchitecture = DesktopArchitecture.NONE
    }

    @Test
    fun test1_desktopMode3PersistenceInRepository() {
        assertFalse(repository.isDesktopMode3Enabled)
        assertEquals(DesktopArchitecture.NONE, repository.desktopArchitecture)

        // Enable Mode 3
        repository.isDesktopMode3Enabled = true
        assertTrue(repository.isDesktopMode3Enabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_3, repository.desktopArchitecture)

        // Disable Mode 3
        repository.isDesktopMode3Enabled = false
        assertFalse(repository.isDesktopMode3Enabled)
        assertEquals(DesktopArchitecture.NONE, repository.desktopArchitecture)
    }

    @Test
    fun test2_browserViewModelDesktopMode3ToggleAndMutualExclusion() {
        val viewModel = BrowserViewModel(context)
        assertFalse(viewModel.uiState.value.isDesktopMode3Enabled)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)

        // Toggle Mode 3 ON
        viewModel.toggleDesktopMode3(true)
        assertTrue(viewModel.uiState.value.isDesktopMode3Enabled)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode1Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode2Enabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_3, viewModel.uiState.value.desktopArchitecture)

        // Toggle Mode 1 ON -> Mode 3 must turn OFF
        viewModel.toggleDesktopMode1(true)
        assertTrue(viewModel.uiState.value.isDesktopMode1Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode3Enabled)

        // Toggle Mode 3 ON again -> Mode 1 must turn OFF
        viewModel.toggleDesktopMode3(true)
        assertTrue(viewModel.uiState.value.isDesktopMode3Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode1Enabled)

        // Toggle Mode 2 ON -> Mode 3 must turn OFF
        viewModel.toggleDesktopMode2(true)
        assertTrue(viewModel.uiState.value.isDesktopMode2Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode3Enabled)

        // Toggle Mode 3 ON again -> Mode 2 must turn OFF
        viewModel.toggleDesktopMode3(true)
        assertTrue(viewModel.uiState.value.isDesktopMode3Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode2Enabled)

        // Toggle Standard Desktop ON -> Mode 3 must turn OFF
        viewModel.toggleDesktopMode(true)
        assertTrue(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode3Enabled)

        // Toggle Mode 3 ON again -> Standard Desktop must turn OFF
        viewModel.toggleDesktopMode3(true)
        assertTrue(viewModel.uiState.value.isDesktopMode3Enabled)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)

        // Toggle Mode 3 OFF -> returns to NONE
        viewModel.toggleDesktopMode3(false)
        assertFalse(viewModel.uiState.value.isDesktopMode3Enabled)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)
    }

    @Test
    fun test3_desktopMode3NativeConfigurationOnly() {
        val webView = WebView(context)
        val defaultUa = webView.settings.userAgentString

        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://example.com",
            architecture = DesktopArchitecture.DESKTOP_MODE_3,
            updateUserAgent = true
        )

        // 1. Native Desktop UA applied
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertFalse(webView.settings.userAgentString.contains("Mobile"))

        // 2. Native WebSettings applied
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
        assertEquals(100, webView.settings.textZoom)
        assertTrue(webView.settings.builtInZoomControls)
        assertFalse(webView.settings.displayZoomControls)

        // 3. Reset to NONE
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://example.com",
            architecture = DesktopArchitecture.NONE,
            updateUserAgent = true
        )
        assertEquals(defaultUa, webView.settings.userAgentString)
    }

    @Test
    fun test4_desktopMode3ComplexityBoundaryAndZeroJavaScriptVerification() {
        val script1 = WebViewConfigurator.DESKTOP_MODE_1_GUARD_SCRIPT
        val script2 = WebViewConfigurator.DESKTOP_MODE_2_EVENT_SCRIPT

        // Mode 1 and Mode 2 contain custom injected JS scripts
        assertTrue("Mode 1 contains custom JS script", script1.isNotBlank())
        assertTrue("Mode 2 contains custom JS script", script2.isNotBlank())

        // Desktop Mode 3 applies CLEANUP_ALL_DESKTOP_SCRIPTS rather than a new guard script
        // Mode 3 introduces NO dedicated guard script:
        // It relies exclusively on native WebSettings and User-Agent without any DOM or event hooks
        val webView = WebView(context)
        WebViewConfigurator.applyArchitectureViewport(webView, DesktopArchitecture.DESKTOP_MODE_3)
        // No custom __mb_desktop_mode3__ script exists:
        assertFalse("Mode 3 does NOT have a dedicated guard script", script1.contains("__mb_desktop_mode3__"))
        assertFalse("Mode 3 does NOT have a dedicated event script", script2.contains("__mb_desktop_mode3__"))
    }

    @Test
    fun test5_githubNavigationCompatibilityUnderMode3() {
        val githubUrls = listOf(
            "https://github.com",
            "https://github.com/torvalds/linux",
            "https://github.com/torvalds/linux/commits/master",
            "https://github.com/torvalds/linux/issues",
            "https://github.com/torvalds/linux/pulls"
        )

        val webView = WebView(context)
        for (url in githubUrls) {
            assertFalse("GitHub URL $url must never be treated as auth", WebViewConfigurator.isAuthenticationUrl(url))
            WebViewConfigurator.syncDesktopArchitecture(
                webView = webView,
                url = url,
                architecture = DesktopArchitecture.DESKTOP_MODE_3,
                updateUserAgent = true
            )
            assertEquals("GitHub page $url must retain desktop UA", WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
            assertTrue("GitHub page $url must have wide viewport", webView.settings.useWideViewPort)
            assertTrue("GitHub page $url must have overview mode", webView.settings.loadWithOverviewMode)
        }
    }

    @Test
    fun test6_authenticationEndpointSafetyUnderMode3() {
        val webView = WebView(context)
        val defaultUa = webView.settings.userAgentString

        val authUrl = "https://accounts.google.com/signin/v2/identifier"
        assertTrue(WebViewConfigurator.isAuthenticationUrl(authUrl))

        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = authUrl,
            architecture = DesktopArchitecture.DESKTOP_MODE_3,
            updateUserAgent = true
        )
        assertEquals(defaultUa, webView.settings.userAgentString)

        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://github.com",
            architecture = DesktopArchitecture.DESKTOP_MODE_3,
            updateUserAgent = true
        )
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test7_tabWebViewManagerLifecycleUnderMode3() {
        repository.desktopArchitecture = DesktopArchitecture.DESKTOP_MODE_3
        val viewModel = BrowserViewModel(context)

        val tabManager = TabWebViewManager(
            context = context,
            webViewFactory = { _ ->
                WebView(context).apply {
                    WebViewConfigurator.configureBaseSettings(
                        webView = this,
                        isDarkTheme = false,
                        desktopArchitecture = viewModel.uiState.value.desktopArchitecture
                    )
                }
            },
            onSyncArchitecture = { wv, arch ->
                WebViewConfigurator.syncDesktopArchitecture(wv, wv.url, arch)
            },
            architectureProvider = { viewModel.uiState.value.desktopArchitecture }
        )

        val (wv1, _) = tabManager.getOrCreateWebView("tab1")
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, wv1.settings.userAgentString)

        val (wv2, _) = tabManager.getOrCreateWebView("tab2")
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, wv2.settings.userAgentString)

        val (wv1Again, _) = tabManager.getOrCreateWebView("tab1")
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, wv1Again.settings.userAgentString)

        wv1.loadUrl("https://example.com/test")
        wv1.reload()
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, wv1.settings.userAgentString)

        viewModel.toggleDesktopMode3(false)
        tabManager.syncAllLiveWebViewsArchitecture(DesktopArchitecture.NONE)
        assertFalse(wv1.settings.userAgentString.contains("Linux x86_64"))
        assertFalse(wv2.settings.userAgentString.contains("Linux x86_64"))
    }
}
