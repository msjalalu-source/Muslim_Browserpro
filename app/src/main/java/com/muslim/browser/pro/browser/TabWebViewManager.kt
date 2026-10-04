package com.muslim.browser.pro.browser

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import java.util.Collections
import java.util.LinkedHashMap

/**
 * Manages retained live WebView instances for open browser windows/tabs.
 *
 * Implements an LRU retention cache:
 * - Keeps up to [maxLiveWebViews] live WebView instances in memory so switching between
 *   recently used windows is instantaneous and NEVER causes an unwanted page reload,
 *   DOM wipe, scroll position reset, form data loss, or script re-execution.
 * - When tab count exceeds [maxLiveWebViews], the least-recently-used background window
 *   has its state saved via [WebView.saveState] into its [BrowserTab.bundle] before
 *   its WebView is safely destroyed, preventing unlimited memory usage.
 * - If a user later returns to an evicted window, a new WebView instance is instantiated
 *   and restored from its saved bundle via [WebView.restoreState].
 */
class TabWebViewManager(
    val context: Context,
    val maxLiveWebViews: Int = MAX_LIVE_WEBVIEWS,
    private val webViewFactory: (tabId: String) -> WebView,
    private val onSaveTabBundle: (tabId: String, bundle: Bundle) -> Unit = { _, _ -> },
    private val onSyncTheme: ((WebView, Boolean) -> Unit)? = null,
    private val onSyncDesktopMode: ((WebView, Boolean) -> Unit)? = null,
    private val isDesktopModeProvider: (() -> Boolean)? = null,
    private val onSyncArchitecture: ((WebView, DesktopArchitecture) -> Unit)? = null,
    private val architectureProvider: (() -> DesktopArchitecture)? = null
) {
    companion object {
        const val MAX_LIVE_WEBVIEWS = 4
    }

    // Synchronized LinkedHashMap with access-order (true) for LRU tracking
    private val liveWebViews = Collections.synchronizedMap(
        LinkedHashMap<String, WebView>(maxLiveWebViews, 0.75f, true)
    )

    fun hasLiveWebView(tabId: String): Boolean {
        return liveWebViews.containsKey(tabId)
    }

    fun getLiveWebView(tabId: String): WebView? {
        return liveWebViews[tabId]
    }

    fun getOrCreateWebView(
        tabId: String,
        url: String? = null,
        bundle: Bundle? = null,
        onRestored: (() -> Unit)? = null
    ): Pair<WebView, Boolean> {
        val existing = liveWebViews[tabId]
        if (existing != null) {
            val arch = architectureProvider?.invoke()
            if (arch != null) {
                onSyncArchitecture?.invoke(existing, arch)
            } else {
                isDesktopModeProvider?.invoke()?.let { isDesktop ->
                    onSyncDesktopMode?.invoke(existing, isDesktop)
                }
            }
            return Pair(existing, false)
        }

        // Must create or restore a WebView for this tab.
        // First enforce memory bound by evicting LRU background tab if limit reached.
        ensureCapacity(exemptTabId = tabId)

        val newWebView = webViewFactory(tabId)
        val arch = architectureProvider?.invoke()
        if (arch != null) {
            onSyncArchitecture?.invoke(newWebView, arch)
        } else {
            isDesktopModeProvider?.invoke()?.let { isDesktop ->
                onSyncDesktopMode?.invoke(newWebView, isDesktop)
            }
        }

        var restored = false
        if (bundle != null) {
            newWebView.restoreState(bundle)
            restored = true
            onRestored?.invoke()
        } else if (!url.isNullOrBlank()) {
            newWebView.loadUrl(url)
        }

        liveWebViews[tabId] = newWebView
        return Pair(newWebView, restored)
    }

    /**
     * Synchronizes all currently retained live WebViews with the authoritative theme state.
     * Ensures consistent dark/light background and dark mode across all tabs.
     */
    fun syncAllLiveWebViewsTheme(isDarkTheme: Boolean) {
        synchronized(liveWebViews) {
            for ((_, webView) in liveWebViews) {
                try {
                    onSyncTheme?.invoke(webView, isDarkTheme)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Synchronizes all currently retained live WebViews with Desktop Mode state.
     */
    fun syncAllLiveWebViews(isDesktopMode: Boolean) {
        synchronized(liveWebViews) {
            for ((_, webView) in liveWebViews) {
                try {
                    onSyncDesktopMode?.invoke(webView, isDesktopMode)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Synchronizes all currently retained live WebViews with the active DesktopArchitecture.
     */
    fun syncAllLiveWebViewsArchitecture(architecture: DesktopArchitecture) {
        synchronized(liveWebViews) {
            for ((_, webView) in liveWebViews) {
                try {
                    if (onSyncArchitecture != null) {
                        onSyncArchitecture.invoke(webView, architecture)
                    } else {
                        onSyncDesktopMode?.invoke(webView, architecture.isAnyDesktop)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Checks if the given WebView is currently tracked in [liveWebViews].
     */
    fun hasLiveWebView(webView: WebView): Boolean {
        synchronized(liveWebViews) {
            return liveWebViews.containsValue(webView)
        }
    }

    /**
     * Executes an action on all currently retained live WebViews.
     */
    fun forEachLiveWebView(action: (WebView) -> Unit) {
        synchronized(liveWebViews) {
            for ((_, webView) in liveWebViews) {
                try {
                    action(webView)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Ensures live WebView count stays within [maxLiveWebViews].
     * Evicts the least recently used tab that is not [exemptTabId].
     */
    fun ensureCapacity(exemptTabId: String) {
        synchronized(liveWebViews) {
            while (liveWebViews.size >= maxLiveWebViews) {
                val iterator = liveWebViews.entries.iterator()
                var candidate: Map.Entry<String, WebView>? = null
                while (iterator.hasNext()) {
                    val entry = iterator.next()
                    if (entry.key != exemptTabId) {
                        candidate = entry
                        break
                    }
                }
                if (candidate == null) break

                val evictedTabId = candidate.key
                val evictedWebView = candidate.value

                // 1. Save state before eviction
                val savedBundle = Bundle()
                try {
                    evictedWebView.saveState(savedBundle)
                    onSaveTabBundle(evictedTabId, savedBundle)
                } catch (e: Exception) {
                    android.util.Log.e("TabWebViewManager", "Error saving state on eviction for $evictedTabId", e)
                }

                // 2. Remove from parent, pause and destroy
                try {
                    (evictedWebView.parent as? ViewGroup)?.removeView(evictedWebView)
                    evictedWebView.stopLoading()
                    evictedWebView.pauseTimers()
                    evictedWebView.destroy()
                } catch (e: Exception) {
                    android.util.Log.e("TabWebViewManager", "Error destroying evicted WebView for $evictedTabId", e)
                }

                // 3. Remove from live map
                iterator.remove()
            }
        }
    }

    /**
     * Explicitly destroys and removes a tab's WebView (e.g. when user closes a tab).
     */
    fun destroyWebView(tabId: String) {
        val webView = liveWebViews.remove(tabId) ?: return
        try {
            (webView.parent as? ViewGroup)?.removeView(webView)
            webView.stopLoading()
            webView.pauseTimers()
            webView.destroy()
        } catch (e: Exception) {
            android.util.Log.e("TabWebViewManager", "Error destroying WebView for $tabId", e)
        }
    }

    /**
     * Pauses all inactive WebViews to prevent background CPU/battery drain.
     */
    fun pauseAll(exceptTabId: String? = null) {
        synchronized(liveWebViews) {
            for ((id, webView) in liveWebViews) {
                if (id != exceptTabId) {
                    try {
                        webView.onPause()
                        webView.pauseTimers()
                    } catch (_: Exception) {}
                }
            }
        }
    }

    /**
     * Resumes the active tab's WebView.
     */
    fun resumeTab(tabId: String) {
        val webView = liveWebViews[tabId] ?: return
        try {
            webView.onResume()
            webView.resumeTimers()
        } catch (_: Exception) {}
    }

    /**
     * Clears browsing data across all retained WebViews.
     */
    fun clearAllData() {
        synchronized(liveWebViews) {
            for ((_, webView) in liveWebViews) {
                try {
                    webView.clearHistory()
                    webView.clearCache(true)
                    webView.clearFormData()
                    webView.clearSslPreferences()
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Destroys all WebViews (called on Activity onDestroy).
     */
    fun destroyAll() {
        synchronized(liveWebViews) {
            for ((_, webView) in liveWebViews) {
                try {
                    (webView.parent as? ViewGroup)?.removeView(webView)
                    webView.stopLoading()
                    webView.pauseTimers()
                    webView.onPause()
                    webView.removeAllViews()
                    webView.destroy()
                } catch (_: Exception) {}
            }
            liveWebViews.clear()
        }
    }

    val liveWebViewCount: Int
        get() = liveWebViews.size

    val liveTabIds: Set<String>
        get() = synchronized(liveWebViews) { liveWebViews.keys.toSet() }
}
