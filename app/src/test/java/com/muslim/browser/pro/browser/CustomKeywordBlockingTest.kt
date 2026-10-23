package com.muslim.browser.pro.browser

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CustomKeywordBlockingTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testCaseInsensitiveAndWhitespaceNormalizedMatching() {
        val keywords = setOf("bad word", "gambling")

        // 1. Case-insensitivity
        assertEquals("gambling", ProtectionEngine.isBlockedByCustomKeywords("GAMBLING", keywords))
        assertEquals("gambling", ProtectionEngine.isBlockedByCustomKeywords("GaMbLiNg online", keywords))

        // 2. Surrounding whitespace in query
        assertEquals("bad word", ProtectionEngine.isBlockedByCustomKeywords("   bad word   ", keywords))

        // 3. Repeated whitespace between words in query
        assertEquals("bad word", ProtectionEngine.isBlockedByCustomKeywords("bad    word", keywords))
        assertEquals("bad word", ProtectionEngine.isBlockedByCustomKeywords("how to find bad   word here", keywords))

        // 4. Repeated whitespace in saved keyword itself
        val spacedKeywords = setOf("  bad   content  ")
        assertEquals("  bad   content  ", ProtectionEngine.isBlockedByCustomKeywords("bad content", spacedKeywords))
        assertEquals("  bad   content  ", ProtectionEngine.isBlockedByCustomKeywords("bad   content", spacedKeywords))

        // 5. Allowed ordinary terms
        assertNull(ProtectionEngine.isBlockedByCustomKeywords("halal news and articles", keywords))
        assertNull(ProtectionEngine.isBlockedByCustomKeywords("islamic prayer times", keywords))
    }

    @Test
    fun testSearchEngineAndGenericWebsiteSearchUrlBlocking() {
        val keywords = setOf("casino")

        // Global search engines
        val googleSearch = "https://www.google.com/search?q=best+online+casino"
        val googleDecision = NavigationController.evaluate(googleSearch, keywords)
        assertTrue(googleDecision is NavigationDecision.Blocked)
        assertEquals("Custom Keyword Protection", (googleDecision as NavigationDecision.Blocked).reason)

        val ddgSearch = "https://duckduckgo.com/?q=casino+games"
        val ddgDecision = NavigationController.evaluate(ddgSearch, keywords)
        assertTrue(ddgDecision is NavigationDecision.Blocked)

        // Generic website search boxes (Wikipedia, Github, Reddit, e-commerce, etc.)
        val wikiSearch = "https://en.wikipedia.org/w/index.php?search=casino+rules"
        val wikiDecision = NavigationController.evaluate(wikiSearch, keywords)
        assertTrue(wikiDecision is NavigationDecision.Blocked)

        val githubSearch = "https://github.com/search?q=casino+bot"
        val githubDecision = NavigationController.evaluate(githubSearch, keywords)
        assertTrue(githubDecision is NavigationDecision.Blocked)

        val siteSearch = "https://example.org/find?query=casino"
        val siteDecision = NavigationController.evaluate(siteSearch, keywords)
        assertTrue(siteDecision is NavigationDecision.Blocked)
    }

    @Test
    fun testOrdinaryNonSearchWebpagesNotBlocked() {
        val keywords = setOf("poker")

        // Unrelated page whose URL does not contain search query parameter with the keyword
        val normalPage = "https://en.wikipedia.org/wiki/Mathematical_probability"
        val decision = NavigationController.evaluate(normalPage, keywords)
        assertTrue(decision is NavigationDecision.Allowed)

        val normalNews = "https://news.ycombinator.com/news"
        val newsDecision = NavigationController.evaluate(normalNews, keywords)
        assertTrue(newsDecision is NavigationDecision.Allowed)
    }

    @Test
    fun testBrowserViewModelSubmitQueryOrUrlRejectsBlockedKeywords() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val repo = SettingsRepository(app)
        repo.addCustomKeyword("blockedterm")

        val viewModel = BrowserViewModel(app)

        // Blocked search input in address bar
        val blockedResult = viewModel.submitQueryOrUrl("how to do blockedterm")
        assertFalse(blockedResult)

        // Safe search input in address bar
        val safeResult = viewModel.submitQueryOrUrl("quran recitation online")
        assertTrue(safeResult)

        // Cleanup
        repo.removeCustomKeyword("blockedterm")
    }

    @Test
    fun testKeywordBlockingScriptContainsRequiredProtections() {
        val keywords = setOf("restricted_topic", "gambling")
        val script = ProtectionEngine.buildKeywordBlockingScript(keywords)

        // Must define global keywords storage and installed flag
        assertTrue(script.contains("window.__mbp_keywords"))
        assertTrue(script.contains("window.__mbp_kw_installed"))
        assertTrue(script.contains("restricted_topic"))
        assertTrue(script.contains("gambling"))

        // Must intercept Enter key, form submit, and search button click
        assertTrue(script.contains("addEventListener('keydown'"))
        assertTrue(script.contains("addEventListener('submit'"))
        assertTrue(script.contains("addEventListener('click'"))

        // Must signal Android app via custom scheme
        assertTrue(script.contains("mbp-blocked-search://block?kw="))

        // Must distinguish search inputs from ordinary textareas/fields
        assertTrue(script.contains("isSearchInput"))
    }
}
