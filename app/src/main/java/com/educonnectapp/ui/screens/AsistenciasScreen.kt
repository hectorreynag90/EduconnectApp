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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.educonnectapp.ui.theme.StatusGreen
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite
import com.educonnectapp.ui.theme.Warning
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AsistenciasScreen(
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onRegistrarAsistencia: () -> Unit = {},
    onHistorial: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
) {
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
            ){
                Column () {
                    IconButton(onClick = onBack
                            ,modifier = Modifier
                            .size(32.dp)
                            .offset(x = (-5).dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.arrowleft_white),
                            contentDescription = "Back",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column (modifier = Modifier.weight(1f)) {

                    Text(
                        text = "Asistencias",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )

                    Text(
                        text = "Gestión de asistencias alumnos",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = TextWhite
                    )

                    Text(
                        text = fechaActual, // fecha actual
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = TextWhite
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

        Spacer(modifier = Modifier.height(29.dp))

        //CONTENIDO PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            Text(
                text = "¿QUÉ DESEAS HACER EN ASISTENCIAS?",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlue,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(25.dp))

            //TARJETA REGISTRAR ASISTENCIA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(28.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(28.dp))
                    .clickable { onRegistrarAsistencia() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(AccentOrange, RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gotoassistance_white),
                        contentDescription = null,
                        modifier = Modifier.size(58.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Registrar Asistencia",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextBlue
                    )
                    Text(
                        text = "Toma lista del dia de hoy",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = TextSecondary
                    )
                    //Spacer(modifier = Modifier.height(4.dp))
                    // Badge pendientes
                    /*Box(
                        modifier = Modifier
                            .background(AccentOrange.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                            .border(1.dp, AccentOrange, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "2 secciones pendientes",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = AccentOrange
                        )
                    }*/
                }

                Image(
                    painter = painterResource(id = R.drawable.next_gray),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            //TARJETA HISTORIAL
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(28.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(28.dp))
                    .clickable { onHistorial() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(StatusGreen, RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sendstatement_white),
                        contentDescription = null,
                        modifier = Modifier.size(58.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Historial de asistencias",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = StatusGreen
                    )
                    Text(
                        text = "Ver registro anteriores",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = TextSecondary
                    )
                   // Spacer(modifier = Modifier.height(4.dp))
                    // Badge porcentaje
                    /*Box(
                        modifier = Modifier
                            .background(AsistenciaPresente, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Mayo 95% asistencias",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = StatusGreen
                        )
                    }*/
                }

                Image(
                    painter = painterResource(id = R.drawable.next_gray),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(120.dp))

            //RESUMEN DEL DÍA
            Text(
                text = "RESUMEN DEL DIA",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Realizado
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "1",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = EduconnectBlue
                    )
                    Text(
                        text = "Realizado",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
                // Divisor
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(60.dp)
                        .background(BorderLight)
                )

                // Pendientes
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "2",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = Warning
                    )
                    Text(
                        text = "Pendientes",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
                // Divisor
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(60.dp)
                        .background(BorderLight)
                )

                // Asistencias
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "95%",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = StatusGreen
                    )
                    Text(
                        text = "Asistencias",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        //NAVBAR INFERIOR
        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Alumnos",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AsistenciasScreenPreview() {
    EduConnectAppTheme {
        AsistenciasScreen()
    }
}