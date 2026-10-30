package com.muslim.browser.pro.browser

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.MuslimBrowserApp
import com.muslim.browser.pro.ui.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LegacyThemeMigrationTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun testDefaultThemeIsBlack() {
        val repo = SettingsRepository(context)
        assertEquals(AppTheme.BLACK, repo.getAppTheme())

        val uiState = BrowserUiState()
        assertEquals(AppTheme.BLACK, uiState.appTheme)
    }

    @Test
    fun testExistingSavedWhiteThemeIsPreserved() {
        val prefs = context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(MuslimBrowserApp.KEY_APP_THEME, "WHITE").commit()

        val repo = SettingsRepository(context)
        assertEquals(AppTheme.WHITE, repo.getAppTheme())
    }

    @Test
    fun testExistingSavedBlackWhiteThemeMigratesToBlack() {
        val prefs = context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
        // Previously saved "BLACK_WHITE" preference migrates safely to Black theme
        prefs.edit().putString(MuslimBrowserApp.KEY_APP_THEME, "BLACK_WHITE").commit()

        val repo = SettingsRepository(context)
        assertEquals(AppTheme.BLACK, repo.getAppTheme())
    }

    @Test
    fun testExistingSavedBlackThemeIsPreserved() {
        val prefs = context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(MuslimBrowserApp.KEY_APP_THEME, "BLACK").commit()

        val repo = SettingsRepository(context)
        assertEquals(AppTheme.BLACK, repo.getAppTheme())
    }

    @Test
    fun testObsoleteOrInvalidThemeFallsBackToDefault() {
        val prefs = context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
        // Obsolete removed theme value
        prefs.edit().putString(MuslimBrowserApp.KEY_APP_THEME, "FORCE_DARK_ON").commit()

        val repo = SettingsRepository(context)
        assertEquals(AppTheme.BLACK, repo.getAppTheme())

        // Completely arbitrary invalid value
        prefs.edit().putString(MuslimBrowserApp.KEY_APP_THEME, "SOME_UNKNOWN_VALUE").commit()
        assertEquals(AppTheme.BLACK, repo.getAppTheme())
    }

    @Test
    fun testThemePersistenceAcrossRestarts() {
        val repo1 = SettingsRepository(context)
        repo1.setAppTheme(AppTheme.WHITE)

        val repo2 = SettingsRepository(context)
        assertEquals(AppTheme.WHITE, repo2.getAppTheme())

        repo2.setAppTheme(AppTheme.BLACK)
        val repo3 = SettingsRepository(context)
        assertEquals(AppTheme.BLACK, repo3.getAppTheme())
    }

    @Test
    fun testAppThemeEnumContainsExactlyTwoThemes() {
        val themes = AppTheme.entries.map { it.name }
        assertEquals(2, themes.size)
        assertTrue(themes.contains("WHITE"))
        assertTrue(themes.contains("BLACK"))
        assertFalse(themes.contains("FORCE_DARK_ON"))

        assertEquals("White", AppTheme.WHITE.displayName)
        assertEquals("Black", AppTheme.BLACK.displayName)
        assertEquals(AppTheme.BLACK, AppTheme.BLACK_WHITE)
    }
}
