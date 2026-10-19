package com.educonnectapp.ui.screens.docente.comunicados

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
 * Una clase del docente (grado · sección · curso) en Comunicados.
 * ultimoEnvio: "Hoy", "vie 16 oct"… o "" si aún no tiene comunicados.
 */
data class ClaseComunicadoItem(
    val gradoId: Long,
    val grado: String,
    val seccionId: Long,
    val seccion: String,
    val cursoId: Long,
    val curso: String,
    val totalPadres: Int,
    val cantidadComunicados: Int = 0,
    val ultimoEnvio: String = "",
    val ultimoEsHoy: Boolean = false
)

// =====================================================================
// COMUNICADOS · inicio del módulo (rediseño 2026)
// Cabecera fija granate · resumen de hoy · mis clases por grado · botón "+ Nuevo"
// =====================================================================

@Composable
fun ComunicadosScreen(
    docenteId: String,
    docenteNombre: String,
    totalEnviados: Int = 0,      // padres notificados hoy
    totalHoy: Int = 0,           // comunicados enviados hoy
    sinLeer: Int = 0,            // padres que aún no leen los de hoy
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onNuevoComunicado: () -> Unit = {},
    onHistorial: () -> Unit = {},     // ya no se usa (se conserva por compatibilidad)
    // ----- Rediseño 2026 -----
    clases: List<ClaseComunicadoItem> = emptyList(),
    cargandoClases: Boolean = false,
    onElegirClase: (ClaseComunicadoItem) -> Unit = {}
) {
    val fechaHoy = remember {
        "Hoy · " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "PE")))
    }
    // Agrupadas por grado y, dentro, por curso y sección
    val porGrado = clases
        .sortedWith(compareBy({ it.gradoId }, { it.curso }, { it.seccion }))
        .groupBy { it.grado }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Comunicados",
            subtitulo = "Avisos a los padres de tus clases",
            onVolver = onBack,
            colorFondo = ColorModulo.Comunicados,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Comunicados
                )
            },
            contenidoInferior = {
                ChipCabeceraEdu(
                    fechaHoy, modifier = Modifier.padding(top = 14.dp),
                    colorCabecera = ColorModulo.Comunicados
                )
            }
        )

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            // CONTENIDO DESPLAZABLE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),  // espacio para "+ Nuevo"
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // RESUMEN DE HOY
                TituloSeccionEdu("Resumen de hoy")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ContadorEdu(totalHoy, "Enviados", TonoEdu.AZUL, Modifier.weight(1f))
                    ContadorEdu((totalEnviados - sinLeer).coerceAtLeast(0), "Leídos", TonoEdu.VERDE, Modifier.weight(1f))
                    ContadorEdu(sinLeer, "Sin leer", TonoEdu.CORAL, Modifier.weight(1f))
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
                            grado.uppercase() + " GRADO",
                            modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                            fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.5.sp,
                            letterSpacing = 0.6.sp, color = EduTextoSecundario.copy(alpha = 0.8f)
                        )
                        lista.forEach { clase ->
                            TarjetaClaseComunicado(clase = clase, onClick = { onElegirClase(clase) })
                        }
                    }
                }
            }

            // BOTÓN FLOTANTE "+ Nuevo"
            BotonCrearEdu(
                onClick = onNuevoComunicado,
                texto = "Nuevo",
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
            itemActivo = "Avisos"
        )
    }
}

// Tarjeta de una clase: curso, sección · padres, cantidad de comunicados y último envío
@Composable
private fun TarjetaClaseComunicado(clase: ClaseComunicadoItem, onClick: () -> Unit) {
    TarjetaEdu(onClick = onClick, relleno = PaddingValues(horizontal = 14.dp, vertical = 13.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconoCirculoEdu(Icons.Rounded.Campaign, TonoEdu.CORAL, tamano = 46.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    clase.curso, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.sp,
                    color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Sección ${clase.seccion} · ${clase.totalPadres} padres",
                    fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (clase.cantidadComunicados == 0) {
                        ChipEdu("Sin comunicados", TonoEdu.GRIS)
                    } else {
                        ChipEdu(
                            if (clase.cantidadComunicados == 1) "1 comunicado" else "${clase.cantidadComunicados} comunicados",
                            TonoEdu.CORAL
                        )
                        if (clase.ultimoEnvio.isNotEmpty()) {
                            ChipEdu("Último: ${clase.ultimoEnvio}", if (clase.ultimoEsHoy) TonoEdu.VERDE else TonoEdu.AZUL)
                        }
                    }
                }
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Ver comunicados",
                tint = EduTextoSecundario, modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ComunicadosPreview() {
    EduConnectAppTheme {
        ComunicadosScreen(
            docenteId = "uuid-docente",
            docenteNombre = "Hector Reyna Gomez",
            totalEnviados = 10,
            totalHoy = 2,
            sinLeer = 3,
            clases = listOf(
                ClaseComunicadoItem(1, "1er Grado", 1, "A", 1, "Ciencia y Tecnología", 5, 3, "Hoy", true),
                ClaseComunicadoItem(2, "2do Grado", 7, "A", 1, "Ciencia y Tecnología", 5, 1, "vie 16 oct"),
                ClaseComunicadoItem(2, "2do Grado", 8, "B", 2, "Matemáticas", 24)
            )
        )
    }
}
