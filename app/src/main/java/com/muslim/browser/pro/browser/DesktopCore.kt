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
     * Complete Desktop Mode Viewport Guard Script (restored from reference Desktop Mode 4):
     * - Viewport enforcement to width=1280
     * - Client hints spoofing (navigator.userAgentData: mobile=false, platform='Linux')
     * - Platform spoofing (navigator.platform: 'Linux x86_64')
     * - attachMetaObserver(): MutationObserver guarding meta[name="viewport"] attribute mutations
     * - headObserver: MutationObserver guarding document.head childList for dynamic viewport tags
     * - SPA navigation listeners: turbo:load, turbo:render, pjax:end, pageshow, popstate
     * - Idempotent manager via window.__mb_desktop_guard__ exposing ensureViewport() and cleanup()
     */
    const val DESKTOP_VIEWPORT_SCRIPT: String = """(function() {
    var TARGET_CONTENT = 'width=1280';
    var GUARD_KEY = '__mb_desktop_guard__';

    var metaObserver = null;
    var headObserver = null;

    function attachMetaObserver(metaEl) {
        if (!metaEl || !window.MutationObserver) return;
        if (metaObserver) {
            try { metaObserver.disconnect(); } catch(e) {}
        }
        metaObserver = new MutationObserver(function(mutations) {
            for (var i = 0; i < mutations.length; i++) {
                if (mutations[i].attributeName === 'content') {
                    if (metaEl.getAttribute('content') !== TARGET_CONTENT) {
                        applyViewport();
                    }
                    break;
                }
            }
        });
        try {
            metaObserver.observe(metaEl, {
                attributes: true,
                attributeFilter: ['content']
            });
        } catch(e) {}
    }

    function applyViewport() {
        try {
            var head = document.head || document.getElementsByTagName('head')[0] || document.documentElement;
            if (!head) return;
            var meta = document.querySelector('meta[name="viewport"]');
            if (!meta) {
                meta = document.createElement('meta');
                meta.name = 'viewport';
                meta.setAttribute('data-mb-created', 'true');
                head.appendChild(meta);
            }
            if (meta.getAttribute('content') !== TARGET_CONTENT) {
                if (meta.getAttribute('data-mb-orig') === null && !meta.hasAttribute('data-mb-created')) {
                    meta.setAttribute('data-mb-orig', meta.getAttribute('content') || '');
                }
                meta.setAttribute('content', TARGET_CONTENT);
            }
            attachMetaObserver(meta);
        } catch(e) {}
    }

    function patchDesktopClientHints() {
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
                        return origUaData.getHighEntropyValues ?
                            origUaData.getHighEntropyValues(hints).then(function(vals) {
                                vals.mobile = false;
                                vals.platform = 'Linux';
                                return vals;
                            }) :
                            Promise.resolve({ mobile: false, platform: 'Linux' });
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
    }

    if (window[GUARD_KEY]) {
        window[GUARD_KEY].ensureViewport();
        return;
    }

    applyViewport();
    patchDesktopClientHints();

    try {
        var head = document.head || document.getElementsByTagName('head')[0] || document.documentElement;
        if (head && window.MutationObserver) {
            headObserver = new MutationObserver(function(mutations) {
                for (var i = 0; i < mutations.length; i++) {
                    var m = mutations[i];
                    if (m.type === 'childList') {
                        var changed = false;
                        for (var j = 0; j < m.addedNodes.length; j++) {
                            var node = m.addedNodes[j];
                            if (node.nodeType === 1 && node.nodeName === 'META' && (node.name === 'viewport' || node.getAttribute('name') === 'viewport')) {
                                changed = true;
                                break;
                            }
                        }
                        if (!changed) {
                            for (var k = 0; k < m.removedNodes.length; k++) {
                                var rNode = m.removedNodes[k];
                                if (rNode.nodeType === 1 && rNode.nodeName === 'META' && (rNode.name === 'viewport' || rNode.getAttribute('name') === 'viewport')) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            applyViewport();
                            break;
                        }
                    }
                }
            });
            headObserver.observe(head, {
                childList: true,
                subtree: false
            });
        }
    } catch(e) {}

    var navEvents = ['turbo:load', 'turbo:render', 'pjax:end', 'pageshow', 'popstate'];
    function onNav() { applyViewport(); }
    navEvents.forEach(function(evt) {
        window.addEventListener(evt, onNav, { passive: true });
    });

    window[GUARD_KEY] = {
        ensureViewport: applyViewport,
        cleanup: function() {
            try {
                if (headObserver) headObserver.disconnect();
                if (metaObserver) metaObserver.disconnect();
                navEvents.forEach(function(evt) {
                    window.removeEventListener(evt, onNav);
                });
                var meta = document.querySelector('meta[name="viewport"]');
                if (meta) {
                    if (meta.hasAttribute('data-mb-created') || meta.hasAttribute('data-mb4-created')) {
                        meta.remove();
                    } else if (meta.hasAttribute('data-mb-orig') || meta.hasAttribute('data-mb4-orig')) {
                        var orig = meta.getAttribute('data-mb-orig') || meta.getAttribute('data-mb4-orig');
                        if (orig) meta.setAttribute('content', orig);
                        else meta.removeAttribute('content');
                        meta.removeAttribute('data-mb-orig');
                        meta.removeAttribute('data-mb4-orig');
                    }
                }
                try { delete navigator.userAgentData; } catch(e) {}
                try { delete navigator.platform; } catch(e) {}
            } catch(e) {}
            delete window[GUARD_KEY];
        }
    };
})();"""

    private const val CLEANUP_DESKTOP_SCRIPT: String = """(function() {
    var keys = ['__mb_desktop_guard__', '__mb_desktop_mode4__', '__mb_desktop_applied__'];
    for (var i = 0; i < keys.length; i++) {
        var k = keys[i];
        if (window[k] && typeof window[k].cleanup === 'function') {
            try { window[k].cleanup(); } catch(e) {}
        }
        try { delete window[k]; } catch(e) {}
    }
    try {
        var meta = document.querySelector('meta[name="viewport"]');
        if (meta) {
            if (meta.hasAttribute('data-mb-created') || meta.hasAttribute('data-mb4-created')) {
                meta.remove();
            } else if (meta.hasAttribute('data-mb-orig') || meta.hasAttribute('data-mb4-orig')) {
                var orig = meta.getAttribute('data-mb-orig') || meta.getAttribute('data-mb4-orig');
                if (orig) meta.setAttribute('content', orig);
                else meta.removeAttribute('content');
                meta.removeAttribute('data-mb-orig');
                meta.removeAttribute('data-mb4-orig');
            }
        }
    } catch(e) {}
    try { delete navigator.userAgentData; } catch(e) {}
    try { delete navigator.platform; } catch(e) {}
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
