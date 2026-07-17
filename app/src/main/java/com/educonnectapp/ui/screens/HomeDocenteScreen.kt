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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.EduconnectBlueLight
import com.educonnectapp.ui.theme.EduconnectBlueMedium
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeDocenteScreen(
    usuarioNombre: String,
    onAsistencias: () -> Unit = {},
    onComunicados: () -> Unit = {},
    onPublicaciones: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {}
) {
    val fechaActual = remember {
        val sdf = SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
        sdf.format(Date()).replaceFirstChar { it.uppercase() }
    }

    val iniciales = usuarioNombre.split(" ").take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")

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
                .padding(horizontal = 20.dp,
                    vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(AccentOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iniciales,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 35.sp,
                        color = TextWhite
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Prof. $usuarioNombre",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "I.E. Pedro M. Ureña - Trujillo",
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

        Spacer(modifier = Modifier.height(29.dp))

        //CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            Text(
                text = "¿QUÉ DESEAS HACER HOY?",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextBlue,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(15.dp))

            //TARJETA ASISTENCIAS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(28.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(28.dp))
                    .clickable { onAsistencias() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(98.dp)
                        .background(AccentOrange, RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gotoassistance_white),
                        contentDescription = null,
                        modifier = Modifier.size(75.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Asistencias",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextBlue
                    )
                    Text(
                        text = "Registrar - Historial",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.next_gray),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            //TARJETA COMUNICADOS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(28.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(28.dp))
                    .clickable { onComunicados() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(98.dp)
                        .background(EduconnectBlueMedium, RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sendstatement_white),
                        contentDescription = null,
                        modifier = Modifier.size(75.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Comunicados",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextBlue
                    )
                    Text(
                        text = "Avisos a padre de familia",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.next_gray),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            //TARJETA PUBLICACIONES
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(28.dp))
                    .border(1.5.dp, BorderBlue, RoundedCornerShape(28.dp))
                    .clickable { onPublicaciones() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(98.dp)
                        .background(EduconnectBlueLight, RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.publications_white),
                        contentDescription = null,
                        modifier = Modifier.size(75.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Publicaciones",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextBlue
                    )
                    Text(
                        text = "Tareas y evaluaciones",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.next_gray),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        //NAVBAR INFERIOR
        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Inicio"
        )
    }
}


//NAVBAR INFERIOR
@Composable
fun BottomNavBarDocente(
    onInicio: () -> Unit,
    onAlumnos: () -> Unit,
    onAvisos: () -> Unit,
    onPerfil: () -> Unit,
    itemActivo: String = "Inicio",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite)
            .border(1.dp, BorderLight, RoundedCornerShape(0.dp))
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavBarItemDocente(
            iconRes = if (itemActivo == "Inicio") R.drawable.home_blue else R.drawable.home_gray,
            label = "Inicio",
            activo = itemActivo == "Inicio",
            onClick = onInicio
        )
        NavBarItemDocente(
            iconRes = if (itemActivo == "Alumnos") R.drawable.students_blue else R.drawable.students_gray,
            label = "Alumnos",
            activo = itemActivo == "Alumnos",
            onClick = onAlumnos
        )
        NavBarItemDocente(
            iconRes = if (itemActivo == "Avisos") R.drawable.sendstatement_blue else R.drawable.sendstatement_gray,
            label = "Avisos",
            activo = itemActivo == "Avisos",
            onClick = onAvisos
        )
        NavBarItemDocente(
            iconRes = if (itemActivo == "Perfil") R.drawable.user_circle_blue else R.drawable.user_circle_gray,
            label = "Perfil",
            activo = itemActivo == "Perfil",
            onClick = onPerfil
        )
    }
}

//ITEM NAVBAR
@Composable
fun NavBarItemDocente(
    iconRes: Int,
    label: String,
    activo: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 25.dp, vertical = 4.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = label,
            fontFamily = Roboto,
            fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            color = if (activo) EduconnectBlue else TextSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeDocentePreview() {
    EduConnectAppTheme {
        HomeDocenteScreen(
            usuarioNombre = "Eduardo Carranza"
        )
    }
}