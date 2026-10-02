package com.educonnectapp.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundStatusOrangeLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.BorderOrange
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextOrange
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val AsistenciaVerde      = Color(0xFF26C281)
private val FaltaRojo            = Color(0xFFE53935)
private val TardanzaAmarillo     = Color(0xFFF59E0B)
private val BgVerdeClaro         = Color(0xFFE8F8F2)   // fondo fila presente
private val BgRojoClaro          = Color(0xFFFDECEC)   // fondo fila falta
private val BgAmarilloClaro      = Color(0xFFFFF8E1)   // fondo fila tardanza
private val BorderVerdePresente  = Color(0xFF26C281)
private val BorderRojoFalta      = Color(0xFFE53935)
private val BorderAmarilloTard   = Color(0xFFF59E0B)

data class AlumnoItem(
    val id: Long,
    val nombres: String,
    val apellidos: String
)

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
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onGuardado: (presentes: Int, ausentes: Int, tardanzas: Int, estados: Map<Long, String>, hora: String, fecha: String) -> Unit = { _, _, _, _, _, _ -> }
) {
    val fechaHoy = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    val fechaDisplay = remember {
        SimpleDateFormat("EEEE dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
    }

    val estadoAsistencia = remember { mutableStateMapOf<Long, String>() }
    val context = LocalContext.current

    // Estados para los dialogs del flujo de 3 pasos
    var mostrarDialogConfirmacion by remember { mutableStateOf(false) }
    var mostrarDialogCargando by remember { mutableStateOf(false) }

    // Precargar estados si hay asistencia previa
    LaunchedEffect(asistenciaPrevia) {
        if (asistenciaPrevia.isNotEmpty()) {
            estadoAsistencia.clear()
            estadoAsistencia.putAll(asistenciaPrevia)
        }
    }

    // Efecto para el loading: espera 2 segundos y luego navega
    LaunchedEffect(mostrarDialogCargando) {
        if (mostrarDialogCargando) {
            val horaActual = SimpleDateFormat("hh:mm a", Locale("es", "PE")).format(Date())
            kotlinx.coroutines.delay(2000)
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

    val hayAsistenciaPrevia = asistenciaPrevia.isNotEmpty()

    // Forzar que Compose observe TODOS los cambios del map (size + values)
    val snapshotEstados = estadoAsistencia.toMap()

    val huboCambios = hayAsistenciaPrevia &&
            snapshotEstados.any { (id, estado) -> asistenciaPrevia[id] != estado } ||
            (hayAsistenciaPrevia && listaAlumnos.any { snapshotEstados[it.id] != asistenciaPrevia[it.id] })

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
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.save_white),
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .background(AccentOrange, RoundedCornerShape(22.dp))
                        .padding(10.dp)
                )
            },
            title = {
                Text(
                    text = "¿Confirmar registro?",
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
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Presentes
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFE8F8F2), RoundedCornerShape(10.dp))
                                .border(1.5.dp, AsistenciaVerde, RoundedCornerShape(10.dp))
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$presentes",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = AsistenciaVerde
                            )
                            Text(
                                text = "Presentes",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = AsistenciaVerde
                            )
                        }
                        // Tardanzas
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFFFF8E1), RoundedCornerShape(10.dp))
                                .border(1.5.dp, TardanzaAmarillo, RoundedCornerShape(10.dp))
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$tardanzas",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = TardanzaAmarillo
                            )
                            Text(
                                text = "Tardanzas",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = TardanzaAmarillo
                            )
                        }
                        // Ausentes
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFFDECEC), RoundedCornerShape(10.dp))
                                .border(1.5.dp, FaltaRojo, RoundedCornerShape(10.dp))
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$ausentes",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = FaltaRojo
                            )
                            Text(
                                text = "Ausentes",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = FaltaRojo
                            )
                        }
                    }
                    Text(
                        text = "Total: $totalAlumnos alumnos",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
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
                        text = "Registro de Asistencia", fontFamily = Roboto,
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

            // AVISO si hay asistencia previa
            if (hayAsistenciaPrevia) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundStatusOrangeLight, RoundedCornerShape(8.dp))
                        .border(1.dp, BorderOrange, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.checklist_orange),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "¡Asistencia Registrada!. Puedes modificar el estado de algún alumno.",
                        fontFamily = Roboto, fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, color = TextOrange
                    )
                }
            }

            // ACCIONES RÁPIDAS: checkboxes Marcar todos / Limpiar todo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox: Marcar todos presentes
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (todosPresentes) {
                                estadoAsistencia.clear()
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
                            if (todosPresentes) estadoAsistencia.clear()
                            else listaAlumnos.forEach { estadoAsistencia[it.id] = "A" }
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

                // Checkbox: Limpiar todo
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = hayAlgunMarcado) {
                            estadoAsistencia.clear()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = {
                            estadoAsistencia.clear()
                        },
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
            }

            // LISTA SCROLLEABLE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listaAlumnos.forEachIndexed { index, alumno ->
                    val estado = estadoAsistencia[alumno.id] ?: ""

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
                            .border(1.5.dp, filaBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
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

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${index + 1}. ${alumno.apellidos}, ${alumno.nombres}".uppercase(),
                                fontFamily = Roboto,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = TextBlue,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // ── BOTÓN A ──
                        Box(
                            modifier = Modifier
                                .size(width = 48.dp, height = 36.dp)
                                .background(
                                    if (estado == "A") AsistenciaVerde else BackgroundWhite,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.5.dp,
                                    if (estado == "A") AsistenciaVerde else BorderLight,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    estadoAsistencia[alumno.id] = if (estado == "A") "" else "A"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "A",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (estado == "A") TextWhite else TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // ── BOTÓN T ──
                        Box(
                            modifier = Modifier
                                .size(width = 44.dp, height = 36.dp)
                                .background(
                                    if (estado == "T") TardanzaAmarillo else BackgroundWhite,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.5.dp,
                                    if (estado == "T") TardanzaAmarillo else BorderLight,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    estadoAsistencia[alumno.id] = if (estado == "T") "" else "T"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "T",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (estado == "T") TextWhite else TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // ── BOTÓN F ──
                        Box(
                            modifier = Modifier
                                .size(width = 44.dp, height = 36.dp)
                                .background(
                                    if (estado == "F") FaltaRojo else BackgroundWhite,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.5.dp,
                                    if (estado == "F") FaltaRojo else BorderLight,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    estadoAsistencia[alumno.id] = if (estado == "F") "" else "F"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "F",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (estado == "F") TextWhite else TextSecondary
                            )
                        }
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

            // BOTÓN GUARDAR — ahora abre el dialog de confirmación
            Button(
                onClick = {
                    if (!hayAsistenciaPrevia) {
                        val sinMarcar = listaAlumnos.count { alumno ->
                            (estadoAsistencia[alumno.id] ?: "").isEmpty()
                        }
                        if (sinMarcar > 0) {
                            Toast.makeText(
                                context,
                                "Faltan $sinMarcar alumno(s) por marcar",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                    }
                    mostrarDialogConfirmacion = true
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = botonHabilitado,
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
                    text = if (hayAsistenciaPrevia) "Actualizar asistencia" else "Guardar y notificar",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (botonHabilitado) TextWhite else TextSecondary
                )
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
            asistenciaPrevia = mapOf(
                1L to "A",
                2L to "F",
                3L to "A"
            )
        )
    }
}
