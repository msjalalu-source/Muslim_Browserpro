package com.muslim.browser.pro

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.DownloadEntry
import com.muslim.browser.pro.browser.DownloadStatus
import com.muslim.browser.pro.browser.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DownloadHistoryTest {

    private lateinit var context: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = SettingsRepository(context)
        repository.clearAllDownloadHistory()
    }

    @Test
    fun `download history starts empty after clearing`() {
        val history = repository.getDownloadHistory()
        assertTrue(history.isEmpty())
    }

    @Test
    fun `add download entry persists across repository instances`() {
        val entry = DownloadEntry(
            id = "test-download-1",
            downloadId = 1001L,
            fileName = "document.pdf",
            url = "https://example.com/files/document.pdf",
            mimeType = "application/pdf",
            timestamp = 1700000000000L,
            status = DownloadStatus.DOWNLOADING,
            totalBytes = 204800L
        )

        repository.addDownloadEntry(entry)

        val inMemory = repository.getDownloadHistory()
        assertEquals(1, inMemory.size)
        assertEquals("test-download-1", inMemory[0].id)
        assertEquals(1001L, inMemory[0].downloadId)
        assertEquals("document.pdf", inMemory[0].fileName)
        assertEquals(DownloadStatus.DOWNLOADING, inMemory[0].status)

        // Create new repository instance to simulate app restart
        val reloadedRepo = SettingsRepository(context)
        val reloadedHistory = reloadedRepo.getDownloadHistory()
        assertEquals(1, reloadedHistory.size)
        assertEquals("test-download-1", reloadedHistory[0].id)
        assertEquals("document.pdf", reloadedHistory[0].fileName)
        assertEquals("https://example.com/files/document.pdf", reloadedHistory[0].url)
        assertEquals("application/pdf", reloadedHistory[0].mimeType)
        assertEquals(DownloadStatus.DOWNLOADING, reloadedHistory[0].status)
        assertEquals(204800L, reloadedHistory[0].totalBytes)
    }

    @Test
    fun `update download status to COMPLETED persists correctly`() {
        val entry = DownloadEntry(
            id = "test-download-2",
            downloadId = 2002L,
            fileName = "archive.zip",
            url = "https://example.com/archive.zip",
            mimeType = "application/zip",
            status = DownloadStatus.DOWNLOADING
        )
        repository.addDownloadEntry(entry)

        val updated = repository.updateDownloadStatus(
            downloadId = 2002L,
            status = DownloadStatus.COMPLETED,
            localUri = "content://downloads/my_downloads/2002",
            totalBytes = 1048576L
        )
        assertTrue(updated)

        // Verify across reload
        val reloadedRepo = SettingsRepository(context)
        val reloadedHistory = reloadedRepo.getDownloadHistory()
        assertEquals(1, reloadedHistory.size)
        assertEquals(DownloadStatus.COMPLETED, reloadedHistory[0].status)
        assertEquals("content://downloads/my_downloads/2002", reloadedHistory[0].localUri)
        assertEquals(1048576L, reloadedHistory[0].totalBytes)
    }

    @Test
    fun `delete specific download entry leaves others intact`() {
        val entry1 = DownloadEntry(id = "d1", downloadId = 1L, fileName = "file1.txt", url = "https://example.com/1")
        val entry2 = DownloadEntry(id = "d2", downloadId = 2L, fileName = "file2.txt", url = "https://example.com/2")
        repository.addDownloadEntry(entry1)
        repository.addDownloadEntry(entry2)

        assertEquals(2, repository.getDownloadHistory().size)

        val deleted = repository.deleteDownloadEntry("d1")
        assertTrue(deleted)

        val remaining = repository.getDownloadHistory()
        assertEquals(1, remaining.size)
        assertEquals("d2", remaining[0].id)
    }

    @Test
    fun `download history is strictly separated from browsing history`() {
        repository.clearHistory()
        repository.clearAllDownloadHistory()

        repository.addHistoryEntry("Google", "https://google.com")
        repository.addDownloadEntry(
            DownloadEntry(id = "d3", downloadId = 3L, fileName = "sample.png", url = "https://example.com/sample.png")
        )

        assertEquals(1, repository.getHistory().size)
        assertEquals(1, repository.getDownloadHistory().size)

        assertEquals("https://google.com", repository.getHistory()[0].url)
        assertEquals("sample.png", repository.getDownloadHistory()[0].fileName)

        // Clearing browsing history does not clear download history
        repository.clearHistory()
        assertEquals(0, repository.getHistory().size)
        assertEquals(1, repository.getDownloadHistory().size)
    }
}
