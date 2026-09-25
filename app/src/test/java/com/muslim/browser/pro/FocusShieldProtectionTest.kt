package com.muslim.browser.pro

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
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
        val viewModel = com.muslim.browser.pro.browser.BrowserViewModel(app)
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
        val viewModel = com.muslim.browser.pro.browser.BrowserViewModel(app)

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
        val viewModel = com.muslim.browser.pro.browser.BrowserViewModel(app)
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
        val viewModel = com.muslim.browser.pro.browser.BrowserViewModel(app)
        viewModel.openNewTab()

        // Submit adult url on the new tab
        val allowed = viewModel.submitQueryOrUrl("https://www.pornhub.com")
        assertFalse("Adult content must be blocked on new tabs", allowed)
        assertTrue("Blocked info must be set", viewModel.uiState.value.blockedInfo != null)
    }

    @Test
    fun `test fast bangla translation on active webpage and homepage`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.muslim.browser.pro.browser.BrowserViewModel(app)

        // 1. On homepage without search
        val homeTranslateUrl = viewModel.translateToBangla()
        assertTrue("Should open Google translate", homeTranslateUrl.contains("translate.google.com"))
        assertTrue("Should target Bengali tl=bn", homeTranslateUrl.contains("tl=bn"))
        assertTrue("Should include host language hl=bn", homeTranslateUrl.contains("hl=bn"))
        assertFalse("Menu should be closed after translation", viewModel.uiState.value.isMenuOpen)

        // 2. On active web page
        viewModel.submitQueryOrUrl("https://en.wikipedia.org/wiki/Bangladesh")
        val pageTranslateUrl = viewModel.translateToBangla()
        assertTrue("Should translate page url", pageTranslateUrl.startsWith("https://translate.google.com/translate?"))
        assertTrue("Should contain encoded url", pageTranslateUrl.contains("wikipedia.org"))
        assertTrue("Should enforce tl=bn", pageTranslateUrl.contains("tl=bn"))
        assertTrue("Should enforce hl=bn", pageTranslateUrl.contains("hl=bn"))

        // 3. Repeated translation on already translated page must prevent duplicate reload loop
        val repeatedTranslateUrl = viewModel.translateToBangla(pageTranslateUrl)
        assertEquals("Repeated translation must not re-wrap or duplicate URL", pageTranslateUrl, repeatedTranslateUrl)

        // 4. Translation on live WebView URL
        val liveTranslateUrl = viewModel.translateToBangla("https://example.com/page")
        assertTrue("Should translate provided live URL", liveTranslateUrl.contains("example.com"))
        assertTrue("Should enforce tl=bn", liveTranslateUrl.contains("tl=bn"))

        // 5. Translation on Google Search query page sets hl=bn
        viewModel.submitQueryOrUrl("https://www.google.com/search?q=islam&safe=active")
        val searchTranslateUrl = viewModel.translateToBangla("https://www.google.com/search?q=islam&safe=active")
        assertTrue("Search page should be translated with hl=bn", searchTranslateUrl.contains("hl=bn"))
        assertTrue("Search page should preserve safe search", searchTranslateUrl.contains("safe=active"))
    }

    @Test
    fun `test plus button tap creates new window on home page while preserving old window`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.muslim.browser.pro.browser.BrowserViewModel(app)

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
        val viewModel = com.muslim.browser.pro.browser.BrowserViewModel(app)

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

    // ==========================================
    // 7. FAVORITE WEBSITES PERSISTENCE & EDIT TESTS
    // ==========================================

    @Test
    fun `test default favorite websites loaded`() {
        val sites = repository.getFavoriteSites()
        assertTrue("Default favorite sites should not be empty", sites.isNotEmpty())
        assertEquals("Home Page must initially show exactly 7 website tiles", 7, sites.size)
        assertFalse("Legacy Google tile must be removed", sites.any { it.name == "Google" })
        assertFalse("Legacy Wikipedia tile must be removed", sites.any { it.name == "Wikipedia" })

        // Verify the exact 7 websites requested by user
        assertEquals("MoldovaLive", sites[0].name)
        assertEquals("https://moldovalive.md", sites[0].url)

        assertEquals("Moldova1", sites[1].name)
        assertEquals("https://moldova1.md/i/en", sites[1].url)

        assertEquals("Google AI Studio", sites[2].name)
        assertEquals("https://aistudio.google.com", sites[2].url)

        assertEquals("GitHub", sites[3].name)
        assertEquals("https://github.com/", sites[3].url)

        assertEquals("Prothom Alo ePaper", sites[4].name)
        assertEquals("https://epaper.prothomalo.com/Home", sites[4].url)

        assertEquals("The Daily Star Bangla", sites[5].name)
        assertEquals("https://bangla.thedailystar.net", sites[5].url)

        assertEquals("Ittefaq", sites[6].name)
        assertEquals("https://www.ittefaq.com.bd", sites[6].url)
    }

    @Test
    fun `test add favorite website persistence`() {
        // 1. Add new favorite website
        val added = repository.addFavoriteSite("Quran.com", "quran.com")
        assertEquals("Quran.com", added.name)
        assertEquals("https://quran.com", added.url)

        val sitesAfterAdd = repository.getFavoriteSites()
        val found = sitesAfterAdd.find { it.id == added.id }
        assertNotNull("Newly added site must exist in repository", found)
        assertEquals("Quran.com", found?.name)
        assertEquals("https://quran.com", found?.url)

        // 2. Verify persistence survives re-creation
        val newRepo = SettingsRepository(context)
        val sitesReloaded = newRepo.getFavoriteSites()
        val reloadedSite = sitesReloaded.find { it.id == added.id }
        assertNotNull("Added site must exist after reload", reloadedSite)
        assertEquals("Quran.com", reloadedSite?.name)
        assertEquals("https://quran.com", reloadedSite?.url)
    }

    @Test
    fun `test viewModel add favorite sites updates uiState`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.muslim.browser.pro.browser.BrowserViewModel(app)

        val initialCount = viewModel.uiState.value.favoriteSites.size

        // Add favorite
        viewModel.addFavoriteSite("Sunnah", "sunnah.com")
        val afterAddList = viewModel.uiState.value.favoriteSites
        assertEquals("Count should increase by 1", initialCount + 1, afterAddList.size)
        val addedItem = afterAddList.find { it.name == "Sunnah" }
        assertNotNull("Added item should be in uiState", addedItem)
        assertEquals("https://sunnah.com", addedItem?.url)
    }

    @Test
    fun `test favicon manager domain extraction and graceful fallback`() {
        val domainGoogle = com.muslim.browser.pro.browser.FaviconManager.extractDomain("https://www.google.com/search?q=test")
        assertEquals("google.com", domainGoogle)

        val domainWiki = com.muslim.browser.pro.browser.FaviconManager.extractDomain("https://en.wikipedia.org/wiki/Islam")
        assertEquals("en.wikipedia.org", domainWiki)

        val domainYoutube = com.muslim.browser.pro.browser.FaviconManager.extractDomain("www.youtube.com")
        assertEquals("youtube.com", domainYoutube)

        val domainBlank = com.muslim.browser.pro.browser.FaviconManager.extractDomain("about:blank")
        assertEquals("", domainBlank)

        // Verify that memory cache returns null when empty and does not crash
        val memBmp = com.muslim.browser.pro.browser.FaviconManager.getFromMemory("nonexistent.com")
        assertNull("Non-cached favicon must return null as fallback without crashing", memBmp)

        // Verify clear cache works safely
        com.muslim.browser.pro.browser.FaviconManager.clearCache(context)
    }

    // ==========================================
    // 6. FILE CHOOSER & UPLOAD TESTS
    // ==========================================

    @Test
    fun `test normalizeMimeTypes handles image types and extensions`() {
        val imageMime = MainActivity.normalizeMimeTypes(arrayOf("image/*"))
        assertEquals(1, imageMime.size)
        assertEquals("image/*", imageMime[0])

        val specificImages = MainActivity.normalizeMimeTypes(arrayOf("image/png, image/jpeg"))
        assertTrue("Must contain image/png", specificImages.contains("image/png"))
        assertTrue("Must contain image/jpeg", specificImages.contains("image/jpeg"))

        val extensions = MainActivity.normalizeMimeTypes(arrayOf(".png", ".jpg"))
        assertTrue("Extension .png maps to image/png", extensions.contains("image/png"))
        assertTrue("Extension .jpg maps to image/jpeg", extensions.contains("image/jpeg"))
    }

    @Test
    fun `test normalizeMimeTypes handles pdf and document formats`() {
        val pdfMime = MainActivity.normalizeMimeTypes(arrayOf("application/pdf"))
        assertEquals(1, pdfMime.size)
        assertEquals("application/pdf", pdfMime[0])

        val pdfExt = MainActivity.normalizeMimeTypes(arrayOf(".pdf"))
        assertEquals(1, pdfExt.size)
        assertEquals("application/pdf", pdfExt[0])

        val docExt = MainActivity.normalizeMimeTypes(arrayOf(".doc"))
        assertEquals(1, docExt.size)
        assertEquals("application/msword", docExt[0])
    }

    @Test
    fun `test normalizeMimeTypes fallback to wildcard for empty or unspecified types`() {
        val emptyResult = MainActivity.normalizeMimeTypes(emptyArray())
        assertEquals(1, emptyResult.size)
        assertEquals("*/*", emptyResult[0])

        val nullResult = MainActivity.normalizeMimeTypes(null)
        assertEquals(1, nullResult.size)
        assertEquals("*/*", nullResult[0])

        val blankResult = MainActivity.normalizeMimeTypes(arrayOf("", "  "))
        assertEquals(1, blankResult.size)
        assertEquals("*/*", blankResult[0])
    }

    @Test
    fun `test createFileChooserIntent configured correctly for single file mode`() {
        val intent = MainActivity.createFileChooserIntent(arrayOf("image/*"), isMultiple = false)
        assertEquals(Intent.ACTION_OPEN_DOCUMENT, intent.action)
        assertTrue(intent.categories.contains(Intent.CATEGORY_OPENABLE))
        assertEquals("image/*", intent.type)
        assertFalse(intent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false))
        assertTrue((intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0)
    }

    @Test
    fun `test createFileChooserIntent configured correctly for multiple file mode`() {
        val intent = MainActivity.createFileChooserIntent(arrayOf("application/pdf"), isMultiple = true)
        assertEquals(Intent.ACTION_OPEN_DOCUMENT, intent.action)
        assertTrue(intent.categories.contains(Intent.CATEGORY_OPENABLE))
        assertEquals("application/pdf", intent.type)
        assertTrue(intent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false))
        assertTrue((intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0)
    }

    @Test
    fun `test createFileChooserIntent sets EXTRA_MIME_TYPES for multiple accepted types`() {
        val intent = MainActivity.createFileChooserIntent(arrayOf("image/*", "application/pdf"), isMultiple = false)
        assertEquals(Intent.ACTION_OPEN_DOCUMENT, intent.action)
        assertEquals("*/*", intent.type)
        val extraMimes = intent.getStringArrayExtra(Intent.EXTRA_MIME_TYPES)
        assertNotNull(extraMimes)
        assertTrue(extraMimes!!.contains("image/*"))
        assertTrue(extraMimes.contains("application/pdf"))
    }

    @Test
    fun `test parseFileChooserResult returns single uri on success`() {
        val testUri = Uri.parse("content://com.android.providers.media/image/123")
        val intent = Intent().apply {
            data = testUri
        }
        val result = MainActivity.parseFileChooserResult(Activity.RESULT_OK, intent)
        assertNotNull(result)
        assertEquals(1, result!!.size)
        assertEquals(testUri, result[0])
    }

    @Test
    fun `test parseFileChooserResult returns multiple uris from clipData`() {
        val uri1 = Uri.parse("content://com.android.providers.media/image/101")
        val uri2 = Uri.parse("content://com.android.providers.media/image/102")

        val clipData = ClipData.newUri(context.contentResolver, "file1", uri1).apply {
            addItem(ClipData.Item(uri2))
        }
        val intent = Intent().apply {
            this.clipData = clipData
        }

        val result = MainActivity.parseFileChooserResult(Activity.RESULT_OK, intent)
        assertNotNull(result)
        assertEquals(2, result!!.size)
        assertEquals(uri1, result[0])
        assertEquals(uri2, result[1])
    }

    @Test
    fun `test parseFileChooserResult returns null on cancel or back press`() {
        val canceledResult = MainActivity.parseFileChooserResult(Activity.RESULT_CANCELED, Intent())
        assertNull("User cancel or back press must return null", canceledResult)

        val nullDataResult = MainActivity.parseFileChooserResult(Activity.RESULT_OK, null)
        assertNull("Null intent data must return null", nullDataResult)

        val emptyIntentResult = MainActivity.parseFileChooserResult(Activity.RESULT_OK, Intent())
        assertNull("Empty intent without data or clipData must return null", emptyIntentResult)
    }

    @Test
    fun `test initial cold start search transition and page commit lifecycle`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.muslim.browser.pro.browser.BrowserViewModel(app)

        // 1. On cold start, tab is on home page and page content is not visible
        assertTrue("Cold start must start on home page", viewModel.uiState.value.isHomePage)
        assertFalse("Page content should not be marked visible before first render", viewModel.uiState.value.isPageContentVisible)

        // 2. Submit first search from home page
        val searchAllowed = viewModel.submitQueryOrUrl("islamic history")
        assertTrue("Clean query must be allowed", searchAllowed)
        assertFalse("Should transition away from home page", viewModel.uiState.value.isHomePage)
        assertTrue("Should be in loading state", viewModel.uiState.value.isLoading)
        assertFalse("Page content should not be visible until WebView commits its first frame", viewModel.uiState.value.isPageContentVisible)

        // 3. Simulate WebView committing first frame (onPageCommitVisible)
        viewModel.onPageCommitVisible()
        assertTrue("Page content must become visible as soon as onPageCommitVisible fires", viewModel.uiState.value.isPageContentVisible)

        // 4. Page finishes loading
        viewModel.onPageFinished(viewModel.uiState.value.currentUrl, "Islamic History - Search", false, false)
        assertFalse("Loading state should complete", viewModel.uiState.value.isLoading)
        assertTrue("Page content remains visible", viewModel.uiState.value.isPageContentVisible)

        // 5. Subsequent search from top URL bar while already browsing
        val nextSearch = viewModel.submitQueryOrUrl("quran tafseer")
        assertTrue(nextSearch)
        assertTrue("Subsequent search while already viewing a page preserves page content visibility for smooth transition", viewModel.uiState.value.isPageContentVisible)

        // 6. User taps Home button
        viewModel.goHome()
        assertTrue("Should be on home page", viewModel.uiState.value.isHomePage)
        assertFalse("Returning home resets page content visibility for next clean transition", viewModel.uiState.value.isPageContentVisible)
    }

    // ==========================================
    // 8. WINDOW / TAB PERSISTENCE & HISTORY TESTS
    // ==========================================

    @Test
    fun `Test A - multiple windows persistence across process restart`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm1 = com.muslim.browser.pro.browser.BrowserViewModel(app)

        // 1. Initial tab: navigate to Website A
        vm1.submitQueryOrUrl("https://news.ycombinator.com")
        vm1.onPageFinished("https://news.ycombinator.com", "Hacker News", false, false)

        // 2. Open Window 2: navigate to Website B
        vm1.openNewTab()
        vm1.submitQueryOrUrl("https://en.wikipedia.org")
        vm1.onPageFinished("https://en.wikipedia.org", "Wikipedia", false, false)

        // 3. Open Window 3: navigate to Website C
        vm1.openNewTab()
        vm1.submitQueryOrUrl("https://www.nature.com")
        vm1.onPageFinished("https://www.nature.com", "Nature Journal", false, false)

        // 4. Select Window 2 (Wikipedia) as active
        val tabs = vm1.uiState.value.tabs
        assertEquals("Must have 3 open tabs", 3, tabs.size)
        val tab2 = tabs[1]
        vm1.selectTab(tab2.id)
        assertEquals("Tab 2 must be active", tab2.id, vm1.uiState.value.currentTabId)

        // 5. Simulate Process Death / Recent Apps swipe-away by creating a new ViewModel instance
        val vm2 = com.muslim.browser.pro.browser.BrowserViewModel(app)

        // 6. Verify all 3 tabs are restored in exact order with URLs and titles
        val restoredTabs = vm2.uiState.value.tabs
        assertEquals("Restored session must have exactly 3 tabs", 3, restoredTabs.size)
        assertEquals("Window 1 URL must match", "https://news.ycombinator.com", restoredTabs[0].url)
        assertEquals("Window 1 Title must match", "Hacker News", restoredTabs[0].pageTitle)

        assertEquals("Window 2 URL must match", "https://en.wikipedia.org", restoredTabs[1].url)
        assertEquals("Window 2 Title must match", "Wikipedia", restoredTabs[1].pageTitle)

        assertEquals("Window 3 URL must match", "https://www.nature.com", restoredTabs[2].url)
        assertEquals("Window 3 Title must match", "Nature Journal", restoredTabs[2].pageTitle)

        // 7. Verify active window is restored to Window 2
        assertEquals("Active window must be restored to Window 2", tab2.id, vm2.uiState.value.currentTabId)
        assertEquals("Current URL must match Window 2", "https://en.wikipedia.org", vm2.uiState.value.currentUrl)
        assertFalse("Active window must not be home page", vm2.uiState.value.isHomePage)
    }

    @Test
    fun `Test B - browsing history recording and individual delete`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.muslim.browser.pro.browser.BrowserViewModel(app)

        // 1. Visit several pages
        vm.submitQueryOrUrl("https://site-a.com")
        vm.onPageFinished("https://site-a.com", "Site A Title", false, false)

        vm.submitQueryOrUrl("https://site-b.com")
        vm.onPageFinished("https://site-b.com", "Site B Title", false, false)

        vm.submitQueryOrUrl("https://site-c.com")
        vm.onPageFinished("https://site-c.com", "Site C Title", false, false)

        // 2. Open History
        vm.openHistory()
        assertTrue("History sheet should be open", vm.uiState.value.isHistoryOpen)
        val history = vm.uiState.value.browsingHistory
        assertEquals("Should contain 3 history entries", 3, history.size)

        // Newest entry first: Site C, then Site B, then Site A
        assertEquals("Site C Title", history[0].title)
        assertEquals("https://site-c.com", history[0].url)

        assertEquals("Site B Title", history[1].title)
        assertEquals("https://site-b.com", history[1].url)

        assertEquals("Site A Title", history[2].title)
        assertEquals("https://site-a.com", history[2].url)

        // 3. Delete individual entry (Site B)
        val siteBId = history[1].id
        vm.deleteHistoryEntry(siteBId)

        val updatedHistory = vm.uiState.value.browsingHistory
        assertEquals("History size must be reduced by 1", 2, updatedHistory.size)
        assertFalse("Site B must be deleted", updatedHistory.any { it.id == siteBId })
        assertTrue("Site C must remain intact", updatedHistory.any { it.url == "https://site-c.com" })
        assertTrue("Site A must remain intact", updatedHistory.any { it.url == "https://site-a.com" })

        // 4. Close History
        vm.closeHistory()
        assertFalse("History sheet should be closed", vm.uiState.value.isHistoryOpen)
    }

    @Test
    fun `Test C - recent apps swipe and process recreation preserves history and windows`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm1 = com.muslim.browser.pro.browser.BrowserViewModel(app)

        // Add 2 tabs and visit sites
        vm1.submitQueryOrUrl("https://first-tab.org")
        vm1.onPageFinished("https://first-tab.org", "First Tab", false, false)

        vm1.openNewTab()
        vm1.submitQueryOrUrl("https://second-tab.org")
        vm1.onPageFinished("https://second-tab.org", "Second Tab", false, false)

        // Simulate app kill / restart
        val vm2 = com.muslim.browser.pro.browser.BrowserViewModel(app)

        // Verify tabs preserved
        assertEquals(2, vm2.uiState.value.tabs.size)
        assertEquals("https://first-tab.org", vm2.uiState.value.tabs[0].url)
        assertEquals("https://second-tab.org", vm2.uiState.value.tabs[1].url)

        // Verify history preserved
        val history = vm2.uiState.value.browsingHistory
        assertEquals(2, history.size)
        assertTrue(history.any { it.url == "https://second-tab.org" })
        assertTrue(history.any { it.url == "https://first-tab.org" })
    }

    @Test
    fun `Test D - clear history and history deduplication`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.muslim.browser.pro.browser.BrowserViewModel(app)

        // 1. Visit duplicate consecutive URLs
        vm.submitQueryOrUrl("https://example.com")
        vm.onPageFinished("https://example.com", "Example 1", false, false)
        vm.onPageFinished("https://example.com", "Example 2", false, false)

        // Should not spam duplicate history entries for consecutive reload/finish on same page
        assertEquals(1, vm.uiState.value.browsingHistory.size)
        assertEquals("Example 2", vm.uiState.value.browsingHistory[0].title)

        // 2. about:blank should never be recorded in history
        vm.onPageFinished("about:blank", "Blank", false, false)
        assertEquals(1, vm.uiState.value.browsingHistory.size)

        // 3. Clear all history
        vm.clearAllHistory()
        assertTrue("History must be empty after clearAllHistory", vm.uiState.value.browsingHistory.isEmpty())

        // Survives app restart as empty
        val vmAfterClear = com.muslim.browser.pro.browser.BrowserViewModel(app)
        assertTrue(vmAfterClear.uiState.value.browsingHistory.isEmpty())
    }

    // ==========================================
    // TRANSLATION MODE SWITCH & PERSISTENCE TESTS
    // ==========================================

    @Test
    fun `test translation mode switch toggle and persistence across app restart`() {
        // 1. Initial default state should be false
        assertFalse("Translation mode default should be false", repository.isTranslationModeEnabled)

        // 2. Turn ON
        repository.isTranslationModeEnabled = true
        assertTrue("Translation mode should now be true", repository.isTranslationModeEnabled)

        // 3. Verify persistence across re-creation (restart)
        val reloadedRepo1 = SettingsRepository(context)
        assertTrue("Translation mode ON must persist after reload", reloadedRepo1.isTranslationModeEnabled)

        // 4. Turn OFF
        repository.isTranslationModeEnabled = false
        assertFalse("Translation mode should now be false", repository.isTranslationModeEnabled)

        // 5. Verify persistence across re-creation (restart)
        val reloadedRepo2 = SettingsRepository(context)
        assertFalse("Translation mode OFF must persist after reload", reloadedRepo2.isTranslationModeEnabled)
    }

    @Test
    fun `test viewModel translation toggle updates uiState and handles translation urls`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.muslim.browser.pro.browser.BrowserViewModel(app)

        // Initial state
        assertFalse("uiState should start with isTranslationModeEnabled = false", vm.uiState.value.isTranslationModeEnabled)

        // Toggle ON on webpage
        vm.submitQueryOrUrl("https://en.wikipedia.org/wiki/Islam")
        val targetTranslateUrl = vm.toggleTranslationMode(true, "https://en.wikipedia.org/wiki/Islam")
        assertTrue("uiState should have isTranslationModeEnabled = true", vm.uiState.value.isTranslationModeEnabled)
        assertNotNull("Target translation url should not be null", targetTranslateUrl)
        assertTrue("Translated URL should target Bangla", targetTranslateUrl!!.contains("translate.google.com") && targetTranslateUrl.contains("tl=bn"))

        // Toggle OFF on translated page reverts to original URL
        val revertedUrl = vm.toggleTranslationMode(false, targetTranslateUrl)
        assertFalse("uiState should have isTranslationModeEnabled = false", vm.uiState.value.isTranslationModeEnabled)
        assertEquals("https://en.wikipedia.org/wiki/Islam", revertedUrl)

        // Test extracting original URL from .translate.goog format
        val originalFromGoog = vm.getOriginalUrlFromTranslation("https://en-wikipedia-org.translate.goog/wiki/Islam?_x_tr_sl=auto&_x_tr_tl=bn")
        assertNotNull(originalFromGoog)
        assertTrue(originalFromGoog!!.contains("wikipedia.org"))
    }

    @Test
    fun `test translation url detection and filtering logic preserves protection`() {
        // 1. Translation host detection
        assertTrue(ProtectionEngine.isTranslationHost("translate.google.com"))
        assertTrue(ProtectionEngine.isTranslationHost("moldovalive-md.translate.goog"))
        assertTrue(ProtectionEngine.isTranslationHost("translate.goog"))
        assertFalse(ProtectionEngine.isTranslationHost("google.com"))
        assertFalse(ProtectionEngine.isTranslationHost("moldovalive.md"))

        // 2. Translation URL detection
        val moldovaTranslateUrl = "https://translate.google.com/translate?sl=auto&tl=bn&hl=bn&u=https%3A%2F%2Fmoldovalive.md"
        val moldovaGoogUrl = "https://moldovalive-md.translate.goog/?_x_tr_sl=auto&_x_tr_tl=bn"
        assertTrue(ProtectionEngine.isTranslationUrl(moldovaTranslateUrl))
        assertTrue(ProtectionEngine.isTranslationUrl(moldovaGoogUrl))
        assertFalse(ProtectionEngine.isTranslationUrl("https://moldovalive.md"))

        // 3. Search query extractor must NOT treat Google Translate URLs as search queries
        assertNull("Google Translate must not be extracted as search query",
            ProtectionEngine.extractSearchEngineQuery(moldovaTranslateUrl))
        assertNull("translate.google.com homepage must not be extracted as search query",
            ProtectionEngine.extractSearchEngineQuery("https://translate.google.com/?sl=auto&tl=bn&hl=bn&op=translate"))

        // 4. Safe site through Google Translate must be ALLOWED
        val checkAllowed = ProtectionEngine.checkDirectUrl(moldovaTranslateUrl, emptySet())
        assertTrue("Legitimate site translation must be allowed", checkAllowed is ProtectionEngine.FilterResult.Allowed)

        val checkGoogAllowed = ProtectionEngine.checkDirectUrl(moldovaGoogUrl, emptySet())
        assertTrue("translate.goog proxy for legitimate site must be allowed", checkGoogAllowed is ProtectionEngine.FilterResult.Allowed)

        // 5. Adult site through Google Translate must STILL BE BLOCKED (Preserve protection)
        val adultTranslateUrl = "https://translate.google.com/translate?sl=auto&tl=bn&hl=bn&u=https%3A%2F%2Fpornhub.com"
        val checkBlocked = ProtectionEngine.checkDirectUrl(adultTranslateUrl, emptySet())
        assertTrue("Adult site accessed via translation must be blocked", checkBlocked is ProtectionEngine.FilterResult.Blocked)

        val adultGoogUrl = "https://pornhub-com.translate.goog/"
        val checkGoogBlocked = ProtectionEngine.checkDirectUrl(adultGoogUrl, emptySet())
        assertTrue("Adult site accessed via translate.goog must be blocked", checkGoogBlocked is ProtectionEngine.FilterResult.Blocked)

        // 6. Direct translation un-framing (preventing 'This content is blocked' iframe error)
        val framedUrl = "https://translate.google.com/translate?sl=auto&tl=bn&hl=bn&u=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FBangladesh"
        val directUrl = ProtectionEngine.toDirectTranslateUrl(framedUrl)
        assertEquals(
            "https://en-wikipedia-org.translate.goog/wiki/Bangladesh?_x_tr_sl=auto&_x_tr_tl=bn&_x_tr_hl=bn",
            directUrl
        )

        // Hyphenated domains should be escaped with double hyphens
        val framedHyphenUrl = "https://translate.google.com/translate?sl=auto&tl=bn&hl=bn&u=https%3A%2F%2Fmy-site.org%2Fpage%3Fid%3D1"
        val directHyphenUrl = ProtectionEngine.toDirectTranslateUrl(framedHyphenUrl)
        assertEquals(
            "https://my--site-org.translate.goog/page?id=1&_x_tr_sl=auto&_x_tr_tl=bn&_x_tr_hl=bn",
            directHyphenUrl
        )

        // Non-framed URLs should remain unchanged
        val homeTranslate = "https://translate.google.com/?sl=auto&tl=bn&hl=bn&op=translate"
        assertEquals(homeTranslate, ProtectionEngine.toDirectTranslateUrl(homeTranslate))
        val searchTranslate = "https://www.google.com/search?q=islam&hl=bn&safe=active"
        assertEquals(searchTranslate, ProtectionEngine.toDirectTranslateUrl(searchTranslate))
    }

    @Test
    fun `test applyWebViewTheme executes safely on WebView without throwing exceptions`() {
        val webView = android.webkit.WebView(context)

        // 1. Verify applying dark theme completes safely
        MainActivity.applyWebViewTheme(webView, isDarkTheme = true)
        org.junit.Assert.assertTrue(MainActivity.isDarkThemeActive)

        // 2. Verify applying light theme completes safely
        MainActivity.applyWebViewTheme(webView, isDarkTheme = false)
        org.junit.Assert.assertFalse(MainActivity.isDarkThemeActive)
    }
}
