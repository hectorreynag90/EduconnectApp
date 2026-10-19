package com.educonnectapp.ui.screens.docente.publicaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.School
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonCrearEdu
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.ContadorEdu
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Una clase del docente (grado · sección · curso) en Publicaciones.
 * proximaFecha: "yyyy-MM-dd" de la próxima entrega/evaluación vigente ("" si no hay).
 */
data class ClasePublicacionItem(
    val gradoId: Long,
    val grado: String,
    val seccionId: Long,
    val seccion: String,
    val cursoId: Long,
    val curso: String,
    val totalAlumnos: Int,
    val tareas: Int = 0,
    val evaluaciones: Int = 0,
    val proximaFecha: String = ""
)

// =====================================================================
// PUBLICACIONES · inicio del módulo (rediseño 2026)
// Cabecera fija amarilla · resumen · mis clases por grado · "+ Crear" (Tarea / Aviso de evaluación)
// =====================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PublicacionesScreen(
    totalTareas: Int = 0,
    totalEvaluaciones: Int = 0,
    venceHoy: Int = 0,
    totalPublicacionesHoy: Int = 0,
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onNuevaTarea: () -> Unit = {},
    onNuevaEvaluacion: () -> Unit = {},
    onHistorial: () -> Unit = {},          // ya no se usa (se conserva por compatibilidad)
    // ----- Rediseño 2026 -----
    clases: List<ClasePublicacionItem> = emptyList(),
    cargandoClases: Boolean = false,
    onElegirClase: (ClasePublicacionItem) -> Unit = {}
) {
    var mostrarCrear by remember { mutableStateOf(false) }
    val fechaHoy = remember {
        "Hoy · " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "PE")))
    }
    val porGrado = clases
        .sortedWith(compareBy({ it.gradoId }, { it.curso }, { it.seccion }))
        .groupBy { it.grado }

    if (mostrarCrear) {
        HojaCrearPublicacion(
            onTarea = onNuevaTarea,
            onEvaluacion = onNuevaEvaluacion,
            onCerrar = { mostrarCrear = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Publicaciones",
            subtitulo = "Tareas y evaluaciones de tus clases",
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
                    fechaHoy, modifier = Modifier.padding(top = 14.dp),
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
                // RESUMEN
                TituloSeccionEdu("Resumen")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ContadorEdu(totalTareas, "Tareas", TonoEdu.AZUL, Modifier.weight(1f))
                    ContadorEdu(totalEvaluaciones, "Evaluaciones", TonoEdu.AMARILLO, Modifier.weight(1f))
                    ContadorEdu(venceHoy, "Vence hoy", TonoEdu.CORAL, Modifier.weight(1f))
                }

                // MIS CLASES
                Spacer(modifier = Modifier.height(4.dp))
                TituloSeccionEdu("Mis clases")
                when {
                    cargandoClases && clases.isEmpty() -> TarjetaEdu {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = EduTeal, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                            Text(
                                "Cargando tus clases...", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                                fontSize = 15.sp, color = EduTextoSecundario
                            )
                        }
                    }
                    clases.isEmpty() -> TarjetaEdu {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconoCirculoEdu(Icons.Rounded.School, TonoEdu.GRIS)
                            Text(
                                "Aún no tienes clases asignadas. Agrégalas desde tu perfil.",
                                modifier = Modifier.weight(1f),
                                fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                                color = EduTextoSecundario
                            )
                        }
                    }
                    else -> porGrado.forEach { (grado, lista) ->
                        Text(
                            grado.uppercase(),
                            modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                            fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.5.sp,
                            letterSpacing = 0.6.sp, color = EduTextoSecundario.copy(alpha = 0.8f)
                        )
                        lista.forEach { clase ->
                            TarjetaClasePublicacion(clase = clase, onClick = { onElegirClase(clase) })
                        }
                    }
                }
            }

            // BOTÓN FLOTANTE "+ Crear"
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

// Tarjeta de una clase: curso, sección · alumnos, cantidad de tareas/evaluaciones y próxima fecha
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TarjetaClasePublicacion(clase: ClasePublicacionItem, onClick: () -> Unit) {
    val hoy = hoyPub()
    TarjetaEdu(onClick = onClick, relleno = PaddingValues(horizontal = 14.dp, vertical = 13.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconoCirculoEdu(Icons.AutoMirrored.Rounded.Assignment, TonoEdu.AMARILLO, tamano = 46.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    clase.curso, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.sp,
                    color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Sección ${clase.seccion} · ${clase.totalAlumnos} alumnos",
                    fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (clase.tareas == 0 && clase.evaluaciones == 0) {
                        ChipEdu("Sin publicaciones", TonoEdu.GRIS)
                    }
                    if (clase.tareas > 0) {
                        ChipEdu(if (clase.tareas == 1) "1 tarea" else "${clase.tareas} tareas", TonoEdu.AZUL)
                    }
                    if (clase.evaluaciones > 0) {
                        ChipEdu(
                            if (clase.evaluaciones == 1) "1 evaluación" else "${clase.evaluaciones} evaluaciones",
                            TonoEdu.AMARILLO
                        )
                    }
                    if (clase.proximaFecha.isNotBlank()) {
                        if (clase.proximaFecha == hoy) ChipEdu("Vence hoy", TonoEdu.CORAL)
                        else ChipEdu("Próxima: ${fechaCortaPub(clase.proximaFecha)}", TonoEdu.VERDE)
                    }
                }
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Ver publicaciones",
                tint = EduTextoSecundario, modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PublicacionesPreview() {
    EduConnectAppTheme {
        PublicacionesScreen(
            totalTareas = 3,
            totalEvaluaciones = 2,
            venceHoy = 1,
            clases = listOf(
                ClasePublicacionItem(1, "1er Grado", 1, "A", 1, "Ciencia y Tecnología", 5, 2, 1, hoyPub()),
                ClasePublicacionItem(2, "2do Grado", 7, "A", 1, "Ciencia y Tecnología", 5, 1, 0, "2026-10-23"),
                ClasePublicacionItem(2, "2do Grado", 8, "B", 2, "Matemáticas", 24)
            )
        )
    }
}
