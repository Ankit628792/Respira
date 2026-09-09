package com.respira.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.respira.viewmodel.SessionSummary

@Composable
fun SessionSummaryDialog(
    summary: SessionSummary,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationMinutes = summary.durationSeconds / 60
    val durationSecondsRemainder = summary.durationSeconds % 60
    val durationFormatted = if (durationMinutes > 0) {
        "$durationMinutes min ${durationSecondsRemainder}s"
    } else {
        "${summary.durationSeconds} seconds"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("summary_done_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftGreenPrimary,
                    contentColor = Color(0xFF042013)
                )
            ) {
                Text(
                    text = "Complete & Return to Home",
                    color = Color(0xFF042013),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF042013)
                    )
                )
            }
        },
        containerColor = CharcoalCard,
        modifier = modifier
            .border(1.dp, SoftGreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
            .testTag("session_summary_dialog"),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Celebration Icon
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(SoftGreenPrimary.copy(alpha = 0.18f))
                        .border(2.dp, SoftGreenPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = SoftGreenAccent,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Session Completed!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OffWhite
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = summary.exerciseTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = SoftGreenLight,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Encouraging message
                Surface(
                    color = SoftGreenContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftGreenPrimary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "“${summary.message}”",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SoftGreenUltraLight,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stat Cards: Duration & Streak Update
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Duration card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = CharcoalCardElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = SoftGreenAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Duration",
                                style = MaterialTheme.typography.labelSmall.copy(color = MutedGrey)
                            )
                            Text(
                                text = durationFormatted,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OffWhite
                                )
                            )
                        }
                    }

                    // Streak card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = CharcoalCardElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFFF9E43),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Streak Updated",
                                style = MaterialTheme.typography.labelSmall.copy(color = MutedGrey)
                            )
                            Text(
                                text = "${summary.streakDayCount} Days",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD59E)
                                )
                            )
                        }
                    }
                }
            }
        }
    )
}
