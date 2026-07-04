package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import java.util.concurrent.TimeUnit

@Composable
fun DetalleAgendaScreen(
    nombreHijo: String = "",
    gradoNombre: String = "",
    seccionNombre: String = "",
    publicacion: PublicacionAgendaItem? = null,
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

    val hoy = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    val diasRestantes = remember(publicacion?.fechaEntrega) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechaE = sdf.parse(publicacion?.fechaEntrega ?: "") !!
            val fechaH = sdf.parse(hoy)!!
            val diff = fechaE.time - fechaH.time
            TimeUnit.MILLISECONDS.toDays(diff).toInt()
        } catch (e: Exception) { 0 }
    }

    val fechaDisplay = remember(publicacion?.fechaEntrega) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(publicacion?.fechaEntrega ?: "")
            SimpleDateFormat("EEE dd 'de' MMMM yyyy", Locale("es", "PE"))
                .format(date!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { publicacion?.fechaEntrega ?: "" }
    }

    val colorDias = when {
        diasRestantes <= 3 -> StatusErrorRed
        diasRestantes <= 7 -> AccentOrange
        else -> EduconnectBlue
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
                        text = if (publicacion?.tipo == "Examen") "Detalle de Examen" else "Detalle de Tarea",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "$nombreHijo $gradoNombre Sec. $seccionNombre",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.85f)
                    )
                    Text(
                        text = fechaActual,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = TextWhite.copy(alpha = 0.7f)
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

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // BANNER VENCE EN X DÍAS
            if (publicacion?.estado?.lowercase() == "pendiente") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundWhite, RoundedCornerShape(12.dp))
                        .border(1.5.dp, BorderOrange, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.alert_orange),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "Vence en $diasRestantes días - $fechaDisplay",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CARD DETALLE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(16.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Título + tipo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(EduconnectBlueMedium, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (publicacion?.tipo == "Examen") R.drawable.exam_white
                                else R.drawable.document_text_white
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                    Column {
                        Text(
                            text = publicacion?.titulo ?: "",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextBlue
                        )
                        Text(
                            text = publicacion?.tipo ?: "",
                            fontFamily = Roboto,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                }

                HorizontalDivider(color = BorderBlue.copy(alpha = 0.3f), thickness = 1.5.dp)

                // Fila Curso
                FilaRegistro(
                    iconRes = R.drawable.task_darkgray,
                    label = "Curso",
                    valor = publicacion?.cursoNombre ?: "",
                    colorValor = TextBlue
                )

                // Fila Docente
                FilaRegistro(
                    iconRes = R.drawable.teacher_darkgray,
                    label = "Docente",
                    valor = "Prof. ${publicacion?.docenteNombre ?: ""}",
                    colorValor = TextBlue
                )

                // Fila Fecha límite
                FilaRegistro(
                    iconRes = R.drawable.agenda_darkgray,
                    label = if (publicacion?.tipo == "Examen") "Fecha Examen" else "Fecha límite",
                    valor = fechaDisplay,
                    colorValor = TextOrange
                )

                // Fila Tiempo restante
                FilaRegistro(
                    iconRes = R.drawable.clock_darkgray,
                    label = "Tiempo restante",
                    valor = "$diasRestantes días",
                    colorValor = colorDias
                )

                HorizontalDivider(color = BorderBlue.copy(alpha = 0.5f), thickness = 1.5.dp)

                // Descripción
                Text(
                    text = publicacion?.descripcion ?: "",
                    fontFamily = Roboto,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundBlueLight, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                )

                // Adjunto
                if (!publicacion?.archivoAdjunto.isNullOrEmpty()) {
                    HorizontalDivider(color = BorderBlue.copy(alpha = 0.3f), thickness = 1.5.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EduconnectBlue.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                            .border(1.dp, BorderBlue, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.paperclip_lightgray),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = publicacion?.archivoAdjunto?.substringAfterLast("/") ?: "",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = EduconnectBlue,
                            modifier = Modifier.weight(1f)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.adjunto_orange),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÓN VOLVER
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrowleft_white),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Volver a la Agenda",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = TextWhite
                )
            }
        }

        BottomNavBarPadre(
            onInicio = onHomePadre,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Agenda"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleAgendaPreview() {
    EduConnectAppTheme {
        DetalleAgendaScreen(
            nombreHijo = "Carlos",
            gradoNombre = "2do",
            seccionNombre = "A",
            publicacion = PublicacionAgendaItem(
                id = 1L,
                titulo = "Ejercicios Cap. 5 - Funciones",
                descripcion = "Resolver los ejercicios del 1 al 20 del capítulo 5. Incluir los ejercicios de repaso de la página 98. Presentar en hoja bond con nombre completo.",
                tipo = "Tarea",
                cursoNombre = "Matemáticas",
                docenteNombre = "Eduardo C.",
                fechaEntrega = "2026-05-23",
                estado = "Pendiente"
            )
        )
    }
}
