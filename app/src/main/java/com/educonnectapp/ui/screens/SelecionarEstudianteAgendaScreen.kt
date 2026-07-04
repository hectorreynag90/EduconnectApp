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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class HijoAgendaItem(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val cursoNombre: String = "",
    val gradoId: Long,
    val seccionId: Long,
    val tareasPendientes: Int = 0,
    val examenesPendientes: Int = 0
)

@Composable
fun SeleccionarHijoAgendaScreen(
    hijos: List<HijoAgendaItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerAgenda: (hijo: HijoAgendaItem) -> Unit = {}
) {
    var hijoSeleccionado by remember { mutableStateOf<HijoAgendaItem?>(null) }

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
                        text = "Agenda Escolar",
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
                        color = TextWhite.copy(alpha = 0.8f)
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

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "¿AGENDA DE QUIÉN DESEA VER?",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextBlue
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(hijos) { hijo ->
                    val isSelected = hijoSeleccionado?.id == hijo.id
                    HijoAgendaCard(
                        hijo = hijo,
                        isSelected = isSelected,
                        onClick = { hijoSeleccionado = hijo }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN VER AGENDA
            hijoSeleccionado?.let { hijo ->
                Button(
                    onClick = { onVerAgenda(hijo) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduconnectBlue)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.agenda_white),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Ver agenda de ${hijo.nombres}",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
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
            itemActivo = "Agenda"
        )
    }
}

@Composable
fun HijoAgendaCard(
    hijo: HijoAgendaItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iniciales = "${hijo.nombres.firstOrNull() ?: ""}${hijo.apellidos.firstOrNull() ?: ""}".uppercase()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) EduconnectBlue.copy(alpha = 0.08f) else BackgroundWhite,
                RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) EduconnectBlue else BorderBlue,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(AccentOrange, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iniciales,
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextWhite
            )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "${hijo.nombres} ${hijo.apellidos}",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextBlue
            )
            Text(
                text = "${hijo.gradoNombre} Sec. ${hijo.seccionNombre}",
                fontFamily = Roboto,
                fontSize = 16.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (hijo.tareasPendientes > 0) StatusErrorRed.copy(alpha = 0.1f)
                            else EduconnectBlue.copy(alpha = 0.08f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (hijo.tareasPendientes == 0) "Sin tareas"
                        else if (hijo.tareasPendientes == 1) "1 Tarea"
                        else "${hijo.tareasPendientes} Tareas",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = if (hijo.tareasPendientes > 0) AccentOrange else TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (hijo.examenesPendientes > 0) AccentOrange.copy(alpha = 0.1f)
                            else EduconnectBlue.copy(alpha = 0.08f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (hijo.examenesPendientes == 0) "Sin exámenes"
                        else if (hijo.examenesPendientes == 1) "1 Examen"
                        else "${hijo.examenesPendientes} Exámenes",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = if (hijo.examenesPendientes > 0) AccentOrange else TextSecondary
                    )
                }
            }
        }

        if (isSelected) {
            Image(
                painter = painterResource(id = R.drawable.checkcircle_blue),
                contentDescription = null,
                modifier = Modifier.size(35.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.check_lightgray),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SeleccionarHijoAgendaPreview() {
    EduConnectAppTheme {
        SeleccionarHijoAgendaScreen(
            hijos = listOf(
                HijoAgendaItem(1L, "Carlos", "Pérez", "2do", "A", "Matemáticas", 1L, 1L, 3, 1),
                HijoAgendaItem(2L, "María", "Pérez", "1ro", "B", "Comunicación", 1L, 2L, 1, 0),
                HijoAgendaItem(3L, "Jorge", "Párez", "2do", "A", "Matemáticas", 1L, 1L, 0, 0)
            )
        )
    }
}
