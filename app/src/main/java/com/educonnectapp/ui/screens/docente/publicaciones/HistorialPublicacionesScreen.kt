package com.educonnectapp.ui.screens.docente.publicaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BarraProgresoEdu
import com.educonnectapp.ui.components.BotonCrearEdu
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

data class PublicacionHistorialItem(
    val id: Long,
    val tipo: String,              // "Tarea" | "Evaluación"
    val titulo: String,
    val cursoNombre: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val fechaEntrega: String,      // "yyyy-MM-dd" (puede venir vacía)
    val fechaPublicacion: String,  // "yyyy-MM-dd"
    val totalAlumnos: Int,
    val calificados: Int,
    val lecturas: Int,
    val estado: String = "ACTIVA"  // "ACTIVA" | "CERRADA" | "ANULADA"
)

// =====================================================================
// PUBLICACIONES DE UNA CLASE (rediseño 2026)
// Cabecera fija amarilla · pestañas Todas / Tareas / Evaluaciones · tarjetas con entregas y lecturas
// =====================================================================

@Composable
fun HistorialPublicacionesScreen(
    publicaciones: List<PublicacionHistorialItem> = emptyList(),
    cargando: Boolean = false,
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerDetalle: (PublicacionHistorialItem) -> Unit = {},
    // ----- Rediseño 2026 -----
    titulo: String = "Publicaciones",      // el curso de la clase
    subtitulo: String = "",                // "Publicaciones · 1er Grado A"
    onNuevaTarea: () -> Unit = {},
    onNuevaEvaluacion: () -> Unit = {}
) {
    var tabSeleccionado by remember { mutableStateOf("Todas") }
    var mostrarCrear by remember { mutableStateOf(false) }

    val tareas = publicaciones.filter { it.tipo != "Evaluación" }
    val evaluaciones = publicaciones.filter { it.tipo == "Evaluación" }
    val lista = when (tabSeleccionado) {
        "Tareas" -> tareas
        "Evaluaciones" -> evaluaciones
        else -> publicaciones
    }

    if (mostrarCrear) {
        HojaCrearPublicacion(onTarea = onNuevaTarea, onEvaluacion = onNuevaEvaluacion, onCerrar = { mostrarCrear = false })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = titulo,
            subtitulo = subtitulo.ifBlank { null },
            onVolver = onBack,
            colorFondo = ColorModulo.Publicaciones,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Publicaciones
                )
            },
            contenidoInferior = {
                ChipCabeceraEdu(
                    if (publicaciones.size == 1) "1 publicación" else "${publicaciones.size} publicaciones",
                    modifier = Modifier.padding(top = 14.dp),
                    colorCabecera = ColorModulo.Publicaciones
                )
            }
        )

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),   // espacio para "+ Crear"
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // PESTAÑAS
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PestanaPub("Todas (${publicaciones.size})", tabSeleccionado == "Todas") { tabSeleccionado = "Todas" }
                    PestanaPub("Tareas (${tareas.size})", tabSeleccionado == "Tareas") { tabSeleccionado = "Tareas" }
                    PestanaPub("Evaluaciones (${evaluaciones.size})", tabSeleccionado == "Evaluaciones") { tabSeleccionado = "Evaluaciones" }
                }

                when {
                    cargando && publicaciones.isEmpty() -> TarjetaEdu {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = EduTeal, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                            Text(
                                "Cargando publicaciones...", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                                fontSize = 15.sp, color = EduTextoSecundario
                            )
                        }
                    }
                    lista.isEmpty() -> TarjetaEdu {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconoCirculoEdu(Icons.Rounded.SearchOff, TonoEdu.GRIS, tamano = 56.dp)
                            Text(
                                if (publicaciones.isEmpty()) "Aún no publicaste tareas ni evaluaciones en esta clase."
                                else "No hay publicaciones de este tipo.",
                                fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp,
                                color = EduTextoSecundario, textAlign = TextAlign.Center
                            )
                        }
                    }
                    else -> lista.forEach { item ->
                        PublicacionHistorialCard(item = item, onClick = { onVerDetalle(item) })
                    }
                }
            }

            BotonCrearEdu(
                onClick = { mostrarCrear = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
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

// Pestaña tipo pastilla: amarilla (texto oscuro) cuando está activa
@Composable
private fun PestanaPub(texto: String, activa: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(50)
    Text(
        texto,
        modifier = Modifier
            .clip(forma)
            .background(if (activa) EduAmarillo else Color.White)
            .border(1.5.dp, if (activa) EduAmarillo else EduBorde, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 7.dp),
        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.5.sp,
        color = if (activa) EduTexto else EduEtiqueta, maxLines = 1
    )
}

// Tarjeta de una publicación: tipo, título, fecha y estado; entregas (tareas) y lecturas
@Composable
fun PublicacionHistorialCard(item: PublicacionHistorialItem, onClick: () -> Unit) {
    val esEvaluacion = item.tipo == "Evaluación"
    val hoy = hoyPub()
    val progreso = if (item.totalAlumnos > 0) item.calificados.toFloat() / item.totalAlumnos else 0f

    TarjetaEdu(onClick = onClick, relleno = PaddingValues(horizontal = 14.dp, vertical = 13.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconoCirculoEdu(
                if (esEvaluacion) Icons.Rounded.Quiz else Icons.AutoMirrored.Rounded.Assignment,
                if (esEvaluacion) TonoEdu.AMARILLO else TonoEdu.AZUL
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.5.sp,
                    color = if (item.estado == "ANULADA") EduTextoSecundario else EduTexto,
                    maxLines = 2, overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        (if (esEvaluacion) "Evaluación: " else "Entrega: ") +
                                (if (item.fechaEntrega.isNotEmpty()) fechaCortaPub(item.fechaEntrega) else "—"),
                        fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
                    )
                    when {
                        item.estado == "ANULADA" -> ChipEdu("Anulada", TonoEdu.CORAL)
                        item.estado == "CERRADA" -> ChipEdu("Cerrada", TonoEdu.GRIS)
                        item.fechaEntrega == hoy -> ChipEdu(if (esEvaluacion) "Es hoy" else "Vence hoy", TonoEdu.CORAL)
                        item.fechaEntrega.isNotEmpty() && item.fechaEntrega < hoy ->
                            ChipEdu(if (esEvaluacion) "Pasó" else "Venció", TonoEdu.GRIS)
                    }
                }
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Ver detalle",
                tint = EduTextoSecundario, modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (esEvaluacion) {
                ChipEdu("Aviso", TonoEdu.AMARILLO)
                ChipEdu("${item.lecturas} lecturas", TonoEdu.AZUL)
            } else {
                ChipEdu("Entregas ${item.calificados}/${item.totalAlumnos}", TonoEdu.VERDE)
                ChipEdu("${item.lecturas} lecturas", TonoEdu.AZUL)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "${(progreso * 100).toInt()}%", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp,
                    color = if (progreso >= 1f) EduTealOscuro else EduTexto
                )
            }
        }
        if (!esEvaluacion) {
            Spacer(modifier = Modifier.height(8.dp))
            BarraProgresoEdu(progreso = progreso, color = EduTeal)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HistorialPublicacionesPreview() {
    EduConnectAppTheme {
        HistorialPublicacionesScreen(
            titulo = "Ciencia y Tecnología",
            subtitulo = "Publicaciones · 1er Grado A",
            publicaciones = listOf(
                PublicacionHistorialItem(1L, "Tarea", "Ejercicios cap. 5 · Fracciones", "Ciencia y Tecnología", "1er Grado", "A", hoyPub(), "2026-10-15", 5, 3, 4),
                PublicacionHistorialItem(2L, "Evaluación", "Práctica calificada N° 2", "Ciencia y Tecnología", "1er Grado", "A", "2026-10-26", "2026-10-16", 5, 0, 4),
                PublicacionHistorialItem(3L, "Tarea", "Lectura cap. 4", "Ciencia y Tecnología", "1er Grado", "A", "2026-10-09", "2026-10-02", 5, 5, 5, "CERRADA")
            )
        )
    }
}
