package com.muslim.browser.pro.browser.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.muslim.browser.pro.browser.BrowserUiState
import com.muslim.browser.pro.browser.DownloadEntry
import com.muslim.browser.pro.browser.DownloadStatus
import com.muslim.browser.pro.ui.theme.LocalAppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DownloadsDialog(
    uiState: BrowserUiState,
    onSelectUrl: (String) -> Unit,
    onDeleteEntry: (String) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    val downloads = uiState.downloadHistory

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 580.dp)
                .clip(RoundedCornerShape(20.dp))
                .testTag("downloads_dialog"),
            color = colors.surface,
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.accent.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Downloads",
                                tint = colors.accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Downloads (${downloads.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (downloads.isNotEmpty()) {
                            TextButton(
                                onClick = onClearAll,
                                modifier = Modifier.testTag("clear_all_downloads_button"),
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Clear All",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("close_downloads_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Downloads",
                                tint = colors.iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.border.copy(alpha = 0.5f), thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(10.dp))

                if (downloads.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = colors.textSecondary.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No downloads yet",
                                fontSize = 14.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(downloads, key = { it.downloadId.toString() + "_" + it.fileName }) { item ->
                            DownloadItemCard(
                                entry = item,
                                onClick = {
                                    if (item.url.isNotBlank()) {
                                        onSelectUrl(item.url)
                                    }
                                },
                                onDelete = {
                                    val idOrName = if (item.downloadId > 0L) {
                                        item.downloadId.toString()
                                    } else {
                                        item.fileName
                                    }
                                    onDeleteEntry(idOrName)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadItemCard(
    entry: DownloadEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalAppColors.current
    val formattedTime = remember(entry.timestamp) {
        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(entry.timestamp))
    }

    val statusColor = when (entry.status) {
        DownloadStatus.COMPLETED -> colors.accent
        DownloadStatus.DOWNLOADING -> colors.textSecondary
        DownloadStatus.PAUSED -> colors.textSecondary
        DownloadStatus.FAILED -> MaterialTheme.colorScheme.error
        DownloadStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("download_item_${entry.downloadId}"),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colors.border.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.accent.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.fileName.ifBlank { "Download" },
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.status.name,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor
                        )
                        if (entry.totalBytes > 0L) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "•  ${formatFileSize(entry.totalBytes)}",
                                fontSize = 10.5.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                    Text(
                        text = formattedTime,
                        fontSize = 10.5.sp,
                        color = colors.textSecondary.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("delete_download_${entry.downloadId}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private val FILE_SIZE_UNITS = arrayOf("B", "KB", "MB", "GB")

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    var size = bytes.toDouble()
    var unitIndex = 0
    while (size >= 1024 && unitIndex < FILE_SIZE_UNITS.size - 1) {
        size /= 1024
        unitIndex++
    }
    return String.format(Locale.US, "%.1f %s", size, FILE_SIZE_UNITS[unitIndex])
}
