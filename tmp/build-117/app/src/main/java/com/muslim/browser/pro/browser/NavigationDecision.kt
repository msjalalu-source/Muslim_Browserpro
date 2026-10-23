package com.muslim.browser.pro.browser

sealed class NavigationDecision {
    data object Allowed : NavigationDecision()
    data class Blocked(val reason: String, val detail: String) : NavigationDecision()
    data class Redirect(val url: String) : NavigationDecision()
    data class Download(val url: String, val mimeType: String? = null) : NavigationDecision()
}
