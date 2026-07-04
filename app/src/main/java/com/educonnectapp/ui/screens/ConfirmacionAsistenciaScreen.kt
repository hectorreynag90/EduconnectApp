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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.DividerColor
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.EduconnectBlueMedium
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.StatusGreen
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ConfirmRed   = Color(0xFFE53935)

@Composable
fun ConfirmacionAsistenciaScreen(
    docenteNombre: String,
    curso: String,
    grado: String,
    seccion: String,
    totalPresentes: Int,
    totalAusentes: Int,
    onNuevaAsistencia: () -> Unit = {},
    onVerHistorial: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    val totalNotificados = totalPresentes + totalAusentes

    val fechaDisplay = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date())
            .replaceFirstChar { it.uppercase() }
    }

    val horaDisplay = remember {
        SimpleDateFormat("hh:mma", Locale.getDefault()).format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {

        //HEADER AZUL
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_educonnect),
                    contentDescription = "Logo",
                    modifier = Modifier.size(46.dp)
                )
                Column {
                    Text(
                        text = "EduConnect",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "\"Mensaje Confirmación\"",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.85f)
                    )
                }
            }
            // BOTÓN X
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(EduconnectBlueMedium, CircleShape)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.close_white),
                    contentDescription = "Cerrar",
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        //CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(modifier = Modifier.height(5.dp))

            // ÍCONO CHECK VERDE
            Box(
                modifier = Modifier
                    .size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.checkcircle_green),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
            }

            // TÍTULO
            Text(
                text = "¡Asistencia Registrada!",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = ConfirmGreen,
                textAlign = TextAlign.Center
            )

            // SUBTÍTULO
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.users_darkgray),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "$totalNotificados padres notificados -",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = TextSecondary
                )
                Image(
                    painter = painterResource(id = R.drawable.clock_darkgray),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = horaDisplay,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            //DATOS DE REGISTRO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(1.dp, BorderBlue, RoundedCornerShape(14.dp))
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "DATOS DE REGISTRO",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextBlue,
                    letterSpacing = 1.sp
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 4.dp,
                    color = DividerColor
                )

                FilaRegistro(
                    iconRes = R.drawable.agenda_darkgray,
                    label = "Fecha:",
                    valor = fechaDisplay,
                    colorValor = EduconnectBlue
                )
                FilaRegistro(
                    iconRes = R.drawable.teacher_darkgray,
                    label = "Docente:",
                    valor = docenteNombre,
                    colorValor = EduconnectBlue
                )
                FilaRegistro(
                    iconRes = R.drawable.task_darkgray,
                    label = "Curso:",
                    valor = curso,
                    colorValor = EduconnectBlue
                )
                FilaRegistro(
                    iconRes = R.drawable.level_darkgray,
                    label = "Grado y Sección:",
                    valor = "$grado - $seccion",
                    colorValor = TextPrimary
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 2.dp,
                    color = DividerColor
                )

                FilaRegistro(
                    iconRes = R.drawable.check_green,
                    label = "Presentes",
                    valor = "$totalPresentes",
                    colorValor = StatusGreen
                )
                FilaRegistro(
                    iconRes = R.drawable.close_red,
                    label = "Ausentes",
                    valor = "$totalAusentes",
                    colorValor = ConfirmRed
                )
                FilaRegistro(
                    iconRes = R.drawable.users_darkgray,
                    label = "Notificados",
                    valor = "$totalNotificados padres",
                    colorValor = TextPrimary
                )
            }

            //BOTÓN NUEVA ASISTENCIA
            Button(
                onClick = onNuevaAsistencia,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gotoassistance_white),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Nueva Asistencia",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextWhite
                )
            }

            //LINK VER HISTORIAL
            Row(
                modifier = Modifier.clickable { onVerHistorial() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.eye_blue),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "Ver Historial Asistencias",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = TextBlue
                )
            }
        }
    }
}

//FILA DE DATO
@Composable
fun FilaRegistro(
    iconRes: Int,
    label: String,
    valor: String,
    colorValor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = label,
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextSecondary
            )
        }
        Text(
            text = valor,
            fontFamily = Roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = colorValor,
            textAlign = TextAlign.End
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ConfirmacionAsistenciaPreview() {
    EduConnectAppTheme {
        ConfirmacionAsistenciaScreen(
            docenteNombre = "Eduardo Carranza",
            curso = "Matemáticas",
            grado = "2do",
            seccion = "A",
            totalPresentes = 25,
            totalAusentes = 3,
            onNuevaAsistencia = {},
            onVerHistorial = {},
            onClose = {}
        )
    }
}