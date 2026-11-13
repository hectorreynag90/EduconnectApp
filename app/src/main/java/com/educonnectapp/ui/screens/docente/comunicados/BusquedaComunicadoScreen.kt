package com.educonnectapp.ui.screens.docente.comunicados

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BarraProgresoEdu
import com.educonnectapp.ui.components.BotonCrearEdu
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ComunicadoResumenItem(
    val id: Long,
    val asunto: String,
    val mensaje: String = "",
    val fecha: String,              // "yyyy-MM-dd"
    val totalNotificados: Int,
    val totalLeidos: Int,
    val hora: String = "",          // "10:24 a. m." (rediseño 2026)
    val clase: String = ""          // "1er A · Ciencia y Tecnología" (solo en el historial de todas las clases)
)

private fun hoyComunicados(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

// "2026-10-14" -> "Miércoles 14 de octubre"
private fun fechaLargaComunicado(fecha: String): String = try {
    val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha)
    SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "PE")).format(d!!).replaceFirstChar { it.uppercase() }
} catch (e: Exception) { fecha }

// "2026-10-14" -> "14 oct" ("Hoy" si es de hoy)
private fun fechaCortaComunicado(fecha: String): String = try {
    if (fecha == hoyComunicados()) "Hoy" else {
        val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha)
        SimpleDateFormat("d MMM", Locale("es", "PE")).format(d!!).replace(".", "")
    }
} catch (e: Exception) { fecha }

// "2026-11-12" -> "NOVIEMBRE 2026"
private fun mesAnioComunicado(fecha: String): String = try {
    val d = SimpleDateFormat("yyyy-MM", Locale.US).parse(fecha.take(7))
    SimpleDateFormat("MMMM yyyy", Locale("es", "PE")).format(d!!).uppercase()
} catch (e: Exception) { fecha.take(7) }

// =====================================================================
// COMUNICADOS DE UNA CLASE (rediseño 2026)
// Cabecera fija granate · búsqueda + fecha · pestañas Todos / Leídos / Sin leer · lista con lectura
// También es el HISTORIAL de todas las clases: con filtrosClase se muestran las pastillas por clase,
// la clase de cada comunicado y la lista agrupada por mes.
// =====================================================================

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
    // ----- Rediseño 2026 -----
    onNuevoComunicado: () -> Unit = {},    // "+ Nuevo" para esta clase
    // ----- Historial de todas las clases -----
    titulo: String? = null,                // null = el curso de la clase
    subtitulo: String? = null,             // null = "Comunicados · 1er A"
    filtrosClase: List<String> = emptyList()   // clases para filtrar (vacío = una sola clase)
) {
    val context = LocalContext.current
    var busqueda by remember { mutableStateOf("") }
    var fechaSeleccionada by remember { mutableStateOf("") }
    var tabSeleccionado by remember { mutableStateOf("Todos") }
    var claseSeleccionada by remember { mutableStateOf("") }     // "" = todas las clases
    val esHistorial = filtrosClase.isNotEmpty()

    fun abrirCalendario() {
        val calendario = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, anio, mes, dia -> fechaSeleccionada = String.format(Locale.US, "%04d-%02d-%02d", anio, mes + 1, dia) },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Búsqueda y fecha primero; las pestañas cuentan sobre ese resultado
    val filtrados = comunicados.filter { item ->
        (claseSeleccionada.isEmpty() || item.clase == claseSeleccionada) &&
                (busqueda.isBlank() || item.asunto.contains(busqueda.trim(), ignoreCase = true)) &&
                (fechaSeleccionada.isEmpty() || item.fecha == fechaSeleccionada)
    }
    val leidos = filtrados.filter { it.totalNotificados - it.totalLeidos <= 0 }
    val sinLeer = filtrados.filter { it.totalNotificados - it.totalLeidos > 0 }
    val lista = when (tabSeleccionado) {
        "Leídos" -> leidos
        "Sin leer" -> sinLeer
        else -> filtrados
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = titulo ?: cursoNombre.ifBlank { "Comunicados" },
            subtitulo = subtitulo ?: "Comunicados · $gradoNombre $seccionNombre",
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
                    if (comunicados.size == 1) "1 comunicado" else "${comunicados.size} comunicados",
                    modifier = Modifier.padding(top = 14.dp),
                    colorCabecera = ColorModulo.Comunicados
                )
            }
        )

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),   // espacio para "+ Nuevo"
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // FILTRO POR CLASE (solo en el historial, si hay más de una clase)
                if (filtrosClase.size > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PildoraClase("Todas (${comunicados.size})", claseSeleccionada.isEmpty()) { claseSeleccionada = "" }
                        filtrosClase.forEach { clase ->
                            PildoraClase(
                                "$clase (${comunicados.count { it.clase == clase }})",
                                claseSeleccionada == clase
                            ) { claseSeleccionada = clase }
                        }
                    }
                }

                // BÚSQUEDA + CALENDARIO
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(1.5.dp, EduBorde, RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Rounded.Search, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(20.dp))
                        BasicTextField(
                            value = busqueda,
                            onValueChange = { busqueda = it },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EduTexto),
                            decorationBox = { inner ->
                                if (busqueda.isEmpty()) {
                                    Text(
                                        "Buscar por asunto", fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp, color = EduTextoSecundario
                                    )
                                }
                                inner()
                            }
                        )
                        if (busqueda.isNotEmpty()) {
                            Icon(
                                Icons.Rounded.Close, contentDescription = "Borrar búsqueda", tint = EduTextoSecundario,
                                modifier = Modifier.size(20.dp).clip(RoundedCornerShape(50)).clickable { busqueda = "" }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (fechaSeleccionada.isNotEmpty()) EduAzulOscuro else Color.White)
                            .border(1.5.dp, if (fechaSeleccionada.isNotEmpty()) EduAzulOscuro else EduBorde, RoundedCornerShape(16.dp))
                            .clickable { abrirCalendario() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.CalendarMonth, contentDescription = "Filtrar por fecha",
                            tint = if (fechaSeleccionada.isNotEmpty()) Color.White else EduEtiqueta,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // FECHA ELEGIDA (con X para quitarla)
                if (fechaSeleccionada.isNotEmpty()) {
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
                            fechaLargaComunicado(fechaSeleccionada), fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp, color = EduAzulOscuro
                        )
                        Icon(
                            Icons.Rounded.Close, contentDescription = "Quitar fecha", tint = EduAzulOscuro,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { fechaSeleccionada = "" }
                                .padding(4.dp)
                                .size(16.dp)
                        )
                    }
                }

                // PESTAÑAS
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PestanaComunicado("Todos (${filtrados.size})", tabSeleccionado == "Todos") { tabSeleccionado = "Todos" }
                    PestanaComunicado("Leídos (${leidos.size})", tabSeleccionado == "Leídos") { tabSeleccionado = "Leídos" }
                    PestanaComunicado("Sin leer (${sinLeer.size})", tabSeleccionado == "Sin leer") { tabSeleccionado = "Sin leer" }
                }

                // LISTA
                if (lista.isEmpty()) {
                    TarjetaEdu {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconoCirculoEdu(Icons.Rounded.SearchOff, TonoEdu.GRIS, tamano = 56.dp)
                            Text(
                                when {
                                    comunicados.isEmpty() && esHistorial -> "Aún no enviaste comunicados."
                                    comunicados.isEmpty() -> "Aún no enviaste comunicados a esta clase."
                                    else -> "No se encontraron comunicados."
                                },
                                fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp,
                                color = EduTextoSecundario, textAlign = TextAlign.Center
                            )
                        }
                    }
                } else if (esHistorial) {
                    // Historial: agrupado por mes, del más reciente al más antiguo
                    lista.sortedByDescending { it.fecha }.groupBy { it.fecha.take(7) }.forEach { (mes, delMes) ->
                        Text(
                            mesAnioComunicado(mes),
                            modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                            fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.5.sp,
                            letterSpacing = 0.6.sp, color = EduTextoSecundario.copy(alpha = 0.8f)
                        )
                        delMes.forEach { item ->
                            ComunicadoResumenCard(item = item, onClick = { onVerDetalle(item.id) })
                        }
                    }
                } else {
                    lista.forEach { item ->
                        ComunicadoResumenCard(item = item, onClick = { onVerDetalle(item.id) })
                    }
                }
            }

            // BOTÓN FLOTANTE "+ Nuevo" (para esta clase)
            BotonCrearEdu(
                onClick = onNuevoComunicado,
                texto = "Nuevo",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
            )
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

// Pestaña tipo pastilla: granate sólido cuando está activa
@Composable
private fun PestanaComunicado(texto: String, activa: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(50)
    Text(
        texto,
        modifier = Modifier
            .clip(forma)
            .background(if (activa) EduCoral else Color.White)
            .border(1.5.dp, if (activa) EduCoral else EduBorde, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.5.sp,
        color = if (activa) Color.White else EduEtiqueta
    )
}

// Pastilla del filtro por clase: azul oscuro sólido cuando está activa
@Composable
private fun PildoraClase(texto: String, activa: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(50)
    Text(
        texto,
        modifier = Modifier
            .clip(forma)
            .background(if (activa) EduAzulOscuro else Color.White)
            .border(1.5.dp, if (activa) EduAzulOscuro else EduBorde, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.5.sp,
        color = if (activa) Color.White else EduEtiqueta, maxLines = 1
    )
}

// Tarjeta de un comunicado: asunto, fecha y hora, cuántos leyeron / sin leer y barra de lectura
// (en el historial, también la clase)
@Composable
fun ComunicadoResumenCard(
    item: ComunicadoResumenItem,
    onClick: () -> Unit
) {
    val sinLeer = (item.totalNotificados - item.totalLeidos).coerceAtLeast(0)
    val progreso = if (item.totalNotificados > 0) item.totalLeidos.toFloat() / item.totalNotificados else 0f
    val esHoy = item.fecha == hoyComunicados()
    val detalleFecha = buildString {
        append(if (esHoy) "Hoy" else fechaLargaComunicado(item.fecha))
        if (item.hora.isNotBlank()) append(" · ${item.hora}")
    }

    TarjetaEdu(onClick = onClick, relleno = PaddingValues(horizontal = 15.dp, vertical = 13.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.asunto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.5.sp,
                    color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    detalleFecha, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                    color = EduTextoSecundario
                )
                if (item.clase.isNotBlank()) {
                    ChipEdu(item.clase, TonoEdu.AZUL, modifier = Modifier.padding(top = 6.dp), icono = Icons.Rounded.Groups)
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                fechaCortaComunicado(item.fecha), fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp, color = EduTextoSecundario
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ChipEdu("${item.totalLeidos} leyeron", TonoEdu.VERDE)
            if (sinLeer > 0) ChipEdu("$sinLeer sin leer", TonoEdu.CORAL)
            else ChipEdu("Todos leyeron", TonoEdu.VERDE, solido = true)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "${(progreso * 100).toInt()}%", fontFamily = Nunito, fontWeight = FontWeight.Black,
                fontSize = 14.sp, color = if (sinLeer == 0) EduTealOscuro else EduTexto
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        BarraProgresoEdu(progreso = progreso, color = EduTeal)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun BusquedaComunicadoPreview() {
    EduConnectAppTheme {
        BusquedaComunicadoScreen(
            gradoNombre = "1er Grado",
            seccionNombre = "A",
            cursoNombre = "Ciencia y Tecnología",
            comunicados = listOf(
                ComunicadoResumenItem(1L, "Reunión de padres · viernes 23", "Mensaje de prueba", "2026-10-19", 5, 3, "10:24 a. m."),
                ComunicadoResumenItem(2L, "Feria de ciencias: materiales", "Mensaje de prueba", "2026-10-14", 5, 2),
                ComunicadoResumenItem(3L, "Entrega de libretas", "Mensaje de prueba", "2026-10-09", 5, 5)
            ),
        )
    }
}
