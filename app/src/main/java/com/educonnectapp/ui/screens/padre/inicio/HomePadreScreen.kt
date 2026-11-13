package com.educonnectapp.ui.screens.padre.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Warning
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BarraInferiorEdu
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
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
import com.educonnectapp.ui.screens.docente.inicio.AvatarUsuario
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduCoralClaro
import com.educonnectapp.ui.theme.EduCoralOscuro
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// =====================================================================
// INICIO DEL PADRE (rediseño 2026 · versión simple)
// Cabecera turquesa fija (saludo, campana, fecha y selector de hijo) ·
// HOY (asistencia del día del hijo elegido) · 3 accesos grandes (Asistencia, Comunicados, Agenda)
// Los datos del hijo elegido los arma MainActivity.
// =====================================================================

// Hijo en el selector de la cabecera
data class HijoInicioItem(
    val id: Long,
    val nombres: String,
    val grado: String,      // "2do Grado"
    val seccion: String     // "A"
)

// Asistencia de hoy en un curso. estado: "A" (Presente), "T" (Tardanza) o "F" (Falta)
data class AsistenciaHoyItem(
    val curso: String,
    val hora: String,       // "08:05 a. m."
    val estado: String,
    val docente: String = ""
)

// Todo lo que se muestra del hijo elegido
data class ResumenInicioHijo(
    val porcentajeMes: Int? = null,     // null = sin registros en el mes
    val sinLeer: Int = 0,
    val comunicadosTotal: Int = 0,      // comunicados recibidos
    val pendientes: Int = 0,            // tareas por entregar (no vencidas)
    val proximaFecha: String = "",      // "yyyy-MM-dd" de la próxima tarea o evaluación ("" = ninguna)
    val asistenciaHoy: List<AsistenciaHoyItem> = emptyList()
)

private val LOCALE_PE = Locale("es", "PE")

@Composable
fun HomePadreScreen(
    usuarioNombre: String,
    fotoUrl: String? = null,      // foto de perfil (null = iniciales)
    tieneNotificaciones: Boolean = false,
    onAsistencias: () -> Unit = {},
    onComunicados: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onEstadoAcademico: () -> Unit = {},   // ya no se usa (se deja para no tocar MainActivity)
    onAvisos: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    // ----- Rediseño 2026 -----
    hijos: List<HijoInicioItem>? = null,          // null = cargando
    hijoSeleccionadoId: Long? = null,
    resumen: ResumenInicioHijo? = null,           // null = cargando los datos del hijo
    onElegirHijo: (Long) -> Unit = {},
    onAsociarHijo: () -> Unit = {}
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
    val estilo = estiloCabecera(ColorModulo.Inicio)
    val hijoSel = hijos?.find { it.id == hijoSeleccionadoId } ?: hijos?.firstOrNull()
    val sinHijos = hijos != null && hijos.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // ===================== CABECERA FIJA =====================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorModulo.Inicio, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 18.dp)
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
                    modifier = Modifier.clickable { onPerfil() }
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        saludo, fontFamily = Nunito, fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp, color = estilo.subtitulo
                    )
                    Text(
                        primerNombre, fontFamily = Nunito, fontWeight = FontWeight.Black,
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

            // Selector de hijo (cambia todo el contenido)
            if (!hijos.isNullOrEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hijos.forEach { h ->
                        ChipHijo(hijo = h, seleccionado = h.id == hijoSel?.id, onClick = { onElegirHijo(h.id) })
                    }
                }
            }
        }

        // ===================== CONTENIDO =====================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when {
                // Cargando los hijos
                hijos == null -> TarjetaCargando("Cargando tu información…")

                // Sin hijos asociados
                sinHijos -> {
                    TarjetaEdu(relleno = PaddingValues(horizontal = 16.dp, vertical = 20.dp)) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconoCirculoEdu(Icons.Rounded.PersonAdd, TonoEdu.VERDE, tamano = 72.dp)
                            Text(
                                "Asocia a tu hijo", fontFamily = Nunito, fontWeight = FontWeight.Black,
                                fontSize = 18.sp, color = EduTexto
                            )
                            Text(
                                "Ingresa el código de estudiante que te entregó el colegio para ver su " +
                                        "asistencia, comunicados y agenda.",
                                fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                                lineHeight = 20.sp, color = EduTextoSecundario, textAlign = TextAlign.Center
                            )
                        }
                    }
                    AvisoInfo(
                        "Si tienes más de un hijo en el colegio, puedes asociarlos a todos y cambiar entre ellos desde aquí."
                    )
                }

                else -> {
                    val nombreHijo = hijoSel?.nombres?.trim()?.split(" ")?.firstOrNull().orEmpty()

                    // ---------- HOY ----------
                    TituloSeccionEdu("Hoy")
                    if (resumen == null) {
                        TarjetaCargando("Cargando la información de $nombreHijo…")
                    } else {
                        val faltas = resumen.asistenciaHoy.filter { it.estado == "F" }
                        if (faltas.isNotEmpty()) AvisoFalta(nombreHijo, faltas)
                        if (resumen.asistenciaHoy.isEmpty()) {
                            TarjetaEdu(relleno = PaddingValues(horizontal = 15.dp, vertical = 13.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    IconoCirculoEdu(Icons.Rounded.Schedule, TonoEdu.GRIS, tamano = 42.dp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Aún sin registros hoy", fontFamily = Nunito, fontWeight = FontWeight.Black,
                                            fontSize = 15.sp, color = EduTexto
                                        )
                                        Text(
                                            "Verás aquí la asistencia de $nombreHijo cuando el docente la tome",
                                            fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp,
                                            color = EduTextoSecundario
                                        )
                                    }
                                }
                            }
                        } else {
                            TarjetaAsistenciaHoy(resumen.asistenciaHoy)
                        }
                    }

                    // ---------- ACCESOS ----------
                    TituloSeccionEdu("¿Qué deseas ver?", modifier = Modifier.padding(top = 4.dp))
                    val pct = resumen?.porcentajeMes
                    val estadoHoy = resumen?.asistenciaHoy?.map { it.estado }.orEmpty().let { e ->
                        when {
                            "F" in e -> "Hoy: Falta"
                            "T" in e -> "Hoy: Tardanza"
                            e.isNotEmpty() -> "Hoy: Presente"
                            else -> "Hoy: sin registros"
                        }
                    }
                    val sinLeer = resumen?.sinLeer ?: 0
                    val total = resumen?.comunicadosTotal ?: 0
                    val pendientes = resumen?.pendientes ?: 0
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TarjetaModuloPadre(
                            icono = Icons.Rounded.FactCheck, color = EduAzulOscuro, titulo = "Asistencia",
                            linea1 = when {
                                resumen == null -> "Cargando…"
                                pct == null -> "Sin registros este mes"
                                else -> "$pct % este mes"
                            },
                            linea2 = if (resumen == null) "" else estadoHoy,
                            onClick = onAsistencias, modifier = Modifier.weight(1f)
                        )
                        TarjetaModuloPadre(
                            icono = Icons.Rounded.Campaign, color = EduCoral, titulo = "Comunicados",
                            linea1 = when {
                                resumen == null -> "Cargando…"
                                sinLeer == 1 -> "1 sin leer"
                                sinLeer > 1 -> "$sinLeer sin leer"
                                else -> "Al día"
                            },
                            linea2 = when {
                                resumen == null -> ""
                                total == 1 -> "1 recibido"
                                else -> "$total recibidos"
                            },
                            alerta = sinLeer > 0,
                            onClick = onComunicados, modifier = Modifier.weight(1f)
                        )
                    }
                    TarjetaModuloPadre(
                        icono = Icons.Rounded.Assignment, color = EduAmarillo, titulo = "Agenda",
                        linea1 = when {
                            resumen == null -> "Cargando…"
                            pendientes == 0 -> "Sin tareas pendientes"
                            pendientes == 1 -> "1 tarea pendiente"
                            else -> "$pendientes tareas pendientes"
                        },
                        linea2 = resumen?.proximaFecha?.takeIf { it.isNotBlank() }
                            ?.let { "Próximo: ${fechaCortaDia(it)}" } ?: "Tareas y evaluaciones",
                        horizontal = true,
                        onClick = onAgenda, modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // ===================== BOTÓN FIJO (sin hijos) =====================
        if (sinHijos) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EduFondo)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                BotonPrincipalEdu(
                    texto = "Asociar hijo",
                    tipo = TipoBotonEdu.GUARDAR,
                    icono = Icons.Rounded.PersonAdd,
                    onClick = onAsociarHijo
                )
            }
        }

        // ===================== BARRA INFERIOR =====================
        BottomNavBarPadre(
            onInicio = {},
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Inicio"
        )
    }
}

// ---------------------------------------------------------------------
// Piezas de la pantalla
// ---------------------------------------------------------------------

// Chip de un hijo en la cabecera: blanco = elegido; translúcido = los demás
@Composable
private fun ChipHijo(hijo: HijoInicioItem, seleccionado: Boolean, onClick: () -> Unit) {
    val nombre = hijo.nombres.trim().split(" ").firstOrNull().orEmpty()
    val iniciales = hijo.nombres.trim().split(" ").filter { it.isNotBlank() }.take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (seleccionado) Color.White else Color.White.copy(alpha = 0.18f))
            .clickable(onClick = onClick)
            .padding(start = 4.dp, end = 13.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(if (seleccionado) EduAmarillo else Color.White.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                iniciales, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.sp,
                color = if (seleccionado) EduTexto else Color.White
            )
        }
        Text(
            "$nombre · ${gradoCorto(hijo.grado)} ${hijo.seccion}",
            fontFamily = Nunito, fontWeight = if (seleccionado) FontWeight.Black else FontWeight.ExtraBold,
            fontSize = 14.sp, color = if (seleccionado) EduTealOscuro else Color.White, maxLines = 1
        )
    }
}

// Acceso grande a un módulo: ícono en cuadro de color, nombre y un dato rápido (2 líneas).
// horizontal = true: ícono a la izquierda (tarjeta de ancho completo). alerta = dato en granate.
@Composable
private fun TarjetaModuloPadre(
    icono: ImageVector,
    color: Color,
    titulo: String,
    linea1: String,
    linea2: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alerta: Boolean = false,
    horizontal: Boolean = false
) {
    val cuadro: @Composable () -> Unit = {
        Box(
            modifier = Modifier.size(58.dp).background(color, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icono, contentDescription = null,
                tint = if (color == EduAmarillo) EduTexto else Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
    val textos: @Composable () -> Unit = {
        Column {
            Text(
                titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 18.sp,
                color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                linea1, fontFamily = Nunito, fontWeight = if (alerta) FontWeight.Black else FontWeight.Bold,
                fontSize = 13.5.sp, color = if (alerta) EduCoralOscuro else EduTextoSecundario
            )
            if (linea2.isNotBlank()) {
                Text(linea2, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = EduTextoSecundario)
            }
        }
    }
    TarjetaEdu(
        modifier = modifier.fillMaxHeight(),
        relleno = PaddingValues(horizontal = 16.dp, vertical = if (horizontal) 16.dp else 18.dp),
        onClick = onClick
    ) {
        if (horizontal) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                cuadro()
                Box(modifier = Modifier.weight(1f)) { textos() }
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null,
                    tint = EduTextoSecundario, modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth()) {
                cuadro()
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null,
                    tint = EduTextoSecundario, modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            textos()
        }
    }
}

// Etiqueta de la tarjeta de asistencia de hoy
@Composable
private fun EtiquetaTipo(texto: String, icono: ImageVector, tono: TonoEdu) {
    val c = coloresDeTono(tono)
    Row(
        modifier = Modifier
            .background(c.fondo, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icono, contentDescription = null, tint = c.texto, modifier = Modifier.size(14.dp))
        Text(
            texto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 11.5.sp,
            letterSpacing = 0.5.sp, color = c.texto
        )
    }
}

@Composable
private fun TarjetaCargando(texto: String) {
    TarjetaEdu {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(color = EduTeal, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
            Text(texto, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EduTextoSecundario)
        }
    }
}

@Composable
private fun AvisoInfo(texto: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(EduAzulOscuroClaro, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Rounded.Info, contentDescription = null, tint = EduAzulOscuro, modifier = Modifier.size(22.dp))
        Text(
            texto, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp, lineHeight = 18.sp, color = EduEtiqueta
        )
    }
}

// Aviso granate cuando el hijo faltó hoy a una o más clases
@Composable
private fun AvisoFalta(nombreHijo: String, faltas: List<AsistenciaHoyItem>) {
    val titulo = if (faltas.size == 1) "$nombreHijo faltó hoy a ${faltas[0].curso}"
    else "$nombreHijo faltó hoy a ${faltas.size} clases"
    val detalle = if (faltas.size == 1) {
        listOf("Registrada ${faltas[0].hora}", faltas[0].docente).filter { it.isNotBlank() }.joinToString(" · ")
    } else faltas.joinToString(" · ") { it.curso }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(EduCoralClaro)
            .border(1.5.dp, EduCoral, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(42.dp).background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Warning, contentDescription = null, tint = EduCoral, modifier = Modifier.size(21.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduCoralOscuro)
            Text(detalle, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduEtiqueta)
        }
    }
}

// Asistencia de hoy: un renglón por curso con Presente / Tardanza / Falta
@Composable
private fun TarjetaAsistenciaHoy(lista: List<AsistenciaHoyItem>) {
    TarjetaEdu(relleno = PaddingValues(horizontal = 15.dp, vertical = 13.dp)) {
        EtiquetaTipo("ASISTENCIA DE HOY", Icons.Rounded.FactCheck, TonoEdu.AZUL)
        lista.forEachIndexed { i, a ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        a.curso, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp,
                        color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Registrada ${a.hora}", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp, color = EduTextoSecundario
                    )
                }
                when (a.estado) {
                    "A" -> ChipEdu("Presente", TonoEdu.VERDE)
                    "T" -> ChipEdu("Tardanza", TonoEdu.AMARILLO)
                    else -> ChipEdu("Falta", TonoEdu.CORAL)
                }
            }
            if (i < lista.lastIndex) {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EduFondo))
            }
        }
    }
}

// ---------------------------------------------------------------------
// Fechas (solo formato para mostrar)
// ---------------------------------------------------------------------

// "2do Grado" -> "2°"
private fun gradoCorto(grado: String): String =
    grado.firstOrNull { it.isDigit() }?.let { "$it°" } ?: grado

// "2026-10-23" -> "vie 23 oct"
private fun fechaCortaDia(fecha: String): String = try {
    LocalDate.parse(fecha.take(10)).format(DateTimeFormatter.ofPattern("EEE d MMM", LOCALE_PE)).replace(".", "")
} catch (e: Exception) { fecha }

// =====================================================================
// BARRA INFERIOR DEL PADRE (la usan todas las pantallas del padre).
// Mismos parámetros que antes; ahora con el diseño 2026 (BarraInferiorEdu).
// "Avisos" abre los comunicados (por eso el ícono de megáfono).
// =====================================================================
@Composable
fun BottomNavBarPadre(
    onInicio: () -> Unit,
    onAvisos: () -> Unit,
    onAgenda: () -> Unit,
    onPerfil: () -> Unit,
    itemActivo: String = "Inicio"
) {
    val items = listOf(
        ItemNavEdu("Inicio", Icons.Rounded.Home),
        ItemNavEdu("Avisos", Icons.Rounded.Campaign),
        ItemNavEdu("Agenda", Icons.Rounded.CalendarMonth),
        ItemNavEdu("Perfil", Icons.Rounded.Person)
    )
    BarraInferiorEdu(
        items = items,
        seleccionado = items.indexOfFirst { it.etiqueta == itemActivo },
        onSeleccionar = { i ->
            when (i) {
                0 -> onInicio()
                1 -> onAvisos()
                2 -> onAgenda()
                else -> onPerfil()
            }
        }
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HomePadrePreview() {
    val hoy = LocalDate.now()
    EduConnectAppTheme {
        HomePadreScreen(
            usuarioNombre = "Luis Perez Garcia",
            tieneNotificaciones = true,
            hijos = listOf(
                HijoInicioItem(1, "Angela", "2do Grado", "A"),
                HijoInicioItem(2, "Pedro", "1er Grado", "A")
            ),
            hijoSeleccionadoId = 1,
            resumen = ResumenInicioHijo(
                porcentajeMes = 96, sinLeer = 2, comunicadosTotal = 9, pendientes = 1,
                proximaFecha = hoy.plusDays(1).toString(),
                asistenciaHoy = listOf(
                    AsistenciaHoyItem("Ciencia y Tecnología", "08:05 a. m.", "A", "Prof. Hector Reyna"),
                    AsistenciaHoyItem("Comunicación", "10:02 a. m.", "T")
                )
            )
        )
    }
}
