package com.educonnectapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.educonnectapp.R
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupExitoEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.NaranjaMedia
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite

// =====================================================================
// FOTO DE PERFIL (común para el perfil del docente y del padre)
// - Sin foto: tocar el avatar o la cámara abre la galería.
// - Con foto: tocar el avatar la muestra ampliada en círculo con un lápiz para cambiarla.
// - Al elegir: confirmación con vista previa -> procesando -> éxito (PopupsEdu.kt).
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
    onCerrarAlerta: () -> Unit
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

    // Avatar con el botón de cámara abajo a la derecha
    Box(contentAlignment = Alignment.BottomEnd) {
        AvatarUsuario(
            nombre = nombre,
            fotoUrl = fotoUrl,
            tamano = 80.dp,
            tamanoTexto = 35.sp,
            modifier = Modifier.clickable { if (fotoUrl != null) verFotoAmpliada = true else abrirGaleria() }
        )
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(BackgroundWhite)
                .clickable { abrirGaleria() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.camera_blue),
                contentDescription = "Cambiar foto",
                modifier = Modifier.size(23.dp)
            )
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
                    .border(3.dp, EduconnectBlue, CircleShape)
            )
        }
    }

    // 2. Procesando
    if (guardando) PopupProcesandoEdu(titulo = "Guardando foto…")

    // 3. Éxito: se cierra solo a los 5 segundos o con la X
    if (actualizada) {
        PopupExitoEdu(
            titulo = "¡Foto actualizada con éxito!",
            mensaje = "Tu nueva foto ya aparece en tu perfil.",
            onCerrar = onCerrarExito
        )
    }

    // Error al guardar la foto
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

// Foto de perfil ampliada: círculo grande, lápiz naranja para cambiarla y X para cerrar
@Composable
private fun PopupFotoAmpliada(fotoUrl: String, onEditar: () -> Unit, onCerrar: () -> Unit) {
    Dialog(onDismissRequest = onCerrar) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite, RoundedCornerShape(20.dp))
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // X para cerrar
            Box(modifier = Modifier.fillMaxWidth().padding(6.dp), contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = onCerrar, modifier = Modifier.size(36.dp)) {
                    Box(
                        modifier = Modifier.size(28.dp).background(BackgroundLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar",
                            tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Text(text = "Foto de perfil", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, color = TextBlue)
            Spacer(modifier = Modifier.height(16.dp))
            // Foto en círculo grande con el lápiz abajo a la derecha
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = fotoUrl,
                    contentDescription = "Foto de perfil ampliada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(250.dp)
                        .clip(CircleShape)
                        .background(BackgroundLight)
                        .border(4.dp, EduconnectBlue, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .padding(end = 14.dp, bottom = 14.dp)
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(AccentOrange)
                        .border(3.dp, BackgroundWhite, CircleShape)
                        .clickable { onEditar() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Cambiar foto",
                        tint = TextWhite, modifier = Modifier.size(26.dp))
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = "Toca el lápiz para cambiar tu foto", fontFamily = Roboto,
                fontSize = 15.sp, color = TextSecondary)
        }
    }
}
