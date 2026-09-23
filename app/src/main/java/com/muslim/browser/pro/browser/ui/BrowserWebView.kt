package com.muslim.browser.pro.browser.ui

import android.annotation.SuppressLint
import android.webkit.WebView
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
import com.muslim.browser.pro.browser.BrowserUiState

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserWebView(
    uiState: BrowserUiState,
    webView: WebView,
    onUrlSubmit: (String) -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditingUrl by remember { mutableStateOf(false) }
    var editUrlText by remember(uiState.currentUrl) { mutableStateOf(uiState.currentUrl) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .testTag("browser_web_view_container")
    ) {
        // Top Web Address Bar (38.dp × 1.10 = 41.8.dp)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0F172A),
            shadowElevation = 3.dp
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
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(13.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B)
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
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                ),
                                cursorBrush = SolidColor(Color(0xFF00E5FF)),
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
                                                color = Color(0xFF78909C),
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
                                        tint = Color(0xFF90A4AE),
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
                            tint = Color.White,
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
                        color = Color(0xFF00E5FF),
                        trackColor = Color(0x3300E5FF)
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
                .background(Color.White)
        ) {
            AndroidView(
                factory = { webView },
                modifier = Modifier.fillMaxSize()
            )

            // Lightweight neutral loading transition surface:
            // Prevents exposing unrendered/unpainted blank canvas while the first frame is being fetched and prepared.
            // Disappears immediately once onPageCommitVisible paints content or the page finishes.
            if (!uiState.isPageContentVisible && uiState.isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Color(0xFF00E5FF),
                            strokeWidth = 2.5.dp
                        )
                    }
                }
            }
        }
    }
}
