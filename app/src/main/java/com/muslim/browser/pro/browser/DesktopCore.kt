package com.muslim.browser.pro.browser

import android.content.Context
import android.net.Uri
import android.webkit.WebView
import java.util.Locale

object DesktopCore {
    const val TARGET_WIDTH: Int = 1280
    const val VIEWPORT_CONTENT: String = "width=1280"
    const val DESKTOP_USER_AGENT: String = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
    const val WINDOWS_10_TOUCH_USER_AGENT: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

    /**
     * Desktop Viewport Script (Recovered & Optimized Desktop Mode 4):
     * - Idempotent one-time execution guard (__mb_desktop_guard__)
     * - Saves original viewport in data-mb-orig for clean zero-reload restoration to Mobile
     * - Directly configures target viewport (width=1280) once via ensureViewport
     * - Patches userAgentData and platform (Linux x86_64) for client hints spoofing
     * - Focused meta observer (attachMetaObserver) observing only attributes on the viewport meta tag
     * - SPA event listeners (turbo:load, pjax:end, popstate)
     * - Clean unregistration of listeners and observer disconnect on Mobile restoration
     */
    const val DESKTOP_VIEWPORT_SCRIPT: String = """(function() {
    if (window.__mb_desktop_guard__) {
        if (typeof window.__mb_desktop_guard__.ensureViewport === 'function') {
            window.__mb_desktop_guard__.ensureViewport();
        }
        return;
    }
    var TARGET = 'width=1280';
    var metaObs = null;
    function ensureViewport() {
        var meta = document.querySelector('meta[name="viewport"]');
        if (!meta) {
            meta = document.createElement('meta');
            meta.name = 'viewport';
            meta.setAttribute('data-mb-created', 'true');
            (document.head || document.documentElement).appendChild(meta);
        }
        if (meta.getAttribute('content') !== TARGET) {
            if (!meta.hasAttribute('data-mb-orig') && !meta.hasAttribute('data-mb-created')) {
                meta.setAttribute('data-mb-orig', meta.getAttribute('content') || '');
            }
            meta.setAttribute('content', TARGET);
        }
    }
    function attachMetaObserver() {
        if (!window.MutationObserver) return;
        var meta = document.querySelector('meta[name="viewport"]');
        if (meta) {
            if (metaObs) {
                try { metaObs.disconnect(); } catch(e) {}
            }
            metaObs = new MutationObserver(function() { ensureViewport(); });
            metaObs.observe(meta, { attributes: true, attributeFilter: ['content'] });
        }
    }
    function onNav() { ensureViewport(); }
    var navEvents = ['turbo:load', 'pjax:end', 'popstate'];
    try {
        ensureViewport();
        attachMetaObserver();
        if (navigator.userAgentData) {
            try {
                Object.defineProperty(navigator, 'userAgentData', {
                    get: function() {
                        return {
                            brands: [
                                { brand: 'Chromium', version: '131' },
                                { brand: 'Google Chrome', version: '131' },
                                { brand: 'Not_A Brand', version: '24' }
                            ],
                            mobile: false,
                            platform: 'Linux x86_64'
                        };
                    },
                    configurable: true
                });
            } catch(e) {}
        }
        try {
            Object.defineProperty(navigator, 'platform', {
                get: function() { return 'Linux x86_64'; },
                configurable: true
            });
        } catch(e) {}
        navEvents.forEach(function(evt) {
            window.addEventListener(evt, onNav, { passive: true });
        });
    } catch(e) {}
    window.__mb_desktop_guard__ = {
        ensureViewport: ensureViewport,
        cleanup: function() {
            try {
                if (metaObs) metaObs.disconnect();
                navEvents.forEach(function(evt) {
                    window.removeEventListener(evt, onNav);
                });
            } catch(e) {}
        }
    };
})();"""

    const val DESKTOP_MODE_4_SCRIPT: String = DESKTOP_VIEWPORT_SCRIPT

    private const val CLEANUP_DESKTOP_SCRIPT: String = """(function() {
    try {
        if (window.__mb_desktop_guard__ && window.__mb_desktop_guard__.cleanup) {
            window.__mb_desktop_guard__.cleanup();
        }
        delete window.__mb_desktop_guard__;
        delete window.__mb_desktop_mode4_applied__;
        try { delete navigator.userAgentData; } catch(e) {}
        try { delete navigator.platform; } catch(e) {}
        var meta = document.querySelector('meta[name="viewport"]');
        if (meta) {
            if (meta.hasAttribute('data-mb-created') || meta.hasAttribute('data-mb4-created')) {
                meta.remove();
            } else if (meta.hasAttribute('data-mb-orig') || meta.hasAttribute('data-mb4-orig')) {
                var orig = meta.getAttribute('data-mb-orig') || meta.getAttribute('data-mb4-orig');
                if (orig) meta.setAttribute('content', orig);
                else meta.setAttribute('content', 'width=device-width, initial-scale=1.0');
                meta.removeAttribute('data-mb-orig');
                meta.removeAttribute('data-mb4-orig');
            } else {
                meta.setAttribute('content', 'width=device-width, initial-scale=1.0');
            }
        }
    } catch(e) {}
})();"""

    const val RESTORE_MOBILE_VIEWPORT_SCRIPT: String = CLEANUP_DESKTOP_SCRIPT

    fun isAuthenticationUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val uri = try {
            Uri.parse(url)
        } catch (_: Throwable) {
            return false
        }
        val host = uri.host?.lowercase(Locale.ROOT) ?: return false
        if (host == "github.com" || host.endsWith(".github.com")) return false
        if (host == "accounts.google.com" || host.endsWith(".accounts.google.com")) return true
        if (host == "appleid.apple.com") return true
        if (host == "login.microsoftonline.com") return true
        if (host == "auth.account.sony.com") return true
        if (host.startsWith("auth.") || host.startsWith("id.") || host.startsWith("login.")) return true

        val path = uri.path?.lowercase(Locale.ROOT) ?: ""
        if (path.contains("/oauth2/") || path.contains("/oauth/") || path.contains("/openid-connect/")) return true
        if (path.endsWith("/authorize")) return true

        return false
    }

    fun applyDesktopMode4Settings(webView: WebView) {
        applyCommonDesktopWebViewSettings(webView)
    }

    fun applyDesktopMode4Viewport(webView: WebView) {
        applyCommonDesktopViewport(webView)
    }

    fun applyCommonDesktopViewport(webView: WebView) {
        try {
            webView.evaluateJavascript(DESKTOP_VIEWPORT_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    fun applyCommonDesktopWebViewSettings(webView: WebView) {
        val settings = webView.settings
        if (!settings.useWideViewPort) settings.useWideViewPort = true
        if (!settings.loadWithOverviewMode) settings.loadWithOverviewMode = true
        if (settings.textZoom != 100) settings.textZoom = 100
        if (!settings.builtInZoomControls) settings.builtInZoomControls = true
        if (settings.displayZoomControls) settings.displayZoomControls = false
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
                applyCommonDesktopViewport(webView)
                if (!isAuthenticationUrl(url)) {
                    // Windows 10 Touch profile injection
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
                if (isAuthenticationUrl(url)) null
                else DESKTOP_USER_AGENT
            }
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                if (isAuthenticationUrl(url)) null
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
                if (!isAuthenticationUrl(url)) {
                    applyCommonDesktopViewport(webView)
                }
            }
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                applyCommonDesktopWebViewSettings(webView)
                if (!isAuthenticationUrl(url)) {
                    // Windows 10 Touch profile injection
                }
                applyCommonDesktopViewport(webView)
            }
        }
    }

    fun migrateSavedArchitecture(context: Context) {
        try {
            val prefs = context.getSharedPreferences("focus_shield_prefs", Context.MODE_PRIVATE)
            val saved = prefs.getString("key_desktop_architecture", null) ?: return
            if (saved == "DESKTOP_MODE_4" || saved == "DESKTOP_MODE_11" || saved == "DESKTOP_MODE_12" || saved == "WINDOWS_7") {
                prefs.edit().putString("key_desktop_architecture", "STANDARD").apply()
            }
        } catch (_: Throwable) {}
    }
}
