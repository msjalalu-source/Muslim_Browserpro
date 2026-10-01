package com.muslim.browser.pro.browser.ui

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.muslim.browser.pro.MainActivity
import com.muslim.browser.pro.browser.BrowserUiState
import com.muslim.browser.pro.ui.theme.LocalAppColors

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserWebView(
    uiState: BrowserUiState,
    webView: WebView,
    onUrlSubmit: (String) -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    var isEditingUrl by remember { mutableStateOf(false) }
    var editUrlText by remember { mutableStateOf("") }

    // Synchronize edit field with actual active URL when external navigation occurs
    LaunchedEffect(uiState.currentUrl) {
        if (!isEditingUrl) {
            editUrlText = uiState.currentUrl
        }
    }

    val canvasBg = colors.background

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("browser_web_view_container")
    ) {
        // Top Web Address Bar (38.dp × 1.10 = 41.8.dp)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.surface,
            shadowElevation = if (colors.isMonochrome) 0.dp else 2.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(41.8.dp)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secure Connection",
                        tint = colors.accent,
                        modifier = Modifier.size(13.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = colors.surfaceVariant,
                        border = BorderStroke(0.5.dp, colors.border)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp)
                        ) {
                            BasicTextField(
                                value = if (isEditingUrl) editUrlText else uiState.currentUrl,
                                onValueChange = {
                                    isEditingUrl = true
                                    editUrlText = it
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("browser_top_url_bar"),
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = colors.textPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                ),
                                cursorBrush = SolidColor(colors.accent),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                                keyboardActions = KeyboardActions(
                                    onGo = {
                                        isEditingUrl = false
                                        onUrlSubmit(editUrlText)
                                    }
                                ),
                                decorationBox = { innerTextField ->
                                    Box(
                                        contentAlignment = Alignment.CenterStart,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if ((isEditingUrl && editUrlText.isEmpty()) || (!isEditingUrl && uiState.currentUrl.isEmpty())) {
                                            Text(
                                                text = "Search or enter address...",
                                                color = colors.textSecondary,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )

                            if (isEditingUrl && editUrlText.isNotEmpty()) {
                                IconButton(
                                    onClick = { editUrlText = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = colors.textSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onReload,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("browser_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh page",
                            tint = colors.iconTint,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Progress indicator during loading
                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        progress = { uiState.loadingProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = colors.accent,
                        trackColor = colors.accent.copy(alpha = 0.2f)
                    )
                } else {
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }

        // Web Content Canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(canvasBg)
        ) {
            AndroidView(
                factory = { context ->
                    android.widget.FrameLayout(context).apply {
                        layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { container ->
                    val isDark = !colors.isLight
                    if (MainActivity.isDarkThemeActive != isDark) {
                        MainActivity.applyWebViewTheme(webView, isDark)
                    }
                    if (container.childCount != 1 || container.getChildAt(0) !== webView) {
                        (webView.parent as? android.view.ViewGroup)?.removeView(webView)
                        container.removeAllViews()
                        container.addView(
                            webView,
                            android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Lightweight neutral loading transition surface
            if (!uiState.isPageContentVisible && uiState.isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = canvasBg
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = colors.accent,
                            strokeWidth = 2.5.dp
                        )
                    }
                }
            }
        }
    }
}
