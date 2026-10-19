package com.educonnectapp.ui.screens.docente.publicaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.screens.docente.comunicados.SeccionDestinatario
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduAmarilloClaro
import com.educonnectapp.ui.theme.EduAmarilloOscuro
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.Nunito
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// =====================================================================
// AVISO DE EVALUACIÓN (rediseño 2026)
// El docente solo informa fecha y temario a los padres: sin adjunto ni registro de entregas.
// Cabecera fija amarilla · Para · evaluación · temario · fecha · "Publicar aviso"
// =====================================================================

@Composable
fun NuevaEvaluacionScreen(
    listaDestinatarios: List<SeccionDestinatario> = emptyList(),
    cursoNombre: String = "",          // ya no se usa (se conserva por compatibilidad)
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onPublicar: (
        titulo: String,
        descripcion: String,
        adjunto: String,              // siempre "" (las evaluaciones no llevan adjunto)
        seccionDestinatario: SeccionDestinatario,
        fechaEvaluacion: String,
        fechaPublicacion: String,
        hora: String
    ) -> Unit = { _, _, _, _, _, _, _ -> },
    // ----- Rediseño 2026 -----
    destinatarioInicial: SeccionDestinatario? = null,
    publicando: Boolean = false
) {
    var destinatario by remember { mutableStateOf(destinatarioInicial ?: listaDestinatarios.singleOrNull()) }
    var titulo by remember { mutableStateOf("") }
    var temario by remember { mutableStateOf("") }
    var fechaEvaluacion by remember { mutableStateOf("") }
    var mostrarClases by remember { mutableStateOf(false) }
    var mostrarConfirmar by remember { mutableStateOf(false) }

    LaunchedEffect(destinatarioInicial) {
        if (destinatario == null && destinatarioInicial != null) destinatario = destinatarioInicial
    }

    val completo = destinatario != null && titulo.isNotBlank() && temario.isNotBlank() && fechaEvaluacion.isNotBlank()

    if (mostrarConfirmar) {
        val dest = destinatario
        PopupConfirmacionEdu(
            titulo = "¿Publicar aviso?",
            mensaje = "Los padres recibirán una notificación al instante.",
            textoConfirmar = "Publicar",
            colorConfirmar = TipoBotonEdu.AVANZAR.fondo,
            icono = Icons.AutoMirrored.Rounded.Send,
            onCancelar = { mostrarConfirmar = false },
            onConfirmar = {
                mostrarConfirmar = false
                if (dest != null && completo) {
                    val fechaPub = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val hora = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    onPublicar(titulo.trim(), temario.trim(), "", dest, fechaEvaluacion, fechaPub, hora)
                }
            }
        ) {
            if (dest != null) {
                ResumenPublicacion(
                    titulo = titulo.trim(),
                    clase = "${dest.gradoNombre} ${dest.seccionNombre} · ${dest.cursoNombre}",
                    fecha = "Evaluación: ${fechaLargaPub(fechaEvaluacion)}",
                    alumnos = dest.totalPadres,
                    conAdjunto = false
                )
            }
        }
    }

    if (publicando) PopupProcesandoEdu(titulo = "Publicando aviso...")

    if (mostrarClases) {
        HojaElegirClasePub(
            clases = listaDestinatarios,
            seleccionada = destinatario,
            onElegir = { destinatario = it; mostrarClases = false },
            onCerrar = { mostrarClases = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Aviso de evaluación",
            subtitulo = "Fecha y temario para los padres",
            onVolver = onBack,
            colorFondo = ColorModulo.Publicaciones,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Publicaciones
                )
            }
        )

        // FORMULARIO DESPLAZABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CampoParaPub(destino = destinatario, onClick = { mostrarClases = true })
            CampoTextoPub(
                valor = titulo, onValorCambia = { titulo = it },
                etiqueta = "Evaluación", placeholder = "Ej: Práctica calificada N° 2"
            )
            CampoTextoPub(
                valor = temario, onValorCambia = { temario = it },
                etiqueta = "Temario", placeholder = "Temas que entran y materiales que debe traer...", lineas = 4
            )
            CampoFechaPub(etiqueta = "Fecha de la evaluación", fecha = fechaEvaluacion, onFecha = { fechaEvaluacion = it })

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EduAmarilloClaro, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = EduAmarilloOscuro, modifier = Modifier.size(22.dp))
                Text(
                    "Es solo un aviso: no lleva adjunto ni registro de entregas.",
                    modifier = Modifier.weight(1f),
                    fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                    lineHeight = 18.sp, color = EduAmarilloOscuro
                )
            }
        }

        // BOTÓN FIJO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            BotonPrincipalEdu(
                texto = "Publicar aviso",
                tipo = TipoBotonEdu.AVANZAR,
                icono = Icons.AutoMirrored.Rounded.Send,
                habilitado = completo && !publicando,
                onClick = { if (completo) mostrarConfirmar = true }
            )
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Inicio"
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun NuevaEvaluacionPreview() {
    EduConnectAppTheme {
        NuevaEvaluacionScreen(
            listaDestinatarios = listOf(
                SeccionDestinatario(1L, 1L, 1L, "1er Grado", "A", "Ciencia y Tecnología", 5),
                SeccionDestinatario(2L, 2L, 1L, "2do Grado", "B", "Matemáticas", 24)
            )
        )
    }
}
