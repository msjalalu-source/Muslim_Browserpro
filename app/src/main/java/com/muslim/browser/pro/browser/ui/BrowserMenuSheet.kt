package com.muslim.browser.pro.browser.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslim.browser.pro.browser.BrowserUiState
import com.muslim.browser.pro.browser.ShareIntentHandler
import com.muslim.browser.pro.ui.theme.AppTheme
import com.muslim.browser.pro.ui.theme.LocalAppColors

@Composable
fun BrowserMenuSheet(
    uiState: BrowserUiState,
    onDismiss: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit = {},
    onAddKeyword: (String) -> Boolean,
    onRemoveKeyword: (String) -> Boolean = { false },
    onTogglePopupBlocking: (Boolean) -> Unit,
    onToggleAdBlocking: (Boolean) -> Unit,
    onClearAllData: () -> Unit,
    onClearCacheAndCookies: () -> Unit,
    onToggleDesktopMode: (Boolean) -> Unit = {},
    onToggleDesktopMode4: (Boolean) -> Unit = {},
    onToggleWindows10Touch: (Boolean) -> Unit = {},
    onTranslateToBengali: () -> Unit,
    onSelectTheme: (AppTheme) -> Unit,
    onAddTimeLimitedDomain: (String) -> Boolean = { false },
    currentWebView: android.webkit.WebView? = null,
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
    var lastPdfJobTimestamp by remember { mutableStateOf(0L) }

    val effectiveUrl = uiState.currentUrl.ifBlank { currentWebView?.url ?: "" }
    val isWebUrlValid = ShareIntentHandler.isDirectHttpUrl(effectiveUrl)
    val isBlocked = uiState.blockedInfo.targetUrl.isNotBlank()
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
                                liveWv.evaluateJavascript("Array.from(document.querySelectorAll('input[type=password]')).some(function(el) { var s = window.getComputedStyle(el); return (el.offsetParent !== null || s.position === 'fixed') && s.display !== 'none' && s.visibility !== 'hidden' && (el.offsetWidth > 0 || el.offsetHeight > 0); })") { result ->
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
                        if (uiState.isHomePage || effectiveUrl.isBlank()) {
                            Toast.makeText(context, "শেয়ার করার জন্য একটি ওয়েব পৃষ্ঠা খুলুন", Toast.LENGTH_SHORT).show()
                        } else if (isBlocked) {
                            Toast.makeText(context, "অবরুদ্ধ পৃষ্ঠা শেয়ার করা যাবে না", Toast.LENGTH_SHORT).show()
                        } else if (!isWebUrlValid) {
                            Toast.makeText(context, "অবৈধ ওয়েব লিঙ্ক", Toast.LENGTH_SHORT).show()
                        } else {
                            onDismiss()
                            performShare(context, effectiveUrl, uiState.pageTitle)
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
                        if (uiState.isHomePage || effectiveUrl.isBlank()) {
                            Toast.makeText(context, "PDF তৈরি করার জন্য একটি ওয়েব পৃষ্ঠা খুলুন", Toast.LENGTH_SHORT).show()
                        } else if (isBlocked) {
                            Toast.makeText(context, "অবরুদ্ধ পৃষ্ঠা PDF তৈরি করা যাবে না", Toast.LENGTH_SHORT).show()
                        } else if (uiState.isLoading) {
                            Toast.makeText(context, "পৃষ্ঠা লোড হওয়া পর্যন্ত অপেক্ষা করুন", Toast.LENGTH_SHORT).show()
                        } else if (!isWebUrlValid) {
                            Toast.makeText(context, "অবৈধ ওয়েব লিঙ্ক", Toast.LENGTH_SHORT).show()
                        } else {
                            val now = System.currentTimeMillis()
                            if (now - lastPdfJobTimestamp >= 1500L) {
                                lastPdfJobTimestamp = now
                                val success = performSaveAsPdf(context, uiState, currentWebView)
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

                // Settings -> Theme (Theme Selection: White, Black & White)
                MenuRow(
                    title = "Theme",
                    icon = Icons.Default.Settings,
                    contentDescription = "Theme",
                    onClick = { showThemeDialog = true },
                    trailingContent = { MenuBadge(text = uiState.appTheme.displayName) },
                    modifier = Modifier.testTag("menu_item_theme")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Browsing History (ইতিহাস)
                MenuRow(
                    title = "History",
                    icon = Icons.Default.History,
                    contentDescription = "Browsing History",
                    onClick = {
                        onDismiss()
                        onOpenHistory()
                    },
                    trailingContent = { MenuBadge(text = "View") },
                    modifier = Modifier.testTag("menu_item_history")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Downloads (Settings item labeled exactly 'Downloads')
                MenuRow(
                    title = "Downloads",
                    icon = Icons.Default.Download,
                    contentDescription = "Downloads",
                    onClick = {
                        onDismiss()
                        onOpenDownloads()
                    },
                    trailingContent = { MenuBadge(text = "View") },
                    modifier = Modifier.testTag("menu_item_downloads")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Default Browser
                MenuRow(
                    title = "Default Browser",
                    icon = Icons.Default.Language,
                    contentDescription = "Default Browser",
                    onClick = {
                        openDefaultBrowserSettings(context, defaultBrowserLauncher)
                        onDismiss()
                    },
                    showArrow = false,
                    modifier = Modifier.testTag("menu_item_default_browser")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Experimental Browser Identity Profile: Windows 10 Touch
                DesktopSection(
                    uiState = uiState,
                    onToggleWindows10Touch = onToggleWindows10Touch
                )

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
                    onAddKeyword = onAddKeyword,
                    onRemoveKeyword = onRemoveKeyword
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Website Time Limit (20-minute daily allowance, permanent domains)
                WebsiteTimeLimitSection(
                    timeLimitedDomains = uiState.timeLimitedDomains,
                    domainUsageSeconds = uiState.timeLimitUsageSeconds,
                    onAddDomain = onAddTimeLimitedDomain
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Download Protection (Compact)
                DownloadProtectionSection()
            }
        }
    }
}
