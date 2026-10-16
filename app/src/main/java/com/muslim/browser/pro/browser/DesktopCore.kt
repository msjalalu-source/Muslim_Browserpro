package com.muslim.browser.pro.browser

import android.content.Context
import android.net.Uri
import android.webkit.WebView
import java.util.Locale

object DesktopCore {
    const val TARGET_WIDTH: Int = 1280
    const val VIEWPORT_CONTENT: String = "width=1280"
    const val WINDOWS_10_TOUCH_USER_AGENT: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

    /**
     * Windows 10 Touch Desktop Profile Script:
     * - Windows 10 x64 User-Agent & Client Hints emulation
     * - 1920x1080 screen dimensions & 1280x720 desktop viewport
     * - Intel Direct3D11 WebGL vendor & renderer
     * - CSS pointer: fine, coarse touch & hover media queries
     * - Idempotent guard (__mb_win10_touch__) to prevent duplicate listeners
     */
    const val WINDOWS_10_TOUCH_SCRIPT: String = """(function() {
    if (window.__mb_win10_touch__) return;
    window.__mb_win10_touch__ = true;

    // 1. Navigator hardware & platform signals (Windows 10 Desktop/Tablet)
    try {
        var navProto = Object.getPrototypeOf(navigator) || (window.Navigator && window.Navigator.prototype);
        var navProps = {
            platform: { get: function() { return 'Win32'; }, configurable: true },
            vendor: { get: function() { return 'Google Inc.'; }, configurable: true },
            userAgent: { get: function() { return 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36'; }, configurable: true },
            appVersion: { get: function() { return '5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36'; }, configurable: true },
            maxTouchPoints: { get: function() { return 10; }, configurable: true },
            hardwareConcurrency: { get: function() { return 8; }, configurable: true },
            deviceMemory: { get: function() { return 8; }, configurable: true }
        };
        for (var key in navProps) {
            try { Object.defineProperty(navigator, key, navProps[key]); } catch(e) {}
            if (navProto) {
                try { Object.defineProperty(navProto, key, navProps[key]); } catch(e) {}
            }
            if (window.Navigator && window.Navigator.prototype) {
                try { Object.defineProperty(window.Navigator.prototype, key, navProps[key]); } catch(e) {}
            }
        }
    } catch(e) {}

    // 2. User-Agent Client Hints (navigator.userAgentData: platform 'Windows', mobile false)
    try {
        var uaData = {
            brands: [
                { brand: 'Google Chrome', version: '131' },
                { brand: 'Chromium', version: '131' },
                { brand: 'Not_A Brand', version: '24' }
            ],
            mobile: false,
            platform: 'Windows',
            getHighEntropyValues: function(hints) {
                return Promise.resolve({
                    architecture: 'x86',
                    bitness: '64',
                    brands: [
                        { brand: 'Google Chrome', version: '131' },
                        { brand: 'Chromium', version: '131' },
                        { brand: 'Not_A Brand', version: '24' }
                    ],
                    fullVersionList: [
                        { brand: 'Google Chrome', version: '131.0.6778.205' },
                        { brand: 'Chromium', version: '131.0.6778.205' },
                        { brand: 'Not_A Brand', version: '24.0.0.0' }
                    ],
                    mobile: false,
                    model: '',
                    platform: 'Windows',
                    platformVersion: '10.0.0'
                });
            },
            toJSON: function() {
                return { brands: this.brands, mobile: this.mobile, platform: this.platform };
            }
        };
        var uadDescriptor = { get: function() { return uaData; }, configurable: true };
        try { Object.defineProperty(navigator, 'userAgentData', uadDescriptor); } catch(e) {}
        if (navProto) {
            try { Object.defineProperty(navProto, 'userAgentData', uadDescriptor); } catch(e) {}
        }
        if (window.Navigator && window.Navigator.prototype) {
            try { Object.defineProperty(window.Navigator.prototype, 'userAgentData', uadDescriptor); } catch(e) {}
        }
    } catch(e) {}

    // 3. Screen Dimensions (Desktop-class Windows 10: 1920x1080)
    try {
        var screenProto = Object.getPrototypeOf(window.screen) || (window.Screen && window.Screen.prototype);
        var screenProps = {
            width: { get: function() { return 1920; }, configurable: true },
            height: { get: function() { return 1080; }, configurable: true },
            availWidth: { get: function() { return 1920; }, configurable: true },
            availHeight: { get: function() { return 1040; }, configurable: true },
            colorDepth: { get: function() { return 24; }, configurable: true },
            pixelDepth: { get: function() { return 24; }, configurable: true }
        };
        for (var sKey in screenProps) {
            try { Object.defineProperty(window.screen, sKey, screenProps[sKey]); } catch(e) {}
            if (screenProto) {
                try { Object.defineProperty(screenProto, sKey, screenProps[sKey]); } catch(e) {}
            }
            if (window.Screen && window.Screen.prototype) {
                try { Object.defineProperty(window.Screen.prototype, sKey, screenProps[sKey]); } catch(e) {}
            }
        }
    } catch(e) {}

    // 4. Window Viewport & Outer Dimensions (Desktop-consistent: inner/outer width & height, document client dimensions)
    try {
        var desktopInnerW = 1280;
        var desktopInnerH = 720;
        var desktopOuterW = 1280;
        var desktopOuterH = 800;

        var winProto = Object.getPrototypeOf(window) || (window.Window && window.Window.prototype);
        var winProps = {
            innerWidth: { get: function() { return desktopInnerW; }, configurable: true },
            innerHeight: { get: function() { return desktopInnerH; }, configurable: true },
            outerWidth: { get: function() { return desktopOuterW; }, configurable: true },
            outerHeight: { get: function() { return desktopOuterH; }, configurable: true }
        };
        for (var wKey in winProps) {
            try { Object.defineProperty(window, wKey, winProps[wKey]); } catch(e) {}
            if (winProto) {
                try { Object.defineProperty(winProto, wKey, winProps[wKey]); } catch(e) {}
            }
            if (window.Window && window.Window.prototype) {
                try { Object.defineProperty(window.Window.prototype, wKey, winProps[wKey]); } catch(e) {}
            }
        }

        var elemProto = window.Element && window.Element.prototype;
        if (elemProto) {
            var origClientHeightDesc = Object.getOwnPropertyDescriptor(elemProto, 'clientHeight');
            var origClientHeightGet = origClientHeightDesc ? origClientHeightDesc.get : null;
            Object.defineProperty(elemProto, 'clientHeight', {
                get: function() {
                    if (this === document.documentElement || (document.compatMode === 'BackCompat' && this === document.body)) {
                        return desktopInnerH;
                    }
                    return origClientHeightGet ? origClientHeightGet.apply(this, arguments) : 0;
                },
                configurable: true
            });

            var origClientWidthDesc = Object.getOwnPropertyDescriptor(elemProto, 'clientWidth');
            var origClientWidthGet = origClientWidthDesc ? origClientWidthGet.apply(this, arguments) : 0;
            Object.defineProperty(elemProto, 'clientWidth', {
                get: function() {
                    if (this === document.documentElement || (document.compatMode === 'BackCompat' && this === document.body)) {
                        return desktopInnerW;
                    }
                    return origClientWidthGet ? origClientWidthGet.apply(this, arguments) : 0;
                },
                configurable: true
            });
        }

        if (document.documentElement) {
            try {
                Object.defineProperty(document.documentElement, 'clientWidth', {
                    get: function() { return desktopInnerW; },
                    configurable: true
                });
                Object.defineProperty(document.documentElement, 'clientHeight', {
                    get: function() { return desktopInnerH; },
                    configurable: true
                });
            } catch(e) {}
        }
    } catch(e) {}

    // 5. WebGL GPU / Unmasked Renderer Signals (Intel Direct3D11)
    try {
        var UNMASKED_VENDOR_WEBGL = 0x9245;
        var UNMASKED_RENDERER_WEBGL = 0x9246;
        var targetVendor = 'Google Inc. (Intel)';
        var targetRenderer = 'ANGLE (Intel, Intel(R) UHD Graphics 620 Direct3D11 vs_5_0 ps_5_0, D3D11)';

        function patchContext(proto) {
            if (!proto || !proto.getParameter) return;
            var origGet = proto.getParameter;
            proto.getParameter = function(param) {
                if (param === UNMASKED_VENDOR_WEBGL) return targetVendor;
                if (param === UNMASKED_RENDERER_WEBGL) return targetRenderer;
                return origGet.apply(this, arguments);
            };
        }
        if (window.WebGLRenderingContext) patchContext(WebGLRenderingContext.prototype);
        if (window.WebGL2RenderingContext) patchContext(WebGL2RenderingContext.prototype);
    } catch(e) {}

    // 6. CSS Media Queries for Windows 10 Touch (fine pointer + coarse touch + hover)
    try {
        if (window.matchMedia) {
            var origMm = window.matchMedia;
            window.matchMedia = function(q) {
                var res = origMm.apply(this, arguments);
                if (q.indexOf('(hover: hover)') !== -1 || q.indexOf('(any-hover: hover)') !== -1 ||
                    q.indexOf('(pointer: fine)') !== -1 || q.indexOf('(any-pointer: fine)') !== -1 ||
                    q.indexOf('(any-pointer: coarse)') !== -1) {
                    return {
                        matches: true,
                        media: q,
                        onchange: null,
                        addListener: function() {},
                        removeListener: function() {},
                        addEventListener: function() {},
                        removeEventListener: function() {},
                        dispatchEvent: function() { return false; }
                    };
                }
                return res;
            };
        }
    } catch(e) {}
})();"""

    const val COMMON_DESKTOP_VIEWPORT_SCRIPT: String = """(function() {
    try {
        var head = document.head || document.getElementsByTagName('head')[0] || document.documentElement;
        if (!head) return;
        var meta = document.querySelector('meta[name="viewport"]');
        if (!meta) {
            meta = document.createElement('meta');
            meta.name = 'viewport';
            head.appendChild(meta);
        }
        meta.setAttribute('content', 'width=1280');
    } catch(e) {}
})();"""

    private const val CLEANUP_DESKTOP_SCRIPT: String = """(function() {
    var keys = ['__mb_win10_touch__'];
    for (var i = 0; i < keys.length; i++) {
        var k = keys[i];
        if (window[k] && typeof window[k].cleanup === 'function') {
            try { window[k].cleanup(); } catch(e) {}
        }
    }
    delete window.__mb_win10_touch__;
    try { delete navigator.userAgentData; } catch(e) {}
    try { delete navigator.platform; } catch(e) {}
    try {
        var meta = document.querySelector('meta[name="viewport"]');
        if (meta) {
            meta.setAttribute('content', 'width=device-width, initial-scale=1.0');
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

    /**
     * Common Desktop Viewport application:
     * Applies standard 1280px desktop viewport for active desktop profile.
     */
    fun applyCommonDesktopViewport(webView: WebView) {
        try {
            webView.evaluateJavascript(COMMON_DESKTOP_VIEWPORT_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    /**
     * Injects the Windows 10 Touch desktop identity profile into the given WebView.
     */
    fun applyWindows10TouchProfile(webView: WebView) {
        try {
            webView.evaluateJavascript(WINDOWS_10_TOUCH_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    /**
     * Shared WebView base settings applied by desktop modes.
     */
    fun applyCommonDesktopWebViewSettings(webView: WebView) {
        val settings = webView.settings
        if (!settings.useWideViewPort) settings.useWideViewPort = true
        if (!settings.loadWithOverviewMode) settings.loadWithOverviewMode = true
        if (settings.textZoom != 100) settings.textZoom = 100
        if (!settings.builtInZoomControls) settings.builtInZoomControls = true
        if (settings.displayZoomControls) settings.displayZoomControls = false
    }

    /**
     * Shared desktop state cleanup: cleans up observers, viewport meta modifications,
     * and scripts for desktop architectures.
     */
    fun cleanupCommonDesktopState(webView: WebView) {
        try {
            webView.evaluateJavascript(CLEANUP_DESKTOP_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    /**
     * Shared lifecycle hook called on page finished / SPA navigation.
     * Configures common settings and injects architecture-specific scripts.
     */
    fun handlePageLifecycle(webView: WebView, architecture: DesktopArchitecture, url: String) {
        if (!architecture.isAnyDesktop()) return
        when (architecture) {
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                applyCommonDesktopWebViewSettings(webView)
                if (!isAuthenticationUrl(url)) {
                    applyCommonDesktopViewport(webView)
                    applyWindows10TouchProfile(webView)
                }
            }
            else -> {}
        }
    }

    /**
     * Shared synchronization engine for live WebViews without requiring destructive reloads.
     * Updates User-Agent, cleans up previous architecture state, applies common desktop settings,
     * and injects the target architecture script.
     */
    fun synchronizeDesktopWebView(
        webView: WebView,
        url: String?,
        architecture: DesktopArchitecture,
        updateUserAgent: Boolean = true
    ) {
        val effectiveArchitecture = when (architecture) {
            DesktopArchitecture.WINDOWS_10_TOUCH -> DesktopArchitecture.WINDOWS_10_TOUCH
            else -> DesktopArchitecture.NONE
        }

        val targetUa = when (effectiveArchitecture) {
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                if (isAuthenticationUrl(url)) null
                else WINDOWS_10_TOUCH_USER_AGENT
            }
            else -> null
        }

        val settings = webView.settings
        if (updateUserAgent && settings.userAgentString != targetUa) {
            settings.userAgentString = targetUa
        }

        when (effectiveArchitecture) {
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                cleanupCommonDesktopState(webView)
                applyCommonDesktopWebViewSettings(webView)
                if (!isAuthenticationUrl(url)) {
                    applyCommonDesktopViewport(webView)
                    applyWindows10TouchProfile(webView)
                }
            }
            else -> {
                cleanupCommonDesktopState(webView)
            }
        }
    }

    fun migrateSavedArchitecture(context: Context) {
        try {
            val prefs = context.getSharedPreferences("focus_shield_prefs", Context.MODE_PRIVATE)
            val saved = prefs.getString("key_desktop_architecture", null) ?: return
            if (saved == "STANDARD" || saved == "DESKTOP_MODE_4" || saved == "DESKTOP_MODE_11" || saved == "DESKTOP_MODE_12" || saved == "WINDOWS_7") {
                prefs.edit().putString("key_desktop_architecture", "NONE").apply()
            }
        } catch (_: Throwable) {}
    }
}
