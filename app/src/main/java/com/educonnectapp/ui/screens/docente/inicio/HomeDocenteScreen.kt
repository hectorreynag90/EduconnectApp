package com.educonnectapp.ui.screens.docente.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.educonnectapp.ui.components.BarraProgresoEdu
import com.educonnectapp.ui.components.BotonCrearEdu
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
import com.educonnectapp.ui.components.estiloCabecera
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Person
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// =====================================================================
// INICIO DEL DOCENTE · "Mi día" (rediseño 2026)
// Cabecera fija turquesa · Mis clases (asistencia de hoy) · Lo que enviaste
// Botón "+ Crear" con hoja de opciones · Barra inferior
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

// Último comunicado o tarea enviada. hechos/total = padres que leyeron / notificados,
// o entregas registradas / alumnos. fecha = "yyyy-MM-dd".
data class EnviadoInicioItem(
    val id: Long,
    val titulo: String,
    val detalle: String,
    val fecha: String,
    val hechos: Int,
    val total: Int
)

private val LOCALE_PE = Locale("es", "PE")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDocenteScreen(
    usuarioNombre: String,
    fotoUrl: String? = null,      // foto de perfil (null = iniciales)
    onAsistencias: () -> Unit = {},
    onComunicados: () -> Unit = {},
    onPublicaciones: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    // ----- Rediseño 2026 -----
    clases: List<ClaseHoyItem> = emptyList(),
    cargandoClases: Boolean = false,
    ultimoComunicado: EnviadoInicioItem? = null,
    ultimaTarea: EnviadoInicioItem? = null,
    tieneNotificaciones: Boolean = false,
    onTomarAsistencia: (ClaseHoyItem) -> Unit = {},
    onVerComunicado: (Long) -> Unit = {},
    onVerTarea: (Long) -> Unit = {},
    onCrearComunicado: () -> Unit = {},
    onCrearTarea: () -> Unit = {},
    onCrearEvaluacion: () -> Unit = {}
) {
    var mostrarCrear by remember { mutableStateOf(false) }

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
    val porTomar = clases.count { !it.tomada }
    val estilo = estiloCabecera(ColorModulo.Inicio)

    // HOJA "+ CREAR"
    if (mostrarCrear) {
        val estadoHoja = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { mostrarCrear = false },
            sheetState = estadoHoja,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "¿Qué deseas hacer?", modifier = Modifier.padding(start = 2.dp, bottom = 4.dp),
                    fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 22.sp, color = EduTexto
                )
                OpcionCrear(Icons.Rounded.FactCheck, TonoEdu.VERDE, "Tomar asistencia", "Registra A / T / F de tu clase") {
                    mostrarCrear = false; onAsistencias()
                }
                OpcionCrear(Icons.Rounded.Campaign, TonoEdu.CORAL, "Comunicado", "Mensaje para los padres") {
                    mostrarCrear = false; onCrearComunicado()
                }
                OpcionCrear(Icons.Rounded.Assignment, TonoEdu.AMARILLO, "Tarea", "Con fecha de entrega y adjunto") {
                    mostrarCrear = false; onCrearTarea()
                }
                OpcionCrear(Icons.Rounded.Event, TonoEdu.AZUL, "Aviso de evaluación", "Temario y fecha de la evaluación") {
                    mostrarCrear = false; onCrearEvaluacion()
                }
            }
        }
    }

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
            Row(
                modifier = Modifier.padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChipCabeceraEdu(fechaHoy, colorCabecera = ColorModulo.Inicio)
                if (!cargandoClases && porTomar > 0) {
                    ChipEdu(
                        texto = if (porTomar == 1) "1 asistencia por tomar" else "$porTomar asistencias por tomar",
                        tono = TonoEdu.AMARILLO,
                        solido = true
                    )
                }
            }
        }

        // CONTENIDO DESPLAZABLE + BOTÓN "+ CREAR"
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // ---------- MIS CLASES ----------
                TituloSeccionEdu("Mis clases · asistencia de hoy")
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
                    else -> clases.forEach { clase ->
                        TarjetaClase(clase = clase, onClick = { onTomarAsistencia(clase) })
                    }
                }

                // ---------- LO QUE ENVIASTE ----------
                Spacer(modifier = Modifier.height(6.dp))
                TituloSeccionEdu("Lo que enviaste") {
                    Text(
                        "Ver todo",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onComunicados)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp, color = EduTealOscuro
                    )
                }
                if (ultimoComunicado == null && ultimaTarea == null) {
                    TarjetaEdu {
                        Text(
                            "Aquí verás tu último comunicado y tu última tarea, con cuántos padres los vieron.",
                            fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                            lineHeight = 19.sp, color = EduTextoSecundario
                        )
                    }
                }
                ultimoComunicado?.let { c ->
                    TarjetaEnviado(
                        icono = Icons.Rounded.Campaign, tono = TonoEdu.CORAL, item = c,
                        onClick = { onVerComunicado(c.id) }
                    ) {
                        val pct = if (c.total > 0) c.hechos * 100 / c.total else 0
                        Text(
                            "Visto por ${c.hechos} de ${c.total} padres · $pct %",
                            fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduTextoSecundario
                        )
                        BarraProgresoEdu(
                            progreso = if (c.total > 0) c.hechos.toFloat() / c.total else 0f,
                            color = if (pct >= 60) EduTeal else EduAmarillo
                        )
                    }
                }
                ultimaTarea?.let { t ->
                    TarjetaEnviado(
                        icono = Icons.Rounded.Assignment, tono = TonoEdu.AMARILLO, item = t,
                        onClick = { onVerTarea(t.id) }
                    ) {
                        val completo = t.total > 0 && t.hechos >= t.total
                        ChipEdu(
                            texto = "${t.hechos}/${t.total} entregas registradas",
                            tono = if (completo) TonoEdu.VERDE else TonoEdu.AMARILLO,
                            icono = if (completo) Icons.Rounded.Check else null
                        )
                    }
                }
            }

            BotonCrearEdu(
                onClick = { mostrarCrear = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 18.dp, bottom = 16.dp)
            )
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

// Tarjeta de una clase: curso, grado y sección, y "Tomar" o "Tomada".
// Pública: también la usa AsistenciasScreen.
@Composable
fun TarjetaClase(clase: ClaseHoyItem, onClick: () -> Unit) {
    TarjetaEdu(onClick = onClick, relleno = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
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

// Tarjeta de lo enviado (comunicado o tarea): ícono, título, detalle, fecha y una parte inferior
@Composable
private fun TarjetaEnviado(
    icono: ImageVector,
    tono: TonoEdu,
    item: EnviadoInicioItem,
    onClick: () -> Unit,
    inferior: @Composable () -> Unit
) {
    TarjetaEdu(onClick = onClick, relleno = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconoCirculoEdu(icono, tono, tamano = 42.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.titulo, modifier = Modifier.weight(1f),
                        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.sp,
                        color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        fechaCorta(item.fecha), modifier = Modifier.padding(start = 8.dp),
                        fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario
                    )
                }
                if (item.detalle.isNotBlank()) {
                    Text(
                        item.detalle, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                        color = EduTextoSecundario, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                inferior()
            }
        }
    }
}

// Fila de la hoja "+ Crear"
@Composable
private fun OpcionCrear(icono: ImageVector, tono: TonoEdu, titulo: String, descripcion: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(EduFondo)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        IconoCirculoEdu(icono, tono, tamano = 50.dp, solido = tono == TonoEdu.AZUL)
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp, color = EduTexto)
            Text(descripcion, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduTextoSecundario)
        }
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null,
            tint = EduTextoSecundario, modifier = Modifier.size(24.dp)
        )
    }
}

// "2026-10-18" -> "18 oct" (hoy -> "Hoy")
private fun fechaCorta(fecha: String): String = try {
    val d = LocalDate.parse(fecha.take(10))
    when (d) {
        LocalDate.now() -> "Hoy"
        LocalDate.now().minusDays(1) -> "Ayer"
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
// Mismos parámetros que antes; ahora con el diseño 2026 (BarraInferiorEdu).
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

// Ítem de la barra anterior (se conserva por compatibilidad; la barra nueva usa BarraInferiorEdu)
@Composable
fun NavBarItemDocente(
    iconRes: Int,
    label: String,
    activo: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 25.dp, vertical = 4.dp)
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = label,
            fontFamily = Nunito,
            fontWeight = if (activo) FontWeight.Black else FontWeight.Bold,
            fontSize = 13.sp,
            color = if (activo) EduTealOscuro else EduTextoSecundario
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HomeDocentePreview() {
    EduConnectAppTheme {
        HomeDocenteScreen(
            usuarioNombre = "Hector Alexander Reyna Gomez",
            clases = listOf(
                ClaseHoyItem(1, "1er Grado", 1, "A", 1, "Ciencia y Tecnología", 24, tomada = true),
                ClaseHoyItem(2, "2do Grado", 7, "A", 3, "Religión", 6, tomada = false)
            ),
            ultimoComunicado = EnviadoInicioItem(1, "Reunión de padres – viernes 23", "1er Grado A", LocalDate.now().minusDays(1).toString(), 18, 24),
            ultimaTarea = EnviadoInicioItem(2, "Maqueta del sistema solar", "1er Grado A · Entrega: 22 oct", LocalDate.now().toString(), 3, 5)
        )
    }
}
