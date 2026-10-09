package com.muslim.browser.pro.browser

import android.content.Context
import android.webkit.WebView
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
class DesktopArchitectureOptimizationTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun test1_mainDesktopBaselinePreserved() {
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
        val isAuthMethod = DesktopCore::class.java.getMethod("isAuthenticationUrl", String::class.java)
        val instance = DesktopCore::class.java.getField("INSTANCE").get(null)

        assertTrue(isAuthMethod.invoke(instance, "https://accounts.google.com/signin") as Boolean)
        assertTrue(isAuthMethod.invoke(instance, "https://login.live.com/login.srf") as Boolean)
        assertTrue(isAuthMethod.invoke(instance, "https://login.microsoftonline.com/common/oauth2") as Boolean)
        assertFalse(isAuthMethod.invoke(instance, "https://example.com") as Boolean)
        assertFalse(isAuthMethod.invoke(instance, "https://wikipedia.org") as Boolean)
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
        assertTrue(names.contains("WINDOWS_10_TOUCH"))
        assertFalse(names.contains("DESKTOP_MODE_4"))
        assertFalse(names.contains("DESKTOP_MODE_11"))
        assertFalse(names.contains("DESKTOP_MODE_12"))
        assertFalse(names.contains("WINDOWS_7"))

        assertEquals(3, names.size)

        val standard = java.lang.Enum.valueOf(archClass as Class<out Enum<*>>, "STANDARD")
        val isStandardMethod = archClass.getMethod("isStandard")
        val isAnyDesktopMethod = archClass.getMethod("isAnyDesktop")
        val isWindows10TouchMethod = archClass.getMethod("isWindows10Touch")

        assertTrue(isStandardMethod.invoke(standard) as Boolean)
        assertTrue(isAnyDesktopMethod.invoke(standard) as Boolean)
        assertFalse(isWindows10TouchMethod.invoke(standard) as Boolean)

        val none = java.lang.Enum.valueOf(archClass, "NONE")
        assertFalse(isAnyDesktopMethod.invoke(none) as Boolean)
        assertFalse(isStandardMethod.invoke(none) as Boolean)
    }

    @Test
    fun test4_mainDesktopOptimizedScriptAndSettings() {
        val webView = WebView(context)
        DesktopCore.applyCommonDesktopWebViewSettings(webView)

        val settings = webView.settings
        assertEquals(true, settings.useWideViewPort)
        assertEquals(true, settings.loadWithOverviewMode)
        assertEquals(100, settings.textZoom)
        assertEquals(true, settings.builtInZoomControls)
        assertEquals(false, settings.displayZoomControls)

        val scriptField = DesktopCore::class.java.getField("DESKTOP_VIEWPORT_SCRIPT")
        val script = scriptField.get(null) as String
        // Idempotent flag
        assertTrue(script.contains("__mb_desktop_applied__"))
        // Viewport 1280
        assertTrue(script.contains("width=1280"))
        // Ultra-lightweight: Zero MutationObservers, zero event listeners, zero navigator tampering
        assertFalse("Main Desktop script must not contain MutationObserver", script.contains("MutationObserver"))
        assertFalse("Main Desktop script must not contain turbo:load", script.contains("turbo:load"))
        assertFalse("Main Desktop script must not contain pjax:end", script.contains("pjax:end"))
        assertFalse("Main Desktop script must not contain userAgentData", script.contains("userAgentData"))
        assertFalse("Main Desktop script must not contain platform spoofing", script.contains("platform"))
        assertTrue("Main Desktop script must be ultra-lightweight (< 600 chars)", script.length < 600)
    }

    @Test
    fun test5_mainDesktopLifecycleHandling() {
        val webView = WebView(context)
        DesktopCore.applyCommonDesktopWebViewSettings(webView)
        DesktopCore.applyCommonDesktopViewport(webView)
        DesktopCore.handlePageLifecycle(webView, DesktopArchitecture.STANDARD, "https://example.com")
        assertEquals(true, webView.settings.useWideViewPort)
        assertEquals(true, webView.settings.loadWithOverviewMode)
    }

    @Test
    fun test6_mainDesktopLiveSynchronization() {
        val webView = WebView(context)
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.STANDARD, true)
        val uaField = DesktopCore::class.java.getField("DESKTOP_USER_AGENT")
        val expectedUa = uaField.get(null) as String

        assertEquals(expectedUa, webView.settings.userAgentString)
        assertEquals(true, webView.settings.useWideViewPort)
        assertEquals(true, webView.settings.loadWithOverviewMode)

        // Authentication protection: keep mobile UA
        DesktopCore.synchronizeDesktopWebView(webView, "https://accounts.google.com/signin", DesktopArchitecture.STANDARD, true)
        assertNull(webView.settings.userAgentString)

        // Mobile / NONE reset
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.NONE, true)
        assertNull(webView.settings.userAgentString)
    }

    @Test
    fun test7_preferenceMigration() {
        val prefs = context.getSharedPreferences("focus_shield_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("key_desktop_architecture", "DESKTOP_MODE_4").commit()

        val instance = DesktopCore::class.java.getField("INSTANCE").get(null)
        val migrateMethod = DesktopCore::class.java.getMethod("migrateSavedArchitecture", Context::class.java)
        migrateMethod.invoke(instance, context)

        val migrated = prefs.getString("key_desktop_architecture", null)
        assertEquals("STANDARD", migrated)
    }
}
