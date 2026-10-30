package com.muslim.browser.pro.browser

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WebsiteTimeLimitTest {

    private lateinit var app: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences(SettingsRepository.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        repository = SettingsRepository(app)
    }

    @Test
    fun testFixedTwentyMinuteDailyAllowance() {
        // Daily limit must be exactly 20 minutes (1200 seconds)
        assertEquals(1200L, WebsiteTimeLimitPolicy.DAILY_LIMIT_SECONDS)

        val domains = setOf("example.com")

        // 1199 seconds: Not reached
        assertFalse(
            WebsiteTimeLimitPolicy.isTimeLimitReached(
                "https://example.com/page",
                domains,
                mapOf("example.com" to 1199L)
            )
        )
        assertEquals("19m 59s / 20m", WebsiteTimeLimitPolicy.formatUsageStatus(1199L))

        // 1200 seconds: Exactly reached
        assertTrue(
            WebsiteTimeLimitPolicy.isTimeLimitReached(
                "https://example.com/page",
                domains,
                mapOf("example.com" to 1200L)
            )
        )
        assertEquals("Limit reached (20m / 20m)", WebsiteTimeLimitPolicy.formatUsageStatus(1200L))

        // Exceeded
        assertTrue(
            WebsiteTimeLimitPolicy.isTimeLimitReached(
                "https://example.com/page",
                domains,
                mapOf("example.com" to 1350L)
            )
        )
        assertEquals("Limit reached (20m / 20m)", WebsiteTimeLimitPolicy.formatUsageStatus(1350L))
    }

    @Test
    fun testSharedDomainAllowanceAcrossSubdomainsAndPaths() {
        val domains = setOf("facebook.com")
        val usageMap = mapOf("facebook.com" to 1200L)

        // All subdomains and paths share the allowance and match "facebook.com"
        val urls = listOf(
            "https://facebook.com",
            "https://facebook.com/messages/123",
            "https://www.facebook.com/profile",
            "https://m.facebook.com/feed",
            "https://sub.touch.facebook.com/video?id=987",
            "http://facebook.com:8080/login"
        )

        for (url in urls) {
            val matched = WebsiteTimeLimitPolicy.findMatchingDomain(url, domains)
            assertEquals("facebook.com", matched)

            val isReached = WebsiteTimeLimitPolicy.isTimeLimitReached(url, domains, usageMap)
            assertTrue("Expected $url to be time limited", isReached)

            val decision = NavigationController.evaluate(
                url = url,
                customKeywords = emptySet(),
                isAdBlockingEnabled = false,
                timeLimitedDomains = domains,
                domainUsageSeconds = usageMap
            )
            assertTrue("Expected blocked decision for $url", decision is NavigationDecision.Blocked)
            val blocked = decision as NavigationDecision.Blocked
            assertEquals("Website Time Limit", blocked.reason)
            assertTrue(blocked.detail.contains("facebook.com"))
        }

        // Domain boundaries: Not matching unrelated or suffix spoofing domains
        assertNull(WebsiteTimeLimitPolicy.findMatchingDomain("https://notfacebook.com", domains))
        assertNull(WebsiteTimeLimitPolicy.findMatchingDomain("https://facebook.com.evil.com", domains))
        assertNull(WebsiteTimeLimitPolicy.findMatchingDomain("https://fakefacebook.com", domains))
    }

    @Test
    fun testUnrelatedDomainsRemainAccessible() {
        val domains = setOf("facebook.com", "instagram.com")
        val usageMap = mapOf(
            "facebook.com" to 1200L,
            "instagram.com" to 1200L
        )

        val allowedUrls = listOf(
            "https://en.wikipedia.org/wiki/Islam",
            "https://www.google.com/search?q=islamic+books",
            "https://github.com/trending",
            "https://bbc.com/news"
        )

        for (url in allowedUrls) {
            val isReached = WebsiteTimeLimitPolicy.isTimeLimitReached(url, domains, usageMap)
            assertFalse("Expected $url to remain accessible", isReached)

            val decision = NavigationController.evaluate(
                url = url,
                customKeywords = emptySet(),
                isAdBlockingEnabled = false,
                timeLimitedDomains = domains,
                domainUsageSeconds = usageMap
            )
            assertFalse("Expected allowed or modified decision for $url", decision is NavigationDecision.Blocked)
        }
    }

    @Test
    fun testDefaultPermanentFacebookEntries() {
        // Both facebook.com and m.facebook.com must be present by default without any user addition
        val defaults = repository.getTimeLimitedDomains()
        assertTrue("facebook.com must be present by default", defaults.contains("facebook.com"))
        assertTrue("m.facebook.com must be present by default", defaults.contains("m.facebook.com"))
        assertTrue("Default domains must contain at least the 2 default entries", defaults.size >= 2)

        // Duplicate attempts to add default domains must fail
        assertFalse("Re-adding facebook.com should fail as duplicate", repository.addTimeLimitedDomain("facebook.com"))
        assertFalse("Re-adding m.facebook.com should fail as duplicate", repository.addTimeLimitedDomain("m.facebook.com"))

        // Both websites must be separate entries
        val fbMatched = WebsiteTimeLimitPolicy.findMatchingDomain("https://facebook.com/feed", defaults)
        val mFbMatched = WebsiteTimeLimitPolicy.findMatchingDomain("https://m.facebook.com/home", defaults)
        assertEquals("facebook.com", fbMatched)
        assertEquals("m.facebook.com", mFbMatched)

        // Each website has the 20-minute daily time limit (1200 seconds)
        assertEquals(1200L, WebsiteTimeLimitPolicy.DAILY_LIMIT_SECONDS)

        // Neither can be deleted (no delete methods exist)
        val methods = SettingsRepository::class.java.declaredMethods.map { it.name }
        assertFalse("No remove method should exist for time limited domains", methods.contains("removeTimeLimitedDomain"))
        assertFalse("No delete method should exist for time limited domains", methods.contains("deleteTimeLimitedDomain"))
    }

    @Test
    fun testRejectionOfInvalidAndDuplicateDomains() {
        // Invalid domains
        assertFalse(repository.addTimeLimitedDomain(""))
        assertFalse(repository.addTimeLimitedDomain("   "))
        assertFalse(repository.addTimeLimitedDomain("invalid"))
        assertFalse(repository.addTimeLimitedDomain("has space.com"))
        assertFalse(repository.addTimeLimitedDomain("http://"))

        // Valid domain addition
        assertTrue(repository.addTimeLimitedDomain("customsite.com"))
        assertTrue(repository.getTimeLimitedDomains().contains("customsite.com"))

        // Duplicate attempts in different formats
        assertFalse("Duplicate raw domain should fail", repository.addTimeLimitedDomain("customsite.com"))
        assertFalse("Duplicate with www should fail", repository.addTimeLimitedDomain("www.customsite.com"))
        assertFalse("Duplicate with https:// and trailing slash should fail", repository.addTimeLimitedDomain("https://customsite.com/"))
        assertFalse("Duplicate with uppercase should fail", repository.addTimeLimitedDomain("CUSTOMSITE.COM"))
        assertFalse("Duplicate with leading/trailing spaces should fail", repository.addTimeLimitedDomain("  customsite.com  "))

        // Registered set contains the 2 defaults + 1 custom site
        assertEquals(3, repository.getTimeLimitedDomains().size)
    }

    @Test
    fun testPermanentNonRemovableDomainsEnforcement() {
        repository.addTimeLimitedDomain("youtube.com")
        repository.addTimeLimitedDomain("tiktok.com")

        val registered = repository.getTimeLimitedDomains()
        assertTrue(registered.contains("facebook.com"))
        assertTrue(registered.contains("m.facebook.com"))
        assertTrue(registered.contains("youtube.com"))
        assertTrue(registered.contains("tiktok.com"))
        assertEquals(4, registered.size)

        // Verify that SettingsRepository has no removeTimeLimitedDomain method
        // and that standard clearing does not delete permanent time limited domains
        val methods = SettingsRepository::class.java.declaredMethods.map { it.name }
        assertFalse("No remove method should exist for time limited domains", methods.contains("removeTimeLimitedDomain"))
        assertFalse("No delete method should exist for time limited domains", methods.contains("deleteTimeLimitedDomain"))
    }

    @Test
    fun testAbilityToAddDomainsSequentiallyAtAnyTime() {
        val initial = repository.getTimeLimitedDomains()
        assertEquals(setOf("facebook.com", "m.facebook.com"), initial)

        assertTrue(repository.addTimeLimitedDomain("site1.com"))
        assertEquals(setOf("facebook.com", "m.facebook.com", "site1.com"), repository.getTimeLimitedDomains())

        assertTrue(repository.addTimeLimitedDomain("site2.org"))
        assertEquals(setOf("facebook.com", "m.facebook.com", "site1.com", "site2.org"), repository.getTimeLimitedDomains())

        assertTrue(repository.addTimeLimitedDomain("site3.net"))
        assertEquals(setOf("facebook.com", "m.facebook.com", "site1.com", "site2.org", "site3.net"), repository.getTimeLimitedDomains())
    }

    @Test
    fun testSharedPreferencesPersistenceAcrossRestarts() {
        repository.addTimeLimitedDomain("reddit.com")
        repository.saveDomainUsage(mapOf("reddit.com" to 750L))

        // Simulate app process restart by creating a new repository reading from SharedPreferences
        val restartedRepo = SettingsRepository(app)

        val domains = restartedRepo.getTimeLimitedDomains()
        assertTrue(domains.contains("reddit.com"))

        val usage = restartedRepo.getDomainUsageSeconds()
        assertEquals(750L, usage["reddit.com"])
    }

    @Test
    fun testDailyResetAcrossLocalCalendarDates() {
        val prefs = app.getSharedPreferences(SettingsRepository.PREFS_NAME, Context.MODE_PRIVATE)

        // Store domain and usage with yesterday's date using repository keys
        prefs.edit()
            .putStringSet(SettingsRepository.KEY_TIME_LIMIT_DOMAINS, setOf("twitter.com"))
            .putString(SettingsRepository.KEY_TIME_LIMIT_DATE, "2020-01-01")
            .putString(SettingsRepository.KEY_TIME_LIMIT_USAGE, """{"twitter.com":1200}""")
            .commit()

        val newRepo = SettingsRepository(app)

        // The domain itself remains registered
        assertTrue(newRepo.getTimeLimitedDomains().contains("twitter.com"))

        // Daily usage automatically resets to empty map because saved date is yesterday
        val usage = newRepo.getDomainUsageSeconds()
        assertTrue("Usage should reset on a new calendar date", usage.isEmpty())

        // The domain now has a fresh 20-minute allowance
        assertFalse(
            WebsiteTimeLimitPolicy.isTimeLimitReached(
                "https://twitter.com",
                newRepo.getTimeLimitedDomains(),
                usage
            )
        )
    }

    @Test
    fun testExistingProtectionFeaturesRemainUnaffected() {
        // SafeSearch enforcement check
        val googleSearch = "https://www.google.com/search?q=halal+food"
        val googleDecision = NavigationController.evaluate(
            url = googleSearch,
            customKeywords = emptySet(),
            searchEngine = SearchEngine.GOOGLE,
            isAdBlockingEnabled = false
        )
        assertTrue(googleDecision is NavigationDecision.Redirect)
        val targetUrl = (googleDecision as NavigationDecision.Redirect).url
        assertTrue(targetUrl.contains("safe=active"))

        // Custom keyword blocking check
        val keywordBlocked = NavigationController.evaluate(
            url = "https://duckduckgo.com/?q=casino+online",
            customKeywords = setOf("casino"),
            isAdBlockingEnabled = false
        )
        assertTrue(keywordBlocked is NavigationDecision.Blocked)
        assertEquals("Custom Keyword Protection", (keywordBlocked as NavigationDecision.Blocked).reason)

        // Ad blocking rule check
        val adUrl = "https://adservice.google.com/ads.js"
        val adDecision = NavigationController.evaluate(
            url = adUrl,
            customKeywords = emptySet(),
            isAdBlockingEnabled = true
        )
        assertTrue(adDecision is NavigationDecision.Blocked)
        assertEquals("Ad Protection", (adDecision as NavigationDecision.Blocked).reason)
    }

    @Test
    fun testActiveTimeTrackingLifecycleAndPause() {
        val viewModel = BrowserViewModel(app)

        // Register domain
        assertTrue(viewModel.addTimeLimitedDomain("news.com"))
        assertTrue(viewModel.uiState.value.timeLimitedDomains.contains("news.com"))

        // Navigate to tracked domain
        viewModel.loadTargetUrl("https://news.com/today")
        viewModel.onPageFinished("https://news.com/today", "News Today", canGoBack = false, canGoForward = false)
        assertEquals("news.com", viewModel.activeTrackedDomain)

        // Move browser to background -> pause tracking
        viewModel.onAppForegroundStateChanged(false)
        assertNull("Tracking should pause when app is in background", viewModel.activeTrackedDomain)

        // Resume browser to foreground -> resume tracking
        viewModel.onAppForegroundStateChanged(true)
        assertEquals("news.com", viewModel.activeTrackedDomain)

        // Store tab id, then open new tab to homepage -> tracking pauses for news.com
        val originalTabId = viewModel.uiState.value.currentTabId
        viewModel.openNewTab()
        assertNull("Tracking should be inactive on homepage tab", viewModel.activeTrackedDomain)

        // Switch back to original tab -> tracking resumes
        viewModel.selectTab(originalTabId)
        assertEquals("news.com", viewModel.activeTrackedDomain)
    }

    @Test
    fun testLiveBlockingWhenLimitReached() {
        // Register domain and pre-fill usage to 1200 seconds (limit reached) in repository
        repository.addTimeLimitedDomain("games.com")
        repository.saveDomainUsage(mapOf("games.com" to 1200L))

        // Create viewModel which reads this initial state
        val viewModel = BrowserViewModel(app)
        assertTrue(viewModel.uiState.value.timeLimitedDomains.contains("games.com"))
        assertEquals(1200L, viewModel.uiState.value.timeLimitUsageSeconds["games.com"])

        // Trigger navigation to games.com
        val navigated = viewModel.submitQueryOrUrl("https://games.com/play")
        assertFalse("Navigation should be prevented when time limit reached", navigated)

        // Blocked info should be populated
        val blocked = viewModel.uiState.value.blockedInfo
        assertEquals("Website Time Limit", blocked.reason)
        assertTrue(blocked.detail.contains("games.com"))
        assertEquals("https://games.com/play", blocked.targetUrl)
    }
}
