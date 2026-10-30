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
class DuckDuckGoStrictSafeSearchTest {

    // 1. DuckDuckGo search URL without "kp" gets "kp=1"
    @Test
    fun testDuckDuckGoSearchWithoutKpGetsKp1() {
        val inputUrl = "https://safe.duckduckgo.com/?q=android+compose"
        val decision = NavigationController.evaluate(
            url = inputUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("URL without kp must redirect to strict safe search", decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        assertEquals("safe.duckduckgo.com", parsed.host)
        assertEquals("1", parsed.getQueryParameter("kp"))
        assertEquals("android compose", URLDecoder.decode(parsed.getQueryParameter("q") ?: "", "UTF-8"))
        assertEquals(listOf("1"), parsed.getQueryParameters("kp"))
    }

    // 2. DuckDuckGo URL containing "kp=-1" is normalized to "kp=1"
    @Test
    fun testDuckDuckGoUrlContainingKpMinusOneNormalizedToKp1() {
        val inputUrl = "https://safe.duckduckgo.com/?q=kotlin+coroutines&kp=-1"
        val decision = NavigationController.evaluate(
            url = inputUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("URL with kp=-1 must redirect to kp=1", decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        assertEquals("1", parsed.getQueryParameter("kp"))
        assertEquals("kotlin coroutines", URLDecoder.decode(parsed.getQueryParameter("q") ?: "", "UTF-8"))
        assertEquals(listOf("1"), parsed.getQueryParameters("kp"))
    }

    // 3. DuckDuckGo URL containing "kp=-2" is normalized to "kp=1"
    @Test
    fun testDuckDuckGoUrlContainingKpMinusTwoNormalizedToKp1() {
        val inputUrl = "https://duckduckgo.com/?q=jetpack+compose&kp=-2"
        val decision = NavigationController.evaluate(
            url = inputUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("URL with kp=-2 must redirect to safe domain with kp=1", decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        assertEquals("safe.duckduckgo.com", parsed.host)
        assertEquals("1", parsed.getQueryParameter("kp"))
        assertEquals("jetpack compose", URLDecoder.decode(parsed.getQueryParameter("q") ?: "", "UTF-8"))
        assertEquals(listOf("1"), parsed.getQueryParameters("kp"))
    }

    // 4. Duplicate "kp" parameters are reduced to one effective "kp=1"
    @Test
    fun testDuplicateKpParametersReducedToOneEffectiveKp1() {
        val inputUrl = "https://safe.duckduckgo.com/?q=test&kp=-1&kp=1&kp=-2&KP=off"
        val decision = NavigationController.evaluate(
            url = inputUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("Duplicate kp must redirect to single kp=1", decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        val kpValues = parsed.getQueryParameters("kp")
        assertEquals(1, kpValues.size)
        assertEquals("1", kpValues[0])
        val kpUpper = parsed.getQueryParameters("KP")
        assertEquals(0, kpUpper.size)
    }

    // 5. Original search query remains unchanged
    @Test
    fun testOriginalSearchQueryRemainsUnchanged() {
        val originalQuery = "UniqueQuery 12345 Special"
        val encodedQuery = Uri.encode(originalQuery)
        val inputUrl = "https://duckduckgo.com/?q=$encodedQuery&kp=-1"
        val decision = NavigationController.evaluate(
            url = inputUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        assertEquals(originalQuery, parsed.getQueryParameter("q"))
    }

    // 6. Legitimate unrelated URL parameters are preserved
    @Test
    fun testLegitimateUnrelatedParametersPreserved() {
        val inputUrl = "https://duckduckgo.com/?q=android&t=h_&ia=web&kl=us-en&kp=-1&df=m"
        val decision = NavigationController.evaluate(
            url = inputUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        assertEquals("safe.duckduckgo.com", parsed.host)
        assertEquals("android", parsed.getQueryParameter("q"))
        assertEquals("h_", parsed.getQueryParameter("t"))
        assertEquals("web", parsed.getQueryParameter("ia"))
        assertEquals("us-en", parsed.getQueryParameter("kl"))
        assertEquals("m", parsed.getQueryParameter("df"))
        assertEquals("1", parsed.getQueryParameter("kp"))
        assertEquals(listOf("1"), parsed.getQueryParameters("kp"))
    }

    // 7. URL encoding remains correct
    @Test
    fun testUrlEncodingRemainsCorrect() {
        val specialQuery = "C++ & Python (Advanced) 100%"
        val inputUrl = "https://duckduckgo.com/?q=${Uri.encode(specialQuery)}&kp=-2"
        val decision = NavigationController.evaluate(
            url = inputUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        assertEquals(specialQuery, parsed.getQueryParameter("q"))
        assertEquals("1", parsed.getQueryParameter("kp"))

        // Also test non-Latin scripts (e.g. Bengali)
        val bengaliQuery = "বাংলাদেশ সংবাদ"
        val bengaliUrl = "https://duckduckgo.com/?q=${Uri.encode(bengaliQuery)}&kp=-1"
        val bengaliDecision = NavigationController.evaluate(
            url = bengaliUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(bengaliDecision is NavigationDecision.Redirect)
        val parsedBengali = Uri.parse((bengaliDecision as NavigationDecision.Redirect).url)
        assertEquals(bengaliQuery, parsedBengali.getQueryParameter("q"))
    }

    // 8. Redirect handling does not create an infinite navigation loop
    @Test
    fun testRedirectHandlingDoesNotCreateInfiniteNavigationLoop() {
        val strictUrl = "https://safe.duckduckgo.com/?q=infinite+loop+check&kp=1"
        val decision = NavigationController.evaluate(
            url = strictUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("Already strict DuckDuckGo search must be Allowed", decision is NavigationDecision.Allowed)

        // Multiple legitimate parameters with kp=1 must also be Allowed
        val multiParamStrictUrl = "https://safe.duckduckgo.com/?q=test&t=h_&ia=web&kp=1"
        val multiDecision = NavigationController.evaluate(
            url = multiParamStrictUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("Already strict DuckDuckGo with other params must be Allowed", multiDecision is NavigationDecision.Allowed)
    }

    // 9. Ordinary non-DuckDuckGo websites are not modified
    @Test
    fun testOrdinaryNonDuckDuckGoWebsitesNotModified() {
        val standardSite = "https://example.com/search?q=kotlin&kp=-1"
        val decision = NavigationController.evaluate(
            url = standardSite,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("External sites must not be redirected or normalized as DuckDuckGo", decision is NavigationDecision.Allowed)

        val wikiUrl = "https://en.wikipedia.org/wiki/Special:Search?search=android"
        val wikiDecision = NavigationController.evaluate(
            url = wikiUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("Wikipedia search must remain Allowed", wikiDecision is NavigationDecision.Allowed)
    }

    // 10. Google Safe Search remains unchanged
    @Test
    fun testGoogleSafeSearchRemainsUnchanged() {
        val unsafeGoogleUrl = "https://www.google.com/search?q=weather"
        val decision = NavigationController.evaluate(
            url = unsafeGoogleUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("Google search without safe=active must redirect", decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        val parsed = Uri.parse(redirectUrl)
        assertEquals("active", parsed.getQueryParameter("safe"))
        assertEquals("weather", parsed.getQueryParameter("q"))

        val safeGoogleUrl = "https://www.google.com/search?q=weather&safe=active"
        val safeDecision = NavigationController.evaluate(
            url = safeGoogleUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("Google search with safe=active must be Allowed", safeDecision is NavigationDecision.Allowed)
    }

    // 11. Existing search engine behavior remains intact
    @Test
    fun testExistingSearchEngineLockBehaviorRemainsIntact() {
        // When Google is selected, DuckDuckGo searches redirect to Google with safe=active
        val ddgSearch = "https://duckduckgo.com/?q=android+architecture&kp=-1"
        val toGoogleDecision = NavigationController.evaluate(
            url = ddgSearch,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("DuckDuckGo must redirect to Google when Google is selected engine", toGoogleDecision is NavigationDecision.Redirect)
        val toGoogleUrl = (toGoogleDecision as NavigationDecision.Redirect).url
        val parsedGoogle = Uri.parse(toGoogleUrl)
        assertTrue(ProtectionEngine.isGoogleHost(parsedGoogle.host ?: ""))
        assertEquals("active", parsedGoogle.getQueryParameter("safe"))

        // When DuckDuckGo is selected, Google searches redirect to safe DuckDuckGo with kp=1
        val googleSearch = "https://www.google.com/search?q=android+architecture"
        val toDdgDecision = NavigationController.evaluate(
            url = googleSearch,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("Google must redirect to DuckDuckGo when DuckDuckGo is selected engine", toDdgDecision is NavigationDecision.Redirect)
        val toDdgUrl = (toDdgDecision as NavigationDecision.Redirect).url
        val parsedDdg = Uri.parse(toDdgUrl)
        assertEquals("safe.duckduckgo.com", parsedDdg.host)
        assertEquals("1", parsedDdg.getQueryParameter("kp"))
        assertEquals("android architecture", parsedDdg.getQueryParameter("q"))
    }

    // 12. Ordinary DuckDuckGo non-search pages remain untouched
    @Test
    fun testOrdinaryDuckDuckGoPagesRemainUntouched() {
        val aboutUrl = "https://duckduckgo.com/about"
        val decisionAbout = NavigationController.evaluate(
            url = aboutUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("About page must not be redirected", decisionAbout is NavigationDecision.Allowed)

        val homeUrl = "https://duckduckgo.com/"
        val decisionHome = NavigationController.evaluate(
            url = homeUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("DuckDuckGo home page without query must be Allowed", decisionHome is NavigationDecision.Allowed)

        val safeHomeUrl = "https://safe.duckduckgo.com/"
        val decisionSafeHome = NavigationController.evaluate(
            url = safeHomeUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("Safe DuckDuckGo home page without query must be Allowed", decisionSafeHome is NavigationDecision.Allowed)
    }

    // 13. Phishing and untrusted hosts containing "duckduckgo" are rejected as search engines
    @Test
    fun testUntrustedDuckDuckGoLookalikesRejected() {
        assertFalse(ProtectionEngine.isDuckDuckGoHost("evil-duckduckgo.com"))
        assertFalse(ProtectionEngine.isDuckDuckGoHost("duckduckgo.com.fake.org"))
        assertFalse(ProtectionEngine.isDuckDuckGoHost("notduckduckgo.com"))
        assertTrue(ProtectionEngine.isDuckDuckGoHost("duckduckgo.com"))
        assertTrue(ProtectionEngine.isDuckDuckGoHost("safe.duckduckgo.com"))
        assertTrue(ProtectionEngine.isDuckDuckGoHost("html.duckduckgo.com"))
        assertTrue(ProtectionEngine.isDuckDuckGoHost("lite.duckduckgo.com"))
    }
}
