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
import com.educonnectapp.ui.theme.BorderBlue
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

// Data class para hijo del padre
data class HijoItem(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val porcentajeMes: Int
)

@Composable
fun SeleccionarEstudianteScreen(
    padreId: String,
    padreNombre: String,
    listaHijos: List<HijoItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerHistorial: (alumnoId: Long, alumnoNombre: String) -> Unit = { _, _ -> }
) {
    val fechaActual = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
    }

    val mesNombre = remember {
        SimpleDateFormat("MMMM", Locale("es", "PE")).format(Date())
            .replaceFirstChar { it.uppercase() }
    }

    var alumnoSeleccionado by remember { mutableStateOf<HijoItem?>(null) }

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
                    Text(text = "LISTA ESTUDIANTES", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextWhite)
                    Text(text = "Asignado a $padreNombre", fontFamily = Roboto,
                        fontWeight = FontWeight.Normal, fontSize = 17.sp,
                        color = TextWhite.copy(alpha = 0.85f))
                    Text(text = fechaActual, fontFamily = Roboto,
                        fontWeight = FontWeight.Normal, fontSize = 14.sp,
                        color = TextWhite.copy(alpha = 0.85f))
                }
            }
            IconButton(onClick = onNotificaciones) {
                Image(painter = painterResource(id = R.drawable.notification_white),
                    contentDescription = "Notificaciones", modifier = Modifier.size(35.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TÍTULO FIJO
        Column(
            modifier = Modifier.fillMaxWidth().background(BackgroundLight)
                .padding(horizontal = 20.dp, vertical = 15.dp)
        ) {
            Text(text = "SELECCIONA EL ESTUDIANTE", fontFamily = Roboto,
                fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextBlue)
        }

        // LISTA SCROLLEABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listaHijos.forEach { alumno ->
                val seleccionado = alumno.id == alumnoSeleccionado?.id
                val iniciales = "${alumno.nombres.firstOrNull() ?: ""}${alumno.apellidos.firstOrNull() ?: ""}".uppercase()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundWhite, RoundedCornerShape(25.dp))
                        .border(
                            if (seleccionado) 2.dp else 1.dp,
                            if (seleccionado) EduconnectBlue else BorderBlue,
                            RoundedCornerShape(25.dp))
                        .clickable { alumnoSeleccionado = alumno }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(50.dp)
                            .background(EduconnectBlue, RoundedCornerShape(25.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = iniciales, fontFamily = Roboto,
                            fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextWhite)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "${alumno.nombres} ${alumno.apellidos}",
                            fontFamily = Roboto, fontWeight = FontWeight.Bold,
                            fontSize = 18.sp, color = TextBlue)
                        Text(text = "${alumno.gradoNombre} Sec. ${alumno.seccionNombre}",
                            fontFamily = Roboto, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .background(AccentOrange.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                                .border(1.dp, AccentOrange.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(text = "${alumno.porcentajeMes}% $mesNombre",
                                fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp, color = AccentOrange)
                        }
                    }

                    if (seleccionado) {
                        Box(modifier = Modifier.size(28.dp).background(EduconnectBlue, CircleShape),
                            contentAlignment = Alignment.Center) {
                            Image(painter = painterResource(id = R.drawable.check_white),
                                contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    } else {
                        Image(painter = painterResource(id = R.drawable.check_lightgray),
                            contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            }

            if (listaHijos.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                    .padding(20.dp),
                    contentAlignment = Alignment.Center) {
                    Text(text = "¡No tienes hijos registrados!", fontFamily = Roboto,
                        fontWeight = FontWeight.Normal, fontSize = 15.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
        }

        // BOTÓN FIJO
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Button(
                onClick = {
                    alumnoSeleccionado?.let { alumno ->
                        onVerHistorial(alumno.id, "${alumno.nombres} ${alumno.apellidos}")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                enabled = alumnoSeleccionado != null
            ) {
                Image(painter = painterResource(id = R.drawable.eye_white),
                    contentDescription = null, modifier = Modifier.size(29.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Ver Asistencias de ${alumnoSeleccionado?.nombres ?: ""}",
                    fontFamily = Roboto, fontWeight = FontWeight.Bold,
                    fontSize = 19.sp, color = TextWhite)
            }
        }

        // NAVBAR
        BottomNavBarPadre(
            onInicio = onHomePadre, onAvisos = onAvisos,
            onAgenda = onAgenda, onPerfil = onPerfil, itemActivo = "Inicio")
    }
}

@Preview(showBackground = true)
@Composable
fun SeleccionarEstudiantePreview() {
    EduConnectAppTheme {
        SeleccionarEstudianteScreen(
            padreId = "uuid-padre",
            padreNombre = "Juan Perez",
            listaHijos = listOf(
                HijoItem(1L, "Pedro", "Sanchez Vega", "1er Grado", "A", 85),
                HijoItem(2L, "Angela", "Perez Garcia", "2do Grado", "B", 90)
            )
        )
    }
}