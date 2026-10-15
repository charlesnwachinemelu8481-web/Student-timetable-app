package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Brand Colors
val VioletPrimary = Color(0xFF7C3AED)
val VioletPrimaryDark = Color(0xFFA78BFA)
val VioletContainerLight = Color(0xFFEDE9FE)
val VioletContainerDark = Color(0xFF4C1D95)
val VioletAccent = Color(0xFF6D28D9)

// Light Palette (Calendar aesthetic)
val BackgroundLight = Color(0xFFF8FAFC)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF1F5F9)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF64748B)
val BorderLight = Color(0xFFE2E8F0)
val GridLineLight = Color(0xFFE2E8F0)

// Dark Palette
val BackgroundDark = Color(0xFF0F172A)
val SurfaceDark = Color(0xFF1E293B)
val SurfaceVariantDark = Color(0xFF334155)
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
val BorderDark = Color(0xFF334155)
val GridLineDark = Color(0xFF1E293B)

// Indicator Color for Current Time
val CurrentTimeIndicator = Color(0xFFEF4444)

// Pre-defined Course Pastel Color Themes
data class PastelTheme(
    val id: Int,
    val name: String,
    val lightBackground: Color,
    val darkBackground: Color,
    val accentColor: Color,
    val textColor: Color
)

val CoursePastelThemes = listOf(
    PastelTheme(
        id = 0,
        name = "Lavender Violet",
        lightBackground = Color(0xFFEDE9FE),
        darkBackground = Color(0xFF2E1065),
        accentColor = Color(0xFF7C3AED),
        textColor = Color(0xFF4C1D95)
    ),
    PastelTheme(
        id = 1,
        name = "Sky Blue",
        lightBackground = Color(0xFFE0F2FE),
        darkBackground = Color(0xFF082F49),
        accentColor = Color(0xFF0284C7),
        textColor = Color(0xFF0369A1)
    ),
    PastelTheme(
        id = 2,
        name = "Emerald Mint",
        lightBackground = Color(0xFFD1FAE5),
        darkBackground = Color(0xFF064E3B),
        accentColor = Color(0xFF059669),
        textColor = Color(0xFF047857)
    ),
    PastelTheme(
        id = 3,
        name = "Rose Pink",
        lightBackground = Color(0xFFFFE4E6),
        darkBackground = Color(0xFF4C0519),
        accentColor = Color(0xFFE11D48),
        textColor = Color(0xFFBE123C)
    ),
    PastelTheme(
        id = 4,
        name = "Amber Sun",
        lightBackground = Color(0xFFFEF3C7),
        darkBackground = Color(0xFF451A03),
        accentColor = Color(0xFFD97706),
        textColor = Color(0xFFB45309)
    ),
    PastelTheme(
        id = 5,
        name = "Warm Peach",
        lightBackground = Color(0xFFFFEDD5),
        darkBackground = Color(0xFF431407),
        accentColor = Color(0xFFEA580C),
        textColor = Color(0xFFC2410C)
    ),
    PastelTheme(
        id = 6,
        name = "Indigo Dusk",
        lightBackground = Color(0xFFE0E7FF),
        darkBackground = Color(0xFF1E1B4B),
        accentColor = Color(0xFF4F46E5),
        textColor = Color(0xFF3730A3)
    ),
    PastelTheme(
        id = 7,
        name = "Teal Ocean",
        lightBackground = Color(0xFFCCFBF1),
        darkBackground = Color(0xFF042F2E),
        accentColor = Color(0xFF0D9488),
        textColor = Color(0xFF0F766E)
    )
)

fun getPastelTheme(index: Int): PastelTheme {
    return CoursePastelThemes.getOrElse(index.coerceIn(0, CoursePastelThemes.size - 1)) { CoursePastelThemes[0] }
}
