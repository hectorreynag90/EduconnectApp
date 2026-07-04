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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

private val AsistenciaVerde = Color(0xFF26C281)
private val FaltaRojo = Color(0xFFE53935)

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
    onGuardado: (presentes: Int, ausentes: Int, estados: Map<Long, String>, hora: String, fecha: String) -> Unit = { _, _, _, _, _ -> }
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

    // Precargar estados si hay asistencia previa
    LaunchedEffect(asistenciaPrevia) {
        if (asistenciaPrevia.isNotEmpty()) {
            estadoAsistencia.clear()
            estadoAsistencia.putAll(asistenciaPrevia)
        }
    }

    val hayAsistenciaPrevia = asistenciaPrevia.isNotEmpty()

    // Detectar si hubo cambios respecto a la asistencia previa
    val huboCambios = hayAsistenciaPrevia &&
            estadoAsistencia.any { (id, estado) -> asistenciaPrevia[id] != estado }

    val totalAlumnos = listaAlumnos.size
    val marcados = estadoAsistencia.values.count { it == "A" || it == "F" }
    val progreso = if (totalAlumnos > 0) marcados.toFloat() / totalAlumnos else 0f
    val porcentaje = (progreso * 100).toInt()

    // Botón habilitado: si no hay previa → todos marcados; si hay previa → hubo cambios
    val todosListos = listaAlumnos.all { (estadoAsistencia[it.id] ?: "").isNotEmpty() }
    val botonHabilitado = if (hayAsistenciaPrevia) huboCambios else todosListos

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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack,
                    modifier = Modifier.size(32.dp).offset(x = (-5).dp)) {
                    Image(painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver", modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(text = "Registro de Asistencia", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextWhite)
                    Text(text = curso, fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextWhite)
                    Text(text = "$grado - Sec. $seccion", fontFamily = Roboto,
                        fontWeight = FontWeight.Normal, fontSize = 15.sp, color = TextWhite)
                    Text(text = fechaDisplay, fontFamily = Roboto,
                        fontWeight = FontWeight.Normal, fontSize = 14.sp,
                        color = TextWhite.copy(alpha = 0.85f))
                }
            }
            IconButton(onClick = onNotificaciones) {
                Image(painter = painterResource(id = R.drawable.notification_white),
                    contentDescription = "Notificaciones", modifier = Modifier.size(35.dp))
            }
        }

        // CONTENIDO
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // BADGE
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = "LISTA DE ALUMNOS", fontFamily = Roboto,
                    fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextBlue)
                Box(modifier = Modifier.background(EduconnectBlue, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(text = "$marcados/$totalAlumnos", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextWhite)
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
                        fontFamily = Roboto, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // LISTA SCROLLEABLE
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listaAlumnos.forEach { alumno ->
                    val estado = estadoAsistencia[alumno.id] ?: ""
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .background(BackgroundWhite, RoundedCornerShape(10.dp))
                            .border(1.dp, BorderBlue, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${alumno.nombres} ${alumno.apellidos}".uppercase(),
                            fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp, color = TextBlue, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))

                        // BOTÓN A
                        Box(modifier = Modifier.size(width = 48.dp, height = 36.dp)
                            .background(if (estado == "A") AsistenciaVerde else BackgroundWhite, RoundedCornerShape(8.dp))
                            .border(1.5.dp, if (estado == "A") AsistenciaVerde else BorderLight, RoundedCornerShape(8.dp))
                            .clickable { estadoAsistencia[alumno.id] = "A" },
                            contentAlignment = Alignment.Center) {
                            Text(text = "A", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                                fontSize = 16.sp, color = if (estado == "A") TextWhite else TextSecondary)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // BOTÓN F
                        Box(modifier = Modifier.size(width = 48.dp, height = 36.dp)
                            .background(if (estado == "F") FaltaRojo else BackgroundWhite, RoundedCornerShape(8.dp))
                            .border(1.5.dp, if (estado == "F") FaltaRojo else BorderLight, RoundedCornerShape(8.dp))
                            .clickable { estadoAsistencia[alumno.id] = "F" },
                            contentAlignment = Alignment.Center) {
                            Text(text = "F", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                                fontSize = 16.sp, color = if (estado == "F") TextWhite else TextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // PROGRESO
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Progreso de asistencia", fontFamily = Roboto,
                    fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextBlue)
                Text(text = "$porcentaje%", fontFamily = Roboto,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextBlue)
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

            // BOTÓN GUARDAR
            Button(
                onClick = {
                    if (!hayAsistenciaPrevia) {
                        val sinMarcar = listaAlumnos.count { alumno ->
                            (estadoAsistencia[alumno.id] ?: "").isEmpty()
                        }
                        if (sinMarcar > 0) {
                            Toast.makeText(context,
                                "Faltan $sinMarcar alumno(s) por marcar", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                    }
                    val presentes = estadoAsistencia.values.count { it == "A" }
                    val ausentes = estadoAsistencia.values.count { it == "F" }
                    val horaActual = SimpleDateFormat("hh:mm a", Locale("es", "PE")).format(Date())
                    onGuardado(presentes, ausentes, estadoAsistencia.toMap(), horaActual, fechaHoy)
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = botonHabilitado,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (botonHabilitado) AccentOrange else BorderLight
                )
            ) {
                Image(painter = painterResource(id = R.drawable.save_white),
                    contentDescription = null, modifier = Modifier.size(30.dp))

                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (hayAsistenciaPrevia) "Actualizar asistencia" else "Guardar y notificar",
                    fontFamily = Roboto, fontWeight = FontWeight.Bold,
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
            itemActivo = "Alumnos")
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
            ),
        )
    }
}
