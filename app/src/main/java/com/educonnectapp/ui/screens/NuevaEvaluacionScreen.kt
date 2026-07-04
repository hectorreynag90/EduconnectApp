package com.educonnectapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
        adjunto: String,
        seccionDestinatario: SeccionDestinatario,
        fechaExamen: String,
        fechaPublicacion: String,
        hora: String
    ) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    val fechaActual = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
    }

    var destinatarioSeleccionado by remember { mutableStateOf<SeccionDestinatario?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var archivoUri by remember { mutableStateOf<Uri?>(null) }
    var nombreArchivo by remember { mutableStateOf("") }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var fechaExamen by remember { mutableStateOf("") }
    var fechaExamenDisplay by remember { mutableStateOf("Seleccionar fecha") }

    if (mostrarDatePicker) {
        val calendario = Calendar.getInstance()
        android.app.DatePickerDialog(
            context,
            { _, anio, mes, dia ->
                fechaExamen = String.format("%04d-%02d-%02d", anio, mes + 1, dia)
                val cal = Calendar.getInstance()
                cal.set(anio, mes, dia)
                fechaExamenDisplay = SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
                    .format(cal.time).replaceFirstChar { it.uppercase() }
                mostrarDatePicker = false
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).also {
            it.datePicker.minDate = System.currentTimeMillis()
            it.show()
        }
    }

    val archivoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            archivoUri = it
            nombreArchivo = it.lastPathSegment ?: "archivo"
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
                        text = "Nuevo Examen",
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
                text = "TÍTULO DEL EXAMEN",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlue
            )
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                placeholder = { Text(text = "Ingrese el título", color = TextSecondary, fontSize = 16.sp) },
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

            // INSTRUCCIONES
            Text(
                text = "INSTRUCCIONES",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlue
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                placeholder = { Text(text = "Instrucciones del examen...", color = TextSecondary, fontSize = 16.sp) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderMedium,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = BackgroundWhite,
                    unfocusedContainerColor = BackgroundWhite
                ),
                modifier = Modifier.fillMaxWidth().height(80.dp),
                maxLines = 6
            )

            // FECHA DEL EXAMEN
            Text(
                text = "FECHA DEL EXAMEN",
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
                        text = fechaExamenDisplay,
                        fontFamily = Roboto,
                        fontSize = 15.sp,
                        color = if (fechaExamen.isEmpty()) TextSecondary else TextBlue,
                        fontWeight = if (fechaExamen.isEmpty()) FontWeight.Normal else FontWeight.SemiBold
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.chevrondown_gray),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            // ADJUNTO
            Text(
                text = "ADJUNTO",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlue
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderBlue, RoundedCornerShape(10.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.paperclip_lightgray),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = if (nombreArchivo.isNotEmpty()) nombreArchivo else "Añadir archivo",
                        fontFamily = Roboto,
                        fontSize = 15.sp,
                        color = if (nombreArchivo.isNotEmpty()) EduconnectBlue else TextSecondary
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.clickable { archivoLauncher.launch("*/*") }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.document_blue),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Archivos",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = EduconnectBlue
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.clickable { archivoLauncher.launch("image/*") }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.camera_blue),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Fotos",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = EduconnectBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÓN PUBLICAR
            Button(
                onClick = {
                    val dest = destinatarioSeleccionado ?: return@Button
                    if (titulo.isBlank() || descripcion.isBlank() || fechaExamen.isBlank()) return@Button
                    val fechaPub = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val hora = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    onPublicar(titulo, descripcion, archivoUri?.toString() ?: "", dest, fechaExamen, fechaPub, hora)
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
                    text = "Publicar examen",
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
