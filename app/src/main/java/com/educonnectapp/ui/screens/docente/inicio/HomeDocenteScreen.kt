package com.educonnectapp.ui.screens.docente.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.educonnectapp.ui.components.BarraInferiorEdu
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.ItemNavEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.coloresDeTono
import com.educonnectapp.ui.components.estiloCabecera
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// =====================================================================
// INICIO DEL DOCENTE (rediseño 2026 · versión simple)
// Cabecera fija turquesa · aviso de hoy (asistencias por tomar) · 4 accesos:
// Asistencia, Comunicados, Publicaciones e Historial · barra inferior
// Solo UI: los datos y la navegación los da MainActivity.
// =====================================================================

// Una clase del docente (asignación) con el estado de la asistencia de hoy
data class ClaseHoyItem(
    val gradoId: Long,
    val grado: String,
    val seccionId: Long,
    val seccion: String,
    val cursoId: Long,
    val curso: String,
    val cantidadAlumnos: Int,
    val tomada: Boolean
)

// Dato rápido de cada acceso del Inicio.
// porcentajeLectura = null si aún no hay comunicados. proximaEntrega = "yyyy-MM-dd" o "".
data class ResumenInicioDocente(
    val comunicadosEnviados: Int = 0,
    val porcentajeLectura: Int? = null,
    val tareasActivas: Int = 0,
    val proximaEntrega: String = ""
)

private val LOCALE_PE = Locale("es", "PE")

@Composable
fun HomeDocenteScreen(
    usuarioNombre: String,
    fotoUrl: String? = null,      // foto de perfil (null = iniciales)
    clases: List<ClaseHoyItem> = emptyList(),
    cargandoClases: Boolean = false,
    resumen: ResumenInicioDocente = ResumenInicioDocente(),
    tieneNotificaciones: Boolean = false,
    onTomarAsistencia: (ClaseHoyItem) -> Unit = {},
    onAsistencias: () -> Unit = {},
    onComunicados: () -> Unit = {},
    onPublicaciones: () -> Unit = {},
    onHistorial: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {}
) {
    val saludo = remember {
        when (LocalTime.now().hour) {
            in 5..11 -> "Buenos días,"
            in 12..18 -> "Buenas tardes,"
            else -> "Buenas noches,"
        }
    }
    val fechaHoy = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", LOCALE_PE))
            .replaceFirstChar { it.uppercase() }
    }
    val primerNombre = usuarioNombre.trim().split(" ").firstOrNull().orEmpty()
    val pendientes = clases.filter { !it.tomada }
    val tomadas = clases.size - pendientes.size
    val estilo = estiloCabecera(ColorModulo.Inicio)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA (color del módulo Inicio)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorModulo.Inicio, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Foto de perfil (o iniciales); al tocarla abre el perfil
                AvatarUsuario(
                    nombre = usuarioNombre,
                    fotoUrl = fotoUrl,
                    tamano = 56.dp,
                    tamanoTexto = 22.sp,
                    modifier = Modifier.clickable { onPerfilDocente() }
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        saludo, fontFamily = Nunito, fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp, color = estilo.subtitulo
                    )
                    Text(
                        "Prof. $primerNombre", fontFamily = Nunito, fontWeight = FontWeight.Black,
                        fontSize = 24.sp, color = estilo.titulo, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    conPunto = tieneNotificaciones,
                    colorCabecera = ColorModulo.Inicio
                )
            }
            ChipCabeceraEdu(fechaHoy, modifier = Modifier.padding(top = 14.dp), colorCabecera = ColorModulo.Inicio)
        }

        // CONTENIDO DESPLAZABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---------- AVISO DE HOY ----------
            when {
                cargandoClases && clases.isEmpty() -> TarjetaEdu {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = EduTeal, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                        Text(
                            "Cargando tus clases...", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                            fontSize = 15.sp, color = EduTextoSecundario
                        )
                    }
                }
                clases.isEmpty() -> TarjetaEdu {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconoCirculoEdu(Icons.Rounded.School, TonoEdu.VERDE)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Aún no tienes clases asignadas", fontFamily = Nunito,
                                fontWeight = FontWeight.Black, fontSize = 16.sp, color = EduTexto
                            )
                            Text(
                                "Agrega tu grado, sección y curso desde tu perfil.", fontFamily = Nunito,
                                fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = EduTextoSecundario
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    BotonSecundarioEdu(texto = "Ir a mi perfil", onClick = onPerfilDocente)
                }
                pendientes.isNotEmpty() -> AvisoHoy(
                    tono = TonoEdu.AMARILLO,
                    icono = Icons.Rounded.FactCheck,
                    titulo = if (pendientes.size == 1) "Tienes 1 asistencia por tomar"
                    else "Tienes ${pendientes.size} asistencias por tomar",
                    // "1er A, 2do A · Ciencia y Tecnología" (agrupado por curso)
                    detalle = pendientes.groupBy { it.curso }.entries.joinToString(" / ") { (curso, lista) ->
                        lista.joinToString(", ") { "${it.grado} ${it.seccion}" } + " · " + curso
                    },
                    // 1 clase pendiente: va directo a tomar lista; varias: abre Asistencia para elegir
                    onTomar = { if (pendientes.size == 1) onTomarAsistencia(pendientes.first()) else onAsistencias() }
                )
                else -> AvisoHoy(
                    tono = TonoEdu.VERDE,
                    icono = Icons.Rounded.Check,
                    titulo = "Asistencia de hoy al día",
                    detalle = if (clases.size == 1) "Tomaste la lista de tu clase de hoy."
                    else "Tomaste la lista de tus ${clases.size} clases de hoy.",
                    onTomar = null
                )
            }

            // ---------- ACCESOS ----------
            TituloSeccionEdu("¿Qué deseas hacer?", modifier = Modifier.padding(top = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaModulo(
                    icono = Icons.Rounded.FactCheck, color = EduAzulOscuro, titulo = "Asistencia",
                    linea1 = when {
                        cargandoClases && clases.isEmpty() -> "Cargando..."
                        clases.size == 1 -> "1 clase hoy"
                        else -> "${clases.size} clases hoy"
                    },
                    linea2 = if (clases.isEmpty()) "" else if (tomadas == 1) "1 tomada" else "$tomadas tomadas",
                    onClick = onAsistencias, modifier = Modifier.weight(1f)
                )
                TarjetaModulo(
                    icono = Icons.Rounded.Campaign, color = EduCoral, titulo = "Comunicados",
                    linea1 = when (resumen.comunicadosEnviados) {
                        0 -> "Sin envíos aún"
                        1 -> "1 enviado"
                        else -> "${resumen.comunicadosEnviados} enviados"
                    },
                    linea2 = resumen.porcentajeLectura?.let { "$it % leído" } ?: "Avisa a los padres",
                    onClick = onComunicados, modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaModulo(
                    icono = Icons.AutoMirrored.Rounded.Assignment, color = EduAmarillo, titulo = "Publicaciones",
                    linea1 = when (resumen.tareasActivas) {
                        0 -> "Sin tareas activas"
                        1 -> "1 tarea activa"
                        else -> "${resumen.tareasActivas} tareas activas"
                    },
                    linea2 = if (resumen.proximaEntrega.isNotBlank()) "Entrega: ${fechaCorta(resumen.proximaEntrega)}"
                    else "Tareas y evaluaciones",
                    onClick = onPublicaciones, modifier = Modifier.weight(1f)
                )
                TarjetaModulo(
                    icono = Icons.Rounded.History, color = EduTeal, titulo = "Historial",
                    linea1 = "Asistencias, comunicados,",
                    linea2 = "tareas y evaluaciones",
                    onClick = onHistorial, modifier = Modifier.weight(1f)
                )
            }
        }

        // BARRA INFERIOR
        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Inicio"
        )
    }
}

// Aviso de hoy: asistencias por tomar (amarillo, con botón "Tomar") o todo al día (verde)
@Composable
private fun AvisoHoy(
    tono: TonoEdu,
    icono: ImageVector,
    titulo: String,
    detalle: String,
    onTomar: (() -> Unit)?
) {
    val c = coloresDeTono(tono)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(c.fondo, RoundedCornerShape(22.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconoCirculoEdu(icono, tono, tamano = 46.dp, solido = true)
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.sp, color = c.texto)
            Text(
                detalle, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                color = c.texto.copy(alpha = 0.85f), maxLines = 2, overflow = TextOverflow.Ellipsis
            )
        }
        if (onTomar != null) {
            Text(
                "Tomar",
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(TipoBotonEdu.AVANZAR.fondo)
                    .clickable(onClick = onTomar)
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp,
                color = TipoBotonEdu.AVANZAR.texto
            )
        }
    }
}

// Acceso del Inicio: ícono en cuadro de color, nombre del módulo y un dato rápido (2 líneas)
@Composable
private fun TarjetaModulo(
    icono: ImageVector,
    color: Color,
    titulo: String,
    linea1: String,
    linea2: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TarjetaEdu(
        modifier = modifier.fillMaxHeight(),
        relleno = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(color, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icono, contentDescription = null,
                    tint = if (color == EduAmarillo) EduTexto else Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null,
                tint = EduTextoSecundario, modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 18.sp,
            color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Column(modifier = Modifier.heightIn(min = 36.dp)) {
            Text(linea1, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduTextoSecundario)
            if (linea2.isNotBlank()) {
                Text(linea2, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduTextoSecundario)
            }
        }
    }
}

// Tarjeta de una clase: curso, grado y sección, y "Tomar" o "Tomada".
// Pública: la usa AsistenciasScreen.
@Composable
fun TarjetaClase(clase: ClaseHoyItem, onClick: () -> Unit) {
    TarjetaEdu(onClick = onClick, relleno = PaddingValues(14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconoCirculoEdu(
                icono = if (clase.tomada) Icons.Rounded.Check else Icons.Rounded.FactCheck,
                tono = if (clase.tomada) TonoEdu.VERDE else TonoEdu.AMARILLO,
                solido = clase.tomada,
                tamano = 46.dp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    clase.curso, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.5.sp,
                    color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${clase.grado} ${clase.seccion} · ${clase.cantidadAlumnos} alumnos",
                    fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp,
                    color = EduTextoSecundario, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            if (clase.tomada) {
                ChipEdu("Tomada", TonoEdu.VERDE, icono = Icons.Rounded.Check)
            } else {
                // Botón pequeño "Tomar" (tipo AVANZAR)
                Text(
                    "Tomar",
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(TipoBotonEdu.AVANZAR.fondo)
                        .clickable(onClick = onClick)
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp,
                    color = TipoBotonEdu.AVANZAR.texto
                )
            }
        }
    }
}

// "2026-11-17" -> "17 nov" (hoy -> "Hoy", mañana -> "Mañana")
private fun fechaCorta(fecha: String): String = try {
    val d = LocalDate.parse(fecha.take(10))
    when (d) {
        LocalDate.now() -> "Hoy"
        LocalDate.now().plusDays(1) -> "Mañana"
        else -> d.format(DateTimeFormatter.ofPattern("d MMM", LOCALE_PE)).replace(".", "")
    }
} catch (e: Exception) { fecha }

// Avatar circular: muestra la foto de perfil si existe; si no (o si no carga), las iniciales.
// Se reutiliza en los encabezados del docente y del padre.
@Composable
fun AvatarUsuario(
    nombre: String,
    fotoUrl: String?,
    tamano: Dp,
    tamanoTexto: TextUnit,
    modifier: Modifier = Modifier
) {
    val iniciales = nombre.split(" ").filter { it.isNotBlank() }.take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(EduAmarillo),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciales,
            fontFamily = Nunito,
            fontWeight = FontWeight.Black,
            fontSize = tamanoTexto,
            color = EduTexto
        )
        if (fotoUrl != null) {
            AsyncImage(
                model = fotoUrl,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        }
    }
}

// BARRA INFERIOR DEL DOCENTE (la usan todas las pantallas del docente).
@Composable
fun BottomNavBarDocente(
    onInicio: () -> Unit,
    onAlumnos: () -> Unit,
    onAvisos: () -> Unit,
    onPerfil: () -> Unit,
    itemActivo: String = "Inicio",
) {
    val items = listOf(
        ItemNavEdu("Inicio", Icons.Rounded.Home),
        ItemNavEdu("Alumnos", Icons.Rounded.Groups),
        ItemNavEdu("Avisos", Icons.Rounded.Campaign),
        ItemNavEdu("Perfil", Icons.Rounded.Person)
    )
    BarraInferiorEdu(
        items = items,
        seleccionado = items.indexOfFirst { it.etiqueta == itemActivo },
        onSeleccionar = { i ->
            when (i) {
                0 -> onInicio()
                1 -> onAlumnos()
                2 -> onAvisos()
                else -> onPerfil()
            }
        }
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HomeDocentePreview() {
    EduConnectAppTheme {
        HomeDocenteScreen(
            usuarioNombre = "Hector Alexander Reyna Gomez",
            clases = listOf(
                ClaseHoyItem(1, "1er", 1, "A", 1, "Ciencia y Tecnología", 5, tomada = false),
                ClaseHoyItem(2, "2do", 7, "A", 1, "Ciencia y Tecnología", 5, tomada = false)
            ),
            resumen = ResumenInicioDocente(
                comunicadosEnviados = 1, porcentajeLectura = 100,
                tareasActivas = 1, proximaEntrega = LocalDate.now().plusDays(4).toString()
            )
        )
    }
}
