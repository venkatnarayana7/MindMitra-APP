package com.mindmitra.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MindMitraDarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = TextPrimary,
    primaryContainer = CardSurface,
    onPrimaryContainer = TextPrimary,
    secondary = LightPurple,
    onSecondary = TextPrimary,
    secondaryContainer = NavySurface,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentLavender,
    onTertiary = TextPrimary,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = CardSurface,
    onSurfaceVariant = TextSecondary,
    outline = TextHint,
    error = PinkAccent,
    onError = TextPrimary,
)

@Composable
fun MindMitraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MindMitraDarkColorScheme,
        typography = MindMitraTypography,
        content = content
    )
}
