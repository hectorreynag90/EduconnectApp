package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
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
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundStatusGreenLight
import com.educonnectapp.ui.theme.BackgroundStatusRedLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.StatusErrorRed
import com.educonnectapp.ui.theme.StatusGreen
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Locale

// Data class para detalle de asistencia sin Room
data class DetalleAsistencia(
    val id: Long,
    val fecha: String,
    val hora: String,
    val estado: String,
    val cursoNombre: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val docenteNombre: String
)

@Composable
fun DetalleAsistenciaScreen(
    asistenciaId: Long,
    alumnoNombre: String,
    detalle: DetalleAsistencia? = null,
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {}
) {
    val esPresente = detalle?.estado == "A"
    val colorEstado = if (esPresente) StatusGreen else StatusErrorRed
    val colorFondoEstado = if (esPresente) BackgroundStatusGreenLight else BackgroundStatusRedLight
    val textoEstado = if (esPresente) "Asistió" else "Faltó"

    val fechaDisplay = remember(detalle?.fecha) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("es", "PE"))
            val date = sdf.parse(detalle?.fecha ?: "")
            SimpleDateFormat("EEEE dd 'de' MMMM yyyy", Locale("es", "PE"))
                .format(date!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { detalle?.fecha ?: "" }
    }

    val fechaCorta = remember(detalle?.fecha) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("es", "PE"))
            val date = sdf.parse(detalle?.fecha ?: "")
            SimpleDateFormat("EEEE dd 'de' MMMM", Locale("es", "PE"))
                .format(date!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { detalle?.fecha ?: "" }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundLight)
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth().background(EduconnectBlue)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onBack,
                    modifier = Modifier.size(32.dp).offset(x = (-5).dp)) {
                    Image(painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver", modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(text = "Detalle de Asistencia", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextWhite)
                    Text(text = alumnoNombre, fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold, fontSize = 17.sp,
                        color = TextWhite.copy(alpha = 0.9f))
                    Text(text = fechaCorta, fontFamily = Roboto,
                        fontWeight = FontWeight.Normal, fontSize = 15.sp,
                        color = TextWhite.copy(alpha = 0.8f))
                }
            }
            IconButton(onClick = onNotificaciones) {
                Image(painter = painterResource(id = R.drawable.notification_white),
                    contentDescription = "Notificaciones", modifier = Modifier.size(30.dp))
            }
        }

        // CONTENIDO
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            // CARD ESTADO
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(colorFondoEstado, RoundedCornerShape(14.dp))
                    .border(1.5.dp, colorEstado, RoundedCornerShape(14.dp))
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val iniciales = alumnoNombre.split(" ").take(2)
                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")

                Box(modifier = Modifier.size(56.dp).background(colorEstado, CircleShape),
                    contentAlignment = Alignment.Center) {
                    Text(text = iniciales, fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 30.sp, color = TextWhite)
                }
                Column {
                    Text(text = textoEstado, fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 24.sp, color = colorEstado)
                    Text(text = alumnoNombre, fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 18.sp, color = colorEstado.copy(alpha = 0.8f))
                    Text(text = fechaDisplay, fontFamily = Roboto, fontWeight = FontWeight.Medium,
                        fontSize = 16.sp, color = colorEstado.copy(alpha = 0.7f))
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = "INFORMACIÓN DEL REGISTRO", fontFamily = Roboto,
                fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlue)

            // TARJETA INFORMACIÓN
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(1.dp, BorderBlue, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                FilaDetalleAsistencia(iconRes = R.drawable.agenda_darkgray, label = "Fecha",
                    valor = fechaCorta, colorValor = EduconnectBlue)
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 8.dp))

                FilaDetalleAsistencia(iconRes = R.drawable.clock_darkgray, label = "Hora",
                    valor = detalle?.hora ?: "—", colorValor = EduconnectBlue)
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 8.dp))

                FilaDetalleAsistencia(iconRes = R.drawable.level_darkgray, label = "Grado",
                    valor = "${detalle?.gradoNombre ?: ""} Sec. \"${detalle?.seccionNombre ?: ""}\"",
                    colorValor = EduconnectBlue)
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 8.dp))

                FilaDetalleAsistencia(iconRes = R.drawable.user_circle_darkgray, label = "Docente",
                    valor = detalle?.docenteNombre ?: "—", colorValor = EduconnectBlue)
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 8.dp))

                FilaDetalleAsistencia(iconRes = R.drawable.task_darkgray, label = "Curso",
                    valor = detalle?.cursoNombre ?: "—", colorValor = EduconnectBlue)
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 8.dp))

                // FILA ESTADO
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Image(painter = painterResource(
                            id = if (esPresente) R.drawable.check_green else R.drawable.close_red),
                            contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(text = "Estado", fontFamily = Roboto, fontWeight = FontWeight.Normal,
                            fontSize = 15.sp, color = TextSecondary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Image(painter = painterResource(
                            id = if (esPresente) R.drawable.check_green else R.drawable.close_red),
                            contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(text = if (esPresente) "Presente" else "Ausente",
                            fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp, color = colorEstado)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BOTÓN VOLVER
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EduconnectBlue)) {
                Image(painter = painterResource(id = R.drawable.arrowleft_white),
                    contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Volver al calendario", fontFamily = Roboto,
                    fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextWhite)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        BottomNavBarPadre(onInicio = onHomePadre, onAvisos = onAvisos,
            onAgenda = onAgenda, onPerfil = onPerfil, itemActivo = "Inicio")
    }
}

@Composable
fun FilaDetalleAsistencia(iconRes: Int, label: String, valor: String, colorValor: Color) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)) {
            Image(painter = painterResource(id = iconRes), contentDescription = null,
                modifier = Modifier.size(22.dp))
            Text(text = label, fontFamily = Roboto, fontWeight = FontWeight.Normal,
                fontSize = 16.sp, color = TextSecondary)
        }
        Text(text = valor, fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp, color = colorValor, textAlign = TextAlign.End)
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleAsistenciaPreview() {
    EduConnectAppTheme {
        DetalleAsistenciaScreen(
            asistenciaId = 1L,
            alumnoNombre = "Pedro Sanchez Vega",
            detalle = DetalleAsistencia(
                id = 1L,
                fecha = "2026-06-13",
                hora = "8:00 AM",
                estado = "A",
                cursoNombre = "Matemáticas",
                gradoNombre = "1er Grado",
                seccionNombre = "A",
                docenteNombre = "Prof. Eduardo Carranza"
            )
        )
    }
}