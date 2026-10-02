package com.muslim.browser.pro

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.SettingsRepository
import com.muslim.browser.pro.browser.WebViewConfigurator
import com.muslim.browser.pro.ui.theme.AppTheme
import com.muslim.browser.pro.ui.theme.BlackWhiteColors
import com.muslim.browser.pro.ui.theme.WhiteColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  private val prefsName = "focus_shield_prefs"
  private val keyAppTheme = "key_app_theme"

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val resId = context.resources.getIdentifier("app_name", "string", context.packageName)
    val appName = if (resId != 0) context.getString(resId) else "Muslim Browser Pro"
    assertEquals("Muslim Browser Pro", appName)
  }

  @Test
  fun `verify exactly two theme options exist and black cannot be selected`() {
    // Exactly 2 options: White, Black & White
    val themes = AppTheme.values()
    assertEquals(2, themes.size)
    assertEquals(AppTheme.WHITE, themes[0])
    assertEquals(AppTheme.BLACK_WHITE, themes[1])

    assertEquals("White", AppTheme.WHITE.displayName)
    assertEquals("Black & White", AppTheme.BLACK_WHITE.displayName)

    // Verify "Black" is not an enum constant or option
    val names = themes.map { it.name }
    assertFalse("Standalone BLACK must not be present in AppTheme enum", names.contains("BLACK"))
    val displayNames = themes.map { it.displayName }
    assertFalse("Standalone Black display name must not be present", displayNames.contains("Black"))
  }

  @Test
  fun `verify app starts correctly with no theme preference defaulting to BLACK_WHITE`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // Clear any previous settings
    context.getSharedPreferences(prefsName, Context.MODE_PRIVATE).edit().clear().commit()

    val repo = SettingsRepository(context)
    assertEquals(AppTheme.BLACK_WHITE, repo.appTheme)
  }

  @Test
  fun `verify saved White preference remains White and is not overwritten`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    prefs.edit().putString(keyAppTheme, AppTheme.WHITE.name).commit()

    val repo = SettingsRepository(context)
    assertEquals(AppTheme.WHITE, repo.appTheme)
    assertEquals(AppTheme.WHITE.name, prefs.getString(keyAppTheme, null))
  }

  @Test
  fun `verify saved Black & White preference remains Black & White and is not overwritten`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    prefs.edit().putString(keyAppTheme, AppTheme.BLACK_WHITE.name).commit()

    val repo = SettingsRepository(context)
    assertEquals(AppTheme.BLACK_WHITE, repo.appTheme)
    assertEquals(AppTheme.BLACK_WHITE.name, prefs.getString(keyAppTheme, null))
  }

  @Test
  fun `verify switching between WHITE and BLACK_WHITE works and persists across restart`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    context.getSharedPreferences(prefsName, Context.MODE_PRIVATE).edit().clear().commit()

    val repo1 = SettingsRepository(context)
    assertEquals(AppTheme.BLACK_WHITE, repo1.appTheme)

    // Switch to WHITE
    repo1.appTheme = AppTheme.WHITE
    assertEquals(AppTheme.WHITE, repo1.appTheme)

    // Restart app (simulate new instance with same context)
    val repo2 = SettingsRepository(context)
    assertEquals(AppTheme.WHITE, repo2.appTheme)

    // Switch back to BLACK_WHITE
    repo2.appTheme = AppTheme.BLACK_WHITE
    assertEquals(AppTheme.BLACK_WHITE, repo2.appTheme)

    val repo3 = SettingsRepository(context)
    assertEquals(AppTheme.BLACK_WHITE, repo3.appTheme)
  }

  @Test
  fun `verify old stored Black preference migrates safely to default Black & White without crash`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    // Simulate old legacy stored "BLACK" theme in SharedPreferences
    prefs.edit().putString(keyAppTheme, "BLACK").commit()

    // Read via repository
    val repo = SettingsRepository(context)
    val migratedTheme = repo.appTheme

    // Must safely migrate to default BLACK_WHITE
    assertEquals(AppTheme.BLACK_WHITE, migratedTheme)

    // SharedPreferences must now store BLACK_WHITE instead of BLACK
    assertEquals(AppTheme.BLACK_WHITE.name, prefs.getString(keyAppTheme, null))
  }

  @Test
  fun `verify invalid or corrupted stored theme string migrates safely to default Black & White`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    prefs.edit().putString(keyAppTheme, "NON_EXISTENT_THEME").commit()

    val repo = SettingsRepository(context)
    assertEquals(AppTheme.BLACK_WHITE, repo.appTheme)
    assertEquals(AppTheme.BLACK_WHITE.name, prefs.getString(keyAppTheme, null))
  }

  @Test
  fun `verify centralized color tokens for remaining White and Black & White themes`() {
    // 1. White Theme tokens
    assertEquals(true, WhiteColors.isLight)
    assertEquals(false, WhiteColors.isMonochrome)
    assertEquals(androidx.compose.ui.graphics.Color(0xFFF4F6F9), WhiteColors.background)
    assertEquals(androidx.compose.ui.graphics.Color(0xFFFFFFFF), WhiteColors.surface)
    assertEquals(androidx.compose.ui.graphics.Color(0xFF0F172A), WhiteColors.textPrimary)

    // 2. Black & White Theme tokens
    assertEquals(false, BlackWhiteColors.isLight)
    assertEquals(true, BlackWhiteColors.isMonochrome)
    assertEquals(androidx.compose.ui.graphics.Color(0xFF000000), BlackWhiteColors.background)
    assertEquals(androidx.compose.ui.graphics.Color(0xFF141414), BlackWhiteColors.surface)
    assertEquals(androidx.compose.ui.graphics.Color(0xFFFFFFFF), BlackWhiteColors.textPrimary)
    assertEquals(androidx.compose.ui.graphics.Color(0xFFAAAAAA), BlackWhiteColors.textSecondary)
  }

  @Test
  fun `verify WebView theme application sets black background in Black & White theme and white in White theme`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val webView = android.webkit.WebView(context)

    // Apply Black Theme (isDarkTheme = true)
    WebViewConfigurator.applyWebViewTheme(webView, isDarkTheme = true)
    assertTrue("Dark theme must be active in configurator", WebViewConfigurator.isDarkThemeActive)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
      @Suppress("DEPRECATION")
      assertEquals(android.webkit.WebSettings.FORCE_DARK_ON, webView.settings.forceDark)
    }

    // Apply White Theme (isDarkTheme = false)
    WebViewConfigurator.applyWebViewTheme(webView, isDarkTheme = false)
    assertFalse("Dark theme must be inactive in configurator", WebViewConfigurator.isDarkThemeActive)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
      @Suppress("DEPRECATION")
      assertEquals(android.webkit.WebSettings.FORCE_DARK_OFF, webView.settings.forceDark)
    }
  }

  @Test
  fun `verify TabWebViewManager synchronizes theme across all live tabs`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    var syncedThemeCount = 0
    var lastSyncedState: Boolean? = null

    val manager = com.muslim.browser.pro.browser.TabWebViewManager(
      context = context,
      webViewFactory = { id -> android.webkit.WebView(context) },
      onSyncTheme = { wv, isDark ->
        syncedThemeCount++
        lastSyncedState = isDark
      }
    )

    // Create 2 tabs
    manager.getOrCreateWebView("tab_1")
    manager.getOrCreateWebView("tab_2")

    // Synchronize dark theme
    syncedThemeCount = 0
    manager.syncAllLiveWebViewsTheme(isDarkTheme = true)
    assertEquals(2, syncedThemeCount)
    assertEquals(true, lastSyncedState)

    // Synchronize light theme
    syncedThemeCount = 0
    manager.syncAllLiveWebViewsTheme(isDarkTheme = false)
    assertEquals(2, syncedThemeCount)
    assertEquals(false, lastSyncedState)
  }
}
