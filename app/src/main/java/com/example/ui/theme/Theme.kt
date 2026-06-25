package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TwitterBlue,
    onPrimary = Color.White,
    secondary = TextSecondary,
    onSecondary = TextPrimary,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextPrimary,
    outline = DarkOutline,
    error = DarkError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = TwitterBlue,
    onPrimary = Color.White,
    secondary = Color(0xFF536471),
    onSecondary = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF7F9F9),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFEFF3F4),
    onSurfaceVariant = Color.Black,
    outline = Color(0xFFCFD9DE),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Theme by default to deliver the modern Twitter/X dark aesthetic requested
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
