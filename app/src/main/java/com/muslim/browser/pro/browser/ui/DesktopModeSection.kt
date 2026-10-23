package com.muslim.browser.pro.browser.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.muslim.browser.pro.browser.BrowserUiState
import com.muslim.browser.pro.ui.theme.LocalAppColors

@Composable
fun DesktopSection(
    uiState: BrowserUiState,
    onToggleWindows10Touch: (Boolean) -> Unit
) {
    val colors = LocalAppColors.current
    MenuToggleRow(
        title = "Windows 10 Desktop",
        subtitle = "Experimental browser identity profile",
        icon = Icons.Default.DesktopWindows,
        contentDescription = "Windows 10 Desktop",
        checked = uiState.isWindows10TouchEnabled,
        onCheckedChange = onToggleWindows10Touch,
        onRowClick = {
            onToggleWindows10Touch(!uiState.isWindows10TouchEnabled)
        },
        surfaceColor = colors.surface,
        borderColor = colors.border.copy(alpha = 0.5f),
        iconTint = if (uiState.isWindows10TouchEnabled) colors.accent else colors.textPrimary,
        switchTestTag = "windows_10_touch_switch",
        modifier = Modifier.testTag("item_windows_10_touch")
    )
}
