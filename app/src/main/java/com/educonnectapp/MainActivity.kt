package com.educonnectapp

import android.app.NotificationChannel
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.app.NotificationManager
import android.os.Build


import com.educonnectapp.ui.screens.CambioAsistenciaItem
import com.educonnectapp.ui.screens.AgregarAsignacionScreen
import com.educonnectapp.ui.screens.AlertaAsignacion
import com.educonnectapp.ui.screens.AlertaDatos
import com.educonnectapp.ui.screens.AsignacionGestion
import com.educonnectapp.ui.screens.BottomNavBarDocente
import com.educonnectapp.ui.screens.EditarDatosScreen
import com.educonnectapp.ui.screens.MisAsignacionesScreen
import com.educonnectapp.ui.screens.AlertaHijo
import com.educonnectapp.ui.screens.BottomNavBarPadre
import com.educonnectapp.ui.screens.MisHijosScreen
import com.educonnectapp.ui.screens.AlumnoEncontrado
import com.educonnectapp.ui.screens.AsistenciasScreen
import com.educonnectapp.ui.screens.AsociarHijoScreen
import com.educonnectapp.ui.screens.ConfirmacionAsistenciaScreen
import com.educonnectapp.ui.screens.ConfirmacionComunicadoScreen
import com.educonnectapp.ui.screens.ComunicadosScreen
import com.educonnectapp.ui.screens.DetalleAsistenciaScreen
import com.educonnectapp.ui.screens.HistorialAsistenciasScreen
import com.educonnectapp.ui.screens.HomeDocenteScreen
import com.educonnectapp.ui.screens.HomePadreScreen
import com.educonnectapp.ui.screens.LoginScreen
import com.educonnectapp.ui.screens.NuevoComunicadoScreen
import com.educonnectapp.ui.screens.PerfilDocenteScreen
import com.educonnectapp.ui.screens.PerfilPadreScreen
import com.educonnectapp.ui.screens.AlertaRegistro
import com.educonnectapp.ui.screens.AlertaRecuperacion
import com.educonnectapp.ui.screens.ContrasenaActualizadaScreen
import com.educonnectapp.ui.screens.ErrorLogin
import com.educonnectapp.ui.screens.NuevaContrasenaScreen
import com.educonnectapp.ui.screens.RecuperarCorreoScreen
import com.educonnectapp.ui.screens.VerificarCodigoScreen
import com.educonnectapp.ui.screens.RegisterScreen
import com.educonnectapp.ui.screens.RegistroAsistenciaScreen
import com.educonnectapp.ui.screens.SeleccionarEstudianteScreen
import com.educonnectapp.ui.screens.SeleccionarSeccionScreen
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.screens.HijoAsociado
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.educonnectapp.ui.screens.AsignacionItem
import com.educonnectapp.ui.screens.AlumnoItem
import com.educonnectapp.ui.screens.GradoItem
import com.educonnectapp.ui.screens.SeccionItem
import com.educonnectapp.ui.screens.CursoItem
import com.educonnectapp.ui.screens.HijoItem
import com.educonnectapp.ui.screens.AsistenciaItem
import com.educonnectapp.ui.screens.CursoItem2
import com.educonnectapp.ui.screens.DetalleAsistencia

import com.educonnectapp.ui.screens.SeccionDestinatario
import com.educonnectapp.ui.screens.SeleccionarComunicadoScreen
import com.educonnectapp.ui.screens.BusquedaComunicadoScreen
import com.educonnectapp.ui.screens.DetalleComunicadoScreen
import com.educonnectapp.ui.screens.SeccionComunicadoItem
import com.educonnectapp.ui.screens.ComunicadoResumenItem
import com.educonnectapp.ui.screens.PadreLecturaItem
import com.educonnectapp.ui.screens.PublicacionesScreen
import com.educonnectapp.ui.screens.SeleccionarCursoPublicacionScreen
import com.educonnectapp.ui.screens.NuevaTareaScreen
import com.educonnectapp.ui.screens.NuevaEvaluacionScreen
import com.educonnectapp.ui.screens.ConfirmacionPublicacionScreen
import com.educonnectapp.ui.screens.SeleccionarHijoAgendaScreen
import com.educonnectapp.ui.screens.AgendaEscolarScreen
import com.educonnectapp.ui.screens.DetalleAgendaScreen
import com.educonnectapp.ui.screens.HijoAgendaItem
import com.educonnectapp.ui.screens.PublicacionAgendaItem
import com.educonnectapp.ui.screens.ComunicadosPadresScreen
import com.educonnectapp.ui.screens.SeleccionarEstudianteComunicadoScreen
import com.educonnectapp.ui.screens.ComunicadosRecibidosScreen
import com.educonnectapp.ui.screens.DetalleComunicadoPadreScreen
import com.educonnectapp.ui.screens.HijoComunicadoItem
import com.educonnectapp.ui.screens.ComunicadoPadreItem
import com.educonnectapp.ui.screens.WelcomeScreen
import com.educonnectapp.ui.screens.HistorialAsistenciaScreen
import com.educonnectapp.ui.screens.HistorialItem
import com.educonnectapp.ui.screens.DetalleAlumnoItem
import com.educonnectapp.ui.screens.SeleccionarSeccionHistorialScreen

// Backend propio (Spring Boot)
import com.educonnectapp.data.api.restablecerContrasena
import com.educonnectapp.data.api.solicitarCodigoRecuperacion
import com.educonnectapp.data.api.verificarCodigoRecuperacion
import com.educonnectapp.data.api.ApiException
import com.educonnectapp.data.api.ingresar
import com.educonnectapp.data.api.registrar
import com.educonnectapp.data.api.cerrarSesion
import com.educonnectapp.data.api.guardarFcmToken
import com.educonnectapp.data.api.AsignacionResponse
import com.educonnectapp.data.api.HijoResponse
import com.educonnectapp.data.api.obtenerAsignaciones
import com.educonnectapp.data.api.agregarAsignacion
import com.educonnectapp.data.api.eliminarAsignacion
import com.educonnectapp.data.api.actualizarPerfil
import com.educonnectapp.data.api.cambiarFotoPerfil
import com.educonnectapp.data.api.crearCargadorImagenes
import com.educonnectapp.data.api.prepararFotoPerfil
import com.educonnectapp.data.api.precargarImagen
import com.educonnectapp.data.api.urlArchivo
import com.educonnectapp.data.api.obtenerGrados
import com.educonnectapp.data.api.obtenerSeccionesDeGrado
import com.educonnectapp.data.api.obtenerCursos
import com.educonnectapp.data.api.obtenerHijos
import com.educonnectapp.data.api.buscarAlumno
import com.educonnectapp.data.api.asociarHijo
import com.educonnectapp.data.api.desasociarHijo
import com.educonnectapp.data.api.AsistenciaHijoResponse
import com.educonnectapp.data.api.obtenerHojaAsistencia
import com.educonnectapp.data.api.registrarAsistencia
import com.educonnectapp.data.api.actualizarAsistenciaDia
import com.educonnectapp.data.api.obtenerHistorialDocente
import com.educonnectapp.data.api.obtenerResumenAsistenciasHoy
import com.educonnectapp.data.api.obtenerAsistenciasHijo
import com.educonnectapp.data.api.formatearHora
import com.educonnectapp.data.api.formatearFechaLarga
import com.educonnectapp.data.api.porcentajeAsistencia
import com.educonnectapp.data.api.inicioAnioEscolar
import com.educonnectapp.data.api.ArchivoResponse
import com.educonnectapp.data.api.ComunicadoDocenteResponse
import com.educonnectapp.data.api.subirArchivo
import com.educonnectapp.data.api.descargarArchivo
import com.educonnectapp.data.api.enviarComunicado
import com.educonnectapp.data.api.obtenerComunicadosDocente
import com.educonnectapp.data.api.obtenerDetalleComunicadoDocente
import com.educonnectapp.data.api.obtenerComunicadosHijo
import com.educonnectapp.data.api.marcarComunicadoLeido
import com.educonnectapp.data.api.fechaLocal
import com.educonnectapp.data.api.horaLocal
import com.educonnectapp.data.api.isoLocalSinZona
import com.educonnectapp.data.api.aFechaHoraLocal
import com.educonnectapp.data.api.PublicacionDocenteResponse
import com.educonnectapp.data.api.PublicacionPadreResponse
import com.educonnectapp.data.api.NotificacionResponse
import com.educonnectapp.data.api.obtenerPublicacionesDocente
import com.educonnectapp.data.api.crearPublicacion
import com.educonnectapp.data.api.obtenerAgendaHijo
import com.educonnectapp.data.api.marcarPublicacionLeida
import com.educonnectapp.data.api.obtenerBandeja
import com.educonnectapp.data.api.marcarNotificacionesLeidas
import com.educonnectapp.data.api.marcarNotificacionLeida
import com.educonnectapp.data.api.obtenerPublicacionHijo
import com.educonnectapp.data.api.ComunicadoPadreResponse
import com.educonnectapp.data.api.DetallePublicacionDocenteResponse
import com.educonnectapp.data.api.CalificacionRequest
import com.educonnectapp.data.api.obtenerDetallePublicacionDocente
import com.educonnectapp.data.api.registrarCalificaciones
import com.educonnectapp.ui.screens.HistorialPublicacionesScreen
import com.educonnectapp.ui.screens.PublicacionHistorialItem
import com.educonnectapp.ui.screens.CalificarPublicacionScreen
import com.educonnectapp.ui.screens.DetallePublicacionItem
import com.educonnectapp.ui.screens.AlumnoCalificacionItem
import com.educonnectapp.ui.screens.PanelNotificaciones
import com.educonnectapp.ui.screens.NotificacionItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope


enum class Screen {
    WELCOME,
    LOGIN, REGISTER, HOME_DOCENTE, HOME_PADRE, ASISTENCIAS,
    SELECCIONAR_SECCION, REGISTRO_ASISTENCIA, CONFIRMACION_ASISTENCIA,
    SELECCIONAR_ESTUDIANTE, HISTORIAL_ASISTENCIAS, DETALLE_ASISTENCIA, SELECCIONAR_SECCION_HISTORIAL, HISTORIAL_ASISTENCIA_DOCENTE,
    DETALLE_ASISTENCIA_DOCENTE,
    COMUNICADOS, NUEVO_COMUNICADO, CONFIRMACION_COMUNICADO,
    PERFIL_DOCENTE, PERFIL_PADRE, AGREGAR_ASIGNACION, ASOCIAR_HIJO,
    MIS_ASIGNACIONES, EDITAR_DATOS_DOCENTE, MIS_HIJOS, EDITAR_DATOS_PADRE,
    SELECCIONAR_COMUNICADO, BUSQUEDA_COMUNICADO, DETALLE_COMUNICADO,
    PUBLICACIONES, SELECCIONAR_CURSO_PUBLICACION, NUEVA_TAREA, NUEVA_EVALUACION, CONFIRMACION_PUBLICACION,
    SELECCIONAR_HIJO_AGENDA, AGENDA_ESCOLAR, DETALLE_AGENDA,
    COMUNICADOS_PADRE, SELECCIONAR_ESTUDIANTE_COMUNICADO, COMUNICADOS_RECIBIDOS, DETALLE_COMUNICADO_PADRE,
    HISTORIAL_PUBLICACIONES, CALIFICAR_PUBLICACION,
    RECUPERAR_CORREO, RECUPERAR_CODIGO, RECUPERAR_NUEVA, RECUPERAR_LISTO
}

data class UsuarioLogueado(
    val id: Long, val nombrecompleto: String, val email: String,
    val rol: String, val dni: String = "", val telefono: String = "", val codigo: String = "",
    val fotoId: Long? = null   // foto de perfil (id del archivo en el backend)
)

// CONVERSION BACKEND -> UI
private fun HijoResponse.aHijoAsociado() = HijoAsociado(
    id = alumnoId, nombres = nombres, apellidos = apellidos,
    gradoNombre = grado, seccionNombre = seccion, codigoEstudiante = codigoEstudiante
)

private fun HijoResponse.aAlumnoEncontrado() = AlumnoEncontrado(
    id = alumnoId, nombres = nombres, apellidos = apellidos,
    gradoNombre = grado, seccionNombre = seccion, codigoEstudiante = codigoEstudiante
)

// Historial docente: 1 fila por día con sus totales (el backend ya los agrupa)
// presentes = solo "A" (total - faltas - tardanzas), así A, T y F se muestran por separado
// sin importar si el backend suma las tardanzas dentro de "asistieron"
private suspend fun cargarHistorialDocente(
    seccionId: Long, cursoId: Long, grado: String, seccion: String, curso: String
): List<HistorialItem> =
    obtenerHistorialDocente(seccionId, cursoId).map { dia ->
        val r = dia.resumen
        HistorialItem(
            id           = dia.fecha.hashCode().toLong(),
            curso        = curso,
            grado        = grado,
            seccion      = seccion,
            fecha        = dia.fecha,
            fechaDisplay = formatearFechaLarga(dia.fecha),
            hora         = formatearHora(dia.hora),
            presentes    = (r.total - r.faltas - r.tardanzas).coerceAtLeast(0).toInt(),
            ausentes     = r.faltas.toInt(),
            total        = r.total.toInt(),
            tardanzas    = r.tardanzas.toInt()
        )
    }.sortedByDescending { it.fecha }

// Detalle de un día: alumnos con asistencia registrada
// (el backend envía nombreCompleto; se muestra completo en "nombres")
private suspend fun cargarDetalleDia(seccionId: Long, cursoId: Long, fecha: String): List<DetalleAlumnoItem> =
    obtenerHojaAsistencia(seccionId, cursoId, fecha).alumnos.mapNotNull { a ->
        a.estado?.let { DetalleAlumnoItem(id = a.alumnoId, nombres = a.nombreCompleto, apellidos = "", estado = it) }
    }

// Agenda del padre: publicación del backend -> tarjeta de AgendaEscolarScreen
// Tarea (entregable): "Pendiente" (por entregar), "Entregada", "No entregada" o "Vencida"
// Evaluación (solo aviso): "Pendiente" (próximo) o "Realizado" (la fecha ya pasó)
private fun PublicacionPadreResponse.aPublicacionAgendaItem(hoy: String): PublicacionAgendaItem {
    val fecha = fechaEntrega ?: ""
    val yaPaso = fecha.isNotEmpty() && fecha < hoy
    val estadoUi = if (tipo == "EVALUACION") {
        if (yaPaso) "Realizado" else "Pendiente"
    } else when (calificacionHijo?.estado) {
        "ENTREGADO", "CALIFICADO" -> "Entregada"
        "NO_ENTREGADO" -> "No entregada"
        else -> if (yaPaso) "Vencida" else "Pendiente"
    }
    return PublicacionAgendaItem(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        tipo = if (tipo == "EVALUACION") "Evaluación" else "Tarea",
        cursoNombre = curso,
        docenteNombre = docente,
        fechaEntrega = fecha,
        estado = estadoUi,
        archivoAdjunto = archivo?.nombre ?: ""
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Coil (fotos de perfil) usa el cliente HTTP de ApiClient, que agrega el token de la sesión
        coil.Coil.setImageLoader(crearCargadorImagenes(this))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "asistencia_channel",
                "Asistencias",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de asistencia de alumnos"
                enableLights(true)
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        setContent { EduConnectAppTheme { EduConnectApp() } }
    }
}

@Composable
fun EduConnectApp() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(Screen.WELCOME) }
    var usuarioLogueado by remember { mutableStateOf<UsuarioLogueado?>(null) }
    var gradoIdSel by remember { mutableStateOf(0L) }
    var seccionIdSel by remember { mutableStateOf(0L) }
    var cursoIdSel by remember { mutableStateOf(0L) }
    var gradoSel by remember { mutableStateOf("") }
    var seccionSel by remember { mutableStateOf("") }
    var cursoSel by remember { mutableStateOf("") }
    var presentesSel by remember { mutableStateOf(0) }
    var ausentesSel by remember { mutableStateOf(0) }
    var alumnoIdSel by remember { mutableStateOf(0L) }
    var alumnoNombreSel by remember { mutableStateOf("") }
    var asistenciaIdSel by remember { mutableStateOf(0L) }
    var fechaAsistenciaSel by remember { mutableStateOf("") }
    var comunicadoAsuntoSel by remember { mutableStateOf("") }
    var comunicadoDestinatarioSel by remember { mutableStateOf("") }
    var comunicadoNotificadosSel by remember { mutableStateOf(0) }
    var comunicadoCursoSel by remember { mutableStateOf("") }
    var comunicadoHoraSel by remember { mutableStateOf("") }
    var comunicadoFechaSel by remember { mutableStateOf("") }
    var alumnoEncontradoSel by remember { mutableStateOf<AlumnoEncontrado?>(null) }
    var errorBusquedaSel by remember { mutableStateOf("") }
    var hijosAsociadosSel by remember { mutableStateOf<List<HijoAsociado>>(emptyList()) }
    var asignacionesSel by remember { mutableStateOf<List<AsignacionItem>>(emptyList()) }
    var gradosDisp by remember { mutableStateOf<List<String>>(emptyList()) }
    var seccionesDisp by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    var cursosDisp by remember { mutableStateOf<List<String>>(emptyList()) }
    var listaAlumnosSel by remember { mutableStateOf<List<AlumnoItem>>(emptyList()) }
    var listaGradosSel by remember { mutableStateOf<List<GradoItem>>(emptyList()) }
    var listaSeccionesSel by remember { mutableStateOf<List<SeccionItem>>(emptyList()) }
    var listaCursosSel by remember { mutableStateOf<List<CursoItem>>(emptyList()) }
    var cantidadAlumnosSel by remember { mutableStateOf(0) }
    var isLoginLoading by remember { mutableStateOf(false) }
    var listaHijosSel by remember { mutableStateOf<List<HijoItem>>(emptyList()) }
    var todasAsistenciasSel by remember { mutableStateOf<List<AsistenciaItem>>(emptyList()) }
    var listaCursosHistorialSel by remember { mutableStateOf<List<CursoItem2>>(emptyList()) }
    var detalleAsistenciaSel by remember { mutableStateOf<DetalleAsistencia?>(null) }
    var listaDestinatariosSel by remember { mutableStateOf<List<SeccionDestinatario>>(emptyList()) }
    var totalEnviadosSel by remember { mutableStateOf(0) }
    var totalHoySel by remember { mutableStateOf(0) }
    var sinLeerSel by remember { mutableStateOf(0) }
    var seccionesComunicadoSel by remember { mutableStateOf<List<SeccionComunicadoItem>>(emptyList()) }
    var comunicadosListaSel by remember { mutableStateOf<List<ComunicadoResumenItem>>(emptyList()) }
    var padresLecturaSel by remember { mutableStateOf<List<PadreLecturaItem>>(emptyList()) }
    var comunicadoIdSel by remember { mutableStateOf(0L) }
    var comunicadoMensajeSel by remember { mutableStateOf("") }
    var comunicadoTotalNotifSel by remember { mutableStateOf(0) }
    var tipoPublicacionSel by remember { mutableStateOf("Tarea") }
    var tituloPublicacionSel by remember { mutableStateOf("") }
    var fechaEntregaSel by remember { mutableStateOf("") }
    var horaPublicacionSel by remember { mutableStateOf("") }
    var hijosAgendaSel by remember { mutableStateOf<List<HijoAgendaItem>>(emptyList()) }
    var hijoAgendaSel by remember { mutableStateOf<HijoAgendaItem?>(null) }
    var publicacionesAgendaSel by remember { mutableStateOf<List<PublicacionAgendaItem>>(emptyList()) }
    var publicacionAgendaSel by remember { mutableStateOf<PublicacionAgendaItem?>(null) }
    var hijosComunicadoSel by remember { mutableStateOf<List<HijoComunicadoItem>>(emptyList()) }
    var hijoComunicadoSel by remember { mutableStateOf<HijoComunicadoItem?>(null) }
    var comunicadosPadreSel by remember { mutableStateOf<List<ComunicadoPadreItem>>(emptyList()) }
    var historialDocenteSel by remember { mutableStateOf<List<HistorialItem>>(emptyList()) }
    var historialItemSel    by remember { mutableStateOf<HistorialItem?>(null) }
    var detalleAlumnosSel   by remember { mutableStateOf<List<DetalleAlumnoItem>>(emptyList()) }
    var comunicadoPadreSel by remember { mutableStateOf<ComunicadoPadreItem?>(null) }
    var sinLeerPadreSel by remember { mutableStateOf(0) }
    var leidosPadreSel by remember { mutableStateOf(0) }
    var totalPadreSel by remember { mutableStateOf(0) }
    var resumenRealizadosSel by remember { mutableStateOf(0) }
    var resumenPendientesSel by remember { mutableStateOf(0) }
    var resumenPorcentajeSel by remember { mutableStateOf(0) }
    var tardanzasSel by remember { mutableStateOf(0) }
    var asignacionesApiSel by remember { mutableStateOf<List<AsignacionResponse>>(emptyList()) }
    var todosGradosSel by remember { mutableStateOf<List<GradoItem>>(emptyList()) }
    // Catálogo para AGREGAR_ASIGNACION: nombre -> id ("1er Grado|A" -> seccionId)
    var seccionIdPorNombre by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }
    var cursoIdPorNombre by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }
    // Agregar asignación: estado de la petición y resultado que muestran los popups
    var guardandoAsignacion by remember { mutableStateOf(false) }
    var asignacionExitosa by remember { mutableStateOf(false) }
    var alertaAsignacionSel by remember { mutableStateOf<AlertaAsignacion?>(null) }
    // Pantalla a la que vuelve "Agregar asignación" (perfil o Mis asignaciones)
    var origenAgregarAsignacion by remember { mutableStateOf(Screen.PERFIL_DOCENTE) }
    // Mis asignaciones: eliminación
    var eliminandoAsignacion by remember { mutableStateOf(false) }
    var asignacionEliminadaSel by remember { mutableStateOf<AsignacionGestion?>(null) }
    var alertaEliminarSel by remember { mutableStateOf<AlertaAsignacion?>(null) }
    // Editar datos personales
    var guardandoDatos by remember { mutableStateOf(false) }
    var datosActualizados by remember { mutableStateOf(false) }
    var alertaDatosSel by remember { mutableStateOf<AlertaDatos?>(null) }
    // Foto de perfil
    var guardandoFoto by remember { mutableStateOf(false) }
    var fotoActualizada by remember { mutableStateOf(false) }
    var alertaFotoSel by remember { mutableStateOf<AlertaDatos?>(null) }
    // Padre: asociar y quitar hijos
    var origenAsociarHijo by remember { mutableStateOf(Screen.PERFIL_PADRE) }
    var asociandoHijo by remember { mutableStateOf(false) }
    var hijoAsociadoOk by remember { mutableStateOf(false) }
    var alertaAsociarSel by remember { mutableStateOf<AlertaHijo?>(null) }
    var quitandoHijo by remember { mutableStateOf(false) }
    var hijoQuitadoSel by remember { mutableStateOf<HijoAsociado?>(null) }
    var alertaQuitarSel by remember { mutableStateOf<AlertaHijo?>(null) }

    // EDITAR DATOS (docente y padre): guarda nombre y teléfono y actualiza la sesión
    fun guardarDatosPersonales(nombre: String, telefono: String) {
        CoroutineScope(Dispatchers.Main).launch {
            guardandoDatos = true
            val inicio = System.currentTimeMillis()
            try {
                val actualizado = actualizarPerfil(nombre, telefono)
                usuarioLogueado = usuarioLogueado?.copy(
                    nombrecompleto = actualizado.nombreCompleto,
                    telefono = actualizado.telefono
                )
                val resto = 1000L - (System.currentTimeMillis() - inicio)
                if (resto > 0) delay(resto)
                datosActualizados = true
            } catch (e: ApiException) {
                alertaDatosSel = AlertaDatos("No se pudo actualizar", e.message ?: "Error al guardar los datos.")
            } catch (e: Exception) {
                alertaDatosSel = AlertaDatos("No se pudo actualizar", "Error al guardar: ${e.message}")
            } finally {
                guardandoDatos = false
            }
        }
    }

    // FOTO DE PERFIL (docente y padre): prepara la imagen, la sube y actualiza la sesión
    fun guardarFotoPerfil(uri: android.net.Uri) {
        CoroutineScope(Dispatchers.Main).launch {
            guardandoFoto = true
            val inicio = System.currentTimeMillis()
            try {
                val jpeg = prepararFotoPerfil(context.contentResolver, uri)
                val actualizado = cambiarFotoPerfil(jpeg)
                usuarioLogueado = usuarioLogueado?.copy(fotoId = actualizado.fotoArchivoId)
                // El popup de carga se ve al menos 1 s aunque el servidor responda antes
                val resto = 1000L - (System.currentTimeMillis() - inicio)
                if (resto > 0) delay(resto)
                fotoActualizada = true
            } catch (e: ApiException) {
                alertaFotoSel = AlertaDatos("No se pudo guardar la foto", e.message ?: "Error al subir la foto.")
            } catch (e: Exception) {
                alertaFotoSel = AlertaDatos("No se pudo guardar la foto", "No se pudo leer la imagen seleccionada. Prueba con otra foto.")
            } finally {
                guardandoFoto = false
            }
        }
    }
    // Asistencias
    var fechaHojaSel by remember { mutableStateOf("") }   // fecha de la hoja cargada (servidor)
    // Registro de cuenta: estado de la petición y resultado que muestran los popups
    var registrandoCuenta by remember { mutableStateOf(false) }
    var alertaRegistroSel by remember { mutableStateOf<AlertaRegistro?>(null) }
    var registroExitosoSel by remember { mutableStateOf(false) }
    // Login: error del último intento (popup + campo en rojo)
    var errorLoginSel by remember { mutableStateOf<ErrorLogin?>(null) }
    // Recuperar contraseña
    var recEmailSel by remember { mutableStateOf("") }
    var recExpiraEnSel by remember { mutableStateOf(0L) }        // millis en que vence el código
    var recReenviarDesdeSel by remember { mutableStateOf(0L) }   // millis desde los que se puede reenviar
    var recTokenSel by remember { mutableStateOf("") }           // token de un solo uso (tras verificar)
    var recCargando by remember { mutableStateOf(false) }
    var recReenviando by remember { mutableStateOf(false) }
    var recErrorCodigoSel by remember { mutableStateOf<String?>(null) }
    var recAlertaSel by remember { mutableStateOf<AlertaRecuperacion?>(null) }
    // Confirmación de asistencia: ¿fue actualización? y qué alumnos cambiaron
    var esActualizacionAsistenciaSel by remember { mutableStateOf(false) }
    var cambiosAsistenciaSel by remember { mutableStateOf<List<CambioAsistenciaItem>>(emptyList()) }
    var asistenciasHijoSel by remember { mutableStateOf<List<AsistenciaHijoResponse>>(emptyList()) }
    // Comunicados
    var enviandoComunicado by remember { mutableStateOf(false) }
    var comunicadosDocenteSel by remember { mutableStateOf<List<ComunicadoDocenteResponse>>(emptyList()) }
    var adjuntosComunicadoSel by remember { mutableStateOf<Map<Long, ArchivoResponse>>(emptyMap()) }
    var descargandoAdjunto by remember { mutableStateOf(false) }
    // Datos ya cargados (evitan peticiones repetidas al volver a una pantalla).
    // Se recargan al entrar al módulo desde el Home o después de guardar algo.
    var datosDocenteCargados by remember { mutableStateOf(false) }
    var comunicadosDocenteCargados by remember { mutableStateOf(false) }
    var comunicadosTodosSel by remember { mutableStateOf<List<ComunicadoDocenteResponse>>(emptyList()) }
    var historialDocenteCargado by remember { mutableStateOf(false) }
    var hijosApiSel by remember { mutableStateOf<List<HijoResponse>?>(null) }
    var hijosAsistenciaCargados by remember { mutableStateOf(false) }
    var historialHijoCargadoDe by remember { mutableStateOf<Long?>(null) }
    var comunicadosPadreCargados by remember { mutableStateOf(false) }
    var comunicadosHijoCargadoDe by remember { mutableStateOf<Long?>(null) }
    // Publicaciones (docente)
    var publicacionesDocenteSel by remember { mutableStateOf<List<PublicacionDocenteResponse>>(emptyList()) }
    var publicacionesCargadas by remember { mutableStateOf(false) }
    var seccionesPublicacionSel by remember { mutableStateOf<List<SeccionComunicadoItem>>(emptyList()) }
    var asignacionPublicacionSel by remember { mutableStateOf<AsignacionResponse?>(null) }
    var publicandoSel by remember { mutableStateOf(false) }
    var publicacionNotificadosSel by remember { mutableStateOf(0) }
    // Historial y calificaciones (docente)
    var publicacionIdSel by remember { mutableStateOf(0L) }
    var detallePublicacionSel by remember { mutableStateOf<DetallePublicacionDocenteResponse?>(null) }
    var cargandoDetallePublicacion by remember { mutableStateOf(false) }
    var guardandoCalificaciones by remember { mutableStateOf(false) }
    // Agenda (padre)
    var agendaPorHijoSel by remember { mutableStateOf<Map<Long, List<PublicacionPadreResponse>>>(emptyMap()) }
    var agendaCargada by remember { mutableStateOf(false) }
    var adjuntosPublicacionSel by remember { mutableStateOf<Map<Long, ArchivoResponse>>(emptyMap()) }
    var publicacionesLeidasSel by remember { mutableStateOf<Set<Long>>(emptySet()) }
    // Notificaciones (campana del Home padre)
    var notificacionesSel by remember { mutableStateOf<List<NotificacionResponse>>(emptyList()) }
    var notificacionesNoLeidasSel by remember { mutableStateOf(0L) }
    var cargandoNotificaciones by remember { mutableStateOf(false) }
    var mostrarNotificaciones by remember { mutableStateOf(false) }   // panel debajo de la campana
    // Comunicados de cada hijo (para ubicar el comunicado de una notificación sin pedirlo de nuevo)
    var comunicadosPorHijoSel by remember { mutableStateOf<Map<Long, List<ComunicadoPadreResponse>>>(emptyMap()) }

    fun aviso(mensaje: String) { Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show() }

    // ASIGNACIONES DEL DOCENTE (1 sola petición: ya traen nombres y cantidad de alumnos)
    fun aplicarAsignaciones(lista: List<AsignacionResponse>) {
        asignacionesApiSel = lista
        todosGradosSel = lista.distinctBy { it.gradoId }.map { GradoItem(it.gradoId, it.grado) }.sortedBy { it.nombre }
        asignacionesSel = lista.map { AsignacionItem(it.grado, it.seccion, it.curso) }
        listaGradosSel = todosGradosSel
        listaSeccionesSel = emptyList()
        listaCursosSel = emptyList()
        cantidadAlumnosSel = 0
    }

    // Filtros LOCALES sobre las asignaciones (sin peticiones al backend)
    fun seccionesDeGrado(gradoId: Long): List<SeccionItem> =
        asignacionesApiSel.filter { it.gradoId == gradoId }
            .distinctBy { it.seccionId }
            .map { SeccionItem(it.seccionId, it.seccion) }
            .sortedBy { it.nombre }

    fun cursosDeSeccion(seccionId: Long): List<CursoItem> =
        asignacionesApiSel.filter { it.seccionId == seccionId }
            .distinctBy { it.cursoId }
            .map { CursoItem(it.cursoId, it.curso) }
            .sortedBy { it.nombre }

    fun alumnosDeSeccion(seccionId: Long): Int =
        asignacionesApiSel.firstOrNull { it.seccionId == seccionId }?.cantidadAlumnos?.toInt() ?: 0

    fun actualizarResumen(resumen: Triple<Int, Int, Int>) {
        resumenRealizadosSel = resumen.first
        resumenPendientesSel = resumen.second
        resumenPorcentajeSel = resumen.third
    }

    // Hijos del padre: se piden 1 sola vez por sesión (se actualizan en PERFIL_PADRE y al asociar)
    suspend fun hijosDelPadre(): List<HijoResponse> =
        hijosApiSel ?: obtenerHijos().also { hijosApiSel = it }

    // Navegación del padre (barra inferior): entrar al módulo recarga sus datos
    fun irAvisosPadre() {
        comunicadosPadreCargados = false
        currentScreen = Screen.COMUNICADOS_PADRE
    }

    fun irAgenda() {
        agendaCargada = false
        currentScreen = Screen.SELECCIONAR_HIJO_AGENDA
    }

    // Agenda de cada hijo: 1 petición por hijo, en paralelo
    // Rango: últimos 30 días (para ver lo entregado/vencido) hasta fin del año escolar
    suspend fun cargarAgendaPadre() {
        val hijos = hijosDelPadre()
        val hoy = java.time.LocalDate.now()
        val inicio = inicioAnioEscolar(hoy)
        val desde = maxOf(hoy.minusDays(30), inicio).toString()
        val hasta = inicio.plusYears(1).minusDays(1).toString()
        val agendas = coroutineScope {
            hijos.map { h -> async { obtenerAgendaHijo(h.alumnoId, desde, hasta) } }.awaitAll()
        }
        val publicacionesPorHijo = hijos.mapIndexed { i, h ->
            // Solo tareas y evaluaciones vigentes (las anuladas no se muestran)
            h.alumnoId to agendas[i].publicaciones.filter {
                it.estado != "ANULADA" && (it.tipo == "TAREA" || it.tipo == "EVALUACION")
            }
        }.toMap()
        agendaPorHijoSel = publicacionesPorHijo
        adjuntosPublicacionSel = publicacionesPorHijo.values.flatten()
            .mapNotNull { p -> p.archivo?.let { p.id to it } }.toMap()
        publicacionesLeidasSel = publicacionesPorHijo.values.flatten().filter { it.leido }.map { it.id }.toSet()
        val hoyTexto = hoy.toString()
        hijosAgendaSel = hijos.map { h ->
            val items = publicacionesPorHijo[h.alumnoId].orEmpty().map { it.aPublicacionAgendaItem(hoyTexto) }
            HijoAgendaItem(
                id = h.alumnoId,
                nombres = h.nombres,
                apellidos = h.apellidos,
                gradoNombre = h.grado,
                seccionNombre = h.seccion,
                gradoId = h.gradoId,
                seccionId = h.seccionId,
                tareasPendientes = items.count { it.tipo == "Tarea" && it.estado == "Pendiente" },
                examenesPendientes = items.count { it.tipo == "Evaluación" && it.estado == "Pendiente" }
            )
        }
        agendaCargada = true
    }

    // Comunicados de cada hijo del padre: 1 petición por hijo, en paralelo
    suspend fun cargarComunicadosPadre() {
        val hijos = hijosDelPadre()
        val respuestas = coroutineScope {
            hijos.map { h -> async { obtenerComunicadosHijo(h.alumnoId) } }.awaitAll()
        }
        hijosComunicadoSel = hijos.mapIndexed { i, h ->
            HijoComunicadoItem(
                id = h.alumnoId,
                nombres = h.nombres,
                apellidos = h.apellidos,
                gradoNombre = h.grado,
                seccionNombre = h.seccion,
                seccionId = h.seccionId,
                sinLeer = respuestas[i].noLeidos,
                total = respuestas[i].comunicados.size
            )
        }
        comunicadosPorHijoSel = hijos.mapIndexed { i, h -> h.alumnoId to respuestas[i].comunicados }.toMap()
        comunicadosPadreCargados = true
    }

    // Lista de comunicados de un hijo (COMUNICADOS_RECIBIDOS): más recientes primero, luego los no leídos arriba
    fun aplicarComunicadosHijo(hijo: HijoComunicadoItem, comunicados: List<ComunicadoPadreResponse>) {
        adjuntosComunicadoSel = comunicados.mapNotNull { c -> c.archivo?.let { c.id to it } }.toMap()
        comunicadosPadreSel = comunicados
            .sortedByDescending { aFechaHoraLocal(it.enviadoEn) }
            .map { c ->
                ComunicadoPadreItem(
                    id = c.id,
                    asunto = c.asunto,
                    mensaje = c.mensaje,
                    fecha = fechaLocal(c.enviadoEn),
                    hora = horaLocal(c.enviadoEn),
                    docenteNombre = c.docente,
                    gradoNombre = hijo.gradoNombre,
                    seccionNombre = hijo.seccionNombre,
                    cursoNombre = c.curso ?: "General",
                    leido = c.leido,
                    leidoEn = isoLocalSinZona(c.leidoEn)
                )
            }.sortedBy { it.leido }
        comunicadosHijoCargadoDe = hijo.id
    }

    // Hijo y comunicado a los que apunta una notificación de comunicado
    fun contextoComunicado(comunicadoId: Long?): Pair<HijoComunicadoItem, ComunicadoPadreResponse>? {
        if (comunicadoId == null) return null
        for ((alumnoId, comunicados) in comunicadosPorHijoSel) {
            val comunicado = comunicados.find { it.id == comunicadoId } ?: continue
            val hijo = hijosComunicadoSel.find { it.id == alumnoId } ?: continue
            return hijo to comunicado
        }
        return null
    }

    // Notificación de comunicado: abre directamente su detalle (DETALLE_COMUNICADO_PADRE).
    // "Volver" lleva a la lista de comunicados de ese hijo. Si no se encuentra, abre el módulo Comunicados.
    fun abrirComunicadoDeNotificacion(comunicadoId: Long?) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                var contexto = contextoComunicado(comunicadoId)
                if (contexto == null) {
                    cargarComunicadosPadre()   // puede ser un comunicado nuevo: datos frescos
                    contexto = contextoComunicado(comunicadoId)
                }
                val encontrado = contexto
                if (encontrado == null) {
                    irAvisosPadre()
                } else {
                    val (hijo, comunicado) = encontrado
                    hijoComunicadoSel = hijo
                    aplicarComunicadosHijo(hijo, comunicadosPorHijoSel[hijo.id].orEmpty())
                    comunicadoPadreSel = comunicadosPadreSel.find { it.id == comunicado.id }
                    currentScreen = Screen.DETALLE_COMUNICADO_PADRE
                }
            } catch (e: Exception) {
                aviso("No se pudo abrir el comunicado: ${e.message}")
            }
        }
    }

    // Campana del padre: abre (o cierra) el panel de notificaciones y carga la bandeja
    fun abrirNotificaciones() {
        if (mostrarNotificaciones) { mostrarNotificaciones = false; return }
        mostrarNotificaciones = true
        cargandoNotificaciones = true
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 1 petición: bandeja del padre (más recientes primero)
                val bandeja = obtenerBandeja()
                notificacionesSel = bandeja.notificaciones.sortedByDescending { aFechaHoraLocal(it.createdAt) }
                notificacionesNoLeidasSel = bandeja.noLeidas
                // Curso, grado y sección de las tareas/exámenes: salen de la agenda (1 petición por hijo, solo si no está cargada)
                val hayPublicaciones = notificacionesSel.any { it.tipo == "PUBLICACION" || it.tipo == "CALIFICACION" }
                // Si hay tareas/evaluaciones o entregas sin leer, la agenda cambió: se vuelve a pedir
                val hayNuevas = notificacionesSel.any { !it.leida && (it.tipo == "PUBLICACION" || it.tipo == "CALIFICACION") }
                if (hayNuevas) agendaCargada = false
                if (hayPublicaciones && !agendaCargada) {
                    try { cargarAgendaPadre() } catch (e: Exception) { /* el panel se muestra sin ese dato */ }
                }
                // Curso, grado y sección de los comunicados (1 petición por hijo, solo si no están cargados)
                val hayComunicados = notificacionesSel.any { it.tipo == "COMUNICADO" }
                if (hayComunicados && !comunicadosPadreCargados) {
                    try { cargarComunicadosPadre() } catch (e: Exception) { /* el panel se muestra sin ese dato */ }
                }
            } catch (e: Exception) {
                aviso("Error cargando notificaciones: ${e.message}")
            } finally {
                cargandoNotificaciones = false
            }
        }
    }

    // Hijo y publicación a los que apunta una notificación de tarea/evaluación (busca en la agenda ya cargada)
    fun contextoPublicacion(publicacionId: Long?): Pair<HijoAgendaItem, PublicacionPadreResponse>? {
        if (publicacionId == null) return null
        for ((alumnoId, publicaciones) in agendaPorHijoSel) {
            val publicacion = publicaciones.find { it.id == publicacionId } ?: continue
            val hijo = hijosAgendaSel.find { it.id == alumnoId } ?: continue
            return hijo to publicacion
        }
        return null
    }

    // Notificación de tarea/evaluación: abre directamente su detalle (DETALLE_AGENDA).
    // Si no está en la agenda cargada, la pide al backend; si no se encuentra, abre la Agenda.
    fun abrirPublicacionDeNotificacion(publicacionId: Long?) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (!agendaCargada) cargarAgendaPadre()
                var contexto = contextoPublicacion(publicacionId)
                if (contexto == null && publicacionId != null) {
                    // Fuera del rango de la agenda: probar con cada hijo
                    for (hijo in hijosAgendaSel) {
                        val publicacion = try { obtenerPublicacionHijo(hijo.id, publicacionId) } catch (e: ApiException) { null }
                        if (publicacion != null) {
                            publicacion.archivo?.let { adjuntosPublicacionSel = adjuntosPublicacionSel + (publicacion.id to it) }
                            if (publicacion.leido) publicacionesLeidasSel = publicacionesLeidasSel + publicacion.id
                            contexto = hijo to publicacion
                            break
                        }
                    }
                }
                val encontrado = contexto
                when {
                    encontrado == null -> irAgenda()
                    encontrado.second.estado == "ANULADA" -> aviso("Esta publicación fue anulada por el docente")
                    else -> {
                        hijoAgendaSel = encontrado.first
                        publicacionAgendaSel = encontrado.second
                            .aPublicacionAgendaItem(java.time.LocalDate.now().toString())
                        currentScreen = Screen.DETALLE_AGENDA
                    }
                }
            } catch (e: Exception) {
                aviso("No se pudo abrir la publicación: ${e.message}")
            }
        }
    }

    // Lleva al módulo de la notificación (asistencias, comunicados o agenda) con datos frescos
    fun irAModuloDeNotificacion(tipo: String) {
        when (tipo) {
            "ASISTENCIA" -> {
                hijosAsistenciaCargados = false
                currentScreen = Screen.SELECCIONAR_ESTUDIANTE
            }
            "COMUNICADO" -> irAvisosPadre()
            "PUBLICACION", "CALIFICACION" -> irAgenda()
            else -> { /* AVISO: el mensaje completo ya se ve en la bandeja */ }
        }
    }


    // Comunicados del docente: 1 petición (todas las secciones); las pantallas filtran localmente
    suspend fun comunicadosDelDocente(): List<ComunicadoDocenteResponse> {
        if (!comunicadosDocenteCargados) {
            comunicadosTodosSel = obtenerComunicadosDocente()
            comunicadosDocenteCargados = true
        }
        return comunicadosTodosSel
    }

    // Publicar tarea o evaluación: sube el adjunto (si hay) y crea la publicación.
    // El backend notifica por push a los padres de la sección.
    fun publicar(
        tipoBackend: String, titulo: String, descripcion: String, adjunto: String,
        seccion: SeccionDestinatario, fechaEntrega: String, hora: String
    ) {
        if (publicandoSel) return   // evita doble envío
        publicandoSel = true
        aviso(if (adjunto.isNotEmpty()) "Subiendo adjunto y publicando..." else "Publicando...")
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val archivoId = if (adjunto.isNotEmpty())
                    subirArchivo(context.contentResolver, android.net.Uri.parse(adjunto)).id
                else null
                val publicada = crearPublicacion(
                    seccion.seccionId, seccion.cursoId, tipoBackend, titulo, descripcion, fechaEntrega, archivoId
                )
                tituloPublicacionSel = publicada.titulo
                fechaEntregaSel = publicada.fechaEntrega ?: fechaEntrega
                horaPublicacionSel = hora
                publicacionNotificadosSel = publicada.totalAlumnos.toInt()
                publicacionesCargadas = false   // el resumen cambió
                currentScreen = Screen.CONFIRMACION_PUBLICACION
            } catch (e: ApiException) {
                aviso(when (e.codigo) {
                    413 -> "El archivo supera los 10 MB"
                    415 -> "Tipo de archivo no permitido (PDF, Word, Excel, TXT o imagen)"
                    else -> "Error al publicar: ${e.message}"
                })
            } catch (e: Exception) {
                aviso("Error al publicar: ${e.message}")
            } finally {
                publicandoSel = false
            }
        }
    }

    // Descarga el adjunto (con el token de sesión) y lo abre con la app que corresponda
    fun abrirAdjunto(archivo: ArchivoResponse) {
        if (descargandoAdjunto) return
        descargandoAdjunto = true
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val nombreSeguro = archivo.nombre.replace(Regex("[^A-Za-z0-9._-]"), "_")
                val destino = java.io.File(context.cacheDir, "adjuntos/${archivo.id}_$nombreSeguro")
                if (!destino.exists() || destino.length() == 0L) descargarArchivo(archivo.id, destino)
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context, "${context.packageName}.fileprovider", destino
                )
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, archivo.tipoMime.ifBlank { "*/*" })
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: android.content.ActivityNotFoundException) {
                aviso("No hay una aplicación para abrir este tipo de archivo")
            } catch (e: Exception) {
                aviso("No se pudo abrir el adjunto: ${e.message}")
            } finally {
                descargandoAdjunto = false
            }
        }
    }

    // Al cerrar sesión: borrar datos del usuario anterior (otra cuenta no debe verlos)
    fun limpiarDatosSesion() {
        usuarioLogueado = null
        isLoginLoading = false
        aplicarAsignaciones(emptyList())
        hijosAsociadosSel = emptyList()
        alumnoEncontradoSel = null
        errorBusquedaSel = ""
        listaAlumnosSel = emptyList()
        historialDocenteSel = emptyList()
        detalleAlumnosSel = emptyList()
        listaHijosSel = emptyList()
        todasAsistenciasSel = emptyList()
        asistenciasHijoSel = emptyList()
        actualizarResumen(Triple(0, 0, 0))
        comunicadosDocenteSel = emptyList()
        comunicadosListaSel = emptyList()
        padresLecturaSel = emptyList()
        hijosComunicadoSel = emptyList()
        comunicadosPadreSel = emptyList()
        comunicadoPadreSel = null
        adjuntosComunicadoSel = emptyMap()
        datosDocenteCargados = false
        comunicadosDocenteCargados = false
        comunicadosTodosSel = emptyList()
        historialDocenteCargado = false
        hijosApiSel = null
        hijosAsistenciaCargados = false
        historialHijoCargadoDe = null
        comunicadosPadreCargados = false
        comunicadosHijoCargadoDe = null
        publicacionesDocenteSel = emptyList()
        publicacionesCargadas = false
        detallePublicacionSel = null
        seccionesPublicacionSel = emptyList()
        asignacionPublicacionSel = null
        agendaPorHijoSel = emptyMap()
        agendaCargada = false
        hijosAgendaSel = emptyList()
        hijoAgendaSel = null
        publicacionesAgendaSel = emptyList()
        adjuntosPublicacionSel = emptyMap()
        publicacionesLeidasSel = emptySet()
        notificacionesSel = emptyList()
        notificacionesNoLeidasSel = 0L
        mostrarNotificaciones = false
        comunicadosPorHijoSel = emptyMap()
    }


    Scaffold(modifier = Modifier.fillMaxSize(), contentColor = TextPrimary) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {

                Screen.WELCOME -> WelcomeScreen(
                    onLoginClick = { currentScreen = Screen.LOGIN },
                    onRegisterClick = { currentScreen = Screen.REGISTER }
                )

                Screen.LOGIN -> LoginScreen(
                    isLoading = isLoginLoading,
                    errorLogin = errorLoginSel,
                    onOlvidePassword = {
                        errorLoginSel = null
                        recAlertaSel = null
                        currentScreen = Screen.RECUPERAR_CORREO
                    },
                    onLogin = { emailInput, passwordInput ->
                        when {
                            // La pantalla ya valida los campos vacíos (popup "Faltan datos")
                            emailInput.isBlank() || passwordInput.isBlank() -> errorLoginSel = ErrorLogin("VACIOS")
                            else -> {
                                isLoginLoading = true
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        val usuario = ingresar(emailInput, passwordInput)
                                        // El director (ADMIN) aún no tiene pantallas en la app
                                        if (usuario.rol != "DOCENTE" && usuario.rol != "PADRE") {
                                            cerrarSesion()
                                            aviso("Esta cuenta no tiene acceso a la app móvil")
                                            return@launch
                                        }
                                        usuarioLogueado = UsuarioLogueado(
                                            id = usuario.id,
                                            nombrecompleto = usuario.nombreCompleto,
                                            email = usuario.email,
                                            rol = usuario.rol,
                                            dni = usuario.dni,
                                            telefono = usuario.telefono,
                                            fotoId = usuario.fotoArchivoId
                                        )
                                        // La foto empieza a descargarse ya (o sale de la caché) mientras se arma el inicio
                                        usuario.fotoArchivoId?.let { precargarImagen(context, urlArchivo(it)) }
                                        errorLoginSel = null
                                        currentScreen = if (usuario.rol == "DOCENTE") Screen.HOME_DOCENTE else Screen.HOME_PADRE

                                        // Registrar token FCM del dispositivo en el backend
                                        com.google.firebase.messaging.FirebaseMessaging.getInstance().token
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    val token = task.result
                                                    CoroutineScope(Dispatchers.IO).launch {
                                                        try { guardarFcmToken(token) } catch (e: Exception) { }
                                                    }
                                                }
                                            }
                                    } catch (e: ApiException) {
                                        // 404 = correo no registrado | 400 = correo con formato inválido
                                        // 401 = contraseña incorrecta | otros (403 cuenta desactivada, sin conexión)
                                        errorLoginSel = when (e.codigo) {
                                            404 -> ErrorLogin("CORREO")
                                            400 -> ErrorLogin("CORREO", e.message ?: "")
                                            401 -> ErrorLogin("CONTRASENA")
                                            else -> ErrorLogin("OTRO", e.message ?: "Error al ingresar")
                                        }
                                    } catch (e: Exception) {
                                        errorLoginSel = ErrorLogin("OTRO", "Error al ingresar: ${e.message}")
                                    } finally {
                                        isLoginLoading = false
                                    }
                                }
                            }
                        }
                    },
                    onGoToRegister = {
                        errorLoginSel = null
                        currentScreen = Screen.REGISTER
                    }
                )

                // ---------- RECUPERAR CONTRASEÑA ----------
                Screen.RECUPERAR_CORREO -> RecuperarCorreoScreen(
                    emailInicial = recEmailSel,
                    enviando = recCargando,
                    alerta = recAlertaSel,
                    onEnviar = { email ->
                        if (!recCargando) {
                            recCargando = true
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    val r = solicitarCodigoRecuperacion(email)
                                    val ahora = System.currentTimeMillis()
                                    recEmailSel = r.email.ifBlank { email }
                                    recExpiraEnSel = ahora + r.expiraEnSegundos * 1000
                                    recReenviarDesdeSel = ahora + r.reenviarEnSegundos * 1000
                                    recErrorCodigoSel = null
                                    currentScreen = Screen.RECUPERAR_CODIGO
                                } catch (e: ApiException) {
                                    recAlertaSel = if (e.codigo == 404) AlertaRecuperacion("CORREO_NO_REGISTRADO")
                                    else AlertaRecuperacion("ERROR", e.message ?: "")
                                } catch (e: Exception) {
                                    recAlertaSel = AlertaRecuperacion("ERROR", e.message ?: "")
                                } finally {
                                    recCargando = false
                                }
                            }
                        }
                    },
                    onCerrarAlerta = { recAlertaSel = null },
                    onVolverLogin = {
                        recAlertaSel = null
                        currentScreen = Screen.LOGIN
                    }
                )

                Screen.RECUPERAR_CODIGO -> VerificarCodigoScreen(
                    email = recEmailSel,
                    expiraEnMillis = recExpiraEnSel,
                    reenviarDesdeMillis = recReenviarDesdeSel,
                    verificando = recCargando,
                    reenviando = recReenviando,
                    errorCodigo = recErrorCodigoSel,
                    alerta = recAlertaSel,
                    onVerificar = { codigo ->
                        if (!recCargando) {
                            recCargando = true
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    recTokenSel = verificarCodigoRecuperacion(recEmailSel, codigo)
                                    recErrorCodigoSel = null
                                    currentScreen = Screen.RECUPERAR_NUEVA
                                } catch (e: ApiException) {
                                    when (e.codigo) {
                                        // Código incorrecto: casillas en rojo con los intentos restantes
                                        400 -> recErrorCodigoSel = e.message ?: "Código incorrecto. Verifica e intenta nuevamente."
                                        // Vencido o sin intentos: hay que reenviar
                                        409 -> {
                                            recErrorCodigoSel = null
                                            recExpiraEnSel = System.currentTimeMillis()
                                            recAlertaSel = AlertaRecuperacion("CODIGO_VENCIDO", e.message ?: "")
                                        }
                                        else -> recAlertaSel = AlertaRecuperacion("ERROR", e.message ?: "")
                                    }
                                } catch (e: Exception) {
                                    recAlertaSel = AlertaRecuperacion("ERROR", e.message ?: "")
                                } finally {
                                    recCargando = false
                                }
                            }
                        }
                    },
                    onReenviar = {
                        if (!recReenviando) {
                            recReenviando = true
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    val r = solicitarCodigoRecuperacion(recEmailSel)
                                    val ahora = System.currentTimeMillis()
                                    recExpiraEnSel = ahora + r.expiraEnSegundos * 1000
                                    recReenviarDesdeSel = ahora + r.reenviarEnSegundos * 1000
                                    recErrorCodigoSel = null
                                    aviso("Te enviamos un nuevo código")
                                } catch (e: ApiException) {
                                    recAlertaSel = AlertaRecuperacion("ERROR", e.message ?: "")
                                } catch (e: Exception) {
                                    recAlertaSel = AlertaRecuperacion("ERROR", e.message ?: "")
                                } finally {
                                    recReenviando = false
                                }
                            }
                        }
                    },
                    onLimpiarError = { recErrorCodigoSel = null },
                    onCerrarAlerta = { recAlertaSel = null },
                    onBack = {
                        recErrorCodigoSel = null
                        recAlertaSel = null
                        currentScreen = Screen.RECUPERAR_CORREO
                    }
                )

                Screen.RECUPERAR_NUEVA -> NuevaContrasenaScreen(
                    guardando = recCargando,
                    alerta = recAlertaSel,
                    onActualizar = { nueva ->
                        if (!recCargando) {
                            recCargando = true
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    restablecerContrasena(recEmailSel, recTokenSel, nueva)
                                    recTokenSel = ""
                                    currentScreen = Screen.RECUPERAR_LISTO
                                } catch (e: ApiException) {
                                    recAlertaSel = when (e.codigo) {
                                        409 -> AlertaRecuperacion("SOLICITUD_VENCIDA", e.message ?: "")
                                        400 -> AlertaRecuperacion("CONTRASENA", e.message ?: "")
                                        else -> AlertaRecuperacion("ERROR", e.message ?: "")
                                    }
                                } catch (e: Exception) {
                                    recAlertaSel = AlertaRecuperacion("ERROR", e.message ?: "")
                                } finally {
                                    recCargando = false
                                }
                            }
                        }
                    },
                    onCerrarAlerta = {
                        // Si la solicitud venció, se vuelve a pedir el código
                        val vencida = recAlertaSel?.tipo == "SOLICITUD_VENCIDA"
                        recAlertaSel = null
                        if (vencida) currentScreen = Screen.RECUPERAR_CORREO
                    },
                    onBack = {
                        recTokenSel = ""
                        recAlertaSel = null
                        currentScreen = Screen.LOGIN
                    }
                )

                Screen.RECUPERAR_LISTO -> ContrasenaActualizadaScreen(
                    onIrLogin = {
                        errorLoginSel = null
                        currentScreen = Screen.LOGIN
                    }
                )

                Screen.REGISTER -> RegisterScreen(
                    // La pantalla ya validó los campos (popups de DNI, teléfono, correo y contraseña)
                    onRegister = { nombre, dni, telefono, email, password, rol ->
                        if (!registrandoCuenta) {
                            registrandoCuenta = true
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    registrar(nombre, dni, telefono, email, password, rol)
                                    registroExitosoSel = true      // popup "¡Cuenta registrada!" (5 s) -> LOGIN
                                } catch (e: ApiException) {
                                    val mensaje = e.message ?: ""
                                    alertaRegistroSel = when {
                                        // 409: correo o DNI ya registrado (el mensaje del backend dice cuál)
                                        e.codigo == 409 && mensaje.contains("dni", ignoreCase = true) ->
                                            AlertaRegistro("DNI", mensaje)
                                        e.codigo == 409 -> AlertaRegistro("CORREO", mensaje)
                                        else -> AlertaRegistro("ERROR", mensaje.ifBlank { "Error al crear la cuenta" })
                                    }
                                } catch (e: Exception) {
                                    alertaRegistroSel = AlertaRegistro("ERROR", e.message ?: "Error al crear la cuenta")
                                } finally {
                                    registrandoCuenta = false
                                }
                            }
                        }
                    },
                    onBack = {
                        alertaRegistroSel = null
                        currentScreen = Screen.LOGIN
                    },
                    registrando = registrandoCuenta,
                    alertaServidor = alertaRegistroSel,
                    registroExitoso = registroExitosoSel,
                    onCerrarAlerta = { alertaRegistroSel = null },
                    onIrALogin = {
                        if (registroExitosoSel) {
                            registroExitosoSel = false
                            alertaRegistroSel = null
                            currentScreen = Screen.LOGIN
                        }
                    }
                )

                Screen.HOME_DOCENTE -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        if (datosDocenteCargados) return@LaunchedEffect   // ya cargado en esta sesión
                        try {
                            // Precarga de asignaciones y resumen del día (1 sola vez por sesión;
                            // el resumen se refresca al guardar una asistencia)
                            val asignaciones = obtenerAsignaciones()
                            aplicarAsignaciones(asignaciones)
                            actualizarResumen(obtenerResumenAsistenciasHoy(asignaciones))
                            datosDocenteCargados = true
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { /* silencioso */ }
                    }
                    HomeDocenteScreen(
                        usuarioNombre = usuarioLogueado?.nombrecompleto ?: "",
                        fotoUrl = usuarioLogueado?.fotoId?.let { urlArchivo(it) },
                        onAsistencias = {
                            currentScreen = Screen.ASISTENCIAS
                        },
                        onComunicados = {
                            comunicadosDocenteCargados = false   // entrar al módulo: datos frescos
                            currentScreen = Screen.COMUNICADOS
                        },
                        onPublicaciones = {
                            publicacionesCargadas = false
                            currentScreen = Screen.PUBLICACIONES
                        },
                        onAlumnos = {},
                        onAvisos = {
                            comunicadosDocenteCargados = false
                            currentScreen = Screen.COMUNICADOS
                        },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {}
                    )
                }

                Screen.HOME_PADRE -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        try {
                            // 1 petición: notificaciones sin leer para el punto rojo de la campana
                            val bandeja = obtenerBandeja()
                            notificacionesSel = bandeja.notificaciones
                            notificacionesNoLeidasSel = bandeja.noLeidas
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { /* silencioso */ }
                    }
                    HomePadreScreen(
                        usuarioNombre = usuarioLogueado?.nombrecompleto ?: "",
                        fotoUrl = usuarioLogueado?.fotoId?.let { urlArchivo(it) },
                        tieneNotificaciones = notificacionesNoLeidasSel > 0,
                        onAsistencias = {
                            hijosAsistenciaCargados = false
                            currentScreen = Screen.SELECCIONAR_ESTUDIANTE
                        },
                        onComunicados = { irAvisosPadre() },
                        onAgenda = { irAgenda() },
                        onEstadoAcademico = { aviso("Estado académico disponible próximamente") },
                        onAvisos = { irAvisosPadre() },
                        onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                        onNotificaciones = { abrirNotificaciones() }
                    )
                }

                Screen.ASISTENCIAS -> {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        // El resumen ya se calculó en HOME_DOCENTE (y se refresca al guardar una asistencia)
                        if (datosDocenteCargados) return@LaunchedEffect
                        try {
                            val asignaciones = asignacionesApiSel.ifEmpty {
                                obtenerAsignaciones().also { aplicarAsignaciones(it) }
                            }
                            actualizarResumen(obtenerResumenAsistenciasHoy(asignaciones))
                            datosDocenteCargados = true
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { /* silencioso */ }
                    }
                    AsistenciasScreen(
                        onBack = { currentScreen = Screen.HOME_DOCENTE },
                        onRegistrarAsistencia = { currentScreen = Screen.SELECCIONAR_SECCION },
                        onHistorial = { currentScreen = Screen.SELECCIONAR_SECCION_HISTORIAL },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = {
                            comunicadosDocenteCargados = false
                            currentScreen = Screen.COMUNICADOS
                        },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        realizados = resumenRealizadosSel,
                        pendientes = resumenPendientesSel,
                        porcentajeAsistencias = resumenPorcentajeSel
                    )
                }

                Screen.SELECCIONAR_SECCION -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        if (asignacionesApiSel.isNotEmpty()) {
                            // Ya cargadas en HOME_DOCENTE: solo reiniciar la selección
                            aplicarAsignaciones(asignacionesApiSel)
                            return@LaunchedEffect
                        }
                        try {
                            aplicarAsignaciones(obtenerAsignaciones())
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando datos: ${e.message}") }
                    }
                    SeleccionarSeccionScreen(
                        docenteId = usuarioLogueado?.id?.toString() ?: "",
                        listaGrados = listaGradosSel,
                        listaSecciones = listaSeccionesSel,
                        listaCursos = listaCursosSel,
                        cantidadAlumnos = cantidadAlumnosSel,

                        // Filtro LOCAL — sin peticiones al backend
                        onGradoSeleccionado = { grado ->
                            listaSeccionesSel = seccionesDeGrado(grado.id)
                            listaCursosSel = emptyList()
                            cantidadAlumnosSel = 0
                        },

                        // Filtro LOCAL — la cantidad de alumnos ya viene en la asignación
                        onSeccionSeleccionada = { seccion ->
                            listaCursosSel = cursosDeSeccion(seccion.id)
                            cantidadAlumnosSel = alumnosDeSeccion(seccion.id)
                            listaAlumnosSel = emptyList()  // la lista de alumnos se carga en REGISTRO_ASISTENCIA (Etapa 3)
                        },

                        onBack = {
                            currentScreen = Screen.ASISTENCIAS
                        },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = { currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onContinuar = { gradoId, seccionId, cursoId, grado, seccion, curso ->
                            gradoIdSel = gradoId; seccionIdSel = seccionId; cursoIdSel = cursoId
                            gradoSel = grado; seccionSel = seccion; cursoSel = curso
                            currentScreen = Screen.REGISTRO_ASISTENCIA
                        }
                    )
                }

                Screen.REGISTRO_ASISTENCIA -> {
                    var asistenciaExistente by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
                    // Hora del registro de hoy ("HH:mm:ss"): define el plazo de edición de 12 h
                    var horaRegistroHoy by remember { mutableStateOf<String?>(null) }
                    var cargandoHoja by remember { mutableStateOf(true) }
                    androidx.compose.runtime.LaunchedEffect(seccionIdSel, cursoIdSel) {
                        cargandoHoja = true
                        try {
                            // 1 petición: alumnos de la sección + asistencia de hoy (si ya se registró)
                            val hoja = obtenerHojaAsistencia(seccionIdSel, cursoIdSel)
                            fechaHojaSel = hoja.fecha
                            listaAlumnosSel = hoja.alumnos.map {
                                AlumnoItem(id = it.alumnoId, nombres = it.nombreCompleto, apellidos = "")
                            }
                            asistenciaExistente = if (hoja.yaRegistrado)
                                hoja.alumnos.mapNotNull { a -> a.estado?.let { a.alumnoId to it } }.toMap()
                            else emptyMap()
                            // La hora más temprana = cuando se registró la asistencia del día
                            horaRegistroHoy = if (hoja.yaRegistrado)
                                hoja.alumnos.mapNotNull { it.hora }.minOrNull()
                            else null
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            aviso("Error cargando alumnos: ${e.message}")
                        } finally {
                            cargandoHoja = false
                        }
                    }
                    RegistroAsistenciaScreen(
                        docenteId = usuarioLogueado?.id?.toString() ?: "",
                        gradoId = gradoIdSel,
                        seccionId = seccionIdSel,
                        cursoId = cursoIdSel,
                        grado = gradoSel,
                        seccion = seccionSel,
                        curso = cursoSel,
                        listaAlumnos = if (cargandoHoja) emptyList() else listaAlumnosSel,
                        asistenciaPrevia = asistenciaExistente,
                        horaRegistro = horaRegistroHoy,
                        fechaHoja = fechaHojaSel,
                        cargando = cargandoHoja,
                        onBack = { currentScreen = Screen.SELECCIONAR_SECCION },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = { currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onGuardado = { presentes, ausentes, tardanzas, estados, _, _ ->
                            val esActualizacion = asistenciaExistente.isNotEmpty()
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    if (esActualizacion) {
                                        // Solo los alumnos cuyo estado cambió (el backend audita, avisa al director
                                        // y notifica solo a los padres de esos alumnos)
                                        val cambios = estados.filter { (id, estado) ->
                                            estado.isNotEmpty() && asistenciaExistente[id] != estado
                                        }
                                        val respuesta = if (cambios.isNotEmpty())
                                            actualizarAsistenciaDia(
                                                seccionIdSel, cursoIdSel, fechaHojaSel,
                                                "Corrección desde el registro del día", cambios
                                            )
                                        else null
                                        cambiosAsistenciaSel = respuesta?.cambios?.map {
                                            CambioAsistenciaItem(it.alumno, it.estadoAnterior, it.estadoNuevo)
                                        } ?: emptyList()
                                    } else {
                                        registrarAsistencia(seccionIdSel, cursoIdSel, estados.filterValues { it.isNotEmpty() })
                                        cambiosAsistenciaSel = emptyList()
                                    }
                                    esActualizacionAsistenciaSel = esActualizacion
                                    // El backend ya envió el push a los padres: solo mostrar la confirmación
                                    presentesSel = presentes
                                    ausentesSel = ausentes
                                    tardanzasSel = tardanzas
                                    currentScreen = Screen.CONFIRMACION_ASISTENCIA
                                    // Refrescar el resumen del día
                                    try { actualizarResumen(obtenerResumenAsistenciasHoy(asignacionesApiSel)) } catch (e: Exception) { }
                                } catch (e: ApiException) {
                                    aviso(if (e.codigo == 409) "La asistencia de hoy ya fue registrada" else "No se pudo guardar: ${e.message}")
                                } catch (e: Exception) { aviso("No se pudo guardar: ${e.message}") }
                            }
                        }
                    )
                }

                Screen.CONFIRMACION_ASISTENCIA -> ConfirmacionAsistenciaScreen(
                    docenteNombre = usuarioLogueado?.nombrecompleto ?: "",
                    curso = cursoSel,
                    grado = gradoSel,
                    seccion = seccionSel,
                    totalPresentes = presentesSel,
                    totalAusentes = ausentesSel,
                    totalTardanzas = tardanzasSel,
                    fecha = fechaHojaSel,
                    esActualizacion = esActualizacionAsistenciaSel,
                    cambios = cambiosAsistenciaSel,
                    onNuevaAsistencia = { currentScreen = Screen.HOME_DOCENTE },
                    onVerHistorial = { currentScreen = Screen.SELECCIONAR_SECCION_HISTORIAL },
                    onClose = { currentScreen = Screen.HOME_DOCENTE }
                )

                Screen.SELECCIONAR_ESTUDIANTE -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        if (hijosAsistenciaCargados) return@LaunchedEffect   // al volver del historial
                        try {
                            val hijos = hijosDelPadre()
                            val hoy = java.time.LocalDate.now()
                            val desde = hoy.withDayOfMonth(1).toString()
                            // % de asistencia del mes de cada hijo (1 petición por hijo, en paralelo)
                            val porcentajes = coroutineScope {
                                hijos.map { h ->
                                    async {
                                        // A y T cuentan como asistencia
                                        val asistencias = obtenerAsistenciasHijo(h.alumnoId, desde, hoy.toString()).asistencias
                                        porcentajeAsistencia(asistencias.map { it.estado })
                                    }
                                }.awaitAll()
                            }
                            listaHijosSel = hijos.mapIndexed { i, h ->
                                HijoItem(
                                    id = h.alumnoId, nombres = h.nombres, apellidos = h.apellidos,
                                    gradoNombre = h.grado, seccionNombre = h.seccion,
                                    porcentajeMes = porcentajes[i]
                                )
                            }
                            hijosAsistenciaCargados = true
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando estudiantes: ${e.message}") }
                    }
                    SeleccionarEstudianteScreen(
                        padreId = usuarioLogueado?.id?.toString() ?: "",
                        padreNombre = usuarioLogueado?.nombrecompleto ?: "",
                        listaHijos = listaHijosSel,
                        onBack = { currentScreen = Screen.HOME_PADRE },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = { irAvisosPadre() },
                        onAgenda = { irAgenda() },
                        onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                        onNotificaciones = { abrirNotificaciones() },
                        onVerHistorial = { alumnoId, alumnoNombre ->
                            alumnoIdSel = alumnoId; alumnoNombreSel = alumnoNombre
                            historialHijoCargadoDe = null   // elegido desde la lista: datos frescos
                            currentScreen = Screen.HISTORIAL_ASISTENCIAS
                        }
                    )
                }

                Screen.HISTORIAL_ASISTENCIAS -> {
                    androidx.compose.runtime.LaunchedEffect(alumnoIdSel) {
                        // Al volver del detalle no se vuelve a pedir el historial
                        if (historialHijoCargadoDe == alumnoIdSel) return@LaunchedEffect
                        todasAsistenciasSel = emptyList()
                        listaCursosHistorialSel = emptyList()
                        try {
                            // Asistencias desde el inicio del año escolar en 1 petición
                            // (el calendario filtra por mes en la pantalla)
                            val hoy = java.time.LocalDate.now()
                            val historial = obtenerAsistenciasHijo(alumnoIdSel, inicioAnioEscolar(hoy).toString(), hoy.toString())
                            asistenciasHijoSel = historial.asistencias
                            listaCursosHistorialSel = historial.asistencias
                                .distinctBy { it.cursoId }
                                .map { CursoItem2(it.cursoId, it.curso) }
                                .sortedBy { it.nombre }
                            todasAsistenciasSel = historial.asistencias.map { a ->
                                AsistenciaItem(
                                    id = a.asistenciaId,
                                    alumnoId = alumnoIdSel,
                                    cursoId = a.cursoId,
                                    cursoNombre = a.curso,
                                    fecha = a.fecha,
                                    estado = a.estado
                                )
                            }
                            historialHijoCargadoDe = alumnoIdSel
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            aviso("Error cargando historial: ${e.message}")
                        }
                    }

                    HistorialAsistenciasScreen(
                        alumnoId = alumnoIdSel,
                        alumnoNombre = alumnoNombreSel,
                        todasAsistencias = todasAsistenciasSel,
                        listaCursos = listaCursosHistorialSel,
                        onBack = { currentScreen = Screen.SELECCIONAR_ESTUDIANTE },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = { irAvisosPadre() },
                        onAgenda = { irAgenda() },
                        onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                        onNotificaciones = { abrirNotificaciones() },
                        onVerDetalle = { asistenciaId, fecha ->
                            asistenciaIdSel = asistenciaId
                            fechaAsistenciaSel = fecha
                            currentScreen = Screen.DETALLE_ASISTENCIA
                        }
                    )
                }

                Screen.DETALLE_ASISTENCIA -> {
                    androidx.compose.runtime.LaunchedEffect(asistenciaIdSel) {
                        // Sin petición: el historial del hijo ya trae hora, curso y docente
                        val a = asistenciasHijoSel.find { it.asistenciaId == asistenciaIdSel } ?: return@LaunchedEffect
                        val hijo = listaHijosSel.find { it.id == alumnoIdSel }
                        detalleAsistenciaSel = DetalleAsistencia(
                            id = a.asistenciaId,
                            fecha = a.fecha,
                            hora = formatearHora(a.hora),
                            estado = a.estado,
                            cursoNombre = a.curso,
                            gradoNombre = hijo?.gradoNombre ?: "",
                            seccionNombre = hijo?.seccionNombre ?: "",
                            docenteNombre = a.docente
                        )
                    }

                    DetalleAsistenciaScreen(
                        asistenciaId = asistenciaIdSel,
                        alumnoNombre = alumnoNombreSel,
                        detalle = detalleAsistenciaSel,
                        onBack = { currentScreen = Screen.HISTORIAL_ASISTENCIAS },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = { irAvisosPadre() },
                        onAgenda = { irAgenda() },
                        onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                        onNotificaciones = { abrirNotificaciones() }
                    )
                }

                Screen.COMUNICADOS -> {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        try {
                            // Resumen del día: 1 petición (el backend ya trae notificados y leídos)
                            val hoy = java.time.LocalDate.now().toString()
                            val deHoy = comunicadosDelDocente().filter { fechaLocal(it.enviadoEn) == hoy }
                            totalHoySel = deHoy.size
                            totalEnviadosSel = deHoy.sumOf { it.totalNotificados }
                            sinLeerSel = deHoy.sumOf { (it.totalNotificados - it.totalLeidos).coerceAtLeast(0).toInt() }
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            aviso("Error cargando resumen: ${e.message}")
                        }
                    }
                    ComunicadosScreen(
                        docenteId = usuarioLogueado?.id?.toString() ?: "",
                        docenteNombre = usuarioLogueado?.nombrecompleto ?: "",
                        totalEnviados = totalEnviadosSel,
                        totalHoy = totalHoySel,
                        sinLeer = sinLeerSel,
                        onBack = { currentScreen = Screen.HOME_DOCENTE },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = { currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onNuevoComunicado = { currentScreen = Screen.NUEVO_COMUNICADO },
                        onHistorial = { currentScreen = Screen.SELECCIONAR_COMUNICADO }
                    )
                }

                Screen.NUEVO_COMUNICADO -> {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        try {
                            // Destinatarios = asignaciones del docente (ya traen nombres y cantidad de alumnos)
                            val asignaciones = asignacionesApiSel.ifEmpty {
                                obtenerAsignaciones().also { aplicarAsignaciones(it) }
                            }
                            listaDestinatariosSel = asignaciones.map {
                                SeccionDestinatario(
                                    gradoId = it.gradoId,
                                    seccionId = it.seccionId,
                                    cursoId = it.cursoId,
                                    gradoNombre = it.grado,
                                    seccionNombre = it.seccion,
                                    cursoNombre = it.curso,
                                    totalPadres = it.cantidadAlumnos.toInt()
                                )
                            }
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando destinatarios: ${e.message}") }
                    }
                    NuevoComunicadoScreen(
                        docenteId = usuarioLogueado?.id?.toString() ?: "",
                        docenteNombre = usuarioLogueado?.nombrecompleto ?: "",
                        listaDestinatarios = listaDestinatariosSel,
                        onBack = { currentScreen = Screen.COMUNICADOS },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        { currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onEnviado = { asunto, mensaje, adjunto, destinatario, curso, _, _, _ ->
                            if (enviandoComunicado) return@NuevoComunicadoScreen   // evita doble envío
                            // Sección + curso (una sección puede tener varios cursos del mismo docente)
                            val dest = listaDestinatariosSel.find {
                                "${it.gradoNombre} Sec. ${it.seccionNombre}" == destinatario && it.cursoNombre == curso
                            } ?: return@NuevoComunicadoScreen
                            enviandoComunicado = true
                            aviso(if (adjunto.isNotEmpty()) "Subiendo adjunto y enviando..." else "Enviando comunicado...")
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    // 1) Subir el adjunto (si hay) y obtener su id
                                    val archivoId = if (adjunto.isNotEmpty())
                                        subirArchivo(context.contentResolver, android.net.Uri.parse(adjunto)).id
                                    else null
                                    // 2) Enviar el comunicado: el backend notifica por push a los padres
                                    val enviado = enviarComunicado(dest.seccionId, dest.cursoId, asunto, mensaje, archivoId)
                                    comunicadoAsuntoSel = enviado.asunto
                                    comunicadoDestinatarioSel = destinatario
                                    comunicadoCursoSel = curso
                                    comunicadoNotificadosSel = enviado.totalNotificados
                                    comunicadoHoraSel = horaLocal(enviado.enviadoEn)
                                    comunicadoFechaSel = fechaLocal(enviado.enviadoEn)
                                    comunicadosDocenteCargados = false   // hay un comunicado nuevo
                                    currentScreen = Screen.CONFIRMACION_COMUNICADO
                                } catch (e: ApiException) {
                                    aviso(when (e.codigo) {
                                        413 -> "El archivo supera los 10 MB"
                                        415 -> "Tipo de archivo no permitido (PDF, Word, Excel, TXT o imagen)"
                                        else -> "Error al enviar: ${e.message}"
                                    })
                                } catch (e: Exception) {
                                    aviso("Error al enviar: ${e.message}")
                                } finally {
                                    enviandoComunicado = false
                                }
                            }
                        }
                    )
                }

                Screen.CONFIRMACION_COMUNICADO -> ConfirmacionComunicadoScreen(
                    docenteNombre = usuarioLogueado?.nombrecompleto ?: "",
                    curso = comunicadoCursoSel,
                    asunto = comunicadoAsuntoSel,
                    destinatario = comunicadoDestinatarioSel,
                    totalNotificados = comunicadoNotificadosSel,
                    hora = comunicadoHoraSel,
                    fecha = comunicadoFechaSel,
                    onNuevoComunicado = { currentScreen = Screen.NUEVO_COMUNICADO },
                    onVerHistorial = { currentScreen = Screen.SELECCIONAR_COMUNICADO },
                    onClose = { currentScreen = Screen.HOME_DOCENTE }
                )

                Screen.PERFIL_DOCENTE -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        try {
                            // 1 petición (antes: 1 + 3 consultas por cada asignación)
                            aplicarAsignaciones(obtenerAsignaciones())
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando asignaciones: ${e.message}") }
                    }
                    PerfilDocenteScreen(
                        usuarioNombre = usuarioLogueado?.nombrecompleto ?: "",
                        dni = usuarioLogueado?.dni ?: "",
                        telefono = usuarioLogueado?.telefono ?: "",
                        email = usuarioLogueado?.email ?: "",
                        asignaciones = asignacionesSel,
                        onBack = { currentScreen = Screen.HOME_DOCENTE },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = { currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onEditarNombre = {},
                        onEditarDni = {},
                        onEditarTelefono = {},
                        onEditarEmail = {},
                        onAgregarAsignacion = {
                            origenAgregarAsignacion = Screen.PERFIL_DOCENTE
                            currentScreen = Screen.AGREGAR_ASIGNACION
                        },
                        onGestionarAsignaciones = { currentScreen = Screen.MIS_ASIGNACIONES },
                        onEditarDatos = { currentScreen = Screen.EDITAR_DATOS_DOCENTE },
                        // Foto de perfil
                        fotoUrl = usuarioLogueado?.fotoId?.let { urlArchivo(it) },
                        onFotoElegida = { uri -> guardarFotoPerfil(uri) },
                        guardandoFoto = guardandoFoto,
                        fotoActualizada = fotoActualizada,
                        alertaFoto = alertaFotoSel,
                        onCerrarExitoFoto = { fotoActualizada = false },
                        onCerrarAlertaFoto = { alertaFotoSel = null },
                        onEliminarAsignacion = {},
                        onCambiarPassword = {},
                        onNotificacionesConfig = {},
                        onCerrarSesion = {
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    cerrarSesion()
                                } catch (e: Exception) { }
                                limpiarDatosSesion()
                                currentScreen = Screen.LOGIN
                            }
                        }
                    )
                }

                Screen.PERFIL_PADRE -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        try {
                            // 1 petición: el backend ya trae grado y sección de cada hijo
                            val hijos = obtenerHijos()
                            hijosApiSel = hijos
                            hijosAsociadosSel = hijos.map { it.aHijoAsociado() }
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando hijos: ${e.message}") }
                    }
                    PerfilPadreScreen(
                        usuarioNombre = usuarioLogueado?.nombrecompleto ?: "",
                        dni = usuarioLogueado?.dni ?: "",
                        telefono = usuarioLogueado?.telefono ?: "",
                        email = usuarioLogueado?.email ?: "",
                        hijosAsociados = hijosAsociadosSel,
                        onBack = { currentScreen = Screen.HOME_PADRE },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = { irAvisosPadre() },
                        onAgenda = { irAgenda() },
                        onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                        onEditarNombre = {},
                        onEditarDni = {},
                        onEditarTelefono = {},
                        onEditarEmail = {},
                        onAsociarHijo = {
                            origenAsociarHijo = Screen.PERFIL_PADRE
                            currentScreen = Screen.ASOCIAR_HIJO
                        },
                        onEliminarHijo = {},
                        onGestionarHijos = { currentScreen = Screen.MIS_HIJOS },
                        onEditarDatos = { currentScreen = Screen.EDITAR_DATOS_PADRE },
                        // Foto de perfil
                        fotoUrl = usuarioLogueado?.fotoId?.let { urlArchivo(it) },
                        onFotoElegida = { uri -> guardarFotoPerfil(uri) },
                        guardandoFoto = guardandoFoto,
                        fotoActualizada = fotoActualizada,
                        alertaFoto = alertaFotoSel,
                        onCerrarExitoFoto = { fotoActualizada = false },
                        onCerrarAlertaFoto = { alertaFotoSel = null },
                        onCambiarPassword = {},
                        onNotificacionesConfig = {},
                        onCerrarSesion = {
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    cerrarSesion()
                                } catch (e: Exception) { }
                                limpiarDatosSesion()
                                currentScreen = Screen.LOGIN
                            }
                        }
                    )
                }

                Screen.AGREGAR_ASIGNACION -> {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        if (gradosDisp.isNotEmpty()) return@LaunchedEffect  // catálogo ya cargado
                        try {
                            // Grados y cursos en paralelo
                            val (grados, cursos) = coroutineScope {
                                val g = async { obtenerGrados() }
                                val c = async { obtenerCursos() }
                                g.await() to c.await()
                            }
                            // Secciones de todos los grados en paralelo
                            val seccionesPorGrado = coroutineScope {
                                grados.map { grado -> async { grado to obtenerSeccionesDeGrado(grado.id) } }.awaitAll()
                            }
                            gradosDisp = grados.map { it.nombre }
                            seccionesDisp = seccionesPorGrado.associate { (grado, secciones) ->
                                grado.nombre to secciones.map { it.nombre }
                            }
                            seccionIdPorNombre = seccionesPorGrado.flatMap { (grado, secciones) ->
                                secciones.map { "${grado.nombre}|${it.nombre}" to it.id }
                            }.toMap()
                            cursosDisp = cursos.map { it.nombre }
                            cursoIdPorNombre = cursos.associate { it.nombre to it.id }
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando datos: ${e.message}") }
                    }
                    AgregarAsignacionScreen(
                        onBack = { currentScreen = origenAgregarAsignacion }, onNotificaciones = {},
                        gradosDisp = gradosDisp, seccionesDisp = seccionesDisp, cursosDisp = cursosDisp,
                        // Se llama después de que el docente confirma en el popup
                        onGuardar = { grado, seccion, curso ->
                            CoroutineScope(Dispatchers.Main).launch {
                                val seccionId = seccionIdPorNombre["$grado|$seccion"]
                                val cursoId = cursoIdPorNombre[curso]
                                if (seccionId == null || cursoId == null) {
                                    alertaAsignacionSel = AlertaAsignacion(
                                        "Selección no válida",
                                        "No se encontró el grado, la sección o el curso elegido. Vuelve a seleccionarlos."
                                    )
                                    return@launch
                                }
                                guardandoAsignacion = true
                                val inicio = System.currentTimeMillis()
                                try {
                                    val nueva = agregarAsignacion(seccionId, cursoId)
                                    aplicarAsignaciones(asignacionesApiSel + nueva)
                                    datosDocenteCargados = false   // el resumen del día debe incluirla
                                    // El popup de carga se ve al menos 1 s aunque el servidor responda antes
                                    val resto = 1000L - (System.currentTimeMillis() - inicio)
                                    if (resto > 0) delay(resto)
                                    asignacionExitosa = true
                                } catch (e: ApiException) {
                                    alertaAsignacionSel = if (e.codigo == 409) AlertaAsignacion(
                                        "Asignación ya registrada",
                                        "Ya tienes asignado $curso en $grado sección $seccion."
                                    ) else AlertaAsignacion("No se pudo registrar", e.message ?: "Error al guardar la asignación.")
                                } catch (e: Exception) {
                                    alertaAsignacionSel = AlertaAsignacion("No se pudo registrar", "Error al guardar: ${e.message}")
                                } finally {
                                    guardandoAsignacion = false
                                }
                            }
                        },
                        onCancelar = { currentScreen = origenAgregarAsignacion },
                        procesando = guardandoAsignacion,
                        exito = asignacionExitosa,
                        alerta = alertaAsignacionSel,
                        // Al cerrar el popup de éxito (X o 5 s) vuelve a la pantalla de origen con la nueva asignación
                        onCerrarExito = {
                            asignacionExitosa = false
                            currentScreen = origenAgregarAsignacion
                        },
                        onCerrarAlerta = { alertaAsignacionSel = null }
                    )
                }

                Screen.MIS_ASIGNACIONES -> MisAsignacionesScreen(
                    asignaciones = asignacionesApiSel
                        .sortedWith(compareBy({ it.grado }, { it.seccion }, { it.curso }))
                        .map { AsignacionGestion(it.id, it.grado, it.seccion, it.curso, it.cantidadAlumnos) },
                    onBack = { currentScreen = Screen.PERFIL_DOCENTE },
                    onNotificaciones = {},
                    onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                    onAvisos = { currentScreen = Screen.COMUNICADOS },
                    onPerfil = { currentScreen = Screen.PERFIL_DOCENTE },
                    onAgregar = {
                        origenAgregarAsignacion = Screen.MIS_ASIGNACIONES
                        currentScreen = Screen.AGREGAR_ASIGNACION
                    },
                    // Se llama después de que el docente confirma en el popup
                    onEliminar = { asignacion ->
                        CoroutineScope(Dispatchers.Main).launch {
                            eliminandoAsignacion = true
                            val inicio = System.currentTimeMillis()
                            try {
                                eliminarAsignacion(asignacion.id)
                                aplicarAsignaciones(asignacionesApiSel.filter { it.id != asignacion.id })
                                datosDocenteCargados = false   // el resumen del día ya no debe incluirla
                                // El popup de carga se ve al menos 1 s aunque el servidor responda antes
                                val resto = 1000L - (System.currentTimeMillis() - inicio)
                                if (resto > 0) delay(resto)
                                asignacionEliminadaSel = asignacion
                            } catch (e: ApiException) {
                                alertaEliminarSel = if (e.codigo == 404) {
                                    // Ya no existe en el servidor: se recarga la lista
                                    try { aplicarAsignaciones(obtenerAsignaciones()) } catch (_: Exception) { }
                                    AlertaAsignacion("Asignación no encontrada", "Esta asignación ya no existe. La lista se actualizó.")
                                } else AlertaAsignacion("No se pudo eliminar", e.message ?: "Error al eliminar la asignación.")
                            } catch (e: Exception) {
                                alertaEliminarSel = AlertaAsignacion("No se pudo eliminar", "Error al eliminar: ${e.message}")
                            } finally {
                                eliminandoAsignacion = false
                            }
                        }
                    },
                    procesando = eliminandoAsignacion,
                    eliminada = asignacionEliminadaSel,
                    alerta = alertaEliminarSel,
                    // Se queda en la lista (si quedó vacía, muestra "Sin asignaciones")
                    onCerrarExito = { asignacionEliminadaSel = null },
                    onCerrarAlerta = { alertaEliminarSel = null }
                )

                Screen.EDITAR_DATOS_DOCENTE -> EditarDatosScreen(
                    nombreActual = usuarioLogueado?.nombrecompleto ?: "",
                    telefonoActual = usuarioLogueado?.telefono ?: "",
                    dni = usuarioLogueado?.dni ?: "",
                    email = usuarioLogueado?.email ?: "",
                    onBack = { currentScreen = Screen.PERFIL_DOCENTE },
                    onNotificaciones = {},
                    // Se llama después de que el usuario confirma en el popup
                    onGuardar = { nombre, telefono -> guardarDatosPersonales(nombre, telefono) },
                    procesando = guardandoDatos,
                    exito = datosActualizados,
                    alerta = alertaDatosSel,
                    // Al cerrar el popup de éxito (X o 5 s) vuelve al perfil con los datos nuevos
                    onCerrarExito = {
                        datosActualizados = false
                        currentScreen = Screen.PERFIL_DOCENTE
                    },
                    onCerrarAlerta = { alertaDatosSel = null },
                    barraInferior = {
                        BottomNavBarDocente(
                            onInicio = { currentScreen = Screen.HOME_DOCENTE },
                            onAlumnos = {},
                            onAvisos = { currentScreen = Screen.COMUNICADOS },
                            onPerfil = { currentScreen = Screen.PERFIL_DOCENTE },
                            itemActivo = "Perfil"
                        )
                    }
                )

                Screen.ASOCIAR_HIJO -> AsociarHijoScreen(
                    onBack = { currentScreen = Screen.PERFIL_PADRE },
                    onHomeDocente = { currentScreen = Screen.HOME_PADRE },   // pantalla del padre
                    onAvisos = { irAvisosPadre() },
                    onAgenda = { irAgenda() },
                    onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                    onNotificaciones = { abrirNotificaciones() },
                    onBuscar = { codigo ->
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                errorBusquedaSel = ""; alumnoEncontradoSel = null
                                val resultado = buscarAlumno(codigo)
                                when {
                                    resultado == null ->
                                        errorBusquedaSel = "No se encontró ningún estudiante con el código $codigo"
                                    resultado.yaAsociado ->
                                        errorBusquedaSel = "${resultado.alumno.nombres} ${resultado.alumno.apellidos} ya está asociado a tu cuenta"
                                    else ->
                                        alumnoEncontradoSel = resultado.alumno.aAlumnoEncontrado()
                                }
                            } catch (e: Exception) { errorBusquedaSel = "Error al buscar: ${e.message}" }
                        }
                    },
                    alumnoEncontrado = alumnoEncontradoSel, mensajeError = errorBusquedaSel,
                    // Se llama después de que el padre confirma en el popup
                    onConfirmar = { alumno ->
                        CoroutineScope(Dispatchers.Main).launch {
                            asociandoHijo = true
                            val inicio = System.currentTimeMillis()
                            try {
                                val hijo = asociarHijo(alumno.codigoEstudiante)
                                hijosAsociadosSel = hijosAsociadosSel.filter { it.id != hijo.alumnoId } + hijo.aHijoAsociado()
                                hijosApiSel = hijosApiSel.orEmpty().filter { it.alumnoId != hijo.alumnoId } + hijo
                                // Los módulos del padre deben incluir al nuevo hijo
                                hijosAsistenciaCargados = false
                                comunicadosPadreCargados = false
                                agendaCargada = false
                                val resto = 1000L - (System.currentTimeMillis() - inicio)
                                if (resto > 0) delay(resto)
                                hijoAsociadoOk = true
                            } catch (e: ApiException) {
                                alertaAsociarSel = if (e.codigo == 409) AlertaHijo(
                                    "Hijo ya asociado",
                                    "${alumno.nombres} ${alumno.apellidos} ya está asociado a tu cuenta."
                                ) else AlertaHijo("No se pudo asociar", e.message ?: "Error al asociar al estudiante.")
                            } catch (e: Exception) {
                                alertaAsociarSel = AlertaHijo("No se pudo asociar", "Error al asociar: ${e.message}")
                            } finally {
                                asociandoHijo = false
                            }
                        }
                    },
                    onCancelar = { alumnoEncontradoSel = null; errorBusquedaSel = ""; currentScreen = origenAsociarHijo },
                    procesando = asociandoHijo,
                    exito = hijoAsociadoOk,
                    alerta = alertaAsociarSel,
                    // Al cerrar el popup de éxito (X o 5 s) vuelve a la pantalla de origen con el nuevo hijo
                    onCerrarExito = {
                        hijoAsociadoOk = false
                        alumnoEncontradoSel = null; errorBusquedaSel = ""
                        currentScreen = origenAsociarHijo
                    },
                    onCerrarAlerta = { alertaAsociarSel = null }
                )

                Screen.MIS_HIJOS -> MisHijosScreen(
                    hijos = hijosAsociadosSel,
                    onBack = { currentScreen = Screen.PERFIL_PADRE },
                    onNotificaciones = { abrirNotificaciones() },
                    onHomePadre = { currentScreen = Screen.HOME_PADRE },
                    onAvisos = { irAvisosPadre() },
                    onAgenda = { irAgenda() },
                    onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                    onAsociar = {
                        origenAsociarHijo = Screen.MIS_HIJOS
                        alumnoEncontradoSel = null; errorBusquedaSel = ""
                        currentScreen = Screen.ASOCIAR_HIJO
                    },
                    // Se llama después de que el padre confirma en el popup
                    onQuitar = { hijo ->
                        CoroutineScope(Dispatchers.Main).launch {
                            quitandoHijo = true
                            val inicio = System.currentTimeMillis()
                            try {
                                desasociarHijo(hijo.id)
                                hijosAsociadosSel = hijosAsociadosSel.filter { it.id != hijo.id }
                                hijosApiSel = hijosApiSel?.filter { it.alumnoId != hijo.id }
                                // Los módulos del padre ya no deben mostrar a este hijo
                                hijosAsistenciaCargados = false
                                comunicadosPadreCargados = false
                                agendaCargada = false
                                val resto = 1000L - (System.currentTimeMillis() - inicio)
                                if (resto > 0) delay(resto)
                                hijoQuitadoSel = hijo
                            } catch (e: ApiException) {
                                alertaQuitarSel = AlertaHijo("No se pudo quitar", e.message ?: "Error al quitar al estudiante.")
                            } catch (e: Exception) {
                                alertaQuitarSel = AlertaHijo("No se pudo quitar", "Error al quitar: ${e.message}")
                            } finally {
                                quitandoHijo = false
                            }
                        }
                    },
                    procesando = quitandoHijo,
                    quitado = hijoQuitadoSel,
                    alerta = alertaQuitarSel,
                    // Se queda en la lista (si quedó vacía, muestra "Sin hijos asociados")
                    onCerrarExito = { hijoQuitadoSel = null },
                    onCerrarAlerta = { alertaQuitarSel = null }
                )

                Screen.EDITAR_DATOS_PADRE -> EditarDatosScreen(
                    nombreActual = usuarioLogueado?.nombrecompleto ?: "",
                    telefonoActual = usuarioLogueado?.telefono ?: "",
                    dni = usuarioLogueado?.dni ?: "",
                    email = usuarioLogueado?.email ?: "",
                    onBack = { currentScreen = Screen.PERFIL_PADRE },
                    onNotificaciones = { abrirNotificaciones() },
                    onGuardar = { nombre, telefono -> guardarDatosPersonales(nombre, telefono) },
                    procesando = guardandoDatos,
                    exito = datosActualizados,
                    alerta = alertaDatosSel,
                    onCerrarExito = {
                        datosActualizados = false
                        currentScreen = Screen.PERFIL_PADRE
                    },
                    onCerrarAlerta = { alertaDatosSel = null },
                    barraInferior = {
                        BottomNavBarPadre(
                            onInicio = { currentScreen = Screen.HOME_PADRE },
                            onAvisos = { irAvisosPadre() },
                            onAgenda = { irAgenda() },
                            onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                            itemActivo = "Perfil"
                        )
                    }
                )

                Screen.SELECCIONAR_COMUNICADO -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        try {
                            // Sin peticiones si se viene de COMUNICADOS (asignaciones y comunicados ya cargados)
                            val asignaciones = asignacionesApiSel.ifEmpty {
                                obtenerAsignaciones().also { aplicarAsignaciones(it) }
                            }
                            val porSeccion = comunicadosDelDocente().groupingBy { it.seccionId }.eachCount()
                            seccionesComunicadoSel = asignaciones.map { a ->
                                SeccionComunicadoItem(
                                    seccionId = a.seccionId,
                                    gradoNombre = a.grado,
                                    seccionNombre = a.seccion,
                                    cursoNombre = a.curso,
                                    cantidadAlumnos = a.cantidadAlumnos.toInt(),
                                    cantidadComunicados = porSeccion[a.seccionId] ?: 0
                                )
                            }
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando secciones: ${e.message}") }
                    }
                    SeleccionarComunicadoScreen(
                        secciones = seccionesComunicadoSel,
                        onBack = { currentScreen = Screen.COMUNICADOS },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = { currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onSeleccionarSeccion = { seccionId, gradoNombre, seccionNombre, cursoNombre ->
                            seccionIdSel = seccionId
                            gradoSel = gradoNombre
                            seccionSel = seccionNombre
                            cursoSel = cursoNombre
                            currentScreen = Screen.BUSQUEDA_COMUNICADO
                        }
                    )
                }

                Screen.BUSQUEDA_COMUNICADO -> {
                    androidx.compose.runtime.LaunchedEffect(seccionIdSel) {
                        try {
                            // Filtro LOCAL: los comunicados ya se cargaron (traen notificados y leídos)
                            val comunicados = comunicadosDelDocente().filter { it.seccionId == seccionIdSel }
                            comunicadosDocenteSel = comunicados
                            comunicadosListaSel = comunicados.map { com ->
                                ComunicadoResumenItem(
                                    id = com.id,
                                    asunto = com.asunto,
                                    mensaje = com.mensaje,
                                    fecha = fechaLocal(com.enviadoEn),
                                    totalNotificados = com.totalNotificados,
                                    totalLeidos = com.totalLeidos.toInt()
                                )
                            }.sortedByDescending { it.fecha }
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando comunicados: ${e.message}") }
                    }
                    BusquedaComunicadoScreen(
                        gradoNombre = gradoSel,
                        seccionNombre = seccionSel,
                        cursoNombre = cursoSel,
                        comunicados = comunicadosListaSel,
                        onBack = { currentScreen = Screen.SELECCIONAR_COMUNICADO },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = {currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onVerDetalle = { comunicadoId ->
                            comunicadoIdSel = comunicadoId
                            currentScreen = Screen.DETALLE_COMUNICADO
                        }
                    )
                }

                Screen.DETALLE_COMUNICADO -> {
                    androidx.compose.runtime.LaunchedEffect(comunicadoIdSel) {
                        // Mostrar de inmediato lo que ya se tiene de la lista
                        comunicadosDocenteSel.find { it.id == comunicadoIdSel }?.let { c ->
                            comunicadoAsuntoSel = c.asunto
                            comunicadoMensajeSel = c.mensaje
                            comunicadoTotalNotifSel = c.totalNotificados
                            comunicadoHoraSel = horaLocal(c.enviadoEn)
                        }
                        padresLecturaSel = emptyList()
                        try {
                            // 1 petición: padres que leyeron y que no, con sus hijos
                            val detalle = obtenerDetalleComunicadoDocente(comunicadoIdSel)
                            comunicadoAsuntoSel = detalle.comunicado.asunto
                            comunicadoMensajeSel = detalle.comunicado.mensaje
                            comunicadoTotalNotifSel = detalle.comunicado.totalNotificados
                            comunicadoHoraSel = horaLocal(detalle.comunicado.enviadoEn)
                            padresLecturaSel = detalle.lecturas.map { l ->
                                PadreLecturaItem(
                                    padreId = l.padreId.toString(),
                                    nombrePadre = l.padre,
                                    nombreHijo = l.hijos.joinToString(", "),
                                    leidoEn = if (l.leido) (isoLocalSinZona(l.leidoEn) ?: "") else null
                                )
                            }
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando detalle: ${e.message}") }
                    }
                    DetalleComunicadoScreen(
                        asunto = comunicadoAsuntoSel,
                        mensaje = comunicadoMensajeSel,
                        fecha = comunicadosListaSel.find { it.id == comunicadoIdSel }?.fecha ?: "",
                        hora = comunicadoHoraSel,
                        gradoNombre = gradoSel,
                        seccionNombre = seccionSel,
                        cursoNombre = cursoSel,
                        totalNotificados = comunicadoTotalNotifSel,
                        padres = padresLecturaSel,
                        onBack = { currentScreen = Screen.BUSQUEDA_COMUNICADO },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = {currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onReenviar = {
                            // Pendiente: el backend aún no tiene endpoint de reenvío
                            aviso("Reenvío disponible próximamente")
                        }
                    )
                }

                Screen.PUBLICACIONES -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        if (publicacionesCargadas) return@LaunchedEffect   // al volver de la selección de curso
                        try {
                            // 1 petición: todas las publicaciones del docente
                            publicacionesDocenteSel = obtenerPublicacionesDocente()
                            publicacionesCargadas = true
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando publicaciones: ${e.message}") }
                    }
                    // Resumen (local): tareas y exámenes vigentes, y los que vencen hoy
                    val vigentes = publicacionesDocenteSel.filter { it.estado != "ANULADA" }
                    val hoy = java.time.LocalDate.now().toString()
                    PublicacionesScreen(
                        totalTareas = vigentes.count { it.tipo == "TAREA" },
                        totalEvaluaciones = vigentes.count { it.tipo == "EVALUACION" },
                        venceHoy = vigentes.count { it.fechaEntrega == hoy },
                        totalPublicacionesHoy = vigentes.count { fechaLocal(it.publicadoEn) == hoy },
                        onBack = { currentScreen = Screen.HOME_DOCENTE },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = {
                            comunicadosDocenteCargados = false
                            currentScreen = Screen.COMUNICADOS
                        },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onNuevaTarea = {
                            tipoPublicacionSel = "Tarea"
                            currentScreen = Screen.SELECCIONAR_CURSO_PUBLICACION
                        },
                        onNuevaEvaluacion = {
                            tipoPublicacionSel = "Evaluación"
                            currentScreen = Screen.SELECCIONAR_CURSO_PUBLICACION
                        },
                        onHistorial = { currentScreen = Screen.HISTORIAL_PUBLICACIONES }
                    )
                }

                Screen.SELECCIONAR_CURSO_PUBLICACION -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        try {
                            // Sin peticiones si las asignaciones ya están cargadas (traen nombres y alumnos)
                            val asignaciones = asignacionesApiSel.ifEmpty {
                                obtenerAsignaciones().also { aplicarAsignaciones(it) }
                            }
                            seccionesPublicacionSel = asignaciones.map { a ->
                                SeccionComunicadoItem(
                                    seccionId = a.seccionId,
                                    gradoNombre = a.grado,
                                    seccionNombre = a.seccion,
                                    cursoNombre = a.curso,
                                    cantidadAlumnos = a.cantidadAlumnos.toInt(),
                                    cantidadComunicados = 0
                                )
                            }
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando cursos: ${e.message}") }
                    }
                    SeleccionarCursoPublicacionScreen(
                        tipo = tipoPublicacionSel,
                        secciones = seccionesPublicacionSel,
                        onBack = { currentScreen = Screen.PUBLICACIONES },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = {
                            comunicadosDocenteCargados = false
                            currentScreen = Screen.COMUNICADOS
                        },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onSeleccionar = { seccion ->
                            // La tarjeta no trae el id del curso: se busca la asignación por sección y curso
                            val asignacion = asignacionesApiSel.find {
                                it.seccionId == seccion.seccionId && it.curso == seccion.cursoNombre
                            }
                            if (asignacion == null) {
                                aviso("No se encontró la asignación, vuelve a intentarlo")
                            } else {
                                asignacionPublicacionSel = asignacion
                                currentScreen = if (tipoPublicacionSel == "Tarea") Screen.NUEVA_TAREA else Screen.NUEVA_EVALUACION
                            }
                        }
                    )
                }

                Screen.NUEVA_TAREA -> {
                    val asignacion = asignacionPublicacionSel
                    if (asignacion != null) {
                        NuevaTareaScreen(
                            listaDestinatarios = listOf(
                                SeccionDestinatario(
                                    gradoId = asignacion.gradoId,
                                    seccionId = asignacion.seccionId,
                                    cursoId = asignacion.cursoId,
                                    gradoNombre = asignacion.grado,
                                    seccionNombre = asignacion.seccion,
                                    cursoNombre = asignacion.curso,
                                    totalPadres = asignacion.cantidadAlumnos.toInt()
                                )
                            ),
                            cursoNombre = asignacion.curso,
                            onBack = { currentScreen = Screen.SELECCIONAR_CURSO_PUBLICACION },
                            onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                            onAlumnos = {},
                            onAvisos = {
                                comunicadosDocenteCargados = false
                                currentScreen = Screen.COMUNICADOS
                            },
                            onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                            onNotificaciones = {},
                            onPublicar = { titulo, descripcion, adjunto, seccion, fechaEntrega, _, hora ->
                                publicar("TAREA", titulo, descripcion, adjunto, seccion, fechaEntrega, hora)
                            }
                        )
                    }
                }

                Screen.NUEVA_EVALUACION -> {
                    val asignacion = asignacionPublicacionSel
                    if (asignacion != null) {
                        NuevaEvaluacionScreen(
                            listaDestinatarios = listOf(
                                SeccionDestinatario(
                                    gradoId = asignacion.gradoId,
                                    seccionId = asignacion.seccionId,
                                    cursoId = asignacion.cursoId,
                                    gradoNombre = asignacion.grado,
                                    seccionNombre = asignacion.seccion,
                                    cursoNombre = asignacion.curso,
                                    totalPadres = asignacion.cantidadAlumnos.toInt()
                                )
                            ),
                            cursoNombre = asignacion.curso,
                            onBack = { currentScreen = Screen.SELECCIONAR_CURSO_PUBLICACION },
                            onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                            onAlumnos = {},
                            onAvisos = {
                                comunicadosDocenteCargados = false
                                currentScreen = Screen.COMUNICADOS
                            },
                            onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                            onNotificaciones = {},
                            onPublicar = { titulo, descripcion, adjunto, seccion, fechaEvaluacion, _, hora ->
                                publicar("EVALUACION", titulo, descripcion, adjunto, seccion, fechaEvaluacion, hora)
                            }
                        )
                    }
                }

                Screen.CONFIRMACION_PUBLICACION -> ConfirmacionPublicacionScreen(
                    tipo = tipoPublicacionSel,
                    titulo = tituloPublicacionSel,
                    gradoNombre = asignacionPublicacionSel?.grado ?: "",
                    seccionNombre = asignacionPublicacionSel?.seccion ?: "",
                    cursoNombre = asignacionPublicacionSel?.curso ?: "",
                    totalNotificados = publicacionNotificadosSel,
                    fechaEntrega = fechaEntregaSel,
                    hora = horaPublicacionSel,
                    onNuevaPublicacion = {
                        currentScreen = Screen.SELECCIONAR_CURSO_PUBLICACION
                    },
                    onVerHistorial = { currentScreen = Screen.HISTORIAL_PUBLICACIONES },
                    onVolver = { currentScreen = Screen.PUBLICACIONES }
                )

                Screen.HISTORIAL_PUBLICACIONES -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        if (publicacionesCargadas) return@LaunchedEffect   // ya cargadas en PUBLICACIONES
                        try {
                            publicacionesDocenteSel = obtenerPublicacionesDocente()
                            publicacionesCargadas = true
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando publicaciones: ${e.message}") }
                    }
                    HistorialPublicacionesScreen(
                        // Sin peticiones extra: la lista ya trae alumnos, calificados y lecturas
                        publicaciones = publicacionesDocenteSel
                            .filter { it.tipo == "TAREA" || it.tipo == "EVALUACION" }
                            .sortedByDescending { aFechaHoraLocal(it.publicadoEn) }
                            .map { p ->
                                PublicacionHistorialItem(
                                    id = p.id,
                                    tipo = if (p.tipo == "EVALUACION") "Evaluación" else "Tarea",
                                    titulo = p.titulo,
                                    cursoNombre = p.curso,
                                    gradoNombre = p.grado,
                                    seccionNombre = p.seccion,
                                    fechaEntrega = p.fechaEntrega ?: "",
                                    fechaPublicacion = fechaLocal(p.publicadoEn),
                                    totalAlumnos = p.totalAlumnos.toInt(),
                                    calificados = p.calificados.toInt(),
                                    lecturas = p.lecturas.toInt(),
                                    estado = p.estado
                                )
                            },
                        cargando = !publicacionesCargadas,
                        onBack = { currentScreen = Screen.PUBLICACIONES },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = {
                            comunicadosDocenteCargados = false
                            currentScreen = Screen.COMUNICADOS
                        },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onVerDetalle = { item ->
                            publicacionIdSel = item.id
                            currentScreen = Screen.CALIFICAR_PUBLICACION
                        }
                    )
                }

                Screen.CALIFICAR_PUBLICACION -> {
                    androidx.compose.runtime.LaunchedEffect(publicacionIdSel) {
                        if (detallePublicacionSel?.publicacion?.id != publicacionIdSel) detallePublicacionSel = null
                        cargandoDetallePublicacion = true
                        try {
                            // 1 petición: la publicación con la calificación de cada alumno de la sección
                            detallePublicacionSel = obtenerDetallePublicacionDocente(publicacionIdSel)
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            aviso("Error cargando la publicación: ${e.message}")
                        } finally {
                            cargandoDetallePublicacion = false
                        }
                    }
                    val detalle = detallePublicacionSel?.takeIf { it.publicacion.id == publicacionIdSel }
                    CalificarPublicacionScreen(
                        publicacion = detalle?.publicacion?.let { p ->
                            DetallePublicacionItem(
                                titulo = p.titulo,
                                tipo = if (p.tipo == "EVALUACION") "Evaluación" else "Tarea",
                                cursoNombre = p.curso,
                                gradoNombre = p.grado,
                                seccionNombre = p.seccion,
                                fechaEntrega = p.fechaEntrega ?: "",
                                descripcion = p.descripcion,
                                nombreAdjunto = p.archivo?.nombre,
                                estado = p.estado
                            )
                        },
                        alumnos = detalle?.calificaciones.orEmpty()
                            .sortedBy { it.nombreCompleto }
                            .map { c ->
                                AlumnoCalificacionItem(
                                    alumnoId = c.alumnoId,
                                    codigo = c.codigoEstudiante,
                                    nombreCompleto = c.nombreCompleto,
                                    estado = c.estado,
                                    notaNumerica = c.notaNumerica,
                                    notaLiteral = c.notaLiteral,
                                    observacion = c.observacion ?: "",
                                    registrado = c.calificacionId != null
                                )
                            },
                        cargando = cargandoDetallePublicacion,
                        guardando = guardandoCalificaciones,
                        descargandoAdjunto = descargandoAdjunto,
                        onAbrirAdjunto = { detalle?.publicacion?.archivo?.let { abrirAdjunto(it) } },
                        onBack = { currentScreen = Screen.HISTORIAL_PUBLICACIONES },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = {
                            comunicadosDocenteCargados = false
                            currentScreen = Screen.COMUNICADOS
                        },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onGuardar = { cambios ->
                            if (guardandoCalificaciones) return@CalificarPublicacionScreen   // evita doble envío
                            guardandoCalificaciones = true
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    // Solo los alumnos que cambiaron; el backend notifica a sus padres
                                    val respuesta = registrarCalificaciones(
                                        publicacionIdSel,
                                        cambios.map { c ->
                                            CalificacionRequest(
                                                alumnoId = c.alumnoId,
                                                estado = c.estado,
                                                notaNumerica = c.notaNumerica,
                                                notaLiteral = c.notaLiteral,
                                                observacion = c.observacion.ifBlank { null }
                                            )
                                        }
                                    )
                                    detallePublicacionSel = respuesta
                                    // Actualizar "calificados" en el historial sin volver a pedirlo
                                    publicacionesDocenteSel = publicacionesDocenteSel.map {
                                        if (it.id == respuesta.publicacion.id) respuesta.publicacion else it
                                    }
                                    // El mensaje de éxito lo muestra la pantalla (popup "Calificación Guardada")
                                } catch (e: ApiException) {
                                    aviso(if (e.codigo == 400) "Datos no válidos: ${e.message}" else "No se pudo guardar: ${e.message}")
                                } catch (e: Exception) {
                                    aviso("No se pudo guardar: ${e.message}")
                                } finally {
                                    guardandoCalificaciones = false
                                }
                            }
                        }
                    )
                }

                Screen.SELECCIONAR_HIJO_AGENDA -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        if (agendaCargada) return@LaunchedEffect   // al volver de la agenda de un hijo
                        try {
                            // 1 petición por hijo, en paralelo (antes: 4 consultas + 1 por publicación)
                            cargarAgendaPadre()
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando agenda: ${e.message}") }
                    }
                    SeleccionarHijoAgendaScreen(
                        hijos = hijosAgendaSel,
                        onBack = { currentScreen = Screen.HOME_PADRE },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = { irAvisosPadre() },
                        onAgenda = { irAgenda() },
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = { abrirNotificaciones() },
                        onVerAgenda = { hijo ->
                            hijoAgendaSel = hijo
                            currentScreen = Screen.AGENDA_ESCOLAR
                        }
                    )
                }

                Screen.AGENDA_ESCOLAR -> {
                    androidx.compose.runtime.LaunchedEffect(hijoAgendaSel?.id, agendaPorHijoSel) {
                        val hijo = hijoAgendaSel ?: return@LaunchedEffect
                        // Sin peticiones: la agenda de cada hijo ya se cargó en SELECCIONAR_HIJO_AGENDA
                        val hoy = java.time.LocalDate.now().toString()
                        publicacionesAgendaSel = agendaPorHijoSel[hijo.id].orEmpty()
                            .map { it.aPublicacionAgendaItem(hoy) }
                    }
                    AgendaEscolarScreen(
                        nombreHijo = hijoAgendaSel?.nombres ?: "",
                        gradoNombre = hijoAgendaSel?.gradoNombre ?: "",
                        seccionNombre = hijoAgendaSel?.seccionNombre ?: "",
                        publicaciones = publicacionesAgendaSel,
                        onBack = { currentScreen = Screen.SELECCIONAR_HIJO_AGENDA },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = { irAvisosPadre() },
                        onAgenda = { irAgenda() },
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = { abrirNotificaciones() },
                        onVerDetalle = { publicacion ->
                            publicacionAgendaSel = publicacion
                            currentScreen = Screen.DETALLE_AGENDA
                        }
                    )
                }

                Screen.DETALLE_AGENDA -> {
                    androidx.compose.runtime.LaunchedEffect(publicacionAgendaSel?.id) {
                        val publicacion = publicacionAgendaSel ?: return@LaunchedEffect
                        if (publicacion.id in publicacionesLeidasSel) return@LaunchedEffect   // ya estaba leída
                        try {
                            marcarPublicacionLeida(publicacion.id)
                            publicacionesLeidasSel = publicacionesLeidasSel + publicacion.id
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { /* se reintentará al abrirla de nuevo */ }
                    }
                    val adjunto = publicacionAgendaSel?.let { adjuntosPublicacionSel[it.id] }
                    DetalleAgendaScreen(
                        nombreHijo = hijoAgendaSel?.nombres ?: "",
                        gradoNombre = hijoAgendaSel?.gradoNombre ?: "",
                        seccionNombre = hijoAgendaSel?.seccionNombre ?: "",
                        publicacion = publicacionAgendaSel,
                        descargandoAdjunto = descargandoAdjunto,
                        onAbrirAdjunto = { adjunto?.let { abrirAdjunto(it) } },
                        onBack = { currentScreen = Screen.AGENDA_ESCOLAR },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = { irAvisosPadre() },
                        onAgenda = { irAgenda() },
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = { abrirNotificaciones() }
                    )
                }

                Screen.COMUNICADOS_PADRE -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        try {
                            if (!comunicadosPadreCargados) cargarComunicadosPadre()
                            sinLeerPadreSel = hijosComunicadoSel.sumOf { it.sinLeer }
                            totalPadreSel = hijosComunicadoSel.sumOf { it.total }
                            leidosPadreSel = totalPadreSel - sinLeerPadreSel
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error: ${e.message}") }
                    }
                    ComunicadosPadresScreen(
                        sinLeer = sinLeerPadreSel,
                        leidos = leidosPadreSel,
                        total = totalPadreSel,
                        onBack = { currentScreen = Screen.HOME_PADRE },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = { irAvisosPadre() },
                        onAgenda = { irAgenda() },
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = { abrirNotificaciones() },
                        onVerRecibidos = { currentScreen = Screen.SELECCIONAR_ESTUDIANTE_COMUNICADO },
                        onVerHistorial = { currentScreen = Screen.SELECCIONAR_ESTUDIANTE_COMUNICADO }
                    )
                }

                Screen.SELECCIONAR_ESTUDIANTE_COMUNICADO -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        try {
                            // Ya cargados en COMUNICADOS_PADRE: solo se piden si se llega por otro camino
                            if (!comunicadosPadreCargados) cargarComunicadosPadre()
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error: ${e.message}") }
                    }
                    SeleccionarEstudianteComunicadoScreen(
                        hijos = hijosComunicadoSel,
                        onBack = { currentScreen = Screen.COMUNICADOS_PADRE },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = { irAvisosPadre() },
                        onAgenda = { irAgenda() },
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = { abrirNotificaciones() },
                        onVerComunicados = { hijo ->
                            hijoComunicadoSel = hijo
                            comunicadosHijoCargadoDe = null   // elegido desde la lista: datos frescos
                            currentScreen = Screen.COMUNICADOS_RECIBIDOS
                        }
                    )
                }

                Screen.COMUNICADOS_RECIBIDOS -> {
                    androidx.compose.runtime.LaunchedEffect(hijoComunicadoSel?.id) {
                        val hijo = hijoComunicadoSel ?: return@LaunchedEffect
                        // Al volver del detalle no se vuelve a pedir (la lectura ya se actualizó localmente)
                        if (comunicadosHijoCargadoDe == hijo.id) return@LaunchedEffect
                        try {
                            // 1 petición: comunicados del hijo con su estado de lectura y adjunto
                            val respuesta = obtenerComunicadosHijo(hijo.id)
                            comunicadosPorHijoSel = comunicadosPorHijoSel + (hijo.id to respuesta.comunicados)
                            aplicarComunicadosHijo(hijo, respuesta.comunicados)
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error: ${e.message}") }
                    }
                    ComunicadosRecibidosScreen(
                        nombreHijo = hijoComunicadoSel?.nombres ?: "",
                        gradoNombre = hijoComunicadoSel?.gradoNombre ?: "",
                        seccionNombre = hijoComunicadoSel?.seccionNombre ?: "",
                        comunicados = comunicadosPadreSel,
                        onBack = { currentScreen = Screen.SELECCIONAR_ESTUDIANTE_COMUNICADO },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = { irAvisosPadre() },
                        onAgenda = { irAgenda() },
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = { abrirNotificaciones() },
                        onVerDetalle = { comunicado ->
                            comunicadoPadreSel = comunicado
                            currentScreen = Screen.DETALLE_COMUNICADO_PADRE
                        }
                    )
                }

                Screen.DETALLE_COMUNICADO_PADRE -> {
                    androidx.compose.runtime.LaunchedEffect(comunicadoPadreSel?.id) {
                        val comunicado = comunicadoPadreSel ?: return@LaunchedEffect
                        if (comunicado.leido) return@LaunchedEffect   // ya estaba leído
                        try {
                            marcarComunicadoLeido(comunicado.id)
                            val ahora = java.time.LocalDateTime.now().withNano(0).toString()
                            val actualizado = comunicado.copy(leido = true, leidoEn = ahora)
                            comunicadoPadreSel = actualizado
                            comunicadosPadreSel = comunicadosPadreSel.map { if (it.id == comunicado.id) actualizado else it }
                            // Un "sin leer" menos en la tarjeta del hijo (sin volver a pedir los datos)
                            hijoComunicadoSel?.let { h ->
                                val hijoActualizado = h.copy(sinLeer = (h.sinLeer - 1).coerceAtLeast(0))
                                hijoComunicadoSel = hijoActualizado
                                hijosComunicadoSel = hijosComunicadoSel.map { if (it.id == h.id) hijoActualizado else it }
                            }
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { /* se reintentará al abrirlo de nuevo */ }
                    }
                    val adjunto = comunicadoPadreSel?.let { adjuntosComunicadoSel[it.id] }
                    DetalleComunicadoPadreScreen(
                        comunicado = comunicadoPadreSel,
                        nombreAdjunto = adjunto?.nombre,
                        descargandoAdjunto = descargandoAdjunto,
                        onAbrirAdjunto = { adjunto?.let { abrirAdjunto(it) } },
                        onBack = { currentScreen = Screen.COMUNICADOS_RECIBIDOS },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = { irAvisosPadre() },
                        onAgenda = { irAgenda() },
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = { abrirNotificaciones() }
                    )
                }

                Screen.SELECCIONAR_SECCION_HISTORIAL -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        // Limpiar historial previo para que no se muestre al volver con otro curso
                        historialDocenteSel = emptyList()
                        historialDocenteCargado = false
                        if (asignacionesApiSel.isNotEmpty()) {
                            // Ya cargadas — solo restablecer las listas de selección
                            aplicarAsignaciones(asignacionesApiSel)
                            return@LaunchedEffect
                        }
                        try {
                            aplicarAsignaciones(obtenerAsignaciones())
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            aviso("Error cargando grados: ${e.message}")
                        }
                    }
                    SeleccionarSeccionHistorialScreen(
                        docenteId = usuarioLogueado?.id?.toString() ?: "",
                        listaGrados = listaGradosSel,
                        listaSecciones = listaSeccionesSel,
                        listaCursos = listaCursosSel,
                        cantidadAlumnos = cantidadAlumnosSel,
                        // Filtro LOCAL — sin peticiones al backend
                        onGradoSeleccionado = { grado ->
                            listaSeccionesSel = seccionesDeGrado(grado.id)
                            listaCursosSel = emptyList()
                            cantidadAlumnosSel = 0
                        },
                        // Filtro LOCAL — la cantidad de alumnos ya viene en la asignación
                        onSeccionSeleccionada = { seccion ->
                            listaCursosSel = cursosDeSeccion(seccion.id)
                            cantidadAlumnosSel = alumnosDeSeccion(seccion.id)
                        },
                        onBack = {
                            currentScreen = Screen.ASISTENCIAS
                        },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = { currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onContinuar = { gradoId, seccionId, cursoId, grado, seccion, curso ->
                            gradoIdSel   = gradoId
                            seccionIdSel = seccionId
                            cursoIdSel   = cursoId
                            gradoSel     = grado
                            seccionSel   = seccion
                            cursoSel     = curso
                            // Cargar historial primero y navegar solo cuando los datos están listos
                            historialDocenteSel = emptyList()
                            historialDocenteCargado = false
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    historialDocenteSel = cargarHistorialDocente(seccionId, cursoId, grado, seccion, curso)
                                    historialDocenteCargado = true   // aunque esté vacío: no volver a pedirlo
                                } catch (e: Exception) {
                                    aviso("Error cargando historial: ${e.message}")
                                }
                                // Navegar DESPUÉS de que los datos ya están en historialDocenteSel
                                currentScreen = Screen.HISTORIAL_ASISTENCIA_DOCENTE
                            }
                        }
                    )
                }

                Screen.HISTORIAL_ASISTENCIA_DOCENTE -> {
                    androidx.compose.runtime.LaunchedEffect(cursoIdSel, seccionIdSel) {
                        if (historialDocenteCargado) return@LaunchedEffect  // ya precargado desde la selección
                        try {
                            historialDocenteSel = cargarHistorialDocente(seccionIdSel, cursoIdSel, gradoSel, seccionSel, cursoSel)
                            historialDocenteCargado = true
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            aviso("Error cargando historial: ${e.message}")
                        }
                    }

                    HistorialAsistenciaScreen(
                        curso    = cursoSel,
                        grado    = gradoSel,
                        seccion  = seccionSel,
                        historial = historialDocenteSel,
                        onBack = { currentScreen = Screen.SELECCIONAR_SECCION_HISTORIAL },
                        onHomeDocente    = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos        = {},
                        onAvisos         = { currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente  = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onVerDetalle     = { item ->
                            historialItemSel = item
                            currentScreen = Screen.DETALLE_ASISTENCIA_DOCENTE
                        }
                    )
                }

                Screen.DETALLE_ASISTENCIA_DOCENTE -> {
                    androidx.compose.runtime.LaunchedEffect(historialItemSel?.fecha) {
                        val fecha = historialItemSel?.fecha ?: return@LaunchedEffect
                        try {
                            // 1 petición: la hoja de ese día ya trae nombre y estado de cada alumno
                            detalleAlumnosSel = cargarDetalleDia(seccionIdSel, cursoIdSel, fecha)
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { aviso("Error cargando detalle: ${e.message}") }
                    }

                    historialItemSel?.let { item ->
                        com.educonnectapp.ui.screens.DetalleAsistenciaDocenteScreen(
                            item             = item,
                            alumnos          = detalleAlumnosSel,
                            onBack           = { currentScreen = Screen.HISTORIAL_ASISTENCIA_DOCENTE },
                            onHomeDocente    = { currentScreen = Screen.HOME_DOCENTE },
                            onAlumnos        = {},
                            onAvisos         = { currentScreen = Screen.COMUNICADOS },
                            onPerfilDocente  = { currentScreen = Screen.PERFIL_DOCENTE },
                            onNotificaciones = {},
                            onGuardarCambios = { alumnosEditados, motivo, onDone ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        // Solo los alumnos cuyo estado cambió
                                        val originales = detalleAlumnosSel.associate { it.id to it.estado }
                                        val cambios = alumnosEditados
                                            .filter { originales[it.id] != it.estado }
                                            .associate { it.id to it.estado }
                                        if (cambios.isNotEmpty()) {
                                            // El backend guarda, audita, avisa al director por correo y notifica a los padres
                                            actualizarAsistenciaDia(
                                                seccionIdSel, cursoIdSel, item.fecha,
                                                motivo.ifBlank { "Corrección de asistencia" }, cambios
                                            )
                                        }
                                        detalleAlumnosSel = cargarDetalleDia(seccionIdSel, cursoIdSel, item.fecha)
                                        historialDocenteCargado = false  // los totales cambiaron: recargar al volver
                                    } catch (e: Exception) {
                                        aviso("Error al guardar: ${e.message}")
                                    }
                                    onDone()
                                }
                            }
                        )
                    }
                }
            }

            // PANEL DE NOTIFICACIONES (padre): se despliega debajo de la campana, encima de la pantalla actual
            PanelNotificaciones(
                visible = mostrarNotificaciones,
                notificaciones = notificacionesSel.map { n ->
                    // Tareas y exámenes: "Matemáticas · 2do Grado Sec. A · Carlos"
                    val contexto = if (n.tipo == "PUBLICACION" || n.tipo == "CALIFICACION")
                        contextoPublicacion(n.referenciaId) else null
                    // Comunicados: "Comunicación · 1er Sec. A · Pedro" ("General" si no tiene curso)
                    val contextoCom = if (n.tipo == "COMUNICADO") contextoComunicado(n.referenciaId) else null
                    val cursoCom = contextoCom?.second?.curso ?: "General"
                    NotificacionItem(
                        id = n.id,
                        tipo = n.tipo,
                        titulo = n.titulo,
                        // El backend envía "Curso - Entrega: dd/MM/yyyy"; el curso ya va en la línea azul
                        mensaje = contexto?.let { (_, publicacion) ->
                            n.mensaje.removePrefix("${publicacion.curso} - ").removePrefix("${publicacion.curso}: ")
                        } ?: contextoCom?.let { n.mensaje.removePrefix("$cursoCom - ").removePrefix("$cursoCom: ") }
                        ?: n.mensaje,
                        fecha = fechaLocal(n.createdAt),
                        hora = horaLocal(n.createdAt),
                        leida = n.leida,
                        detalle = contexto?.let { (hijo, publicacion) ->
                            "${publicacion.curso} · ${hijo.gradoNombre} Sec. ${hijo.seccionNombre} · ${hijo.nombres}"
                        } ?: contextoCom?.let { (hijo, _) ->
                            "$cursoCom · ${hijo.gradoNombre} Sec. ${hijo.seccionNombre} · ${hijo.nombres}"
                        } ?: ""
                    )
                },
                noLeidas = notificacionesNoLeidasSel.toInt(),
                cargando = cargandoNotificaciones,
                onCerrar = { mostrarNotificaciones = false },
                onMarcarTodas = {
                    notificacionesSel = notificacionesSel.map { it.copy(leida = true) }
                    notificacionesNoLeidasSel = 0L
                    CoroutineScope(Dispatchers.Main).launch {
                        try { marcarNotificacionesLeidas() } catch (e: Exception) {
                            aviso("No se pudieron marcar como leídas: ${e.message}")
                        }
                    }
                },
                onAbrir = { item ->
                    if (!item.leida) {
                        notificacionesSel = notificacionesSel.map { if (it.id == item.id) it.copy(leida = true) else it }
                        notificacionesNoLeidasSel = (notificacionesNoLeidasSel - 1).coerceAtLeast(0L)
                        CoroutineScope(Dispatchers.Main).launch {
                            try { marcarNotificacionLeida(item.id) } catch (e: Exception) { }
                        }
                    }
                    mostrarNotificaciones = false
                    val notificacion = notificacionesSel.find { it.id == item.id }
                    if (item.tipo == "PUBLICACION" || item.tipo == "CALIFICACION") {
                        // Entrega registrada: se recarga la agenda para ver el estado actualizado
                        if (item.tipo == "CALIFICACION") agendaCargada = false
                        abrirPublicacionDeNotificacion(notificacion?.referenciaId)   // directo al detalle
                    } else if (item.tipo == "COMUNICADO") {
                        abrirComunicadoDeNotificacion(notificacion?.referenciaId)    // directo al comunicado
                    } else {
                        irAModuloDeNotificacion(item.tipo)
                    }
                }
            )
        }
    }

    // El botón "atrás" del celular cierra el panel en lugar de salir de la pantalla
    androidx.activity.compose.BackHandler(enabled = mostrarNotificaciones) { mostrarNotificaciones = false }
    // Al cambiar de pantalla, el panel se cierra
    androidx.compose.runtime.LaunchedEffect(currentScreen) { mostrarNotificaciones = false }
}
