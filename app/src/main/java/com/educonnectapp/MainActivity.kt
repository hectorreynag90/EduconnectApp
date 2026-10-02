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


import com.educonnectapp.ui.screens.AgregarAsignacionScreen
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
import com.educonnectapp.ui.screens.AsignacionItem
import com.educonnectapp.ui.screens.AlumnoItem
import com.educonnectapp.ui.screens.GradoItem
import com.educonnectapp.ui.screens.SeccionItem
import com.educonnectapp.ui.screens.CursoItem
import com.educonnectapp.ui.screens.HijoItem
import com.educonnectapp.ui.screens.AsistenciaItem
import com.educonnectapp.ui.screens.CursoItem2
import com.educonnectapp.ui.screens.DetalleAsistencia

import com.educonnectapp.data.remote.buscarAlumnoPorCodigo
import com.educonnectapp.data.remote.obtenerAlumnosPorSeccion
import com.educonnectapp.data.remote.obtenerAlumnoPorId
import com.educonnectapp.data.remote.obtenerGradoPorId
import com.educonnectapp.data.remote.obtenerSeccionPorId
import com.educonnectapp.data.remote.AsistenciaInsert
import com.educonnectapp.data.remote.obtenerAsistenciasDia
import com.educonnectapp.data.remote.insertarAsistencia
import com.educonnectapp.data.remote.insertarAsistenciasEnLote
import com.educonnectapp.data.remote.actualizarAsistencia
import com.educonnectapp.data.remote.obtenerAsistenciasPorAlumno
import com.educonnectapp.data.remote.obtenerAsistenciasPorMes
import com.educonnectapp.data.remote.obtenerDetalleAsistencia
import com.educonnectapp.data.remote.obtenerCursoPorId
import com.educonnectapp.data.remote.obtenerDocenteSecciones
import com.educonnectapp.data.remote.obtenerDocenteSeccionesPorSeccion
import com.educonnectapp.data.remote.obtenerHijosPadre
import com.educonnectapp.data.remote.ComunicadoInsert
import com.educonnectapp.data.remote.insertarComunicado
import com.educonnectapp.data.remote.obtenerResumenComunicados
import com.educonnectapp.ui.screens.SeccionDestinatario
import com.educonnectapp.data.remote.obtenerCantidadComunicadosPorSeccion
import com.educonnectapp.data.remote.obtenerComunicadosPorSeccionConLecturas
import com.educonnectapp.data.remote.obtenerLecturasConPadre
import com.educonnectapp.data.remote.obtenerPadresSinLeer
import com.educonnectapp.ui.screens.SeleccionarComunicadoScreen
import com.educonnectapp.ui.screens.BusquedaComunicadoScreen
import com.educonnectapp.ui.screens.DetalleComunicadoScreen
import com.educonnectapp.ui.screens.SeccionComunicadoItem
import com.educonnectapp.ui.screens.ComunicadoResumenItem
import com.educonnectapp.ui.screens.PadreLecturaItem
import com.educonnectapp.data.remote.obtenerLecturasComunicado
import com.educonnectapp.data.remote.PublicacionInsert
import com.educonnectapp.data.remote.contarNotificacionesNoLeidas
import com.educonnectapp.data.remote.enviarNotificacionAsistencia
import com.educonnectapp.data.remote.guardarCambios
import com.educonnectapp.data.remote.insertarPublicacion
import com.educonnectapp.data.remote.obtenerResumenPublicaciones
import com.educonnectapp.ui.screens.PublicacionesScreen
import com.educonnectapp.ui.screens.SeleccionarCursoPublicacionScreen
import com.educonnectapp.ui.screens.NuevaTareaScreen
import com.educonnectapp.ui.screens.NuevaEvaluacionScreen
import com.educonnectapp.ui.screens.ConfirmacionPublicacionScreen
import com.educonnectapp.data.remote.obtenerPublicacionesPorAlumno
import com.educonnectapp.ui.screens.SeleccionarHijoAgendaScreen
import com.educonnectapp.ui.screens.AgendaEscolarScreen
import com.educonnectapp.ui.screens.DetalleAgendaScreen
import com.educonnectapp.ui.screens.HijoAgendaItem
import com.educonnectapp.ui.screens.PublicacionAgendaItem
import com.educonnectapp.data.remote.marcarComunicadoLeido
import com.educonnectapp.data.remote.obtenerAsistenciasPorDocente
import com.educonnectapp.data.remote.obtenerComunicadosPadre
import com.educonnectapp.data.remote.obtenerFcmTokenPorPadreId
import com.educonnectapp.data.remote.obtenerPadreIdDeAlumno
import com.educonnectapp.data.remote.supabase
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
import com.educonnectapp.data.remote.obtenerResumenAsistenciasDocente

// Backend propio (Spring Boot)
import com.educonnectapp.data.api.ApiException
import com.educonnectapp.data.api.ingresar
import com.educonnectapp.data.api.registrar
import com.educonnectapp.data.api.cerrarSesion
import com.educonnectapp.data.api.guardarFcmToken
import com.educonnectapp.data.api.AsignacionResponse
import com.educonnectapp.data.api.HijoResponse
import com.educonnectapp.data.api.obtenerAsignaciones
import com.educonnectapp.data.api.agregarAsignacion
import com.educonnectapp.data.api.obtenerGrados
import com.educonnectapp.data.api.obtenerSeccionesDeGrado
import com.educonnectapp.data.api.obtenerCursos
import com.educonnectapp.data.api.obtenerHijos
import com.educonnectapp.data.api.buscarAlumno
import com.educonnectapp.data.api.asociarHijo
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import com.educonnectapp.data.remote.obtenerUsuario

import io.github.jan.supabase.postgrest.postgrest

enum class Screen {
    WELCOME,
    LOGIN, REGISTER, HOME_DOCENTE, HOME_PADRE, ASISTENCIAS,
    SELECCIONAR_SECCION, REGISTRO_ASISTENCIA, CONFIRMACION_ASISTENCIA,
    SELECCIONAR_ESTUDIANTE, HISTORIAL_ASISTENCIAS, DETALLE_ASISTENCIA, SELECCIONAR_SECCION_HISTORIAL, HISTORIAL_ASISTENCIA_DOCENTE,
    DETALLE_ASISTENCIA_DOCENTE,
    COMUNICADOS, NUEVO_COMUNICADO, CONFIRMACION_COMUNICADO,
    PERFIL_DOCENTE, PERFIL_PADRE, AGREGAR_ASIGNACION, ASOCIAR_HIJO,
    SELECCIONAR_COMUNICADO, BUSQUEDA_COMUNICADO, DETALLE_COMUNICADO,
    PUBLICACIONES, SELECCIONAR_CURSO_PUBLICACION, NUEVA_TAREA, NUEVA_EVALUACION, CONFIRMACION_PUBLICACION,
    SELECCIONAR_HIJO_AGENDA, AGENDA_ESCOLAR, DETALLE_AGENDA,
    COMUNICADOS_PADRE, SELECCIONAR_ESTUDIANTE_COMUNICADO, COMUNICADOS_RECIBIDOS, DETALLE_COMUNICADO_PADRE
}

data class UsuarioLogueado(
    val id: String, val nombrecompleto: String, val email: String,
    val rol: String, val dni: String = "", val telefono: String = "", val codigo: String = ""
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    var totalTareasSel by remember { mutableStateOf(0) }
    var totalExamenesSel by remember { mutableStateOf(0) }
    var venceHoySel by remember { mutableStateOf(0) }
    var tipoPublicacionSel by remember { mutableStateOf("Tarea") }
    var tituloPublicacionSel by remember { mutableStateOf("") }
    var fechaEntregaSel by remember { mutableStateOf("") }
    var horaPublicacionSel by remember { mutableStateOf("") }
    var puntajeMaximoSel by remember { mutableStateOf(0) }
    var seccionPublicacionSel by remember { mutableStateOf<SeccionComunicadoItem?>(null) }
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
                    onLogin = { emailInput, passwordInput ->
                        when {
                            emailInput.isBlank() || passwordInput.isBlank() -> aviso("Completa el correo y contraseña")
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
                                            id = usuario.id.toString(),   // pasa a Long en la Etapa 2
                                            nombrecompleto = usuario.nombreCompleto,
                                            email = usuario.email,
                                            rol = usuario.rol,
                                            dni = usuario.dni,
                                            telefono = usuario.telefono
                                        )
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
                                        aviso(if (e.codigo == 401) "Correo o contraseña incorrectos" else (e.message ?: "Error al ingresar"))
                                    } catch (e: Exception) {
                                        aviso("Error al ingresar: ${e.message}")
                                    } finally {
                                        isLoginLoading = false
                                    }
                                }
                            }
                        }
                    },
                    onGoToRegister = { currentScreen = Screen.REGISTER }
                )

                Screen.REGISTER -> RegisterScreen(
                    onRegister = { nombre, dni, telefono, email, password, rol ->
                        when {
                            nombre.isBlank() || dni.isBlank() || telefono.isBlank() || email.isBlank() || password.isBlank() -> aviso("Completa todos los campos")
                            dni.length != 8 -> aviso("El DNI debe tener 8 dígitos")
                            telefono.length != 9 -> aviso("El teléfono debe tener 9 dígitos")
                            password.length < 8 -> aviso("La contraseña debe tener al menos 8 caracteres")
                            !password.any { it.isLetter() } || !password.any { it.isDigit() } -> aviso("La contraseña debe tener letras y números")
                            else -> {
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        registrar(nombre, dni, telefono, email, password, rol)
                                        aviso("Cuenta creada exitosamente")
                                        currentScreen = Screen.LOGIN
                                    } catch (e: ApiException) {
                                        aviso(if (e.codigo == 409) "Ya existe una cuenta con ese correo o DNI" else (e.message ?: "Error al crear la cuenta"))
                                    } catch (e: Exception) {
                                        aviso("Error: ${e.message ?: e.toString()}")
                                    }
                                }
                            }
                        }
                    },
                    onBack = { currentScreen = Screen.LOGIN }
                )

                Screen.HOME_DOCENTE -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        val docenteId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            // Precarga de asignaciones (backend propio)
                            aplicarAsignaciones(obtenerAsignaciones())
                            // Precarga resumen para AsistenciasScreen (Supabase: se migra en la Etapa 3)
                            val (realizados, pendientes, porcentaje) = obtenerResumenAsistenciasDocente(docenteId)
                            resumenRealizadosSel = realizados
                            resumenPendientesSel = pendientes
                            resumenPorcentajeSel = porcentaje
                        } catch (e: kotlinx.coroutines.CancellationException) {
                            throw e
                        } catch (e: Exception) { /* silencioso */ }
                    }
                    HomeDocenteScreen(
                        usuarioNombre = usuarioLogueado?.nombrecompleto ?: "",
                        onAsistencias = {
                            currentScreen = Screen.ASISTENCIAS
                        },
                        onComunicados = { currentScreen = Screen.COMUNICADOS },
                        onPublicaciones = { currentScreen = Screen.PUBLICACIONES },
                        onAlumnos = {},
                        onAvisos = { currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {}
                    )
                }

                Screen.HOME_PADRE -> {
                    var tieneNotificaciones by remember { mutableStateOf(false) }
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        val padreId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val sinLeer = contarNotificacionesNoLeidas(padreId)
                            tieneNotificaciones = sinLeer > 0
                        } catch (e: Exception) { /* silencioso */ }
                    }
                    HomePadreScreen(
                        usuarioNombre = usuarioLogueado?.nombrecompleto ?: "",
                        tieneNotificaciones = tieneNotificaciones,
                        onAsistencias = { currentScreen = Screen.SELECCIONAR_ESTUDIANTE },
                        onComunicados = { currentScreen = Screen.COMUNICADOS_PADRE },
                        onAgenda = { currentScreen = Screen.SELECCIONAR_HIJO_AGENDA },
                        onEstadoAcademico = {},
                        onAvisos = {},
                        onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                        onNotificaciones = { currentScreen = Screen.HISTORIAL_ASISTENCIAS }
                    )
                }

                Screen.ASISTENCIAS -> {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        val docenteId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val (realizados, pendientes, porcentaje) = kotlinx.coroutines.withContext(Dispatchers.IO) {
                                obtenerResumenAsistenciasDocente(docenteId)
                            }
                            resumenRealizadosSel = realizados
                            resumenPendientesSel = pendientes
                            resumenPorcentajeSel = porcentaje
                        } catch (e: Exception) { /* silencioso */ }
                    }
                    AsistenciasScreen(
                        onBack = { currentScreen = Screen.HOME_DOCENTE },
                        onRegistrarAsistencia = { currentScreen = Screen.SELECCIONAR_SECCION },
                        onHistorial = { currentScreen = Screen.SELECCIONAR_SECCION_HISTORIAL },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = {},
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
                        docenteId = usuarioLogueado?.id ?: "",
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
                    androidx.compose.runtime.LaunchedEffect(seccionIdSel, cursoIdSel) {
                        try {
                            // Si no hay alumnos precargados, cargarlos ahora
                            if (listaAlumnosSel.isEmpty()) {
                                listaAlumnosSel = obtenerAlumnosPorSeccion(seccionIdSel).map { AlumnoItem(id = it.id, nombres = it.nombres, apellidos = it.apellidos) }
                            }
                            val fechaHoy = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                            val docenteId = usuarioLogueado?.id ?: return@LaunchedEffect
                            val asistencias = obtenerAsistenciasDia(cursoIdSel, docenteId, fechaHoy)
                            asistenciaExistente = if (asistencias.isNotEmpty()) asistencias.associate { it.alumno_id to it.estado } else emptyMap()
                        } catch (e: Exception) { aviso("Error cargando alumnos: ${e.message}") }
                    }
                    RegistroAsistenciaScreen(
                        docenteId = usuarioLogueado?.id ?: "",
                        gradoId = gradoIdSel,
                        seccionId = seccionIdSel,
                        cursoId = cursoIdSel,
                        grado = gradoSel,
                        seccion = seccionSel,
                        curso = cursoSel,
                        listaAlumnos = listaAlumnosSel, asistenciaPrevia = asistenciaExistente,
                        onBack = { currentScreen = Screen.SELECCIONAR_SECCION },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = { currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onGuardado = { presentes, ausentes, tardanzas, estados, hora, fecha ->
                            val docenteId = usuarioLogueado?.id ?: return@RegistroAsistenciaScreen
                            val esActualizacion = asistenciaExistente.isNotEmpty()
                            // Navegar inmediatamente a confirmación
                            presentesSel = presentes
                            ausentesSel = ausentes
                            tardanzasSel = tardanzas
                            currentScreen = Screen.CONFIRMACION_ASISTENCIA
                            // Guardar asistencias y luego actualizar resumen
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    // 1) Guardar registros en IO, esperar que terminen
                                    kotlinx.coroutines.withContext(Dispatchers.IO) {
                                        if (esActualizacion) {
                                            // Actualizar solo los que cambiaron
                                            estados.forEach { (alumnoId, estado) ->
                                                if (asistenciaExistente[alumnoId] != estado) {
                                                    actualizarAsistencia(alumnoId, cursoIdSel, docenteId, fecha, estado)
                                                }
                                            }
                                        } else {
                                            // Insertar todos en un solo batch (1 llamada a Supabase)
                                            val lista = estados.map { (alumnoId, estado) ->
                                                AsistenciaInsert(
                                                    alumno_id = alumnoId, curso_id = cursoIdSel,
                                                    docente_id = docenteId, fecha = fecha, hora = hora, estado = estado
                                                )
                                            }
                                            insertarAsistenciasEnLote(lista)
                                        }
                                    }
                                    // 2) Consultar resumen en IO DESPUÉS de que el guardado terminó
                                    val (realizados, pendientes, porcentaje) = kotlinx.coroutines.withContext(Dispatchers.IO) {
                                        obtenerResumenAsistenciasDocente(docenteId)
                                    }
                                    // 3) Actualizar estado en Main thread
                                    resumenRealizadosSel = realizados
                                    resumenPendientesSel = pendientes
                                    resumenPorcentajeSel = porcentaje
                                } catch (e: Exception) { /* silencioso */ }
                                // 4) Notificaciones en background separado (no bloquean el resumen)
                                CoroutineScope(Dispatchers.IO).launch {
                                    estados.forEach { (alumnoId, estado) ->
                                        try {
                                            val alumno = listaAlumnosSel.find { it.id == alumnoId }
                                            val alumnoNombre = if (alumno != null) "${alumno.nombres} ${alumno.apellidos}" else "Estudiante"
                                            val padreId = obtenerPadreIdDeAlumno(alumnoId) ?: return@forEach
                                            val fcmToken = obtenerFcmTokenPorPadreId(padreId)
                                            enviarNotificacionAsistencia(
                                                padreId = padreId,
                                                alumnoNombre = alumnoNombre,
                                                grado = gradoSel,
                                                seccion = seccionSel,
                                                curso = cursoSel,
                                                estado = estado,
                                                hora = hora,
                                                fecha = fecha,
                                                fcmToken = fcmToken,
                                                esActualizacion = esActualizacion
                                            )
                                        } catch (e: Exception) { /* silencioso */ }
                                    }
                                }
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
                    onNuevaAsistencia = { currentScreen = Screen.HOME_DOCENTE },
                    onVerHistorial = {},
                    onClose = { currentScreen = Screen.HOME_DOCENTE }
                )

                Screen.SELECCIONAR_ESTUDIANTE -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        val padreId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val relaciones = obtenerHijosPadre(padreId)
                            val fechaHoy = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault()).format(java.util.Date())
                            val lista = mutableListOf<HijoItem>()
                            relaciones.forEach { rel ->
                                val alumno = buscarAlumnoPorCodigo(rel.codigo_estudiante) ?: return@forEach
                                val grado = obtenerGradoPorId(alumno.grado_id) ?: return@forEach
                                val seccion = obtenerSeccionPorId(alumno.seccion_id) ?: return@forEach
                                val asistenciasMes = obtenerAsistenciasPorMes(alumno.id, fechaHoy)
                                val presentes = asistenciasMes.count { it.estado == "A" }
                                val total = asistenciasMes.size
                                lista.add(HijoItem(id = alumno.id, nombres = alumno.nombres, apellidos = alumno.apellidos, gradoNombre = grado.nombre, seccionNombre = seccion.nombre, porcentajeMes = if (total > 0) (presentes * 100) / total else 0))
                            }
                            listaHijosSel = lista
                        } catch (e: Exception) { aviso("Error cargando estudiantes: ${e.message}") }
                    }
                    SeleccionarEstudianteScreen(
                        padreId = usuarioLogueado?.id ?: "",
                        padreNombre = usuarioLogueado?.nombrecompleto ?: "",
                        listaHijos = listaHijosSel,
                        onBack = { currentScreen = Screen.HOME_PADRE },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = {},
                        onAgenda = {},
                        onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                        onNotificaciones = {},
                        onVerHistorial = { alumnoId, alumnoNombre ->
                            alumnoIdSel = alumnoId; alumnoNombreSel = alumnoNombre
                            currentScreen = Screen.HISTORIAL_ASISTENCIAS
                        }
                    )
                }

                Screen.HISTORIAL_ASISTENCIAS -> {
                    androidx.compose.runtime.LaunchedEffect(alumnoIdSel) {
                        try {
                            // Cargar todas las asistencias del alumno
                            val asistencias = obtenerAsistenciasPorAlumno(alumnoIdSel)

                            // Cargar cursos únicos del alumno
                            val cursoIds = asistencias.map { it.curso_id }.distinct()
                            val cursos = cursoIds.mapNotNull { cursoId ->
                                val c = obtenerCursoPorId(cursoId) ?: return@mapNotNull null
                                CursoItem2(c.id, c.nombre)
                            }
                            listaCursosHistorialSel = cursos

                            // Mapear asistencias con nombre de curso
                            todasAsistenciasSel = asistencias.map { a ->
                                val cursoNombre = cursos.find { it.id == a.curso_id }?.nombre ?: ""
                                AsistenciaItem(
                                    id = a.id,
                                    alumnoId = a.alumno_id,
                                    cursoId = a.curso_id,
                                    cursoNombre = cursoNombre,
                                    fecha = a.fecha,
                                    estado = a.estado
                                )
                            }
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
                        onAvisos = {},
                        onAgenda = {},
                        onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                        onNotificaciones = {},
                        onVerDetalle = { asistenciaId, fecha ->
                            asistenciaIdSel = asistenciaId
                            fechaAsistenciaSel = fecha
                            currentScreen = Screen.DETALLE_ASISTENCIA
                        }
                    )
                }

                Screen.DETALLE_ASISTENCIA -> {
                    androidx.compose.runtime.LaunchedEffect(asistenciaIdSel) {
                        try {
                            val asistenciaBase = todasAsistenciasSel.find { it.id == asistenciaIdSel } ?: return@LaunchedEffect
                            val asistencia = obtenerDetalleAsistencia(asistenciaBase.alumnoId, asistenciaBase.fecha, asistenciaBase.cursoId) ?: return@LaunchedEffect
                            val alumno = obtenerAlumnoPorId(asistencia.alumno_id) ?: return@LaunchedEffect
                            val grado = obtenerGradoPorId(alumno.grado_id) ?: return@LaunchedEffect
                            val seccion = obtenerSeccionPorId(alumno.seccion_id) ?: return@LaunchedEffect
                            val docente = obtenerUsuario(asistencia.docente_id)

                            detalleAsistenciaSel = DetalleAsistencia(
                                id = asistencia.id,
                                fecha = asistencia.fecha,
                                hora = asistencia.hora,
                                estado = asistencia.estado,
                                cursoNombre = asistenciaBase?.cursoNombre ?: "",
                                gradoNombre = grado.nombre,
                                seccionNombre = seccion.nombre,
                                docenteNombre = docente.nombrecompleto
                            )
                        } catch (e: Exception) {
                            aviso("Error cargando detalle: ${e.message}")
                        }
                    }

                    DetalleAsistenciaScreen(
                        asistenciaId = asistenciaIdSel,
                        alumnoNombre = alumnoNombreSel,
                        detalle = detalleAsistenciaSel,
                        onBack = { currentScreen = Screen.HISTORIAL_ASISTENCIAS },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = {},
                        onAgenda = {},
                        onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                        onNotificaciones = {}
                    )
                }

                Screen.COMUNICADOS -> {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        val docenteId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val (total, hoy, sinLeer) = obtenerResumenComunicados(docenteId)
                            totalEnviadosSel = total
                            totalHoySel = hoy
                            sinLeerSel = sinLeer
                        } catch (e: Exception) {
                            aviso("Error cargando resumen: ${e.message}")
                        }
                    }
                    ComunicadosScreen(
                        docenteId = usuarioLogueado?.id ?: "",
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
                        val docenteId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val relaciones = obtenerDocenteSecciones(docenteId)
                            val lista = mutableListOf<SeccionDestinatario>()
                            relaciones.forEach { rel ->
                                val grado = obtenerGradoPorId(rel.grado_id) ?: return@forEach
                                val seccion = obtenerSeccionPorId(rel.seccion_id) ?: return@forEach
                                val curso = obtenerCursoPorId(rel.curso_id) ?: return@forEach
                                val totalPadres = obtenerAlumnosPorSeccion(rel.seccion_id).size
                                lista.add(SeccionDestinatario(
                                    gradoId = rel.grado_id,
                                    seccionId = rel.seccion_id,
                                    cursoId = rel.curso_id,
                                    gradoNombre = grado.nombre,
                                    seccionNombre = seccion.nombre,
                                    cursoNombre = curso.nombre,
                                    totalPadres = totalPadres
                                ))
                            }
                            listaDestinatariosSel = lista
                        } catch (e: Exception) { aviso("Error cargando destinatarios: ${e.message}") }
                    }
                    NuevoComunicadoScreen(
                        docenteId = usuarioLogueado?.id ?: "",
                        docenteNombre = usuarioLogueado?.nombrecompleto ?: "",
                        listaDestinatarios = listaDestinatariosSel,
                        onBack = { currentScreen = Screen.COMUNICADOS },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        { currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onEnviado = { asunto, mensaje, adjunto, destinatario, curso, total, hora, fecha ->
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    val docenteId = usuarioLogueado?.id ?: return@launch
                                    val dest = listaDestinatariosSel.find {
                                        "${it.gradoNombre} Sec. ${it.seccionNombre}" == destinatario
                                    } ?: return@launch
                                    insertarComunicado(ComunicadoInsert(
                                        docente_id = docenteId,
                                        grado_id = dest.gradoId,
                                        seccion_id = dest.seccionId,
                                        curso_id = dest.cursoId,
                                        asunto = asunto,
                                        mensaje = mensaje,
                                        archivo_adjunto = adjunto,
                                        fecha = fecha,
                                        hora = hora,
                                        total_notificados = total
                                    ))
                                    comunicadoAsuntoSel = asunto
                                    comunicadoDestinatarioSel = destinatario
                                    comunicadoCursoSel = curso
                                    comunicadoNotificadosSel = total
                                    comunicadoHoraSel = hora
                                    comunicadoFechaSel = fecha
                                    currentScreen = Screen.CONFIRMACION_COMUNICADO
                                } catch (e: Exception) {
                                    aviso("Error al enviar: ${e.message}")
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
                    onVerHistorial = {},
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
                        onAgregarAsignacion = { currentScreen = Screen.AGREGAR_ASIGNACION },
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
                            hijosAsociadosSel = obtenerHijos().map { it.aHijoAsociado() }
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
                        onAvisos = {},
                        onAgenda = {},
                        onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                        onEditarNombre = {},
                        onEditarDni = {},
                        onEditarTelefono = {},
                        onEditarEmail = {},
                        onAsociarHijo = { currentScreen = Screen.ASOCIAR_HIJO },
                        onEliminarHijo = {},
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
                        onBack = { currentScreen = Screen.PERFIL_DOCENTE }, onNotificaciones = {},
                        gradosDisp = gradosDisp, seccionesDisp = seccionesDisp, cursosDisp = cursosDisp,
                        onGuardar = { grado, seccion, curso ->
                            CoroutineScope(Dispatchers.Main).launch {
                                val seccionId = seccionIdPorNombre["$grado|$seccion"]
                                val cursoId = cursoIdPorNombre[curso]
                                if (seccionId == null || cursoId == null) {
                                    aviso("Selección no válida, vuelve a intentarlo")
                                    return@launch
                                }
                                try {
                                    val nueva = agregarAsignacion(seccionId, cursoId)
                                    aplicarAsignaciones(asignacionesApiSel + nueva)
                                    aviso("Asignación agregada"); currentScreen = Screen.PERFIL_DOCENTE
                                } catch (e: ApiException) {
                                    aviso(if (e.codigo == 409) "Ya tienes asignado ese curso en esa sección" else (e.message ?: "Error al guardar"))
                                } catch (e: Exception) { aviso("Error al guardar: ${e.message}") }
                            }
                        },
                        onCancelar = { currentScreen = Screen.PERFIL_DOCENTE }
                    )
                }

                Screen.ASOCIAR_HIJO -> AsociarHijoScreen(
                    onBack = { currentScreen = Screen.PERFIL_PADRE },
                    onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                    onAvisos = {},
                    onAgenda = {},
                    onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                    onNotificaciones = {},
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
                    onConfirmar = { alumno ->
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                val hijo = asociarHijo(alumno.codigoEstudiante)
                                aviso("Hijo asociado correctamente")
                                alumnoEncontradoSel = null; errorBusquedaSel = ""
                                hijosAsociadosSel = hijosAsociadosSel.filter { it.id != hijo.alumnoId } + hijo.aHijoAsociado()
                                currentScreen = Screen.PERFIL_PADRE
                            } catch (e: ApiException) {
                                aviso(if (e.codigo == 409) "Este estudiante ya está asociado a tu cuenta" else (e.message ?: "Error al asociar"))
                            } catch (e: Exception) { aviso("Error al asociar: ${e.message}") }
                        }
                    },
                    onCancelar = { alumnoEncontradoSel = null; errorBusquedaSel = ""; currentScreen = Screen.PERFIL_PADRE }
                )

                Screen.SELECCIONAR_COMUNICADO -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        val docenteId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val asignaciones = obtenerDocenteSecciones(docenteId)
                            val lista = mutableListOf<SeccionComunicadoItem>()
                            asignaciones.forEach { asig ->
                                val grado = obtenerGradoPorId(asig.grado_id) ?: return@forEach
                                val seccion = obtenerSeccionPorId(asig.seccion_id) ?: return@forEach
                                val curso = obtenerCursoPorId(asig.curso_id) ?: return@forEach
                                val alumnos = obtenerAlumnosPorSeccion(asig.seccion_id)
                                val cantComunicados = obtenerCantidadComunicadosPorSeccion(docenteId, asig.seccion_id)
                                lista.add(SeccionComunicadoItem(
                                    seccionId = asig.seccion_id,
                                    gradoNombre = grado.nombre,
                                    seccionNombre = seccion.nombre,
                                    cursoNombre = curso.nombre,
                                    cantidadAlumnos = alumnos.size,
                                    cantidadComunicados = cantComunicados
                                ))
                            }
                            seccionesComunicadoSel = lista
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
                        val docenteId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val comunicados = obtenerComunicadosPorSeccionConLecturas(docenteId, seccionIdSel)
                            comunicadosListaSel = comunicados.map { com ->
                                val leidos = obtenerLecturasComunicado(com.id)
                                ComunicadoResumenItem(
                                    id = com.id,
                                    asunto = com.asunto,
                                    mensaje = com.mensaje,
                                    fecha = com.fecha,
                                    totalNotificados = com.total_notificados,
                                    totalLeidos = leidos
                                )
                            }
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
                        try {
                            val comunicado = comunicadosListaSel.find { it.id == comunicadoIdSel }
                            comunicadoAsuntoSel = comunicado?.asunto ?: ""
                            comunicadoMensajeSel = comunicado?.mensaje ?: ""
                            comunicadoTotalNotifSel = comunicado?.totalNotificados ?: 0

                            val lecturas = obtenerLecturasConPadre(comunicadoIdSel)
                            val padresSinLeer = obtenerPadresSinLeer(comunicadoIdSel, seccionIdSel)

                            val padresLeidos = lecturas.map { l ->
                                PadreLecturaItem(
                                    padreId = l.padre_id,
                                    nombrePadre = l.nombrecompleto,
                                    nombreHijo = "",
                                    leidoEn = l.leido_en
                                )
                            }
                            val padresSinLeerList = padresSinLeer.map { p ->
                                PadreLecturaItem(
                                    padreId = p.id,
                                    nombrePadre = p.nombrecompleto,
                                    nombreHijo = "",
                                    leidoEn = null
                                )
                            }
                            padresLecturaSel = padresLeidos + padresSinLeerList
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
                            aviso("Reenviando a ${padresLecturaSel.count { it.leidoEn == null }} padres sin leer...")
                        }
                    )
                }

                Screen.PUBLICACIONES -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        val docenteId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val (tareas, examenes, vence) = obtenerResumenPublicaciones(docenteId)
                            totalTareasSel = tareas
                            totalExamenesSel = examenes
                            venceHoySel = vence
                        } catch (e: Exception) { aviso("Error cargando publicaciones: ${e.message}") }
                    }
                    PublicacionesScreen(
                        totalTareas = totalTareasSel,
                        totalExamenes = totalExamenesSel,
                        venceHoy = venceHoySel,
                        onBack = { currentScreen = Screen.HOME_DOCENTE },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = {currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onNuevaTarea = {
                            tipoPublicacionSel = "Tarea"
                            currentScreen = Screen.SELECCIONAR_CURSO_PUBLICACION
                        },
                        onNuevoExamen = {
                            tipoPublicacionSel = "Examen"
                            currentScreen = Screen.SELECCIONAR_CURSO_PUBLICACION
                        },
                        onHistorial = {}
                    )
                }

                Screen.SELECCIONAR_CURSO_PUBLICACION -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        val docenteId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val asignaciones = obtenerDocenteSecciones(docenteId)
                            val lista = mutableListOf<SeccionComunicadoItem>()
                            asignaciones.forEach { asig ->
                                val grado = obtenerGradoPorId(asig.grado_id) ?: return@forEach
                                val seccion = obtenerSeccionPorId(asig.seccion_id) ?: return@forEach
                                val curso = obtenerCursoPorId(asig.curso_id) ?: return@forEach
                                val alumnos = obtenerAlumnosPorSeccion(asig.seccion_id)
                                lista.add(SeccionComunicadoItem(
                                    seccionId = asig.seccion_id,
                                    gradoNombre = grado.nombre,
                                    seccionNombre = seccion.nombre,
                                    cursoNombre = curso.nombre,
                                    cantidadAlumnos = alumnos.size,
                                    cantidadComunicados = 0
                                ))
                            }
                            seccionesComunicadoSel = lista
                        } catch (e: Exception) { aviso("Error cargando cursos: ${e.message}") }
                    }
                    SeleccionarCursoPublicacionScreen(
                        tipo = tipoPublicacionSel,
                        secciones = seccionesComunicadoSel,
                        onBack = { currentScreen = Screen.PUBLICACIONES },
                        onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                        onAlumnos = {},
                        onAvisos = {currentScreen = Screen.COMUNICADOS },
                        onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                        onNotificaciones = {},
                        onSeleccionar = { seccion ->
                            seccionPublicacionSel = seccion
                            currentScreen = if (tipoPublicacionSel == "Tarea") Screen.NUEVA_TAREA else Screen.NUEVA_EVALUACION
                        }
                    )
                }

                Screen.NUEVA_TAREA -> {
                    val dest = seccionPublicacionSel
                    if (dest != null) {
                        NuevaTareaScreen(
                            listaDestinatarios = listOf(
                                SeccionDestinatario(
                                    gradoId = dest.seccionId,
                                    seccionId = dest.seccionId,
                                    cursoId = dest.seccionId,
                                    gradoNombre = dest.gradoNombre,
                                    seccionNombre = dest.seccionNombre,
                                    cursoNombre = dest.cursoNombre,
                                    totalPadres = dest.cantidadAlumnos
                                )
                            ),
                            cursoNombre = seccionPublicacionSel?.cursoNombre ?: "",
                            onBack = { currentScreen = Screen.SELECCIONAR_CURSO_PUBLICACION },
                            onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                            onAlumnos = {},
                            onAvisos = {currentScreen = Screen.COMUNICADOS },
                            onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                            onNotificaciones = {},
                            onPublicar = { titulo, descripcion, adjunto, seccion, fechaEntrega, fechaPub, hora ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        val docenteId = usuarioLogueado?.id ?: return@launch
                                        val asig = obtenerDocenteSeccionesPorSeccion(docenteId, seccion.seccionId)
                                            .firstOrNull() ?: return@launch
                                        insertarPublicacion(PublicacionInsert(
                                            docente_id = docenteId,
                                            curso_id = asig.curso_id,
                                            grado_id = asig.grado_id,
                                            seccion_id = asig.seccion_id,
                                            titulo = titulo,
                                            descripcion = descripcion,
                                            tipo = "Tarea",
                                            fecha_entrega = fechaEntrega,
                                            fecha_publicacion = fechaPub,
                                            archivo_adjunto = adjunto
                                        ))
                                        tituloPublicacionSel = titulo
                                        fechaEntregaSel = fechaEntrega
                                        horaPublicacionSel = hora
                                        puntajeMaximoSel = 0
                                        currentScreen = Screen.CONFIRMACION_PUBLICACION
                                    } catch (e: Exception) { aviso("Error al publicar: ${e.message}") }
                                }
                            }
                        )
                    }
                }

                Screen.NUEVA_EVALUACION -> {
                    val dest = seccionPublicacionSel
                    if (dest != null) {
                        NuevaEvaluacionScreen(
                            listaDestinatarios = listOf(
                                SeccionDestinatario(
                                    gradoId = dest.seccionId,
                                    seccionId = dest.seccionId,
                                    cursoId = dest.seccionId,
                                    gradoNombre = dest.gradoNombre,
                                    seccionNombre = dest.seccionNombre,
                                    cursoNombre = dest.cursoNombre,
                                    totalPadres = dest.cantidadAlumnos
                                )
                            ),
                            cursoNombre = seccionPublicacionSel?.cursoNombre ?: "",
                            onBack = { currentScreen = Screen.SELECCIONAR_CURSO_PUBLICACION },
                            onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                            onAlumnos = {},
                            onAvisos = {currentScreen = Screen.COMUNICADOS },
                            onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                            onNotificaciones = {},
                            onPublicar = { titulo, descripcion, adjunto, seccion, fechaExamen, fechaPub, hora ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        val docenteId = usuarioLogueado?.id ?: return@launch
                                        val asig = obtenerDocenteSeccionesPorSeccion(docenteId, seccion.seccionId)
                                            .firstOrNull() ?: return@launch
                                        insertarPublicacion(PublicacionInsert(
                                            docente_id = docenteId,
                                            curso_id = asig.curso_id,
                                            grado_id = asig.grado_id,
                                            seccion_id = asig.seccion_id,
                                            titulo = titulo,
                                            descripcion = descripcion,
                                            tipo = "Examen",
                                            fecha_entrega = fechaExamen,
                                            fecha_publicacion = fechaPub,
                                            archivo_adjunto = adjunto
                                        ))
                                        tituloPublicacionSel = titulo
                                        fechaEntregaSel = fechaExamen
                                        horaPublicacionSel = hora
                                        currentScreen = Screen.CONFIRMACION_PUBLICACION
                                    } catch (e: Exception) { aviso("Error al publicar: ${e.message}") }
                                }
                            }
                        )
                    }
                }

                Screen.CONFIRMACION_PUBLICACION -> ConfirmacionPublicacionScreen(
                    tipo = tipoPublicacionSel,
                    titulo = tituloPublicacionSel,
                    gradoNombre = seccionPublicacionSel?.gradoNombre ?: "",
                    seccionNombre = seccionPublicacionSel?.seccionNombre ?: "",
                    cursoNombre = seccionPublicacionSel?.cursoNombre ?: "",
                    totalNotificados = seccionPublicacionSel?.cantidadAlumnos ?: 0,
                    fechaEntrega = fechaEntregaSel,
                    hora = horaPublicacionSel,
                    onNuevaPublicacion = {
                        currentScreen = Screen.SELECCIONAR_CURSO_PUBLICACION
                    },
                    onVerHistorial = {},
                    onVolver = { currentScreen = Screen.PUBLICACIONES }
                )

                Screen.SELECCIONAR_HIJO_AGENDA -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        val padreId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val relaciones = obtenerHijosPadre(padreId)
                            val lista = mutableListOf<HijoAgendaItem>()
                            relaciones.forEach { rel ->
                                val alumno = buscarAlumnoPorCodigo(rel.codigo_estudiante) ?: return@forEach
                                val grado = obtenerGradoPorId(alumno.grado_id) ?: return@forEach
                                val seccion = obtenerSeccionPorId(alumno.seccion_id) ?: return@forEach
                                val publicaciones = obtenerPublicacionesPorAlumno(alumno.seccion_id, alumno.grado_id)
                                val tareas = publicaciones.count { it.tipo == "Tarea" && it.estado.lowercase() == "pendiente" }
                                val examenes = publicaciones.count { it.tipo == "Examen" && it.estado.lowercase() == "pendiente" }
                                lista.add(HijoAgendaItem(
                                    id = alumno.id,
                                    nombres = alumno.nombres,
                                    apellidos = alumno.apellidos,
                                    gradoNombre = grado.nombre,
                                    seccionNombre = seccion.nombre,
                                    gradoId = alumno.grado_id,
                                    seccionId = alumno.seccion_id,
                                    tareasPendientes = tareas,
                                    examenesPendientes = examenes
                                ))
                            }
                            hijosAgendaSel = lista
                        } catch (e: Exception) { aviso("Error cargando hijos: ${e.message}") }
                    }
                    SeleccionarHijoAgendaScreen(
                        hijos = hijosAgendaSel,
                        onBack = { currentScreen = Screen.HOME_PADRE },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = {},
                        onAgenda = {currentScreen = Screen.AGENDA_ESCOLAR},
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = {},
                        onVerAgenda = { hijo ->
                            hijoAgendaSel = hijo
                            currentScreen = Screen.AGENDA_ESCOLAR
                        }
                    )
                }

                Screen.AGENDA_ESCOLAR -> {
                    androidx.compose.runtime.LaunchedEffect(hijoAgendaSel?.id) {
                        val hijo = hijoAgendaSel ?: return@LaunchedEffect
                        try {
                            val publicaciones = obtenerPublicacionesPorAlumno(hijo.seccionId, hijo.gradoId)
                            publicacionesAgendaSel = publicaciones.map { pub ->
                                val docente = obtenerUsuario(pub.docente_id)
                                val curso = obtenerCursoPorId(pub.curso_id)
                                PublicacionAgendaItem(
                                    id = pub.id,
                                    titulo = pub.titulo,
                                    descripcion = pub.descripcion,
                                    tipo = pub.tipo,
                                    cursoNombre = curso?.nombre ?: "",
                                    docenteNombre = docente.nombrecompleto,
                                    fechaEntrega = pub.fecha_entrega,
                                    estado = pub.estado,
                                    archivoAdjunto = pub.archivo_adjunto
                                )
                            }
                        } catch (e: Exception) { aviso("Error cargando agenda: ${e.message}") }
                    }
                    AgendaEscolarScreen(
                        nombreHijo = hijoAgendaSel?.nombres ?: "",
                        gradoNombre = hijoAgendaSel?.gradoNombre ?: "",
                        seccionNombre = hijoAgendaSel?.seccionNombre ?: "",
                        publicaciones = publicacionesAgendaSel,
                        onBack = { currentScreen = Screen.SELECCIONAR_HIJO_AGENDA },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = {},
                        onAgenda = {currentScreen = Screen.AGENDA_ESCOLAR},
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = {},
                        onVerDetalle = { publicacion ->
                            publicacionAgendaSel = publicacion
                            currentScreen = Screen.DETALLE_AGENDA
                        }
                    )
                }

                Screen.DETALLE_AGENDA -> DetalleAgendaScreen(
                    nombreHijo = hijoAgendaSel?.nombres ?: "",
                    gradoNombre = hijoAgendaSel?.gradoNombre ?: "",
                    seccionNombre = hijoAgendaSel?.seccionNombre ?: "",
                    publicacion = publicacionAgendaSel,
                    onBack = { currentScreen = Screen.AGENDA_ESCOLAR },
                    onHomePadre = { currentScreen = Screen.HOME_PADRE },
                    onAvisos = {},
                    onAgenda = {currentScreen = Screen.AGENDA_ESCOLAR},
                    onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                    onNotificaciones = {}
                )

                Screen.COMUNICADOS_PADRE -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        val padreId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val relaciones = obtenerHijosPadre(padreId)
                            var sinLeer = 0
                            var leidos = 0
                            var total = 0
                            relaciones.forEach { rel ->
                                val alumno = buscarAlumnoPorCodigo(rel.codigo_estudiante) ?: return@forEach
                                val comunicados = obtenerComunicadosPadre(alumno.seccion_id)
                                comunicados.forEach { com ->
                                    val lecturas = obtenerLecturasComunicado(com.id)
                                    val yaLeido = supabase.postgrest["comunicado_lecturas"]
                                        .select(io.github.jan.supabase.postgrest.query.Columns.ALL) {
                                            filter {
                                                eq("comunicado_id", com.id)
                                                eq("padre_id", padreId)
                                            }
                                        }
                                        .decodeList<com.educonnectapp.data.remote.ComunicadoLecturaRow>()
                                        .isNotEmpty()
                                    if (yaLeido) leidos++ else sinLeer++
                                    total++
                                }
                            }
                            sinLeerPadreSel = sinLeer
                            leidosPadreSel = leidos
                            totalPadreSel = total
                        } catch (e: Exception) { aviso("Error: ${e.message}") }
                    }
                    ComunicadosPadresScreen(
                        sinLeer = sinLeerPadreSel,
                        leidos = leidosPadreSel,
                        total = totalPadreSel,
                        onBack = { currentScreen = Screen.HOME_PADRE },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = {},
                        onAgenda = {currentScreen = Screen.AGENDA_ESCOLAR},
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = {},
                        onVerRecibidos = { currentScreen = Screen.SELECCIONAR_ESTUDIANTE_COMUNICADO },
                        onVerHistorial = { currentScreen = Screen.SELECCIONAR_ESTUDIANTE_COMUNICADO }
                    )
                }

                Screen.SELECCIONAR_ESTUDIANTE_COMUNICADO -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        val padreId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val relaciones = obtenerHijosPadre(padreId)
                            val lista = mutableListOf<HijoComunicadoItem>()
                            relaciones.forEach { rel ->
                                val alumno = buscarAlumnoPorCodigo(rel.codigo_estudiante) ?: return@forEach
                                val grado = obtenerGradoPorId(alumno.grado_id) ?: return@forEach
                                val seccion = obtenerSeccionPorId(alumno.seccion_id) ?: return@forEach
                                val comunicados = obtenerComunicadosPadre(alumno.seccion_id)
                                var sinLeer = 0
                                comunicados.forEach { com ->
                                    val yaLeido = supabase.postgrest["comunicado_lecturas"]
                                        .select(io.github.jan.supabase.postgrest.query.Columns.ALL) {
                                            filter {
                                                eq("comunicado_id", com.id)
                                                eq("padre_id", padreId)
                                            }
                                        }
                                        .decodeList<com.educonnectapp.data.remote.ComunicadoLecturaRow>()
                                        .isNotEmpty()
                                    if (!yaLeido) sinLeer++
                                }
                                lista.add(HijoComunicadoItem(
                                    id = alumno.id,
                                    nombres = alumno.nombres,
                                    apellidos = alumno.apellidos,
                                    gradoNombre = grado.nombre,
                                    seccionNombre = seccion.nombre,
                                    seccionId = alumno.seccion_id,
                                    sinLeer = sinLeer,
                                    total = comunicados.size
                                ))
                            }
                            hijosComunicadoSel = lista
                        } catch (e: Exception) { aviso("Error: ${e.message}") }
                    }
                    SeleccionarEstudianteComunicadoScreen(
                        hijos = hijosComunicadoSel,
                        onBack = { currentScreen = Screen.COMUNICADOS_PADRE },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = {},
                        onAgenda = {currentScreen = Screen.AGENDA_ESCOLAR},
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = {},
                        onVerComunicados = { hijo ->
                            hijoComunicadoSel = hijo
                            currentScreen = Screen.COMUNICADOS_RECIBIDOS
                        }
                    )
                }

                Screen.COMUNICADOS_RECIBIDOS -> {
                    androidx.compose.runtime.LaunchedEffect(hijoComunicadoSel?.id) {
                        val padreId = usuarioLogueado?.id ?: return@LaunchedEffect
                        val hijo = hijoComunicadoSel ?: return@LaunchedEffect
                        try {
                            val comunicados = obtenerComunicadosPadre(hijo.seccionId)
                            val lista = mutableListOf<ComunicadoPadreItem>()
                            comunicados.forEach { com ->
                                val docente = obtenerUsuario(com.docente_id)
                                val grado = obtenerGradoPorId(com.grado_id)
                                val seccion = obtenerSeccionPorId(com.seccion_id)
                                val curso = obtenerCursoPorId(com.curso_id)
                                val lecturas = supabase.postgrest["comunicado_lecturas"]
                                    .select(io.github.jan.supabase.postgrest.query.Columns.ALL) {
                                        filter {
                                            eq("comunicado_id", com.id)
                                            eq("padre_id", padreId)
                                        }
                                    }
                                    .decodeList<com.educonnectapp.data.remote.ComunicadoLecturaRow>()
                                val leido = lecturas.isNotEmpty()
                                lista.add(ComunicadoPadreItem(
                                    id = com.id,
                                    asunto = com.asunto,
                                    mensaje = com.mensaje,
                                    fecha = com.fecha,
                                    hora = com.hora,
                                    docenteNombre = docente.nombrecompleto,
                                    gradoNombre = grado?.nombre ?: "",
                                    seccionNombre = seccion?.nombre ?: "",
                                    cursoNombre = curso?.nombre ?: "",
                                    leido = leido,
                                    leidoEn = lecturas.firstOrNull()?.leido_en
                                ))
                            }
                            comunicadosPadreSel = lista.sortedBy { it.leido }
                        } catch (e: Exception) { aviso("Error: ${e.message}") }
                    }
                    ComunicadosRecibidosScreen(
                        nombreHijo = hijoComunicadoSel?.nombres ?: "",
                        gradoNombre = hijoComunicadoSel?.gradoNombre ?: "",
                        seccionNombre = hijoComunicadoSel?.seccionNombre ?: "",
                        comunicados = comunicadosPadreSel,
                        onBack = { currentScreen = Screen.SELECCIONAR_ESTUDIANTE_COMUNICADO },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = {},
                        onAgenda = {currentScreen = Screen.AGENDA_ESCOLAR},
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = {},
                        onVerDetalle = { comunicado ->
                            comunicadoPadreSel = comunicado
                            currentScreen = Screen.DETALLE_COMUNICADO_PADRE
                        }
                    )
                }

                Screen.DETALLE_COMUNICADO_PADRE -> {
                    androidx.compose.runtime.LaunchedEffect(comunicadoPadreSel?.id) {
                        val padreId = usuarioLogueado?.id ?: return@LaunchedEffect
                        val comunicadoId = comunicadoPadreSel?.id ?: return@LaunchedEffect
                        try {
                            marcarComunicadoLeido(comunicadoId, padreId)
                            // Actualizar estado leído en la lista
                            comunicadosPadreSel = comunicadosPadreSel.map {
                                if (it.id == comunicadoId) it.copy(leido = true) else it
                            }
                        } catch (e: Exception) { }
                    }
                    DetalleComunicadoPadreScreen(
                        comunicado = comunicadoPadreSel,
                        onBack = { currentScreen = Screen.COMUNICADOS_RECIBIDOS },
                        onHomePadre = { currentScreen = Screen.HOME_PADRE },
                        onAvisos = {},
                        onAgenda = {currentScreen = Screen.AGENDA_ESCOLAR},
                        onPerfil = {currentScreen = Screen.PERFIL_PADRE},
                        onNotificaciones = {}
                    )
                }

                Screen.SELECCIONAR_SECCION_HISTORIAL -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        if (usuarioLogueado == null) return@LaunchedEffect
                        // Limpiar historial previo para que no se muestre al volver con otro curso
                        historialDocenteSel = emptyList()
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
                        docenteId = usuarioLogueado?.id ?: "",
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
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    val docenteId = usuarioLogueado?.id ?: return@launch
                                    val asistencias: List<com.educonnectapp.data.remote.AsistenciaHistorialRow> =
                                        obtenerAsistenciasPorDocente(docenteId, cursoId)
                                    val sdf    = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale("es", "PE"))
                                    val sdfOut = java.text.SimpleDateFormat("EEEE dd 'de' MMMM yyyy", java.util.Locale("es", "PE"))
                                    val agrupadas = asistencias.groupBy { it.fecha }
                                    historialDocenteSel = agrupadas.map { (fechaKey, registros) ->
                                        val presentes = registros.count { it.estado == "A" }
                                        val tardanzas = registros.count { it.estado == "T" }
                                        val ausentes  = registros.count { it.estado == "F" }
                                        val total     = registros.size
                                        val fechaDate = try { sdf.parse(fechaKey) } catch (e: Exception) { null }
                                        val fechaDisplay = fechaDate?.let {
                                            sdfOut.format(it).replaceFirstChar { c -> c.uppercase() }
                                        } ?: fechaKey
                                        val horaReg = registros.firstOrNull()?.hora ?: ""
                                        HistorialItem(
                                            id           = fechaKey.hashCode().toLong(),
                                            curso        = curso,
                                            grado        = grado,
                                            seccion      = seccion,
                                            fecha        = fechaKey,
                                            fechaDisplay = fechaDisplay,
                                            hora         = horaReg,
                                            presentes    = presentes,
                                            ausentes     = ausentes,
                                            total        = total,
                                            tardanzas    = tardanzas
                                        )
                                    }.sortedByDescending { it.fecha }
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
                        if (historialDocenteSel.isNotEmpty()) return@LaunchedEffect  // ya precargado desde selección
                        val docenteId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val asistencias: List<com.educonnectapp.data.remote.AsistenciaHistorialRow> =
                                obtenerAsistenciasPorDocente(docenteId, cursoIdSel)

                            val sdf    = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale("es", "PE"))
                            val sdfOut = java.text.SimpleDateFormat("EEEE dd 'de' MMMM yyyy", java.util.Locale("es", "PE"))

                            val agrupadas = asistencias.groupBy { it.fecha }
                            val lista = agrupadas.map { (fechaKey, registros) ->
                                val presentes  = registros.count { it.estado == "A" }
                                val tardanzas  = registros.count { it.estado == "T" }
                                val ausentes   = registros.count { it.estado == "F" }
                                val total      = registros.size
                                val fechaDate = try { sdf.parse(fechaKey) } catch (e: Exception) { null }
                                val fechaDisplay = fechaDate?.let {
                                    sdfOut.format(it).replaceFirstChar { c -> c.uppercase() }
                                } ?: fechaKey
                                val horaReg = registros.firstOrNull()?.hora ?: ""

                                HistorialItem(
                                    id           = fechaKey.hashCode().toLong(),
                                    curso        = cursoSel,
                                    grado        = gradoSel,
                                    seccion      = seccionSel,
                                    fecha        = fechaKey,
                                    fechaDisplay = fechaDisplay,
                                    hora         = horaReg,
                                    presentes    = presentes,
                                    ausentes     = ausentes,
                                    total        = total,
                                    tardanzas    = tardanzas
                                )
                            }.sortedByDescending { it.fecha }

                            historialDocenteSel = lista
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
                        val fecha     = historialItemSel?.fecha ?: return@LaunchedEffect
                        val docenteId = usuarioLogueado?.id     ?: return@LaunchedEffect
                        try {
                            val registros = obtenerAsistenciasDia(cursoIdSel, docenteId, fecha)
                            detalleAlumnosSel = registros.mapNotNull { reg ->
                                val alumno = obtenerAlumnoPorId(reg.alumno_id) ?: return@mapNotNull null
                                DetalleAlumnoItem(
                                    id        = alumno.id,
                                    nombres   = alumno.nombres,
                                    apellidos = alumno.apellidos,
                                    estado    = reg.estado
                                )
                            }.sortedBy { it.apellidos }
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
                                        val docenteId = usuarioLogueado?.id ?: return@launch
                                        guardarCambios(
                                            alumnosOriginales = detalleAlumnosSel,
                                            alumnosEditados   = alumnosEditados,
                                            asistenciaId      = item.id,
                                            docenteId         = docenteId,
                                            docenteNombre     = usuarioLogueado?.nombrecompleto ?: "",
                                            motivo            = motivo,
                                            cursoId           = cursoIdSel,
                                            fecha             = item.fecha,
                                            gradoId           = gradoIdSel,
                                            seccionId         = seccionIdSel,
                                            gradoNombre       = gradoSel,
                                            seccionNombre     = seccionSel,
                                            cursoNombre       = cursoSel
                                        )
                                        // Recargar los datos actualizados
                                        val registros = obtenerAsistenciasDia(cursoIdSel, docenteId, item.fecha)
                                        detalleAlumnosSel = registros.mapNotNull { reg ->
                                            val alumno = obtenerAlumnoPorId(reg.alumno_id) ?: return@mapNotNull null
                                            DetalleAlumnoItem(
                                                id        = alumno.id,
                                                nombres   = alumno.nombres,
                                                apellidos = alumno.apellidos,
                                                estado    = reg.estado
                                            )
                                        }.sortedBy { it.apellidos }
                                        onDone()
                                    } catch (e: Exception) {
                                        aviso("Error al guardar: ${e.message}")
                                        onDone()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
