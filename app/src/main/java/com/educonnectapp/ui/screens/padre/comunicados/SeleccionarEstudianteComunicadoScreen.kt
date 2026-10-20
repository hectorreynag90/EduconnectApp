package com.educonnectapp.ui.screens.padre.comunicados

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
import androidx.compose.material.icons.rounded.Campaign
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
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// Hijo con el resumen de sus comunicados (lo arma MainActivity)
data class HijoComunicadoItem(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val seccionId: Long,
    val sinLeer: Int = 0,
    val total: Int = 0,
    // ----- Rediseño 2026 -----
    val ultimoAsunto: String = "",      // asunto del comunicado más reciente
    val ultimaFecha: String = ""        // "yyyy-MM-dd" del más reciente
)

// =====================================================================
// COMUNICADOS DEL PADRE · ELEGIR HIJO (rediseño 2026)
// Cabecera fija granate · resumen general · una tarjeta por hijo (se toca y entra)
// =====================================================================

@Composable
fun SeleccionarEstudianteComunicadoScreen(
    hijos: List<HijoComunicadoItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerComunicados: (hijo: HijoComunicadoItem) -> Unit = {},
    // ----- Rediseño 2026 -----
    cargando: Boolean = false,
    onAsociarHijo: () -> Unit = {}
) {
    val sinLeer = hijos.sumOf { it.sinLeer }
    val total = hijos.sumOf { it.total }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Comunicados",
            subtitulo = "Elige a tu hijo",
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
                if (hijos.isNotEmpty()) {
                    ChipCabeceraEdu(
                        when (sinLeer) {
                            0 -> "Todo leído"
                            1 -> "1 sin leer en total"
                            else -> "$sinLeer sin leer en total"
                        },
                        modifier = Modifier.padding(top = 14.dp),
                        colorCabecera = ColorModulo.Comunicados
                    )
                }
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
                            "Cargando comunicados…", fontFamily = Nunito, fontWeight = FontWeight.Bold,
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
                            "Asócialos con el código de estudiante para recibir sus comunicados.", fontFamily = Nunito,
                            fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = EduTextoSecundario,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    // Resumen general
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        Indicador("$sinLeer", "Sin leer", TonoEdu.CORAL, Modifier.weight(1f))
                        Indicador("${total - sinLeer}", "Leídos", TonoEdu.VERDE, Modifier.weight(1f))
                        Indicador("$total", "Recibidos", TonoEdu.AZUL, Modifier.weight(1f))
                    }
                    TituloSeccionEdu("Mis hijos")
                    hijos.forEachIndexed { i, hijo ->
                        TarjetaHijoComunicado(hijo, colorAvatar = if (i % 2 == 0) EduAmarillo else EduTealClaro) {
                            onVerComunicados(hijo)
                        }
                    }
                }
            }
        }

        BottomNavBarPadre(
            onInicio = onHomePadre,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Avisos"
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
        Text(etiqueta, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.5.sp, color = c.texto, maxLines = 1)
    }
}

// Tarjeta de un hijo: iniciales, nombre, grado, sin leer, recibidos y el último comunicado
@Composable
private fun TarjetaHijoComunicado(hijo: HijoComunicadoItem, colorAvatar: Color, onClick: () -> Unit) {
    val iniciales = "${hijo.nombres.firstOrNull() ?: ""}${hijo.apellidos.firstOrNull() ?: ""}".uppercase()
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
            if (hijo.sinLeer > 0) ChipEdu("${hijo.sinLeer} sin leer", TonoEdu.CORAL, solido = true)
            else ChipEdu("Al día", TonoEdu.VERDE)
            ChipEdu(if (hijo.total == 1) "1 recibido" else "${hijo.total} recibidos", TonoEdu.AZUL)
        }
        if (hijo.ultimoAsunto.isNotBlank()) {
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .background(EduFondo, RoundedCornerShape(14.dp))
                    .padding(horizontal = 11.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.Campaign, contentDescription = null, tint = EduCoral, modifier = Modifier.size(17.dp))
                Text(
                    "Último: ${hijo.ultimoAsunto}" + fechaCortaComunicado(hijo.ultimaFecha).let { if (it.isBlank()) "" else " · $it" },
                    modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp, color = EduEtiqueta, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun SeleccionarEstudianteComunicadoPreview() {
    EduConnectAppTheme {
        SeleccionarEstudianteComunicadoScreen(
            hijos = listOf(
                HijoComunicadoItem(1L, "Angela", "Perez", "2do Grado", "A", 1L, 2, 9, "Reunión de padres este viernes", java.time.LocalDate.now().toString()),
                HijoComunicadoItem(2L, "Pedro", "Perez", "1er Grado", "A", 2L, 0, 6, "Salida al museo", java.time.LocalDate.now().minusDays(1).toString())
            )
        )
    }
}
