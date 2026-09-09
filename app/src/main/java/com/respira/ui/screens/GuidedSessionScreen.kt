package com.respira.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.respira.ui.components.BreathingTrainerVisualizer
import com.respira.ui.theme.CardSurface
import com.respira.ui.theme.CardSurfaceElevated
import com.respira.ui.theme.PrimaryEmerald
import com.respira.ui.theme.SageBorder
import com.respira.ui.theme.SecondarySage
import com.respira.ui.theme.SurfaceBackground
import com.respira.ui.theme.TextPrimary
import com.respira.ui.theme.TextSecondary
import com.respira.viewmodel.WellnessViewModel
import java.util.Locale

@Composable
fun GuidedSessionScreen(
    exercise: Exercise,
    viewModel: WellnessViewModel,
    modifier: Modifier = Modifier
) {
    val currentPhase by viewModel.currentPhase.collectAsState()
    val remainingSecs by viewModel.phaseRemainingSeconds.collectAsState()
    val phaseDuration by viewModel.phaseDurationSeconds.collectAsState()
    val elapsedSessionSecs by viewModel.elapsedSessionSeconds.collectAsState()
    val targetSessionSecs by viewModel.targetSessionSeconds.collectAsState()
    val isPaused by viewModel.isSessionPaused.collectAsState()
    val isAudioEnabled by viewModel.isAmbientSoundEnabled.collectAsState()
    val allExercises by viewModel.allExercises.collectAsState()

    var showExitDialog by remember { mutableStateOf(false) }
    var exerciseDropdownExpanded by remember { mutableStateOf(false) }

    val cycleSeconds = (exercise.inhaleSeconds + exercise.holdAfterInhaleSeconds + exercise.exhaleSeconds + exercise.holdAfterExhaleSeconds).coerceAtLeast(4)
    val targetCycles = (targetSessionSecs / cycleSeconds).coerceAtLeast(1)
    val currentCycle = ((elapsedSessionSecs / cycleSeconds) + 1).coerceAtMost(targetCycles)

    val sessionProgress = (elapsedSessionSecs.toFloat() / targetSessionSecs.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    val patternDescription = "${exercise.inhaleSeconds}-${exercise.holdAfterInhaleSeconds}-${exercise.exhaleSeconds}-${exercise.holdAfterExhaleSeconds}"
    val cadenceLabel = "${exercise.durationMinutes} min • ${exercise.difficulty}"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .statusBarsPadding()
            .testTag("guided_session_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Exit button, cycle counter pill, audio toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                        .border(1.dp, SageBorder, CircleShape)
                        .testTag("session_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Session",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Cycle progress badge
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SageBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cycle ",
                            style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                        )
                        Text(
                            text = "$currentCycle",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald
                            )
                        )
                        Text(
                            text = " / $targetCycles",
                            style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.toggleAmbientSound() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                        .border(1.dp, SageBorder, CircleShape)
                        .testTag("session_audio_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isAudioEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = if (isAudioEnabled) "Mute Audio" else "Unmute Audio",
                        tint = if (isAudioEnabled) PrimaryEmerald else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Center Body: Central circular timer + Exercise selector dropdown
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Large central animated circular timer with moving node along the perimeter
                BreathingTrainerVisualizer(
                    phase = currentPhase,
                    secondsRemaining = remainingSecs,
                    phaseTotalSeconds = phaseDuration,
                    isPaused = isPaused
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Exercise Selector Dropdown directly below circular timer
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { exerciseDropdownExpanded = true }
                            .testTag("exercise_selector_dropdown_button"),
                        color = CardSurface,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SageBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text(
                                    text = exercise.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "$patternDescription • $cadenceLabel",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SecondarySage,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Exercise",
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Dropdown menu to switch routines
                    DropdownMenu(
                        expanded = exerciseDropdownExpanded,
                        onDismissRequest = { exerciseDropdownExpanded = false },
                        modifier = Modifier
                            .background(CardSurface)
                            .border(1.dp, SageBorder, RoundedCornerShape(16.dp))
                    ) {
                        allExercises.forEach { candidate ->
                            val candidatePattern = "${candidate.inhaleSeconds}-${candidate.holdAfterInhaleSeconds}-${candidate.exhaleSeconds}-${candidate.holdAfterExhaleSeconds}"
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = candidate.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = if (candidate.id == exercise.id) FontWeight.Bold else FontWeight.Medium,
                                                color = if (candidate.id == exercise.id) PrimaryEmerald else TextPrimary
                                            )
                                        )
                                        Text(
                                            text = "$candidatePattern • ${candidate.category}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                        )
                                    }
                                },
                                onClick = {
                                    exerciseDropdownExpanded = false
                                    if (candidate.id != exercise.id) {
                                        viewModel.startExercise(candidate)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Bottom Area: Progress bar & Dark pill-shaped control button ("Pause Session" / "Start Session")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress Bar: PrimaryEmerald fill
                LinearProgressIndicator(
                    progress = { sessionProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(CircleShape)
                        .testTag("session_progress_bar"),
                    color = PrimaryEmerald,
                    trackColor = Color(0xFF16221B),
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Dark pill-shaped control button at the bottom ("Pause Session" / "Start Session")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dark pill-shaped control button
                    Surface(
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(28.dp))
                            .clickable { viewModel.togglePauseSession() }
                            .testTag("pill_session_control_button"),
                        color = CardSurfaceElevated,
                        shape = RoundedCornerShape(28.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isPaused) PrimaryEmerald else SageBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (isPaused) "Start Session" else "Pause Session",
                                tint = if (isPaused) PrimaryEmerald else TextPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isPaused) "Start Session" else "Pause Session",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPaused) PrimaryEmerald else TextPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Finish early icon button
                    Surface(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .clickable { viewModel.finishExercise(completed = true) }
                            .testTag("finish_session_pill_button"),
                        color = CardSurface,
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SageBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "End",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SecondarySage
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog before exiting session early
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "End Breathing Session?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Text(
                    text = "You have completed ${String.format(Locale.US, "%d:%02d", elapsedSessionSecs / 60, elapsedSessionSecs % 60)} of breathwork. Would you like to save and finish now?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        viewModel.finishExercise(completed = elapsedSessionSecs > 10)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryEmerald,
                        contentColor = Color(0xFF062317)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save & Finish", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showExitDialog = false
                        viewModel.exitSessionEarly()
                    },
                    border = androidx.compose.foundation.BorderStroke(1.dp, SageBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Discard", color = TextSecondary)
                }
            },
            containerColor = CardSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
