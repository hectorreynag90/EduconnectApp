package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.*

data class PadreLecturaItem(
    val padreId: String,
    val nombrePadre: String,
    val nombreHijo: String,
    val leidoEn: String? = null  // null = no leyó
)

@Composable
fun DetalleComunicadoScreen(
    asunto: String = "",
    mensaje: String = "",
    fecha: String = "",
    hora: String = "",
    gradoNombre: String = "",
    seccionNombre: String = "",
    cursoNombre: String = "",
    totalNotificados: Int = 0,
    padres: List<PadreLecturaItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onReenviar: () -> Unit = {},
) {
    var tabSeleccionado by remember { mutableStateOf("Todos") }

    val padresLeidos = padres.filter { it.leidoEn != null }
    val padresSinLeer = padres.filter { it.leidoEn == null }
    val totalLeidos = padresLeidos.size
    val totalSinLeer = padresSinLeer.size

    val progreso = if (totalNotificados > 0)
        totalLeidos.toFloat() / totalNotificados.toFloat()
    else 0f

    val padresFiltrados = when (tabSeleccionado) {
        "Sin Leer" -> padresSinLeer
        else -> padresLeidos
    }

    val fechaActual = remember {
        java.text.SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", java.util.Locale("es", "PE"))
            .format(java.util.Date()).replaceFirstChar { it.uppercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduconnectBlue)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(32.dp).offset(x = (-5).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Detalle Comunicado",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "Ver detalle",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.8f)
                    )
                    Text(
                        text = fechaActual,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = TextWhite.copy(alpha = 0.7f)
                    )
                }
            }
            IconButton(onClick = onNotificaciones) {
                Image(
                    painter = painterResource(id = R.drawable.notification_white),
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(35.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // CARD INFO COMUNICADO
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundWhite, RoundedCornerShape(16.dp))
                        .border(1.5.dp, BorderBlue, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Título
                    Text(
                        text = asunto,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextBlue
                    )

                    // Hora y sección
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.clock_blue),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "$fecha $hora",
                                fontFamily = Roboto,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.students_blue),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "$gradoNombre Sec. $seccionNombre - $totalNotificados",
                                fontFamily = Roboto,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                        }
                    }

                    // Mensaje
                    Text(
                        text = mensaje,
                        fontFamily = Roboto,
                        fontSize = 15.sp,
                        color = TextPrimary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BackgroundBlueLight, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    )

                    // Progreso lectura
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Leido",
                            fontFamily = Roboto,
                            fontSize = 15.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "$totalLeidos / $totalNotificados",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = StatusGreen
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(BackgroundLight, RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progreso)
                                .height(8.dp)
                                .background(StatusGreen, RoundedCornerShape(4.dp))
                        )
                    }
                }
            }

            // TABS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        "Todos" to "Todos ($totalLeidos)",
                        "Sin Leer" to "Sin Leer ($totalSinLeer)"
                    ).forEach { (tab, label) ->
                        val isActive = tabSeleccionado == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isActive && tab == "Todos") EduconnectBlue
                                    else Color.White
                                )
                                .border(
                                    1.5.dp,
                                    if (isActive && tab == "Sin Leer") StatusErrorRed
                                    else if (isActive) EduconnectBlue
                                    else BorderBlue,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { tabSeleccionado = tab }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontFamily = Roboto,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = when {
                                    isActive && tab == "Sin Leer" -> StatusErrorRed
                                    isActive -> TextWhite
                                    else -> TextPrimary
                                }
                            )
                        }
                    }
                }
            }

            // LISTA PADRES
            items(padresFiltrados) { padre ->
                PadreLecturaCard(padre = padre)
            }

            // VER MÁS
            if (padresFiltrados.size > 3) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ ${padresFiltrados.size - 3} más  ↓",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = EduconnectBlue
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // BOTÓN REENVIAR
        if (totalSinLeer > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = onReenviar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sendstatement_white),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            text = "Reenviar a $totalSinLeer sin leer",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextWhite
                        )
                    }
                }
            }
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente ,
            itemActivo = "Avisos"
        )
    }
}

@Composable
fun PadreLecturaCard(padre: PadreLecturaItem) {
    val leyo = padre.leidoEn != null

    // Formatear hora desde timestamp "2026-05-23T09:52:00"
    val horaFormateada = remember(padre.leidoEn) {
        if (padre.leidoEn == null) return@remember ""
        try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val date = sdf.parse(padre.leidoEn)
            java.text.SimpleDateFormat("hh:mm a", java.util.Locale("es", "PE")).format(date!!)
        } catch (e: Exception) {
            try {
                val sdf2 = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                val date2 = sdf2.parse(padre.leidoEn)
                java.text.SimpleDateFormat("hh:mm a", java.util.Locale("es", "PE")).format(date2!!)
            } catch (e2: Exception) { padre.leidoEn }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(12.dp))
            .border(1.5.dp, BorderOrange.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Punto indicador
        Image(
            painter = painterResource(
                id = if (leyo) R.drawable.check_green_dark else R.drawable.close_red
            ),
            contentDescription = null,
            modifier = Modifier.size(if (leyo) 22.dp else 10.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = padre.nombrePadre,
                fontFamily = Roboto,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = TextBlue
            )
            Text(
                text = "Padre de ${padre.nombreHijo}",
                fontFamily = Roboto,
                fontSize = 15.sp,
                color = TextPrimary
            )
        }

        if (leyo) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.eye_green),
                    contentDescription = null,
                    modifier = Modifier.size(21.dp)
                )
                Text(
                    text = horaFormateada,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = StatusGreen
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleComunicadoPreview() {
    EduConnectAppTheme {
        DetalleComunicadoScreen(
            asunto = "Reunión de padres - Vie 23",
            mensaje = "Estimados padres de familia, los invitamos a la reunión del viernes 23 de mayo a las 4:00pm en el aula de 2A...",
            fecha = "Hoy",
            hora = "9:45 AM",
            gradoNombre = "2do",
            seccionNombre = "A",
            cursoNombre = "Matemáticas",
            totalNotificados = 28,
            padres = listOf(
                PadreLecturaItem("1", "Luis Peres", "Carlos", "2026-05-23T09:52:00"),
                PadreLecturaItem("2", "Carlos Castillo", "Maria", "2026-05-23T09:52:00"),
                PadreLecturaItem("3", "Jesus Viera", "Cesar", "2026-05-23T09:52:00"),
                PadreLecturaItem("4", "Ana Torres", "Pedro", null),
                PadreLecturaItem("5", "Rosa Medina", "Luis", null)
            ),
        )
    }
}
