package com.muslim.browser.pro.browser

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.URLDecoder

@RunWith(RobolectricTestRunner::class)
class HardenSafeSearchTest {

    // 1. Google selected + "safe=off" → "safe=active"
    @Test
    fun testGoogleSelectedSafeOffRedirectsToSafeActive() {
        val inputUrl = "https://www.google.com/search?q=space+exploration&safe=off"
        val decision = NavigationController.evaluate(
            url = inputUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("Google search with safe=off must redirect", decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        assertTrue(ProtectionEngine.isGoogleHost(parsed.host ?: ""))
        assertEquals("active", parsed.getQueryParameter("safe"))
        assertEquals(listOf("active"), parsed.getQueryParameters("safe"))
        assertEquals("space exploration", URLDecoder.decode(parsed.getQueryParameter("q") ?: "", "UTF-8"))
    }

    // 2. Google selected + "safe=images" → "safe=active"
    @Test
    fun testGoogleSelectedSafeImagesRedirectsToSafeActive() {
        val inputUrl = "https://www.google.com/search?q=mars+rover&safe=images"
        val decision = NavigationController.evaluate(
            url = inputUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("Google search with safe=images must redirect", decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        assertEquals("active", parsed.getQueryParameter("safe"))
        assertEquals(listOf("active"), parsed.getQueryParameters("safe"))
        assertEquals("mars rover", URLDecoder.decode(parsed.getQueryParameter("q") ?: "", "UTF-8"))
    }

    // 3. Google selected + duplicate or missing "safe" parameters → exactly one "safe=active"
    @Test
    fun testGoogleSelectedDuplicateOrMissingSafeParameters() {
        // A: Missing safe parameter
        val missingSafeUrl = "https://www.google.com/search?q=quantum+physics"
        val missingDecision = NavigationController.evaluate(
            url = missingSafeUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("Google search without safe parameter must redirect", missingDecision is NavigationDecision.Redirect)
        val missingRedirect = (missingDecision as NavigationDecision.Redirect).url
        val missingParsed = Uri.parse(missingRedirect)
        assertEquals("active", missingParsed.getQueryParameter("safe"))
        assertEquals(listOf("active"), missingParsed.getQueryParameters("safe"))

        // B: Duplicate and conflicting safe parameters (e.g. ?safe=active&safe=off&SAFE=images)
        val duplicateSafeUrl = "https://www.google.com/search?q=quantum+physics&safe=active&safe=off&SAFE=images"
        val duplicateDecision = NavigationController.evaluate(
            url = duplicateSafeUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("Google search with duplicate safe parameters must redirect", duplicateDecision is NavigationDecision.Redirect)
        val duplicateRedirect = (duplicateDecision as NavigationDecision.Redirect).url
        val duplicateParsed = Uri.parse(duplicateRedirect)
        assertEquals(listOf("active"), duplicateParsed.getQueryParameters("safe"))
        assertEquals(0, duplicateParsed.getQueryParameters("SAFE").size)
    }

    // 4. DuckDuckGo selected + "kp=-1" or "kp=-2" → exactly one "kp=1"
    @Test
    fun testDuckDuckGoSelectedKpMinusOneOrMinusTwoRedirectsToKp1() {
        val urlMinusOne = "https://safe.duckduckgo.com/?q=jetpack+compose&kp=-1"
        val decision1 = NavigationController.evaluate(
            url = urlMinusOne,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(decision1 is NavigationDecision.Redirect)
        val redirectUrl1 = (decision1 as NavigationDecision.Redirect).url
        val parsed1 = Uri.parse(redirectUrl1)
        assertEquals("safe.duckduckgo.com", parsed1.host)
        assertEquals(listOf("1"), parsed1.getQueryParameters("kp"))

        val urlMinusTwo = "https://duckduckgo.com/?q=jetpack+compose&kp=-2"
        val decision2 = NavigationController.evaluate(
            url = urlMinusTwo,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(decision2 is NavigationDecision.Redirect)
        val redirectUrl2 = (decision2 as NavigationDecision.Redirect).url
        val parsed2 = Uri.parse(redirectUrl2)
        assertEquals("safe.duckduckgo.com", parsed2.host)
        assertEquals(listOf("1"), parsed2.getQueryParameters("kp"))
    }

    // 5. DuckDuckGo selected + duplicate "kp" parameters → exactly one "kp=1"
    @Test
    fun testDuckDuckGoSelectedDuplicateKpParametersReducedToOne() {
        val urlDuplicates = "https://safe.duckduckgo.com/?q=android&kp=1&kp=-1&kp=-2&KP=off"
        val decision = NavigationController.evaluate(
            url = urlDuplicates,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        assertEquals("safe.duckduckgo.com", parsed.host)
        assertEquals(listOf("1"), parsed.getQueryParameters("kp"))
        assertEquals(0, parsed.getQueryParameters("KP").size)
    }

    // 6. Google enforcement does not modify DuckDuckGo URLs
    @Test
    fun testGoogleEnforcementDoesNotModifyDuckDuckGoUrls() {
        val ddgUrl = "https://safe.duckduckgo.com/?q=test&kp=1"
        // Directly testing the Google strict search helper on DuckDuckGo URL
        val result = ProtectionEngine.buildGoogleStrictSearchUrl(ddgUrl)
        assertEquals("Google enforcement must not modify DuckDuckGo URLs", ddgUrl, result)

        // General enforcement with GOOGLE selected on DuckDuckGo URL
        val generalResult = ProtectionEngine.enforceSafeSearch(ddgUrl, SearchEngine.GOOGLE)
        assertEquals(ddgUrl, generalResult)

        // Ordinary DuckDuckGo non-search URLs with Google selected must be Allowed
        val ordinaryDdg = "https://duckduckgo.com/about"
        val navDecision = NavigationController.evaluate(ordinaryDdg, emptySet(), searchEngine = SearchEngine.GOOGLE)
        assertTrue("Ordinary DuckDuckGo pages must remain Allowed when Google is selected", navDecision is NavigationDecision.Allowed)
    }

    // 7. DuckDuckGo enforcement does not modify Google URLs
    @Test
    fun testDuckDuckGoEnforcementDoesNotModifyGoogleUrls() {
        val googleUrl = "https://www.google.com/search?q=test&safe=active"
        // Directly testing DuckDuckGo strict search helper on Google URL
        val result = ProtectionEngine.buildDuckDuckGoStrictSearchUrl(googleUrl)
        assertEquals("DuckDuckGo enforcement must not modify Google URLs", googleUrl, result)

        // General enforcement with DUCKDUCKGO selected on Google URL
        val generalResult = ProtectionEngine.enforceSafeSearch(googleUrl, SearchEngine.DUCKDUCKGO)
        assertEquals(googleUrl, generalResult)

        // Ordinary Google non-search URLs with DuckDuckGo selected must be Allowed
        val ordinaryGoogle = "https://www.google.com/maps"
        val navDecision = NavigationController.evaluate(ordinaryGoogle, emptySet(), searchEngine = SearchEngine.DUCKDUCKGO)
        assertTrue("Ordinary Google pages must remain Allowed when DuckDuckGo is selected", navDecision is NavigationDecision.Allowed)
    }

    // 8. Switching Google → DuckDuckGo activates only DuckDuckGo enforcement
    @Test
    fun testSwitchingGoogleToDuckDuckGoActivatesOnlyDuckDuckGoEnforcement() {
        val ddgSearch = "https://duckduckgo.com/?q=kotlin&kp=-1"
        val decision = NavigationController.evaluate(
            url = ddgSearch,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        assertEquals("safe.duckduckgo.com", parsed.host)
        assertEquals("1", parsed.getQueryParameter("kp"))
        assertFalse("DuckDuckGo enforcement must never apply safe parameter", redirectUrl.contains("safe="))
    }

    // 9. Switching DuckDuckGo → Google activates only Google enforcement
    @Test
    fun testSwitchingDuckDuckGoToGoogleActivatesOnlyGoogleEnforcement() {
        val googleSearch = "https://www.google.com/search?q=kotlin&safe=off"
        val decision = NavigationController.evaluate(
            url = googleSearch,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue(decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        assertTrue(ProtectionEngine.isGoogleHost(parsed.host ?: ""))
        assertEquals("active", parsed.getQueryParameter("safe"))
        assertFalse("Google enforcement must never apply kp parameter", redirectUrl.contains("kp="))
    }

    // 10. Ordinary non-search website URLs remain unaffected
    @Test
    fun testOrdinaryNonSearchWebsiteUrlsRemainUnaffected() {
        val externalSites = listOf(
            "https://example.com/search?q=kotlin&safe=off&kp=-1",
            "https://en.wikipedia.org/wiki/Special:Search?search=android",
            "https://github.com/search?q=compose",
            "https://stackoverflow.com/questions"
        )
        for (site in externalSites) {
            val decisionGoogle = NavigationController.evaluate(site, emptySet(), searchEngine = SearchEngine.GOOGLE)
            assertTrue("External site must not be redirected by Google: $site", decisionGoogle is NavigationDecision.Allowed)

            val decisionDdg = NavigationController.evaluate(site, emptySet(), searchEngine = SearchEngine.DUCKDUCKGO)
            assertTrue("External site must not be redirected by DuckDuckGo: $site", decisionDdg is NavigationDecision.Allowed)
        }
    }

    // 11. Query text, legitimate parameters, and URL encoding are preserved
    @Test
    fun testQueryTextLegitimateParametersAndEncodingPreserved() {
        val specialQuery = "C++ & Python (Algorithms) 100%"
        val encoded = Uri.encode(specialQuery)

        // Google with extra params
        val googleInput = "https://www.google.com/search?q=$encoded&hl=en&gl=us&safe=off"
        val googleDecision = NavigationController.evaluate(googleInput, emptySet(), searchEngine = SearchEngine.GOOGLE)
        assertTrue(googleDecision is NavigationDecision.Redirect)
        val googleParsed = Uri.parse((googleDecision as NavigationDecision.Redirect).url)
        assertEquals(specialQuery, googleParsed.getQueryParameter("q"))
        assertEquals("en", googleParsed.getQueryParameter("hl"))
        assertEquals("us", googleParsed.getQueryParameter("gl"))
        assertEquals("active", googleParsed.getQueryParameter("safe"))

        // DuckDuckGo with extra params
        val ddgInput = "https://duckduckgo.com/?q=$encoded&ia=web&kl=us-en&kp=-2"
        val ddgDecision = NavigationController.evaluate(ddgInput, emptySet(), searchEngine = SearchEngine.DUCKDUCKGO)
        assertTrue(ddgDecision is NavigationDecision.Redirect)
        val ddgParsed = Uri.parse((ddgDecision as NavigationDecision.Redirect).url)
        assertEquals(specialQuery, ddgParsed.getQueryParameter("q"))
        assertEquals("web", ddgParsed.getQueryParameter("ia"))
        assertEquals("us-en", ddgParsed.getQueryParameter("kl"))
        assertEquals("1", ddgParsed.getQueryParameter("kp"))
    }

    // 12. Redirect handling does not create navigation loops
    @Test
    fun testRedirectHandlingDoesNotCreateNavigationLoops() {
        val strictGoogle = "https://www.google.com/search?q=loop+check&safe=active"
        val decisionGoogle = NavigationController.evaluate(strictGoogle, emptySet(), searchEngine = SearchEngine.GOOGLE)
        assertTrue("Already strict Google URL must be Allowed", decisionGoogle is NavigationDecision.Allowed)

        val strictDdg = "https://safe.duckduckgo.com/?q=loop+check&kp=1"
        val decisionDdg = NavigationController.evaluate(strictDdg, emptySet(), searchEngine = SearchEngine.DUCKDUCKGO)
        assertTrue("Already strict DuckDuckGo URL must be Allowed", decisionDdg is NavigationDecision.Allowed)
    }
}
