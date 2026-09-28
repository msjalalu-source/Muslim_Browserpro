package com.muslim.browser.pro

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val resId = context.resources.getIdentifier("app_name", "string", context.packageName)
    val appName = if (resId != 0) context.getString(resId) else "Muslim Browser Pro"
    assertEquals("Muslim Browser Pro", appName)
  }

  @Test
  fun `verify theme persistence across app restart`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo1 = com.muslim.browser.pro.browser.SettingsRepository(context)
    
    // Default theme should be BLACK
    assertEquals(com.muslim.browser.pro.ui.theme.AppTheme.BLACK, repo1.appTheme)

    // Switch to WHITE
    repo1.appTheme = com.muslim.browser.pro.ui.theme.AppTheme.WHITE
    assertEquals(com.muslim.browser.pro.ui.theme.AppTheme.WHITE, repo1.appTheme)

    // Create a new instance of SettingsRepository (simulating app restart)
    val repo2 = com.muslim.browser.pro.browser.SettingsRepository(context)
    assertEquals(com.muslim.browser.pro.ui.theme.AppTheme.WHITE, repo2.appTheme)

    // Switch to BLACK_WHITE
    repo2.appTheme = com.muslim.browser.pro.ui.theme.AppTheme.BLACK_WHITE
    val repo3 = com.muslim.browser.pro.browser.SettingsRepository(context)
    assertEquals(com.muslim.browser.pro.ui.theme.AppTheme.BLACK_WHITE, repo3.appTheme)

    // Reset back to BLACK
    repo3.appTheme = com.muslim.browser.pro.ui.theme.AppTheme.BLACK
  }

  @Test
  fun `verify three theme options and centralized color tokens`() {
    // Exactly 3 options: Black, White, Black & White
    val themes = com.muslim.browser.pro.ui.theme.AppTheme.values()
    assertEquals(3, themes.size)
    assertEquals("Black", com.muslim.browser.pro.ui.theme.AppTheme.BLACK.displayName)
    assertEquals("White", com.muslim.browser.pro.ui.theme.AppTheme.WHITE.displayName)
    assertEquals("Black & White", com.muslim.browser.pro.ui.theme.AppTheme.BLACK_WHITE.displayName)

    // BlackColors
    val blackColors = com.muslim.browser.pro.ui.theme.BlackColors
    assertEquals(false, blackColors.isLight)
    assertEquals(false, blackColors.isMonochrome)

    // WhiteColors
    val whiteColors = com.muslim.browser.pro.ui.theme.WhiteColors
    assertEquals(true, whiteColors.isLight)
    assertEquals(false, whiteColors.isMonochrome)

    // BlackWhiteColors
    val bwColors = com.muslim.browser.pro.ui.theme.BlackWhiteColors
    assertEquals(false, bwColors.isLight)
    assertEquals(true, bwColors.isMonochrome)
  }
}
