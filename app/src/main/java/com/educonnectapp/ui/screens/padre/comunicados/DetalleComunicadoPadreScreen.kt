package com.educonnectapp.ui.screens.padre.comunicados

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.educonnectapp.ui.screens.padre.inicio.BottomNavBarPadre
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// =====================================================================
// COMUNICADOS DEL PADRE · DETALLE (rediseño 2026)
// Cabecera fija granate · curso y grado · asunto · docente con fecha y hora · mensaje ·
// adjunto con "Abrir" · franja turquesa "Leído ..."
// =====================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetalleComunicadoPadreScreen(
    comunicado: ComunicadoPadreItem? = null,
    nombreAdjunto: String? = null,          // null = el comunicado no tiene adjunto
    descargandoAdjunto: Boolean = false,
    onAbrirAdjunto: () -> Unit = {},
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    // ----- Rediseño 2026 -----
    nombreHijo: String = ""                 // "Angela Perez" (subtítulo de la cabecera)
) {
    val grado = "${comunicado?.gradoNombre ?: ""} ${comunicado?.seccionNombre ?: ""}".trim()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Comunicado",
            subtitulo = listOf(nombreHijo, grado).filter { it.isNotBlank() }.joinToString(" · ")
                .ifBlank { "I.E. Pedro M. Ureña" },
            onVolver = onBack,
            colorFondo = ColorModulo.Comunicados,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Comunicados
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
            if (comunicado == null) {
                TarjetaEdu {
                    Text(
                        "No se encontró el comunicado.", fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = EduTextoSecundario
                    )
                }
            } else {

                // ---------- COMUNICADO ----------
                TarjetaEdu(relleno = PaddingValues(16.dp)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ChipEdu(comunicado.cursoNombre.ifBlank { "General" }, TonoEdu.CORAL)
                        if (grado.isNotBlank()) ChipEdu(grado, TonoEdu.AZUL)
                    }
                    Text(
                        comunicado.asunto, modifier = Modifier.padding(top = 10.dp), fontFamily = Nunito,
                        fontWeight = FontWeight.Black, fontSize = 20.sp, lineHeight = 25.sp, color = EduTexto
                    )
                    // Docente que lo envió, con fecha y hora
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).background(EduAmarillo, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                inicialesDe(comunicado.docenteNombre), fontFamily = Nunito, fontWeight = FontWeight.Black,
                                fontSize = 14.sp, color = EduTexto
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                comunicado.docenteNombre.ifBlank { "Docente" }, fontFamily = Nunito,
                                fontWeight = FontWeight.Black, fontSize = 14.5.sp, color = EduTexto,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                listOf(fechaLargaComunicado(comunicado.fecha), comunicado.hora)
                                    .filter { it.isNotBlank() }.joinToString(" · "),
                                fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario
                            )
                        }
                    }
                    Box(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth().height(1.dp).background(EduFondo))
                    Text(
                        comunicado.mensaje, fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                        fontSize = 14.5.sp, lineHeight = 22.sp, color = EduTexto
                    )
                }

                // ---------- ADJUNTO ----------
                if (nombreAdjunto != null) {
                    TituloSeccionEdu("Adjunto")
                    TarjetaEdu(
                        relleno = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        onClick = if (descargandoAdjunto) null else onAbrirAdjunto
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconoCirculoEdu(Icons.Rounded.Description, TonoEdu.AZUL)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    nombreAdjunto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.5.sp,
                                    color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    if (descargandoAdjunto) "Descargando…" else "Toca para abrir",
                                    fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario
                                )
                            }
                            if (descargandoAdjunto) {
                                CircularProgressIndicator(color = EduAzulOscuro, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                            } else {
                                // Botón secundario "Abrir"
                                Row(
                                    modifier = Modifier
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(Color.White)
                                        .border(2.dp, EduBorde, RoundedCornerShape(13.dp))
                                        .clickable(onClick = onAbrirAdjunto)
                                        .padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Rounded.Visibility, contentDescription = null, tint = EduEtiqueta, modifier = Modifier.size(16.dp))
                                    Text("Abrir", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp, color = EduEtiqueta)
                                }
                            }
                        }
                        if (descargandoAdjunto) {
                            LinearProgressIndicator(
                                modifier = Modifier.padding(top = 10.dp).fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                                color = EduAzulOscuro,
                                trackColor = EduAzulOscuroClaro
                            )
                        }
                    }
                }

                // ---------- LEÍDO ----------
                if (comunicado.leido) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EduTealClaro, RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = EduTealOscuro, modifier = Modifier.size(22.dp))
                        Text(
                            textoLeido(comunicado.leidoEn), modifier = Modifier.weight(1f), fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold, fontSize = 13.5.sp, color = EduTealOscuro
                        )
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

// "Prof. Hector Reyna" -> "HR" (sin contar "Prof.")
private fun inicialesDe(nombre: String): String =
    nombre.split(" ").filter { it.isNotBlank() && !it.endsWith(".") }.take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("").ifBlank { "D" }

// "2026-10-19" -> "Lunes 19 de octubre"
private fun fechaLargaComunicado(fecha: String): String = try {
    LocalDate.parse(fecha.take(10)).format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", LOCALE_PE_COM))
        .replaceFirstChar { it.uppercase() }
} catch (e: Exception) { fecha }

// "Leído hoy a las 11:17 a. m." · "Leído ayer a las ..." · "Leído el lunes 12 de octubre a las ..."
private fun textoLeido(leidoEn: String?): String {
    val f = try { leidoEn?.let { LocalDateTime.parse(it.take(19)) } } catch (e: Exception) { null }
        ?: return "Comunicado leído"
    val hora = f.format(DateTimeFormatter.ofPattern("hh:mm a", LOCALE_PE_COM))
    return when (ChronoUnit.DAYS.between(f.toLocalDate(), LocalDate.now())) {
        0L -> "Leído hoy a las $hora"
        1L -> "Leído ayer a las $hora"
        else -> "Leído el " + f.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", LOCALE_PE_COM)) + " a las $hora"
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DetalleComunicadoPadrePreview() {
    val hoy = LocalDate.now()
    EduConnectAppTheme {
        DetalleComunicadoPadreScreen(
            comunicado = ComunicadoPadreItem(
                id = 1L,
                asunto = "Reunión de padres este viernes",
                mensaje = "Estimados padres de familia:\n\nLos esperamos el viernes 23 a las 6:00 p. m. en el aula de 2° A. " +
                        "Revisaremos el avance del bimestre.\n\nSu asistencia es muy importante.",
                fecha = hoy.toString(),
                hora = "09:40 a. m.",
                docenteNombre = "Prof. Hector Reyna",
                gradoNombre = "2do Grado",
                seccionNombre = "A",
                cursoNombre = "Ciencia y Tecnología",
                leido = true,
                leidoEn = "${hoy}T11:17:00"
            ),
            nombreAdjunto = "citacion_reunion.pdf",
            nombreHijo = "Angela Perez"
        )
    }
}
