package com.educonnectapp.ui.screens.docente.publicaciones

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.educonnectapp.ui.components.ContadorEdu
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupExitoEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.coloresDeTono
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduAmarilloClaro
import com.educonnectapp.ui.theme.EduAmarilloOscuro
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduCampo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// Datos de la tarea/evaluación
data class DetallePublicacionItem(
    val titulo: String,
    val tipo: String,               // "Tarea" | "Evaluación"
    val cursoNombre: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val fechaEntrega: String,       // "yyyy-MM-dd"
    val descripcion: String,
    val nombreAdjunto: String? = null,
    val estado: String = "ACTIVA"
)

// Entrega de un alumno (solo tareas: las evaluaciones son avisos). estado usa los códigos del backend:
// "PENDIENTE" | "ENTREGADO" | "NO_ENTREGADO" | "RENDIDO" | "NO_RINDIO" | "CALIFICADO"
// (notaNumerica, notaLiteral y observacion se mantienen solo por compatibilidad: ya no se usan)
data class AlumnoCalificacionItem(
    val alumnoId: Long,
    val codigo: String,
    val nombreCompleto: String,
    val estado: String = "PENDIENTE",
    val notaNumerica: Double? = null,
    val notaLiteral: String? = null,
    val observacion: String = "",
    val registrado: Boolean = false    // true si el docente ya registró la entrega de este alumno
)

// Estados que ve el docente (se traducen a los códigos del backend al guardar)
private const val UI_ENTREGADO = "ENTREGADO"
private const val UI_PENDIENTE = "PENDIENTE"
private const val UI_NO_ENTREGADO = "NO_ENTREGADO"

private val ESTADOS_ENTREGADO = setOf("ENTREGADO", "RENDIDO", "CALIFICADO")
private val ESTADOS_NO_ENTREGADO = setOf("NO_ENTREGADO", "NO_RINDIO")

// Código del backend -> estado que ve el docente
private fun estadoUi(estado: String): String = when (estado) {
    in ESTADOS_ENTREGADO -> UI_ENTREGADO
    in ESTADOS_NO_ENTREGADO -> UI_NO_ENTREGADO
    else -> UI_PENDIENTE
}

// Cada toque pasa al siguiente estado: Pendiente -> Entregó -> No entregó -> Pendiente
private fun siguienteEstado(estado: String): String = when (estado) {
    UI_PENDIENTE -> UI_ENTREGADO
    UI_ENTREGADO -> UI_NO_ENTREGADO
    else -> UI_PENDIENTE
}

// Entregó = turquesa · Pendiente = azul · No entregó = granate
private fun tonoEntrega(estado: String): TonoEdu = when (estado) {
    UI_ENTREGADO -> TonoEdu.VERDE
    UI_NO_ENTREGADO -> TonoEdu.CORAL
    else -> TonoEdu.AZUL
}

private fun textoEntrega(estado: String): String = when (estado) {
    UI_ENTREGADO -> "Entregó"
    UI_NO_ENTREGADO -> "No entregó"
    else -> "Pendiente"
}

// =====================================================================
// ENTREGAS DE UNA TAREA / DETALLE DE UN AVISO DE EVALUACIÓN (rediseño 2026)
// Cabecera fija amarilla · datos · contadores · lista de alumnos (un toque cambia el estado)
// Botones fijos: Registrar / Editar entregas · Cancelar + Guardar
// =====================================================================

@Composable
fun CalificarPublicacionScreen(
    publicacion: DetallePublicacionItem? = null,
    alumnos: List<AlumnoCalificacionItem> = emptyList(),
    cargando: Boolean = false,
    guardando: Boolean = false,
    descargandoAdjunto: Boolean = false,
    onAbrirAdjunto: () -> Unit = {},
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    // Solo los alumnos que cambiaron, con el estado final para el backend
    onGuardar: (List<AlumnoCalificacionItem>) -> Unit = {}
) {
    val context = LocalContext.current
    val esEvaluacion = publicacion?.tipo == "Evaluación"
    val anulada = publicacion?.estado == "ANULADA"

    // MODO: consulta (solo lectura) o registro (editable)
    var editando by remember { mutableStateOf(false) }
    // Al llegar la lista actualizada del backend (después de guardar), vuelve a modo consulta
    LaunchedEffect(alumnos) { editando = false }

    // Estados editables (se reinician con cada lista nueva o al cancelar)
    var version by remember { mutableStateOf(0) }
    val estadosUi = remember(alumnos, version) {
        mutableStateMapOf<Long, String>().apply { alumnos.forEach { put(it.alumnoId, estadoUi(it.estado)) } }
    }

    // POPUPS: confirmación -> "Guardando..." -> "Entregas guardadas"
    var cambiosPorConfirmar by remember { mutableStateOf<List<AlumnoCalificacionItem>?>(null) }
    var esperandoGuardado by remember { mutableStateOf(false) }
    var alumnosAlGuardar by remember { mutableStateOf<List<AlumnoCalificacionItem>>(emptyList()) }
    var mostrarExito by remember { mutableStateOf(false) }

    // Cuando termina de guardar: si llegó la lista actualizada, fue un éxito
    // (si falló, la lista no cambia y MainActivity muestra el error)
    LaunchedEffect(guardando) {
        if (!guardando && esperandoGuardado) {
            esperandoGuardado = false
            if (alumnos != alumnosAlGuardar) mostrarExito = true
        }
    }

    // ¿Ya se registró alguna vez? -> el botón dice "Editar entregas"
    val yaRegistrada = alumnos.any { it.registrado }
    // Contadores: en edición cuentan lo que se va marcando
    val estadosActuales = if (editando) alumnos.map { estadosUi[it.alumnoId] ?: UI_PENDIENTE } else alumnos.map { estadoUi(it.estado) }
    val totalEntregados = estadosActuales.count { it == UI_ENTREGADO }
    val totalNoEntregados = estadosActuales.count { it == UI_NO_ENTREGADO }
    val totalPendientes = estadosActuales.size - totalEntregados - totalNoEntregados
    val cantidadCambios = if (editando) alumnos.count { (estadosUi[it.alumnoId] ?: UI_PENDIENTE) != estadoUi(it.estado) } else 0

    fun cancelarEdicion() {
        version++            // descarta lo editado
        editando = false
    }

    // El botón "atrás" del celular cancela la edición en lugar de salir
    BackHandler(enabled = editando) { cancelarEdicion() }

    // Resultado final de un alumno. Si el docente no cambió su estado, se devuelve tal cual. Nunca se envían notas.
    fun resultado(a: AlumnoCalificacionItem): AlumnoCalificacionItem {
        val elegido = estadosUi[a.alumnoId] ?: UI_PENDIENTE
        if (elegido == estadoUi(a.estado)) return a
        val codigo = when (elegido) {
            UI_ENTREGADO -> "ENTREGADO"
            UI_NO_ENTREGADO -> "NO_ENTREGADO"
            else -> "PENDIENTE"
        }
        return a.copy(estado = codigo, notaNumerica = null, notaLiteral = null)
    }

    // ── POPUPS ──────────────────────────────────────────────────────────────
    cambiosPorConfirmar?.let { cambios ->
        val entregaron = cambios.count { it.estado in ESTADOS_ENTREGADO }
        val noEntregaron = cambios.count { it.estado in ESTADOS_NO_ENTREGADO }
        val pendientes = cambios.size - entregaron - noEntregaron
        PopupConfirmacionEdu(
            titulo = "¿Guardar entregas?",
            mensaje = "Se actualizarán ${cambios.size} " + (if (cambios.size == 1) "alumno" else "alumnos") +
                    " y se avisará a sus padres.",
            textoConfirmar = "Guardar",
            colorConfirmar = TipoBotonEdu.GUARDAR.fondo,
            icono = Icons.Rounded.FactCheck,
            onCancelar = { cambiosPorConfirmar = null },
            onConfirmar = {
                cambiosPorConfirmar = null
                alumnosAlGuardar = alumnos
                esperandoGuardado = true
                onGuardar(cambios)
            }
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (entregaron > 0) ContadorEdu(entregaron, "Entregaron", TonoEdu.VERDE, Modifier.weight(1f))
                if (pendientes > 0) ContadorEdu(pendientes, "Pendientes", TonoEdu.AZUL, Modifier.weight(1f))
                if (noEntregaron > 0) ContadorEdu(noEntregaron, "No entregó", TonoEdu.CORAL, Modifier.weight(1f))
            }
        }
    }

    if (guardando) PopupProcesandoEdu(titulo = "Guardando entregas...")

    if (mostrarExito) {
        PopupExitoEdu(
            titulo = "Entregas guardadas",
            mensaje = "Se avisó a los padres de los alumnos que cambiaron.",
            onCerrar = { mostrarExito = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = when {
                esEvaluacion -> "Aviso de evaluación"
                editando && yaRegistrada -> "Editar entregas"
                editando -> "Registrar entregas"
                else -> "Entregas"
            },
            subtitulo = publicacion?.let {
                if (editando) "${it.titulo} · ${it.gradoNombre} ${it.seccionNombre}"
                else "${it.cursoNombre} · ${it.gradoNombre} ${it.seccionNombre}"
            },
            onVolver = { if (editando) cancelarEdicion() else onBack() },
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

        if (cargando && alumnos.isEmpty() && publicacion == null) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EduTeal, strokeWidth = 3.dp)
            }
        } else {
            // CONTENIDO DESPLAZABLE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // DATOS DE LA PUBLICACIÓN (se ocultan al editar para dar espacio a la lista)
                if (publicacion != null && !editando) {
                    TarjetaPublicacion(
                        p = publicacion,
                        esEvaluacion = esEvaluacion,
                        descargandoAdjunto = descargandoAdjunto,
                        onAbrirAdjunto = onAbrirAdjunto
                    )
                }

                if (esEvaluacion) {
                    // EVALUACIÓN: solo aviso
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EduAmarilloClaro, RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = EduAmarilloOscuro, modifier = Modifier.size(22.dp))
                        Text(
                            "Las evaluaciones se publican como aviso para los padres. No requieren registro de entrega.",
                            modifier = Modifier.weight(1f),
                            fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                            lineHeight = 18.sp, color = EduAmarilloOscuro
                        )
                    }
                } else {
                    // TAREA: contadores + lista
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ContadorEdu(totalEntregados, "Entregaron", TonoEdu.VERDE, Modifier.weight(1f))
                        ContadorEdu(totalPendientes, "Pendientes", TonoEdu.AZUL, Modifier.weight(1f))
                        ContadorEdu(totalNoEntregados, "No entregó", TonoEdu.CORAL, Modifier.weight(1f))
                    }

                    if (editando) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(EduAmarilloClaro, RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Rounded.EditNote, contentDescription = null, tint = EduAmarilloOscuro, modifier = Modifier.size(22.dp))
                            Text(
                                "Toca el estado: Pendiente → Entregó → No entregó. Al guardar se avisa a los padres " +
                                        "de los alumnos que cambiaron.",
                                modifier = Modifier.weight(1f),
                                fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                                lineHeight = 18.sp, color = EduAmarilloOscuro
                            )
                        }
                    }

                    TituloSeccionEdu(
                        if (editando && cantidadCambios > 0)
                            "Alumnos · " + if (cantidadCambios == 1) "1 cambio" else "$cantidadCambios cambios"
                        else "Alumnos",
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        if (editando) {
                            Text(
                                "Todos entregaron",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EduTeal.copy(alpha = 0.12f))
                                    .clickable { alumnos.forEach { estadosUi[it.alumnoId] = UI_ENTREGADO } }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.sp, color = EduTealOscuro
                            )
                        } else {
                            ChipEdu("${alumnos.size}", TonoEdu.AZUL)
                        }
                    }

                    if (alumnos.isEmpty()) {
                        TarjetaEdu {
                            Text(
                                if (cargando) "Cargando alumnos..." else "No hay alumnos en esta sección.",
                                fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp,
                                color = EduTextoSecundario
                            )
                        }
                    } else {
                        TarjetaEdu(relleno = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                            alumnos.forEachIndexed { i, a ->
                                val original = estadoUi(a.estado)
                                val estado = if (editando) estadosUi[a.alumnoId] ?: UI_PENDIENTE else original
                                FilaEntrega(
                                    nombre = a.nombreCompleto,
                                    estado = estado,
                                    editable = editando,
                                    cambiado = editando && estado != original,
                                    ultima = i == alumnos.lastIndex,
                                    onCambiar = { estadosUi[a.alumnoId] = siguienteEstado(estado) }
                                )
                            }
                        }
                    }
                }
            }

            // BOTONES FIJOS (solo tareas)
            if (!esEvaluacion) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EduFondo)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (editando) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            BotonSecundarioEdu(
                                texto = "Cancelar",
                                modifier = Modifier.weight(1f),
                                onClick = { if (!guardando) cancelarEdicion() }
                            )
                            BotonPrincipalEdu(
                                texto = "Guardar",
                                tipo = TipoBotonEdu.GUARDAR,
                                icono = Icons.Rounded.Save,
                                modifier = Modifier.weight(1f),
                                habilitado = !guardando,
                                onClick = {
                                    val cambios = alumnos.mapNotNull { original ->
                                        resultado(original).takeIf { it.estado != original.estado }
                                    }
                                    if (cambios.isEmpty()) {
                                        Toast.makeText(context, "No hay cambios para guardar", Toast.LENGTH_SHORT).show()
                                        editando = false
                                    } else {
                                        cambiosPorConfirmar = cambios   // primero se confirma
                                    }
                                }
                            )
                        }
                    } else {
                        BotonPrincipalEdu(
                            texto = if (yaRegistrada) "Editar entregas" else "Registrar entregas",
                            tipo = if (yaRegistrada) TipoBotonEdu.ACTUALIZAR else TipoBotonEdu.GUARDAR,
                            icono = if (yaRegistrada) Icons.Rounded.Edit else Icons.Rounded.FactCheck,
                            habilitado = alumnos.isNotEmpty() && !anulada,
                            onClick = { version++; editando = true }
                        )
                    }
                }
            }
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

// Tarjeta con tipo, estado, título, fecha, descripción/temario y adjunto
@Composable
private fun TarjetaPublicacion(
    p: DetallePublicacionItem,
    esEvaluacion: Boolean,
    descargandoAdjunto: Boolean,
    onAbrirAdjunto: () -> Unit
) {
    val hoy = hoyPub()
    TarjetaEdu(relleno = PaddingValues(horizontal = 16.dp, vertical = 14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChipEdu(if (esEvaluacion) "Evaluación" else "Tarea", if (esEvaluacion) TonoEdu.AMARILLO else TonoEdu.AZUL)
            when {
                p.estado == "ANULADA" -> ChipEdu("Anulada", TonoEdu.CORAL)
                p.estado == "CERRADA" -> ChipEdu("Cerrada", TonoEdu.GRIS)
                p.fechaEntrega == hoy -> ChipEdu(if (esEvaluacion) "Es hoy" else "Vence hoy", TonoEdu.CORAL)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(p.titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp, color = EduTexto)
        Text(
            (if (esEvaluacion) "Fecha de la evaluación: " else "Fecha límite: ") +
                    (if (p.fechaEntrega.isNotEmpty()) fechaLargaPub(p.fechaEntrega) else "—"),
            fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduTextoSecundario,
            modifier = Modifier.padding(top = 2.dp)
        )
        if (p.descripcion.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            if (esEvaluacion) {
                Text(
                    "TEMARIO", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.sp,
                    letterSpacing = 0.6.sp, color = EduTextoSecundario, modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Text(
                p.descripcion,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EduCampo, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                lineHeight = 19.sp, color = EduEtiqueta
            )
        }
        if (!esEvaluacion && p.nombreAdjunto != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, EduBorde, RoundedCornerShape(14.dp))
                    .clickable(enabled = !descargandoAdjunto) { onAbrirAdjunto() }
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.Description, contentDescription = null, tint = EduAzulOscuro, modifier = Modifier.size(20.dp))
                Text(
                    if (descargandoAdjunto) "Descargando..." else p.nombreAdjunto,
                    modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp, color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                if (descargandoAdjunto) {
                    CircularProgressIndicator(color = EduTeal, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                } else {
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Abrir adjunto",
                        tint = EduTextoSecundario, modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

// Fila de un alumno: iniciales + nombre + estado (chip en consulta, botón en edición; amarillo claro si cambió)
@Composable
private fun FilaEntrega(
    nombre: String,
    estado: String,
    editable: Boolean,
    cambiado: Boolean,
    ultima: Boolean,
    onCambiar: () -> Unit
) {
    val tono = tonoEntrega(estado)
    val c = coloresDeTono(tono)
    val iniciales = nombre.replace(",", " ").split(" ").filter { it.isNotBlank() }.take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
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
                nombre, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp, color = EduTexto, maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (editable) {
                // Botón sólido con el estado (Pendiente: blanco con borde para que se note que se puede tocar)
                val pendiente = estado == UI_PENDIENTE
                Box(
                    modifier = Modifier
                        .width(108.dp)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (pendiente) Color.White else c.solido)
                        .border(if (pendiente) 1.5.dp else 0.dp, if (pendiente) EduBorde else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable { onCambiar() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        textoEntrega(estado), fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp,
                        color = if (pendiente) EduAzulOscuro else Color.White
                    )
                }
            } else {
                ChipEdu(textoEntrega(estado), tono)
            }
        }
        if (!ultima) Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EduFondo))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun CalificarPublicacionPreview() {
    EduConnectAppTheme {
        CalificarPublicacionScreen(
            publicacion = DetallePublicacionItem(
                "Ejercicios cap. 5 · Fracciones", "Tarea", "Ciencia y Tecnología", "1er Grado", "A",
                "2026-10-23", "Resolver los ejercicios del 1 al 20 en el cuaderno.", "practica_fracciones.pdf"
            ),
            alumnos = listOf(
                AlumnoCalificacionItem(1L, "PMU0001", "Garcia Vera, Ana", "ENTREGADO", registrado = true),
                AlumnoCalificacionItem(2L, "PMU0002", "Lopez Cruz, María", "ENTREGADO", registrado = true),
                AlumnoCalificacionItem(3L, "PMU0003", "Mendoza Paz, Carlos", "PENDIENTE"),
                AlumnoCalificacionItem(4L, "PMU0004", "Sanchez Vega, Pedro", "NO_ENTREGADO", registrado = true)
            )
        )
    }
}
