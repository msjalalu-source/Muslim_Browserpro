package com.muslim.browser.pro.browser

import android.webkit.WebView
import java.util.Collections
import java.util.WeakHashMap

/**
 * Desktop Mode 12: Aggressively optimized desktop architecture derived from Desktop Mode 4 baseline.
 *
 * Deeper optimizations over Mode 11 and Mode 4:
 * 1. Zero MutationObservers:
 *    - Mode 4 uses 2 continuous observers (<head> childList + <meta> attributes).
 *    - Mode 11 uses 1 targeted observer (<meta> attributes).
 *    - Mode 12 completely eliminates MutationObservers. Pure static idempotent injection with zero background listeners running in JavaScript.
 * 2. Strictly Idempotent One-Time WebView Settings Configuration:
 *    - Uses an in-memory WeakHashMap cache to ensure WebSettings are configured strictly once per WebView instance session.
 *    - Subsequent syncs are instantaneous O(1) no-ops, completely bypassing native JNI WebSettings calls.
 * 3. Zero-Allocation Navigation Fast-Path:
 *    - Checks authentication URLs and executes minimal one-line JS guard with instant exit if already present in document.
 *    - Zero Handlers, zero timers, zero coroutines, zero object allocations.
 */
object DesktopMode12Engine {

    const val TARGET_WIDTH: Int = 1280
    const val VIEWPORT_CONTENT: String = "width=1280"

    const val SCRIPT: String = """(function() {
    if (window.__mb_desktop_mode12__) return;
    window.__mb_desktop_mode12__ = true;
    var m = document.querySelector('meta[name="viewport"]');
    if (!m) {
        m = document.createElement('meta');
        m.name = 'viewport';
        (document.head || document.documentElement).appendChild(m);
    }
    m.setAttribute('content', 'width=1280');
})();"""

    private val configuredWebViews: MutableSet<WebView> = Collections.synchronizedSet(
        Collections.newSetFromMap(WeakHashMap<WebView, Boolean>())
    )

    fun isAuthenticationUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val lower = url.lowercase()
        return lower.contains("accounts.google.com") ||
               lower.contains("appleid.apple.com") ||
               lower.contains("login.microsoftonline.com") ||
               lower.contains("auth.account.sony.com") ||
               lower.contains("/oauth2/") ||
               lower.contains("/oauth/") ||
               lower.contains("/openid-connect/") ||
               lower.endsWith("/authorize")
    }

    fun applySettings(webView: WebView) {
        if (configuredWebViews.add(webView)) {
            val settings = webView.settings
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.textZoom = 100
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }
    }

    fun isConfigured(webView: WebView): Boolean = configuredWebViews.contains(webView)

    fun resetCache() {
        configuredWebViews.clear()
    }

    fun cleanupState(webView: WebView) {
        configuredWebViews.remove(webView)
    }

    fun applyViewport(webView: WebView) {
        try {
            webView.evaluateJavascript(SCRIPT, null)
        } catch (_: Throwable) {}
    }

    fun handleLifecycle(webView: WebView, url: String) {
        if (!isAuthenticationUrl(url)) {
            applyViewport(webView)
        }
    }
}
