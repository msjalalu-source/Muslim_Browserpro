package com.example.browser.ui

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.browser.BrowserUiState

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
        // Compact Top Web Address Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0F172A),
            shadowElevation = 4.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 30% visual size reduction applied (from 16.dp to 11.dp)
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secure Connection",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(11.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            TextField(
                                value = if (isEditingUrl) editUrlText else uiState.currentUrl,
                                onValueChange = {
                                    isEditingUrl = true
                                    editUrlText = it
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("browser_top_url_bar"),
                                singleLine = true,
                                placeholder = {
                                    Text(
                                        text = "Search or enter address...",
                                        color = Color(0xFF78909C),
                                        fontSize = 12.sp
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color(0xFFECEFF1),
                                    cursorColor = Color(0xFF00E5FF),
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                                keyboardActions = KeyboardActions(
                                    onGo = {
                                        isEditingUrl = false
                                        onUrlSubmit(editUrlText)
                                    }
                                )
                            )

                            if (isEditingUrl && editUrlText.isNotEmpty()) {
                                IconButton(
                                    onClick = { editUrlText = "" },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    // 30% visual size reduction applied (from 16.dp to 11.dp)
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color(0xFF90A4AE),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onReload,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("browser_refresh_button")
                    ) {
                        // 30% visual size reduction applied (from 20.dp to 14.dp)
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh page",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
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
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { webView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
