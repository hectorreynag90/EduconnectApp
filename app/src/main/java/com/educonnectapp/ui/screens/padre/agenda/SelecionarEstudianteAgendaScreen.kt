package com.educonnectapp.ui.screens.padre.agenda

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
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.coloresDeTono
import com.educonnectapp.ui.screens.padre.inicio.BottomNavBarPadre
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoralOscuro
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// Hijo con el resumen de su agenda (lo arma MainActivity)
data class HijoAgendaItem(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val cursoNombre: String = "",
    val gradoId: Long,
    val seccionId: Long,
    val tareasPendientes: Int = 0,
    val examenesPendientes: Int = 0,        // evaluaciones próximas
    // ----- Rediseño 2026 -----
    val proximoTitulo: String = "",         // lo próximo que vence (tarea o evaluación)
    val proximaFecha: String = ""           // "yyyy-MM-dd"
)

// =====================================================================
// AGENDA DEL PADRE · ELEGIR HIJO (rediseño 2026)
// Cabecera fija amarilla (como Publicaciones del docente) · resumen · una tarjeta por hijo
// =====================================================================

@Composable
fun SeleccionarHijoAgendaScreen(
    hijos: List<HijoAgendaItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerAgenda: (hijo: HijoAgendaItem) -> Unit = {},
    // ----- Rediseño 2026 -----
    cargando: Boolean = false,
    onAsociarHijo: () -> Unit = {}
) {
    val tareas = hijos.sumOf { it.tareasPendientes }
    val evaluaciones = hijos.sumOf { it.examenesPendientes }
    val vencePronto = hijos.count { (diasHastaAgenda(it.proximaFecha) ?: 99L) <= 1L }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Agenda",
            subtitulo = "Elige a tu hijo",
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
                ChipCabeceraEdu("Tareas y evaluaciones", modifier = Modifier.padding(top = 14.dp), colorCabecera = ColorModulo.Publicaciones)
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
                cargando && hijos.isEmpty() -> TarjetaEdu {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = EduTeal, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                        Text(
                            "Cargando la agenda…", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                            fontSize = 15.sp, color = EduTextoSecundario
                        )
                    }
                }
                hijos.isEmpty() -> TarjetaEdu(
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
                            "Asócialos con el código de estudiante para ver sus tareas y evaluaciones.", fontFamily = Nunito,
                            fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = EduTextoSecundario,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        Indicador("$tareas", "Tareas por hacer", TonoEdu.AMARILLO, Modifier.weight(1f))
                        Indicador("$evaluaciones", "Evaluaciones", TonoEdu.AZUL, Modifier.weight(1f))
                        Indicador("$vencePronto", "Vence pronto", TonoEdu.CORAL, Modifier.weight(1f))
                    }
                    TituloSeccionEdu("Mis hijos")
                    hijos.forEachIndexed { i, hijo ->
                        TarjetaHijoAgenda(hijo, colorAvatar = if (i % 2 == 0) EduAmarillo else EduTealClaro) { onVerAgenda(hijo) }
                    }
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
private fun Indicador(valor: String, etiqueta: String, tono: TonoEdu, modifier: Modifier = Modifier) {
    val c = coloresDeTono(tono)
    Column(
        modifier = modifier
            .background(c.fondo, RoundedCornerShape(18.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 21.sp, color = c.texto)
        Text(
            etiqueta, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp,
            color = c.texto, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}

// Tarjeta de un hijo: tareas pendientes, evaluaciones próximas y lo próximo que vence
@Composable
private fun TarjetaHijoAgenda(hijo: HijoAgendaItem, colorAvatar: Color, onClick: () -> Unit) {
    val iniciales = "${hijo.nombres.firstOrNull() ?: ""}${hijo.apellidos.firstOrNull() ?: ""}".uppercase()
    val dias = diasHastaAgenda(hijo.proximaFecha)
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
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null,
                tint = EduTextoSecundario, modifier = Modifier.size(24.dp)
            )
        }
        Row(
            modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            when (hijo.tareasPendientes) {
                0 -> ChipEdu("Sin tareas pendientes", TonoEdu.VERDE)
                1 -> ChipEdu("1 tarea pendiente", TonoEdu.AMARILLO)
                else -> ChipEdu("${hijo.tareasPendientes} tareas pendientes", TonoEdu.AMARILLO)
            }
            if (hijo.examenesPendientes > 0) {
                ChipEdu(
                    if (hijo.examenesPendientes == 1) "1 evaluación próxima" else "${hijo.examenesPendientes} evaluaciones",
                    TonoEdu.AZUL
                )
            }
        }
        if (hijo.proximoTitulo.isNotBlank()) {
            val urgente = dias != null && dias <= 1L
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .background(EduFondo, RoundedCornerShape(14.dp))
                    .padding(horizontal = 11.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Rounded.Alarm, contentDescription = null,
                    tint = if (urgente) EduCoralOscuro else EduTextoSecundario, modifier = Modifier.size(17.dp)
                )
                Text(
                    "Próximo: ${hijo.proximoTitulo} · ${cuandoVence(hijo.proximaFecha)}",
                    modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp, color = EduEtiqueta, maxLines = 2, overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun SeleccionarHijoAgendaPreview() {
    val hoy = java.time.LocalDate.now()
    EduConnectAppTheme {
        SeleccionarHijoAgendaScreen(
            hijos = listOf(
                HijoAgendaItem(1L, "Angela", "Perez", "2do Grado", "A", "", 1L, 1L, 2, 1, "Maqueta de ecosistemas", hoy.plusDays(1).toString()),
                HijoAgendaItem(2L, "Pedro", "Perez", "1er Grado", "A", "", 1L, 2L, 1, 1, "Resolver la ficha 12", hoy.plusDays(4).toString())
            )
        )
    }
}
