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
}
