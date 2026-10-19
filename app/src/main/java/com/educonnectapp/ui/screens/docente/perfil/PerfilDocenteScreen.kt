package com.educonnectapp.ui.screens.docente.perfil

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.educonnectapp.ui.components.AvatarPerfilEditable
import com.educonnectapp.ui.screens.AlertaDatos
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
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

private val BackgroundBlueLight = Color(0xFFE6F1FB)
private val BackgroundGreenLight = Color(0xFFEAF3DE)
private val BackgroundOrangeLight = Color(0xFFFFF0ED)
private val BackgroundAmberLight = Color(0xFFFAEEDA)
private val BackgroundGrayLight = Color(0xFFF1EFE8)
private val GreenText = Color(0xFF3B6D11)
private val AmberText = Color(0xFF854F0B)

// Data class para asignación académica
data class AsignacionItem(
    val gradoNombre: String,
    val seccionNombre: String,
    val cursoNombre: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PerfilDocenteScreen(
    usuarioNombre: String,
    dni: String = "",
    telefono: String = "",
    email: String = "",
    asignaciones: List<AsignacionItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onEditarNombre: () -> Unit = {},
    onEditarDni: () -> Unit = {},
    onEditarTelefono: () -> Unit = {},
    onEditarEmail: () -> Unit = {},
    onAgregarAsignacion: () -> Unit = {},
    onGestionarAsignaciones: () -> Unit = {},
    onEditarDatos: () -> Unit = {},
    // Foto de perfil
    fotoUrl: String? = null,                  // null = sin foto (se muestran las iniciales)
    onFotoElegida: (Uri) -> Unit = {},        // se llama después de confirmar la foto
    guardandoFoto: Boolean = false,
    fotoActualizada: Boolean = false,
    alertaFoto: AlertaDatos? = null,
    onCerrarExitoFoto: () -> Unit = {},
    onCerrarAlertaFoto: () -> Unit = {},
    onEliminarAsignacion: (AsignacionItem) -> Unit = {},
    onCambiarPassword: () -> Unit = {},
    onNotificacionesConfig: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {

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
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón volver
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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

            // Avatar con foto de perfil editable (FotoPerfilEdu.kt)
            AvatarPerfilEditable(
                nombre = usuarioNombre,
                fotoUrl = fotoUrl,
                onFotoElegida = onFotoElegida,
                guardando = guardandoFoto,
                actualizada = fotoActualizada,
                alerta = alertaFoto,
                onCerrarExito = onCerrarExitoFoto,
                onCerrarAlerta = onCerrarAlertaFoto
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = usuarioNombre,
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextWhite
            )
            Text(
                text = "Docente · I.E. Pedro M. Ureña",
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = TextWhite.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .background(BackgroundBlueLight, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Docente verificado",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = EduconnectBlue
                )
            }
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
            // SECCIÓN DATOS PERSONALES: título + botón "Editar" (abre EditarDatosScreen)
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DATOS PERSONALES",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = EduconnectBlue,
                    modifier = Modifier.weight(1f)
                )
                BotonTitulo(icono = Icons.Filled.Edit, texto = "", onClick = onEditarDatos)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
            ) {
                FilaPerfil(
                    iconRes = R.drawable.user_circle_darkgray,
                    bgColor = BackgroundBlueLight,
                    label = "Nombre completo",
                    valor = usuarioNombre,
                    onClick = null
                )
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
                FilaPerfil(
                    iconRes = R.drawable.dni_darkgray,
                    bgColor = BackgroundBlueLight,
                    label = "DNI",
                    valor = if (dni.isNotEmpty()) dni else "No registrado",
                    onClick = null
                )
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
                FilaPerfil(
                    iconRes = R.drawable.phone_darkgray,
                    bgColor = BackgroundBlueLight,
                    label = "Teléfono",
                    valor = if (telefono.isNotEmpty()) telefono else "No registrado",
                    onClick = null
                )
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
                FilaPerfil(
                    iconRes = R.drawable.mail_darkgray,
                    bgColor = BackgroundBlueLight,
                    label = "Correo",
                    valor = if (email.isNotEmpty()) email else "No registrado",
                    onClick = null
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // SECCIÓN ASIGNACIÓN ACADÉMICA: título + botón "+ Asignar" (único acceso a Agregar asignación)
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ASIGNACIONES",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = EduconnectBlue,
                    modifier = Modifier.weight(1f)
                )
                // "Gestionar" abre Mis asignaciones (ver y eliminar); "+ Asignar" abre Agregar asignación
                BotonTitulo(icono = Icons.Filled.Add, texto = "", onClick = onAgregarAsignacion)

                if (asignaciones.isNotEmpty()) {
                    BotonTitulo(icono = Icons.Filled.Settings, texto = "", onClick = onGestionarAsignaciones)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
            ) {
                // FILA GRADO Y SECCIÓN (solo informativa)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(BackgroundBlueLight, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.level_darkgray),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Grado y sección",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Medium,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (asignaciones.isEmpty()) SinAsignaciones()
                            asignaciones.map { "${it.gradoNombre.first()}° ${it.seccionNombre}" }
                                .distinct().forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .background(BackgroundBlueLight, RoundedCornerShape(20.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(text = tag, fontFamily = Roboto,
                                            fontWeight = FontWeight.Bold, fontSize = 16.sp,
                                            color = EduconnectBlue)
                                    }
                                }
                        }
                    }
                }

                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)

                // FILA CURSOS ASIGNADOS (solo informativa)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(BackgroundBlueLight, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.task_darkgray),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Cursos asignados",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Medium,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (asignaciones.isEmpty()) SinAsignaciones()
                            asignaciones.map { it.cursoNombre }.distinct().forEach { curso ->
                                Box(
                                    modifier = Modifier
                                        .background(BackgroundBlueLight, RoundedCornerShape(20.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(text = curso, fontFamily = Roboto,
                                        fontWeight = FontWeight.Bold, fontSize = 15.sp,
                                        color = TextBlue)
                                }
                            }
                        }
                    }
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

            // BOTÓN CERRAR SESIÓN
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundOrangeLight, RoundedCornerShape(14.dp))
                    .border(1.dp, AccentOrange, RoundedCornerShape(14.dp))
                    .clickable { onCerrarSesion() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logout_orange),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
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
        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Perfil"
        )
    }
}

// COMPONENTES REUTILIZABLES
@Composable
private fun SinAsignaciones() {
    Text(text = "Sin asignar · usa \"+ Asignar\"", fontFamily = Roboto,
        fontSize = 14.sp, color = TextSecondary)
}

@Composable
fun SeccionTitulo(titulo: String) {
    Text(
        text = titulo,
        fontFamily = Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = EduconnectBlue,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun FilaPerfil(
    iconRes: Int,
    bgColor: Color,
    label: String,
    valor: String,
    onClick: (() -> Unit)? = null      // null = fila solo informativa (sin flecha)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(bgColor, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontFamily = Roboto,
                fontSize = 16.sp,
                color = TextPrimary
            )
            Text(
                text = valor,
                fontFamily = Roboto,
                fontWeight = FontWeight.Medium,
                fontSize = 19.sp,
                color = TextBlue
            )
        }
        if (onClick != null) {
            Image(
                painter = painterResource(id = R.drawable.next_gray),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// Botón de texto naranja con ícono, a la derecha de un título de sección (también lo usa el perfil del padre)
@Composable
fun BotonTitulo(icono: ImageVector, texto: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = AccentOrange,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = texto,
            fontFamily = Roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = AccentOrange
        )
    }
}

@Composable
fun FilaAccion(
    iconRes: Int,
    bgColor: Color,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(bgColor, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = label,
            fontFamily = Roboto,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Image(
            painter = painterResource(id = R.drawable.next_gray),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilDocentePreview() {
    EduConnectAppTheme {
        PerfilDocenteScreen(
            usuarioNombre = "Hector Reyna Gomez",
            dni = "47829301",
            telefono = "987654321",
            email = "hector@gmail.com",
            asignaciones = listOf(
                AsignacionItem("1er Grado", "A", "Matemáticas"),
                AsignacionItem("2do Grado", "B", "Comunicación")
            )
        )
    }
}