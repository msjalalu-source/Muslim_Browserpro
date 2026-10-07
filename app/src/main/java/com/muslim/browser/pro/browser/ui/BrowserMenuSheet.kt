package com.muslim.browser.pro.browser.ui

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.ui.platform.LocalContext
import com.muslim.browser.pro.browser.DesktopArchitecture
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslim.browser.pro.browser.BrowserUiState
import com.muslim.browser.pro.browser.TranslationEngine
import com.muslim.browser.pro.ui.theme.AppTheme
import com.muslim.browser.pro.ui.theme.LocalAppColors

@OptIn(ExperimentalLayoutApi::class)
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

    var newKeywordInput by remember { mutableStateOf("") }
    var keywordError by remember { mutableStateOf<String?>(null) }
    var showClearAllDataDialog by remember { mutableStateOf(false) }
    var showClearCacheCookiesDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // Dialog: Theme Selection (Settings -> Theme)
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
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
                    AppTheme.values().forEach { theme ->
                        val isSelected = uiState.appTheme == theme
                        Surface(
                            onClick = {
                                onSelectTheme(theme)
                                showThemeDialog = false
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
                                        showThemeDialog = false
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
                    onClick = { showThemeDialog = false },
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

    // Dialog: Clear All Data
    if (showClearAllDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDataDialog = false },
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
                        showClearAllDataDialog = false
                        onClearAllData()
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
                    onClick = { showClearAllDataDialog = false },
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

    // Dialog: Clear Cache & Cookies
    if (showClearCacheCookiesDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheCookiesDialog = false },
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
                        showClearCacheCookiesDialog = false
                        onClearCacheAndCookies()
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
                    onClick = { showClearCacheCookiesDialog = false },
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

                HorizontalDivider(color = colors.border, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(6.dp))

                // Translate to বাংলা (বা Original Page Restore)
                Surface(
                    onClick = onTranslateToBengali,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_item_translate_bengali"),
                    shape = RoundedCornerShape(8.dp),
                    color = if (uiState.isPageTranslated) colors.surfaceVariant else colors.surface,
                    border = BorderStroke(1.dp, if (uiState.isPageTranslated) colors.accent else colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Translate to বাংলা",
                                tint = colors.iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.isPageTranslated) "Original Page" else "Translate to বাংলা",
                                color = colors.textPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (uiState.isTranslating) {
                            Text(
                                text = "Translating...",
                                color = colors.accent,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Translation Engine Selection (3 Selectable Switches, Single Active Engine)
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
                                fontWeight = if (uiState.selectedTranslationEngine == TranslationEngine.LIBRE_TRANSLATE) FontWeight.Bold else FontWeight.Normal
                            )
                            Switch(
                                checked = uiState.selectedTranslationEngine == TranslationEngine.LIBRE_TRANSLATE,
                                onCheckedChange = { checked ->
                                    if (checked || uiState.selectedTranslationEngine != TranslationEngine.LIBRE_TRANSLATE) {
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
                                fontWeight = if (uiState.selectedTranslationEngine == TranslationEngine.MYMEMORY) FontWeight.Bold else FontWeight.Normal
                            )
                            Switch(
                                checked = uiState.selectedTranslationEngine == TranslationEngine.MYMEMORY,
                                onCheckedChange = { checked ->
                                    if (checked || uiState.selectedTranslationEngine != TranslationEngine.MYMEMORY) {
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

                Spacer(modifier = Modifier.height(6.dp))

                // Settings -> Theme (Centralized Theme Selection: White, Black & White)
                Surface(
                    onClick = { showThemeDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_item_theme"),
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant,
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Theme",
                                tint = colors.iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Theme",
                                color = colors.textPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colors.accent.copy(alpha = 0.15f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = uiState.appTheme.displayName,
                                    color = colors.accent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
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

                Spacer(modifier = Modifier.height(6.dp))

                // Browsing History (ইতিহাস)
                Surface(
                    onClick = onOpenHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_item_history"),
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant,
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Browsing History",
                                tint = colors.iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "History",
                                color = colors.textPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colors.accent.copy(alpha = 0.15f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "View",
                                    color = colors.accent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
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

                Spacer(modifier = Modifier.height(6.dp))

                // Downloads (Settings item labeled exactly 'Downloads')
                Surface(
                    onClick = onOpenDownloads,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_item_downloads"),
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant,
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Downloads",
                                tint = colors.iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Downloads",
                                color = colors.textPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colors.accent.copy(alpha = 0.15f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "View",
                                    color = colors.accent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
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

                Spacer(modifier = Modifier.height(6.dp))


                // Desktop Mode 4 (Targeted Viewport Guard)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("section_desktop_mode_4"),
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant,
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DesktopWindows,
                                contentDescription = "Desktop Mode 4",
                                tint = if (uiState.isDesktopMode4Enabled) colors.accent else colors.iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Desktop Mode 4",
                                    color = colors.textPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Targeted viewport guard",
                                    color = colors.textSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Switch(
                            checked = uiState.isDesktopMode4Enabled,
                            onCheckedChange = onToggleDesktopMode4,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.buttonText,
                                checkedTrackColor = colors.accent,
                                uncheckedThumbColor = colors.textSecondary,
                                uncheckedTrackColor = colors.border.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("desktop_mode_4_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Desktop Mode 11 (Moderately Optimized Viewport Guard)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("section_desktop_mode_11")
                        .clickable {
                            val isChecked = uiState.desktopArchitecture == DesktopArchitecture.DESKTOP_MODE_11
                            val target = if (!isChecked) DesktopArchitecture.DESKTOP_MODE_11 else DesktopArchitecture.NONE
                            notifyArchitectureChange(context, target)
                        },
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant,
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DesktopWindows,
                                contentDescription = "Desktop Mode 11",
                                tint = if (uiState.desktopArchitecture == DesktopArchitecture.DESKTOP_MODE_11) colors.accent else colors.iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Desktop Mode 11",
                                    color = colors.textPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Moderately optimized viewport guard",
                                    color = colors.textSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Switch(
                            checked = uiState.desktopArchitecture == DesktopArchitecture.DESKTOP_MODE_11,
                            onCheckedChange = { checked ->
                                val target = if (checked) DesktopArchitecture.DESKTOP_MODE_11 else DesktopArchitecture.NONE
                                notifyArchitectureChange(context, target)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.buttonText,
                                checkedTrackColor = colors.accent,
                                uncheckedThumbColor = colors.textSecondary,
                                uncheckedTrackColor = colors.border.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("desktop_mode_11_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Desktop Mode 12 (Aggressively Optimized Zero-Observer)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("section_desktop_mode_12")
                        .clickable {
                            val isChecked = uiState.desktopArchitecture == DesktopArchitecture.DESKTOP_MODE_12
                            val target = if (!isChecked) DesktopArchitecture.DESKTOP_MODE_12 else DesktopArchitecture.NONE
                            notifyArchitectureChange(context, target)
                        },
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant,
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DesktopWindows,
                                contentDescription = "Desktop Mode 12",
                                tint = if (uiState.desktopArchitecture == DesktopArchitecture.DESKTOP_MODE_12) colors.accent else colors.iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Desktop Mode 12",
                                    color = colors.textPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Aggressively optimized (zero-observer)",
                                    color = colors.textSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Switch(
                            checked = uiState.desktopArchitecture == DesktopArchitecture.DESKTOP_MODE_12,
                            onCheckedChange = { checked ->
                                val target = if (checked) DesktopArchitecture.DESKTOP_MODE_12 else DesktopArchitecture.NONE
                                notifyArchitectureChange(context, target)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.buttonText,
                                checkedTrackColor = colors.accent,
                                uncheckedThumbColor = colors.textSecondary,
                                uncheckedTrackColor = colors.border.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("desktop_mode_12_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Experimental Browser Identity Profile: Windows 10 Touch
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_windows_10_touch"),
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surface,
                    border = BorderStroke(1.dp, colors.border.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DesktopWindows,
                                contentDescription = "Windows 10 Desktop",
                                tint = if (uiState.isWindows10TouchEnabled) colors.accent else colors.textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Windows 10 Desktop",
                                    color = colors.textPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Experimental browser identity profile",
                                    color = colors.textSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Switch(
                            checked = uiState.isWindows10TouchEnabled,
                            onCheckedChange = onToggleWindows10Touch,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.buttonText,
                                checkedTrackColor = colors.accent,
                                uncheckedThumbColor = colors.textSecondary,
                                uncheckedTrackColor = colors.border.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("windows_10_touch_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Experimental Desktop Architecture: Windows 7
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_windows_7"),
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surface,
                    border = BorderStroke(1.dp, colors.border.copy(alpha = 0.5f))
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
                                imageVector = Icons.Default.DesktopWindows,
                                contentDescription = "Windows 7",
                                tint = if (false) colors.accent else colors.iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Windows 7",
                                    color = colors.textPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Experimental desktop architecture",
                                    color = colors.textSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Switch(
                            checked = false,
                            onCheckedChange = { /* Windows 7 placeholder */ },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.buttonText,
                                checkedTrackColor = colors.accent,
                                uncheckedThumbColor = colors.textSecondary,
                                uncheckedTrackColor = colors.border.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("windows_7_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Ad Blocking (Compact, No description text)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("section_ad_blocking"),
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant,
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = colors.iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ad Blocking",
                                color = colors.textPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Switch(
                            checked = uiState.isAdBlockingEnabled,
                            onCheckedChange = onToggleAdBlocking,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.buttonText,
                                checkedTrackColor = colors.accent,
                                uncheckedThumbColor = colors.textSecondary,
                                uncheckedTrackColor = colors.border.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("ad_blocking_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Pop-up Blocking (Compact, No description text)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("section_popup_blocking"),
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant,
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                tint = colors.iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pop-up Blocking",
                                color = colors.textPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Switch(
                            checked = uiState.isPopupBlockingEnabled,
                            onCheckedChange = onTogglePopupBlocking,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.buttonText,
                                checkedTrackColor = colors.accent,
                                uncheckedThumbColor = colors.textSecondary,
                                uncheckedTrackColor = colors.border.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("popup_blocking_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Adult Content Protection (Permanent Badge, Compact)
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

                Spacer(modifier = Modifier.height(6.dp))

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
                            onClick = { showClearCacheCookiesDialog = true },
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
                            onClick = { showClearAllDataDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (colors.isMonochrome) colors.border.copy(alpha = 0.3f) else Color(0x33EF5350),
                                contentColor = if (colors.isMonochrome) colors.textPrimary else Color(0xFFFF8A80)
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

                Spacer(modifier = Modifier.height(6.dp))

                // Custom Keyword Blocking (Compact)
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
                                tint = if (colors.isMonochrome) colors.iconTint else Color(0xFFFFA726),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Custom Keywords",
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

                        if (uiState.customKeywords.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                uiState.customKeywords.forEach { kw ->
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

                Spacer(modifier = Modifier.height(6.dp))

                // Download Protection (Compact)
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
        }
    }
}

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
                        contentDescription = "Permanently Blocked",
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
        while (currentContext is android.content.ContextWrapper) {
            val method = currentContext.javaClass.methods.firstOrNull {
                it.name.startsWith("setDesktopArchitecture") && it.parameterTypes.size == 1
            }
            if (method != null) {
                method.invoke(currentContext, architecture)
                return
            }
            currentContext = currentContext.baseContext
        }
        val method = currentContext?.javaClass?.methods?.firstOrNull {
            it.name.startsWith("setDesktopArchitecture") && it.parameterTypes.size == 1
        }
        method?.invoke(currentContext, architecture)
    } catch (_: Throwable) {}
}
