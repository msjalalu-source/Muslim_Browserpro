package com.muslim.browser.pro

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.ShareIntentHandler

/**
 * Application class for Muslim Browser Pro.
 * Guarantees lifecycle execution before any Activity or WebView initialization occurs,
 * and coordinates incoming share intents from the native Android Sharesheet.
 */
class MuslimBrowserApp : Application() {

    override fun onCreate() {
        super.onCreate()
        migrateLegacyTheme(this)
        registerShareIntentDispatcher()
    }

    private fun registerShareIntentDispatcher() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is ComponentActivity && activity.javaClass.name.endsWith("MainActivity")) {
                    // Handle incoming share intents received while the activity is already running (singleTask)
                    activity.addOnNewIntentListener { newIntent ->
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
        const val LEGACY_FORCE_DARK_ON = "FORCE_DARK_ON"
        const val THEME_WHITE = "WHITE"

        /**
         * Migrates any legacy "FORCE_DARK_ON" theme preference to "WHITE".
         * Preserves existing "WHITE" and "BLACK_WHITE" preferences.
         * Runs synchronously using commit() before SettingsRepository or WebView initialization.
         *
         * @return true if migration occurred, false otherwise.
         */
        fun migrateLegacyTheme(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val storedTheme = prefs.getString(KEY_APP_THEME, null)
            return if (storedTheme == LEGACY_FORCE_DARK_ON) {
                prefs.edit().putString(KEY_APP_THEME, THEME_WHITE).commit()
            } else {
                false
            }
        }

        /**
         * Safely extracts and dispatches a shared URL or search query from an incoming intent
         * to the active BrowserViewModel.
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
                viewModel.submitQueryOrUrl(content)
            } catch (_: Exception) {
                false
            }
        }
    }
}
