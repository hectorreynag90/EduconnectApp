package com.educonnectapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
import com.educonnectapp.ui.screens.comun.AlertaDatos
import com.educonnectapp.ui.screens.docente.inicio.AvatarUsuario
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// =====================================================================
// FOTO DE PERFIL (común para el perfil del docente y del padre) · rediseño 2026
// - Sin foto: tocar el avatar o la cámara abre la galería.
// - Con foto: tocar el avatar la muestra ampliada con un lápiz para cambiarla.
// - Al elegir: confirmación con vista previa -> procesando -> éxito (PopupsEdu.kt).
// colorFondo: color de la cabecera donde va el avatar (para el borde de la cámara)
// =====================================================================

@Composable
fun AvatarPerfilEditable(
    nombre: String,
    fotoUrl: String?,                         // null = sin foto (se muestran las iniciales)
    onFotoElegida: (Uri) -> Unit,             // se llama después de confirmar la foto
    guardando: Boolean,
    actualizada: Boolean,
    alerta: AlertaDatos?,
    onCerrarExito: () -> Unit,
    onCerrarAlerta: () -> Unit,
    tamano: Dp = 72.dp,
    colorFondo: Color = EduAzulOscuro
) {
    var verFotoAmpliada by remember { mutableStateOf(false) }
    var fotoPendiente by remember { mutableStateOf<Uri?>(null) }
    // Selector de fotos de Android: abre la galería sin pedir permisos
    val selectorFoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) fotoPendiente = uri
    }
    val abrirGaleria = {
        verFotoAmpliada = false
        selectorFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // Avatar con borde claro y el botón de cámara (turquesa) abajo a la derecha
    Box(contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .size(tamano)
                .border(3.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                .padding(3.dp)
        ) {
            AvatarUsuario(
                nombre = nombre,
                fotoUrl = fotoUrl,
                tamano = tamano - 6.dp,
                tamanoTexto = (tamano.value * 0.36f).sp,
                modifier = Modifier.clickable {
                    if (fotoUrl != null) verFotoAmpliada = true else abrirGaleria()
                }
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(EduTeal)
                .border(2.dp, colorFondo, CircleShape)
                .clickable { abrirGaleria() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.CameraAlt, contentDescription = "Cambiar foto", tint = Color.White, modifier = Modifier.size(15.dp))
        }
    }

    // Foto ampliada con el lápiz
    if (verFotoAmpliada && fotoUrl != null) {
        PopupFotoAmpliada(
            fotoUrl = fotoUrl,
            onEditar = { abrirGaleria() },
            onCerrar = { verFotoAmpliada = false }
        )
    }

    // 1. Confirmación con la vista previa de la foto elegida
    fotoPendiente?.let { uri ->
        PopupConfirmacionEdu(
            titulo = "¿Usar esta foto?",
            mensaje = "Así se verá tu foto de perfil:",
            textoConfirmar = "Guardar",
            colorConfirmar = TipoBotonEdu.GUARDAR.fondo,
            onCancelar = { fotoPendiente = null },
            onConfirmar = {
                fotoPendiente = null
                onFotoElegida(uri)
            }
        ) {
            AsyncImage(
                model = uri,
                contentDescription = "Vista previa",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(3.dp, EduTeal, CircleShape)
            )
        }
    }

    // 2. Procesando
    if (guardando) PopupProcesandoEdu(titulo = "Guardando foto…")

    // 3. Éxito: se cierra solo a los 5 segundos o con la X
    if (actualizada) {
        PopupExitoEdu(
            titulo = "¡Foto actualizada!",
            mensaje = "Tu nueva foto ya aparece en tu perfil.",
            onCerrar = onCerrarExito
        )
    }

    // Error al guardar la foto
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

// Foto de perfil ampliada: círculo grande, lápiz azul oscuro para cambiarla y X para cerrar
@Composable
private fun PopupFotoAmpliada(fotoUrl: String, onEditar: () -> Unit, onCerrar: () -> Unit) {
    Dialog(onDismissRequest = onCerrar) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // X para cerrar
            Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = onCerrar, modifier = Modifier.size(40.dp)) {
                    Box(
                        modifier = Modifier.size(34.dp).background(EduFondo, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Cerrar", tint = EduTextoSecundario, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Text("Foto de perfil", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 21.sp, color = EduTexto)
            Spacer(modifier = Modifier.height(16.dp))
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = fotoUrl,
                    contentDescription = "Foto de perfil ampliada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape)
                        .background(EduFondo)
                        .border(4.dp, EduTeal, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp, bottom = 12.dp)
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(TipoBotonEdu.ACTUALIZAR.fondo)
                        .border(3.dp, Color.White, CircleShape)
                        .clickable { onEditar() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Cambiar foto", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                "Toca el lápiz para cambiar tu foto", fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                fontSize = 14.5.sp, color = EduTextoSecundario
            )
        }
    }
}
