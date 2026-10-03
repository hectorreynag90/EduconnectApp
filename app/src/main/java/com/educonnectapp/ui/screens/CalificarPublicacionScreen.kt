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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import kotlinx.coroutines.delay
import com.educonnectapp.R
import com.educonnectapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// Datos de la tarea/evaluación
data class DetallePublicacionItem(
    val titulo: String,
    val tipo: String,               // "Tarea" | "Evaluación"
    val cursoNombre: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val fechaEntrega: String,       // "yyyy-MM-dd"
    val descripcion: String,
    val nombreAdjunto: String? = null,
    val estado: String = "ACTIVA"
)

// Entrega de un alumno (solo tareas: las evaluaciones son avisos). estado usa los códigos del backend:
// "PENDIENTE" | "ENTREGADO" | "NO_ENTREGADO" | "RENDIDO" | "NO_RINDIO" | "CALIFICADO"
// (notaNumerica, notaLiteral y observacion se mantienen solo por compatibilidad: ya no se usan)
data class AlumnoCalificacionItem(
    val alumnoId: Long,
    val codigo: String,
    val nombreCompleto: String,
    val estado: String = "PENDIENTE",
    val notaNumerica: Double? = null,
    val notaLiteral: String? = null,
    val observacion: String = "",
    val registrado: Boolean = false    // true si el docente ya registró la entrega de este alumno
)

// Estados que ve el docente (se traducen a los códigos del backend al guardar)
private const val UI_ENTREGADO = "ENTREGADO"
private const val UI_PENDIENTE = "PENDIENTE"
private const val UI_NO_ENTREGADO = "NO_ENTREGADO"

private val ESTADOS_ENTREGADO = setOf("ENTREGADO", "RENDIDO", "CALIFICADO")
private val ESTADOS_NO_ENTREGADO = setOf("NO_ENTREGADO", "NO_RINDIO")

// Código del backend -> estado que ve el docente
private fun estadoUi(estado: String): String = when (estado) {
    in ESTADOS_ENTREGADO -> UI_ENTREGADO
    in ESTADOS_NO_ENTREGADO -> UI_NO_ENTREGADO
    else -> UI_PENDIENTE
}

// Cada toque pasa al siguiente estado: Pendiente -> Entregado -> No entregado -> Pendiente
private fun siguienteEstado(estado: String): String = when (estado) {
    UI_PENDIENTE -> UI_ENTREGADO
    UI_ENTREGADO -> UI_NO_ENTREGADO
    else -> UI_PENDIENTE
}

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
    val esEvaluacion = publicacion?.tipo == "Evaluación"

    // MODO: consulta (solo lectura) o registro (editable)
    var editando by remember { mutableStateOf(false) }
    // Al llegar la lista actualizada del backend (después de guardar), vuelve a modo consulta
    LaunchedEffect(alumnos) { editando = false }

    // Estados editables (se reinician con cada lista nueva o al cancelar)
    var version by remember { mutableStateOf(0) }
    val estadosUi = remember(alumnos, version) {
        mutableStateMapOf<Long, String>().apply { alumnos.forEach { put(it.alumnoId, estadoUi(it.estado)) } }
    }

    // POPUPS: confirmación -> "Guardando..." -> "Entregas guardadas"
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

    // ¿Ya se registró alguna vez? -> el botón dice "EDITAR ENTREGAS"
    val yaRegistrada = alumnos.any { it.registrado }
    val totalEntregados = alumnos.count { it.estado in ESTADOS_ENTREGADO }
    val totalNoEntregados = alumnos.count { it.estado in ESTADOS_NO_ENTREGADO }
    val totalPendientes = alumnos.size - totalEntregados - totalNoEntregados

    fun cancelarEdicion() {
        version++            // descarta lo editado
        editando = false
    }

    // El botón "atrás" del celular cancela la edición en lugar de salir
    BackHandler(enabled = editando) { cancelarEdicion() }

    // Resultado final de un alumno. Si el docente no cambió su estado, se devuelve tal cual.
    // Nunca se envían notas.
    fun resultado(a: AlumnoCalificacionItem): AlumnoCalificacionItem {
        val elegido = estadosUi[a.alumnoId] ?: UI_PENDIENTE
        if (elegido == estadoUi(a.estado)) return a
        val codigo = when (elegido) {
            UI_ENTREGADO -> "ENTREGADO"
            UI_NO_ENTREGADO -> "NO_ENTREGADO"
            else -> "PENDIENTE"
        }
        return a.copy(estado = codigo, notaNumerica = null, notaLiteral = null)
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
                            esEvaluacion -> "Aviso de evaluación"
                            editando && yaRegistrada -> "Editar entregas"
                            editando -> "Registrar entregas"
                            else -> "Seguimiento de entregas"
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
                // TARJETA: tipo, título, fecha límite y adjunto
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
                                text = p.tipo.uppercase(),
                                fontFamily = Roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = p.titulo,
                                fontFamily = Roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextBlue
                            )
                            Text(
                                text = (if (esEvaluacion) "Fecha de la evaluación: " else "Fecha límite: ") +
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

                // EVALUACIÓN: solo aviso (temario + nota informativa), sin seguimiento
                if (esEvaluacion) {
                    item { AvisoEvaluacion(publicacion?.descripcion ?: "") }
                }

                // TAREA: seguimiento de entregas
                if (!esEvaluacion) {
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
                                        .clickable { alumnos.forEach { estadosUi[it.alumnoId] = UI_ENTREGADO } }
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // CONTADORES (solo en consulta)
                    if (!editando) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Contador(totalEntregados, "Entregaron", StatusGreen, Modifier.weight(1f))
                                Contador(totalPendientes, "Pendientes", EduconnectBlue, Modifier.weight(1f))
                                Contador(totalNoEntregados, "No entregaron", StatusErrorRed, Modifier.weight(1f))
                            }
                        }
                    }

                    // ENCABEZADO DE COLUMNAS
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (editando) {
                                Text(
                                    text = "Toca el estado para cambiarlo",
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
                            val estado = estadosUi[a.alumnoId] ?: UI_PENDIENTE
                            FilaAlumno(
                                alumno = a,
                                borde = colorEstado(estado).copy(alpha = 0.6f)
                            ) {
                                EstadoPill(estado, onClick = { estadosUi[a.alumnoId] = siguienteEstado(estado) })
                            }
                        } else {
                            FilaAlumno(
                                alumno = a,
                                borde = BorderBlue.copy(alpha = 0.5f)
                            ) {
                                EstadoPill(estadoUi(a.estado))
                            }
                        }
                    }
                }
            }

            // BOTONES INFERIORES (solo tareas)
            if (!esEvaluacion) {
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
                                    resultado(original).takeIf { it.estado != original.estado }
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
                            containerColor = if (yaRegistrada) EduconnectBlue else AccentOrange
                        )
                    ) {
                        Text(
                            text = if (yaRegistrada) "EDITAR ENTREGAS" else "REGISTRAR ENTREGAS",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextWhite
                        )
                    }
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
        val entregaron = cambios.count { it.estado in ESTADOS_ENTREGADO }
        val noEntregaron = cambios.count { it.estado in ESTADOS_NO_ENTREGADO }
        val pendientes = cambios.size - entregaron - noEntregaron
        val detalle = buildList {
            if (entregaron > 0) add("$entregaron entregaron")
            if (noEntregaron > 0) add("$noEntregaron no entregaron")
            if (pendientes > 0) add("$pendientes " + if (pendientes == 1) "pendiente" else "pendientes")
        }.joinToString(", ")
        AlertDialog(
            onDismissRequest = { cambiosPorConfirmar = null },
            title = {
                Text(
                    text = "¿Guardar entregas?",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextBlue
                )
            },
            text = {
                Text(
                    text = "Se actualizarán ${cambios.size} " + (if (cambios.size == 1) "alumno" else "alumnos") +
                            " ($detalle).",
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

    // 3) ENTREGAS GUARDADAS (se cierra sola o al tocar)
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
                    text = "Entregas guardadas",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = StatusGreen
                )
            }
        }
    }
}

// Evaluación: se muestra el temario y una nota de que no lleva registro de entrega
@Composable
private fun AvisoEvaluacion(descripcion: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (descripcion.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(16.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "TEMARIO",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Text(
                    text = descripcion,
                    fontFamily = Roboto,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduconnectBlue.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                .border(1.dp, BorderBlue, RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.notification_white),
                contentDescription = null,
                colorFilter = ColorFilter.tint(EduconnectBlue),
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = "Las evaluaciones se publican como aviso para los padres. No requieren registro de entrega.",
                fontFamily = Roboto,
                fontSize = 14.sp,
                color = TextBlue
            )
        }
    }
}

// Ancho fijo de la columna ESTADO: así todas las filas quedan alineadas
private val ANCHO_ESTADO = 140.dp

@Composable
private fun Contador(valor: Int, texto: String, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = valor.toString(),
            fontFamily = Roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = color
        )
        Text(
            text = texto,
            fontFamily = Roboto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = color,
            maxLines = 1
        )
    }
}

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
        textAlign = if (centrado) TextAlign.Center else TextAlign.Start,
        modifier = modifier
    )
}

// Fila: tarjeta con nombre (columna flexible) + celda de estado de ancho fijo
@Composable
private fun FilaAlumno(
    alumno: AlumnoCalificacionItem,
    borde: Color,
    celdaEstado: @Composable () -> Unit
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
// Relleno si se puede tocar (modo registro); suave si es solo lectura.
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
                AlumnoCalificacionItem(1L, "PMU0001", "Pérez García, Pedro", "ENTREGADO", registrado = true),
                AlumnoCalificacionItem(2L, "PMU0002", "López Ruiz, Ana", "NO_ENTREGADO", registrado = true),
                AlumnoCalificacionItem(3L, "PMU0003", "Díaz Soto, Luis", "PENDIENTE")
            )
        )
    }
}
