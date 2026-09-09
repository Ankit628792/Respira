package com.respira.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.respira.ui.theme.OffWhite
import com.respira.ui.theme.SoftGreenAccent
import com.respira.ui.theme.SoftGreenLight
import com.respira.ui.theme.SoftGreenPrimary
import com.respira.ui.theme.SubtleGrey
import com.respira.viewmodel.BreathingPhase
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BreathingVisualizer(
    phase: BreathingPhase,
    secondsRemaining: Int,
    phaseTotalSeconds: Int,
    animationStyle: String = "Pulsing Circle",
    modifier: Modifier = Modifier
) {
    // Animatable scale that smoothly transitions based on breathing phase
    val animatedScale = remember { Animatable(0.45f) }

    LaunchedEffect(phase, phaseTotalSeconds) {
        val target = when (phase) {
            BreathingPhase.PREPARING -> 0.45f
            BreathingPhase.INHALE -> 0.95f
            BreathingPhase.HOLD_IN -> 0.95f
            BreathingPhase.EXHALE -> 0.45f
            BreathingPhase.HOLD_OUT -> 0.45f
            BreathingPhase.FINISHED -> 0.60f
        }

        val durationMillis = (phaseTotalSeconds.coerceAtLeast(1) * 1000)
        animatedScale.animateTo(
            targetValue = target,
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = FastOutSlowInEasing
            )
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientPulse"
    )

    val currentScale = animatedScale.value * ambientPulse

    val phaseLabel = when (phase) {
        BreathingPhase.PREPARING -> "Get Ready"
        BreathingPhase.INHALE -> "Inhale Deeply"
        BreathingPhase.HOLD_IN -> "Hold Breath"
        BreathingPhase.EXHALE -> "Exhale Slowly"
        BreathingPhase.HOLD_OUT -> "Hold Empty"
        BreathingPhase.FINISHED -> "Complete"
    }

    val phaseSubtext = when (phase) {
        BreathingPhase.PREPARING -> "Relax your shoulders"
        BreathingPhase.INHALE -> "Through your nose"
        BreathingPhase.HOLD_IN -> "Soft stillness"
        BreathingPhase.EXHALE -> "Release all tension"
        BreathingPhase.HOLD_OUT -> "Pause in calm"
        BreathingPhase.FINISHED -> "Well done"
    }

    Box(
        modifier = modifier
            .size(310.dp)
            .testTag("breathing_visualizer_box"),
        contentAlignment = Alignment.Center
    ) {
        // Multi-layered animated canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.width / 2

            when (animationStyle) {
                "Expanding Lotus" -> {
                    // Lotus petal pattern
                    val petals = 8
                    val petalRadius = maxRadius * currentScale * 0.72f
                    for (i in 0 until petals) {
                        val angle = (i * 2 * Math.PI / petals).toFloat()
                        val petalCenter = Offset(
                            center.x + (petalRadius * 0.38f * cos(angle)),
                            center.y + (petalRadius * 0.38f * sin(angle))
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    SoftGreenLight.copy(alpha = 0.18f),
                                    SoftGreenPrimary.copy(alpha = 0.04f)
                                ),
                                center = petalCenter,
                                radius = petalRadius * 0.7f
                            ),
                            center = petalCenter,
                            radius = petalRadius * 0.65f
                        )
                    }
                }
                "Calm Waves" -> {
                    // Concentric wave ripple rings
                    for (ring in 1..4) {
                        val ringRadius = maxRadius * currentScale * (0.35f + ring * 0.16f)
                        drawCircle(
                            color = SoftGreenPrimary.copy(alpha = (0.28f / ring)),
                            center = center,
                            radius = ringRadius,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
                else -> {
                    // Default Pulsing Glowing Circle
                    // Outer atmospheric glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                SoftGreenPrimary.copy(alpha = 0.22f),
                                SoftGreenPrimary.copy(alpha = 0.06f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = maxRadius * currentScale
                        ),
                        center = center,
                        radius = maxRadius * currentScale
                    )

                    // Middle translucent harmonic ring
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                SoftGreenAccent.copy(alpha = 0.30f),
                                SoftGreenPrimary.copy(alpha = 0.12f)
                            ),
                            center = center,
                            radius = maxRadius * currentScale * 0.78f
                        ),
                        center = center,
                        radius = maxRadius * currentScale * 0.78f
                    )

                    // Inner energetic boundary stroke
                    drawCircle(
                        color = SoftGreenLight.copy(alpha = 0.65f),
                        center = center,
                        radius = maxRadius * currentScale * 0.78f,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }
            }

            // Core center pearl
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF243B30),
                        Color(0xFF14241B)
                    ),
                    center = center,
                    radius = maxRadius * 0.46f
                ),
                center = center,
                radius = maxRadius * 0.46f
            )
            drawCircle(
                color = SoftGreenAccent.copy(alpha = 0.45f),
                center = center,
                radius = maxRadius * 0.46f,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // Center Instruction Labels
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = phaseLabel,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = OffWhite,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$secondsRemaining s",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = SoftGreenAccent,
                    fontSize = 44.sp
                ),
                modifier = Modifier.testTag("phase_countdown_text")
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = phaseSubtext,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SubtleGrey,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
