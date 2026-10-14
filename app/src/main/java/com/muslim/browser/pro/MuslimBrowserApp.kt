package com.muslim.browser.pro

import android.app.Application
import android.content.Context

/**
 * Application class for Muslim Browser Pro.
 * Guarantees lifecycle execution before any Activity or WebView initialization occurs.
 */
class MuslimBrowserApp : Application() {

    override fun onCreate() {
        super.onCreate()
        migrateLegacyTheme(this)
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
    }
}
