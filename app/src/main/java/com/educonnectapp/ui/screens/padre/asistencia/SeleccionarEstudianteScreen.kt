package com.educonnectapp.ui.screens.padre.asistencia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BarraProgresoEdu
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.padre.inicio.BottomNavBarPadre
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAmarilloOscuro
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduCoralOscuro
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Hijo del padre con su resumen de asistencia del mes (lo arma MainActivity)
data class HijoItem(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val porcentajeMes: Int,
    // ----- Rediseño 2026 -----
    val registrosMes: Int = 0,          // clases con asistencia registrada en el mes
    val faltasMes: Int = 0,
    val estadoHoy: String? = null       // peor estado de hoy: "A" | "T" | "F" (null = sin registros hoy)
)

// =====================================================================
// ASISTENCIA DEL PADRE · ELEGIR HIJO (rediseño 2026)
// Cabecera fija azul oscuro · una tarjeta por hijo (se toca y entra a su historial)
// =====================================================================

@Composable
fun SeleccionarEstudianteScreen(
    padreId: String,
    padreNombre: String,
    listaHijos: List<HijoItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerHistorial: (alumnoId: Long, alumnoNombre: String) -> Unit = { _, _ -> },
    // ----- Rediseño 2026 -----
    cargando: Boolean = false,
    onAsociarHijo: () -> Unit = {}
) {
    val mesActual = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "PE")))
            .replaceFirstChar { it.uppercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Asistencia",
            subtitulo = "Elige a tu hijo",
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
                ChipCabeceraEdu(mesActual, modifier = Modifier.padding(top = 14.dp), colorCabecera = ColorModulo.Asistencia)
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
            TituloSeccionEdu("Mis hijos")
            when {
                cargando && listaHijos.isEmpty() -> TarjetaEdu {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = EduTeal, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                        Text(
                            "Cargando la asistencia…", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                            fontSize = 15.sp, color = EduTextoSecundario
                        )
                    }
                }
                listaHijos.isEmpty() -> TarjetaEdu(
                    relleno = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                    onClick = onAsociarHijo
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconoCirculoEdu(Icons.Rounded.PersonAdd, TonoEdu.VERDE, tamano = 64.dp)
                        Text(
                            "Aún no tienes hijos asociados", fontFamily = Nunito, fontWeight = FontWeight.Black,
                            fontSize = 17.sp, color = EduTexto
                        )
                        Text(
                            "Asócialos desde tu perfil con el código de estudiante.", fontFamily = Nunito,
                            fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = EduTextoSecundario,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> listaHijos.forEachIndexed { i, hijo ->
                    TarjetaHijoAsistencia(hijo, colorAvatar = if (i % 2 == 0) EduAmarillo else EduTealClaro) {
                        onVerHistorial(hijo.id, "${hijo.nombres} ${hijo.apellidos}".trim())
                    }
                }
            }

            if (listaHijos.isNotEmpty()) {
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
                        "Toca a tu hijo para ver su calendario de asistencia y su avance por curso. " +
                                "La tardanza cuenta como asistencia.",
                        modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp, lineHeight = 18.sp, color = EduEtiqueta
                    )
                }
            }
        }

        BottomNavBarPadre(
            onInicio = onHomePadre,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Inicio"
        )
    }
}

// Tarjeta de un hijo: iniciales, nombre, grado, % del mes con barra y cómo le fue hoy
@Composable
private fun TarjetaHijoAsistencia(hijo: HijoItem, colorAvatar: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    val iniciales = "${hijo.nombres.firstOrNull() ?: ""}${hijo.apellidos.firstOrNull() ?: ""}".uppercase()
    val sinRegistros = hijo.registrosMes == 0
    val colorPct = when {
        sinRegistros -> EduTextoSecundario
        hijo.porcentajeMes >= 90 -> EduTealOscuro
        hijo.porcentajeMes >= 75 -> EduAmarilloOscuro
        else -> EduCoralOscuro
    }
    TarjetaEdu(relleno = PaddingValues(horizontal = 15.dp, vertical = 14.dp), onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(52.dp).background(colorAvatar, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(iniciales, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 19.sp, color = EduTexto)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${hijo.nombres} ${hijo.apellidos}".trim(), fontFamily = Nunito, fontWeight = FontWeight.Black,
                    fontSize = 17.sp, color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${hijo.gradoNombre} · Sección ${hijo.seccionNombre}", fontFamily = Nunito,
                    fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    if (sinRegistros) "—" else "${hijo.porcentajeMes}%", fontFamily = Nunito,
                    fontWeight = FontWeight.Black, fontSize = 22.sp, color = colorPct
                )
                Text("este mes", fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 11.5.sp, color = EduTextoSecundario)
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null,
                tint = EduTextoSecundario, modifier = Modifier.size(24.dp)
            )
        }
        BarraProgresoEdu(
            progreso = if (sinRegistros) 0f else hijo.porcentajeMes / 100f,
            modifier = Modifier.padding(top = 10.dp, bottom = 8.dp),
            color = when {
                hijo.porcentajeMes >= 90 -> EduTeal
                hijo.porcentajeMes >= 75 -> EduAmarillo
                else -> EduCoral
            }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            when (hijo.estadoHoy) {
                "A" -> ChipEdu("Hoy: Presente", TonoEdu.VERDE)
                "T" -> ChipEdu("Hoy: Tardanza", TonoEdu.AMARILLO)
                "F" -> ChipEdu("Hoy: Falta", TonoEdu.CORAL)
                else -> ChipEdu("Hoy: sin registros", TonoEdu.GRIS)
            }
            if (hijo.faltasMes > 0) {
                ChipEdu(if (hijo.faltasMes == 1) "1 falta en el mes" else "${hijo.faltasMes} faltas en el mes", TonoEdu.CORAL)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun SeleccionarEstudiantePreview() {
    EduConnectAppTheme {
        SeleccionarEstudianteScreen(
            padreId = "1",
            padreNombre = "Luis Perez",
            listaHijos = listOf(
                HijoItem(1L, "Angela", "Perez Garcia", "2do Grado", "A", 89, registrosMes = 37, faltasMes = 4, estadoHoy = "T"),
                HijoItem(2L, "Pedro", "Perez Garcia", "1er Grado", "A", 88, registrosMes = 30, faltasMes = 2, estadoHoy = "F")
            )
        )
    }
}
