package com.respira.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.respira.data.model.Exercise
import com.respira.ui.components.DayProgress
import com.respira.ui.components.LatestTestLungsCard
import com.respira.ui.components.LungCapacityProgressRingCard
import com.respira.ui.components.OfflineStatusBar
import com.respira.ui.components.WeeklyProgressBarChart
import com.respira.ui.theme.CardSurface
import com.respira.ui.theme.PrimaryEmerald
import com.respira.ui.theme.SageBorder
import com.respira.ui.theme.SecondarySage
import com.respira.ui.theme.SurfaceBackground
import com.respira.ui.theme.TextPrimary
import com.respira.ui.theme.TextSecondary
import com.respira.viewmodel.WellnessViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: WellnessViewModel,
    onNavigateToAbout: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onStartRecommendedExercise: (Exercise) -> Unit,
    onStartLungTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exercises by viewModel.allExercises.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()
    val latestTest by viewModel.latestLungTest.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val downloadedCount = exercises.count { it.isDownloaded }
    val totalExercises = exercises.size.coerceAtLeast(1)

    // Dynamic greeting based on current time
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Good night"
    }
    val greetingSubtitle = when (hour) {
        in 5..11 -> "Start your day with conscious breath"
        in 12..16 -> "Midday reset & calm focus"
        in 17..21 -> "Evening relaxation & wind down"
        else -> "Restful breathing & night relaxation"
    }

    // Active day name (e.g. Thu)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val todayLabel = dayLabels[dayOfWeek - 1]

    // Compute actual weekly minutes per day from completed sessions
    val dayMinutesMap = androidx.compose.runtime.remember(allSessions) {
        val map = mutableMapOf<String, Int>()
        val cal = Calendar.getInstance()
        val nowMs = System.currentTimeMillis()
        val sevenDaysAgoMs = nowMs - (7 * 24 * 60 * 60 * 1000L)

        allSessions.filter { it.completedTimestamp >= sevenDaysAgoMs }.forEach { session ->
            cal.timeInMillis = session.completedTimestamp
            val dayName = when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "Mon"
                Calendar.TUESDAY -> "Tue"
                Calendar.WEDNESDAY -> "Wed"
                Calendar.THURSDAY -> "Thu"
                Calendar.FRIDAY -> "Fri"
                Calendar.SATURDAY -> "Sat"
                else -> "Sun"
            }
            map[dayName] = (map[dayName] ?: 0) + (session.durationSeconds / 60)
        }
        map
    }

    val daysOrder = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val weeklyData = daysOrder.map { day ->
        DayProgress(
            dayLabel = day,
            minutes = dayMinutesMap[day] ?: 0,
            isHighlighted = todayLabel == day
        )
    }

    val hasTest = latestTest != null
    val currentCapacity = latestTest?.capacityLiters ?: 0.0f
    val capacityPercent = if (hasTest) ((currentCapacity / 5.0f) * 100).toInt().coerceIn(1, 100) else 0
    val statusText = if (hasTest) (if (capacityPercent >= 80) "Optimal" else "Good") else "No Data"
    val improvementText = if (hasTest) latestTest!!.note else "No test taken"
    val recommendedExercise = exercises.firstOrNull { it.id == "box_breathing" } ?: exercises.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("dashboard_activity_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = greetingSubtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = onNavigateToAbout,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CardSurface)
                    .border(1.dp, SageBorder, CircleShape)
                    .testTag("dashboard_about_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "About Respira",
                    tint = SecondarySage,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Top Section: Circular progress ring showing Lung Capacity percentage (86%)
        LungCapacityProgressRingCard(
            percentage = capacityPercent,
            capacityLiters = currentCapacity,
            statusText = statusText,
            improvementText = improvementText,
            hasData = hasTest
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Middle Section: "Weekly Progress" vertical bar chart with rounded tops on bars
        WeeklyProgressBarChart(days = weeklyData)

        Spacer(modifier = Modifier.height(16.dp))

        // Lower Section: "Latest Test" card with a glowing vector graphic of lungs
        LatestTestLungsCard(
            latestTest = latestTest,
            onRetakeTest = onStartLungTest
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Breathing Session CTA
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SageBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Quick Breathwork",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = recommendedExercise?.title ?: "Box Breathing 4-4-4-4",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SecondarySage,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Primary CTA Accent: Color(0xFF6EE7B7)
                Button(
                    onClick = {
                        if (recommendedExercise != null) {
                            onStartRecommendedExercise(recommendedExercise)
                        } else {
                            onNavigateToExercises()
                        }
                    },
                    modifier = Modifier.testTag("dashboard_quick_start_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryEmerald,
                        contentColor = Color(0xFF042013)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF042013),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Start",
                        color = Color(0xFF042013),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF042013)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Offline Status indicator
        OfflineStatusBar(
            isOffline = settings.offlineModeForced,
            downloadedCount = downloadedCount,
            totalCount = totalExercises,
            onToggleOffline = { isForced ->
                viewModel.updateSettings(settings.copy(offlineModeForced = isForced))
            }
        )

        Spacer(modifier = Modifier.height(28.dp))
    }
}
