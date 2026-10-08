package com.muslim.browser.pro.browser

import android.webkit.WebView

object DesktopCore {
    const val TARGET_WIDTH: Int = 1280
    const val VIEWPORT_CONTENT: String = "width=1280"

    /**
     * Ultra-lightweight Desktop Mode 4 Viewport Script:
     * - Idempotent one-time execution flag (__mb_desktop_mode4_applied__)
     * - Directly configures target viewport (width=1280) once
     * - Zero MutationObservers (eliminates background CPU/memory observer loops)
     * - Zero navigation event listeners (no turbo/pjax/pageshow listener overhead)
     * - Relies strictly on native WebSettings for User-Agent (no navigator spoofing overhead)
     */
    const val DESKTOP_MODE_4_SCRIPT: String = """(function() {
    if (window.__mb_desktop_mode4_applied__) return;
    window.__mb_desktop_mode4_applied__ = true;
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

    fun applyDesktopMode4Settings(webView: WebView) {
        val settings = webView.settings
        if (!settings.useWideViewPort) settings.useWideViewPort = true
        if (!settings.loadWithOverviewMode) settings.loadWithOverviewMode = true
        if (settings.textZoom != 100) settings.textZoom = 100
        if (!settings.builtInZoomControls) settings.builtInZoomControls = true
        if (settings.displayZoomControls) settings.displayZoomControls = false
    }

    fun applyDesktopMode4Viewport(webView: WebView) {
        try {
            webView.evaluateJavascript(DESKTOP_MODE_4_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    fun applyCommonDesktopViewport(webView: WebView) {
        try {
            webView.evaluateJavascript(WebViewConfigurator.DESKTOP_VIEWPORT_GUARD_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    fun applyCommonDesktopWebViewSettings(webView: WebView) {
        val settings = webView.settings
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.textZoom = 100
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
    }

    fun cleanupCommonDesktopState(webView: WebView) {
        DesktopMode12Engine.cleanupState(webView)
        try {
            webView.evaluateJavascript(WebViewConfigurator.CLEANUP_ALL_DESKTOP_SCRIPTS, null)
        } catch (_: Throwable) {}
    }

    fun handlePageLifecycle(webView: WebView, architecture: DesktopArchitecture, url: String) {
        if (!architecture.isAnyDesktop()) return
        when (architecture) {
            DesktopArchitecture.NONE -> {}
            DesktopArchitecture.STANDARD -> {
                applyCommonDesktopViewport(webView)
            }
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                applyCommonDesktopViewport(webView)
                if (!DesktopMode11Engine.isAuthenticationUrl(url)) {
                    WebViewConfigurator.injectWindows10TouchProfileIfEnabled(webView, true)
                }
            }
            DesktopArchitecture.DESKTOP_MODE_4 -> {
                applyDesktopMode4Settings(webView)
                if (!DesktopMode11Engine.isAuthenticationUrl(url)) {
                    applyDesktopMode4Viewport(webView)
                }
            }
            DesktopArchitecture.WINDOWS_7 -> {
                applyCommonDesktopWebViewSettings(webView)
                WebViewConfigurator.applyArchitectureViewport(webView, architecture)
            }
            DesktopArchitecture.DESKTOP_MODE_11 -> {
                DesktopMode11Engine.handleLifecycle(webView, url)
            }
            DesktopArchitecture.DESKTOP_MODE_12 -> {
                DesktopMode12Engine.handleLifecycle(webView, url)
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
            DesktopArchitecture.STANDARD,
            DesktopArchitecture.DESKTOP_MODE_4,
            DesktopArchitecture.WINDOWS_7,
            DesktopArchitecture.DESKTOP_MODE_11,
            DesktopArchitecture.DESKTOP_MODE_12 -> {
                if (DesktopMode11Engine.isAuthenticationUrl(url)) null
                else WebViewConfigurator.DESKTOP_USER_AGENT
            }
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                if (DesktopMode11Engine.isAuthenticationUrl(url)) null
                else WebViewConfigurator.WINDOWS_10_TOUCH_USER_AGENT
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
                applyCommonDesktopViewport(webView)
            }
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                applyCommonDesktopWebViewSettings(webView)
                if (!DesktopMode11Engine.isAuthenticationUrl(url)) {
                    WebViewConfigurator.injectWindows10TouchProfileIfEnabled(webView, true)
                }
                applyCommonDesktopViewport(webView)
            }
            DesktopArchitecture.DESKTOP_MODE_4 -> {
                applyDesktopMode4Settings(webView)
                if (!DesktopMode11Engine.isAuthenticationUrl(url)) {
                    applyDesktopMode4Viewport(webView)
                }
            }
            DesktopArchitecture.WINDOWS_7 -> {
                applyCommonDesktopWebViewSettings(webView)
                WebViewConfigurator.applyArchitectureViewport(webView, architecture)
            }
            DesktopArchitecture.DESKTOP_MODE_11 -> {
                DesktopMode11Engine.applySettings(webView)
                if (!DesktopMode11Engine.isAuthenticationUrl(url)) {
                    DesktopMode11Engine.applyViewport(webView)
                }
            }
            DesktopArchitecture.DESKTOP_MODE_12 -> {
                DesktopMode12Engine.applySettings(webView)
                if (!DesktopMode12Engine.isAuthenticationUrl(url)) {
                    DesktopMode12Engine.applyViewport(webView)
                }
            }
        }
    }
}
