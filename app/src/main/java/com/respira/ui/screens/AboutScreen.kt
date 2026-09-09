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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.respira.BuildConfig
import com.respira.ui.components.RespiraLogoMark
import com.respira.ui.theme.CardSurface
import com.respira.ui.theme.CardSurfaceElevated
import com.respira.ui.theme.PrimaryEmerald
import com.respira.ui.theme.SageBorder
import com.respira.ui.theme.SecondarySage
import com.respira.ui.theme.SoftAmber
import com.respira.ui.theme.SurfaceBackground
import com.respira.ui.theme.TextPrimary
import com.respira.ui.theme.TextSecondary

/**
 * About Screen:
 * Written in simple, easy-to-understand conversational language
 * explaining how conscious breathwork helps, how to use the app,
 * privacy guarantees, and clearly displaying the App Version.
 */
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appVersionName = BuildConfig.VERSION_NAME.ifBlank { "1.0.0" }
    val appBuildNumber = "2026.09"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag("about_screen_content")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CardSurface)
                    .border(1.dp, SageBorder, CircleShape)
                    .testTag("about_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            RespiraLogoMark(size = 32.dp)

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Respira",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Branding & Version Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SageBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Simplified Minimalist Logo
                RespiraLogoMark(size = 72.dp)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Respira",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Your daily breathing & relaxation guide",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SecondarySage,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // App Version Badge
                Surface(
                    color = CardSurfaceElevated,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SageBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "App Version ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = "$appVersionName • Build $appBuildNumber",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 1: What is Respira? (Easy to understand)
        EasyAboutCard(
            title = "What is Respira?",
            icon = Icons.Default.SelfImprovement,
            content = "Respira is a simple wellness app that helps you slow down and breathe with intention. Taking a few moments for rhythmic breathing can quickly calm your nerves, improve your focus, and help you sleep more peacefully."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Section 2: How to use it
        EasyAboutCard(
            title = "How it Works",
            icon = Icons.Default.Air,
            content = "Just tap any exercise and follow the glowing circle on screen:\n\n• Inhale as the circle expands\n• Hold gently as it pauses\n• Exhale slowly as it contracts\n\nEven 2 to 3 minutes a day can reset your mood and refresh your mind."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Section 3: Works 100% Offline
        EasyAboutCard(
            title = "Works Anywhere Offline",
            icon = Icons.Default.CloudDone,
            content = "No internet connection is required. All guided sessions, lung assessments, and ambient sounds are saved directly on your phone. You can practice on flights, during commutes, or out in nature."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Section 4: Privacy
        EasyAboutCard(
            title = "Private & On-Device",
            icon = Icons.Default.Lock,
            content = "Your test results, session logs, and daily streaks stay 100% on your device. We do not track your activity, collect personal info, or share your data."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Friendly Health Note
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("wellness_disclaimer_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SoftAmber.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = SoftAmber,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "A Friendly Health Note",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftAmber
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Respira is designed for everyday relaxation, meditation, and wellness. It is not a medical device or a clinical spirometer. If you have asthma, respiratory conditions, or medical concerns, please speak with a doctor or healthcare provider.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun EasyAboutCard(
    title: String,
    icon: ImageVector,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SageBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CardSurfaceElevated)
                        .border(1.dp, SageBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            )
        }
    }
}
