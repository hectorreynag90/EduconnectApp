package com.educonnectapp.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme

private val EduConnectColorScheme = lightColorScheme(
    primary = EduconnectBlue,
    primaryContainer = EduconnectBlueMedium,
    secondaryContainer = EduconnectBlueLight,
    secondary = AccentOrange,
    tertiary = AccentOrangeLight,
    background = Background,
    surface = BackgroundWhite,
    surfaceVariant = BackgroundLight,
    onPrimary = TextWhite,
    onPrimaryContainer = TextWhite,
    onSecondary = TextWhite,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = StatusErrorRed,
    onError = TextWhite,
    outline = BorderLight,
    outlineVariant = BorderMedium,
    scrim = NavBarBackground,
)

@Composable
fun EduConnectAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EduConnectColorScheme,
        typography = Typography,
        content = content
    )
}