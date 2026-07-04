package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class ComunicadoPadreItem(
    val id: Long,
    val asunto: String,
    val mensaje: String,
    val fecha: String,
    val hora: String,
    val docenteNombre: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val cursoNombre: String,
    val leido: Boolean = false,
    val leidoEn: String? = null
)

@Composable
fun ComunicadosRecibidosScreen(
    nombreHijo: String = "",
    gradoNombre: String = "",
    seccionNombre: String = "",
    comunicados: List<ComunicadoPadreItem> = emptyList(),
    mostrarTodos: Boolean = false,
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerDetalle: (ComunicadoPadreItem) -> Unit = {}
) {
    var mostrarTodosState by remember { mutableStateOf(mostrarTodos) }

    val fechaActual = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
    }

    val nuevos = comunicados.filter { !it.leido }
    val leidos = comunicados.filter { it.leido }
    val leidosMostrados = if (mostrarTodosState) leidos else leidos.take(3)

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
                        text = "Comunicados recibidos",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "$nombreHijo - $gradoNombre Sec. $seccionNombre",
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

        // TÍTULO FIJO
        Text(
            text = "COMUNICADOS RECIBIDOS",
            fontFamily = Roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextBlue,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
        )

        // LISTA SCROLLEABLE
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // NUEVOS
            items(nuevos) { comunicado ->
                ComunicadoNuevoCard(
                    comunicado = comunicado,
                    onClick = { onVerDetalle(comunicado) }
                )
            }

            // LEÍDOS
            items(leidosMostrados) { comunicado ->
                ComunicadoLeidoCard(
                    comunicado = comunicado,
                    onClick = { onVerDetalle(comunicado) }
                )
            }

            // VER MÁS
            if (leidos.size > 3 && !mostrarTodosState) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ ${leidos.size - 3} más  ↓",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = EduconnectBlue,
                            modifier = Modifier.clickable { mostrarTodosState = true }
                        )
                    }
                }
            }

            // VACÍO
            if (comunicados.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay comunicados recibidos",
                            fontFamily = Roboto,
                            fontSize = 15.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // BOTÓN VOLVER
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onHomePadre,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                ) {
                    Text(
                        text = "Volver al inicio",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
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
fun ComunicadoNuevoCard(
    comunicado: ComunicadoPadreItem,
    onClick: () -> Unit
) {
    val fechaDisplay = remember(comunicado.fecha) {
        try {
            val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            if (comunicado.fecha == hoy) return@remember "Hoy"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(comunicado.fecha)
            SimpleDateFormat("EEE dd", Locale("es", "PE")).format(date!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { comunicado.fecha }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(14.dp))
            .border(1.8.dp, BorderOrange, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.megaphone_orange),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                    Text(
                        text = comunicado.asunto,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "Nuevo",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextOrange
                )
            }
            Text(
                text = "$fechaDisplay ${comunicado.hora} - Prof. ${comunicado.docenteNombre}",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Text(
                text = comunicado.cursoNombre,
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Text(
                text = comunicado.mensaje,
                fontFamily = Roboto,
                fontSize = 14.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Image(
            painter = painterResource(id = R.drawable.next_gray),
            contentDescription = null,
            modifier = Modifier.size(30.dp).align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun ComunicadoLeidoCard(
    comunicado: ComunicadoPadreItem,
    onClick: () -> Unit
) {
    val fechaDisplay = remember(comunicado.fecha) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(comunicado.fecha)
            SimpleDateFormat("EEE dd", Locale("es", "PE")).format(date!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) { comunicado.fecha }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(12.dp))
            .border(1.5.dp, BorderBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.megaphone_green),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                    Text(
                        text = comunicado.asunto,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = TextBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = fechaDisplay,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
            Text(
                text = "Prof. ${comunicado.docenteNombre} - ${comunicado.cursoNombre}",
                fontFamily = Roboto,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.check_green_dark),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Leído",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = StatusGreen
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.next_gray),
            contentDescription = null,
            modifier = Modifier.size(30.dp).align(Alignment.CenterVertically)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ComunicadosRecibidosPreview() {
    EduConnectAppTheme {
        ComunicadosRecibidosScreen(
            nombreHijo = "Carlos",
            gradoNombre = "2do",
            seccionNombre = "A",
            comunicados = listOf(
                ComunicadoPadreItem(1L, "Reunión de padres - Vie 23", "Estimados padres de familia, los invitamos a la reunión...", "2026-05-23", "9:45 AM", "Eduardo C.", "2do", "A", "Matemáticas", false),
                ComunicadoPadreItem(2L, "Entrega de libretas", "Estimados padres de familia, les informo que se llevará a cabo...", "2026-05-23", "9:45 AM", "Eduardo C.", "2do", "A", "Matemáticas", false),
                ComunicadoPadreItem(3L, "Uniforme escolar", "Recordatorio uniforme.", "2026-05-16", "8:00 AM", "Eduardo C.", "2do", "A", "Matemáticas", true, "2026-05-16T11:17:00"),
                ComunicadoPadreItem(4L, "Informe rendimiento", "Informe bimestral.", "2026-05-13", "7:00 AM", "Eduardo C.", "2do", "A", "Matemáticas", true, "2026-05-13T09:00:00")
            )
        )
    }
}
