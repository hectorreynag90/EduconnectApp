package com.educonnectapp.ui.screens.docente.asistencia

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockClock
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BarraProgresoEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.ContadorEdu
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.coloresDeTono
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

// Tono de cada estado: A = turquesa, T = amarillo, F = granate
private fun tonoEstado(estado: String): TonoEdu? = when (estado) {
    "A" -> TonoEdu.VERDE
    "T" -> TonoEdu.AMARILLO
    "F" -> TonoEdu.CORAL
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

// =====================================================================
// REGISTRO DE ASISTENCIA (rediseño 2026)
// Cabecera fija azul oscuro · contadores A/T/F · lista con un botón por alumno · botones fijos abajo
// =====================================================================

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
    val fechaCorta = remember(fechaHoy) {
        try {
            formatoPeru("EEEE d 'de' MMMM").format(formatoPeru("yyyy-MM-dd").parse(fechaHoy)!!)
                .replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { fechaHoy }
    }
    // Fecha y hora del aviso: "Lun 19/10/2026 · 08:05 a. m."
    val formatoFechaHora = remember { formatoPeru("EEE dd/MM/yyyy · hh:mm a") }
    fun fechaHoraTexto(d: Date) = formatoFechaHora.format(d).replaceFirstChar { it.uppercase() }

    val estadoAsistencia = remember { mutableStateMapOf<Long, String>() }
    val context = LocalContext.current

    // Popups: confirmación -> guardando
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

    val todosListos     = listaAlumnos.all { (snapshotEstados[it.id] ?: "").isNotEmpty() }
    val botonHabilitado = if (hayAsistenciaPrevia) huboCambios else todosListos

    val hayAlgunMarcado  = snapshotEstados.values.any { it == "A" || it == "F" || it == "T" }
    val todosPresentes   = listaAlumnos.isNotEmpty() && listaAlumnos.all { snapshotEstados[it.id] == "A" }

    fun alternarTodosPresentes() {
        if (todosPresentes) {
            if (hayAsistenciaPrevia) restaurarPrevia() else estadoAsistencia.clear()
        } else {
            listaAlumnos.forEach { estadoAsistencia[it.id] = "A" }
        }
    }

    // ── POPUP 1: Confirmación ────────────────────────────────────────────
    if (mostrarDialogConfirmacion) {
        PopupConfirmacionEdu(
            titulo = if (hayAsistenciaPrevia) "¿Confirmar cambios?" else "¿Confirmar registro?",
            mensaje = "Revisa el resumen antes de guardar. Los padres recibirán un aviso.",
            textoConfirmar = if (hayAsistenciaPrevia) "Actualizar" else "Guardar",
            colorConfirmar = if (hayAsistenciaPrevia) TipoBotonEdu.ACTUALIZAR.fondo else TipoBotonEdu.GUARDAR.fondo,
            onCancelar = { mostrarDialogConfirmacion = false },
            onConfirmar = {
                mostrarDialogConfirmacion = false
                mostrarDialogCargando = true
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContadorEdu(presentes, "Asistió", TonoEdu.VERDE, Modifier.weight(1f))
                ContadorEdu(tardanzas, "Tardanza", TonoEdu.AMARILLO, Modifier.weight(1f))
                ContadorEdu(ausentes, "Faltó", TonoEdu.CORAL, Modifier.weight(1f))
            }
            Text(
                "Total: $totalAlumnos alumnos", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp, color = EduTextoSecundario
            )
        }
    }

    // ── POPUP 2: Guardando ───────────────────────────────────────────────
    if (mostrarDialogCargando) {
        PopupProcesandoEdu(
            titulo = if (hayAsistenciaPrevia) "Actualizando asistencia..." else "Guardando asistencia...",
            mensaje = "Estamos avisando a los padres."
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // ── CABECERA FIJA ────────────────────────────────────────────────
        CabeceraEdu(
            titulo = if (editando) "Editar asistencia" else curso,
            subtitulo = if (editando) "$curso · $grado $seccion" else "$grado $seccion · $fechaCorta",
            onVolver = { if (editando) cancelarEdicion() else onBack() },
            colorFondo = ColorModulo.Asistencia
        )

        // ── CONTENIDO ────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 16.dp, end = 16.dp, top = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // CONTADORES
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ContadorEdu(presentes, "Asistió", TonoEdu.VERDE, Modifier.weight(1f))
                ContadorEdu(tardanzas, "Tardanza", TonoEdu.AMARILLO, Modifier.weight(1f))
                ContadorEdu(ausentes, "Faltó", TonoEdu.CORAL, Modifier.weight(1f))
            }

            // AVISO si ya se registró: fecha y hora del registro y hasta cuándo se puede editar
            if (hayAsistenciaPrevia) {
                val bloqueada = !dentroDelPlazo
                val titulo = when {
                    editando -> "Editando asistencia"
                    bloqueada -> "Asistencia registrada · edición cerrada"
                    else -> "Asistencia registrada"
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            when {
                                editando -> coloresDeTono(TonoEdu.AMARILLO).fondo
                                bloqueada -> EduAzulOscuroClaro
                                else -> EduTealClaro
                            },
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                editando -> Icons.Rounded.Edit
                                bloqueada -> Icons.Rounded.LockClock
                                else -> Icons.Rounded.Lock
                            },
                            contentDescription = null,
                            tint = when {
                                editando -> coloresDeTono(TonoEdu.AMARILLO).texto
                                bloqueada -> EduTextoSecundario
                                else -> EduTealOscuro
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp,
                            color = EduTexto
                        )
                        registro?.let { FilaAviso("Registrada:", fechaHoraTexto(it)) }
                        limite?.let { FilaAviso("Editable hasta:", fechaHoraTexto(it)) }
                        if (bloqueada) {
                            Text(
                                "Para corregirla usa el Historial de asistencias.",
                                fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                                color = EduTextoSecundario
                            )
                        }
                    }
                }
            }

            // LEYENDA + ACCIONES RÁPIDAS
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Toca: A → T → F", modifier = Modifier.weight(1f),
                    fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
                )
                if (editable) {
                    if (!hayAsistenciaPrevia && hayAlgunMarcado) {
                        AccionTexto("Limpiar", EduCoral) { estadoAsistencia.clear() }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    AccionTexto(if (todosPresentes) "Deshacer" else "Todos asistieron", EduTealOscuro) {
                        alternarTodosPresentes()
                    }
                }
            }

            // LISTA DE ALUMNOS (desplazable)
            if (cargando && listaAlumnos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EduTeal)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    TarjetaEdu(relleno = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                        listaAlumnos.forEachIndexed { index, alumno ->
                            val estado = estadoAsistencia[alumno.id] ?: ""
                            FilaAsistencia(
                                numero = index + 1,
                                alumno = alumno,
                                estado = estado,
                                editable = editable,
                                ultima = index == listaAlumnos.lastIndex,
                                onCambiar = { estadoAsistencia[alumno.id] = siguienteEstadoAsistencia(estado) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // ── ZONA FIJA INFERIOR: progreso y botones ───────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (editable && !hayAsistenciaPrevia) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Marcados", modifier = Modifier.weight(1f), fontFamily = Nunito,
                        fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduTextoSecundario
                    )
                    Text(
                        "$marcados/$totalAlumnos", fontFamily = Nunito, fontWeight = FontWeight.Black,
                        fontSize = 13.5.sp, color = EduTexto
                    )
                }
                BarraProgresoEdu(progreso = progreso, color = if (todosListos) EduTeal else EduAmarillo)
            }

            when {
                // 1) Registro nuevo
                !hayAsistenciaPrevia -> BotonPrincipalEdu(
                    texto = "Guardar y notificar",
                    tipo = TipoBotonEdu.GUARDAR,
                    icono = Icons.Rounded.Save,
                    habilitado = botonHabilitado && !cargando,
                    onClick = {
                        val sinMarcar = listaAlumnos.count { (estadoAsistencia[it.id] ?: "").isEmpty() }
                        if (sinMarcar > 0) {
                            Toast.makeText(context, "Faltan $sinMarcar alumno(s) por marcar", Toast.LENGTH_SHORT).show()
                        } else {
                            mostrarDialogConfirmacion = true
                        }
                    }
                )

                // 2) Editando: Cancelar + Actualizar
                editando -> Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BotonSecundarioEdu(
                        texto = "Cancelar",
                        onClick = { cancelarEdicion() },
                        modifier = Modifier.weight(1f)
                    )
                    BotonPrincipalEdu(
                        texto = "Actualizar",
                        tipo = TipoBotonEdu.ACTUALIZAR,
                        icono = Icons.Rounded.Sync,
                        habilitado = botonHabilitado,
                        modifier = Modifier.weight(1.4f),
                        onClick = {
                            if (!dentroDelPlazo) {
                                Toast.makeText(context, "Terminó el plazo de edición ($HORAS_EDICION h)", Toast.LENGTH_SHORT).show()
                            } else {
                                mostrarDialogConfirmacion = true
                            }
                        }
                    )
                }

                // 3) Registrada y dentro del plazo: Editar asistencia
                dentroDelPlazo -> BotonPrincipalEdu(
                    texto = "Editar asistencia",
                    tipo = TipoBotonEdu.ACTUALIZAR,
                    icono = Icons.Rounded.Edit,
                    onClick = { editando = true }
                )

                // 4) Registrada y fuera del plazo: solo lectura
                else -> {}
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
}

// Acción de texto pequeña ("Todos asistieron", "Limpiar")
@Composable
private fun AccionTexto(texto: String, color: Color, onClick: () -> Unit) {
    Text(
        texto,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.10f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.sp, color = color
    )
}

// Fila "Etiqueta:  valor" del aviso; la etiqueta tiene ancho fijo para que ambas filas queden alineadas
@Composable
private fun FilaAviso(etiqueta: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            etiqueta, modifier = Modifier.width(104.dp),
            fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
        )
        Text(valor, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.sp, color = EduEtiqueta)
    }
}

// Fila de un alumno: iniciales en círculo + nombre + un solo botón de estado (A / T / F)
@Composable
private fun FilaAsistencia(
    numero: Int,
    alumno: AlumnoItem,
    estado: String,
    editable: Boolean,
    ultima: Boolean,
    onCambiar: () -> Unit
) {
    val nombre = nombreAlumno(alumno.apellidos, alumno.nombres)
    val iniciales = nombre.replace(",", " ").split(" ").filter { it.isNotBlank() }.take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
    val tono = tonoEstado(estado)
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = editable) { onCambiar() }
                .padding(vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(tono?.let { coloresDeTono(it).fondo } ?: EduAzulOscuroClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    iniciales, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.sp,
                    color = tono?.let { coloresDeTono(it).texto } ?: EduTextoSecundario
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "$numero. $nombre",
                fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = EduTexto,
                maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            BotonEstadoAsistencia(estado = estado, editable = editable, onClick = onCambiar)
        }
        if (!ultima) Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EduFondo))
    }
}

// Un solo botón que cambia de estado con cada toque. Muestra la letra con el color del estado.
// Bloqueado: tono suave y sin respuesta al toque.
@Composable
private fun BotonEstadoAsistencia(estado: String, editable: Boolean, onClick: () -> Unit) {
    val tono = tonoEstado(estado)
    val c = tono?.let { coloresDeTono(it) }
    val forma = RoundedCornerShape(12.dp)
    val fondo = when {
        c == null -> Color.White
        editable -> c.solido
        else -> c.fondo
    }
    val colorLetra = when {
        c == null -> EduTextoSecundario
        !editable -> c.texto
        estado == "T" -> EduTexto          // texto oscuro sobre amarillo
        else -> Color.White
    }
    Box(
        modifier = Modifier
            .size(width = 52.dp, height = 40.dp)
            .clip(forma)
            .background(fondo)
            .border(1.5.dp, if (c == null) EduBorde else Color.Transparent, forma)
            .clickable(enabled = editable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (estado.isEmpty()) "–" else estado,
            fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp, color = colorLetra
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun RegistroAsistenciaPreview() {
    EduConnectAppTheme {
        RegistroAsistenciaScreen(
            docenteId = "1",
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
            fechaHoja = "2026-10-19"
        )
    }
}
