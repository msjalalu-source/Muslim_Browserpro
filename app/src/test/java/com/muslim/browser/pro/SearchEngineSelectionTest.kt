package com.muslim.browser.pro

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.NavigationController
import com.muslim.browser.pro.browser.NavigationDecision
import com.muslim.browser.pro.browser.ProtectionEngine
import com.muslim.browser.pro.browser.SearchEngine
import com.muslim.browser.pro.browser.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SearchEngineSelectionTest {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val prefs = context.getSharedPreferences("focus_shield_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        repository = SettingsRepository(context)
    }

    @Test
    fun test1_freshInstallationDefaultsToDuckDuckGo() {
        // Fresh repository with empty SharedPreferences
        val defaultEngine = repository.selectedSearchEngine
        assertEquals("Default search engine must be DuckDuckGo", SearchEngine.DUCKDUCKGO, defaultEngine)

        // BrowserViewModel also initializes with DuckDuckGo
        val viewModel = BrowserViewModel(context)
        assertEquals("ViewModel must initialize with DuckDuckGo", SearchEngine.DUCKDUCKGO, viewModel.uiState.value.selectedSearchEngine)
    }

    @Test
    fun test2_exactlyTwoSearchEnginesSupported() {
        val supportedEngines = SearchEngine.values()
        assertEquals("Exactly two search engines must be supported", 2, supportedEngines.size)
        assertTrue("Must contain DuckDuckGo", supportedEngines.contains(SearchEngine.DUCKDUCKGO))
        assertTrue("Must contain Google", supportedEngines.contains(SearchEngine.GOOGLE))
    }

    @Test
    fun test3_googleCanBeSelected() {
        val viewModel = BrowserViewModel(context)
        viewModel.selectSearchEngine(SearchEngine.GOOGLE)

        assertEquals("Selected engine in UI state must be Google", SearchEngine.GOOGLE, viewModel.uiState.value.selectedSearchEngine)
        assertEquals("Persisted engine must be Google", SearchEngine.GOOGLE, repository.selectedSearchEngine)
    }

    @Test
    fun test4_duckDuckGoCanBeSelected() {
        val viewModel = BrowserViewModel(context)
        viewModel.selectSearchEngine(SearchEngine.GOOGLE)
        assertEquals(SearchEngine.GOOGLE, viewModel.uiState.value.selectedSearchEngine)

        viewModel.selectSearchEngine(SearchEngine.DUCKDUCKGO)
        assertEquals("Selected engine in UI state must be DuckDuckGo", SearchEngine.DUCKDUCKGO, viewModel.uiState.value.selectedSearchEngine)
        assertEquals("Persisted engine must be DuckDuckGo", SearchEngine.DUCKDUCKGO, repository.selectedSearchEngine)
    }

    @Test
    fun test5_duckDuckGoSearchAlwaysUsesSafeDuckDuckGoEndpoint() {
        val viewModel = BrowserViewModel(context)
        viewModel.selectSearchEngine(SearchEngine.DUCKDUCKGO)

        // Raw search query submitted
        val allowed = viewModel.submitQueryOrUrl("islamic architecture history")
        assertTrue("Search query must be allowed", allowed)

        val targetUrl = viewModel.uiState.value.currentUrl
        assertTrue(
            "DuckDuckGo search must use safe.duckduckgo.com: $targetUrl",
            targetUrl.startsWith("https://safe.duckduckgo.com/?q=")
        )
        assertFalse("Must not use unsafe duckduckgo.com endpoint without safe", targetUrl.startsWith("https://duckduckgo.com/?q="))
    }

    @Test
    fun test6_googleSearchAlwaysRetainsForcedSafeSearch() {
        val viewModel = BrowserViewModel(context)
        viewModel.selectSearchEngine(SearchEngine.GOOGLE)

        // Raw search query submitted
        val allowed = viewModel.submitQueryOrUrl("astronomy science")
        assertTrue("Search query must be allowed", allowed)

        val targetUrl = viewModel.uiState.value.currentUrl
        assertTrue(
            "Google search must target google.com/search: $targetUrl",
            targetUrl.startsWith("https://www.google.com/search?q=")
        )
        assertTrue("Google search must enforce safe=active: $targetUrl", targetUrl.contains("safe=active"))
    }

    @Test
    fun test7_selectedEnginePersistsAcrossRecreation() {
        // User selects Google
        repository.selectedSearchEngine = SearchEngine.GOOGLE

        // Recreate repository (simulating app restart)
        val reloadedRepository = SettingsRepository(context)
        assertEquals("Google selection must persist", SearchEngine.GOOGLE, reloadedRepository.selectedSearchEngine)

        val newViewModel = BrowserViewModel(context)
        assertEquals("New ViewModel instance must pick up persisted Google setting", SearchEngine.GOOGLE, newViewModel.uiState.value.selectedSearchEngine)
    }

    @Test
    fun test8_customKeywordBlockingHappensBeforeSearchUrlGeneration() {
        val viewModel = BrowserViewModel(context)
        viewModel.selectSearchEngine(SearchEngine.DUCKDUCKGO)

        // Search query containing protected keyword 'download'
        val blocked = viewModel.submitQueryOrUrl("how to download video free")
        assertFalse("Search with protected keyword must be blocked", blocked)
        assertNotNull("Blocked info must be set", viewModel.uiState.value.blockedInfo)
        assertEquals("Custom Keyword Protection", viewModel.uiState.value.blockedInfo?.reason)

        // URL must not have navigated to search engine
        assertFalse(viewModel.uiState.value.currentUrl.contains("safe.duckduckgo.com"))
        assertFalse(viewModel.uiState.value.currentUrl.contains("google.com/search"))
    }

    @Test
    fun test9_navigationControllerRedirectsGenericSearchQueryToSelectedEngine() {
        // When DuckDuckGo is selected, external search engine query (e.g. Bing search URL) redirects to safe.duckduckgo.com
        val bingSearchUrl = "https://www.bing.com/search?q=space+exploration"
        val decisionDdg = NavigationController.evaluate(
            url = bingSearchUrl,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(decisionDdg is NavigationDecision.Redirect)
        val redirectDdg = (decisionDdg as NavigationDecision.Redirect).url
        assertTrue("Must redirect to safe.duckduckgo.com", redirectDdg.startsWith("https://safe.duckduckgo.com/?q="))

        // When Google is selected, external search engine query redirects to google.com with safe=active
        val decisionGoogle = NavigationController.evaluate(
            url = bingSearchUrl,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue(decisionGoogle is NavigationDecision.Redirect)
        val redirectGoogle = (decisionGoogle as NavigationDecision.Redirect).url
        assertTrue("Must redirect to Google safe search", redirectGoogle.startsWith("https://www.google.com/search?q="))
        assertTrue("Must enforce safe=active", redirectGoogle.contains("safe=active"))
    }

    @Test
    fun test10_normalWebsiteNavigationIsNotBlocked() {
        // Wikipedia homepage or article
        val wikiDecision = NavigationController.evaluate(
            url = "https://en.wikipedia.org/wiki/Special:Random",
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertEquals(NavigationDecision.Allowed, wikiDecision)

        // Search engine homepage without search query is not blocked
        val ddgHomeDecision = NavigationController.evaluate(
            url = "https://duckduckgo.com/",
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertEquals(NavigationDecision.Allowed, ddgHomeDecision)

        val googleHomeDecision = NavigationController.evaluate(
            url = "https://www.google.com/",
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertEquals(NavigationDecision.Allowed, googleHomeDecision)
    }

    @Test
    fun test11_safeSearchUrlsAreAllowedDirectlyWithoutInfiniteRedirect() {
        val safeDdgUrl = "https://safe.duckduckgo.com/?q=healthy+food"
        val safeGoogleUrl = "https://www.google.com/search?q=healthy+food&safe=active"

        val ddgCheck = NavigationController.evaluate(
            url = safeDdgUrl,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertEquals("safe.duckduckgo.com must be Allowed", NavigationDecision.Allowed, ddgCheck)

        val googleCheck = NavigationController.evaluate(
            url = safeGoogleUrl,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertEquals("google.com with safe=active must be Allowed", NavigationDecision.Allowed, googleCheck)
    }

    @Test
    fun test12_googleSearchWithSafeOffCannotBypassIntendedPolicy() {
        val unsafeGoogleUrl = "https://www.google.com/search?q=astronomy&safe=off"
        val decision = NavigationController.evaluate(
            url = unsafeGoogleUrl,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("safe=off must be redirected", decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        assertTrue("Redirect must enforce safe=active: $redirectUrl", redirectUrl.contains("safe=active"))
        assertFalse("Redirect must not contain safe=off", redirectUrl.contains("safe=off"))
    }

    @Test
    fun test13_googleSearchWithMissingSafeParameterCannotBypassIntendedPolicy() {
        val missingSafeUrl = "https://www.google.com/search?q=astronomy"
        val decision = NavigationController.evaluate(
            url = missingSafeUrl,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("Missing safe parameter must be redirected", decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        assertTrue("Redirect must add safe=active: $redirectUrl", redirectUrl.contains("safe=active"))
    }

    @Test
    fun test14_googleSearchPageAllowedWithSafeActive() {
        val validSearchPage = "https://www.google.com/search?q=space+science&safe=active"
        val decision = NavigationController.evaluate(
            url = validSearchPage,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertEquals("Valid Google search page with safe=active must be Allowed", NavigationDecision.Allowed, decision)
    }

    @Test
    fun test15_safeActiveDoesNotBypassDestinationProtectionForBlockedDomain() {
        // Direct navigation to adult domain even with safe=active parameter appended
        val maliciousUrlWithSafeParam = "https://brazzers.com/landing?safe=active"
        val decision = NavigationController.evaluate(
            url = maliciousUrlWithSafeParam,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("safe=active parameter must not bypass adult content protection", decision is NavigationDecision.Blocked)
        assertEquals("Adult Content Protection", (decision as NavigationDecision.Blocked).reason)
    }

    @Test
    fun test16_googleOutboundRedirectToLegitimateResultAllowed() {
        val googleOutboundLegit = "https://www.google.com/url?q=https://en.wikipedia.org/wiki/Science&sa=U&ved=0ahU"
        val decision = NavigationController.evaluate(
            url = googleOutboundLegit,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertEquals("Outbound Google link to legitimate site must be Allowed", NavigationDecision.Allowed, decision)
    }

    @Test
    fun test17_googleOutboundRedirectToBlockedDestinationIsBlocked() {
        // Google outbound redirect wrapping a blocked adult domain
        val googleOutboundBlocked = "https://www.google.com/url?q=https://brazzers.com/gallery&sa=U&ved=0ahU"
        val decision = NavigationController.evaluate(
            url = googleOutboundBlocked,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("Outbound redirect to adult domain must be Blocked", decision is NavigationDecision.Blocked)
        assertEquals("Adult Content Protection", (decision as NavigationDecision.Blocked).reason)

        // Even if someone appends safe=active to the Google redirect URL
        val googleOutboundWithSafeActive = "https://www.google.com/url?q=https://brazzers.com/gallery&safe=active"
        val decisionWithSafe = NavigationController.evaluate(
            url = googleOutboundWithSafeActive,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("Outbound redirect to adult domain with safe=active must still be Blocked", decisionWithSafe is NavigationDecision.Blocked)
        assertEquals("Adult Content Protection", (decisionWithSafe as NavigationDecision.Blocked).reason)
    }

    @Test
    fun test18_duckDuckGoSearchPreservesSafeEndpointAndDestinationProtection() {
        // Normal DuckDuckGo search without safe endpoint redirects to safe.duckduckgo.com
        val unsafeDdg = "https://duckduckgo.com/?q=quantum+physics"
        val redirectDecision = NavigationController.evaluate(
            url = unsafeDdg,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(redirectDecision is NavigationDecision.Redirect)
        assertEquals("https://safe.duckduckgo.com/?q=quantum+physics", (redirectDecision as NavigationDecision.Redirect).url)

        // DuckDuckGo outbound redirect to adult domain is Blocked
        val ddgOutboundBlocked = "https://duckduckgo.com/l/?uddg=https%3A%2F%2Fbrazzers.com%2Fgallery"
        val blockedDecision = NavigationController.evaluate(
            url = ddgOutboundBlocked,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue("DDG outbound redirect to adult site must be Blocked", blockedDecision is NavigationDecision.Blocked)
        assertEquals("Adult Content Protection", (blockedDecision as NavigationDecision.Blocked).reason)

        // DuckDuckGo outbound redirect to legitimate site is Allowed
        val ddgOutboundLegit = "https://duckduckgo.com/l/?uddg=https%3A%2F%2Fen.wikipedia.org"
        val allowedDecision = NavigationController.evaluate(
            url = ddgOutboundLegit,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertEquals("DDG outbound redirect to legitimate site must be Allowed", NavigationDecision.Allowed, allowedDecision)
    }

    @Test
    fun test19_separationBetweenSearchPageAndDestinationProtection() {
        // Search page itself for "astronomy" on Google with safe=active is allowed
        val searchPage = "https://www.google.com/search?q=astronomy&safe=active"
        val searchPageDecision = NavigationController.evaluate(
            url = searchPage,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertEquals("Search results page is Allowed", NavigationDecision.Allowed, searchPageDecision)

        // Result 1: Legitimate clicked result navigates normally
        val legitResult = "https://www.nasa.gov/missions"
        val legitDecision = NavigationController.evaluate(
            url = legitResult,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertEquals("Legitimate result navigates normally", NavigationDecision.Allowed, legitDecision)

        // Result 2: Adult destination clicked from the results is intercepted by centralized destination protection
        val adultResult = "https://brazzers.com/astronomy-spoof"
        val adultDecision = NavigationController.evaluate(
            url = adultResult,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue("Adult destination from search results is Blocked", adultDecision is NavigationDecision.Blocked)
        assertEquals("Adult Content Protection", (adultDecision as NavigationDecision.Blocked).reason)
    }
}
