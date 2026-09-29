package com.muslim.browser.pro.browser

enum class DownloadStatus {
    DOWNLOADING,
    COMPLETED,
    FAILED,
    CANCELLED
}

/**
 * Lightweight model representing a download initiated by Muslim Browser Pro.
 * Preserved in persistent storage across app restarts.
 */
data class DownloadEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val downloadId: Long = -1L,
    val fileName: String,
    val url: String,
    val mimeType: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: DownloadStatus = DownloadStatus.DOWNLOADING,
    val totalBytes: Long = -1L,
    val localUri: String? = null
)
