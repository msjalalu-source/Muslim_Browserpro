package com.muslim.browser.pro.browser

import android.webkit.WebSettings

/**
 * Diagnostics and test verification state for Desktop Mode and window switching.
 * Isolated from core Activity UI lifecycle.
 */
object DesktopModeDiagnostics {
    var urlBeforeToggle: String? = null
    var urlAfterToggle: String? = null
    var userAgentBeforeToggle: String? = null
    var userAgentAfterToggle: String? = null
    var reloadCount: Int = 0
    var loadUrlCount: Int = 0
    var lastTriggerSource: String? = null
    val redirectChain: MutableList<String> = mutableListOf()
    var lastShouldOverrideResult: Boolean = false
    var currentDesktopMode: Boolean = false
    var currentCacheMode: Int = WebSettings.LOAD_DEFAULT
    var webViewRecreationCount: Int = 0
    var isAuthFlowActive: Boolean = false
    var restorationCount: Int = 0
    var windowSwitchReloadCount: Int = 0

    fun reset() {
        urlBeforeToggle = null
        urlAfterToggle = null
        userAgentBeforeToggle = null
        userAgentAfterToggle = null
        reloadCount = 0
        loadUrlCount = 0
        lastTriggerSource = null
        redirectChain.clear()
        lastShouldOverrideResult = false
        currentDesktopMode = false
        currentCacheMode = WebSettings.LOAD_DEFAULT
        webViewRecreationCount = 0
        isAuthFlowActive = false
        restorationCount = 0
        windowSwitchReloadCount = 0
    }
}
