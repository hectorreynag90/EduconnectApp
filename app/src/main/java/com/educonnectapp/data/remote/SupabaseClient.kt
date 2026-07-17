package com.educonnectapp.data.remote

import com.educonnectapp.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable

val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY
) {
    install(Auth)
    install(Postgrest)
}

// CLASSES

@Serializable
data class UsuarioSupabase(
    val id: String,
    val nombrecompleto: String,
    val dni: String,
    val telefono: String,
    val email: String,
    val rol: String
)

// AUTENTICACION

suspend fun ingresar(email: String, password: String) {
    supabase.auth.signInWith(Email) {
        this.email = email.trim()
        this.password = password
    }
}

suspend fun registrar(email: String, password: String) {
    supabase.auth.signUpWith(Email) {
        this.email = email.trim()
        this.password = password
    }
}

suspend fun cerrarSesion() {
    supabase.auth.signOut()
}

suspend fun obtenerUsuario(userId: String): UsuarioSupabase {
    return supabase.postgrest["usuarios"]
        .select(Columns.ALL) { filter { eq("id", userId) } }
        .decodeSingle<UsuarioSupabase>()
}

suspend fun insertarUsuario(
    id: String, nombre: String, dni: String,
    telefono: String, email: String, rol: String
) {
    supabase.postgrest["usuarios"].insert(
        mapOf(
            "id" to id,
            "nombrecompleto" to nombre,
            "dni" to dni,
            "telefono" to telefono,
            "email" to email.trim(),
            "rol" to rol
        )
    )
}

fun obtenerUsuarioActualId(): String? {
    return supabase.auth.currentUserOrNull()?.id
}

// DATA CLASSES ALUMNOS

@Serializable
data class AlumnoRow(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val codigo_estudiante: String,
    val grado_id: Long,
    val seccion_id: Long
)

@Serializable
data class AlumnoSeccionRow(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val seccion_id: Long
)

// ALUMNOS

suspend fun buscarAlumnoPorCodigo(codigo: String): AlumnoRow? {
    return supabase.postgrest["alumnos"]
        .select(Columns.ALL) { filter { eq("codigo_estudiante", codigo.trim()) } }
        .decodeList<AlumnoRow>()
        .firstOrNull()
}

suspend fun obtenerAlumnosPorSeccion(seccionId: Long): List<AlumnoSeccionRow> {
    return supabase.postgrest["alumnos"]
        .select(Columns.ALL) { filter { eq("seccion_id", seccionId) } }
        .decodeList<AlumnoSeccionRow>()
}

suspend fun obtenerAlumnoPorId(alumnoId: Long): AlumnoRow? {
    return supabase.postgrest["alumnos"]
        .select(Columns.ALL) { filter { eq("id", alumnoId) } }
        .decodeList<AlumnoRow>()
        .firstOrNull()
}

// DATA CLASSES GRADOS Y SECCIONES

@Serializable
data class GradoRow(
    val id: Long,
    val nombre: String
)

@Serializable
data class SeccionRow(
    val id: Long,
    val nombre: String,
    val grado_id: Long = 0L
)

// GRADOS

suspend fun obtenerTodosGrados(): List<GradoRow> {
    return supabase.postgrest["grados"]
        .select(Columns.ALL)
        .decodeList<GradoRow>()
}

suspend fun obtenerGradoPorId(gradoId: Long): GradoRow? {
    return supabase.postgrest["grados"]
        .select(Columns.ALL) { filter { eq("id", gradoId) } }
        .decodeList<GradoRow>()
        .firstOrNull()
}

suspend fun obtenerGradoPorNombre(nombre: String): GradoRow? {
    return supabase.postgrest["grados"]
        .select(Columns.ALL) { filter { eq("nombre", nombre) } }
        .decodeList<GradoRow>()
        .firstOrNull()
}

// SECCIONES

suspend fun obtenerTodasSecciones(): List<SeccionRow> {
    return supabase.postgrest["secciones"]
        .select(Columns.ALL)
        .decodeList<SeccionRow>()
}

suspend fun obtenerSeccionPorId(seccionId: Long): SeccionRow? {
    return supabase.postgrest["secciones"]
        .select(Columns.ALL) { filter { eq("id", seccionId) } }
        .decodeList<SeccionRow>()
        .firstOrNull()
}

suspend fun obtenerSeccionesPorGrado(gradoId: Long): List<SeccionRow> {
    return supabase.postgrest["secciones"]
        .select(Columns.ALL) { filter { eq("grado_id", gradoId) } }
        .decodeList<SeccionRow>()
}

suspend fun obtenerSeccionPorNombreYGrado(nombre: String, gradoId: Long): SeccionRow? {
    return supabase.postgrest["secciones"]
        .select(Columns.ALL) { filter { eq("nombre", nombre); eq("grado_id", gradoId) } }
        .decodeList<SeccionRow>()
        .firstOrNull()
}

// DATA CLASSES ASISTENCIAS

@Serializable
data class AsistenciaInsert(
    val alumno_id: Long,
    val curso_id: Long,
    val docente_id: String,
    val fecha: String,
    val hora: String,
    val estado: String
)

@Serializable
data class AsistenciaCheck2(
    val id: Long,
    val alumno_id: Long,
    val estado: String
)

@Serializable
data class AsistenciaHistorialRow(
    val id: Long,
    val alumno_id: Long,
    val curso_id: Long,
    val docente_id: String,
    val fecha: String,
    val hora: String,
    val estado: String
)

@Serializable
data class AsistenciaDetalleRow(
    val id: Long,
    val alumno_id: Long,
    val curso_id: Long,
    val docente_id: String,
    val fecha: String,
    val hora: String,
    val estado: String
)

// ASISTENCIAS

suspend fun obtenerAsistenciasDia(cursoId: Long, docenteId: String, fecha: String): List<AsistenciaCheck2> {
    return supabase.postgrest["asistencias"]
        .select(Columns.ALL) {
            filter {
                eq("curso_id", cursoId)
                eq("docente_id", docenteId)
                eq("fecha", fecha)
            }
        }
        .decodeList<AsistenciaCheck2>()
}

suspend fun insertarAsistencia(asistencia: AsistenciaInsert) {
    supabase.postgrest["asistencias"].insert(asistencia)
}

suspend fun actualizarAsistencia(alumnoId: Long, cursoId: Long, docenteId: String, fecha: String, estado: String) {
    supabase.postgrest["asistencias"]
        .update(mapOf("estado" to estado)) {
            filter {
                eq("alumno_id", alumnoId)
                eq("curso_id", cursoId)
                eq("docente_id", docenteId)
                eq("fecha", fecha)
            }
        }
}

suspend fun obtenerAsistenciasPorAlumno(alumnoId: Long): List<AsistenciaHistorialRow> {
    return supabase.postgrest["asistencias"]
        .select(Columns.ALL) { filter { eq("alumno_id", alumnoId) } }
        .decodeList<AsistenciaHistorialRow>()
}

suspend fun obtenerAsistenciasPorMes(alumnoId: Long, mes: String): List<AsistenciaHistorialRow> {
    return supabase.postgrest["asistencias"]
        .select(Columns.ALL) {
            filter {
                eq("alumno_id", alumnoId)
                like("fecha", "$mes%")
            }
        }
        .decodeList<AsistenciaHistorialRow>()
}

suspend fun obtenerDetalleAsistencia(alumnoId: Long, fecha: String, cursoId: Long): AsistenciaHistorialRow? {
    return supabase.postgrest["asistencias"]
        .select(Columns.ALL) {
            filter {
                eq("alumno_id", alumnoId)
                eq("fecha", fecha)
                eq("curso_id", cursoId)
            }
        }
        .decodeList<AsistenciaHistorialRow>()
        .firstOrNull()
}

//  DATA CLASSES CURSOS

@Serializable
data class CursoRow(val id: Long, val nombre: String)

// CURSOS

suspend fun obtenerTodosCursos(): List<CursoRow> {
    return supabase.postgrest["cursos"]
        .select(Columns.ALL)
        .decodeList<CursoRow>()
}

suspend fun obtenerCursoPorId(cursoId: Long): CursoRow? {
    return supabase.postgrest["cursos"]
        .select(Columns.ALL) { filter { eq("id", cursoId) } }
        .decodeList<CursoRow>()
        .firstOrNull()
}

suspend fun obtenerCursoPorNombre(nombre: String): CursoRow? {
    return supabase.postgrest["cursos"]
        .select(Columns.ALL) { filter { eq("nombre", nombre) } }
        .decodeList<CursoRow>()
        .firstOrNull()
}

//DATA CLASSES DOCENTE SECCIONES

@Serializable
data class DocenteSeccionRow(val grado_id: Long, val seccion_id: Long, val curso_id: Long)

@Serializable
data class DocenteSeccionInsert(val docente_id: String, val grado_id: Long, val seccion_id: Long, val curso_id: Long)

// DOCENTE SECCIONES

suspend fun obtenerDocenteSecciones(docenteId: String): List<DocenteSeccionRow> {
    return supabase.postgrest["docente_secciones"]
        .select(Columns.ALL) { filter { eq("docente_id", docenteId) } }
        .decodeList<DocenteSeccionRow>()
}

suspend fun obtenerDocenteSeccionesPorGrado(docenteId: String, gradoId: Long): List<DocenteSeccionRow> {
    return supabase.postgrest["docente_secciones"]
        .select(Columns.ALL) { filter { eq("docente_id", docenteId); eq("grado_id", gradoId) } }
        .decodeList<DocenteSeccionRow>()
}

suspend fun obtenerDocenteSeccionesPorSeccion(docenteId: String, seccionId: Long): List<DocenteSeccionRow> {
    return supabase.postgrest["docente_secciones"]
        .select(Columns.ALL) { filter { eq("docente_id", docenteId); eq("seccion_id", seccionId) } }
        .decodeList<DocenteSeccionRow>()
}

suspend fun insertarDocenteSeccion(insert: DocenteSeccionInsert) {
    supabase.postgrest["docente_secciones"].insert(insert)
}

// DATA CLASSES PADRE ALUMNOS

@Serializable
data class PadreAlumnoRow(val codigo_estudiante: String)

@Serializable
data class PadreAlumnoInsert(val padre_id: String, val codigo_estudiante: String)

// PADRE ALUMNOS

suspend fun obtenerHijosPadre(padreId: String): List<PadreAlumnoRow> {
    return supabase.postgrest["padre_alumnos"]
        .select(Columns.ALL) { filter { eq("padre_id", padreId) } }
        .decodeList<PadreAlumnoRow>()
}

suspend fun insertarPadreAlumno(insert: PadreAlumnoInsert) {
    supabase.postgrest["padre_alumnos"].insert(insert)
}

// DATA CLASSES COMUNICADOS

@Serializable
data class ComunicadoInsert(
    val docente_id: String,
    val grado_id: Long,
    val seccion_id: Long,
    val curso_id: Long,
    val asunto: String,
    val mensaje: String,
    val archivo_adjunto: String = "",
    val fecha: String,
    val hora: String,
    val total_notificados: Int
)

@Serializable
data class ComunicadoRow(
    val id: Long,
    val docente_id: String,
    val grado_id: Long,
    val seccion_id: Long,
    val curso_id: Long,
    val asunto: String,
    val mensaje: String,
    val archivo_adjunto: String = "",
    val fecha: String,
    val hora: String,
    val total_notificados: Int
)

// COMUNICADOS

suspend fun insertarComunicado(comunicado: ComunicadoInsert): Long {
    val result = supabase.postgrest["comunicados"]
        .insert(comunicado) { select() }
        .decodeSingle<ComunicadoRow>()
    return result.id
}

suspend fun obtenerComunicadosDocente(docenteId: String): List<ComunicadoRow> {
    return supabase.postgrest["comunicados"]
        .select(Columns.ALL) { filter { eq("docente_id", docenteId) } }
        .decodeList<ComunicadoRow>()
}

suspend fun obtenerResumenComunicados(docenteId: String): Triple<Int, Int, Int> {
    val fechaHoy = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    val todos = supabase.postgrest["comunicados"]
        .select(Columns.ALL) { filter { eq("docente_id", docenteId) } }
        .decodeList<ComunicadoRow>()
    val hoy = todos.count { it.fecha == fechaHoy }
    val totalNotificados = todos.sumOf { it.total_notificados }
    // sinLeer requiere comunicado_lecturas - por ahora retorna 0
    return Triple(totalNotificados, hoy, 0)
}

// DATA CLASSES COMUNICADO LECTURAS
@Serializable
data class ComunicadoLecturaInsert(
    val comunicado_id: Long,
    val padre_id: String
)

@Serializable
data class ComunicadoLecturaRow(
    val id: Long,
    val comunicado_id: Long,
    val padre_id: String,
    val leido_en: String? = null
)

@Serializable
data class LecturaConPadreRow(
    val padre_id: String,
    val leido_en: String? = null,
    val nombrecompleto: String = ""
)

@Serializable
data class PadreAlumnoRow2(
    val padre_id: String,
    val codigo_estudiante: String
)
//  COMUNICADO LECTURAS

suspend fun marcarComunicadoLeido(comunicadoId: Long, padreId: String) {
    // Verificar si ya lo leyó
    val yaLeido = supabase.postgrest["comunicado_lecturas"]
        .select(Columns.ALL) {
            filter {
                eq("comunicado_id", comunicadoId)
                eq("padre_id", padreId)
            }
        }
        .decodeList<ComunicadoLecturaRow>()

    if (yaLeido.isEmpty()) {
        supabase.postgrest["comunicado_lecturas"].insert(
            ComunicadoLecturaInsert(
                comunicado_id = comunicadoId,
                padre_id = padreId
            )
        )
    }
}

suspend fun obtenerLecturasComunicado(comunicadoId: Long): Int {
    return supabase.postgrest["comunicado_lecturas"]
        .select(Columns.ALL) { filter { eq("comunicado_id", comunicadoId) } }
        .decodeList<ComunicadoLecturaRow>()
        .size
}

suspend fun obtenerComunicadosPadre(seccionId: Long): List<ComunicadoRow> {
    return supabase.postgrest["comunicados"]
        .select(Columns.ALL) { filter { eq("seccion_id", seccionId) } }
        .decodeList<ComunicadoRow>()
}

// Obtener lecturas de un comunicado con nombre del padre
suspend fun obtenerLecturasConPadre(comunicadoId: Long): List<LecturaConPadreRow> {
    val lecturas = supabase.postgrest["comunicado_lecturas"]
        .select(Columns.ALL) {
            filter { eq("comunicado_id", comunicadoId) }
        }
        .decodeList<ComunicadoLecturaRow>()

    return lecturas.map { lectura ->
        val usuario = supabase.postgrest["usuarios"]
            .select(Columns.ALL) { filter { eq("id", lectura.padre_id) } }
            .decodeList<UsuarioSupabase>()
            .firstOrNull()
        LecturaConPadreRow(
            padre_id = lectura.padre_id,
            leido_en = lectura.leido_en,
            nombrecompleto = usuario?.nombrecompleto ?: "Padre desconocido"
        )
    }
}

// Obtener comunicados por sección con conteo de lecturas

suspend fun obtenerComunicadosPorSeccionConLecturas(
    docenteId: String,
    seccionId: Long
): List<ComunicadoRow> {
    return supabase.postgrest["comunicados"]
        .select(Columns.ALL) {
            filter {
                eq("docente_id", docenteId)
                eq("seccion_id", seccionId)
            }
        }
        .decodeList<ComunicadoRow>()
}

// Obtener cantidad de comunicados por sección (para ElegirSeccion)
suspend fun obtenerCantidadComunicadosPorSeccion(
    docenteId: String,
    seccionId: Long
): Int {
    return supabase.postgrest["comunicados"]
        .select(Columns.ALL) {
            filter {
                eq("docente_id", docenteId)
                eq("seccion_id", seccionId)
            }
        }
        .decodeList<ComunicadoRow>()
        .size
}

// Obtener padres que NO leyeron un comunicado (para reenviar)
suspend fun obtenerPadresSinLeer(
    comunicadoId: Long,
    seccionId: Long
): List<UsuarioSupabase> {
    // Padres que sí leyeron
    val leidos = supabase.postgrest["comunicado_lecturas"]
        .select(Columns.ALL) { filter { eq("comunicado_id", comunicadoId) } }
        .decodeList<ComunicadoLecturaRow>()
        .map { it.padre_id }

    // Todos los padres de alumnos de la sección
    val alumnos = supabase.postgrest["alumnos"]
        .select(Columns.ALL) { filter { eq("seccion_id", seccionId) } }
        .decodeList<AlumnoRow>()

    val padresSinLeer = mutableListOf<UsuarioSupabase>()
    for (alumno in alumnos) {
        val padreLinks = supabase.postgrest["padre_alumnos"]
            .select(Columns.ALL) {
                filter { eq("codigo_estudiante", alumno.codigo_estudiante) }
            }
            .decodeList<PadreAlumnoRow2>()

        for (link in padreLinks) {
            if (link.padre_id !in leidos) {
                val padre = supabase.postgrest["usuarios"]
                    .select(Columns.ALL) { filter { eq("id", link.padre_id) } }
                    .decodeList<UsuarioSupabase>()
                    .firstOrNull()
                if (padre != null && padresSinLeer.none { it.id == padre.id }) {
                    padresSinLeer.add(padre)
                }
            }
        }
    }
    return padresSinLeer
}

// DATA CLASSES PUBLICACIONES
@Serializable
data class PublicacionInsert(
    val docente_id: String,
    val curso_id: Long,
    val grado_id: Long,
    val seccion_id: Long,
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val fecha_entrega: String,
    val fecha_publicacion: String,
    val archivo_adjunto: String = "",
    val estado: String = "Publicado"
)

@Serializable
data class PublicacionRow(
    val id: Long,
    val docente_id: String,
    val curso_id: Long,
    val grado_id: Long,
    val seccion_id: Long,
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val fecha_entrega: String,
    val fecha_publicacion: String,
    val archivo_adjunto: String = "",
    val estado: String = "Publicado"
)

// PUBLICACIONES

suspend fun insertarPublicacion(publicacion: PublicacionInsert): Long {
    val result = supabase.postgrest["publicaciones"]
        .insert(publicacion) { select() }
        .decodeSingle<PublicacionRow>()
    return result.id
}

suspend fun obtenerPublicacionesDocente(docenteId: String): List<PublicacionRow> {
    return supabase.postgrest["publicaciones"]
        .select(Columns.ALL) { filter { eq("docente_id", docenteId) } }
        .decodeList<PublicacionRow>()
}

suspend fun obtenerPublicacionesPorSeccion(seccionId: Long): List<PublicacionRow> {
    return supabase.postgrest["publicaciones"]
        .select(Columns.ALL) { filter { eq("seccion_id", seccionId) } }
        .decodeList<PublicacionRow>()
}

suspend fun obtenerResumenPublicaciones(docenteId: String): Triple<Int, Int, Int> {
    val todas = obtenerPublicacionesDocente(docenteId)
    val tareas = todas.count { it.tipo == "Tarea" }
    val examenes = todas.count { it.tipo == "Examen" }
    val hoy = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    val venceHoy = todas.count { it.fecha_entrega == hoy }
    return Triple(tareas, examenes, venceHoy)
}

// DATA CLASS PUBLICACION CON DOCENTE
@Serializable
data class PublicacionDetalleRow(
    val id: Long,
    val docente_id: String,
    val curso_id: Long,
    val grado_id: Long,
    val seccion_id: Long,
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val fecha_entrega: String,
    val fecha_publicacion: String,
    val archivo_adjunto: String = "",
    val estado: String = "Pendiente"
)

// PUBLICACIONES POR SECCION Y GRADO
suspend fun obtenerPublicacionesPorAlumno(
    seccionId: Long,
    gradoId: Long
): List<PublicacionDetalleRow> {
    return supabase.postgrest["publicaciones"]
        .select(Columns.ALL) {
            filter {
                eq("seccion_id", seccionId)
                eq("grado_id", gradoId)
            }
        }
        .decodeList<PublicacionDetalleRow>()
}

// CONTAR TAREAS PENDIENTES POR ALUMNO
suspend fun contarTareasPendientes(
    seccionId: Long,
    gradoId: Long
): Int {
    return obtenerPublicacionesPorAlumno(seccionId, gradoId)
        .count { it.tipo == "Tarea" && it.estado == "Pendiente" }
}