package com.respira.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.respira.R
import com.respira.ui.theme.CardSurface
import com.respira.ui.theme.SageBorder
import com.respira.ui.theme.SecondarySage
import com.respira.ui.theme.TextPrimary

/**
 * Official application logo mark for Respira.
 * Uses the exact vector resource (ic_respira_logo) matching the app launcher icon.
 */
@Composable
fun RespiraLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    showBackground: Boolean = false,
    backgroundColor: Color = CardSurface,
    borderColor: Color = SageBorder
) {
    Image(
        painter = painterResource(id = R.drawable.ic_respira_logo),
        contentDescription = "Respira Logo",
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .testTag("respira_logo_mark")
    )
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
