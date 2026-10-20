package com.educonnectapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
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
import java.time.temporal.ChronoUnit
import java.util.Locale

// tipo: "ASISTENCIA" | "COMUNICADO" | "PUBLICACION" | "CALIFICACION" | "AVISO"
data class NotificacionItem(
    val id: Long,
    val tipo: String,
    val titulo: String,
    val mensaje: String,
    val fecha: String,      // "yyyy-MM-dd" (hora del celular)
    val hora: String,       // "hh:mm a"
    val leida: Boolean,
    val detalle: String = ""   // "Curso · Grado Sec. X · Hijo"
)

// =====================================================================
// PANEL DE NOTIFICACIONES (rediseño 2026)
// Se despliega debajo de la campana, encima de la pantalla actual (tocar fuera lo cierra).
// Filtros Todas / Sin leer · agrupadas por Hoy, Ayer y Anteriores · cada tipo con su color ·
// "Marcar leídas" · "Configurar notificaciones"
// =====================================================================

@Composable
fun PanelNotificaciones(
    visible: Boolean,
    notificaciones: List<NotificacionItem> = emptyList(),
    noLeidas: Int = 0,
    cargando: Boolean = false,
    onCerrar: () -> Unit = {},
    onMarcarTodas: () -> Unit = {},
    onAbrir: (NotificacionItem) -> Unit = {},
    // ----- Rediseño 2026 -----
    onConfigurar: () -> Unit = {}
) {
    var soloSinLeer by remember { mutableStateOf(false) }

    // Fondo oscuro: cierra el panel al tocar fuera
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onCerrar() }
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .padding(top = 92.dp, end = 12.dp)   // justo debajo de la campana de la cabecera (misma posición que antes)
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 390.dp)
            ) {
                // Flecha que apunta a la campana
                Box(
                    modifier = Modifier
                        .padding(end = 26.dp)
                        .offset(y = 8.dp)
                        .size(16.dp)
                        .rotate(45f)
                        .background(Color.White, RoundedCornerShape(3.dp))
                )

                val forma = RoundedCornerShape(24.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, forma)
                        .clip(forma)
                        .background(Color.White)
                        // evita que un toque dentro del panel lo cierre
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { }
                ) {
                    // ---------- CABECERA ----------
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Notificaciones", fontFamily = Nunito, fontWeight = FontWeight.Black,
                            fontSize = 18.sp, color = EduTexto
                        )
                        if (noLeidas > 0) {
                            Text(
                                "$noLeidas",
                                modifier = Modifier
                                    .background(EduCoral, RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        if (noLeidas > 0) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EduTealClaro)
                                    .clickable { onMarcarTodas() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(Icons.Rounded.DoneAll, contentDescription = null, tint = EduTealOscuro, modifier = Modifier.size(16.dp))
                                Text("Marcar leídas", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.sp, color = EduTealOscuro)
                            }
                        }
                    }

                    // ---------- FILTROS ----------
                    if (notificaciones.isNotEmpty()) {
                        Row(
                            modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ChipFiltro("Todas (${notificaciones.size})", !soloSinLeer) { soloSinLeer = false }
                            ChipFiltro("Sin leer (${notificaciones.count { !it.leida }})", soloSinLeer) { soloSinLeer = true }
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EduFondo))

                    // ---------- CONTENIDO ----------
                    val visibles = if (soloSinLeer) notificaciones.filter { !it.leida } else notificaciones
                    when {
                        cargando && notificaciones.isEmpty() -> Box(
                            modifier = Modifier.fillMaxWidth().padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = EduTeal, modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                        }
                        visibles.isEmpty() -> PanelVacio(
                            titulo = if (notificaciones.isEmpty()) "No tienes notificaciones" else "Estás al día",
                            mensaje = if (notificaciones.isEmpty()) "Aquí verás los avisos de asistencia, comunicados y tareas."
                            else "No tienes notificaciones sin leer. Te avisaremos cuando llegue algo nuevo."
                        )
                        else -> {
                            // Agrupadas por fecha (en el orden en que llegan: más recientes primero)
                            val grupos = visibles.groupBy { grupoFecha(it.fecha) }
                            LazyColumn(modifier = Modifier.heightIn(max = 440.dp)) {
                                listOf("Hoy", "Ayer", "Anteriores").forEach { grupo ->
                                    val lista = grupos[grupo].orEmpty()
                                    if (lista.isNotEmpty()) {
                                        item(key = "g_$grupo") {
                                            Text(
                                                grupo.uppercase(),
                                                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, bottom = 4.dp),
                                                fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 11.5.sp,
                                                letterSpacing = 0.6.sp, color = EduTextoSecundario.copy(alpha = 0.8f)
                                            )
                                        }
                                        items(lista, key = { it.id }) { n ->
                                            FilaNotificacion(n, mostrarFecha = grupo == "Anteriores") { onAbrir(n) }
                                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EduFondo))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ---------- PIE ----------
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConfigurar() }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Settings, contentDescription = null, tint = EduAzulOscuro, modifier = Modifier.size(17.dp))
                        Text(
                            "Configurar notificaciones", modifier = Modifier.padding(start = 6.dp),
                            fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.5.sp, color = EduAzulOscuro
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipFiltro(texto: String, activo: Boolean, onClick: () -> Unit) {
    Text(
        texto,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (activo) EduTeal else Color.White)
            .border(1.5.dp, if (activo) EduTeal else EduBorde, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.5.sp,
        color = if (activo) Color.White else EduEtiqueta
    )
}

@Composable
private fun PanelVacio(titulo: String, mensaje: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconoCirculoEdu(Icons.Rounded.DoneAll, TonoEdu.VERDE, tamano = 60.dp)
        Text(titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.sp, color = EduTexto)
        Text(
            mensaje, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
            lineHeight = 18.sp, color = EduTextoSecundario, textAlign = TextAlign.Center
        )
    }
}

// Estilo de cada tipo: ícono, tono y etiqueta
private data class EstiloNotificacion(val icono: ImageVector, val tono: TonoEdu, val etiqueta: String)

private fun estiloNotificacion(n: NotificacionItem): EstiloNotificacion {
    val titulo = n.titulo.lowercase()
    return when (n.tipo) {
        "ASISTENCIA" -> EstiloNotificacion(Icons.Rounded.FactCheck, TonoEdu.AZUL, "ASISTENCIA")
        "COMUNICADO" -> EstiloNotificacion(Icons.Rounded.Campaign, TonoEdu.CORAL, "COMUNICADO")
        "PUBLICACION" ->
            if ("evaluaci" in titulo || "examen" in titulo) EstiloNotificacion(Icons.Rounded.Event, TonoEdu.AZUL, "EVALUACIÓN")
            else EstiloNotificacion(Icons.Rounded.Assignment, TonoEdu.AMARILLO, "TAREA")
        "CALIFICACION" ->
            if ("no entregad" in titulo) EstiloNotificacion(Icons.Rounded.Close, TonoEdu.CORAL, "ENTREGA")
            else EstiloNotificacion(Icons.Rounded.Check, TonoEdu.VERDE, "ENTREGA")
        else -> EstiloNotificacion(Icons.Rounded.Notifications, TonoEdu.GRIS, "AVISO")
    }
}

// Quita los emojis y espacios repetidos del título ("Tarea entregada ✅: X" -> "Tarea entregada: X")
private fun limpiarTitulo(titulo: String): String {
    val sb = StringBuilder()
    var i = 0
    while (i < titulo.length) {
        val cp = titulo.codePointAt(i)
        val esEmoji = Character.getType(cp) == Character.OTHER_SYMBOL.toInt() ||
                Character.getType(cp) == Character.SURROGATE.toInt() ||
                cp == 0xFE0F || cp == 0x200D || cp in 0x1F000..0x1FAFF || cp in 0x2600..0x27BF
        if (!esEmoji) sb.appendCodePoint(cp)
        i += Character.charCount(cp)
    }
    return sb.toString().replace(Regex("\\s+:"), ":").replace(Regex("\\s{2,}"), " ").trim()
}

// "Hoy" · "Ayer" · "Anteriores"
private fun grupoFecha(fecha: String): String = try {
    when (ChronoUnit.DAYS.between(LocalDate.parse(fecha.take(10)), LocalDate.now())) {
        0L -> "Hoy"
        1L -> "Ayer"
        else -> "Anteriores"
    }
} catch (e: Exception) { "Anteriores" }

// "vie 16 oct"
private fun fechaCortaNotif(fecha: String): String = try {
    LocalDate.parse(fecha.take(10)).format(DateTimeFormatter.ofPattern("EEE d MMM", Locale("es", "PE"))).replace(".", "")
} catch (e: Exception) { fecha }

@Composable
private fun FilaNotificacion(n: NotificacionItem, mostrarFecha: Boolean, onClick: () -> Unit) {
    val estilo = estiloNotificacion(n)
    val c = coloresDeTono(estilo.tono)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (n.leida) Color.White else EduTealClaro.copy(alpha = 0.45f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        IconoCirculoEdu(estilo.icono, estilo.tono, tamano = 40.dp)
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    estilo.etiqueta, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Black,
                    fontSize = 11.sp, letterSpacing = 0.5.sp, color = c.texto
                )
                Text(
                    if (mostrarFecha) "${fechaCortaNotif(n.fecha)} · ${n.hora}" else n.hora,
                    fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 11.5.sp, color = EduTextoSecundario
                )
            }
            Text(
                limpiarTitulo(n.titulo), fontFamily = Nunito,
                fontWeight = if (n.leida) FontWeight.ExtraBold else FontWeight.Black,
                fontSize = 14.5.sp, color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            if (n.detalle.isNotBlank()) {
                Text(
                    n.detalle, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.5.sp,
                    color = EduTextoSecundario, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            if (n.mensaje.isNotBlank()) {
                Text(
                    n.mensaje, modifier = Modifier.padding(top = 2.dp), fontFamily = Nunito,
                    fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, lineHeight = 17.sp, color = EduEtiqueta,
                    maxLines = 2, overflow = TextOverflow.Ellipsis
                )
            }
        }
        // Punto de "sin leer"
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(9.dp)
                .background(if (n.leida) Color.Transparent else EduCoral, CircleShape)
        )
    }
}

@Preview(showBackground = true, heightDp = 760)
@Composable
fun PanelNotificacionesPreview() {
    val hoy = LocalDate.now()
    EduConnectAppTheme {
        Box(modifier = Modifier.fillMaxSize().background(EduFondo)) {
            Box(modifier = Modifier.fillMaxWidth().height(110.dp).background(EduTeal))
            PanelNotificaciones(
                visible = true,
                noLeidas = 3,
                notificaciones = listOf(
                    NotificacionItem(1L, "CALIFICACION", "Tarea entregada ✅: Nueva tarea", "El docente registró que Pedro entregó la tarea.", hoy.toString(), "02:32 p. m.", false, "Ciencia y Tecnología · 1er Sec. A · Pedro"),
                    NotificacionItem(2L, "PUBLICACION", "Nueva tarea: Maqueta de ecosistemas", "Entrega: 20/10/2026", hoy.toString(), "02:31 p. m.", false, "Ciencia y Tecnología · 2do Sec. A · Angela"),
                    NotificacionItem(3L, "COMUNICADO", "Nuevo comunicado: Reunión viernes", "Reunión de padres el viernes a las 6:00 p. m.", hoy.toString(), "01:30 p. m.", false),
                    NotificacionItem(4L, "ASISTENCIA", "Asistencia registrada", "Angela llegó tarde a Comunicación.", hoy.minusDays(1).toString(), "10:02 a. m.", true),
                    NotificacionItem(5L, "PUBLICACION", "Aviso de evaluación: Fracciones", "Fecha: 26/10/2026", hoy.minusDays(4).toString(), "04:15 p. m.", true)
                )
            )
        }
    }
}
