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
    BLACK("Black");

    companion object {
        val BLACK_WHITE: AppTheme get() = BLACK
    }
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
    val isMonochrome: Boolean,
    val disabledText: Color = Color(0xFF757575)
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
    isMonochrome = false,
    disabledText = Color(0xFF757575)
)

val BlackColors = AppColors(
    background = Color(0xFF000000),
    surface = Color(0xFF000000),
    surfaceVariant = Color(0xFF1E1E1E),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFBDBDBD),
    border = Color(0xFF333333),
    accent = Color(0xFF8AB4F8),
    iconTint = Color(0xFFBDBDBD),
    buttonBackground = Color(0xFF8AB4F8),
    buttonText = Color(0xFF000000),
    isLight = false,
    isMonochrome = true,
    disabledText = Color(0xFF757575)
)

val BlackWhiteColors: AppColors get() = BlackColors

val LocalAppColors: ProvidableCompositionLocal<AppColors> = staticCompositionLocalOf { BlackColors }

@Composable
fun MyApplicationTheme(
    appTheme: AppTheme = AppTheme.BLACK,
    content: @Composable () -> Unit
) {
    val colors = when (appTheme) {
        AppTheme.WHITE -> WhiteColors
        AppTheme.BLACK -> BlackColors
    }

    val m3Scheme: ColorScheme = if (colors.isLight) {
        lightColorScheme(
            primary = colors.accent,
            onPrimary = Color.White,
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.surface,
            onSurface = colors.textPrimary,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.textSecondary,
            outline = colors.border
        )
    } else {
        darkColorScheme(
            primary = colors.accent,
            onPrimary = Color.Black,
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.surface,
            onSurface = colors.textPrimary,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.textSecondary,
            outline = colors.border,
            surfaceTint = Color.Transparent
        )
    }

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = m3Scheme,
            content = content
        )
    }
}
