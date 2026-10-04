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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit test suite for Desktop Mode 1 (Conservative Simplification).
 *
 * Architecture Characteristics:
 * - Desktop User-Agent: Linux x86_64 Chrome
 * - Native WebSettings: useWideViewPort = true, loadWithOverviewMode = true, textZoom = 100
 * - Streamlined Head-only Viewport Guard: observes document.head for <meta name="viewport">
 * - Retained: Turbo/PJAX navigation events (turbo:load, turbo:render, pjax:end, popstate, pageshow)
 * - Retained: UA Client Hints spoofing (mobile: false, platform: 'Linux')
 * - Removed: document.documentElement root observer (avoids recursive DOM observation)
 * - Removed: history.pushState / history.replaceState monkey-patching
 * - Mutual Exclusion: strictly mutually exclusive with Standard, Mode 2, Mode 3, Windows 10 Touch
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DesktopMode1Test {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.desktopArchitecture = DesktopArchitecture.NONE
    }

    @Test
    fun test1_desktopMode1PersistenceInRepository() {
        assertFalse(repository.isDesktopMode1Enabled)
        assertEquals(DesktopArchitecture.NONE, repository.desktopArchitecture)

        // Enable Mode 1
        repository.isDesktopMode1Enabled = true
        assertTrue(repository.isDesktopMode1Enabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_1, repository.desktopArchitecture)

        // Disable Mode 1
        repository.isDesktopMode1Enabled = false
        assertFalse(repository.isDesktopMode1Enabled)
        assertEquals(DesktopArchitecture.NONE, repository.desktopArchitecture)
    }

    @Test
    fun test2_browserViewModelDesktopMode1ToggleAndMutualExclusion() {
        val viewModel = BrowserViewModel(context)
        assertFalse(viewModel.uiState.value.isDesktopMode1Enabled)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)

        // Toggle Mode 1 ON
        viewModel.toggleDesktopMode1(true)
        assertTrue(viewModel.uiState.value.isDesktopMode1Enabled)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode2Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode3Enabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_1, viewModel.uiState.value.desktopArchitecture)

        // Toggle Standard Desktop ON -> Mode 1 must turn OFF
        viewModel.toggleDesktopMode(true)
        assertTrue(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode1Enabled)
        assertEquals(DesktopArchitecture.STANDARD, viewModel.uiState.value.desktopArchitecture)

        // Toggle Mode 1 ON again -> Standard Desktop must turn OFF
        viewModel.toggleDesktopMode1(true)
        assertTrue(viewModel.uiState.value.isDesktopMode1Enabled)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)

        // Toggle Windows 10 Touch ON -> Mode 1 must turn OFF
        viewModel.toggleWindows10Touch(true)
        assertTrue(viewModel.uiState.value.isWindows10TouchEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode1Enabled)

        // Toggle Mode 1 ON again -> Windows 10 Touch must turn OFF
        viewModel.toggleDesktopMode1(true)
        assertTrue(viewModel.uiState.value.isDesktopMode1Enabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)

        // Toggle Mode 1 OFF -> returns to NONE
        viewModel.toggleDesktopMode1(false)
        assertFalse(viewModel.uiState.value.isDesktopMode1Enabled)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)
    }

    @Test
    fun test3_desktopMode1UserAgentAndWebSettingsConfiguration() {
        val webView = WebView(context)
        val defaultUa = webView.settings.userAgentString

        // Configure with Mode 1
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://example.com",
            architecture = DesktopArchitecture.DESKTOP_MODE_1,
            updateUserAgent = true
        )

        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertFalse(webView.settings.userAgentString.contains("Mobile"))
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
        assertEquals(100, webView.settings.textZoom)
        assertTrue(webView.settings.builtInZoomControls)
        assertFalse(webView.settings.displayZoomControls)

        // Switch to NONE
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://example.com",
            architecture = DesktopArchitecture.NONE,
            updateUserAgent = true
        )
        assertEquals(defaultUa, webView.settings.userAgentString)
    }

    @Test
    fun test4_desktopMode1ScriptIntegrityAndBoundary() {
        val script = WebViewConfigurator.DESKTOP_MODE_1_GUARD_SCRIPT

        // 1. Must enforce target viewport width=1280
        assertTrue("Script must enforce width=1280", script.contains("width=1280"))
        assertTrue("Script must use __mb_desktop_mode1__ key", script.contains("__mb_desktop_mode1__"))

        // 2. Retains head-only MutationObserver
        assertTrue("Mode 1 must contain MutationObserver", script.contains("MutationObserver"))
        assertTrue("Mode 1 must observe document.head", script.contains("headObserver.observe(document.head"))

        // 3. Removed documentElement root observer (Conservative Simplification)
        assertFalse("Mode 1 must NOT observe documentElement", script.contains("docObserver"))
        assertFalse("Mode 1 must NOT observe documentElement directly", script.contains(".observe(document.documentElement"))

        // 4. Removed history.pushState and replaceState monkey-patching
        assertFalse("Mode 1 must NOT monkey-patch history.pushState", script.contains("history.pushState ="))
        assertFalse("Mode 1 must NOT monkey-patch history.replaceState", script.contains("history.replaceState ="))

        // 5. Retains Turbo/PJAX and SPA navigation event listeners
        assertTrue("Mode 1 must listen to turbo:load", script.contains("turbo:load"))
        assertTrue("Mode 1 must listen to turbo:render", script.contains("turbo:render"))
        assertTrue("Mode 1 must listen to pjax:end", script.contains("pjax:end"))
        assertTrue("Mode 1 must listen to popstate", script.contains("popstate"))
        assertTrue("Mode 1 must listen to pageshow", script.contains("pageshow"))

        // 6. Retains UA Client Hints spoofing
        assertTrue("Mode 1 must spoof userAgentData", script.contains("userAgentData"))
        assertTrue("Mode 1 must set mobile: false", script.contains("mobile: false"))
        assertTrue("Mode 1 must set platform: 'Linux'", script.contains("platform: 'Linux'"))
    }

    @Test
    fun test5_githubNavigationCompatibilityUnderMode1() {
        val githubUrls = listOf(
            "https://github.com",
            "https://github.com/torvalds/linux",
            "https://github.com/torvalds/linux/commits/master",
            "https://github.com/torvalds/linux/issues",
            "https://github.com/torvalds/linux/pulls",
            "https://github.com/torvalds/linux/releases",
            "https://github.com/settings/profile"
        )

        val webView = WebView(context)
        for (url in githubUrls) {
            assertFalse("GitHub URL $url must never be treated as an auth endpoint", WebViewConfigurator.isAuthenticationUrl(url))
            WebViewConfigurator.syncDesktopArchitecture(
                webView = webView,
                url = url,
                architecture = DesktopArchitecture.DESKTOP_MODE_1,
                updateUserAgent = true
            )
            assertEquals("GitHub page $url must retain desktop UA", WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
            assertTrue("GitHub page $url must have wide viewport", webView.settings.useWideViewPort)
            assertTrue("GitHub page $url must have overview mode", webView.settings.loadWithOverviewMode)
        }
    }

    @Test
    fun test6_authenticationEndpointSafetyUnderMode1() {
        val webView = WebView(context)
        val defaultUa = webView.settings.userAgentString

        // Google sign-in auth endpoint must temporarily fallback to mobile UA
        val authUrl = "https://accounts.google.com/signin/v2/identifier"
        assertTrue(WebViewConfigurator.isAuthenticationUrl(authUrl))

        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = authUrl,
            architecture = DesktopArchitecture.DESKTOP_MODE_1,
            updateUserAgent = true
        )
        assertEquals(defaultUa, webView.settings.userAgentString)

        // Regular page restores desktop UA
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://github.com",
            architecture = DesktopArchitecture.DESKTOP_MODE_1,
            updateUserAgent = true
        )
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test7_tabWebViewManagerLifecycleUnderMode1() {
        repository.desktopArchitecture = DesktopArchitecture.DESKTOP_MODE_1
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

        // 1. Initial tab created with Mode 1
        val (wv1, _) = tabManager.getOrCreateWebView("tab1")
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, wv1.settings.userAgentString)

        // 2. New tab created while Mode 1 is active
        val (wv2, _) = tabManager.getOrCreateWebView("tab2")
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, wv2.settings.userAgentString)

        // 3. Tab switching restores Mode 1
        val (wv1Again, _) = tabManager.getOrCreateWebView("tab1")
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, wv1Again.settings.userAgentString)

        // 4. Reload and navigation retain Mode 1
        wv1.loadUrl("https://example.com/subpage")
        wv1.reload()
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, wv1.settings.userAgentString)

        // 5. Back and forward retain Mode 1
        if (wv1.canGoBack()) wv1.goBack()
        if (wv1.canGoForward()) wv1.goForward()
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, wv1.settings.userAgentString)

        // 6. Disable Mode 1
        viewModel.toggleDesktopMode1(false)
        tabManager.syncAllLiveWebViewsArchitecture(DesktopArchitecture.NONE)
        assertFalse(wv1.settings.userAgentString.contains("Linux x86_64"))
        assertFalse(wv2.settings.userAgentString.contains("Linux x86_64"))
    }
}
