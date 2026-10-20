package com.muslim.browser.pro.browser

import android.webkit.WebView
import androidx.webkit.ScriptHandler
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import java.util.WeakHashMap

enum class DesktopArchitecture {
    NONE,
    WINDOWS_10_TOUCH
}

object DesktopCore {
    const val WINDOWS_10_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

    val WINDOWS_10_TOUCH_SCRIPT: String = """
        (function() {
            try {
                var host = window.location.hostname || '';
                if (host === 'accounts.google.com' ||
                    host === 'appleid.apple.com' ||
                    host === 'login.microsoftonline.com' ||
                    host.startsWith('auth.') ||
                    host.startsWith('id.') ||
                    host.startsWith('login.')) {
                    return;
                }

                var origClientWidthDesc = Object.getOwnPropertyDescriptor(Element.prototype, 'clientWidth');
                var origClientWidthGet = origClientWidthDesc ? origClientWidthDesc.get : null;

                var origClientHeightDesc = Object.getOwnPropertyDescriptor(Element.prototype, 'clientHeight');
                var origClientHeightGet = origClientHeightDesc ? origClientHeightDesc.get : null;

                Object.defineProperty(navigator, 'platform', { get: function() { return 'Win32'; } });
                Object.defineProperty(navigator, 'maxTouchPoints', { get: function() { return 10; } });

                if (!('ontouchstart' in window)) {
                    window.ontouchstart = null;
                }
            } catch(e) {}
        })();
    """.trimIndent()

    private val scriptHandlers = WeakHashMap<WebView, ScriptHandler>()
    private val defaultUserAgents = WeakHashMap<WebView, String>()

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

        when (architecture) {
            DesktopArchitecture.WINDOWS_10_TOUCH -> {
                if (updateUserAgent) {
                    webView.settings.userAgentString = WINDOWS_10_USER_AGENT
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
            }
            DesktopArchitecture.NONE -> {
                if (updateUserAgent) {
                    val defaultUa = defaultUserAgents[webView]
                    if (defaultUa != null) {
                        webView.settings.userAgentString = defaultUa
                    }
                }
                scriptHandlers.remove(webView)?.remove()
            }
        }
    }
}
