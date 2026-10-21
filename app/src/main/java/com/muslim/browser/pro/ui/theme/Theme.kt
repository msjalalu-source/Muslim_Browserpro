package com.muslim.browser.pro.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppTheme(val displayName: String) {
    WHITE("White"),
    BLACK_WHITE("Black & White"),
    FORCE_DARK_ON("Force Dark On")
}

data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val accent: Color,
    val iconTint: Color,
    val buttonBackground: Color,
    val buttonText: Color,
    val isLight: Boolean,
    val isMonochrome: Boolean
)

val WhiteColors = AppColors(
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1F3F4),
    textPrimary = Color(0xFF202124),
    textSecondary = Color(0xFF5F6368),
    border = Color(0xFFDADCE0),
    accent = Color(0xFF1A73E8),
    iconTint = Color(0xFF5F6368),
    buttonBackground = Color(0xFF1A73E8),
    buttonText = Color(0xFFFFFFFF),
    isLight = true,
    isMonochrome = false
)

val BlackWhiteColors = AppColors(
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2D2D2D),
    textPrimary = Color(0xFFE8EAED),
    textSecondary = Color(0xFF9AA0A6),
    border = Color(0xFF3C4043),
    accent = Color(0xFF8AB4F8),
    iconTint = Color(0xFF9AA0A6),
    buttonBackground = Color(0xFF8AB4F8),
    buttonText = Color(0xFF202124),
    isLight = false,
    isMonochrome = true
)

val LocalAppColors: ProvidableCompositionLocal<AppColors> = staticCompositionLocalOf { WhiteColors }

@Composable
fun MyApplicationTheme(
    appTheme: AppTheme = AppTheme.WHITE,
    content: @Composable () -> Unit
) {
    val colors = when (appTheme) {
        AppTheme.WHITE -> WhiteColors
        AppTheme.BLACK_WHITE -> BlackWhiteColors
        AppTheme.FORCE_DARK_ON -> BlackWhiteColors
    }

    val m3Scheme: ColorScheme = if (colors.isLight) {
        lightColorScheme(
            primary = colors.accent,
            background = colors.background,
            surface = colors.surface,
            surfaceVariant = colors.surfaceVariant
        )
    } else {
        darkColorScheme(
            primary = colors.accent,
            background = colors.background,
            surface = colors.surface,
            surfaceVariant = colors.surfaceVariant
        )
    }

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = m3Scheme,
            content = content
        )
    }
}
