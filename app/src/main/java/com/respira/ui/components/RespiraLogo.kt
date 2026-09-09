package com.respira.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.respira.ui.theme.CardSurface
import com.respira.ui.theme.PrimaryEmerald
import com.respira.ui.theme.SageBorder
import com.respira.ui.theme.SecondarySage
import com.respira.ui.theme.TextPrimary
import com.respira.ui.theme.TextSecondary

/**
 * Simplified, minimalist logo mark for Respira.
 * Clean, simple symmetrical breath loop symbolizing conscious respiration.
 */
@Composable
fun RespiraLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    showBackground: Boolean = true,
    backgroundColor: Color = CardSurface,
    borderColor: Color = SageBorder
) {
    Box(
        modifier = modifier
            .size(size)
            .then(
                if (showBackground) {
                    Modifier
                        .clip(CircleShape)
                        .background(backgroundColor)
                        .border(1.dp, borderColor, CircleShape)
                } else Modifier
            )
            .testTag("respira_logo_mark"),
        contentAlignment = Alignment.Center
    ) {
        val innerSize = size * 0.54f
        Canvas(
            modifier = Modifier.size(innerSize)
        ) {
            val w = this.size.width
            val h = this.size.height
            val strokeWidth = (w * 0.13f).coerceAtLeast(2.5f)

            // Symmetrical, minimal breath arcs (left & right lobes)
            val leftArc = Path().apply {
                moveTo(w * 0.5f, h * 0.16f)
                cubicTo(w * 0.10f, h * 0.28f, w * 0.10f, h * 0.72f, w * 0.5f, h * 0.84f)
            }
            drawPath(
                path = leftArc,
                color = PrimaryEmerald,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            val rightArc = Path().apply {
                moveTo(w * 0.5f, h * 0.16f)
                cubicTo(w * 0.90f, h * 0.28f, w * 0.90f, h * 0.72f, w * 0.5f, h * 0.84f)
            }
            drawPath(
                path = rightArc,
                color = PrimaryEmerald.copy(alpha = 0.75f),
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            // Simple center breath node
            drawCircle(
                color = PrimaryEmerald,
                radius = strokeWidth * 0.7f,
                center = Offset(w * 0.5f, h * 0.50f)
            )
        }
    }
}

/**
 * Combined Logo and Name branding header.
 */
@Composable
fun RespiraBranding(
    modifier: Modifier = Modifier,
    logoSize: Dp = 40.dp,
    showTagline: Boolean = true,
    tagline: String = "Conscious Breathwork"
) {
    Row(
        modifier = modifier.testTag("respira_branding_header"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RespiraLogoMark(size = logoSize)

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Respira",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.5).sp
                )
            )
            if (showTagline) {
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = tagline,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SecondarySage,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.2.sp
                    )
                )
            }
        }
    }
}
