package com.yuricunha.yumusic.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val YuMusicColorScheme = darkColorScheme(
    primary = PrimaryAccent,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryAccentHover,
    onPrimaryContainer = TextPrimary,
    secondary = AccentReadableOnDark,
    onSecondary = Background,
    secondaryContainer = ActiveSelected,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentReadableOnDark,
    onTertiary = Background,
    tertiaryContainer = ActiveSelected,
    onTertiaryContainer = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = Background,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundElevated,
    onSurfaceVariant = TextSecondary,
    surfaceTint = PrimaryAccent,
    surfaceContainer = BackgroundElevated,
    surfaceContainerLow = Background,
    surfaceContainerHigh = SurfaceCard,
    surfaceContainerHighest = HoverRipple,
    outline = Divider,
    outlineVariant = BorderCard,
    error = PrimaryAccent,
    onError = TextPrimary,
)

@Composable
fun YuMusicTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = YuMusicColorScheme,
        typography = YuMusicTypography,
        content = content,
    )
}
