package com.educonnectapp.ui.screens.docente.comunicados

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.KeyboardArrowDown
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.AdjuntoLocal
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.CampoTextoEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.HojaInferiorEdu
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.ListaAdjuntosResumenEdu
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.SelectorAdjuntosEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduCampo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class para destinatario sin Room
data class SeccionDestinatario(
    val gradoId: Long,
    val seccionId: Long,
    val cursoId: Long,
    val gradoNombre: String,
    val seccionNombre: String,
    val cursoNombre: String,
    val totalPadres: Int
)

// =====================================================================
// NUEVO COMUNICADO (rediseño 2026)
// Cabecera fija granate · Para (hoja con las clases) · asunto · mensaje · adjuntos (hasta 5)
// "Enviar comunicado" → confirmación → "Enviando..." (mientras MainActivity envía)
// =====================================================================

@Composable
fun NuevoComunicadoScreen(
    docenteId: String,
    docenteNombre: String,
    listaDestinatarios: List<SeccionDestinatario> = emptyList(),
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    // adjuntos: Uri (en texto) de cada archivo elegido, en orden
    onEnviado: (asunto: String, mensaje: String, adjuntos: List<String>, destinatario: String, curso: String, totalNotificados: Int, hora: String, fecha: String) -> Unit = { _, _, _, _, _, _, _, _ -> },
    // ----- Rediseño 2026 -----
    destinatarioInicial: SeccionDestinatario? = null,   // clase ya elegida (al crear desde una clase)
    enviando: Boolean = false                           // muestra "Enviando comunicado..."
) {
    var destinatarioSeleccionado by remember { mutableStateOf(destinatarioInicial) }
    var asunto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    // Adjuntos elegidos (hasta 5 de 10 MB cada uno)
    var adjuntos by remember { mutableStateOf<List<AdjuntoLocal>>(emptyList()) }
    var mostrarClases by remember { mutableStateOf(false) }
    var mostrarConfirmar by remember { mutableStateOf(false) }

    // Si la clase inicial llega después (la lista se carga al entrar), se toma
    LaunchedEffect(destinatarioInicial) {
        if (destinatarioSeleccionado == null && destinatarioInicial != null) destinatarioSeleccionado = destinatarioInicial
    }

    val completo = destinatarioSeleccionado != null && asunto.isNotBlank() && mensaje.isNotBlank()

    // ── POPUPS ──────────────────────────────────────────────────────────────
    if (mostrarConfirmar) {
        val dest = destinatarioSeleccionado
        PopupConfirmacionEdu(
            titulo = "¿Enviar comunicado?",
            mensaje = "Los padres recibirán una notificación al instante.",
            textoConfirmar = "Enviar",
            colorConfirmar = TipoBotonEdu.AVANZAR.fondo,
            icono = Icons.AutoMirrored.Rounded.Send,
            onCancelar = { mostrarConfirmar = false },
            onConfirmar = {
                mostrarConfirmar = false
                if (dest != null && asunto.isNotBlank() && mensaje.isNotBlank()) {
                    val fechaEnvio = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val horaEnvio = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    onEnviado(
                        asunto.trim(),
                        mensaje.trim(),
                        adjuntos.map { it.uri },
                        "${dest.gradoNombre} Sec. ${dest.seccionNombre}",
                        dest.cursoNombre,
                        dest.totalPadres,
                        horaEnvio,
                        fechaEnvio
                    )
                }
            }
        ) {
            if (dest != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EduFondo, RoundedCornerShape(18.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        asunto.trim(), fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp,
                        color = EduTexto, maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${dest.gradoNombre} ${dest.seccionNombre} · ${dest.cursoNombre}",
                        fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduTextoSecundario
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ChipEdu("${dest.totalPadres} padres", TonoEdu.VERDE)
                        if (adjuntos.isNotEmpty()) {
                            ChipEdu(if (adjuntos.size == 1) "1 adjunto" else "${adjuntos.size} adjuntos", TonoEdu.CORAL)
                        }
                    }
                    // Adjuntos con el color del módulo (granate)
                    if (adjuntos.isNotEmpty()) {
                        ListaAdjuntosResumenEdu(adjuntos.map { it.nombre }, TonoEdu.CORAL, Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }

    if (enviando) {
        PopupProcesandoEdu(
            titulo = when (adjuntos.size) {
                0 -> "Enviando comunicado..."
                1 -> "Subiendo adjunto y enviando..."
                else -> "Subiendo ${adjuntos.size} adjuntos y enviando..."
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Nuevo comunicado",
            subtitulo = "Se notifica a los padres al enviar",
            onVolver = onBack,
            colorFondo = ColorModulo.Comunicados,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Comunicados
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
            // PARA
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Para", fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = EduEtiqueta)
                CampoPara(destino = destinatarioSeleccionado, onClick = { mostrarClases = true })
            }

            // ASUNTO
            CampoTextoEdu(
                valor = asunto,
                onValorCambia = { asunto = it },
                etiqueta = "Asunto",
                placeholder = "Ej: Reunión de padres"
            )

            // MENSAJE
            CampoTextoEdu(
                valor = mensaje,
                onValorCambia = { mensaje = it },
                etiqueta = "Mensaje",
                placeholder = "Escribe el mensaje para los padres...",
                lineas = 5,
                mensajeAyuda = if (mensaje.isEmpty()) null else "${mensaje.length} caracteres"
            )

            // ADJUNTOS (varios, con el color del módulo)
            SelectorAdjuntosEdu(
                adjuntos = adjuntos,
                onCambio = { adjuntos = it },
                tono = TonoEdu.CORAL,
                habilitado = !enviando
            )
        }

        // BOTÓN FIJO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            BotonPrincipalEdu(
                texto = "Enviar comunicado",
                tipo = TipoBotonEdu.AVANZAR,
                icono = Icons.AutoMirrored.Rounded.Send,
                habilitado = completo && !enviando,
                onClick = { if (completo) mostrarConfirmar = true }
            )
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Avisos"
        )
    }

    // HOJA INFERIOR: ELEGIR LA CLASE (sube encima de la barra inferior, sin taparla)
    if (mostrarClases) {
        HojaInferiorEdu(onCerrar = { mostrarClases = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconoCirculoEdu(Icons.Rounded.Groups, TonoEdu.VERDE)
                    Column {
                        Text("¿A quién se envía?", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 20.sp, color = EduTexto)
                        Text(
                            "Llega a los padres de la clase elegida", fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp, color = EduTextoSecundario
                        )
                    }
                }
                if (listaDestinatarios.isEmpty()) {
                    Text(
                        "Aún no tienes clases asignadas. Agrégalas desde tu perfil.",
                        fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = EduTextoSecundario
                    )
                }
                listaDestinatarios
                    .sortedWith(compareBy({ it.gradoId }, { it.cursoNombre }, { it.seccionNombre }))
                    .groupBy { it.gradoNombre }
                    .forEach { (grado, lista) ->
                        Text(
                            grado.uppercase(), modifier = Modifier.padding(start = 2.dp, top = 4.dp),
                            fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.5.sp,
                            letterSpacing = 0.6.sp, color = EduTextoSecundario.copy(alpha = 0.8f)
                        )
                        lista.forEach { dest ->
                            OpcionClase(
                                destino = dest,
                                seleccionada = dest.seccionId == destinatarioSeleccionado?.seccionId &&
                                        dest.cursoId == destinatarioSeleccionado?.cursoId,
                                onClick = { destinatarioSeleccionado = dest; mostrarClases = false }
                            )
                        }
                    }
            }
        }
    }
}

// Campo "Para": muestra la clase elegida y cuántos padres recibirán el aviso; abre la hoja de clases
@Composable
private fun CampoPara(destino: SeccionDestinatario?, onClick: () -> Unit) {
    val forma = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(if (destino != null) Color.White else EduCampo)
            .border(if (destino != null) 2.dp else 1.5.dp, if (destino != null) EduTeal else EduBorde, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = if (destino != null) 10.dp else 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (destino != null) {
            IconoCirculoEdu(Icons.Rounded.Groups, TonoEdu.VERDE, tamano = 36.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${destino.gradoNombre} ${destino.seccionNombre} · ${destino.cursoNombre}",
                    fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTexto
                )
                Text(
                    "${destino.totalPadres} padres recibirán el aviso", fontFamily = Nunito,
                    fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario
                )
            }
        } else {
            Icon(Icons.Rounded.Groups, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(22.dp))
            Text(
                "Elige la clase", modifier = Modifier.weight(1f), fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = EduTextoSecundario
            )
        }
        Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "Elegir clase", tint = EduTextoSecundario, modifier = Modifier.size(24.dp))
    }
}

// Opción de la hoja de clases (con círculo de selección)
@Composable
private fun OpcionClase(destino: SeccionDestinatario, seleccionada: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(if (seleccionada) EduTealClaro else Color.White)
            .border(if (seleccionada) 2.dp else 1.5.dp, if (seleccionada) EduTeal else EduBorde, forma)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(2.dp, if (seleccionada) EduTeal else EduBorde, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (seleccionada) Box(modifier = Modifier.size(11.dp).background(EduTeal, CircleShape))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(destino.cursoNombre, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTexto)
            Text(
                "Sección ${destino.seccionNombre} · ${destino.totalPadres} padres", fontFamily = Nunito,
                fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun NuevoComunicadoPreview() {
    EduConnectAppTheme {
        NuevoComunicadoScreen(
            docenteId = "uuid-docente",
            docenteNombre = "Hector Reyna Gomez",
            listaDestinatarios = listOf(
                SeccionDestinatario(1L, 1L, 1L, "1er Grado", "A", "Matemáticas", 25),
                SeccionDestinatario(2L, 2L, 1L, "2do Grado", "B", "Matemáticas", 22)
            ),
            destinatarioInicial = SeccionDestinatario(1L, 1L, 1L, "1er Grado", "A", "Matemáticas", 25)
        )
    }
}
