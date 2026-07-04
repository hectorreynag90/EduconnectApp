package com.educonnectapp.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.StatusErrorRed
import com.educonnectapp.ui.theme.StatusGreen
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Data class para asistencia sin Room
data class AsistenciaItem(
    val id: Long,
    val alumnoId: Long,
    val cursoId: Long,
    val cursoNombre: String,
    val fecha: String,
    val estado: String
)

// Data class para curso sin Room
data class CursoItem2(val id: Long, val nombre: String)

@Composable
fun HistorialAsistenciasScreen(
    alumnoId: Long,
    alumnoNombre: String,
    todasAsistencias: List<AsistenciaItem> = emptyList(),
    listaCursos: List<CursoItem2> = emptyList(),
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerDetalle: (asistenciaId: Long, fecha: String) -> Unit = { _, _ -> }
) {
    val fechaActual = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
    }

    val calActual = remember { Calendar.getInstance() }
    var anioActual by remember { mutableStateOf(calActual.get(Calendar.YEAR)) }
    var mesActual by remember { mutableStateOf(calActual.get(Calendar.MONTH)) }
    var diaSeleccionado by remember { mutableStateOf<Int?>(null) }
    var cursoSeleccionado by remember { mutableStateOf<CursoItem2?>(null) }
    var dropdownCursoExpanded by remember { mutableStateOf(false) }

    val nombresMeses = listOf("Enero","Febrero","Marzo","Abril","Mayo","Junio",
        "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre")

    val mesStr = String.format("%04d-%02d", anioActual, mesActual + 1)

    val asistenciasFiltradas = if (cursoSeleccionado == null) emptyList() else
        todasAsistencias.filter { a ->
            a.fecha.startsWith(mesStr) &&
                    a.cursoId == cursoSeleccionado!!.id &&
                    (diaSeleccionado == null || a.fecha == String.format("%04d-%02d-%02d", anioActual, mesActual + 1, diaSeleccionado))
        }.sortedByDescending { it.fecha }

    val diasPresente = if (cursoSeleccionado == null) emptySet() else todasAsistencias
        .filter { it.fecha.startsWith(mesStr) && it.estado == "A" && it.cursoId == cursoSeleccionado!!.id }
        .mapNotNull { it.fecha.split("-").lastOrNull()?.toIntOrNull() }.toSet()

    val diasAusente = if (cursoSeleccionado == null) emptySet() else todasAsistencias
        .filter { it.fecha.startsWith(mesStr) && it.estado == "F" && it.cursoId == cursoSeleccionado!!.id }
        .mapNotNull { it.fecha.split("-").lastOrNull()?.toIntOrNull() }.toSet()

    val calMes = Calendar.getInstance().apply {
        set(Calendar.YEAR, anioActual)
        set(Calendar.MONTH, mesActual)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val primerDiaSemana = calMes.get(Calendar.DAY_OF_WEEK) - 1
    val totalDiasMes = calMes.getActualMaximum(Calendar.DAY_OF_MONTH)
    val hoyDia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    val hoyMes = Calendar.getInstance().get(Calendar.MONTH)
    val hoyAnio = Calendar.getInstance().get(Calendar.YEAR)

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundLight)
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
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            )
            {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(32.dp)
                        .offset(x = (-5).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver",
                        modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(
                        text = "Historial de Asistencias",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextWhite
                    )
                    Text(
                        text = alumnoNombre,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.9f)
                    )
                    Text(
                        text = fechaActual,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = TextWhite.copy(alpha = 0.8f)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.filter_white),
                        contentDescription = "Filtrar",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(
                    onClick = onNotificaciones
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.notification_white),
                        contentDescription = "Notificaciones",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        // FILTROS + CALENDARIO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "CURSO",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextBlue
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = cursoSeleccionado?.nombre ?: "Seleccionar...",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { dropdownCursoExpanded = true }
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = EduconnectBlue)
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EduconnectBlue,
                        unfocusedBorderColor = EduconnectBlue,
                        focusedTextColor = TextBlue,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = BackgroundWhite,
                        unfocusedContainerColor = BackgroundWhite),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(end = 48.dp)
                    .clickable { dropdownCursoExpanded = true }
                )
                DropdownMenu(
                    expanded = dropdownCursoExpanded,
                    onDismissRequest = { dropdownCursoExpanded = false }
                ) {
                    listaCursos.forEach { curso ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    curso.nombre,
                                    fontFamily = Roboto,
                                    fontSize = 16.sp,
                                    color = TextBlue
                                ) },
                            onClick = { cursoSeleccionado = curso; dropdownCursoExpanded = false; diaSeleccionado = null }
                        )
                    }
                }
            }

            Text(
                text = "CALENDARIO",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextBlue
            )

            // CALENDARIO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                .background(BackgroundWhite, RoundedCornerShape(12.dp))
                .border(1.5.dp, EduconnectBlue, RoundedCornerShape(12.dp))
                .padding(12.dp)) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (mesActual == 0) { mesActual = 11; anioActual-- } else mesActual--
                        diaSeleccionado = null
                    }, modifier = Modifier
                        .size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = EduconnectBlue
                        )
                    }
                    Text(
                        text = "${nombresMeses[mesActual]} $anioActual",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = EduconnectBlue
                    )
                    IconButton(
                        onClick = {
                        if (mesActual == 11) { mesActual = 0; anioActual++ } else mesActual++
                        diaSeleccionado = null
                    }, modifier = Modifier
                        .size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = EduconnectBlue)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Do","Lu","Ma","Mi","Ju","Vi","Sá").forEach { dia ->
                        Text(
                            text = dia,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontFamily = Roboto,
                            fontSize = 14.sp,
                            color = TextBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                val filas = (primerDiaSemana + totalDiasMes + 6) / 7
                for (fila in 0 until filas) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0..6) {
                            val idx = fila * 7 + col
                            val dia = idx - primerDiaSemana + 1
                            val esHoy = dia == hoyDia && mesActual == hoyMes && anioActual == hoyAnio
                            val esSeleccionado = dia == diaSeleccionado
                            val esPresente = dia in diasPresente
                            val esAusente = dia in diasAusente

                            Box(
                                modifier = Modifier
                                .weight(1f)
                                .padding(2.dp).size(32.dp)
                                .background(when {
                                    esSeleccionado -> EduconnectBlue
                                    esPresente -> StatusGreen
                                    esAusente -> StatusErrorRed
                                    esHoy && dia in 1..totalDiasMes -> EduconnectBlue.copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                }, CircleShape)
                                .then(if (dia in 1..totalDiasMes)
                                    Modifier.clickable { diaSeleccionado = if (diaSeleccionado == dia) null else dia }
                                else Modifier),
                                contentAlignment = Alignment.Center) {
                                if (dia in 1..totalDiasMes) {
                                    Text(
                                        text = "$dia",
                                        fontFamily = Roboto,
                                        fontSize = 15.sp,
                                        fontWeight = if (esSeleccionado || esHoy || esPresente || esAusente) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            esSeleccionado || esPresente || esAusente -> TextWhite
                                            esHoy -> EduconnectBlue
                                            else -> TextPrimary
                                        })
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(Pair(StatusGreen, "Presente"),
                        Pair(StatusErrorRed, "Ausente"),
                        Pair(EduconnectBlue, "Hoy/Selec.")).forEach { (color, label) ->
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier.size(12.dp)
                                    .background(color, CircleShape))
                            Text(
                                text = label,
                                fontFamily = Roboto,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // TÍTULO REGISTROS FIJO
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (diaSeleccionado != null) "REGISTRO DEL DÍA $diaSeleccionado" else "REGISTROS DEL MES",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextBlue
            )
            Text(
                text = "${asistenciasFiltradas.size} registro(s)",
                fontFamily = Roboto,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TextPrimary)
        }

        // LISTA REGISTROS SCROLLEABLE
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            if (asistenciasFiltradas.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderBlue, RoundedCornerShape(10.dp))
                    .padding(20.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (cursoSeleccionado == null) "Selecciona un curso para ver registros"
                    else if (diaSeleccionado != null) "Sin registro para el día $diaSeleccionado"
                    else "Sin registros este mes",
                        fontFamily = Roboto,
                        fontSize = 16.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            asistenciasFiltradas.forEach { asistencia ->
                val esPresente = asistencia.estado == "A"
                val colorEstado = if (esPresente) StatusGreen else StatusErrorRed
                val textoEstado = if (esPresente) "Presente" else "Ausente"

                val fechaFormateada = remember(asistencia.fecha) {
                    try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("es", "PE"))
                        val date = sdf.parse(asistencia.fecha)
                        SimpleDateFormat("EEEE dd 'de' MMMM", Locale("es", "PE"))
                            .format(date!!).replaceFirstChar { it.uppercase() }
                    } catch (e: Exception) { asistencia.fecha }
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderBlue, RoundedCornerShape(10.dp))
                    .clickable { onVerDetalle(asistencia.id, asistencia.fecha) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = fechaFormateada,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorEstado
                        )
                        Text(
                            text = asistencia.cursoNombre,
                            fontFamily = Roboto,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(
                            id = if (esPresente) R.drawable.check_green else R.drawable.close_red),
                            contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = textoEstado,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = colorEstado
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.next_gray),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        BottomNavBarPadre(
            onInicio = onHomePadre,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Inicio"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistorialAsistenciasPreview() {
    EduConnectAppTheme {
        HistorialAsistenciasScreen(
            alumnoId = 1L,
            alumnoNombre = "Pedro Sanchez Vega",
            listaCursos = listOf(
                CursoItem2(1L, "Matemáticas"),
                CursoItem2(2L, "Comunicación")
            ),
            todasAsistencias = listOf(
                AsistenciaItem(1L, 1L, 1L, "Matemáticas", "2026-06-12", "A"),
                AsistenciaItem(2L, 1L, 1L, "Matemáticas", "2026-06-11", "F"),
                AsistenciaItem(3L, 1L, 1L, "Matemáticas", "2026-06-10", "A")
            )
        )
    }
}