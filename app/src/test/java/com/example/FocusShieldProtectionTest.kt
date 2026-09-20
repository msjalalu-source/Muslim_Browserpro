package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.browser.ProtectionEngine
import com.example.browser.SettingsRepository
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FocusShieldProtectionTest {

    private lateinit var context: Context
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Clear prefs for fresh test runs
        context.getSharedPreferences("focus_shield_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        repository = SettingsRepository(context)
    }

    // ==========================================
    // 1. CUSTOM KEYWORD BLOCKING TESTS
    // ==========================================

    @Test
    fun `test add keyword and persist keyword`() {
        val added = repository.addCustomKeyword("gambling")
        assertTrue("Keyword should be added successfully", added)
        assertTrue("Custom keywords must contain gambling", repository.getCustomKeywords().contains("gambling"))

        // Create new repository instance to simulate app restart
        val reloadedRepository = SettingsRepository(context)
        assertTrue(
            "Keyword must survive app restart",
            reloadedRepository.getCustomKeywords().contains("gambling")
        )
    }

    @Test
    fun `test duplicate keyword prevented`() {
        val firstAdd = repository.addCustomKeyword("distraction")
        assertTrue(firstAdd)

        // Attempt to add duplicate with different casing
        val duplicateAdd = repository.addCustomKeyword("DISTRACTION")
        assertFalse("Duplicate keyword with different casing must be rejected", duplicateAdd)

        // Attempt to add blank
        val blankAdd = repository.addCustomKeyword("   ")
        assertFalse("Blank keyword must be rejected", blankAdd)
    }

    @Test
    fun `test case-insensitive matching for custom keyword`() {
        val keywords = setOf("SocialFeed")

        val resultLower = ProtectionEngine.checkUrlOrQuery("how to open socialfeed today", keywords)
        assertTrue(resultLower is ProtectionEngine.FilterResult.Blocked)

        val resultUpper = ProtectionEngine.checkUrlOrQuery("HTTPS://SOCIALFEED.COM", keywords)
        assertTrue(resultUpper is ProtectionEngine.FilterResult.Blocked)
    }

    @Test
    fun `test search blocked for custom keyword`() {
        val keywords = setOf("blockedgame")
        val searchResult = ProtectionEngine.checkUrlOrQuery("best cheats for blockedgame download", keywords)
        assertTrue(searchResult is ProtectionEngine.FilterResult.Blocked)
        val blocked = searchResult as ProtectionEngine.FilterResult.Blocked
        assertEquals("Custom Keyword Protection", blocked.reason)
    }

    @Test
    fun `test url navigation blocked for custom keyword`() {
        val keywords = setOf("timewaster")
        val navResult = ProtectionEngine.checkUrlOrQuery("https://www.example.com/play/timewaster", keywords)
        assertTrue(navResult is ProtectionEngine.FilterResult.Blocked)
    }

    // ==========================================
    // 2. PERMANENT RESTRICTIONS TESTS (NO TOGGLE)
    // ==========================================

    @Test
    fun `test adult protection is always enabled for known adult domains`() {
        val adultDomains = listOf(
            "https://www.pornhub.com",
            "https://xvideos.com/video123",
            "https://redtube.com/watch",
            "https://www.xnxx.com"
        )
        for (url in adultDomains) {
            val res = ProtectionEngine.checkDirectUrl(url, emptySet())
            assertTrue("Domain $url must be permanently blocked", res is ProtectionEngine.FilterResult.Blocked)
            val blocked = res as ProtectionEngine.FilterResult.Blocked
            assertEquals("Adult Content Protection", blocked.reason)
        }
    }

    @Test
    fun `test google safe search url building helper`() {
        val url = ProtectionEngine.buildGoogleSafeSearchUrl("kotlin android")
        assertTrue("Must target google.com/search", url.startsWith("https://www.google.com/search?q="))
        assertTrue("Must enforce safe=active", url.contains("safe=active"))
        assertTrue("Must encode spaces", url.contains("kotlin+android") || url.contains("kotlin%20android"))
    }

    @Test
    fun `test search engine query extraction and homepage preservation`() {
        // Search queries from various engines
        assertEquals("cats", ProtectionEngine.extractSearchEngineQuery("https://www.bing.com/search?q=cats"))
        assertEquals("android", ProtectionEngine.extractSearchEngineQuery("https://duckduckgo.com/?q=android"))
        assertEquals("kotlin", ProtectionEngine.extractSearchEngineQuery("https://search.yahoo.com/search?p=kotlin"))
        assertEquals("weather", ProtectionEngine.extractSearchEngineQuery("https://www.google.com/search?q=weather"))

        // Homepages should return null to preserve direct homepage navigation
        assertNull(ProtectionEngine.extractSearchEngineQuery("https://www.bing.com/"))
        assertNull(ProtectionEngine.extractSearchEngineQuery("https://www.google.com/"))
        assertNull(ProtectionEngine.extractSearchEngineQuery("https://duckduckgo.com/"))
        assertNull(ProtectionEngine.extractSearchEngineQuery("https://en.wikipedia.org/wiki/Main_Page"))
    }

    @Test
    fun `test google safe search verification helper`() {
        assertTrue(ProtectionEngine.isGoogleSafeSearchUrl("https://www.google.com/search?q=cars&safe=active"))
        assertFalse("Missing safe=active must return false", ProtectionEngine.isGoogleSafeSearchUrl("https://www.google.com/search?q=cars"))
        assertFalse("safe=off must return false", ProtectionEngine.isGoogleSafeSearchUrl("https://www.google.com/search?q=cars&safe=off"))
        assertFalse("Bing search must return false", ProtectionEngine.isGoogleSafeSearchUrl("https://www.bing.com/search?q=cars&safe=active"))
    }

    @Test
    fun `test query-first custom keyword blocking prevents search submission`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.browser.BrowserViewModel(app)
        viewModel.addCustomKeyword("secretblockedword")

        // 1. Raw search query with blocked keyword
        val allowedRaw = viewModel.submitQueryOrUrl("how to find secretblockedword today")
        assertFalse("Search query with custom keyword must be blocked before submission", allowedRaw)
        assertTrue(viewModel.uiState.value.blockedInfo != null)
        assertEquals("Custom Keyword Protection", viewModel.uiState.value.blockedInfo?.reason)

        // 2. Search engine URL with blocked keyword
        val allowedEngine = viewModel.submitQueryOrUrl("https://www.bing.com/search?q=secretblockedword")
        assertFalse("Bing search URL with custom keyword must be blocked", allowedEngine)
        assertTrue(viewModel.uiState.value.blockedInfo != null)
    }

    @Test
    fun `test normal search query normalizes to google safe search in viewmodel`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.browser.BrowserViewModel(app)

        // Submit regular query
        val allowed = viewModel.submitQueryOrUrl("learn jetpack compose")
        assertTrue("Valid query must be allowed", allowed)
        val target = viewModel.uiState.value.currentUrl
        assertTrue("Must be directed to Google", target.startsWith("https://www.google.com/search?q="))
        assertTrue("Must include safe=active", target.contains("safe=active"))
    }

    @Test
    fun `test video download blocked permanently`() {
        val videoUrl = "https://example.com/media/clip.mp4"
        val status = ProtectionEngine.checkDownloadType(videoUrl, "video/mp4", null)
        assertEquals(ProtectionEngine.DownloadStatus.BLOCKED_VIDEO, status)

        val mkvStatus = ProtectionEngine.checkDownloadType("https://example.com/movie.mkv", null, null)
        assertEquals(ProtectionEngine.DownloadStatus.BLOCKED_VIDEO, mkvStatus)

        val webmStatus = ProtectionEngine.checkDownloadType("https://example.com/stream.webm", "video/webm", null)
        assertEquals(ProtectionEngine.DownloadStatus.BLOCKED_VIDEO, webmStatus)
    }

    @Test
    fun `test audio download blocked permanently`() {
        val mp3Url = "https://example.com/music/song.mp3"
        val status = ProtectionEngine.checkDownloadType(mp3Url, "audio/mpeg", null)
        assertEquals(ProtectionEngine.DownloadStatus.BLOCKED_AUDIO, status)

        val wavStatus = ProtectionEngine.checkDownloadType("https://example.com/track.wav", null, null)
        assertEquals(ProtectionEngine.DownloadStatus.BLOCKED_AUDIO, wavStatus)

        val flacStatus = ProtectionEngine.checkDownloadType("https://example.com/audio.flac", "audio/flac", null)
        assertEquals(ProtectionEngine.DownloadStatus.BLOCKED_AUDIO, flacStatus)
    }

    @Test
    fun `test apk download blocked permanently`() {
        val apkUrl = "https://example.com/apps/installer.apk"
        val status = ProtectionEngine.checkDownloadType(apkUrl, "application/vnd.android.package-archive", null)
        assertEquals(ProtectionEngine.DownloadStatus.BLOCKED_APK, status)
    }

    // ==========================================
    // 3. ALLOWED DOWNLOADS TESTS
    // ==========================================

    @Test
    fun `test image downloads allowed`() {
        val jpgStatus = ProtectionEngine.checkDownloadType("https://example.com/photo.jpg", "image/jpeg", null)
        assertEquals(ProtectionEngine.DownloadStatus.ALLOWED_IMAGE, jpgStatus)

        val pngStatus = ProtectionEngine.checkDownloadType("https://example.com/diagram.png", "image/png", null)
        assertEquals(ProtectionEngine.DownloadStatus.ALLOWED_IMAGE, pngStatus)

        val webpStatus = ProtectionEngine.checkDownloadType("https://example.com/banner.webp", "image/webp", null)
        assertEquals(ProtectionEngine.DownloadStatus.ALLOWED_IMAGE, webpStatus)
    }

    @Test
    fun `test pdf downloads allowed`() {
        val pdfStatus = ProtectionEngine.checkDownloadType("https://example.com/document.pdf", "application/pdf", null)
        assertEquals(ProtectionEngine.DownloadStatus.ALLOWED_PDF, pdfStatus)
    }

    // ==========================================
    // 4. POP-UP BLOCKING SETTING TESTS
    // ==========================================

    @Test
    fun `test popup blocking toggle behavior`() {
        // Default should be ON (true)
        assertTrue("Pop-up blocking default should be true", repository.isPopupBlockingEnabled)

        // Turn OFF
        repository.isPopupBlockingEnabled = false
        assertFalse("Pop-up blocking should now be false", repository.isPopupBlockingEnabled)

        // Turn back ON
        repository.isPopupBlockingEnabled = true
        assertTrue("Pop-up blocking should be true", repository.isPopupBlockingEnabled)
    }

    // ==========================================
    // 5. AD BLOCKING TESTS
    // ==========================================

    @Test
    fun `test ad blocking matches known ad domains`() {
        assertTrue(ProtectionEngine.isAdRequest("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"))
        assertTrue(ProtectionEngine.isAdRequest("https://ad.doubleclick.net/ddm/trackclk/"))
        assertTrue(ProtectionEngine.isAdRequest("https://s.amazon-adsystem.com/iu3"))
        assertTrue(ProtectionEngine.isAdRequest("https://criteo.com/delivery/ajs.php"))

        // Legitimate non-ad domains must not be blocked
        assertFalse(ProtectionEngine.isAdRequest("https://en.wikipedia.org/wiki/Main_Page"))
        assertFalse(ProtectionEngine.isAdRequest("https://github.com/torvalds/linux"))
    }

    @Test
    fun `test ad blocking toggle state`() {
        // Default should be ON
        assertTrue("Ad blocking default should be true", repository.isAdBlockingEnabled)

        repository.isAdBlockingEnabled = false
        assertFalse("Ad blocking should now be false", repository.isAdBlockingEnabled)
    }

    // ==========================================
    // 6. THREE-LINE MENU: DESKTOP MODE & DATA CLEARING TESTS
    // ==========================================

    @Test
    fun `test desktop mode default and persistence`() {
        // Default must be OFF (mobile behavior)
        assertFalse("Desktop mode should default to false", repository.isDesktopModeEnabled)

        // Enable desktop mode
        repository.isDesktopModeEnabled = true
        assertTrue("Desktop mode should be true after enabling", repository.isDesktopModeEnabled)

        // Verify survival across app restart / new repository instance
        val reloadedRepo = SettingsRepository(context)
        assertTrue("Desktop mode must persist across restart", reloadedRepo.isDesktopModeEnabled)

        // Disable desktop mode
        reloadedRepo.isDesktopModeEnabled = false
        assertFalse("Desktop mode should be false after disabling", reloadedRepo.isDesktopModeEnabled)
        assertFalse("Persisted state must reflect false", SettingsRepository(context).isDesktopModeEnabled)
    }

    @Test
    fun `test browsing data clear does not alter settings or custom keywords`() {
        // Setup initial custom keyword and settings
        repository.addCustomKeyword("samplekeyword")
        repository.isDesktopModeEnabled = true
        repository.isPopupBlockingEnabled = true
        repository.isAdBlockingEnabled = true

        // Verify they are set
        assertTrue(repository.getCustomKeywords().contains("samplekeyword"))
        assertTrue(repository.isDesktopModeEnabled)

        // Verify that clearing browsing history/cache does not touch keywords or preferences
        val reloadedRepo = SettingsRepository(context)
        assertTrue("Keywords remain intact", reloadedRepo.getCustomKeywords().contains("samplekeyword"))
        assertTrue("Desktop mode remains intact", reloadedRepo.isDesktopModeEnabled)
        assertTrue("Popup blocking remains intact", reloadedRepo.isPopupBlockingEnabled)
        assertTrue("Ad blocking remains intact", reloadedRepo.isAdBlockingEnabled)
    }

    // ==========================================
    // 7. MULTI-TAB & NEW TAB NAVIGATION TESTS
    // ==========================================

    @Test
    fun `test new tab creation and switching in BrowserViewModel`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.browser.BrowserViewModel(app)
        assertEquals("Initial state should have 1 tab", 1, viewModel.uiState.value.tabs.size)
        val firstTabId = viewModel.uiState.value.currentTabId

        // Open new tab
        viewModel.openNewTab()
        assertEquals("Should now have 2 tabs", 2, viewModel.uiState.value.tabs.size)
        val secondTabId = viewModel.uiState.value.currentTabId
        assertTrue("New tab must have different ID", firstTabId != secondTabId)
        assertTrue("New tab should start on home page", viewModel.uiState.value.isHomePage)

        // Switch back to first tab
        viewModel.selectTab(firstTabId)
        assertEquals("Current tab ID should be first tab", firstTabId, viewModel.uiState.value.currentTabId)

        // Close second tab
        viewModel.closeTab(secondTabId)
        assertEquals("Should have 1 tab remaining", 1, viewModel.uiState.value.tabs.size)
    }

    @Test
    fun `test protection applies equally across multiple tabs`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.browser.BrowserViewModel(app)
        viewModel.openNewTab()

        // Submit adult url on the new tab
        val allowed = viewModel.submitQueryOrUrl("https://www.pornhub.com")
        assertFalse("Adult content must be blocked on new tabs", allowed)
        assertTrue("Blocked info must be set", viewModel.uiState.value.blockedInfo != null)
    }

    @Test
    fun `test fast bangla translation on active webpage and homepage`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.browser.BrowserViewModel(app)

        // 1. On homepage without search
        val homeTranslateUrl = viewModel.translateToBangla()
        assertTrue("Should open Google translate", homeTranslateUrl.contains("translate.google.com"))
        assertTrue("Should target Bengali tl=bn", homeTranslateUrl.contains("tl=bn"))
        assertFalse("Menu should be closed after translation", viewModel.uiState.value.isMenuOpen)

        // 2. On active web page
        viewModel.submitQueryOrUrl("https://en.wikipedia.org/wiki/Bangladesh")
        val pageTranslateUrl = viewModel.translateToBangla()
        assertTrue("Should translate page url", pageTranslateUrl.startsWith("https://translate.google.com/translate?"))
        assertTrue("Should contain encoded url", pageTranslateUrl.contains("wikipedia.org"))
        assertTrue("Should enforce tl=bn", pageTranslateUrl.contains("tl=bn"))
    }

    @Test
    fun `test plus button tap creates new window on home page while preserving old window`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.browser.BrowserViewModel(app)

        // 1. Browse on first tab
        val tab1Id = viewModel.uiState.value.currentTabId
        viewModel.submitQueryOrUrl("https://www.google.com")
        viewModel.onPageStarted("https://www.google.com")
        viewModel.onPageFinished("https://www.google.com", "Google", false, false)

        assertEquals("First tab has url", "https://www.google.com", viewModel.uiState.value.currentUrl)
        assertFalse("First tab is not on home page", viewModel.uiState.value.isHomePage)

        // Save a mock bundle state on tab 1
        val mockBundle = android.os.Bundle().apply { putString("test_key", "tab1_state") }
        viewModel.saveCurrentTabState(mockBundle)

        // 2. Tap Plus button (openNewTab)
        viewModel.openNewTab()

        assertEquals("Should have 2 tabs now", 2, viewModel.uiState.value.tabs.size)
        val tab2Id = viewModel.uiState.value.currentTabId
        assertTrue("Current tab ID should be new tab", tab2Id != tab1Id)
        assertTrue("New window must start on Home Page", viewModel.uiState.value.isHomePage)
        assertEquals("New window url should be empty", "", viewModel.uiState.value.currentUrl)

        // Verify Old Window is intact in the tabs list!
        val oldTab = viewModel.uiState.value.tabs.find { it.id == tab1Id }
        assertNotNull("Old window must exist in tabs list", oldTab)
        assertEquals("Old window url must be intact", "https://www.google.com", oldTab?.url)
        assertEquals("Old window title must be intact", "Google", oldTab?.pageTitle)
        assertFalse("Old window is not home page", oldTab?.isHomePage == true)
        assertEquals("Old window bundle state preserved", "tab1_state", oldTab?.bundle?.getString("test_key"))
    }

    @Test
    fun `test long press plus button opens dialog without creating new window and allows restoration`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.browser.BrowserViewModel(app)

        val tab1Id = viewModel.uiState.value.currentTabId
        viewModel.submitQueryOrUrl("https://en.wikipedia.org")
        viewModel.onPageStarted("https://en.wikipedia.org")
        viewModel.onPageFinished("https://en.wikipedia.org", "Wikipedia", false, false)

        viewModel.openNewTab()
        val tab2Id = viewModel.uiState.value.currentTabId
        assertEquals("Total 2 tabs", 2, viewModel.uiState.value.tabs.size)

        // 1. Long-press action: Open Tabs Dialog
        viewModel.openTabsDialog()
        assertTrue("Tabs dialog must be open", viewModel.uiState.value.isTabsDialogOpen)
        assertEquals("Long press MUST NOT create a new tab", 2, viewModel.uiState.value.tabs.size)
        assertEquals("Active tab should remain tab2", tab2Id, viewModel.uiState.value.currentTabId)

        // 2. Select Tab 1 from the list
        viewModel.selectTab(tab1Id)
        assertFalse("Selecting tab should close dialog", viewModel.uiState.value.isTabsDialogOpen)
        assertEquals("Selected tab 1 should be restored as current", tab1Id, viewModel.uiState.value.currentTabId)
        assertEquals("Restored tab should have its url", "https://en.wikipedia.org", viewModel.uiState.value.currentUrl)
        assertEquals("Restored tab should have its title", "Wikipedia", viewModel.uiState.value.pageTitle)
        assertFalse("Restored tab is not home page", viewModel.uiState.value.isHomePage)
        assertEquals("Tabs count remains exactly 2", 2, viewModel.uiState.value.tabs.size)
    }
}
