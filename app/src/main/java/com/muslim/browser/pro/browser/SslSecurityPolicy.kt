package com.muslim.browser.pro.browser

import android.net.Uri
import java.util.concurrent.ConcurrentHashMap

object SslSecurityPolicy {
    private const val TAG = "SslSecurityPolicy"
    private val sessionApprovedHosts = ConcurrentHashMap.newKeySet<String>()

    sealed class Decision {
        object ProceedSessionApproved : Decision()
        data class PromptUser(val host: String, val details: String) : Decision()
        data class Reject(val reason: String) : Decision()
    }

    fun isHostApproved(host: String): Boolean {
        return sessionApprovedHosts.contains(host.lowercase())
    }

    fun extractHostFromUrl(url: String): String {
        return try {
            Uri.parse(url).host ?: url
        } catch (_: Exception) {
            url
        }
    }
}
