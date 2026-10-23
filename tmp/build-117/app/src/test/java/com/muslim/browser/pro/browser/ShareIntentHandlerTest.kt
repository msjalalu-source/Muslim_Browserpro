package com.muslim.browser.pro.browser

import android.content.Intent
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShareIntentHandlerTest {

    @Test
    fun testSendIntentWithDirectHttpsUrl() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "https://en.wikipedia.org/wiki/Islam")
        }

        val extracted = ShareIntentHandler.extractContent(intent)
        assertEquals("https://en.wikipedia.org/wiki/Islam", extracted)
        assertTrue(ShareIntentHandler.isDirectHttpUrl(extracted!!))
    }

    @Test
    fun testSendIntentWithDirectHttpUrl() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "http://example.com/articles/daily")
        }

        val extracted = ShareIntentHandler.extractContent(intent)
        assertEquals("http://example.com/articles/daily", extracted)
        assertTrue(ShareIntentHandler.isDirectHttpUrl(extracted!!))
    }

    @Test
    fun testSendIntentWithEmbeddedUrlInNotes() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out this article: https://islamqa.info/en/answers/1 for guidance."
            )
        }

        val extracted = ShareIntentHandler.extractContent(intent)
        // Clean trailing punctuation removed
        assertEquals("https://islamqa.info/en/answers/1", extracted)
        assertTrue(ShareIntentHandler.isDirectHttpUrl(extracted!!))
    }

    @Test
    fun testSendIntentWithUrlInParentheses() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Great resource (https://quran.com/1)")
        }

        val extracted = ShareIntentHandler.extractContent(intent)
        assertEquals("https://quran.com/1", extracted)
        assertTrue(ShareIntentHandler.isDirectHttpUrl(extracted!!))
    }

    @Test
    fun testSendIntentWithStandaloneDomain() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "islamweb.net")
        }

        val extracted = ShareIntentHandler.extractContent(intent)
        assertEquals("islamweb.net", extracted)
    }

    @Test
    fun testSendIntentWithOrdinarySearchText() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "halal travel destinations in Europe")
        }

        val extracted = ShareIntentHandler.extractContent(intent)
        // Non-URL plain text is forwarded as a safe search query
        assertEquals("halal travel destinations in Europe", extracted)
        assertFalse(ShareIntentHandler.isDirectHttpUrl(extracted!!))
    }

    @Test
    fun testSendIntentWithNullOrEmptyExtraText() {
        val nullIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
        }
        assertNull(ShareIntentHandler.extractContent(nullIntent))

        val emptyIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "")
        }
        assertNull(ShareIntentHandler.extractContent(emptyIntent))

        val whitespaceIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "   \n\t  ")
        }
        assertNull(ShareIntentHandler.extractContent(whitespaceIntent))
    }

    @Test
    fun testSendIntentWithUnsupportedMimeTypeRejected() {
        val imageIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_TEXT, "https://example.com/photo.png")
        }
        assertNull(ShareIntentHandler.extractContent(imageIntent))

        val videoIntent = Intent(Intent.ACTION_SEND).apply {
            type = "video/mp4"
            putExtra(Intent.EXTRA_TEXT, "https://example.com/video.mp4")
        }
        assertNull(ShareIntentHandler.extractContent(videoIntent))
    }

    @Test
    fun testViewIntentWithValidWebUrls() {
        val httpsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://sunnah.com/bukhari/1")
        }
        assertEquals("https://sunnah.com/bukhari/1", ShareIntentHandler.extractContent(httpsIntent))

        val httpIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("http://example.org/test")
        }
        assertEquals("http://example.org/test", ShareIntentHandler.extractContent(httpIntent))
    }

    @Test
    fun testViewIntentWithNonWebSchemesRejected() {
        val fileIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("file:///sdcard/test.html")
        }
        assertNull(ShareIntentHandler.extractContent(fileIntent))

        val contentIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("content://downloads/my_downloads/1")
        }
        assertNull(ShareIntentHandler.extractContent(contentIntent))

        val jsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("javascript:alert(1)")
        }
        assertNull(ShareIntentHandler.extractContent(jsIntent))
    }

    @Test
    fun testExcessivelyLongTextInputSafelyBounded() {
        val longPadding = "a".repeat(12000)
        val candidate = "$longPadding https://example.com/safe"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, candidate)
        }

        // Must not throw OOM or StackOverflowError
        val extracted = ShareIntentHandler.extractContent(intent)
        assertNotNull(extracted)
        assertTrue(extracted!!.length <= ShareIntentHandler.MAX_INPUT_LENGTH)
    }

    @Test
    fun testDuplicateProcessingStateTracking() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "https://example.com")
        }

        assertFalse(ShareIntentHandler.isAlreadyProcessed(intent))
        ShareIntentHandler.markAsProcessed(intent)
        assertTrue(ShareIntentHandler.isAlreadyProcessed(intent))
    }

    @Test
    fun testActionMainNotTreatedAsShareTarget() {
        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        assertNull(ShareIntentHandler.extractContent(mainIntent))
    }
}
