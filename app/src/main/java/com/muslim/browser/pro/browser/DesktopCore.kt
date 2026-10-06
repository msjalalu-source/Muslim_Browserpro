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
        if (updateUserAgent && settings.userAgentString != targetUa && targetUa != null) {
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
