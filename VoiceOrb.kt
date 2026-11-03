package com.yesmin.ai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.yesmin.ai.viewmodel.AssistantStatus
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VoiceOrb(
    status: AssistantStatus,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "VoiceOrbTransition")
    
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (status == AssistantStatus.THINKING) 1200 else 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val waveMultiplier = when (status) {
        AssistantStatus.LISTENING -> 2.2f
        AssistantStatus.SPEAKING -> 2.6f
        AssistantStatus.THINKING -> 1.5f
        AssistantStatus.IDLE -> 1.0f
    }

    val primaryColor = when (status) {
        AssistantStatus.LISTENING -> Color(0xFF06B6D4) // Glowing Cyan
        AssistantStatus.THINKING -> Color(0xFFEC4899)  // Quantum Magenta
        AssistantStatus.SPEAKING -> Color(0xFFA855F7)  // Electric Violet
        AssistantStatus.IDLE -> Color(0xFF8B5CF6)      // Futuristic Purple
    }

    val secondaryColor = Color(0xFF3B82F6) // Deep Sci-Fi Blue

    Box(
        modifier = modifier
            .size(240.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.minDimension / 2f

            // 1. Core radial aura glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.35f),
                        secondaryColor.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius * 0.9f
                ),
                radius = maxRadius * 0.9f,
                center = center
            )

            // 2. Harmonic sinusoidal frequency rings
            val baseRadii = listOf(maxRadius * 0.55f, maxRadius * 0.42f, maxRadius * 0.30f)
            baseRadii.forEachIndexed { idx, baseRadius ->
                val waveCount = 5 + idx * 2
                val path = Path()
                val steps = 100

                for (i in 0..steps) {
                    val theta = (i.toFloat() / steps) * (2f * PI.toFloat())
                    val waveOffset = sin(theta * waveCount + phase * (if (idx % 2 == 0) 1f else -1f)) * (6f * waveMultiplier)
                    val r = baseRadius + waveOffset
                    val x = center.x + cos(theta) * r
                    val y = center.y + sin(theta) * r

                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()

                drawPath(
                    path = path,
                    color = if (idx == 0) primaryColor else secondaryColor.copy(alpha = 0.7f),
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // 3. Central luminous power core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        primaryColor,
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius * 0.22f
                ),
                radius = maxRadius * 0.22f,
                center = center
            )
        }
    }
}
