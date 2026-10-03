package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// Aviso de evaluación: el docente solo informa fecha y temario a los padres.
// No se sube ningún archivo ni se registran notas.
@Composable
fun NuevaEvaluacionScreen(
    listaDestinatarios: List<SeccionDestinatario> = emptyList(),
    cursoNombre: String = "",
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onPublicar: (
        titulo: String,
        descripcion: String,
        adjunto: String,              // siempre "" (las evaluaciones no llevan adjunto)
        seccionDestinatario: SeccionDestinatario,
        fechaEvaluacion: String,
        fechaPublicacion: String,
        hora: String
    ) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    val fechaActual = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
    }

    // Si solo hay un destinatario (el curso elegido en la pantalla anterior), ya viene seleccionado
    var destinatarioSeleccionado by remember { mutableStateOf<SeccionDestinatario?>(listaDestinatarios.singleOrNull()) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var fechaEvaluacion by remember { mutableStateOf("") }
    var fechaEvaluacionDisplay by remember { mutableStateOf("Seleccionar fecha") }

    if (mostrarDatePicker) {
        val calendario = Calendar.getInstance()
        android.app.DatePickerDialog(
            context,
            { _, anio, mes, dia ->
                fechaEvaluacion = String.format("%04d-%02d-%02d", anio, mes + 1, dia)
                val cal = Calendar.getInstance()
                cal.set(anio, mes, dia)
                fechaEvaluacionDisplay = SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
                    .format(cal.time).replaceFirstChar { it.uppercase() }
                mostrarDatePicker = false
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).also {
            it.datePicker.minDate = System.currentTimeMillis()
            // Si se cancela, no volver a abrir el calendario en la siguiente recomposición
            it.setOnDismissListener { mostrarDatePicker = false }
            it.show()
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBack,
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
                        text = "Aviso de evaluación",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "Curso: $cursoNombre",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.8f)
                    )
                    Text(
                        text = fechaActual,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = TextWhite.copy(alpha = 0.7f)
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

        // FORMULARIO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // DESTINATARIOS
            Text(
                text = "DESTINATARIOS",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlue
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = destinatarioSeleccionado?.let {
                        "${it.gradoNombre} Sec. ${it.seccionNombre} - ${it.totalPadres} alumnos"
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = {
                        Text(text = "Seleccione sección", color = TextSecondary, fontSize = 16.sp, fontFamily = Roboto)
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.users_darkgray),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { dropdownExpanded = true }) {
                            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = EduconnectBlue)
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextBlue,
                        unfocusedBorderColor = BorderMedium,
                        focusedTextColor = TextBlue,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = BackgroundWhite,
                        unfocusedContainerColor = BackgroundWhite
                    ),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                )
                Box(modifier = Modifier.matchParentSize().padding(end = 48.dp).clickable { dropdownExpanded = true })
                DropdownMenu(expanded = dropdownExpanded, onDismissRequest = { dropdownExpanded = false }) {
                    listaDestinatarios.forEach { dest ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "${dest.gradoNombre} Sec. ${dest.seccionNombre} - ${dest.totalPadres} alumnos",
                                    fontFamily = Roboto,
                                    fontSize = 14.sp,
                                    color = TextBlue
                                )
                            },
                            onClick = { destinatarioSeleccionado = dest; dropdownExpanded = false }
                        )
                    }
                }
            }

            // TÍTULO
            Text(
                text = "EVALUACIÓN",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlue
            )
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                placeholder = { Text(text = "Ej. Práctica calificada N° 2", color = TextSecondary, fontSize = 16.sp) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderMedium,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = BackgroundWhite,
                    unfocusedContainerColor = BackgroundWhite
                ),
                modifier = Modifier.fillMaxWidth().height(54.dp),
                singleLine = true
            )

            // TEMARIO
            Text(
                text = "TEMARIO",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlue
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                placeholder = { Text(text = "Temas que entran y materiales que debe traer...", color = TextSecondary, fontSize = 16.sp) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderMedium,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = BackgroundWhite,
                    unfocusedContainerColor = BackgroundWhite
                ),
                modifier = Modifier.fillMaxWidth().heightIn(min = 110.dp),
                maxLines = 8
            )

            // FECHA DE LA EVALUACIÓN
            Text(
                text = "FECHA DE LA EVALUACIÓN",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlue
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderMedium, RoundedCornerShape(10.dp))
                    .clickable { mostrarDatePicker = true }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.agenda_blue),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = fechaEvaluacionDisplay,
                        fontFamily = Roboto,
                        fontSize = 15.sp,
                        color = if (fechaEvaluacion.isEmpty()) TextSecondary else TextBlue,
                        fontWeight = if (fechaEvaluacion.isEmpty()) FontWeight.Normal else FontWeight.SemiBold
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.chevrondown_gray),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÓN PUBLICAR
            Button(
                onClick = {
                    val dest = destinatarioSeleccionado
                    if (dest == null || titulo.isBlank() || descripcion.isBlank() || fechaEvaluacion.isBlank()) {
                        android.widget.Toast.makeText(
                            context, "Completa sección, evaluación, temario y fecha", android.widget.Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    val fechaPub = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val hora = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    onPublicar(titulo, descripcion, "", dest, fechaEvaluacion, fechaPub, hora)
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EduconnectBlue)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sendstatement_white),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Publicar aviso",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Inicio"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NuevaEvaluacionPreview() {
    EduConnectAppTheme {
        NuevaEvaluacionScreen(
            listaDestinatarios = listOf(
                SeccionDestinatario(1L, 1L, 1L, "2do", "A", "Matemáticas", 28),
                SeccionDestinatario(2L, 2L, 1L, "3er", "B", "Matemáticas", 30)
            ),
        )
    }
}
