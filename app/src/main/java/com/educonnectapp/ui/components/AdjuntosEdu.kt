package com.educonnectapp.ui.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.util.Locale

// =====================================================================
// ADJUNTOS DE EDUCONNECT (varios por comunicado o tarea · paso 27)
// - SelectorAdjuntosEdu: formulario (Archivo / Foto, hasta 5 de 10 MB c/u), con el color del módulo
// - ListaAdjuntosResumenEdu: lista corta (confirmación y pantalla de enviado)
// - ListaAdjuntosAbrirEdu: lista con "Abrir" (detalles del docente y del padre)
// El tono define el color: CORAL = Comunicados (granate), AMARILLO = Publicaciones / Agenda
// =====================================================================

const val MAXIMO_ADJUNTOS_EDU = 5                          // el mismo límite que valida el backend
private const val TAMANO_MAXIMO_ADJUNTO = 10L * 1024 * 1024  // 10 MB por archivo

// Archivo elegido en el celular (aún no subido). uri = Uri en texto.
data class AdjuntoLocal(
    val uri: String,
    val nombre: String,
    val tamano: Long?,
    val esImagen: Boolean
)

// "1.2 MB" / "850 KB"
fun tamanoLegibleEdu(bytes: Long?): String = when {
    bytes == null -> ""
    bytes >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
    else -> "${(bytes / 1024).coerceAtLeast(1)} KB"
}

// ¿El nombre del archivo es de una imagen? (para elegir el ícono)
fun esNombreImagenEdu(nombre: String): Boolean =
    nombre.substringAfterLast('.', "").lowercase() in setOf("jpg", "jpeg", "png", "webp", "gif", "heic")

private fun iconoAdjunto(esImagen: Boolean): ImageVector =
    if (esImagen) Icons.Rounded.Image else Icons.Rounded.Description

// Nombre visible, tamaño y tipo de un archivo elegido, sin leer su contenido
private fun leerAdjunto(context: Context, uri: Uri): AdjuntoLocal {
    var nombre: String? = null
    var tamano: Long? = null
    context.contentResolver.query(
        uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null
    )?.use { c ->
        if (c.moveToFirst()) {
            nombre = c.getString(0)
            if (!c.isNull(1)) tamano = c.getLong(1)
        }
    }
    val nombreFinal = nombre ?: uri.lastPathSegment ?: "archivo"
    val mime = context.contentResolver.getType(uri).orEmpty()
    return AdjuntoLocal(
        uri = uri.toString(),
        nombre = nombreFinal,
        tamano = tamano,
        esImagen = mime.startsWith("image/") || esNombreImagenEdu(nombreFinal)
    )
}

// ---------------------------------------------------------------------
// 1. SELECTOR DEL FORMULARIO
// Se pueden elegir varios a la vez. Los que superan 10 MB o exceden el máximo
// no se agregan y se avisa con un popup. Cada adjunto se quita con su X.
// ---------------------------------------------------------------------
@Composable
fun SelectorAdjuntosEdu(
    adjuntos: List<AdjuntoLocal>,
    onCambio: (List<AdjuntoLocal>) -> Unit,
    tono: TonoEdu,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    val context = LocalContext.current
    val c = coloresDeTono(tono)
    var rechazados by remember { mutableStateOf<List<String>>(emptyList()) }
    val lleno = adjuntos.size >= MAXIMO_ADJUNTOS_EDU

    fun agregar(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val nuevos = mutableListOf<AdjuntoLocal>()
        val avisos = mutableListOf<String>()
        uris.map { leerAdjunto(context, it) }.forEach { a ->
            val tam = a.tamano
            when {
                adjuntos.any { it.uri == a.uri } || nuevos.any { it.uri == a.uri } -> Unit   // ya estaba
                tam != null && tam > TAMANO_MAXIMO_ADJUNTO ->
                    avisos += "${a.nombre} (${tamanoLegibleEdu(tam)}): supera 10 MB"
                adjuntos.size + nuevos.size >= MAXIMO_ADJUNTOS_EDU ->
                    avisos += "${a.nombre}: ya tienes $MAXIMO_ADJUNTOS_EDU adjuntos"
                else -> nuevos += a
            }
        }
        if (nuevos.isNotEmpty()) onCambio(adjuntos + nuevos)
        rechazados = avisos
    }

    val launcherArchivos = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { agregar(it) }

    if (rechazados.isNotEmpty()) {
        PopupAlertaEdu(
            icono = Icons.Rounded.AttachFile,
            color = EduAmarillo,
            titulo = if (rechazados.size == 1) "No se adjuntó 1 archivo" else "No se adjuntaron ${rechazados.size} archivos",
            mensaje = rechazados.joinToString("\n") { "• $it" } +
                    "\n\nMáximo $MAXIMO_ADJUNTOS_EDU adjuntos de 10 MB cada uno.",
            onCerrar = { rechazados = emptyList() }
        )
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Adjuntos (opcional · máx. $MAXIMO_ADJUNTOS_EDU · 10 MB c/u)", modifier = Modifier.weight(1f),
                fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = EduEtiqueta
            )
            if (adjuntos.isNotEmpty()) ChipEdu("${adjuntos.size} de $MAXIMO_ADJUNTOS_EDU", tono)
        }

        // Un adjunto por tarjeta, con el color del módulo
        adjuntos.forEach { a ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(c.fondo, RoundedCornerShape(16.dp))
                    .border(2.dp, c.solido, RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier.size(38.dp).background(c.texto, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(iconoAdjunto(a.esImagen), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        a.nombre, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.5.sp,
                        color = c.texto, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    if (a.tamano != null) {
                        Text(
                            tamanoLegibleEdu(a.tamano), fontFamily = Nunito, fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp, color = c.texto.copy(alpha = 0.8f)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(Color.White)
                        .clickable(enabled = habilitado) { onCambio(adjuntos.filterNot { it.uri == a.uri }) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Quitar adjunto", tint = c.texto, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Archivo / Foto (grises al llegar al máximo)
        val activo = habilitado && !lleno
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BotonAgregarAdjunto(Icons.Rounded.AttachFile, "Archivo", c.texto, c.solido, activo, Modifier.weight(1f)) {
                launcherArchivos.launch("*/*")
            }
            BotonAgregarAdjunto(Icons.Rounded.Image, "Foto", c.texto, c.solido, activo, Modifier.weight(1f)) {
                launcherArchivos.launch("image/*")
            }
        }
        if (lleno) {
            Text(
                "Llegaste al máximo de $MAXIMO_ADJUNTOS_EDU adjuntos. Quita uno para agregar otro.",
                fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
            )
        }
    }
}

@Composable
private fun BotonAgregarAdjunto(
    icono: ImageVector,
    texto: String,
    color: Color,
    borde: Color,
    activo: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(14.dp)
    val tinte = if (activo) color else EduTextoSecundario.copy(alpha = 0.5f)
    Row(
        modifier = modifier
            .clip(forma)
            .background(Color.White)
            .border(1.5.dp, if (activo) borde else EduBorde, forma)
            .clickable(enabled = activo, onClick = onClick)
            .padding(vertical = 11.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = tinte, modifier = Modifier.size(19.dp))
        Spacer(modifier = Modifier.width(7.dp))
        Text(texto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp, color = tinte)
    }
}

// ---------------------------------------------------------------------
// 2. LISTA CORTA (confirmación "¿Enviar…?" y pantalla de enviado)
// Ícono y nombre en el color del módulo
// ---------------------------------------------------------------------
@Composable
fun ListaAdjuntosResumenEdu(nombres: List<String>, tono: TonoEdu, modifier: Modifier = Modifier) {
    val c = coloresDeTono(tono)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        nombres.forEach { nombre ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(iconoAdjunto(esNombreImagenEdu(nombre)), contentDescription = null, tint = c.texto, modifier = Modifier.size(19.dp))
                Text(
                    nombre, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp,
                    color = c.texto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ---------------------------------------------------------------------
// 3. LISTA CON "ABRIR" (detalle del comunicado, de la tarea y de la agenda)
// Va dentro de una TarjetaEdu. onAbrir recibe la posición del adjunto.
// ---------------------------------------------------------------------
@Composable
fun ListaAdjuntosAbrirEdu(
    nombres: List<String>,
    tono: TonoEdu,
    descargando: Boolean,
    onAbrir: (Int) -> Unit
) {
    nombres.forEachIndexed { i, nombre ->
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = !descargando) { onAbrir(i) }
                    .padding(vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconoCirculoEdu(iconoAdjunto(esNombreImagenEdu(nombre)), tono, tamano = 40.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        nombre, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.5.sp,
                        color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        if (descargando) "Descargando…" else "Toca para abrir",
                        fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario
                    )
                }
                if (descargando) {
                    CircularProgressIndicator(color = coloresDeTono(tono).texto, strokeWidth = 3.dp, modifier = Modifier.size(22.dp))
                } else {
                    Row(
                        modifier = Modifier
                            .height(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(2.dp, EduBorde, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Rounded.Visibility, contentDescription = null, tint = EduEtiqueta, modifier = Modifier.size(16.dp))
                        Text("Abrir", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp, color = EduEtiqueta)
                    }
                }
            }
            if (i < nombres.lastIndex) Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EduFondo))
        }
    }
}
