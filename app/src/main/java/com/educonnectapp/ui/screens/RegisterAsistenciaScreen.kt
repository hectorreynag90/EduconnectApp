package com.educonnectapp.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.educonnectapp.R
import com.educonnectapp.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val AsistenciaVerde      = Color(0xFF26C281)
private val FaltaRojo            = Color(0xFFE53935)
private val TardanzaAmarillo     = Color(0xFFF59E0B)
private val BorderVerdePresente  = Color(0xFF26C281)
private val BorderRojoFalta      = Color(0xFFE53935)
private val BorderAmarilloTard   = Color(0xFFF59E0B)

// Horas que el docente tiene para editar la asistencia desde este módulo (desde que la registró)
private const val HORAS_EDICION = 12

data class AlumnoItem(
    val id: Long,
    val nombres: String,
    val apellidos: String
)

// "Apellidos, Nombres"; si el backend solo envía el nombre completo, lo muestra tal cual
fun nombreAlumno(apellidos: String, nombres: String): String =
    listOf(apellidos, nombres).filter { it.isNotBlank() }.joinToString(", ")

// Cada toque pasa al siguiente estado: (sin marcar) -> A -> T -> F -> A ...
private fun siguienteEstadoAsistencia(estado: String): String = when (estado) {
    "A" -> "T"
    "T" -> "F"
    else -> "A"
}

private fun colorEstadoAsistencia(estado: String): Color? = when (estado) {
    "A" -> AsistenciaVerde
    "T" -> TardanzaAmarillo
    "F" -> FaltaRojo
    else -> null
}

// Las fechas y horas de la asistencia son de Perú (el backend trabaja en America/Lima),
// aunque el celular tenga otra zona horaria configurada
private val ZONA_PERU: TimeZone = TimeZone.getTimeZone("America/Lima")

private fun formatoPeru(patron: String) =
    SimpleDateFormat(patron, Locale("es", "PE")).apply { timeZone = ZONA_PERU }

// Momento del registro: fecha de la hoja ("yyyy-MM-dd") + hora ("HH:mm:ss"). null si no se puede calcular.
private fun momentoRegistro(fecha: String, hora: String?): Date? {
    if (fecha.isBlank() || hora.isNullOrBlank()) return null
    return try {
        val hhmmss = if (hora.length >= 8) hora.take(8) else hora.take(5) + ":00"   // "HH:mm:ss" o "HH:mm"
        formatoPeru("yyyy-MM-dd HH:mm:ss").parse("$fecha $hhmmss")
    } catch (e: Exception) { null }
}

// Límite para editar: registro + 12 h, pero nunca después del fin del día de la clase (23:59)
private fun limiteEdicion(registro: Date?, fecha: String): Date? {
    if (registro == null) return null
    val masDoce = Calendar.getInstance(ZONA_PERU).apply { time = registro; add(Calendar.HOUR_OF_DAY, HORAS_EDICION) }.time
    val finDelDia = try { formatoPeru("yyyy-MM-dd HH:mm:ss").parse("$fecha 23:59:59") } catch (e: Exception) { null }
    return if (finDelDia != null && finDelDia.before(masDoce)) finDelDia else masDoce
}

@Composable
fun RegistroAsistenciaScreen(
    docenteId: String,
    gradoId: Long,
    seccionId: Long,
    cursoId: Long,
    grado: String,
    seccion: String,
    curso: String,
    listaAlumnos: List<AlumnoItem> = emptyList(),
    asistenciaPrevia: Map<Long, String> = emptyMap(),
    horaRegistro: String? = null,        // "HH:mm:ss" de la asistencia de hoy (null si aún no se registra)
    fechaHoja: String = "",              // "yyyy-MM-dd" de la hoja (fecha del servidor)
    cargando: Boolean = false,
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onGuardado: (presentes: Int, ausentes: Int, tardanzas: Int, estados: Map<Long, String>, hora: String, fecha: String) -> Unit = { _, _, _, _, _, _ -> }
) {
    // Fecha de la clase: la de la hoja del servidor (si aún no llega, la de hoy en Perú)
    val fechaHoy = fechaHoja.ifBlank { formatoPeru("yyyy-MM-dd").format(Date()) }
    val fechaDisplay = remember(fechaHoy) {
        try {
            formatoPeru("EEEE dd 'de' MMMM yyyy").format(formatoPeru("yyyy-MM-dd").parse(fechaHoy)!!)
                .replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { fechaHoy }
    }
    // Fecha y hora del aviso, con el mismo formato en ambas filas: "Sáb 03/10/2026 · 07:12 p. m."
    val formatoFechaHora = remember { formatoPeru("EEE dd/MM/yyyy · hh:mm a") }
    fun fechaHoraTexto(d: Date) = formatoFechaHora.format(d).replaceFirstChar { it.uppercase() }

    val estadoAsistencia = remember { mutableStateMapOf<Long, String>() }
    val context = LocalContext.current

    // Diálogos: confirmación -> guardando
    var mostrarDialogConfirmacion by remember { mutableStateOf(false) }
    var mostrarDialogCargando by remember { mutableStateOf(false) }

    val hayAsistenciaPrevia = asistenciaPrevia.isNotEmpty()

    // MODO: con asistencia ya registrada, la lista queda bloqueada hasta presionar "Editar asistencia"
    var editando by remember { mutableStateOf(false) }

    // Reloj: se revisa cada minuto si todavía está dentro del plazo de edición
    var ahora by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            ahora = Date()
        }
    }
    val registro = remember(horaRegistro, fechaHoy) { momentoRegistro(fechaHoy, horaRegistro) }
    val limite = remember(registro, fechaHoy) { limiteEdicion(registro, fechaHoy) }
    val dentroDelPlazo = limite == null || ahora.before(limite)

    // Precargar estados si hay asistencia previa (también al cancelar la edición)
    fun restaurarPrevia() {
        estadoAsistencia.clear()
        estadoAsistencia.putAll(asistenciaPrevia)
    }
    LaunchedEffect(asistenciaPrevia) {
        if (hayAsistenciaPrevia) {
            restaurarPrevia()
            editando = false
        }
    }

    // Si vence el plazo mientras edita, se cancela la edición
    LaunchedEffect(dentroDelPlazo) {
        if (!dentroDelPlazo && editando) {
            restaurarPrevia()
            editando = false
            Toast.makeText(context, "Terminó el plazo de edición ($HORAS_EDICION h)", Toast.LENGTH_LONG).show()
        }
    }

    fun cancelarEdicion() {
        restaurarPrevia()
        editando = false
    }
    BackHandler(enabled = editando) { cancelarEdicion() }

    // Se puede tocar la lista: registro nuevo, o edición activa
    val editable = !hayAsistenciaPrevia || editando

    // Espera 2 segundos (popup "Guardando...") y entrega los estados a MainActivity
    LaunchedEffect(mostrarDialogCargando) {
        if (mostrarDialogCargando) {
            val horaActual = formatoPeru("hh:mm a").format(Date())
            delay(2000)
            mostrarDialogCargando = false
            onGuardado(
                estadoAsistencia.values.count { it == "A" },
                estadoAsistencia.values.count { it == "F" },
                estadoAsistencia.values.count { it == "T" },
                estadoAsistencia.toMap(),
                horaActual,
                fechaHoy
            )
        }
    }

    // Forzar que Compose observe TODOS los cambios del map (size + values)
    val snapshotEstados = estadoAsistencia.toMap()

    val huboCambios = hayAsistenciaPrevia &&
            listaAlumnos.any { (snapshotEstados[it.id] ?: "") != (asistenciaPrevia[it.id] ?: "") }

    val totalAlumnos = listaAlumnos.size
    val presentes  = snapshotEstados.values.count { it == "A" }
    val ausentes   = snapshotEstados.values.count { it == "F" }
    val tardanzas  = snapshotEstados.values.count { it == "T" }
    val marcados   = presentes + ausentes + tardanzas
    val progreso   = if (totalAlumnos > 0) marcados.toFloat() / totalAlumnos else 0f
    val porcentaje = (progreso * 100).toInt()

    val todosListos     = listaAlumnos.all { (snapshotEstados[it.id] ?: "").isNotEmpty() }
    val botonHabilitado = if (hayAsistenciaPrevia) huboCambios else todosListos

    val hayAlgunMarcado  = snapshotEstados.values.any { it == "A" || it == "F" || it == "T" }
    val todosPresentes   = listaAlumnos.isNotEmpty() && listaAlumnos.all { snapshotEstados[it.id] == "A" }

    // ── DIALOG PASO 1: Confirmación ──────────────────────────────────────────
    if (mostrarDialogConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogConfirmacion = false },
            title = {
                Text(
                    text = if (hayAsistenciaPrevia) "¿Confirmar cambios?" else "¿Confirmar registro?",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextBlue,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Revisa el resumen antes de guardar:",
                        fontFamily = Roboto,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ConteoResumen(presentes, "Presentes", AsistenciaVerde, Modifier.weight(1f))
                        ConteoResumen(tardanzas, "Tardanzas", TardanzaAmarillo, Modifier.weight(1f))
                        ConteoResumen(ausentes, "Ausentes", FaltaRojo, Modifier.weight(1f))
                    }
                    Text(
                        text = "Total: $totalAlumnos alumnos",
                        fontFamily = Roboto,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogConfirmacion = false }) {
                    Text(
                        text = "Corregir",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogConfirmacion = false
                        mostrarDialogCargando = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Confirmar",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // ── DIALOG PASO 2: Cargando ──────────────────────────────────────────────
    if (mostrarDialogCargando) {
        Dialog(
            onDismissRequest = { /* no se puede cerrar mientras carga */ },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(horizontal = 32.dp, vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(52.dp),
                        color = EduconnectBlue,
                        strokeWidth = 4.dp
                    )
                    Text(
                        text = "Guardando el registro...",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextBlue,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // ── HEADER AZUL ──────────────────────────────────────────────────────
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
                    onClick = { if (editando) cancelarEdicion() else onBack() },
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
                        text = if (editando) "Editar Asistencia" else "Registro de Asistencia",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextWhite
                    )
                    Text(
                        text = curso, fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextWhite
                    )
                    Text(
                        text = "$grado - Sec. $seccion", fontFamily = Roboto,
                        fontWeight = FontWeight.Normal, fontSize = 15.sp, color = TextWhite
                    )
                    Text(
                        text = fechaDisplay, fontFamily = Roboto,
                        fontWeight = FontWeight.Normal, fontSize = 14.sp,
                        color = TextWhite.copy(alpha = 0.85f)
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

        // ── CONTENIDO ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // ENCABEZADO: título + contador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LISTA DE ALUMNOS", fontFamily = Roboto,
                    fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextBlue
                )
                Box(
                    modifier = Modifier
                        .background(EduconnectBlue, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$marcados/$totalAlumnos", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextWhite
                    )
                }
            }

            // AVISO si ya se registró: fecha y hora del registro y hasta cuándo se puede editar
            if (hayAsistenciaPrevia) {
                val bloqueada = !dentroDelPlazo
                val titulo = when {
                    editando -> "Editando asistencia"
                    bloqueada -> "Asistencia registrada · edición cerrada"
                    else -> "¡Asistencia registrada!"
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (bloqueada) BackgroundWhite else BackgroundStatusOrangeLight,
                            RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, if (bloqueada) BorderLight else BorderOrange, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = if (bloqueada) R.drawable.clock_darkgray else R.drawable.checklist_orange),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = titulo,
                            fontFamily = Roboto, fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (bloqueada) TextBlue else TextOrange
                        )
                        registro?.let { FilaAviso("Registrada:", fechaHoraTexto(it), TextBlue) }
                        limite?.let {
                            FilaAviso(
                                "Editable hasta:",
                                fechaHoraTexto(it),
                                if (bloqueada) TextSecondary else TextOrange
                            )
                        }
                        if (bloqueada) {
                            Text(
                                text = "Para corregirla usa el Historial de asistencias.",
                                fontFamily = Roboto, fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // ACCIONES RÁPIDAS (solo si se puede editar)
            if (editable) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Marcar todos presentes
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (todosPresentes) {
                                    if (hayAsistenciaPrevia) restaurarPrevia() else estadoAsistencia.clear()
                                } else {
                                    listaAlumnos.forEach { estadoAsistencia[it.id] = "A" }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Checkbox(
                            checked = todosPresentes,
                            onCheckedChange = {
                                if (todosPresentes) {
                                    if (hayAsistenciaPrevia) restaurarPrevia() else estadoAsistencia.clear()
                                } else listaAlumnos.forEach { estadoAsistencia[it.id] = "A" }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AsistenciaVerde,
                                uncheckedColor = BorderBlue
                            )
                        )
                        Text(
                            text = "Presentes",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = if (todosPresentes) AsistenciaVerde else TextBlue
                        )
                    }

                    // Limpiar (solo en un registro nuevo; al editar no se puede dejar alumnos sin estado)
                    if (!hayAsistenciaPrevia) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = hayAlgunMarcado) { estadoAsistencia.clear() },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = { estadoAsistencia.clear() },
                                enabled = hayAlgunMarcado,
                                colors = CheckboxDefaults.colors(
                                    uncheckedColor = if (hayAlgunMarcado) FaltaRojo else BorderLight
                                )
                            )
                            Text(
                                text = "Limpiar",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = if (hayAlgunMarcado) FaltaRojo else TextSecondary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // LISTA SCROLLEABLE
            if (cargando && listaAlumnos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EduconnectBlue)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listaAlumnos.forEachIndexed { index, alumno ->
                        val estado = estadoAsistencia[alumno.id] ?: ""
                        FilaAsistencia(
                            numero = index + 1,
                            alumno = alumno,
                            estado = estado,
                            editable = editable,
                            onCambiar = { estadoAsistencia[alumno.id] = siguienteEstadoAsistencia(estado) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // PROGRESO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso de asistencia", fontFamily = Roboto,
                    fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextBlue
                )
                Text(
                    text = "$porcentaje%", fontFamily = Roboto,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextBlue
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(13.dp)
                    .background(BorderLight, RoundedCornerShape(10.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progreso)
                        .height(13.dp)
                        .background(EduconnectBlue, RoundedCornerShape(10.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // BOTONES
            when {
                // 1) Registro nuevo
                !hayAsistenciaPrevia -> Button(
                    onClick = {
                        val sinMarcar = listaAlumnos.count { (estadoAsistencia[it.id] ?: "").isEmpty() }
                        if (sinMarcar > 0) {
                            Toast.makeText(context, "Faltan $sinMarcar alumno(s) por marcar", Toast.LENGTH_SHORT).show()
                        } else {
                            mostrarDialogConfirmacion = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = botonHabilitado && !cargando,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (botonHabilitado) AccentOrange else BorderLight
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.save_white),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Guardar y notificar",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = if (botonHabilitado) TextWhite else TextSecondary
                    )
                }

                // 2) Editando: Cancelar + Actualizar
                editando -> Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { cancelarEdicion() },
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = "Cancelar",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = EduconnectBlue
                        )
                    }
                    Button(
                        onClick = {
                            if (!dentroDelPlazo) {
                                Toast.makeText(context, "Terminó el plazo de edición ($HORAS_EDICION h)", Toast.LENGTH_SHORT).show()
                            } else {
                                mostrarDialogConfirmacion = true
                            }
                        },
                        enabled = botonHabilitado,
                        modifier = Modifier.weight(1.5f).height(54.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (botonHabilitado) AccentOrange else BorderLight
                        )
                    ) {
                        Text(
                            text = "Actualizar",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (botonHabilitado) TextWhite else TextSecondary
                        )
                    }
                }

                // 3) Registrada y dentro del plazo: botón Editar asistencia
                dentroDelPlazo -> Button(
                    onClick = { editando = true },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduconnectBlue)
                ) {
                    Text(
                        text = "Editar asistencia",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextWhite
                    )
                }

                // 4) Registrada y fuera del plazo: sin botón (solo lectura)
                else -> {}
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Alumnos"
        )
    }
}

// Conteo del popup de confirmación: solo número y texto, sin cuadro
@Composable
private fun ConteoResumen(valor: Int, texto: String, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$valor",
            fontFamily = Roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = color
        )
        Text(
            text = texto,
            fontFamily = Roboto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = color
        )
    }
}

// Fila "Etiqueta:  valor" del aviso; la etiqueta tiene ancho fijo para que ambas filas queden alineadas
@Composable
private fun FilaAviso(etiqueta: String, valor: String, colorValor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = etiqueta,
            fontFamily = Roboto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.width(112.dp)
        )
        Text(
            text = valor,
            fontFamily = Roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = colorValor
        )
    }
}

// Fila de un alumno: ícono + nombre completo + un solo botón de estado (A / T / F)
@Composable
private fun FilaAsistencia(
    numero: Int,
    alumno: AlumnoItem,
    estado: String,
    editable: Boolean,
    onCambiar: () -> Unit
) {
    val filaBorder = when (estado) {
        "A" -> BorderVerdePresente
        "F" -> BorderRojoFalta
        "T" -> BorderAmarilloTard
        else -> BorderBlue
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(10.dp))
            .border(1.5.dp, filaBorder.copy(alpha = if (editable) 1f else 0.6f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(
                id = when (estado) {
                    "A"  -> R.drawable.user_blue_check
                    "F"  -> R.drawable.user_blue_cross
                    "T"  -> R.drawable.user_alert
                    else -> R.drawable.user_grey
                }
            ),
            contentDescription = when (estado) {
                "A"  -> "Presente"
                "F"  -> "Falta"
                "T"  -> "Tardanza"
                else -> "Sin marcar"
            },
            modifier = Modifier.size(25.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "$numero. ${nombreAlumno(alumno.apellidos, alumno.nombres)}".uppercase(),
            fontFamily = Roboto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = TextBlue,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        BotonEstadoAsistencia(estado = estado, editable = editable, onClick = onCambiar)
    }
}

// Un solo botón que cambia de estado con cada toque. Muestra solo la letra con el color del estado.
// Bloqueado: mismo color, pero más suave y sin respuesta al toque.
@Composable
private fun BotonEstadoAsistencia(estado: String, editable: Boolean, onClick: () -> Unit) {
    val color = colorEstadoAsistencia(estado)
    val fondo = when {
        color == null -> BackgroundWhite
        editable -> color
        else -> color.copy(alpha = 0.15f)
    }
    Box(
        modifier = Modifier
            .size(width = 52.dp, height = 38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(fondo)
            .border(1.5.dp, color ?: BorderLight, RoundedCornerShape(8.dp))
            .clickable(enabled = editable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (estado.isEmpty()) "–" else estado,
            fontFamily = Roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = when {
                color == null -> TextSecondary
                editable -> TextWhite
                else -> color
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegistroAsistenciaPreview() {
    EduConnectAppTheme {
        RegistroAsistenciaScreen(
            docenteId = "uuid-docente",
            gradoId = 1L,
            seccionId = 1L,
            cursoId = 1L,
            grado = "1er Grado",
            seccion = "A",
            curso = "Matemáticas",
            listaAlumnos = listOf(
                AlumnoItem(1L, "Ana Beatriz", "Garcia Vera"),
                AlumnoItem(2L, "Pedro", "Sanchez Vega"),
                AlumnoItem(3L, "Maria", "Sanchez Vela")
            ),
            asistenciaPrevia = mapOf(1L to "A", 2L to "F", 3L to "T"),
            horaRegistro = "08:05:00",
            fechaHoja = "2026-10-03"
        )
    }
}
