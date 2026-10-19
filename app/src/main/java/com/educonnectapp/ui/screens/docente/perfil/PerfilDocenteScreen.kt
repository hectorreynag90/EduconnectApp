package com.educonnectapp.ui.screens.docente.perfil

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.components.AvatarPerfilEditable
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.estiloCabecera
import com.educonnectapp.ui.screens.comun.AlertaDatos
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Nunito
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import androidx.compose.foundation.layout.statusBarsPadding

// Data class para asignación académica (alumnos: cantidad, 0 si no se conoce)
data class AsignacionItem(
    val gradoNombre: String,
    val seccionNombre: String,
    val cursoNombre: String,
    val alumnos: Int = 0
)

// =====================================================================
// MI PERFIL · docente (rediseño 2026)
// Cabecera fija azul oscuro con foto, nombre y "Docente verificado" · datos personales ·
// mis clases · configuración · "Cerrar sesión" (con confirmación)
// =====================================================================

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
    onCerrarSesion: () -> Unit = {}           // se llama después de confirmar en el popup
) {
    var confirmarCierre by remember { mutableStateOf(false) }
    val estilo = estiloCabecera(ColorModulo.Perfil)
    // Agrupadas por grado y sección (en el orden en que llegan)
    val clases = asignaciones.sortedWith(compareBy({ it.gradoNombre }, { it.seccionNombre }, { it.cursoNombre }))

    if (confirmarCierre) {
        PopupConfirmacionEdu(
            titulo = "¿Cerrar sesión?",
            mensaje = "Tendrás que ingresar de nuevo con tu correo y contraseña para volver a usar EduConnect.",
            textoConfirmar = "Cerrar sesión",
            colorConfirmar = TipoBotonEdu.ELIMINAR.fondo,
            icono = Icons.AutoMirrored.Rounded.Logout,
            onCancelar = { confirmarCierre = false },
            onConfirmar = {
                confirmarCierre = false
                onCerrarSesion()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA: título + campana, y debajo foto, nombre y verificación
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorModulo.Perfil, RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                .statusBarsPadding()
                .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 22.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mi perfil", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 23.sp, color = estilo.titulo)
                    Text(
                        "Datos, clases y configuración", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                        fontSize = 14.sp, color = estilo.subtitulo
                    )
                }
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Perfil
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Foto de perfil editable (FotoPerfilEdu.kt): al tocarla se elige una nueva
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        usuarioNombre, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 19.sp,
                        color = estilo.titulo, maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Docente · I.E. Pedro M. Ureña", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp, color = estilo.subtitulo
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ChipEdu("Docente verificado", TonoEdu.VERDE, icono = Icons.Rounded.Verified)
                }
            }
        }

        // CONTENIDO DESPLAZABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // DATOS PERSONALES
            TituloSeccionEdu("Datos personales") {
                AccionTitulo(Icons.Rounded.Edit, "Editar", onEditarDatos)
            }
            TarjetaEdu(relleno = PaddingValues(horizontal = 14.dp, vertical = 2.dp)) {
                FilaDatoPerfil(Icons.Rounded.Badge, "DNI", dni.ifBlank { "No registrado" })
                FilaDatoPerfil(Icons.Rounded.Phone, "Teléfono", telefono.ifBlank { "No registrado" })
                FilaDatoPerfil(Icons.Rounded.Mail, "Correo", email.ifBlank { "No registrado" }, ultima = true)
            }

            // MIS CLASES
            Spacer(modifier = Modifier.height(2.dp))
            TituloSeccionEdu(if (clases.isEmpty()) "Mis clases" else "Mis clases · ${clases.size}") {
                if (clases.isEmpty()) AccionTitulo(Icons.Rounded.Add, "Agregar", onAgregarAsignacion)
                else AccionTitulo(Icons.Rounded.Settings, "Gestionar", onGestionarAsignaciones)
            }
            TarjetaEdu(relleno = PaddingValues(horizontal = 14.dp, vertical = 2.dp)) {
                if (clases.isEmpty()) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconoCirculoEdu(Icons.Rounded.School, TonoEdu.GRIS, tamano = 38.dp)
                        Text(
                            "Aún no tienes clases. Usa \"Agregar\" para empezar.",
                            modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp, color = EduTextoSecundario
                        )
                    }
                }
                clases.forEachIndexed { i, a ->
                    FilaDatoPerfil(
                        Icons.Rounded.School,
                        "${a.gradoNombre} ${a.seccionNombre}",
                        a.cursoNombre + if (a.alumnos > 0) " · ${a.alumnos} alumnos" else "",
                        ultima = i == clases.lastIndex
                    )
                }
            }

            // CONFIGURACIÓN
            Spacer(modifier = Modifier.height(2.dp))
            TituloSeccionEdu("Configuración")
            TarjetaEdu(relleno = PaddingValues(horizontal = 14.dp, vertical = 2.dp)) {
                FilaAccionPerfil(Icons.Rounded.Key, "Cambiar contraseña", onCambiarPassword)
                FilaAccionPerfil(Icons.Rounded.NotificationsActive, "Notificaciones", onNotificacionesConfig, ultima = true)
            }

            // CERRAR SESIÓN
            Spacer(modifier = Modifier.height(6.dp))
            BotonPrincipalEdu(
                texto = "Cerrar sesión",
                tipo = TipoBotonEdu.ELIMINAR,
                icono = Icons.AutoMirrored.Rounded.Logout,
                onClick = { confirmarCierre = true }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Perfil"
        )
    }
}

// Acción pequeña a la derecha de un título de sección ("Editar", "Gestionar")
@Composable
private fun AccionTitulo(icono: ImageVector, texto: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(EduAzulOscuroClaro)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icono, contentDescription = null, tint = EduAzulOscuro, modifier = Modifier.size(15.dp))
        Text(texto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.sp, color = EduAzulOscuro)
    }
}

// Fila informativa: ícono en círculo, etiqueta pequeña y valor
@Composable
private fun FilaDatoPerfil(icono: ImageVector, etiqueta: String, valor: String, ultima: Boolean = false) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconoCirculoEdu(icono, TonoEdu.AZUL, tamano = 38.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(etiqueta, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario)
                Text(
                    valor, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTexto,
                    maxLines = 2, overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (!ultima) HorizontalDivider(color = EduFondo, thickness = 1.dp)
    }
}

// Fila de acción con flecha (Cambiar contraseña, Notificaciones)
@Composable
private fun FilaAccionPerfil(icono: ImageVector, texto: String, onClick: () -> Unit, ultima: Boolean = false) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconoCirculoEdu(icono, TonoEdu.AZUL, tamano = 38.dp)
            Text(texto, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTexto)
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(22.dp))
        }
        if (!ultima) HorizontalDivider(color = EduFondo, thickness = 1.dp)
    }
}

// =====================================================================
// COMPONENTES ANTERIORES: los sigue usando el perfil del padre (se rediseñan con el módulo del padre)
// =====================================================================

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
            Text(text = label, fontFamily = Roboto, fontSize = 16.sp, color = TextPrimary)
            Text(text = valor, fontFamily = Roboto, fontWeight = FontWeight.Medium, fontSize = 19.sp, color = TextBlue)
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

// Botón de texto con ícono a la derecha de un título de sección (lo usa el perfil del padre)
@Composable
fun BotonTitulo(icono: ImageVector, texto: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icono, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = texto, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AccentOrange)
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
            Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(28.dp))
        }
        Text(
            text = label, fontFamily = Roboto, fontWeight = FontWeight.Medium, fontSize = 17.sp,
            color = TextPrimary, modifier = Modifier.weight(1f)
        )
        Image(painter = painterResource(id = R.drawable.next_gray), contentDescription = null, modifier = Modifier.size(18.dp))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PerfilDocentePreview() {
    EduConnectAppTheme {
        PerfilDocenteScreen(
            usuarioNombre = "Hector Reyna Gomez",
            dni = "47829301",
            telefono = "987654321",
            email = "hector@gmail.com",
            asignaciones = listOf(
                AsignacionItem("1er Grado", "A", "Ciencia y Tecnología", 5),
                AsignacionItem("2do Grado", "B", "Matemáticas", 24)
            )
        )
    }
}
