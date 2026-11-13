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
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.AdjuntoLocal
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.ListaAdjuntosResumenEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.SelectorAdjuntosEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.docente.comunicados.SeccionDestinatario
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// =====================================================================
// NUEVA TAREA (rediseño 2026)
// Cabecera fija amarilla · Para · título · descripción · fecha límite · adjuntos (hasta 5)
// "Publicar tarea" → confirmación → "Publicando..." (mientras MainActivity publica)
// =====================================================================

@Composable
fun NuevaTareaScreen(
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
        adjuntos: List<String>,          // Uri (en texto) de cada archivo elegido, en orden
        seccionDestinatario: SeccionDestinatario,
        fechaEntrega: String,
        fechaPublicacion: String,
        hora: String
    ) -> Unit = { _, _, _, _, _, _, _ -> },
    // ----- Rediseño 2026 -----
    destinatarioInicial: SeccionDestinatario? = null,   // clase ya elegida
    publicando: Boolean = false                         // muestra "Publicando..."
) {
    var destinatario by remember { mutableStateOf(destinatarioInicial ?: listaDestinatarios.singleOrNull()) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaEntrega by remember { mutableStateOf("") }
    // Adjuntos elegidos (hasta 5 de 10 MB cada uno)
    var adjuntos by remember { mutableStateOf<List<AdjuntoLocal>>(emptyList()) }
    var mostrarClases by remember { mutableStateOf(false) }
    var mostrarConfirmar by remember { mutableStateOf(false) }

    // Si la clase inicial llega después (la lista se carga al entrar), se toma
    LaunchedEffect(destinatarioInicial) {
        if (destinatario == null && destinatarioInicial != null) destinatario = destinatarioInicial
    }

    val completo = destinatario != null && titulo.isNotBlank() && descripcion.isNotBlank() && fechaEntrega.isNotBlank()

    // ── POPUPS ──────────────────────────────────────────────────────────────
    if (mostrarConfirmar) {
        val dest = destinatario
        PopupConfirmacionEdu(
            titulo = "¿Publicar tarea?",
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
                    onPublicar(titulo.trim(), descripcion.trim(), adjuntos.map { it.uri }, dest, fechaEntrega, fechaPub, hora)
                }
            }
        ) {
            if (dest != null) {
                ResumenPublicacion(
                    titulo = titulo.trim(),
                    clase = "${dest.gradoNombre} ${dest.seccionNombre} · ${dest.cursoNombre}",
                    fecha = "Entrega: ${fechaLargaPub(fechaEntrega)}",
                    alumnos = dest.totalPadres,
                    conAdjunto = adjuntos.isNotEmpty(),
                    adjuntos = adjuntos.map { it.nombre }
                )
            }
        }
    }

    if (publicando) {
        PopupProcesandoEdu(
            titulo = when (adjuntos.size) {
                0 -> "Publicando tarea..."
                1 -> "Subiendo adjunto y publicando..."
                else -> "Subiendo ${adjuntos.size} adjuntos y publicando..."
            }
        )
    }

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
            titulo = "Nueva tarea",
            subtitulo = "Los padres reciben un aviso al publicar",
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
                etiqueta = "Título", placeholder = "Ej: Ejercicios cap. 5 · Fracciones"
            )
            CampoTextoPub(
                valor = descripcion, onValorCambia = { descripcion = it },
                etiqueta = "Descripción", placeholder = "Qué deben hacer y cómo entregarlo...", lineas = 3
            )
            CampoFechaPub(etiqueta = "Fecha límite", fecha = fechaEntrega, onFecha = { fechaEntrega = it })

            // ADJUNTOS (varios, con el color del módulo Publicaciones)
            SelectorAdjuntosEdu(
                adjuntos = adjuntos,
                onCambio = { adjuntos = it },
                tono = TonoEdu.AMARILLO,
                habilitado = !publicando
            )
        }

        // BOTÓN FIJO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            BotonPrincipalEdu(
                texto = "Publicar tarea",
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

// Resumen dentro de la confirmación (título, clase, fecha y chips)
@Composable
internal fun ResumenPublicacion(
    titulo: String,
    clase: String,
    fecha: String,
    alumnos: Int,
    conAdjunto: Boolean,
    adjuntos: List<String> = emptyList()      // nombres (se muestran en amarillo oscuro)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(EduFondo, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTexto, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(clase, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduTextoSecundario)
        Text(fecha, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduEtiqueta)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChipEdu("$alumnos alumnos", TonoEdu.VERDE)
            when {
                adjuntos.size > 1 -> ChipEdu("${adjuntos.size} adjuntos", TonoEdu.AMARILLO)
                conAdjunto -> ChipEdu("1 adjunto", TonoEdu.AMARILLO)
            }
        }
        if (adjuntos.isNotEmpty()) ListaAdjuntosResumenEdu(adjuntos, TonoEdu.AMARILLO, Modifier.padding(top = 2.dp))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun NuevaTareaPreview() {
    EduConnectAppTheme {
        NuevaTareaScreen(
            listaDestinatarios = listOf(
                SeccionDestinatario(1L, 1L, 1L, "1er Grado", "A", "Ciencia y Tecnología", 5),
                SeccionDestinatario(2L, 2L, 1L, "2do Grado", "B", "Matemáticas", 24)
            ),
            destinatarioInicial = SeccionDestinatario(1L, 1L, 1L, "1er Grado", "A", "Ciencia y Tecnología", 5)
        )
    }
}
