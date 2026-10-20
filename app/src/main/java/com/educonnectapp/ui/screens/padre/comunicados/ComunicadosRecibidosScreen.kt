package com.educonnectapp.ui.screens.padre.comunicados

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
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Notifications
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
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.padre.inicio.BottomNavBarPadre
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

// Comunicado recibido por el padre (lo arma MainActivity)
data class ComunicadoPadreItem(
    val id: Long,
    val asunto: String,
    val mensaje: String,
    val fecha: String,              // "yyyy-MM-dd" (hora local)
    val hora: String,               // "09:40 a. m."
    val docenteNombre: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val cursoNombre: String,
    val leido: Boolean = false,
    val leidoEn: String? = null,    // "yyyy-MM-ddTHH:mm:ss" (hora local)
    // ----- Rediseño 2026 -----
    val adjunto: String = ""        // nombre del archivo adjunto ("" = sin adjunto)
)

internal val LOCALE_PE_COM = Locale("es", "PE")

// "yyyy-MM-dd" -> "hoy" · "ayer" · "vie 16 oct" (compartida por las pantallas de comunicados del padre)
internal fun fechaCortaComunicado(fecha: String): String = try {
    val f = LocalDate.parse(fecha.take(10))
    when (ChronoUnit.DAYS.between(f, LocalDate.now())) {
        0L -> "hoy"
        1L -> "ayer"
        else -> f.format(DateTimeFormatter.ofPattern("EEE d MMM", LOCALE_PE_COM)).replace(".", "")
    }
} catch (e: Exception) { "" }

// =====================================================================
// COMUNICADOS DEL PADRE · LISTA DE UN HIJO (rediseño 2026)
// Cabecera fija granate · pestañas Todos / Sin leer / Leídos · agrupados por Hoy, Esta semana y Anteriores
// =====================================================================

@Composable
fun ComunicadosRecibidosScreen(
    nombreHijo: String = "",
    gradoNombre: String = "",
    seccionNombre: String = "",
    comunicados: List<ComunicadoPadreItem> = emptyList(),   // más recientes primero
    mostrarTodos: Boolean = false,                          // ya no se usa (se deja por compatibilidad)
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerDetalle: (ComunicadoPadreItem) -> Unit = {},
    // ----- Rediseño 2026 -----
    apellidosHijo: String = "",
    cargando: Boolean = false
) {
    var pestana by remember { mutableStateOf(0) }       // 0 = Todos · 1 = Sin leer · 2 = Leídos
    val sinLeer = comunicados.count { !it.leido }
    val leidos = comunicados.size - sinLeer
    val visibles = when (pestana) {
        1 -> comunicados.filter { !it.leido }
        2 -> comunicados.filter { it.leido }
        else -> comunicados
    }
    // Grupos por fecha de envío
    val hoy = LocalDate.now()
    val grupos = visibles.groupBy { c ->
        val dias = try { ChronoUnit.DAYS.between(LocalDate.parse(c.fecha.take(10)), hoy) } catch (e: Exception) { 99L }
        when {
            dias <= 0L -> "Hoy"
            dias <= 7L -> "Esta semana"
            else -> "Anteriores"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Comunicados",
            subtitulo = listOf("$nombreHijo $apellidosHijo".trim(), "$gradoNombre $seccionNombre".trim())
                .filter { it.isNotBlank() }.joinToString(" · "),
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
                    (if (comunicados.size == 1) "1 recibido" else "${comunicados.size} recibidos") +
                            (if (sinLeer == 0) " · al día" else " · $sinLeer sin leer"),
                    modifier = Modifier.padding(top = 14.dp),
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
            Pestanas(
                opciones = listOf("Todos (${comunicados.size})", "Sin leer ($sinLeer)", "Leídos ($leidos)"),
                seleccionada = pestana,
                onElegir = { pestana = it }
            )

            when {
                cargando && comunicados.isEmpty() -> TarjetaEdu {
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
                visibles.isEmpty() -> TarjetaVacia(
                    titulo = when (pestana) {
                        1 -> "Estás al día"
                        2 -> "Aún no hay leídos"
                        else -> "Sin comunicados"
                    },
                    mensaje = when (pestana) {
                        1 -> "Leíste todos los comunicados de $nombreHijo. Te avisaremos cuando llegue uno nuevo."
                        2 -> "Aquí verás los comunicados que ya abriste."
                        else -> "Aún no hay comunicados para $nombreHijo. Te avisaremos cuando llegue uno."
                    },
                    alDia = pestana == 1
                )
                else -> listOf("Hoy", "Esta semana", "Anteriores").forEach { grupo ->
                    val lista = grupos[grupo].orEmpty()
                    if (lista.isNotEmpty()) {
                        Text(
                            grupo.uppercase(), modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                            fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.5.sp,
                            letterSpacing = 0.6.sp, color = EduTextoSecundario.copy(alpha = 0.8f)
                        )
                        lista.forEach { c -> TarjetaComunicadoPadre(c) { onVerDetalle(c) } }
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

// Pestañas Todos / Sin leer / Leídos (la elegida en granate)
@Composable
private fun Pestanas(opciones: List<String>, seleccionada: Int, onElegir: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.5.dp, EduBorde, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        opciones.forEachIndexed { i, texto ->
            val activa = i == seleccionada
            Text(
                texto,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activa) EduCoral else Color.Transparent)
                    .clickable { onElegir(i) }
                    .padding(vertical = 10.dp),
                fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.5.sp,
                color = if (activa) Color.White else EduEtiqueta, textAlign = TextAlign.Center, maxLines = 1
            )
        }
    }
}

@Composable
private fun TarjetaVacia(titulo: String, mensaje: String, alDia: Boolean) {
    TarjetaEdu(relleno = PaddingValues(horizontal = 16.dp, vertical = 18.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconoCirculoEdu(
                if (alDia) Icons.Rounded.CheckCircle else Icons.Rounded.Campaign,
                if (alDia) TonoEdu.VERDE else TonoEdu.GRIS, tamano = 64.dp
            )
            Text(titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp, color = EduTexto)
            Text(
                mensaje, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp,
                lineHeight = 19.sp, color = EduTextoSecundario, textAlign = TextAlign.Center
            )
        }
    }
}

// Tarjeta de un comunicado: asunto, fecha, docente · curso, 2 líneas del mensaje, Nuevo/Leído y adjunto
@Composable
private fun TarjetaComunicadoPadre(c: ComunicadoPadreItem, onClick: () -> Unit) {
    val nuevo = !c.leido
    val forma = RoundedCornerShape(22.dp)
    TarjetaEdu(
        modifier = if (nuevo) Modifier.border(1.5.dp, EduCoral, forma) else Modifier,
        relleno = PaddingValues(horizontal = 14.dp, vertical = 13.dp),
        onClick = onClick
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconoCirculoEdu(Icons.Rounded.Campaign, if (nuevo) TonoEdu.CORAL else TonoEdu.GRIS, tamano = 42.dp)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        c.asunto, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Black,
                        fontSize = 15.5.sp, color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        if (fechaCortaComunicado(c.fecha) == "hoy") c.hora else fechaCortaComunicado(c.fecha),
                        fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = EduTextoSecundario
                    )
                }
                Text(
                    listOf(c.docenteNombre, c.cursoNombre).filter { it.isNotBlank() }.joinToString(" · "),
                    fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.5.sp,
                    color = EduTextoSecundario, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    c.mensaje, modifier = Modifier.padding(top = 4.dp), fontFamily = Nunito,
                    fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, lineHeight = 18.sp, color = EduEtiqueta,
                    maxLines = 2, overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (nuevo) {
                        Text(
                            "Nuevo",
                            modifier = Modifier
                                .background(EduCoral, RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 3.dp),
                            fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 11.5.sp, color = Color.White
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = EduTealOscuro, modifier = Modifier.size(15.dp))
                            Text("Leído", fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = EduTextoSecundario)
                        }
                    }
                    if (c.adjunto.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Rounded.AttachFile, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(15.dp))
                            Text("1 adjunto", fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = EduTextoSecundario)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ComunicadosRecibidosPreview() {
    val hoy = LocalDate.now()
    EduConnectAppTheme {
        ComunicadosRecibidosScreen(
            nombreHijo = "Angela",
            apellidosHijo = "Perez",
            gradoNombre = "2do Grado",
            seccionNombre = "A",
            comunicados = listOf(
                ComunicadoPadreItem(1L, "Reunión de padres este viernes", "Los esperamos el viernes 23 a las 6:00 p. m. en el aula.", hoy.toString(), "09:40 a. m.", "Prof. Hector Reyna", "2do Grado", "A", "Ciencia y Tecnología", false, adjunto = "citacion.pdf"),
                ComunicadoPadreItem(2L, "Salida al museo", "Recuerden enviar la autorización firmada hasta el jueves.", hoy.minusDays(3).toString(), "08:00 a. m.", "Prof. Ana Ruiz", "2do Grado", "A", "Comunicación", false),
                ComunicadoPadreItem(3L, "Uniforme de educación física", "Desde el lunes asistirán con buzo los martes.", hoy.minusDays(12).toString(), "07:30 a. m.", "Prof. Rosa Díaz", "2do Grado", "A", "General", true, "${hoy.minusDays(11)}T11:17:00")
            )
        )
    }
}
