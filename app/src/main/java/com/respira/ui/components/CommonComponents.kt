package com.respira.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.respira.data.model.LungTestRecord
import com.respira.ui.theme.CharcoalBorder
import com.respira.ui.theme.CharcoalCard
import com.respira.ui.theme.CharcoalCardElevated
import com.respira.ui.theme.MutedGrey
import com.respira.ui.theme.OffWhite
import com.respira.ui.theme.SoftAmber
import com.respira.ui.theme.SoftGreenAccent
import com.respira.ui.theme.SoftGreenContainer
import com.respira.ui.theme.SoftGreenPrimary

@Composable
fun OfflineStatusBar(
    isOffline: Boolean,
    downloadedCount: Int,
    totalCount: Int,
    onToggleOffline: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("offline_status_bar"),
        color = if (isOffline) Color(0xFF221F18) else Color(0xFF14241B),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isOffline) Color(0xFF6B5824) else Color(0xFF285437)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isOffline) SoftAmber.copy(alpha = 0.2f) else SoftGreenPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudDone,
                        contentDescription = if (isOffline) "Offline active" else "Online active",
                        tint = if (isOffline) SoftAmber else SoftGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isOffline) "Offline Mode Enabled" else "Online • Cloud Sync Active",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OffWhite
                        )
                    )
                    Text(
                        text = "$downloadedCount of $totalCount exercises saved offline",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MutedGrey,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isOffline) "Offline" else "Online",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isOffline) SoftAmber else SoftGreenAccent,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(end = 6.dp)
                )
                Switch(
                    checked = !isOffline,
                    onCheckedChange = { isOnline -> onToggleOffline(!isOnline) },
                    modifier = Modifier.testTag("offline_toggle_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SoftGreenPrimary,
                        checkedTrackColor = SoftGreenContainer,
                        uncheckedThumbColor = SoftAmber,
                        uncheckedTrackColor = Color(0xFF383120)
                    )
                )
            }
        }
    }
}

@Composable
fun StreakBadge(
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.testTag("streak_badge"),
        color = Color(0xFF2E2218),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6B4822))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Streak Flame",
                tint = Color(0xFFFF9E43),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$streakDays day streak",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD59E)
                )
            )
        }
    }
}

@Composable
fun LungProgressTrendChart(
    testRecords: List<LungTestRecord>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CharcoalCard)
            .border(1.dp, CharcoalBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("progress_trend_chart")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Lung Capacity Trend",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OffWhite
                    )
                )
                Text(
                    text = "Vital volume over recent assessments",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MutedGrey
                    )
                )
            }

            val latestImprovement = testRecords.firstOrNull()?.improvementPercent ?: 0f
            Surface(
                color = if (latestImprovement >= 0) SoftGreenPrimary.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (latestImprovement >= 0) "+${String.format("%.1f", latestImprovement)}% overall" else "${String.format("%.1f", latestImprovement)}%",
                    color = if (latestImprovement >= 0) SoftGreenAccent else Color(0xFFFF8B8B),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Custom Canvas Chart with smooth bezier curve & data point circles
        if (testRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1B2026)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MutedGrey,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "No Trend Data Yet",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OffWhite
                        )
                    )
                    Text(
                        text = "Take lung capacity tests to track progress over time",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MutedGrey,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        } else {
            val points = testRecords.reversed().map { it.capacityLiters }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                    val width = size.width
                    val height = size.height
                    val padY = 24.dp.toPx()
                    val minVal = (points.minOrNull() ?: 3.0f) - 0.5f
                    val maxVal = (points.maxOrNull() ?: 5.0f) + 0.5f

                    // Draw background subtle grid lines
                    for (i in 0..3) {
                        val y = padY + (height - 2 * padY) * (i / 3f)
                        drawLine(
                            color = Color(0xFF282D33),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    if (points.size >= 2) {
                        val stepX = width / (points.size - 1)
                        val coords = points.mapIndexed { index, value ->
                            val normalizedY = ((value - minVal) / (maxVal - minVal)).coerceIn(0f, 1f)
                            val x = index * stepX
                            val y = height - padY - (normalizedY * (height - 2 * padY))
                            Offset(x, y)
                        }

                        // Build filled gradient path
                        val fillPath = Path().apply {
                            moveTo(coords.first().x, height)
                            lineTo(coords.first().x, coords.first().y)
                            for (i in 1 until coords.size) {
                                val prev = coords[i - 1]
                                val curr = coords[i]
                                val midX = (prev.x + curr.x) / 2
                                cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                            }
                            lineTo(coords.last().x, height)
                            close()
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    SoftGreenPrimary.copy(alpha = 0.35f),
                                    SoftGreenPrimary.copy(alpha = 0.02f)
                                )
                            )
                        )

                        // Build line path
                        val linePath = Path().apply {
                            moveTo(coords.first().x, coords.first().y)
                            for (i in 1 until coords.size) {
                                val prev = coords[i - 1]
                                val curr = coords[i]
                                val midX = (prev.x + curr.x) / 2
                                cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                            }
                        }

                        drawPath(
                            path = linePath,
                            color = SoftGreenPrimary,
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Draw dots
                        coords.forEach { coord ->
                            drawCircle(
                                color = CharcoalCard,
                                radius = 6.dp.toPx(),
                                center = coord
                            )
                            drawCircle(
                                color = SoftGreenAccent,
                                radius = 4.dp.toPx(),
                                center = coord
                            )
                        }
                    } else if (points.size == 1) {
                        val x = width / 2f
                        val y = height / 2f
                        drawCircle(
                            color = CharcoalCard,
                            radius = 8.dp.toPx(),
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = SoftGreenAccent,
                            radius = 6.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Past tests",
                style = MaterialTheme.typography.bodySmall.copy(color = MutedGrey, fontSize = 11.sp)
            )
            Text(
                text = "Latest: ${testRecords.firstOrNull()?.let { "${it.capacityLiters} L" } ?: "--"}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SoftGreenAccent,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            )
        }
    }
}
