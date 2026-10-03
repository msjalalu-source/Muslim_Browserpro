package com.muslim.browser.pro

import android.app.Application
import android.os.Bundle
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.BrowserTab
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.SettingsRepository
import com.muslim.browser.pro.browser.TabWebViewManager
import com.muslim.browser.pro.browser.WebViewConfigurator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Windows10TouchProfileTest {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.isDesktopModeEnabled = false
        repository.isWindows10TouchEnabled = false
    }

    @Test
    fun test1_windows10TouchPersistenceInRepository() {
        assertFalse(repository.isWindows10TouchEnabled)

        // Enable Windows 10 Touch
        repository.isWindows10TouchEnabled = true
        assertTrue(repository.isWindows10TouchEnabled)

        // Re-read from repository
        val newRepo = SettingsRepository(context)
        assertTrue(newRepo.isWindows10TouchEnabled)

        // Disable Windows 10 Touch
        repository.isWindows10TouchEnabled = false
        assertFalse(repository.isWindows10TouchEnabled)
    }

    @Test
    fun test2_browserViewModelWindows10TouchToggle() {
        val viewModel = BrowserViewModel(context)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)

        viewModel.toggleWindows10Touch(true)
        assertTrue(viewModel.uiState.value.isWindows10TouchEnabled)
        assertTrue(repository.isWindows10TouchEnabled)

        viewModel.toggleWindows10Touch(false)
        assertFalse(viewModel.uiState.value.isWindows10TouchEnabled)
        assertFalse(repository.isWindows10TouchEnabled)
    }

    @Test
    fun test3_applyWindows10TouchSetsUserAgentAndViewport() {
        val webView = WebView(context)
        MainActivity.configureBaseSettings(webView, isDarkTheme = false, isDesktopEnabled = false, isWindows10TouchEnabled = false)
        val defaultUa = webView.settings.userAgentString

        // Apply Windows 10 Touch
        MainActivity.applyWindows10Touch(webView, isWindows10TouchEnabled = true)
        assertEquals(MainActivity.WINDOWS_10_TOUCH_USER_AGENT, webView.settings.userAgentString)
        assertTrue(webView.settings.userAgentString.contains("Windows NT 10.0"))
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
        assertEquals(100, webView.settings.textZoom)

        // Turn OFF -> Reverts cleanly to default mobile UA
        MainActivity.applyWindows10Touch(webView, isWindows10TouchEnabled = false)
        assertEquals(defaultUa, webView.settings.userAgentString)
        assertFalse(webView.settings.userAgentString.contains("Windows NT"))
    }

    @Test
    fun test4_windows10TouchPrecedenceOverDesktopMode() {
        val webView = WebView(context)
        MainActivity.configureBaseSettings(webView, isDarkTheme = false, isDesktopEnabled = false, isWindows10TouchEnabled = false)
        val defaultUa = webView.settings.userAgentString

        // Both ON: Windows 10 Touch wins (highest precedence)
        assertEquals(
            WebViewConfigurator.BrowserIdentityMode.WINDOWS_10_TOUCH,
            WebViewConfigurator.getActiveIdentityMode(isDesktopEnabled = true, isWindows10TouchEnabled = true)
        )
        MainActivity.applyIdentityMode(webView, isDesktopEnabled = true, isWindows10TouchEnabled = true)
        assertEquals(MainActivity.WINDOWS_10_TOUCH_USER_AGENT, webView.settings.userAgentString)

        // Windows 10 Touch OFF, Desktop Mode ON: Linux Desktop Profile
        assertEquals(
            WebViewConfigurator.BrowserIdentityMode.DESKTOP_LINUX,
            WebViewConfigurator.getActiveIdentityMode(isDesktopEnabled = true, isWindows10TouchEnabled = false)
        )
        MainActivity.applyIdentityMode(webView, isDesktopEnabled = true, isWindows10TouchEnabled = false)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Both OFF: Mobile Profile
        assertEquals(
            WebViewConfigurator.BrowserIdentityMode.MOBILE,
            WebViewConfigurator.getActiveIdentityMode(isDesktopEnabled = false, isWindows10TouchEnabled = false)
        )
        MainActivity.applyIdentityMode(webView, isDesktopEnabled = false, isWindows10TouchEnabled = false)
        assertEquals(defaultUa, webView.settings.userAgentString)
    }

    @Test
    fun test5_windows10TouchPropagationToLiveWebViews() {
        val webView1 = WebView(context)
        val webView2 = WebView(context)
        MainActivity.configureBaseSettings(webView1, isDarkTheme = false, isDesktopEnabled = false, isWindows10TouchEnabled = false)
        MainActivity.configureBaseSettings(webView2, isDarkTheme = false, isDesktopEnabled = false, isWindows10TouchEnabled = false)
        val defaultUa1 = webView1.settings.userAgentString
        val defaultUa2 = webView2.settings.userAgentString

        val tabManager = TabWebViewManager(
            context = context,
            maxLiveWebViews = 4,
            webViewFactory = { id -> if (id == "tab1") webView1 else webView2 }
        )

        tabManager.getOrCreateWebView("tab1")
        tabManager.getOrCreateWebView("tab2")

        // Propagate Windows 10 Touch to all live tabs
        tabManager.forEachLiveWebView {
            MainActivity.applyIdentityMode(it, isDesktopEnabled = false, isWindows10TouchEnabled = true)
        }

        assertEquals(MainActivity.WINDOWS_10_TOUCH_USER_AGENT, webView1.settings.userAgentString)
        assertEquals(MainActivity.WINDOWS_10_TOUCH_USER_AGENT, webView2.settings.userAgentString)

        // Disable Windows 10 Touch -> all live tabs restored to normal mobile
        tabManager.forEachLiveWebView {
            MainActivity.applyIdentityMode(it, isDesktopEnabled = false, isWindows10TouchEnabled = false)
        }

        assertEquals(defaultUa1, webView1.settings.userAgentString)
        assertEquals(defaultUa2, webView2.settings.userAgentString)
    }

    @Test
    fun test6_newlyCreatedTabInheritsWindows10Touch() {
        val viewModel = BrowserViewModel(context)
        viewModel.toggleWindows10Touch(true)

        val tabManager = TabWebViewManager(
            context = context,
            maxLiveWebViews = 4,
            webViewFactory = {
                val wv = WebView(context)
                MainActivity.configureBaseSettings(
                    wv,
                    isDarkTheme = false,
                    isDesktopEnabled = viewModel.uiState.value.isDesktopModeEnabled,
                    isWindows10TouchEnabled = viewModel.uiState.value.isWindows10TouchEnabled
                )
                wv
            }
        )

        val (newWv, _) = tabManager.getOrCreateWebView("new_tab_id")
        assertEquals(MainActivity.WINDOWS_10_TOUCH_USER_AGENT, newWv.settings.userAgentString)
        assertTrue(newWv.settings.useWideViewPort)
    }

    @Test
    fun test7_restoredTabPreservesWindows10Touch() {
        val tabManager = TabWebViewManager(
            context = context,
            maxLiveWebViews = 4,
            webViewFactory = {
                val wv = WebView(context)
                MainActivity.configureBaseSettings(
                    wv,
                    isDarkTheme = false,
                    isDesktopEnabled = false,
                    isWindows10TouchEnabled = true
                )
                wv
            }
        )

        val bundle = Bundle()
        val (restoredWv, wasRestored) = tabManager.getOrCreateWebView(
            tabId = "restored_tab",
            bundle = bundle
        )

        assertTrue(wasRestored)
        assertEquals(MainActivity.WINDOWS_10_TOUCH_USER_AGENT, restoredWv.settings.userAgentString)
    }

    @Test
    fun test8_authenticationEndpointCompatibilityInWindows10Touch() {
        val webView = WebView(context)

        // Normal site: Windows 10 Touch UA
        MainActivity.applyIdentityMode(webView, isDesktopEnabled = false, isWindows10TouchEnabled = true)
        assertEquals(MainActivity.WINDOWS_10_TOUCH_USER_AGENT, webView.settings.userAgentString)

        // Auth endpoint: detected by isAuthenticationUrl -> returns compatible mobile UA
        assertTrue(MainActivity.isAuthenticationUrl("https://accounts.google.com/signin/v2/identifier"))
        assertTrue(MainActivity.isAuthenticationUrl("https://appleid.apple.com/auth/authorize"))
        assertTrue(MainActivity.isAuthenticationUrl("https://login.microsoftonline.com/common/oauth2/v2.0/authorize"))
        assertTrue(MainActivity.isAuthenticationUrl("https://example.com/oauth/authorize"))

        assertFalse(MainActivity.isAuthenticationUrl("https://www.google.com/search?q=test"))
        assertFalse(MainActivity.isAuthenticationUrl("https://en.wikipedia.org/wiki/Special:Search"))
    }

    @Test
    fun test9_completeRestorationOfNormalAndroidBehaviorWhenDisabled() {
        val webView = WebView(context)
        MainActivity.configureBaseSettings(webView, isDarkTheme = false, isDesktopEnabled = false, isWindows10TouchEnabled = false)
        val defaultUa = webView.settings.userAgentString

        // 1. Enable Windows 10 Touch
        MainActivity.applyIdentityMode(webView, isDesktopEnabled = false, isWindows10TouchEnabled = true)
        assertEquals(MainActivity.WINDOWS_10_TOUCH_USER_AGENT, webView.settings.userAgentString)

        // 2. Disable Windows 10 Touch completely
        MainActivity.applyIdentityMode(webView, isDesktopEnabled = false, isWindows10TouchEnabled = false)
        assertEquals("User-Agent must return to default mobile UA", defaultUa, webView.settings.userAgentString)
        assertFalse(webView.settings.userAgentString.contains("Windows NT"))
    }

    @Test
    fun test10_popupWebViewInheritsWindows10TouchProfile() {
        val popupWebView = WebView(context)
        popupWebView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            setSupportMultipleWindows(false)
        }
        MainActivity.applyIdentityMode(popupWebView, isDesktopEnabled = false, isWindows10TouchEnabled = true)
        assertEquals(MainActivity.WINDOWS_10_TOUCH_USER_AGENT, popupWebView.settings.userAgentString)
    }

    @Test
    fun test11_windows10TouchScriptIntegrity() {
        val script = WebViewConfigurator.WINDOWS_10_TOUCH_INJECTION_SCRIPT

        // Verify navigator hardware and platform spoofing
        assertTrue("Must spoof platform to Win32", script.contains("'Win32'"))
        assertTrue("Must spoof vendor to Google Inc.", script.contains("'Google Inc.'"))
        assertTrue("Must spoof maxTouchPoints to 10", script.contains("maxTouchPoints"))
        assertTrue("Must spoof hardwareConcurrency to 8", script.contains("hardwareConcurrency"))
        assertTrue("Must spoof deviceMemory to 8", script.contains("deviceMemory"))

        // Verify UA Client Hints
        assertTrue("Must spoof userAgentData", script.contains("userAgentData"))
        assertTrue("Must set platform to Windows in Client Hints", script.contains("platform: 'Windows'"))
        assertTrue("Must set mobile to false in Client Hints", script.contains("mobile: false"))
        assertTrue("Must set architecture to x86", script.contains("architecture: 'x86'"))
        assertTrue("Must set bitness to 64", script.contains("bitness: '64'"))

        // Verify WebGL GPU unmasked renderer info
        assertTrue("Must spoof WebGL UNMASKED_VENDOR_WEBGL", script.contains("0x9245"))
        assertTrue("Must spoof WebGL UNMASKED_RENDERER_WEBGL", script.contains("0x9246"))
        assertTrue("Must spoof Intel GPU renderer", script.contains("Intel(R) UHD Graphics 620"))

        // Verify CSS matchMedia touch & pointer capabilities
        assertTrue("Must support hover: hover", script.contains("(hover: hover)"))
        assertTrue("Must support pointer: fine", script.contains("(pointer: fine)"))
        assertTrue("Must support any-pointer: coarse", script.contains("(any-pointer: coarse)"))
    }
}
