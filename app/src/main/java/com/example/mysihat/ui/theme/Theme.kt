package com.example.mysihat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueVeryLight,
    onPrimaryContainer = BlueDark,
    secondary = BlueMedium,
    onSecondary = Color.White,
    secondaryContainer = BlueSurface,
    onSecondaryContainer = BlueDark,
    tertiary = BlueGlow,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextDark,
    surface = SurfaceLight,
    onSurface = TextDark,
    surfaceVariant = BlueSurface,
    onSurfaceVariant = TextMuted,
    outline = CardBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueSoft,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = BlueVeryLight,
    secondary = BlueGlow,
    onSecondary = Color.Black,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
)

@Composable
fun MySihatTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // Always enforce LightColorScheme so text inside OutlinedTextField is always crisp dark (#0F172A) on white cards
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
