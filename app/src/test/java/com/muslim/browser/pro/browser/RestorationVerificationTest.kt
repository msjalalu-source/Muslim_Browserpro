package com.muslim.browser.pro.browser

import android.app.Application
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
        val safeGoogleUrl = "https://www.google.com/search?q=weather+today"
        val decision = NavigationController.evaluate(
            url = safeGoogleUrl,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.GOOGLE
        )
        // Should not indiscriminately redirect Google search to DuckDuckGo
        assertTrue(
            "Allowed direct search navigation for Google",
            decision is NavigationDecision.Allowed
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
}
