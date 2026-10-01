package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = SakhiRosePrimary,
    onPrimary = Color.White,
    primaryContainer = SakhiRoseLight,
    onPrimaryContainer = SakhiRoseDark,
    secondary = SakhiMarigoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = SakhiMarigoldContainer,
    onSecondaryContainer = SakhiMarigoldDark,
    tertiary = SakhiTealTertiary,
    onTertiary = Color.White,
    tertiaryContainer = SakhiTealContainer,
    onTertiaryContainer = Color(0xFF134E4A),
    background = SakhiBackgroundWarm,
    onBackground = SakhiTextDark,
    surface = SakhiSurfaceCard,
    onSurface = SakhiTextDark,
    surfaceVariant = SakhiSurfaceVariantWarm,
    onSurfaceVariant = SakhiTextMuted
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB1C8),
    onPrimary = Color(0xFF5E1128),
    primaryContainer = Color(0xFF7A1D36),
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = Color(0xFFFFB951),
    onSecondary = Color(0xFF452B00),
    secondaryContainer = Color(0xFF633F00),
    onSecondaryContainer = Color(0xFFFFDDB3),
    tertiary = Color(0xFF4EDECA),
    onTertiary = Color(0xFF003733),
    background = Color(0xFF1F171A),
    onBackground = Color(0xFFECE0E3),
    surface = Color(0xFF261D21),
    onSurface = Color(0xFFECE0E3),
    surfaceVariant = Color(0xFF382C31),
    onSurfaceVariant = Color(0xFFD6C2C7)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted warm theme for consistent cultural accessibility
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
