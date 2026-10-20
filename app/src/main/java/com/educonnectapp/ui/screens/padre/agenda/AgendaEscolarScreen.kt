package com.educonnectapp.ui.screens.padre.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.WbSunny
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
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.padre.inicio.BottomNavBarPadre
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

// Tarea o evaluación de la agenda del hijo (la arma MainActivity)
data class PublicacionAgendaItem(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val tipo: String,                   // "Tarea" | "Evaluación"
    val cursoNombre: String,
    val docenteNombre: String,
    val fechaEntrega: String,           // "yyyy-MM-dd"
    val estado: String = "Pendiente",   // Tarea: "Pendiente", "Entregada", "No entregada", "Vencida" | Evaluación: "Pendiente", "Realizado"
    val archivoAdjunto: String = "",
    // ----- Rediseño 2026 -----
    val publicadoEn: String = ""        // "yyyy-MM-dd" (hora local)
)

// ---------------------------------------------------------------------
// Fechas de la agenda (compartidas por las 3 pantallas del módulo)
// ---------------------------------------------------------------------
internal val LOCALE_PE_AGENDA = Locale("es", "PE")

// Días desde hoy hasta la fecha ("yyyy-MM-dd"); null si no hay fecha válida
internal fun diasHastaAgenda(fecha: String): Long? = try {
    ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(fecha.take(10)))
} catch (e: Exception) { null }

// "2026-10-20" -> "Mar 20 oct"
internal fun fechaCortaAgenda(fecha: String): String = try {
    LocalDate.parse(fecha.take(10)).format(DateTimeFormatter.ofPattern("EEE d MMM", LOCALE_PE_AGENDA))
        .replace(".", "").replaceFirstChar { it.uppercase() }
} catch (e: Exception) { fecha }

// "2026-10-20" -> "Martes 20 de octubre"
internal fun fechaLargaAgenda(fecha: String): String = try {
    LocalDate.parse(fecha.take(10)).format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", LOCALE_PE_AGENDA))
        .replaceFirstChar { it.uppercase() }
} catch (e: Exception) { fecha }

// "vence hoy" · "vence mañana" · "vie 23 oct"
internal fun cuandoVence(fecha: String): String = when (diasHastaAgenda(fecha)) {
    null -> "sin fecha"
    0L -> "vence hoy"
    1L -> "vence mañana"
    else -> fechaCortaAgenda(fecha).replaceFirstChar { it.lowercase() }
}

// "Hoy", "1 día", "5 días" (se conserva por compatibilidad)
fun textoDiasRestantes(dias: Int): String = when {
    dias <= 0 -> "Hoy"
    dias == 1 -> "1 día"
    else -> "$dias días"
}

// =====================================================================
// AGENDA DEL PADRE · AGENDA DE UN HIJO (rediseño 2026)
// Cabecera fija amarilla · pestañas Todos / Tareas / Evaluaciones · "Por hacer" y "Ya pasaron"
// =====================================================================

@Composable
fun AgendaEscolarScreen(
    nombreHijo: String = "",
    gradoNombre: String = "",
    seccionNombre: String = "",
    publicaciones: List<PublicacionAgendaItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerDetalle: (PublicacionAgendaItem) -> Unit = {},
    // ----- Rediseño 2026 -----
    apellidosHijo: String = ""
) {
    var pestana by remember { mutableStateOf(0) }       // 0 = Todos · 1 = Tareas · 2 = Evaluaciones
    val tareas = publicaciones.filter { it.tipo == "Tarea" }
    val evaluaciones = publicaciones.filter { it.tipo == "Evaluación" }
    val visibles = when (pestana) {
        1 -> tareas
        2 -> evaluaciones
        else -> publicaciones
    }
    // Por hacer: lo pendiente, de la fecha más cercana a la más lejana
    val porHacer = visibles.filter { it.estado == "Pendiente" }.sortedBy { it.fechaEntrega.ifEmpty { "9999-12-31" } }
    // Ya pasaron: lo entregado, no entregado, vencido o realizado, de lo más reciente a lo más antiguo
    val pasadas = visibles.filter { it.estado != "Pendiente" }.sortedByDescending { it.fechaEntrega }
    val tareasPorHacer = tareas.count { it.estado == "Pendiente" }
    val evaluacionesProximas = evaluaciones.count { it.estado == "Pendiente" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Agenda",
            subtitulo = listOf("$nombreHijo $apellidosHijo".trim(), "$gradoNombre $seccionNombre".trim())
                .filter { it.isNotBlank() }.joinToString(" · "),
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
                    listOf(
                        when (tareasPorHacer) { 0 -> "Sin tareas por hacer"; 1 -> "1 tarea por hacer"; else -> "$tareasPorHacer tareas por hacer" },
                        when (evaluacionesProximas) { 0 -> ""; 1 -> "1 evaluación"; else -> "$evaluacionesProximas evaluaciones" }
                    ).filter { it.isNotBlank() }.joinToString(" · "),
                    modifier = Modifier.padding(top = 14.dp),
                    colorCabecera = ColorModulo.Publicaciones
                )
            }
        )

        // CONTENIDO DESPLAZABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Pestanas(
                opciones = listOf("Todos (${publicaciones.size})", "Tareas (${tareas.size})", "Evaluaciones (${evaluaciones.size})"),
                seleccionada = pestana,
                onElegir = { pestana = it }
            )

            if (visibles.isEmpty()) {
                TarjetaEdu(relleno = PaddingValues(horizontal = 16.dp, vertical = 18.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconoCirculoEdu(Icons.Rounded.WbSunny, TonoEdu.VERDE, tamano = 64.dp)
                        Text(
                            when (pestana) { 1 -> "Sin tareas"; 2 -> "Sin evaluaciones"; else -> "Agenda vacía" },
                            fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp, color = EduTexto
                        )
                        Text(
                            "Aún no hay publicaciones para $nombreHijo. Te avisaremos cuando el docente publique algo.",
                            fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp,
                            lineHeight = 19.sp, color = EduTextoSecundario, textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Grupo(if (pestana == 2) "Próximas" else "Por hacer")
                if (porHacer.isEmpty()) {
                    TarjetaEdu {
                        Text(
                            if (pestana == 2) "No hay evaluaciones próximas." else "¡Todo al día! No hay pendientes por ahora.",
                            modifier = Modifier.fillMaxWidth(), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp, color = EduTextoSecundario, textAlign = TextAlign.Center
                        )
                    }
                }
                porHacer.forEach { p -> TarjetaAgenda(p) { onVerDetalle(p) } }
                if (pasadas.isNotEmpty()) {
                    Grupo("Ya pasaron")
                    pasadas.forEach { p -> TarjetaAgenda(p) { onVerDetalle(p) } }
                }
            }
        }

        BottomNavBarPadre(
            onInicio = onHomePadre,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Agenda"
        )
    }
}

@Composable
private fun Grupo(texto: String) {
    Text(
        texto.uppercase(), modifier = Modifier.padding(start = 2.dp, top = 2.dp),
        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.5.sp,
        letterSpacing = 0.6.sp, color = EduTextoSecundario.copy(alpha = 0.8f)
    )
}

// Pestañas Todos / Tareas / Evaluaciones (la elegida en amarillo)
@Composable
private fun Pestanas(opciones: List<String>, seleccionada: Int, onElegir: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.5.dp, EduBorde, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        opciones.forEachIndexed { i, texto ->
            val activa = i == seleccionada
            Text(
                texto,
                modifier = Modifier
                    .weight(if (i == 2) 1.35f else 1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activa) EduAmarillo else Color.Transparent)
                    .clickable { onElegir(i) }
                    .padding(vertical = 10.dp),
                fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.5.sp,
                color = if (activa) EduTexto else EduEtiqueta, textAlign = TextAlign.Center, maxLines = 1
            )
        }
    }
}

// Tarjeta de una tarea o evaluación: ícono, título, curso · docente, fecha y estado
@Composable
private fun TarjetaAgenda(p: PublicacionAgendaItem, onClick: () -> Unit) {
    val esEvaluacion = p.tipo == "Evaluación"
    val dias = diasHastaAgenda(p.fechaEntrega)
    TarjetaEdu(relleno = PaddingValues(horizontal = 14.dp, vertical = 13.dp), onClick = onClick) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconoCirculoEdu(
                if (esEvaluacion) Icons.Rounded.Event else Icons.Rounded.Assignment,
                if (esEvaluacion) TonoEdu.AZUL else TonoEdu.AMARILLO, tamano = 42.dp
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        p.titulo, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Black,
                        fontSize = 15.5.sp, color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null,
                        tint = EduTextoSecundario, modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    listOf(p.cursoNombre, p.docenteNombre).filter { it.isNotBlank() }.joinToString(" · "),
                    fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.5.sp,
                    color = EduTextoSecundario, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(15.dp))
                    Text(
                        if (p.fechaEntrega.isBlank()) "Sin fecha" else fechaCortaAgenda(p.fechaEntrega),
                        modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.5.sp, color = EduTextoSecundario
                    )
                    ChipEstadoAgenda(p.estado, esEvaluacion, dias)
                }
            }
        }
    }
}

// Chip del estado: cuenta regresiva si está pendiente; resultado si ya pasó
@Composable
internal fun ChipEstadoAgenda(estado: String, esEvaluacion: Boolean, dias: Long?) {
    when (estado) {
        "Pendiente" -> when {
            dias == null -> ChipEdu(if (esEvaluacion) "Programada" else "Pendiente", if (esEvaluacion) TonoEdu.AZUL else TonoEdu.AMARILLO)
            dias <= 0L -> ChipEdu(if (esEvaluacion) "Hoy" else "Vence hoy", TonoEdu.CORAL)
            dias == 1L -> ChipEdu(if (esEvaluacion) "Mañana" else "Vence mañana", TonoEdu.CORAL)
            else -> ChipEdu("En $dias días", if (esEvaluacion) TonoEdu.AZUL else TonoEdu.AMARILLO)
        }
        "Entregada" -> ChipEdu("Entregó", TonoEdu.VERDE)
        "No entregada" -> ChipEdu("No entregó", TonoEdu.CORAL)
        "Realizado" -> ChipEdu("Realizada", TonoEdu.GRIS)
        else -> ChipEdu("Vencida", TonoEdu.GRIS)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun AgendaEscolarPreview() {
    val hoy = LocalDate.now()
    EduConnectAppTheme {
        AgendaEscolarScreen(
            nombreHijo = "Angela",
            apellidosHijo = "Perez",
            gradoNombre = "2do Grado",
            seccionNombre = "A",
            publicaciones = listOf(
                PublicacionAgendaItem(1L, "Maqueta de ecosistemas", "Elaborar una maqueta.", "Tarea", "Ciencia y Tecnología", "Prof. Hector Reyna", hoy.plusDays(1).toString()),
                PublicacionAgendaItem(2L, "Evaluación de fracciones", "Fracciones equivalentes.", "Evaluación", "Matemáticas", "Prof. Rosa Díaz", hoy.plusDays(7).toString()),
                PublicacionAgendaItem(3L, "Resumen del cuento", "Media página.", "Tarea", "Comunicación", "Prof. Ana Ruiz", hoy.minusDays(3).toString(), "Entregada")
            )
        )
    }
}
