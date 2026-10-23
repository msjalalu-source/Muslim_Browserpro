package com.muslim.browser.pro

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.ShareIntentHandler

/**
 * Application class for Muslim Browser Pro.
 * Guarantees lifecycle execution before any Activity or WebView initialization occurs,
 * and coordinates incoming share intents from the native Android Sharesheet and deep link VIEW intents.
 */
class MuslimBrowserApp : Application() {

    override fun onCreate() {
        super.onCreate()
        com.muslim.browser.pro.browser.DesktopCore.migrateSavedArchitecture(this)
        registerShareIntentDispatcher()
    }

    private fun registerShareIntentDispatcher() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is ComponentActivity && activity.javaClass.name.endsWith("MainActivity")) {
                    // Handle incoming deep link or share intents received while the activity is already running (singleTask)
                    activity.addOnNewIntentListener { newIntent ->
                        activity.intent = newIntent
                        handleIncomingShare(activity, newIntent)
                    }

                    // For cold launch: process the intent only if this is not a configuration change recreation
                    if (savedInstanceState == null) {
                        handleIncomingShare(activity, activity.intent)
                    } else {
                        ShareIntentHandler.markAsProcessed(activity.intent)
                    }
                }
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    companion object {
        const val PREFS_NAME = "focus_shield_prefs"
        const val KEY_APP_THEME = "key_app_theme"

        /**
         * Safely extracts and dispatches a shared URL, deep link URL, or search query
         * from an incoming intent to the active BrowserViewModel and navigates the active WebView.
         *
         * @return true if a valid share or view target was extracted and submitted, false otherwise.
         */
        fun handleIncomingShare(activity: ComponentActivity, intent: Intent?): Boolean {
            if (intent == null || ShareIntentHandler.isAlreadyProcessed(intent)) {
                return false
            }

            val content = ShareIntentHandler.extractContent(intent) ?: return false
            ShareIntentHandler.markAsProcessed(intent)

            return try {
                val viewModel = ViewModelProvider(activity)[BrowserViewModel::class.java]
                val submitted = viewModel.submitQueryOrUrl(content)
                if (submitted) {
                    val currentUrl = viewModel.uiState.value.currentUrl
                    if (currentUrl.isNotBlank()) {
                        val mainActivity = activity as? MainActivity
                        val liveWebView = try {
                            mainActivity?.tabWebViewManager?.getLiveWebView(viewModel.uiState.value.currentTabId)
                        } catch (_: Throwable) {
                            null
                        } ?: run {
                            try {
                                val field = activity.javaClass.getDeclaredField("webViewInstance")
                                field.isAccessible = true
                                field.get(activity) as? WebView
                            } catch (_: Throwable) {
                                null
                            }
                        }
                        liveWebView?.post {
                            liveWebView.loadUrl(currentUrl)
                        }
                    }
                }
                submitted
            } catch (_: Exception) {
                false
            }
        }
    }
}
