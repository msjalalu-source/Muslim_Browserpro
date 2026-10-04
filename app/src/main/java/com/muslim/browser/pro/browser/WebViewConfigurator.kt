package com.muslim.browser.pro.browser

import android.net.Uri
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import java.util.Locale

/**
 * Build 48 Architecture: Centralized WebView Configurator with Experimental
 * "Windows 10 Touch" Browser Identity Profile.
 *
 * Encapsulates the complete WebView configuration and Desktop Mode lifecycle:
 * - Base WebView settings initialization (JavaScript, DOM storage, database, caching, multi-window, zoom controls)
 * - Standard Desktop Mode (Linux x86_64 User-Agent, wide viewport, overview mode)
 * - Experimental "Windows 10 Touch" Identity Profile:
 *     - Windows 10 desktop Chrome User-Agent
 *     - Navigator spoofing: platform ("Win32"), vendor, appVersion, maxTouchPoints (10), hardwareConcurrency (8), deviceMemory (8)
 *     - User-Agent Client Hints (navigator.userAgentData: platform "Windows", mobile false, x86_64, Windows 10.0.0)
 *     - WebGL unmasked vendor/renderer spoofing (Intel UHD Graphics Direct3D11)
 *     - CSS touch/hover media queries ((hover: hover), (pointer: fine), (any-pointer: coarse))
 * - Dynamic adaptive authentication endpoint handling to allow Google/OAuth flows to complete without redirect loops
 * - Dynamic theme and dark mode styling synchronization
 */
object WebViewConfigurator {

    const val DESKTOP_USER_AGENT =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

    const val WINDOWS_10_TOUCH_USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

    val WINDOWS_10_TOUCH_INJECTION_SCRIPT: String = """
        (function() {
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

            // 4. Window Outer Dimensions (Desktop-consistent: outerWidth/outerHeight)
            try {
                Object.defineProperty(window, 'outerWidth', {
                    get: function() { return window.innerWidth ? Math.max(window.innerWidth, 1280) : 1280; },
                    configurable: true
                });
                Object.defineProperty(window, 'outerHeight', {
                    get: function() { return window.innerHeight ? Math.max(window.innerHeight, 720) : 1040; },
                    configurable: true
                });
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
        })();
    """.trimIndent()

    enum class BrowserIdentityMode {
        MOBILE,
        DESKTOP_LINUX,
        WINDOWS_10_TOUCH
    }

    @Volatile
    var isDarkThemeActive: Boolean = true

    /**
     * Clear centralized precedence rule:
     * 1. If Windows 10 Touch profile is enabled, it takes highest precedence.
     * 2. Else if standard Desktop Mode is enabled, standard Linux desktop profile is used.
     * 3. Else standard Mobile Mode is used.
     */
    fun getActiveIdentityMode(isDesktopEnabled: Boolean, isWindows10TouchEnabled: Boolean): BrowserIdentityMode {
        return when {
            isWindows10TouchEnabled -> BrowserIdentityMode.WINDOWS_10_TOUCH
            isDesktopEnabled -> BrowserIdentityMode.DESKTOP_LINUX
            else -> BrowserIdentityMode.MOBILE
        }
    }

    /**
     * Checks if the given URL corresponds to an identity provider / authentication endpoint
     * (such as accounts.google.com, appleid.apple.com, OAuth 2.0 / OpenID Connect authorization)
     * that enforces client consistency checks between HTTP User-Agent and native Client Hints.
     */
    fun isAuthenticationUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val uri = try { Uri.parse(url) } catch (_: Exception) { return false }
        val host = uri.host?.lowercase(Locale.ROOT) ?: return false

        // GitHub pages must NEVER disable Desktop Mode or be treated as auth redirects requiring mobile UA
        if (host == "github.com" || host.endsWith(".github.com")) {
            return false
        }

        if (host == "accounts.google.com" || host.endsWith(".accounts.google.com") ||
            host == "appleid.apple.com" ||
            host == "login.microsoftonline.com" ||
            host == "auth.account.sony.com" ||
            host.startsWith("auth.") || host.startsWith("id.") || host.startsWith("login.")) {
            return true
        }

        val path = uri.path?.lowercase(Locale.ROOT) ?: ""
        if (path.contains("/oauth2/") || path.contains("/oauth/") ||
            path.contains("/openid-connect/") || path.endsWith("/authorize")) {
            return true
        }

        return false
    }

    /**
     * Injects the Windows 10 Touch profile script into the WebView if the profile is enabled.
     */
    fun injectWindows10TouchProfileIfEnabled(webView: WebView?, isWindows10TouchEnabled: Boolean) {
        if (webView == null || !isWindows10TouchEnabled) return
        try {
            webView.evaluateJavascript(WINDOWS_10_TOUCH_INJECTION_SCRIPT, null)
        } catch (_: Throwable) {}
    }

    /**
     * Applies the active browser identity mode to a WebView instance.
     * Synchronizes User-Agent, viewport parameters, text zoom, and hardware/platform script injection.
     */
    fun applyIdentityMode(
        webView: WebView,
        isDesktopEnabled: Boolean,
        isWindows10TouchEnabled: Boolean
    ) {
        val mode = getActiveIdentityMode(isDesktopEnabled, isWindows10TouchEnabled)
        val currentUrl = webView.url

        webView.settings.apply {
            when (mode) {
                BrowserIdentityMode.WINDOWS_10_TOUCH -> {
                    if (isAuthenticationUrl(currentUrl)) {
                        userAgentString = null
                    } else {
                        userAgentString = WINDOWS_10_TOUCH_USER_AGENT
                    }
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    textZoom = 100
                    builtInZoomControls = true
                    displayZoomControls = false
                }
                BrowserIdentityMode.DESKTOP_LINUX -> {
                    if (isAuthenticationUrl(currentUrl)) {
                        userAgentString = null
                    } else {
                        userAgentString = DESKTOP_USER_AGENT
                    }
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    textZoom = 100
                    builtInZoomControls = true
                    displayZoomControls = false
                }
                BrowserIdentityMode.MOBILE -> {
                    userAgentString = null
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    textZoom = 100
                    builtInZoomControls = true
                    displayZoomControls = false
                }
            }
        }

        if (mode == BrowserIdentityMode.WINDOWS_10_TOUCH) {
            injectWindows10TouchProfileIfEnabled(webView, true)
        }
        applyDesktopViewport(webView, isDesktopEnabled || isWindows10TouchEnabled)
    }

    const val DESKTOP_VIEWPORT_TARGET_WIDTH = 1280
    const val DESKTOP_VIEWPORT_CONTENT = "width=$DESKTOP_VIEWPORT_TARGET_WIDTH"

    /**
     * Persistent, event-driven Desktop Viewport Guard script:
     * - Maintains an appropriate desktop CSS viewport (width=1280) for desktop-class layouts
     * - Survives GitHub Turbo, PJAX, SPA DOM replacements via MutationObserver on head/documentElement
     * - Intercepts turbo:load, turbo:render, pjax:end, pageshow, popstate, history.pushState/replaceState
     * - Patches navigator.userAgentData to ensure mobile: false and platform: 'Linux'
     * - Strictly idempotent: installs once per document, avoids repeated execution loops
     * - Zero timers or polling intervals
     */
    val DESKTOP_VIEWPORT_GUARD_SCRIPT: String = """
        (function() {
            var TARGET_CONTENT = '$DESKTOP_VIEWPORT_CONTENT';
            var GUARD_KEY = '__mb_desktop_guard__';

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

            var headObserver = null;
            var docObserver = null;
            try {
                headObserver = new MutationObserver(function(mutations) {
                    for (var i = 0; i < mutations.length; i++) {
                        var m = mutations[i];
                        if (m.type === 'childList') {
                            applyViewport();
                            break;
                        } else if (m.type === 'attributes' && m.attributeName === 'content') {
                            if (m.target && m.target.name === 'viewport' && m.target.getAttribute('content') !== TARGET_CONTENT) {
                                applyViewport();
                                break;
                            }
                        }
                    }
                });
                if (document.head) {
                    headObserver.observe(document.head, {
                        childList: true,
                        subtree: true,
                        attributes: true,
                        attributeFilter: ['content']
                    });
                }

                docObserver = new MutationObserver(function(mutations) {
                    for (var i = 0; i < mutations.length; i++) {
                        if (mutations[i].type === 'childList') {
                            applyViewport();
                            if (document.head && headObserver) {
                                try {
                                    headObserver.disconnect();
                                    headObserver.observe(document.head, {
                                        childList: true,
                                        subtree: true,
                                        attributes: true,
                                        attributeFilter: ['content']
                                    });
                                } catch(e) {}
                            }
                            break;
                        }
                    }
                });
                docObserver.observe(document.documentElement, {
                    childList: true,
                    subtree: false
                });
            } catch(e) {}

            var navEvents = ['turbo:load', 'turbo:render', 'pjax:end', 'pageshow', 'popstate'];
            function onNav() {
                applyViewport();
            }
            navEvents.forEach(function(evt) {
                window.addEventListener(evt, onNav, { passive: true });
            });

            var origPush = history.pushState;
            if (origPush) {
                history.pushState = function() {
                    var res = origPush.apply(this, arguments);
                    applyViewport();
                    return res;
                };
            }
            var origReplace = history.replaceState;
            if (origReplace) {
                history.replaceState = function() {
                    var res = origReplace.apply(this, arguments);
                    applyViewport();
                    return res;
                };
            }

            window[GUARD_KEY] = {
                ensureViewport: applyViewport,
                cleanup: function() {
                    try {
                        if (headObserver) headObserver.disconnect();
                        if (docObserver) docObserver.disconnect();
                        navEvents.forEach(function(evt) {
                            window.removeEventListener(evt, onNav);
                        });
                        if (origPush) history.pushState = origPush;
                        if (origReplace) history.replaceState = origReplace;

                        var meta = document.querySelector('meta[name="viewport"]');
                        if (meta) {
                            if (meta.hasAttribute('data-mb-created')) {
                                meta.remove();
                            } else if (meta.hasAttribute('data-mb-orig')) {
                                var orig = meta.getAttribute('data-mb-orig');
                                if (orig) {
                                    meta.setAttribute('content', orig);
                                } else {
                                    meta.removeAttribute('content');
                                }
                                meta.removeAttribute('data-mb-orig');
                            }
                        }
                    } catch(e) {}
                    delete window[GUARD_KEY];
                }
            };
        })();
    """.trimIndent()

    val DESKTOP_VIEWPORT_CLEANUP_SCRIPT: String = """
        (function() {
            var GUARD_KEY = '__mb_desktop_guard__';
            if (window[GUARD_KEY] && typeof window[GUARD_KEY].cleanup === 'function') {
                window[GUARD_KEY].cleanup();
            } else {
                try {
                    var meta = document.querySelector('meta[name="viewport"]');
                    if (meta) {
                        if (meta.hasAttribute('data-mb-created')) {
                            meta.remove();
                        } else if (meta.hasAttribute('data-mb-orig')) {
                            var orig = meta.getAttribute('data-mb-orig');
                            if (orig) {
                                meta.setAttribute('content', orig);
                            } else {
                                meta.removeAttribute('content');
                            }
                            meta.removeAttribute('data-mb-orig');
                        }
                    }
                } catch(e) {}
            }
        })();
    """.trimIndent()

    /**
     * Dedicated Desktop Viewport mechanism:
     * Maintains desktop-style layout for pages using <meta name="viewport">.
     * Lightweight, idempotent, scoped to document, safe for SPA, removable when disabled.
     */
    fun applyDesktopViewport(webView: WebView, isDesktopOrTouchEnabled: Boolean) {
        try {
            val script = if (isDesktopOrTouchEnabled) {
                DESKTOP_VIEWPORT_GUARD_SCRIPT
            } else {
                DESKTOP_VIEWPORT_CLEANUP_SCRIPT
            }
            webView.evaluateJavascript(script, null)
        } catch (_: Throwable) {}
    }

    /**
     * Authoritative Desktop Mode synchronization function:
     * - Determines the correct User-Agent and Desktop Mode state.
     * - Applies the correct User-Agent to the WebView if [updateUserAgent] is true.
     * - Ensures useWideViewPort = true, loadWithOverviewMode = true.
     * - Preserves the current URL without calling loadUrl() or reload().
     * - Idempotent and lightweight.
     */
    fun syncDesktopMode(
        webView: WebView,
        url: String?,
        isDesktopEnabled: Boolean,
        updateUserAgent: Boolean = true,
        isWindows10TouchEnabled: Boolean = false
    ) {
        val mode = getActiveIdentityMode(isDesktopEnabled, isWindows10TouchEnabled)
        val targetUa = when (mode) {
            BrowserIdentityMode.WINDOWS_10_TOUCH -> {
                if (isAuthenticationUrl(url)) null else WINDOWS_10_TOUCH_USER_AGENT
            }
            BrowserIdentityMode.DESKTOP_LINUX -> {
                if (isAuthenticationUrl(url)) null else DESKTOP_USER_AGENT
            }
            BrowserIdentityMode.MOBILE -> null
        }

        webView.settings.apply {
            if (updateUserAgent) {
                if (userAgentString != targetUa) {
                    userAgentString = targetUa
                }
            }
            useWideViewPort = true
            loadWithOverviewMode = true
            textZoom = 100
            builtInZoomControls = true
            displayZoomControls = false
        }

        if (mode == BrowserIdentityMode.WINDOWS_10_TOUCH) {
            injectWindows10TouchProfileIfEnabled(webView, true)
        }
        applyDesktopViewport(webView, isDesktopEnabled || isWindows10TouchEnabled)
    }

    /**
     * Backwards-compatible delegator for Desktop Mode configuration.
     */
    fun applyDesktopMode(webView: WebView, isDesktopEnabled: Boolean, isWindows10TouchEnabled: Boolean = false) {
        applyIdentityMode(webView, isDesktopEnabled, isWindows10TouchEnabled)
    }

    /**
     * Configures the full base settings for a newly created or restored WebView instance.
     */
    fun configureBaseSettings(
        webView: WebView,
        isDarkTheme: Boolean,
        isDesktopEnabled: Boolean = false,
        isWindows10TouchEnabled: Boolean = false
    ) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            @Suppress("DEPRECATION")
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            setSupportMultipleWindows(true)
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            textZoom = 100
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                offscreenPreRaster = false
            }
            mediaPlaybackRequiresUserGesture = true
            @Suppress("DEPRECATION")
            saveFormData = false
        }

        applyIdentityMode(webView, isDesktopEnabled, isWindows10TouchEnabled)
        applyWebViewTheme(webView, isDarkTheme)
    }

    /**
     * Applies the application theme (dark/light) to the WebView.
     */
    fun applyWebViewTheme(webView: WebView, isDarkTheme: Boolean) {
        try {
            isDarkThemeActive = isDarkTheme
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                webView.settings.isAlgorithmicDarkeningAllowed = isDarkTheme
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                @Suppress("DEPRECATION")
                webView.settings.forceDark = if (isDarkTheme) {
                    WebSettings.FORCE_DARK_ON
                } else {
                    WebSettings.FORCE_DARK_OFF
                }
            }
            val bgColor = if (isDarkTheme) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            webView.setBackgroundColor(bgColor)
            applyWebPageDarkTheme(webView, isDarkTheme)
        } catch (_: Exception) {}
    }

    fun applyWebPageDarkTheme(webView: WebView?, isDarkTheme: Boolean) {
        if (webView == null) return
        try {
            if (isDarkTheme) {
                val script = """
                    (function() {
                        try {
                            var style = document.getElementById('__mb_dark_theme__');
                            if (!style) {
                                style = document.createElement('style');
                                style.id = '__mb_dark_theme__';
                                (document.head || document.documentElement).appendChild(style);
                            }
                            style.textContent = `
                                [data-mb-white-bg="true"] { background-color: gray !important; }
                                [data-mb-white-color="true"] { color: gray !important; }
                                [data-mb-white-border="true"] { border-color: gray !important; }
                            `;

                            function isPureWhite(colorStr) {
                                if (!colorStr) return false;
                                var s = colorStr.replace(/\s+/g, '').toLowerCase();
                                return s === 'rgb(255,255,255)' ||
                                       s === 'rgba(255,255,255,1)' ||
                                       s === '#ffffff' ||
                                       s === '#fff' ||
                                       s === 'white';
                            }

                            var html = document.documentElement;
                            var body = document.body;
                            if (html) {
                                var htmlBg = window.getComputedStyle(html).backgroundColor;
                                var bodyBg = body ? window.getComputedStyle(body).backgroundColor : null;
                                var isHtmlTrans = !htmlBg || htmlBg === 'rgba(0,0,0,0)' || htmlBg === 'transparent';
                                var isBodyTrans = !bodyBg || bodyBg === 'rgba(0,0,0,0)' || bodyBg === 'transparent';
                                if (isPureWhite(htmlBg) || (isHtmlTrans && (isBodyTrans || isPureWhite(bodyBg)))) {
                                    html.setAttribute('data-mb-white-bg', 'true');
                                }
                            }

                            var all = document.querySelectorAll('*');
                            for (var i = 0; i < all.length; i++) {
                                var node = all[i];
                                if (node.id === '__mb_dark_theme__') continue;
                                var cs = window.getComputedStyle(node);
                                if (isPureWhite(cs.backgroundColor)) {
                                    node.setAttribute('data-mb-white-bg', 'true');
                                }
                                if (isPureWhite(cs.color)) {
                                    node.setAttribute('data-mb-white-color', 'true');
                                }
                                if (isPureWhite(cs.borderColor)) {
                                    node.setAttribute('data-mb-white-border', 'true');
                                }
                            }
                        } catch(e) {}
                    })();
                """.trimIndent()
                webView.evaluateJavascript(script, null)
            } else {
                val cleanupScript = """
                    (function() {
                        try {
                            var el = document.getElementById('__mb_dark_theme__');
                            if (el) el.remove();
                            var nodes = document.querySelectorAll('[data-mb-white-bg],[data-mb-white-color],[data-mb-white-border]');
                            for (var i = 0; i < nodes.length; i++) {
                                nodes[i].removeAttribute('data-mb-white-bg');
                                nodes[i].removeAttribute('data-mb-white-color');
                                nodes[i].removeAttribute('data-mb-white-border');
                            }
                        } catch(e) {}
                    })();
                """.trimIndent()
                webView.evaluateJavascript(cleanupScript, null)
            }
        } catch (_: Throwable) {}
    }
}
