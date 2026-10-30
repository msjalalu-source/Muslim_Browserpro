package com.muslim.browser.pro.browser

import android.os.Build
import android.view.WindowManager
import androidx.core.view.WindowCompat
import com.muslim.browser.pro.MainActivity
import com.muslim.browser.pro.ui.theme.AppTheme
import com.muslim.browser.pro.ui.theme.BlackWhiteColors
import com.muslim.browser.pro.ui.theme.WhiteColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class StatusBarAndCutoutColorTest {

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q]) // Android 10
    fun test1_cutoutModeAndFlagsConfigured_api29() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).create()
        val activity = controller.get()
        val window = activity.window
        val lp = window.attributes

        // On API 28+ P, cutout mode must allow extending into cutout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            @Suppress("DEPRECATION")
            val expectedMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            assertEquals(expectedMode, lp.layoutInDisplayCutoutMode)
        }

        // Flag checks: DRAWS_SYSTEM_BAR_BACKGROUNDS active, TRANSLUCENT_STATUS cleared
        val flags = lp.flags
        assertTrue((flags and WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) != 0)
        assertEquals(0, flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // Transparent status bar
        assertEquals(android.graphics.Color.TRANSPARENT, window.statusBarColor)

        // Android 10+ contrast enforcement disabled
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            assertFalse(window.isStatusBarContrastEnforced)
            assertFalse(window.isNavigationBarContrastEnforced)
        }
        controller.destroy()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.R]) // Android 11+
    fun test2_cutoutModeAlways_api30Plus() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).create()
        val activity = controller.get()
        val window = activity.window
        val lp = window.attributes

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            assertEquals(
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS,
                lp.layoutInDisplayCutoutMode
            )
        }
        controller.destroy()
    }

    @Test
    fun test3_themeColorResolutionConsistency() {
        // White Theme: light status bar icons (dark text/icons), white/light surfaces
        assertTrue(WhiteColors.isLight)
        assertEquals(androidx.compose.ui.graphics.Color(0xFFFFFFFF), WhiteColors.surface)
        assertEquals(androidx.compose.ui.graphics.Color(0xFFF8F9FA), WhiteColors.background)

        // Pure Black Theme: dark status bar icons (light text/icons), pure black surfaces (#000000)
        assertFalse(BlackWhiteColors.isLight)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF000000), BlackWhiteColors.surface)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF000000), BlackWhiteColors.background)
        assertEquals(androidx.compose.ui.graphics.Color(0xFFFFFFFF), BlackWhiteColors.textPrimary)
        assertEquals(androidx.compose.ui.graphics.Color(0xFFBDBDBD), BlackWhiteColors.textSecondary)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF333333), BlackWhiteColors.border)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF8AB4F8), BlackWhiteColors.accent)
    }

    @Test
    fun test4_dynamicThemeAppearanceSwitching() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).create()
        val activity = controller.get()
        val window = activity.window
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        assertNotNull(insetsController)

        // Switch to White theme
        activity.viewModel.setAppTheme(AppTheme.WHITE)
        assertEquals(AppTheme.WHITE, activity.viewModel.uiState.value.appTheme)

        // Switch to Black theme
        activity.viewModel.setAppTheme(AppTheme.BLACK)
        assertEquals(AppTheme.BLACK, activity.viewModel.uiState.value.appTheme)
        controller.destroy()
    }
}
