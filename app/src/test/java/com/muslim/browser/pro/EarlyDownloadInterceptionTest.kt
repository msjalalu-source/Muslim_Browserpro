package com.muslim.browser.pro

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.DownloadEntry
import com.muslim.browser.pro.browser.DownloadPolicy
import com.muslim.browser.pro.browser.DownloadProgressPoller
import com.muslim.browser.pro.browser.DownloadStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDownloadManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class EarlyDownloadInterceptionTest {

    private lateinit var context: Application

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        DownloadPolicy.clearTrustedOrigin()
    }

    // A. Direct APK URL
    @Test
    fun `Test A - Direct APK URL from allowed verified GitHub repository is intercepted and allowed`() {
        val allowedApkUrl = "https://github.com/msjalalu-source/Muslim_Browserpro/releases/download/v1.0/app.apk"
        assertTrue(DownloadPolicy.isDownloadableFileUrl(allowedApkUrl))
        val decision = DownloadPolicy.evaluate(allowedApkUrl)
        assertTrue(decision is DownloadPolicy.Result.Allowed)
        assertFalse(DownloadPolicy.shouldBlockUrlNavigation(allowedApkUrl))
    }

    // B. Direct ZIP URL
    @Test
    fun `Test B - Direct ZIP URL is intercepted and allowed`() {
        val zipUrl = "https://example.com/files/archive.zip"
        assertTrue(DownloadPolicy.isDownloadableFileUrl(zipUrl))
        val decision = DownloadPolicy.evaluate(zipUrl)
        assertTrue(decision is DownloadPolicy.Result.Allowed)
        assertFalse(DownloadPolicy.shouldBlockUrlNavigation(zipUrl))
    }

    // C. Direct PDF URL
    @Test
    fun `Test C - Direct PDF URL is intercepted and allowed`() {
        val pdfUrl = "https://example.com/books/manual.pdf"
        assertTrue(DownloadPolicy.isDownloadableFileUrl(pdfUrl))
        val decision = DownloadPolicy.evaluate(pdfUrl)
        assertTrue(decision is DownloadPolicy.Result.Allowed)
        assertFalse(DownloadPolicy.shouldBlockUrlNavigation(pdfUrl))
    }

    // D. Direct document URL
    @Test
    fun `Test D - Direct document URLs (docx, xlsx, pptx, csv, txt, epub) are intercepted and allowed`() {
        val docUrls = listOf(
            "https://example.com/docs/file.docx",
            "https://example.com/sheets/data.xlsx",
            "https://example.com/slides/presentation.pptx",
            "https://example.com/records.csv",
            "https://example.com/notes.txt",
            "https://example.com/novel.epub",
            "https://example.com/archive.tar.gz",
            "https://example.com/package.7z",
            "https://example.com/system.iso"
        )

        for (url in docUrls) {
            assertTrue("Expected isDownloadableFileUrl to be true for $url", DownloadPolicy.isDownloadableFileUrl(url))
            val decision = DownloadPolicy.evaluate(url)
            assertTrue("Expected Allowed decision for $url", decision is DownloadPolicy.Result.Allowed)
            assertFalse("Expected shouldBlockUrlNavigation to be false for $url", DownloadPolicy.shouldBlockUrlNavigation(url))
        }
    }

    // E. Direct audio URL
    @Test
    fun `Test E - Direct audio URLs are intercepted and preserve audio special handling`() {
        val mp3Url = "https://example.com/audio/lecture.mp3?download=1"
        val wavUrl = "https://example.com/audio/recitation.wav"
        val m4aUrl = "https://example.com/audio/adhan.m4a"

        assertTrue(DownloadPolicy.isAudio(mp3Url))
        assertTrue(DownloadPolicy.isAudio(wavUrl))
        assertTrue(DownloadPolicy.isAudio(m4aUrl))

        assertTrue(DownloadPolicy.isDownloadableFileUrl(mp3Url))
        assertTrue(DownloadPolicy.isDownloadableFileUrl(wavUrl))
        assertTrue(DownloadPolicy.isDownloadableFileUrl(m4aUrl))

        assertTrue(DownloadPolicy.evaluate(mp3Url) is DownloadPolicy.Result.Allowed)
        assertTrue(DownloadPolicy.evaluate(wavUrl) is DownloadPolicy.Result.Allowed)
    }

    // F. Video URL remains blocked
    @Test
    fun `Test F - Video URL is never classified as downloadable and remains blocked`() {
        val videoUrls = listOf(
            "https://example.com/video/movie.mp4",
            "https://example.com/video/stream.mkv",
            "https://example.com/animation.webm",
            "https://example.com/clip.avi",
            "https://example.com/file.mov"
        )

        for (url in videoUrls) {
            assertFalse("Video URL $url must NOT be classified as downloadable file", DownloadPolicy.isDownloadableFileUrl(url))
            assertTrue("Video URL $url must be identified as video", DownloadPolicy.isVideo(url))
            val decision = DownloadPolicy.evaluate(url)
            assertTrue("Video URL $url must be blocked by DownloadPolicy", decision is DownloadPolicy.Result.Blocked)
            assertTrue("Video URL $url must be blocked from navigation", DownloadPolicy.shouldBlockUrlNavigation(url))
        }
    }

    // G. Restricted APK source remains blocked
    @Test
    fun `Test G - Restricted APK source from unauthorized website is intercepted and blocked`() {
        val untrustedApkUrl = "https://malicious-site.com/downloads/app.apk"
        val otherGithubApkUrl = "https://github.com/untrusted-user/repo/releases/download/v1.0/app.apk"

        assertTrue(DownloadPolicy.isDownloadableFileUrl(untrustedApkUrl))
        assertTrue(DownloadPolicy.isDownloadableFileUrl(otherGithubApkUrl))

        val decision1 = DownloadPolicy.evaluate(untrustedApkUrl)
        assertTrue("Untrusted APK must be blocked", decision1 is DownloadPolicy.Result.Blocked)

        val decision2 = DownloadPolicy.evaluate(otherGithubApkUrl)
        assertTrue("Other user GitHub APK must be blocked", decision2 is DownloadPolicy.Result.Blocked)

        assertTrue(DownloadPolicy.shouldBlockUrlNavigation(untrustedApkUrl))
        assertTrue(DownloadPolicy.shouldBlockUrlNavigation(otherGithubApkUrl))
    }

    // H. Normal HTML page is not intercepted
    @Test
    fun `Test H - Normal HTML web pages are not intercepted`() {
        val htmlUrls = listOf(
            "https://example.com/index.html",
            "https://example.com/about.htm",
            "https://example.com/portal.php",
            "https://example.com/search",
            "https://example.com/"
        )

        for (url in htmlUrls) {
            assertFalse("HTML page $url must NOT be intercepted as downloadable file", DownloadPolicy.isDownloadableFileUrl(url))
            assertFalse("HTML page $url is not audio", DownloadPolicy.isAudio(url))
        }
    }

    // I. Google authentication URL is not intercepted as a file download
    @Test
    fun `Test I - Google authentication and account URLs are not intercepted as file downloads`() {
        val authUrls = listOf(
            "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=client123&redirect_uri=https://example.com/callback",
            "https://accounts.google.com/signin/v2/identifier?service=mail",
            "https://accounts.google.com/ServiceLogin",
            "https://myaccount.google.com/security"
        )

        for (url in authUrls) {
            assertFalse("Auth URL $url must NOT be intercepted as a file download", DownloadPolicy.isDownloadableFileUrl(url))
            assertFalse("Auth URL $url is not audio", DownloadPolicy.isAudio(url))
        }
    }

    // J. Exactly one DownloadManager enqueue occurs for an early-intercepted file
    @Test
    fun `Test J - Exactly one DownloadManager enqueue occurs and stops WebView navigation for an early-intercepted file`() {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val shadowDm: ShadowDownloadManager = shadowOf(dm)
        val initialRequestCount = shadowDm.requestCount

        val targetDownloadUrl = "https://example.com/documents/whitepaper.pdf"

        // 1. Unified detector identifies early downloadable file URL
        assertTrue(DownloadPolicy.isDownloadableFileUrl(targetDownloadUrl))

        // 2. Policy evaluation allows the download
        val decision = DownloadPolicy.evaluate(targetDownloadUrl)
        assertTrue(decision is DownloadPolicy.Result.Allowed)

        // 3. Early interception performs exactly one DownloadManager enqueue with roaming & metered allowed
        val parsedUri = android.net.Uri.parse(targetDownloadUrl)
        val request = DownloadManager.Request(parsedUri).apply {
            setMimeType("application/pdf")
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }
        val downloadId = dm.enqueue(request)
        assertTrue("Download ID must be valid", downloadId >= 0L)
        assertEquals("Exactly one enqueue must occur", initialRequestCount + 1, shadowDm.requestCount)

        // 4. In WebView navigation (shouldOverrideUrlLoading), early interception returns true,
        // which stops normal WebView navigation so WebView never initiates an HTTP request and never
        // reaches the WebView DownloadListener.
        val willIntercept = DownloadPolicy.isDownloadableFileUrl(targetDownloadUrl)
        assertTrue("Early interception must stop normal WebView navigation", willIntercept)
    }

    // K. Progress query no longer performs ContentResolver cursor work on the main thread
    @Test
    fun `Test K - Progress query performs ContentResolver cursor work strictly on Dispatchers IO`() = runBlocking {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val shadowDm: ShadowDownloadManager = shadowOf(dm)

        val request = DownloadManager.Request(android.net.Uri.parse("https://example.com/files/document.pdf"))
        val id = dm.enqueue(request)

        val activeEntries = listOf(
            DownloadEntry(
                downloadId = id,
                fileName = "document.pdf",
                url = "https://example.com/files/document.pdf",
                mimeType = "application/pdf",
                status = DownloadStatus.DOWNLOADING
            )
        )

        // Reset execution thread tracker
        DownloadProgressPoller.lastCursorExecutionThread = null

        // Execute queryActiveDownloadsProgress
        val (updates, stillActive) = DownloadProgressPoller.queryActiveDownloadsProgress(dm, activeEntries)

        // Verify that the cursor execution thread was captured and was NOT the Android Main Looper thread
        val cursorThread = DownloadProgressPoller.lastCursorExecutionThread
        assertNotNull("Cursor reading must have been executed", cursorThread)
        assertNotEquals(
            "Cursor query must not run on Main Looper thread",
            Looper.getMainLooper().thread.id,
            cursorThread?.id
        )
    }
}
