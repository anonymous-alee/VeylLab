package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonYellow,
    onPrimary = ObsidianBlack,
    primaryContainer = Color(0xFF232300),
    onPrimaryContainer = NeonYellow,
    secondary = CyberCyan,
    onSecondary = ObsidianBlack,
    secondaryContainer = Color(0xFF002A30),
    onSecondaryContainer = CyberCyan,
    tertiary = CyberOrange,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6B7200),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAF205),
    onPrimaryContainer = Color(0xFF1E2100),
    secondary = Color(0xFF006874),
    onSecondary = Color.White,
    background = Color(0xFFF8F9FE),
    onBackground = Color(0xFF191C20),
    surface = Color.White,
    onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = Color(0xFF44474F),
    outline = Color(0xFF74777F)
)

@Composable
fun AleetrixTheme(
    darkTheme: Boolean = true, // Default to sleek OLED Dark Mode for SaaS AI Agency aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

