package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ClassicLightColorScheme = lightColorScheme(
    primary = ClassicBlue,
    onPrimary = TextLight,
    primaryContainer = ClassicBlueContainer,
    onPrimaryContainer = ClassicBlueText,
    secondary = ClassicPurple,
    onSecondary = TextLight,
    secondaryContainer = ClassicPurpleContainer,
    onSecondaryContainer = ClassicPurple,
    tertiary = ClassicGreen,
    onTertiary = TextLight,
    tertiaryContainer = ClassicGreenContainer,
    onTertiaryContainer = ClassicGreenText,
    background = ClassicBg,
    onBackground = TextDarkPrimary,
    surface = ClassicSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = ClassicSurfaceVariant,
    onSurfaceVariant = TextDarkSecondary,
    outline = ClassicBorder,
    outlineVariant = ClassicBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = ClassicLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = ClassicBg.toArgb()
            window.navigationBarColor = ClassicSurface.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
