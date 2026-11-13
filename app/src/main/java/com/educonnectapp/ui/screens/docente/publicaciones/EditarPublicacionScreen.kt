package com.educonnectapp.ui.screens.docente.publicaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Save
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.esNombreImagenEdu
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduAmarilloClaro
import com.educonnectapp.ui.theme.EduAmarilloOscuro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduCampo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// =====================================================================
// EDITAR TAREA / AVISO DE EVALUACIÓN (rediseño 2026)
// Se abre con el botón "Editar" del detalle (CalificarPublicacionScreen).
// Se cambian título, descripción (temario) y fecha. La clase y el adjunto quedan bloqueados.
// "Guardar cambios" → confirmación → "Guardando..." (mientras MainActivity guarda)
// Al guardar, el backend avisa a los padres (con la nueva fecha si cambió).
// =====================================================================

@Composable
fun EditarPublicacionScreen(
    publicacion: DetallePublicacionItem? = null,
    guardando: Boolean = false,
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    // Solo se llama si hay cambios y ya se confirmó. fecha = "yyyy-MM-dd"
    onGuardar: (titulo: String, descripcion: String, fecha: String) -> Unit = { _, _, _ -> }
) {
    val esEvaluacion = publicacion?.tipo == "Evaluación"
    val nombreTipo = if (esEvaluacion) "evaluación" else "tarea"
    val fechaOriginal = publicacion?.fechaEntrega.orEmpty()

    var titulo by remember { mutableStateOf(publicacion?.titulo.orEmpty()) }
    var descripcion by remember { mutableStateOf(publicacion?.descripcion.orEmpty()) }
    var fecha by remember { mutableStateOf(fechaOriginal) }
    var mostrarConfirmar by remember { mutableStateOf(false) }

    // Si los datos llegan después de abrir la pantalla, se cargan en el formulario
    LaunchedEffect(publicacion) {
        if (publicacion != null && titulo.isEmpty() && descripcion.isEmpty()) {
            titulo = publicacion.titulo
            descripcion = publicacion.descripcion
            fecha = publicacion.fechaEntrega
        }
    }

    val cambioTitulo = publicacion != null && titulo.trim() != publicacion.titulo
    val cambioDescripcion = publicacion != null && descripcion.trim() != publicacion.descripcion
    val cambioFecha = publicacion != null && fecha != fechaOriginal
    val hayCambios = cambioTitulo || cambioDescripcion || cambioFecha
    val completo = titulo.isNotBlank() && descripcion.isNotBlank() && fecha.isNotBlank()

    // ── POPUPS ──────────────────────────────────────────────────────────────
    if (mostrarConfirmar && publicacion != null) {
        PopupConfirmacionEdu(
            titulo = "¿Guardar cambios?",
            mensaje = "Se actualizará la $nombreTipo y se avisará a los padres de " +
                    "${publicacion.gradoNombre} ${publicacion.seccionNombre}.",
            textoConfirmar = "Guardar",
            colorConfirmar = TipoBotonEdu.ACTUALIZAR.fondo,
            icono = Icons.Rounded.EditNote,
            onCancelar = { mostrarConfirmar = false },
            onConfirmar = {
                mostrarConfirmar = false
                onGuardar(titulo.trim(), descripcion.trim(), fecha)
            }
        ) {
            // Qué cambia
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EduFondo, RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Cambia:", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.5.sp,
                    color = EduTextoSecundario
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (cambioTitulo) ChipEdu(if (esEvaluacion) "Nombre" else "Título", TonoEdu.AZUL)
                    if (cambioDescripcion) ChipEdu(if (esEvaluacion) "Temario" else "Descripción", TonoEdu.AZUL)
                    if (cambioFecha) ChipEdu("Fecha", TonoEdu.AMARILLO)
                }
                if (cambioFecha) {
                    Text(
                        "Nueva fecha: ${fechaLargaPub(fecha)}", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp, color = EduTexto
                    )
                }
            }
        }
    }

    if (guardando) PopupProcesandoEdu(titulo = "Guardando cambios...")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = if (esEvaluacion) "Editar evaluación" else "Editar tarea",
            subtitulo = publicacion?.let { "${it.cursoNombre} · ${it.gradoNombre} ${it.seccionNombre}" },
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
            if (publicacion != null) {
                CampoBloqueadoPub(
                    etiqueta = "Para",
                    icono = Icons.Rounded.Groups,
                    texto = "${publicacion.gradoNombre} ${publicacion.seccionNombre} · ${publicacion.cursoNombre}"
                )
            }
            CampoTextoPub(
                valor = titulo, onValorCambia = { titulo = it },
                etiqueta = if (esEvaluacion) "Evaluación" else "Título",
                placeholder = if (esEvaluacion) "Ej: Práctica calificada N° 2" else "Ej: Ejercicios cap. 5 · Fracciones"
            )
            CampoTextoPub(
                valor = descripcion, onValorCambia = { descripcion = it },
                etiqueta = if (esEvaluacion) "Temario" else "Descripción",
                placeholder = if (esEvaluacion) "Temas que entran y materiales que debe traer..."
                else "Qué deben hacer y cómo entregarlo...",
                lineas = if (esEvaluacion) 4 else 3
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                CampoFechaPub(
                    etiqueta = if (esEvaluacion) "Fecha de la evaluación" else "Fecha límite",
                    fecha = fecha,
                    onFecha = { fecha = it }
                )
                if (cambioFecha && fechaOriginal.isNotBlank()) {
                    Text(
                        "Antes: ${fechaLargaPub(fechaOriginal).replaceFirstChar { it.lowercase() }}",
                        fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
                    )
                }
            }
            // ADJUNTOS (bloqueados): uno debajo de otro
            val nombresAdjuntos = publicacion?.adjuntos.orEmpty()
                .ifEmpty { listOfNotNull(publicacion?.nombreAdjunto?.takeIf { it.isNotBlank() }) }
            if (!esEvaluacion && nombresAdjuntos.isNotEmpty()) {
                nombresAdjuntos.forEachIndexed { i, nombre ->
                    CampoBloqueadoPub(
                        etiqueta = when {
                            i > 0 -> null
                            nombresAdjuntos.size == 1 -> "Adjunto"
                            else -> "Adjuntos · ${nombresAdjuntos.size}"
                        },
                        icono = if (esNombreImagenEdu(nombre)) Icons.Rounded.Image else Icons.Rounded.Description,
                        texto = nombre
                    )
                }
            }
            Text(
                if (esEvaluacion) "La clase no se puede cambiar." else "La clase y el adjunto no se pueden cambiar.",
                fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
            )

            // AVISO A LOS PADRES
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EduAmarilloClaro, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = EduAmarilloOscuro, modifier = Modifier.size(22.dp))
                Text(
                    "Al guardar se avisa a los padres que la $nombreTipo se actualizó" +
                            (if (cambioFecha) ", con la nueva fecha." else "."),
                    modifier = Modifier.weight(1f),
                    fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                    lineHeight = 18.sp, color = EduAmarilloOscuro
                )
            }
        }

        // BOTONES FIJOS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BotonSecundarioEdu(
                texto = "Cancelar",
                modifier = Modifier.weight(1f),
                onClick = { if (!guardando) onBack() }
            )
            BotonPrincipalEdu(
                texto = "Guardar cambios",
                tipo = TipoBotonEdu.ACTUALIZAR,
                icono = Icons.Rounded.Save,
                modifier = Modifier.weight(1f),
                habilitado = publicacion != null && completo && hayCambios && !guardando,
                onClick = { if (completo && hayCambios) mostrarConfirmar = true }
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

// Campo que no se puede cambiar: fondo gris, texto gris y candado
@Composable
private fun CampoBloqueadoPub(etiqueta: String?, icono: ImageVector, texto: String) {
    val forma = RoundedCornerShape(16.dp)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (etiqueta != null) EtiquetaCampoPub(etiqueta)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduCampo, forma)
                .border(1.5.dp, EduBorde, forma)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icono, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(20.dp))
            Text(
                texto, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Bold,
                fontSize = 15.sp, color = EduTextoSecundario, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Icon(Icons.Rounded.Lock, contentDescription = "No se puede cambiar", tint = EduTextoSecundario, modifier = Modifier.size(18.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun EditarPublicacionPreview() {
    EduConnectAppTheme {
        EditarPublicacionScreen(
            publicacion = DetallePublicacionItem(
                "Ejercicios de fracciones", "Tarea", "Ciencia y Tecnología", "1er Grado", "A",
                "2026-11-17", "Resolver los ejercicios del 1 al 20 en el cuaderno.", "practica_fracciones.pdf",
                adjuntos = listOf("practica_fracciones.pdf", "ejemplo_resuelto.jpg")
            )
        )
    }
}
