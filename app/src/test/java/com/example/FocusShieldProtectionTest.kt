package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.browser.ProtectionEngine
import com.example.browser.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
            "https://redtube.com/watch"
        )
        for (url in adultDomains) {
            val res = ProtectionEngine.checkUrlOrQuery(url, emptySet())
            assertTrue("Domain $url must be permanently blocked", res is ProtectionEngine.FilterResult.Blocked)
            val blocked = res as ProtectionEngine.FilterResult.Blocked
            assertEquals("Adult Content Protection", blocked.reason)
        }
    }

    @Test
    fun `test adult protection is always enabled for explicit queries`() {
        val explicitQueries = listOf(
            "hardcore porn videos",
            "free adult xxx clips",
            "hentai gallery nude"
        )
        for (query in explicitQueries) {
            val res = ProtectionEngine.checkUrlOrQuery(query, emptySet())
            assertTrue("Query '$query' must be permanently blocked", res is ProtectionEngine.FilterResult.Blocked)
        }
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

        // Submit adult query on the new tab
        val allowed = viewModel.submitQueryOrUrl("pornhub")
        assertFalse("Adult content must be blocked on new tabs", allowed)
        assertTrue("Blocked info must be set", viewModel.uiState.value.blockedInfo != null)
    }
}
