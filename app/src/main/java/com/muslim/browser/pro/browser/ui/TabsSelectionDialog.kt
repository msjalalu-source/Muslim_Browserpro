package com.muslim.browser.pro.browser.ui

import android.view.Gravity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.muslim.browser.pro.browser.BrowserUiState
import com.muslim.browser.pro.ui.theme.LocalAppColors

@Composable
fun TabsSelectionDialog(
    uiState: BrowserUiState,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val view = LocalView.current
        SideEffect {
            var parent = view.parent
            while (parent != null) {
                if (parent is DialogWindowProvider) {
                    parent.window.setGravity(Gravity.BOTTOM)
                    break
                }
                parent = parent.parent
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 44.dp)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .heightIn(max = 380.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("tabs_dialog"),
                color = colors.surface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, colors.border),
                tonalElevation = 6.dp,
                shadowElevation = 10.dp
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(uiState.tabs, key = { it.id }) { tab ->
                        val isCurrent = tab.id == uiState.currentTabId
                        val displayText = if (tab.title.isNotBlank()) {
                            tab.title
                        } else if (tab.url.isNotBlank()) {
                            tab.url
                        } else {
                            "Home"
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onSelectTab(tab.id)
                                    onDismiss()
                                }
                                .testTag("tab_item_${tab.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) colors.accent.copy(alpha = 0.12f) else colors.surfaceVariant
                            ),
                            border = BorderStroke(0.8.dp, if (isCurrent) colors.accent else colors.border)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = displayText,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isCurrent) colors.accent else colors.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { onCloseTab(tab.id) },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .testTag("close_tab_${tab.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close tab",
                                        tint = if (isCurrent) colors.accent else colors.iconTint,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
