package com.example.browser.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    isHomePage: Boolean,
    onGoBack: () -> Unit,
    onGoForward: () -> Unit,
    onGoHome: () -> Unit,
    onReload: () -> Unit,
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
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Back Button
            IconButton(
                onClick = onGoBack,
                enabled = canGoBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (canGoBack) Color.White else Color(0x44FFFFFF)
                )
            }

            // Forward Button
            IconButton(
                onClick = onGoForward,
                enabled = canGoForward,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_forward_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = if (canGoForward) Color.White else Color(0x44FFFFFF)
                )
            }

            // Home Button
            IconButton(
                onClick = onGoHome,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_home_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (isHomePage) Color(0xFF00E5FF) else Color.White
                )
            }

            // Reload Button
            IconButton(
                onClick = onReload,
                enabled = !isHomePage,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_reload_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reload",
                    tint = if (!isHomePage) Color.White else Color(0x44FFFFFF)
                )
            }

            // Three-line Menu Button
            IconButton(
                onClick = onOpenMenu,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu and Settings",
                    tint = Color(0xFF00E5FF)
                )
            }
        }
    }
}
