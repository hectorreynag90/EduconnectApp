package com.educonnectapp.data.remote

import com.educonnectapp.BuildConfig
import com.educonnectapp.ui.screens.DetalleAlumnoItem
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable

import io.github.jan.supabase.functions.functions
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
data class ConfiguracionInstitucionRow(
    val id: Long,
    val nombre: String? = null,
    val correo_director: String? = null
)

@Serializable
data class EstadoUpdate(
    val estado: String
)

@Serializable
data class CambioAsistenciaInsert(
    val alumno_id: Long,
    val asistencia_id: Long,
    val docente_id: String,
    val estado_anterior: String,
    val estado_nuevo: String,
    val motivo: String,
    val grado_id: Long,
    val seccion_id: Long,
    val curso_id: Long
)

val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY
) {
    install(Auth)
    install(Postgrest)
    install(Functions)
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

suspend fun obtenerGradosPorIds(ids: List<Long>): List<GradoRow> {
    if (ids.isEmpty()) return emptyList()
    return supabase.postgrest["grados"]
        .select(Columns.ALL) { filter { isIn("id", ids) } }
        .decodeList<GradoRow>()
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

suspend fun obtenerSeccionesPorIds(ids: List<Long>): List<SeccionRow> {
    if (ids.isEmpty()) return emptyList()
    return supabase.postgrest["secciones"]
        .select(Columns.ALL) { filter { isIn("id", ids) } }
        .decodeList<SeccionRow>()
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

suspend fun insertarAsistenciasEnLote(asistencias: List<AsistenciaInsert>) {
    if (asistencias.isEmpty()) return
    supabase.postgrest["asistencias"].insert(asistencias)
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

suspend fun obtenerCursosPorIds(ids: List<Long>): List<CursoRow> {
    if (ids.isEmpty()) return emptyList()
    return supabase.postgrest["cursos"]
        .select(Columns.ALL) { filter { isIn("id", ids) } }
        .decodeList<CursoRow>()
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

suspend fun obtenerAsistenciasPorDocente(docenteId: String, cursoId: Long): List<AsistenciaHistorialRow> {
    return supabase.postgrest["asistencias"]
        .select(Columns.ALL) {
            filter {
                eq("docente_id", docenteId)
                eq("curso_id", cursoId)
            }
        }
        .decodeList<AsistenciaHistorialRow>()
}

suspend fun obtenerCorreoDirector(): String {
    return supabase.postgrest["configuracion_institucion"]
        .select(Columns.ALL)
        .decodeList<ConfiguracionInstitucionRow>()
        .firstOrNull()?.correo_director ?: ""
}

suspend fun guardarCambios(
    alumnosOriginales: List<DetalleAlumnoItem>,
    alumnosEditados: List<DetalleAlumnoItem>,
    asistenciaId: Long,
    docenteId: String,
    docenteNombre: String,
    motivo: String,
    cursoId: Long,
    fecha: String,
    gradoId: Long,
    seccionId: Long,
    gradoNombre: String,
    seccionNombre: String,
    cursoNombre: String
) {
    val correoDirector = obtenerCorreoDirector()

    alumnosOriginales.forEachIndexed { i, original ->
        val editado = alumnosEditados[i]
        if (original.estado != editado.estado) {

            // 1. Actualizar estado en asistencias
            supabase.postgrest["asistencias"]
                .update(EstadoUpdate(estado = editado.estado)) {
                    filter {
                        eq("alumno_id", original.id)
                        eq("curso_id", cursoId)
                        eq("docente_id", docenteId)
                        eq("fecha", fecha)
                    }
                }

            // 2. Registrar en cambio_asistencia
            supabase.postgrest["cambio_asistencia"]
                .insert(CambioAsistenciaInsert(
                    alumno_id       = original.id,
                    asistencia_id   = asistenciaId,
                    docente_id      = docenteId,
                    estado_anterior = original.estado,
                    estado_nuevo    = editado.estado,
                    motivo          = motivo,
                    grado_id        = gradoId,
                    seccion_id      = seccionId,
                    curso_id        = cursoId
                ))

            // 3. Llamar Edge Function para correo al director
            val estadoAnteriorTexto = when(original.estado) { "A" -> "Presente"; "T" -> "Tardanza"; else -> "Ausente" }
            val estadoNuevoTexto    = when(editado.estado)  { "A" -> "Presente"; "T" -> "Tardanza"; else -> "Ausente" }
            supabase.functions.invoke(
                function = "notificar-cambio-asistencia",
                body = buildJsonObject {
                    put("correoDirector", correoDirector)
                    put("alumno", "${original.apellidos}, ${original.nombres}")
                    put("estadoAnterior", estadoAnteriorTexto)
                    put("estadoNuevo", estadoNuevoTexto)
                    put("motivo", motivo)
                    put("docente", docenteNombre)
                    put("grado", gradoNombre)
                    put("seccion", seccionNombre)
                    put("curso", cursoNombre)
                }
            )

            // 4. Enviar push al padre del alumno
            val fcmTokenPadre = obtenerFcmTokenPadre(original.id)
            if (!fcmTokenPadre.isNullOrEmpty()) {
                val emoji = when(editado.estado) { "A" -> "✅"; "T" -> "⚠️"; else -> "❌" }
                val horaActual = java.text.SimpleDateFormat("hh:mma", java.util.Locale.getDefault()).format(java.util.Date())
                supabase.functions.invoke(
                    function = "send-notification",
                    body = buildJsonObject {
                        put("token", fcmTokenPadre)
                        put("title", "Asistencia Actualizada $estadoNuevoTexto $emoji $fecha $horaActual")
                        put("body", "Alumno: ${original.apellidos}, ${original.nombres} — $gradoNombre Sec. $seccionNombre\nCurso: $cursoNombre\nAntes: $estadoAnteriorTexto → Ahora: $estadoNuevoTexto")
                        put("data", buildJsonObject {
                            put("tipo", "actualizacion_asistencia")
                            put("alumno", "${original.apellidos}, ${original.nombres}")
                            put("grado", gradoNombre)
                            put("seccion", seccionNombre)
                            put("curso", cursoNombre)
                            put("estadoAnterior", estadoAnteriorTexto)
                            put("estadoNuevo", estadoNuevoTexto)
                            put("fecha", fecha)
                            put("hora", horaActual)
                        })
                    }
                )
            }

        }
    }
}

suspend fun obtenerResumenAsistenciasDocente(docenteId: String): Triple<Int, Int, Int> {
    val fechaHoy = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

    val relaciones = obtenerDocenteSecciones(docenteId)
    val totalCursos = relaciones.size
    if (totalCursos == 0) return Triple(0, 0, 0)

    // Todas las asistencias de hoy del docente — 1 consulta
    val asistenciasHoy = supabase.postgrest["asistencias"]
        .select(Columns.ALL) {
            filter {
                eq("docente_id", docenteId)
                eq("fecha", fechaHoy)
            }
        }
        .decodeList<AsistenciaHistorialRow>()

    if (asistenciasHoy.isEmpty()) return Triple(0, totalCursos, 0)

    // Todos los alumnos de todas las secciones del docente — 1 consulta en lugar de N
    val todasSeccionIds = relaciones.map { it.seccion_id }.distinct()
    val todosAlumnos = supabase.postgrest["alumnos"]
        .select(Columns.ALL) { filter { isIn("seccion_id", todasSeccionIds) } }
        .decodeList<AlumnoSeccionRow>()

    // Mapa seccion_id → lista de alumno ids (en memoria, sin más consultas)
    val alumnosPorSeccion = todosAlumnos.groupBy({ it.seccion_id }, { it.id })

    var cursosRealizados = 0
    for (rel in relaciones) {
        val alumnosDeLaSeccion = alumnosPorSeccion[rel.seccion_id] ?: emptyList()
        val tieneAsistencia = asistenciasHoy.any {
            it.curso_id == rel.curso_id && it.alumno_id in alumnosDeLaSeccion
        }
        if (tieneAsistencia) cursosRealizados++
    }

    val pendientes = (totalCursos - cursosRealizados).coerceAtLeast(0)
    val presentes = asistenciasHoy.count { it.estado == "A" }
    val porcentaje = if (asistenciasHoy.isNotEmpty()) (presentes * 100) / asistenciasHoy.size else 0

    return Triple(cursosRealizados, pendientes, porcentaje)
}

// FCM Token del usuario actual
suspend fun guardarFcmToken(token: String) {
    val userId = supabase.auth.currentUserOrNull()?.id ?: return
    supabase.postgrest["usuarios"].update(
        mapOf("fcm_token" to token)
    ) {
        filter { eq("id", userId) }
    }
}

// Obtener FCM token del padre de un alumno
suspend fun obtenerFcmTokenPadre(alumnoId: Long): String? {
    // Buscar el padre asociado al alumno
    val resultado = supabase.postgrest["alumnos"]
        .select(Columns.list("padre_id")) {
            filter { eq("id", alumnoId) }
        }
        .decodeList<AlumnoPadreRow>()
    val padreId = resultado.firstOrNull()?.padre_id ?: return null

    val padreRow = supabase.postgrest["usuarios"]
        .select(Columns.list("fcm_token")) {
            filter { eq("id", padreId) }
        }
        .decodeList<UsuarioFcmRow>()
    return padreRow.firstOrNull()?.fcm_token
}

@Serializable
data class AlumnoPadreRow(val padre_id: String)

@Serializable
data class UsuarioFcmRow(val fcm_token: String? = null)

// Guardar notificación en tabla y enviar push
suspend fun enviarNotificacionAsistencia(
    padreId: String,
    alumnoNombre: String,
    grado: String,
    seccion: String,
    curso: String,
    estado: String,
    hora: String,
    fecha: String,
    fcmToken: String?,
    esActualizacion: Boolean = false
) {
    // 1. Guardar en tabla notificaciones
    supabase.postgrest["notificaciones"].insert(
        mapOf(
            "padre_id" to padreId,
            "alumno_nombre" to alumnoNombre,
            "grado" to grado,
            "seccion" to seccion,
            "curso" to curso,
            "estado" to estado,
            "hora" to hora,
            "fecha" to fecha
        )
    )

    // 2. Enviar push via Supabase Edge Function (si hay token)
    if (!fcmToken.isNullOrEmpty()) {
        val estadoTexto = when (estado) { "A" -> "Presente"; "T" -> "Tardanza"; else -> "Ausente" }
        val emoji = when (estado) { "A" -> "✅"; "T" -> "⚠️"; else -> "❌" }
        val titulo = if (esActualizacion)
            "Asistencia Actualizada $estadoTexto $emoji $fecha $hora"
        else
            "Asistencia Registrada $estadoTexto $emoji $fecha $hora"
        supabase.functions.invoke(
            function = "send-notification",
            body = buildJsonObject {
                put("token", fcmToken)
                put("title", titulo)
                put("body", "Alumno: $alumnoNombre — $grado Sec. $seccion\nCurso: $curso")
                put("data", buildJsonObject {
                    put("tipo", "asistencia")
                    put("alumno", alumnoNombre)
                    put("grado", grado)
                    put("seccion", seccion)
                    put("curso", curso)
                    put("estado", estadoTexto)
                    put("fecha", fecha)
                    put("hora", hora)
                })
            }
        )
    }
}

// Contar notificaciones no leídas del padre
suspend fun contarNotificacionesNoLeidas(padreId: String): Int {
    val resultado = supabase.postgrest["notificaciones"]
        .select(Columns.list("id")) {
            filter {
                eq("padre_id", padreId)
                eq("leida", false)
            }
        }
        .decodeList<NotificacionIdRow>()
    return resultado.size
}

@Serializable
data class NotificacionIdRow(val id: String)

// Obtener padre_id de un alumno via tabla padre_alumnos
suspend fun obtenerPadreIdDeAlumno(alumnoId: Long): String? {
    // Primero obtener el codigo_estudiante del alumno
    val alumno = obtenerAlumnoPorId(alumnoId) ?: return null
    // Buscar en padre_alumnos por codigo_estudiante
    val padreLink = supabase.postgrest["padre_alumnos"]
        .select(Columns.ALL) {
            filter { eq("codigo_estudiante", alumno.codigo_estudiante) }
        }
        .decodeList<PadreAlumnoRow2>()
        .firstOrNull()
    return padreLink?.padre_id
}

// Obtener FCM token del padre por padre_id
suspend fun obtenerFcmTokenPorPadreId(padreId: String): String? {
    return supabase.postgrest["usuarios"]
        .select(Columns.list("fcm_token")) {
            filter { eq("id", padreId) }
        }
        .decodeList<UsuarioFcmRow>()
        .firstOrNull()?.fcm_token
}