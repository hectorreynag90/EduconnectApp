package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.*

data class ComunicadoResumenItem(
    val id: Long,
    val asunto: String,
    val mensaje: String = "",
    val fecha: String,
    val totalNotificados: Int,
    val totalLeidos: Int
)

@Composable
fun BusquedaComunicadoScreen(
    gradoNombre: String = "",
    seccionNombre: String = "",
    cursoNombre: String = "",
    comunicados: List<ComunicadoResumenItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerDetalle: (comunicadoId: Long) -> Unit = {},
) {
    val context = LocalContext.current
    var busqueda by remember { mutableStateOf("") }
    var fechaSeleccionada by remember { mutableStateOf("") }
    var tabSeleccionado by remember { mutableStateOf("Todos") }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    if (mostrarDatePicker) {
        val calendario = java.util.Calendar.getInstance()
        android.app.DatePickerDialog(
            context,
            { _, anio, mes, dia ->
                fechaSeleccionada = String.format("%04d-%02d-%02d", anio, mes + 1, dia)
                mostrarDatePicker = false
            },
            calendario.get(java.util.Calendar.YEAR),
            calendario.get(java.util.Calendar.MONTH),
            calendario.get(java.util.Calendar.DAY_OF_MONTH)
        ).also { it.show() }
    }

    val comunicadosFiltrados = comunicados.filter { item ->
        val coincideBusqueda = busqueda.isEmpty() ||
                item.asunto.contains(busqueda, ignoreCase = true)
        val coincideFecha = fechaSeleccionada.isEmpty() || item.fecha == fechaSeleccionada
        val sinLeer = item.totalNotificados - item.totalLeidos
        val coincideTab = when (tabSeleccionado) {
            "Leídos" -> sinLeer == 0
            "Sin Leer" -> sinLeer > 0
            else -> true
        }
        coincideBusqueda && coincideFecha && coincideTab
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
                    modifier = Modifier.size(35.dp).offset(x = (-5).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Búsqueda comunicados",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "$gradoNombre Sec. $seccionNombre - $cursoNombre",
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // BARRA BÚSQUEDA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderBlue, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search_gray),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                BasicTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        fontFamily = Roboto,
                        fontSize = 15.sp,
                        color = TextPrimary
                    ),
                    decorationBox = { inner ->
                        if (busqueda.isEmpty()) {
                            Text(
                                text = "Buscar comunicado...",
                                fontFamily = Roboto,
                                fontSize = 15.sp,
                                color = TextSecondary
                            )
                        }
                        inner()
                    }
                )
            }

            // FILTRO FECHA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderBlue, RoundedCornerShape(12.dp))
                    .clickable { mostrarDatePicker = true }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.agenda_blue),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Fecha",
                            fontFamily = Roboto,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = if (fechaSeleccionada.isEmpty()) "Todas" else fechaSeleccionada,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextBlue
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (fechaSeleccionada.isNotEmpty()) {
                        Text(
                            text = "Limpiar",
                            fontFamily = Roboto,
                            fontSize = 12.sp,
                            color = StatusErrorRed,
                            modifier = Modifier.clickable { fechaSeleccionada = "" }
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.chevrondown_gray),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // TABS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Todos" to "Todos (${comunicados.size})",
                    "Leídos" to "Leídos",
                    "Sin Leer" to "Sin Leer"
                ).forEach { (tab, label) ->
                    val isActive = tabSeleccionado == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isActive && tab != "Sin Leer") EduconnectBlue
                                else Color.White
                            )
                            .border(
                                1.5.dp,
                                if (tab == "Sin Leer" && isActive) StatusErrorRed
                                else if (isActive) EduconnectBlue
                                else BorderBlue,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { tabSeleccionado = tab }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
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

            // LISTA COMUNICADOS
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(comunicadosFiltrados) { item ->
                    ComunicadoResumenCard(
                        item = item,
                        onClick = { onVerDetalle(item.id) }
                    )
                }
                if (comunicadosFiltrados.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontraron comunicados",
                                fontFamily = Roboto,
                                fontSize = 15.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Avisos"
        )
    }
}

@Composable
fun ComunicadoResumenCard(
    item: ComunicadoResumenItem,
    onClick: () -> Unit
) {
    val sinLeer = item.totalNotificados - item.totalLeidos
    val progreso = if (item.totalNotificados > 0)
        item.totalLeidos.toFloat() / item.totalNotificados.toFloat()
    else 0f
    val porcentaje = (progreso * 100).toInt()

    val colorBarra = when {
        sinLeer == 0 -> StatusGreen
        progreso < 0.4f -> StatusErrorRed
        else -> EduconnectBlue
    }
    val colorBorde = when {
        sinLeer == 0 -> StatusGreen
        progreso < 0.4f -> StatusErrorRed
        else -> BorderBlue
    }

    val fechaFormateada = remember(item.fecha) {
        try {
            val hoy = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            if (item.fecha == hoy) return@remember "Hoy"
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val date = sdf.parse(item.fecha)
            java.text.SimpleDateFormat("EEE dd", java.util.Locale("es", "PE"))
                .format(date!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { item.fecha }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(16.dp))
            .border(1.5.dp, colorBorde, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título + fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.asunto,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = fechaFormateada,
                    fontFamily = Roboto,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }

            // Badges leídos / sin leer
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .background(StatusGreen.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.check_green_dark),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (sinLeer == 0) "${item.totalLeidos} todos" else "${item.totalLeidos}",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = StatusGreen
                    )
                }
                if (sinLeer > 0) {
                    Box(
                        modifier = Modifier
                            .background(StatusErrorRed.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$sinLeer",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = StatusErrorRed
                        )
                    }
                }
            }

            // Leídos + porcentaje
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Leídos",
                    fontFamily = Roboto,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = "$porcentaje%",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = colorBarra
                )
            }

            // Barra progreso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(BackgroundLight, RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progreso)
                        .height(6.dp)
                        .background(colorBarra, RoundedCornerShape(3.dp))
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.next_gray),
            contentDescription = null,
            modifier = Modifier.size(30.dp).align(Alignment.CenterVertically)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BusquedaComunicadoPreview() {
    EduConnectAppTheme {
        BusquedaComunicadoScreen(
            gradoNombre = "2do",
            seccionNombre = "A",
            cursoNombre = "Matemáticas",
            comunicados = listOf(
                ComunicadoResumenItem(1L, "Reunión de padres - Vie 23", "Mensaje de prueba", "2026-05-23", 28, 25),
                ComunicadoResumenItem(2L, "Entrega de libretas", "Mensaje de prueba", "2026-05-16", 28, 28),
                ComunicadoResumenItem(3L, "Suspensión de clases", "Mensaje de prueba", "2026-05-12", 28, 10)
            ),
        )
    }
}
