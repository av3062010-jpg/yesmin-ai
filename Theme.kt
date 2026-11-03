package com.yesmin.ai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DeepCosmicBlue = Color(0xFF060814)
val DarkNavySurface = Color(0xFF0C102A)
val NeonPurple = Color(0xFFA855F7)
val GlowCyan = Color(0xFF06B6D4)
val ElectricIndigo = Color(0xFF6366F1)
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)

private val DarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    secondary = GlowCyan,
    tertiary = ElectricIndigo,
    background = DeepCosmicBlue,
    surface = DarkNavySurface,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun YesminAiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
