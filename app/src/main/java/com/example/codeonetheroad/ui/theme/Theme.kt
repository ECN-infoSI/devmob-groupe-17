package com.example.codeontheroad.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography

// Custom colors for green-on-black terminal vibe
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00FF00),       // Bright green
    secondary = Color(0xFF00AA00),     // Slightly dimmer green
    background = Color(0xFF000000),    // Pure black
    surface = Color(0xFF121212),       // Dark gray for surfaces
    onPrimary = Color.Black,           // Text on green button
    onSecondary = Color.Black,
    onBackground = Color(0xFF00FF00),  // Green text on black background
    onSurface = Color(0xFF00FF00)
)

@Composable
fun CodeOnTheRoadTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(), // <- function call creates a new instance
        content = content
    )

}
