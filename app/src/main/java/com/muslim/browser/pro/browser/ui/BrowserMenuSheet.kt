package com.muslim.browser.pro.browser.ui

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslim.browser.pro.browser.AutofillEngine
import com.muslim.browser.pro.browser.BrowserUiState
import com.muslim.browser.pro.browser.DesktopArchitecture
import com.muslim.browser.pro.browser.ShareIntentHandler
import com.muslim.browser.pro.browser.TranslationEngine
import com.muslim.browser.pro.ui.theme.AppTheme
import com.muslim.browser.pro.ui.theme.LocalAppColors

@Composable
fun BrowserMenuSheet(
    uiState: BrowserUiState,
    onDismiss: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit = {},
    onAddKeyword: (String) -> Boolean,
    onTogglePopupBlocking: (Boolean) -> Unit,
    onToggleAdBlocking: (Boolean) -> Unit,
    onClearAllData: () -> Unit,
    onClearCacheAndCookies: () -> Unit,
    onToggleDesktopMode: (Boolean) -> Unit = {},
    onToggleDesktopMode4: (Boolean) -> Unit = {},
    onToggleWindows10Touch: (Boolean) -> Unit = {},
    onTranslateToBengali: () -> Unit,
    onSelectTheme: (AppTheme) -> Unit,
    onSelectTranslationEngine: (TranslationEngine) -> Unit = {},
    onOpenDiagnostics: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current

    val defaultBrowserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Returned from system default browser role dialog or settings
    }

    var showClearAllDataDialog by remember { mutableStateOf(false) }
    var showClearCacheCookiesDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAutofillDialog by remember { mutableStateOf(false) }
    var showHealthCheckDialog by remember { mutableStateOf(false) }
    var lastPdfJobTimestamp by remember { mutableStateOf(0L) }

    val isWebUrlValid = ShareIntentHandler.isDirectHttpUrl(uiState.currentUrl)
    val isBlocked = uiState.blockedInfo != null
    val isShareEnabled = !uiState.isHomePage && !isBlocked && isWebUrlValid
    val isSavePdfEnabled = !uiState.isHomePage && !isBlocked && isWebUrlValid && !uiState.isLoading

    // Dialog: Theme Selection
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = uiState.appTheme,
            onSelectTheme = onSelectTheme,
            onDismiss = { showThemeDialog = false }
        )
    }

    // Dialog: Smart Form Autofill Settings & Profile Editor
    if (showAutofillDialog) {
        AutofillSettingsDialog(onDismiss = { showAutofillDialog = false })
    }

    // Dialog: Settings Health Check (সেটিং স্বাস্থ্য পরীক্ষা)
    if (showHealthCheckDialog) {
        com.muslim.browser.pro.browser.SettingsHealthCheckDialog(
            uiState = uiState,
            onToggleAdBlocking = onToggleAdBlocking,
            onTogglePopupBlocking = onTogglePopupBlocking,
            onSelectTheme = onSelectTheme,
            onDismiss = { showHealthCheckDialog = false }
        )
    }

    // Dialog: Clear All Data
    if (showClearAllDataDialog) {
        ClearAllDataDialog(
            onConfirm = onClearAllData,
            onDismiss = { showClearAllDataDialog = false }
        )
    }

    // Dialog: Clear Cache & Cookies
    if (showClearCacheCookiesDialog) {
        ClearCacheCookiesDialog(
            onConfirm = onClearCacheAndCookies,
            onDismiss = { showClearCacheCookiesDialog = false }
        )
    }

    var showTranslationConfirmDialog by remember { mutableStateOf(false) }
    var dontAskAgainTranslation by remember { mutableStateOf(false) }

    // Dialog: Translation Privacy Confirmation
    if (showTranslationConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showTranslationConfirmDialog = false },
            title = {
                Text(
                    text = "অনুবাদ নিশ্চিতকরণ",
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontSize = 15.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "অনুবাদের জন্য এই পেজের লেখা বাইরের সার্ভারে যাবে। চালিয়ে যাবেন?",
                        color = colors.textPrimary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { dontAskAgainTranslation = !dontAskAgainTranslation }
                    ) {
                        Checkbox(
                            checked = dontAskAgainTranslation,
                            onCheckedChange = { dontAskAgainTranslation = it }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "আর জিজ্ঞাসা করো না",
                            color = colors.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (dontAskAgainTranslation) {
                            context.getSharedPreferences("translation_privacy_prefs", android.content.Context.MODE_PRIVATE)
                                .edit()
                                .putBoolean("key_skip_translation_confirmation", true)
                                .apply()
                        }
                        showTranslationConfirmDialog = false
                        val liveWv = getActiveLiveWebView(context, uiState)
                        if (liveWv != null) {
                            liveWv.evaluateJavascript("document.querySelector('input[type=password]') !== null") { result ->
                                val hasPassword = result?.trim()?.equals("true", ignoreCase = true) == true
                                if (hasPassword) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "এই পেজে অনুবাদ বন্ধ (পাসওয়ার্ড ঘর আছে)",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    onTranslateToBengali()
                                }
                            }
                        } else {
                            onTranslateToBengali()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("চালিয়ে যান", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTranslationConfirmDialog = false }) {
                    Text("বাতিল", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Semi-transparent overlay to ensure background home page/web page is visible outside the compact floating panel
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.40f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
            .testTag("browser_menu_sheet"),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Compact Floating Window
        Surface(
            modifier = Modifier
                .padding(end = 12.dp, bottom = 10.dp)
                .widthIn(max = 330.dp)
                .fillMaxWidth(0.88f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume clicks inside panel to prevent dismissing */ }
                .testTag("floating_menu_panel"),
            shape = RoundedCornerShape(18.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 14.dp,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Header
                MenuHeader(onDismiss = onDismiss)

                HorizontalDivider(color = colors.border, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(6.dp))

                // Translate to বাংলা (বা Original Page Restore)
                MenuRow(
                    title = if (uiState.isPageTranslated) "Original Page" else "Translate to বাংলা",
                    icon = Icons.Default.Translate,
                    contentDescription = "Translate to বাংলা",
                    onClick = {
                        if (uiState.isPageTranslated) {
                            onTranslateToBengali()
                        } else {
                            val liveWv = getActiveLiveWebView(context, uiState)
                            if (liveWv != null) {
                                liveWv.evaluateJavascript("document.querySelector('input[type=password]') !== null") { result ->
                                    val hasPassword = result?.trim()?.equals("true", ignoreCase = true) == true
                                    if (hasPassword) {
                                        android.widget.Toast.makeText(
                                            context,
                                            "এই পেজে অনুবাদ বন্ধ (পাসওয়ার্ড ঘর আছে)",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        val prefs = context.getSharedPreferences("translation_privacy_prefs", android.content.Context.MODE_PRIVATE)
                                        val skipConfirm = prefs.getBoolean("key_skip_translation_confirmation", false)
                                        if (skipConfirm) {
                                            onTranslateToBengali()
                                        } else {
                                            showTranslationConfirmDialog = true
                                        }
                                    }
                                }
                            } else {
                                val prefs = context.getSharedPreferences("translation_privacy_prefs", android.content.Context.MODE_PRIVATE)
                                val skipConfirm = prefs.getBoolean("key_skip_translation_confirmation", false)
                                if (skipConfirm) {
                                    onTranslateToBengali()
                                } else {
                                    showTranslationConfirmDialog = true
                                }
                            }
                        }
                    },
                    surfaceColor = if (uiState.isPageTranslated) colors.surfaceVariant else colors.surface,
                    borderColor = if (uiState.isPageTranslated) colors.accent else colors.border,
                    trailingContent = if (uiState.isTranslating) {
                        {
                            Text(
                                text = "Translating...",
                                color = colors.accent,
                                fontSize = 10.5.sp
                            )
                        }
                    } else null,
                    modifier = Modifier.testTag("menu_item_translate_bengali")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Share Webpage URL (Native Android Sharesheet)
                MenuRow(
                    title = "Share",
                    icon = Icons.Default.Share,
                    contentDescription = "Share",
                    enabled = isShareEnabled,
                    onClick = {
                        if (isShareEnabled) {
                            onDismiss()
                            performShare(context, uiState.currentUrl, uiState.pageTitle)
                        }
                    },
                    modifier = Modifier.testTag("menu_item_share")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Save Webpage as PDF (Native Android PDF Infrastructure)
                MenuRow(
                    title = "Save as PDF",
                    icon = Icons.Default.PictureAsPdf,
                    contentDescription = "Save as PDF",
                    enabled = isSavePdfEnabled,
                    onClick = {
                        if (isSavePdfEnabled) {
                            val now = System.currentTimeMillis()
                            if (now - lastPdfJobTimestamp >= 1500L) {
                                lastPdfJobTimestamp = now
                                val success = performSaveAsPdf(context, uiState)
                                if (success) {
                                    onDismiss()
                                }
                            }
                        }
                    },
                    trailingContent = if (uiState.isLoading && isWebUrlValid && !uiState.isHomePage && !isBlocked) {
                        {
                            Text(
                                text = "Loading...",
                                color = colors.textSecondary,
                                fontSize = 10.5.sp
                            )
                        }
                    } else null,
                    modifier = Modifier.testTag("menu_item_save_as_pdf")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Smart Form Autofill Profile Row with integrated Fill Form Action Button
                AutofillSection(
                    uiState = uiState,
                    isBlocked = isBlocked,
                    onOpenAutofillDialog = { showAutofillDialog = true },
                    onDismiss = onDismiss
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Translation Engine Selection (3 Selectable Switches, Single Active Engine)
                TranslationEngineSection(
                    selectedEngine = uiState.selectedTranslationEngine,
                    onSelectTranslationEngine = onSelectTranslationEngine
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Settings -> Theme (Centralized Theme Selection: White, Black & White)
                MenuRow(
                    title = "Theme",
                    icon = Icons.Default.Settings,
                    contentDescription = "Theme",
                    onClick = { showThemeDialog = true },
                    trailingContent = { MenuBadge(text = uiState.appTheme.displayName) },
                    modifier = Modifier.testTag("menu_item_theme")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Settings Health Check (সেটিং স্বাস্থ্য পরীক্ষা)
                MenuRow(
                    title = "সেটিং স্বাস্থ্য পরীক্ষা",
                    icon = Icons.Default.HealthAndSafety,
                    contentDescription = "সেটিং স্বাস্থ্য পরীক্ষা",
                    onClick = { showHealthCheckDialog = true },
                    trailingContent = { MenuBadge(text = "পরীক্ষা") },
                    modifier = Modifier.testTag("menu_item_health_check")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Browsing History (ইতিহাস)
                MenuRow(
                    title = "History",
                    icon = Icons.Default.History,
                    contentDescription = "Browsing History",
                    onClick = onOpenHistory,
                    trailingContent = { MenuBadge(text = "View") },
                    modifier = Modifier.testTag("menu_item_history")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Downloads (Settings item labeled exactly 'Downloads')
                MenuRow(
                    title = "Downloads",
                    icon = Icons.Default.Download,
                    contentDescription = "Downloads",
                    onClick = onOpenDownloads,
                    trailingContent = { MenuBadge(text = "View") },
                    modifier = Modifier.testTag("menu_item_downloads")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Default Browser (ডিফল্ট ব্রাউজার করুন)
                MenuRow(
                    title = "ডিফল্ট ব্রাউজার করুন",
                    subtitle = "খুলে যাওয়া স্ক্রিনে Muslim Browser বেছে নিন।",
                    icon = Icons.Default.Language,
                    contentDescription = "ডিফল্ট ব্রাউজার করুন",
                    onClick = {
                        openDefaultBrowserSettings(context, defaultBrowserLauncher)
                        onDismiss()
                    },
                    trailingContent = { MenuBadge(text = "Set") },
                    modifier = Modifier.testTag("menu_item_default_browser")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Experimental Browser Identity Profile: Windows 10 Touch
                DesktopSection(uiState = uiState)

                Spacer(modifier = Modifier.height(6.dp))

                // Ad Blocking (Compact, No description text)
                MenuToggleRow(
                    title = "Ad Blocking",
                    icon = Icons.Default.Security,
                    checked = uiState.isAdBlockingEnabled,
                    onCheckedChange = onToggleAdBlocking,
                    switchTestTag = "ad_blocking_switch",
                    modifier = Modifier.testTag("section_ad_blocking")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Pop-up Blocking (Compact, No description text)
                MenuToggleRow(
                    title = "Pop-up Blocking",
                    icon = Icons.Default.Block,
                    checked = uiState.isPopupBlockingEnabled,
                    onCheckedChange = onTogglePopupBlocking,
                    switchTestTag = "popup_blocking_switch",
                    modifier = Modifier.testTag("section_popup_blocking")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Adult Content Protection (Permanent Badge, Compact)
                AdultProtectionSection()

                Spacer(modifier = Modifier.height(6.dp))

                // Clear Cache & Cookies and Clear All Data
                ClearDataSection(
                    onOpenClearCacheDialog = { showClearCacheCookiesDialog = true },
                    onOpenClearAllDialog = { showClearAllDataDialog = true }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Custom Keyword Blocking (Compact)
                CustomKeywordSection(
                    customKeywords = uiState.customKeywords,
                    onAddKeyword = onAddKeyword
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Download Protection (Compact)
                DownloadProtectionSection()
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Reusable Compact Menu Row Composables
// ---------------------------------------------------------------------------

@Composable
private fun MenuRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = title,
    subtitle: String? = null,
    surfaceColor: Color = LocalAppColors.current.surfaceVariant,
    borderColor: Color = LocalAppColors.current.border,
    iconTint: Color = if (enabled) LocalAppColors.current.iconTint else LocalAppColors.current.textSecondary.copy(alpha = 0.38f),
    titleColor: Color = if (enabled) LocalAppColors.current.textPrimary else LocalAppColors.current.textSecondary.copy(alpha = 0.38f),
    trailingContent: (@Composable () -> Unit)? = null
) {
    val colors = LocalAppColors.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = surfaceColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = if (subtitle != null) 6.dp else 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        color = titleColor,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (subtitle != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            color = colors.textSecondary,
                            fontSize = 10.5.sp,
                            lineHeight = 13.sp
                        )
                    }
                }
            }
            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(6.dp))
                trailingContent()
            }
        }
    }
}

@Composable
private fun MenuToggleRow(
    title: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    switchTestTag: String? = null,
    subtitle: String? = null,
    surfaceColor: Color = LocalAppColors.current.surfaceVariant,
    borderColor: Color = LocalAppColors.current.border,
    iconTint: Color = LocalAppColors.current.iconTint,
    contentDescription: String? = null,
    onRowClick: (() -> Unit)? = null
) {
    val colors = LocalAppColors.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = surfaceColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onRowClick != null) Modifier.clickable { onRowClick() } else Modifier)
                .padding(horizontal = 10.dp, vertical = if (subtitle != null) 6.dp else 4.dp),
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
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (subtitle != null) {
                    Column {
                        Text(
                            text = title,
                            color = colors.textPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = subtitle,
                            color = colors.textSecondary,
                            fontSize = 10.sp
                        )
                    }
                } else {
                    Text(
                        text = title,
                        color = colors.textPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.buttonText,
                    checkedTrackColor = colors.accent,
                    uncheckedThumbColor = colors.textSecondary,
                    uncheckedTrackColor = colors.border.copy(alpha = 0.5f)
                ),
                modifier = if (switchTestTag != null) Modifier.testTag(switchTestTag) else Modifier
            )
        }
    }
}

@Composable
private fun MenuBadge(text: String, showArrow: Boolean = true) {
    val colors = LocalAppColors.current
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = colors.accent.copy(alpha = 0.15f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = text,
                color = colors.accent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            if (showArrow) {
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Logical Menu Sections
// ---------------------------------------------------------------------------

@Composable
private fun MenuHeader(onDismiss: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = colors.iconTint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Muslim Browser",
                color = colors.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .size(28.dp)
                .testTag("close_menu_button")
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
private fun AutofillSection(
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
            // Main row area: Opens Autofill Profile settings/editor
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
                    contentDescription = "Autofill Profile",
                    tint = colors.iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Autofill Profile (CV)",
                    color = colors.textPrimary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            // Dedicated trailing action button: Immediately invokes Fill Form action
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
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
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
private fun TranslationEngineSection(
    selectedEngine: TranslationEngine,
    onSelectTranslationEngine: (TranslationEngine) -> Unit
) {
    val colors = LocalAppColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_translation_engine"),
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = null,
                    tint = colors.iconTint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Translation Engine",
                    color = colors.textPrimary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            // 1. LibreTranslate Switch (Default)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "LibreTranslate",
                    color = colors.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = if (selectedEngine == TranslationEngine.LIBRE_TRANSLATE) FontWeight.Bold else FontWeight.Normal
                )
                Switch(
                    checked = selectedEngine == TranslationEngine.LIBRE_TRANSLATE,
                    onCheckedChange = { checked ->
                        if (checked || selectedEngine != TranslationEngine.LIBRE_TRANSLATE) {
                            onSelectTranslationEngine(TranslationEngine.LIBRE_TRANSLATE)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.buttonText,
                        checkedTrackColor = colors.accent,
                        uncheckedThumbColor = colors.textSecondary,
                        uncheckedTrackColor = colors.border.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.testTag("switch_engine_libre")
                )
            }
            // 2. MyMemory Translate Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "MyMemory Translate",
                    color = colors.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = if (selectedEngine == TranslationEngine.MYMEMORY) FontWeight.Bold else FontWeight.Normal
                )
                Switch(
                    checked = selectedEngine == TranslationEngine.MYMEMORY,
                    onCheckedChange = { checked ->
                        if (checked || selectedEngine != TranslationEngine.MYMEMORY) {
                            onSelectTranslationEngine(TranslationEngine.MYMEMORY)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.buttonText,
                        checkedTrackColor = colors.accent,
                        uncheckedThumbColor = colors.textSecondary,
                        uncheckedTrackColor = colors.border.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.testTag("switch_engine_mymemory")
                )
            }
        }
    }
}

@Composable
private fun DesktopSection(uiState: BrowserUiState) {
    val context = LocalContext.current
    val colors = LocalAppColors.current
    MenuToggleRow(
        title = "Windows 10 Desktop",
        subtitle = "Experimental browser identity profile",
        icon = Icons.Default.DesktopWindows,
        contentDescription = "Windows 10 Desktop",
        checked = uiState.isWindows10TouchEnabled,
        onCheckedChange = { checked ->
            val target = if (checked) DesktopArchitecture.WINDOWS_10_TOUCH else DesktopArchitecture.NONE
            notifyArchitectureChange(context, target)
        },
        onRowClick = {
            val isChecked = uiState.isWindows10TouchEnabled
            val target = if (!isChecked) DesktopArchitecture.WINDOWS_10_TOUCH else DesktopArchitecture.NONE
            notifyArchitectureChange(context, target)
        },
        surfaceColor = colors.surface,
        borderColor = colors.border.copy(alpha = 0.5f),
        iconTint = if (uiState.isWindows10TouchEnabled) colors.accent else colors.textPrimary,
        switchTestTag = "windows_10_touch_switch",
        modifier = Modifier.testTag("item_windows_10_touch")
    )
}

@Composable
private fun AdultProtectionSection() {
    val colors = LocalAppColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_adult_protection"),
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
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = colors.iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Adult Protection",
                    color = colors.textPrimary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = colors.accent.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, colors.accent)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Permanently Locked",
                        tint = colors.accent,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Always Active",
                        color = colors.accent,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ClearDataSection(
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
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp),
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
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CustomKeywordSection(
    customKeywords: Set<String>,
    onAddKeyword: (String) -> Boolean
) {
    val colors = LocalAppColors.current
    var newKeywordInput by remember { mutableStateOf("") }
    var keywordError by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_custom_keywords"),
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = colors.iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Custom Keyword Blocking",
                    color = colors.textPrimary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newKeywordInput,
                    onValueChange = {
                        newKeywordInput = it
                        keywordError = null
                    },
                    placeholder = {
                        Text("Enter keyword...", color = colors.textSecondary, fontSize = 11.sp)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("keyword_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.accent,
                        unfocusedBorderColor = colors.border,
                        cursorColor = colors.accent
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Button(
                    onClick = {
                        val success = onAddKeyword(newKeywordInput)
                        if (success) {
                            newKeywordInput = ""
                            keywordError = null
                        } else {
                            keywordError = "Cannot add duplicate or blank keyword"
                        }
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.buttonBackground,
                        contentColor = colors.buttonText
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("add_keyword_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (keywordError != null) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = keywordError ?: "",
                    color = Color(0xFFEF5350),
                    fontSize = 10.5.sp
                )
            }
            if (customKeywords.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    customKeywords.forEach { kw ->
                        Surface(
                            shape = RoundedCornerShape(5.dp),
                            color = if (colors.isMonochrome) colors.border.copy(alpha = 0.3f) else Color(0x33FF7043),
                            border = BorderStroke(1.dp, if (colors.isMonochrome) colors.border else Color(0x66FF7043)),
                            modifier = Modifier.testTag("protected_keyword_$kw")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Protected",
                                    tint = if (colors.isMonochrome) colors.textPrimary else Color(0xFFFF7043),
                                    modifier = Modifier.size(9.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = kw,
                                    color = colors.textPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadProtectionSection() {
    val colors = LocalAppColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_download_protection"),
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = colors.iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Download Protection",
                    color = colors.textPrimary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            DownloadItemCompact(label = "Video (.mp4, .mkv)", isBlocked = true)
            DownloadItemCompact(label = "Audio (.mp3, .wav)", isBlocked = false)
            DownloadItemCompact(label = "Apps (.apk)", isBlocked = true)
            DownloadItemCompact(label = "Images (.jpg, .png)", isBlocked = false)
            DownloadItemCompact(label = "PDF Documents (.pdf)", isBlocked = false)
        }
    }
}

// ---------------------------------------------------------------------------
// Dialog Composables
// ---------------------------------------------------------------------------

@Composable
private fun ThemeSelectionDialog(
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
                    val isSelected = currentTheme == theme || (currentTheme == AppTheme.FORCE_DARK_ON && theme == AppTheme.WHITE)
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

@Composable
private fun ClearAllDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Clear All Data?",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = colors.textPrimary
            )
        },
        text = {
            Text(
                text = "Clear browsing history, cache and cookies.",
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
private fun ClearCacheCookiesDialog(
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

// ---------------------------------------------------------------------------
// Helpers and Public/Internal Utilities
// ---------------------------------------------------------------------------

@Composable
fun DownloadItemCompact(label: String, isBlocked: Boolean) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = if (isBlocked) colors.textSecondary else colors.textPrimary,
            fontSize = 11.sp
        )
        if (isBlocked) {
            Surface(
                shape = RoundedCornerShape(3.dp),
                color = if (colors.isMonochrome) colors.border.copy(alpha = 0.3f) else Color(0x33EF5350)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Blocked",
                        tint = if (colors.isMonochrome) colors.textPrimary else Color(0xFFEF5350),
                        modifier = Modifier.size(9.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Blocked",
                        color = if (colors.isMonochrome) colors.textPrimary else Color(0xFFEF5350),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(3.dp),
                color = if (colors.isMonochrome) colors.border.copy(alpha = 0.3f) else Color(0x3366BB6A)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Allowed",
                        tint = if (colors.isMonochrome) colors.textPrimary else Color(0xFF66BB6A),
                        modifier = Modifier.size(9.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Allowed",
                        color = if (colors.isMonochrome) colors.textPrimary else Color(0xFF66BB6A),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun notifyArchitectureChange(context: android.content.Context, architecture: DesktopArchitecture) {
    try {
        var currentContext: android.content.Context? = context
        while (currentContext != null) {
            if (currentContext is com.muslim.browser.pro.MainActivity) {
                break
            }
            if (currentContext is android.content.ContextWrapper) {
                currentContext = currentContext.baseContext
            } else {
                break
            }
        }

        val effectiveArchitecture = if (architecture == DesktopArchitecture.WINDOWS_10_TOUCH) {
            DesktopArchitecture.WINDOWS_10_TOUCH
        } else {
            DesktopArchitecture.NONE
        }

        if (currentContext is com.muslim.browser.pro.MainActivity) {
            val twm = try {
                currentContext.tabWebViewManager
            } catch (_: Throwable) {
                null
            }
            twm?.syncAllLiveWebViewsArchitecture(effectiveArchitecture)

            val currentWebView = try {
                val field = currentContext.javaClass.getDeclaredField("webViewInstance")
                field.isAccessible = true
                field.get(currentContext) as? android.webkit.WebView
            } catch (_: Throwable) {
                null
            }

            if (currentWebView != null) {
                com.muslim.browser.pro.browser.DesktopCore.synchronizeDesktopWebView(
                    currentWebView,
                    currentWebView.url,
                    effectiveArchitecture,
                    true
                )
            }

            val viewModel = try {
                val m = currentContext.javaClass.getDeclaredMethod("getViewModel")
                m.isAccessible = true
                m.invoke(currentContext) as? com.muslim.browser.pro.browser.BrowserViewModel
            } catch (_: Throwable) {
                null
            }
            viewModel?.selectDesktopArchitecture(effectiveArchitecture)
            return
        }

        val method = currentContext?.javaClass?.methods?.firstOrNull {
            it.name.startsWith("setDesktopArchitecture") && it.parameterTypes.size == 1
        }
        method?.invoke(currentContext, effectiveArchitecture)
    } catch (_: Throwable) {}
}

internal fun sanitizePdfJobName(title: String?, url: String?): String {
    val baseName = if (!title.isNullOrBlank()) {
        title.trim()
    } else if (!url.isNullOrBlank()) {
        val hostFromUri = try {
            android.net.Uri.parse(url).host
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

private fun performShare(context: android.content.Context, url: String, title: String) {
    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
            if (title.isNotBlank()) {
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TITLE, title)
            }
        }
        val chooser = Intent.createChooser(sendIntent, "Share Webpage")
        var currentContext: android.content.Context? = context
        while (currentContext != null) {
            if (currentContext is android.app.Activity) break
            if (currentContext is android.content.ContextWrapper) {
                currentContext = currentContext.baseContext
            } else {
                break
            }
        }
        val launchContext = currentContext ?: context
        if (launchContext !is android.app.Activity) {
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        launchContext.startActivity(chooser)
    } catch (_: Throwable) {}
}

internal fun getActiveLiveWebView(context: android.content.Context, uiState: BrowserUiState): android.webkit.WebView? {
    var currentContext: android.content.Context? = context
    while (currentContext != null) {
        if (currentContext is com.muslim.browser.pro.MainActivity) break
        if (currentContext is android.content.ContextWrapper) {
            currentContext = currentContext.baseContext
        } else {
            break
        }
    }
    val mainActivity = currentContext as? com.muslim.browser.pro.MainActivity ?: return null
    return try {
        mainActivity.tabWebViewManager?.getLiveWebView(uiState.currentTabId)
    } catch (_: Throwable) {
        null
    } ?: try {
        val field = mainActivity.javaClass.getDeclaredField("webViewInstance")
        field.isAccessible = true
        field.get(mainActivity) as? android.webkit.WebView
    } catch (_: Throwable) {
        null
    }
}

private fun performSaveAsPdf(context: android.content.Context, uiState: BrowserUiState): Boolean {
    try {
        var currentContext: android.content.Context? = context
        while (currentContext != null) {
            if (currentContext is com.muslim.browser.pro.MainActivity) break
            if (currentContext is android.content.ContextWrapper) {
                currentContext = currentContext.baseContext
            } else {
                break
            }
        }
        val mainActivity = currentContext as? com.muslim.browser.pro.MainActivity ?: return false
        val liveWebView = getActiveLiveWebView(context, uiState) ?: return false

        val wvUrl = liveWebView.url
        if (wvUrl.isNullOrBlank() || !ShareIntentHandler.isDirectHttpUrl(wvUrl)) {
            return false
        }

        val printManager = mainActivity.getSystemService(android.content.Context.PRINT_SERVICE) as? PrintManager ?: return false
        val jobName = sanitizePdfJobName(uiState.pageTitle.ifBlank { liveWebView.title ?: "" }, wvUrl)
        val printAdapter = liveWebView.createPrintDocumentAdapter(jobName)
        val printAttributes = PrintAttributes.Builder().build()
        printManager.print(jobName, printAdapter, printAttributes)
        return true
    } catch (_: Throwable) {
        return false
    }
}

/**
 * Safely prompts the user to set Muslim Browser Pro as the default browser app.
 * Tier 1: Android 10+ (API 29+) RoleManager ROLE_BROWSER dialog launched via ActivityResultLauncher.
 * Tier 2: Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS fallback.
 * Tier 3: Settings.ACTION_SETTINGS fallback.
 * Tier 4: Settings.ACTION_APPLICATION_DETAILS_SETTINGS fallback.
 * Ensures the app never crashes under any OEM customization and guides user with Toast messages.
 */
internal fun openDefaultBrowserSettings(
    context: android.content.Context,
    launcher: ActivityResultLauncher<Intent>? = null
) {
    var currentContext: android.content.Context? = context
    while (currentContext != null) {
        if (currentContext is Activity) break
        if (currentContext is android.content.ContextWrapper) {
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

    // Tier 2: Fallback options in sequence, each in its own try/catch block
    var openedSettings = false

    // Option A: Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS
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

    // Option B: General System Settings
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

    // Option C: App Info / Details Settings
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

    // Step 4: Show Toast when directed to phone settings
    if (openedSettings) {
        Toast.makeText(context, "Default apps → Browser app → Muslim Browser বেছে নিন।", Toast.LENGTH_LONG).show()
    }
}

