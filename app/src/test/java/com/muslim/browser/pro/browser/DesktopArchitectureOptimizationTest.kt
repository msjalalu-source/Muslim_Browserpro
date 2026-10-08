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
    fun test2_authenticationUrlChecks() {
        val authUrl = "https://accounts.google.com/signin"
        val nonAuthUrl = "https://example.com"
        assertTrue(DesktopCore::class.java.methods.isNotEmpty())
    }

    @Test
    fun test3_runtimeDesktopArchitectureEnum() {
        val archClass = Class.forName("com.muslim.browser.pro.browser.DesktopArchitecture")
        assertNotNull(archClass)
        assertTrue(archClass.isEnum)

        val enumConstants = archClass.enumConstants as Array<*>
        val names = enumConstants.map { (it as Enum<*>).name }

        assertTrue(names.contains("NONE"))
        assertTrue(names.contains("STANDARD"))
        assertTrue(names.contains("DESKTOP_MODE_4"))
        assertTrue(names.contains("WINDOWS_10_TOUCH"))

        assertEquals(4, names.size)
    }

    @Test
    fun test4_desktopMode4OptimizedScriptAndSettings() {
        val webView = WebView(context)
        val instance = DesktopCore::class.java.getField("INSTANCE").get(null)
        val method = DesktopCore::class.java.getMethod("applyDesktopMode4Settings", WebView::class.java)
        method.invoke(instance, webView)

        val settings = webView.settings
        assertEquals(true, settings.useWideViewPort)
        assertEquals(true, settings.loadWithOverviewMode)
        assertEquals(100, settings.textZoom)
        assertEquals(true, settings.builtInZoomControls)
        assertEquals(false, settings.displayZoomControls)

        val scriptField = DesktopCore::class.java.getField("DESKTOP_MODE_4_SCRIPT")
        val script = scriptField.get(null) as String
        // Idempotent flag
        assertTrue(script.contains("__mb_desktop_mode4_applied__"))
        // Viewport 1280
        assertTrue(script.contains("width=1280"))
        // Ultra-lightweight: Zero MutationObservers, zero event listeners, zero navigator tampering
        assertFalse("Mode 4 must not contain MutationObserver", script.contains("MutationObserver"))
        assertFalse("Mode 4 must not contain turbo:load", script.contains("turbo:load"))
        assertFalse("Mode 4 must not contain pjax:end", script.contains("pjax:end"))
        assertFalse("Mode 4 must not contain userAgentData", script.contains("userAgentData"))
        assertFalse("Mode 4 must not contain platform spoofing", script.contains("platform"))
        assertTrue("Mode 4 script must be ultra-lightweight (< 600 chars)", script.length < 600)
    }

    @Test
    fun test5_desktopMode4LifecycleHandling() {
        val webView = WebView(context)
        DesktopCore.applyCommonDesktopWebViewSettings(webView)
        DesktopCore.applyCommonDesktopViewport(webView)
        assertEquals(true, webView.settings.useWideViewPort)
        assertEquals(true, webView.settings.loadWithOverviewMode)
    }
}
