package com.muslim.browser.pro.browser.ui

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.Settings
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslim.browser.pro.MainActivity
import com.muslim.browser.pro.browser.AutofillEngine
import com.muslim.browser.pro.browser.BrowserUiState
import com.muslim.browser.pro.browser.ShareIntentHandler
import com.muslim.browser.pro.ui.theme.AppTheme
import com.muslim.browser.pro.ui.theme.LocalAppColors

@Composable
fun MenuHeader(onDismiss: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Browser Menu",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = colors.accent.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "Pro",
                    color = colors.accent,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .size(28.dp)
                .testTag("menu_header_close")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Menu",
                tint = colors.textSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun MenuRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = title,
    subtitle: String? = null,
    surfaceColor: Color = LocalAppColors.current.surfaceVariant,
    borderColor: Color = LocalAppColors.current.border,
    iconTint: Color = LocalAppColors.current.textPrimary,
    trailingContent: (@Composable () -> Unit)? = null,
    showArrow: Boolean = true
) {
    val colors = LocalAppColors.current
    Surface(
        onClick = { if (enabled) onClick() },
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp),
        shape = RoundedCornerShape(8.dp),
        color = surfaceColor,
        border = BorderStroke(1.dp, borderColor),
        enabled = enabled
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = if (enabled) iconTint else colors.textSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        color = if (enabled) colors.textPrimary else colors.textSecondary.copy(alpha = 0.5f),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = colors.textSecondary,
                            fontSize = 9.5.sp
                        )
                    }
                }
            }
            if (trailingContent != null) {
                trailingContent()
            } else if (showArrow) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = colors.textSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun MenuToggleRow(
    title: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    contentDescription: String? = title,
    onRowClick: (() -> Unit)? = null,
    surfaceColor: Color? = null,
    borderColor: Color? = null,
    iconTint: Color? = null,
    switchTestTag: String? = null
) {
    val colors = LocalAppColors.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = surfaceColor ?: colors.surfaceVariant,
        border = BorderStroke(1.dp, borderColor ?: colors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (onRowClick != null) onRowClick() else onCheckedChange(!checked)
                }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconTint ?: colors.iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        color = colors.textPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = colors.textSecondary,
                            fontSize = 9.5.sp
                        )
                    }
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = if (switchTestTag != null) Modifier.testTag(switchTestTag) else Modifier,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = colors.accent,
                    uncheckedThumbColor = colors.textSecondary,
                    uncheckedTrackColor = colors.border
                )
            )
        }
    }
}

@Composable
fun MenuBadge(text: String, showArrow: Boolean = true) {
    val colors = LocalAppColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border.copy(alpha = 0.5f))
        ) {
            Text(
                text = text,
                color = colors.textSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
            )
        }
        if (showArrow) {
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colors.textSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun AutofillSection(
    uiState: BrowserUiState,
    isBlocked: Boolean,
    onOpenAutofillDialog: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("menu_item_autofill_profile"),
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onOpenAutofillDialog()
                    }
                    .padding(vertical = 6.dp)
                    .testTag("menu_action_autofill_profile_main"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Badge,
                    contentDescription = "Autofill",
                    tint = colors.iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Autofill",
                    color = colors.textPrimary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = {
                    val liveWv = getActiveLiveWebView(context, uiState)
                    if (liveWv != null && !uiState.isHomePage && !isBlocked) {
                        onDismiss()
                        AutofillEngine.executeAutofill(context, liveWv) { _, _, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "No active web form to fill", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent.copy(alpha = 0.2f),
                    contentColor = colors.accent
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .height(28.dp)
                    .testTag("menu_action_autofill_fill_form")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = colors.accent
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Fill Form", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: AppTheme,
    onSelectTheme: (AppTheme) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Theme",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = colors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(AppTheme.WHITE, AppTheme.BLACK_WHITE).forEach { theme ->
                    val isSelected = currentTheme == theme
                    Surface(
                        onClick = {
                            onSelectTheme(theme)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) colors.surfaceVariant else Color.Transparent,
                        border = BorderStroke(1.dp, if (isSelected) colors.accent else colors.border.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("theme_option_${theme.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onSelectTheme(theme)
                                    onDismiss()
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colors.accent,
                                    unselectedColor = colors.textSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = theme.displayName,
                                color = colors.textPrimary,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_theme_dialog_button")
            ) {
                Text("Close", color = colors.accent)
            }
        },
        containerColor = colors.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("dialog_theme_selector")
    )
}

internal fun sanitizePdfJobName(title: String?, url: String?): String {
    val baseName = if (!title.isNullOrBlank()) {
        title.trim()
    } else if (!url.isNullOrBlank()) {
        val hostFromUri = try {
            Uri.parse(url).host
        } catch (_: Throwable) {
            null
        }
        val fallbackHost = if (url.contains("://")) {
            url.substringAfter("://").substringBefore('/').substringBefore('?').substringBefore(':')
        } else {
            url.substringBefore('/').substringBefore('?').substringBefore(':')
        }
        if (!hostFromUri.isNullOrBlank()) hostFromUri else fallbackHost.ifBlank { "Webpage" }
    } else {
        "Webpage"
    }

    val sanitized = baseName.replace(Regex("[/\\\\:*?\"<>|\\r\\n\\t]"), "_").trim()
    val candidate = if (sanitized.isEmpty() || sanitized.all { it == '_' }) "Webpage" else sanitized
    val maxLen = 60
    val truncated = if (candidate.length > maxLen) candidate.substring(0, maxLen).trimEnd('_', ' ') else candidate
    return "MuslimBrowser_$truncated"
}

fun performShare(context: Context, url: String, title: String) {
    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
            if (title.isNotBlank()) {
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TITLE, title)
            }
        }
        val chooser = Intent.createChooser(sendIntent, "পৃষ্ঠা শেয়ার করুন")
        var currentContext: Context? = context
        while (currentContext != null) {
            if (currentContext is Activity) break
            if (currentContext is ContextWrapper) {
                currentContext = currentContext.baseContext
            } else {
                break
            }
        }
        val launchContext = currentContext ?: context
        if (launchContext !is Activity) {
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        launchContext.startActivity(chooser)
    } catch (e: Throwable) {
        android.util.Log.e("BrowserMenuSheet", "Share failed", e)
        Toast.makeText(context, "শেয়ার করা যায়নি", Toast.LENGTH_SHORT).show()
    }
}

internal fun getActiveLiveWebView(context: Context, uiState: BrowserUiState): WebView? {
    var currentContext: Context? = context
    while (currentContext != null) {
        if (currentContext is MainActivity) break
        if (currentContext is ContextWrapper) {
            currentContext = currentContext.baseContext
        } else {
            break
        }
    }
    val mainActivity = currentContext as? MainActivity ?: return null
    return try {
        mainActivity.tabWebViewManager.getLiveWebView(uiState.currentTabId)
    } catch (_: Throwable) {
        null
    } ?: try {
        mainActivity.tabWebViewManager.getOrCreateWebView(uiState.currentTabId, uiState.currentUrl).first
    } catch (_: Throwable) {
        null
    }
}

fun performSaveAsPdf(
    context: Context,
    uiState: BrowserUiState,
    passedWebView: WebView? = null
): Boolean {
    try {
        var currentContext: Context? = context
        while (currentContext != null) {
            if (currentContext is Activity) break
            if (currentContext is ContextWrapper) {
                currentContext = currentContext.baseContext
            } else {
                break
            }
        }
        val activity = (currentContext as? Activity)
            ?: (if (context is Activity) context else null)
            ?: run {
                android.util.Log.e("BrowserMenuSheet", "Save as PDF failed: Activity context not found")
                Toast.makeText(context, "PDF তৈরি করা যায়নি: অ্যাক্টিভিটি পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
                return false
            }

        val liveWebView = passedWebView ?: getActiveLiveWebView(context, uiState)
        if (liveWebView == null) {
            android.util.Log.e("BrowserMenuSheet", "Save as PDF failed: liveWebView is null")
            Toast.makeText(context, "PDF তৈরি করা যায়নি: ওয়েবভিউ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            return false
        }

        val wvUrl = liveWebView.url?.takeIf { it.isNotBlank() } ?: uiState.currentUrl
        if (wvUrl.isBlank() || !ShareIntentHandler.isDirectHttpUrl(wvUrl)) {
            android.util.Log.e("BrowserMenuSheet", "Save as PDF failed: wvUrl is blank or invalid: $wvUrl")
            Toast.makeText(context, "PDF তৈরি করা যায়নি: অবৈধ ওয়েব লিঙ্ক", Toast.LENGTH_SHORT).show()
            return false
        }

        val printManager = activity.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        if (printManager == null) {
            android.util.Log.e("BrowserMenuSheet", "Save as PDF failed: PrintManager is null")
            Toast.makeText(context, "PDF প্রিন্ট সেবা পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            return false
        }

        val jobName = sanitizePdfJobName(uiState.pageTitle.ifBlank { liveWebView.title ?: "" }, wvUrl)
        val printAdapter = liveWebView.createPrintDocumentAdapter(jobName)
        val printAttributes = PrintAttributes.Builder().build()
        printManager.print(jobName, printAdapter, printAttributes)
        return true
    } catch (e: Throwable) {
        android.util.Log.e("BrowserMenuSheet", "Save as PDF failed with exception", e)
        Toast.makeText(context, "PDF তৈরি করা যায়নি", Toast.LENGTH_SHORT).show()
        return false
    }
}

internal fun openDefaultBrowserSettings(
    context: Context,
    launcher: ActivityResultLauncher<Intent>? = null
) {
    var currentContext: Context? = context
    while (currentContext != null) {
        if (currentContext is Activity) break
        if (currentContext is ContextWrapper) {
            currentContext = currentContext.baseContext
        } else {
            break
        }
    }
    val targetActivity = currentContext as? Activity

    // Tier 1: Android 10+ (API 29+) RoleManager request
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        try {
            val roleManager = context.getSystemService(RoleManager::class.java)
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_BROWSER)) {
                if (roleManager.isRoleHeld(RoleManager.ROLE_BROWSER)) {
                    Toast.makeText(context, "Muslim Browser আগে থেকেই ডিফল্ট ব্রাউজার", Toast.LENGTH_SHORT).show()
                    return
                }
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_BROWSER)
                if (launcher != null) {
                    launcher.launch(intent)
                    return
                } else if (targetActivity != null) {
                    @Suppress("DEPRECATION")
                    targetActivity.startActivityForResult(intent, 1001)
                    return
                }
            }
        } catch (_: Throwable) {
            // Fall through to fallback tiers
        }
    }

    // Tier 2: Fallback options in sequence
    var openedSettings = false

    try {
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        if (targetActivity != null) {
            targetActivity.startActivity(intent)
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        openedSettings = true
    } catch (_: Throwable) {}

    if (!openedSettings) {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS)
            if (targetActivity != null) {
                targetActivity.startActivity(intent)
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            openedSettings = true
        } catch (_: Throwable) {}
    }

    if (!openedSettings) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            if (targetActivity != null) {
                targetActivity.startActivity(intent)
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            openedSettings = true
        } catch (_: Throwable) {}
    }

    if (openedSettings) {
        Toast.makeText(context, "Default apps → Browser app → Muslim Browser বেছে নিন।", Toast.LENGTH_LONG).show()
    }
}
