package com.educonnectapp.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduTexto

// Alto de la barra inferior (BarraInferiorEdu: 74 dp + barra de navegación del sistema)
private val ALTO_BARRA_INFERIOR = 74.dp

// =====================================================================
// HOJA INFERIOR DE EDUCONNECT (rediseño 2026)
// Reemplaza a ModalBottomSheet: NO tapa la barra inferior. Sube justo encima de ella,
// oscurece solo la cabecera y el contenido, y la barra sigue visible y se puede usar.
// - Se cierra al tocar la zona oscura, con el botón Atrás o arrastrando la barrita hacia abajo.
// - Se usa igual que antes: if (mostrar) HojaInferiorEdu(onCerrar = { mostrar = false }) { ... }
//   (se dibuja por encima de la pantalla aunque se declare antes que ella, gracias a zIndex)
// =====================================================================

@Composable
fun HojaInferiorEdu(
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    // Animación de entrada al aparecer y de salida antes de avisar onCerrar
    val estado = remember { MutableTransitionState(false) }
    var cerrando by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { estado.targetState = true }
    LaunchedEffect(cerrando, estado.isIdle, estado.currentState) {
        if (cerrando && estado.isIdle && !estado.currentState) onCerrar()
    }
    val cerrar = {
        cerrando = true
        estado.targetState = false
    }

    var arrastre by remember { mutableFloatStateOf(0f) }
    val sinEfecto = remember { MutableInteractionSource() }
    val espacioBarra = ALTO_BARRA_INFERIOR + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    BackHandler { cerrar() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .zIndex(10f)
            .padding(bottom = espacioBarra)      // deja libre la barra inferior
    ) {
        val altoMaximo = maxHeight * 0.88f

        // VELO (oscurece la cabecera y el contenido; tocarlo cierra la hoja)
        AnimatedVisibility(visibleState = estado, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(EduTexto.copy(alpha = 0.45f))
                    .clickable(interactionSource = sinEfecto, indication = null, onClick = cerrar)
            )
        }

        // HOJA (sube desde la barra inferior)
        AnimatedVisibility(
            visibleState = estado,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            val forma = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = altoMaximo)
                    .graphicsLayer { translationY = arrastre }
                    .shadow(12.dp, forma)
                    .background(Color.White, forma)
                    // Un toque dentro de la hoja no debe cerrarla
                    .clickable(interactionSource = sinEfecto, indication = null) {}
                    .padding(bottom = 20.dp)
            ) {
                // BARRITA DE ARRASTRE (arrastrar hacia abajo cierra la hoja)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta -> arrastre = (arrastre + delta).coerceAtLeast(0f) },
                            onDragStopped = { if (arrastre > 120f) { cerrar() } else { arrastre = 0f } }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(5.dp)
                            .background(EduBorde, RoundedCornerShape(50))
                    )
                }
                contenido()
            }
        }
    }
}
