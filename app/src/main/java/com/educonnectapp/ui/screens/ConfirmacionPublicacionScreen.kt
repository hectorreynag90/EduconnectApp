package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConfirmacionPublicacionScreen(
    tipo: String = "Tarea",
    titulo: String = "",
    gradoNombre: String = "",
    seccionNombre: String = "",
    cursoNombre: String = "",
    totalNotificados: Int = 0,
    fechaEntrega: String = "",
    hora: String = "",
    onNuevaPublicacion: () -> Unit = {},
    onVerHistorial: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    // Formatear fecha entrega
    val fechaEntregaDisplay = remember(fechaEntrega) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(fechaEntrega)
            SimpleDateFormat("EEE dd 'de' MMMM", Locale("es", "PE"))
                .format(date!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { fechaEntrega }
    }

    val horaDisplay = remember {
        SimpleDateFormat("hh:mma", Locale.getDefault()).format(Date())
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
                        text = if (tipo == "Tarea") "¡Confirmación tarea!" else "¡Confirmación examen!",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.85f)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(EduconnectBlueMedium, CircleShape)
                    .clickable { onVolver() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.close_white),
                    contentDescription = "Cerrar",
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ICONO CHECK
            Box(
                modifier = Modifier
                    .size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.checkcircle_green),
                    contentDescription = null,
                    modifier = Modifier.size(110.dp)
                )
            }

            // TÍTULO CONFIRMACIÓN
            Text(
                text = if (tipo == "Tarea") "¡Tarea publicada!" else "¡Examen publicado!",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = ConfirmGreen,
                textAlign = TextAlign.Center
            )
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
                    text = "$totalNotificados padres notificados - ",
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
            // CARD DETALLE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(1.dp, BorderBlue, RoundedCornerShape(14.dp))
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Título publicación
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(
                            id = if (tipo == "Tarea") R.drawable.task_white else R.drawable.exam_white
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(45.dp)
                            .background(EduconnectBlue, RoundedCornerShape(8.dp))
                            .padding(6.dp)
                    )
                    Text(
                        text = titulo,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextBlue
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 4.dp,
                    color = DividerColor
                )

                FilaRegistro(
                    iconRes = R.drawable.users_darkgray,
                    label = "Sección",
                    valor = "$gradoNombre Sec. $seccionNombre",
                    colorValor = EduconnectBlue
                )
                FilaRegistro(
                    iconRes = R.drawable.task_darkgray,
                    label = "Curso",
                    valor = cursoNombre,
                    colorValor = EduconnectBlue
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 1.dp,
                    color = DividerColor
                )

                FilaRegistro(
                    iconRes = R.drawable.agenda_darkgray,
                    label = if (tipo == "Tarea") "Fecha Límite" else "Fecha Examen",
                    valor = fechaEntregaDisplay,
                    colorValor = StatusErrorRed
                )
                FilaRegistro(
                    iconRes = R.drawable.users_darkgray,
                    label = "Notificados",
                    valor = "$totalNotificados padres",
                    colorValor = StatusGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÓN NUEVA PUBLICACIÓN
            Button(
                onClick = onNuevaPublicacion,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.task_white),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (tipo == "Tarea") "Nueva Tarea" else "Nuevo Examen",
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
                    text = "Ver Historial Publicaciones",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = TextBlue
                )
            }
        }
    }
}

@Composable
fun DetalleRow(label: String, valor: String, colorValor: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = Roboto,
            fontSize = 14.sp,
            color = TextSecondary
        )
        Text(
            text = valor,
            fontFamily = Roboto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = colorValor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmacionPublicacionPreview() {
    EduConnectAppTheme {
        ConfirmacionPublicacionScreen(
            tipo = "Tarea",
            titulo = "Ejercicios Cap. 5 - Fracciones",
            gradoNombre = "2do",
            seccionNombre = "A",
            cursoNombre = "Matemáticas",
            totalNotificados = 28,
            fechaEntrega = "2026-05-23",
            hora = "9:55 AM"
        )
    }
}
