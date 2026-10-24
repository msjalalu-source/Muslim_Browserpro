package com.muslim.browser.pro.browser

import android.app.Application
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.URLDecoder

@RunWith(RobolectricTestRunner::class)
class RestorationVerificationTest {

    private lateinit var application: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        val prefs = application.getSharedPreferences(SettingsRepository.PREFS_NAME, Application.MODE_PRIVATE)
        prefs.edit().clear().commit()
        repository = SettingsRepository(application)
    }

    @Test
    fun testDuckDuckGoStrictSafeSearchUrlGeneration() {
        val query = "Clean browsing & news"
        val url = SearchEngine.DUCKDUCKGO.buildSearchUrl(query)

        assertTrue("Must use safe.duckduckgo.com", url.startsWith("https://safe.duckduckgo.com/?q="))
        assertTrue("Must enforce strict safe search parameter kp=1", url.contains("kp=1"))

        val queryString = url.substringAfter("?q=").substringBefore("&")
        val decoded = URLDecoder.decode(queryString, "UTF-8")
        assertEquals(query.trim(), decoded)
    }

    @Test
    fun testDuckDuckGoUnicodeQueryEncoding() {
        val bengaliQuery = "বাংলাদেশ ইসলামিক ব্রাউজার"
        val url = SearchEngine.DUCKDUCKGO.buildSearchUrl(bengaliQuery)

        assertTrue(url.startsWith("https://safe.duckduckgo.com/?q="))
        val queryString = url.substringAfter("?q=").substringBefore("&")
        val decoded = URLDecoder.decode(queryString, "UTF-8")
        assertEquals(bengaliQuery, decoded)
    }

    @Test
    fun testDefaultSearchEngineIsDuckDuckGo() {
        assertEquals("Default search engine must be DuckDuckGo", SearchEngine.DUCKDUCKGO, repository.getSearchEngine())
    }

    @Test
    fun testUserSearchEnginePreferencePersistence() {
        // Change to Google explicitly
        repository.setSearchEngine(SearchEngine.GOOGLE)
        assertEquals(SearchEngine.GOOGLE, repository.getSearchEngine())

        // Create new repository instance to simulate app restart
        val newRepoInstance = SettingsRepository(application)
        assertEquals("User-configured search engine must be preserved", SearchEngine.GOOGLE, newRepoInstance.getSearchEngine())
    }

    @Test
    fun testDefaultFavoritesMatchScreenshot() {
        val defaults = repository.getFavoriteSites()
        assertEquals(8, defaults.size)

        val names = defaults.map { it.name }
        assertTrue("Contains MoldovaLive", names.contains("MoldovaLive"))
        assertTrue("Contains Moldova1", names.contains("Moldova1"))
        assertTrue("Contains Google AI Studio", names.contains("Google AI Studio"))
        assertTrue("Contains GitHub", names.contains("GitHub"))
        assertTrue("Contains Prothom Alo", names.contains("Prothom Alo"))
        assertTrue("Contains The Daily Star", names.contains("The Daily Star"))
        assertTrue("Contains Ittefaq", names.contains("Ittefaq"))
        assertTrue("Contains BangiNews", names.contains("BangiNews"))
    }

    @Test
    fun testUserCustomFavoritesArePreserved() {
        val customName = "My Custom Mosque"
        val customUrl = "https://examplemosque.org"
        repository.addFavoriteSite(customName, customUrl)

        val saved = repository.getFavoriteSites()
        assertTrue(saved.any { it.name == customName && it.url == customUrl })

        // Ensure reloading from persistence preserves custom list
        val newRepoInstance = SettingsRepository(application)
        val reloaded = newRepoInstance.getFavoriteSites()
        assertTrue(reloaded.any { it.name == customName && it.url == customUrl })
    }

    @Test
    fun testTabManagementInViewModel() {
        val viewModel = BrowserViewModel(application)
        val initialTabCount = viewModel.uiState.value.tabs.size
        val firstTabId = viewModel.uiState.value.currentTabId

        // Normal tap: creates exactly one new tab
        viewModel.openNewTab()
        val afterNewTab = viewModel.uiState.value
        assertEquals("Tab count should increase by 1", initialTabCount + 1, afterNewTab.tabs.size)
        val secondTabId = afterNewTab.currentTabId
        assertFalse("New tab should have a distinct ID", firstTabId == secondTabId)

        // Long press opens tabs selection dialog
        viewModel.openTabsDialog()
        assertTrue("Tabs dialog must be open", viewModel.uiState.value.isTabsDialogOpen)

        // Switching tabs selects target tab and closes dialog
        viewModel.selectTab(firstTabId)
        val stateAfterSelect = viewModel.uiState.value
        assertEquals("Must switch back to first tab", firstTabId, stateAfterSelect.currentTabId)
        assertFalse("Dialog must close after tab selection", stateAfterSelect.isTabsDialogOpen)

        // Closing second tab
        viewModel.closeTab(secondTabId)
        val stateAfterClose = viewModel.uiState.value
        assertEquals(initialTabCount, stateAfterClose.tabs.size)
        assertFalse("Closed tab must not be in list", stateAfterClose.tabs.any { it.id == secondTabId })
    }

    @Test
    fun testNavigationControllerEvaluatesRecognizedEnginesWithoutIndiscriminateRedirect() {
        val googleSearchWithoutSafe = "https://www.google.com/search?q=weather+today"
        val decision = NavigationController.evaluate(
            url = googleSearchWithoutSafe,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.GOOGLE
        )
        // Google search without safe=active must redirect to same search with safe=active
        assertTrue("Google search without safe=active must redirect", decision is NavigationDecision.Redirect)
        val parsedDecisionUri = Uri.parse((decision as NavigationDecision.Redirect).url)
        assertEquals("active", parsedDecisionUri.getQueryParameter("safe"))
        assertEquals("weather+today", parsedDecisionUri.getQueryParameter("q"))

        // Google search with safe=active must be allowed without redirect loops
        val alreadySafeGoogleUrl = "https://www.google.com/search?q=weather+today&safe=active"
        val alreadySafeDecision = NavigationController.evaluate(
            url = alreadySafeGoogleUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue(
            "Allowed direct search navigation for Google with safe=active",
            alreadySafeDecision is NavigationDecision.Allowed
        )

        // Standard DuckDuckGo search should upgrade to safe.duckduckgo.com
        val standardDdgUrl = "https://duckduckgo.com/?q=android+compose"
        val ddgDecision = NavigationController.evaluate(
            url = standardDdgUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(
            "Standard DuckDuckGo must redirect to safe DuckDuckGo",
            ddgDecision is NavigationDecision.Redirect &&
                    ddgDecision.url.startsWith("https://safe.duckduckgo.com/?q=")
        )

        // DuckDuckGo safe search already on safe.duckduckgo.com must be allowed
        val safeDdgUrl = "https://safe.duckduckgo.com/?q=android+compose&kp=1"
        val safeDdgDecision = NavigationController.evaluate(
            url = safeDdgUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.DUCKDUCKGO
        )
        assertTrue(
            "Already safe DuckDuckGo must be allowed",
            safeDdgDecision is NavigationDecision.Allowed
        )

        // Custom keyword blocking still applies
        val blockedQueryUrl = "https://www.google.com/search?q=download+vpn+client"
        val blockedDecision = NavigationController.evaluate(
            url = blockedQueryUrl,
            customKeywords = setOf("vpn"),
            searchEngine = SearchEngine.GOOGLE
        )
        assertTrue(
            "Protected keywords must be blocked across search engines",
            blockedDecision is NavigationDecision.Blocked
        )
    }

    @Test
    fun testSearchEngineLockAcrossEngines() {
        val bingSearch = "https://www.bing.com/search?q=kotlin+programming"
        val yahooSearch = "https://search.yahoo.com/search?p=kotlin+programming"
        val ddgSearch = "https://duckduckgo.com/?q=kotlin+programming"
        val googleSearch = "https://www.google.com/search?q=kotlin+programming"

        // 1. When GOOGLE is the selected engine:
        // Bing -> redirects to Google safe search
        val toGoogleFromBing = NavigationController.evaluate(bingSearch, emptySet(), searchEngine = SearchEngine.GOOGLE)
        assertTrue(toGoogleFromBing is NavigationDecision.Redirect)
        assertEquals("https://www.google.com/search?q=kotlin+programming&safe=active", (toGoogleFromBing as NavigationDecision.Redirect).url)

        // Yahoo -> redirects to Google safe search
        val toGoogleFromYahoo = NavigationController.evaluate(yahooSearch, emptySet(), searchEngine = SearchEngine.GOOGLE)
        assertTrue(toGoogleFromYahoo is NavigationDecision.Redirect)
        assertEquals("https://www.google.com/search?q=kotlin+programming&safe=active", (toGoogleFromYahoo as NavigationDecision.Redirect).url)

        // DuckDuckGo -> redirects to Google safe search
        val toGoogleFromDdg = NavigationController.evaluate(ddgSearch, emptySet(), searchEngine = SearchEngine.GOOGLE)
        assertTrue(toGoogleFromDdg is NavigationDecision.Redirect)
        assertEquals("https://www.google.com/search?q=kotlin+programming&safe=active", (toGoogleFromDdg as NavigationDecision.Redirect).url)

        // 2. When DUCKDUCKGO is the selected engine:
        // Bing -> redirects to DuckDuckGo safe search
        val toDdgFromBing = NavigationController.evaluate(bingSearch, emptySet(), searchEngine = SearchEngine.DUCKDUCKGO)
        assertTrue(toDdgFromBing is NavigationDecision.Redirect)
        assertEquals("https://safe.duckduckgo.com/?q=kotlin+programming&kp=1", (toDdgFromBing as NavigationDecision.Redirect).url)

        // Google -> redirects to DuckDuckGo safe search
        val toDdgFromGoogle = NavigationController.evaluate(googleSearch, emptySet(), searchEngine = SearchEngine.DUCKDUCKGO)
        assertTrue(toDdgFromGoogle is NavigationDecision.Redirect)
        assertEquals("https://safe.duckduckgo.com/?q=kotlin+programming&kp=1", (toDdgFromGoogle as NavigationDecision.Redirect).url)
    }

    @Test
    fun testGoogleSafeSearchParameterPreservation() {
        // Multi-parameter Google search preserves query and other parameters while ensuring safe=active
        val multiParamUrl = "https://www.google.com/search?client=android&q=compose+tutorial&hl=en"
        val decision = NavigationController.evaluate(multiParamUrl, emptySet(), searchEngine = SearchEngine.GOOGLE)
        assertTrue(decision is NavigationDecision.Redirect)
        val redirectUrl = (decision as NavigationDecision.Redirect).url
        assertTrue(redirectUrl.startsWith("https://www.google.com/search?"))
        val parsedRedirect = Uri.parse(redirectUrl)
        assertEquals("compose+tutorial", parsedRedirect.getQueryParameter("q"))
        assertEquals("en", parsedRedirect.getQueryParameter("hl"))
        assertEquals("android", parsedRedirect.getQueryParameter("client"))
        assertEquals("active", parsedRedirect.getQueryParameter("safe"))

        // Overrides safe=images or safe=off
        val unsafeParamUrl = "https://www.google.com/search?q=nature&safe=off"
        val safeDecision = NavigationController.evaluate(unsafeParamUrl, emptySet(), searchEngine = SearchEngine.GOOGLE)
        assertTrue(safeDecision is NavigationDecision.Redirect)
        val safeRedirectUrl = (safeDecision as NavigationDecision.Redirect).url
        assertTrue(safeRedirectUrl.contains("safe=active"))
        assertFalse(safeRedirectUrl.contains("safe=off"))
    }

    @Test
    fun testNonSearchGooglePagesAndGenericSearchNotRedirected() {
        // Generic search (e.g. Wikipedia, GitHub) must NOT be redirected to selected search engine
        val wikiSearch = "https://en.wikipedia.org/w/index.php?search=Albert+Einstein"
        val wikiDecision = NavigationController.evaluate(wikiSearch, emptySet(), searchEngine = SearchEngine.GOOGLE)
        assertTrue(wikiDecision is NavigationDecision.Allowed)

        val githubSearch = "https://github.com/search?q=jetpack+compose"
        val githubDecision = NavigationController.evaluate(githubSearch, emptySet(), searchEngine = SearchEngine.DUCKDUCKGO)
        assertTrue(githubDecision is NavigationDecision.Allowed)

        // Non-search Google pages must NOT be redirected or treated as search
        val mapsUrl = "https://maps.google.com"
        val mapsDecision = NavigationController.evaluate(mapsUrl, emptySet(), searchEngine = SearchEngine.DUCKDUCKGO)
        assertTrue(mapsDecision is NavigationDecision.Allowed)

        val accountsUrl = "https://accounts.google.com/signin"
        val accountsDecision = NavigationController.evaluate(accountsUrl, emptySet(), searchEngine = SearchEngine.DUCKDUCKGO)
        assertTrue(accountsDecision is NavigationDecision.Allowed)
    }

    @Test
    fun testIsGoogleHostValidation() {
        // Standard Google hosts
        assertTrue(ProtectionEngine.isGoogleHost("google.com"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.com"))
        assertTrue(ProtectionEngine.isGoogleHost("google.co.uk"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.co.uk"))
        assertTrue(ProtectionEngine.isGoogleHost("google.com.bd"))
        assertTrue(ProtectionEngine.isGoogleHost("www.google.com.bd"))
        assertTrue(ProtectionEngine.isGoogleHost("google.de"))
        assertTrue(ProtectionEngine.isGoogleHost("google.fr"))
        assertTrue(ProtectionEngine.isGoogleHost("google.ca"))
        assertTrue(ProtectionEngine.isGoogleHost("google.com."))

        // Subdomains of Google must NOT count as Google search
        assertFalse(ProtectionEngine.isGoogleHost("drive.google.com"))
        assertFalse(ProtectionEngine.isGoogleHost("docs.google.com"))
        assertFalse(ProtectionEngine.isGoogleHost("mail.google.com"))
        assertFalse(ProtectionEngine.isGoogleHost("photos.google.com"))
        assertFalse(ProtectionEngine.isGoogleHost("play.google.com"))

        // Malicious or unrelated domains
        assertFalse(ProtectionEngine.isGoogleHost("fakegoogle.com"))
        assertFalse(ProtectionEngine.isGoogleHost("google.com.attacker.com"))
        assertFalse(ProtectionEngine.isGoogleHost("notgoogle.com"))
        assertFalse(ProtectionEngine.isGoogleHost("google.com@evil.com"))
        assertFalse(ProtectionEngine.isGoogleHost("google.com:8080"))
        assertFalse(ProtectionEngine.isGoogleHost(""))
    }

    @Test
    fun testSearchEngineBuildSearchUrl() {
        assertEquals(
            "https://www.google.com/search?q=kotlin&safe=active",
            SearchEngine.GOOGLE.buildSearchUrl("kotlin")
        )
        assertEquals(
            "https://safe.duckduckgo.com/?q=kotlin&kp=1",
            SearchEngine.DUCKDUCKGO.buildSearchUrl("kotlin")
        )
    }
}
