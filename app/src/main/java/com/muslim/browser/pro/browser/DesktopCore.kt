package com.muslim.browser.pro.browser

import android.webkit.WebView

object DesktopCore {
    const val TARGET_WIDTH: Int = 1280
    const val VIEWPORT_CONTENT: String = "width=1280"

    /**
     * Optimized Desktop Mode 4 Viewport Script:
     * - Idempotent one-time execution flag (__mb_desktop_mode4_applied__)
     * - Enforces width=1280 desktop viewport
     * - Zero permanent MutationObservers (eliminates background CPU/memory observer loops)
     * - Listens to PJAX / Turbo / pageshow navigation events for seamless dynamic page transitions
     * - Preserves desktop Client Hints (navigator.userAgentData and navigator.platform)
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
    try {
        if (navigator.userAgentData) {
            var origUaData = navigator.userAgentData;
            var fakeUaData = {
                brands: origUaData.brands || [
                    { brand: 'Google Chrome', version: '131' },
                    { brand: 'Chromium', version: '131' },
                    { brand: 'Not_A Brand', version: '24' }
                ],
                mobile: false,
                platform: 'Linux',
                getHighEntropyValues: function(hints) {
                    return Promise.resolve({ mobile: false, platform: 'Linux' });
                },
                toJSON: function() {
                    return { brands: this.brands, mobile: false, platform: 'Linux' };
                }
            };
            Object.defineProperty(navigator, 'userAgentData', {
                get: function() { return fakeUaData; },
                configurable: true
            });
        }
    } catch(e) {}
    try {
        Object.defineProperty(navigator, 'platform', {
            get: function() { return 'Linux x86_64'; },
            configurable: true
        });
    } catch(e) {}
    var navEvents = ['turbo:load', 'turbo:render', 'pjax:end', 'pageshow', 'popstate'];
    function onNav() {
        try {
            var m = document.querySelector('meta[name="viewport"]');
            if (m && m.getAttribute('content') !== 'width=1280') {
                m.setAttribute('content', 'width=1280');
            }
        } catch(e) {}
    }
    navEvents.forEach(function(evt) {
        window.addEventListener(evt, onNav, { passive: true });
    });
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
                applyDesktopMode4Viewport(webView)
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
