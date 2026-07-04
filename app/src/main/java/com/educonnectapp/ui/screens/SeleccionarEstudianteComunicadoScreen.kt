package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

data class HijoComunicadoItem(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val seccionId: Long,
    val sinLeer: Int = 0,
    val total: Int = 0
)

@Composable
fun SeleccionarEstudianteComunicadoScreen(
    hijos: List<HijoComunicadoItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerComunicados: (hijo: HijoComunicadoItem) -> Unit = {}
) {
    var hijoSeleccionado by remember { mutableStateOf<HijoComunicadoItem?>(null) }

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
                        text = "Comunicados",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "Selecciona el estudiante",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
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

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "¿DE QUIEN DESEA VER LOS COMUNICADOS?",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextBlue
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(hijos) { hijo ->
                    val isSelected = hijoSeleccionado?.id == hijo.id
                    HijoComunicadoCard(
                        hijo = hijo,
                        isSelected = isSelected,
                        onClick = { hijoSeleccionado = hijo }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN VER COMUNICADOS
            hijoSeleccionado?.let { hijo ->
                Button(
                    onClick = { onVerComunicados(hijo) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.eye_white),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Ver comunicados de ${hijo.nombres}",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
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

@Composable
fun HijoComunicadoCard(
    hijo: HijoComunicadoItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iniciales = "${hijo.nombres.firstOrNull() ?: ""}${hijo.apellidos.firstOrNull() ?: ""}".uppercase()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) AccentOrangeLight.copy(alpha = 0.08f) else BackgroundWhite,
                RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isSelected) 2.5.dp else 1.5.dp,
                color = if (isSelected) BorderOrange else AccentOrangeLight,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(AccentOrange, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iniciales,
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = TextWhite
            )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "${hijo.nombres} ${hijo.apellidos}",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextBlue
            )
            Text(
                text = "${hijo.gradoNombre} Sec. ${hijo.seccionNombre}",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .background(
                            if (hijo.sinLeer > 0) StatusErrorRed.copy(alpha = 0.1f)
                            else EduconnectBlue.copy(alpha = 0.08f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (hijo.sinLeer == 0) "Sin nuevos"
                        else "${hijo.sinLeer} sin leer",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = if (hijo.sinLeer > 0) StatusErrorRed else TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .background(EduconnectBlue.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${hijo.total} total",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = EduconnectBlue
                    )
                }
            }
        }

        if (isSelected) {
            Image(
                painter = painterResource(id = R.drawable.check_circle_orange),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.check_lightgray),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SeleccionarEstudianteComunicadoPreview() {
    EduConnectAppTheme {
        SeleccionarEstudianteComunicadoScreen(
            hijos = listOf(
                HijoComunicadoItem(1L, "Carlos", "Pérez", "2do", "A", 1L, 1, 9),
                HijoComunicadoItem(2L, "María", "Pérez", "1ro", "B", 2L, 0, 5)
            )
        )
    }
}
