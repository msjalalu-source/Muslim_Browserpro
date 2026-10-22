package com.muslim.browser.pro.browser.ui

import android.webkit.JavascriptInterface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DebugKeyboardOverlay {
    const val DEBUG_OVERLAY: Boolean = true

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    private val _events = MutableStateFlow<List<String>>(emptyList())
    val events = _events.asStateFlow()

    fun log(tag: String, message: String) {
        if (!DEBUG_OVERLAY) return
        val timestamp = synchronized(timeFormat) {
            timeFormat.format(Date())
        }
        val entry = "$timestamp [$tag] $message"
        synchronized(_events) {
            val current = _events.value
            val next = (current + entry).takeLast(10)
            _events.value = next
        }
    }

    class DomBridge {
        @JavascriptInterface
        fun onDomEvent(event: String, tag: String, type: String, details: String) {
            val typeStr = if (type.isNotBlank()) " type='$type'" else ""
            val detailStr = if (details.isNotBlank()) " $details" else ""
            log("DOM", "$event <$tag$typeStr>$detailStr")
        }
    }

    const val DOM_INSPECTION_SCRIPT = """
        (function() {
            try {
                if (window.__mb_debug_overlay_attached__) return;
                window.__mb_debug_overlay_attached__ = true;

                function report(e) {
                    try {
                        var t = e.target || {};
                        var tag = t.tagName ? t.tagName.toLowerCase() : 'window';
                        var type = t.type || '';
                        var details = '';
                        if (e.type === 'resize') {
                            details = window.visualViewport 
                                ? (Math.round(window.visualViewport.width) + 'x' + Math.round(window.visualViewport.height))
                                : (window.innerWidth + 'x' + window.innerHeight);
                        }
                        if (window.DebugKeyboardBridge) {
                            window.DebugKeyboardBridge.onDomEvent(e.type, tag, type, details);
                        }
                    } catch(err) {}
                }

                window.addEventListener('focusin', report, true);
                window.addEventListener('focusout', report, true);
                window.addEventListener('blur', report, true);
                if (window.visualViewport) {
                    window.visualViewport.addEventListener('resize', report);
                } else {
                    window.addEventListener('resize', report);
                }
            } catch(e) {}
        })();
    """
}

@Composable
fun DebugOverlayView(modifier: Modifier = Modifier) {
    if (!DebugKeyboardOverlay.DEBUG_OVERLAY) return

    val events by DebugKeyboardOverlay.events.collectAsState()
    if (events.isEmpty()) return

    Box(
        modifier = modifier
            .padding(top = 46.dp, start = 6.dp)
            .widthIn(max = 280.dp)
            .background(Color.Black.copy(alpha = 0.75f), shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            events.forEach { line ->
                Text(
                    text = line,
                    color = Color(0xFF00FF66),
                    fontSize = 8.sp,
                    lineHeight = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
            }
        }
    }
}
