package com.respira.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Sage & Soft Emerald ColorScheme for Premium Eye-Friendly Dark Mode
 * Eliminates high-contrast eye strain by avoiding harsh pure black (#000000)
 * and pure bright white (#FFFFFF).
 */
val SageSoftEmeraldColorScheme = darkColorScheme(
    primary = PrimaryEmerald,                  // Color(0xFF6EE7B7)
    onPrimary = Color(0xFF062317),
    primaryContainer = EmeraldContainer,       // Color(0xFF142B20)
    onPrimaryContainer = OnEmeraldContainer,   // Color(0xFFD1FAE5)

    secondary = SecondarySage,                 // Color(0xFF4ADE80)
    onSecondary = Color(0xFF062812),
    secondaryContainer = CardSurfaceElevated,  // Color(0xFF18201C)
    onSecondaryContainer = TextPrimary,        // Color(0xFFE2E8F0)

    tertiary = SecondarySage,
    onTertiary = Color(0xFF062317),
    tertiaryContainer = Color(0xFF162E22),
    onTertiaryContainer = Color(0xFFA7F3D0),

    background = SurfaceBackground,            // Color(0xFF0B0F0D)
    onBackground = TextPrimary,                // Color(0xFFE2E8F0)

    surface = CardSurface,                     // Color(0xFF121815)
    onSurface = TextPrimary,                   // Color(0xFFE2E8F0)
    surfaceVariant = CardSurfaceElevated,      // Color(0xFF18201C)
    onSurfaceVariant = TextSecondary,          // Color(0xFF8FA396)

    outline = SageBorder,                      // Color(0xFF1E2822)
    outlineVariant = SageDivider,              // Color(0xFF151D18)
    scrim = Color(0x99000000)
)

val SageShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),        // Subtle 16dp rounded corners for container surfaces
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = SageSoftEmeraldColorScheme,
        typography = Typography,
        shapes = SageShapes,
        content = content
    )
}
