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
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(packageName = "com.muslim.browser.pro")
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
        // Allowed GitHub sources
        assertTrue(DownloadPolicy.isAllowedGitHubApkUrl("https://github.com/user/repo/releases/download/v1.0/app.apk"))
        assertTrue(DownloadPolicy.isAllowedGitHubApkUrl("https://objects.githubusercontent.com/github-production-release-asset/app.apk"))

        // Blocked non-GitHub origins
        assertFalse(DownloadPolicy.isAllowedGitHubApkUrl("https://malicious-site.com/app.apk"))
        assertFalse(DownloadPolicy.isAllowedGitHubApkUrl("https://apkpure.com/app.apk"))

        val blockedResult = DownloadPolicy.evaluate("https://malicious-site.com/app.apk", null, null)
        assertTrue(blockedResult is DownloadPolicy.Result.Blocked)
    }

    @Test
    fun testFileProviderAuthorityAndPaths() {
        val file = File("src/main/res/xml/file_paths.xml").takeIf { it.exists() }
            ?: File("app/src/main/res/xml/file_paths.xml")
        assertTrue(file.exists())
        val content = file.readText()
        assertTrue(content.contains("<external-path"))
        assertTrue(content.contains("<files-path"))
        assertTrue(content.contains("<cache-path"))
        assertTrue(content.contains("<external-files-path"))
        assertTrue(content.contains("<external-cache-path"))
    }

    @Test
    fun testReleaseBuildSigningConfigConfigured() {
        val buildGradle = File("build.gradle.kts").takeIf { it.exists() }
            ?: File("app/build.gradle.kts")
        assertTrue("app/build.gradle.kts should exist", buildGradle.exists())
        val content = buildGradle.readText()
        assertTrue(content.contains("signingConfig = signingConfigs.findByName(\"release\") ?: signingConfigs.getByName(\"debugConfig\")"))
        assertTrue(content.contains("create(\"debugConfig\")"))
    }

    @Test
    fun testGitHubWorkflowVerifiesApkSignature() {
        val workflow = File(".github/workflows/build-apk.yml")
        if (workflow.exists()) {
            val content = workflow.readText()
            assertTrue(content.contains("apksigner verify"))
        }
    }

    @Test
    fun testZipDetectionWithMimeTypesAndExtensions() {
        // Direct extension
        assertTrue(DownloadPolicy.isZip("https://example.com/archive.zip"))
        assertTrue(DownloadPolicy.isZip("https://example.com/ARCHIVE.ZIP"))

        // Common ZIP MIME types
        assertTrue(DownloadPolicy.isZip("https://example.com/download", mimeType = "application/zip"))
        assertTrue(DownloadPolicy.isZip("https://example.com/download", mimeType = "application/x-zip-compressed"))
        assertTrue(DownloadPolicy.isZip("https://example.com/download", mimeType = "application/x-zip"))

        // Content-Disposition with .zip filename
        assertTrue(DownloadPolicy.isZip("https://example.com/download?id=42", contentDisposition = "attachment; filename=\"data_export.zip\""))
        assertTrue(DownloadPolicy.isZip("https://example.com/download?id=42", contentDisposition = "attachment; filename*=UTF-8''data_export.zip"))

        // Query parameter filename
        assertTrue(DownloadPolicy.isZip("https://example.com/download?file=backup.zip"))

        // isDownloadableFileUrl
        assertTrue(DownloadPolicy.isDownloadableFileUrl("https://example.com/archive.zip"))
        assertTrue(DownloadPolicy.isDownloadableFileUrl("https://example.com/download", mimeType = "application/zip"))
        assertTrue(DownloadPolicy.isDownloadableFileUrl("https://example.com/download", contentDisposition = "attachment; filename=project.zip"))

        // DownloadPolicy.evaluate allows legitimate zip
        val result = DownloadPolicy.evaluate("https://example.com/archive.zip", "application/zip", null)
        assertTrue(result is DownloadPolicy.Result.Allowed)
    }

    @Test
    fun testDownloadPolicyAllowsZipAndPreservesRestrictions() {
        // ZIP allowed
        assertTrue(DownloadPolicy.evaluate("https://example.com/archive.zip", null, null) is DownloadPolicy.Result.Allowed)
        assertTrue(DownloadPolicy.evaluate("https://example.com/dl", "application/zip", null) is DownloadPolicy.Result.Allowed)
        assertTrue(DownloadPolicy.evaluate("https://example.com/dl", "application/x-zip-compressed", null) is DownloadPolicy.Result.Allowed)
        assertTrue(DownloadPolicy.evaluate("https://example.com/dl?id=1", null, "attachment; filename=export.zip") is DownloadPolicy.Result.Allowed)

        // Allowed document & media types preserved
        assertTrue(DownloadPolicy.evaluate("https://example.com/doc.pdf", "application/pdf", null) is DownloadPolicy.Result.Allowed)
        assertTrue(DownloadPolicy.evaluate("https://example.com/audio.mp3", "audio/mpeg", null) is DownloadPolicy.Result.Allowed)

        // Restricted types remain blocked
        assertTrue(DownloadPolicy.evaluate("https://example.com/video.mp4", "video/mp4", null) is DownloadPolicy.Result.Blocked)
        assertTrue(DownloadPolicy.evaluate("https://malicious.com/app.apk", null, null) is DownloadPolicy.Result.Blocked)
    }

    @Test
    fun testNavigationControllerEvaluatesZipDownload() {
        val decision = NavigationController.evaluate("https://example.com/files/archive.zip", emptySet())
        assertTrue(decision is NavigationDecision.Download)
        assertEquals("https://example.com/files/archive.zip", (decision as NavigationDecision.Download).url)
    }

    @Test
    fun testBlobDownloadBridgeCallbacks() {
        var receivedBase64 = ""
        var receivedMime = ""
        var receivedError = ""

        val bridge = BlobDownloadBridge(
            onReadyCallback = { b64, mime ->
                receivedBase64 = b64
                receivedMime = mime
            },
            onErrorCallback = { err ->
                receivedError = err
            }
        )

        bridge.onBlobReady("dGVzdA==", "application/zip")
        assertEquals("dGVzdA==", receivedBase64)
        assertEquals("application/zip", receivedMime)

        bridge.onBlobError("TOO_LARGE")
        assertEquals("TOO_LARGE", receivedError)
    }
}

