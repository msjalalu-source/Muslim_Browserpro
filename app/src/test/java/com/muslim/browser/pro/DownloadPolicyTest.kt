package com.muslim.browser.pro

import com.muslim.browser.pro.browser.DownloadPolicy
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
class DownloadPolicyTest {

    @Before
    fun setUp() {
        DownloadPolicy.clearTrustedOrigin()
    }

    // 1. APK TESTS: github.com/msjalalu-source ONLY
    @Test
    fun `allowed APK from msjalalu-source on github dot com`() {
        val url = "https://github.com/msjalalu-source/Muslim_Browserpro/releases/download/v1.0/app.apk"
        val result = DownloadPolicy.evaluate(url, "application/vnd.android.package-archive", null)
        assertTrue(result is DownloadPolicy.Result.Allowed)
        assertFalse(DownloadPolicy.shouldBlockUrlNavigation(url))
    }

    @Test
    fun `APK from another GitHub owner is BLOCKED`() {
        val url = "https://github.com/other-dev/repo/releases/download/v1.0/app.apk"
        val result = DownloadPolicy.evaluate(url, "application/vnd.android.package-archive", null)
        assertTrue(result is DownloadPolicy.Result.Blocked)
        assertTrue(DownloadPolicy.shouldBlockUrlNavigation(url))
    }

    @Test
    fun `APK from another website is BLOCKED`() {
        val url = "https://malicious-site.com/downloads/app.apk"
        val result = DownloadPolicy.evaluate(url, "application/vnd.android.package-archive", null)
        assertTrue(result is DownloadPolicy.Result.Blocked)
        assertTrue(DownloadPolicy.shouldBlockUrlNavigation(url))
    }

    @Test
    fun `APK with uppercase extension is correctly handled and blocked if non-github`() {
        val url = "https://example.com/files/APP.APK"
        val result = DownloadPolicy.evaluate(url, null, null)
        assertTrue(result is DownloadPolicy.Result.Blocked)
        assertTrue(DownloadPolicy.shouldBlockUrlNavigation(url))

        val allowedUpperUrl = "https://github.com/msjalalu-source/repo/releases/download/v1.0/APP.APK"
        val allowedResult = DownloadPolicy.evaluate(allowedUpperUrl, null, null)
        assertTrue(allowedResult is DownloadPolicy.Result.Allowed)
    }

    @Test
    fun `XAPK and APKS follow same source restriction`() {
        val badXapk = "https://example.com/app.xapk"
        assertTrue(DownloadPolicy.evaluate(badXapk) is DownloadPolicy.Result.Blocked)
        assertTrue(DownloadPolicy.shouldBlockUrlNavigation(badXapk))

        val badApks = "https://example.com/app.apks"
        assertTrue(DownloadPolicy.evaluate(badApks) is DownloadPolicy.Result.Blocked)
        assertTrue(DownloadPolicy.shouldBlockUrlNavigation(badApks))

        val goodXapk = "https://github.com/msjalalu-source/repo/releases/download/v1/bundle.xapk"
        assertTrue(DownloadPolicy.evaluate(goodXapk) is DownloadPolicy.Result.Allowed)

        val goodApks = "https://github.com/msjalalu-source/repo/releases/download/v1/bundle.apks"
        assertTrue(DownloadPolicy.evaluate(goodApks) is DownloadPolicy.Result.Allowed)
    }

    // 2. VIDEO TESTS: ALL VIDEOS BLOCKED
    @Test
    fun `MP4 is BLOCKED across all sources`() {
        val url = "https://example.com/video/movie.mp4"
        assertTrue(DownloadPolicy.evaluate(url) is DownloadPolicy.Result.Blocked)
        assertTrue(DownloadPolicy.shouldBlockUrlNavigation(url))
    }

    @Test
    fun `MKV is BLOCKED across all sources`() {
        val url = "https://cdn.site.com/media/clip.mkv"
        assertTrue(DownloadPolicy.evaluate(url) is DownloadPolicy.Result.Blocked)
        assertTrue(DownloadPolicy.shouldBlockUrlNavigation(url))
    }

    @Test
    fun `WEBM is BLOCKED across all sources`() {
        val url = "https://example.com/animation.webm"
        assertTrue(DownloadPolicy.evaluate(url) is DownloadPolicy.Result.Blocked)
        assertTrue(DownloadPolicy.shouldBlockUrlNavigation(url))
    }

    @Test
    fun `video MIME type is BLOCKED regardless of extension or unknown URL`() {
        val url = "https://example.com/stream/download?id=123"
        val result = DownloadPolicy.evaluate(url, mimeType = "video/mp4", null)
        assertTrue(result is DownloadPolicy.Result.Blocked)
    }

    // 3. ALL OTHER FILE TYPES ALLOWED BY DEFAULT
    @Test
    fun `MP3 audio is ALLOWED`() {
        val url = "https://example.com/audio/quran.mp3"
        assertTrue(DownloadPolicy.evaluate(url, "audio/mpeg", null) is DownloadPolicy.Result.Allowed)
        assertFalse(DownloadPolicy.shouldBlockUrlNavigation(url))
    }

    @Test
    fun `JPG and PNG images are ALLOWED`() {
        val jpg = "https://example.com/images/photo.jpg"
        assertTrue(DownloadPolicy.evaluate(jpg, "image/jpeg", null) is DownloadPolicy.Result.Allowed)
        assertFalse(DownloadPolicy.shouldBlockUrlNavigation(jpg))

        val png = "https://example.com/images/icon.png"
        assertTrue(DownloadPolicy.evaluate(png, "image/png", null) is DownloadPolicy.Result.Allowed)
        assertFalse(DownloadPolicy.shouldBlockUrlNavigation(png))
    }

    @Test
    fun `PDF documents are ALLOWED`() {
        val url = "https://example.com/books/manual.pdf"
        assertTrue(DownloadPolicy.evaluate(url, "application/pdf", null) is DownloadPolicy.Result.Allowed)
        assertFalse(DownloadPolicy.shouldBlockUrlNavigation(url))
    }

    @Test
    fun `ZIP archives are ALLOWED`() {
        val url = "https://example.com/files/archive.zip"
        assertTrue(DownloadPolicy.evaluate(url, "application/zip", null) is DownloadPolicy.Result.Allowed)
        assertFalse(DownloadPolicy.shouldBlockUrlNavigation(url))
    }

    @Test
    fun `DOC and DOCX office files are ALLOWED`() {
        val doc = "https://example.com/docs/file.doc"
        assertTrue(DownloadPolicy.evaluate(doc) is DownloadPolicy.Result.Allowed)

        val docx = "https://example.com/docs/file.docx"
        assertTrue(DownloadPolicy.evaluate(docx) is DownloadPolicy.Result.Allowed)
        assertFalse(DownloadPolicy.shouldBlockUrlNavigation(docx))
    }

    @Test
    fun `XLS and XLSX spreadsheets are ALLOWED`() {
        val xls = "https://example.com/sheets/data.xls"
        assertTrue(DownloadPolicy.evaluate(xls) is DownloadPolicy.Result.Allowed)

        val xlsx = "https://example.com/sheets/data.xlsx"
        assertTrue(DownloadPolicy.evaluate(xlsx) is DownloadPolicy.Result.Allowed)
    }

    @Test
    fun `PPT and PPTX presentations are ALLOWED`() {
        val ppt = "https://example.com/slides/presentation.ppt"
        assertTrue(DownloadPolicy.evaluate(ppt) is DownloadPolicy.Result.Allowed)

        val pptx = "https://example.com/slides/presentation.pptx"
        assertTrue(DownloadPolicy.evaluate(pptx) is DownloadPolicy.Result.Allowed)
    }

    @Test
    fun `TXT, CSV, JSON, and XML data files are ALLOWED`() {
        val txt = "https://example.com/notes.txt"
        assertTrue(DownloadPolicy.evaluate(txt) is DownloadPolicy.Result.Allowed)

        val csv = "https://example.com/records.csv"
        assertTrue(DownloadPolicy.evaluate(csv) is DownloadPolicy.Result.Allowed)

        val json = "https://example.com/data.json"
        assertTrue(DownloadPolicy.evaluate(json) is DownloadPolicy.Result.Allowed)

        val xml = "https://example.com/feed.xml"
        assertTrue(DownloadPolicy.evaluate(xml) is DownloadPolicy.Result.Allowed)
    }

    @Test
    fun `RAR and 7Z archives are ALLOWED`() {
        val rar = "https://example.com/archive.rar"
        assertTrue(DownloadPolicy.evaluate(rar) is DownloadPolicy.Result.Allowed)

        val sz = "https://example.com/archive.7z"
        assertTrue(DownloadPolicy.evaluate(sz) is DownloadPolicy.Result.Allowed)
    }

    @Test
    fun `unknown file extensions are ALLOWED by default`() {
        val unknown = "https://example.com/custom/binary.unknownext"
        assertTrue(DownloadPolicy.evaluate(unknown) is DownloadPolicy.Result.Allowed)
        assertFalse(DownloadPolicy.shouldBlockUrlNavigation(unknown))

        val dat = "https://example.com/blob.dat"
        assertTrue(DownloadPolicy.evaluate(dat) is DownloadPolicy.Result.Allowed)
    }

    // 4. URL DETAILS: Query parameters & fragments
    @Test
    fun `URL with query parameters and fragments handled correctly`() {
        val videoWithQuery = "https://example.com/video.mp4?token=123&quality=high#timestamp=45"
        assertTrue(DownloadPolicy.evaluate(videoWithQuery) is DownloadPolicy.Result.Blocked)
        assertTrue(DownloadPolicy.shouldBlockUrlNavigation(videoWithQuery))

        val docWithQuery = "https://example.com/sheet.xlsx?auth=xyz#sheet1"
        assertTrue(DownloadPolicy.evaluate(docWithQuery) is DownloadPolicy.Result.Allowed)
        assertFalse(DownloadPolicy.shouldBlockUrlNavigation(docWithQuery))
    }

    // 5. CONTENT-DISPOSITION FILENAME CLASSIFICATION
    @Test
    fun `Content-Disposition filename classifies download correctly`() {
        val opaqueUrl = "https://example.com/download/asset?id=999"
        val videoDisposition = "attachment; filename=\"teaser.mov\""
        assertTrue(DownloadPolicy.evaluate(opaqueUrl, null, videoDisposition) is DownloadPolicy.Result.Blocked)

        val docDisposition = "attachment; filename=\"report.pdf\""
        assertTrue(DownloadPolicy.evaluate(opaqueUrl, null, docDisposition) is DownloadPolicy.Result.Allowed)
    }

    // 6. CONFLICTING MIME TYPE VS EXTENSION
    @Test
    fun `conflicting MIME type vs extension cases prioritize safety`() {
        // Safe-looking extension but video MIME -> must BLOCK
        val disguisedVideo = "https://example.com/download/document.pdf"
        assertTrue(DownloadPolicy.evaluate(disguisedVideo, "video/mp4", null) is DownloadPolicy.Result.Blocked)

        // Safe-looking extension but APK MIME -> must BLOCK unless from msjalalu-source
        val disguisedApk = "https://example.com/download/document.pdf"
        assertTrue(DownloadPolicy.evaluate(disguisedApk, "application/vnd.android.package-archive", null) is DownloadPolicy.Result.Blocked)

        // Video extension with generic octet-stream MIME -> must BLOCK
        val videoOctet = "https://example.com/files/film.mkv"
        assertTrue(DownloadPolicy.evaluate(videoOctet, "application/octet-stream", null) is DownloadPolicy.Result.Blocked)
    }
}
