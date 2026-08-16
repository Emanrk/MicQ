package com.eman.micq.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MicQColorScheme = darkColorScheme(
    primary = MicQPrimary,
    secondary = MicQSecondary,
    background = MicQBackground,
    surface = MicQSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = MicQOnBackground,
    onSurface = MicQOnSurface,
    surfaceVariant = MicQSurface.copy(alpha = 0.8f),
    onSurfaceVariant = Color.LightGray
)

// We only use the dark-themed night-life aesthetic for MicQ, 
// even in "light" mode to preserve the atmosphere.
@Composable
fun MicQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to maintain brand identity
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MicQColorScheme,
        typography = Typography,
        content = content
    )
}
