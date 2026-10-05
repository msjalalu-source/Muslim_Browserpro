package com.muslim.browser.pro.browser

import android.webkit.WebView

object DesktopCore {
    const val TARGET_WIDTH: Int = 1280
    const val VIEWPORT_CONTENT: String = "width=1280"

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
                if (!WebViewConfigurator.isAuthenticationUrl(url)) {
                    WebViewConfigurator.injectWindows10TouchProfileIfEnabled(webView, true)
                }
            }
            DesktopArchitecture.DESKTOP_MODE_4 -> {
                WebViewConfigurator.applyArchitectureViewport(webView, architecture)
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
            DesktopArchitecture.STANDARD, DesktopArchitecture.DESKTOP_MODE_4 -> {
                if (WebViewConfigurator.isAuthenticationUrl(url)) null
                else WebViewConfigurator.DESKTOP_USER_AGENT
            }
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                if (WebViewConfigurator.isAuthenticationUrl(url)) null
                else WebViewConfigurator.WINDOWS_10_TOUCH_USER_AGENT
            }
        }

        val settings = webView.settings
        if (updateUserAgent && settings.userAgentString != targetUa && targetUa != null) {
            settings.userAgentString = targetUa
        }

        applyCommonDesktopWebViewSettings(webView)

        when (architecture) {
            DesktopArchitecture.NONE -> {
                cleanupCommonDesktopState(webView)
            }
            DesktopArchitecture.STANDARD -> {
                applyCommonDesktopViewport(webView)
            }
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                if (!WebViewConfigurator.isAuthenticationUrl(url)) {
                    WebViewConfigurator.injectWindows10TouchProfileIfEnabled(webView, true)
                }
                applyCommonDesktopViewport(webView)
            }
            DesktopArchitecture.DESKTOP_MODE_4 -> {
                WebViewConfigurator.applyArchitectureViewport(webView, architecture)
            }
        }
    }
}
