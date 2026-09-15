package com.respira.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.respira.R

/**
 * Official application logo mark for Respira.
 * Uses the custom application logo asset (img_app_logo) matching the app launcher icon.
 */
@Composable
fun RespiraLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Image(
        painter = painterResource(id = R.drawable.img_app_logo),
        contentDescription = "Respira Logo",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .testTag("respira_logo_mark")
    )
}
