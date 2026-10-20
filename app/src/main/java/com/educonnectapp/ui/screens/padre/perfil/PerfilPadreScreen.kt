package com.educonnectapp.ui.screens.padre.perfil

import android.net.Uri
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Settings
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.educonnectapp.ui.screens.padre.inicio.BottomNavBarPadre
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// Hijo asociado al padre
data class HijoAsociado(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val codigoEstudiante: String
)

// =====================================================================
// PERFIL DEL PADRE (rediseño 2026) · mismo diseño que el perfil del docente
// Cabecera fija azul oscuro (foto, nombre, hijos asociados) · datos personales ·
// mis hijos (Asociar / Gestionar) · configuración · "Cerrar sesión" (con confirmación)
// =====================================================================

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
    onEliminarHijo: (HijoAsociado) -> Unit = {},   // ya no se usa aquí: se quita desde "Gestionar"
    onGestionarHijos: () -> Unit = {},
    onEditarDatos: () -> Unit = {},
    // Foto de perfil
    fotoUrl: String? = null,
    onFotoElegida: (Uri) -> Unit = {},
    guardandoFoto: Boolean = false,
    fotoActualizada: Boolean = false,
    alertaFoto: AlertaDatos? = null,
    onCerrarExitoFoto: () -> Unit = {},
    onCerrarAlertaFoto: () -> Unit = {},
    onCambiarPassword: () -> Unit = {},
    onNotificacionesConfig: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},            // se llama después de confirmar en el popup
) {
    var confirmarCierre by remember { mutableStateOf(false) }
    val estilo = estiloCabecera(ColorModulo.Perfil)

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
        // CABECERA FIJA: título + campana, y debajo foto, nombre e hijos asociados
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
                        "Datos, hijos y configuración", fontFamily = Nunito, fontWeight = FontWeight.Bold,
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
                // Foto de perfil editable (FotoPerfilEdu.kt)
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
                        "Padre de familia · I.E. Pedro M. Ureña", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp, color = estilo.subtitulo
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ChipEdu(
                        when (hijosAsociados.size) {
                            0 -> "Sin hijos asociados"
                            1 -> "1 hijo asociado"
                            else -> "${hijosAsociados.size} hijos asociados"
                        },
                        if (hijosAsociados.isEmpty()) TonoEdu.AMARILLO else TonoEdu.VERDE,
                        icono = Icons.Rounded.Groups
                    )
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
                AccionTitulo(Icons.Rounded.Edit, "Editar", EduAzulOscuroClaro, EduAzulOscuro, onEditarDatos)
            }
            TarjetaEdu(relleno = PaddingValues(horizontal = 14.dp, vertical = 2.dp)) {
                FilaDatoPerfil(Icons.Rounded.Badge, "DNI", dni.ifBlank { "No registrado" })
                FilaDatoPerfil(Icons.Rounded.Phone, "Teléfono", telefono.ifBlank { "No registrado" })
                FilaDatoPerfil(Icons.Rounded.Mail, "Correo", email.ifBlank { "No registrado" }, ultima = true)
            }

            // MIS HIJOS
            Spacer(modifier = Modifier.height(2.dp))
            TituloSeccionEdu(if (hijosAsociados.isEmpty()) "Mis hijos" else "Mis hijos · ${hijosAsociados.size}") {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AccionTitulo(Icons.Rounded.PersonAdd, "Asociar", EduTealClaro, EduTealOscuro, onAsociarHijo)
                    if (hijosAsociados.isNotEmpty()) {
                        AccionTitulo(Icons.Rounded.Settings, "Gestionar", EduAzulOscuroClaro, EduAzulOscuro, onGestionarHijos)
                    }
                }
            }
            TarjetaEdu(relleno = PaddingValues(horizontal = 14.dp, vertical = 2.dp)) {
                if (hijosAsociados.isEmpty()) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconoCirculoEdu(Icons.Rounded.Groups, TonoEdu.GRIS, tamano = 38.dp)
                        Text(
                            "Aún no tienes hijos asociados. Usa \"Asociar\" con el código que te dio el colegio.",
                            modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp, color = EduTextoSecundario
                        )
                    }
                }
                hijosAsociados.forEachIndexed { i, h ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InicialesHijoPerfil(h, if (i % 2 == 0) EduAmarillo else EduTealClaro)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${h.nombres} ${h.apellidos}".trim(), fontFamily = Nunito, fontWeight = FontWeight.Black,
                                    fontSize = 15.sp, color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "${h.gradoNombre} ${h.seccionNombre} · Código ${h.codigoEstudiante}",
                                    fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario
                                )
                            }
                        }
                        if (i < hijosAsociados.lastIndex) HorizontalDivider(color = EduFondo, thickness = 1.dp)
                    }
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

        BottomNavBarPadre(
            onInicio = onHomePadre,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Perfil"
        )
    }
}

// Acción pequeña a la derecha de un título de sección ("Editar", "Asociar", "Gestionar")
@Composable
private fun AccionTitulo(icono: ImageVector, texto: String, fondo: Color, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(fondo)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
        Text(texto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.sp, color = color)
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

// Iniciales del hijo en un círculo de color (también la usan Mis hijos y Asociar hijo)
@Composable
internal fun InicialesHijoPerfil(hijo: HijoAsociado, fondo: Color, tamano: Int = 42) {
    val iniciales = "${hijo.nombres.firstOrNull() ?: ""}${hijo.apellidos.firstOrNull() ?: ""}".uppercase()
    Box(
        modifier = Modifier.size(tamano.dp).background(fondo, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(iniciales, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = (tamano * 0.36f).sp, color = EduTexto)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PerfilPadrePreview() {
    EduConnectAppTheme {
        PerfilPadreScreen(
            usuarioNombre = "Luis Perez Garcia",
            dni = "41238904",
            telefono = "912345678",
            email = "luis@gmail.com",
            hijosAsociados = listOf(
                HijoAsociado(1L, "Angela", "Perez Garcia", "2do Grado", "A", "PMU0006"),
                HijoAsociado(2L, "Pedro", "Perez Garcia", "1er Grado", "A", "PMU0001")
            ),
        )
    }
}
