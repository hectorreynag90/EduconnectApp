package com.educonnectapp.ui.screens.docente.publicaciones

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.docente.comunicados.SeccionDestinatario
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduCoralClaro
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Límite del adjunto (el mismo que valida el backend)
private const val MAXIMO_ADJUNTO_BYTES = 10L * 1024 * 1024   // 10 MB

// =====================================================================
// NUEVA TAREA (rediseño 2026)
// Cabecera fija amarilla · Para · título · descripción · fecha límite · adjunto
// "Publicar tarea" → confirmación → "Publicando..." (mientras MainActivity publica)
// =====================================================================

@Composable
fun NuevaTareaScreen(
    listaDestinatarios: List<SeccionDestinatario> = emptyList(),
    cursoNombre: String = "",          // ya no se usa (se conserva por compatibilidad)
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onPublicar: (
        titulo: String,
        descripcion: String,
        adjunto: String,
        seccionDestinatario: SeccionDestinatario,
        fechaEntrega: String,
        fechaPublicacion: String,
        hora: String
    ) -> Unit = { _, _, _, _, _, _, _ -> },
    // ----- Rediseño 2026 -----
    destinatarioInicial: SeccionDestinatario? = null,   // clase ya elegida
    publicando: Boolean = false                         // muestra "Publicando..."
) {
    val context = LocalContext.current
    var destinatario by remember { mutableStateOf(destinatarioInicial ?: listaDestinatarios.singleOrNull()) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaEntrega by remember { mutableStateOf("") }
    var archivoUri by remember { mutableStateOf<Uri?>(null) }
    var nombreArchivo by remember { mutableStateOf("") }
    var tamanoArchivo by remember { mutableStateOf<Long?>(null) }
    var archivoRechazado by remember { mutableStateOf<Pair<String, Long>?>(null) }
    var mostrarClases by remember { mutableStateOf(false) }
    var mostrarConfirmar by remember { mutableStateOf(false) }

    // Si la clase inicial llega después (la lista se carga al entrar), se toma
    LaunchedEffect(destinatarioInicial) {
        if (destinatario == null && destinatarioInicial != null) destinatario = destinatarioInicial
    }

    val archivoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Nombre visible y tamaño en bytes, sin leer el archivo completo
            var nombre: String? = null
            var tamano: Long? = null
            context.contentResolver.query(
                it, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null
            )?.use { c ->
                if (c.moveToFirst()) {
                    nombre = c.getString(0)
                    if (!c.isNull(1)) tamano = c.getLong(1)
                }
            }
            val nombreFinal = nombre ?: it.lastPathSegment ?: "archivo"
            val tamanoFinal = tamano
            if (tamanoFinal != null && tamanoFinal > MAXIMO_ADJUNTO_BYTES) {
                archivoRechazado = nombreFinal to tamanoFinal   // no se adjunta: se avisa
            } else {
                archivoUri = it
                nombreArchivo = nombreFinal
                tamanoArchivo = tamanoFinal
            }
        }
    }

    val completo = destinatario != null && titulo.isNotBlank() && descripcion.isNotBlank() && fechaEntrega.isNotBlank()

    // ── POPUPS ──────────────────────────────────────────────────────────────
    archivoRechazado?.let { (nombre, tamano) ->
        PopupConfirmacionEdu(
            titulo = "Archivo demasiado grande",
            mensaje = "\"$nombre\" pesa ${tamanoLegiblePub(tamano)}. El tamaño máximo permitido es 10 MB. " +
                    "Elige un archivo más liviano o comprímelo antes de adjuntarlo.",
            textoConfirmar = "Elegir otro",
            colorConfirmar = TipoBotonEdu.AVANZAR.fondo,
            icono = Icons.Rounded.AttachFile,
            onCancelar = { archivoRechazado = null },
            onConfirmar = { archivoRechazado = null; archivoLauncher.launch("*/*") }
        )
    }

    if (mostrarConfirmar) {
        val dest = destinatario
        PopupConfirmacionEdu(
            titulo = "¿Publicar tarea?",
            mensaje = "Los padres recibirán una notificación al instante.",
            textoConfirmar = "Publicar",
            colorConfirmar = TipoBotonEdu.AVANZAR.fondo,
            icono = Icons.AutoMirrored.Rounded.Send,
            onCancelar = { mostrarConfirmar = false },
            onConfirmar = {
                mostrarConfirmar = false
                if (dest != null && completo) {
                    val fechaPub = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val hora = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    onPublicar(titulo.trim(), descripcion.trim(), archivoUri?.toString() ?: "", dest, fechaEntrega, fechaPub, hora)
                }
            }
        ) {
            if (dest != null) {
                ResumenPublicacion(
                    titulo = titulo.trim(),
                    clase = "${dest.gradoNombre} ${dest.seccionNombre} · ${dest.cursoNombre}",
                    fecha = "Entrega: ${fechaLargaPub(fechaEntrega)}",
                    alumnos = dest.totalPadres,
                    conAdjunto = nombreArchivo.isNotEmpty()
                )
            }
        }
    }

    if (publicando) {
        PopupProcesandoEdu(
            titulo = if (nombreArchivo.isNotEmpty()) "Subiendo adjunto y publicando..." else "Publicando tarea..."
        )
    }

    if (mostrarClases) {
        HojaElegirClasePub(
            clases = listaDestinatarios,
            seleccionada = destinatario,
            onElegir = { destinatario = it; mostrarClases = false },
            onCerrar = { mostrarClases = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Nueva tarea",
            subtitulo = "Los padres reciben un aviso al publicar",
            onVolver = onBack,
            colorFondo = ColorModulo.Publicaciones,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Publicaciones
                )
            }
        )

        // FORMULARIO DESPLAZABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CampoParaPub(destino = destinatario, onClick = { mostrarClases = true })
            CampoTextoPub(
                valor = titulo, onValorCambia = { titulo = it },
                etiqueta = "Título", placeholder = "Ej: Ejercicios cap. 5 · Fracciones"
            )
            CampoTextoPub(
                valor = descripcion, onValorCambia = { descripcion = it },
                etiqueta = "Descripción", placeholder = "Qué deben hacer y cómo entregarlo...", lineas = 3
            )
            CampoFechaPub(etiqueta = "Fecha límite", fecha = fechaEntrega, onFecha = { fechaEntrega = it })

            // ADJUNTO
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EtiquetaCampoPub("Adjunto (opcional · máx. 10 MB)")
                if (nombreArchivo.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(2.dp, EduTeal, RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconoCirculoEdu(Icons.Rounded.Description, TonoEdu.AZUL, tamano = 38.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                nombreArchivo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.5.sp,
                                color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            if (tamanoArchivo != null) {
                                Text(
                                    tamanoLegiblePub(tamanoArchivo), fontFamily = Nunito, fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp, color = EduTextoSecundario
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(EduCoralClaro)
                                .clickable { archivoUri = null; nombreArchivo = ""; tamanoArchivo = null },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Close, contentDescription = "Quitar adjunto", tint = EduCoral, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BotonAdjuntarPub(Icons.Rounded.AttachFile, "Archivo", Modifier.weight(1f)) { archivoLauncher.launch("*/*") }
                    BotonAdjuntarPub(Icons.Rounded.Image, "Foto", Modifier.weight(1f)) { archivoLauncher.launch("image/*") }
                }
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
                texto = "Publicar tarea",
                tipo = TipoBotonEdu.AVANZAR,
                icono = Icons.AutoMirrored.Rounded.Send,
                habilitado = completo && !publicando,
                onClick = { if (completo) mostrarConfirmar = true }
            )
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Inicio"
        )
    }
}

// Resumen dentro de la confirmación (título, clase, fecha y chips)
@Composable
internal fun ResumenPublicacion(titulo: String, clase: String, fecha: String, alumnos: Int, conAdjunto: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(EduFondo, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTexto, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(clase, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduTextoSecundario)
        Text(fecha, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduEtiqueta)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChipEdu("$alumnos alumnos", TonoEdu.VERDE)
            if (conAdjunto) ChipEdu("1 adjunto", TonoEdu.AZUL)
        }
    }
}

// Botón para adjuntar (Archivo / Foto)
@Composable
private fun BotonAdjuntarPub(icono: ImageVector, texto: String, modifier: Modifier, onClick: () -> Unit) {
    val forma = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .clip(forma)
            .background(Color.White)
            .border(1.5.dp, EduAzulOscuroClaro, forma)
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = EduEtiqueta, modifier = Modifier.size(19.dp))
        Spacer(modifier = Modifier.size(7.dp))
        Text(texto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp, color = EduEtiqueta)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun NuevaTareaPreview() {
    EduConnectAppTheme {
        NuevaTareaScreen(
            listaDestinatarios = listOf(
                SeccionDestinatario(1L, 1L, 1L, "1er Grado", "A", "Ciencia y Tecnología", 5),
                SeccionDestinatario(2L, 2L, 1L, "2do Grado", "B", "Matemáticas", 24)
            ),
            destinatarioInicial = SeccionDestinatario(1L, 1L, 1L, "1er Grado", "A", "Ciencia y Tecnología", 5)
        )
    }
}
