package com.muslim.browser.pro.browser

import android.view.InputDevice
import android.view.MotionEvent
import android.webkit.WebView
import java.util.WeakHashMap

/**
 * ZoomController for Muslim Browser Pro.
 * Provides live, non-destructive webpage zoom with mouse-cursor and touch focal point centering.
 *
 * Designed to preserve content under the user's cursor or touch position during Zoom In and Zoom Out,
 * completely independent of Desktop Width and viewport configurations.
 */
object ZoomController {

    const val DEFAULT_ZOOM: Int = 100
    const val MIN_ZOOM: Int = 50
    const val MAX_ZOOM: Int = 300
    const val STEP_ZOOM: Int = 10

    /**
     * Tracker for storing the last interaction coordinates per WebView.
     */
    data class FocalPoint(
        val x: Float,
        val y: Float,
        val isMouse: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val focalMap = WeakHashMap<WebView, FocalPoint>()

    /**
     * In-page tracking script that captures real-time DOM cursor and touch positions.
     * Idempotent and lightweight: uses passive capturing listeners without mutating state.
     */
    const val TRACKER_SCRIPT: String = """(function() {
    if (window.__mb_zoom_tracker_installed__) return;
    window.__mb_zoom_tracker_installed__ = true;
    window.__mb_last_mouse_x = null;
    window.__mb_last_mouse_y = null;
    window.__mb_last_touch_x = null;
    window.__mb_last_touch_y = null;

    document.addEventListener('mousemove', function(e) {
        window.__mb_last_mouse_x = e.clientX;
        window.__mb_last_mouse_y = e.clientY;
    }, { passive: true, capture: true });

    document.addEventListener('touchstart', function(e) {
        if (e.touches && e.touches[0]) {
            window.__mb_last_touch_x = e.touches[0].clientX;
            window.__mb_last_touch_y = e.touches[0].clientY;
        }
    }, { passive: true, capture: true });

    document.addEventListener('touchmove', function(e) {
        if (e.touches && e.touches[0]) {
            window.__mb_last_touch_x = e.touches[0].clientX;
            window.__mb_last_touch_y = e.touches[0].clientY;
        }
    }, { passive: true, capture: true });
})();"""

    /**
     * Calculates the new scroll position needed to keep the content under (focalX, focalY)
     * at the exact same screen position when scaling from oldZoomPercent to newZoomPercent.
     */
    fun calculateFocalScroll(
        scrollX: Int,
        scrollY: Int,
        focalX: Float,
        focalY: Float,
        oldZoomPercent: Int,
        newZoomPercent: Int
    ): Pair<Int, Int> {
        val oldZoom = oldZoomPercent.coerceAtLeast(1)
        val newZoom = newZoomPercent.coerceIn(MIN_ZOOM, MAX_ZOOM)
        val ratio = newZoom.toFloat() / oldZoom.toFloat()

        val newScrollX = Math.round((scrollX + focalX) * ratio - focalX).toInt().coerceAtLeast(0)
        val newScrollY = Math.round((scrollY + focalY) * ratio - focalY).toInt().coerceAtLeast(0)
        return Pair(newScrollX, newScrollY)
    }

    /**
     * Generates the JavaScript needed to apply a target zoom percentage with focal point preservation.
     */
    fun buildZoomScript(
        targetPercent: Int,
        explicitFocalX: Float? = null,
        explicitFocalY: Float? = null
    ): String {
        val clamped = targetPercent.coerceIn(MIN_ZOOM, MAX_ZOOM)
        val fxStr = explicitFocalX?.let { "%.1f".format(java.util.Locale.US, it) } ?: "null"
        val fyStr = explicitFocalY?.let { "%.1f".format(java.util.Locale.US, it) } ?: "null"

        return """(function() {
    var targetPercent = $clamped;
    var targetScale = targetPercent / 100.0;
    var currentScale = parseFloat(document.documentElement.style.zoom) || 1.0;
    if (Math.abs(currentScale - targetScale) < 0.001) return;

    // 1. Determine focal point: explicit -> DOM mouse -> DOM touch -> viewport center
    var fx = $fxStr;
    var fy = $fyStr;
    if (fx === null || fy === null || isNaN(fx) || isNaN(fy)) {
        fx = window.__mb_last_mouse_x;
        fy = window.__mb_last_mouse_y;
    }
    if (fx === null || fy === null || isNaN(fx) || isNaN(fy)) {
        fx = window.__mb_last_touch_x;
        fy = window.__mb_last_touch_y;
    }
    var winW = window.innerWidth || document.documentElement.clientWidth || 360;
    var winH = window.innerHeight || document.documentElement.clientHeight || 640;
    if (fx === null || fy === null || isNaN(fx) || isNaN(fy) || fx < 0 || fx > winW || fy < 0 || fy > winH) {
        fx = winW / 2;
        fy = winH / 2;
    }

    // 2. Measure current scroll position
    var scrollX = window.scrollX || window.pageXOffset || 0;
    var scrollY = window.scrollY || window.pageYOffset || 0;

    // 3. Compute compensated scroll coordinates around the focal point
    var ratio = targetScale / currentScale;
    var newScrollX = Math.round((scrollX + fx) * ratio - fx);
    var newScrollY = Math.round((scrollY + fy) * ratio - fy);

    // 4. Apply live zoom and align viewport
    document.documentElement.style.zoom = targetScale.toString();
    window.scrollTo(Math.max(0, newScrollX), Math.max(0, newScrollY));
})();"""
    }

    /**
     * Installs tracking on the WebView through both Android Motion/Touch listeners
     * and in-page JavaScript event listeners.
     */
    fun attachFocalTracker(webView: WebView) {
        // Track mouse hover and scroll events on Android view level
        webView.setOnGenericMotionListener { _, event ->
            val isMouse = (event.source and InputDevice.SOURCE_MOUSE == InputDevice.SOURCE_MOUSE) ||
                    (event.getToolType(0) == MotionEvent.TOOL_TYPE_MOUSE)
            if (isMouse) {
                when (event.actionMasked) {
                    MotionEvent.ACTION_HOVER_MOVE,
                    MotionEvent.ACTION_HOVER_ENTER,
                    MotionEvent.ACTION_SCROLL -> {
                        focalMap[webView] = FocalPoint(event.x, event.y, isMouse = true)
                    }
                }
            }
            false
        }

        // Track touch events on Android view level
        webView.setOnTouchListener { _, event ->
            val isMouse = (event.source and InputDevice.SOURCE_MOUSE == InputDevice.SOURCE_MOUSE) ||
                    (event.getToolType(0) == MotionEvent.TOOL_TYPE_MOUSE)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    focalMap[webView] = FocalPoint(event.x, event.y, isMouse = isMouse)
                }
            }
            false
        }
    }

    /**
     * Injects the in-page tracker script to ensure client-side DOM events are tracked as well.
     */
    fun installTracker(webView: WebView) {
        try {
            webView.evaluateJavascript(TRACKER_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    /**
     * Applies the target zoom percentage to the WebView with focal point preservation.
     */
    fun applyZoom(webView: WebView, targetPercent: Int) {
        val lastPoint = focalMap[webView]
        val script = buildZoomScript(targetPercent, lastPoint?.x, lastPoint?.y)
        try {
            webView.evaluateJavascript(script, null)
        } catch (_: Throwable) {}
    }

    /**
     * Resets the zoom of the given WebView back to 100%.
     */
    fun resetZoom(webView: WebView) {
        applyZoom(webView, DEFAULT_ZOOM)
    }

    fun getLastFocalPoint(webView: WebView): FocalPoint? = focalMap[webView]
}
