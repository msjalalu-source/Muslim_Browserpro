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
        context.getSharedPreferences(SettingsRepository.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
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

    @Test
    fun testAllElevenRequiredBuiltInKeywordsExistByDefault() {
        val repo = SettingsRepository(context)
        val keywords = repo.getCustomKeywords()

        val expectedBuiltInKeywords = listOf(
            "Aashiq Banaya",
            "hot",
            "intimate",
            "adult",
            "kiss",
            "video",
            "anonymous",
            "anonymity",
            "18+",
            "download",
            "apk"
        )

        assertEquals(11, expectedBuiltInKeywords.size)

        for (expected in expectedBuiltInKeywords) {
            val found = keywords.any {
                SettingsRepository.normalizeKeyword(it) == SettingsRepository.normalizeKeyword(expected)
            }
            assertTrue("Expected built-in keyword '$expected' to be present by default", found)
            assertTrue(
                "Expected '$expected' to be identified as protected",
                SettingsRepository.isProtected(expected)
            )
            assertTrue(
                "Expected repo.isProtectedKeyword('$expected') to be true",
                repo.isProtectedKeyword(expected)
            )
        }
    }

    @Test
    fun testMissingDefaultsAddedSafelyToExistingInstallationsAndUserKeywordsPreserved() {
        val existingKeywords = setOf("my_custom_tag", "hot", "saved_vpn_item")
        context.getSharedPreferences(SettingsRepository.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(SettingsRepository.KEY_CUSTOM_KEYWORDS, existingKeywords)
            .commit()

        val repo = SettingsRepository(context)
        val loaded = repo.getCustomKeywords()

        // 1. Existing user-added keywords preserved
        assertTrue("Expected user custom keyword to be preserved", loaded.contains("my_custom_tag"))
        assertTrue("Expected existing keyword to be preserved", loaded.contains("saved_vpn_item"))
        assertTrue("Expected existing keyword to be preserved", loaded.contains("hot"))

        // 2. All 11 default keywords now present
        val expectedBuiltInKeywords = listOf(
            "Aashiq Banaya", "hot", "intimate", "adult", "kiss", "video",
            "anonymous", "anonymity", "18+", "download", "apk"
        )
        for (kw in expectedBuiltInKeywords) {
            val exists = loaded.any {
                SettingsRepository.normalizeKeyword(it) == SettingsRepository.normalizeKeyword(kw)
            }
            assertTrue("Expected missing default '$kw' to be added safely", exists)
        }

        // 3. "hot" was already present, must not be duplicated
        val hotCount = loaded.count { SettingsRepository.normalizeKeyword(it) == "hot" }
        assertEquals("Duplicate 'hot' must not be created", 1, hotCount)
    }

    @Test
    fun testBuiltInKeywordsCannotBeRemovedThroughRepositoryRemovalOperations() {
        val repo = SettingsRepository(context)
        val initialKeywords = repo.getCustomKeywords()

        val builtInKeywords = listOf(
            "Aashiq Banaya", "hot", "intimate", "adult", "kiss", "video",
            "anonymous", "anonymity", "18+", "download", "apk"
        )

        for (kw in builtInKeywords) {
            // Attempt standard removal
            val removeResult = repo.removeCustomKeyword(kw)
            assertFalse("Repository removal of built-in keyword '$kw' must return false", removeResult)

            // Attempt uppercase / whitespace varied removal
            val upperRemove = repo.removeCustomKeyword(kw.uppercase())
            assertFalse("Repository removal of uppercase '$kw' must return false", upperRemove)

            val spacedRemove = repo.removeCustomKeyword("   $kw   ")
            assertFalse("Repository removal of spaced '$kw' must return false", spacedRemove)
        }

        val afterAttempt = repo.getCustomKeywords()
        for (kw in builtInKeywords) {
            val exists = afterAttempt.any {
                SettingsRepository.normalizeKeyword(it) == SettingsRepository.normalizeKeyword(kw)
            }
            assertTrue("Built-in keyword '$kw' must remain present after removal attempts", exists)
        }
    }

    @Test
    fun testUserAddedCustomKeywordsRemainRemovable() {
        val repo = SettingsRepository(context)

        val userKeyword = "custom-tracker-block"
        val added = repo.addCustomKeyword(userKeyword)
        assertTrue("User keyword addition must return true", added)

        val withUser = repo.getCustomKeywords()
        assertTrue("User keyword must be present in repository", withUser.contains(userKeyword))
        assertFalse("User keyword must NOT be marked as protected", repo.isProtectedKeyword(userKeyword))

        val removed = repo.removeCustomKeyword(userKeyword)
        assertTrue("User keyword removal must return true", removed)

        val afterRemove = repo.getCustomKeywords()
        assertFalse("User keyword must be removed from repository", afterRemove.contains(userKeyword))
    }

    @Test
    fun testDuplicateBuiltInEntriesAreNotCreated() {
        val repo = SettingsRepository(context)
        val initialSize = repo.getCustomKeywords().size

        // Attempt to add duplicates with different case and whitespace
        assertFalse(repo.addCustomKeyword("hot"))
        assertFalse(repo.addCustomKeyword("HOT"))
        assertFalse(repo.addCustomKeyword("  hot  "))
        assertFalse(repo.addCustomKeyword("aashiq banaya"))
        assertFalse(repo.addCustomKeyword("Aashiq Banaya"))
        assertFalse(repo.addCustomKeyword("   Aashiq    Banaya   "))
        assertFalse(repo.addCustomKeyword("18+"))
        assertFalse(repo.addCustomKeyword("  18+  "))
        assertFalse(repo.addCustomKeyword("download"))
        assertFalse(repo.addCustomKeyword("apk"))

        assertEquals("Repository size must remain unchanged when adding duplicate entries", initialSize, repo.getCustomKeywords().size)
    }

    @Test
    fun testExistingKeywordMatchingAndSearchBlockingFunctionalWithRequiredKeywords() {
        val repo = SettingsRepository(context)
        val keywords = repo.getCustomKeywords()

        // 1. "Aashiq Banaya" phrase search blocking
        val aashiqUrl = "https://www.google.com/search?q=Aashiq+Banaya+video+song"
        val decision1 = NavigationController.evaluate(aashiqUrl, keywords)
        assertTrue(decision1 is NavigationDecision.Blocked)

        // 2. "18+" search blocking
        val plusUrl = "https://duckduckgo.com/?q=top+movies+18%2B"
        val decision2 = NavigationController.evaluate(plusUrl, keywords)
        assertTrue(decision2 is NavigationDecision.Blocked)

        // 3. "hot" search blocking
        val hotUrl = "https://www.google.com/search?q=hot+scenes"
        val decision3 = NavigationController.evaluate(hotUrl, keywords)
        assertTrue(decision3 is NavigationDecision.Blocked)

        // 4. "adult" search blocking on website search
        val adultUrl = "https://en.wikipedia.org/w/index.php?search=adult+content"
        val decision4 = NavigationController.evaluate(adultUrl, keywords)
        assertTrue(decision4 is NavigationDecision.Blocked)

        // 5. "download" search blocking
        val downloadUrl = "https://www.bing.com/search?q=download+crack"
        val decision5 = NavigationController.evaluate(downloadUrl, keywords)
        assertTrue(decision5 is NavigationDecision.Blocked)

        // 6. "apk" search blocking
        val apkUrl = "https://duckduckgo.com/?q=game+mod+apk"
        val decision6 = NavigationController.evaluate(apkUrl, keywords)
        assertTrue(decision6 is NavigationDecision.Blocked)

        // 7. Allowed searches without protected keywords
        val safeDdgUrl = "https://safe.duckduckgo.com/?q=islamic+history+and+architecture&kp=1"
        val safeDecision = NavigationController.evaluate(safeDdgUrl, keywords, searchEngine = SearchEngine.DUCKDUCKGO)
        assertTrue(safeDecision is NavigationDecision.Allowed)

        val safeGoogleUrl = "https://www.google.com/search?q=islamic+history+and+architecture&safe=active"
        val safeGoogleDecision = NavigationController.evaluate(safeGoogleUrl, keywords, searchEngine = SearchEngine.GOOGLE)
        assertTrue(safeGoogleDecision is NavigationDecision.Allowed)
    }

    @Test
    fun testUiNonDeletableProtectionDistinction() {
        // Built-in keywords are identified as protected (UI renders Lock icon without Delete button)
        assertTrue(SettingsRepository.isProtected("Aashiq Banaya"))
        assertTrue(SettingsRepository.isProtected("aashiq banaya"))
        assertTrue(SettingsRepository.isProtected("hot"))
        assertTrue(SettingsRepository.isProtected("HOT"))
        assertTrue(SettingsRepository.isProtected("  intimate  "))
        assertTrue(SettingsRepository.isProtected("adult"))
        assertTrue(SettingsRepository.isProtected("kiss"))
        assertTrue(SettingsRepository.isProtected("video"))
        assertTrue(SettingsRepository.isProtected("anonymous"))
        assertTrue(SettingsRepository.isProtected("anonymity"))
        assertTrue(SettingsRepository.isProtected("18+"))
        assertTrue(SettingsRepository.isProtected("download"))
        assertTrue(SettingsRepository.isProtected("apk"))

        // User-added custom keyword is NOT protected (UI renders Delete button)
        assertFalse(SettingsRepository.isProtected("my_temporary_block"))
        assertFalse(SettingsRepository.isProtected("user_tag"))
    }

    @Test
    fun testThirdPartyWebsiteWordPressAndCustomSearchQueryExtraction() {
        val keywords = setOf("casino", "18+")

        // WordPress search: ?s=casino
        val wpUrl = "https://myblog.example.com/?s=casino"
        val wpDecision = NavigationController.evaluate(wpUrl, keywords)
        assertTrue(wpDecision is NavigationDecision.Blocked)

        // General search parameters: searchTerm, search_term, text
        val paramUrl1 = "https://shop.example.com/items?searchTerm=casino+chips"
        assertTrue(NavigationController.evaluate(paramUrl1, keywords) is NavigationDecision.Blocked)

        val paramUrl2 = "https://portal.example.com/browse?text=casino"
        assertTrue(NavigationController.evaluate(paramUrl2, keywords) is NavigationDecision.Blocked)

        // WordPress post link with ?p=... MUST NOT be treated as search query (prevent false positive)
        val wpPostUrl = "https://myblog.example.com/?p=18"
        val wpPostDecision = NavigationController.evaluate(wpPostUrl, keywords)
        assertTrue("WordPress post link ?p=18 must not trigger false positive for keyword '18+'", wpPostDecision is NavigationDecision.Allowed)

        // Verify generic search query extraction handles search parameters and ignores unrelated parameters
        assertEquals("casino", ProtectionEngine.extractGenericSearchQuery(wpUrl))
        assertEquals("casino chips", ProtectionEngine.extractGenericSearchQuery(paramUrl1))
        assertEquals("casino", ProtectionEngine.extractGenericSearchQuery(paramUrl2))
        assertNull("p parameter must not be extracted as generic search query", ProtectionEngine.extractGenericSearchQuery(wpPostUrl))
        assertNull("user parameter must not be extracted as generic search query", ProtectionEngine.extractGenericSearchQuery("https://forum.example.com/profile?user=casino_fan"))

        // Unrelated page without keywords must be allowed
        val userProfileUrl = "https://forum.example.com/profile?user=regular_member"
        assertTrue(NavigationController.evaluate(userProfileUrl, keywords) is NavigationDecision.Allowed)
    }

    @Test
    fun testKeywordBlockingScriptProgrammaticSubmitAndAriaSearchboxCoverage() {
        val keywords = setOf("forbidden_content")
        val script = ProtectionEngine.buildKeywordBlockingScript(keywords)

        // Verify HTMLFormElement.prototype.submit hooking for programmatic search triggers
        assertTrue("Script must hook HTMLFormElement.prototype.submit", script.contains("HTMLFormElement.prototype.submit"))

        // Verify searchbox and combobox ARIA roles
        assertTrue("Script must check role === 'searchbox'", script.contains("role === 'searchbox'"))
        assertTrue("Script must handle WordPress 's' parameter", script.contains("n === 's'"))
        assertTrue("Script must check searchTerm parameter", script.contains("n === 'searchterm'"))

        // Verify proximity resolution for buttons outside form
        assertTrue("Script must resolve button proximity for inputs", script.contains("btn.parentElement"))
    }
}
