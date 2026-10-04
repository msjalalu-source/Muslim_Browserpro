package com.muslim.browser.pro

import android.app.Application
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.SettingsRepository
import com.muslim.browser.pro.browser.SimpleDesktopMode
import com.muslim.browser.pro.browser.TabWebViewManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SimpleDesktopModeTest {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.isDesktopModeEnabled = false
        repository.isSimpleDesktopModeEnabled = false
        repository.isWindows10TouchEnabled = false
    }

    // Test 1: New mode OFF -> normal WebView configuration
    @Test
    fun test1_newModeOff_normalWebView() {
        val webView = WebView(context)
        val defaultUa = webView.settings.userAgentString

        SimpleDesktopMode.apply(webView, enabled = false)

        assertEquals("User-Agent should be default/null when OFF", defaultUa, webView.settings.userAgentString)
        assertTrue("Wide viewport must be enabled", webView.settings.useWideViewPort)
        assertTrue("Overview mode must be enabled", webView.settings.loadWithOverviewMode)
    }

    // Test 2: New mode ON -> desktop WebView configuration
    @Test
    fun test2_newModeOn_desktopWebView() {
        val webView = WebView(context)

        SimpleDesktopMode.apply(webView, enabled = true)

        assertEquals("User-Agent should match desktop UA when ON", SimpleDesktopMode.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertFalse("Desktop User-Agent should not contain Mobile", webView.settings.userAgentString.contains("Mobile"))
        assertTrue("Wide viewport must be enabled", webView.settings.useWideViewPort)
        assertTrue("Overview mode must be enabled", webView.settings.loadWithOverviewMode)
    }

    // Test 3: New WebView inherits the new mode upon creation
    @Test
    fun test3_newWebViewInheritsNewMode() {
        val viewModel = BrowserViewModel(context)
        viewModel.toggleSimpleDesktopMode(true)
        assertTrue(viewModel.uiState.value.isSimpleDesktopModeEnabled)

        val tabManager = TabWebViewManager(
            context = context,
            webViewFactory = { id ->
                WebView(context).apply {
                    if (viewModel.uiState.value.isSimpleDesktopModeEnabled) {
                        SimpleDesktopMode.apply(this, true)
                    }
                }
            },
            onSyncDesktopMode = { wv, _ ->
                if (viewModel.uiState.value.isSimpleDesktopModeEnabled) {
                    SimpleDesktopMode.apply(wv, true)
                }
            },
            isDesktopModeProvider = { viewModel.uiState.value.isSimpleDesktopModeEnabled }
        )

        val (newWv, _) = tabManager.getOrCreateWebView("tab_simple_1")
        assertEquals(SimpleDesktopMode.DESKTOP_USER_AGENT, newWv.settings.userAgentString)
    }

    // Test 4: Existing WebView can switch between normal and desktop configuration seamlessly
    @Test
    fun test4_existingWebViewCanSwitchBetweenNormalAndDesktop() {
        val webView = WebView(context)
        val defaultUa = webView.settings.userAgentString

        // 1. Initial state (OFF)
        SimpleDesktopMode.apply(webView, enabled = false)
        assertEquals(defaultUa, webView.settings.userAgentString)

        // 2. Turn ON
        SimpleDesktopMode.apply(webView, enabled = true)
        assertEquals(SimpleDesktopMode.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // 3. Turn OFF
        SimpleDesktopMode.apply(webView, enabled = false)
        assertEquals(defaultUa, webView.settings.userAgentString)

        // 4. Turn ON again
        SimpleDesktopMode.apply(webView, enabled = true)
        assertEquals(SimpleDesktopMode.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    // Test 5: App restart / repository reload preserves the new mode state
    @Test
    fun test5_appRestartPreservesNewMode() {
        // Toggle in first session
        val viewModel1 = BrowserViewModel(context)
        assertFalse(viewModel1.uiState.value.isSimpleDesktopModeEnabled)

        viewModel1.toggleSimpleDesktopMode(true)
        assertTrue(viewModel1.uiState.value.isSimpleDesktopModeEnabled)
        assertTrue(repository.isSimpleDesktopModeEnabled)

        // Simulate app restart: instantiate fresh ViewModel with fresh repository read
        val newRepo = SettingsRepository(context)
        assertTrue("New repository instance must read persistent true", newRepo.isSimpleDesktopModeEnabled)

        val viewModel2 = BrowserViewModel(context)
        assertTrue("Restored ViewModel uiState must preserve simple desktop mode as true", viewModel2.uiState.value.isSimpleDesktopModeEnabled)

        // Disable and test restart again
        viewModel2.toggleSimpleDesktopMode(false)
        assertFalse(viewModel2.uiState.value.isSimpleDesktopModeEnabled)
        assertFalse(newRepo.isSimpleDesktopModeEnabled)

        val viewModel3 = BrowserViewModel(context)
        assertFalse("Restored ViewModel uiState must preserve simple desktop mode as false", viewModel3.uiState.value.isSimpleDesktopModeEnabled)
    }

    // Test 6: Existing Desktop Mode remains functional and intact
    @Test
    fun test6_existingDesktopModeRemainsFunctional() {
        val webView = WebView(context)
        val defaultUa = webView.settings.userAgentString

        // Existing Desktop Mode can be configured as before
        MainActivity.configureBaseSettings(webView, isDarkTheme = false, isDesktopEnabled = true)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)

        // Existing Desktop Mode turns off as before
        MainActivity.applyDesktopMode(webView, isDesktopEnabled = false)
        assertEquals(defaultUa, webView.settings.userAgentString)
    }

    // Test 7: Both modes cannot simultaneously control the same WebView (Mutual Exclusivity)
    @Test
    fun test7_mutualExclusivityEnforced() {
        val viewModel = BrowserViewModel(context)

        // 1. Turn ON existing Desktop Mode
        viewModel.toggleDesktopMode(true)
        assertTrue("Existing mode must be ON", viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse("Simple mode must be OFF", viewModel.uiState.value.isSimpleDesktopModeEnabled)

        // 2. Turn ON Simple Desktop Mode -> Existing mode must automatically turn OFF
        viewModel.toggleSimpleDesktopMode(true)
        assertTrue("Simple mode must be ON", viewModel.uiState.value.isSimpleDesktopModeEnabled)
        assertFalse("Existing mode must be deactivated", viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse("Existing mode repo must be deactivated", repository.isDesktopModeEnabled)

        // 3. Turn ON existing Desktop Mode again -> Simple mode must automatically turn OFF
        viewModel.toggleDesktopMode(true)
        assertTrue("Existing mode must be ON", viewModel.uiState.value.isDesktopModeEnabled)
        assertFalse("Simple mode must be deactivated", viewModel.uiState.value.isSimpleDesktopModeEnabled)
        assertFalse("Simple mode repo must be deactivated", repository.isSimpleDesktopModeEnabled)

        // 4. Turn ON Windows 10 Touch -> Simple mode must also be deactivated
        viewModel.toggleSimpleDesktopMode(true)
        assertTrue(viewModel.uiState.value.isSimpleDesktopModeEnabled)
        viewModel.toggleWindows10Touch(true)
        assertTrue(viewModel.uiState.value.isWindows10TouchEnabled)
        assertFalse(viewModel.uiState.value.isSimpleDesktopModeEnabled)
    }
}
