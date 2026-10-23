package com.muslim.browser.pro.browser.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslim.browser.pro.ui.theme.LocalAppColors

@Composable
fun ClearDataSection(
    onOpenClearCacheDialog: () -> Unit,
    onOpenClearAllDialog: () -> Unit
) {
    val colors = LocalAppColors.current
    // Clear Cache & Cookies (Compact)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_clear_cache_cookies"),
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.CleaningServices,
                    contentDescription = null,
                    tint = if (colors.isMonochrome) colors.iconTint else Color(0xFFFFB74D),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Clear Cache & Cookies",
                    color = colors.textPrimary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Button(
                onClick = onOpenClearCacheDialog,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent.copy(alpha = 0.2f),
                    contentColor = colors.accent
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .height(28.dp)
                    .testTag("btn_clear_cache_cookies")
            ) {
                Text("Clear", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Clear All Data (Compact)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_clear_all_data"),
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(1.dp, if (colors.isMonochrome) colors.border else Color(0x2BEF5350))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = if (colors.isMonochrome) colors.iconTint else Color(0xFFEF5350),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Clear All Data",
                    color = colors.textPrimary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Button(
                onClick = onOpenClearAllDialog,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent.copy(alpha = 0.2f),
                    contentColor = colors.accent
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .height(28.dp)
                    .testTag("btn_clear_all_data")
            ) {
                Text("Clear", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ClearAllDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Clear All Browsing Data?",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = colors.textPrimary
            )
        },
        text = {
            Text(
                text = "Clear browsing history, cache, cookies, and website storage. Your saved bookmarks, downloads, and app settings will be preserved.",
                color = colors.textSecondary,
                fontSize = 13.sp
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onConfirm()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF5350),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_clear_all_data_button")
            ) {
                Text("Clear All Data", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.border.copy(alpha = 0.2f),
                    contentColor = colors.textPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("cancel_clear_all_data_button")
            ) {
                Text("Cancel")
            }
        },
        containerColor = colors.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("dialog_clear_all_data")
    )
}

@Composable
fun ClearCacheCookiesDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Clear Cache & Cookies?",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = colors.textPrimary
            )
        },
        text = {
            Text(
                text = "Clear temporary web cache and website cookies. Browsing history, saved bookmarks, downloads, and app settings will not be affected.",
                color = colors.textSecondary,
                fontSize = 13.sp
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onConfirm()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.buttonBackground,
                    contentColor = colors.buttonText
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_clear_cache_cookies_button")
            ) {
                Text("Clear", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.border.copy(alpha = 0.2f),
                    contentColor = colors.textPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("cancel_clear_cache_cookies_button")
            ) {
                Text("Cancel")
            }
        },
        containerColor = colors.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("dialog_clear_cache_cookies")
    )
}
