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
 * Unit test suite for Desktop Mode 2 (Balanced Simplification).
 *
 * Architecture Characteristics:
 * - Desktop User-Agent: Linux x86_64 Chrome
 * - Native WebSettings: useWideViewPort = true, loadWithOverviewMode = true, textZoom = 100
 * - Pure Event-Driven Viewport Script: zero continuous DOM observation
 * - Retained: Navigation event listeners (turbo:load, turbo:render, pjax:end, popstate, pageshow)
 * - Removed: ALL MutationObservers (zero MutationObserver instances)
 * - Removed: history.pushState / history.replaceState monkey-patching
 * - Removed: Client Hints JavaScript override (relies on HTTP User-Agent)
 * - Complexity Boundary: strictly simpler than Mode 1 (less JS, no DOM observers)
 * - Mutual Exclusion: strictly mutually exclusive with Standard, Mode 1, Mode 3, Windows 10 Touch
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DesktopMode2Test {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.desktopArchitecture = DesktopArchitecture.NONE
    }

    @Test
    fun test1_desktopMode2PersistenceInRepository() {
        assertFalse(repository.isDesktopMode2Enabled)
        assertEquals(DesktopArchitecture.NONE, repository.desktopArchitecture)

        // Enable Mode 2
        repository.isDesktopMode2Enabled = true
        assertTrue(repository.isDesktopMode2Enabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_2, repository.desktopArchitecture)

        // Disable Mode 2
        repository.isDesktopMode2Enabled = false
        assertFalse(repository.isDesktopMode2Enabled)
        assertEquals(DesktopArchitecture.NONE, repository.desktopArchitecture)
    }

    @Test
    fun test2_browserViewModelDesktopMode2ToggleAndMutualExclusion() {
        val viewModel = BrowserViewModel(context)
        assertFalse(viewModel.uiState.value.isDesktopMode2Enabled)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)

        // Toggle Mode 2 ON
        viewModel.toggleDesktopMode2(true)
        assertTrue(viewModel.uiState.value.isDesktopMode2Enabled)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode1Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode3Enabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_2, viewModel.uiState.value.desktopArchitecture)

        // Toggle Mode 1 ON -> Mode 2 must turn OFF
        viewModel.toggleDesktopMode1(true)
        assertTrue(viewModel.uiState.value.isDesktopMode1Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode2Enabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_1, viewModel.uiState.value.desktopArchitecture)

        // Toggle Mode 2 ON again -> Mode 1 must turn OFF
        viewModel.toggleDesktopMode2(true)
        assertTrue(viewModel.uiState.value.isDesktopMode2Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode1Enabled)

        // Toggle Standard Desktop ON -> Mode 2 must turn OFF
        viewModel.toggleDesktopMode(true)
        assertTrue(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode2Enabled)

        // Toggle Mode 2 ON again -> Standard Desktop must turn OFF
        viewModel.toggleDesktopMode2(true)
        assertTrue(viewModel.uiState.value.isDesktopMode2Enabled)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)

        // Toggle Mode 2 OFF
        viewModel.toggleDesktopMode2(false)
        assertFalse(viewModel.uiState.value.isDesktopMode2Enabled)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)
    }

    @Test
    fun test3_desktopMode2UserAgentAndWebSettingsConfiguration() {
        val webView = WebView(context)
        val defaultUa = webView.settings.userAgentString

        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://example.com",
            architecture = DesktopArchitecture.DESKTOP_MODE_2,
            updateUserAgent = true
        )

        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertFalse(webView.settings.userAgentString.contains("Mobile"))
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
        assertEquals(100, webView.settings.textZoom)
        assertTrue(webView.settings.builtInZoomControls)
        assertFalse(webView.settings.displayZoomControls)

        // Reset to NONE
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://example.com",
            architecture = DesktopArchitecture.NONE,
            updateUserAgent = true
        )
        assertEquals(defaultUa, webView.settings.userAgentString)
    }

    @Test
    fun test4_desktopMode2ScriptIntegrityAndBoundaryComparisonWithMode1() {
        val script2 = WebViewConfigurator.DESKTOP_MODE_2_EVENT_SCRIPT
        val script1 = WebViewConfigurator.DESKTOP_MODE_1_GUARD_SCRIPT

        // 1. Must enforce target viewport width=1280
        assertTrue("Mode 2 must enforce width=1280", script2.contains("width=1280"))
        assertTrue("Mode 2 must use __mb_desktop_mode2__ key", script2.contains("__mb_desktop_mode2__"))

        // 2. Zero MutationObservers in Mode 2 (Balanced Simplification)
        assertFalse("Mode 2 must NOT use MutationObserver", script2.contains("MutationObserver"))
        assertFalse("Mode 2 must NOT observe head", script2.contains("headObserver"))

        // 3. Zero history monkey-patching in Mode 2
        assertFalse("Mode 2 must NOT monkey-patch history.pushState", script2.contains("history.pushState"))
        assertFalse("Mode 2 must NOT monkey-patch history.replaceState", script2.contains("history.replaceState"))

        // 4. Zero client hints override script in Mode 2
        assertFalse("Mode 2 must NOT override navigator.userAgentData", script2.contains("userAgentData"))

        // 5. Retains event-driven navigation listeners
        assertTrue("Mode 2 must listen to turbo:load", script2.contains("turbo:load"))
        assertTrue("Mode 2 must listen to turbo:render", script2.contains("turbo:render"))
        assertTrue("Mode 2 must listen to pjax:end", script2.contains("pjax:end"))
        assertTrue("Mode 2 must listen to popstate", script2.contains("popstate"))
        assertTrue("Mode 2 must listen to pageshow", script2.contains("pageshow"))

        // 6. Complexity boundary: Mode 1 has more compatibility logic than Mode 2
        assertTrue("Mode 1 must have MutationObserver while Mode 2 does not", script1.contains("MutationObserver") && !script2.contains("MutationObserver"))
        assertTrue("Mode 1 must have client hints patch while Mode 2 does not", script1.contains("userAgentData") && !script2.contains("userAgentData"))
        assertTrue("Mode 2 script must be shorter and simpler than Mode 1", script2.length < script1.length)
    }

    @Test
    fun test5_githubNavigationCompatibilityUnderMode2() {
        val githubUrls = listOf(
            "https://github.com",
            "https://github.com/torvalds/linux",
            "https://github.com/torvalds/linux/commits/master",
            "https://github.com/torvalds/linux/issues",
            "https://github.com/torvalds/linux/pulls",
            "https://github.com/torvalds/linux/releases"
        )

        val webView = WebView(context)
        for (url in githubUrls) {
            assertFalse("GitHub URL $url must never be treated as auth", WebViewConfigurator.isAuthenticationUrl(url))
            WebViewConfigurator.syncDesktopArchitecture(
                webView = webView,
                url = url,
                architecture = DesktopArchitecture.DESKTOP_MODE_2,
                updateUserAgent = true
            )
            assertEquals("GitHub page $url must retain desktop UA", WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
            assertTrue("GitHub page $url must have wide viewport", webView.settings.useWideViewPort)
            assertTrue("GitHub page $url must have overview mode", webView.settings.loadWithOverviewMode)
        }
    }

    @Test
    fun test6_authenticationEndpointSafetyUnderMode2() {
        val webView = WebView(context)
        val defaultUa = webView.settings.userAgentString

        val authUrl = "https://accounts.google.com/signin/v2/identifier"
        assertTrue(WebViewConfigurator.isAuthenticationUrl(authUrl))

        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = authUrl,
            architecture = DesktopArchitecture.DESKTOP_MODE_2,
            updateUserAgent = true
        )
        assertEquals(defaultUa, webView.settings.userAgentString)

        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://example.com/dashboard",
            architecture = DesktopArchitecture.DESKTOP_MODE_2,
            updateUserAgent = true
        )
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test7_tabWebViewManagerLifecycleUnderMode2() {
        repository.desktopArchitecture = DesktopArchitecture.DESKTOP_MODE_2
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

        viewModel.toggleDesktopMode2(false)
        tabManager.syncAllLiveWebViewsArchitecture(DesktopArchitecture.NONE)
        assertFalse(wv1.settings.userAgentString.contains("Linux x86_64"))
        assertFalse(wv2.settings.userAgentString.contains("Linux x86_64"))
    }
}
