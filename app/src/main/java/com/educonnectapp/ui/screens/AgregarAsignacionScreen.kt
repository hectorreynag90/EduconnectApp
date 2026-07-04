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
import androidx.compose.ui.graphics.Color
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
import com.educonnectapp.ui.theme.BorderLight
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

private val BackgroundBlueLight3 = Color(0xFFE6F1FB)

@Composable
fun AgregarAsignacionScreen(
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    gradosDisp: List<String> = emptyList(),
    seccionesDisp: Map<String, List<String>> = emptyMap(),
    cursosDisp: List<String> = emptyList(),
    onGuardar: (grado: String, seccion: String, curso: String) -> Unit = { _, _, _ -> },
    onCancelar: () -> Unit = {}
){
    var gradoSeleccionado by remember { mutableStateOf("Seleccione...") }
    var seccionSeleccionada by remember { mutableStateOf("") }
    var cursoSeleccionado by remember { mutableStateOf("Seleccione...") }
    var dropdownGradoExpanded by remember { mutableStateOf(false) }
    var dropdownCursoExpanded by remember { mutableStateOf(false) }

    val listaSecciones = if (gradoSeleccionado != "Seleccione...")
        seccionesDisp[gradoSeleccionado] ?: emptyList()
    else emptyList()

    val seleccionCompleta = gradoSeleccionado != "Seleccione..." &&
            seccionSeleccionada.isNotEmpty() &&
            cursoSeleccionado != "Seleccione..."

    // obtiene la fecha actual del dispositivo en español
    val fechaActual = remember {
        val sdf = SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
        sdf.format(Date())
            .replaceFirstChar { it.uppercase() } // pone la primera letra en mayúscula
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
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        text = "Agregar asignación",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "Selecciona grado, sección y curso",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.85f)
                    )
                    Text(
                        text = fechaActual, // fecha actual
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = TextWhite
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

        // CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // GRADO
            Text(
                text = "GRADO",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = EduconnectBlue,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Text(text = "Seleccione el grado", fontFamily = Roboto,
                    fontSize = 15.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = gradoSeleccionado,
                        onValueChange = {}, readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { dropdownGradoExpanded = true }) {
                                Icon(Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null, tint = TextSecondary)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EduconnectBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = BackgroundBlueLight3,
                            unfocusedContainerColor = BackgroundBlueLight3
                        ),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    )
                    Box(modifier = Modifier.matchParentSize().padding(end = 48.dp)
                        .clickable { dropdownGradoExpanded = true })
                    DropdownMenu(expanded = dropdownGradoExpanded,
                        onDismissRequest = { dropdownGradoExpanded = false }) {
                        gradosDisp.forEach { grado ->
                            DropdownMenuItem(
                                text = { Text(grado, fontFamily = Roboto,
                                    fontSize = 16.sp, color = TextBlue) },
                                onClick = {
                                    gradoSeleccionado = grado
                                    seccionSeleccionada = ""
                                    dropdownGradoExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            // SECCIÓN
            Text(
                text = "SECCIÓN",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = EduconnectBlue,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Text(text = "Seleccione la sección", fontFamily = Roboto,
                    fontSize = 15.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                if (listaSecciones.isEmpty()) {
                    Text(text = "Primero selecciona un grado",
                        fontFamily = Roboto, fontSize = 14.sp, color = TextSecondary)
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listaSecciones.forEach { seccion ->
                            val seleccionada = seccion == seccionSeleccionada
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        if (seleccionada) EduconnectBlue else BackgroundWhite,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        1.5.dp,
                                        if (seleccionada) EduconnectBlue else BorderLight,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { seccionSeleccionada = seccion },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = seccion, fontFamily = Roboto,
                                    fontWeight = FontWeight.Bold, fontSize = 16.sp,
                                    color = if (seleccionada) TextWhite else TextSecondary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // CURSO
            Text(
                text = "CURSO",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = EduconnectBlue,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Text(text = "Seleccione el curso", fontFamily = Roboto,
                    fontSize = 15.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = cursoSeleccionado,
                        onValueChange = {}, readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { dropdownCursoExpanded = true }) {
                                Icon(Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null, tint = TextSecondary)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EduconnectBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = BackgroundBlueLight3,
                            unfocusedContainerColor = BackgroundBlueLight3
                        ),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    )
                    Box(modifier = Modifier.matchParentSize().padding(end = 48.dp)
                        .clickable { dropdownCursoExpanded = true })
                    DropdownMenu(expanded = dropdownCursoExpanded,
                        onDismissRequest = { dropdownCursoExpanded = false }) {
                        cursosDisp.forEach { curso ->
                            DropdownMenuItem(
                                text = { Text(curso, fontFamily = Roboto,
                                    fontSize = 14.sp, color = TextBlue) },
                                onClick = {
                                    cursoSeleccionado = curso
                                    dropdownCursoExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // PREVIEW SELECCIÓN
            if (seleccionCompleta) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundBlueLight3, RoundedCornerShape(12.dp))
                        .border(1.dp, EduconnectBlue, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier.size(40.dp)
                            .background(EduconnectBlue, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(painter = painterResource(id = R.drawable.teacher_white),
                            contentDescription = null, modifier = Modifier.size(22.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "$gradoSeleccionado · Sec. $seccionSeleccionada",
                            fontFamily = Roboto, fontWeight = FontWeight.Bold,
                            fontSize = 17.sp, color = EduconnectBlue)
                        Text(text = cursoSeleccionado, fontFamily = Roboto,
                            fontSize = 15.sp, color = TextBlue)
                    }
                    Image(painter = painterResource(id = R.drawable.check_blue),
                        contentDescription = null, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // BOTÓN GUARDAR
            Button(
                onClick = {
                    if (seleccionCompleta) {
                        onGuardar(gradoSeleccionado, seccionSeleccionada, cursoSeleccionado)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (seleccionCompleta) AccentOrange else BorderLight
                ),
                enabled = seleccionCompleta
            ) {
                Image(painter = painterResource(id = R.drawable.add_white),
                    contentDescription = null, modifier = Modifier.size(25.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Agregar Asignación", fontFamily = Roboto,
                    fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    color = if (seleccionCompleta) TextWhite else TextSecondary)
            }

            // BOTÓN CANCELAR
            Button(
                onClick = onCancelar,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundWhite)
            ) {
                Text(text = "Cancelar", fontFamily = Roboto,
                    fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        //NAVBAR INFERIOR
        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfil,
            itemActivo = "Perfil",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AgregarAsignacionPreview() {
    EduConnectAppTheme {
        AgregarAsignacionScreen()
    }
}