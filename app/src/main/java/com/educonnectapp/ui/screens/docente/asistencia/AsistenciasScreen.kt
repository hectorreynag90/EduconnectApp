package com.educonnectapp.ui.screens.docente.asistencia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.coloresDeTono
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.screens.docente.inicio.ClaseHoyItem
import com.educonnectapp.ui.screens.docente.inicio.TarjetaClase
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AsistenciasScreen(
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onHistorial: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    realizados: Int = 0,
    pendientes: Int = 0,
    porcentajeAsistencias: Int = 0,
    // ----- Rediseño 2026 -----
    clases: List<ClaseHoyItem> = emptyList(),
    cargandoClases: Boolean = false,
    onTomarAsistencia: (ClaseHoyItem) -> Unit = {}
) {
    val fechaHoy = remember {
        "Hoy · " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "PE")))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Asistencia",
            subtitulo = "Elige la clase para tomar lista",
            onVolver = onBack,
            colorFondo = ColorModulo.Asistencia,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Asistencia
                )
            },
            contenidoInferior = {
                ChipCabeceraEdu(
                    fechaHoy, modifier = Modifier.padding(top = 14.dp),
                    colorCabecera = ColorModulo.Asistencia
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
            // RESUMEN DEL DÍA
            TituloSeccionEdu("Resumen del día")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ContadorResumen("$realizados", "Tomadas", TonoEdu.VERDE, Modifier.weight(1f))
                ContadorResumen("$pendientes", "Pendientes", TonoEdu.AMARILLO, Modifier.weight(1f))
                ContadorResumen("$porcentajeAsistencias%", "Asistencia", TonoEdu.AZUL, Modifier.weight(1f))
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
                        IconoCirculoEdu(Icons.Rounded.School, TonoEdu.VERDE)
                        Text(
                            "Aún no tienes clases asignadas. Agrégalas desde tu perfil.",
                            modifier = Modifier.weight(1f),
                            fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                            color = EduTextoSecundario
                        )
                    }
                }
                else -> clases.forEach { clase ->
                    TarjetaClase(clase = clase, onClick = { onTomarAsistencia(clase) })
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            BotonSecundarioEdu(texto = "Ver historial", onClick = onHistorial, icono = Icons.Rounded.History)
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Alumnos",
        )
    }
}

// Contador con texto (permite "85%")
@Composable
private fun ContadorResumen(valor: String, etiqueta: String, tono: TonoEdu, modifier: Modifier) {
    val c = coloresDeTono(tono)
    Column(
        modifier = modifier
            .background(c.fondo, RoundedCornerShape(18.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 24.sp, color = c.texto)
        Text(etiqueta, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.5.sp, color = c.texto)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun AsistenciasScreenPreview() {
    EduConnectAppTheme {
        AsistenciasScreen(
            realizados = 1, pendientes = 1, porcentajeAsistencias = 92,
            clases = listOf(
                ClaseHoyItem(1, "1er Grado", 1, "A", 1, "Ciencia y Tecnología", 24, tomada = true),
                ClaseHoyItem(2, "2do Grado", 7, "A", 3, "Religión", 6, tomada = false)
            )
        )
    }
}
