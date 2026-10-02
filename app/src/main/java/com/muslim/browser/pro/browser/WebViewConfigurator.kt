package com.muslim.browser.pro.browser

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import java.util.Locale

/**
 * Centralized, authoritative WebView configuration pipeline for Muslim Browser Pro.
 *
 * Responsibilities:
 * - Single source of truth for WebSettings, cookies, and hardware configuration.
 * - Idempotent Desktop Mode synchronization (Desktop Mode ON = Desktop UA, OFF = Mobile UA).
 * - Strict isolation of authentication (Google/Apple login) without mutating global Desktop state.
 * - Centralized viewport and dark/light content theming.
 *
 * Invariants:
 * - Never calls loadUrl()
 * - Never calls reload()
 * - Never modifies global browser state
 * - Never makes navigation decisions
 */
object WebViewConfigurator {

    const val DESKTOP_USER_AGENT =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"

    @Volatile
    var defaultMobileUserAgent: String? = null

    @Volatile
    var isAuthFlowActive: Boolean = false

    @Volatile
    var isDarkThemeActive: Boolean = true

    /**
     * Resolves the desktop User-Agent string, dynamically incorporating the actual Chrome
     * version installed on the device for optimal compatibility and bot-detection avoidance.
     */
    fun resolveDesktopUserAgent(context: Context): String {
        val baseMobile = defaultMobileUserAgent ?: try {
            WebSettings.getDefaultUserAgent(context)
        } catch (_: Exception) {
            null
        }
        return if (!baseMobile.isNullOrBlank()) {
            val chromeVersionRegex = Regex("Chrome/([0-9.]+)")
            val match = chromeVersionRegex.find(baseMobile)
            val chromeVer = match?.value ?: "Chrome/134.0.0.0"
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) $chromeVer Safari/537.36"
        } else {
            DESKTOP_USER_AGENT
        }
    }

    /**
     * Detects whether a URL represents a strict identity provider authentication endpoint
     * (specifically Google Accounts or Apple ID) that disallows desktop user-agents on embedded WebViews.
     */
    fun isAuthenticationEndpoint(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return try {
            val uri = Uri.parse(url)
            val host = uri.host?.lowercase(Locale.ROOT) ?: return false
            val path = uri.path?.lowercase(Locale.ROOT) ?: ""

            // 1. Google Account & OAuth hosts
            if (host == "accounts.google.com" ||
                host.endsWith(".accounts.google.com") ||
                host == "oauth2.googleapis.com" ||
                host == "myaccount.google.com" ||
                host == "accounts.youtube.com"
            ) {
                return true
            }

            // 2. Apple ID Auth
            if (host == "appleid.apple.com") {
                return true
            }

            // 3. Google services sign-in, account addition, and sign-out endpoints
            val isGoogle = host == "google.com" || host.endsWith(".google.com")
            if (isGoogle && (
                path.startsWith("/servicelogin") ||
                path.startsWith("/signin") ||
                path.startsWith("/interactive_login") ||
                path.contains("/addsession") ||
                path.contains("/accountchooser") ||
                path.contains("/logout") ||
                path.contains("/signout") ||
                path.startsWith("/accounts/") ||
                (uri.getQueryParameter("service") != null && path.contains("login"))
            )) {
                return true
            }

            false
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Configures baseline WebView settings, cookies, and system features on a newly instantiated WebView.
     */
    fun configureBaseSettings(webView: WebView, isDarkTheme: Boolean) {
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
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                offscreenPreRaster = false
            }
            mediaPlaybackRequiresUserGesture = true
            @Suppress("DEPRECATION")
            saveFormData = false
        }

        applyWebViewTheme(webView, isDarkTheme)
    }

    /**
     * Single authoritative synchronization function for WebView desktop mode.
     * Enforces the invariant:
     * Desktop Mode ON -> all normal web navigation uses Desktop configuration consistently.
     * Desktop Mode OFF -> all normal web navigation uses Mobile configuration consistently.
     */
    fun syncDesktopMode(
        webView: WebView?,
        url: String? = null,
        isDesktopEnabled: Boolean? = null,
        updateUserAgent: Boolean = true
    ): Boolean {
        if (webView == null) return false
        var uaChanged = false
        try {
            if (defaultMobileUserAgent == null) {
                defaultMobileUserAgent = try {
                    WebSettings.getDefaultUserAgent(webView.context)
                } catch (_: Exception) {
                    webView.settings.userAgentString
                }
            }

            val enabled = isDesktopEnabled ?: false
            val targetUrl = url ?: webView.url

            // Update auth flow state strictly based on identity provider endpoint matching
            if (enabled && !targetUrl.isNullOrBlank()) {
                if (isAuthenticationEndpoint(targetUrl)) {
                    isAuthFlowActive = true
                    android.util.Log.d("DESKTOP_DIAG", "Auth flow activated for endpoint: $targetUrl")
                } else if (isAuthFlowActive) {
                    val uri = try { Uri.parse(targetUrl) } catch (_: Exception) { null }
                    val host = uri?.host?.lowercase(Locale.ROOT) ?: ""
                    val path = uri?.path?.lowercase(Locale.ROOT) ?: ""
                    val isGoogleAuthHost = host == "accounts.google.com" ||
                            host.endsWith(".accounts.google.com") ||
                            host == "oauth2.googleapis.com" ||
                            host == "myaccount.google.com" ||
                            host == "appleid.apple.com"
                    val isIntermediateAuth = isGoogleAuthHost ||
                            path.contains("/callback/google") ||
                            path.contains("/challenge/") ||
                            path.contains("/checkcookie") ||
                            path.contains("/setosid") ||
                            path.contains("/consent") ||
                            path.contains("/saml") ||
                            path.contains("/federation") ||
                            (host.endsWith("google.com") && (path.contains("/accounts/") || path.contains("/addsession") || path.contains("/accountchooser")))
                    if (!isIntermediateAuth) {
                        isAuthFlowActive = false
                        android.util.Log.d("DESKTOP_DIAG", "Auth flow completed on URL: $targetUrl")
                    }
                }
            } else if (!enabled) {
                isAuthFlowActive = false
            }

            val shouldUseMobile = !enabled || isAuthFlowActive
            val targetUserAgent = if (shouldUseMobile) {
                defaultMobileUserAgent ?: webView.settings.userAgentString
            } else {
                resolveDesktopUserAgent(webView.context)
            }

            if (updateUserAgent && webView.settings.userAgentString != targetUserAgent) {
                android.util.Log.d(
                    "DESKTOP_DIAG",
                    "Setting userAgentString: $targetUserAgent (isAuthFlowActive=$isAuthFlowActive, enabled=$enabled)"
                )
                webView.settings.userAgentString = targetUserAgent
                uaChanged = true
            }

            if (!webView.settings.useWideViewPort) {
                webView.settings.useWideViewPort = true
                webView.settings.loadWithOverviewMode = true
                webView.settings.builtInZoomControls = true
                webView.settings.displayZoomControls = false
            }

            // Diagnostic assertion: if Desktop Mode is ON and not in auth flow, actual UA must be Desktop
            if (enabled && !isAuthFlowActive) {
                val expectedDesktop = resolveDesktopUserAgent(webView.context)
                if (webView.settings.userAgentString != expectedDesktop) {
                    android.util.Log.e(
                        "DESKTOP_STATE_ERROR",
                        "Desktop Mode invariant violated! URL=$targetUrl, expectedUA=$expectedDesktop, actualUA=${webView.settings.userAgentString}"
                    )
                }
            }
        } catch (_: Exception) {}
        return uaChanged
    }

    /**
     * Configures viewport meta tag dynamically to present full desktop layout (width=1024)
     * in Desktop Mode, and device-width in Mobile Mode.
     * Idempotent: safe to invoke once per page commit.
     */
    fun applyDesktopViewport(webView: WebView?, enabled: Boolean) {
        if (webView == null) return
        try {
            val effectiveDesktopMode = enabled && !isAuthFlowActive

            val script = if (effectiveDesktopMode) {
                """
                    (function() {
                        try {
                            var metas = document.querySelectorAll('meta[name="viewport"]');
                            if (metas.length > 0) {
                                metas.forEach(function(m) {
                                    if (m.getAttribute('content') !== 'width=1024') {
                                        m.setAttribute('content', 'width=1024');
                                    }
                                });
                            } else {
                                var meta = document.createElement('meta');
                                meta.name = 'viewport';
                                meta.content = 'width=1024';
                                (document.head || document.documentElement).appendChild(meta);
                            }
                        } catch(e) {}
                    })();
                """.trimIndent()
            } else {
                """
                    (function() {
                        try {
                            var metas = document.querySelectorAll('meta[name="viewport"]');
                            if (metas.length > 0) {
                                metas.forEach(function(m) {
                                    if (m.getAttribute('content') !== 'width=device-width, initial-scale=1.0') {
                                        m.setAttribute('content', 'width=device-width, initial-scale=1.0');
                                    }
                                });
                            }
                        } catch(e) {}
                    })();
                """.trimIndent()
            }
            webView.evaluateJavascript(script, null)
        } catch (_: Throwable) {}
    }

    /**
     * Applies native dark or light theme settings to the WebView.
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
            val bgColor = if (isDarkTheme) Color.parseColor("#0F172A") else Color.WHITE
            webView.setBackgroundColor(bgColor)
            applyWebPageDarkTheme(webView, isDarkTheme)
        } catch (_: Exception) {}
    }

    /**
     * Applies or removes lightweight dark rendering on web content.
     * Preserves true image, video, and media colors while darkening light backgrounds and text.
     * Includes luminance detection so already-dark websites (like GitHub in dark mode) are not inverted.
     */
    fun applyWebPageDarkTheme(webView: WebView?, isDarkTheme: Boolean) {
        if (webView == null) return
        try {
            if (isDarkTheme) {
                val script = """
                    (function() {
                        try {
                            var id = '__mb_dark_theme__';
                            if (document.getElementById(id)) return;
                            var target = document.body || document.documentElement;
                            if (!target) return;
                            var bg = window.getComputedStyle(target).backgroundColor;
                            var m = bg ? bg.match(/rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*([\d.]+))?\)/) : null;
                            if (m) {
                                var alpha = m[4] !== undefined ? parseFloat(m[4]) : 1;
                                if (alpha > 0.1) {
                                    var r = parseInt(m[1]), g = parseInt(m[2]), b = parseInt(m[3]);
                                    var lum = 0.299 * r + 0.587 * g + 0.114 * b;
                                    if (lum < 65) return;
                                }
                            }
                            var style = document.createElement('style');
                            style.id = id;
                            style.textContent = 'html { filter: invert(100%) hue-rotate(180deg) !important; background-color: #0F172A !important; } img, video, canvas, svg, picture, iframe, [style*="background-image"] { filter: invert(100%) hue-rotate(180deg) !important; }';
                            (document.head || document.documentElement).appendChild(style);
                        } catch(e) {}
                    })();
                """.trimIndent()
                webView.evaluateJavascript(script, null)
            } else {
                val script = """
                    (function() {
                        try {
                            var el = document.getElementById('__mb_dark_theme__');
                            if (el) el.remove();
                        } catch(e) {}
                    })();
                """.trimIndent()
                webView.evaluateJavascript(script, null)
            }
        } catch (_: Throwable) {}
    }
}
