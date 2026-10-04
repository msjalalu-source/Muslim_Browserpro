package com.muslim.browser.pro.browser

import android.webkit.WebView

/**
 * Shared Desktop Core responsible for common desktop-screen and WebView behavior
 * across desktop modes (specifically STANDARD Desktop Mode and WINDOWS_10_TOUCH profile).
 *
 * Responsibilities:
 * - Common native WebView desktop settings (useWideViewPort, loadWithOverviewMode, textZoom, zoom controls)
 * - Common desktop viewport enforcement (width=1280, MutationObserver, Turbo/PJAX navigation events)
 * - Common lifecycle handling (onPageCommitVisible, onPageFinished)
 * - Common cleanup
 * - Authoritative, consolidated WebView synchronization
 *
 * Identity profiles remain distinct:
 * - STANDARD profile: DESKTOP_USER_AGENT + Linux Client Hints
 * - WINDOWS_10_TOUCH profile: WINDOWS_10_TOUCH_USER_AGENT + Windows 10 Touch injection (Win32, 10 touch points, D3D11 GPU)
 */
object DesktopCore {

    const val TARGET_WIDTH: Int = WebViewConfigurator.DESKTOP_VIEWPORT_TARGET_WIDTH
    const val VIEWPORT_CONTENT: String = WebViewConfigurator.DESKTOP_VIEWPORT_CONTENT

    /**
     * Applies common native desktop settings to the WebView:
     * - useWideViewPort = true
     * - loadWithOverviewMode = true
     * - textZoom = 100
     * - builtInZoomControls = true
     * - displayZoomControls = false
     */
    fun applyCommonDesktopWebViewSettings(webView: WebView) {
        webView.settings.apply {
            useWideViewPort = true
            loadWithOverviewMode = true
            textZoom = 100
            builtInZoomControls = true
            displayZoomControls = false
        }
    }

    /**
     * Applies common desktop viewport enforcement to the WebView.
     * Enforces <meta name="viewport" content="width=1280"> and attaches Turbo/PJAX navigation listeners.
     */
    fun applyCommonDesktopViewport(webView: WebView) {
        try {
            webView.evaluateJavascript(WebViewConfigurator.DESKTOP_VIEWPORT_GUARD_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    /**
     * Cleans up common desktop viewport state from the WebView and restores original viewport if saved.
     */
    fun cleanupCommonDesktopState(webView: WebView) {
        try {
            webView.evaluateJavascript(WebViewConfigurator.CLEANUP_ALL_DESKTOP_SCRIPTS, null)
        } catch (_: Throwable) {}
    }

    /**
     * Consolidated lifecycle handling for desktop modes (called from onPageCommitVisible and onPageFinished).
     * Dispatches common desktop viewport and profile-specific scripts exactly once per lifecycle step.
     */
    fun handlePageLifecycle(webView: WebView, architecture: DesktopArchitecture, url: String?) {
        if (!architecture.isAnyDesktop) return
        when (architecture) {
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
            DesktopArchitecture.NONE -> {}
        }
    }

    /**
     * Authoritative synchronization path for all desktop architectures.
     * Ensures:
     * 1. Common desktop WebView settings are applied for all desktop modes.
     * 2. Target User-Agent is determined and applied deterministically.
     * 3. Previous architecture scripts are cleaned up when transitioning away or between architectures.
     * 4. Identity profile (STANDARD vs WINDOWS_10_TOUCH) is applied cleanly without duplicate evaluateJavascript calls.
     */
    fun synchronizeDesktopWebView(
        webView: WebView,
        url: String?,
        architecture: DesktopArchitecture,
        updateUserAgent: Boolean = true
    ) {
        val targetUa = when (architecture) {
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                if (WebViewConfigurator.isAuthenticationUrl(url)) null else WebViewConfigurator.WINDOWS_10_TOUCH_USER_AGENT
            }
            DesktopArchitecture.STANDARD,
            DesktopArchitecture.DESKTOP_MODE_4 -> {
                if (WebViewConfigurator.isAuthenticationUrl(url)) null else WebViewConfigurator.DESKTOP_USER_AGENT
            }
            DesktopArchitecture.NONE -> null
        }

        webView.settings.apply {
            if (updateUserAgent) {
                if (userAgentString != targetUa) {
                    userAgentString = targetUa
                }
            }
            applyCommonDesktopWebViewSettings(webView)
        }

        when (architecture) {
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
            DesktopArchitecture.NONE -> {
                cleanupCommonDesktopState(webView)
            }
        }
    }
}
