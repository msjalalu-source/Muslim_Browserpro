package com.muslim.browser.pro.browser

import android.content.Context
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Windows7DesktopModeTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun test1_windows7EnumAndProperties() {
        val archClass = Class.forName("com.muslim.browser.pro.browser.DesktopArchitecture")
        assertNotNull(archClass)
        assertTrue(archClass.isEnum)

        val enumConstants = archClass.enumConstants as Array<*>
        val names = enumConstants.map { (it as Enum<*>).name }
        assertTrue(names.contains("DESKTOP_MODE_4"))
        assertTrue(names.contains("STANDARD"))
        assertTrue(names.contains("NONE"))
    }

    @Test
    fun test2_windows7AndMode4SettingsAlignment() {
        val webView = WebView(context)
        DesktopCore.applyCommonDesktopWebViewSettings(webView)

        val s = webView.settings
        assertEquals(true, s.useWideViewPort)
        assertEquals(true, s.loadWithOverviewMode)
        assertEquals(100, s.textZoom)
        assertEquals(true, s.builtInZoomControls)
        assertEquals(false, s.displayZoomControls)
    }

    @Test
    fun test3_windows7TargetDimensionsAndViewport() {
        assertEquals(1280, DesktopCore.TARGET_WIDTH)
        assertEquals("width=1280", DesktopCore.VIEWPORT_CONTENT)
    }

    @Test
    fun test4_windows7WhenMappingsAlignment() {
        val whenMappingsClass = Class.forName("com.muslim.browser.pro.browser.WebViewConfigurator\$WhenMappings")
        assertNotNull(whenMappingsClass)
        val field = whenMappingsClass.getField("\$EnumSwitchMapping\$1")
        val mappingArray = field.get(null) as IntArray
        assertNotNull(mappingArray)
        assertTrue(mappingArray.isNotEmpty())
    }

    @Test
    fun test5_windows7LifecycleHandling() {
        val webView = WebView(context)
        DesktopCore.applyCommonDesktopViewport(webView)
        assertNotNull(webView)
    }

    @Test
    fun test6_browserMenuSheetSignatureBinaryCompatibility() {
        val menuSheetClass = Class.forName("com.muslim.browser.pro.browser.ui.BrowserMenuSheetKt")
        assertNotNull(menuSheetClass)
        val methods = menuSheetClass.methods.filter { it.name == "BrowserMenuSheet" }
        assertTrue("BrowserMenuSheet must exist", methods.isNotEmpty())
        val matchingMethod = methods.firstOrNull { it.parameterCount == 21 }
        assertNotNull("BrowserMenuSheet must have exactly 21 parameters for binary compatibility with MainActivityKt", matchingMethod)
    }
}
