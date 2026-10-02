package com.educonnectapp.data.api

import com.educonnectapp.BuildConfig
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
    if (token == SesionApi.fcmTokenRegistrado) return
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
