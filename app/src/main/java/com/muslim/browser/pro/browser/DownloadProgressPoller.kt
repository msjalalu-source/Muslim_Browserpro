package com.muslim.browser.pro.browser

import android.app.DownloadManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DownloadProgressUpdate(
    val downloadId: Long,
    val status: DownloadStatus,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val localUri: String?
)

object DownloadProgressPoller {

    @Volatile
    var lastCursorExecutionThread: Thread? = null
        internal set

    /**
     * Queries DownloadManager cursor for active downloads strictly on Dispatchers.IO.
     * Returns lightweight progress updates and whether any downloads remain active.
     */
    suspend fun queryActiveDownloadsProgress(
        dm: DownloadManager,
        activeEntries: List<DownloadEntry>
    ): Pair<List<DownloadProgressUpdate>, Boolean> {
        if (activeEntries.isEmpty()) {
            return Pair(emptyList(), false)
        }
        return withContext(Dispatchers.IO) {
            fetchCursorProgress(dm, activeEntries)
        }
    }

    /**
     * Synchronous cursor reading function intended to be called strictly off the main thread.
     */
    fun fetchCursorProgress(
        dm: DownloadManager,
        activeEntries: List<DownloadEntry>
    ): Pair<List<DownloadProgressUpdate>, Boolean> {
        lastCursorExecutionThread = Thread.currentThread()

        val updates = mutableListOf<DownloadProgressUpdate>()
        var stillActive = false

        for (entry in activeEntries) {
            val query = DownloadManager.Query().setFilterById(entry.downloadId)
            try {
                dm.query(query)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status = if (statusIndex != -1) cursor.getInt(statusIndex) else -1
                        val bytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        val downloadedBytes = if (bytesIndex != -1) cursor.getLong(bytesIndex) else 0L
                        val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                        val totalBytes = if (totalBytesIndex != -1) cursor.getLong(totalBytesIndex) else -1L
                        val localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                        val localUri = if (localUriIndex != -1) cursor.getString(localUriIndex) else null

                        when (status) {
                            DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING -> {
                                stillActive = true
                                updates.add(
                                    DownloadProgressUpdate(
                                        downloadId = entry.downloadId,
                                        status = DownloadStatus.DOWNLOADING,
                                        downloadedBytes = downloadedBytes,
                                        totalBytes = totalBytes,
                                        localUri = localUri
                                    )
                                )
                            }
                            DownloadManager.STATUS_PAUSED -> {
                                stillActive = true
                                updates.add(
                                    DownloadProgressUpdate(
                                        downloadId = entry.downloadId,
                                        status = DownloadStatus.PAUSED,
                                        downloadedBytes = downloadedBytes,
                                        totalBytes = totalBytes,
                                        localUri = localUri
                                    )
                                )
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                updates.add(
                                    DownloadProgressUpdate(
                                        downloadId = entry.downloadId,
                                        status = DownloadStatus.COMPLETED,
                                        downloadedBytes = downloadedBytes,
                                        totalBytes = totalBytes,
                                        localUri = localUri
                                    )
                                )
                            }
                            DownloadManager.STATUS_FAILED -> {
                                val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                                val reason = if (reasonIndex != -1) cursor.getInt(reasonIndex) else -1
                                android.util.Log.d("DownloadManager", "Download ${entry.downloadId} failed: reason=$reason")
                                updates.add(
                                    DownloadProgressUpdate(
                                        downloadId = entry.downloadId,
                                        status = DownloadStatus.FAILED,
                                        downloadedBytes = downloadedBytes,
                                        totalBytes = totalBytes,
                                        localUri = localUri
                                    )
                                )
                            }
                        }
                    } else {
                        updates.add(
                            DownloadProgressUpdate(
                                downloadId = entry.downloadId,
                                status = DownloadStatus.FAILED,
                                downloadedBytes = 0L,
                                totalBytes = -1L,
                                localUri = null
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DownloadManager", "Error querying downloadId ${entry.downloadId}", e)
            }
        }

        return Pair(updates, stillActive)
    }

    /**
     * Queries single download for completion or status change strictly on Dispatchers.IO.
     */
    suspend fun querySingleDownloadProgress(
        dm: DownloadManager,
        downloadId: Long
    ): DownloadProgressUpdate? {
        return withContext(Dispatchers.IO) {
            lastCursorExecutionThread = Thread.currentThread()
            val query = DownloadManager.Query().setFilterById(downloadId)
            try {
                dm.query(query)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status = if (statusIndex != -1) cursor.getInt(statusIndex) else -1
                        val localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                        val localUri = if (localUriIndex != -1) cursor.getString(localUriIndex) else null
                        val bytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        val downloadedBytes = if (bytesIndex != -1) cursor.getLong(bytesIndex) else 0L
                        val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                        val totalBytes = if (totalBytesIndex != -1) cursor.getLong(totalBytesIndex) else -1L

                        when (status) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                DownloadProgressUpdate(downloadId, DownloadStatus.COMPLETED, downloadedBytes, totalBytes, localUri)
                            }
                            DownloadManager.STATUS_FAILED -> {
                                DownloadProgressUpdate(downloadId, DownloadStatus.FAILED, downloadedBytes, totalBytes, localUri)
                            }
                            DownloadManager.STATUS_PAUSED -> {
                                DownloadProgressUpdate(downloadId, DownloadStatus.PAUSED, downloadedBytes, totalBytes, localUri)
                            }
                            DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING -> {
                                DownloadProgressUpdate(downloadId, DownloadStatus.DOWNLOADING, downloadedBytes, totalBytes, localUri)
                            }
                            else -> null
                        }
                    } else null
                }
            } catch (e: Exception) {
                android.util.Log.e("DownloadManager", "Error checking single download status for $downloadId", e)
                null
            }
        }
    }
}
