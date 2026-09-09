package com.respira.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.respira.ui.theme.MutedGreenAccent
import com.respira.ui.theme.NeonGreenAccent

@Composable
fun GlowingLungsGraphic(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 88.dp,
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lungsGlow")
    val pulse by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAlpha"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1f) }
    }

    Canvas(modifier = modifier.size(sizeDp)) {
        val w = size.width
        val h = size.height

        val primaryColor = NeonGreenAccent
        val secondaryColor = MutedGreenAccent
        val glowColor = NeonGreenAccent.copy(alpha = 0.22f * pulse)

        // Glow Aura behind lungs
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glowColor, Color.Transparent),
                center = Offset(w * 0.32f, h * 0.55f),
                radius = w * 0.38f * pulse
            ),
            center = Offset(w * 0.32f, h * 0.55f),
            radius = w * 0.38f * pulse
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glowColor, Color.Transparent),
                center = Offset(w * 0.68f, h * 0.55f),
                radius = w * 0.38f * pulse
            ),
            center = Offset(w * 0.68f, h * 0.55f),
            radius = w * 0.38f * pulse
        )

        // Trachea (windpipe)
        val tracheaX = w * 0.5f
        drawLine(
            color = secondaryColor,
            start = Offset(tracheaX, h * 0.08f),
            end = Offset(tracheaX, h * 0.34f),
            strokeWidth = 3.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Trachea rings
        for (i in 1..3) {
            val ringY = h * (0.12f + i * 0.055f)
            drawLine(
                color = primaryColor.copy(alpha = 0.6f),
                start = Offset(tracheaX - w * 0.045f, ringY),
                end = Offset(tracheaX + w * 0.045f, ringY),
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Left Bronchus
        val leftBronchus = Path().apply {
            moveTo(tracheaX, h * 0.34f)
            cubicTo(
                tracheaX - w * 0.08f, h * 0.38f,
                tracheaX - w * 0.14f, h * 0.44f,
                tracheaX - w * 0.18f, h * 0.52f
            )
        }
        drawPath(
            path = leftBronchus,
            color = secondaryColor,
            style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
        )

        // Right Bronchus
        val rightBronchus = Path().apply {
            moveTo(tracheaX, h * 0.34f)
            cubicTo(
                tracheaX + w * 0.08f, h * 0.38f,
                tracheaX + w * 0.14f, h * 0.44f,
                tracheaX + w * 0.18f, h * 0.52f
            )
        }
        drawPath(
            path = rightBronchus,
            color = secondaryColor,
            style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
        )

        // Left Lung Outer Lobe Path
        val leftLung = Path().apply {
            moveTo(tracheaX - w * 0.06f, h * 0.30f)
            cubicTo(
                tracheaX - w * 0.32f, h * 0.26f,
                tracheaX - w * 0.46f, h * 0.44f,
                tracheaX - w * 0.44f, h * 0.72f
            )
            cubicTo(
                tracheaX - w * 0.42f, h * 0.90f,
                tracheaX - w * 0.28f, h * 0.92f,
                tracheaX - w * 0.16f, h * 0.86f
            )
            cubicTo(
                tracheaX - w * 0.08f, h * 0.80f,
                tracheaX - w * 0.06f, h * 0.60f,
                tracheaX - w * 0.06f, h * 0.30f
            )
            close()
        }

        // Fill subtle glowing green gradient
        drawPath(
            path = leftLung,
            brush = Brush.verticalGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.22f),
                    primaryColor.copy(alpha = 0.06f)
                )
            ),
            style = Fill
        )
        // Outline
        drawPath(
            path = leftLung,
            color = primaryColor,
            style = Stroke(width = 2.2.dp.toPx(), join = StrokeJoin.Round)
        )

        // Right Lung Outer Lobe Path
        val rightLung = Path().apply {
            moveTo(tracheaX + w * 0.06f, h * 0.30f)
            cubicTo(
                tracheaX + w * 0.32f, h * 0.26f,
                tracheaX + w * 0.46f, h * 0.44f,
                tracheaX + w * 0.44f, h * 0.72f
            )
            cubicTo(
                tracheaX + w * 0.42f, h * 0.90f,
                tracheaX + w * 0.28f, h * 0.92f,
                tracheaX + w * 0.16f, h * 0.86f
            )
            cubicTo(
                tracheaX + w * 0.08f, h * 0.80f,
                tracheaX + w * 0.06f, h * 0.60f,
                tracheaX + w * 0.06f, h * 0.30f
            )
            close()
        }

        drawPath(
            path = rightLung,
            brush = Brush.verticalGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.22f),
                    primaryColor.copy(alpha = 0.06f)
                )
            ),
            style = Fill
        )
        drawPath(
            path = rightLung,
            color = primaryColor,
            style = Stroke(width = 2.2.dp.toPx(), join = StrokeJoin.Round)
        )

        // Bronchial tree fine branching vessels
        // Left branches
        drawLine(
            color = secondaryColor.copy(alpha = 0.7f),
            start = Offset(tracheaX - w * 0.18f, h * 0.52f),
            end = Offset(tracheaX - w * 0.28f, h * 0.48f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = secondaryColor.copy(alpha = 0.7f),
            start = Offset(tracheaX - w * 0.18f, h * 0.52f),
            end = Offset(tracheaX - w * 0.26f, h * 0.64f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = primaryColor.copy(alpha = 0.6f),
            start = Offset(tracheaX - w * 0.26f, h * 0.64f),
            end = Offset(tracheaX - w * 0.34f, h * 0.72f),
            strokeWidth = 1.2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Right branches
        drawLine(
            color = secondaryColor.copy(alpha = 0.7f),
            start = Offset(tracheaX + w * 0.18f, h * 0.52f),
            end = Offset(tracheaX + w * 0.28f, h * 0.48f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = secondaryColor.copy(alpha = 0.7f),
            start = Offset(tracheaX + w * 0.18f, h * 0.52f),
            end = Offset(tracheaX + w * 0.26f, h * 0.64f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = primaryColor.copy(alpha = 0.6f),
            start = Offset(tracheaX + w * 0.26f, h * 0.64f),
            end = Offset(tracheaX + w * 0.34f, h * 0.72f),
            strokeWidth = 1.2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
