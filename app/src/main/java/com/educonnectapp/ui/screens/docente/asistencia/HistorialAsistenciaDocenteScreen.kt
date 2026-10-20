package com.educonnectapp.ui.screens.docente.asistencia

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.coloresDeTono
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduCampo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.util.Calendar

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

// =====================================================================
// HISTORIAL DE ASISTENCIA de una clase (rediseño 2026)
// Cabecera fija azul oscuro · resumen (registros y %) · filtro por fecha · lista de días
// =====================================================================

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
                fechaDesde.isNotEmpty() -> item.fecha == fechaDesde
                // Solo Hasta → día exacto
                fechaHasta.isNotEmpty() -> item.fecha == fechaHasta
                // Sin filtro
                else -> true
            }
        }
    }
    val filtroActivo = fechaDesde.isNotEmpty() || fechaHasta.isNotEmpty()

    val totalRegistros = historialFiltrado.size
    val totalAlumnosHistorial = historialFiltrado.sumOf { it.total }
    // Asistencia = Presentes + Tardanzas (la tardanza cuenta como asistió)
    val promedioAsistencia = if (totalAlumnosHistorial > 0) {
        historialFiltrado.sumOf { it.presentes + it.tardanzas }.toFloat() / totalAlumnosHistorial.toFloat() * 100
    } else 0f
    val promedioFaltas = if (totalAlumnosHistorial > 0) {
        historialFiltrado.sumOf { it.ausentes }.toFloat() / totalAlumnosHistorial.toFloat() * 100
    } else 0f
    val promedioTardanzas = if (totalAlumnosHistorial > 0) {
        historialFiltrado.sumOf { it.tardanzas }.toFloat() / totalAlumnosHistorial.toFloat() * 100
    } else 0f

    val textoFiltro = buildString {
        if (fechaDesde.isNotEmpty()) append(labelDesde)
        if (fechaDesde.isNotEmpty() && fechaHasta.isNotEmpty()) append(" — ")
        if (fechaHasta.isNotEmpty()) append(labelHasta)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = curso,
            subtitulo = "Historial · $grado $seccion",
            onVolver = onBack,
            colorFondo = ColorModulo.Asistencia,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Asistencia
                )
            },
            contenidoInferior = {
                ChipCabeceraEdu(
                    if (totalRegistros == 1) "1 registro" else "$totalRegistros registros",
                    modifier = Modifier.padding(top = 14.dp),
                    colorCabecera = ColorModulo.Asistencia
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
            // RESUMEN
            TituloSeccionEdu(if (filtroActivo) "Resumen del filtro" else "Resumen")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ContadorHistorial("$totalRegistros", "Registros", TonoEdu.AZUL, Modifier.weight(1f))
                ContadorHistorial("${promedioAsistencia.toInt()}%", "Asistencia", TonoEdu.VERDE, Modifier.weight(1f))
                ContadorHistorial("${promedioTardanzas.toInt()}%", "Tardanza", TonoEdu.AMARILLO, Modifier.weight(1f))
                ContadorHistorial("${promedioFaltas.toInt()}%", "Faltas", TonoEdu.CORAL, Modifier.weight(1f))
            }

            // REGISTROS + BOTÓN FILTRAR
            Spacer(modifier = Modifier.height(4.dp))
            TituloSeccionEdu("Registros") {
                BotonFiltro(activo = filtroActivo, onClick = { mostrarFiltro = true })
            }

            // CHIP DEL FILTRO ACTIVO (con X para quitarlo)
            if (filtroActivo) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(EduAzulOscuroClaro)
                        .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = EduAzulOscuro, modifier = Modifier.size(16.dp))
                    Text(
                        textoFiltro, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp, color = EduAzulOscuro
                    )
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Quitar filtro",
                        tint = EduAzulOscuro,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                fechaDesde = ""; fechaHasta = ""
                                labelDesde = "Desde"; labelHasta = "Hasta"
                            }
                            .padding(4.dp)
                            .size(16.dp)
                    )
                }
            }

            // LISTA
            if (historialFiltrado.isEmpty()) {
                TarjetaEdu {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconoCirculoEdu(Icons.Rounded.EventBusy, TonoEdu.GRIS, tamano = 56.dp)
                        Text(
                            if (filtroActivo) "No hay registros en el rango seleccionado."
                            else "Aún no hay registros de asistencia para esta clase.",
                            fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp,
                            color = EduTextoSecundario, textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                historialFiltrado.forEach { item ->
                    TarjetaRegistroHistorial(item = item, onClick = { onVerDetalle(item) })
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

    // HOJA INFERIOR: FILTRO POR FECHA
    if (mostrarFiltro) {
        ModalBottomSheet(
            onDismissRequest = { mostrarFiltro = false },
            sheetState = sheetState,
            containerColor = Color.White
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

// Botón "Filtrar" junto al título de la lista: azul oscuro sólido cuando hay un filtro activo
@Composable
private fun BotonFiltro(activo: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .clip(forma)
            .background(if (activo) EduAzulOscuro else Color.White)
            .border(1.5.dp, if (activo) EduAzulOscuro else EduBorde, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            Icons.Rounded.FilterAlt, contentDescription = null,
            tint = if (activo) Color.White else EduEtiqueta, modifier = Modifier.size(16.dp)
        )
        Text(
            "Filtrar", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.sp,
            color = if (activo) Color.White else EduEtiqueta
        )
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
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconoCirculoEdu(Icons.Rounded.CalendarMonth, TonoEdu.AZUL)
            Column {
                Text(
                    "Filtrar por fecha", fontFamily = Nunito, fontWeight = FontWeight.Black,
                    fontSize = 20.sp, color = EduTexto
                )
                Text(
                    "Si eliges una sola fecha, se muestra ese día.",
                    fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp,
                    color = EduTextoSecundario
                )
            }
        }

        CampoFecha(etiqueta = "Desde", valor = lDesde, onClick = { openDesde() })
        CampoFecha(etiqueta = "Hasta", valor = lHasta, onClick = { openHasta() })

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonSecundarioEdu(
                texto = "Limpiar",
                onClick = onLimpiar,
                modifier = Modifier.weight(1f)
            )
            BotonPrincipalEdu(
                texto = "Aplicar",
                tipo = TipoBotonEdu.AVANZAR,
                icono = Icons.Rounded.Check,
                modifier = Modifier.weight(1f),
                onClick = {
                    onAplicar(
                        tempDesde, tempHasta,
                        if (lDesde.isEmpty()) "Desde" else lDesde,
                        if (lHasta.isEmpty()) "Hasta" else lHasta
                    )
                }
            )
        }
    }
}

// Campo de fecha (abre el calendario al tocarlo), con el mismo estilo de los campos de texto
@Composable
private fun CampoFecha(etiqueta: String, valor: String, onClick: () -> Unit) {
    val forma = RoundedCornerShape(16.dp)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            etiqueta, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp, color = EduEtiqueta
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(forma)
                .background(EduCampo)
                .border(1.5.dp, EduBorde, forma)
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(20.dp))
            Text(
                if (valor.isEmpty()) "Seleccionar fecha" else valor,
                modifier = Modifier.weight(1f),
                fontFamily = Nunito,
                fontWeight = if (valor.isEmpty()) FontWeight.SemiBold else FontWeight.Bold,
                fontSize = 15.sp,
                color = if (valor.isEmpty()) EduTextoSecundario else EduTexto
            )
        }
    }
}

// Contador con texto (permite "85%")
@Composable
private fun ContadorHistorial(valor: String, etiqueta: String, tono: TonoEdu, modifier: Modifier) {
    val c = coloresDeTono(tono)
    Column(
        modifier = modifier
            .background(c.fondo, RoundedCornerShape(18.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 21.sp, color = c.texto, maxLines = 1)
        Text(
            etiqueta, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp,
            color = c.texto, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}

// Tarjeta de un día del historial: fecha, hora y chips Asistió / Tardanza / Faltó
@Composable
private fun TarjetaRegistroHistorial(item: HistorialItem, onClick: () -> Unit) {
    TarjetaEdu(onClick = onClick, relleno = PaddingValues(horizontal = 14.dp, vertical = 12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconoCirculoEdu(Icons.Rounded.Event, TonoEdu.AZUL)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    item.fechaDisplay, fontFamily = Nunito, fontWeight = FontWeight.Black,
                    fontSize = 15.sp, color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Rounded.Schedule, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(14.dp))
                    Text(
                        "${item.hora} · ${item.total} alumnos", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                        fontSize = 13.sp, color = EduTextoSecundario
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ChipEdu("${item.presentes} A", TonoEdu.VERDE)
                    if (item.tardanzas > 0) ChipEdu("${item.tardanzas} T", TonoEdu.AMARILLO)
                    ChipEdu("${item.ausentes} F", TonoEdu.CORAL)
                }
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Ver detalle",
                tint = EduTextoSecundario, modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
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
