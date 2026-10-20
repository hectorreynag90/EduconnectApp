package com.educonnectapp.ui.screens.padre.asistencia

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.coloresDeTono
import com.educonnectapp.ui.screens.padre.inicio.BottomNavBarPadre
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

// Una asistencia del hijo (la arma MainActivity desde el historial del backend)
data class AsistenciaItem(
    val id: Long,
    val alumnoId: Long,
    val cursoId: Long,
    val cursoNombre: String,
    val fecha: String,          // "yyyy-MM-dd"
    val estado: String,         // "A" | "T" | "F"
    // ----- Rediseño 2026 -----
    val hora: String = "",      // "08:05 a. m."
    val docente: String = ""
)

// Curso del historial (para los filtros)
data class CursoItem2(val id: Long, val nombre: String)

private val LOCALE_PE = Locale("es", "PE")
private val DIAS_ESCOLARES = listOf("Lun", "Mar", "Mié", "Jue", "Vie")
private val DIAS_SEMANA = DIAS_ESCOLARES + listOf("Sáb", "Dom")

// Colores y texto de cada estado. La tardanza cuenta como asistencia.
private fun tonoEstado(estado: String): TonoEdu = when (estado) {
    "A" -> TonoEdu.VERDE
    "T" -> TonoEdu.AMARILLO
    else -> TonoEdu.CORAL
}

private fun textoEstado(estado: String): String = when (estado) {
    "A" -> "Presente"
    "T" -> "Tardanza"
    else -> "Falta"
}

// % de asistencia: Presente y Tardanza cuentan como asistencia; solo la Falta resta
private fun porcentaje(lista: List<AsistenciaItem>): Int =
    if (lista.isEmpty()) 0 else lista.count { it.estado == "A" || it.estado == "T" } * 100 / lista.size

// =====================================================================
// ASISTENCIA DEL PADRE · HISTORIAL DE UN HIJO (rediseño 2026)
// Cabecera fija azul oscuro · pestañas "Calendario" y "Por curso" · resumen del mes ·
// calendario de lunes a viernes con un punto por clase · detalle en hoja inferior
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
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
    onVerDetalle: (asistenciaId: Long, fecha: String) -> Unit = { _, _ -> },   // ya no se usa: el detalle es una hoja
    // ----- Rediseño 2026 -----
    gradoSeccion: String = "",          // "2do Grado A"
    cargando: Boolean = false
) {
    val hoy = remember { LocalDate.now(java.time.ZoneId.of("America/Lima")) }
    val mesHoy = remember { YearMonth.from(hoy) }
    var mes by remember { mutableStateOf(mesHoy) }
    var pestana by remember { mutableStateOf(0) }                    // 0 = Calendario · 1 = Por curso
    var cursoSel by remember { mutableStateOf<Long?>(null) }         // null = Todos
    var diaElegido by remember { mutableStateOf<LocalDate?>(null) }
    var detalle by remember { mutableStateOf<AsistenciaItem?>(null) }

    // Primer mes con registros (no se puede retroceder antes)
    val mesMinimo = todasAsistencias.mapNotNull { fechaDe(it.fecha) }.minOrNull()?.let { YearMonth.from(it) } ?: mesHoy
    val delMes = todasAsistencias.filter { fechaDe(it.fecha)?.let { f -> YearMonth.from(f) == mes } == true }
    val delMesFiltradas = if (cursoSel == null) delMes else delMes.filter { it.cursoId == cursoSel }
    val porDia = delMesFiltradas.groupBy { it.fecha.take(10) }
    // Día que se muestra: el elegido (si es de este mes) o el último con registros
    val diaMostrado = diaElegido?.takeIf { YearMonth.from(it) == mes }
        ?: porDia.keys.maxOrNull()?.let { fechaDe(it) }
    // Si hay registros en sábado o domingo, el calendario muestra los 7 días
    val conFinDeSemana = porDia.keys.mapNotNull { fechaDe(it) }
        .any { it.dayOfWeek == DayOfWeek.SATURDAY || it.dayOfWeek == DayOfWeek.SUNDAY }

    val nombreMes = mes.format(DateTimeFormatter.ofPattern("MMMM yyyy", LOCALE_PE)).replaceFirstChar { it.uppercase() }
    val primerNombre = alumnoNombre.trim().split(" ").firstOrNull().orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // ===================== CABECERA FIJA =====================
        CabeceraEdu(
            titulo = "Asistencia",
            subtitulo = listOf(alumnoNombre, gradoSeccion).filter { it.isNotBlank() }.joinToString(" · "),
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
                    if (mes == mesHoy) "$nombreMes · al día de hoy" else nombreMes,
                    modifier = Modifier.padding(top = 14.dp),
                    colorCabecera = ColorModulo.Asistencia
                )
            }
        )

        // ===================== CONTENIDO =====================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Pestañas
            Pestanas(
                opciones = listOf("Calendario", "Por curso"),
                seleccionada = pestana,
                onElegir = { pestana = it }
            )

            if (cargando && todasAsistencias.isEmpty()) {
                TarjetaEdu {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = EduTeal, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                        Text(
                            "Cargando la asistencia de $primerNombre…", fontFamily = Nunito,
                            fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EduTextoSecundario
                        )
                    }
                }
            } else if (pestana == 0) {
                // ---------- CALENDARIO ----------
                FiltroCursos(listaCursos, cursoSel) { cursoSel = it; diaElegido = null }
                ResumenMes(delMesFiltradas)
                CalendarioMes(
                    mes = mes,
                    hoy = hoy,
                    porDia = porDia,
                    diaMostrado = diaMostrado,
                    conFinDeSemana = conFinDeSemana,
                    puedeRetroceder = mes > mesMinimo,
                    puedeAvanzar = mes < mesHoy,
                    onMesAnterior = { mes = mes.minusMonths(1); diaElegido = null },
                    onMesSiguiente = { mes = mes.plusMonths(1); diaElegido = null },
                    onElegirDia = { diaElegido = it }
                )
                val nombreCurso = listaCursos.find { it.id == cursoSel }?.nombre
                if (diaMostrado == null) {
                    SinRegistros(
                        titulo = "Sin asistencias en $nombreMes",
                        mensaje = if (nombreCurso == null) "Aún no hay asistencias registradas este mes."
                        else "Aún no hay asistencias de $nombreCurso este mes."
                    )
                } else {
                    val registros = porDia[diaMostrado.toString()].orEmpty()   // en el orden de registro (lo da MainActivity)
                    TituloSeccionEdu(fechaLarga(diaMostrado)) {
                        if (registros.isNotEmpty()) {
                            ChipEdu(if (registros.size == 1) "1 clase" else "${registros.size} clases", TonoEdu.AZUL)
                        }
                    }
                    if (registros.isEmpty()) {
                        SinRegistros(
                            titulo = "Sin asistencia registrada",
                            mensaje = "No hay asistencia registrada" +
                                    (if (nombreCurso != null) " de $nombreCurso" else "") +
                                    " el ${fechaLarga(diaMostrado).replaceFirstChar { it.lowercase() }}."
                        )
                    } else {
                        TarjetaEdu(relleno = PaddingValues(horizontal = 14.dp, vertical = 4.dp)) {
                            registros.forEachIndexed { i, a ->
                                FilaRegistro(a, ultima = i == registros.lastIndex) { detalle = a }
                            }
                        }
                    }
                }
            } else {
                // ---------- POR CURSO ----------
                SelectorMes(
                    nombreMes = nombreMes,
                    puedeRetroceder = mes > mesMinimo,
                    puedeAvanzar = mes < mesHoy,
                    onMesAnterior = { mes = mes.minusMonths(1); diaElegido = null },
                    onMesSiguiente = { mes = mes.plusMonths(1); diaElegido = null }
                )
                ResumenMes(delMes)
                TituloSeccionEdu("Por curso") {
                    Text(
                        "cada cuadro = 1 clase", fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp, color = EduTextoSecundario
                    )
                }
                val cursos = delMes.groupBy { it.cursoId }.toList()
                    .sortedBy { (_, lista) -> lista.first().cursoNombre }
                if (cursos.isEmpty()) {
                    TarjetaEdu {
                        Text(
                            "No hay asistencias registradas en $nombreMes.", modifier = Modifier.fillMaxWidth(),
                            fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                            color = EduTextoSecundario, textAlign = TextAlign.Center
                        )
                    }
                }
                cursos.forEach { (cursoId, lista) ->
                    TarjetaCurso(lista) {
                        // Abre el calendario filtrado por este curso
                        cursoSel = cursoId
                        diaElegido = null
                        pestana = 0
                    }
                }
            }
        }

        BottomNavBarPadre(
            onInicio = onHomePadre,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Inicio"
        )
    }

    // ===================== DETALLE (HOJA INFERIOR) =====================
    detalle?.let { a ->
        val estadoHoja = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { detalle = null },
            sheetState = estadoHoja,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        ) {
            HojaDetalle(a, gradoSeccion, alumnoNombre) { detalle = null }
        }
    }
}

// ---------------------------------------------------------------------
// Piezas de la pantalla
// ---------------------------------------------------------------------

// Pestañas tipo "píldora" (Calendario · Por curso)
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
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activa) EduAzulOscuro else Color.Transparent)
                    .clickable { onElegir(i) }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (i == 0) Icons.Rounded.CalendarMonth else Icons.Rounded.School, contentDescription = null,
                    tint = if (activa) Color.White else EduEtiqueta, modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    texto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.5.sp,
                    color = if (activa) Color.White else EduEtiqueta
                )
            }
        }
    }
}

// Chips de cursos: "Todos" + cada curso
@Composable
private fun FiltroCursos(cursos: List<CursoItem2>, seleccionado: Long?, onElegir: (Long?) -> Unit) {
    if (cursos.size < 2) return   // con un solo curso no hace falta filtrar
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        ChipFiltro("Todos", seleccionado == null) { onElegir(null) }
        cursos.forEach { c -> ChipFiltro(c.nombre, seleccionado == c.id) { onElegir(c.id) } }
    }
}

@Composable
private fun ChipFiltro(texto: String, activo: Boolean, onClick: () -> Unit) {
    Text(
        texto,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (activo) EduAzulOscuro else Color.White)
            .border(1.5.dp, if (activo) EduAzulOscuro else EduBorde, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 7.dp),
        fontFamily = Nunito, fontWeight = if (activo) FontWeight.Black else FontWeight.ExtraBold,
        fontSize = 13.5.sp, color = if (activo) Color.White else EduEtiqueta, maxLines = 1
    )
}

// Resumen del mes: % de asistencia y conteo de Presente, Tardanza y Falta
@Composable
private fun ResumenMes(lista: List<AsistenciaItem>) {
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        Indicador(if (lista.isEmpty()) "—" else "${porcentaje(lista)}%", "Asistencia", TonoEdu.AZUL, Modifier.weight(1f))
        Indicador("${lista.count { it.estado == "A" }}", "Presente", TonoEdu.VERDE, Modifier.weight(1f))
        Indicador("${lista.count { it.estado == "T" }}", "Tardanza", TonoEdu.AMARILLO, Modifier.weight(1f))
        Indicador("${lista.count { it.estado == "F" }}", "Falta", TonoEdu.CORAL, Modifier.weight(1f))
    }
}

@Composable
private fun Indicador(valor: String, etiqueta: String, tono: TonoEdu, modifier: Modifier = Modifier) {
    val c = coloresDeTono(tono)
    Column(
        modifier = modifier
            .background(c.fondo, RoundedCornerShape(18.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 21.sp, color = c.texto)
        Text(
            etiqueta, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp,
            color = c.texto, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}

// Flechas para cambiar de mes
@Composable
private fun FlechaMes(icono: ImageVector, habilitada: Boolean, descripcion: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(if (habilitada) EduAzulOscuroClaro else EduFondo)
            .then(if (habilitada) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icono, contentDescription = descripcion,
            tint = if (habilitada) EduAzulOscuro else EduBorde, modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun SelectorMes(
    nombreMes: String,
    puedeRetroceder: Boolean,
    puedeAvanzar: Boolean,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit
) {
    TarjetaEdu(relleno = PaddingValues(horizontal = 12.dp, vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FlechaMes(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, puedeRetroceder, "Mes anterior", onMesAnterior)
            Text(
                nombreMes, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Black,
                fontSize = 16.sp, color = EduTexto, textAlign = TextAlign.Center
            )
            FlechaMes(Icons.AutoMirrored.Rounded.KeyboardArrowRight, puedeAvanzar, "Mes siguiente", onMesSiguiente)
        }
    }
}

// Calendario de lunes a viernes (o toda la semana si hay registros el fin de semana):
// un punto por clase; el fondo del día toma el color del peor estado (sólido si es el día elegido)
@Composable
private fun CalendarioMes(
    mes: YearMonth,
    hoy: LocalDate,
    porDia: Map<String, List<AsistenciaItem>>,
    diaMostrado: LocalDate?,
    conFinDeSemana: Boolean,
    puedeRetroceder: Boolean,
    puedeAvanzar: Boolean,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit,
    onElegirDia: (LocalDate) -> Unit
) {
    val nombreMes = mes.format(DateTimeFormatter.ofPattern("MMMM yyyy", LOCALE_PE)).replaceFirstChar { it.uppercase() }
    val semanas = remember(mes, conFinDeSemana) { semanasEscolares(mes, if (conFinDeSemana) 7 else 5) }

    TarjetaEdu(relleno = PaddingValues(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FlechaMes(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, puedeRetroceder, "Mes anterior", onMesAnterior)
            Text(
                nombreMes, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Black,
                fontSize = 16.sp, color = EduTexto, textAlign = TextAlign.Center
            )
            FlechaMes(Icons.AutoMirrored.Rounded.KeyboardArrowRight, puedeAvanzar, "Mes siguiente", onMesSiguiente)
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (if (conFinDeSemana) DIAS_SEMANA else DIAS_ESCOLARES).forEach {
                Text(
                    it, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Black,
                    fontSize = 12.5.sp, color = EduTextoSecundario, textAlign = TextAlign.Center
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            semanas.forEach { semana ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    semana.forEach { dia ->
                        if (dia == null) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            CeldaDia(
                                dia = dia,
                                registros = porDia[dia.toString()].orEmpty(),
                                esHoy = dia == hoy,
                                futuro = dia.isAfter(hoy),
                                elegido = dia == diaMostrado,
                                modifier = Modifier.weight(1f),
                                onClick = { onElegirDia(dia) }
                            )
                        }
                    }
                }
            }
        }
        // Leyenda
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("A", "T", "F").forEach { e ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(modifier = Modifier.size(9.dp).background(coloresDeTono(tonoEstado(e)).solido, CircleShape))
                    Text(textoEstado(e), fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = EduEtiqueta)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(modifier = Modifier.size(10.dp).border(2.dp, EduAzulOscuro, RoundedCornerShape(4.dp)))
                Text("Hoy", fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = EduEtiqueta)
            }
        }
    }
}

@Composable
private fun CeldaDia(
    dia: LocalDate,
    registros: List<AsistenciaItem>,
    esHoy: Boolean,
    futuro: Boolean,
    elegido: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val peor = when {
        registros.any { it.estado == "F" } -> "F"
        registros.any { it.estado == "T" } -> "T"
        registros.isNotEmpty() -> "A"
        else -> null
    }
    val c = peor?.let { coloresDeTono(tonoEstado(it)) }
    // Día con registros: fondo claro del color del estado (verde, amarillo o granate).
    // Día elegido: el mismo color pero sólido, para que se note cuál se está viendo.
    val solido = elegido && c != null
    val fondo = when {
        solido -> c!!.solido
        c != null -> c.fondo
        elegido -> EduAzulOscuroClaro               // día elegido sin asistencia
        else -> Color.White
    }
    val colorNumero = when {
        solido && peor == "T" -> EduTexto          // sobre amarillo se usa texto oscuro
        solido -> Color.White
        elegido -> EduAzulOscuro
        c != null -> c.texto
        futuro -> EduBorde
        else -> EduTextoSecundario
    }
    val forma = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .height(50.dp)
            .clip(forma)
            .background(fondo)
            .then(if (esHoy) Modifier.border(2.dp, EduAzulOscuro, forma) else Modifier)
            .then(if (!futuro) Modifier.clickable(onClick = onClick) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "${dia.dayOfMonth}", fontFamily = Nunito, fontWeight = FontWeight.Black,
            fontSize = 15.sp, color = colorNumero
        )
        Row(
            modifier = Modifier.padding(top = 4.dp).height(7.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Un punto por clase (máximo 5)
            registros.take(5).forEach { a ->
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(if (solido) colorNumero else coloresDeTono(tonoEstado(a.estado)).solido, CircleShape)
                )
            }
        }
    }
}

// Mensaje con ícono cuando no hay asistencia en el día o en el mes elegido
@Composable
private fun SinRegistros(titulo: String, mensaje: String) {
    TarjetaEdu(relleno = PaddingValues(horizontal = 16.dp, vertical = 18.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconoCirculoEdu(Icons.Rounded.EventBusy, TonoEdu.GRIS, tamano = 60.dp)
            Text(titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.sp, color = EduTexto, textAlign = TextAlign.Center)
            Text(
                mensaje, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp,
                lineHeight = 19.sp, color = EduTextoSecundario, textAlign = TextAlign.Center
            )
        }
    }
}

// Una clase del día: barra de color, curso, hora · docente y estado
@Composable
private fun FilaRegistro(a: AsistenciaItem, ultima: Boolean, onClick: () -> Unit) {
    val c = coloresDeTono(tonoEstado(a.estado))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.width(6.dp).height(36.dp).background(c.solido, RoundedCornerShape(9.dp)))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                a.cursoNombre, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp,
                color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                listOf(a.hora, a.docente).filter { it.isNotBlank() }.joinToString(" · "),
                fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
        ChipEdu(textoEstado(a.estado), tonoEstado(a.estado))
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null,
            tint = EduTextoSecundario, modifier = Modifier.size(20.dp)
        )
    }
    if (!ultima) Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EduFondo))
}

// Tarjeta de un curso: %, docente, cuadritos (1 por clase) y conteo P / T / F
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TarjetaCurso(lista: List<AsistenciaItem>, onClick: () -> Unit) {
    val ordenadas = lista.sortedBy { it.fecha }   // mismo día: orden de registro
    val pct = porcentaje(lista)
    val docente = lista.groupingBy { it.docente }.eachCount().maxByOrNull { it.value }?.key.orEmpty()
    val tonoPct = when {
        pct >= 90 -> TonoEdu.VERDE
        pct >= 75 -> TonoEdu.AMARILLO
        else -> TonoEdu.CORAL
    }
    TarjetaEdu(relleno = PaddingValues(horizontal = 15.dp, vertical = 13.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    lista.first().cursoNombre, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.5.sp,
                    color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    listOf(docente, if (lista.size == 1) "1 clase" else "${lista.size} clases")
                        .filter { it.isNotBlank() }.joinToString(" · "),
                    fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            Text("$pct%", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 20.sp, color = coloresDeTono(tonoPct).texto)
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null,
                tint = EduTextoSecundario, modifier = Modifier.size(20.dp)
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ordenadas.forEach { a ->
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .background(coloresDeTono(tonoEstado(a.estado)).solido, RoundedCornerShape(5.dp))
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChipEdu("${lista.count { it.estado == "A" }} P", TonoEdu.VERDE)
            ChipEdu("${lista.count { it.estado == "T" }} T", TonoEdu.AMARILLO)
            ChipEdu("${lista.count { it.estado == "F" }} F", TonoEdu.CORAL)
        }
    }
}

// Contenido de la hoja inferior con el detalle de una asistencia
@Composable
private fun HojaDetalle(a: AsistenciaItem, gradoSeccion: String, alumnoNombre: String, onCerrar: () -> Unit) {
    val tono = tonoEstado(a.estado)
    val c = coloresDeTono(tono)
    val fecha = fechaDe(a.fecha)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconoCirculoEdu(
                when (a.estado) {
                    "A" -> Icons.Rounded.Check
                    "T" -> Icons.Rounded.Schedule
                    else -> Icons.Rounded.Close
                },
                tono, tamano = 52.dp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${textoEstado(a.estado)} · ${a.cursoNombre}", fontFamily = Nunito, fontWeight = FontWeight.Black,
                    fontSize = 20.sp, color = c.texto, maxLines = 2, overflow = TextOverflow.Ellipsis
                )
                Text(
                    listOf(fecha?.let { fechaLarga(it) } ?: a.fecha, a.hora).filter { it.isNotBlank() }.joinToString(" · "),
                    fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduTextoSecundario
                )
            }
        }
        TarjetaEdu(relleno = PaddingValues(horizontal = 14.dp, vertical = 2.dp), colorFondo = EduFondo) {
            FilaDato(Icons.Rounded.Person, "Estudiante", alumnoNombre)
            FilaDato(Icons.Rounded.Person, "Docente", a.docente.ifBlank { "—" })
            FilaDato(Icons.Rounded.School, "Grado y sección", gradoSeccion.ifBlank { "—" }, ultima = true)
        }
        val aviso = when (a.estado) {
            "F" -> "Si la falta tiene justificación, comunícate con el docente del curso. " +
                    "Si el docente corrige el registro, te llegará una notificación."
            "T" -> "La tardanza cuenta como asistencia en el porcentaje del mes."
            else -> null
        }
        if (aviso != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(c.fondo, RoundedCornerShape(16.dp))
                    .padding(horizontal = 13.dp, vertical = 11.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = c.texto, modifier = Modifier.size(20.dp))
                Text(
                    aviso, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp, lineHeight = 18.sp, color = c.texto
                )
            }
        }
        BotonSecundarioEdu(texto = "Cerrar", onClick = onCerrar)
    }
}

@Composable
private fun FilaDato(icono: ImageVector, etiqueta: String, valor: String, ultima: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconoCirculoEdu(icono, TonoEdu.AZUL, tamano = 36.dp)
        Text(etiqueta, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EduTextoSecundario)
        Text(
            valor, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.5.sp, color = EduTexto,
            textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.3f)
        )
    }
    if (!ultima) Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White))
}

// ---------------------------------------------------------------------
// Fechas
// ---------------------------------------------------------------------

private fun fechaDe(texto: String): LocalDate? = try { LocalDate.parse(texto.take(10)) } catch (e: Exception) { null }

// "Viernes 16 de octubre"
private fun fechaLarga(fecha: LocalDate): String =
    fecha.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", LOCALE_PE)).replaceFirstChar { it.uppercase() }

// Semanas del mes de lunes a viernes, o de lunes a domingo con dias = 7 (null = día de otro mes). Se omiten las semanas sin días del mes.
private fun semanasEscolares(mes: YearMonth, dias: Int = 5): List<List<LocalDate?>> {
    val primero = mes.atDay(1)
    val ultimo = mes.atEndOfMonth()
    var lunes = primero.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val semanas = mutableListOf<List<LocalDate?>>()
    while (!lunes.isAfter(ultimo)) {
        val semana = (0L until dias.toLong()).map { lunes.plusDays(it) }.map { d -> if (YearMonth.from(d) == mes) d else null }
        if (semana.any { it != null }) semanas.add(semana)
        lunes = lunes.plusWeeks(1)
    }
    return semanas
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HistorialAsistenciasPreview() {
    val hoy = LocalDate.now()
    val cursos = listOf(CursoItem2(1L, "Ciencia y Tecnología"), CursoItem2(2L, "Comunicación"), CursoItem2(3L, "Matemáticas"))
    val estados = listOf("A", "A", "T", "A", "F", "A", "A", "A", "T", "A")
    val asistencias = (0 until 10).flatMap { i ->
        val dia = hoy.minusDays(i.toLong())
        if (dia.dayOfWeek == DayOfWeek.SATURDAY || dia.dayOfWeek == DayOfWeek.SUNDAY) emptyList()
        else cursos.mapIndexed { j, c ->
            AsistenciaItem(
                id = (i * 10 + j).toLong(), alumnoId = 1L, cursoId = c.id, cursoNombre = c.nombre,
                fecha = dia.toString(), estado = estados[(i + j) % estados.size],
                hora = "%02d:05 a. m.".format(8 + j), docente = "Prof. Hector Reyna"
            )
        }
    }
    EduConnectAppTheme {
        HistorialAsistenciasScreen(
            alumnoId = 1L,
            alumnoNombre = "Angela Perez Garcia",
            gradoSeccion = "2do Grado A",
            listaCursos = cursos,
            todasAsistencias = asistencias
        )
    }
}
