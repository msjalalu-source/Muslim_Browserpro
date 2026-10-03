package com.muslim.browser.pro.browser

import android.net.Uri
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import java.util.Locale

/**
 * Build 48 Architecture: Centralized WebView Configurator.
 *
 * Encapsulates the complete WebView configuration and Desktop Mode lifecycle:
 * - Base WebView settings initialization (JavaScript, DOM storage, database, caching, multi-window, zoom controls)
 * - Desktop Mode user-agent and viewport configuration (wide viewport, overview mode, text zoom)
 * - Dynamic adaptive authentication endpoint handling to allow Google/OAuth flows to complete without redirect loops
 * - Dynamic theme and dark mode styling synchronization
 */
object WebViewConfigurator {

    const val DESKTOP_USER_AGENT =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

    @Volatile
    var isDarkThemeActive: Boolean = true

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
     * Applies Desktop Mode or Mobile Mode to a WebView instance.
     * Synchronizes User-Agent, viewport parameters, and text zoom.
     */
    fun applyDesktopMode(webView: WebView, isDesktopEnabled: Boolean) {
        webView.settings.apply {
            if (isDesktopEnabled) {
                val currentUrl = webView.url
                if (isAuthenticationUrl(currentUrl)) {
                    userAgentString = null
                } else {
                    userAgentString = DESKTOP_USER_AGENT
                }
                useWideViewPort = true
                loadWithOverviewMode = true
                textZoom = 100
            } else {
                userAgentString = null
                useWideViewPort = true
                loadWithOverviewMode = true
                textZoom = 100
            }
        }
    }

    /**
     * Configures the full base settings for a newly created or restored WebView instance.
     */
    fun configureBaseSettings(webView: WebView, isDarkTheme: Boolean, isDesktopEnabled: Boolean = false) {
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

        applyDesktopMode(webView, isDesktopEnabled)
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
