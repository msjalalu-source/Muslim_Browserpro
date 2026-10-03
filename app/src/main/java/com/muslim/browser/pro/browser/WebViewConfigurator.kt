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
            if (window.__mb_win10_touch_active__) return;
            window.__mb_win10_touch_active__ = true;

            // 1. Navigator hardware & platform signals
            try {
                Object.defineProperty(navigator, 'platform', { get: () => 'Win32', configurable: true });
                Object.defineProperty(navigator, 'vendor', { get: () => 'Google Inc.', configurable: true });
                Object.defineProperty(navigator, 'appVersion', { get: () => '5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36', configurable: true });
                Object.defineProperty(navigator, 'maxTouchPoints', { get: () => 10, configurable: true });
                Object.defineProperty(navigator, 'hardwareConcurrency', { get: () => 8, configurable: true });
                Object.defineProperty(navigator, 'deviceMemory', { get: () => 8, configurable: true });
            } catch(e) {}

            // 2. User-Agent Client Hints (navigator.userAgentData)
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
                Object.defineProperty(navigator, 'userAgentData', { get: () => uaData, configurable: true });
            } catch(e) {}

            // 3. WebGL GPU / Unmasked Renderer Signals
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

            // 4. CSS Media Queries for Windows 10 Touch (fine pointer + coarse touch + hover)
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
        val path = uri.path?.lowercase(Locale.ROOT) ?: ""

        if (host == "accounts.google.com" || host.endsWith(".accounts.google.com") ||
            host == "appleid.apple.com" ||
            host == "login.microsoftonline.com" ||
            host == "auth.account.sony.com" ||
            host.startsWith("auth.") || host.startsWith("id.") || host.startsWith("login.")) {
            return true
        }

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
            val cleanupScript = """
                (function() {
                    try {
                        var el = document.getElementById('__mb_dark_theme__');
                        if (el) el.remove();
                    } catch(e) {}
                })();
            """.trimIndent()
            webView.evaluateJavascript(cleanupScript, null)
        } catch (_: Throwable) {}
    }
}
