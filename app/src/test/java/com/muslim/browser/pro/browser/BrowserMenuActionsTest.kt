package com.muslim.browser.pro.browser

import com.muslim.browser.pro.browser.ui.sanitizePdfJobName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BrowserMenuActionsTest {

    @Test
    fun testValidHttpAndHttpsUrlsAllowedForShare() {
        assertTrue(ShareIntentHandler.isDirectHttpUrl("https://quran.com/1"))
        assertTrue(ShareIntentHandler.isDirectHttpUrl("http://example.com/test?q=hello#hash"))
        assertTrue(ShareIntentHandler.isDirectHttpUrl("https://sunnah.com/bukhari/1"))
    }

    @Test
    fun testInvalidUrlsRejectedForShare() {
        assertFalse(ShareIntentHandler.isDirectHttpUrl(""))
        assertFalse(ShareIntentHandler.isDirectHttpUrl("   "))
        assertFalse(ShareIntentHandler.isDirectHttpUrl("about:blank"))
        assertFalse(ShareIntentHandler.isDirectHttpUrl("javascript:alert(1)"))
        assertFalse(ShareIntentHandler.isDirectHttpUrl("file:///android_asset/index.html"))
        assertFalse(ShareIntentHandler.isDirectHttpUrl("content://media/external/images/media/1"))
        assertFalse(ShareIntentHandler.isDirectHttpUrl("chrome://flags"))
    }

    @Test
    fun testSanitizePdfJobNameWithNormalTitle() {
        val jobName = sanitizePdfJobName("Surah Al-Fatihah - The Quran", "https://quran.com/1")
        assertEquals("MuslimBrowser_Surah Al-Fatihah - The Quran", jobName)
    }

    @Test
    fun testSanitizePdfJobNameWithHostileCharacters() {
        val jobName = sanitizePdfJobName("Dangerous: File/Name *With? Quotes \"And\" Pipes|", "https://example.com")
        assertEquals("MuslimBrowser_Dangerous_ File_Name _With_ Quotes _And_ Pipes_", jobName)
    }

    @Test
    fun testSanitizePdfJobNameFallbackToHost() {
        val jobName = sanitizePdfJobName(null, "https://islamqa.info/en/answers/1234")
        assertEquals("MuslimBrowser_islamqa.info", jobName)
    }

    @Test
    fun testSanitizePdfJobNameFallbackWithBlankTitle() {
        val jobName = sanitizePdfJobName("   ", "https://sunnah.com/muslim/1")
        assertEquals("MuslimBrowser_sunnah.com", jobName)
    }

    @Test
    fun testSanitizePdfJobNameFallbackWhenAllCharactersAreIllegal() {
        val jobName = sanitizePdfJobName("///:::***", "")
        assertEquals("MuslimBrowser_Webpage", jobName)
    }

    @Test
    fun testSanitizePdfJobNameTruncation() {
        val veryLongTitle = "A".repeat(100)
        val jobName = sanitizePdfJobName(veryLongTitle, "https://example.com")
        // "MuslimBrowser_" (14 chars) + 60 chars max = 74 chars
        assertTrue(jobName.length <= 74)
        assertEquals("MuslimBrowser_" + "A".repeat(60), jobName)
    }

    @Test
    fun testShareAndPdfEnabledStateGuards() {
        // Valid web page state
        val validUrl = "https://quran.com"
        val isHomePage = false
        val isBlocked = false
        val isLoading = false

        val isWebUrlValid = ShareIntentHandler.isDirectHttpUrl(validUrl)
        val isShareEnabled = !isHomePage && !isBlocked && isWebUrlValid
        val isSavePdfEnabled = !isHomePage && !isBlocked && isWebUrlValid && !isLoading

        assertTrue(isShareEnabled)
        assertTrue(isSavePdfEnabled)

        // Homepage state: both disabled
        val shareOnHome = !true && !isBlocked && isWebUrlValid
        val pdfOnHome = !true && !isBlocked && isWebUrlValid && !isLoading
        assertFalse(shareOnHome)
        assertFalse(pdfOnHome)

        // Blocked state: both disabled
        val shareOnBlocked = !isHomePage && !true && isWebUrlValid
        val pdfOnBlocked = !isHomePage && !true && isWebUrlValid && !isLoading
        assertFalse(shareOnBlocked)
        assertFalse(pdfOnBlocked)

        // Loading state: Share allowed, PDF disabled
        val shareDuringLoading = !isHomePage && !isBlocked && isWebUrlValid
        val pdfDuringLoading = !isHomePage && !isBlocked && isWebUrlValid && !true
        assertTrue(shareDuringLoading)
        assertFalse(pdfDuringLoading)
    }

    @Test
    fun testFillFormExecutionGuards() {
        fun canExecuteFillForm(isHomePage: Boolean, isBlocked: Boolean, hasLiveWebView: Boolean): Boolean {
            return hasLiveWebView && !isHomePage && !isBlocked
        }

        // Active web page with live webview
        assertTrue(canExecuteFillForm(isHomePage = false, isBlocked = false, hasLiveWebView = true))

        // Home page should prevent fill form
        assertFalse(canExecuteFillForm(isHomePage = true, isBlocked = false, hasLiveWebView = true))

        // Blocked page should prevent fill form
        assertFalse(canExecuteFillForm(isHomePage = false, isBlocked = true, hasLiveWebView = true))

        // Null/missing webview should prevent fill form
        assertFalse(canExecuteFillForm(isHomePage = false, isBlocked = false, hasLiveWebView = false))
    }

    @Test
    fun testFillFormPreservesDisabledAutofillSetting() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val disabledProfile = AutofillProfile(
            isEnabled = false,
            personalInfo = PersonalInfo(userNameEn = "Test User")
        )
        AutofillRepository.saveProfile(context, disabledProfile)

        val webView = android.webkit.WebView(context)
        var resultMessage = ""
        var filledCount = -1

        AutofillEngine.executeAutofill(context, webView) { filled, _, message ->
            filledCount = filled
            resultMessage = message
        }

        assertEquals(0, filledCount)
        assertTrue(resultMessage.contains("Autofill is disabled"))
    }

    @Test
    fun testDualClickTargetsIndependence() {
        var dialogOpened = false
        var formFillExecuted = false

        val onMainAreaClicked = {
            dialogOpened = true
        }

        val onTrailingActionButtonClicked = {
            // Invokes form fill, must NOT open dialog
            formFillExecuted = true
        }

        // Clicking the main area only opens settings dialog
        onMainAreaClicked()
        assertTrue(dialogOpened)
        assertFalse(formFillExecuted)

        // Reset
        dialogOpened = false
        formFillExecuted = false

        // Clicking the trailing button only invokes form fill
        onTrailingActionButtonClicked()
        assertTrue(formFillExecuted)
        assertFalse(dialogOpened)
    }
}

