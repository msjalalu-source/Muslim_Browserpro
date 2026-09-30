package com.muslim.browser.pro

import android.app.Application
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.ui.DiagnosticData
import com.muslim.browser.pro.browser.ui.collectLiveWebViewDiagnostics
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
class ViewportDiagnosticTest {

    private lateinit var context: Application

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun test1_diagnosticDataFormatContainsAll16Fields() {
        val sample = DiagnosticData(
            webViewUserAgentString = "SampleUA/1.0",
            windowInnerWidth = "1024",
            windowInnerHeight = "768",
            windowOuterWidth = "1024",
            windowOuterHeight = "768",
            documentClientWidth = "1024",
            documentClientHeight = "768",
            screenWidth = "1080",
            screenHeight = "2400",
            devicePixelRatio = "2.75",
            navigatorUserAgent = "SampleNavigatorUA/1.0",
            userAgentDataMobile = "false",
            navigatorMaxTouchPoints = "5",
            viewportMetaContent = "width=1024",
            currentWebViewUrl = "https://example.com/test",
            desktopModeState = "DESKTOP MODE"
        )

        val clipboardText = sample.formatForClipboard()

        assertTrue(clipboardText.contains("1. webView.settings.userAgentString:"))
        assertTrue(clipboardText.contains("2. window.innerWidth: 1024"))
        assertTrue(clipboardText.contains("3. window.innerHeight: 768"))
        assertTrue(clipboardText.contains("4. window.outerWidth: 1024"))
        assertTrue(clipboardText.contains("5. window.outerHeight: 768"))
        assertTrue(clipboardText.contains("6. document.documentElement.clientWidth: 1024"))
        assertTrue(clipboardText.contains("7. document.documentElement.clientHeight: 768"))
        assertTrue(clipboardText.contains("8. screen.width: 1080"))
        assertTrue(clipboardText.contains("9. screen.height: 2400"))
        assertTrue(clipboardText.contains("10. window.devicePixelRatio: 2.75"))
        assertTrue(clipboardText.contains("11. navigator.userAgent:"))
        assertTrue(clipboardText.contains("12. navigator.userAgentData.mobile: false"))
        assertTrue(clipboardText.contains("13. navigator.maxTouchPoints: 5"))
        assertTrue(clipboardText.contains("14. document.querySelector('meta[name=\"viewport\"]'):"))
        assertTrue(clipboardText.contains("15. Current WebView URL: https://example.com/test"))
        assertTrue(clipboardText.contains("16. Current Desktop Mode state: DESKTOP MODE"))
    }

    @Test
    fun test2_twoClearlyLabeledStatesMobileAndDesktop() {
        val mobileSample = DiagnosticData(desktopModeState = "MOBILE MODE")
        assertEquals("MOBILE MODE", mobileSample.desktopModeState)

        val desktopSample = DiagnosticData(desktopModeState = "DESKTOP MODE")
        assertEquals("DESKTOP MODE", desktopSample.desktopModeState)
    }

    @Test
    fun test3_viewModelOpensAndClosesDiagnostic() {
        val viewModel = BrowserViewModel(context)
        assertFalse(viewModel.uiState.value.isDiagnosticOpen)

        viewModel.openDiagnostic()
        assertTrue(viewModel.uiState.value.isDiagnosticOpen)
        assertFalse("Opening diagnostic should dismiss menu", viewModel.uiState.value.isMenuOpen)

        viewModel.closeDiagnostic()
        assertFalse(viewModel.uiState.value.isDiagnosticOpen)
    }

    @Test
    fun test4_collectLiveWebViewDiagnosticsHandlesNullWebViewGracefully() {
        var receivedData: DiagnosticData? = null
        collectLiveWebViewDiagnostics(
            webView = null,
            isDesktopModeEnabled = true
        ) { data ->
            receivedData = data
        }

        assertEquals("DESKTOP MODE", receivedData?.desktopModeState)
        assertEquals("Error: WebView is null", receivedData?.windowInnerWidth)

        // Test with mobile mode
        var mobileData: DiagnosticData? = null
        collectLiveWebViewDiagnostics(
            webView = null,
            isDesktopModeEnabled = false
        ) { data ->
            mobileData = data
        }

        assertEquals("MOBILE MODE", mobileData?.desktopModeState)
    }
}
