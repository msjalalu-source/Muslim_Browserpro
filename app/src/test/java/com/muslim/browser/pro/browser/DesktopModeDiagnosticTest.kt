package com.muslim.browser.pro.browser

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class DesktopModeDiagnosticTest {

    @Test
    fun testDisplayMetricsDensityOrigin() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val metrics = context.resources.displayMetrics
        assertNotNull(metrics)
        // Verify density calculation: density = densityDpi / 160.0
        val expectedDensity = metrics.densityDpi / 160.0f
        assertEquals(expectedDensity, metrics.density, 0.01f)
    }

    @Test
    fun testConfigurationContextDensity() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val config = Configuration(context.resources.configuration)
        config.densityDpi = DisplayMetrics.DENSITY_DEFAULT // 160 dpi -> 1.0f density
        val configContext = context.createConfigurationContext(config)
        val configMetrics = configContext.resources.displayMetrics
        assertEquals(1.0f, configMetrics.density, 0.01f)
        assertEquals(160, configMetrics.densityDpi)
    }

    @Test
    fun testDesktopCoreSettingsPreserved() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val webView = WebView(context)
        DesktopCore.applyCommonDesktopWebViewSettings(webView)
        val settings = webView.settings
        assertEquals(true, settings.useWideViewPort)
        assertEquals(true, settings.loadWithOverviewMode)
        assertEquals(100, settings.textZoom)
        assertEquals(true, settings.builtInZoomControls)
        assertEquals(false, settings.displayZoomControls)
    }
}
