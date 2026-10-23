package com.muslim.browser.pro.browser

import android.content.Context
import android.os.Bundle
import android.webkit.WebView
import java.util.concurrent.ConcurrentHashMap

class TabWebViewManager(
    val context: Context,
    val maxLiveWebViews: Int = MAX_LIVE_WEBVIEWS,
    val webViewFactory: (String) -> WebView,
    val onSyncTheme: (WebView, Boolean) -> Unit = { _, _ -> },
    val onSyncDesktopMode: (WebView, Boolean) -> Unit = { _, _ -> },
    val onSyncArchitecture: (WebView, DesktopArchitecture) -> Unit = { _, _ -> },
    val isDesktopModeProvider: () -> Boolean = { false },
    val onSaveTabBundle: (String, Bundle) -> Unit = { _, _ -> },
    val architectureProvider: () -> DesktopArchitecture = { DesktopArchitecture.NONE }
) {
    private val liveWebViews = ConcurrentHashMap<String, WebView>()

    companion object {
        const val MAX_LIVE_WEBVIEWS = 5
    }

    fun getLiveWebView(tabId: String): WebView? = liveWebViews[tabId]

    fun hasLiveWebView(tabId: String): Boolean = liveWebViews.containsKey(tabId)

    fun hasLiveWebView(webView: WebView): Boolean = liveWebViews.containsValue(webView)


    fun getOrCreateWebView(
        tabId: String,
        url: String,
        savedState: Bundle? = null,
        onCreated: () -> Unit = {}
    ): Pair<WebView, Boolean> {
        val existing = liveWebViews[tabId]
        if (existing != null) {
            return Pair(existing, false)
        }
        ensureCapacity(tabId)
        val webView = webViewFactory(tabId)
        if (savedState != null) {
            webView.restoreState(savedState)
        } else if (url.isNotBlank()) {
            webView.loadUrl(url)
        }
        liveWebViews[tabId] = webView
        onCreated()
        return Pair(webView, true)
    }

    fun ensureCapacity(protectedTabId: String) {
        if (liveWebViews.size >= maxLiveWebViews) {
            val evictKey = liveWebViews.keys.firstOrNull { it != protectedTabId }
            if (evictKey != null) {
                destroyWebView(evictKey)
            }
        }
    }

    fun getLiveWebViews(): Collection<WebView> = liveWebViews.values

    fun destroyWebView(tabId: String, saveState: Boolean = true) {
        val webView = liveWebViews.remove(tabId) ?: return
        try {
            if (saveState) {
                val bundle = Bundle()
                webView.saveState(bundle)
                onSaveTabBundle(tabId, bundle)
            }
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.clearHistory()
            webView.destroy()
        } catch (_: Exception) {}
    }

    fun destroyAll(saveState: Boolean = true) {
        for (tabId in liveWebViews.keys.toList()) {
            destroyWebView(tabId, saveState = saveState)
        }
    }


    /**
     * Clears WebView data for Clear All Data.
     */
    fun clearAllWebViewsData() {
        liveWebViews.values.forEach { webView ->
            try {
                webView.stopLoading()
                webView.clearCache(true)
                webView.clearFormData()
                webView.clearHistory()
                webView.clearSslPreferences()
                webView.loadUrl("about:blank")
            } catch (_: Exception) {}
        }
    }


    fun pauseAll(exceptTabId: String? = null) {
        liveWebViews.forEach { (id, webView) ->
            if (id != exceptTabId) {
                webView.onPause()
                webView.pauseTimers()
            }
        }
    }

    fun resumeTab(tabId: String) {
        liveWebViews[tabId]?.let { webView ->
            webView.onResume()
            webView.resumeTimers()
        }
    }

    fun forEachLiveWebView(action: (WebView) -> Unit) {
        liveWebViews.values.forEach(action)
    }


    fun syncAllLiveWebViewsArchitecture(arch: DesktopArchitecture) {
        liveWebViews.values.forEach { onSyncArchitecture(it, arch) }
    }

    fun syncAllLiveWebViewsTheme(isDark: Boolean) {
        liveWebViews.values.forEach { onSyncTheme(it, isDark) }
    }
}
