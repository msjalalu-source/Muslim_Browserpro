package com.muslim.browser.pro.browser

import android.webkit.WebView

/**
 * Desktop Mode 11: Moderately optimized desktop architecture derived from Desktop Mode 4 baseline.
 *
 * Optimizations over Mode 4 baseline:
 * 1. WebSettings Optimization: Checks current values before writing to avoid redundant JNI native calls.
 * 2. Guard Script Optimization:
 *    - Idempotent flag check (`__mb_desktop_mode11__`) prevents redundant execution on the same page.
 *    - Single targeted MutationObserver strictly on the <meta name="viewport"> tag attributes,
 *      eliminating Mode 4's dual <head> childList observer overhead.
 * 3. Lifecycle Optimization: Skips re-attaching when the page is an authentication URL or already guarded.
 */
object DesktopMode11Engine {

    const val TARGET_WIDTH: Int = 1280
    const val VIEWPORT_CONTENT: String = "width=1280"

    const val GUARD_SCRIPT: String = """(function() {
    if (window.__mb_desktop_mode11__) return;
    window.__mb_desktop_mode11__ = true;
    var TARGET = 'width=1280';
    function ensureMeta() {
        var meta = document.querySelector('meta[name="viewport"]');
        if (!meta) {
            meta = document.createElement('meta');
            meta.name = 'viewport';
            (document.head || document.documentElement).appendChild(meta);
        }
        if (meta.getAttribute('content') !== TARGET) {
            meta.setAttribute('content', TARGET);
        }
        return meta;
    }
    var metaEl = ensureMeta();
    if (window.MutationObserver && metaEl) {
        var obs = new MutationObserver(function(muts) {
            for (var i = 0; i < muts.length; i++) {
                if (muts[i].attributeName === 'content' && metaEl.getAttribute('content') !== TARGET) {
                    metaEl.setAttribute('content', TARGET);
                    break;
                }
            }
        });
        obs.observe(metaEl, { attributes: true, attributeFilter: ['content'] });
    }
})();"""

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
        val settings = webView.settings
        if (!settings.useWideViewPort) settings.useWideViewPort = true
        if (!settings.loadWithOverviewMode) settings.loadWithOverviewMode = true
        if (settings.textZoom != 100) settings.textZoom = 100
        if (!settings.builtInZoomControls) settings.builtInZoomControls = true
        if (settings.displayZoomControls) settings.displayZoomControls = false
    }

    fun applyViewport(webView: WebView) {
        try {
            webView.evaluateJavascript(GUARD_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    fun handleLifecycle(webView: WebView, url: String) {
        if (!isAuthenticationUrl(url)) {
            applyViewport(webView)
        }
    }
}
