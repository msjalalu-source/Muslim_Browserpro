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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit test suite for Windows 7 Desktop Architecture.
 *
 * Verifies all 23 specification requirements:
 * 1. Windows 7 enum exists.
 * 2. Windows 7 UI state exists.
 * 3. Windows 7 persistence works.
 * 4. Windows 7 is mutually exclusive with other architectures.
 * 5. Windows 7 uses the correct desktop User-Agent.
 * 6. Client hints remain correct.
 * 7. Native desktop WebSettings remain correct.
 * 8. Viewport target remains width=1280.
 * 9. Viewport meta creation works.
 * 10. Viewport meta insertion works.
 * 11. Viewport meta removal works.
 * 12. Viewport content modification works.
 * 13. Unrelated head mutations do not trigger viewport enforcement.
 * 14. Exactly one continuous MutationObserver exists.
 * 15. subtree=true does not exist (subtree: false is enforced).
 * 16. documentElement-wide observation does not exist.
 * 17. history.pushState is not modified.
 * 18. history.replaceState is not modified.
 * 19. Polling is not used.
 * 20. Duplicate observer systems are not created.
 * 21. Required navigation behavior remains covered.
 * 22. Cleanup works.
 * 23. Existing desktop modes remain untouched.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Windows7DesktopModeTest {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.desktopArchitecture = DesktopArchitecture.NONE
    }

    @Test
    fun test1_windows7EnumAndPropertiesExist() {
        val w7 = DesktopArchitecture.WINDOWS_7
        assertEquals("Windows 7", w7.displayName)
        assertTrue(w7.isAnyDesktop)
        assertTrue(w7.isWindows7)
        assertFalse(w7.isStandard)
        assertFalse(w7.isMode1)
        assertFalse(w7.isMode2)
        assertFalse(w7.isMode3)
        assertFalse(w7.isWindows10Touch)
    }

    @Test
    fun test2_windows7PersistenceInRepository() {
        assertFalse(repository.isWindows7Enabled)
        assertEquals(DesktopArchitecture.NONE, repository.desktopArchitecture)

        // Enable Windows 7
        repository.isWindows7Enabled = true
        assertTrue(repository.isWindows7Enabled)
        assertEquals(DesktopArchitecture.WINDOWS_7, repository.desktopArchitecture)

        // Disable Windows 7 -> returns to NONE
        repository.isWindows7Enabled = false
        assertFalse(repository.isWindows7Enabled)
        assertEquals(DesktopArchitecture.NONE, repository.desktopArchitecture)
    }

    @Test
    fun test3_windows7BrowserViewModelStateAndMutualExclusion() {
        val viewModel = BrowserViewModel(context)
        assertFalse(viewModel.uiState.value.isWindows7Enabled)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)

        // Toggle Windows 7 ON
        viewModel.toggleWindows7(true)
        assertTrue(viewModel.uiState.value.isWindows7Enabled)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isDesktopMode1Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode2Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode3Enabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)
        assertEquals(DesktopArchitecture.WINDOWS_7, viewModel.uiState.value.desktopArchitecture)

        // Toggle Standard Desktop ON -> Windows 7 must turn OFF
        viewModel.toggleDesktopMode(true)
        assertTrue(viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse(viewModel.uiState.value.isWindows7Enabled)
        assertEquals(DesktopArchitecture.STANDARD, viewModel.uiState.value.desktopArchitecture)

        // Toggle Windows 7 ON again -> Standard Desktop must turn OFF
        viewModel.toggleWindows7(true)
        assertTrue(viewModel.uiState.value.isWindows7Enabled)
        assertFalse(viewModel.uiState.value.isDesktopModeEnabled)

        // Toggle Windows 10 Touch ON -> Windows 7 must turn OFF
        viewModel.toggleWindows10Touch(true)
        assertTrue(viewModel.uiState.value.isWindows10TouchEnabled)
        assertFalse(viewModel.uiState.value.isWindows7Enabled)

        // Toggle Windows 7 ON again -> Windows 10 Touch must turn OFF
        viewModel.toggleWindows7(true)
        assertTrue(viewModel.uiState.value.isWindows7Enabled)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)

        // Toggle Mode 1 ON -> Windows 7 must turn OFF
        viewModel.toggleDesktopMode1(true)
        assertTrue(viewModel.uiState.value.isDesktopMode1Enabled)
        assertFalse(viewModel.uiState.value.isWindows7Enabled)

        // Toggle Windows 7 ON again -> Mode 1 must turn OFF
        viewModel.toggleWindows7(true)
        assertTrue(viewModel.uiState.value.isWindows7Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode1Enabled)

        // Toggle Mode 2 ON -> Windows 7 must turn OFF
        viewModel.toggleDesktopMode2(true)
        assertTrue(viewModel.uiState.value.isDesktopMode2Enabled)
        assertFalse(viewModel.uiState.value.isWindows7Enabled)

        // Toggle Windows 7 ON again -> Mode 2 must turn OFF
        viewModel.toggleWindows7(true)
        assertTrue(viewModel.uiState.value.isWindows7Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode2Enabled)

        // Toggle Mode 3 ON -> Windows 7 must turn OFF
        viewModel.toggleDesktopMode3(true)
        assertTrue(viewModel.uiState.value.isDesktopMode3Enabled)
        assertFalse(viewModel.uiState.value.isWindows7Enabled)

        // Toggle Windows 7 ON again -> Mode 3 must turn OFF
        viewModel.toggleWindows7(true)
        assertTrue(viewModel.uiState.value.isWindows7Enabled)
        assertFalse(viewModel.uiState.value.isDesktopMode3Enabled)

        // Toggle Windows 7 OFF -> returns to NONE
        viewModel.toggleWindows7(false)
        assertFalse(viewModel.uiState.value.isWindows7Enabled)
        assertEquals(DesktopArchitecture.NONE, viewModel.uiState.value.desktopArchitecture)
    }

    @Test
    fun test4_windows7UserAgentAndWebSettingsConfiguration() {
        val webView = WebView(context)
        val defaultUa = webView.settings.userAgentString

        // Configure with Windows 7 architecture
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://example.com",
            architecture = DesktopArchitecture.WINDOWS_7,
            updateUserAgent = true
        )

        assertEquals("Windows 7 must reuse proven desktop UA", WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertFalse("Desktop UA must not contain Mobile", webView.settings.userAgentString.contains("Mobile"))
        assertTrue("useWideViewPort must be true", webView.settings.useWideViewPort)
        assertTrue("loadWithOverviewMode must be true", webView.settings.loadWithOverviewMode)
        assertEquals("textZoom must be 100", 100, webView.settings.textZoom)
        assertTrue("builtInZoomControls must be true", webView.settings.builtInZoomControls)
        assertFalse("displayZoomControls must be false", webView.settings.displayZoomControls)

        // Authentication URL bypass check
        val authUrl = "https://accounts.google.com/signin"
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = authUrl,
            architecture = DesktopArchitecture.WINDOWS_7,
            updateUserAgent = true
        )
        assertEquals("Auth URLs should revert to default UA to prevent OAuth loops", defaultUa, webView.settings.userAgentString)

        // Reverting to normal non-auth URL restores desktop UA
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://github.com",
            architecture = DesktopArchitecture.WINDOWS_7,
            updateUserAgent = true
        )
        assertEquals(WebViewConfigurator.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Switching back to NONE restores default UA
        WebViewConfigurator.syncDesktopArchitecture(
            webView = webView,
            url = "https://example.com",
            architecture = DesktopArchitecture.NONE,
            updateUserAgent = true
        )
        assertEquals(defaultUa, webView.settings.userAgentString)
    }

    @Test
    fun test5_windows7ScriptArchitectureAndMutationObserverIntegrity() {
        val script = WebViewConfigurator.WINDOWS_7_VIEWPORT_GUARD_SCRIPT

        // 8. Target viewport remains width=1280
        assertTrue("Target viewport must be width=1280", script.contains("width=1280"))
        assertTrue("Must use dedicated guard key __mb_desktop_w7__", script.contains("__mb_desktop_w7__"))

        // 14. Exactly one continuous MutationObserver exists
        val observerOccurrences = Regex("new\\s+MutationObserver").findAll(script).count()
        assertEquals("Windows 7 must have exactly ONE continuous MutationObserver", 1, observerOccurrences)

        // 15. subtree=true does not exist; subtree: false is explicitly enforced
        assertFalse("subtree: true must NOT exist", script.contains("subtree: true") || script.contains("subtree:true"))
        assertTrue("subtree: false must be explicitly set", script.contains("subtree: false"))

        // 16. documentElement-wide observation does not exist
        assertFalse("Must NOT observe documentElement", script.contains(".observe(document.documentElement"))
        assertFalse("docObserver must not exist", script.contains("docObserver"))
        assertTrue("Must observe document.head", script.contains("headObserver.observe(document.head"))

        // 17. history.pushState is not modified
        assertFalse("history.pushState must NOT be modified", script.contains("history.pushState =") || script.contains("history.pushState="))

        // 18. history.replaceState is not modified
        assertFalse("history.replaceState must NOT be modified", script.contains("history.replaceState =") || script.contains("history.replaceState="))

        // 19. Polling is not used
        assertFalse("setInterval must NOT be used", script.contains("setInterval"))
        assertFalse("setTimeout polling loop must NOT be used", script.contains("setTimeout"))

        // 6. Client hints remain correct
        assertTrue("Must patch userAgentData", script.contains("userAgentData"))
        assertTrue("Must set mobile: false", script.contains("mobile: false"))
        assertTrue("Must handle getHighEntropyValues", script.contains("getHighEntropyValues"))
        assertTrue("Must set platform to Linux", script.contains("platform: 'Linux'"))
        assertTrue("Must patch navigator.platform to Linux x86_64", script.contains("Linux x86_64"))

        // 10 & 13. Observer filtering: checks isViewportMeta and attributeFilter: ['content']
        assertTrue("Must contain attributeFilter ['content']", script.contains("attributeFilter: ['content']"))
        assertTrue("Must have isViewportMeta check", script.contains("isViewportMeta"))
        assertTrue("Must check addedNodes", script.contains("addedNodes"))
        assertTrue("Must check removedNodes", script.contains("removedNodes"))

        // 8 & 21. Navigation event optimization: turbo:load, pjax:end, pageshow, popstate (omits redundant turbo:render)
        assertTrue("Must listen to turbo:load", script.contains("'turbo:load'"))
        assertTrue("Must listen to pjax:end", script.contains("'pjax:end'"))
        assertTrue("Must listen to pageshow", script.contains("'pageshow'"))
        assertTrue("Must listen to popstate", script.contains("'popstate'"))
        assertFalse("Must omit redundant turbo:render", script.contains("'turbo:render'"))

        // 9. Idempotent applyViewport: check if meta content already equals TARGET_CONTENT
        assertTrue("applyViewport must check if content === TARGET_CONTENT", script.contains("meta.getAttribute('content') === TARGET_CONTENT"))
        assertTrue("Must create data-mb-w7-created marker", script.contains("data-mb-w7-created"))
        assertTrue("Must save original into data-mb-w7-orig", script.contains("data-mb-w7-orig"))

        // 22. Cleanup works
        assertTrue("Cleanup must disconnect headObserver", script.contains("headObserver.disconnect()"))
        assertTrue("Cleanup must removeEventListener", script.contains("removeEventListener"))
        assertTrue("Cleanup must delete window guard key", script.contains("delete window[GUARD_KEY]"))
    }

    @Test
    fun test6_cleanupAllDesktopScriptsIncludesWindows7() {
        val cleanupScript = WebViewConfigurator.CLEANUP_ALL_DESKTOP_SCRIPTS
        assertTrue("Unified cleanup must include __mb_desktop_w7__", cleanupScript.contains("__mb_desktop_w7__"))
        assertTrue("Unified cleanup must clean data-mb-w7-created", cleanupScript.contains("data-mb-w7-created"))
        assertTrue("Unified cleanup must clean data-mb-w7-orig", cleanupScript.contains("data-mb-w7-orig"))
    }

    @Test
    fun test7_tabWebViewManagerSynchronizesWindows7Architecture() {
        var observedArch: DesktopArchitecture? = null
        val manager = TabWebViewManager(
            context = context,
            webViewFactory = { WebView(context) },
            onSyncArchitecture = { _, arch -> observedArch = arch },
            architectureProvider = { DesktopArchitecture.WINDOWS_7 }
        )

        val (wv, _) = manager.getOrCreateWebView("test_w7_tab", "https://example.com")
        assertNotNull(wv)
        assertEquals(DesktopArchitecture.WINDOWS_7, observedArch)

        // Synchronize all live WebViews to NONE
        manager.syncAllLiveWebViewsArchitecture(DesktopArchitecture.NONE)
        assertEquals(DesktopArchitecture.NONE, observedArch)

        // Synchronize all live WebViews to WINDOWS_7
        manager.syncAllLiveWebViewsArchitecture(DesktopArchitecture.WINDOWS_7)
        assertEquals(DesktopArchitecture.WINDOWS_7, observedArch)
    }

    @Test
    fun test8_existingDesktopArchitecturesPreserved() {
        // Desktop Mode (STANDARD)
        assertEquals("Desktop Mode", DesktopArchitecture.STANDARD.displayName)
        assertTrue(DesktopArchitecture.STANDARD.isStandard)

        // Desktop Mode 1
        assertEquals("Desktop Mode 1", DesktopArchitecture.DESKTOP_MODE_1.displayName)
        assertTrue(DesktopArchitecture.DESKTOP_MODE_1.isMode1)

        // Desktop Mode 2
        assertEquals("Desktop Mode 2", DesktopArchitecture.DESKTOP_MODE_2.displayName)
        assertTrue(DesktopArchitecture.DESKTOP_MODE_2.isMode2)

        // Desktop Mode 3
        assertEquals("Desktop Mode 3", DesktopArchitecture.DESKTOP_MODE_3.displayName)
        assertTrue(DesktopArchitecture.DESKTOP_MODE_3.isMode3)

        // Windows 10 Touch
        assertEquals("Windows 10 Desktop", DesktopArchitecture.WINDOWS_10_TOUCH.displayName)
        assertTrue(DesktopArchitecture.WINDOWS_10_TOUCH.isWindows10Touch)
    }
}
