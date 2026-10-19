package com.educonnectapp.ui.screens.comun

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupExitoEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoralClaro
import com.educonnectapp.ui.theme.EduCoralOscuro
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// Texto, ícono y tono de cada tipo de notificación (los tipos que llegan dependen del rol)
private data class InfoTipo(val titulo: String, val descripcion: String, val icono: ImageVector, val tono: TonoEdu)

private fun infoTipo(tipo: String): InfoTipo = when (tipo) {
    "ASISTENCIA" -> InfoTipo("Asistencias", "Registro y corrección de asistencias", Icons.Rounded.EventAvailable, TonoEdu.AZUL)
    "COMUNICADO" -> InfoTipo("Comunicados", "Mensajes del docente a los padres de la sección", Icons.Rounded.Mail, TonoEdu.CORAL)
    "PUBLICACION" -> InfoTipo("Tareas y evaluaciones", "Nuevas tareas y avisos de evaluación", Icons.AutoMirrored.Rounded.Assignment, TonoEdu.AMARILLO)
    "CALIFICACION" -> InfoTipo("Entregas de tareas", "Si el alumno entregó o no una tarea", Icons.Rounded.TaskAlt, TonoEdu.VERDE)
    "AVISO" -> InfoTipo("Avisos de la institución", "Comunicados generales de la I.E.", Icons.Rounded.Campaign, TonoEdu.AZUL)
    else -> InfoTipo(tipo, "", Icons.Rounded.Info, TonoEdu.GRIS)
}

// =====================================================================
// NOTIFICACIONES (rediseño 2026) · lo usan docente y padre
// Cabecera fija azul oscuro · estado de las notificaciones del celular + Ajustes ·
// interruptor por tipo (se guarda en el backend) · "Guardar cambios"
// =====================================================================

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
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Notificaciones",
            subtitulo = "Elige qué avisos recibir en tu celular",
            onVolver = onBack,
            colorFondo = ColorModulo.Perfil,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Perfil
                )
            }
        )

        // CONTENIDO DESPLAZABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ESTADO EN EL CELULAR (Android) + acceso a los ajustes del sistema
            TarjetaEdu(
                relleno = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                colorFondo = if (notificacionesSistemaActivas) Color.White else EduCoralClaro
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconoCirculoEdu(
                        if (notificacionesSistemaActivas) Icons.Rounded.NotificationsActive else Icons.Rounded.NotificationsOff,
                        if (notificacionesSistemaActivas) TonoEdu.VERDE else TonoEdu.CORAL,
                        tamano = 40.dp,
                        solido = !notificacionesSistemaActivas
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Notificaciones del celular", fontFamily = Nunito, fontWeight = FontWeight.Black,
                            fontSize = 15.sp, color = EduTexto
                        )
                        Text(
                            if (notificacionesSistemaActivas) "Activadas · sonido y vibración en Ajustes"
                            else "Bloqueadas: actívalas en Ajustes",
                            fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp,
                            color = if (notificacionesSistemaActivas) EduTealOscuro else EduCoralOscuro
                        )
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(EduAzulOscuroClaro)
                            .clickable(onClick = onAbrirAjustesSistema)
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(Icons.Rounded.Settings, contentDescription = null, tint = EduAzulOscuro, modifier = Modifier.size(15.dp))
                        Text("Ajustes", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.sp, color = EduAzulOscuro)
                    }
                }
            }

            // AVISOS DE EDUCONNECT
            Spacer(modifier = Modifier.size(2.dp))
            TituloSeccionEdu("Avisos de EduConnect") {
                if (cambios.isNotEmpty()) {
                    ChipEdu(if (cambios.size == 1) "1 cambio" else "${cambios.size} cambios", TonoEdu.AMARILLO)
                }
            }
            TarjetaEdu(relleno = PaddingValues(horizontal = 14.dp, vertical = 2.dp)) {
                if (cargando) {
                    Row(
                        modifier = Modifier.padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = EduTeal, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                        Text(
                            "Cargando tus preferencias...", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                            fontSize = 15.sp, color = EduTextoSecundario
                        )
                    }
                } else {
                    val tipos = editadas.keys.toList()
                    tipos.forEachIndexed { i, tipo ->
                        FilaInterruptor(
                            info = infoTipo(tipo),
                            activo = editadas[tipo] ?: true,
                            onCambio = { editadas = editadas + (tipo to it) }
                        )
                        if (i < tipos.lastIndex) HorizontalDivider(color = EduFondo, thickness = 1.dp)
                    }
                }
            }

            // Aclaración: la campana siempre registra
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EduTealClaro, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = EduTealOscuro, modifier = Modifier.size(22.dp))
                Text(
                    "Si apagas un tipo, no te llegará al celular, pero seguirás viéndolo en la campana de la app.",
                    modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp, lineHeight = 18.sp, color = EduEtiqueta
                )
            }
        }

        // BOTÓN FIJO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            BotonPrincipalEdu(
                texto = "Guardar cambios",
                tipo = TipoBotonEdu.GUARDAR,
                icono = Icons.Rounded.Save,
                habilitado = cambios.isNotEmpty() && !cargando && !procesando,
                onClick = { mostrarConfirmacion = true }
            )
        }

        barraInferior()
    }

    // ===================== POPUPS =====================
    if (mostrarConfirmacion) {
        val activar = cambios.filterValues { it }.keys.map { infoTipo(it).titulo }
        val desactivar = cambios.filterValues { !it }.keys.map { infoTipo(it).titulo }
        PopupConfirmacionEdu(
            titulo = "¿Guardar tus preferencias?",
            mensaje = "Revisa los cambios:",
            textoConfirmar = "Guardar",
            colorConfirmar = TipoBotonEdu.GUARDAR.fondo,
            icono = Icons.Rounded.NotificationsActive,
            onCancelar = { mostrarConfirmacion = false },
            onConfirmar = {
                mostrarConfirmacion = false
                onGuardar(cambios)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EduFondo, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (activar.isNotEmpty()) {
                    Text("Se activarán", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.5.sp, color = EduTealOscuro)
                    activar.forEach { ChipEdu(it, TonoEdu.VERDE) }
                }
                if (desactivar.isNotEmpty()) {
                    Text("Se desactivarán", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.5.sp, color = EduCoralOscuro)
                    desactivar.forEach { ChipEdu(it, TonoEdu.CORAL) }
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
            icono = Icons.Rounded.Warning,
            color = EduAmarillo,
            titulo = it.titulo,
            mensaje = it.mensaje,
            onCerrar = onCerrarAlerta
        )
    }
}

// Fila con ícono de color, título, descripción e interruptor turquesa
@Composable
private fun FilaInterruptor(info: InfoTipo, activo: Boolean, onCambio: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCambio(!activo) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconoCirculoEdu(info.icono, info.tono, tamano = 40.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(info.titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTexto)
            if (info.descripcion.isNotBlank()) {
                Text(
                    info.descripcion, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp,
                    color = EduTextoSecundario, lineHeight = 17.sp
                )
            }
        }
        Switch(
            checked = activo,
            onCheckedChange = onCambio,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EduTeal,
                checkedBorderColor = EduTeal,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = EduBorde,
                uncheckedBorderColor = EduBorde
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
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
