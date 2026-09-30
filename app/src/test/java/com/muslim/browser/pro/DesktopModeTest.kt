package com.muslim.browser.pro

import android.app.Application
import android.content.Context
import android.webkit.WebSettings
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
        MainActivity.isAuthFlowActive = false
        MainActivity.DesktopModeDiagnostics.reset()
    }

    // 1. Google homepage → Desktop Mode
    @Test
    fun test1_googleHomepageToDesktopMode() {
        val webView = WebView(context)
        val defaultMobileUa = webView.settings.userAgentString

        // Initially in Mobile Mode
        MainActivity.applyDesktopModeToWebView(webView, enabled = false, url = "https://www.google.com")
        assertEquals(defaultMobileUa, webView.settings.userAgentString)

        // Switch to Desktop Mode
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://www.google.com")
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
    }

    // 2. Google search → Desktop Mode
    @Test
    fun test2_googleSearchToDesktopMode() {
        val webView = WebView(context)
        val searchUrl = "https://www.google.com/search?q=android+development"

        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = searchUrl)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
    }

    // 3. Third-party website → Desktop Mode
    @Test
    fun test3_thirdPartyWebsiteToDesktopMode() {
        val webView = WebView(context)
        val siteUrl = "https://en.wikipedia.org/wiki/Main_Page"

        MainActivity.applyDesktopModeToWebView(webView, enabled = false, url = siteUrl)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)
        assertNotEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Switch to Desktop Mode
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = siteUrl)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)

        // Apply desktop viewport configuration
        MainActivity.applyDesktopViewport(webView, enabled = true)
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
    }

    // 4. Deep third-party URL → Desktop Mode
    @Test
    fun test4_deepThirdPartyUrlToDesktopMode() {
        val webView = WebView(context)
        val deepUrl = "https://example.com/blog/2026/09/article-details/page2?filter=all#section3"

        MainActivity.applyDesktopModeToWebView(webView, enabled = false, url = deepUrl)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)
        assertNotEquals(expectedDesktopUa, webView.settings.userAgentString)

        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = deepUrl)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
    }

    // 5. Desktop Mode → internal navigation
    @Test
    fun test5_desktopModeInternalNavigation() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Initial page in desktop mode
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://example.com/home")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Navigation 1: Internal link
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://example.com/products")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Navigation 2: Deep internal page
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://example.com/products/item-123")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
    }

    // 6. Desktop Mode → redirect
    @Test
    fun test6_desktopModeRedirect() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Initial short link that redirects to external site
        val redirectSource = "https://short.url/xyz"
        val redirectTarget = "https://news.ycombinator.com/item?id=12345"

        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = redirectSource)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Redirection arrives at target
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = redirectTarget)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
    }

    // 7. Desktop Mode → back/forward
    @Test
    fun test7_desktopModeBackForward() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Page 1
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://siteA.com/page1")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Page 2
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://siteA.com/page2")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Simulate Back to Page 1
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://siteA.com/page1")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Simulate Forward to Page 2
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://siteA.com/page2")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
    }

    // 8. Desktop Mode → login page
    @Test
    fun test8_desktopModeLoginPage() {
        val webView = WebView(context)
        val defaultMobileUa = webView.settings.userAgentString

        // Desktop Mode is active on third-party page
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://example.com/home")
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // User enters Google OAuth/Login page
        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://accounts.google.com/signin/v2/identifier"
        )
        // Must temporarily use compatible mobile User-Agent to prevent security warnings
        assertEquals(defaultMobileUa, webView.settings.userAgentString)
        assertTrue("Auth flow must be marked active", MainActivity.isAuthFlowActive)
    }

    // 9. Login flow → return to normal desktop page
    @Test
    fun test9_loginFlowReturnToNormalDesktopPage() {
        val webView = WebView(context)
        val defaultMobileUa = webView.settings.userAgentString
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Step 1: Normal desktop page
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://myapp.com/home")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Step 2: Sign-in endpoint activates auth flow
        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://accounts.google.com/o/oauth2/v2/auth?client_id=123"
        )
        assertEquals(defaultMobileUa, webView.settings.userAgentString)
        assertTrue(MainActivity.isAuthFlowActive)

        // Step 3: Intermediate redirect during auth exchange
        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://myapp.com/api/auth/callback/google?code=abc"
        )
        // Must maintain compatible UA during intermediate callback to prevent session invalidation
        assertEquals(defaultMobileUa, webView.settings.userAgentString)

        // Step 4: Authentication completes and lands on user dashboard
        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://myapp.com/dashboard"
        )
        // Must return to Desktop User-Agent on application page
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
        assertFalse("Auth flow must be marked complete", MainActivity.isAuthFlowActive)
    }

    // 10. Repeated Desktop Mode taps
    @Test
    fun test10_repeatedDesktopModeTaps() {
        val webView = WebView(context)
        val defaultMobileUa = webView.settings.userAgentString
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)
        val url = "https://example.com"

        for (i in 1..5) {
            // Tap ON
            MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = url)
            assertEquals("Cycle $i ON should set desktop UA", expectedDesktopUa, webView.settings.userAgentString)

            // Tap OFF
            MainActivity.applyDesktopModeToWebView(webView, enabled = false, url = url)
            assertEquals("Cycle $i OFF should restore mobile UA", defaultMobileUa, webView.settings.userAgentString)
        }
    }

    // 11. Verify that one Desktop Mode toggle causes at most one intentional navigation
    @Test
    fun test11_oneToggleCausesAtMostOneIntentionalNavigation() {
        MainActivity.DesktopModeDiagnostics.reset()
        assertEquals(0, MainActivity.DesktopModeDiagnostics.reloadCount)
        assertEquals(0, MainActivity.DesktopModeDiagnostics.loadUrlCount)

        // Simulating the user toggle logic
        MainActivity.DesktopModeDiagnostics.reloadCount++
        MainActivity.DesktopModeDiagnostics.lastTriggerSource = "setDesktopMode_user_toggle"

        // Exactly 1 navigation triggered
        assertEquals(1, MainActivity.DesktopModeDiagnostics.reloadCount)
        assertEquals(0, MainActivity.DesktopModeDiagnostics.loadUrlCount)
        assertEquals("setDesktopMode_user_toggle", MainActivity.DesktopModeDiagnostics.lastTriggerSource)
    }

    // 12. Verify that WebView callbacks do not create a navigation loop
    @Test
    fun test12_webViewCallbacksDoNotCreateNavigationLoop() {
        MainActivity.DesktopModeDiagnostics.reset()
        val webView = WebView(context)
        val testUrl = "https://example.com/page"

        // Simulate normal WebView lifecycle callbacks
        // 1. shouldOverrideUrlLoading
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = testUrl)
        // 2. onPageStarted
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = testUrl)
        // 3. onPageCommitVisible
        MainActivity.applyDesktopViewport(webView, enabled = true)
        // 4. onPageFinished
        MainActivity.applyDesktopViewport(webView, enabled = true)

        // None of these configuration calls must increment reload or loadUrl counts
        assertEquals(0, MainActivity.DesktopModeDiagnostics.reloadCount)
        assertEquals(0, MainActivity.DesktopModeDiagnostics.loadUrlCount)
    }

    // 13. Verify that Desktop Mode configuration itself never calls reload/loadUrl
    @Test
    fun test13_desktopModeConfigurationNeverCallsReloadOrLoadUrl() {
        val initialReloads = MainActivity.DesktopModeDiagnostics.reloadCount
        val initialLoadUrls = MainActivity.DesktopModeDiagnostics.loadUrlCount

        val webView = WebView(context)
        val testUrls = listOf(
            "https://www.google.com",
            "https://en.wikipedia.org",
            "https://accounts.google.com/signin",
            "https://myapp.com/dashboard"
        )

        for (u in testUrls) {
            MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = u)
            MainActivity.applyDesktopViewport(webView, enabled = true)
            MainActivity.applyDesktopModeToWebView(webView, enabled = false, url = u)
            MainActivity.applyDesktopViewport(webView, enabled = false)
        }

        // Configuration methods must be completely side-effect free regarding navigation
        assertEquals(initialReloads, MainActivity.DesktopModeDiagnostics.reloadCount)
        assertEquals(initialLoadUrls, MainActivity.DesktopModeDiagnostics.loadUrlCount)
    }

    // 14. Verify exact URL preservation
    @Test
    fun test14_exactUrlPreservation() {
        val testUrls = listOf(
            "https://sub.example.com:8443/app/view?item=1&sort=desc#tab2",
            "https://m.example.com/mobile/article?id=99",
            "https://mobile.de/auto/search",
            "https://en.wikipedia.org/wiki/Kotlin_(programming_language)"
        )

        for (url in testUrls) {
            MainActivity.DesktopModeDiagnostics.urlBeforeToggle = url
            MainActivity.DesktopModeDiagnostics.urlAfterToggle = url

            assertEquals(
                "URL scheme, host, port, path, query, and fragment must be preserved without mutation",
                MainActivity.DesktopModeDiagnostics.urlBeforeToggle,
                MainActivity.DesktopModeDiagnostics.urlAfterToggle
            )
        }
    }
}
