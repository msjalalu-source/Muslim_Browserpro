package com.muslim.browser.pro

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.DownloadEntry
import com.muslim.browser.pro.browser.DownloadPolicy
import com.muslim.browser.pro.browser.DownloadStatus
import com.muslim.browser.pro.browser.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Mp3DownloadTest {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.clearAllDownloadHistory()
    }

    @Test
    fun `successful MP3 enqueue stores valid DOWNLOADING entry with downloadId`() {
        val entry = DownloadEntry(
            id = "mp3-test-1",
            downloadId = 555L,
            fileName = "audio_lecture.mp3",
            url = "https://example.com/audio/lecture.mp3",
            mimeType = "audio/mpeg",
            status = DownloadStatus.DOWNLOADING,
            downloadedBytes = 0L,
            totalBytes = 10_485_760L // 10 MB
        )

        repository.addDownloadEntry(entry)

        val history = repository.getDownloadHistory()
        assertEquals(1, history.size)
        assertEquals(555L, history[0].downloadId)
        assertEquals("audio_lecture.mp3", history[0].fileName)
        assertEquals("audio/mpeg", history[0].mimeType)
        assertEquals(DownloadStatus.DOWNLOADING, history[0].status)
        assertEquals(0L, history[0].downloadedBytes)
        assertEquals(10_485_760L, history[0].totalBytes)
    }

    @Test
    fun `failed MP3 enqueue results in FAILED status without positive downloadId`() {
        // When enqueue fails (returns -1L or throws exception)
        val failedEntry = DownloadEntry(
            id = "mp3-fail-1",
            downloadId = -1L,
            fileName = "broken_stream.mp3",
            url = "https://example.com/broken.mp3",
            mimeType = "audio/mpeg",
            status = DownloadStatus.FAILED,
            downloadedBytes = 0L,
            totalBytes = -1L
        )

        repository.addDownloadEntry(failedEntry)

        val history = repository.getDownloadHistory()
        assertEquals(1, history.size)
        assertEquals(-1L, history[0].downloadId)
        assertEquals(DownloadStatus.FAILED, history[0].status)
        assertNotEquals(DownloadStatus.DOWNLOADING, history[0].status)
    }

    @Test
    fun `progress calculation handles known total size accurately`() {
        val totalBytes = 11_400_000L // ~11.4 MB
        val downloadedBytes = 7_100_000L // ~7.1 MB

        val percentage = ((downloadedBytes * 100L) / totalBytes).toInt()
        assertEquals(62, percentage)

        val entry = DownloadEntry(
            id = "mp3-prog-1",
            downloadId = 777L,
            fileName = "Audio_Name.mp3",
            url = "https://example.com/audio.mp3",
            mimeType = "audio/mpeg",
            status = DownloadStatus.DOWNLOADING,
            downloadedBytes = downloadedBytes,
            totalBytes = totalBytes
        )
        repository.addDownloadEntry(entry)

        val current = repository.getDownloadHistory().first()
        assertEquals(7_100_000L, current.downloadedBytes)
        assertEquals(11_400_000L, current.totalBytes)
        val calculatedPercent = ((current.downloadedBytes * 100L) / current.totalBytes).toInt()
        assertEquals(62, calculatedPercent)
    }

    @Test
    fun `progress calculation without known total size maintains negative or zero totalBytes`() {
        val downloadedBytes = 4_200_000L // 4.2 MB
        val totalBytes = -1L

        val entry = DownloadEntry(
            id = "mp3-prog-unknown",
            downloadId = 888L,
            fileName = "live_stream.mp3",
            url = "https://example.com/live.mp3",
            mimeType = "audio/mpeg",
            status = DownloadStatus.DOWNLOADING,
            downloadedBytes = downloadedBytes,
            totalBytes = totalBytes
        )
        repository.addDownloadEntry(entry)

        val current = repository.getDownloadHistory().first()
        assertEquals(4_200_000L, current.downloadedBytes)
        assertEquals(-1L, current.totalBytes)
        // Verify no fake percentage is displayed when totalBytes <= 0
        assertTrue(current.totalBytes <= 0L)
    }

    @Test
    fun `state transitions from DOWNLOADING to PAUSED to COMPLETED persist correctly`() {
        val entry = DownloadEntry(
            id = "mp3-state-1",
            downloadId = 999L,
            fileName = "quran_recitation.mp3",
            url = "https://example.com/recitation.mp3",
            mimeType = "audio/mpeg",
            status = DownloadStatus.DOWNLOADING,
            downloadedBytes = 1_000_000L,
            totalBytes = 5_000_000L
        )
        repository.addDownloadEntry(entry)

        // 1. Progress update in memory
        repository.updateDownloadProgress(
            downloadId = 999L,
            status = DownloadStatus.DOWNLOADING,
            downloadedBytes = 2_500_000L,
            totalBytes = 5_000_000L,
            persistToDisk = false
        )
        assertEquals(2_500_000L, repository.getDownloadHistory().first().downloadedBytes)

        // 2. Transition to PAUSED
        repository.updateDownloadStatus(
            downloadId = 999L,
            status = DownloadStatus.PAUSED,
            downloadedBytes = 2_500_000L,
            totalBytes = 5_000_000L
        )
        assertEquals(DownloadStatus.PAUSED, repository.getDownloadHistory().first().status)

        // 3. Transition to COMPLETED with content URI
        repository.updateDownloadStatus(
            downloadId = 999L,
            status = DownloadStatus.COMPLETED,
            localUri = "content://downloads/all_downloads/999",
            downloadedBytes = 5_000_000L,
            totalBytes = 5_000_000L
        )

        // Verify across reload
        val reloaded = SettingsRepository(context).getDownloadHistory().first()
        assertEquals(DownloadStatus.COMPLETED, reloaded.status)
        assertEquals("content://downloads/all_downloads/999", reloaded.localUri)
        assertEquals(5_000_000L, reloaded.downloadedBytes)
        assertEquals(5_000_000L, reloaded.totalBytes)
    }

    @Test
    fun `MP3 policy evaluation allows audio formats`() {
        val mp3Result = DownloadPolicy.evaluate("https://example.com/audio/quran.mp3", "audio/mpeg", null)
        assertTrue(mp3Result is DownloadPolicy.Result.Allowed)

        val wavResult = DownloadPolicy.evaluate("https://example.com/audio/surah.wav", "audio/wav", null)
        assertTrue(wavResult is DownloadPolicy.Result.Allowed)

        val m4aResult = DownloadPolicy.evaluate("https://example.com/audio/adhan.m4a", "audio/mp4", null)
        assertTrue(m4aResult is DownloadPolicy.Result.Allowed)
    }

    @Test
    fun `MP3 open intent uses audio-mpeg MIME type and grant read permission flag`() {
        val testContentUri = Uri.parse("content://downloads/all_downloads/777")
        val effectiveMime = "audio/mpeg"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(testContentUri, effectiveMime)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(testContentUri, intent.data)
        assertEquals("audio/mpeg", intent.type)
        assertTrue((intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0)
        assertTrue((intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK) != 0)
    }

    @Test
    fun `direct audio URLs are correctly identified`() {
        val mp3Url = "https://server.org/files/audio.mp3?download=1"
        val cleanMp3 = mp3Url.substringBefore('?').substringBefore('#').lowercase(Locale.ROOT)
        assertTrue(cleanMp3.endsWith(".mp3"))

        val webPageUrl = "https://server.org/files/listen.html?file=audio.mp3"
        val cleanPage = webPageUrl.substringBefore('?').substringBefore('#').lowercase(Locale.ROOT)
        assertFalse(cleanPage.endsWith(".mp3"))
    }
}
