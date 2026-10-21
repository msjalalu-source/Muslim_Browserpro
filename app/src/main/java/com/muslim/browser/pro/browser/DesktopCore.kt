package com.muslim.browser.pro.browser

import android.content.Context
import android.net.Uri
import android.webkit.WebView
import androidx.webkit.ScriptHandler
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import java.util.WeakHashMap

object DesktopCore {
    const val VIEWPORT_CONTENT = "width=1280"
    const val TARGET_WIDTH = 1280

    const val WINDOWS_10_TOUCH_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
    const val WINDOWS_10_USER_AGENT = WINDOWS_10_TOUCH_USER_AGENT

    const val KEYBOARD_VISIBILITY_SCRIPT: String = """
        (function() {
            try {
                if (window.__mb_keyboard_guard__) return;
                window.__mb_keyboard_guard__ = true;

                function getDeepActiveElement() {
                    var el = document.activeElement;
                    while (el && el.shadowRoot && el.shadowRoot.activeElement) {
                        el = el.shadowRoot.activeElement;
                    }
                    return el;
                }

                function isEditable(el) {
                    if (!el) return false;
                    var tag = el.tagName;
                    if (tag === 'INPUT' || tag === 'TEXTAREA') return true;
                    if (el.isContentEditable) return true;
                    var role = el.getAttribute ? el.getAttribute('role') : null;
                    if (role === 'textbox') return true;
                    return false;
                }

                function scrollActiveIntoView() {
                    var active = getDeepActiveElement();
                    if (isEditable(active)) {
                        if (typeof active.scrollIntoViewIfNeeded === 'function') {
                            active.scrollIntoViewIfNeeded();
                        } else if (typeof active.scrollIntoView === 'function') {
                            active.scrollIntoView({ block: 'center', inline: 'nearest' });
                        }
                    }
                }

                window.addEventListener('focusin', function() {
                    setTimeout(scrollActiveIntoView, 300);
                });

                if (window.visualViewport) {
                    window.visualViewport.addEventListener('resize', function() {
                        setTimeout(scrollActiveIntoView, 200);
                    });
                } else {
                    window.addEventListener('resize', function() {
                        setTimeout(scrollActiveIntoView, 200);
                    });
                }
            } catch(e) {}
        })();
    """

    private const val CLEANUP_DESKTOP_SCRIPT = """
        (function() {
            try {
                if (window.__mb_win10_touch__) {
                    delete window.__mb_win10_touch__;
                    delete navigator.userAgentData;
                    delete navigator.platform;
                    // cleanup
                }
            } catch(e) {}
        })();
    """

    @JvmField
    val WINDOWS_10_TOUCH_SCRIPT: String = """
        (function() {
            try {
                if (window.__mb_win10_touch__) return;
                window.__mb_win10_touch__ = true;

                var host = window.location.hostname || '';
                if (host === 'accounts.google.com' ||
                    host === 'appleid.apple.com' ||
                    host === 'login.microsoftonline.com' ||
                    host.startsWith('auth.') ||
                    host.startsWith('id.') ||
                    host.startsWith('login.')) {
                    return;
                }

                Object.defineProperties(navigator, {
                    platform: { get: function() { return 'Win32'; } },
                    maxTouchPoints: { get: function() { return 10; } },
                    userAgent: { get: function() { return 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36'; } }
                });

                Object.defineProperties(screen, {
                    width: { get: function() { return 1920; } },
                    height: { get: function() { return 1080; } },
                    availWidth: { get: function() { return 1920; } },
                    availHeight: { get: function() { return 1040; } }
                });

                var desktopInnerW = 1280;
                var desktopInnerH = 720;

                var getParamOrig = (typeof WebGLRenderingContext !== 'undefined' && WebGLRenderingContext.prototype.getParameter) ? WebGLRenderingContext.prototype.getParameter : null;
                if (getParamOrig) {
                    WebGLRenderingContext.prototype.getParameter = function(param) {
                        if (param === 0x1F00) return 'Intel';
                        if (param === 0x1F01) return 'Intel(R) UHD Graphics (Direct3D11)';
                        return getParamOrig.apply(this, arguments);
                    };
                }

                var origMatchMedia = window.matchMedia;
                if (origMatchMedia) {
                    window.matchMedia = function(query) {
                        if (query && (query.indexOf('any-pointer: coarse') !== -1 || query.indexOf('hover: hover') !== -1)) {
                            return { matches: true, media: query, addListener: function(){}, removeListener: function(){} };
                        }
                        return origMatchMedia.apply(this, arguments);
                    };
                }

                if (!('ontouchstart' in window)) {
                    window.ontouchstart = null;
                }
            } catch(e) {}
        })();
    """.trimIndent()

    private val scriptHandlers = WeakHashMap<WebView, ScriptHandler>()
    private val defaultUserAgents = WeakHashMap<WebView, String>()

    @JvmStatic
    fun applyCommonDesktopWebViewSettings(webView: WebView) {
        val settings = webView.settings
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.textZoom = 100
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
    }

    @JvmStatic
    fun applyCommonDesktopViewport(webView: WebView) {
        applyCommonDesktopWebViewSettings(webView)
    }

    @JvmStatic
    fun applyWindows10TouchProfile(webView: WebView) {
        applyCommonDesktopWebViewSettings(webView)
        synchronizeDesktopWebView(webView, null, DesktopArchitecture.WINDOWS_10_TOUCH, true)
    }

    @JvmStatic
    fun isAuthenticationUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return try {
            val uri = Uri.parse(url)
            val host = uri.host?.lowercase() ?: return false
            host == "accounts.google.com" ||
                    host == "appleid.apple.com" ||
                    host == "login.microsoftonline.com" ||
                    host == "login.live.com" ||
                    host.startsWith("auth.") ||
                    host.startsWith("id.") ||
                    host.startsWith("login.")
        } catch (_: Exception) {
            false
        }
    }

    @JvmStatic
    fun handlePageLifecycle(webView: WebView, architecture: DesktopArchitecture, url: String?) {
        if (architecture != DesktopArchitecture.WINDOWS_10_TOUCH) {
            return
        }
        applyCommonDesktopWebViewSettings(webView)
        synchronizeDesktopWebView(webView, url, architecture, false)
    }

    @JvmStatic
    fun migrateSavedArchitecture(context: Context) {
        val prefs = context.getSharedPreferences("focus_shield_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getString("key_desktop_architecture", null) ?: return
        if (saved == "STANDARD" || saved == "DESKTOP_MODE_4" || saved == "DESKTOP_MODE_11") {
            prefs.edit().putString("key_desktop_architecture", "NONE").apply()
        }
    }

    @JvmStatic
    fun synchronizeDesktopWebView(
        webView: WebView,
        url: String?,
        architecture: DesktopArchitecture,
        updateUserAgent: Boolean
    ) {
        if (!defaultUserAgents.containsKey(webView)) {
            defaultUserAgents[webView] = webView.settings.userAgentString
        }

        if (architecture == DesktopArchitecture.WINDOWS_10_TOUCH) {
            if (isAuthenticationUrl(url)) {
                if (updateUserAgent) {
                    webView.settings.userAgentString = null
                }
                scriptHandlers.remove(webView)?.remove()
                return
            }

            if (updateUserAgent) {
                webView.settings.userAgentString = WINDOWS_10_TOUCH_USER_AGENT
                webView.settings.useWideViewPort = true
                webView.settings.loadWithOverviewMode = true
            }

            if (WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
                scriptHandlers.remove(webView)?.remove()
                val handler = WebViewCompat.addDocumentStartJavaScript(
                    webView,
                    WINDOWS_10_TOUCH_SCRIPT,
                    setOf("*")
                )
                scriptHandlers[webView] = handler
            } else {
                webView.evaluateJavascript(WINDOWS_10_TOUCH_SCRIPT, null)
            }
        } else {
            if (updateUserAgent) {
                webView.settings.userAgentString = null
            }
            scriptHandlers.remove(webView)?.remove()
        }
    }
}
