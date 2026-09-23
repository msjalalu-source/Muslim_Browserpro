package com.muslim.browser.pro.browser.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomNavBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    isDesktopModeEnabled: Boolean,
    onGoBack: () -> Unit,
    onGoForward: () -> Unit,
    onNewTab: () -> Unit,
    onShowTabs: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onOpenMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xF00A0F1D))
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = Color(0x3342A5F5)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(41.8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // 1. Back Button
            IconButton(
                onClick = onGoBack,
                enabled = canGoBack,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("nav_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (canGoBack) Color.White else Color(0x44FFFFFF),
                    modifier = Modifier.size(20.dp)
                )
            }

            // 2. Forward Button
            IconButton(
                onClick = onGoForward,
                enabled = canGoForward,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("nav_forward_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = if (canGoForward) Color.White else Color(0x44FFFFFF),
                    modifier = Modifier.size(20.dp)
                )
            }

            // 3. Boxed Plus Button [ + ] (Normal Tap: New Window/Tab, Long Press: Show Open Windows)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .combinedClickable(
                        onClick = onNewTab,
                        onLongClick = onShowTabs,
                        onLongClickLabel = "Show open windows"
                    )
                    .testTag("nav_plus_button"),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0x2200E5FF),
                    border = BorderStroke(1.2.dp, Color(0xFF00E5FF)),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Tab",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 4. Desktop Mode Button
            IconButton(
                onClick = onToggleDesktopMode,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("nav_desktop_mode_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DesktopWindows,
                    contentDescription = if (isDesktopModeEnabled) "Desktop Mode Enabled" else "Desktop Mode Disabled",
                    tint = if (isDesktopModeEnabled) Color(0xFF00E5FF) else Color(0xAAFFFFFF),
                    modifier = Modifier.size(20.dp)
                )
            }

            // 5. Three-line Menu Button
            IconButton(
                onClick = onOpenMenu,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("nav_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu and Settings",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
