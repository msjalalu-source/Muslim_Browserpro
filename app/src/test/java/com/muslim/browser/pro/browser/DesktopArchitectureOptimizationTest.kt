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

        // Retained in enum for binary compatibility with app-prebuilt.jar
        assertTrue(names.contains("NONE"))
        assertTrue(names.contains("STANDARD"))
        assertTrue(names.contains("DESKTOP_MODE_4"))
        assertTrue(names.contains("WINDOWS_10_TOUCH"))
        assertEquals(4, names.size)

        val isAnyDesktopMethod = archClass.getMethod("isAnyDesktop")
        val isWindows10TouchMethod = archClass.getMethod("isWindows10Touch")

        // Inactive modes return false for isAnyDesktop (behave as mobile)
        val mode4 = java.lang.Enum.valueOf(archClass as Class<out Enum<*>>, "DESKTOP_MODE_4")
        assertFalse(isAnyDesktopMethod.invoke(mode4) as Boolean)
        assertFalse(isWindows10TouchMethod.invoke(mode4) as Boolean)

        val standard = java.lang.Enum.valueOf(archClass, "STANDARD")
        assertFalse(isAnyDesktopMethod.invoke(standard) as Boolean)
        assertFalse(isWindows10TouchMethod.invoke(standard) as Boolean)

        val win10 = java.lang.Enum.valueOf(archClass, "WINDOWS_10_TOUCH")
        assertTrue(isWindows10TouchMethod.invoke(win10) as Boolean)
        assertTrue(isAnyDesktopMethod.invoke(win10) as Boolean)

        val none = java.lang.Enum.valueOf(archClass, "NONE")
        assertFalse(isAnyDesktopMethod.invoke(none) as Boolean)
        assertFalse(isWindows10TouchMethod.invoke(none) as Boolean)
    }

    @Test
    fun test4_desktopLifecycleHandling() {
        val webView = WebView(context)
        DesktopCore.applyCommonDesktopWebViewSettings(webView)

        // Windows 10 Touch lifecycle handling
        DesktopCore.handlePageLifecycle(webView, DesktopArchitecture.WINDOWS_10_TOUCH, "https://example.com")
        assertEquals(true, webView.settings.useWideViewPort)

        // Inactive modes return early without error
        DesktopCore.handlePageLifecycle(webView, DesktopArchitecture.STANDARD, "https://example.com")
        DesktopCore.handlePageLifecycle(webView, DesktopArchitecture.DESKTOP_MODE_4, "https://example.com")
        DesktopCore.handlePageLifecycle(webView, DesktopArchitecture.NONE, "https://example.com")
    }

    @Test
    fun test5_desktopLiveSynchronization() {
        val webView = WebView(context)

        // Windows 10 Touch live synchronization
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.WINDOWS_10_TOUCH, true)
        val winUaField = DesktopCore::class.java.getField("WINDOWS_10_TOUCH_USER_AGENT")
        val expectedWinUa = winUaField.get(null) as String
        assertEquals(expectedWinUa, webView.settings.userAgentString)

        // Authentication protection: keep mobile UA
        DesktopCore.synchronizeDesktopWebView(webView, "https://accounts.google.com/signin", DesktopArchitecture.WINDOWS_10_TOUCH, true)
        assertNull(webView.settings.userAgentString)

        // Mobile / NONE reset
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.NONE, true)
        assertNull(webView.settings.userAgentString)

        // Inactive modes fallback to mobile
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.STANDARD, true)
        assertNull(webView.settings.userAgentString)

        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.DESKTOP_MODE_4, true)
        assertNull(webView.settings.userAgentString)
    }

    @Test
    fun test6_preferenceMigration() {
        val prefs = context.getSharedPreferences("focus_shield_prefs", Context.MODE_PRIVATE)

        val instance = DesktopCore::class.java.getField("INSTANCE").get(null)
        val migrateMethod = DesktopCore::class.java.getMethod("migrateSavedArchitecture", Context::class.java)

        // Legacy STANDARD mode must be migrated to NONE
        prefs.edit().putString("key_desktop_architecture", "STANDARD").commit()
        migrateMethod.invoke(instance, context)
        assertEquals("NONE", prefs.getString("key_desktop_architecture", null))

        // DESKTOP_MODE_4 must be migrated to NONE
        prefs.edit().putString("key_desktop_architecture", "DESKTOP_MODE_4").commit()
        migrateMethod.invoke(instance, context)
        assertEquals("NONE", prefs.getString("key_desktop_architecture", null))

        // Legacy mode 11 must be migrated to NONE
        prefs.edit().putString("key_desktop_architecture", "DESKTOP_MODE_11").commit()
        migrateMethod.invoke(instance, context)
        assertEquals("NONE", prefs.getString("key_desktop_architecture", null))

        // WINDOWS_10_TOUCH is preserved intact
        prefs.edit().putString("key_desktop_architecture", "WINDOWS_10_TOUCH").commit()
        migrateMethod.invoke(instance, context)
        assertEquals("WINDOWS_10_TOUCH", prefs.getString("key_desktop_architecture", null))
    }

    @Test
    fun test7_cleanupScriptRestoresState() {
        val cleanupField = DesktopCore::class.java.getDeclaredField("CLEANUP_DESKTOP_SCRIPT")
        cleanupField.isAccessible = true
        val cleanupScript = cleanupField.get(null) as String

        assertTrue(cleanupScript.contains("__mb_win10_touch__"))
        assertTrue(cleanupScript.contains("cleanup"))
        assertTrue(cleanupScript.contains("delete navigator.userAgentData"))
        assertTrue(cleanupScript.contains("delete navigator.platform"))
    }

    @Test
    fun test8_windows10DesktopArchitectureActivationAndScript() {
        val webView = WebView(context)

        DesktopCore.applyCommonDesktopWebViewSettings(webView)
        val settings = webView.settings
        assertEquals(true, settings.useWideViewPort)
        assertEquals(true, settings.loadWithOverviewMode)
        assertEquals(100, settings.textZoom)
        assertEquals(true, settings.builtInZoomControls)
        assertEquals(false, settings.displayZoomControls)

        val scriptField = DesktopCore::class.java.getField("WINDOWS_10_TOUCH_SCRIPT")
        val script = scriptField.get(null) as String

        assertTrue("Win10 script must emulate Win32 platform", script.contains("platform: { get: function() { return 'Win32'; }"))
        assertTrue("Win10 script must emulate Windows 10 User-Agent", script.contains("Windows NT 10.0"))
        assertTrue("Win10 script must configure 1920x1080 screen dimensions", script.contains("1920") && script.contains("1080"))
        assertTrue("Win10 script must emulate desktop inner viewport", script.contains("desktopInnerW = 1280") && script.contains("desktopInnerH = 720"))
        assertTrue("Win10 script must emulate Intel Direct3D11 WebGL renderer", script.contains("Intel") && script.contains("Direct3D11"))
        assertTrue("Win10 script must match Windows 10 touch/hover media queries", script.contains("any-pointer: coarse") && script.contains("hover: hover"))
        assertTrue("Win10 script must have idempotent guard key to prevent duplication", script.contains("__mb_win10_touch__"))

        val applyCommonMethod = DesktopCore::class.java.getMethod("applyCommonDesktopViewport", WebView::class.java)
        assertNotNull(applyCommonMethod)

        val applyWin10Method = DesktopCore::class.java.getMethod("applyWindows10TouchProfile", WebView::class.java)
        assertNotNull(applyWin10Method)

        // Synchronize to Windows 10 Desktop and verify User-Agent
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.WINDOWS_10_TOUCH, true)
        val winUaField = DesktopCore::class.java.getField("WINDOWS_10_TOUCH_USER_AGENT")
        val expectedWinUa = winUaField.get(null) as String
        assertEquals(expectedWinUa, webView.settings.userAgentString)

        // Switch back to mobile
        DesktopCore.synchronizeDesktopWebView(webView, "https://example.com", DesktopArchitecture.NONE, true)
        assertNull(webView.settings.userAgentString)
    }

    @Test
    fun test9_windows10DesktopModeStateAndPersistence() {
        val app = context as android.app.Application
        val repository = SettingsRepository(app)
        val vm = BrowserViewModel(app)

        // Initial default: Mobile (NONE)
        assertEquals(DesktopArchitecture.NONE, vm.uiState.value.desktopArchitecture)
        assertFalse(vm.uiState.value.isWindows10TouchEnabled)

        // Toggle to Windows 10 Desktop
        vm.toggleWindows10Touch(true)
        assertEquals(DesktopArchitecture.WINDOWS_10_TOUCH, vm.uiState.value.desktopArchitecture)
        assertTrue(vm.uiState.value.isWindows10TouchEnabled)
        assertEquals(DesktopArchitecture.WINDOWS_10_TOUCH, repository.getDesktopArchitecture())

        // Toggle back to Mobile
        vm.toggleWindows10Touch(false)
        assertEquals(DesktopArchitecture.NONE, vm.uiState.value.desktopArchitecture)
        assertFalse(vm.uiState.value.isWindows10TouchEnabled)
        assertEquals(DesktopArchitecture.NONE, repository.getDesktopArchitecture())
    }

    @Test
    fun test10_tabWebViewManagerSynchronizeArchitecture() {
        val twm = TabWebViewManager(
            context = context,
            webViewFactory = { tabId ->
                WebView(context).apply {
                    DesktopCore.applyCommonDesktopWebViewSettings(this)
                }
            },
            onSyncArchitecture = { webView, arch ->
                DesktopCore.applyCommonDesktopWebViewSettings(webView)
                DesktopCore.synchronizeDesktopWebView(webView, webView.url ?: "", arch, true)
            }
        )

        val wv1 = twm.getOrCreateWebView("tab1", "https://example.com").first
        val wv2 = twm.getOrCreateWebView("tab2", "https://example.org").first

        // Sync to Windows 10 Desktop
        twm.syncAllLiveWebViewsArchitecture(DesktopArchitecture.WINDOWS_10_TOUCH)
        assertEquals(DesktopCore.WINDOWS_10_TOUCH_USER_AGENT, wv1.settings.userAgentString)
        assertEquals(DesktopCore.WINDOWS_10_TOUCH_USER_AGENT, wv2.settings.userAgentString)
        assertTrue(wv1.settings.useWideViewPort)
        assertTrue(wv2.settings.useWideViewPort)

        // Sync back to Mobile
        twm.syncAllLiveWebViewsArchitecture(DesktopArchitecture.NONE)
        assertNull(wv1.settings.userAgentString)
        assertNull(wv2.settings.userAgentString)
    }

    @Test
    fun test11_initialWebViewCreationRespectsSavedArchitecture() {
        val app = context as android.app.Application
        val repository = SettingsRepository(app)
        repository.setDesktopArchitecture(DesktopArchitecture.WINDOWS_10_TOUCH)

        val vm = BrowserViewModel(app)
        assertEquals(DesktopArchitecture.WINDOWS_10_TOUCH, vm.uiState.value.desktopArchitecture)
        assertTrue(vm.uiState.value.isWindows10TouchEnabled)

        // Clean up preference
        repository.setDesktopArchitecture(DesktopArchitecture.NONE)
    }
}
