package com.muslim.browser.pro.browser

import android.webkit.WebView

/**
 * SimpleDesktopMode: New, independent, and minimal experimental Desktop Mode architecture.
 *
 * Design principles:
 * - Minimal code: single small object, single apply method.
 * - Minimal state: pure boolean toggle stored directly in settings.
 * - Minimal configuration: uses only native WebSettings (desktop userAgent, wide viewport, overview mode).
 * - Zero complex JavaScript: no MutationObservers, no SPA/Turbo event listeners, no History API interception, no UserAgentData spoofing.
 * - Zero timers, zero polling, zero repeated reloads.
 * - Completely independent from the existing Desktop Mode implementation.
 */
object SimpleDesktopMode {

    const val DESKTOP_USER_AGENT =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

    /**
     * Applies the minimal native WebView configuration for desktop or mobile mode.
     */
    fun apply(webView: WebView, enabled: Boolean) {
        webView.settings.apply {
            userAgentString = if (enabled) DESKTOP_USER_AGENT else null
            useWideViewPort = true
            loadWithOverviewMode = true
        }
    }
}
