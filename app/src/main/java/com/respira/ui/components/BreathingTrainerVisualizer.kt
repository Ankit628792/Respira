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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.respira.ui.theme.PrimaryEmerald
import com.respira.ui.theme.SecondarySage
import com.respira.ui.theme.TextPrimary
import com.respira.ui.theme.TextSecondary
import com.respira.viewmodel.BreathingPhase
import kotlin.math.cos
import kotlin.math.sin

/**
 * Large central animated circular timer with a moving node along the perimeter.
 * Styled with the Sage & Soft Emerald color scheme for an eye-friendly dark mode.
 */
@Composable
fun BreathingTrainerVisualizer(
    phase: BreathingPhase,
    secondsRemaining: Int,
    phaseTotalSeconds: Int,
    isPaused: Boolean = false,
    modifier: Modifier = Modifier
) {
    val totalSecsSafe = phaseTotalSeconds.coerceAtLeast(1)
    val targetProgress = ((totalSecsSafe - secondsRemaining).toFloat() / totalSecsSafe.toFloat()).coerceIn(0f, 1f)

    val animatedSweep = remember { Animatable(0f) }

    LaunchedEffect(phase, secondsRemaining) {
        animatedSweep.animateTo(
            targetValue = targetProgress,
            animationSpec = tween(durationMillis = 950, easing = LinearEasing)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "orbGlow")
    val orbPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbPulse"
    )

    val instruction = when (phase) {
        BreathingPhase.PREPARING -> "Get Ready"
        BreathingPhase.INHALE -> "Inhale"
        BreathingPhase.HOLD_IN -> "Hold"
        BreathingPhase.EXHALE -> "Exhale"
        BreathingPhase.HOLD_OUT -> "Hold Empty"
        BreathingPhase.FINISHED -> "Complete"
    }

    val subInstruction = when (phase) {
        BreathingPhase.PREPARING -> "Relax your shoulders"
        BreathingPhase.INHALE -> "Through your nose deep"
        BreathingPhase.HOLD_IN -> "Hold gently with stillness"
        BreathingPhase.EXHALE -> "Slow continuous release"
        BreathingPhase.HOLD_OUT -> "Pause in tranquil calm"
        BreathingPhase.FINISHED -> "Session complete"
    }

    Box(
        modifier = modifier
            .size(310.dp)
            .testTag("breathing_trainer_visualizer"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10.dp.toPx()
            val diameter = size.minDimension - strokeWidth - 28.dp.toPx()
            val radius = diameter / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val topLeft = Offset(center.x - radius, center.y - radius)
            val arcSize = Size(diameter, diameter)

            // Outer subtle track (eye-safe muted charcoal)
            drawArc(
                color = Color(0xFF1A241F),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active animated breathing ring with Primary Emerald & Sage gradient
            val sweepAngle = 360f * animatedSweep.value
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to PrimaryEmerald,
                    0.5f to SecondarySage,
                    1.0f to PrimaryEmerald
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Inner atmospheric backdrop
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF142019),
                        Color(0xFF101713),
                        Color(0xFF0B0F0D)
                    ),
                    center = center,
                    radius = radius * 0.92f
                ),
                center = center,
                radius = radius * 0.92f
            )

            // Inner subtle guide ring
            drawCircle(
                color = Color(0xFF1C2821),
                center = center,
                radius = radius * 0.88f,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Moving Node along the perimeter!
            val currentAngleRad = Math.toRadians((-90f + sweepAngle).toDouble())
            val nodeX = (center.x + radius * cos(currentAngleRad)).toFloat()
            val nodeY = (center.y + radius * sin(currentAngleRad)).toFloat()
            val nodeCenter = Offset(nodeX, nodeY)

            // Soft glowing halo around node using Primary Emerald
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PrimaryEmerald.copy(alpha = 0.5f * orbPulse),
                        PrimaryEmerald.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = nodeCenter,
                    radius = 22.dp.toPx()
                ),
                center = nodeCenter,
                radius = 22.dp.toPx()
            )

            // Outer node border
            drawCircle(
                color = TextPrimary,
                center = nodeCenter,
                radius = 8.5.dp.toPx()
            )

            // Core emerald node
            drawCircle(
                color = PrimaryEmerald,
                center = nodeCenter,
                radius = 6.dp.toPx()
            )
        }

        // Center Content inside circle: Instruction + Large Countdown Seconds
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = instruction.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryEmerald,
                    letterSpacing = 2.sp,
                    fontSize = 15.sp
                ),
                modifier = Modifier.testTag("trainer_instruction_text")
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Large countdown seconds display in TextPrimary
            Text(
                text = "$secondsRemaining",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    fontSize = 68.sp,
                    letterSpacing = (-1).sp
                ),
                modifier = Modifier.testTag("trainer_seconds_countdown")
            )

            Text(
                text = "SECONDS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subInstruction,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}
