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
        assertTrue(names.contains("DESKTOP_MODE_4"))
        assertTrue(names.contains("WINDOWS_10_TOUCH"))
        assertFalse(names.contains("DESKTOP_MODE_11"))
        assertFalse(names.contains("DESKTOP_MODE_12"))
        assertFalse(names.contains("WINDOWS_7"))

        assertEquals(4, names.size)

        val mode4 = java.lang.Enum.valueOf(archClass as Class<out Enum<*>>, "DESKTOP_MODE_4")
        val isAnyDesktopMethod = archClass.getMethod("isAnyDesktop")
        val isWindows10TouchMethod = archClass.getMethod("isWindows10Touch")
        val isStandardMethod = archClass.getMethod("isStandard")
        val isMode4Method = archClass.getMethod("isMode4")

        assertTrue(isMode4Method.invoke(mode4) as Boolean)
        assertTrue(isAnyDesktopMethod.invoke(mode4) as Boolean)
        assertFalse(isWindows10TouchMethod.invoke(mode4) as Boolean)

        val win10 = java.lang.Enum.valueOf(archClass, "WINDOWS_10_TOUCH")
        assertTrue(isWindows10TouchMethod.invoke(win10) as Boolean)
        assertTrue(isAnyDesktopMethod.invoke(win10) as Boolean)
        assertFalse(isMode4Method.invoke(win10) as Boolean)

        val none = java.lang.Enum.valueOf(archClass, "NONE")
        assertFalse(isAnyDesktopMethod.invoke(none) as Boolean)
        assertFalse(isMode4Method.invoke(none) as Boolean)
        assertFalse(isWindows10TouchMethod.invoke(none) as Boolean)
    }

    @Test
    fun test4_desktopMode4DedicatedScriptAndSettings() {
        val webView = WebView(context)
        DesktopCore.applyCommonDesktopWebViewSettings(webView)

        val settings = webView.settings
        assertEquals(true, settings.useWideViewPort)
        assertEquals(true, settings.loadWithOverviewMode)
        assertEquals(100, settings.textZoom)
        assertEquals(true, settings.builtInZoomControls)
        assertEquals(false, settings.displayZoomControls)

        val scriptField = DesktopCore::class.java.getField("DESKTOP_MODE_4_SCRIPT")
        val script = scriptField.get(null) as String

        // Guard key and idempotent manager
        assertTrue(script.contains("__mb_desktop_mode4__"))
        assertTrue(script.contains("applyViewport"))
        assertTrue(script.contains("cleanup"))

        // Viewport 1280
        assertTrue(script.contains("width=1280"))

        // Desktop Mode 4: Client hints spoofing & platform
        assertTrue("Desktop Mode 4 script must patch userAgentData", script.contains("userAgentData"))
        assertTrue("Desktop Mode 4 script must patch platform", script.contains("platform"))
        assertTrue("Desktop Mode 4 script must set Linux x86_64", script.contains("Linux x86_64"))

        // Desktop Mode 4: MutationObservers for viewport meta and head
        assertTrue("Desktop Mode 4 script must include MutationObserver", script.contains("MutationObserver"))
        assertTrue("Desktop Mode 4 script must attach meta observer", script.contains("attachMetaObserver"))

        // Desktop Mode 4: SPA navigation event listeners
        assertTrue("Desktop Mode 4 script must listen to turbo:load", script.contains("turbo:load"))
        assertTrue("Desktop Mode 4 script must listen to pjax:end", script.contains("pjax:end"))
        assertTrue("Desktop Mode 4 script must listen to popstate", script.contains("popstate"))
    }

    @Test
    fun test5_desktopLifecycleHandling() {
        val webView = WebView(context)
        DesktopCore.applyCommonDesktopWebViewSettings(webView)
        val instance = DesktopCore::class.java.getField("INSTANCE").get(null)
        val mode4ViewportMethod = try {
            DesktopCore::class.java.getMethod("applyDesktopMode4Viewport", WebView::class.java)
        } catch (_: Throwable) {
            null
        }
        mode4ViewportMethod?.invoke(instance, webView)

        DesktopCore.handlePageLifecycle(webView, DesktopArchitecture.DESKTOP_MODE_4, "https://example.com")
        assertEquals(true, webView.settings.useWideViewPort)
        assertEquals(true, webView.settings.loadWithOverviewMode)

        // Windows 10 Touch independent lifecycle
        DesktopCore.handlePageLifecycle(webView, DesktopArchitecture.WINDOWS_10_TOUCH, "https://example.com")
        assertEquals(true, webView.settings.useWideViewPort)
    }

    @Test
    fun test6_desktopMode4LiveSynchronization() {
        val webView = WebView(context)
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.DESKTOP_MODE_4, true)
        val uaField = DesktopCore::class.java.getField("DESKTOP_USER_AGENT")
        val expectedUa = uaField.get(null) as String

        assertEquals(expectedUa, webView.settings.userAgentString)
        assertEquals(true, webView.settings.useWideViewPort)
        assertEquals(true, webView.settings.loadWithOverviewMode)

        // Windows 10 Touch live synchronization
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.WINDOWS_10_TOUCH, true)
        val winUaField = DesktopCore::class.java.getField("WINDOWS_10_TOUCH_USER_AGENT")
        val expectedWinUa = winUaField.get(null) as String
        assertEquals(expectedWinUa, webView.settings.userAgentString)

        // Authentication protection: keep mobile UA
        DesktopCore.synchronizeDesktopWebView(webView, "https://accounts.google.com/signin", DesktopArchitecture.DESKTOP_MODE_4, true)
        assertNull(webView.settings.userAgentString)

        // Mobile / NONE reset
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.NONE, true)
        assertNull(webView.settings.userAgentString)
    }

    @Test
    fun test7_preferenceMigration() {
        val prefs = context.getSharedPreferences("focus_shield_prefs", Context.MODE_PRIVATE)

        // Legacy STANDARD mode must be migrated to DESKTOP_MODE_4
        prefs.edit().putString("key_desktop_architecture", "STANDARD").commit()

        val instance = DesktopCore::class.java.getField("INSTANCE").get(null)
        val migrateMethod = DesktopCore::class.java.getMethod("migrateSavedArchitecture", Context::class.java)
        migrateMethod.invoke(instance, context)

        val migratedStandard = prefs.getString("key_desktop_architecture", null)
        assertEquals("DESKTOP_MODE_4", migratedStandard)

        // Legacy mode 11 must be migrated to DESKTOP_MODE_4
        prefs.edit().putString("key_desktop_architecture", "DESKTOP_MODE_11").commit()
        migrateMethod.invoke(instance, context)
        val migrated11 = prefs.getString("key_desktop_architecture", null)
        assertEquals("DESKTOP_MODE_4", migrated11)

        // DESKTOP_MODE_4 is preserved intact
        prefs.edit().putString("key_desktop_architecture", "DESKTOP_MODE_4").commit()
        migrateMethod.invoke(instance, context)
        val preservedMode4 = prefs.getString("key_desktop_architecture", null)
        assertEquals("DESKTOP_MODE_4", preservedMode4)

        // WINDOWS_10_TOUCH is preserved intact
        prefs.edit().putString("key_desktop_architecture", "WINDOWS_10_TOUCH").commit()
        migrateMethod.invoke(instance, context)
        val preservedWin = prefs.getString("key_desktop_architecture", null)
        assertEquals("WINDOWS_10_TOUCH", preservedWin)
    }

    @Test
    fun test8_cleanupScriptRestoresState() {
        val cleanupField = DesktopCore::class.java.getDeclaredField("CLEANUP_DESKTOP_SCRIPT")
        cleanupField.isAccessible = true
        val cleanupScript = cleanupField.get(null) as String

        assertTrue(cleanupScript.contains("__mb_desktop_mode4__"))
        assertTrue(cleanupScript.contains("__mb_win10_touch__"))
        assertTrue(cleanupScript.contains("cleanup"))
        assertTrue(cleanupScript.contains("delete navigator.userAgentData"))
        assertTrue(cleanupScript.contains("delete navigator.platform"))
    }

    @Test
    fun test9_windows10DesktopArchitectureActivationAndScript() {
        val webView = WebView(context)

        // Windows 10 Desktop shares common base settings
        DesktopCore.applyCommonDesktopWebViewSettings(webView)
        val settings = webView.settings
        assertEquals(true, settings.useWideViewPort)
        assertEquals(true, settings.loadWithOverviewMode)
        assertEquals(100, settings.textZoom)
        assertEquals(true, settings.builtInZoomControls)
        assertEquals(false, settings.displayZoomControls)

        // Windows 10 Touch Script verification
        val scriptField = DesktopCore::class.java.getField("WINDOWS_10_TOUCH_SCRIPT")
        val script = scriptField.get(null) as String

        // Windows 10 Touch specific profile signals
        assertTrue("Win10 script must emulate Win32 platform", script.contains("platform: { get: function() { return 'Win32'; }"))
        assertTrue("Win10 script must emulate Windows 10 User-Agent", script.contains("Windows NT 10.0"))
        assertTrue("Win10 script must configure 1920x1080 screen dimensions", script.contains("1920") && script.contains("1080"))
        assertTrue("Win10 script must emulate desktop inner viewport", script.contains("desktopInnerW = 1280") && script.contains("desktopInnerH = 720"))
        assertTrue("Win10 script must emulate Intel Direct3D11 WebGL renderer", script.contains("Intel") && script.contains("Direct3D11"))
        assertTrue("Win10 script must match Windows 10 touch/hover media queries", script.contains("any-pointer: coarse") && script.contains("hover: hover"))
        assertTrue("Win10 script must have idempotent guard key to prevent duplication", script.contains("__mb_win10_touch__"))

        // Common desktop viewport method availability for shared engine
        val applyCommonMethod = DesktopCore::class.java.getMethod("applyCommonDesktopViewport", WebView::class.java)
        assertNotNull(applyCommonMethod)

        // Windows 10 Touch profile injection method availability
        val applyWin10Method = DesktopCore::class.java.getMethod("applyWindows10TouchProfile", WebView::class.java)
        assertNotNull(applyWin10Method)

        // Synchronize to Windows 10 Desktop and verify User-Agent
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.WINDOWS_10_TOUCH, true)
        val winUaField = DesktopCore::class.java.getField("WINDOWS_10_TOUCH_USER_AGENT")
        val expectedWinUa = winUaField.get(null) as String
        assertEquals(expectedWinUa, webView.settings.userAgentString)

        // Switch to Desktop Mode 4 without reload
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.DESKTOP_MODE_4, true)
        val mode4UaField = DesktopCore::class.java.getField("DESKTOP_USER_AGENT")
        val expectedMode4Ua = mode4UaField.get(null) as String
        assertEquals(expectedMode4Ua, webView.settings.userAgentString)

        // Switch back to mobile
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.NONE, true)
        assertNull(webView.settings.userAgentString)
    }
}
