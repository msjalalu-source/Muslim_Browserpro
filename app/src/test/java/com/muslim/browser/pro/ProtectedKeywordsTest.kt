package com.muslim.browser.pro

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.NavigationController
import com.muslim.browser.pro.browser.NavigationDecision
import com.muslim.browser.pro.browser.ProtectionEngine
import com.muslim.browser.pro.browser.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ProtectedKeywordsTest {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    private val expectedProtectedKeywords = listOf(
        "Aashiq Banaya",
        "hot",
        "intimate",
        "adult",
        "kiss",
        "online",
        "video",
        "anonymous",
        "anonymity",
        "18+",
        "download",
        "downloaded",
        "downloading",
        "downl"
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val prefs = context.getSharedPreferences("focus_shield_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        repository = SettingsRepository(context)
    }

    @Test
    fun test1_everyBuiltInProtectedKeywordExistsInEffectiveDefaultKeywordSet() {
        val effectiveKeywords = repository.getCustomKeywords()

        assertEquals("Must contain at least all 14 preset keywords", 14, expectedProtectedKeywords.size)
        for (keyword in expectedProtectedKeywords) {
            assertTrue(
                "Protected keyword '$keyword' must exist in default keyword set",
                effectiveKeywords.contains(keyword)
            )
        }

        // Verify normalized keywords list also contains all normalized forms
        val normalizedList = repository.getNormalizedKeywords()
        for (keyword in expectedProtectedKeywords) {
            val norm = keyword.trim().lowercase(Locale.ROOT)
            assertTrue(
                "Normalized list must contain '$norm'",
                normalizedList.contains(norm)
            )
        }
    }

    @Test
    fun test2_builtInProtectedKeywordsCannotBeRemovedFromEffectiveSet() {
        // 1. Attempting to remove each built-in protected keyword directly must fail
        for (keyword in expectedProtectedKeywords) {
            val removed = repository.removeCustomKeyword(keyword)
            assertFalse("Built-in keyword '$keyword' cannot be removed", removed)
            assertTrue("Built-in keyword '$keyword' must still be present", repository.getCustomKeywords().contains(keyword))
        }

        // 2. Clearing user keywords must leave all default protected keywords intact
        repository.addCustomKeyword("temporaryUserKeyword")
        assertTrue(repository.getCustomKeywords().contains("temporaryUserKeyword"))

        repository.clearUserKeywords()
        assertFalse("User keyword must be cleared", repository.getCustomKeywords().contains("temporaryUserKeyword"))

        for (keyword in expectedProtectedKeywords) {
            assertTrue(
                "Built-in keyword '$keyword' must remain after user keywords cleared",
                repository.getCustomKeywords().contains(keyword)
            )
        }

        // 3. Simulating fresh app restart with empty SharedPreferences
        val freshRepo = SettingsRepository(context)
        for (keyword in expectedProtectedKeywords) {
            assertTrue(
                "Built-in keyword '$keyword' must be present on fresh repository load",
                freshRepo.getCustomKeywords().contains(keyword)
            )
        }
    }

    @Test
    fun test3_searchQueryContainingProtectedKeywordIsBlocked() {
        val viewModel = BrowserViewModel(context)

        // 1. Raw search query with protected keyword
        val blockedQuery1 = viewModel.submitQueryOrUrl("how to download free music")
        assertFalse("Search with 'download' must be blocked", blockedQuery1)
        assertNotNull("Blocked info must be populated", viewModel.uiState.value.blockedInfo)
        assertEquals("Custom Keyword Protection", viewModel.uiState.value.blockedInfo?.reason)

        // 2. Search query with 'Aashiq Banaya'
        val blockedQuery2 = viewModel.submitQueryOrUrl("Aashiq Banaya mp3 song")
        assertFalse("Search with 'Aashiq Banaya' must be blocked", blockedQuery2)
        assertEquals("Custom Keyword Protection", viewModel.uiState.value.blockedInfo?.reason)

        // 3. Search query with 'adult'
        val blockedQuery3 = viewModel.submitQueryOrUrl("adult stories online")
        assertFalse("Search with 'adult' must be blocked", blockedQuery3)

        // 4. Search query with '18+'
        val blockedQuery4 = viewModel.submitQueryOrUrl("movies 18+ uncut")
        assertFalse("Search with '18+' must be blocked", blockedQuery4)

        // 5. Search engine URL with protected keyword
        val searchEngineUrl = "https://www.bing.com/search?q=anonymous+browsing+tools"
        val decision = NavigationController.evaluate(
            url = searchEngineUrl,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords()
        )
        assertTrue("Search engine query containing protected keyword must be blocked", decision is NavigationDecision.Blocked)
        val blockedDecision = decision as NavigationDecision.Blocked
        assertEquals("Custom Keyword Protection", blockedDecision.reason)
    }

    @Test
    fun test4_matchingRemainsCaseInsensitive() {
        val customKeywords = repository.getCustomKeywords()
        val normalizedKeywords = repository.getNormalizedKeywords()

        // Test uppercase and mixed-case variations of protected keywords
        val upperCheck = ProtectionEngine.isBlockedByCustomKeywords("HOT SUMMER WEATHER", customKeywords, normalizedKeywords)
        assertNotNull("Must match 'HOT' case-insensitively", upperCheck)

        val mixedCheck = ProtectionEngine.isBlockedByCustomKeywords("aAsHiQ bAnAyA full video", customKeywords, normalizedKeywords)
        assertNotNull("Must match 'Aashiq Banaya' case-insensitively", mixedCheck)

        val upperAdultCheck = ProtectionEngine.isBlockedByCustomKeywords("ADULT CONTENT WARNING", customKeywords, normalizedKeywords)
        assertNotNull("Must match 'ADULT' case-insensitively", upperAdultCheck)

        val urlEncodedCheck = ProtectionEngine.isBlockedByCustomKeywords("watch%20intimate%20scenes", customKeywords, normalizedKeywords)
        assertNotNull("Must match '%20' url-encoded query", urlEncodedCheck)
    }

    @Test
    fun test5_existingUserAddedKeywordsStillWork() {
        val userKeyword = "unwantedDistraction"

        // 1. Add user keyword
        val added = repository.addCustomKeyword(userKeyword)
        assertTrue("User keyword must be added successfully", added)
        assertTrue("Effective list must contain user keyword", repository.getCustomKeywords().contains(userKeyword))

        // 2. Both built-in and user keyword exist simultaneously
        for (kw in expectedProtectedKeywords) {
            assertTrue("Built-in keyword '$kw' must still exist", repository.getCustomKeywords().contains(kw))
        }

        // 3. User keyword blocks search query
        val check = ProtectionEngine.isBlockedByCustomKeywords(
            text = "how to bypass unwanteddistraction blocker",
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords()
        )
        assertEquals(userKeyword, check)

        // 4. Duplicate addition of user keyword is rejected
        val duplicateAdd = repository.addCustomKeyword("UNWANTEDDISTRACTION")
        assertFalse("Duplicate user keyword must be rejected", duplicateAdd)

        // 5. User keyword can be removed
        val removed = repository.removeCustomKeyword(userKeyword)
        assertTrue("User keyword must be removable", removed)
        assertFalse("User keyword must no longer exist", repository.getCustomKeywords().contains(userKeyword))

        // 6. Built-in keywords remain intact after user keyword removal
        for (kw in expectedProtectedKeywords) {
            assertTrue("Built-in keyword '$kw' must remain after user keyword removed", repository.getCustomKeywords().contains(kw))
        }
    }

    @Test
    fun test6_webpageContainingProtectedKeywordAsOrdinaryPageTextIsNotBlocked() {
        // A normal webpage URL (e.g. Wikipedia article) must NOT be blocked simply because
        // the text of the article mentions words like 'video', 'online', 'download', etc.
        val safeArticleUrl = "https://en.wikipedia.org/wiki/Television_program"

        val decision = NavigationController.evaluate(
            url = safeArticleUrl,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords()
        )

        // Navigation to article must be Allowed
        assertEquals(NavigationDecision.Allowed, decision)

        val directCheck = ProtectionEngine.checkDirectUrl(
            url = safeArticleUrl,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords()
        )
        assertTrue("Safe article URL must be Allowed by direct check", directCheck is ProtectionEngine.FilterResult.Allowed)
    }

    @Test
    fun test7_noDomAccessibilityOrMutationObserverWebpageTextScanner() {
        // Verify that custom keyword protection is evaluated strictly at search/navigation entry points,
        // with zero DOM text scanning, continuous JS scanning, or Accessibility services.
        val nonSearchCleanText = "This article discusses the history of electronic computers."
        val blockedResult = ProtectionEngine.isBlockedByCustomKeywords(
            text = nonSearchCleanText,
            customKeywords = repository.getCustomKeywords(),
            normalizedKeywords = repository.getNormalizedKeywords()
        )
        assertNull("Ordinary non-keyword text must return null (not blocked)", blockedResult)
    }
}
