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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
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
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data classes simples para reemplazar las entidades de Room
data class GradoItem(val id: Long, val nombre: String)
data class SeccionItem(val id: Long, val nombre: String)
data class CursoItem(val id: Long, val nombre: String)

@Composable
fun SeleccionarSeccionScreen(
    docenteId: String,
    listaGrados: List<GradoItem> = emptyList(),
    listaSecciones: List<SeccionItem> = emptyList(),
    listaCursos: List<CursoItem> = emptyList(),
    cantidadAlumnos: Int = 0,
    onGradoSeleccionado: (GradoItem) -> Unit = {},
    onSeccionSeleccionada: (SeccionItem) -> Unit = {},
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onContinuar: (gradoId: Long, seccionId: Long, cursoId: Long, grado: String, seccion: String, curso: String) -> Unit = { _, _, _, _, _, _ -> }
) {
    val fechaActual = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
    }

    var gradoSeleccionado by remember { mutableStateOf<GradoItem?>(null) }
    var seccionSeleccionada by remember { mutableStateOf<SeccionItem?>(null) }
    var cursoSeleccionado by remember { mutableStateOf<CursoItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // HEADER AZUL
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduconnectBlue)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    IconButton(onClick = onBack,
                        modifier = Modifier.size(32.dp).offset(x = (-5).dp)) {
                        Image(painter = painterResource(id = R.drawable.arrowleft_white),
                            contentDescription = "Back", modifier = Modifier.size(18.dp))
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Seleccionar Sección", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextWhite)
                    Text(text = "Elige grado, sección y curso", fontFamily = Roboto,
                        fontWeight = FontWeight.Normal, fontSize = 16.sp, color = TextWhite)
                    Text(text = fechaActual, fontFamily = Roboto,
                        fontWeight = FontWeight.Normal, fontSize = 14.sp, color = TextWhite)
                }
            }
            Column {
                IconButton(onClick = onNotificaciones) {
                    Image(painter = painterResource(id = R.drawable.notification_white),
                        contentDescription = "Notificaciones", modifier = Modifier.size(35.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ZONA SCROLLEABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // SELECTOR GRADO
            Text(text = "SELECCIONE GRADO", fontFamily = Roboto,
                fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextBlue)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listaGrados.forEach { grado ->
                    val seleccionado = grado.id == gradoSeleccionado?.id
                    Box(
                        modifier = Modifier
                            .background(
                                if (seleccionado) EduconnectBlue else BackgroundWhite,
                                RoundedCornerShape(30.dp))
                            .border(1.5.dp,
                                if (seleccionado) EduconnectBlue else BorderLight,
                                RoundedCornerShape(30.dp))
                            .clickable {
                                gradoSeleccionado = grado
                                seccionSeleccionada = null
                                cursoSeleccionado = null
                                onGradoSeleccionado(grado)
                            }
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(text = grado.nombre, fontFamily = Roboto,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 15.sp,
                            color = if (seleccionado) TextWhite else TextSecondary)
                    }
                }
            }

            // SELECTOR SECCIÓN
            Text(text = "SELECCIONE SECCIÓN", fontFamily = Roboto,
                fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextBlue)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listaSecciones.forEach { seccion ->
                    val seleccionada = seccion.id == seccionSeleccionada?.id
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (seleccionada) EduconnectBlue else BackgroundWhite,
                                RoundedCornerShape(22.dp))
                            .border(1.5.dp,
                                if (seleccionada) EduconnectBlue else BorderLight,
                                RoundedCornerShape(22.dp))
                            .clickable {
                                seccionSeleccionada = seccion
                                cursoSeleccionado = null
                                onSeccionSeleccionada(seccion)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = seccion.nombre, fontFamily = Roboto,
                            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 18.sp,
                            color = if (seleccionada) TextWhite else TextSecondary)
                    }
                }
            }

            // SELECTOR CURSO
            Text(text = "SELECCIONE CURSO", fontFamily = Roboto,
                fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextBlue)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listaCursos.forEach { curso ->
                    val seleccionado = curso.id == cursoSeleccionado?.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (seleccionado) EduconnectBlue.copy(alpha = 0.08f) else BackgroundWhite,
                                RoundedCornerShape(10.dp))
                            .border(
                                if (seleccionado) 1.5.dp else 1.dp,
                                if (seleccionado) EduconnectBlue else BorderLight,
                                RoundedCornerShape(10.dp))
                            .clickable { cursoSeleccionado = curso }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = curso.nombre, fontFamily = Roboto,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 18.sp,
                            color = if (seleccionado) EduconnectBlue else TextSecondary)
                        if (seleccionado) {
                            Box(modifier = Modifier.size(24.dp)
                                .background(EduconnectBlue, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center) {
                                Text(text = "✓", fontSize = 16.sp, color = TextWhite,
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (listaCursos.isEmpty() && seccionSeleccionada != null) {
                Box(modifier = Modifier.fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                    .padding(16.dp)) {
                    Text(text = "No tiene curso asignado a dicho grado y sección",
                        fontFamily = Roboto, fontWeight = FontWeight.Normal,
                        fontSize = 15.sp, color = TextSecondary)
                }
            }
        }

        // ZONA FIJA INFERIOR
        Column(
            modifier = Modifier.fillMaxWidth().background(BackgroundLight)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(EduconnectBlue.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .border(1.5.dp, AccentOrange, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(painter = painterResource(id = R.drawable.students_blue),
                    contentDescription = null, modifier = Modifier.size(36.dp))
                Column {
                    Text(
                        text = "${gradoSeleccionado?.nombre?.replace(" Grado", "") ?: ""} Sec. ${seccionSeleccionada?.nombre ?: ""} - ${cursoSeleccionado?.nombre ?: ""}",
                        fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 18.sp, color = AccentOrange)
                    Text(text = "$cantidadAlumnos Alumnos", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextBlue)
                }
            }

            Button(
                onClick = {
                    val g = gradoSeleccionado
                    val s = seccionSeleccionada
                    val c = cursoSeleccionado
                    if (g != null && s != null && c != null) {
                        onContinuar(g.id, s.id, c.id, g.nombre, s.nombre, c.nombre)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
            ) {
                Image(painter = painterResource(id = R.drawable.goasistent_white),
                    contentDescription = null, modifier = Modifier.size(35.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Ir Asistencia", fontFamily = Roboto,
                    fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextWhite)
            }
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Inicio")
    }
}

@Preview(showBackground = true)
@Composable
fun SeleccionarSeccionPreview() {
    EduConnectAppTheme {
        SeleccionarSeccionScreen(
            docenteId = "uuid-docente",
            listaGrados = listOf(
                GradoItem(1, "1er Grado"),
                GradoItem(2, "2do Grado")
            ),
            listaSecciones = listOf(
                SeccionItem(1, "A"),
                SeccionItem(2, "B")
            ),
            listaCursos = listOf(
                CursoItem(1, "Matemáticas"),
                CursoItem(2, "Comunicación")
            ),
            cantidadAlumnos = 10,
        )
    }
}