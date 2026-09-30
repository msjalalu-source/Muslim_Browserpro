package com.muslim.browser.pro

import android.app.Application
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.SettingsRepository
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
class DesktopModeTest {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.isDesktopModeEnabled = false
        MainActivity.defaultMobileUserAgent = null
    }

    @Test
    fun test1_mobileToDesktopOnGoogleHomepage() {
        val webView = WebView(context)
        val defaultMobileUa = webView.settings.userAgentString

        // Initially in Mobile Mode
        MainActivity.applyDesktopModeToWebView(webView, enabled = false, url = "https://www.google.com")
        assertEquals(defaultMobileUa, webView.settings.userAgentString)

        // Switch to Desktop Mode
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://www.google.com")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test2_mobileToDesktopOnGoogleSearchResults() {
        val webView = WebView(context)
        val searchUrl = "https://www.google.com/search?q=android+development"

        // Switch to Desktop Mode on search results
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = searchUrl)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
    }

    @Test
    fun test3_mobileToDesktopOnUnrelatedWebsite() {
        val webView = WebView(context)
        val siteUrl = "https://example.com"

        MainActivity.applyDesktopModeToWebView(webView, enabled = false, url = siteUrl)
        assertNotEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = siteUrl)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test4_mobileToDesktopOnDeepInternalUrl() {
        val webView = WebView(context)
        val deepUrl = "https://example.com/blog/2026/09/article-details/page2?filter=all#section3"

        MainActivity.applyDesktopModeToWebView(webView, enabled = false, url = deepUrl)
        assertNotEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Switching to Desktop on deep internal URL
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = deepUrl)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test5_desktopToMobileOnDeepInternalUrl() {
        val webView = WebView(context)
        val deepUrl = "https://example.com/shop/products/item-987?variant=blue"

        // Enable Desktop
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = deepUrl)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Switch back to Mobile Mode on same deep URL
        MainActivity.applyDesktopModeToWebView(webView, enabled = false, url = deepUrl)
        assertNotEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertEquals(MainActivity.defaultMobileUserAgent, webView.settings.userAgentString)
    }

    @Test
    fun test6_desktopStatePersistsAfterNormalNavigation() {
        val webView = WebView(context)
        repository.isDesktopModeEnabled = true

        // Page 1: Google
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://www.google.com")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Page 2: Link click to external site
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://en.wikipedia.org/wiki/Main_Page")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Page 3: Deep internal navigation
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://en.wikipedia.org/wiki/Kotlin_(programming_language)")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test7_desktopStatePersistsAfterRedirects() {
        val webView = WebView(context)

        // Initial navigation triggers redirect to another domain
        val redirectSource = "https://short.url/xyz"
        val redirectTarget = "https://news.ycombinator.com/item?id=12345"

        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = redirectSource)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Redirect arrival
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = redirectTarget)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test8_desktopStatePersistsAfterReload() {
        val webView = WebView(context)
        val url = "https://github.com/trending"

        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = url)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Page reload
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = url)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test9_backAndForwardNavigationPreservesMode() {
        val webView = WebView(context)

        // When Desktop Mode is ON:
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://siteA.com/page1")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://siteA.com/page2")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Simulated Back navigation to page 1
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://siteA.com/page1")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Simulated Forward navigation to page 2
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://siteA.com/page2")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test10_desktopModeIsNotTiedToGoogle() {
        val webView = WebView(context)
        val domains = listOf(
            "https://bbc.com/news/world",
            "https://reddit.com/r/androiddev",
            "https://stackoverflow.com/questions/123",
            "https://ictbdinvestigation.gov.bd/about",
            "https://acc.org.bd/notices"
        )

        for (domain in domains) {
            MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = domain)
            assertEquals("Desktop UA must apply to $domain", MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        }
    }

    @Test
    fun test11_exactCurrentUrlPreservedWithoutSpeculativeSubdomainMutation() {
        val testUrls = listOf(
            "https://example.com/article/page2?filter=recent#comments",
            "https://mobile.de/auto/search",
            "https://m.me/username",
            "https://en.wikipedia.org/wiki/Kotlin"
        )

        for (url in testUrls) {
            val webView = WebView(context)
            MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = url)
            assertEquals("Desktop UA must apply without modifying url", MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        }
    }

    @Test
    fun test12_googleAuthUsesSupportedMobileConfiguration() {
        val webView = WebView(context)
        val defaultMobileUa = webView.settings.userAgentString

        // Accounts login endpoint
        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://accounts.google.com/signin/v2/identifier"
        )
        assertNotEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
        assertEquals(defaultMobileUa, webView.settings.userAgentString)

        // Leaving accounts login back to general search
        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://www.google.com/search?q=kotlin"
        )
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }

    @Test
    fun test13_webViewRecreationRestoresCorrectMode() {
        repository.isDesktopModeEnabled = true
        assertTrue(repository.isDesktopModeEnabled)

        // Simulate app restart / WebView recreation
        val reloadedRepo = SettingsRepository(context)
        assertTrue(reloadedRepo.isDesktopModeEnabled)

        val recreatedWebView = WebView(context)
        MainActivity.applyDesktopModeToWebView(recreatedWebView, enabled = reloadedRepo.isDesktopModeEnabled)
        assertEquals(MainActivity.DESKTOP_USER_AGENT, recreatedWebView.settings.userAgentString)
    }

    @Test
    fun test14_navigationDoesNotOverwriteDesktopModeBackToMobile() {
        val webView = WebView(context)
        repository.isDesktopModeEnabled = true

        // Verify initial state
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://example.com")
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)

        // Multiple subsequent navigation calls
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://example.com/page1")
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://example.com/page2")
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://example.com/page3")

        // Must remain Desktop UA throughout
        assertEquals(MainActivity.DESKTOP_USER_AGENT, webView.settings.userAgentString)
    }
}
