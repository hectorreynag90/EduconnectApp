package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import com.educonnectapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetalleComunicadoPadreScreen(
    comunicado: ComunicadoPadreItem? = null,
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {}
) {
    val fechaActual = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
    }

    val fechaDisplay = remember(comunicado?.fecha) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(comunicado?.fecha ?: "")
            SimpleDateFormat("EEE dd 'de' MMMM yyyy", Locale("es", "PE"))
                .format(date!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { comunicado?.fecha ?: "" }
    }

    val horaLectura = remember(comunicado?.leidoEn) {
        if (comunicado?.leidoEn == null) return@remember ""
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = sdf.parse(comunicado.leidoEn)
            SimpleDateFormat("hh:mm a", Locale("es", "PE")).format(date!!)
        } catch (e: Exception) {
            try {
                val sdf2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date2 = sdf2.parse(comunicado.leidoEn)
                SimpleDateFormat("hh:mm a", Locale("es", "PE")).format(date2!!)
            } catch (e2: Exception) { comunicado.leidoEn }
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
                        text = "Detalle comunicado",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "I.E. Pedro M. Ureña",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextWhite
                    )
                    Text(
                        text = fechaActual,
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // CARD IE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(3.dp, BorderBlue, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(EduconnectBlue.copy(alpha = 0.1f), CircleShape)
                        .border(1.5.dp, EduconnectBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_educonnect),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Column {
                    Text(
                        text = "I.E. Pedro M.Ureña",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextBlue
                    )
                    Text(
                        text = "Trujillo, La Libertad",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                }
            }

            // CARD DETALLE COMUNICADO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(1.5.dp, BorderOrange, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Asunto
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.megaphone_orange),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = comunicado?.asunto ?: "",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextBlue
                    )
                }

                HorizontalDivider(color = BorderOrange, thickness = 1.5.dp)

                // Fecha + hora
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.agenda_darkgray),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = fechaDisplay,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.clock_darkgray),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = comunicado?.hora ?: "",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                }

                // Docente + sección
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.teacher_darkgray),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = "Prof. ${comunicado?.docenteNombre ?: ""}",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.users_darkgray),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = "${comunicado?.gradoNombre ?: ""} Sec ${comunicado?.seccionNombre ?: ""}",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                }

                HorizontalDivider(color = BorderOrange, thickness = 1.dp)

                // MENSAJE
                Text(
                    text = comunicado?.mensaje ?: "",
                    fontFamily = Roboto,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    lineHeight = 22.sp
                )

                HorizontalDivider(color = BorderOrange, thickness = 1.dp)

                // Docente firma
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Prof. ${comunicado?.docenteNombre ?: ""}",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = EduconnectBlue
                    )
                    Text(
                        text = "Docente - ${comunicado?.cursoNombre ?: ""} - ${comunicado?.gradoNombre ?: ""} ${comunicado?.seccionNombre ?: ""}",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }

            // ESTADO LEÍDO
            if (comunicado?.leido == true && horaLectura.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StatusGreen, RoundedCornerShape(12.dp))
                        .border(1.5.dp, StatusGreen, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.check_white),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Comunicado Leído - $horaLectura",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = TextWhite
                    )
                }
            }
        }

        BottomNavBarPadre(
            onInicio = onHomePadre,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Avisos"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleComunicadoPadrePreview() {
    EduConnectAppTheme {
        DetalleComunicadoPadreScreen(
            comunicado = ComunicadoPadreItem(
                id = 1L,
                asunto = "Reunión de padres de familia",
                mensaje = "Estimados padres de familia del 2do Sec. A,\n\nLos invitamos cordialmente a la reunión de padres de familia que se realizará el viernes 23 de mayo a las 4:00 PM en el aula de 2do Sec. A.\n\nTemas a tratar: Avance académico, comportamiento y fin de año escolar.\n\nSu asistencia es muy importante.",
                fecha = "2026-05-23",
                hora = "9:45 AM",
                docenteNombre = "Eduardo Carranza",
                gradoNombre = "2do",
                seccionNombre = "A",
                cursoNombre = "Matemáticas",
                leido = true,
                leidoEn = "2026-05-23T11:17:00"
            )
        )
    }
}
