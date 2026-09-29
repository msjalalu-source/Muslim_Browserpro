package com.muslim.browser.pro.browser.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslim.browser.pro.browser.DownloadEntry
import com.muslim.browser.pro.browser.DownloadStatus
import com.muslim.browser.pro.ui.theme.LocalAppColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Dedicated In-App Download History Screen for Muslim Browser Pro.
 * Displays only browser-initiated downloads, categorized by date,
 * with status indicators, file opening, and individual/bulk removal.
 */
@Composable
fun DownloadHistoryScreen(
    downloads: List<DownloadEntry>,
    onOpenFile: (DownloadEntry) -> Unit,
    onDeleteEntry: (String) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    BackHandler(onBack = onDismiss)

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = "Clear Download History",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to clear your download history? Downloaded files on your device will not be deleted.",
                    color = colors.textSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirmDialog = false
                        onClearAll()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (colors.isMonochrome) colors.border.copy(alpha = 0.5f) else Color(0xFFD32F2F),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_clear_download_history_button")
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showClearConfirmDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary),
                    border = BorderStroke(1.dp, colors.border),
                    modifier = Modifier.testTag("cancel_clear_download_history_button")
                ) {
                    Text("Cancel")
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.testTag("dialog_clear_download_history")
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("download_history_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surface,
                border = BorderStroke(0.5.dp, colors.border),
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("download_history_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colors.iconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = colors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Downloads",
                            color = colors.textPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (downloads.isNotEmpty()) {
                        Button(
                            onClick = { showClearConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (colors.isMonochrome) colors.border.copy(alpha = 0.3f) else Color(0x28EF5350),
                                contentColor = if (colors.isMonochrome) colors.textPrimary else Color(0xFFFF8A80)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("download_history_clear_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            if (downloads.isEmpty()) {
                // Empty State View
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("downloads_empty_view"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = colors.surfaceVariant,
                            border = BorderStroke(1.dp, colors.border),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Downloads",
                            color = colors.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Files you download with Muslim Browser Pro will appear here.",
                            color = colors.textSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Grouped Downloads List
                val groupedDownloads = remember(downloads) {
                    downloads.groupBy { getDownloadDateGroup(it.timestamp) }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("download_history_list")
                ) {
                    groupedDownloads.forEach { (dateHeader, itemsInDate) ->
                        item(key = "header_$dateHeader") {
                            Text(
                                text = dateHeader,
                                color = colors.accent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 6.dp)
                            )
                        }

                        items(
                            items = itemsInDate,
                            key = { it.id }
                        ) { entry ->
                            DownloadItemRow(
                                entry = entry,
                                onOpen = { onOpenFile(entry) },
                                onDelete = { onDeleteEntry(entry.id) }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadItemRow(
    entry: DownloadEntry,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalAppColors.current
    val isCompleted = entry.status == DownloadStatus.COMPLETED
    val isDownloading = entry.status == DownloadStatus.DOWNLOADING
    val isPaused = entry.status == DownloadStatus.PAUSED
    val isFailed = entry.status == DownloadStatus.FAILED

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isCompleted) { onOpen() }
            .testTag("download_item_${entry.id}"),
        shape = RoundedCornerShape(10.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(0.8.dp, colors.border),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Type Icon Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.accent.copy(alpha = 0.15f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = getFileIcon(entry.fileName, entry.mimeType),
                            contentDescription = null,
                            tint = colors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.fileName,
                        color = colors.textPrimary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Status & Real Progress Info
                    StatusProgressLine(entry = entry)

                    if (entry.url.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = extractHost(entry.url),
                            color = colors.textSecondary.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isCompleted) {
                        IconButton(
                            onClick = onOpen,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("download_open_${entry.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Open file",
                                tint = if (colors.isMonochrome) colors.textPrimary else Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Action: Delete / Cancel
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("download_delete_${entry.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete download record",
                            tint = if (colors.isMonochrome) colors.textSecondary else Color(0xFFEF5350),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Real-time Progress Bar
            if (isDownloading || isPaused) {
                Spacer(modifier = Modifier.height(8.dp))
                if (entry.totalBytes > 0L) {
                    val progressFraction = (entry.downloadedBytes.toFloat() / entry.totalBytes.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = if (isPaused) (if (colors.isMonochrome) colors.textSecondary else Color(0xFFFFB300)) else colors.accent,
                        trackColor = colors.border.copy(alpha = 0.3f)
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = colors.accent,
                        trackColor = colors.border.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusProgressLine(entry: DownloadEntry) {
    val colors = LocalAppColors.current

    when (entry.status) {
        DownloadStatus.DOWNLOADING -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (entry.totalBytes > 0L) {
                    val percentage = ((entry.downloadedBytes * 100L) / entry.totalBytes).coerceIn(0L, 100L)
                    Text(
                        text = "Downloading — $percentage%",
                        color = colors.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${formatFileSize(entry.downloadedBytes)} / ${formatFileSize(entry.totalBytes)}",
                        color = colors.textSecondary,
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = "Downloading...",
                        color = colors.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (entry.downloadedBytes > 0L) {
                        Text(
                            text = "${formatFileSize(entry.downloadedBytes)} downloaded",
                            color = colors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
        DownloadStatus.PAUSED -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val pausedColor = if (colors.isMonochrome) colors.textSecondary else Color(0xFFFFB300)
                if (entry.totalBytes > 0L && entry.downloadedBytes > 0L) {
                    val percentage = ((entry.downloadedBytes * 100L) / entry.totalBytes).coerceIn(0L, 100L)
                    Text(
                        text = "Paused — $percentage%",
                        color = pausedColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${formatFileSize(entry.downloadedBytes)} / ${formatFileSize(entry.totalBytes)}",
                        color = colors.textSecondary,
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = "Paused",
                        color = pausedColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        DownloadStatus.COMPLETED -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (colors.isMonochrome) colors.textSecondary else Color(0xFF4CAF50),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Completed",
                        color = if (colors.isMonochrome) colors.textSecondary else Color(0xFF4CAF50),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (entry.totalBytes > 0L) {
                    Text(
                        text = formatFileSize(entry.totalBytes),
                        color = colors.textSecondary,
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = formatDownloadTime(entry.timestamp),
                    color = colors.textSecondary,
                    fontSize = 11.sp
                )
            }
        }
        DownloadStatus.FAILED -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = if (colors.isMonochrome) colors.textSecondary else Color(0xFFE53935),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Failed",
                        color = if (colors.isMonochrome) colors.textSecondary else Color(0xFFE53935),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = formatDownloadTime(entry.timestamp),
                    color = colors.textSecondary,
                    fontSize = 11.sp
                )
            }
        }
        DownloadStatus.CANCELLED -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Cancelled",
                    color = colors.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = formatDownloadTime(entry.timestamp),
                    color = colors.textSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private fun getFileIcon(fileName: String, mimeType: String): ImageVector {
    val lowerName = fileName.lowercase(Locale.ROOT)
    val lowerMime = mimeType.lowercase(Locale.ROOT)

    return when {
        lowerMime.startsWith("image/") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".png") || lowerName.endsWith(".webp") || lowerName.endsWith(".gif") ||
                lowerName.endsWith(".svg") -> Icons.Default.Image

        lowerMime == "application/pdf" || lowerName.endsWith(".pdf") -> Icons.Default.PictureAsPdf

        lowerMime.startsWith("audio/") || lowerName.endsWith(".mp3") || lowerName.endsWith(".wav") ||
                lowerName.endsWith(".m4a") || lowerName.endsWith(".ogg") || lowerName.endsWith(".aac") ||
                lowerName.endsWith(".flac") -> Icons.Default.AudioFile

        lowerMime.contains("zip") || lowerMime.contains("compressed") || lowerName.endsWith(".zip") ||
                lowerName.endsWith(".rar") || lowerName.endsWith(".7z") || lowerName.endsWith(".tar") ||
                lowerName.endsWith(".gz") -> Icons.Default.FolderZip

        lowerName.endsWith(".json") || lowerName.endsWith(".xml") || lowerName.endsWith(".html") ||
                lowerName.endsWith(".js") || lowerName.endsWith(".css") || lowerName.endsWith(".py") ||
                lowerName.endsWith(".kt") || lowerName.endsWith(".java") -> Icons.Default.Code

        lowerName.endsWith(".doc") || lowerName.endsWith(".docx") || lowerName.endsWith(".txt") ||
                lowerName.endsWith(".xls") || lowerName.endsWith(".xlsx") || lowerName.endsWith(".ppt") ||
                lowerName.endsWith(".pptx") || lowerName.endsWith(".csv") -> Icons.Default.Description

        else -> Icons.Default.InsertDriveFile
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return ""
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0

    return when {
        gb >= 1.0 -> String.format(Locale.ROOT, "%.1f GB", gb)
        mb >= 1.0 -> String.format(Locale.ROOT, "%.1f MB", mb)
        kb >= 1.0 -> String.format(Locale.ROOT, "%.1f KB", kb)
        else -> "$bytes B"
    }
}

private val downloadTimeFormat = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue(): SimpleDateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
}

private val downloadDateFormat = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue(): SimpleDateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
}

private fun getDownloadDateGroup(timestamp: Long): String {
    val now = Calendar.getInstance()
    val itemTime = Calendar.getInstance().apply { timeInMillis = timestamp }

    val isSameYear = now.get(Calendar.YEAR) == itemTime.get(Calendar.YEAR)
    val dayOfYearNow = now.get(Calendar.DAY_OF_YEAR)
    val dayOfYearItem = itemTime.get(Calendar.DAY_OF_YEAR)

    return when {
        isSameYear && dayOfYearNow == dayOfYearItem -> "Today"
        isSameYear && dayOfYearNow - dayOfYearItem == 1 -> "Yesterday"
        else -> downloadDateFormat.get()?.format(Date(timestamp)) ?: ""
    }
}

private fun formatDownloadTime(timestamp: Long): String {
    return downloadTimeFormat.get()?.format(Date(timestamp)) ?: ""
}

private fun extractHost(url: String): String {
    return try {
        android.net.Uri.parse(url).host ?: url
    } catch (_: Exception) {
        url
    }
}
