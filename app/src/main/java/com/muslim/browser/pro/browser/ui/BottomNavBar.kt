package com.muslim.browser.pro.browser.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.muslim.browser.pro.ui.theme.LocalAppColors

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
    val colors = LocalAppColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(if (colors.isLight) colors.surface.copy(alpha = 0.95f) else colors.surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = colors.border
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(41.8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    modifier = Modifier.size(20.dp),
                    tint = if (canGoBack) colors.textPrimary else colors.textSecondary.copy(alpha = 0.35f)
                )
            }

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
                    modifier = Modifier.size(20.dp),
                    tint = if (canGoForward) colors.textPrimary else colors.textSecondary.copy(alpha = 0.35f)
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .combinedClickable(
                        onLongClickLabel = "Show open windows",
                        onLongClick = onShowTabs,
                        onClick = onNewTab
                    )
                    .testTag("nav_plus_button"),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = colors.accent.copy(alpha = 0.15f),
                    border = BorderStroke(1.2.dp, colors.accent)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "New Tab",
                            modifier = Modifier.size(20.dp),
                            tint = colors.accent
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(
                        onClickLabel = "Menu and Settings",
                        onClick = onOpenMenu
                    )
                    .testTag("nav_menu_button"),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = colors.surfaceVariant,
                    border = BorderStroke(1.2.dp, colors.border)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu and Settings",
                            modifier = Modifier.size(20.dp),
                            tint = colors.iconTint
                        )
                    }
                }
            }
        }
    }
}
