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
        var TARGET_CONTENT = 'width=1280';
        function updateViewport() {
            var metas = document.querySelectorAll('meta[name="viewport"]');
            if (metas.length > 0) {
                metas.forEach(function(m) {
                    if (m.getAttribute('content') !== TARGET_CONTENT) {
                        m.setAttribute('content', TARGET_CONTENT);
                    }
                });
            } else {
                var head = document.head || document.documentElement;
                if (head) {
                    var meta = document.createElement('meta');
                    meta.name = 'viewport';
                    meta.content = TARGET_CONTENT;
                    head.appendChild(meta);
                }
            }
        }
        updateViewport();
        if (window.navigator && window.navigator.userAgentData) {
            try {
                if (window.navigator.userAgentData.mobile !== false) {
                    Object.defineProperty(window.navigator.userAgentData, 'mobile', {
                        get: function() { return false; },
                        configurable: true
                    });
                }
            } catch(e) {}
        }
        if (window.__mbViewportObserver) {
            window.__mbViewportObserver.disconnect();
            window.__mbViewportObserver = null;
        }
        var targetHead = document.head || document.documentElement;
        if (window.MutationObserver && targetHead) {
            window.__mbViewportObserver = new MutationObserver(function(mutations) {
                for (var i = 0; i < mutations.length; i++) {
                    var m = mutations[i];
                    if (m.type === 'childList') {
                        updateViewport();
                        break;
                    } else if (m.type === 'attributes' && m.target && m.target.getAttribute('content') !== TARGET_CONTENT) {
                        m.target.setAttribute('content', TARGET_CONTENT);
                        break;
                    }
                }
            });
            window.__mbViewportObserver.observe(targetHead, { childList: true, subtree: false });
            var vp = document.querySelector('meta[name="viewport"]');
            if (vp) {
                window.__mbViewportObserver.observe(vp, { attributes: true, attributeFilter: ['content'] });
            }
        }
        if (!window.__mbSpaListenersAttached) {
            window.__mbSpaListenersAttached = true;
            var spaHandler = function() { updateViewport(); };
            window.addEventListener('turbo:load', spaHandler, { passive: true });
            window.addEventListener('popstate', spaHandler, { passive: true });
        }
    } catch(e) {}
})();"""

    private const val CLEANUP_DESKTOP_SCRIPT: String = """(function() {
    try {
        if (window.__mbViewportObserver) {
            window.__mbViewportObserver.disconnect();
            window.__mbViewportObserver = null;
        }
    } catch(e) {}
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
        var metas = document.querySelectorAll('meta[name="viewport"]');
        metas.forEach(function(m) {
            if (m.getAttribute('content') !== 'width=device-width, initial-scale=1.0') {
                m.setAttribute('content', 'width=device-width, initial-scale=1.0');
            }
        });
    } catch(e) {}
})();"""

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
     * Universal Keyboard Visibility Helper Script:
     * - Detects all editable fields: input (text, search, email, etc.), textarea, contenteditable, role="textbox"
     * - Handles shadow DOM and nested editable containers (such as Monaco, CodeMirror, rich editors)
     * - Dispatches visibility adjustments on focusin, pointerup, input, and visualViewport/window resize
     * - Scrolls active element into visible viewport using scrollIntoViewIfNeeded or scrollIntoView({ block: 'nearest' })
     * - Uses progressive delays (0ms, 120ms, 300ms) to ensure visibility across keyboard appearance animations
     * - Safe against cross-origin frames and idempotent via __mb_keyboard_guard__
     */
    const val KEYBOARD_VISIBILITY_SCRIPT: String = """(function() {
    if (window.__mb_keyboard_guard__) return;
    window.__mb_keyboard_guard__ = true;

    function getActiveEditable() {
        try {
            var el = document.activeElement;
            while (el && el.shadowRoot && el.shadowRoot.activeElement) {
                el = el.shadowRoot.activeElement;
            }
            if (!el) return null;
            var tag = (el.tagName || '').toUpperCase();
            if (tag === 'INPUT') {
                var type = (el.type || 'text').toLowerCase();
                var nonText = ['hidden', 'submit', 'button', 'reset', 'checkbox', 'radio', 'file', 'image'];
                if (nonText.indexOf(type) === -1) return el;
            } else if (tag === 'TEXTAREA') {
                return el;
            } else if (el.isContentEditable) {
                return el;
            } else if (el.getAttribute && (el.getAttribute('contenteditable') === 'true' || el.getAttribute('contenteditable') === '' || el.getAttribute('role') === 'textbox')) {
                return el;
            }
            var p = el.parentElement;
            while (p && p !== document.body && p !== document.documentElement) {
                if (p.isContentEditable || (p.getAttribute && (p.getAttribute('contenteditable') === 'true' || p.getAttribute('role') === 'textbox'))) {
                    return el;
                }
                p = p.parentElement;
            }
        } catch(e) {}
        return null;
    }

    function ensureVisible(target) {
        try {
            var el = target || getActiveEditable();
            if (!el || !el.isConnected) return;
            if (window.visualViewport) {
                var rect = el.getBoundingClientRect();
                var vv = window.visualViewport;
                if (rect.top >= 0 && rect.bottom <= vv.height) {
                    return;
                }
            }
            if (typeof el.scrollIntoViewIfNeeded === 'function') {
                el.scrollIntoViewIfNeeded(false);
            } else if (typeof el.scrollIntoView === 'function') {
                el.scrollIntoView({ block: 'nearest', inline: 'nearest' });
            }
        } catch(e) {}
    }

    document.addEventListener('focusin', function(e) {
        ensureVisible(e.target);
    }, { passive: true, capture: true });

    if (window.visualViewport) {
        var resizeTimer = null;
        window.visualViewport.addEventListener('resize', function() {
            if (resizeTimer) clearTimeout(resizeTimer);
            resizeTimer = setTimeout(function() {
                var el = getActiveEditable();
                if (el) ensureVisible(el);
            }, 100);
        }, { passive: true });
    } else {
        var winResizeTimer = null;
        window.addEventListener('resize', function() {
            if (winResizeTimer) clearTimeout(winResizeTimer);
            winResizeTimer = setTimeout(function() {
                var el = getActiveEditable();
                if (el) ensureVisible(el);
            }, 100);
        }, { passive: true });
    }
})();"""

    /**
     * Injects the universal keyboard visibility helper into the given WebView.
     */
    fun ensureKeyboardVisibilityHelper(webView: WebView) {
        try {
            webView.evaluateJavascript(KEYBOARD_VISIBILITY_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    /**
     * Shared lifecycle hook called on page finished / SPA navigation.
     * Configures common settings, injects universal keyboard visibility helper,
     * and injects architecture-specific scripts.
     */
    fun handlePageLifecycle(webView: WebView, architecture: DesktopArchitecture, url: String) {
        ensureKeyboardVisibilityHelper(webView)
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
