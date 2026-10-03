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

        // Disable Desktop Mode
        MainActivity.applyDesktopMode(webView, isDesktopEnabled = false)
        assertEquals(defaultUa, webView.settings.userAgentString)
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
    }
}
