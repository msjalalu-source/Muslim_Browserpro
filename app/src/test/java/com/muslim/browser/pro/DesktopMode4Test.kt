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
 * Unit test suite for Desktop Mode 4 (Targeted Guard).
 *
 * Architecture Characteristics:
 * - Desktop User-Agent: Linux x86_64 Chrome
 * - Native WebSettings: useWideViewPort = true, loadWithOverviewMode = true, textZoom = 100
 * - Narrowly targeted viewport observer:
 *   * Directly observes ONLY the viewport meta element for 'content' attribute changes
 *   * Observes document.head with childList=true, subtree=false to react ONLY to direct insertions/removals of <meta name="viewport">
 *   * Ignores unrelated <head> changes: title, stylesheets, favicons, preloads, scripts, links, other meta tags
 * - Retains navigation event listeners: turbo:load, turbo:render, pjax:end, pageshow, popstate
 * - Retains UA Client Hints spoofing: mobile: false, platform: 'Linux'
 * - Removed: history.pushState / history.replaceState monkey-patching
 * - Removed: document.head subtree recursive observation
 * - Mutual Exclusion: strictly mutually exclusive with Standard, Mode 1, Mode 5, Windows 10 Touch
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DesktopMode4Test {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.desktopArchitecture = DesktopArchitecture.NONE
    }

    @Test
    fun test1_desktopMode4PersistenceInRepository() {
        assertFalse(repository.isDesktopMode4Enabled)
        assertEquals(DesktopArchitecture.NONE, repository.desktopArchitecture)

        // Enable Mode 4
        repository.isDesktopMode4Enabled = true
        assertTrue(repository.isDesktopMode4Enabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_4, repository.desktopArchitecture)

        // Disable Mode 4
        repository.isDesktopMode4Enabled = false
        assertFalse(repository.isDesktopMode4Enabled)
        assertEquals(DesktopArchitecture.NONE, repository.desktopArchitecture)
    }

    @Test
    fun test2_browserViewModelDesktopMode4ToggleAndMutualExclusion() {
        val viewModel = BrowserViewModel(context)
        assertFalse(viewModel.uiState.value.isDesktopMode4Enabled)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)

        // Toggle Mode 4 ON
        viewModel.toggleDesktopMode4(true)
        assertTrue(viewModel.uiState.value.isDesktopMode4Enabled)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_4, viewModel.uiState.value.desktopArchitecture)

        // Toggle Standard Desktop ON -> Mode 4 must turn OFF
        viewModel.toggleDesktopMode(true)
        assertTrue(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode4Enabled)
        assertEquals(DesktopArchitecture.STANDARD, viewModel.uiState.value.desktopArchitecture)

        // Toggle Mode 4 ON again -> Standard Desktop must turn OFF
        viewModel.toggleDesktopMode4(true)
        assertTrue(viewModel.uiState.value.isDesktopMode4Enabled)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        assertEquals(DesktopArchitecture.DESKTOP_MODE_4, viewModel.uiState.value.desktopArchitecture)

        // Toggle Windows 10 Touch ON -> Mode 4 must turn OFF
        viewModel.toggleWindows10Touch(true)
        assertTrue(viewModel.uiState.value.isWindows10TouchEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode4Enabled)

        // Toggle Mode 4 ON again
        viewModel.toggleDesktopMode4(true)
        assertTrue(viewModel.uiState.value.isDesktopMode4Enabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)

        // Toggle Mode 4 OFF -> NONE
        viewModel.toggleDesktopMode4(false)
        assertFalse(viewModel.uiState.value.isDesktopMode4Enabled)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)
    }

    @Test
    fun test3_desktopMode4AppliesDesktopUserAgentAndNativeWebSettings() {
        val webView = WebView(context)
        val defaultMobileUa = webView.settings.userAgentString

        WebViewConfigurator.configureBaseSettings(
            webView = webView,
            isDarkTheme = false,
            desktopArchitecture = DesktopArchitecture.DESKTOP_MODE_4
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
    fun test4_desktopMode4ScriptStructureAndTargetedObserverVerification() {
        val script = WebViewConfigurator.DESKTOP_MODE_4_GUARD_SCRIPT

        // 1. Must use the unique guard key
        assertTrue("Mode 4 must use __mb_desktop_mode4__ key", script.contains("__mb_desktop_mode4__"))

        // 2. Must enforce width=1280
        assertTrue("Mode 4 must set width=1280", script.contains("width=1280"))

        // 3. Must use targeted observer on head with subtree=false (NOT subtree=true!)
        assertTrue("Mode 4 must observe head with childList: true", script.contains("childList: true"))
        assertTrue("Mode 4 must observe head with subtree: false", script.contains("subtree: false"))
        assertFalse("Mode 4 MUST NOT observe head with subtree: true", script.contains("subtree: true"))

        // 4. Must check for viewport meta element before triggering applyViewport
        assertTrue("Mode 4 must check node.name === 'viewport'", script.contains("'viewport'"))

        // 5. Must observe the viewport meta element directly for 'content' changes
        assertTrue("Mode 4 must observe meta attributes: true", script.contains("attributes: true"))
        assertTrue("Mode 4 must filter attributeFilter: ['content']", script.contains("attributeFilter: ['content']"))

        // 6. Must retain navigation event listeners
        assertTrue("Mode 4 must listen to turbo:load", script.contains("turbo:load"))
        assertTrue("Mode 4 must listen to turbo:render", script.contains("turbo:render"))
        assertTrue("Mode 4 must listen to pjax:end", script.contains("pjax:end"))
        assertTrue("Mode 4 must listen to pageshow", script.contains("pageshow"))
        assertTrue("Mode 4 must listen to popstate", script.contains("popstate"))

        // 7. Must NOT monkey-patch history.pushState or replaceState
        assertFalse("Mode 4 MUST NOT modify history.pushState", script.contains("history.pushState ="))
        assertFalse("Mode 4 MUST NOT modify history.replaceState", script.contains("history.replaceState ="))

        // 8. Must retain desktop client hints override
        assertTrue("Mode 4 must spoof mobile: false", script.contains("mobile: false"))
        assertTrue("Mode 4 must spoof platform: 'Linux'", script.contains("platform: 'Linux'"))

        // 9. Cleanup must be defined and disconnect both observers
        assertTrue("Mode 4 must include cleanup function", script.contains("cleanup: function()"))
        assertTrue("Mode 4 cleanup must disconnect headObserver", script.contains("headObserver.disconnect()"))
        assertTrue("Mode 4 cleanup must disconnect metaObserver", script.contains("metaObserver.disconnect()"))
        assertTrue("Mode 4 cleanup must restore original viewport", script.contains("data-mb4-orig"))
    }

    @Test
    fun test5_desktopMode4CleanupInUnifiedCleanupScript() {
        val cleanupScript = WebViewConfigurator.CLEANUP_ALL_DESKTOP_SCRIPTS
        assertTrue("Unified cleanup must check __mb_desktop_mode4__", cleanupScript.contains("__mb_desktop_mode4__"))
        assertTrue("Unified cleanup must clean data-mb4-created", cleanupScript.contains("data-mb4-created"))
        assertTrue("Unified cleanup must clean data-mb4-orig", cleanupScript.contains("data-mb4-orig"))
    }

    @Test
    fun test6_desktopMode4PreservesMobileUaOnAuthenticationUrls() {
        val webView = WebView(context)
        val defaultMobileUa = webView.settings.userAgentString

        // Normal page -> Desktop UA
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://github.com/torvalds/linux",
            architecture = DesktopArchitecture.DESKTOP_MODE_4,
            updateUserAgent = true
        )
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Authentication page -> Mobile UA
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://accounts.google.com/signin/v2",
            architecture = DesktopArchitecture.DESKTOP_MODE_4,
            updateUserAgent = true
        )
        assertEquals(defaultMobileUa, webView.settings.userAgentString)

        // Return to normal page -> Desktop UA restored
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://www.example.com",
            architecture = DesktopArchitecture.DESKTOP_MODE_4,
            updateUserAgent = true
        )
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test7_tabWebViewManagerSynchronizesDesktopMode4() {
        var activeArchitecture = DesktopArchitecture.DESKTOP_MODE_4
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

        // 1. Create tab -> Desktop Mode 4 UA applied
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

        // 4. Switch back to Mode 4
        activeArchitecture = DesktopArchitecture.DESKTOP_MODE_4
        tabManager.syncAllLiveWebViewsArchitecture(DesktopArchitecture.DESKTOP_MODE_4)
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, wv1.settings.userAgentString)
    }
}
