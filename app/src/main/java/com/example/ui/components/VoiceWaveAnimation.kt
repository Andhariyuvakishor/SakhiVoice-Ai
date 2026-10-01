package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SakhiMarigoldSecondary
import com.example.ui.theme.SakhiRosePrimary

@Composable
fun VoiceWaveAnimation(
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    isActive: Boolean = false,
    amplitude: Float = 0.2f,
    primaryColor: Color = SakhiRosePrimary,
    secondaryColor: Color = SakhiMarigoldSecondary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_rings")

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "wavePhase"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 3.4f
            val ampFactor = if (isActive) (amplitude.coerceIn(0.2f, 1.0f) * 1.3f) else 0.4f

            // Outer ring 2
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        secondaryColor.copy(alpha = if (isActive) 0.35f else 0.12f),
                        secondaryColor.copy(alpha = 0f)
                    ),
                    center = center,
                    radius = baseRadius * pulse2 * ampFactor
                ),
                radius = baseRadius * pulse2 * ampFactor,
                center = center
            )

            // Outer ring 1
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = if (isActive) 0.5f else 0.2f),
                        primaryColor.copy(alpha = 0f)
                    ),
                    center = center,
                    radius = baseRadius * pulse1 * ampFactor
                ),
                radius = baseRadius * pulse1 * ampFactor,
                center = center
            )

            // Inner glowing disc
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.9f),
                        secondaryColor.copy(alpha = 0.85f)
                    ),
                    center = center,
                    radius = baseRadius * 0.85f
                ),
                radius = baseRadius * 0.85f,
                center = center
            )
        }
    }
}
