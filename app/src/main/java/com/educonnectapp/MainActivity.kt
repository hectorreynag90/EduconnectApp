package com.educonnectapp

import android.R.attr.filter
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
import com.educonnectapp.data.remote.ingresar
import com.educonnectapp.data.remote.insertarUsuario
import com.educonnectapp.data.remote.obtenerUsuario
import com.educonnectapp.data.remote.obtenerUsuarioActualId
import com.educonnectapp.data.remote.registrar
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
import com.educonnectapp.data.remote.cerrarSesion
import com.educonnectapp.data.remote.buscarAlumnoPorCodigo
import com.educonnectapp.data.remote.obtenerAlumnosPorSeccion
import com.educonnectapp.data.remote.obtenerAlumnoPorId
import com.educonnectapp.data.remote.obtenerTodosGrados
import com.educonnectapp.data.remote.obtenerGradoPorId
import com.educonnectapp.data.remote.obtenerGradoPorNombre
import com.educonnectapp.data.remote.obtenerTodasSecciones
import com.educonnectapp.data.remote.obtenerSeccionPorId
import com.educonnectapp.data.remote.obtenerSeccionPorNombreYGrado
import com.educonnectapp.data.remote.AsistenciaInsert
import com.educonnectapp.data.remote.obtenerAsistenciasDia
import com.educonnectapp.data.remote.insertarAsistencia
import com.educonnectapp.data.remote.actualizarAsistencia
import com.educonnectapp.data.remote.obtenerAsistenciasPorAlumno
import com.educonnectapp.data.remote.obtenerAsistenciasPorMes
import com.educonnectapp.data.remote.obtenerDetalleAsistencia
import com.educonnectapp.data.remote.obtenerTodosCursos
import com.educonnectapp.data.remote.obtenerCursoPorId
import com.educonnectapp.data.remote.obtenerCursoPorNombre
import com.educonnectapp.data.remote.DocenteSeccionInsert
import com.educonnectapp.data.remote.PadreAlumnoInsert
import com.educonnectapp.data.remote.obtenerDocenteSecciones
import com.educonnectapp.data.remote.obtenerDocenteSeccionesPorGrado
import com.educonnectapp.data.remote.obtenerDocenteSeccionesPorSeccion
import com.educonnectapp.data.remote.insertarDocenteSeccion
import com.educonnectapp.data.remote.obtenerHijosPadre
import com.educonnectapp.data.remote.insertarPadreAlumno
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
import com.educonnectapp.data.remote.insertarPublicacion
import com.educonnectapp.data.remote.obtenerResumenPublicaciones
import com.educonnectapp.ui.screens.PublicacionesScreen
import com.educonnectapp.ui.screens.SeleccionarCursoPublicacionScreen
import com.educonnectapp.ui.screens.NuevaTareaScreen
import com.educonnectapp.ui.screens.NuevaEvaluacionScreen
import com.educonnectapp.ui.screens.ConfirmacionPublicacionScreen
import com.educonnectapp.data.remote.obtenerPublicacionesPorAlumno
import com.educonnectapp.data.remote.contarTareasPendientes
import com.educonnectapp.data.remote.PublicacionDetalleRow
import com.educonnectapp.ui.screens.SeleccionarHijoAgendaScreen
import com.educonnectapp.ui.screens.AgendaEscolarScreen
import com.educonnectapp.ui.screens.DetalleAgendaScreen
import com.educonnectapp.ui.screens.HijoAgendaItem
import com.educonnectapp.ui.screens.PublicacionAgendaItem
import com.educonnectapp.data.remote.marcarComunicadoLeido
import com.educonnectapp.data.remote.obtenerComunicadosPadre
import com.educonnectapp.data.remote.obtenerLecturasComunicado
import com.educonnectapp.data.remote.supabase
import com.educonnectapp.ui.screens.ComunicadosPadresScreen
import com.educonnectapp.ui.screens.SeleccionarEstudianteComunicadoScreen
import com.educonnectapp.ui.screens.ComunicadosRecibidosScreen
import com.educonnectapp.ui.screens.DetalleComunicadoPadreScreen
import com.educonnectapp.ui.screens.HijoComunicadoItem
import com.educonnectapp.ui.screens.ComunicadoPadreItem
import io.github.jan.supabase.postgrest.postgrest

enum class Screen {
    LOGIN, REGISTER, HOME_DOCENTE, HOME_PADRE, ASISTENCIAS,
    SELECCIONAR_SECCION, REGISTRO_ASISTENCIA, CONFIRMACION_ASISTENCIA,
    SELECCIONAR_ESTUDIANTE, HISTORIAL_ASISTENCIAS, DETALLE_ASISTENCIA,
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EduConnectAppTheme { EduConnectApp() } }
    }
}

@Composable
fun EduConnectApp() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
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
    var comunicadoPadreSel by remember { mutableStateOf<ComunicadoPadreItem?>(null) }
    var sinLeerPadreSel by remember { mutableStateOf(0) }
    var leidosPadreSel by remember { mutableStateOf(0) }
    var totalPadreSel by remember { mutableStateOf(0) }

    fun aviso(mensaje: String) { Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show() }

    Scaffold(modifier = Modifier.fillMaxSize(), contentColor = TextPrimary) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {

                Screen.LOGIN -> LoginScreen(
                    isLoading = isLoginLoading,
                    onLogin = { emailInput, passwordInput ->
                        when {
                            emailInput.isBlank() || passwordInput.isBlank() -> aviso("Completa el correo y contraseña")
                            else -> {
                                isLoginLoading = true
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        ingresar(emailInput, passwordInput)
                                        val userId = obtenerUsuarioActualId()
                                        if (userId != null) {
                                            val usuario = obtenerUsuario(userId)
                                            usuarioLogueado = UsuarioLogueado(
                                                id = usuario.id,
                                                nombrecompleto = usuario.nombrecompleto,
                                                email = usuario.email,
                                                rol = usuario.rol,
                                                dni = usuario.dni,
                                                telefono = usuario.telefono
                                            )
                                            currentScreen = if (usuario.rol == "Docente") Screen.HOME_DOCENTE else Screen.HOME_PADRE
                                        }
                                    } catch (e: Exception) { aviso("Correo o contraseña incorrectos"); isLoginLoading = false }
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
                            else -> {
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        registrar(email, password)
                                        val userId = obtenerUsuarioActualId()
                                        if (userId != null) {
                                            insertarUsuario(userId, nombre, dni, telefono, email, rol)
                                            aviso("Cuenta creada exitosamente")
                                            currentScreen = Screen.LOGIN
                                        } else aviso("Error al crear la cuenta")
                                    } catch (e: Exception) { aviso("Error: ${e.message ?: e.toString()}") }
                                }
                            }
                        }
                    },
                    onBack = { currentScreen = Screen.LOGIN }
                )

                Screen.HOME_DOCENTE -> HomeDocenteScreen(
                    usuarioNombre = usuarioLogueado?.nombrecompleto ?: "",
                    onAsistencias = { currentScreen = Screen.ASISTENCIAS },
                    onComunicados = { currentScreen = Screen.COMUNICADOS },
                    onPublicaciones = { currentScreen = Screen.PUBLICACIONES },
                    onAlumnos = {},
                    onAvisos = { currentScreen = Screen.COMUNICADOS },
                    onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                    onNotificaciones = {}
                )

                Screen.HOME_PADRE -> HomePadreScreen(
                    usuarioNombre = usuarioLogueado?.nombrecompleto ?: "",
                    onAsistencias = { currentScreen = Screen.SELECCIONAR_ESTUDIANTE },
                    onComunicados = { currentScreen = Screen.COMUNICADOS_PADRE },
                    onAgenda = { currentScreen = Screen.SELECCIONAR_HIJO_AGENDA },
                    onEstadoAcademico = {},
                    onAvisos = {},
                    onPerfil = { currentScreen = Screen.PERFIL_PADRE },
                    onNotificaciones = {}
                )

                Screen.ASISTENCIAS -> AsistenciasScreen(
                    onBack = { currentScreen = Screen.HOME_DOCENTE },
                    onRegistrarAsistencia = { currentScreen = Screen.SELECCIONAR_SECCION },
                    onHistorial = {},
                    onHomeDocente = { currentScreen = Screen.HOME_DOCENTE },
                    onAlumnos = {},
                    onAvisos = { currentScreen = Screen.COMUNICADOS },
                    onPerfilDocente = { currentScreen = Screen.PERFIL_DOCENTE },
                    onNotificaciones = {}
                )

                Screen.SELECCIONAR_SECCION -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        val docenteId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val relaciones = obtenerDocenteSecciones(docenteId)
                            val gradoIds = relaciones.map { it.grado_id }.distinct()
                            listaGradosSel = gradoIds.mapNotNull { gradoId ->
                                val g = obtenerGradoPorId(gradoId) ?: return@mapNotNull null
                                GradoItem(g.id, g.nombre)
                            }
                            listaSeccionesSel = emptyList(); listaCursosSel = emptyList()
                        } catch (e: Exception) { aviso("Error cargando grados: ${e.message}") }
                    }
                    SeleccionarSeccionScreen(
                        docenteId = usuarioLogueado?.id ?: "",
                        listaGrados = listaGradosSel, listaSecciones = listaSeccionesSel,
                        listaCursos = listaCursosSel, cantidadAlumnos = cantidadAlumnosSel,
                        onGradoSeleccionado = { grado ->
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    val docenteId = usuarioLogueado?.id ?: return@launch
                                    val relaciones = obtenerDocenteSeccionesPorGrado(docenteId, grado.id)
                                    listaSeccionesSel = relaciones.map { it.seccion_id }.distinct().mapNotNull { seccionId ->
                                        val s = obtenerSeccionPorId(seccionId) ?: return@mapNotNull null
                                        SeccionItem(s.id, s.nombre)
                                    }
                                    listaCursosSel = emptyList(); cantidadAlumnosSel = 0
                                } catch (e: Exception) { aviso("Error cargando secciones: ${e.message}") }
                            }
                        },
                        onSeccionSeleccionada = { seccion ->
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    val docenteId = usuarioLogueado?.id ?: return@launch
                                    val relaciones = obtenerDocenteSeccionesPorSeccion(docenteId, seccion.id)
                                    listaCursosSel = relaciones.mapNotNull { rel ->
                                        val c = obtenerCursoPorId(rel.curso_id) ?: return@mapNotNull null
                                        CursoItem(c.id, c.nombre)
                                    }
                                    cantidadAlumnosSel = obtenerAlumnosPorSeccion(seccion.id).size
                                } catch (e: Exception) { aviso("Error cargando cursos: ${e.message}") }
                            }
                        },
                        onBack = { currentScreen = Screen.ASISTENCIAS },
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
                    androidx.compose.runtime.LaunchedEffect(seccionIdSel) {
                        try {
                            listaAlumnosSel = obtenerAlumnosPorSeccion(seccionIdSel).map { AlumnoItem(id = it.id, nombres = it.nombres, apellidos = it.apellidos) }
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
                        onGuardado = { presentes, ausentes, estados, hora, fecha ->
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    val docenteId = usuarioLogueado?.id ?: return@launch
                                    if (asistenciaExistente.isNotEmpty()) {
                                        estados.forEach { (alumnoId, estado) ->
                                            if (asistenciaExistente[alumnoId] != estado) {
                                                actualizarAsistencia(alumnoId, cursoIdSel, docenteId, fecha, estado)
                                            }
                                        }
                                    } else {
                                        estados.forEach { (alumnoId, estado) ->
                                            insertarAsistencia(AsistenciaInsert(alumno_id = alumnoId, curso_id = cursoIdSel, docente_id = docenteId, fecha = fecha, hora = hora, estado = estado))
                                        }
                                    }
                                    presentesSel = presentes; ausentesSel = ausentes
                                    currentScreen = Screen.CONFIRMACION_ASISTENCIA
                                } catch (e: Exception) { aviso("Error al guardar asistencia: ${e.message}") }
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
                        val docenteId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val relaciones = obtenerDocenteSecciones(docenteId)
                            val lista = mutableListOf<AsignacionItem>()
                            relaciones.forEach { rel ->
                                val grado = obtenerGradoPorId(rel.grado_id) ?: return@forEach
                                val seccion = obtenerSeccionPorId(rel.seccion_id) ?: return@forEach
                                val curso = obtenerCursoPorId(rel.curso_id) ?: return@forEach
                                lista.add(AsignacionItem(grado.nombre, seccion.nombre, curso.nombre))
                            }
                            asignacionesSel = lista
                        } catch (e: Exception) { }
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
                                usuarioLogueado = null
                                isLoginLoading = false
                                currentScreen = Screen.LOGIN
                            }
                        }
                    )
                }

                Screen.PERFIL_PADRE -> {
                    androidx.compose.runtime.LaunchedEffect(usuarioLogueado?.id) {
                        val padreId = usuarioLogueado?.id ?: return@LaunchedEffect
                        try {
                            val relaciones = obtenerHijosPadre(padreId)
                            val lista = mutableListOf<HijoAsociado>()
                            relaciones.forEach { rel ->
                                val alumno = buscarAlumnoPorCodigo(rel.codigo_estudiante) ?: return@forEach
                                val grado = obtenerGradoPorId(alumno.grado_id) ?: return@forEach
                                val seccion = obtenerSeccionPorId(alumno.seccion_id) ?: return@forEach
                                lista.add(HijoAsociado(id = alumno.id, nombres = alumno.nombres, apellidos = alumno.apellidos, gradoNombre = grado.nombre, seccionNombre = seccion.nombre, codigoEstudiante = alumno.codigo_estudiante))
                            }
                            hijosAsociadosSel = lista
                        } catch (e: Exception) { }
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
                                usuarioLogueado = null
                                isLoginLoading = false
                                currentScreen = Screen.LOGIN
                            }
                        }
                    )
                }

                Screen.AGREGAR_ASIGNACION -> {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        try {
                            val grados = obtenerTodosGrados()
                            val secciones = obtenerTodasSecciones()
                            val cursos = obtenerTodosCursos()
                            gradosDisp = grados.map { it.nombre }
                            val seccionesMap = mutableMapOf<String, List<String>>()
                            grados.forEach { grado -> seccionesMap[grado.nombre] = secciones.filter { it.grado_id == grado.id }.map { it.nombre } }
                            seccionesDisp = seccionesMap
                            cursosDisp = cursos.map { it.nombre }
                        } catch (e: Exception) { aviso("Error cargando datos: ${e.message}") }
                    }
                    AgregarAsignacionScreen(
                        onBack = { currentScreen = Screen.PERFIL_DOCENTE }, onNotificaciones = {},
                        gradosDisp = gradosDisp, seccionesDisp = seccionesDisp, cursosDisp = cursosDisp,
                        onGuardar = { grado, seccion, curso ->
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    val docenteId = usuarioLogueado?.id ?: return@launch
                                    val gradoRow = obtenerGradoPorNombre(grado) ?: return@launch
                                    val seccionRow = obtenerSeccionPorNombreYGrado(seccion, gradoRow.id) ?: return@launch
                                    val cursoRow = obtenerCursoPorNombre(curso) ?: return@launch
                                    insertarDocenteSeccion(DocenteSeccionInsert(docente_id = docenteId, grado_id = gradoRow.id, seccion_id = seccionRow.id, curso_id = cursoRow.id))
                                    asignacionesSel = asignacionesSel + AsignacionItem(grado, seccion, curso)
                                    aviso("Asignación agregada"); currentScreen = Screen.PERFIL_DOCENTE
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
                                val alumno = buscarAlumnoPorCodigo(codigo)
                                if (alumno == null) {
                                    errorBusquedaSel = "No se encontró ningún estudiante con el código $codigo"
                                } else {
                                    val grado = obtenerGradoPorId(alumno.grado_id) ?: return@launch
                                    val seccion = obtenerSeccionPorId(alumno.seccion_id) ?: return@launch
                                    alumnoEncontradoSel = AlumnoEncontrado(id = alumno.id, nombres = alumno.nombres, apellidos = alumno.apellidos, gradoNombre = grado.nombre, seccionNombre = seccion.nombre, codigoEstudiante = alumno.codigo_estudiante)
                                }
                            } catch (e: Exception) { errorBusquedaSel = "Error al buscar: ${e.message}" }
                        }
                    },
                    alumnoEncontrado = alumnoEncontradoSel, mensajeError = errorBusquedaSel,
                    onConfirmar = { alumno ->
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                val padreId = usuarioLogueado?.id ?: return@launch
                                insertarPadreAlumno(PadreAlumnoInsert(padre_id = padreId, codigo_estudiante = alumno.codigoEstudiante))
                                aviso("Hijo asociado correctamente")
                                alumnoEncontradoSel = null; errorBusquedaSel = ""
                                hijosAsociadosSel = hijosAsociadosSel + HijoAsociado(id = alumno.id, nombres = alumno.nombres, apellidos = alumno.apellidos, gradoNombre = alumno.gradoNombre, seccionNombre = alumno.seccionNombre, codigoEstudiante = alumno.codigoEstudiante)
                                currentScreen = Screen.PERFIL_PADRE
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
            }
        }
    }
}
