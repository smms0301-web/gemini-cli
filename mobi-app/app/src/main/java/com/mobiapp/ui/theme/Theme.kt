package com.mobiapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Amber,
    onPrimary = OnAmber,
    primaryContainer = Color(0xFF3D2E00),
    onPrimaryContainer = AmberLight,
    secondary = AmberDark,
    onSecondary = OnAmber,
    secondaryContainer = Color(0xFF2E2000),
    onSecondaryContainer = AmberLight,
    tertiary = Color(0xFF8B9E6B),
    onTertiary = Color(0xFF1A2000),
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = ErrorColor,
    onError = Color(0xFF1A0000),
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = Color(0xFF363636),
    surfaceContainerHighest = Color(0xFF404040),
    inversePrimary = Color(0xFF5C4000),
    inverseSurface = OnBackground,
    inverseOnSurface = Background,
    scrim = Color(0x99000000)
)

@Composable
fun MobiAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
