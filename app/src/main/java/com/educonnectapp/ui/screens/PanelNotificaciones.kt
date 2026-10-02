package com.educonnectapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// tipo: "ASISTENCIA" | "COMUNICADO" | "PUBLICACION" | "CALIFICACION" | "AVISO"
data class NotificacionItem(
    val id: Long,
    val tipo: String,
    val titulo: String,
    val mensaje: String,
    val fecha: String,      // "yyyy-MM-dd" (hora del celular)
    val hora: String,       // "hh:mm a"
    val leida: Boolean,
    val detalle: String = ""   // tareas/exámenes: "Curso · Grado Sec. X · Hijo"
)

// Panel desplegable debajo de la campana (arriba a la derecha), encima de la pantalla actual.
// Tocar fuera del panel lo cierra.
@Composable
fun PanelNotificaciones(
    visible: Boolean,
    notificaciones: List<NotificacionItem> = emptyList(),
    noLeidas: Int = 0,
    cargando: Boolean = false,
    onCerrar: () -> Unit = {},
    onMarcarTodas: () -> Unit = {},
    onAbrir: (NotificacionItem) -> Unit = {}
) {
    // Fondo semitransparente: cierra el panel al tocar fuera
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
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
                    .padding(top = 92.dp, end = 12.dp)   // justo debajo de la campana del encabezado
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 380.dp)
            ) {
                // Flecha que apunta a la campana
                Box(
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .offset(y = 8.dp)
                        .size(16.dp)
                        .rotate(45f)
                        .background(BackgroundWhite)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(16.dp))
                        .background(BackgroundWhite, RoundedCornerShape(16.dp))
                        // evita que un toque dentro del panel lo cierre
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { }
                ) {
                    // CABECERA DEL PANEL
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Notificaciones",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextBlue
                            )
                            if (noLeidas > 0) {
                                Box(
                                    modifier = Modifier
                                        .background(StatusErrorRed, RoundedCornerShape(20.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$noLeidas",
                                        fontFamily = Roboto,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                        if (noLeidas > 0) {
                            Text(
                                text = "Marcar leídas",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = EduconnectBlue,
                                modifier = Modifier
                                    .clickable { onMarcarTodas() }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = BorderBlue.copy(alpha = 0.4f), thickness = 1.dp)

                    // CONTENIDO
                    when {
                        cargando && notificaciones.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(28.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = EduconnectBlue,
                                    modifier = Modifier.size(28.dp),
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                        notificaciones.isEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.notification_gray),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    text = "No tienes notificaciones",
                                    fontFamily = Roboto,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        else -> {
                            LazyColumn(modifier = Modifier.heightIn(max = 420.dp)) {
                                items(notificaciones, key = { it.id }) { item ->
                                    FilaNotificacion(item = item, onClick = { onAbrir(item) })
                                    HorizontalDivider(color = BorderBlue.copy(alpha = 0.25f), thickness = 1.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Ícono y color del recuadro según el tipo de notificación
private fun estiloNotificacion(tipo: String): Pair<Int, Color> = when (tipo) {
    "ASISTENCIA" -> R.drawable.gotoassistance_white to StatusGreen
    "COMUNICADO" -> R.drawable.megaphone_white to EduconnectBlue
    "PUBLICACION" -> R.drawable.publications_white to EduconnectBlueMedium
    "CALIFICACION" -> R.drawable.exam_white to AccentOrange
    else -> R.drawable.notification_white to TextSecondary
}

// "Hoy", "Ayer" o "Lun 28 sep"
private fun fechaRelativa(fecha: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val hoy = sdf.format(Date())
    val ayer = sdf.format(Date(System.currentTimeMillis() - 24L * 60 * 60 * 1000))
    return when (fecha) {
        hoy -> "Hoy"
        ayer -> "Ayer"
        else -> try {
            SimpleDateFormat("EEE dd MMM", Locale("es", "PE"))
                .format(sdf.parse(fecha)!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { fecha }
    }
}

@Composable
private fun FilaNotificacion(item: NotificacionItem, onClick: () -> Unit) {
    val (icono, color) = estiloNotificacion(item.tipo)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (item.leida) BackgroundWhite else EduconnectBlue.copy(alpha = 0.06f))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(color, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icono),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = item.titulo,
                fontFamily = Roboto,
                fontWeight = if (item.leida) FontWeight.Medium else FontWeight.Bold,
                fontSize = 15.sp,
                color = TextBlue,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (item.detalle.isNotEmpty()) {
                Text(
                    text = item.detalle,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = EduconnectBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = item.mensaje,
                fontFamily = Roboto,
                fontSize = 13.sp,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${fechaRelativa(item.fecha)} · ${item.hora}",
                fontFamily = Roboto,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        if (!item.leida) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(9.dp)
                    .background(StatusErrorRed, CircleShape)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
fun PanelNotificacionesPreview() {
    EduConnectAppTheme {
        Box(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
            Box(modifier = Modifier.fillMaxWidth().height(110.dp).background(EduconnectBlue))
            PanelNotificaciones(
                visible = true,
                noLeidas = 2,
                notificaciones = listOf(
                    NotificacionItem(1L, "PUBLICACION", "Nueva tarea", "Ejercicios Cap. 5 - Fracciones. Fecha límite: 23 de mayo.", "2026-10-02", "11:58 a. m.", false, "Matemáticas · 2do Grado Sec. A · Carlos"),
                    NotificacionItem(2L, "COMUNICADO", "Nuevo comunicado", "Reunión de padres el viernes a las 6:00 p. m.", "2026-10-02", "10:20 a. m.", false),
                    NotificacionItem(3L, "ASISTENCIA", "Asistencia registrada", "Carlos llegó tarde a Comunicación.", "2026-10-01", "08:15 a. m.", true)
                )
            )
        }
    }
}
