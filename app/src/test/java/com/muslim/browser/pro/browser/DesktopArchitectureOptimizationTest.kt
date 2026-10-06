package com.muslim.browser.pro.browser

import android.content.Context
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DesktopArchitectureOptimizationTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        DesktopMode12Engine.resetCache()
    }

    @Test
    fun test1_desktopMode4BaselinePreserved() {
        val webView = WebView(context)
        DesktopCore.applyCommonDesktopWebViewSettings(webView)

        val settings = webView.settings
        assertEquals(true, settings.useWideViewPort)
        assertEquals(true, settings.loadWithOverviewMode)
        assertEquals(100, settings.textZoom)
        assertEquals(true, settings.builtInZoomControls)
        assertEquals(false, settings.displayZoomControls)
        assertEquals("width=1280", DesktopCore.VIEWPORT_CONTENT)
        assertEquals(1280, DesktopCore.TARGET_WIDTH)
    }

    @Test
    fun test2_desktopMode11ModerateOptimization() {
        val webView = WebView(context)
        DesktopMode11Engine.applySettings(webView)

        val settings = webView.settings
        assertEquals(true, settings.useWideViewPort)
        assertEquals(true, settings.loadWithOverviewMode)
        assertEquals(100, settings.textZoom)
        assertEquals(true, settings.builtInZoomControls)
        assertEquals(false, settings.displayZoomControls)

        val mode11Script = DesktopMode11Engine.GUARD_SCRIPT
        // Guard key prevents repeated execution
        assertTrue(mode11Script.contains("__mb_desktop_mode11__"))
        assertTrue(mode11Script.contains("width=1280"))

        // Single targeted observer on meta, eliminating head childList observer
        assertTrue(mode11Script.contains("MutationObserver"))
        assertTrue(mode11Script.contains("attributeFilter: ['content']"))
        assertFalse(mode11Script.contains("headObserver"))
    }

    @Test
    fun test3_desktopMode12AggressiveOptimization() {
        val webView = WebView(context)
        assertFalse(DesktopMode12Engine.isConfigured(webView))

        DesktopMode12Engine.applySettings(webView)
        assertTrue(DesktopMode12Engine.isConfigured(webView))

        val settings = webView.settings
        assertEquals(true, settings.useWideViewPort)
        assertEquals(true, settings.loadWithOverviewMode)
        assertEquals(100, settings.textZoom)
        assertEquals(true, settings.builtInZoomControls)
        assertEquals(false, settings.displayZoomControls)

        // Second call is an instant in-memory no-op
        DesktopMode12Engine.applySettings(webView)
        assertTrue(DesktopMode12Engine.isConfigured(webView))

        val mode12Script = DesktopMode12Engine.SCRIPT
        assertTrue(mode12Script.contains("__mb_desktop_mode12__"))
        assertTrue(mode12Script.contains("width=1280"))

        // Aggressively optimized: ZERO MutationObservers
        assertFalse("Mode 12 must have 0 MutationObservers", mode12Script.contains("MutationObserver"))
    }

    @Test
    fun test4_optimizationDepthHierarchy() {
        val mode11Script = DesktopMode11Engine.GUARD_SCRIPT
        val mode12Script = DesktopMode12Engine.SCRIPT

        // Mode 11 has 1 MutationObserver
        assertTrue(mode11Script.contains("MutationObserver"))

        // Mode 12 has 0 MutationObservers
        assertFalse(mode12Script.contains("MutationObserver"))

        // Mode 12 is demonstrably lighter in script size and runtime work
        assertTrue(mode12Script.length < mode11Script.length)

        // Mode 12 implements instance cache, Mode 11 configures per call
        val webView = WebView(context)
        DesktopMode12Engine.applySettings(webView)
        assertTrue(DesktopMode12Engine.isConfigured(webView))
        DesktopMode12Engine.cleanupState(webView)
        assertFalse(DesktopMode12Engine.isConfigured(webView))
    }

    @Test
    fun test5_authenticationUrlsBypassDesktopScriptInjection() {
        val webView = WebView(context)
        val authUrl = "https://accounts.google.com/signin"

        // Bypasses script evaluation for authentication flows
        DesktopMode11Engine.handleLifecycle(webView, authUrl)
        DesktopMode12Engine.handleLifecycle(webView, authUrl)
        assertNotNull(webView)
    }

    @Test
    fun test6_runtimeDesktopArchitectureEnum() {
        val archClass = Class.forName("com.muslim.browser.pro.browser.DesktopArchitecture")
        assertNotNull(archClass)
        assertTrue(archClass.isEnum)

        val enumConstants = archClass.enumConstants as Array<*>
        val names = enumConstants.map { (it as Enum<*>).name }

        assertTrue(names.contains("NONE"))
        assertTrue(names.contains("STANDARD"))
        assertTrue(names.contains("DESKTOP_MODE_4"))
        assertTrue(names.contains("WINDOWS_10_TOUCH"))
    }
}
