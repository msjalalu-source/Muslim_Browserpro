package com.example.browser.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.browser.BrowserTab
import com.example.browser.BrowserUiState

@Composable
fun OpenWindowsDialog(
    uiState: BrowserUiState,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Backdrop: semi-transparent so the background webpage / home page remains visible
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
            .testTag("open_windows_dialog"),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating compact panel
        Surface(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .widthIn(max = 380.dp)
                .fillMaxWidth(0.92f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume clicks to prevent dismiss */ }
                .testTag("open_windows_panel"),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF131D31),
            border = BorderStroke(1.dp, Color(0x4000E5FF)),
            shadowElevation = 14.dp,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Open Windows",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x2200E5FF),
                            border = BorderStroke(0.5.dp, Color(0xFF00E5FF))
                        ) {
                            Text(
                                text = "${uiState.tabs.size}",
                                color = Color(0xFF00E5FF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("close_windows_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF90A4AE),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0x3342A5F5), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // List of Tabs/Windows
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState())
                        .testTag("open_windows_list"),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    uiState.tabs.forEachIndexed { index, tab ->
                        val isActive = tab.id == uiState.currentTabId
                        WindowTabItem(
                            index = index + 1,
                            tab = tab,
                            isActive = isActive,
                            canClose = uiState.tabs.size > 1,
                            onClick = { onSelectTab(tab.id) },
                            onClose = { onCloseTab(tab.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0x2242A5F5), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Actions: + New Window & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onNewTab,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E5FF),
                            contentColor = Color(0xFF0A0F1D)
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("new_window_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "New Window",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x2690A4AE),
                            contentColor = Color(0xFFECEFF1)
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("dismiss_dialog_button")
                    ) {
                        Text(text = "Close", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun WindowTabItem(
    index: Int,
    tab: BrowserTab,
    isActive: Boolean,
    canClose: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isActive) Color(0x2600E5FF) else Color(0xFF182238),
        border = BorderStroke(
            width = if (isActive) 1.5.dp else 1.dp,
            color = if (isActive) Color(0xFF00E5FF) else Color(0x2242A5F5)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("window_tab_item_${tab.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Index number
                Text(
                    text = "$index.",
                    color = if (isActive) Color(0xFF00E5FF) else Color(0xFF90A4AE),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(22.dp)
                )

                // Icon
                Icon(
                    imageVector = if (tab.isHomePage) Icons.Default.Home else Icons.Default.Language,
                    contentDescription = null,
                    tint = if (isActive) Color(0xFF00E5FF) else Color(0xFF81D4FA),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Title & URL
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (tab.isHomePage) "Browser Home Page" else tab.pageTitle.ifEmpty { "Web Page" },
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (tab.isHomePage) "Home" else tab.url.ifEmpty { "about:blank" },
                        color = if (isActive) Color(0xFF80DEEA) else Color(0xFF78909C),
                        fontSize = 10.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isActive) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF00E5FF)
                    ) {
                        Text(
                            text = "Active",
                            color = Color(0xFF0A0F1D),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                if (canClose) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("close_tab_button_${tab.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Tab",
                            tint = Color(0xFF90A4AE),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
