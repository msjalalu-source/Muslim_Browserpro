package com.muslim.browser.pro.browser

import android.os.Bundle
import java.util.UUID

data class BlockedInfo(
    val targetUrl: String = "",
    val reason: String = "",
    val detail: String = ""
)

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val url: String = "",
    val title: String = "",
    val bundle: Bundle? = null,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val blockedInfo: BlockedInfo? = null
)

data class HistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class FavoriteSite(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val url: String,
    val iconLetter: String = name.firstOrNull()?.uppercase() ?: "W",
    val badgeColor: Long = 0xFF2E7D32
)

enum class DownloadStatus {
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class DownloadEntry(
    val downloadId: Long = 0L,
    val fileName: String = "",
    val url: String = "",
    val filePath: String = "",
    val totalBytes: Long = 0L,
    val downloadedBytes: Long = 0L,
    val status: DownloadStatus = DownloadStatus.DOWNLOADING,
    val timestamp: Long = System.currentTimeMillis()
)

data class SslWarningState(
    val url: String,
    val host: String,
    val details: String
)
