package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class PublicacionAgendaItem(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val cursoNombre: String,
    val docenteNombre: String,
    val fechaEntrega: String,
    val estado: String = "Pendiente",
    val archivoAdjunto: String = ""
)

@Composable
fun AgendaEscolarScreen(
    nombreHijo: String = "",
    gradoNombre: String = "",
    seccionNombre: String = "",
    publicaciones: List<PublicacionAgendaItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerDetalle: (PublicacionAgendaItem) -> Unit = {}
) {
    var tabSeleccionado by remember { mutableStateOf("Tareas") }

    val fechaActual = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
    }

    val hoy = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    val publicacionesFiltradas = publicaciones.filter { item ->
        when (tabSeleccionado) {
            "Tareas" -> item.tipo == "Tarea"
            "Exámenes" -> item.tipo == "Examen"
            else -> true
        }
    }.sortedWith(compareBy(
        { if (it.estado == "Pendiente") 0 else 1 },
        { it.fechaEntrega }
    ))

    val pendientes = publicacionesFiltradas.filter { it.estado.lowercase() == "pendiente" }
    val entregadas = publicacionesFiltradas.filter { it.estado.lowercase() != "pendiente" }

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
                .padding(horizontal = 20.dp, vertical = 20.dp),
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
                        text = "Agenda Escolar",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "$nombreHijo - $gradoNombre Sec. $seccionNombre",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.85f)
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // TABS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Todos", "Tareas", "Exámenes").forEachIndexed { index, tab ->
                    val isActive = tabSeleccionado == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isActive) EduconnectBlue else Color.White)
                            .border(1.5.dp, if (isActive) EduconnectBlue else BorderBlue, RoundedCornerShape(20.dp))
                            .clickable { tabSeleccionado = tab }
                            .padding(horizontal = 25.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = if (isActive) TextWhite else TextPrimary
                        )
                    }
                    if (index < 2) Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                // PENDIENTES
                if (pendientes.isNotEmpty()) {
                    item {
                        Text(
                            text = "PRÓXIMAS - ORDENADO POR FECHA",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextBlue
                        )
                    }
                    items(pendientes) { item ->
                        PublicacionAgendaCard(
                            item = item,
                            hoy = hoy,
                            onClick = { onVerDetalle(item) }
                        )
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BackgroundWhite, RoundedCornerShape(16.dp))
                                .border(1.dp, BorderBlue, RoundedCornerShape(16.dp))
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.agenda_gray),
                                contentDescription = null,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                text = "No hay tareas o eventos",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "programados por el momento.",
                                fontFamily = Roboto,
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // ENTREGADAS
                if (entregadas.isNotEmpty()) {
                    items(entregadas) { item ->
                        EntregadaAgendaCard(item = item)
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
fun PublicacionAgendaCard(
    item: PublicacionAgendaItem,
    hoy: String,
    onClick: () -> Unit
) {
    val diasRestantes = remember(item.fechaEntrega) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechaE = sdf.parse(item.fechaEntrega)!!
            val fechaH = sdf.parse(hoy)!!
            val diff = fechaE.time - fechaH.time
            TimeUnit.MILLISECONDS.toDays(diff).toInt()
        } catch (e: Exception) { 0 }
    }

    val fechaDisplay = remember(item.fechaEntrega) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(item.fechaEntrega)
            SimpleDateFormat("EEE dd 'de' MMMM yyyy", Locale("es", "PE"))
                .format(date!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { item.fechaEntrega }
    }

    val colorBorde = when {
        diasRestantes <= 3 -> StatusErrorRed
        diasRestantes <= 7 -> AccentOrange
        else -> BorderBlue
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(16.dp))
            .border(1.5.dp, AccentOrange, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Título + días restantes
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(AccentOrange, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            id = if (item.tipo == "Tarea") R.drawable.document_text_white
                            else R.drawable.exam_white
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Column {
                    Text(
                        text = item.titulo,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextBlue,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${item.cursoNombre} - Prof. ${item.docenteNombre}",
                        fontFamily = Roboto,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }
            Text(
                text = "$diasRestantes días",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = colorBorde
            )
        }

        // Fecha + estado
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.agenda_orange),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = fechaDisplay,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = TextOrange
                )
            }
            Box(
                modifier = Modifier
                    .background(StatusErrorRed.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Pendiente",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = StatusErrorRed
                )
            }
        }
    }
}

@Composable
fun EntregadaAgendaCard(item: PublicacionAgendaItem) {
    val fechaDisplay = remember(item.fechaEntrega) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(item.fechaEntrega)
            SimpleDateFormat("EEE dd 'de' MMMM", Locale("es", "PE"))
                .format(date!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { item.fechaEntrega }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(16.dp))
            .border(1.8.dp, BorderGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.titulo,
                fontFamily = Roboto,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = TextBlue
            )
            Text(
                text = "${item.cursoNombre} - $fechaDisplay",
                fontFamily = Roboto,
                fontSize = 15.sp,
                color = TextSecondary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.check_green_dark),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Entregada",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = StatusGreen
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AgendaEscolarPreview() {
    EduConnectAppTheme {
        AgendaEscolarScreen(
            nombreHijo = "Carlos",
            gradoNombre = "2do",
            seccionNombre = "A",
            publicaciones = listOf(
                PublicacionAgendaItem(1L, "Ejercicios Cap. 5", "Resolver los 5 ejercicios del 1 al 20 del capítulo 5.", "Tarea", "Matemáticas", "Eduardo Carranza", "2026-05-23", "Pendiente"),
                PublicacionAgendaItem(2L, "Ejercicios Cap. 6", "Resolver los ejercicios del 1 al 10 del cap. 6.", "Tarea", "Matemáticas", "Eduardo Carranza", "2026-05-26", "Pendiente"),
                PublicacionAgendaItem(3L, "Lectura Cap. 8", "Leer capítulo 8.", "Tarea", "Comunicación", "Rosa A.", "2026-05-16", "Entregada")
            )
        )
    }
}
