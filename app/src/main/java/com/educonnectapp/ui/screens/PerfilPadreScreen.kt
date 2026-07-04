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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.Background
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite

private val BackgroundBlueLight2 = Color(0xFFE6F1FB)
private val BackgroundGreenLight2 = Color(0xFFEAF3DE)
private val BackgroundOrangeLight2 = Color(0xFFFFF0ED)
private val BackgroundAmberLight2 = Color(0xFFFAEEDA)
private val BackgroundGrayLight2 = Color(0xFFF1EFE8)

// Data class para hijo asociado
data class HijoAsociado(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val codigoEstudiante: String
)

@Composable
fun PerfilPadreScreen(
    usuarioNombre: String,
    dni: String = "",
    telefono: String = "",
    email: String = "",
    hijosAsociados: List<HijoAsociado> = emptyList(),
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onEditarNombre: () -> Unit = {},
    onEditarDni: () -> Unit = {},
    onEditarTelefono: () -> Unit = {},
    onEditarEmail: () -> Unit = {},
    onAsociarHijo: () -> Unit = {},
    onEliminarHijo: (HijoAsociado) -> Unit = {},
    onCambiarPassword: () -> Unit = {},
    onNotificacionesConfig: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
) {
    val iniciales = usuarioNombre.split(" ").take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // HEADER AZUL
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduconnectBlue)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(35.dp)
                        .offset(x = (-5).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver",
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onNotificaciones,
                    modifier = Modifier.size(32.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.notification_white),
                        contentDescription = "Notificaciones",
                        modifier = Modifier.size(35.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
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
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(BackgroundWhite)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.camera_blue),
                        contentDescription = "Cambiar foto",
                        modifier = Modifier.size(23.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = usuarioNombre,
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextWhite
            )
            Text(
                text = "Padre de familia",
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = TextWhite.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        // CONTENIDO SCROLLEABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // DATOS PERSONALES
            Text(
                text = "DATOS PERSONALES",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = EduconnectBlue,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
            ) {
                FilaPerfil(
                    iconRes = R.drawable.user_circle_darkgray,
                    bgColor = BackgroundBlueLight2,
                    label = "Nombre completo",
                    valor = usuarioNombre,
                    onClick = onEditarNombre
                )
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
                FilaPerfil(
                    iconRes = R.drawable.dni_darkgray,
                    bgColor = BackgroundBlueLight2,
                    label = "DNI",
                    valor = if (dni.isNotEmpty()) dni else "No registrado",
                    onClick = onEditarDni
                )
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
                FilaPerfil(
                    iconRes = R.drawable.phone_darkgray,
                    bgColor = BackgroundBlueLight2,
                    label = "Teléfono",
                    valor = if (telefono.isNotEmpty()) telefono else "No registrado",
                    onClick = onEditarTelefono
                )
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
                FilaPerfil(
                    iconRes = R.drawable.mail_darkgray,
                    bgColor = BackgroundBlueLight2,
                    label = "Correo",
                    valor = if (email.isNotEmpty()) email else "No registrado",
                    onClick = onEditarEmail
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // MIS HIJOS
            Text(
                text = "MIS HIJOS",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = EduconnectBlue,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
            ) {
                if (hijosAsociados.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(BackgroundGreenLight2, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.students_blue),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = "Sin hijos asociados aún",
                            fontFamily = Roboto,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                    HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                } else {
                    hijosAsociados.forEach { hijo ->
                        val inicialesHijo = "${hijo.nombres.firstOrNull() ?: ""}${hijo.apellidos.firstOrNull() ?: ""}".uppercase()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(BackgroundBlueLight2, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = inicialesHijo,
                                    fontFamily = Roboto,
                                    fontWeight = FontWeight.Bold,
                                    fontSize =18.sp,
                                    color = EduconnectBlue
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${hijo.nombres} ${hijo.apellidos}",
                                    fontFamily = Roboto,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextBlue
                                )
                                Text(
                                    text = "${hijo.gradoNombre} · Sec. ${hijo.seccionNombre}",
                                    fontFamily = Roboto,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Código: ${hijo.codigoEstudiante}",
                                    fontFamily = Roboto,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(0xFFFCEBEB), RoundedCornerShape(8.dp))
                                    .clickable { onEliminarHijo(hijo) },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.close_red),
                                    contentDescription = "Eliminar",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = BorderLight, thickness = 1.dp)
                    }
                }

                // Botón asociar hijo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAsociarHijo() }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(BackgroundOrangeLight2, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.useradd_red),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = "Asociar nuevo hijo",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = AccentOrange,
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.next_red),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // SECCIÓN CONFIGURACIÓN
            Text(
                text = "CONFIGURACIÓN",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = EduconnectBlue,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
            ) {
                FilaAccion(
                    iconRes = R.drawable.closed_white,
                    bgColor = Background,
                    label = "Cambiar contraseña",
                    onClick = onCambiarPassword
                )
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
                FilaAccion(
                    iconRes = R.drawable.notification_white,
                    bgColor = Background,
                    label = "Notificaciones",
                    onClick = onNotificacionesConfig
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CERRAR SESIÓN
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundOrangeLight2, RoundedCornerShape(14.dp))
                    .border(1.dp, AccentOrange, RoundedCornerShape(14.dp))
                    .clickable { onCerrarSesion() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logout_orange),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerrar sesión",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = AccentOrange
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // NAVBAR
        BottomNavBarPadre(
            onInicio = onHomePadre,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Perfil"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilPadrePreview() {
    EduConnectAppTheme {
        PerfilPadreScreen(
            usuarioNombre = "Luis Perez Garcia",
            dni = "41238904",
            telefono = "912345678",
            email = "luis@gmail.com",
            hijosAsociados = listOf(
                HijoAsociado(1L, "Pedro", "Sanchez Vega", "1er Grado", "A", "PMU0001")
            ),
        )
    }
}