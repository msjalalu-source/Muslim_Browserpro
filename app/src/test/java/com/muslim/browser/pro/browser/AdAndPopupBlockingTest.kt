package com.muslim.browser.pro.browser

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AdAndPopupBlockingTest {

    @Test
    fun testAdDomainsBlocked() {
        // Direct ad domains
        assertTrue(ProtectionEngine.isAdOrTracker("doubleclick.net"))
        assertTrue(ProtectionEngine.isAdOrTracker("googlesyndication.com"))
        assertTrue(ProtectionEngine.isAdOrTracker("googleadservices.com"))
        assertTrue(ProtectionEngine.isAdOrTracker("criteo.com"))
        assertTrue(ProtectionEngine.isAdOrTracker("adnxs.com"))
        assertTrue(ProtectionEngine.isAdOrTracker("popads.net"))
        assertTrue(ProtectionEngine.isAdOrTracker("popcash.net"))
        assertTrue(ProtectionEngine.isAdOrTracker("adsterra.com"))
        assertTrue(ProtectionEngine.isAdOrTracker("propellerads.com"))
        assertTrue(ProtectionEngine.isAdOrTracker("google-analytics.com"))
        assertTrue(ProtectionEngine.isAdOrTracker("clarity.ms"))
    }

    @Test
    fun testAdSubdomainsBlocked() {
        // Deep subdomains of ad networks
        assertTrue(ProtectionEngine.isAdOrTracker("ad.doubleclick.net"))
        assertTrue(ProtectionEngine.isAdOrTracker("securepubads.g.doubleclick.net"))
        assertTrue(ProtectionEngine.isAdOrTracker("pagead2.googlesyndication.com"))
        assertTrue(ProtectionEngine.isAdOrTracker("static.criteo.net"))
        assertTrue(ProtectionEngine.isAdOrTracker("serve.popads.net"))
        assertTrue(ProtectionEngine.isAdOrTracker("cdn.adsterra.com"))
    }

    @Test
    fun testGoogleAdPathsBlocked() {
        val pageAdUri = Uri.parse("https://www.google.com/pagead/conversion/12345/")
        assertTrue(ProtectionEngine.isAdOrTracker(pageAdUri))

        val adServiceUri = Uri.parse("https://google.com/adservice/test")
        assertTrue(ProtectionEngine.isAdOrTracker(adServiceUri))
    }

    @Test
    fun testSearchQueriesNotBlockedAsAds() {
        // Searching for ad terms on DuckDuckGo must NEVER be blocked
        val ddgSearchUri = Uri.parse("https://safe.duckduckgo.com/?q=what+is+doubleclick.net")
        assertFalse(ProtectionEngine.isAdOrTracker(ddgSearchUri))
        assertFalse(ProtectionEngine.isAdRequest(ddgSearchUri))

        // Searching for ad networks on Google search must NEVER be blocked
        val googleSearchUri = Uri.parse("https://www.google.com/search?q=criteo+stock+price")
        assertFalse(ProtectionEngine.isAdOrTracker(googleSearchUri))
        assertFalse(ProtectionEngine.isAdRequest(googleSearchUri))

        // Normal URLs with query parameters mentioning ad keywords
        val normalArticleUri = Uri.parse("https://en.wikipedia.org/wiki/DoubleClick")
        assertFalse(ProtectionEngine.isAdOrTracker(normalArticleUri))

        val normalNewsWithParam = Uri.parse("https://techcrunch.com/article?source=google-analytics.com")
        assertFalse(ProtectionEngine.isAdOrTracker(normalNewsWithParam))
    }

    @Test
    fun testDirectUrlCheckRespectsAdBlockingSetting() {
        val adUrl = "https://doubleclick.net/test"

        // When enabled (default)
        val blockedResult = ProtectionEngine.checkDirectUrl(
            url = adUrl,
            customKeywords = emptySet(),
            isAdBlockingEnabled = true
        )
        assertTrue(blockedResult is ProtectionEngine.FilterResult.Blocked)
        assertEquals("Ad Protection", (blockedResult as ProtectionEngine.FilterResult.Blocked).reason)

        // When disabled
        val allowedResult = ProtectionEngine.checkDirectUrl(
            url = adUrl,
            customKeywords = emptySet(),
            isAdBlockingEnabled = false
        )
        assertTrue(allowedResult is ProtectionEngine.FilterResult.Allowed)
    }

    @Test
    fun testNavigationControllerEvaluatesAdBlockingSetting() {
        val adUrl = "https://adsterra.com/banner"

        // Enabled
        val blockedDecision = NavigationController.evaluate(
            url = adUrl,
            customKeywords = emptySet(),
            isAdBlockingEnabled = true
        )
        assertTrue(blockedDecision is NavigationDecision.Blocked)
        assertEquals("Ad Protection", (blockedDecision as NavigationDecision.Blocked).reason)

        // Disabled
        val allowedDecision = NavigationController.evaluate(
            url = adUrl,
            customKeywords = emptySet(),
            isAdBlockingEnabled = false
        )
        assertTrue(allowedDecision is NavigationDecision.Allowed)
    }

    @Test
    fun testLegitimateWebsitesAllowed() {
        val sites = listOf(
            "https://en.wikipedia.org/wiki/Islamic_world",
            "https://quran.com",
            "https://sunnah.com",
            "https://github.com",
            "https://developer.android.com"
        )
        for (site in sites) {
            assertFalse(ProtectionEngine.isAdOrTracker(site))
            val decision = NavigationController.evaluate(site, emptySet(), isAdBlockingEnabled = true)
            assertTrue("Expected allowed for $site", decision is NavigationDecision.Allowed)
        }
    }
}
