package com.muslim.browser.pro.browser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GoogleSafeSearchOptimizationTest {

    private val customKeywords = setOf("gambling", "casino", "adultcontent", "blockedword")
    private val normalizedKeywords = listOf("gambling", "casino", "adultcontent", "blockedword")

    @Before
    fun setUp() {
        NavigationController.clearSearchUrlValidation()
    }

    @Test
    fun test1_normalGoogleSearchUrlGeneration() {
        val query = "halal restaurants near me"
        val generatedUrl = ProtectionEngine.buildGoogleSafeSearchUrl(query)

        // Verify correct construction
        assertTrue(generatedUrl.startsWith("https://www.google.com/search?q="))
        assertTrue(generatedUrl.contains("safe=active"))
        assertTrue(generatedUrl.contains("halal+restaurants+near+me"))

        // Verify safe=active is detected
        assertTrue(ProtectionEngine.isGoogleSafeSearchUrl(generatedUrl))
        assertTrue(ProtectionEngine.isSafeSearchUrl(generatedUrl))
    }

    @Test
    fun test2_keywordBlockedGoogleSearchQuery() {
        val query = "online casino free spins"
        val blocked = ProtectionEngine.isBlockedByCustomKeywords(query, customKeywords, normalizedKeywords)
        assertEquals("casino", blocked)
    }

    @Test
    fun test3_fastPathApplicationGeneratedGoogleSearch() {
        val query = "islamic history"
        // 1. App generates safe search URL
        val safeUrl = ProtectionEngine.buildGoogleSafeSearchUrl(query)
        assertTrue(NavigationController.isSearchUrlValidated(safeUrl))

        // 2. NavigationController evaluates the app-generated URL via fast-path
        val decision = NavigationController.evaluate(
            url = safeUrl,
            customKeywords = customKeywords,
            normalizedKeywords = normalizedKeywords,
            searchEngine = SearchEngine.GOOGLE
        )

        // Fast-path allows without redundant parsing or keyword loops
        assertEquals(NavigationDecision.Allowed, decision)

        // Fast-path marker consumed
        assertFalse(NavigationController.isSearchUrlValidated(safeUrl))
    }

    @Test
    fun test4_directGoogleSearchUrlWithBlockedKeyword() {
        // Direct / untrusted navigation to Google search with a blocked keyword
        val directUrl = "https://www.google.com/search?q=online+gambling+games&safe=active"

        val decision = NavigationController.evaluate(
            url = directUrl,
            customKeywords = customKeywords,
            normalizedKeywords = normalizedKeywords,
            searchEngine = SearchEngine.GOOGLE
        )

        assertTrue(decision is NavigationDecision.Blocked)
        val blocked = decision as NavigationDecision.Blocked
        assertEquals("Custom Keyword Protection", blocked.reason)
        assertTrue(blocked.detail.contains("gambling"))
    }

    @Test
    fun test5_directGoogleSearchUrlWithoutSafeSearchRedirects() {
        // Direct untrusted navigation to Google search without safe=active
        val untrustedUrl = "https://www.google.com/search?q=technology+news"

        val decision = NavigationController.evaluate(
            url = untrustedUrl,
            customKeywords = customKeywords,
            normalizedKeywords = normalizedKeywords,
            searchEngine = SearchEngine.GOOGLE
        )

        assertTrue(decision is NavigationDecision.Redirect)
        val redirect = decision as NavigationDecision.Redirect
        assertTrue(redirect.url.contains("safe=active"))
        assertTrue(redirect.url.contains("technology+news"))
        assertTrue(ProtectionEngine.isGoogleSafeSearchUrl(redirect.url))
    }

    @Test
    fun test6_directGoogleSearchUrlWithSafeSearchAllowed() {
        // Direct untrusted navigation to Google search with safe=active and clean query
        val directUrl = "https://www.google.com/search?q=science+articles&safe=active"

        val decision = NavigationController.evaluate(
            url = directUrl,
            customKeywords = customKeywords,
            normalizedKeywords = normalizedKeywords,
            searchEngine = SearchEngine.GOOGLE
        )

        assertEquals(NavigationDecision.Allowed, decision)
    }

    @Test
    fun test7_googleResultClickReachesDestinationProtection() {
        // Destination link clicked from Google search results (e.g. an adult domain)
        val destinationUrl = "https://www.pornhub.com/view_video"

        val decision = NavigationController.evaluate(
            url = destinationUrl,
            customKeywords = customKeywords,
            normalizedKeywords = normalizedKeywords,
            searchEngine = SearchEngine.GOOGLE
        )

        assertTrue(decision is NavigationDecision.Blocked)
        val blocked = decision as NavigationDecision.Blocked
        assertEquals("Adult Content Protection", blocked.reason)
    }

    @Test
    fun test8_googleResultClickReachesCleanSite() {
        // Destination link clicked from Google search results (clean site)
        val cleanDestination = "https://www.wikipedia.org/wiki/Islam"

        val decision = NavigationController.evaluate(
            url = cleanDestination,
            customKeywords = customKeywords,
            normalizedKeywords = normalizedKeywords,
            searchEngine = SearchEngine.GOOGLE
        )

        assertEquals(NavigationDecision.Allowed, decision)
    }

    @Test
    fun test9_duckDuckGoSearchUnchanged() {
        // DuckDuckGo behavior remains intact and functional
        val ddgQuery = "prophet stories"
        val ddgUrl = ProtectionEngine.buildDuckDuckGoSafeSearchUrl(ddgQuery)
        assertEquals("https://safe.duckduckgo.com/?q=prophet+stories", ddgUrl)

        val decision = NavigationController.evaluate(
            url = ddgUrl,
            customKeywords = customKeywords,
            normalizedKeywords = normalizedKeywords,
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertEquals(NavigationDecision.Allowed, decision)

        val untrustedDdg = "https://duckduckgo.com/?q=prophet+stories"
        val redirectDecision = NavigationController.evaluate(
            url = untrustedDdg,
            customKeywords = customKeywords,
            normalizedKeywords = normalizedKeywords,
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(redirectDecision is NavigationDecision.Redirect)
    }

    @Test
    fun test10_googleHostDetection() {
        assertTrue(ProtectionEngine.isGoogleHost("google.com"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.com"))
        assertTrue(ProtectionEngine.isGoogleHost("google.co.uk"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.co.uk"))
        assertFalse(ProtectionEngine.isGoogleHost("example.com"))
        assertFalse(ProtectionEngine.isGoogleHost("notgoogle.com"))
    }

    @Test
    fun test11_extractQueryAccuracy() {
        val query = ProtectionEngine.extractSearchEngineQuery("https://www.google.com/search?q=prayer+times&safe=active")
        assertEquals("prayer times", query)

        val emptyQuery = ProtectionEngine.extractSearchEngineQuery("https://www.google.com/search?safe=active")
        assertEquals(null, emptyQuery)
    }
}
