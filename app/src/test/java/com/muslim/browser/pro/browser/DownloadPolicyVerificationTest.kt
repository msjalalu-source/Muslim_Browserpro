package com.muslim.browser.pro.browser

import android.content.Context
import androidx.core.content.FileProvider
import androidx.test.core.app.ApplicationProvider
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DownloadPolicyVerificationTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun testApkFileNameCasingPreserved() {
        // Filename casing must be preserved
        val mixedCaseName = "MyApp-v1.0.0-Release.apk"
        val extracted = DownloadPolicy.extractFileName(
            "https://github.com/msjalalu-source/app/releases/download/v1.0/MyApp-v1.0.0-Release.apk",
            null
        )
        assertEquals(mixedCaseName, extracted)

        // Content disposition with mixed case
        val fromDisp = DownloadPolicy.extractFileName(
            "https://github.com/msjalalu-source/app/releases/download/v1.0/download",
            "attachment; filename='CustomBuild-V2.APK'"
        )
        assertEquals("CustomBuild-V2.APK", fromDisp)
    }

    @Test
    fun testCaseInsensitiveApkDetection() {
        assertTrue(DownloadPolicy.isApk("https://example.com/app.apk"))
        assertTrue(DownloadPolicy.isApk("https://example.com/app.APK"))
        assertTrue(DownloadPolicy.isApk("https://example.com/app.Apk"))
        assertTrue(DownloadPolicy.isApk("https://example.com/download", mimeType = "application/vnd.android.package-archive"))
        assertTrue(DownloadPolicy.isApk("https://example.com/download", contentDisposition = "filename=app.APK"))
    }

    @Test
    fun testGitHubApkSourceRestrictionsEnforced() {
        // Allowed msjalalu-source GitHub repo
        assertTrue(DownloadPolicy.isAllowedGitHubApkUrl("https://github.com/msjalalu-source/repo/releases/download/v1.0/app.apk"))

        // Blocked untrusted origins
        assertFalse(DownloadPolicy.isAllowedGitHubApkUrl("https://github.com/untrusted-user/repo/releases/download/v1.0/app.apk"))
        assertFalse(DownloadPolicy.isAllowedGitHubApkUrl("https://malicious-site.com/app.apk"))

        val blockedResult = DownloadPolicy.evaluate("https://malicious-site.com/app.apk", null, null)
        assertTrue(blockedResult is DownloadPolicy.Result.Blocked)
    }

    @Test
    fun testFileProviderAuthorityAndPaths() {
        val authority = "${context.packageName}.fileprovider"

        // Verify FileProvider can generate URI for a simulated APK file in cache or files dir
        val dummyFile = File(context.cacheDir, "test_app.apk")
        dummyFile.writeBytes(byteArrayOf(0x50, 0x4B, 0x03, 0x04)) // ZIP magic bytes
        val uri = FileProvider.getUriForFile(context, authority, dummyFile)
        assertTrue(uri.toString().startsWith("content://"))
        assertTrue(uri.toString().endsWith("test_app.apk"))
        dummyFile.delete()
    }
}
