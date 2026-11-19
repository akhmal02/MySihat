package com.example.mysihat.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// MySejahtera & Belanja Inspired Soft Blue & Gradient Palette
val BlueDark = Color(0xFF0D47A1)         // Deep Trust Royal Blue
val BluePrimary = Color(0xFF1976D2)      // Classic MySejahtera Primary Blue
val BlueMedium = Color(0xFF2196F3)       // Vibrant Oceanic Blue
val BlueSoft = Color(0xFF42A5F5)         // Soft Sky Accent
val BlueLight = Color(0xFF90CAF9)        // Light Cyan-Blue
val BlueVeryLight = Color(0xFFE3F2FD)    // Soft Sky Background Tint
val BlueSurface = Color(0xFFF0F7FF)      // Ultra-soft Card Background Tint
val BlueGlow = Color(0xFF00B4D8)         // Cyan Glow Accent

// Backgrounds & Surfaces
val BackgroundLight = Color(0xFFF8FAFC)  // Clean Modern Slate 50
val SurfaceLight = Color(0xFFFFFFFF)     // Pure White
val CardBorder = Color(0xFFE2E8F0)       // Subtle Border Grey
val DividerColor = Color(0xFFEEF2F6)

// Text Colors
val TextDark = Color(0xFF0F172A)         // Slate 900 (High contrast readability)
val TextMuted = Color(0xFF475569)        // Slate 600
val TextSubtle = Color(0xFF94A3B8)       // Slate 400

// Clinical Status & Safety (Malaysian CPG & MySejahtera Standards)
val StatusLowRisk = Color(0xFF059669)       // Verified Green
val StatusLowRiskBg = Color(0xFFD1FAE5)     // Soft Mint Green
val StatusVaccinated = Color(0xFF0284C7)    // Cyan/Blue Verified
val StatusVaccinatedBg = Color(0xFFE0F2FE)
val StatusWarning = Color(0xFFD97706)       // Amber
val StatusWarningBg = Color(0xFFFEF3C7)
val StatusAlert = Color(0xFFDC2626)         // Crimson Red
val StatusAlertBg = Color(0xFFFEE2E2)

// Premium Gradient Brushes
val PrimaryHeroGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0D47A1),
        Color(0xFF1976D2),
        Color(0xFF42A5F5)
    )
)

val SoftBlueGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF1976D2),
        Color(0xFF00B4D8)
    )
)

val CardGlowGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFF0F7FF)
    )
)

val StatusCardGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1565C0),
        Color(0xFF1E88E5)
    )
)

val AmberCardGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFD97706),
        Color(0xFFF59E0B)
    )
)
