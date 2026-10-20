package com.educonnectapp.ui.screens.padre.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.coloresDeTono
import com.educonnectapp.ui.screens.padre.inicio.BottomNavBarPadre
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// =====================================================================
// AGENDA DEL PADRE · DETALLE (rediseño 2026)
// Cabecera fija amarilla · franja con el estado · tarjeta con curso, título, docente y
// descripción (o temario) · adjunto con "Abrir" · nota según el tipo
// =====================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetalleAgendaScreen(
    nombreHijo: String = "",
    gradoNombre: String = "",
    seccionNombre: String = "",
    publicacion: PublicacionAgendaItem? = null,
    descargandoAdjunto: Boolean = false,
    onAbrirAdjunto: () -> Unit = {},
    onBack: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    // ----- Rediseño 2026 -----
    apellidosHijo: String = ""
) {
    val esEvaluacion = publicacion?.tipo == "Evaluación"
    val primerNombre = nombreHijo.trim().split(" ").firstOrNull().orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = if (esEvaluacion) "Evaluación" else "Tarea",
            subtitulo = listOf("$nombreHijo $apellidosHijo".trim(), "$gradoNombre $seccionNombre".trim())
                .filter { it.isNotBlank() }.joinToString(" · "),
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

        // CONTENIDO DESPLAZABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (publicacion == null) {
                TarjetaEdu {
                    Text(
                        "No se encontró la publicación.", fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = EduTextoSecundario
                    )
                }
            } else {
                // ---------- FRANJA DE ESTADO ----------
                FranjaEstado(publicacion, esEvaluacion)

                // ---------- DETALLE ----------
                TarjetaEdu(relleno = PaddingValues(16.dp)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ChipEdu(publicacion.cursoNombre, if (esEvaluacion) TonoEdu.AZUL else TonoEdu.AMARILLO)
                        ChipEdu(if (esEvaluacion) "Aviso de evaluación" else "Tarea", TonoEdu.GRIS)
                    }
                    Text(
                        publicacion.titulo, modifier = Modifier.padding(top = 10.dp), fontFamily = Nunito,
                        fontWeight = FontWeight.Black, fontSize = 20.sp, lineHeight = 25.sp, color = EduTexto
                    )
                    // Docente y fecha de publicación
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).background(EduAmarillo, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                inicialesDocente(publicacion.docenteNombre), fontFamily = Nunito,
                                fontWeight = FontWeight.Black, fontSize = 14.sp, color = EduTexto
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                publicacion.docenteNombre.ifBlank { "Docente" }, fontFamily = Nunito,
                                fontWeight = FontWeight.Black, fontSize = 14.5.sp, color = EduTexto,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                if (publicacion.publicadoEn.isNotBlank())
                                    "Publicado el " + fechaLargaAgenda(publicacion.publicadoEn).replaceFirstChar { it.lowercase() }
                                else if (esEvaluacion) "Fecha: ${fechaLargaAgenda(publicacion.fechaEntrega)}"
                                else "Fecha límite: ${fechaLargaAgenda(publicacion.fechaEntrega)}",
                                fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario
                            )
                        }
                    }
                    if (publicacion.descripcion.isNotBlank()) {
                        Box(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth().height(1.dp).background(EduFondo))
                        Text(
                            if (esEvaluacion) "TEMARIO" else "DESCRIPCIÓN", fontFamily = Nunito,
                            fontWeight = FontWeight.Black, fontSize = 12.5.sp, letterSpacing = 0.6.sp,
                            color = EduTextoSecundario, modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            publicacion.descripcion, fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                            fontSize = 14.5.sp, lineHeight = 22.sp, color = EduTexto
                        )
                    }
                }

                // ---------- ADJUNTO ----------
                if (publicacion.archivoAdjunto.isNotBlank()) {
                    TituloSeccionEdu("Adjunto")
                    TarjetaEdu(
                        relleno = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        onClick = if (descargandoAdjunto) null else onAbrirAdjunto
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconoCirculoEdu(Icons.Rounded.Description, TonoEdu.AZUL)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    publicacion.archivoAdjunto.substringAfterLast("/"), fontFamily = Nunito,
                                    fontWeight = FontWeight.Black, fontSize = 14.5.sp, color = EduTexto,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    if (descargandoAdjunto) "Descargando…" else "Toca para abrir",
                                    fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario
                                )
                            }
                            if (descargandoAdjunto) {
                                CircularProgressIndicator(color = EduAzulOscuro, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                            } else {
                                // Botón secundario "Abrir"
                                Row(
                                    modifier = Modifier
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(Color.White)
                                        .border(2.dp, EduBorde, RoundedCornerShape(13.dp))
                                        .clickable(onClick = onAbrirAdjunto)
                                        .padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Rounded.Visibility, contentDescription = null, tint = EduEtiqueta, modifier = Modifier.size(16.dp))
                                    Text("Abrir", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp, color = EduEtiqueta)
                                }
                            }
                        }
                        if (descargandoAdjunto) {
                            LinearProgressIndicator(
                                modifier = Modifier.padding(top = 10.dp).fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                                color = EduAzulOscuro,
                                trackColor = EduAzulOscuroClaro
                            )
                        }
                    }
                }

                // ---------- NOTA ----------
                when {
                    esEvaluacion && publicacion.estado == "Pendiente" -> Nota(
                        "Ayude a $primerNombre a repasar el temario. Es solo un aviso: la evaluación se toma en clase.",
                        Icons.Rounded.Lightbulb, TonoEdu.AMARILLO
                    )
                    !esEvaluacion && publicacion.estado == "Pendiente" -> Nota(
                        "El docente registrará si $primerNombre entregó la tarea; te llegará una notificación.",
                        Icons.Rounded.Info, TonoEdu.AZUL
                    )
                    publicacion.estado == "Vencida" -> Nota(
                        "El plazo ya pasó y el docente aún no registra la entrega. Si tienes dudas, comunícate con el docente.",
                        Icons.Rounded.Info, TonoEdu.AZUL
                    )
                }
            }
        }

        BottomNavBarPadre(
            onInicio = onHomePadre,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Agenda"
        )
    }
}

// Contenido de la franja de estado
private data class Franja(val icono: ImageVector, val tono: TonoEdu, val titulo: String, val detalle: String)

// Franja superior con el estado de la tarea o evaluación
@Composable
private fun FranjaEstado(p: PublicacionAgendaItem, esEvaluacion: Boolean) {
    val dias = diasHastaAgenda(p.fechaEntrega)
    val fecha = fechaLargaAgenda(p.fechaEntrega)
    val f = when (p.estado) {
        "Pendiente" -> if (esEvaluacion) {
            when {
                dias == null -> Franja(Icons.Rounded.Event, TonoEdu.AZUL, "Evaluación programada", "Sin fecha")
                dias <= 0L -> Franja(Icons.Rounded.Event, TonoEdu.CORAL, "Evaluación hoy", fecha)
                dias == 1L -> Franja(Icons.Rounded.Event, TonoEdu.CORAL, "Evaluación mañana", fecha)
                else -> Franja(Icons.Rounded.Event, TonoEdu.AZUL, "Evaluación en $dias días", fecha)
            }
        } else {
            when {
                dias == null -> Franja(Icons.Rounded.Alarm, TonoEdu.AMARILLO, "Tarea pendiente", "Sin fecha límite")
                dias <= 0L -> Franja(Icons.Rounded.Alarm, TonoEdu.CORAL, "Vence hoy", fecha)
                dias == 1L -> Franja(Icons.Rounded.Alarm, TonoEdu.CORAL, "Vence mañana", fecha)
                else -> Franja(Icons.Rounded.Alarm, TonoEdu.AMARILLO, "Vence en $dias días", fecha)
            }
        }
        "Entregada" -> Franja(Icons.Rounded.Check, TonoEdu.VERDE, "Tarea entregada", "Registrado por el docente · fecha límite ${fechaCortaAgenda(p.fechaEntrega).replaceFirstChar { it.lowercase() }}")
        "No entregada" -> Franja(Icons.Rounded.Close, TonoEdu.CORAL, "Tarea no entregada", "Registrado por el docente · fecha límite ${fechaCortaAgenda(p.fechaEntrega).replaceFirstChar { it.lowercase() }}")
        "Realizado" -> Franja(Icons.Rounded.EventAvailable, TonoEdu.GRIS, "Evaluación realizada", fecha)
        else -> Franja(Icons.Rounded.Warning, TonoEdu.AMARILLO, "Plazo vencido", "$fecha · sin registro del docente")
    }
    val c = coloresDeTono(f.tono)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(c.fondo)
            .border(1.5.dp, c.solido, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(44.dp).background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(f.icono, contentDescription = null, tint = c.texto, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(f.titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.sp, color = c.texto)
            Text(f.detalle, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduEtiqueta)
        }
    }
}

@Composable
private fun Nota(texto: String, icono: ImageVector, tono: TonoEdu) {
    val c = coloresDeTono(tono)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(c.fondo, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icono, contentDescription = null, tint = c.texto, modifier = Modifier.size(21.dp))
        Text(
            texto, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp, lineHeight = 18.sp, color = EduEtiqueta
        )
    }
}

// "Prof. Hector Reyna" -> "HR"
private fun inicialesDocente(nombre: String): String =
    nombre.split(" ").filter { it.isNotBlank() && !it.endsWith(".") }.take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("").ifBlank { "D" }

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DetalleAgendaPreview() {
    val hoy = java.time.LocalDate.now()
    EduConnectAppTheme {
        DetalleAgendaScreen(
            nombreHijo = "Angela",
            apellidosHijo = "Perez",
            gradoNombre = "2do Grado",
            seccionNombre = "A",
            publicacion = PublicacionAgendaItem(
                id = 1L,
                titulo = "Maqueta de ecosistemas",
                descripcion = "Elaborar una maqueta de un ecosistema de la costa con material reciclado. Incluir un cartel con 3 seres vivos.",
                tipo = "Tarea",
                cursoNombre = "Ciencia y Tecnología",
                docenteNombre = "Prof. Hector Reyna",
                fechaEntrega = hoy.plusDays(1).toString(),
                estado = "Pendiente",
                archivoAdjunto = "guia_maqueta.pdf",
                publicadoEn = hoy.minusDays(4).toString()
            )
        )
    }
}
