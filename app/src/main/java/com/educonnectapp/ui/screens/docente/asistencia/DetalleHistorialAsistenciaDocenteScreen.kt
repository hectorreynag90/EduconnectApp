package com.educonnectapp.ui.screens.docente.asistencia

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.educonnectapp.ui.components.BarraProgresoEdu
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.CampoTextoEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.ContadorEdu
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.coloresDeTono
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduAmarilloClaro
import com.educonnectapp.ui.theme.EduAmarilloOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

/** Registro de un alumno dentro del detalle de asistencia */
data class DetalleAlumnoItem(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val estado: String   // "A" = Presente, "T" = Tardanza, "F" = Falta
)

// Estado A / T / F → tono de la paleta y texto
private fun tonoDetalle(estado: String): TonoEdu = when (estado) {
    "A" -> TonoEdu.VERDE
    "T" -> TonoEdu.AMARILLO
    "F" -> TonoEdu.CORAL
    else -> TonoEdu.GRIS
}

private fun textoDetalle(estado: String): String = when (estado) {
    "A" -> "Asistió"
    "T" -> "Tardanza"
    "F" -> "Faltó"
    else -> "–"
}

// Cada toque pasa al siguiente estado: A -> T -> F -> A ...
private fun siguienteEstadoDetalle(estado: String): String = when (estado) {
    "A" -> "T"
    "T" -> "F"
    else -> "A"
}

// =====================================================================
// DETALLE DE UN DÍA DEL HISTORIAL (rediseño 2026)
// Cabecera fija azul oscuro · contadores · % del día · lista de alumnos · editar con motivo
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAsistenciaDocenteScreen(
    item: HistorialItem,
    alumnos: List<DetalleAlumnoItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onGuardarCambios: (alumnosEditados: List<DetalleAlumnoItem>, motivo: String, onDone: () -> Unit) -> Unit = { _, _, _ -> }
) {
    // ── ESTADO EDICIÓN ───────────────────────────────────────────────────────
    var modoEdicion by remember { mutableStateOf(false) }
    val estadosEditados = remember(alumnos) {
        mutableStateListOf(*alumnos.map { it.estado }.toTypedArray())
    }
    val hayCambios = remember(estadosEditados.toList(), alumnos) {
        estadosEditados.toList() != alumnos.map { it.estado }
    }
    var mostrarMotivo    by remember { mutableStateOf(false) }
    var motivo           by remember { mutableStateOf("") }
    var mostrarCargando  by remember { mutableStateOf(false) }
    var mostrarResultado by remember { mutableStateOf(false) }
    var cambiosResumen   by remember { mutableStateOf<List<CambioAsistenciaItem>>(emptyList()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Contadores dinámicos según el modo
    val estadosActuales = if (modoEdicion) estadosEditados.toList() else alumnos.map { it.estado }
    val totalAlumnos = alumnos.size
    val presentes    = estadosActuales.count { it == "A" }
    val tardanzas    = estadosActuales.count { it == "T" }
    val faltas       = estadosActuales.count { it == "F" }
    // Asistencia del día = Presentes + Tardanzas (la tardanza cuenta como asistió)
    val porcentaje   = if (totalAlumnos > 0) (presentes + tardanzas).toFloat() / totalAlumnos.toFloat() else 0f
    val cantidadCambios = alumnos.indices.count { i -> estadosEditados.getOrNull(i) != alumnos[i].estado }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = if (modoEdicion) "Editar asistencia" else item.curso,
            subtitulo = if (modoEdicion) "${item.curso} · ${item.grado} ${item.seccion}" else "${item.grado} ${item.seccion}",
            onVolver = onBack,
            colorFondo = ColorModulo.Asistencia,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Asistencia
                )
            },
            contenidoInferior = {
                ChipCabeceraEdu(
                    "${item.fechaDisplay} · ${item.hora}",
                    modifier = Modifier.padding(top = 14.dp),
                    colorCabecera = ColorModulo.Asistencia
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
            // CONTADORES
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ContadorEdu(presentes, "Asistió", TonoEdu.VERDE, Modifier.weight(1f))
                ContadorEdu(tardanzas, "Tardanza", TonoEdu.AMARILLO, Modifier.weight(1f))
                ContadorEdu(faltas, "Faltó", TonoEdu.CORAL, Modifier.weight(1f))
            }

            // ASISTENCIA DEL DÍA
            TarjetaEdu(relleno = PaddingValues(horizontal = 16.dp, vertical = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Asistencia del día", modifier = Modifier.weight(1f),
                        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTexto
                    )
                    Text(
                        "${(porcentaje * 100).toInt()}%", fontFamily = Nunito, fontWeight = FontWeight.Black,
                        fontSize = 18.sp, color = EduTealOscuro
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                BarraProgresoEdu(progreso = porcentaje, color = EduTeal)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "${presentes + tardanzas} de $totalAlumnos alumnos asistieron (la tardanza cuenta como asistencia)",
                    fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp,
                    color = EduTextoSecundario, lineHeight = 17.sp
                )
            }

            // AVISO DEL MODO EDICIÓN
            if (modoEdicion) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EduAmarilloClaro, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Rounded.EditNote, contentDescription = null, tint = EduAmarilloOscuro, modifier = Modifier.size(22.dp))
                    Text(
                        "Toca el estado de cada alumno: A → T → F. Al actualizar se pedirá el motivo, " +
                                "que se envía al director, y se avisa a los padres.",
                        modifier = Modifier.weight(1f),
                        fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                        lineHeight = 18.sp, color = EduAmarilloOscuro
                    )
                }
            }

            // LISTA DE ALUMNOS
            Spacer(modifier = Modifier.height(2.dp))
            TituloSeccionEdu("Alumnos") {
                if (modoEdicion && cantidadCambios > 0) {
                    ChipEdu(
                        if (cantidadCambios == 1) "1 cambio" else "$cantidadCambios cambios",
                        TonoEdu.AMARILLO
                    )
                } else {
                    ChipEdu("$totalAlumnos", TonoEdu.AZUL, icono = Icons.Rounded.Groups)
                }
            }
            if (alumnos.isEmpty()) {
                TarjetaEdu {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconoCirculoEdu(Icons.Rounded.Groups, TonoEdu.GRIS)
                        Text(
                            "No hay alumnos en este registro.", modifier = Modifier.weight(1f),
                            fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp, color = EduTextoSecundario
                        )
                    }
                }
            } else {
                TarjetaEdu(relleno = PaddingValues(horizontal = 14.dp, vertical = 4.dp)) {
                    alumnos.forEachIndexed { index, alumno ->
                        val estado = if (modoEdicion) estadosEditados[index] else alumno.estado
                        FilaDetalleAlumno(
                            numero = index + 1,
                            alumno = alumno,
                            estado = estado,
                            editable = modoEdicion,
                            cambiado = modoEdicion && estado != alumno.estado,
                            ultima = index == alumnos.lastIndex,
                            onCambiar = { estadosEditados[index] = siguienteEstadoDetalle(estadosEditados[index]) }
                        )
                    }
                }
            }
        }

        // ── BOTONES FIJOS ABAJO ──────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (!modoEdicion) {
                BotonPrincipalEdu(
                    texto = "Editar asistencia",
                    tipo = TipoBotonEdu.ACTUALIZAR,
                    icono = Icons.Rounded.Edit,
                    habilitado = alumnos.isNotEmpty(),
                    onClick = { modoEdicion = true }
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BotonSecundarioEdu(
                        texto = "Cancelar",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // Restaurar estados originales
                            alumnos.forEachIndexed { i, a -> estadosEditados[i] = a.estado }
                            modoEdicion = false
                        }
                    )
                    BotonPrincipalEdu(
                        texto = "Actualizar",
                        tipo = TipoBotonEdu.ACTUALIZAR,
                        icono = Icons.Rounded.Sync,
                        modifier = Modifier.weight(1f),
                        habilitado = hayCambios,
                        onClick = { if (hayCambios) mostrarMotivo = true }
                    )
                }
            }
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Alumnos"
        )
    }

    // ── POPUP: ACTUALIZANDO ──────────────────────────────────────────────────
    if (mostrarCargando) {
        PopupProcesandoEdu(titulo = "Actualizando registros...")
    }

    // ── POPUP: RESULTADO (lista de cambios) ──────────────────────────────────
    if (mostrarResultado) {
        PopupCambiosActualizados(
            cambios = cambiosResumen,
            onCerrar = { mostrarResultado = false }
        )
    }

    // ── HOJA INFERIOR: MOTIVO DEL CAMBIO ─────────────────────────────────────
    if (mostrarMotivo) {
        ModalBottomSheet(
            onDismissRequest = { mostrarMotivo = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconoCirculoEdu(Icons.Rounded.EditNote, TonoEdu.AZUL)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Motivo del cambio", fontFamily = Nunito, fontWeight = FontWeight.Black,
                            fontSize = 20.sp, color = EduTexto
                        )
                        Text(
                            if (cantidadCambios == 1) "1 alumno modificado" else "$cantidadCambios alumnos modificados",
                            fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp,
                            color = EduTextoSecundario
                        )
                    }
                }
                Text(
                    "Este motivo será enviado al director como aviso del cambio realizado.",
                    fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                    color = EduEtiqueta, lineHeight = 19.sp
                )
                CampoTextoEdu(
                    valor = motivo,
                    onValorCambia = { motivo = it },
                    etiqueta = "Motivo",
                    placeholder = "Ej: Error en el registro inicial de asistencia...",
                    lineas = 4
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BotonSecundarioEdu(
                        texto = "Cancelar",
                        modifier = Modifier.weight(1f),
                        onClick = { mostrarMotivo = false }
                    )
                    BotonPrincipalEdu(
                        texto = "Confirmar",
                        tipo = TipoBotonEdu.GUARDAR,
                        icono = Icons.Rounded.Check,
                        modifier = Modifier.weight(1f),
                        habilitado = motivo.isNotBlank(),
                        onClick = {
                            if (motivo.isNotBlank()) {
                                val alumnosEditados = alumnos.mapIndexed { i, a ->
                                    a.copy(estado = estadosEditados[i])
                                }
                                // Resumen de cambios antes de guardar
                                cambiosResumen = alumnos.mapIndexedNotNull { i, a ->
                                    val nuevoEstado = estadosEditados[i]
                                    if (a.estado != nuevoEstado)
                                        CambioAsistenciaItem(nombreAlumno(a.apellidos, a.nombres), a.estado, nuevoEstado)
                                    else null
                                }
                                val motivoIngresado = motivo   // guardar ANTES de limpiar el campo
                                mostrarMotivo   = false
                                modoEdicion     = false
                                motivo          = ""
                                mostrarCargando = true
                                onGuardarCambios(alumnosEditados, motivoIngresado) {
                                    mostrarCargando  = false
                                    mostrarResultado = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

// Fila de un alumno: iniciales en círculo + nombre + estado
// Vista: chip con el estado · Edición: botón que cambia A → T → F (fondo amarillo claro si cambió)
@Composable
private fun FilaDetalleAlumno(
    numero: Int,
    alumno: DetalleAlumnoItem,
    estado: String,
    editable: Boolean,
    cambiado: Boolean,
    ultima: Boolean,
    onCambiar: () -> Unit
) {
    val nombre = nombreAlumno(alumno.apellidos, alumno.nombres)
    val iniciales = nombre.replace(",", " ").split(" ").filter { it.isNotBlank() }.take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
    val c = coloresDeTono(tonoDetalle(estado))
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (cambiado) EduAmarilloClaro else Color.Transparent)
                .clickable(enabled = editable) { onCambiar() }
                .padding(horizontal = 4.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(38.dp).background(c.fondo, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(iniciales, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.sp, color = c.texto)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "$numero. $nombre",
                fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = EduTexto,
                maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (editable) {
                // Botón sólido con la letra del estado (texto oscuro sobre amarillo)
                Box(
                    modifier = Modifier
                        .size(width = 52.dp, height = 40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.solido)
                        .clickable { onCambiar() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        estado, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp,
                        color = if (estado == "T") EduTexto else Color.White
                    )
                }
            } else {
                ChipEdu(textoDetalle(estado), tonoDetalle(estado))
            }
        }
        if (!ultima) Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EduFondo))
    }
}

// Resultado de la actualización: check turquesa + lista "antes → ahora" + botón Entendido
@Composable
private fun PopupCambiosActualizados(cambios: List<CambioAsistenciaItem>, onCerrar: () -> Unit) {
    Dialog(onDismissRequest = onCerrar) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(horizontal = 22.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(88.dp).background(EduTealClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(66.dp).background(EduTeal, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(38.dp))
                }
            }
            Text(
                "¡Registros actualizados!", fontFamily = Nunito, fontWeight = FontWeight.Black,
                fontSize = 21.sp, color = EduTealOscuro, textAlign = TextAlign.Center
            )
            Text(
                if (cambios.size == 1) "1 alumno modificado · se avisó al director y a los padres"
                else "${cambios.size} alumnos modificados · se avisó al director y a los padres",
                fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                color = EduEtiqueta, textAlign = TextAlign.Center, lineHeight = 19.sp
            )
            if (cambios.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 260.dp)
                        .background(EduAzulOscuroClaro.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    cambios.forEach { c ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                c.alumno, fontFamily = Nunito, fontWeight = FontWeight.Black,
                                fontSize = 14.sp, color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ChipEdu(textoDetalle(c.estadoAnterior), tonoDetalle(c.estadoAnterior))
                                Icon(
                                    Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = "cambia a",
                                    tint = EduTextoSecundario, modifier = Modifier.size(16.dp)
                                )
                                ChipEdu(textoDetalle(c.estadoNuevo), tonoDetalle(c.estadoNuevo), solido = true)
                            }
                        }
                    }
                }
            }
            BotonPrincipalEdu(
                texto = "Entendido",
                tipo = TipoBotonEdu.AVANZAR,
                onClick = onCerrar,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun DetalleAsistenciaDocentePreview() {
    EduConnectAppTheme {
        DetalleAsistenciaDocenteScreen(
            item = HistorialItem(
                1L, "Personal Social", "1er Grado", "A",
                "2026-06-23", "Martes 23 de junio 2026", "03:48 p.m.",
                3, 1, 5, tardanzas = 1
            ),
            alumnos = listOf(
                DetalleAlumnoItem(1L, "Ana Beatriz", "Garcia Vera", "A"),
                DetalleAlumnoItem(2L, "Maria Elena", "Lopez Cruz", "T"),
                DetalleAlumnoItem(3L, "Carlos", "Mendoza Paz", "A"),
                DetalleAlumnoItem(4L, "Pedro", "Sanchez Vega", "F"),
                DetalleAlumnoItem(5L, "Luis Miguel", "Torres Rios", "A"),
            )
        )
    }
}
