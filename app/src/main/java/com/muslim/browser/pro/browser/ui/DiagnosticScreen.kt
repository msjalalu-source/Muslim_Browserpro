package com.muslim.browser.pro.browser.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.WebView
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslim.browser.pro.ui.theme.LocalAppColors
import org.json.JSONObject
import org.json.JSONTokener

data class DiagnosticData(
    val webViewUserAgentString: String = "",
    val windowInnerWidth: String = "Fetching...",
    val windowInnerHeight: String = "Fetching...",
    val windowOuterWidth: String = "Fetching...",
    val windowOuterHeight: String = "Fetching...",
    val documentClientWidth: String = "Fetching...",
    val documentClientHeight: String = "Fetching...",
    val screenWidth: String = "Fetching...",
    val screenHeight: String = "Fetching...",
    val devicePixelRatio: String = "Fetching...",
    val navigatorUserAgent: String = "Fetching...",
    val userAgentDataMobile: String = "Fetching...",
    val navigatorMaxTouchPoints: String = "Fetching...",
    val viewportMetaContent: String = "Fetching...",
    val currentWebViewUrl: String = "",
    val desktopModeState: String = "" // "MOBILE MODE" or "DESKTOP MODE"
) {
    fun formatForClipboard(): String {
        return """
            === TEMPORARY DEBUG / DIAGNOSTIC REPORT ===
            [Mode]: $desktopModeState
            [Active URL]: $currentWebViewUrl

            1. webView.settings.userAgentString:
               $webViewUserAgentString
            2. window.innerWidth: $windowInnerWidth
            3. window.innerHeight: $windowInnerHeight
            4. window.outerWidth: $windowOuterWidth
            5. window.outerHeight: $windowOuterHeight
            6. document.documentElement.clientWidth: $documentClientWidth
            7. document.documentElement.clientHeight: $documentClientHeight
            8. screen.width: $screenWidth
            9. screen.height: $screenHeight
            10. window.devicePixelRatio: $devicePixelRatio
            11. navigator.userAgent:
                $navigatorUserAgent
            12. navigator.userAgentData.mobile: $userAgentDataMobile
            13. navigator.maxTouchPoints: $navigatorMaxTouchPoints
            14. document.querySelector('meta[name="viewport"]'):
                $viewportMetaContent
            15. Current WebView URL: $currentWebViewUrl
            16. Current Desktop Mode state: $desktopModeState
            ===========================================
        """.trimIndent()
    }
}

/**
 * Executes a diagnostic script inside the exact active browser WebView to extract
 * live viewport, window, screen, and navigator properties.
 */
fun collectLiveWebViewDiagnostics(
    webView: WebView?,
    isDesktopModeEnabled: Boolean,
    onResult: (DiagnosticData) -> Unit
) {
    if (webView == null) {
        onResult(
            DiagnosticData(
                desktopModeState = if (isDesktopModeEnabled) "DESKTOP MODE" else "MOBILE MODE",
                windowInnerWidth = "Error: WebView is null"
            )
        )
        return
    }

    val uaSettings = try { webView.settings.userAgentString ?: "" } catch (_: Exception) { "" }
    val currentUrl = try { webView.url ?: "" } catch (_: Exception) { "" }
    val modeState = if (isDesktopModeEnabled) "DESKTOP MODE" else "MOBILE MODE"

    val js = """
        (function() {
            try {
                var vp = document.querySelector('meta[name="viewport"]');
                var vpContent = vp ? vp.getAttribute('content') : 'NONE (No meta[name=viewport] tag found)';
                var uadMobile = 'Not available (navigator.userAgentData undefined)';
                if (window.navigator && window.navigator.userAgentData && typeof window.navigator.userAgentData.mobile !== 'undefined') {
                    uadMobile = String(window.navigator.userAgentData.mobile);
                }
                return JSON.stringify({
                    innerWidth: String(window.innerWidth),
                    innerHeight: String(window.innerHeight),
                    outerWidth: String(window.outerWidth),
                    outerHeight: String(window.outerHeight),
                    clientWidth: String(document.documentElement ? document.documentElement.clientWidth : 'N/A'),
                    clientHeight: String(document.documentElement ? document.documentElement.clientHeight : 'N/A'),
                    screenWidth: String(window.screen ? window.screen.width : 'N/A'),
                    screenHeight: String(window.screen ? window.screen.height : 'N/A'),
                    devicePixelRatio: String(window.devicePixelRatio),
                    navigatorUserAgent: String(window.navigator.userAgent),
                    userAgentDataMobile: uadMobile,
                    maxTouchPoints: String(window.navigator.maxTouchPoints),
                    viewportMeta: vpContent
                });
            } catch(e) {
                return JSON.stringify({ error: e.toString() });
            }
        })()
    """.trimIndent()

    try {
        webView.evaluateJavascript(js) { rawResult ->
            if (rawResult == null || rawResult == "null") {
                onResult(
                    DiagnosticData(
                        webViewUserAgentString = uaSettings,
                        currentWebViewUrl = currentUrl,
                        desktopModeState = modeState,
                        windowInnerWidth = "Unavailable (evaluateJavascript returned null)"
                    )
                )
                return@evaluateJavascript
            }
            try {
                val cleanJson = if (rawResult.startsWith("\"") && rawResult.endsWith("\"")) {
                    JSONTokener(rawResult).nextValue().toString()
                } else {
                    rawResult
                }
                val obj = JSONObject(cleanJson)
                onResult(
                    DiagnosticData(
                        webViewUserAgentString = uaSettings,
                        windowInnerWidth = obj.optString("innerWidth", "N/A"),
                        windowInnerHeight = obj.optString("innerHeight", "N/A"),
                        windowOuterWidth = obj.optString("outerWidth", "N/A"),
                        windowOuterHeight = obj.optString("outerHeight", "N/A"),
                        documentClientWidth = obj.optString("clientWidth", "N/A"),
                        documentClientHeight = obj.optString("clientHeight", "N/A"),
                        screenWidth = obj.optString("screenWidth", "N/A"),
                        screenHeight = obj.optString("screenHeight", "N/A"),
                        devicePixelRatio = obj.optString("devicePixelRatio", "N/A"),
                        navigatorUserAgent = obj.optString("navigatorUserAgent", "N/A"),
                        userAgentDataMobile = obj.optString("userAgentDataMobile", "N/A"),
                        navigatorMaxTouchPoints = obj.optString("maxTouchPoints", "N/A"),
                        viewportMetaContent = obj.optString("viewportMeta", "N/A"),
                        currentWebViewUrl = currentUrl,
                        desktopModeState = modeState
                    )
                )
            } catch (e: Exception) {
                onResult(
                    DiagnosticData(
                        webViewUserAgentString = uaSettings,
                        currentWebViewUrl = currentUrl,
                        desktopModeState = modeState,
                        windowInnerWidth = "Parse Error: ${e.message}"
                    )
                )
            }
        }
    } catch (e: Exception) {
        onResult(
            DiagnosticData(
                webViewUserAgentString = uaSettings,
                currentWebViewUrl = currentUrl,
                desktopModeState = modeState,
                windowInnerWidth = "Execution Error: ${e.message}"
            )
        )
    }
}

/**
 * Temporary diagnostic screen to inspect live WebView viewport and layout parameters.
 */
@Composable
fun DiagnosticScreen(
    webView: WebView?,
    isDesktopModeEnabled: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current

    var diagnosticData by remember { mutableStateOf(DiagnosticData()) }
    var isRefreshing by remember { mutableStateOf(false) }

    fun refreshData() {
        isRefreshing = true
        collectLiveWebViewDiagnostics(webView, isDesktopModeEnabled) { data ->
            diagnosticData = data
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        refreshData()
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("diagnostic_screen_overlay"),
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header: Temporary Debug Badge & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = "Diagnostic Debug",
                        tint = colors.accent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (colors.isMonochrome) colors.border.copy(alpha = 0.3f) else Color(0xFFEF5350).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, if (colors.isMonochrome) colors.border else Color(0xFFEF5350))
                        ) {
                            Text(
                                text = "TEMPORARY DEBUG / DIAGNOSTIC",
                                color = if (colors.isMonochrome) colors.textPrimary else Color(0xFFEF5350),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "WebView Viewport Inspector",
                            color = colors.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("diagnostic_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Return to Browser",
                        tint = colors.iconTint
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // State Mode Banner: DESKTOP MODE vs MOBILE MODE
            val isDesktop = isDesktopModeEnabled
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = if (isDesktop) colors.accent.copy(alpha = 0.15f) else colors.surfaceVariant,
                border = BorderStroke(1.5.dp, if (isDesktop) colors.accent else colors.border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CURRENT BROWSER STATE",
                            fontSize = 10.sp,
                            color = colors.textSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isDesktop) "DESKTOP MODE" else "MOBILE MODE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDesktop) colors.accent else colors.textPrimary,
                            modifier = Modifier.testTag("diagnostic_mode_label")
                        )
                    }

                    Row {
                        OutlinedButton(
                            onClick = { refreshData() },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colors.textPrimary
                            ),
                            border = BorderStroke(1.dp, colors.border),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("diagnostic_refresh_button"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = colors.textPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Refresh", color = colors.textPrimary, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("WebView Diagnostics", diagnosticData.formatForClipboard())
                                cm.setPrimaryClip(clip)
                                Toast.makeText(context, "Diagnostics copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.buttonBackground,
                                contentColor = colors.buttonText
                            ),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("diagnostic_copy_button"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable Diagnostic Values
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                DiagnosticItemCard(
                    index = 1,
                    label = "webView.settings.userAgentString",
                    value = diagnosticData.webViewUserAgentString,
                    colors = colors
                )
                DiagnosticItemCard(
                    index = 2,
                    label = "window.innerWidth",
                    value = diagnosticData.windowInnerWidth,
                    colors = colors,
                    highlight = true
                )
                DiagnosticItemCard(
                    index = 3,
                    label = "window.innerHeight",
                    value = diagnosticData.windowInnerHeight,
                    colors = colors
                )
                DiagnosticItemCard(
                    index = 4,
                    label = "window.outerWidth",
                    value = diagnosticData.windowOuterWidth,
                    colors = colors
                )
                DiagnosticItemCard(
                    index = 5,
                    label = "window.outerHeight",
                    value = diagnosticData.windowOuterHeight,
                    colors = colors
                )
                DiagnosticItemCard(
                    index = 6,
                    label = "document.documentElement.clientWidth",
                    value = diagnosticData.documentClientWidth,
                    colors = colors,
                    highlight = true
                )
                DiagnosticItemCard(
                    index = 7,
                    label = "document.documentElement.clientHeight",
                    value = diagnosticData.documentClientHeight,
                    colors = colors
                )
                DiagnosticItemCard(
                    index = 8,
                    label = "screen.width",
                    value = diagnosticData.screenWidth,
                    colors = colors
                )
                DiagnosticItemCard(
                    index = 9,
                    label = "screen.height",
                    value = diagnosticData.screenHeight,
                    colors = colors
                )
                DiagnosticItemCard(
                    index = 10,
                    label = "window.devicePixelRatio",
                    value = diagnosticData.devicePixelRatio,
                    colors = colors
                )
                DiagnosticItemCard(
                    index = 11,
                    label = "navigator.userAgent",
                    value = diagnosticData.navigatorUserAgent,
                    colors = colors
                )
                DiagnosticItemCard(
                    index = 12,
                    label = "navigator.userAgentData.mobile",
                    value = diagnosticData.userAgentDataMobile,
                    colors = colors
                )
                DiagnosticItemCard(
                    index = 13,
                    label = "navigator.maxTouchPoints",
                    value = diagnosticData.navigatorMaxTouchPoints,
                    colors = colors
                )
                DiagnosticItemCard(
                    index = 14,
                    label = "document.querySelector('meta[name=\"viewport\"]')",
                    value = diagnosticData.viewportMetaContent,
                    colors = colors,
                    highlight = true
                )
                DiagnosticItemCard(
                    index = 15,
                    label = "Current WebView URL",
                    value = diagnosticData.currentWebViewUrl,
                    colors = colors
                )
                DiagnosticItemCard(
                    index = 16,
                    label = "Current Desktop Mode State",
                    value = diagnosticData.desktopModeState,
                    colors = colors,
                    highlight = true
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Return Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("diagnostic_return_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.buttonBackground,
                    contentColor = colors.buttonText
                )
            ) {
                Text("Return to Browser", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun DiagnosticItemCard(
    index: Int,
    label: String,
    value: String,
    colors: com.muslim.browser.pro.ui.theme.AppColors,
    highlight: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        shape = RoundedCornerShape(6.dp),
        color = if (highlight) colors.surfaceVariant else colors.surface,
        border = BorderStroke(
            0.5.dp,
            if (highlight) colors.accent.copy(alpha = 0.5f) else colors.border.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Text(
                text = "$index. $label",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (highlight) colors.accent else colors.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value.ifBlank { "(empty)" },
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary,
                lineHeight = 16.sp
            )
        }
    }
}
