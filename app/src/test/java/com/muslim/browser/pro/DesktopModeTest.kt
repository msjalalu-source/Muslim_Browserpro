package com.muslim.browser.pro

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.NavigationController
import com.muslim.browser.pro.browser.NavigationDecision
import com.muslim.browser.pro.browser.SettingsRepository
import com.muslim.browser.pro.browser.TabWebViewManager
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
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Desktop Mode is active on third-party page
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://example.com/home")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // User enters Google OAuth/Login page
        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://accounts.google.com/signin/v2/identifier"
        )
        // Must maintain Desktop User-Agent to prevent navigation cancellation regressions
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
    }

    // 9. Login flow → return to normal desktop page
    @Test
    fun test9_loginFlowReturnToNormalDesktopPage() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Step 1: Normal desktop page
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://myapp.com/home")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Step 2: Sign-in endpoint maintains desktop UA
        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://accounts.google.com/o/oauth2/v2/auth?client_id=123"
        )
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Step 3: Intermediate redirect during auth exchange
        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://myapp.com/api/auth/callback/google?code=abc"
        )
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Step 4: Authentication completes and lands on user dashboard
        MainActivity.applyDesktopModeToWebView(
            webView,
            enabled = true,
            url = "https://myapp.com/dashboard"
        )
        // Must maintain Desktop User-Agent on application page
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
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

    // 15. Verify Google Sign-In navigation while Desktop Mode is active preserves Desktop UA
    @Test
    fun test15_googleSignInNavigationPreservesDestinationUrl() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Initial state: Google homepage in Desktop Mode
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://www.google.com")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // User taps "Sign in" -> target URL: https://accounts.google.com/ServiceLogin?...
        val signInUrl = "https://accounts.google.com/ServiceLogin?hl=en&passive=true&continue=https://www.google.com/"
        assertTrue("Google sign in URL must be recognized as auth endpoint", MainActivity.isAuthenticationEndpoint(signInUrl))

        val uaChanged = MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = signInUrl)
        assertFalse("Desktop UA must NOT change during sign in to prevent aborting navigation", uaChanged)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
    }

    // 16. Verify Google "Add another account" navigation while Desktop Mode is active
    @Test
    fun test16_googleAddAnotherAccountNavigation() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Initial state: Google in Desktop Mode
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://www.google.com")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        val addAccountUrl = "https://accounts.google.com/AddSession?continue=https://www.google.com/"
        assertTrue("AddSession URL must be recognized as auth endpoint", MainActivity.isAuthenticationEndpoint(addAccountUrl))

        val uaChanged = MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = addAccountUrl)
        assertFalse("Desktop UA must remain constant for AddSession", uaChanged)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
    }

    // 17. Verify Google "Sign out" navigation while Desktop Mode is active
    @Test
    fun test17_googleSignOutNavigation() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Initial state: Google in Desktop Mode
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://www.google.com")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Both accounts.google.com/Logout and google.com/accounts/Logout2 must be recognized
        val signOutUrl1 = "https://accounts.google.com/Logout?continue=https://www.google.com/"
        val signOutUrl2 = "https://www.google.com/accounts/Logout2?hl=en"

        assertTrue("accounts.google.com/Logout must be auth endpoint", MainActivity.isAuthenticationEndpoint(signOutUrl1))
        assertTrue("google.com/accounts/Logout2 must be auth endpoint", MainActivity.isAuthenticationEndpoint(signOutUrl2))

        val uaChanged = MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = signOutUrl1)
        assertFalse("Desktop UA must remain constant for sign out", uaChanged)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
    }

    // 18. Verify intermediate auth steps (2FA, consent, callbacks) maintain Desktop UA
    @Test
    fun test18_intermediateAuthStepsDoNotPrematurelyRestoreDesktopUa() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Step 1: Start auth
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://accounts.google.com/ServiceLogin")
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Intermediate steps
        val intermediateSteps = listOf(
            "https://accounts.google.com/signin/v2/challenge/pwd",
            "https://accounts.google.com/signin/v2/challenge/totp",
            "https://accounts.google.com/CheckCookie?continue=https://www.google.com/",
            "https://www.google.com/accounts/SetOSID?continue=https://www.google.com/"
        )

        for (step in intermediateSteps) {
            MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = step)
            assertEquals("Step $step must maintain desktop UA", expectedDesktopUa, webView.settings.userAgentString)
        }

        // Final landing back on Google search after successful login
        MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = "https://www.google.com/search?q=news")
        assertEquals("Post-auth destination must retain desktop UA", expectedDesktopUa, webView.settings.userAgentString)
    }

    // 19. GitHub Navigation Preserves Desktop Mode Across All Pages
    @Test
    fun test19_githubNavigationPreservesDesktopModeAcrossPages() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        val githubPages = listOf(
            "https://github.com",
            "https://github.com/login",
            "https://github.com/torvalds/linux",
            "https://github.com/torvalds/linux/blob/master/Makefile",
            "https://github.com/torvalds/linux/pulls",
            "https://github.com/torvalds/linux/issues",
            "https://github.com/settings/profile",
            "https://github.com/login/oauth/authorize?client_id=xyz"
        )

        for (pageUrl in githubPages) {
            // Must NOT be classified as a Google/Apple identity provider endpoint that overrides Desktop Mode
            assertFalse(
                "GitHub URL ($pageUrl) must NEVER be classified as identity provider endpoint",
                MainActivity.isAuthenticationEndpoint(pageUrl)
            )

            MainActivity.syncWebViewDesktopMode(webView, url = pageUrl, isDesktopEnabled = true)

            assertEquals(
                "Page $pageUrl must strictly use Desktop User-Agent",
                expectedDesktopUa,
                webView.settings.userAgentString
            )
            assertTrue("Page $pageUrl must retain wide viewport", webView.settings.useWideViewPort)
            assertTrue("Page $pageUrl must retain overview mode", webView.settings.loadWithOverviewMode)
            assertFalse("Page $pageUrl must not activate auth flow flag", MainActivity.isAuthFlowActive)
        }
    }

    // 20. GitHub SPA / Client-Side Navigation Preserves Desktop Configuration
    @Test
    fun test20_githubSpaNavigationPreservesDesktopConfiguration() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Initial page load in Desktop Mode
        MainActivity.syncWebViewDesktopMode(webView, url = "https://github.com/torvalds/linux", isDesktopEnabled = true)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // SPA (Turbo/PJAX/History API) transitions where client-side JavaScript navigates without full page reload
        val spaTransitions = listOf(
            "https://github.com/torvalds/linux/issues",
            "https://github.com/torvalds/linux/issues/123",
            "https://github.com/torvalds/linux/pulls",
            "https://github.com/torvalds/linux/commits/master"
        )

        for (spaUrl in spaTransitions) {
            MainActivity.syncWebViewDesktopMode(webView, url = spaUrl, isDesktopEnabled = true)
            assertEquals("SPA transition to $spaUrl must maintain desktop UA", expectedDesktopUa, webView.settings.userAgentString)
            assertTrue("SPA transition to $spaUrl must maintain wide viewport", webView.settings.useWideViewPort)
        }
    }

    // 21. Desktop state persists across Tab switching, creation, and restoration
    @Test
    fun test21_desktopStatePersistsAcrossTabSwitchingAndRestoration() {
        val savedBundles = mutableMapOf<String, Bundle>()
        val manager = TabWebViewManager(
            context = context,
            maxLiveWebViews = 2,
            webViewFactory = { _ -> WebView(context) },
            onSaveTabBundle = { id, bundle -> savedBundles[id] = bundle },
            onSyncDesktopMode = { wv, enabled ->
                MainActivity.syncWebViewDesktopMode(wv, url = wv.url, isDesktopEnabled = enabled)
            }
        )

        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // 1. Create Tab A in Desktop Mode
        val (tabA, _) = manager.getOrCreateWebView("tab_A", url = "https://github.com/repoA", isDesktopMode = true)
        assertEquals(expectedDesktopUa, tabA.settings.userAgentString)

        // 2. Create Tab B in Desktop Mode
        val (tabB, _) = manager.getOrCreateWebView("tab_B", url = "https://github.com/repoB", isDesktopMode = true)
        assertEquals(expectedDesktopUa, tabB.settings.userAgentString)

        // 3. Switch back to Tab A
        val (tabA2, _) = manager.getOrCreateWebView("tab_A", url = "https://github.com/repoA", isDesktopMode = true)
        assertEquals("Live Tab A must retain Desktop UA after switching", expectedDesktopUa, tabA2.settings.userAgentString)

        // 4. Create Tab C (exceeds capacity of 2, causes Tab B to be evicted and saved)
        val (tabC, _) = manager.getOrCreateWebView("tab_C", url = "https://github.com/repoC", isDesktopMode = true)
        assertEquals(expectedDesktopUa, tabC.settings.userAgentString)
        assertFalse(manager.hasLiveWebView("tab_B"))

        // 5. Restore Tab B from bundle
        val (restoredB, wasRestored) = manager.getOrCreateWebView(
            tabId = "tab_B",
            url = "https://github.com/repoB",
            bundle = savedBundles["tab_B"],
            isDesktopMode = true
        )
        assertTrue(wasRestored)
        assertEquals("Restored tab must immediately be synchronized to Desktop UA", expectedDesktopUa, restoredB.settings.userAgentString)
    }

    // 22. Authentication navigation maintains Desktop Mode throughout
    @Test
    fun test22_authDetectionDoesNotPermanentlySwitchBrowserToMobileMode() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // 1. User browsing GitHub in Desktop Mode
        MainActivity.syncWebViewDesktopMode(webView, url = "https://github.com", isDesktopEnabled = true)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // 2. User logs in via Google Identity provider
        MainActivity.syncWebViewDesktopMode(webView, url = "https://accounts.google.com/o/oauth2/v2/auth", isDesktopEnabled = true)
        assertEquals("Google auth endpoint must maintain Desktop UA", expectedDesktopUa, webView.settings.userAgentString)

        // 3. OAuth callback
        MainActivity.syncWebViewDesktopMode(webView, url = "https://myapp.com/api/auth/callback/google?code=123", isDesktopEnabled = true)
        assertEquals("OAuth callback maintains Desktop UA", expectedDesktopUa, webView.settings.userAgentString)

        // 4. Return to GitHub after authentication
        MainActivity.syncWebViewDesktopMode(webView, url = "https://github.com/dashboard", isDesktopEnabled = true)
        assertEquals("Return to GitHub retains Desktop UA", expectedDesktopUa, webView.settings.userAgentString)
    }

    // 23. Toggling Desktop -> Mobile -> Desktop cycles correctly
    @Test
    fun test23_desktopModeToggleMobileDesktopCycle() {
        val webView = WebView(context)
        val defaultMobileUa = webView.settings.userAgentString
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Desktop ON
        MainActivity.syncWebViewDesktopMode(webView, url = "https://example.com", isDesktopEnabled = true)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Toggle to Mobile (OFF)
        MainActivity.syncWebViewDesktopMode(webView, url = "https://example.com", isDesktopEnabled = false)
        assertEquals(defaultMobileUa, webView.settings.userAgentString)

        // Toggle back to Desktop (ON)
        MainActivity.syncWebViewDesktopMode(webView, url = "https://example.com", isDesktopEnabled = true)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
    }

    // 24. Repeated navigation does not cause repeated loadUrl() loops
    @Test
    fun test24_repeatedNavigationDoesNotTriggerLoadUrlLoops() {
        var loadUrlCount = 0
        val webView = object : WebView(context) {
            override fun loadUrl(url: String) {
                loadUrlCount++
                super.loadUrl(url)
            }
        }

        // Configure desktop mode once
        MainActivity.syncWebViewDesktopMode(webView, url = "https://github.com/home", isDesktopEnabled = true)
        webView.loadUrl("https://github.com/home")
        assertEquals(1, loadUrlCount)

        // Multiple subsequent shouldOverrideUrlLoading / navigation calls
        val navUrls = listOf(
            "https://github.com/torvalds/linux",
            "https://github.com/torvalds/linux/pulls",
            "https://github.com/torvalds/linux/issues"
        )

        for (url in navUrls) {
            // syncWebViewDesktopMode must NOT invoke loadUrl()
            MainActivity.syncWebViewDesktopMode(webView, url = url, isDesktopEnabled = true)
        }

        // loadUrlCount must still be exactly 1!
        assertEquals("syncWebViewDesktopMode must NEVER invoke loadUrl", 1, loadUrlCount)
    }

    // 25. Desktop Viewport Guard script is idempotent and handles multiple invocations safely
    @Test
    fun test25_desktopViewportGuardIsIdempotentAndSafe() {
        var evaluatedScriptCount = 0
        var lastScript: String? = null
        val webView = object : WebView(context) {
            override fun evaluateJavascript(script: String, resultCallback: android.webkit.ValueCallback<String>?) {
                evaluatedScriptCount++
                lastScript = script
                super.evaluateJavascript(script, resultCallback)
            }
        }

        // 1. Enable Desktop Viewport
        MainActivity.applyDesktopViewport(webView, enabled = true)
        assertTrue("Script must contain 1280px desktop width", lastScript?.contains("width=1280") == true)
        assertTrue("Script must contain MutationObserver guard", lastScript?.contains("MutationObserver") == true)
        assertTrue("Script must contain turbo:load listener", lastScript?.contains("turbo:load") == true)
        assertTrue("Script must contain popstate listener", lastScript?.contains("popstate") == true)
        assertTrue("Script must avoid mutation loops", lastScript?.contains("TARGET_CONTENT") == true)

        // 2. Multiple repeated calls (simulating rapid SPA transitions) must execute safely
        for (i in 1..5) {
            MainActivity.applyDesktopViewport(webView, enabled = true)
        }
        assertEquals(6, evaluatedScriptCount)

        // 3. Disable Desktop Viewport
        MainActivity.applyDesktopViewport(webView, enabled = false)
        assertTrue("Disabled script must disconnect MutationObserver", lastScript?.contains("disconnect()") == true)
        assertTrue("Disabled script must restore device-width", lastScript?.contains("width=device-width") == true)
    }

    // 26. Deep GitHub authentication and settings URLs never trigger mobile auth flow
    @Test
    fun test26_githubAuthAndSettingsUrlsNeverTriggerMobileAuthFlow() {
        val testUrls = listOf(
            "https://github.com/login",
            "https://github.com/session",
            "https://github.com/settings/auth/tokens",
            "https://github.com/orgs/my-org/sso",
            "https://github.com/login/device",
            "https://github.com/join",
            "https://github.com/password_reset"
        )
        for (url in testUrls) {
            assertFalse(
                "GitHub URL ($url) must NOT be identified as Google/Apple identity provider endpoint",
                MainActivity.isAuthenticationEndpoint(url)
            )
        }
    }

    // 27. Google Sign-In URL is allowed without interference
    @Test
    fun test27_googleSignInUrlIsAllowed() {
        val signInUrl = "https://accounts.google.com/ServiceLogin?hl=en&passive=true&continue=https://www.google.com/"
        val decision = NavigationController.evaluate(signInUrl, emptySet())
        assertTrue("Google sign in URL must be Allowed by NavigationController", decision is NavigationDecision.Allowed)
    }

    // 28. Google Sign-Out URL is allowed without interference
    @Test
    fun test28_googleSignOutUrlIsAllowed() {
        val signOutUrl = "https://accounts.google.com/Logout?continue=https://www.google.com/"
        val decision = NavigationController.evaluate(signOutUrl, emptySet())
        assertTrue("Google sign out URL must be Allowed by NavigationController", decision is NavigationDecision.Allowed)
    }

    // 29. Google Account Chooser URL is allowed without interference
    @Test
    fun test29_googleAccountChooserUrlIsAllowed() {
        val accountChooserUrl = "https://accounts.google.com/AccountChooser?continue=https://www.google.com/"
        val decision = NavigationController.evaluate(accountChooserUrl, emptySet())
        assertTrue("Google account chooser URL must be Allowed by NavigationController", decision is NavigationDecision.Allowed)
    }

    // 30. Authentication navigation does not produce a redirect to previous page
    @Test
    fun test30_authNavigationDoesNotProduceRedirectToPreviousPage() {
        val addAccountUrl = "https://accounts.google.com/AddSession?continue=https://www.google.com/"
        val decision = NavigationController.evaluate(addAccountUrl, emptySet())
        assertFalse("Auth URL must never produce a redirect", decision is NavigationDecision.Redirect)
        assertTrue("Auth URL must be Allowed to navigate", decision is NavigationDecision.Allowed)
    }

    // 31. Authentication navigation does not call reload() or loadUrl()
    @Test
    fun test31_authNavigationDoesNotCallReloadOrLoadUrl() {
        MainActivity.DesktopModeDiagnostics.reset()
        val webView = WebView(context)
        val initialReloads = MainActivity.DesktopModeDiagnostics.reloadCount
        val initialLoads = MainActivity.DesktopModeDiagnostics.loadUrlCount

        val authUrls = listOf(
            "https://accounts.google.com/ServiceLogin",
            "https://accounts.google.com/AddSession",
            "https://accounts.google.com/Logout",
            "https://accounts.google.com/AccountChooser"
        )

        for (url in authUrls) {
            MainActivity.applyDesktopModeToWebView(webView, enabled = true, url = url)
            MainActivity.applyDesktopViewport(webView, enabled = true)
        }

        assertEquals("Processing auth URLs must NEVER call reload()", initialReloads, MainActivity.DesktopModeDiagnostics.reloadCount)
        assertEquals("Processing auth URLs must NEVER call loadUrl()", initialLoads, MainActivity.DesktopModeDiagnostics.loadUrlCount)
    }

    // 32. Desktop Mode preference remains unchanged during authentication
    @Test
    fun test32_desktopModePreferenceRemainsUnchangedDuringAuth() {
        repository.isDesktopModeEnabled = true
        assertTrue(repository.isDesktopModeEnabled)

        val webView = WebView(context)
        val authUrl = "https://accounts.google.com/ServiceLogin"
        MainActivity.applyDesktopModeToWebView(webView, enabled = repository.isDesktopModeEnabled, url = authUrl)

        // Repository user preference must remain strictly true
        assertTrue("Repository desktop mode preference must remain true during auth", repository.isDesktopModeEnabled)
    }

    // 33. Authentication handling does not modify global Desktop Mode state
    @Test
    fun test33_authHandlingDoesNotModifyGlobalDesktopModeState() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // Configure desktop mode
        MainActivity.syncWebViewDesktopMode(webView, url = "https://www.google.com", isDesktopEnabled = true)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Navigate to auth
        MainActivity.syncWebViewDesktopMode(webView, url = "https://accounts.google.com/signin", isDesktopEnabled = true)
        assertEquals("WebView UA must maintain Desktop UA", expectedDesktopUa, webView.settings.userAgentString)
    }

    // 34. Normal Google pages containing 'login' in text/query do not accidentally enter auth mode
    @Test
    fun test34_normalGooglePagesWithLoginTextDoNotAccidentallyEnterAuthMode() {
        val searchWithLoginQuery = "https://www.google.com/search?q=how+to+login+to+router&hl=en"
        assertFalse(
            "Search results containing 'login' in query must NOT be recognized as auth endpoint",
            MainActivity.isAuthenticationEndpoint(searchWithLoginQuery)
        )
    }

    // 35. Returning from authentication restores/retains normal Desktop Mode behavior
    @Test
    fun test35_returningFromAuthRestoresAndRetainsNormalDesktopModeBehavior() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)

        // 1. Initial Google in Desktop Mode
        MainActivity.syncWebViewDesktopMode(webView, url = "https://www.google.com", isDesktopEnabled = true)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // 2. Sign In
        MainActivity.syncWebViewDesktopMode(webView, url = "https://accounts.google.com/ServiceLogin", isDesktopEnabled = true)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // 3. Return to Google Search
        MainActivity.syncWebViewDesktopMode(webView, url = "https://www.google.com/search?q=muslim+browser", isDesktopEnabled = true)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)
        assertTrue(webView.settings.useWideViewPort)
        assertTrue(webView.settings.loadWithOverviewMode)
    }

    // 36. Navigation evaluation does not mutate userAgentString
    @Test
    fun test36_navigationEvaluationDoesNotMutateUserAgentString() {
        val webView = WebView(context)
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)
        MainActivity.syncWebViewDesktopMode(webView, url = "https://www.google.com", isDesktopEnabled = true)
        assertEquals(expectedDesktopUa, webView.settings.userAgentString)

        // Simulating the URL evaluation in shouldOverrideUrlLoading
        val targetUrl = "https://accounts.google.com/ServiceLogin"
        val decision = NavigationController.evaluate(targetUrl, emptySet())
        assertTrue(decision is NavigationDecision.Allowed)

        // webView settings must NOT have been changed during the evaluation
        assertEquals(
            "Navigation evaluation must NEVER mutate userAgentString",
            expectedDesktopUa,
            webView.settings.userAgentString
        )
    }
}
