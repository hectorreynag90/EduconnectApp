package com.educonnectapp.ui.screens.docente.historial

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Resumen de cada historial (se arma en MainActivity con datos ya cargados; sin peticiones extra).
// ultimoRegistro: "Hoy", "jue 12 nov"… o "" si aún no se consultó. proximaEvaluacion: "yyyy-MM-dd" o "".
data class ResumenHistorialDocente(
    val clases: Int = 0,
    val ultimoRegistro: String = "",
    val comunicados: Int = 0,
    val porcentajeLectura: Int? = null,
    val tareas: Int = 0,
    val entregasRegistradas: Int = 0,
    val entregasTotales: Int = 0,
    val evaluaciones: Int = 0,
    val proximaEvaluacion: String = ""
)

// =====================================================================
// HISTORIAL DEL DOCENTE (rediseño 2026)
// Se abre desde el Inicio. Cabecera fija turquesa · 4 accesos:
// Asistencias, Comunicados, Tareas y Evaluaciones (de todas las clases)
// =====================================================================

@Composable
fun HistorialDocenteScreen(
    resumen: ResumenHistorialDocente = ResumenHistorialDocente(),
    onAsistencias: () -> Unit = {},
    onComunicados: () -> Unit = {},
    onTareas: () -> Unit = {},
    onEvaluaciones: () -> Unit = {},
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {}
) {
    val anio = LocalDate.now().year

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Historial",
            subtitulo = "Todo lo que registraste y enviaste",
            onVolver = onBack,
            colorFondo = ColorModulo.Inicio,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Inicio
                )
            },
            contenidoInferior = {
                ChipCabeceraEdu(
                    "Año escolar $anio", modifier = Modifier.padding(top = 14.dp),
                    colorCabecera = ColorModulo.Inicio
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
            FilaHistorial(
                icono = Icons.Rounded.History, tono = TonoEdu.AZUL, titulo = "Asistencias",
                detalle = buildString {
                    append(if (resumen.clases == 1) "1 clase" else "${resumen.clases} clases")
                    if (resumen.ultimoRegistro.isNotBlank()) append(" · último registro: ${resumen.ultimoRegistro}")
                },
                onClick = onAsistencias
            )
            FilaHistorial(
                icono = Icons.Rounded.Campaign, tono = TonoEdu.CORAL, titulo = "Comunicados",
                detalle = when (resumen.comunicados) {
                    0 -> "Aún no enviaste comunicados"
                    else -> (if (resumen.comunicados == 1) "1 enviado" else "${resumen.comunicados} enviados") +
                            (resumen.porcentajeLectura?.let { " · $it % leído en promedio" } ?: "")
                },
                onClick = onComunicados
            )
            FilaHistorial(
                icono = Icons.AutoMirrored.Rounded.Assignment, tono = TonoEdu.AMARILLO, titulo = "Tareas",
                detalle = when (resumen.tareas) {
                    0 -> "Aún no publicaste tareas"
                    else -> (if (resumen.tareas == 1) "1 tarea" else "${resumen.tareas} tareas") +
                            " · ${resumen.entregasRegistradas} de ${resumen.entregasTotales} entregas"
                },
                onClick = onTareas
            )
            FilaHistorial(
                icono = Icons.Rounded.Event, tono = TonoEdu.AZUL, solido = true, titulo = "Evaluaciones",
                detalle = when (resumen.evaluaciones) {
                    0 -> "Aún no publicaste avisos"
                    else -> (if (resumen.evaluaciones == 1) "1 aviso" else "${resumen.evaluaciones} avisos") +
                            (if (resumen.proximaEvaluacion.isNotBlank()) " · próxima: ${fechaCortaHist(resumen.proximaEvaluacion)}"
                            else " · sin próximas")
                },
                onClick = onEvaluaciones
            )

            // AVISO
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .background(EduTealClaro, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = EduTealOscuro, modifier = Modifier.size(22.dp))
                Text(
                    "También puedes abrir el historial desde el botón “Historial” de cada módulo.",
                    modifier = Modifier.weight(1f),
                    fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp,
                    lineHeight = 18.sp, color = EduEtiqueta
                )
            }
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

// Fila de un historial: ícono en círculo, nombre, resumen y flecha
@Composable
private fun FilaHistorial(
    icono: ImageVector,
    tono: TonoEdu,
    titulo: String,
    detalle: String,
    onClick: () -> Unit,
    solido: Boolean = false
) {
    TarjetaEdu(onClick = onClick, relleno = PaddingValues(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconoCirculoEdu(icono, tono, tamano = 50.dp, solido = solido)
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp, color = EduTexto)
                Text(
                    detalle, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp,
                    color = EduTextoSecundario, lineHeight = 18.sp
                )
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null,
                tint = EduTextoSecundario, modifier = Modifier.size(26.dp)
            )
        }
    }
}

// "2026-11-20" -> "20 nov" (hoy -> "Hoy")
private fun fechaCortaHist(fecha: String): String = try {
    val d = LocalDate.parse(fecha.take(10))
    if (d == LocalDate.now()) "Hoy"
    else d.format(DateTimeFormatter.ofPattern("d MMM", Locale("es", "PE"))).replace(".", "")
} catch (e: Exception) { fecha }

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HistorialDocentePreview() {
    EduConnectAppTheme {
        HistorialDocenteScreen(
            resumen = ResumenHistorialDocente(
                clases = 2, ultimoRegistro = "jue 12 nov",
                comunicados = 3, porcentajeLectura = 80,
                tareas = 2, entregasRegistradas = 4, entregasTotales = 10,
                evaluaciones = 1, proximaEvaluacion = LocalDate.now().plusDays(7).toString()
            )
        )
    }
}
