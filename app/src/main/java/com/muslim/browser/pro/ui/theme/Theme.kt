package com.muslim.browser.pro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Three distinct app themes supported by Muslim Browser Pro.
 * Persisted simply and centrally via SettingsRepository.
 */
enum class AppTheme(val displayName: String) {
    BLACK("Black"),
    WHITE("White"),
    BLACK_WHITE("Black & White")
}

/**
 * Centralized, low-code color tokens for Muslim Browser Pro.
 * Provides a single source of truth for background, surface, text, borders, and accents.
 */
data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val iconTint: Color,
    val accent: Color,
    val buttonBackground: Color,
    val buttonText: Color,
    val isLight: Boolean = false,
    val isMonochrome: Boolean = false
)

// 1. Black Theme: Deep dark background, card surfaces, readable light text, cyan accents
val BlackColors = AppColors(
    background = Color(0xFF0A0F1D),
    surface = Color(0xFF131D31),
    surfaceVariant = Color(0xFF182238),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF90A4AE),
    border = Color(0x3300E5FF),
    iconTint = Color(0xFF00E5FF),
    accent = Color(0xFF00E5FF),
    buttonBackground = Color(0xFF00E5FF),
    buttonText = Color(0xFF0A0F1D),
    isLight = false,
    isMonochrome = false
)

// 2. White Theme: Clean light background, pure white surface, high-contrast dark text, blue accents
val WhiteColors = AppColors(
    background = Color(0xFFF4F6F9),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE9EEF4),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF5A6A80),
    border = Color(0xFFD1D9E4),
    iconTint = Color(0xFF0284C7),
    accent = Color(0xFF0284C7),
    buttonBackground = Color(0xFF0284C7),
    buttonText = Color(0xFFFFFFFF),
    isLight = true,
    isMonochrome = false
)

// 3. Black & White Theme: Pure monochrome, neutral grays, zero colorful gradients or accents
val BlackWhiteColors = AppColors(
    background = Color(0xFF000000),
    surface = Color(0xFF141414),
    surfaceVariant = Color(0xFF222222),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFAAAAAA),
    border = Color(0xFF444444),
    iconTint = Color(0xFFFFFFFF),
    accent = Color(0xFFFFFFFF),
    buttonBackground = Color(0xFFFFFFFF),
    buttonText = Color(0xFF000000),
    isLight = false,
    isMonochrome = true
)

val LocalAppColors = staticCompositionLocalOf { BlackColors }

private val DarkM3Scheme = darkColorScheme(
    primary = Color(0xFF00E5FF),
    secondary = Color(0xFF81D4FA),
    surface = Color(0xFF131D31),
    background = Color(0xFF0A0F1D)
)

private val LightM3Scheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    secondary = Color(0xFF0369A1),
    surface = Color(0xFFFFFFFF),
    background = Color(0xFFF4F6F9)
)

private val MonochromeM3Scheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    secondary = Color(0xFFAAAAAA),
    surface = Color(0xFF141414),
    background = Color(0xFF000000)
)

@Composable
fun MyApplicationTheme(
    appTheme: AppTheme = AppTheme.BLACK,
    content: @Composable () -> Unit
) {
    val appColors = when (appTheme) {
        AppTheme.BLACK -> BlackColors
        AppTheme.WHITE -> WhiteColors
        AppTheme.BLACK_WHITE -> BlackWhiteColors
    }

    val m3Scheme = when (appTheme) {
        AppTheme.BLACK -> DarkM3Scheme
        AppTheme.WHITE -> LightM3Scheme
        AppTheme.BLACK_WHITE -> MonochromeM3Scheme
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = m3Scheme,
            content = content
        )
    }
}
