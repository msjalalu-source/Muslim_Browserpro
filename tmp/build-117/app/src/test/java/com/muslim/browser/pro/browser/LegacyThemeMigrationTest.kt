package com.muslim.browser.pro.browser

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.MuslimBrowserApp
import com.muslim.browser.pro.ui.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
        // Reset prefs to clean state before each test
        context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun testMigrationConvertsForceDarkOnToWhite() {
        val prefs = context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(MuslimBrowserApp.KEY_APP_THEME, "FORCE_DARK_ON").commit()

        assertEquals("FORCE_DARK_ON", prefs.getString(MuslimBrowserApp.KEY_APP_THEME, null))

        val migrated = MuslimBrowserApp.migrateLegacyTheme(context)
        assertTrue("Migration should return true when FORCE_DARK_ON was found and migrated", migrated)

        val updatedValue = prefs.getString(MuslimBrowserApp.KEY_APP_THEME, null)
        assertEquals("WHITE", updatedValue)
    }

    @Test
    fun testMigrationPreservesWhiteTheme() {
        val prefs = context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(MuslimBrowserApp.KEY_APP_THEME, "WHITE").commit()

        val migrated = MuslimBrowserApp.migrateLegacyTheme(context)
        assertFalse("Migration should return false when WHITE theme is already set", migrated)

        assertEquals("WHITE", prefs.getString(MuslimBrowserApp.KEY_APP_THEME, null))
    }

    @Test
    fun testMigrationPreservesBlackWhiteTheme() {
        val prefs = context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(MuslimBrowserApp.KEY_APP_THEME, "BLACK_WHITE").commit()

        val migrated = MuslimBrowserApp.migrateLegacyTheme(context)
        assertFalse("Migration should return false when BLACK_WHITE theme is set", migrated)

        assertEquals("BLACK_WHITE", prefs.getString(MuslimBrowserApp.KEY_APP_THEME, null))
    }

    @Test
    fun testMigrationPreservesUnsetDefaults() {
        val prefs = context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
        assertNull(prefs.getString(MuslimBrowserApp.KEY_APP_THEME, null))

        val migrated = MuslimBrowserApp.migrateLegacyTheme(context)
        assertFalse("Migration should return false when no preference is stored", migrated)

        assertNull("Unset preference should remain unset so default applies", prefs.getString(MuslimBrowserApp.KEY_APP_THEME, null))
    }

    @Test
    fun testAppThemeEnumPreservesForceDarkOnBinaryCompatibility() {
        // AppTheme.FORCE_DARK_ON must exist in enum for prebuilt JAR bytecode compatibility
        val forceDark = AppTheme.valueOf("FORCE_DARK_ON")
        assertNotNull(forceDark)
        assertEquals(AppTheme.FORCE_DARK_ON, forceDark)
        assertEquals("Force Dark On", forceDark.displayName)

        // Core supported themes
        assertEquals(AppTheme.WHITE, AppTheme.valueOf("WHITE"))
        assertEquals("White", AppTheme.WHITE.displayName)
        assertEquals(AppTheme.BLACK_WHITE, AppTheme.valueOf("BLACK_WHITE"))
        assertEquals("Black & White", AppTheme.BLACK_WHITE.displayName)
    }

    @Test
    fun testConstantsMatchSettingsRepositoryContract() {
        // Must match exact SharedPreferences file and key used by prebuilt SettingsRepository
        assertEquals("focus_shield_prefs", MuslimBrowserApp.PREFS_NAME)
        assertEquals("key_app_theme", MuslimBrowserApp.KEY_APP_THEME)
        assertEquals("FORCE_DARK_ON", MuslimBrowserApp.LEGACY_FORCE_DARK_ON)
        assertEquals("WHITE", MuslimBrowserApp.THEME_WHITE)
    }

    @Test
    fun testMigrationIdempotency() {
        val prefs = context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(MuslimBrowserApp.KEY_APP_THEME, "FORCE_DARK_ON").commit()

        // First run migrates FORCE_DARK_ON -> WHITE
        val firstRun = MuslimBrowserApp.migrateLegacyTheme(context)
        assertTrue(firstRun)
        assertEquals("WHITE", prefs.getString(MuslimBrowserApp.KEY_APP_THEME, null))

        // Second run is a no-op
        val secondRun = MuslimBrowserApp.migrateLegacyTheme(context)
        assertFalse(secondRun)
        assertEquals("WHITE", prefs.getString(MuslimBrowserApp.KEY_APP_THEME, null))
    }
}
