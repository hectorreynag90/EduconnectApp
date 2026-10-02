package com.educonnectapp.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.AccentOrangeLight
import com.educonnectapp.ui.theme.AsistentTardanza
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.BorderGreen
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite
import java.util.Calendar

private val VerdeAsistencia  = Color(0xFF26C281)
private val RojoFalta        = Color(0xFFE53935)
private val AmarilloTardanza = Color(0xFFF59E0B)
private val BgVerde          = Color(0xFFE8F8F2)
private val BgRojo           = Color(0xFFFDECEC)
private val BgAmarillo       = Color(0xFFFFF8E1)

/** Modelo de un registro de asistencia en el historial */
data class HistorialItem(
    val id: Long,
    val curso: String,
    val grado: String,
    val seccion: String,
    val fecha: String,          // "yyyy-MM-dd"
    val fechaDisplay: String,   // "Martes 17 de septiembre 2026"
    val hora: String,           // "10:05 a.m."
    val presentes: Int,
    val ausentes: Int,
    val total: Int,
    val tardanzas: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialAsistenciaScreen(
    curso: String,
    grado: String,
    seccion: String,
    historial: List<HistorialItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerDetalle: (HistorialItem) -> Unit = {}
) {
    // ESTADO FILTRO FECHA
    var mostrarFiltro by remember { mutableStateOf(false) }
    var fechaDesde    by remember { mutableStateOf("") }
    var fechaHasta    by remember { mutableStateOf("") }
    var labelDesde    by remember { mutableStateOf("Desde") }
    var labelHasta    by remember { mutableStateOf("Hasta") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val historialFiltrado = remember(historial, fechaDesde, fechaHasta) {
        historial.filter { item ->
            when {
                // Ambas fechas → rango
                fechaDesde.isNotEmpty() && fechaHasta.isNotEmpty() ->
                    item.fecha >= fechaDesde && item.fecha <= fechaHasta
                // Solo Desde → día exacto
                fechaDesde.isNotEmpty() ->
                    item.fecha == fechaDesde
                // Solo Hasta → día exacto
                fechaHasta.isNotEmpty() ->
                    item.fecha == fechaHasta
                // Sin filtro
                else -> true
            }
        }
    }
    val filtroActivo = fechaDesde.isNotEmpty() || fechaHasta.isNotEmpty()

    val totalRegistros     = historialFiltrado.size
    val totalAlumnosHistorial = historialFiltrado.sumOf { it.total }
    // Asistencia = Presentes + Tardanzas (la tardanza cuenta como asistió)
    val promedioAsistencia = if (totalAlumnosHistorial > 0) {
        historialFiltrado.sumOf { it.presentes + it.tardanzas }.toFloat() / totalAlumnosHistorial.toFloat() * 100
    } else 0f
    val promedioFaltas    = if (totalAlumnosHistorial > 0) {
        historialFiltrado.sumOf { it.ausentes }.toFloat() / totalAlumnosHistorial.toFloat() * 100
    } else 0f
    val promedioTardanzas = if (totalAlumnosHistorial > 0) {
        historialFiltrado.sumOf { it.tardanzas }.toFloat() / totalAlumnosHistorial.toFloat() * 100
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // HEADER AZUL
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduconnectBlue)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        text = "Historial de Asistencias",
                        fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, color = TextWhite
                    )
                    Text(
                        text = curso,
                        fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp, color = TextWhite
                    )
                    Text(
                        text = "$grado - Sec. $seccion",
                        fontFamily = Roboto, fontWeight = FontWeight.Normal,
                        fontSize = 15.sp, color = TextWhite
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

        // CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // CHIPS DE RESUMEN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ResumenChip(
                    valor = "$totalRegistros",
                    etiqueta = "REGISTROS",
                    colorValor = TextBlue,
                    modifier = Modifier.weight(1f)
                )
                ResumenChip(
                    valor = "${promedioAsistencia.toInt()}%",
                    etiqueta = "ASISTENCIA",
                    colorValor = BorderGreen,
                    modifier = Modifier.weight(1f)
                )
                ResumenChip(
                    valor = "${promedioTardanzas.toInt()}%",
                    etiqueta = "TARDANZAS",
                    colorValor = AccentOrange,
                    modifier = Modifier.weight(1f)
                )
                ResumenChip(
                    valor = "${promedioFaltas.toInt()}%",
                    etiqueta = "FALTAS",
                    colorValor = RojoFalta,
                    modifier = Modifier.weight(1f)
                )
            }

            // ENCABEZADO LISTA + BOTÓN FILTRAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REGISTROS",
                    fontFamily = Roboto, fontWeight = FontWeight.Bold,
                    fontSize = 16.sp, color = TextBlue
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (filtroActivo) EduconnectBlue else BackgroundWhite,
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                1.dp,
                                if (filtroActivo) EduconnectBlue else BorderBlue,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { mostrarFiltro = true }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = if (filtroActivo) TextWhite else TextBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Filtrar",
                                fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = if (filtroActivo) TextWhite else TextBlue
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(EduconnectBlue, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$totalRegistros",
                            fontFamily = Roboto, fontWeight = FontWeight.Bold,
                            fontSize = 15.sp, color = TextWhite
                        )
                    }
                }
            }

            // CHIP FILTRO ACTIVO
            if (filtroActivo) {
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE3F0FB), RoundedCornerShape(20.dp))
                        .border(1.dp, EduconnectBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = buildString {
                            append("Filtro: ")
                            if (fechaDesde.isNotEmpty()) append(labelDesde)
                            if (fechaDesde.isNotEmpty() && fechaHasta.isNotEmpty()) append(" — ")
                            if (fechaHasta.isNotEmpty()) append(labelHasta)
                        },
                        fontFamily = Roboto, fontWeight = FontWeight.Medium,
                        fontSize = 12.sp, color = TextBlue
                    )
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Limpiar filtro",
                        tint = TextBlue,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable {
                                fechaDesde = ""; fechaHasta = ""
                                labelDesde = "Desde"; labelHasta = "Hasta"
                            }
                    )
                }
            }

            // LISTA SCROLLEABLE
            if (historialFiltrado.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_no_records),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (filtroActivo)
                                "No hay registros en el rango seleccionado."
                            else
                                "No hay registros de asistencia aún.",
                            fontFamily = Roboto, fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    historialFiltrado.forEach { item ->
                        HistorialCard(
                            item = item,
                            onClick = { onVerDetalle(item) }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Alumnos"
        )
    }

    // BOTTOM SHEET FILTRO
    if (mostrarFiltro) {
        ModalBottomSheet(
            onDismissRequest = { mostrarFiltro = false },
            sheetState = sheetState,
            containerColor = BackgroundWhite
        ) {
            FiltroFechaSheet(
                labelDesde = labelDesde,
                labelHasta = labelHasta,
                fechaDesdeActual = fechaDesde,
                fechaHastaActual = fechaHasta,
                onAplicar = { desde, hasta, lDesde, lHasta ->
                    fechaDesde = desde
                    fechaHasta = hasta
                    labelDesde = lDesde
                    labelHasta = lHasta
                    mostrarFiltro = false
                },
                onLimpiar = {
                    fechaDesde = ""; fechaHasta = ""
                    labelDesde = "Desde"; labelHasta = "Hasta"
                    mostrarFiltro = false
                }
            )
        }
    }
}

@Composable
private fun FiltroFechaSheet(
    labelDesde: String,
    labelHasta: String,
    fechaDesdeActual: String,
    fechaHastaActual: String,
    onAplicar: (String, String, String, String) -> Unit,
    onLimpiar: () -> Unit
) {
    val context = LocalContext.current

    var tempDesde by remember { mutableStateOf(fechaDesdeActual) }
    var tempHasta by remember { mutableStateOf(fechaHastaActual) }
    var lDesde    by remember { mutableStateOf(if (labelDesde == "Desde") "" else labelDesde) }
    var lHasta    by remember { mutableStateOf(if (labelHasta == "Hasta") "" else labelHasta) }

    fun formatLabel(y: Int, m: Int, d: Int) = "%02d/%02d/%04d".format(d, m + 1, y)
    fun formatKey(y: Int, m: Int, d: Int)   = "%04d-%02d-%02d".format(y, m + 1, d)

    fun openDesde() {
        val cal = Calendar.getInstance()
        DatePickerDialog(context, { _, y, m, d ->
            tempDesde = formatKey(y, m, d)
            lDesde    = formatLabel(y, m, d)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun openHasta() {
        val cal = Calendar.getInstance()
        DatePickerDialog(context, { _, y, m, d ->
            tempHasta = formatKey(y, m, d)
            lHasta    = formatLabel(y, m, d)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Filtrar por fecha",
            fontFamily = Roboto, fontWeight = FontWeight.Bold,
            fontSize = 18.sp, color = TextBlue
        )

        // Campo DESDE
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "DESDE", fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp, color = TextSecondary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(10.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(10.dp))
                    .clickable { openDesde() }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (lDesde.isEmpty()) "Seleccionar fecha" else lDesde,
                    fontFamily = Roboto, fontSize = 15.sp,
                    color = if (lDesde.isEmpty()) TextSecondary else TextBlue
                )
                Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null,
                    tint = EduconnectBlue, modifier = Modifier.size(20.dp))
            }
        }

        // Campo HASTA
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "HASTA", fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp, color = TextSecondary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(10.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(10.dp))
                    .clickable { openHasta() }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (lHasta.isEmpty()) "Seleccionar fecha" else lHasta,
                    fontFamily = Roboto, fontSize = 15.sp,
                    color = if (lHasta.isEmpty()) TextSecondary else TextBlue
                )
                Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null,
                    tint = EduconnectBlue, modifier = Modifier.size(20.dp))
            }
        }

        // Botones Limpiar / Aplicar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onLimpiar,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, EduconnectBlue)
            ) {
                Text(text = "Limpiar", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                    fontSize = 16.sp, color = EduconnectBlue)
            }
            Button(
                onClick = {
                    onAplicar(
                        tempDesde, tempHasta,
                        if (lDesde.isEmpty()) "Desde" else lDesde,
                        if (lHasta.isEmpty()) "Hasta" else lHasta
                    )
                },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EduconnectBlue)
            ) {
                Text(text = "Aplicar", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                    fontSize = 16.sp, color = TextWhite)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ResumenChip(
    valor: String,
    etiqueta: String,
    colorValor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(BackgroundWhite, RoundedCornerShape(10.dp))
            .border(1.dp, BorderBlue, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = valor, fontFamily = Roboto, fontWeight = FontWeight.Bold,
            fontSize = 20.sp, color = colorValor)
        Text(text = etiqueta, fontFamily = Roboto, fontWeight = FontWeight.Bold,
            fontSize = 11.sp, color = TextSecondary, letterSpacing = 0.5.sp)
    }
}

@Composable
private fun HistorialCard(
    item: HistorialItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(12.dp))
            .border(1.5.dp, BorderBlue, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFFFF5ED), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.agenda_blue),
                contentDescription = null, modifier = Modifier.size(35.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.curso, fontFamily = Roboto, fontWeight = FontWeight.Bold,
                fontSize = 16.sp, color = TextBlue,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "${item.grado} — Sec. ${item.seccion}",
                fontFamily = Roboto, fontWeight = FontWeight.Bold,
                fontSize = 14.sp, color = TextSecondary)
            Text(text = item.fechaDisplay, fontFamily = Roboto, fontWeight = FontWeight.Medium,
                fontSize = 13.sp, color = TextBlue)
        }

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = item.hora, fontFamily = Roboto, fontWeight = FontWeight.Normal,
                fontSize = 13.sp, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier
                    .background(BgVerde, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text(text = "✓ ${item.presentes}", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BorderGreen)
                }
                if (item.tardanzas > 0) {
                    Box(modifier = Modifier
                        .background(AsistentTardanza, RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)) {
                        Text(text = "⚠ ${item.tardanzas}", fontFamily = Roboto,
                            fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AccentOrange)
                    }
                }
                Box(modifier = Modifier
                    .background(BgRojo, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text(text = "✗ ${item.ausentes}", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RojoFalta)
                }
            }
            Text(text = "›", fontFamily = Roboto, fontSize = 22.sp, color = TextSecondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistorialAsistenciaPreview() {
    EduConnectAppTheme {
        HistorialAsistenciaScreen(
            curso = "Matemáticas",
            grado = "1er Grado",
            seccion = "A",
            historial = listOf(
                HistorialItem(1L, "Matemáticas", "1er Grado", "A",
                    "2026-09-17", "Martes 17 de septiembre 2026", "10:05 a.m.", 26, 2, 30, tardanzas = 2),
                HistorialItem(2L, "Matemáticas", "1er Grado", "A",
                    "2026-09-12", "Jueves 12 de septiembre 2026", "09:58 a.m.", 25, 5, 30, tardanzas = 0),
                HistorialItem(3L, "Matemáticas", "1er Grado", "A",
                    "2026-09-10", "Martes 10 de septiembre 2026", "10:02 a.m.", 30, 0, 30, tardanzas = 0),
                HistorialItem(4L, "Matemáticas", "1er Grado", "A",
                    "2026-09-05", "Jueves 05 de septiembre 2026", "10:10 a.m.", 25, 3, 30, tardanzas = 2),
            )
        )
    }
}
