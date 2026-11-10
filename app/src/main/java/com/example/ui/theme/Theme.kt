package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RestPrimaryDark,
    secondary = RestSecondaryDark,
    tertiary = RestTertiary,
    background = Color(0xFF141517),
    surface = Color(0xFF1A1B1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFC1C2C5),
    onSurface = Color(0xFFC1C2C5)
)

private val LightColorScheme = lightColorScheme(
    primary = RestPrimary,
    secondary = RestSecondary,
    tertiary = RestTertiary,
    background = RestBackground,
    surface = RestSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF212529),
    onSurface = Color(0xFF212529)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
