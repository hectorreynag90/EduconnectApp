package com.educonnectapp.ui.screens.docente.asistencia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

/**
 * Una clase del docente (grado · sección · curso) en el historial.
 * ultimoRegistro: texto ya formateado ("Hoy · 08:05 a. m.", "vie 16 oct"),
 *   "" = la clase aún no tiene registros, null = todavía se está consultando.
 */
data class ClaseHistorialItem(
    val gradoId: Long,
    val grado: String,
    val seccionId: Long,
    val seccion: String,
    val cursoId: Long,
    val curso: String,
    val cantidadAlumnos: Int,
    val ultimoRegistro: String? = null,
    val ultimoEsHoy: Boolean = false
)

// =====================================================================
// HISTORIAL DE ASISTENCIA · elegir clase (rediseño 2026)
// Cabecera fija azul oscuro · clases agrupadas por grado · fecha del último registro
// Un toque en la clase abre su historial (sin elegir grado → sección → curso)
// =====================================================================

@Composable
fun SeleccionarSeccionHistorialScreen(
    clases: List<ClaseHistorialItem> = emptyList(),
    cargandoClases: Boolean = false,
    onElegirClase: (ClaseHistorialItem) -> Unit = {},
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {}
) {
    // Agrupadas por grado (en el orden de los grados) y, dentro, por curso y sección
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
            titulo = "Historial",
            subtitulo = "Elige la clase para ver sus registros",
            onVolver = onBack,
            colorFondo = ColorModulo.Asistencia,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
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
                else -> {
                    porGrado.entries.forEachIndexed { i, (grado, lista) ->
                        if (i > 0) Spacer(modifier = Modifier.height(4.dp))
                        TituloSeccionEdu(grado) {
                            ChipEdu(if (lista.size == 1) "1 clase" else "${lista.size} clases", TonoEdu.AZUL)
                        }
                        lista.forEach { clase ->
                            TarjetaClaseHistorial(clase = clase, onClick = { onElegirClase(clase) })
                        }
                    }

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
                            "Toca una clase para ver todos los días en que tomaste asistencia, " +
                                    "filtrar por fecha y corregir un registro.",
                            modifier = Modifier.weight(1f),
                            fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp,
                            lineHeight = 18.sp, color = EduEtiqueta
                        )
                    }
                }
            }
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Alumnos"
        )
    }
}

// Tarjeta de una clase: ícono de historial, curso, sección · alumnos y chip con el último registro
@Composable
private fun TarjetaClaseHistorial(clase: ClaseHistorialItem, onClick: () -> Unit) {
    TarjetaEdu(onClick = onClick, relleno = PaddingValues(horizontal = 14.dp, vertical = 13.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconoCirculoEdu(Icons.Rounded.History, TonoEdu.AZUL, tamano = 46.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    clase.curso, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.sp,
                    color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Sección ${clase.seccion} · ${clase.cantidadAlumnos} alumnos",
                    fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
                )
                Spacer(modifier = Modifier.height(4.dp))
                when {
                    clase.ultimoRegistro == null -> ChipEdu("Cargando...", TonoEdu.GRIS)
                    clase.ultimoRegistro.isEmpty() -> ChipEdu("Aún sin registros", TonoEdu.GRIS)
                    clase.ultimoEsHoy -> ChipEdu("Último: ${clase.ultimoRegistro}", TonoEdu.VERDE)
                    else -> ChipEdu("Último: ${clase.ultimoRegistro}", TonoEdu.AZUL)
                }
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Ver historial",
                tint = EduTextoSecundario, modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun SeleccionarSeccionHistorialPreview() {
    EduConnectAppTheme {
        SeleccionarSeccionHistorialScreen(
            clases = listOf(
                ClaseHistorialItem(1, "1er Grado", 1, "A", 1, "Ciencia y Tecnología", 5, "Hoy · 08:05 a. m.", true),
                ClaseHistorialItem(2, "2do Grado", 7, "A", 1, "Ciencia y Tecnología", 5, "vie 16 oct"),
                ClaseHistorialItem(2, "2do Grado", 8, "B", 2, "Matemáticas", 24, "jue 15 oct"),
                ClaseHistorialItem(3, "3er Grado", 9, "A", 3, "Religión", 18, "")
            )
        )
    }
}
