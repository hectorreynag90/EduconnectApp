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
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.EduconnectBlueMedium
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.StatusErrorRed
import com.educonnectapp.ui.theme.StatusGreen
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ComunicadosScreen(
    docenteId: String,
    docenteNombre: String,
    totalEnviados: Int = 0,
    totalHoy: Int = 0,
    sinLeer: Int = 0,
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onNuevoComunicado: () -> Unit = {},
    onHistorial: () -> Unit = {},
) {
    val fechaActual = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundLight)
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
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.
                    size(32.dp).offset(x = (-5).dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver",
                        modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(
                        text = "Comunicados",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite)
                    Text(
                        text = docenteNombre,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.9f))
                    Text(
                        text = fechaActual,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
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

        Spacer(modifier = Modifier.height(30.dp))

        // CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxWidth().weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "¿QUÉ DESEAS HACER EN COMUNICADOS?",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextBlue
            )

            Spacer(modifier = Modifier.height(10.dp))

            // TARJETA ENVIAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(20.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(20.dp))
                    .clickable { onNuevoComunicado() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .background(EduconnectBlue, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sendstatement_white),
                        contentDescription = null, modifier = Modifier.size(62.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Enviar comunicado",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextBlue)
                    Text(
                        text = "Nuevo aviso a padres",
                        fontFamily = Roboto,
                        fontSize = 17.sp,
                        color = TextPrimary)
                }
                Image(
                    painter = painterResource(id = R.drawable.next_gray),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TARJETA HISTORIAL
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(20.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(20.dp))
                    .clickable { onHistorial() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier
                    .size(82.dp)
                    .background(EduconnectBlueMedium, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clock_white),
                        contentDescription = null,
                        modifier = Modifier.size(62.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Historial de comunicados",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextBlue)
                    Text(
                        text = "Ver registros anteriores",
                        fontFamily = Roboto,
                        fontSize = 17.sp,
                        color = TextPrimary)
                }
                Image(
                    painter = painterResource(id = R.drawable.next_gray),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(70.dp))

            // RESUMEN DEL DÍA
            Text(
                text = "RESUMEN DEL DÍA",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,

                color = TextBlue, letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(1.dp, AccentOrange, RoundedCornerShape(14.dp))
                    .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalHoy",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = EduconnectBlue
                    )
                    Text(
                        text = "Enviados",
                        fontFamily = Roboto,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(1.dp, AccentOrange, RoundedCornerShape(14.dp))
                    .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${totalEnviados - sinLeer}",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = StatusGreen
                    )
                    Text(
                        text = "Leídos",
                        fontFamily = Roboto,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(1.dp, AccentOrange, RoundedCornerShape(14.dp))
                    .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$sinLeer",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = StatusErrorRed
                    )
                    Text(
                        text = "Sin leer",
                        fontFamily = Roboto,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                }
            }
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Avisos"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ComunicadosPreview() {
    EduConnectAppTheme {
        ComunicadosScreen(
            docenteId = "uuid-docente",
            docenteNombre = "Hector Reyna Gomez",
            totalEnviados = 5,
            totalHoy = 2,
            sinLeer = 3,
        )
    }
}