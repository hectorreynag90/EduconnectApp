package com.educonnectapp.ui.screens.comun

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupExitoEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.FondoRojo
import com.educonnectapp.ui.theme.NaranjaMedia
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.RojoError
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite

private val FondoAzulNotif = Color(0xFFE6F1FB)

// Texto e ícono de cada tipo de notificación (los tipos que llegan dependen del rol)
private data class InfoTipo(val titulo: String, val descripcion: String, val icono: ImageVector)

private fun infoTipo(tipo: String): InfoTipo = when (tipo) {
    "ASISTENCIA" -> InfoTipo("Asistencias", "Registro y corrección de la asistencia de tu hijo", Icons.Filled.EventAvailable)
    "COMUNICADO" -> InfoTipo("Comunicados", "Mensajes del docente a los padres de la sección", Icons.Filled.Mail)
    "PUBLICACION" -> InfoTipo("Tareas y evaluaciones", "Nuevas tareas y avisos de evaluación", Icons.Filled.Assignment)
    "CALIFICACION" -> InfoTipo("Entregas de tareas", "Si tu hijo entregó o no una tarea", Icons.Filled.TaskAlt)
    "AVISO" -> InfoTipo("Avisos de la institución", "Comunicados generales de la I.E.", Icons.Filled.Campaign)
    else -> InfoTipo(tipo, "", Icons.Filled.Info)
}

// Configuración de notificaciones: interruptores por tipo (se guardan en el backend)
// y acceso a los ajustes del sistema (sonido, vibración, bloquear).
@Composable
fun NotificacionesConfigScreen(
    preferencias: Map<String, Boolean>,       // tipo -> activo (como está guardado en el servidor)
    cargando: Boolean = false,
    notificacionesSistemaActivas: Boolean = true,
    onBack: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onGuardar: (cambios: Map<String, Boolean>) -> Unit = {},
    onAbrirAjustesSistema: () -> Unit = {},
    // Estado del guardado (lo controla MainActivity)
    procesando: Boolean = false,
    exito: Boolean = false,
    alerta: AlertaDatos? = null,
    onCerrarExito: () -> Unit = {},
    onCerrarAlerta: () -> Unit = {},
    barraInferior: @Composable () -> Unit = {}
) {
    // Copia editable; se reinicia cuando llegan las preferencias del servidor
    var editadas by remember(preferencias) { mutableStateOf(preferencias) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    val cambios = editadas.filter { (tipo, activo) -> preferencias[tipo] != activo }

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
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(35.dp).offset(x = (-5).dp)) {
                    Image(painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver", modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(text = "Notificaciones", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextWhite)
                    Text(text = "Elige qué avisos recibir en tu celular", fontFamily = Roboto,
                        fontSize = 16.sp, color = TextWhite.copy(alpha = 0.85f))
                }
            }
            IconButton(onClick = onNotificaciones) {
                Image(painter = painterResource(id = R.drawable.notification_white),
                    contentDescription = "Notificaciones", modifier = Modifier.size(35.dp))
            }
        }

        // CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Aviso si Android tiene bloqueadas las notificaciones de la app
            if (!notificacionesSistemaActivas) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FondoRojo, RoundedCornerShape(12.dp))
                        .border(1.dp, RojoError, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Filled.NotificationsOff, contentDescription = null,
                        tint = RojoError, modifier = Modifier.size(24.dp))
                    Text(
                        text = "Las notificaciones de EduConnect están bloqueadas en tu celular. " +
                                "Actívalas en \"Ajustes del sistema\".",
                        fontFamily = Roboto, fontSize = 14.sp, color = RojoError, lineHeight = 19.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(text = "NOTIFICACIONES PUSH", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                fontSize = 16.sp, color = EduconnectBlue, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
            ) {
                if (cargando) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EduconnectBlue, strokeWidth = 3.dp,
                            modifier = Modifier.size(32.dp))
                    }
                } else {
                    val tipos = editadas.keys.toList()
                    tipos.forEachIndexed { i, tipo ->
                        FilaInterruptor(
                            info = infoTipo(tipo),
                            activo = editadas[tipo] ?: true,
                            onCambio = { editadas = editadas + (tipo to it) }
                        )
                        if (i < tipos.lastIndex) HorizontalDivider(color = BorderLight, thickness = 1.dp)
                    }
                }
            }

            // Aclaración: la campana siempre registra
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoAzulNotif, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Info, contentDescription = null,
                    tint = EduconnectBlue, modifier = Modifier.size(20.dp))
                Text(
                    text = "Si apagas un tipo, no te llegará al celular, pero seguirás viéndolo en la campana de la app.",
                    fontFamily = Roboto, fontSize = 14.sp, color = TextBlue, lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // AJUSTES DEL SISTEMA
            Text(text = "SONIDO Y VIBRACIÓN", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                fontSize = 16.sp, color = EduconnectBlue, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
            OutlinedButton(
                onClick = onAbrirAjustesSistema,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(1.dp, EduconnectBlue)
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null,
                    tint = EduconnectBlue, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Ajustes del sistema", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, color = EduconnectBlue)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // GUARDAR (solo si hay cambios)
            Button(
                onClick = { mostrarConfirmacion = true },
                enabled = cambios.isNotEmpty() && !cargando,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOrange,
                    disabledContainerColor = BorderLight
                )
            ) {
                Text(text = "Guardar cambios", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = if (cambios.isNotEmpty()) TextWhite else TextSecondary)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        barraInferior()
    }

    // ===================== POPUPS (PopupsEdu.kt) =====================
    if (mostrarConfirmacion) {
        val activar = cambios.filterValues { it }.keys.map { infoTipo(it).titulo }
        val desactivar = cambios.filterValues { !it }.keys.map { infoTipo(it).titulo }
        PopupConfirmacionEdu(
            titulo = "¿Guardar tus preferencias?",
            mensaje = "Revisa los cambios:",
            textoConfirmar = "Guardar",
            onCancelar = { mostrarConfirmacion = false },
            onConfirmar = {
                mostrarConfirmacion = false
                onGuardar(cambios)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoAzulNotif, RoundedCornerShape(12.dp))
                    .border(1.dp, EduconnectBlue, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (activar.isNotEmpty()) {
                    Text(
                        text = "Se activarán:", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 14.sp, color = EduconnectBlue
                    )
                    activar.forEach {
                        Text(
                            "• $it",
                            fontFamily = Roboto,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
                if (desactivar.isNotEmpty()) {
                    Text(
                        text = "Se desactivarán:",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = RojoError
                    )
                    desactivar.forEach {
                        Text(
                            "• $it",
                            fontFamily = Roboto,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }

    if (procesando) PopupProcesandoEdu(titulo = "Guardando preferencias…")

    if (exito) {
        PopupExitoEdu(
            titulo = "¡Preferencias guardadas!",
            mensaje = "Solo recibirás en tu celular los avisos que dejaste activos.",
            onCerrar = onCerrarExito
        )
    }

    alerta?.let {
        PopupAlertaEdu(
            icono = Icons.Filled.Warning,
            color = NaranjaMedia,
            titulo = it.titulo,
            mensaje = it.mensaje,
            onCerrar = onCerrarAlerta
        )
    }
}

@Composable
private fun FilaInterruptor(info: InfoTipo, activo: Boolean, onCambio: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(FondoAzulNotif, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(info.icono, contentDescription = null, tint = EduconnectBlue, modifier = Modifier.size(24.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = info.titulo, fontFamily = Roboto, fontWeight = FontWeight.Medium,
                fontSize = 17.sp, color = TextPrimary)
            Text(text = info.descripcion, fontFamily = Roboto, fontSize = 13.sp,
                color = TextSecondary, lineHeight = 17.sp)
        }
        Switch(
            checked = activo,
            onCheckedChange = onCambio,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextWhite,
                checkedTrackColor = AccentOrange,
                uncheckedThumbColor = TextWhite,
                uncheckedTrackColor = BorderLight,
                uncheckedBorderColor = BorderLight
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificacionesConfigPreview() {
    EduConnectAppTheme {
        NotificacionesConfigScreen(
            preferencias = linkedMapOf(
                "ASISTENCIA" to true, "COMUNICADO" to true, "PUBLICACION" to false,
                "CALIFICACION" to true, "AVISO" to true
            )
        )
    }
}
