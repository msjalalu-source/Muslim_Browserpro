package com.muslim.browser.pro.browser.ui

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
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
import com.muslim.browser.pro.browser.BrowserTab
import com.muslim.browser.pro.browser.BrowserUiState

/**
 * Minimal Dropdown for Open Windows.
 * Triggered by long-pressing the '+' button in BottomNavBar.
 *
 * Characteristics:
 * - Small floating card positioned directly above the bottom nav '+' button.
 * - Dark theme with simple rounded corners, minimal border, minimal shadow.
 * - No header, no window count, no '+ New Window' button inside.
 * - Starts directly with the window list.
 * - Active window highlighted with a brighter background and thin border (no "Active" badge).
 * - Overall visual size is ~20% larger for comfortable legibility and tap ergonomics.
 * - Tapping outside dismisses the dropdown.
 */
@Composable
fun OpenWindowsDialog(
    uiState: BrowserUiState,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: () -> Unit = {},
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Backdrop: lightweight click-to-dismiss layer that keeps the browser page visible
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.25f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
            .testTag("open_windows_dialog"),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Minimal Dropdown Card (positioned above bottom navigation bar, ~20% larger scale)
        Surface(
            modifier = Modifier
                .padding(bottom = 68.dp, start = 16.dp, end = 16.dp)
                .widthIn(max = 360.dp)
                .fillMaxWidth(0.92f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume clicks to prevent dismiss */ }
                .testTag("open_windows_panel"),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF131D31),
            border = BorderStroke(1.dp, Color(0x3342A5F5)),
            shadowElevation = 4.dp
        ) {
            // Direct Window List without Header or '+ New Window' button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .heightIn(max = 340.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("open_windows_list"),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                uiState.tabs.forEach { tab ->
                    val isActive = tab.id == uiState.currentTabId
                    WindowTabItem(
                        tab = tab,
                        isActive = isActive,
                        canClose = uiState.tabs.size > 1,
                        onClick = { onSelectTab(tab.id) },
                        onClose = { onCloseTab(tab.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WindowTabItem(
    tab: BrowserTab,
    isActive: Boolean,
    canClose: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isActive) Color(0xFF1E2E4A) else Color(0xFF162032),
        border = BorderStroke(
            width = 1.dp,
            color = if (isActive) Color(0xFF00E5FF) else Color(0x2242A5F5)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("window_tab_item_${tab.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon (~20% larger: 20.dp)
                Icon(
                    imageVector = if (tab.isHomePage) Icons.Default.Home else Icons.Default.Language,
                    contentDescription = if (tab.isHomePage) "Home Page" else "Web Page",
                    tint = if (isActive) Color(0xFF00E5FF) else Color(0xFF81D4FA),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Title & Subtitle (~20% larger typography: 14sp & 11.5sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (tab.isHomePage) "Browser Home Page" else tab.pageTitle.ifEmpty { "Web Page" },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (tab.isHomePage) "Home" else tab.url.ifEmpty { "about:blank" },
                        color = if (isActive) Color(0xFF80DEEA) else Color(0xFF78909C),
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Close button [×] (No "Active" badge, just close icon)
            if (canClose) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("close_tab_button_${tab.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Window",
                        tint = Color(0xFF90A4AE),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
