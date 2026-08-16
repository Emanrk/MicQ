package com.eman.micq.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val MicQPrimary = Color(0xFFFF6B9D)   // Pink
val MicQSecondary = Color(0xFF4ECDC4) // Teal
val MicQBackground = Color(0xFF0F0F1A)
val MicQSurface = Color(0xFF1A1A2E)

val MicQOnBackground = Color(0xFFFFFFFF)
val MicQOnSurface = Color(0xFFE0E0E0)

// Hero Gradient: Primary -> Secondary
val MicQHeroGradient = Brush.linearGradient(
    colors = listOf(MicQPrimary, MicQSecondary)
)
