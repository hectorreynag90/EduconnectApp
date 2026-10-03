package com.educonnectapp.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import com.educonnectapp.R
import com.educonnectapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// Datos de la tarea/examen
data class DetallePublicacionItem(
    val titulo: String,
    val tipo: String,               // "Tarea" | "Examen"
    val cursoNombre: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val fechaEntrega: String,       // "yyyy-MM-dd"
    val descripcion: String,
    val nombreAdjunto: String? = null,
    val estado: String = "ACTIVA"
)

// Calificación de un alumno. estado usa los códigos del backend:
// "PENDIENTE" | "ENTREGADO" | "NO_ENTREGADO" | "RENDIDO" | "NO_RINDIO" | "CALIFICADO"
data class AlumnoCalificacionItem(
    val alumnoId: Long,
    val codigo: String,
    val nombreCompleto: String,
    val estado: String = "PENDIENTE",
    val notaNumerica: Double? = null,
    val notaLiteral: String? = null,   // "AD" | "A" | "B" | "C"
    val observacion: String = "",
    val registrado: Boolean = false    // true si el docente ya guardó una calificación para este alumno
)

// Estados que ve el docente (se traducen a los códigos del backend al guardar)
private const val UI_ENTREGADO = "ENTREGADO"
private const val UI_PENDIENTE = "PENDIENTE"
private const val UI_NO_ENTREGADO = "NO_ENTREGADO"

private val ESTADOS_ENTREGADO = setOf("ENTREGADO", "RENDIDO", "CALIFICADO")
private val ESTADOS_NO_ENTREGADO = setOf("NO_ENTREGADO", "NO_RINDIO")

// Nota que se registra automáticamente cuando no entregó (desaprobatoria)
private const val NOTA_NO_ENTREGADO = "C"

// Código del backend -> estado que ve el docente
private fun estadoUi(estado: String): String = when (estado) {
    in ESTADOS_ENTREGADO -> UI_ENTREGADO
    in ESTADOS_NO_ENTREGADO -> UI_NO_ENTREGADO
    else -> UI_PENDIENTE
}

// Escala literal del MINEDU
private val LITERALES = listOf("AD", "A", "B", "C")

private fun fechaLargaCalificar(fecha: String): String = try {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    SimpleDateFormat("EEE dd 'de' MMMM yyyy", Locale("es", "PE"))
        .format(sdf.parse(fecha)!!).replaceFirstChar { it.uppercase() }
} catch (e: Exception) { fecha }

@Composable
fun CalificarPublicacionScreen(
    publicacion: DetallePublicacionItem? = null,
    alumnos: List<AlumnoCalificacionItem> = emptyList(),
    cargando: Boolean = false,
    guardando: Boolean = false,
    descargandoAdjunto: Boolean = false,
    onAbrirAdjunto: () -> Unit = {},
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    // Solo los alumnos que cambiaron, con el estado final para el backend
    onGuardar: (List<AlumnoCalificacionItem>) -> Unit = {}
) {
    val context = LocalContext.current
    val esExamen = publicacion?.tipo == "Examen"

    // MODO: consulta (solo lectura) o calificación (editable)
    var editando by remember { mutableStateOf(false) }
    // Al llegar la lista actualizada del backend (después de guardar), vuelve a modo consulta
    LaunchedEffect(alumnos) { editando = false }

    // Valores editables (se reinician con cada lista nueva o al cancelar)
    var version by remember { mutableStateOf(0) }
    val estadosUi = remember(alumnos, version) {
        mutableStateMapOf<Long, String>().apply { alumnos.forEach { put(it.alumnoId, estadoUi(it.estado)) } }
    }
    val notas = remember(alumnos, version) {
        mutableStateMapOf<Long, String>().apply { alumnos.forEach { a -> a.notaLiteral?.let { put(a.alumnoId, it) } } }
    }

    // POPUPS: confirmación -> "Guardando..." -> "Calificación Guardada"
    var cambiosPorConfirmar by remember { mutableStateOf<List<AlumnoCalificacionItem>?>(null) }
    var esperandoGuardado by remember { mutableStateOf(false) }
    var alumnosAlGuardar by remember { mutableStateOf<List<AlumnoCalificacionItem>>(emptyList()) }
    var mostrarExito by remember { mutableStateOf(false) }

    // Cuando termina de guardar: si llegó la lista actualizada, fue un éxito
    // (si falló, la lista no cambia y MainActivity muestra el error)
    LaunchedEffect(guardando) {
        if (!guardando && esperandoGuardado) {
            esperandoGuardado = false
            if (alumnos != alumnosAlGuardar) mostrarExito = true
        }
    }
    // El popup de éxito se cierra solo
    LaunchedEffect(mostrarExito) {
        if (mostrarExito) {
            delay(1800)
            mostrarExito = false
        }
    }

    // ¿Ya se calificó alguna vez? -> el botón dice "EDITAR CALIFICACIONES" en lugar de "CALIFICAR"
    val yaCalificada = alumnos.any { it.registrado }
    val totalEntregados = alumnos.count { it.estado in ESTADOS_ENTREGADO }
    val totalNoEntregados = alumnos.count { it.estado in ESTADOS_NO_ENTREGADO }
    val totalPendientes = alumnos.size - totalEntregados - totalNoEntregados

    fun cancelarEdicion() {
        version++            // descarta lo editado
        editando = false
    }

    // El botón "atrás" del celular cancela la edición en lugar de salir
    BackHandler(enabled = editando) { cancelarEdicion() }

    // Resultado final de un alumno (la observación existente no se toca)
    fun resultado(a: AlumnoCalificacionItem): AlumnoCalificacionItem {
        when (estadosUi[a.alumnoId] ?: UI_PENDIENTE) {
            UI_PENDIENTE ->
                return a.copy(estado = "PENDIENTE", notaNumerica = null, notaLiteral = null)
            UI_NO_ENTREGADO ->   // no entregó: se registra con nota C
                return a.copy(
                    estado = if (esExamen) "NO_RINDIO" else "NO_ENTREGADO",
                    notaLiteral = NOTA_NO_ENTREGADO,
                    notaNumerica = null
                )
        }
        val nota = notas[a.alumnoId]
        return when {
            nota != null -> a.copy(estado = "CALIFICADO", notaLiteral = nota, notaNumerica = null)
            a.estado == "CALIFICADO" && a.notaNumerica != null -> a   // nota numérica antigua: se respeta
            else -> a.copy(estado = if (esExamen) "RENDIDO" else "ENTREGADO", notaLiteral = null, notaNumerica = null)
        }
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
                    onClick = { if (editando) cancelarEdicion() else onBack() },
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
                        text = when {
                            editando && yaCalificada -> "Editar calificaciones"
                            editando -> "Calificar"
                            esExamen -> "Detalle del examen"
                            else -> "Detalle de la tarea"
                        },
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = publicacion?.let { "${it.cursoNombre} · ${it.gradoNombre} Sec. ${it.seccionNombre}" } ?: "",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
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

        if (cargando && alumnos.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EduconnectBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // TARJETA: título, fecha límite y adjunto
                publicacion?.let { p ->
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BackgroundWhite, RoundedCornerShape(16.dp))
                                .border(1.5.dp, BorderBlue, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = p.titulo,
                                fontFamily = Roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextBlue
                            )
                            Text(
                                text = (if (esExamen) "Fecha del examen: " else "Fecha límite: ") +
                                        (if (p.fechaEntrega.isNotEmpty()) fechaLargaCalificar(p.fechaEntrega) else "—"),
                                fontFamily = Roboto,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = TextOrange
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (p.nombreAdjunto != null) EduconnectBlue.copy(alpha = 0.06f) else BackgroundLight,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(1.dp, if (p.nombreAdjunto != null) BorderBlue else BorderMedium, RoundedCornerShape(10.dp))
                                    .clickable(enabled = p.nombreAdjunto != null && !descargandoAdjunto) { onAbrirAdjunto() }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.paperclip_lightgray),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = when {
                                        p.nombreAdjunto == null -> "Sin archivo adjunto"
                                        descargandoAdjunto -> "Descargando..."
                                        else -> p.nombreAdjunto
                                    },
                                    fontFamily = Roboto,
                                    fontWeight = if (p.nombreAdjunto != null) FontWeight.SemiBold else FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = if (p.nombreAdjunto != null) EduconnectBlue else TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // RESUMEN + ACCIÓN RÁPIDA
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ALUMNOS (${alumnos.size})",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextBlue
                        )
                        if (editando) {
                            Text(
                                text = "Todos entregaron",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = EduconnectBlue,
                                modifier = Modifier
                                    .clickable {
                                        alumnos.forEach {
                                            if (estadosUi[it.alumnoId] == UI_NO_ENTREGADO) notas.remove(it.alumnoId)
                                            estadosUi[it.alumnoId] = UI_ENTREGADO
                                        }
                                    }
                                    .padding(vertical = 4.dp)
                            )
                        } else {
                            Text(
                                text = "$totalEntregados entreg. · $totalPendientes pend. · $totalNoEntregados no entreg.",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // ENCABEZADO DE COLUMNAS
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (editando) {
                            Text(
                                text = "Toca el estado o la nota para cambiarlos",
                                fontFamily = Roboto,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                        EncabezadoColumnas()
                    }
                }

                // LISTA DE ALUMNOS
                items(alumnos, key = { it.alumnoId }) { a ->
                    if (editando) {
                        FilaAlumnoEditable(
                            alumno = a,
                            estado = estadosUi[a.alumnoId] ?: UI_PENDIENTE,
                            nota = notas[a.alumnoId],
                            // Cada toque pasa al siguiente estado: Pendiente -> Entregado -> No entregado -> Pendiente
                            onCambiarEstado = {
                                val anterior = estadosUi[a.alumnoId] ?: UI_PENDIENTE
                                val nuevo = when (anterior) {
                                    UI_PENDIENTE -> UI_ENTREGADO
                                    UI_ENTREGADO -> UI_NO_ENTREGADO
                                    else -> UI_PENDIENTE
                                }
                                estadosUi[a.alumnoId] = nuevo
                                when (nuevo) {
                                    UI_NO_ENTREGADO -> notas[a.alumnoId] = NOTA_NO_ENTREGADO   // C automática
                                    else -> notas.remove(a.alumnoId)
                                }
                            },
                            // Cada toque pasa a la siguiente nota: — -> AD -> A -> B -> C -> —
                            onCambiarNota = {
                                val actual = notas[a.alumnoId]
                                val siguiente = when (val i = LITERALES.indexOf(actual)) {
                                    -1 -> LITERALES.first()
                                    LITERALES.lastIndex -> null
                                    else -> LITERALES[i + 1]
                                }
                                if (siguiente == null) notas.remove(a.alumnoId) else notas[a.alumnoId] = siguiente
                            }
                        )
                    } else {
                        FilaAlumnoConsulta(alumno = a)
                    }
                }
            }

            // BOTONES INFERIORES
            HorizontalDivider(color = BorderBlue.copy(alpha = 0.4f))
            if (editando) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { cancelarEdicion() },
                        enabled = !guardando,
                        modifier = Modifier.weight(1f).height(52.dp),
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
                            val cambios = alumnos.mapNotNull { original ->
                                resultado(original).takeIf {
                                    it.estado != original.estado || it.notaNumerica != original.notaNumerica ||
                                            it.notaLiteral != original.notaLiteral
                                }
                            }
                            if (cambios.isEmpty()) {
                                Toast.makeText(context, "No hay cambios para guardar", Toast.LENGTH_SHORT).show()
                                editando = false
                            } else {
                                cambiosPorConfirmar = cambios   // primero se confirma
                            }
                        },
                        enabled = !guardando,
                        modifier = Modifier.weight(1.4f).height(52.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                    ) {
                        Text(
                            text = "GUARDAR",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextWhite
                        )
                    }
                }
            } else {
                Button(
                    onClick = { version++; editando = true },
                    enabled = alumnos.isNotEmpty() && publicacion?.estado != "ANULADA",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (yaCalificada) EduconnectBlue else AccentOrange
                    )
                ) {
                    Text(
                        text = if (yaCalificada) "EDITAR CALIFICACIONES" else "CALIFICAR",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextWhite
                    )
                }
            }
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Inicio"
        )
    }

    // 1) CONFIRMACIÓN
    cambiosPorConfirmar?.let { cambios ->
        val entregadosCambio = cambios.count { it.estado in ESTADOS_ENTREGADO }
        val noEntregadosCambio = cambios.count { it.estado in ESTADOS_NO_ENTREGADO }
        val conNota = cambios.count { it.notaLiteral != null }
        AlertDialog(
            onDismissRequest = { cambiosPorConfirmar = null },
            title = {
                Text(
                    text = "¿Guardar calificaciones?",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextBlue
                )
            },
            text = {
                Text(
                    text = "Se actualizarán ${cambios.size} " + (if (cambios.size == 1) "alumno" else "alumnos") +
                            " ($entregadosCambio entregados, $noEntregadosCambio no entregados, $conNota con nota).",
                    fontFamily = Roboto,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        cambiosPorConfirmar = null
                        alumnosAlGuardar = alumnos
                        esperandoGuardado = true
                        onGuardar(cambios)
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                ) {
                    Text("Confirmar", fontFamily = Roboto, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { cambiosPorConfirmar = null }) {
                    Text("Revisar", fontFamily = Roboto, fontWeight = FontWeight.SemiBold, color = EduconnectBlue)
                }
            },
            containerColor = BackgroundWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 2) GUARDANDO... (no se puede cerrar mientras guarda)
    if (guardando) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Column(
                modifier = Modifier
                    .background(BackgroundWhite, RoundedCornerShape(20.dp))
                    .padding(horizontal = 40.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    color = EduconnectBlue,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(52.dp)
                )
                Text(
                    text = "Guardando...",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextBlue
                )
            }
        }
    }

    // 3) CALIFICACIÓN GUARDADA (se cierra sola o al tocar)
    if (mostrarExito) {
        Dialog(onDismissRequest = { mostrarExito = false }) {
            Column(
                modifier = Modifier
                    .background(BackgroundWhite, RoundedCornerShape(20.dp))
                    .clickable { mostrarExito = false }
                    .padding(horizontal = 36.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.checkcircle_green),
                    contentDescription = null,
                    modifier = Modifier.size(76.dp)
                )
                Text(
                    text = "Calificación Guardada",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = StatusGreen
                )
            }
        }
    }
}

// Anchos fijos de las columnas: así todas las filas quedan alineadas
private val ANCHO_ESTADO = 132.dp
private val ANCHO_NOTA = 60.dp

@Composable
private fun EncabezadoColumnas() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextoEncabezado("ALUMNO", Modifier.weight(1f))
        TextoEncabezado("ESTADO", Modifier.width(ANCHO_ESTADO), centrado = true)
        TextoEncabezado("NOTA", Modifier.width(ANCHO_NOTA), centrado = true)
    }
}

@Composable
private fun TextoEncabezado(texto: String, modifier: Modifier, centrado: Boolean = false) {
    Text(
        text = texto,
        fontFamily = Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = TextSecondary,
        textAlign = if (centrado) androidx.compose.ui.text.style.TextAlign.Center else androidx.compose.ui.text.style.TextAlign.Start,
        modifier = modifier
    )
}

// Fila base: tarjeta con nombre (columna flexible) + celdas de ancho fijo
@Composable
private fun FilaAlumno(
    alumno: AlumnoCalificacionItem,
    borde: Color,
    celdaEstado: @Composable () -> Unit,
    celdaNota: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .background(BackgroundWhite, RoundedCornerShape(12.dp))
            .border(1.dp, borde, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = alumno.nombreCompleto,
            fontFamily = Roboto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = TextBlue,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Box(modifier = Modifier.width(ANCHO_ESTADO), contentAlignment = Alignment.Center) { celdaEstado() }
        Box(modifier = Modifier.width(ANCHO_NOTA), contentAlignment = Alignment.Center) { celdaNota() }
    }
}

// Color, ícono y texto de cada estado
private fun colorEstado(estado: String): Color = when (estado) {
    UI_ENTREGADO -> StatusGreen
    UI_NO_ENTREGADO -> StatusErrorRed
    else -> EduconnectBlue
}

private fun iconoEstado(estado: String): Int = when (estado) {
    UI_ENTREGADO -> R.drawable.check_green_dark
    UI_NO_ENTREGADO -> R.drawable.close_white
    else -> R.drawable.clock_darkgray
}

private fun textoEstado(estado: String): String = when (estado) {
    UI_ENTREGADO -> "Entregado"
    UI_NO_ENTREGADO -> "No entregado"
    else -> "Pendiente"
}

// Estado con ícono: ✓ verde = Entregado | reloj azul = Pendiente | ✕ rojo = No entregado.
// Relleno (con flecha) si se puede tocar; suave si es solo lectura.
@Composable
private fun EstadoPill(estado: String, onClick: (() -> Unit)? = null) {
    val color = colorEstado(estado)
    val editable = onClick != null
    Row(
        modifier = Modifier
            .width(ANCHO_ESTADO)
            .height(34.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (editable) color else color.copy(alpha = 0.10f))
            .then(if (editable) Modifier.clickable { onClick?.invoke() } else Modifier)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = iconoEstado(estado)),
            contentDescription = null,
            colorFilter = when {
                editable -> ColorFilter.tint(TextWhite)
                estado == UI_ENTREGADO -> null
                else -> ColorFilter.tint(color)
            },
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = textoEstado(estado),
            fontFamily = Roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (editable) TextWhite else color,
            maxLines = 1
        )
    }
}

// MODO CONSULTA: solo lectura
@Composable
private fun FilaAlumnoConsulta(alumno: AlumnoCalificacionItem) {
    val estado = estadoUi(alumno.estado)
    FilaAlumno(
        alumno = alumno,
        borde = BorderBlue.copy(alpha = 0.5f),
        celdaEstado = { EstadoPill(estado) },
        celdaNota = {
            val nota = alumno.notaLiteral
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        when {
                            nota == null -> BackgroundLight
                            nota == NOTA_NO_ENTREGADO && estado == UI_NO_ENTREGADO -> StatusErrorRed
                            else -> EduconnectBlue
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nota ?: "—",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (nota != null) TextWhite else TextSecondary
                )
            }
        }
    )
}

// MODO CALIFICACIÓN: el estado y la nota cambian con cada toque
// (No entregado = nota C fija; Pendiente = sin nota)
@Composable
private fun FilaAlumnoEditable(
    alumno: AlumnoCalificacionItem,
    estado: String,
    nota: String?,
    onCambiarEstado: () -> Unit,
    onCambiarNota: () -> Unit
) {
    val notaEditable = estado == UI_ENTREGADO
    FilaAlumno(
        alumno = alumno,
        borde = colorEstado(estado).copy(alpha = 0.6f),
        celdaEstado = { EstadoPill(estado, onClick = onCambiarEstado) },
        celdaNota = {
            Box(
                modifier = Modifier
                    .width(ANCHO_NOTA)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when {
                            estado == UI_NO_ENTREGADO -> StatusErrorRed.copy(alpha = 0.08f)
                            notaEditable && nota != null -> EduconnectBlue
                            notaEditable -> BackgroundWhite
                            else -> BackgroundLight
                        }
                    )
                    .border(
                        1.5.dp,
                        when {
                            estado == UI_NO_ENTREGADO -> StatusErrorRed.copy(alpha = 0.6f)
                            !notaEditable -> BorderMedium.copy(alpha = 0.5f)
                            nota != null -> EduconnectBlue
                            else -> BorderBlue
                        },
                        RoundedCornerShape(10.dp)
                    )
                    .clickable(enabled = notaEditable) { onCambiarNota() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nota ?: "—",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = when {
                        estado == UI_NO_ENTREGADO -> StatusErrorRed
                        notaEditable && nota != null -> TextWhite
                        else -> TextSecondary
                    }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CalificarPublicacionPreview() {
    EduConnectAppTheme {
        CalificarPublicacionScreen(
            publicacion = DetallePublicacionItem(
                "Ejercicios Cap. 5 - Fracciones", "Tarea", "Matemáticas", "1er", "A",
                "2026-10-08", "Resolver los ejercicios del 1 al 20.", "Practica.pdf"
            ),
            alumnos = listOf(
                AlumnoCalificacionItem(1L, "PMU0001", "Pérez García, Pedro", "CALIFICADO", notaLiteral = "A", registrado = true),
                AlumnoCalificacionItem(2L, "PMU0002", "López Ruiz, Ana", "NO_ENTREGADO", notaLiteral = "C", registrado = true),
                AlumnoCalificacionItem(3L, "PMU0003", "Díaz Soto, Luis", "ENTREGADO", registrado = true)
            )
        )
    }
}
