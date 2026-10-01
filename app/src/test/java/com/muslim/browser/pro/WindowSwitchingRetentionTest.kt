package com.muslim.browser.pro

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.TabWebViewManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WindowSwitchingRetentionTest {

    private lateinit var context: Application
    private val savedBundles = mutableMapOf<String, Bundle>()
    private val loadUrlCalls = mutableMapOf<String, Int>()
    private var totalCreations = 0

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        savedBundles.clear()
        loadUrlCalls.clear()
        totalCreations = 0
        MainActivity.DesktopModeDiagnostics.reset()
    }

    private fun createTestTabWebViewManager(maxLive: Int = TabWebViewManager.MAX_LIVE_WEBVIEWS): TabWebViewManager {
        return TabWebViewManager(
            context = context,
            maxLiveWebViews = maxLive,
            webViewFactory = { tabId ->
                totalCreations++
                object : WebView(context) {
                    override fun loadUrl(url: String) {
                        super.loadUrl(url)
                        loadUrlCalls[tabId] = (loadUrlCalls[tabId] ?: 0) + 1
                    }
                }
            },
            onSaveTabBundle = { tabId, bundle ->
                savedBundles[tabId] = bundle
            }
        )
    }

    // Test 1: Window A loads a page -> Window B -> Window A. Expected: Window A is not reloaded.
    @Test
    fun test1_windowSwitchingDoesNotReloadLiveWebpage() {
        val manager = createTestTabWebViewManager()

        // 1. Open Window A and load a webpage
        val (windowA1, restoredA1) = manager.getOrCreateWebView("tab_A", url = "https://example.com/pageA")
        assertFalse(restoredA1)
        assertEquals(1, loadUrlCalls["tab_A"])
        assertEquals(0, MainActivity.DesktopModeDiagnostics.reloadCount)

        // 2. Open Window B using the '+' button and load another page
        val (windowB, restoredB) = manager.getOrCreateWebView("tab_B", url = "https://example.com/pageB")
        assertFalse(restoredB)
        assertEquals(1, loadUrlCalls["tab_B"])

        // 3. Return to Window A
        assertTrue("Window A must have a live WebView in retention cache", manager.hasLiveWebView("tab_A"))
        val (windowA2, restoredA2) = manager.getOrCreateWebView("tab_A", url = "https://example.com/pageA")

        // Assertions for Test 1
        assertSame("Live WebView instance of Window A must be exactly retained", windowA1, windowA2)
        assertFalse("Window A must NOT be restored from bundle when live instance exists", restoredA2)
        assertEquals("Window A must NOT have an additional loadUrl call", 1, loadUrlCalls["tab_A"])
        assertEquals("Window A must NOT trigger reload()", 0, MainActivity.DesktopModeDiagnostics.reloadCount)
        assertEquals("Window A must NOT trigger restoreState()", 0, MainActivity.DesktopModeDiagnostics.restorationCount)
    }

    // Test 2: Window A scrolls down -> Window B -> Window A. Expected: scroll position remains unchanged.
    @Test
    fun test2_windowSwitchingPreservesScrollPosition() {
        val manager = createTestTabWebViewManager()

        // 1. Open Window A and simulate user scroll
        val (windowA1, _) = manager.getOrCreateWebView("tab_A", url = "https://example.com/long-article")
        windowA1.scrollTo(0, 520)
        assertEquals(520, windowA1.scrollY)

        // 2. Open Window B and simulate different scroll
        val (windowB, _) = manager.getOrCreateWebView("tab_B", url = "https://example.com/dashboard")
        windowB.scrollTo(0, 150)
        assertEquals(150, windowB.scrollY)

        // 3. Switch back to Window A
        val (windowA2, _) = manager.getOrCreateWebView("tab_A", url = "https://example.com/long-article")
        assertSame(windowA1, windowA2)
        assertEquals("Window A scroll position must remain exactly 520 without jumping or reset", 520, windowA2.scrollY)
    }

    // Test 3: Window A is in Desktop Mode -> Window B -> Window A. Expected: Desktop Mode remains active.
    @Test
    fun test3_windowSwitchingPreservesDesktopMode() {
        val manager = createTestTabWebViewManager()

        // 1. Open Window A and enable Desktop Mode
        val (windowA1, _) = manager.getOrCreateWebView("tab_A", url = "https://example.com/desktop-site")
        MainActivity.applyDesktopModeToWebView(windowA1, enabled = true, url = "https://example.com/desktop-site")
        val expectedDesktopUa = MainActivity.resolveDesktopUserAgent(context)
        assertEquals(expectedDesktopUa, windowA1.settings.userAgentString)
        assertTrue(windowA1.settings.useWideViewPort)

        // 2. Open Window B in Mobile Mode
        val (windowB, _) = manager.getOrCreateWebView("tab_B", url = "https://m.example.com/mobile-site")
        MainActivity.applyDesktopModeToWebView(windowB, enabled = false, url = "https://m.example.com/mobile-site")

        // 3. Switch back to Window A
        val (windowA2, _) = manager.getOrCreateWebView("tab_A", url = "https://example.com/desktop-site")
        assertSame(windowA1, windowA2)
        assertEquals("Window A Desktop User-Agent must remain untouched", expectedDesktopUa, windowA2.settings.userAgentString)
        assertTrue("Window A wide viewport setting must remain active", windowA2.settings.useWideViewPort)
    }

    // Test 4: Window A has an authenticated session -> Window B -> Window A. Expected: session remains intact.
    @Test
    fun test4_windowSwitchingPreservesAuthenticatedSession() {
        val manager = createTestTabWebViewManager()
        val authUrl = "https://portal.example.com/account"

        // 1. Open Window A and establish session cookies
        val (windowA1, _) = manager.getOrCreateWebView("tab_A", url = authUrl)
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setCookie(authUrl, "session_id=secret_auth_token_987; Path=/; Secure")

        // 2. Open Window B and browse elsewhere
        val (windowB, _) = manager.getOrCreateWebView("tab_B", url = "https://public.example.com")
        cookieManager.setCookie("https://public.example.com", "anon_id=visitor_42; Path=/")

        // 3. Return to Window A
        val (windowA2, _) = manager.getOrCreateWebView("tab_A", url = authUrl)
        assertSame(windowA1, windowA2)

        val activeCookies = cookieManager.getCookie(authUrl)
        assertNotNull(activeCookies)
        assertTrue("Window A session token must remain active and accessible", activeCookies.contains("session_id=secret_auth_token_987"))
    }

    // Test 5: A genuinely destroyed WebView has no live instance. Expected: state restoration is allowed in that case.
    @Test
    fun test5_genuinelyDestroyedWebViewAllowsStateRestoration() {
        val manager = createTestTabWebViewManager(maxLive = 2)

        // Open Tab 1 and Tab 2
        manager.getOrCreateWebView("tab_1", url = "https://example.com/tab1")
        manager.getOrCreateWebView("tab_2", url = "https://example.com/tab2")
        assertTrue(manager.hasLiveWebView("tab_1"))
        assertTrue(manager.hasLiveWebView("tab_2"))

        // Open Tab 3: Exceeds capacity of 2. LRU Tab 1 must be evicted and state saved
        manager.getOrCreateWebView("tab_3", url = "https://example.com/tab3")
        assertFalse("Tab 1 must no longer have a live WebView instance after eviction", manager.hasLiveWebView("tab_1"))
        assertTrue("Tab 1 state must have been captured in saved bundles", savedBundles.containsKey("tab_1"))

        // Returning to Tab 1: Since live instance is genuinely gone, state restoration is allowed
        var restoredCallbackInvoked = false
        val (restoredTab1, wasRestored) = manager.getOrCreateWebView(
            tabId = "tab_1",
            url = "https://example.com/tab1",
            bundle = savedBundles["tab_1"],
            onRestored = { restoredCallbackInvoked = true }
        )

        assertTrue("Restoration must be allowed when WebView was genuinely destroyed", wasRestored)
        assertTrue("Restoration callback must be invoked", restoredCallbackInvoked)
        assertTrue("Tab 1 must now be in live cache again", manager.hasLiveWebView("tab_1"))
    }

    // Test 6: Multiple windows are opened. Expected: memory usage remains controlled and there is no unlimited WebView creation.
    @Test
    fun test6_multipleWindowsMemoryUsageRemainsControlledWithoutUnlimitedWebViews() {
        val maxAllowed = TabWebViewManager.MAX_LIVE_WEBVIEWS
        val manager = createTestTabWebViewManager(maxLive = maxAllowed)

        // Open 10 windows sequentially
        for (i in 1..10) {
            manager.getOrCreateWebView("tab_$i", url = "https://example.com/window_$i")
            assertTrue(
                "Live WebView count (${manager.liveWebViewCount}) must never exceed $maxAllowed",
                manager.liveWebViewCount <= maxAllowed
            )
        }

        // Final verification
        assertEquals("Total live WebViews must be capped at $maxAllowed", maxAllowed, manager.liveWebViewCount)
        assertEquals("Exactly $maxAllowed live tabs should be retained", maxAllowed, manager.liveTabIds.size)

        // The newest tabs (7, 8, 9, 10) must be alive, older tabs (1..6) must have been evicted and saved
        for (i in 1..6) {
            assertFalse("Tab $i should have been evicted to save RAM", manager.hasLiveWebView("tab_$i"))
            assertTrue("Tab $i state must have been preserved in bundle before eviction", savedBundles.containsKey("tab_$i"))
        }
        for (i in 7..10) {
            assertTrue("Tab $i must be retained live in memory", manager.hasLiveWebView("tab_$i"))
        }
    }

    // Test 7: Closing a tab explicitly destroys and frees its WebView
    @Test
    fun test7_closingTabExplicitlyDestroysAndFreesWebView() {
        val manager = createTestTabWebViewManager()
        manager.getOrCreateWebView("tab_close_test", url = "https://example.com/close")
        assertTrue(manager.hasLiveWebView("tab_close_test"))
        assertEquals(1, manager.liveWebViewCount)

        manager.destroyWebView("tab_close_test")
        assertFalse(manager.hasLiveWebView("tab_close_test"))
        assertEquals(0, manager.liveWebViewCount)
    }

    // Test 8: FrameLayout container in UI allows swapping live WebViews without detaching or destroying state
    @Test
    fun test8_frameLayoutSwappingLiveWebViews() {
        val container = FrameLayout(context)
        val manager = createTestTabWebViewManager()

        val (viewA, _) = manager.getOrCreateWebView("tab_A", url = "https://example.com/pageA")
        val (viewB, _) = manager.getOrCreateWebView("tab_B", url = "https://example.com/pageB")

        // 1. Attach View A
        container.addView(viewA, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        assertEquals(1, container.childCount)
        assertSame(viewA, container.getChildAt(0))

        // 2. Swap to View B (as performed by BrowserWebView AndroidView update block)
        (viewB.parent as? ViewGroup)?.removeView(viewB)
        container.removeAllViews()
        container.addView(viewB, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        assertEquals(1, container.childCount)
        assertSame(viewB, container.getChildAt(0))

        // 3. Swap back to View A
        (viewA.parent as? ViewGroup)?.removeView(viewA)
        container.removeAllViews()
        container.addView(viewA, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        assertEquals(1, container.childCount)
        assertSame(viewA, container.getChildAt(0))

        // View A must remain live and identical
        assertSame(viewA, manager.getOrCreateWebView("tab_A").first)
    }
}
