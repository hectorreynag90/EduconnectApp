package com.educonnectapp.data.api

import com.educonnectapp.BuildConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.util.concurrent.TimeUnit

// =====================================================================
// SESION (token JWT en memoria)
// =====================================================================

object SesionApi {
    @Volatile var token: String? = null
    // Token FCM recibido antes del login: se registra al iniciar sesión
    @Volatile var fcmTokenPendiente: String? = null
    // Último token FCM registrado en el backend: se elimina al cerrar sesión
    @Volatile var fcmTokenRegistrado: String? = null

    fun limpiar() {
        token = null
        fcmTokenRegistrado = null
    }
}

// =====================================================================
// ERRORES
// =====================================================================

class ApiException(val codigo: Int, mensaje: String) : Exception(mensaje)

private val json = Json {
    ignoreUnknownKeys = true   // el backend puede agregar campos sin romper la app
    explicitNulls = false
    coerceInputValues = true
}

// Convierte cualquier error de red/HTTP en un mensaje legible para el usuario
private suspend fun <T> llamar(bloque: suspend () -> T): T {
    try {
        return bloque()
    } catch (e: HttpException) {
        val cuerpo = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
        throw ApiException(e.code(), extraerMensaje(cuerpo) ?: mensajePorCodigo(e.code()))
    } catch (e: IOException) {
        throw ApiException(0, "No se pudo conectar con el servidor. Revisa tu conexión.")
    }
}

private fun extraerMensaje(cuerpo: String?): String? {
    if (cuerpo.isNullOrBlank()) return null
    return try {
        val obj = json.parseToJsonElement(cuerpo) as? JsonObject ?: return null
        listOf("mensaje", "message", "detail", "error")
            .firstNotNullOfOrNull { clave -> obj[clave]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() } }
    } catch (_: Exception) { null }
}

private fun mensajePorCodigo(codigo: Int): String = when (codigo) {
    400 -> "Datos inválidos"
    401 -> "Sesión no válida. Vuelve a iniciar sesión."
    403 -> "No tienes permiso para esta acción"
    404 -> "Recurso no encontrado"
    409 -> "El registro ya existe"
    else -> "Error del servidor ($codigo)"
}

// =====================================================================
// CLIENTE HTTP
// =====================================================================

private val interceptorAuth = Interceptor { chain ->
    val token = SesionApi.token
    val request = if (token != null) {
        chain.request().newBuilder().header("Authorization", "Bearer $token").build()
    } else chain.request()
    chain.proceed(request)
}

private val okHttp = OkHttpClient.Builder()
    .addInterceptor(interceptorAuth)
    .addInterceptor(HttpLoggingInterceptor().apply {
        // BASIC: solo método, URL y código (no registra contraseñas ni tokens)
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
        else HttpLoggingInterceptor.Level.NONE
    })
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)   // subida de adjuntos
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.API_URL)
    .client(okHttp)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()

private val api: EduConnectApi = retrofit.create(EduConnectApi::class.java)

// =====================================================================
// ENDPOINTS
// =====================================================================

interface EduConnectApi {
    // Autenticación
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("api/auth/registro")
    suspend fun registro(@Body body: RegistroRequest): AuthResponse

    @GET("api/auth/me")
    suspend fun me(): UsuarioResponse

    // Dispositivos (tokens FCM)
    @POST("api/dispositivos")
    suspend fun registrarDispositivo(@Body body: RegistrarDispositivoRequest)

    @DELETE("api/dispositivos")
    suspend fun eliminarDispositivo(@Query("fcmToken") fcmToken: String)

    // Catálogos
    @GET("api/catalogos/grados")
    suspend fun grados(): List<GradoResponse>

    @GET("api/catalogos/grados/{gradoId}/secciones")
    suspend fun secciones(@Path("gradoId") gradoId: Long): List<SeccionResponse>

    @GET("api/catalogos/cursos")
    suspend fun cursos(): List<CursoResponse>

    // Asignaciones del docente
    @GET("api/docente/asignaciones")
    suspend fun asignaciones(): List<AsignacionResponse>

    @POST("api/docente/asignaciones")
    suspend fun agregarAsignacion(@Body body: AsignacionRequest): AsignacionResponse

    @DELETE("api/docente/asignaciones/{id}")
    suspend fun eliminarAsignacion(@Path("id") id: Long)

    // Hijos del padre
    @GET("api/padre/hijos")
    suspend fun hijos(): List<HijoResponse>

    @GET("api/padre/hijos/buscar")
    suspend fun buscarAlumno(@Query("codigo") codigo: String): BusquedaAlumnoResponse

    @POST("api/padre/hijos")
    suspend fun asociarHijo(@Body body: AsociarHijoRequest): HijoResponse

    @DELETE("api/padre/hijos/{alumnoId}")
    suspend fun desasociarHijo(@Path("alumnoId") alumnoId: Long)

    // Asistencias docente
    @GET("api/docente/asistencias")
    suspend fun hojaAsistencia(
        @Query("seccionId") seccionId: Long,
        @Query("cursoId") cursoId: Long,
        @Query("fecha") fecha: String? = null          // null = hoy (fecha del servidor)
    ): HojaAsistenciaResponse

    @POST("api/docente/asistencias")
    suspend fun registrarAsistencia(@Body body: RegistrarAsistenciaRequest): HojaAsistenciaResponse

    @PUT("api/docente/asistencias")
    suspend fun actualizarAsistencia(@Body body: ActualizarAsistenciaRequest): ActualizacionAsistenciaResponse

    @GET("api/docente/asistencias/historial")
    suspend fun historialAsistencia(
        @Query("seccionId") seccionId: Long,
        @Query("cursoId") cursoId: Long,
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null
    ): List<DiaHistorialResponse>

    // Asistencias padre
    @GET("api/padre/hijos/{alumnoId}/asistencias")
    suspend fun asistenciasHijo(
        @Path("alumnoId") alumnoId: Long,
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null
    ): HistorialHijoResponse
}

// =====================================================================
// DATA CLASSES AUTENTICACION
// =====================================================================

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegistroRequest(
    val nombreCompleto: String,
    val dni: String,
    val telefono: String,
    val email: String,
    val password: String,
    val rol: String            // "DOCENTE" o "PADRE"
)

@Serializable
data class UsuarioResponse(
    val id: Long,
    val nombreCompleto: String,
    val dni: String = "",
    val telefono: String = "",
    val email: String,
    val rol: String            // "DOCENTE", "PADRE" o "ADMIN"
)

@Serializable
data class AuthResponse(
    val token: String,
    val tipo: String = "Bearer",
    val expiraEnSegundos: Long = 0,
    val usuario: UsuarioResponse
)

@Serializable
data class RegistrarDispositivoRequest(
    val fcmToken: String,
    val plataforma: String = "ANDROID"
)

// =====================================================================
// AUTENTICACION
// =====================================================================

// Inicia sesión, guarda el token y devuelve el usuario
suspend fun ingresar(email: String, password: String): UsuarioResponse = llamar {
    val respuesta = api.login(LoginRequest(email.trim(), password))
    SesionApi.token = respuesta.token
    // Si llegó un token FCM antes del login, registrarlo ahora
    SesionApi.fcmTokenPendiente?.let { pendiente ->
        try { guardarFcmToken(pendiente) } catch (_: Exception) { }
    }
    respuesta.usuario
}

// Crea la cuenta. No inicia sesión: el usuario vuelve a LOGIN (mismo flujo que antes)
suspend fun registrar(
    nombre: String, dni: String, telefono: String,
    email: String, password: String, rol: String
): UsuarioResponse = llamar {
    api.registro(
        RegistroRequest(
            nombreCompleto = nombre.trim(),
            dni = dni.trim(),
            telefono = telefono.trim(),
            email = email.trim(),
            password = password,
            rol = rol.uppercase()      // "Docente" -> "DOCENTE"
        )
    ).usuario
}

suspend fun obtenerUsuarioActual(): UsuarioResponse = llamar { api.me() }

// Quita el token FCM del backend (para no recibir push de otra cuenta) y borra la sesión
suspend fun cerrarSesion() {
    val fcm = SesionApi.fcmTokenRegistrado
    try {
        if (fcm != null && SesionApi.token != null) llamar { api.eliminarDispositivo(fcm) }
    } catch (_: Exception) { /* si falla, igual se cierra la sesión local */ }
    SesionApi.limpiar()
}

// =====================================================================
// FCM TOKEN
// =====================================================================

// Registra el token FCM del dispositivo. Sin sesión, lo deja pendiente hasta el login.
suspend fun guardarFcmToken(token: String) {
    SesionApi.fcmTokenPendiente = token
    if (SesionApi.token == null) return
    if (token == SesionApi.fcmTokenRegistrado) return   // ya registrado en esta sesión
    llamar { api.registrarDispositivo(RegistrarDispositivoRequest(fcmToken = token)) }
    SesionApi.fcmTokenRegistrado = token
}

// =====================================================================
// DATA CLASSES CATALOGOS
// =====================================================================

@Serializable
data class GradoResponse(val id: Long, val nombre: String)

@Serializable
data class SeccionResponse(val id: Long, val nombre: String)

@Serializable
data class CursoResponse(val id: Long, val nombre: String)

// =====================================================================
// DATA CLASSES ASIGNACIONES DOCENTE
// =====================================================================

@Serializable
data class AsignacionRequest(val seccionId: Long, val cursoId: Long)

// Ya trae nombres y cantidad de alumnos: no hace falta consultar grado/sección/curso aparte
@Serializable
data class AsignacionResponse(
    val id: Long,
    val gradoId: Long,
    val grado: String,
    val seccionId: Long,
    val seccion: String,
    val cursoId: Long,
    val curso: String,
    val cantidadAlumnos: Long = 0
)

// =====================================================================
// DATA CLASSES HIJOS PADRE
// =====================================================================

@Serializable
data class HijoResponse(
    val alumnoId: Long,
    val codigoEstudiante: String,
    val nombres: String,
    val apellidos: String,
    val nombreCompleto: String = "",
    val gradoId: Long,
    val grado: String,
    val seccionId: Long,
    val seccion: String
)

@Serializable
data class BusquedaAlumnoResponse(
    val alumno: HijoResponse,
    val yaAsociado: Boolean = false
)

@Serializable
data class AsociarHijoRequest(val codigoEstudiante: String)

// =====================================================================
// CATALOGOS
// =====================================================================

suspend fun obtenerGrados(): List<GradoResponse> = llamar { api.grados() }

suspend fun obtenerSeccionesDeGrado(gradoId: Long): List<SeccionResponse> =
    llamar { api.secciones(gradoId) }

suspend fun obtenerCursos(): List<CursoResponse> = llamar { api.cursos() }

// =====================================================================
// ASIGNACIONES DOCENTE (el docente se identifica por el token)
// =====================================================================

suspend fun obtenerAsignaciones(): List<AsignacionResponse> = llamar { api.asignaciones() }

suspend fun agregarAsignacion(seccionId: Long, cursoId: Long): AsignacionResponse =
    llamar { api.agregarAsignacion(AsignacionRequest(seccionId, cursoId)) }

suspend fun eliminarAsignacion(asignacionId: Long) = llamar { api.eliminarAsignacion(asignacionId) }

// =====================================================================
// HIJOS PADRE (el padre se identifica por el token)
// =====================================================================

suspend fun obtenerHijos(): List<HijoResponse> = llamar { api.hijos() }

// Devuelve null si no existe ningún alumno con ese código
suspend fun buscarAlumno(codigo: String): BusquedaAlumnoResponse? {
    return try {
        llamar { api.buscarAlumno(codigo.trim().uppercase()) }
    } catch (e: ApiException) {
        if (e.codigo == 404) null else throw e
    }
}

suspend fun asociarHijo(codigo: String): HijoResponse =
    llamar { api.asociarHijo(AsociarHijoRequest(codigo.trim().uppercase())) }

suspend fun desasociarHijo(alumnoId: Long) = llamar { api.desasociarHijo(alumnoId) }

// =====================================================================
// DATA CLASSES ASISTENCIAS
// =====================================================================

@Serializable
data class ResumenConteo(
    val total: Long = 0,
    val asistieron: Long = 0,
    val faltas: Long = 0,
    val tardanzas: Long = 0
)

@Serializable
data class AlumnoAsistenciaResponse(
    val alumnoId: Long,
    val codigoEstudiante: String = "",
    val nombreCompleto: String,
    val asistenciaId: Long? = null,
    val estado: String? = null,      // "A", "F", "T" o null si aún no se registra
    val hora: String? = null         // "HH:mm:ss"
)

@Serializable
data class HojaAsistenciaResponse(
    val seccionId: Long,
    val cursoId: Long,
    val fecha: String,               // "yyyy-MM-dd"
    val yaRegistrado: Boolean = false,
    val resumen: ResumenConteo = ResumenConteo(),
    val alumnos: List<AlumnoAsistenciaResponse> = emptyList()
)

@Serializable
data class EstadoAlumnoRequest(val alumnoId: Long, val estado: String)

@Serializable
data class RegistrarAsistenciaRequest(
    val seccionId: Long,
    val cursoId: Long,
    val fecha: String? = null,
    val registros: List<EstadoAlumnoRequest>
)

@Serializable
data class ActualizarAsistenciaRequest(
    val seccionId: Long,
    val cursoId: Long,
    val fecha: String,
    val motivo: String,
    val cambios: List<EstadoAlumnoRequest>
)

@Serializable
data class CambioRealizadoResponse(
    val alumnoId: Long,
    val alumno: String = "",
    val estadoAnterior: String = "",
    val estadoNuevo: String = ""
)

@Serializable
data class ActualizacionAsistenciaResponse(
    val totalActualizados: Int = 0,
    val cambios: List<CambioRealizadoResponse> = emptyList()
)

@Serializable
data class DiaHistorialResponse(
    val fecha: String,
    val hora: String = "",
    val resumen: ResumenConteo = ResumenConteo()
)

@Serializable
data class AsistenciaHijoResponse(
    val asistenciaId: Long,
    val fecha: String,
    val hora: String = "",
    val estado: String,
    val cursoId: Long,
    val curso: String = "",
    val docente: String = ""
)

@Serializable
data class HistorialHijoResponse(
    val alumnoId: Long,
    val desde: String = "",
    val hasta: String = "",
    val resumen: ResumenConteo = ResumenConteo(),
    val asistencias: List<AsistenciaHijoResponse> = emptyList()
)

// =====================================================================
// ASISTENCIAS DOCENTE
// (el backend envía el push a los padres, audita los cambios y avisa al director)
// =====================================================================

// Hoja del día: alumnos de la sección con su estado (si ya se registró)
suspend fun obtenerHojaAsistencia(seccionId: Long, cursoId: Long, fecha: String? = null): HojaAsistenciaResponse =
    llamar { api.hojaAsistencia(seccionId, cursoId, fecha) }

// Registro inicial del día (todos los alumnos)
suspend fun registrarAsistencia(seccionId: Long, cursoId: Long, estados: Map<Long, String>): HojaAsistenciaResponse =
    llamar {
        api.registrarAsistencia(
            RegistrarAsistenciaRequest(
                seccionId = seccionId,
                cursoId = cursoId,
                registros = estados.map { (alumnoId, estado) -> EstadoAlumnoRequest(alumnoId, estado) }
            )
        )
    }

// Edición de un día ya registrado (solo los alumnos que cambiaron)
suspend fun actualizarAsistenciaDia(
    seccionId: Long, cursoId: Long, fecha: String,
    motivo: String, cambios: Map<Long, String>
): ActualizacionAsistenciaResponse = llamar {
    api.actualizarAsistencia(
        ActualizarAsistenciaRequest(
            seccionId = seccionId,
            cursoId = cursoId,
            fecha = fecha,
            motivo = motivo.trim(),
            cambios = cambios.map { (alumnoId, estado) -> EstadoAlumnoRequest(alumnoId, estado) }
        )
    )
}

// Historial resumido por día de una sección y curso
suspend fun obtenerHistorialDocente(
    seccionId: Long, cursoId: Long, desde: String? = null, hasta: String? = null
): List<DiaHistorialResponse> = llamar { api.historialAsistencia(seccionId, cursoId, desde, hasta) }

// Resumen del día para AsistenciasScreen: (realizados, pendientes, % asistencia)
// 1 petición por asignación, todas en paralelo
suspend fun obtenerResumenAsistenciasHoy(asignaciones: List<AsignacionResponse>): Triple<Int, Int, Int> {
    if (asignaciones.isEmpty()) return Triple(0, 0, 0)
    val hojas = coroutineScope {
        asignaciones.map { a -> async { obtenerHojaAsistencia(a.seccionId, a.cursoId) } }.awaitAll()
    }
    val realizadas = hojas.filter { it.yaRegistrado }
    // Se cuenta desde los estados de cada alumno: Presente (A) y Tardanza (T) son asistencia
    val estados = realizadas.flatMap { h -> h.alumnos.mapNotNull { it.estado } }
    val porcentaje = porcentajeAsistencia(estados)
    return Triple(realizadas.size, hojas.size - realizadas.size, porcentaje)
}

// =====================================================================
// ASISTENCIAS PADRE
// =====================================================================

suspend fun obtenerAsistenciasHijo(
    alumnoId: Long, desde: String? = null, hasta: String? = null
): HistorialHijoResponse = llamar { api.asistenciasHijo(alumnoId, desde, hasta) }

// =====================================================================
// REGLAS DE ASISTENCIA
// =====================================================================

// % de asistencia: Presente (A) y Tardanza (T) cuentan como asistió; solo Falta (F) resta
fun porcentajeAsistencia(estados: List<String>): Int {
    if (estados.isEmpty()) return 0
    val asistio = estados.count { it == "A" || it == "T" }
    return (asistio * 100) / estados.size
}

// Inicio del año escolar (Perú: marzo). Ajusta MES/DIA si la I.E. inicia en otra fecha.
private const val INICIO_ESCOLAR_MES = 3
private const val INICIO_ESCOLAR_DIA = 1

// Fecha de inicio del año escolar vigente: si aún no llega marzo, es el del año anterior
fun inicioAnioEscolar(hoy: java.time.LocalDate = java.time.LocalDate.now()): java.time.LocalDate {
    val inicioEsteAnio = java.time.LocalDate.of(hoy.year, INICIO_ESCOLAR_MES, INICIO_ESCOLAR_DIA)
    return if (hoy.isBefore(inicioEsteAnio)) inicioEsteAnio.minusYears(1) else inicioEsteAnio
}

// =====================================================================
// UTILIDADES DE FORMATO
// =====================================================================

// "14:35:00" -> "02:35 p. m."
fun formatearHora(hora: String?): String {
    if (hora.isNullOrBlank()) return ""
    return try {
        val entrada = java.text.SimpleDateFormat("HH:mm", java.util.Locale.US).parse(hora.take(5))
        java.text.SimpleDateFormat("hh:mm a", java.util.Locale("es", "PE")).format(entrada!!)
    } catch (e: Exception) { hora.take(5) }
}

// "2026-10-02" -> "Viernes 02 de octubre 2026"
fun formatearFechaLarga(fecha: String): String {
    return try {
        val d = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(fecha)
        java.text.SimpleDateFormat("EEEE dd 'de' MMMM yyyy", java.util.Locale("es", "PE"))
            .format(d!!).replaceFirstChar { it.uppercase() }
    } catch (e: Exception) { fecha }
}
