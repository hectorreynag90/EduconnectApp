package com.educonnectapp.ui.screens.docente.comunicados

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BarraProgresoEdu
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.ListaAdjuntosAbrirEdu
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupExitoEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.coloresDeTono
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduAmarilloClaro
import com.educonnectapp.ui.theme.EduAmarilloOscuro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduCampo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PadreLecturaItem(
    val padreId: String,
    val nombrePadre: String,
    val nombreHijo: String,
    val leidoEn: String? = null  // null = no leyó
)

// "2026-10-19T09:52:00" -> "09:52 a. m."
private fun horaLectura(leidoEn: String?): String {
    if (leidoEn.isNullOrBlank()) return ""
    for (patron in listOf("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss")) {
        try {
            val d = SimpleDateFormat(patron, Locale.US).parse(leidoEn)
            return SimpleDateFormat("hh:mm a", Locale("es", "PE")).format(d!!)
        } catch (_: Exception) { }
    }
    return leidoEn
}

// "2026-10-19" -> "Hoy" / "14 oct"
private fun fechaCortaDetalle(fecha: String): String = try {
    if (fecha == SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) "Hoy" else {
        val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha)
        SimpleDateFormat("EEE d MMM", Locale("es", "PE")).format(d!!).replace(".", "")
    }
} catch (e: Exception) { fecha }

// =====================================================================
// DETALLE DE UN COMUNICADO (rediseño 2026)
// Cabecera fija granate · asunto, mensaje y adjunto · lectura · pestañas Leyeron / Sin leer
// "Reenviar a N sin leer" en la pestaña Sin leer
// =====================================================================

@Composable
fun DetalleComunicadoScreen(
    asunto: String = "",
    mensaje: String = "",
    fecha: String = "",
    hora: String = "",
    gradoNombre: String = "",
    seccionNombre: String = "",
    cursoNombre: String = "",
    totalNotificados: Int = 0,
    padres: List<PadreLecturaItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onReenviar: () -> Unit = {},
    // ----- Rediseño 2026 -----
    adjuntoNombre: String = "",          // "" = sin adjunto (compatibilidad: se usa si adjuntos está vacío)
    onAbrirAdjunto: () -> Unit = {},
    cargando: Boolean = false,           // se están consultando las lecturas
    // ----- Reenvío (recordatorio a los que no leyeron) -----
    reenviando: Boolean = false,         // muestra "Enviando recordatorio..."
    reenviadosA: Int? = null,            // != null: popup de éxito con la cantidad de padres
    onCerrarReenvio: () -> Unit = {},
    // ----- Varios adjuntos (paso 27) -----
    adjuntos: List<String> = emptyList(),      // nombres, en orden
    onAbrirAdjuntoEn: (Int) -> Unit = {},      // posición del adjunto tocado
    descargandoAdjunto: Boolean = false
) {
    val nombresAdjuntos = adjuntos.ifEmpty { listOf(adjuntoNombre).filter { it.isNotBlank() } }
    var tabSeleccionado by remember { mutableStateOf("Leyeron") }
    var confirmarReenvio by remember { mutableStateOf(false) }

    val padresLeidos = padres.filter { it.leidoEn != null }
    val padresSinLeer = padres.filter { it.leidoEn == null }
    val total = if (padres.isNotEmpty()) padres.size else totalNotificados
    val totalLeidos = padresLeidos.size
    val totalSinLeer = padresSinLeer.size
    val progreso = if (total > 0) totalLeidos.toFloat() / total else 0f
    val lista = if (tabSeleccionado == "Sin leer") padresSinLeer else padresLeidos

    val textoFecha = buildString {
        if (fecha.isNotBlank()) append(fechaCortaDetalle(fecha))
        if (hora.isNotBlank()) { if (isNotEmpty()) append(" · "); append(hora) }
    }
    val claseTexto = listOf(cursoNombre, "$gradoNombre $seccionNombre".trim()).filter { it.isNotBlank() }.joinToString(" · ")

    // ── POPUPS DEL REENVÍO: confirmación -> "Enviando..." -> "Recordatorio enviado" ──
    if (confirmarReenvio) {
        PopupConfirmacionEdu(
            titulo = "¿Reenviar recordatorio?",
            mensaje = "Se enviará una notificación solo a " +
                    (if (totalSinLeer == 1) "el padre que aún no lee" else "los $totalSinLeer padres que aún no leen") +
                    " este comunicado.",
            textoConfirmar = "Reenviar",
            colorConfirmar = TipoBotonEdu.AVANZAR.fondo,
            icono = Icons.AutoMirrored.Rounded.Send,
            onCancelar = { confirmarReenvio = false },
            onConfirmar = {
                confirmarReenvio = false
                onReenviar()
            }
        )
    }
    if (reenviando) PopupProcesandoEdu(titulo = "Enviando recordatorio...")
    reenviadosA?.let { n ->
        PopupExitoEdu(
            titulo = "Recordatorio enviado",
            mensaje = (if (n == 1) "Se avisó a 1 padre." else "Se avisó a $n padres.") +
                    " Verás en la pestaña Leyeron cuando lo abran.",
            onCerrar = onCerrarReenvio
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Detalle",
            subtitulo = claseTexto.ifBlank { "Comunicado" },
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

        // CONTENIDO DESPLAZABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // COMUNICADO
            TarjetaEdu(relleno = PaddingValues(horizontal = 16.dp, vertical = 14.dp)) {
                Text(asunto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp, color = EduTexto)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (textoFecha.isNotBlank()) ChipEdu(textoFecha, TonoEdu.AZUL)
                    ChipEdu(if (total == 1) "1 padre" else "$total padres", TonoEdu.AZUL)
                }
                if (mensaje.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        mensaje,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EduCampo, RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                        lineHeight = 19.sp, color = EduEtiqueta
                    )
                }
                if (nombresAdjuntos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        if (nombresAdjuntos.size == 1) "ADJUNTO" else "ADJUNTOS · ${nombresAdjuntos.size}",
                        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.5.sp,
                        letterSpacing = 0.6.sp, color = EduTextoSecundario
                    )
                    ListaAdjuntosAbrirEdu(
                        nombres = nombresAdjuntos,
                        tono = TonoEdu.CORAL,
                        descargando = descargandoAdjunto,
                        onAbrir = { i -> if (adjuntos.isEmpty()) onAbrirAdjunto() else onAbrirAdjuntoEn(i) }
                    )
                }
            }

            // LECTURA
            TarjetaEdu(relleno = PaddingValues(horizontal = 16.dp, vertical = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Leído por $totalLeidos de $total padres", modifier = Modifier.weight(1f),
                        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTexto
                    )
                    Text(
                        "${(progreso * 100).toInt()}%", fontFamily = Nunito, fontWeight = FontWeight.Black,
                        fontSize = 18.sp, color = EduTealOscuro
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                BarraProgresoEdu(progreso = progreso, color = EduTeal)
            }

            // PESTAÑAS
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PestanaDetalle("Leyeron ($totalLeidos)", tabSeleccionado == "Leyeron") { tabSeleccionado = "Leyeron" }
                PestanaDetalle("Sin leer ($totalSinLeer)", tabSeleccionado == "Sin leer") { tabSeleccionado = "Sin leer" }
            }

            // LISTA DE PADRES
            when {
                cargando && padres.isEmpty() -> TarjetaEdu {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = EduTeal, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                        Text(
                            "Cargando lectura...", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                            fontSize = 15.sp, color = EduTextoSecundario
                        )
                    }
                }
                lista.isEmpty() -> TarjetaEdu {
                    Text(
                        if (tabSeleccionado == "Sin leer") "¡Todos los padres ya lo leyeron!"
                        else "Aún ningún padre lo ha leído.",
                        fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp,
                        color = EduTextoSecundario
                    )
                }
                else -> TarjetaEdu(relleno = PaddingValues(horizontal = 12.dp, vertical = 2.dp)) {
                    lista.forEachIndexed { i, padre ->
                        FilaPadreLectura(padre = padre, ultima = i == lista.lastIndex)
                    }
                }
            }

            // AVISO DE REENVÍO
            if (tabSeleccionado == "Sin leer" && totalSinLeer > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EduAmarilloClaro, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = EduAmarilloOscuro, modifier = Modifier.size(22.dp))
                    Text(
                        "Reenviar manda otra notificación solo a los padres que aún no lo leen.",
                        modifier = Modifier.weight(1f),
                        fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                        lineHeight = 18.sp, color = EduAmarilloOscuro
                    )
                }
            }
        }

        // BOTÓN FIJO: REENVIAR (solo en la pestaña Sin leer)
        if (tabSeleccionado == "Sin leer" && totalSinLeer > 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EduFondo)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                BotonPrincipalEdu(
                    texto = "Reenviar a $totalSinLeer sin leer",
                    tipo = TipoBotonEdu.AVANZAR,
                    icono = Icons.AutoMirrored.Rounded.Send,
                    habilitado = !reenviando,
                    onClick = { confirmarReenvio = true }
                )
            }
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Avisos"
        )
    }
}

// Pestaña tipo pastilla: granate sólido cuando está activa
@Composable
private fun PestanaDetalle(texto: String, activa: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(50)
    Text(
        texto,
        modifier = Modifier
            .clip(forma)
            .background(if (activa) EduCoral else Color.White)
            .border(1.5.dp, if (activa) EduCoral else EduBorde, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.5.sp,
        color = if (activa) Color.White else EduEtiqueta
    )
}

// Fila de un padre: iniciales, nombre, "Padre de …" y hora de lectura o "Sin leer"
@Composable
private fun FilaPadreLectura(padre: PadreLecturaItem, ultima: Boolean) {
    val leyo = padre.leidoEn != null
    val c = coloresDeTono(if (leyo) TonoEdu.VERDE else TonoEdu.CORAL)
    val iniciales = padre.nombrePadre.split(" ").filter { it.isNotBlank() }.take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(38.dp).background(c.fondo, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(iniciales, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.sp, color = c.texto)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    padre.nombrePadre, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.5.sp,
                    color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                if (padre.nombreHijo.isNotBlank()) {
                    Text(
                        "Padre de ${padre.nombreHijo}", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp, color = EduTextoSecundario, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (leyo) ChipEdu(horaLectura(padre.leidoEn).ifBlank { "Leído" }, TonoEdu.VERDE, icono = Icons.Rounded.Visibility)
            else ChipEdu("Sin leer", TonoEdu.CORAL)
        }
        if (!ultima) Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EduFondo))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DetalleComunicadoPreview() {
    EduConnectAppTheme {
        DetalleComunicadoScreen(
            asunto = "Reunión de padres · viernes 23",
            mensaje = "Estimados padres de familia, los invitamos a la reunión del viernes 23 a las 4:00 p. m. en el aula de 1er Grado A.",
            fecha = "2026-10-19",
            hora = "10:24 a. m.",
            gradoNombre = "1er Grado",
            seccionNombre = "A",
            cursoNombre = "Ciencia y Tecnología",
            totalNotificados = 5,
            adjuntos = listOf("agenda_reunion.pdf", "foto_pizarra.jpg"),
            padres = listOf(
                PadreLecturaItem("1", "Luis Pérez", "Carlos", "2026-10-19T09:52:00"),
                PadreLecturaItem("2", "Carla Castillo", "María", "2026-10-19T10:05:00"),
                PadreLecturaItem("3", "Jesús Viera", "César", "2026-10-19T10:31:00"),
                PadreLecturaItem("4", "Ana Torres", "Pedro", null),
                PadreLecturaItem("5", "Rosa Medina", "Luis", null)
            ),
        )
    }
}
