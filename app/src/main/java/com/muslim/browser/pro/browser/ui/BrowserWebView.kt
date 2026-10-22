package com.muslim.browser.pro.browser.ui

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.NavigationController
import com.muslim.browser.pro.browser.NavigationDecision
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
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
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.muslim.browser.pro.browser.DesktopCore
import com.muslim.browser.pro.ui.theme.AppTheme
import com.muslim.browser.pro.ui.theme.LocalAppColors

@Composable
fun BrowserWebView(
    uiState: BrowserUiState,
    webView: WebView,
    onUrlSubmit: (String) -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val isEditingUrl = remember { mutableStateOf(false) }
    val editUrlText = remember { mutableStateOf("") }

    LaunchedEffect(uiState.currentUrl) {
        if (!isEditingUrl.value) {
            editUrlText.value = uiState.currentUrl
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
            .testTag("browser_web_view_container")
    ) {
        // Top URL Bar Surface
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = appColors.surface,
            shadowElevation = if (appColors.isMonochrome) 0.dp else 2.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(41.8.dp)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentUrl = uiState.currentUrl.trim()
                    val isHttps = currentUrl.startsWith("https://", ignoreCase = true)
                    val isHttp = currentUrl.startsWith("http://", ignoreCase = true)

                    if (isHttp) {
                        Icon(
                            imageVector = Icons.Filled.LockOpen,
                            contentDescription = "Not secure",
                            modifier = Modifier.size(13.dp),
                            tint = Color(0xFFE53935)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = if (isHttps) "Secure Connection" else "Security",
                            modifier = Modifier.size(13.dp),
                            tint = if (isHttps) appColors.accent else appColors.iconTint
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = appColors.surfaceVariant,
                        border = BorderStroke(0.5.dp, appColors.border)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = if (isEditingUrl.value) editUrlText.value else uiState.currentUrl,
                                onValueChange = {
                                    isEditingUrl.value = true
                                    editUrlText.value = it
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("browser_top_url_bar"),
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = appColors.textPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                ),
                                cursorBrush = SolidColor(appColors.accent),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Go
                                ),
                                keyboardActions = KeyboardActions(
                                    onGo = {
                                        isEditingUrl.value = false
                                        onUrlSubmit(editUrlText.value)
                                    }
                                ),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        val isEmpty = if (isEditingUrl.value) {
                                            editUrlText.value.isEmpty()
                                        } else {
                                            uiState.currentUrl.isEmpty()
                                        }
                                        if (isEmpty) {
                                            Text(
                                                text = "Search or enter address...",
                                                color = appColors.textSecondary,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )

                            if (isEditingUrl.value && editUrlText.value.isNotEmpty()) {
                                IconButton(
                                    onClick = { editUrlText.value = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(12.dp),
                                        tint = appColors.textSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onReload,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh page",
                            modifier = Modifier.size(15.dp),
                            tint = appColors.iconTint
                        )
                    }
                }

                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        progress = { uiState.loadingProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = appColors.accent,
                        trackColor = Color.Transparent
                    )
                }
            }
        }

        if (DebugKeyboardOverlay.DEBUG_OVERLAY) {
            val imeDp = WindowInsets.ime.asPaddingValues().calculateBottomPadding().value
            LaunchedEffect(imeDp) {
                DebugKeyboardOverlay.log("IME", "${imeDp.toInt()}dp")
            }
        }

        // Web Content Viewport Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(appColors.background)
        ) {
            AndroidView(
                factory = { context ->
                    FrameLayout(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        isFocusable = false
                        isFocusableInTouchMode = false
                        descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { container ->
                    val isForceDark = uiState.appTheme == AppTheme.FORCE_DARK_ON
                    if (MainActivity.isDarkThemeActive != isForceDark) {
                        MainActivity.applyWebViewTheme(webView, isForceDark)
                    }

                    // Prevent container from stealing focus from the child WebView
                    if (container.isFocusable || container.isFocusableInTouchMode) {
                        container.isFocusable = false
                        container.isFocusableInTouchMode = false
                        container.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
                    }

                    // Ensure the WebView itself is focusable in touch mode to receive input and suggestions
                    if (!webView.isFocusable || !webView.isFocusableInTouchMode) {
                        webView.isFocusable = true
                        webView.isFocusableInTouchMode = true
                    }

                    if (container.childCount != 1 || container.getChildAt(0) != webView) {
                        val parent = webView.parent as? ViewGroup
                        parent?.removeView(webView)
                        container.removeAllViews()
                        container.addView(
                            webView,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                    }
                }
            )

            if (!uiState.isPageContentVisible && uiState.isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = appColors.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = appColors.accent,
                            strokeWidth = 2.5.dp
                        )
                    }
                }
            }
        }
    }
}
