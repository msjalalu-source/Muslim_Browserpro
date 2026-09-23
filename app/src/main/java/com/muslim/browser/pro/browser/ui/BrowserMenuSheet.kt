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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrowserMenuSheet(
    uiState: BrowserUiState,
    onDismiss: () -> Unit,
    onAddKeyword: (String) -> Boolean,
    onTogglePopupBlocking: (Boolean) -> Unit,
    onToggleAdBlocking: (Boolean) -> Unit,
    onClearAllData: () -> Unit,
    onClearCacheAndCookies: () -> Unit,
    onToggleDesktopMode: (Boolean) -> Unit,
    onTranslateToBangla: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newKeywordInput by remember { mutableStateOf("") }
    var keywordError by remember { mutableStateOf<String?>(null) }
    var showClearAllDataDialog by remember { mutableStateOf(false) }
    var showClearCacheCookiesDialog by remember { mutableStateOf(false) }

    // Dialog: Clear All Data
    if (showClearAllDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDataDialog = false },
            title = {
                Text(
                    text = "Clear All Data?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Clear browsing history, cache and cookies.",
                    color = Color(0xFFCFD8DC),
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
                        containerColor = Color(0x3390A4AE),
                        contentColor = Color(0xFFECEFF1)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("cancel_clear_all_data_button")
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF162036),
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
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearCacheCookiesDialog = false
                        onClearCacheAndCookies()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF),
                        contentColor = Color(0xFF0A0F1D)
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
                        containerColor = Color(0x3390A4AE),
                        contentColor = Color(0xFFECEFF1)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("cancel_clear_cache_cookies_button")
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF162036),
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
            color = Color(0xFF131D31),
            border = BorderStroke(1.dp, Color(0x4000E5FF)),
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
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Muslim Browser",
                            color = Color.White,
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
                            tint = Color(0xFF90A4AE),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                HorizontalDivider(color = Color(0x3342A5F5), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(6.dp))

                // Fast Bangla Translation (বাংলা অনুবাদ)
                Surface(
                    onClick = onTranslateToBangla,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("action_bangla_translate"),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x1F00E5FF),
                    border = BorderStroke(1.dp, Color(0x5500E5FF))
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
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "বাংলা অনুবাদ (Fast Bangla)",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF00E5FF)
                        ) {
                            Text(
                                text = "Translate",
                                color = Color(0xFF0A0F1D),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Desktop Mode (Compact, No description text)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("section_desktop_mode"),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF182238),
                    border = BorderStroke(1.dp, Color(0x1F42A5F5))
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
                                imageVector = Icons.Default.DesktopWindows,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Desktop Mode",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Switch(
                            checked = uiState.isDesktopModeEnabled,
                            onCheckedChange = onToggleDesktopMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF0A0F1D),
                                checkedTrackColor = Color(0xFF00E5FF),
                                uncheckedThumbColor = Color(0xFF78909C),
                                uncheckedTrackColor = Color(0xFF263238)
                            ),
                            modifier = Modifier.testTag("desktop_mode_switch")
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
                    color = Color(0xFF182238),
                    border = BorderStroke(1.dp, Color(0x1F42A5F5))
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
                                tint = Color(0xFF81D4FA),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ad Blocking",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Switch(
                            checked = uiState.isAdBlockingEnabled,
                            onCheckedChange = onToggleAdBlocking,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF0A0F1D),
                                checkedTrackColor = Color(0xFF00E5FF),
                                uncheckedThumbColor = Color(0xFF78909C),
                                uncheckedTrackColor = Color(0xFF263238)
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
                    color = Color(0xFF182238),
                    border = BorderStroke(1.dp, Color(0x1F42A5F5))
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
                                tint = Color(0xFF81D4FA),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pop-up Blocking",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Switch(
                            checked = uiState.isPopupBlockingEnabled,
                            onCheckedChange = onTogglePopupBlocking,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF0A0F1D),
                                checkedTrackColor = Color(0xFF00E5FF),
                                uncheckedThumbColor = Color(0xFF78909C),
                                uncheckedTrackColor = Color(0xFF263238)
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
                    color = Color(0xFF182238),
                    border = BorderStroke(1.dp, Color(0x2B00E5FF))
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
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Adult Protection",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x2200E5FF),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Permanently Locked",
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Always Active",
                                    color = Color(0xFF00E5FF),
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
                    color = Color(0xFF182238),
                    border = BorderStroke(1.dp, Color(0x1F42A5F5))
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
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Clear Cache & Cookies",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = { showClearCacheCookiesDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0x3342A5F5),
                                contentColor = Color(0xFF81D4FA)
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
                    color = Color(0xFF182238),
                    border = BorderStroke(1.dp, Color(0x2BEF5350))
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
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Clear All Data",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = { showClearAllDataDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0x33EF5350),
                                contentColor = Color(0xFFFF8A80)
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
                    color = Color(0xFF182238),
                    border = BorderStroke(1.dp, Color(0x1F42A5F5))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = Color(0xFFFFA726),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Custom Keywords",
                                color = Color.White,
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
                                    Text("Enter keyword...", color = Color(0xFF78909C), fontSize = 11.sp)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("keyword_input_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF00E5FF),
                                    unfocusedBorderColor = Color(0x4442A5F5),
                                    cursorColor = Color(0xFF00E5FF)
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
                                    containerColor = Color(0xFF00E5FF),
                                    contentColor = Color(0xFF0A0F1D)
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
                                        color = Color(0x33FF7043),
                                        border = BorderStroke(1.dp, Color(0x66FF7043)),
                                        modifier = Modifier.testTag("protected_keyword_$kw")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Protected",
                                                tint = Color(0xFFFF7043),
                                                modifier = Modifier.size(9.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = kw,
                                                color = Color(0xFFFFCCBC),
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
                    color = Color(0xFF182238),
                    border = BorderStroke(1.dp, Color(0x1F42A5F5))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = Color(0xFF42A5F5),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Download Protection",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        DownloadItemCompact(label = "Video (.mp4, .mkv)", isBlocked = true)
                        DownloadItemCompact(label = "Audio (.mp3, .wav)", isBlocked = true)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = if (isBlocked) Color(0xFFB0BEC5) else Color(0xFFECEFF1),
            fontSize = 11.sp
        )
        if (isBlocked) {
            Surface(
                shape = RoundedCornerShape(3.dp),
                color = Color(0x33EF5350)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Permanently Blocked",
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(9.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Blocked",
                        color = Color(0xFFEF5350),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(3.dp),
                color = Color(0x3366BB6A)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Allowed",
                        tint = Color(0xFF66BB6A),
                        modifier = Modifier.size(9.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Allowed",
                        color = Color(0xFF66BB6A),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
