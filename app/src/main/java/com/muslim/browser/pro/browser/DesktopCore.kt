package com.muslim.browser.pro.browser

import android.content.Context
import android.net.Uri
import android.webkit.WebView
import java.util.Locale

object DesktopCore {
    const val TARGET_WIDTH: Int = 1280
    const val VIEWPORT_CONTENT: String = "width=1280"

    const val DESKTOP_USER_AGENT: String =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

    const val WINDOWS_10_TOUCH_USER_AGENT: String =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

    /**
     * Ultra-lightweight Desktop Viewport Script:
     * - Idempotent one-time execution flag (__mb_desktop_applied__)
     * - Directly configures target viewport (width=1280) once
     * - Zero MutationObservers (eliminates background CPU/memory observer loops)
     * - Zero navigation event listeners (no turbo/pjax/pageshow listener overhead)
     * - Relies strictly on native WebSettings for User-Agent (no navigator spoofing overhead)
     */
    const val DESKTOP_VIEWPORT_SCRIPT: String = """(function() {
    if (window.__mb_desktop_applied__) return;
    window.__mb_desktop_applied__ = true;
    var TARGET = 'width=1280';
    try {
        var meta = document.querySelector('meta[name="viewport"]');
        if (!meta) {
            meta = document.createElement('meta');
            meta.name = 'viewport';
            (document.head || document.documentElement).appendChild(meta);
        }
        if (meta.getAttribute('content') !== TARGET) {
            meta.setAttribute('content', TARGET);
        }
    } catch(e) {}
})();"""

    private const val CLEANUP_DESKTOP_SCRIPT: String = """(function() {
    try {
        delete window.__mb_desktop_applied__;
    } catch(e) {}
})();"""

    fun isAuthenticationUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val host = try {
            Uri.parse(url).host?.lowercase(Locale.ROOT)
        } catch (_: Throwable) {
            null
        } ?: return false

        val authHosts = listOf(
            "accounts.google.com",
            "appleid.apple.com",
            "login.microsoftonline.com",
            "login.live.com"
        )
        return authHosts.any { host == it || host.endsWith(".$it") }
    }

    fun applyCommonDesktopWebViewSettings(webView: WebView) {
        val settings = webView.settings
        if (!settings.useWideViewPort) settings.useWideViewPort = true
        if (!settings.loadWithOverviewMode) settings.loadWithOverviewMode = true
        if (settings.textZoom != 100) settings.textZoom = 100
        if (!settings.builtInZoomControls) settings.builtInZoomControls = true
        if (settings.displayZoomControls) settings.displayZoomControls = false
    }

    fun applyCommonDesktopViewport(webView: WebView) {
        try {
            webView.evaluateJavascript(DESKTOP_VIEWPORT_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    fun cleanupCommonDesktopState(webView: WebView) {
        try {
            webView.evaluateJavascript(CLEANUP_DESKTOP_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    fun handlePageLifecycle(webView: WebView, architecture: DesktopArchitecture, url: String) {
        if (!architecture.isAnyDesktop()) return
        when (architecture) {
            DesktopArchitecture.NONE -> {}
            DesktopArchitecture.STANDARD -> {
                applyCommonDesktopWebViewSettings(webView)
                if (!isAuthenticationUrl(url)) {
                    applyCommonDesktopViewport(webView)
                }
            }
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                applyCommonDesktopWebViewSettings(webView)
                if (!isAuthenticationUrl(url)) {
                    try {
                        WebViewConfigurator.injectWindows10TouchProfileIfEnabled(webView, true)
                    } catch (_: Throwable) {}
                    applyCommonDesktopViewport(webView)
                }
            }
        }
    }

    fun synchronizeDesktopWebView(
        webView: WebView,
        url: String?,
        architecture: DesktopArchitecture,
        updateUserAgent: Boolean = true
    ) {
        val targetUa = when (architecture) {
            DesktopArchitecture.NONE -> null
            DesktopArchitecture.STANDARD -> {
                if (url != null && isAuthenticationUrl(url)) null
                else DESKTOP_USER_AGENT
            }
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                if (url != null && isAuthenticationUrl(url)) null
                else WINDOWS_10_TOUCH_USER_AGENT
            }
        }

        val settings = webView.settings
        if (updateUserAgent && settings.userAgentString != targetUa) {
            settings.userAgentString = targetUa
        }

        when (architecture) {
            DesktopArchitecture.NONE -> {
                cleanupCommonDesktopState(webView)
            }
            DesktopArchitecture.STANDARD -> {
                applyCommonDesktopWebViewSettings(webView)
                if (url != null && !isAuthenticationUrl(url)) {
                    applyCommonDesktopViewport(webView)
                }
            }
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                applyCommonDesktopWebViewSettings(webView)
                if (url != null && !isAuthenticationUrl(url)) {
                    try {
                        WebViewConfigurator.injectWindows10TouchProfileIfEnabled(webView, true)
                    } catch (_: Throwable) {}
                    applyCommonDesktopViewport(webView)
                }
            }
        }
    }

    @JvmStatic
    fun migrateSavedArchitecture(context: Context) {
        try {
            val prefs = context.getSharedPreferences("focus_shield_prefs", Context.MODE_PRIVATE)
            val saved = prefs.getString("key_desktop_architecture", null)
            if (saved != null) {
                when (saved) {
                    "DESKTOP_MODE_4", "DESKTOP_MODE_11", "DESKTOP_MODE_12", "WINDOWS_7" -> {
                        prefs.edit().putString("key_desktop_architecture", DesktopArchitecture.STANDARD.name).apply()
                    }
                }
            }
        } catch (_: Throwable) {}
    }
}
