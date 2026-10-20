package com.educonnectapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema de Material 3 con la paleta 2026 (Color.kt).
// Lo usan los componentes que toman el color del tema (selector de fecha, interruptores,
// indicadores de carga por defecto, selección de texto...).
private val EduConnectColorScheme = lightColorScheme(
    primary = EduTeal,
    onPrimary = Color.White,
    primaryContainer = EduTealClaro,
    onPrimaryContainer = EduTealOscuro,
    secondary = EduAmarillo,
    onSecondary = EduTexto,
    secondaryContainer = EduAmarilloClaro,
    onSecondaryContainer = EduAmarilloOscuro,
    tertiary = EduAzulOscuro,
    onTertiary = Color.White,
    tertiaryContainer = EduAzulOscuroClaro,
    onTertiaryContainer = EduAzulOscuro,
    background = EduFondo,
    onBackground = EduTexto,
    surface = Color.White,
    onSurface = EduTexto,
    surfaceVariant = EduCampo,
    onSurfaceVariant = EduTextoSecundario,
    error = EduCoral,
    onError = Color.White,
    errorContainer = EduCoralClaro,
    onErrorContainer = EduCoralOscuro,
    outline = EduBorde,
    outlineVariant = EduBorde,
    scrim = Color.Black,
)

@Composable
fun EduConnectAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EduConnectColorScheme,
        typography = Typography,
        content = content
    )
}
