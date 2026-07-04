package com.educonnectapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.BorderMedium
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class para destinatario sin Room
data class SeccionDestinatario(
    val gradoId: Long,
    val seccionId: Long,
    val cursoId: Long,
    val gradoNombre: String,
    val seccionNombre: String,
    val cursoNombre: String,
    val totalPadres: Int
)

@Composable
fun NuevoComunicadoScreen(
    docenteId: String,
    docenteNombre: String,
    listaDestinatarios: List<SeccionDestinatario> = emptyList(),
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onEnviado: (asunto: String, mensaje: String, adjunto: String, destinatario: String, curso: String, totalNotificados: Int, hora: String, fecha: String) -> Unit = { _, _, _, _, _, _, _, _ -> }
) {
    val fechaActual = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
    }

    var destinatarioSeleccionado by remember { mutableStateOf<SeccionDestinatario?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var asunto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var archivoUri by remember { mutableStateOf<Uri?>(null) }
    var nombreArchivo by remember { mutableStateOf("") }

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
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(35.dp)
                        .offset(x = (-5).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Nuevo comunicado",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = docenteNombre,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.9f)
                    )
                    Text(
                        text = fechaActual,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = TextWhite.copy(alpha = 0.8f)
                    )
                }
            }
            Column {
                IconButton(onClick = onNotificaciones) {
                    Image(
                        painter = painterResource(id = R.drawable.notification_white),
                        contentDescription = "Notificaciones",
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
        }

        // FORMULARIO
        Column(
            modifier = Modifier
                .fillMaxWidth().weight(1f)
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = destinatarioSeleccionado?.let {
                        "${it.gradoNombre} Sec. ${it.seccionNombre} - ${it.totalPadres} padres"
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = {
                        Text(
                            text ="Seleccione destinatarios",
                            color = TextSecondary,
                            fontSize = 18.sp,
                            fontFamily = Roboto
                        )
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.users_darkgray),
                            contentDescription = null, modifier = Modifier.size(28.dp))
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { dropdownExpanded = true }) {
                            Icon(Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = EduconnectBlue
                            )
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextBlue,
                        unfocusedBorderColor = TextPrimary,
                        focusedTextColor = TextBlue,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = BackgroundWhite,
                        unfocusedContainerColor = BackgroundWhite),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(end = 48.dp)
                    .clickable { dropdownExpanded = true }
                )
                DropdownMenu(expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    listaDestinatarios.forEach { dest ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "${dest.gradoNombre} Sec. ${dest.seccionNombre} - ${dest.totalPadres} padres",
                                    fontFamily = Roboto,
                                    fontSize = 14.sp,
                                    color = TextBlue)
                            },
                            onClick = { destinatarioSeleccionado = dest; dropdownExpanded = false }
                        )
                    }
                }
            }

            // ASUNTO
            Text(
                text = "ASUNTO",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlue
            )
            OutlinedTextField(
                value = asunto, onValueChange = { asunto = it },
                placeholder = {
                    Text(
                        text = "Ingrese asunto",
                        color = TextSecondary,
                        fontSize = 17.sp
                    )},
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderMedium,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextSecondary,
                    focusedContainerColor = BackgroundWhite,
                    unfocusedContainerColor = BackgroundWhite),
                modifier = Modifier.fillMaxWidth().height(52.dp), singleLine = true
            )

            // MENSAJE
            Text(
                text = "MENSAJE",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlue
            )
            OutlinedTextField(
                value = mensaje, onValueChange = { mensaje = it },
                placeholder = {
                    Text(
                        text = "Redacte el mensaje...",
                        color = TextSecondary,
                        fontSize = 17.sp
                    ) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderMedium,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextSecondary,
                    focusedContainerColor = BackgroundWhite,
                    unfocusedContainerColor = BackgroundWhite),
                modifier = Modifier.fillMaxWidth().height(180.dp), maxLines = 6
            )

            // ARCHIVO ADJUNTO
            Text(
                text = "ARCHIVO ADJUNTO",
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.paperclip_lightgray),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = if (nombreArchivo.isNotEmpty()) nombreArchivo else "Añadir archivo",
                        fontFamily = Roboto,
                        fontSize = 17.sp,
                        color = if (nombreArchivo.isNotEmpty()) EduconnectBlue else TextSecondary
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.clickable { archivoLauncher.launch("*/*") }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.document_blue),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = "Archivos",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
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
                            contentDescription = null, modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = "Fotos",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = EduconnectBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÓN ENVIAR
            Button(
                onClick = {
                    val dest = destinatarioSeleccionado ?: return@Button
                    if (asunto.isBlank() || mensaje.isBlank()) return@Button
                    val fechaEnvio = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val horaEnvio = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    // TODO: guardar comunicado en Supabase desde MainActivity
                    onEnviado(
                        asunto,
                        mensaje,
                        archivoUri?.toString() ?: "",
                        "${dest.gradoNombre} Sec. ${dest.seccionNombre}",
                        dest.cursoNombre,
                        dest.totalPadres,
                        horaEnvio,
                        fechaEnvio
                    )
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sendstatement_white),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Enviar Comunicado",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
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
            itemActivo = "Avisos")
    }
}

@Preview(showBackground = true)
@Composable
fun NuevoComunicadoPreview() {
    EduConnectAppTheme {
        NuevoComunicadoScreen(
            docenteId = "uuid-docente",
            docenteNombre = "Hector Reyna Gomez",
            listaDestinatarios = listOf(
                SeccionDestinatario(1L, 1L, 1L, "1er Grado", "A", "Matemáticas", 25),
                SeccionDestinatario(2L, 2L, 1L, "2do Grado", "B", "Matemáticas", 22)
            ),
        )
    }
}