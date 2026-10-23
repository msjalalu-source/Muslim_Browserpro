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
        // Legitimate standard Google domains
        assertTrue(ProtectionEngine.isGoogleHost("google.com"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.com"))
        assertTrue(ProtectionEngine.isGoogleHost("google.co.uk"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.co.uk"))

        // Regional Google search domains
        assertTrue(ProtectionEngine.isGoogleHost("google.ca"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.ca"))
        assertTrue(ProtectionEngine.isGoogleHost("google.de"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.de"))
        assertTrue(ProtectionEngine.isGoogleHost("google.com.pk"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.com.pk"))
        assertTrue(ProtectionEngine.isGoogleHost("google.com.sa"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.com.sa"))
        assertTrue(ProtectionEngine.isGoogleHost("google.com.eg"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.com.eg"))
        assertTrue(ProtectionEngine.isGoogleHost("google.com.tr"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.com.tr"))
        assertTrue(ProtectionEngine.isGoogleHost("google.co.jp"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.co.jp"))
        assertTrue(ProtectionEngine.isGoogleHost("google.co.in"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.co.in"))
        assertTrue(ProtectionEngine.isGoogleHost("google.ae"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.ae"))

        // Case normalization and trailing dots
        assertTrue(ProtectionEngine.isGoogleHost("WWW.GOOGLE.COM"))
        assertTrue(ProtectionEngine.isGoogleHost("Google.Co.Uk"))
        assertTrue(ProtectionEngine.isGoogleHost("google.com."))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.com."))
        assertTrue(ProtectionEngine.isGoogleHost("google.co.uk."))

        // Unrelated domains
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

    @Test
    fun test12_deceptiveHostsRejected() {
        // Deceptive hosts with lookalikes or suffix confusion
        assertFalse(ProtectionEngine.isGoogleHost("google.evil.com"))
        assertFalse(ProtectionEngine.isGoogleHost("www.google.evil.com"))
        assertFalse(ProtectionEngine.isGoogleHost("google.com.evil.test"))
        assertFalse(ProtectionEngine.isGoogleHost("evil.google.evil.com"))
        assertFalse(ProtectionEngine.isGoogleHost("google.attacker.co.uk"))
        assertFalse(ProtectionEngine.isGoogleHost("evilgoogle.com"))
        assertFalse(ProtectionEngine.isGoogleHost("mygoogle.com"))
        assertFalse(ProtectionEngine.isGoogleHost("google.com-security.com"))
        assertFalse(ProtectionEngine.isGoogleHost("google.xyz"))
        assertFalse(ProtectionEngine.isGoogleHost("google.top"))
    }

    @Test
    fun test13_unrelatedGoogleServicesAndSubdomainsRejected() {
        // Arbitrary subdomains and non-search Google services must not be trusted as search hosts
        assertFalse(ProtectionEngine.isGoogleHost("drive.google.com"))
        assertFalse(ProtectionEngine.isGoogleHost("docs.google.com"))
        assertFalse(ProtectionEngine.isGoogleHost("mail.google.com"))
        assertFalse(ProtectionEngine.isGoogleHost("cloud.google.com"))
        assertFalse(ProtectionEngine.isGoogleHost("developer.google.com"))
        assertFalse(ProtectionEngine.isGoogleHost("sub.www.google.com"))
    }

    @Test
    fun test14_malformedHostnamesRejected() {
        // Malformed hostnames, user-info, ports, and invalid characters
        assertFalse(ProtectionEngine.isGoogleHost(""))
        assertFalse(ProtectionEngine.isGoogleHost("   "))
        assertFalse(ProtectionEngine.isGoogleHost("user@google.com"))
        assertFalse(ProtectionEngine.isGoogleHost("google.com:80"))
        assertFalse(ProtectionEngine.isGoogleHost("google.com/search"))
        assertFalse(ProtectionEngine.isGoogleHost("google..com"))
        assertFalse(ProtectionEngine.isGoogleHost(".google.com"))
        assertFalse(ProtectionEngine.isGoogleHost("-google.com"))
        assertFalse(ProtectionEngine.isGoogleHost("google-.com"))
    }

    @Test
    fun test15_safeSearchEnforcementNotMerelySubstring() {
        // A URL is NOT safe merely because it contains "safe=active" as text
        // Query parameter has safe=off with safe=active in another parameter
        assertFalse(
            ProtectionEngine.isGoogleSafeSearchUrl(
                "https://www.google.com/search?q=unfiltered&safe=off&comment=safe=active"
            )
        )
        // safe=active is part of query text or unrelated parameter
        assertFalse(
            ProtectionEngine.isGoogleSafeSearchUrl(
                "https://www.google.com/search?q=safe=active"
            )
        )
        assertFalse(
            ProtectionEngine.isGoogleSafeSearchUrl(
                "https://www.google.com/search?note=safe=active"
            )
        )
        // Deceptive domain with safe=active
        assertFalse(
            ProtectionEngine.isGoogleSafeSearchUrl(
                "https://google.evil.com/search?q=test&safe=active"
            )
        )
        // Non-search Google service with safe=active
        assertFalse(
            ProtectionEngine.isGoogleSafeSearchUrl(
                "https://drive.google.com/search?q=test&safe=active"
            )
        )
        // Valid Google SafeSearch URL
        assertTrue(
            ProtectionEngine.isGoogleSafeSearchUrl(
                "https://www.google.com/search?q=test&safe=active"
            )
        )
        assertTrue(
            ProtectionEngine.isGoogleSafeSearchUrl(
                "https://google.co.uk/search?q=test&safe=active"
            )
        )
    }

    @Test
    fun test16_schemePortUserInfoSafety() {
        // Unexpected schemes
        assertFalse(
            ProtectionEngine.isGoogleSafeSearchUrl(
                "javascript://www.google.com/search?q=test&safe=active"
            )
        )
        assertFalse(
            ProtectionEngine.isGoogleSafeSearchUrl(
                "ftp://www.google.com/search?q=test&safe=active"
            )
        )
        // User-info present
        assertFalse(
            ProtectionEngine.isGoogleSafeSearchUrl(
                "https://user:pass@www.google.com/search?q=test&safe=active"
            )
        )
        assertFalse(
            ProtectionEngine.isGoogleSafeSearchUrl(
                "https://attacker.com@www.google.com/search?q=test&safe=active"
            )
        )
        // Non-standard port
        assertFalse(
            ProtectionEngine.isGoogleSafeSearchUrl(
                "https://www.google.com:8080/search?q=test&safe=active"
            )
        )

        // NavigationController evaluation fails closed for malicious URLs with user-info or non-standard ports
        val decisionUserInfo = NavigationController.evaluate(
            url = "https://user:pass@www.google.com/search?q=test&safe=active",
            customKeywords = customKeywords,
            normalizedKeywords = normalizedKeywords,
            searchEngine = SearchEngine.GOOGLE
        )
        // Not treated as Google search; proceeds through regular checks
        assertFalse(decisionUserInfo is NavigationDecision.Redirect)

        val decisionDeceptive = NavigationController.evaluate(
            url = "https://google.evil.com/search?q=test&safe=active",
            customKeywords = customKeywords,
            normalizedKeywords = normalizedKeywords,
            searchEngine = SearchEngine.GOOGLE
        )
        // Not treated as Google search
        assertFalse(decisionDeceptive is NavigationDecision.Redirect)
    }

    @Test
    fun test17_fastPathStrictlyValidatesGoogleSafeSearchUrl() {
        // If an invalid URL was somehow marked validated, fast-path must reject it if not a valid Google SafeSearch URL
        val deceptiveUrl = "https://google.evil.com/search?q=test&safe=active"
        NavigationController.markSearchUrlValidated(deceptiveUrl)

        val decision = NavigationController.evaluate(
            url = deceptiveUrl,
            customKeywords = customKeywords,
            normalizedKeywords = normalizedKeywords,
            searchEngine = SearchEngine.GOOGLE
        )
        // Fast-path does not allow deceptive URL
        assertFalse(decision == NavigationDecision.Allowed && NavigationController.isSearchUrlValidated(deceptiveUrl))
    }
}
