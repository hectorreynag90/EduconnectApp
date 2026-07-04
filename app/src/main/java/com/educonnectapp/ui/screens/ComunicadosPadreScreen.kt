package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.educonnectapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ComunicadosPadresScreen(
    sinLeer: Int = 0,
    leidos: Int = 0,
    total: Int = 0,
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerRecibidos: () -> Unit = {},
    onVerHistorial: () -> Unit = {}
) {
    val fechaActual = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(32.dp).offset(x = (-5).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Comunicados",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "Opciones de comunicados",
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

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "¿QUÉ DESEAS HACER HOY?",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextBlue
            )

            Spacer(modifier = Modifier.height(4.dp))

            // TARJETA VER RECIBIDOS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(20.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(20.dp))
                    .clickable { onVerRecibidos() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .background(EduconnectBlueMedium, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mail_white),
                        contentDescription = null,
                        modifier = Modifier.size(70.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ver Recibidos",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextBlue
                    )
                    Text(
                        text = "Estados - Historial",
                        fontFamily = Roboto,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    if (sinLeer > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .background(StatusErrorRed.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$sinLeer nuevo${if (sinLeer > 1) "s" else ""}",
                                fontFamily = Roboto,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = StatusErrorRed
                            )
                        }
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.next_gray),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }

            // TARJETA VER HISTORIAL
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(20.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(20.dp))
                    .clickable { onVerHistorial() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .background(EduconnectBlue, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clock_white),
                        contentDescription = null,
                        modifier = Modifier.size(70.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ver Historial",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextBlue
                    )
                    Text(
                        text = "Comunicados anteriores",
                        fontFamily = Roboto,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(EduconnectBlue.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$total comunicados",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = EduconnectBlue
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.next_gray),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(120.dp))

            // RESUMEN
            Text(
                text = "RESUMEN DE COMUNICADOS",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlue
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Triple("$sinLeer", "Sin leer", StatusErrorRed),
                    Triple("$leidos", "Leídos", StatusGreen),
                    Triple("$total", "Total", EduconnectBlue)
                ).forEach { (valor, etiqueta, color) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(BackgroundWhite, RoundedCornerShape(14.dp))
                            .border(1.dp, BorderBlue, RoundedCornerShape(14.dp))
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = valor,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 35.sp,
                            color = color
                        )
                        Text(
                            text = etiqueta,
                            fontFamily = Roboto,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                    }
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
fun ComunicadosPadrePreview() {
    EduConnectAppTheme {
        ComunicadosPadresScreen(
            sinLeer = 1,
            leidos = 8,
            total = 9
        )
    }
}
