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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.AsistentTardanza
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite

private val VerdePresente    = Color(0xFF26C281)
private val RojoFalta        = Color(0xFFE53935)
private val AmarilloTardanza = Color(0xFFF59E0B)
private val BgVerdeDetalle   = Color(0xFFE8F8F2)
private val BgRojoDetalle    = Color(0xFFFDECEC)
private val BgAmarilloDetalle= Color(0xFFFFF8E1)
private val BordeGrisClaro   = Color(0xFFDDE3EA)

/** Registro de un alumno dentro del detalle de asistencia */
data class DetalleAlumnoItem(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val estado: String   // "A" = Presente, "T" = Tardanza, "F" = Falta
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAsistenciaDocenteScreen(
    item: HistorialItem,
    alumnos: List<DetalleAlumnoItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onGuardarCambios: (alumnosEditados: List<DetalleAlumnoItem>, motivo: String, onDone: () -> Unit) -> Unit = { _, _, _ -> }
) {
    // ── ESTADO EDICIÓN ───────────────────────────────────────────────────────
    var modoEdicion by remember { mutableStateOf(false) }
    val estadosEditados = remember(alumnos) {
        mutableStateListOf(*alumnos.map { it.estado }.toTypedArray())
    }
    val hayCambios = remember(estadosEditados.toList(), alumnos) {
        estadosEditados.toList() != alumnos.map { it.estado }
    }
    var mostrarMotivo    by remember { mutableStateOf(false) }
    var motivo           by remember { mutableStateOf("") }
    var mostrarCargando  by remember { mutableStateOf(false) }
    var mostrarResultado by remember { mutableStateOf(false) }
    // Guardamos los cambios efectuados para mostrar en el popup resultado
    data class CambioResumen(val nombre: String, val estadoAnterior: String, val estadoNuevo: String)
    var cambiosResumen   by remember { mutableStateOf<List<CambioResumen>>(emptyList()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // KPIs dinámicos según modo
    val estadosActuales = if (modoEdicion) estadosEditados.toList() else alumnos.map { it.estado }
    val totalAlumnos = alumnos.size
    val presentes    = estadosActuales.count { it == "A" }
    val tardanzas    = estadosActuales.count { it == "T" }
    val faltas       = estadosActuales.count { it == "F" }
    // Asistencia del día = Presentes + Tardanzas (la tardanza cuenta como asistió)
    val porcentaje   = if (totalAlumnos > 0) (presentes + tardanzas).toFloat() / totalAlumnos.toFloat() else 0f

    fun estadoTexto(e: String) = when(e) { "A" -> "Presente"; "T" -> "Tardanza"; else -> "Falta" }

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
                        text = "Detalle de Asistencia",
                        fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, color = TextWhite
                    )
                    Text(
                        text = item.curso,
                        fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp, color = TextWhite
                    )
                    Text(
                        text = "${item.grado} - Sec. ${item.seccion}",
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

        // ── CONTENIDO ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // FECHA Y HORA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderBlue, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.agenda_blue),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.fechaDisplay,
                        fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = TextBlue
                    )
                }
                Text(
                    text = item.hora,
                    fontFamily = Roboto, fontWeight = FontWeight.Normal,
                    fontSize = 14.sp, color = TextSecondary
                )
            }

            // KPI CARDS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                KpiCard(
                    valor = "$presentes", etiqueta = "PRESENTES",
                    colorValor = VerdePresente, bgColor = BgVerdeDetalle,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    valor = "$tardanzas", etiqueta = "TARDANZAS",
                    colorValor = AccentOrange, bgColor = AsistentTardanza,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    valor = "$faltas", etiqueta = "FALTAS",
                    colorValor = RojoFalta, bgColor = BgRojoDetalle,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    valor = "$totalAlumnos", etiqueta = "TOTAL",
                    colorValor = TextBlue, bgColor = Color(0xFFEBF0FF),
                    modifier = Modifier.weight(1f)
                )
            }

            // BARRA DE PROGRESO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderBlue, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Asistencia del día",
                        fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = TextBlue
                    )
                    Text(
                        text = "${(porcentaje * 100).toInt()}%",
                        fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 14.sp, color = VerdePresente
                    )
                }
                LinearProgressIndicator(
                    progress = { porcentaje },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = VerdePresente,
                    trackColor = Color(0xFFE0E0E0)
                )
            }

            // ENCABEZADO LISTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ALUMNOS",
                    fontFamily = Roboto, fontWeight = FontWeight.Bold,
                    fontSize = 15.sp, color = TextBlue
                )
                Box(
                    modifier = Modifier
                        .background(EduconnectBlue, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$totalAlumnos",
                        fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 15.sp, color = TextWhite
                    )
                }
            }

            // LISTA SCROLLEABLE
            if (alumnos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay alumnos en este registro.",
                        fontFamily = Roboto, fontSize = 14.sp, color = TextSecondary
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    alumnos.forEachIndexed { index, alumno ->
                        if (modoEdicion) {
                            // MODO EDICIÓN: botones A/F activos
                            DetalleAlumnoRowEditable(
                                alumno = alumno,
                                estadoActual = estadosEditados[index],
                                onEstadoChange = { nuevoEstado ->
                                    estadosEditados[index] = nuevoEstado
                                }
                            )
                        } else {
                            // MODO VISTA: borde gris claro
                            DetalleAlumnoRow(alumno = alumno)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // ── ZONA INFERIOR: BOTONES ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundLight)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!modoEdicion) {
                // Botón EDITAR ASISTENCIA
                Button(
                    onClick = { modoEdicion = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduconnectBlue)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.goasistent_white),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Editar Asistencia",
                        fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 18.sp, color = TextWhite
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Botón CANCELAR
                    Button(
                        onClick = {
                            // Restaurar estados originales
                            alumnos.forEachIndexed { i, a -> estadosEditados[i] = a.estado }
                            modoEdicion = false
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB0BEC5)
                        )
                    ) {
                        Text(
                            text = "Cancelar",
                            fontFamily = Roboto, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, color = TextWhite
                        )
                    }
                    // Botón GUARDAR CAMBIO (activo solo si hay cambios)
                    Button(
                        onClick = { if (hayCambios) mostrarMotivo = true },
                        enabled = hayCambios,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdePresente,
                            disabledContainerColor = Color(0xFFB0BEC5)
                        )
                    ) {
                        Text(
                            text = "Guardar Cambio",
                            fontFamily = Roboto, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, color = TextWhite
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
            itemActivo = "Alumnos"
        )
    }

    // ── POPUP: CARGANDO ──────────────────────────────────────────────────────
    if (mostrarCargando) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            containerColor = androidx.compose.ui.graphics.Color.White,
            shape = RoundedCornerShape(16.dp),
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = EduconnectBlue, strokeWidth = 4.dp)
                    Text(
                        text = "Actualizando Registros...",
                        fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 17.sp, color = TextBlue
                    )
                }
            }
        )
    }

    // ── POPUP: RESULTADO ─────────────────────────────────────────────────────
    if (mostrarResultado) {
        AlertDialog(
            onDismissRequest = { mostrarResultado = false },
            confirmButton = {
                Button(
                    onClick = { mostrarResultado = false },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduconnectBlue)
                ) {
                    Text("Aceptar", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, color = TextWhite)
                }
            },
            containerColor = androidx.compose.ui.graphics.Color.White,
            shape = RoundedCornerShape(16.dp),
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Ícono + título
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.size(42.dp)
                            .background(BgVerdeDetalle, CircleShape),
                            contentAlignment = Alignment.Center) {
                            Text("✓", fontSize = 22.sp, color = VerdePresente)
                        }
                        Column {
                            Text("¡Registros Actualizados!", fontFamily = Roboto,
                                fontWeight = FontWeight.Bold, fontSize = 17.sp, color = VerdePresente)
                            Text("${cambiosResumen.size} registro(s) modificado(s)",
                                fontFamily = Roboto, fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                    HorizontalDivider(thickness = 1.dp, color = BordeGrisClaro)
                    // Lista de cambios
                    cambiosResumen.forEach { c ->
                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(c.nombre, fontFamily = Roboto,
                                    fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextBlue,
                                    maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            }
                            // Estado anterior
                            Box(modifier = Modifier
                                .background(
                                    when(c.estadoAnterior) { "A" -> BgVerdeDetalle; "T" -> BgAmarilloDetalle; else -> BgRojoDetalle },
                                    RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)) {
                                Text(estadoTexto(c.estadoAnterior), fontFamily = Roboto,
                                    fontWeight = FontWeight.Bold, fontSize = 11.sp,
                                    color = when(c.estadoAnterior) { "A" -> VerdePresente; "T" -> AmarilloTardanza; else -> RojoFalta })
                            }
                            Text("→", fontSize = 14.sp, color = TextSecondary)
                            // Estado nuevo
                            Box(modifier = Modifier
                                .background(
                                    when(c.estadoNuevo) { "A" -> BgVerdeDetalle; "T" -> BgAmarilloDetalle; else -> BgRojoDetalle },
                                    RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)) {
                                Text(estadoTexto(c.estadoNuevo), fontFamily = Roboto,
                                    fontWeight = FontWeight.Bold, fontSize = 11.sp,
                                    color = when(c.estadoNuevo) { "A" -> VerdePresente; "T" -> AmarilloTardanza; else -> RojoFalta })
                            }
                        }
                    }
                }
            }
        )
    }

    // ── BOTTOM SHEET: MOTIVO DEL CAMBIO ─────────────────────────────────────
    if (mostrarMotivo) {
        ModalBottomSheet(
            onDismissRequest = { mostrarMotivo = false },
            sheetState = sheetState,
            containerColor = BackgroundWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Motivo del cambio",
                    fontFamily = Roboto, fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, color = TextBlue
                )
                Text(
                    text = "Este motivo será enviado al director como aviso del cambio realizado.",
                    fontFamily = Roboto, fontSize = 13.sp, color = TextSecondary
                )

                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    placeholder = {
                        Text(
                            text = "Ej: Error en el registro inicial de asistencia...",
                            fontFamily = Roboto, fontSize = 14.sp, color = TextSecondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EduconnectBlue,
                        unfocusedBorderColor = BordeGrisClaro
                    ),
                    maxLines = 5
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { mostrarMotivo = false },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB0BEC5)
                        )
                    ) {
                        Text(
                            text = "Cancelar",
                            fontFamily = Roboto, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, color = TextWhite
                        )
                    }
                    Button(
                        onClick = {
                            if (motivo.isNotBlank()) {
                                val alumnosEditados = alumnos.mapIndexed { i, a ->
                                    a.copy(estado = estadosEditados[i])
                                }
                                // Calcular resumen de cambios antes de guardar
                                cambiosResumen = alumnos.mapIndexedNotNull { i, a ->
                                    val nuevoEstado = estadosEditados[i]
                                    if (a.estado != nuevoEstado)
                                        CambioResumen(nombreAlumno(a.apellidos, a.nombres), a.estado, nuevoEstado)
                                    else null
                                }
                                val motivoIngresado = motivo   // guardar ANTES de limpiar el campo
                                mostrarMotivo  = false
                                modoEdicion    = false
                                motivo         = ""
                                mostrarCargando = true
                                onGuardarCambios(alumnosEditados, motivoIngresado) {
                                    mostrarCargando  = false
                                    mostrarResultado = true
                                }
                            }
                        },
                        enabled = motivo.isNotBlank(),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EduconnectBlue,
                            disabledContainerColor = Color(0xFFB0BEC5)
                        )
                    ) {
                        Text(
                            text = "Confirmar",
                            fontFamily = Roboto, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, color = TextWhite
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ── CARD MODO VISTA (borde gris claro) ───────────────────────────────────────
@Composable
private fun DetalleAlumnoRow(alumno: DetalleAlumnoItem) {
    val bgChip    = when(alumno.estado) { "A" -> BgVerdeDetalle; "T" -> AsistentTardanza; else -> BgRojoDetalle }
    val colorChip = when(alumno.estado) { "A" -> VerdePresente;  "T" -> AccentOrange;  else -> RojoFalta }
    val textoChip = when(alumno.estado) { "A" -> "✓ Presente";   "T" -> "⚠ Tardanza";      else -> "✗ Falta" }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(10.dp))
            .border(1.dp, BordeGrisClaro, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(
                id = when (alumno.estado) { "A" -> R.drawable.user_blue_check; "T" -> R.drawable.user_alert; else -> R.drawable.user_blue_cross }
            ),
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
        Text(
            text = nombreAlumno(alumno.apellidos, alumno.nombres),
            fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp, color = TextBlue,
            modifier = Modifier.weight(1f),
            maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Box(
            modifier = Modifier
                .background(bgChip, RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(text = textoChip, fontFamily = Roboto, fontWeight = FontWeight.Bold,
                fontSize = 12.sp, color = colorChip)
        }
    }
}

// ── CARD MODO EDICIÓN (botones A/T/F) ────────────────────────────────────────
@Composable
private fun DetalleAlumnoRowEditable(
    alumno: DetalleAlumnoItem,
    estadoActual: String,
    onEstadoChange: (String) -> Unit
) {
    val filaBorder = when(estadoActual) {
        "A" -> VerdePresente.copy(alpha = 0.5f)
        "T" -> AmarilloTardanza.copy(alpha = 0.5f)
        else -> RojoFalta.copy(alpha = 0.5f)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(10.dp))
            .border(1.5.dp, filaBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(
                id = when (estadoActual) { "A" -> R.drawable.user_blue_check; "T" -> R.drawable.user_alert; else -> R.drawable.user_blue_cross }
            ),
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
        Text(
            text = nombreAlumno(alumno.apellidos, alumno.nombres),
            fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp, color = TextBlue,
            modifier = Modifier.weight(1f),
            maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        // Botón A (Presente)
        Box(
            modifier = Modifier
                .background(if (estadoActual == "A") VerdePresente else BackgroundWhite, RoundedCornerShape(20.dp))
                .border(1.5.dp, VerdePresente, RoundedCornerShape(20.dp))
                .clickable { onEstadoChange("A") }
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("A", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                color = if (estadoActual == "A") TextWhite else VerdePresente)
        }
        // Botón T (Tardanza)
        Box(
            modifier = Modifier
                .background(if (estadoActual == "T") AmarilloTardanza else BackgroundWhite, RoundedCornerShape(20.dp))
                .border(1.5.dp, AmarilloTardanza, RoundedCornerShape(20.dp))
                .clickable { onEstadoChange("T") }
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("T", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                color = if (estadoActual == "T") TextWhite else AmarilloTardanza)
        }
        // Botón F (Falta)
        Box(
            modifier = Modifier
                .background(if (estadoActual == "F") RojoFalta else BackgroundWhite, RoundedCornerShape(20.dp))
                .border(1.5.dp, RojoFalta, RoundedCornerShape(20.dp))
                .clickable { onEstadoChange("F") }
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("F", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                color = if (estadoActual == "F") TextWhite else RojoFalta)
        }
    }
}

@Composable
private fun KpiCard(
    valor: String,
    etiqueta: String,
    colorValor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(10.dp))
            .border(1.dp, colorValor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
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

@Preview(showBackground = true)
@Composable
fun DetalleAsistenciaDocentePreview() {
    EduConnectAppTheme {
        DetalleAsistenciaDocenteScreen(
            item = HistorialItem(
                1L, "Personal Social", "1er Grado", "A",
                "2026-06-23", "Martes 23 de junio 2026", "03:48 p.m.",
                5, 0, 5
            ),
            alumnos = listOf(
                DetalleAlumnoItem(1L, "Ana Beatriz", "Garcia Vera", "A"),
                DetalleAlumnoItem(2L, "Maria Elena", "Lopez Cruz", "A"),
                DetalleAlumnoItem(3L, "Carlos", "Mendoza Paz", "A"),
                DetalleAlumnoItem(4L, "Pedro", "Sanchez Vega", "A"),
                DetalleAlumnoItem(5L, "Luis Miguel", "Torres Rios", "A"),
            )
        )
    }
}
