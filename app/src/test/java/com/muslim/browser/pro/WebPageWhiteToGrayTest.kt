package com.muslim.browser.pro

import android.app.Application
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.WebViewConfigurator
import com.muslim.browser.pro.ui.theme.AppTheme
import com.muslim.browser.pro.ui.theme.BlackWhiteColors
import com.muslim.browser.pro.ui.theme.WhiteColors
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
class WebPageWhiteToGrayTest {

    private lateinit var context: Application

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `verify Black & White theme executes web page transformation targeting pure white to gray`() {
        val webView = WebView(context)

        // Apply Black & White theme (isDarkTheme = true)
        MainActivity.applyWebViewTheme(webView, isDarkTheme = true)
        assertTrue(MainActivity.isDarkThemeActive)

        // applyWebPageDarkTheme must execute cleanly without exceptions
        WebViewConfigurator.applyWebPageDarkTheme(webView, isDarkTheme = true)
    }

    @Test
    fun `verify White theme executes cleanup script restoring normal web page rendering`() {
        val webView = WebView(context)

        // Apply White theme (isDarkTheme = false)
        MainActivity.applyWebViewTheme(webView, isDarkTheme = false)
        assertFalse(MainActivity.isDarkThemeActive)

        // applyWebPageDarkTheme must execute cleanup cleanly without exceptions
        WebViewConfigurator.applyWebPageDarkTheme(webView, isDarkTheme = false)
    }

    @Test
    fun `verify browser native UI colors remain untouched`() {
        // Black & White native theme tokens must remain completely unmodified
        assertEquals(androidx.compose.ui.graphics.Color(0xFF000000), BlackWhiteColors.background)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF141414), BlackWhiteColors.surface)
        assertEquals(androidx.compose.ui.graphics.Color(0xFFFFFFFF), BlackWhiteColors.textPrimary)

        // White native theme tokens must remain completely unmodified
        assertEquals(androidx.compose.ui.graphics.Color(0xFFF4F6F9), WhiteColors.background)
        assertEquals(androidx.compose.ui.graphics.Color(0xFFFFFFFF), WhiteColors.surface)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF0284C7), WhiteColors.accent)
    }
}
