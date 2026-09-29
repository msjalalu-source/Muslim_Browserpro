package com.muslim.browser.pro

import android.app.Application
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
    fun test1_desktopModeOff_normalMobileUserAgent() {
        val defaultMobileUa = WebView(context).settings.userAgentString
        val webView = WebView(context)
        MainActivity.applyDesktopModeToWebView(webView, enabled = false, url = "https://example.com")
        assertNotEquals(
            "When Desktop Mode is OFF, userAgentString should not be desktop UA",
            MainActivity.DESKTOP_USER_AGENT,
            webView.settings.userAgentString
        )
        assertEquals(defaultMobileUa, webView.settings.userAgentString)
    }

    @Test
    fun test2_desktopModeOn_desktopUserAgentApplied() {
        val webView = WebView(context)
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://example.com")
        assertEquals(
            "When Desktop Mode is ON, desktop UA should be applied",
            MainActivity.DESKTOP_USER_AGENT,
            webView.settings.userAgentString
        )
        assertTrue("Wide viewport should be enabled", webView.settings.useWideViewPort)
        assertTrue("Overview mode should be enabled", webView.settings.loadWithOverviewMode)
    }

    @Test
    fun test3_toggleOnWhilePageIsOpen_preservesDesktopMode() {
        val defaultMobileUa = WebView(context).settings.userAgentString
        val webView = WebView(context)
        MainActivity.applyDesktopModeToWebView(webView, enabled = false, url = "https://example.com")
        assertEquals(defaultMobileUa, webView.settings.userAgentString)

        // Toggle ON while page is open
        repository.isDesktopModeEnabled = true
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://example.com")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test4_navigateFromGoogleToAnotherWebsite_desktopModeRemainsEnabled() {
        val webView = WebView(context)
        // 1. On Google search
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://www.google.com/search?q=kotlin")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // 2. Navigate from Google search to Wikipedia
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://en.wikipedia.org/wiki/Kotlin")
        assertEquals(
            "Navigating from Google to another website must keep Desktop Mode enabled",
            MainActivity.DESKTOP_USER_AGENT,
            webView.settings.userAgentString
        )
    }

    @Test
    fun test5_openNewTabOrWindowWhileDesktopModeOn_inheritsDesktopMode() {
        repository.isDesktopModeEnabled = true
        val newWebView = WebView(context)
        MainActivity.applyDesktopModeToWebView(newWebView, enabled = repository.isDesktopModeEnabled, url = null)
        assertEquals(
            "Newly created tab/window must inherit Desktop Mode",
            MainActivity.DESKTOP_USER_AGENT,
            newWebView.settings.userAgentString
        )
    }

    @Test
    fun test6_closeAndReopenTab_desktopModeRemainsEnabled() {
        repository.isDesktopModeEnabled = true
        val webView = WebView(context)
        MainActivity.applyDesktopModeToWebView(webView, enabled = repository.isDesktopModeEnabled, url = "https://github.com")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Reopen / switch tab
        MainActivity.applyDesktopModeToWebView(webView, enabled = repository.isDesktopModeEnabled, url = "https://github.com")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test7_browserRestart_persistedDesktopModeStateRestored() {
        repository.isDesktopModeEnabled = true
        assertTrue(repository.isDesktopModeEnabled)

        // Simulate app restart with new repository instance
        val reloadedRepo = SettingsRepository(context)
        assertTrue("Desktop mode preference must persist across restart", reloadedRepo.isDesktopModeEnabled)

        val freshWebView = WebView(context)
        MainActivity.applyDesktopModeToWebView(freshWebView, enabled = reloadedRepo.isDesktopModeEnabled)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, freshWebView.settings.userAgentString)
    }

    @Test
    fun test8_toggleDesktopModeOff_allWebViewsReturnToNormalMobileConfiguration() {
        val defaultMobileUa = WebView(context).settings.userAgentString
        val webView = WebView(context)
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://example.com")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Toggle OFF
        repository.isDesktopModeEnabled = false
        MainActivity.applyDesktopModeToWebView(webView, enabled = false, url = "https://example.com")
        assertEquals("Toggling OFF returns userAgentString to system default mobile", defaultMobileUa, webView.settings.userAgentString)
    }

    @Test
    fun test9_googleSearch_continuesToWorkInDesktopMode() {
        val webView = WebView(context)
        assertFalse(
            "Google search URL must not be detected as an auth URL",
            MainActivity.isGoogleAuthUrl("https://www.google.com/search?q=test")
        )
        assertFalse(
            "Google home page must not be detected as an auth URL",
            MainActivity.isGoogleAuthUrl("https://www.google.com/")
        )

        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://www.google.com/search?q=test"
        )
        assertEquals(
            "Google search must receive desktop UA in Desktop Mode",
            MainActivity.DESKTOP_USER_AGENT,
            webView.settings.userAgentString
        )
    }

    @Test
    fun test10_googleAuthentication_usesSupportedMobileMechanismWithoutBypass() {
        val defaultMobileUa = WebView(context).settings.userAgentString

        // Verify detection of Google Auth endpoints
        assertTrue(
            "accounts.google.com should be identified as Google auth endpoint",
            MainActivity.isGoogleAuthUrl("https://accounts.google.com/signin/v2/identifier")
        )
        assertTrue(
            "accounts.google.com ServiceLogin should be identified as auth",
            MainActivity.isGoogleAuthUrl("https://accounts.google.com/ServiceLogin?service=mail")
        )
        assertTrue(
            "mail.google.com root should be identified as auth entry point",
            MainActivity.isGoogleAuthUrl("https://mail.google.com")
        )
        assertTrue(
            "accounts.youtube.com should be identified as Google auth",
            MainActivity.isGoogleAuthUrl("https://accounts.youtube.com/accounts/SetSID")
        )

        // When Desktop Mode is ON and user visits accounts.google.com:
        val webView = WebView(context)
        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://accounts.google.com/signin/v2/identifier"
        )
        assertEquals(
            "Google auth must use genuine supported mobile configuration to prevent security warnings",
            defaultMobileUa,
            webView.settings.userAgentString
        )

        // After navigating away from Google Auth back to search or other sites, Desktop Mode is restored:
        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://www.google.com/search?q=news"
        )
        assertEquals(
            "Navigating away from Google Auth restores desktop UA",
            MainActivity.DESKTOP_USER_AGENT,
            webView.settings.userAgentString
        )
    }
}
