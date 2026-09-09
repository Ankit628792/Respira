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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.respira.ui.theme.CharcoalBackground
import com.respira.ui.theme.CharcoalBorder
import com.respira.ui.theme.CharcoalCard
import com.respira.ui.theme.CharcoalCardElevated
import com.respira.ui.theme.LightGrey
import com.respira.ui.theme.MutedGrey
import com.respira.ui.theme.OffWhite
import com.respira.ui.theme.SoftGreenAccent
import com.respira.ui.theme.SoftGreenContainer
import com.respira.ui.theme.SoftGreenLight
import com.respira.ui.theme.SoftGreenPrimary
import com.respira.ui.theme.SoftGreenUltraLight
import com.respira.ui.theme.SubtleGrey
import com.respira.viewmodel.LungTestStage
import com.respira.viewmodel.WellnessViewModel

@Composable
fun LungCapacityTestScreen(
    viewModel: WellnessViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stage by viewModel.lungTestStage.collectAsState()
    val holdSeconds by viewModel.lungTestHoldSeconds.collectAsState()
    val latestSavedRecord by viewModel.latestCreatedTestRecord.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("lung_capacity_test_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.dismissLungTest()
                        onDismiss()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CharcoalCardElevated)
                        .border(1.dp, CharcoalBorder, CircleShape)
                        .testTag("exit_lung_test_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = LightGrey,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Lung Capacity Test",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OffWhite
                    )
                )

                Spacer(modifier = Modifier.size(44.dp))
            }

            // Center Content based on Stage
            when (stage) {
                LungTestStage.READY -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(SoftGreenPrimary.copy(alpha = 0.15f))
                                .border(2.dp, SoftGreenPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = null,
                                tint = SoftGreenAccent,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Breath Hold & Vital Volume Assessment",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OffWhite
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "This test measures your comfortable breath retention and estimates vital lung capacity. Sit upright with relaxed posture, take a full deep inhalation, and hold comfortably.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = LightGrey.copy(alpha = 0.8f),
                                lineHeight = 22.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Surface(
                            color = CharcoalCardElevated,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
                        ) {
                            Text(
                                text = "Note: Do not strain beyond comfort. Intended for wellness tracking.",
                                color = MutedGrey,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.proceedToDeepInhale() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("start_lung_test_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftGreenPrimary,
                            contentColor = Color(0xFF072115)
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Begin Deep Inhale",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                LungTestStage.DEEP_INHALE -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .clip(CircleShape)
                                .background(SoftGreenPrimary.copy(alpha = 0.2f))
                                .border(3.dp, SoftGreenAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Air,
                                    contentDescription = null,
                                    tint = SoftGreenLight,
                                    modifier = Modifier.size(52.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Inhale",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = OffWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "Fill Your Lungs Completely",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OffWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Expand your diaphragm, ribcage, and upper chest. When your lungs are completely filled, tap 'Start Breath Hold'.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = LightGrey,
                                lineHeight = 20.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = { viewModel.startHoldingBreath() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("start_hold_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftGreenPrimary,
                            contentColor = Color(0xFF072115)
                        )
                    ) {
                        Text(
                            text = "Start Breath Hold",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                LungTestStage.HOLDING -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { (holdSeconds % 60) / 60f },
                                modifier = Modifier.fillMaxSize(),
                                color = SoftGreenPrimary,
                                trackColor = CharcoalCardElevated,
                                strokeWidth = 8.dp
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$holdSeconds",
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = SoftGreenAccent,
                                        fontSize = 58.sp
                                    )
                                )
                                Text(
                                    text = "seconds hold",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MutedGrey,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "Holding Breath Softly",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = OffWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Keep neck and face relaxed. As soon as you feel the first natural urge to breathe, tap 'Exhale & Finish'.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = LightGrey.copy(alpha = 0.8f),
                                lineHeight = 20.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = { viewModel.completeLungTestHold() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("complete_hold_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE57373),
                            contentColor = Color(0xFF2B0A0A)
                        )
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Exhale & Finish",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                LungTestStage.EXHALING, LungTestStage.RESULT -> {
                    val record = latestSavedRecord
                    val capacity = record?.capacityLiters ?: 4.3f
                    val improvement = record?.improvementPercent ?: 4.8f
                    val holdTime = record?.holdTimeSeconds ?: holdSeconds

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(SoftGreenPrimary.copy(alpha = 0.2f))
                                .border(2.dp, SoftGreenAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SoftGreenAccent,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Test Completed!",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OffWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Your results have been recorded in your wellness history.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MutedGrey),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Results Metrics Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = CharcoalCardElevated),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Vital Capacity", style = MaterialTheme.typography.labelSmall.copy(color = MutedGrey))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${String.format("%.1f", capacity)} L",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SoftGreenAccent
                                        )
                                    )
                                    Text("Estimated volume", style = MaterialTheme.typography.bodySmall.copy(color = SubtleGrey, fontSize = 10.sp))
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = CharcoalCardElevated),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Breath Hold", style = MaterialTheme.typography.labelSmall.copy(color = MutedGrey))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${holdTime}s",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OffWhite
                                        )
                                    )
                                    Text("Duration", style = MaterialTheme.typography.bodySmall.copy(color = SubtleGrey, fontSize = 10.sp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Comparison badge
                        Surface(
                            color = SoftGreenContainer,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SoftGreenPrimary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = SoftGreenUltraLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (improvement >= 0) "+${String.format("%.1f", improvement)}% improvement vs previous test" else "${String.format("%.1f", improvement)}% vs previous test",
                                    color = SoftGreenUltraLight,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.dismissLungTest()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("save_and_return_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftGreenPrimary,
                            contentColor = Color(0xFF072115)
                        )
                    ) {
                        Text(
                            text = "Save & Return to Dashboard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
