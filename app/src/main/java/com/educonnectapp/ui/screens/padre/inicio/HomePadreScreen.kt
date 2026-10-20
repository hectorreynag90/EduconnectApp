package com.educonnectapp.ui.screens.padre.inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.WbSunny
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
import androidx.compose.ui.res.painterResource
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
import com.educonnectapp.ui.theme.EduBorde
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
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Nunito
import com.educonnectapp.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

// =====================================================================
// INICIO DEL PADRE · "muro de novedades" (rediseño 2026)
// Cabecera turquesa fija (saludo, campana, fecha y selector de hijo) ·
// 3 accesos (Asistencia, Comunicados, Agenda) · HOY (asistencia del día) · NOVEDADES
// Los datos y las reglas de qué entra en NOVEDADES los arma MainActivity.
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

enum class TipoNovedad { COMUNICADO, TAREA, EVALUACION }

// Una tarjeta de NOVEDADES
data class NovedadPadreItem(
    val id: Long,
    val tipo: TipoNovedad,
    val titulo: String,
    val detalle: String = "",       // mensaje del comunicado
    val curso: String = "",
    val docente: String = "",
    val fecha: String = "",         // comunicado: "yyyy-MM-ddTHH:mm:ss" (envío) · tarea/evaluación: "yyyy-MM-dd"
    val estado: String = "",        // tarea: "Pendiente" | "Entregada" | "No entregada"
    val leido: Boolean = true,      // comunicado
    val adjunto: String = ""        // nombre del archivo adjunto
)

// Todo lo que se muestra del hijo elegido
data class ResumenInicioHijo(
    val porcentajeMes: Int? = null,     // null = sin registros en el mes
    val sinLeer: Int = 0,
    val pendientes: Int = 0,            // tareas por entregar (no vencidas)
    val asistenciaHoy: List<AsistenciaHoyItem> = emptyList(),
    val novedades: List<NovedadPadreItem> = emptyList()
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
    onAbrirNovedad: (NovedadPadreItem) -> Unit = {},
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

                    // ---------- ACCESOS ----------
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val pct = resumen?.porcentajeMes
                        TarjetaAcceso(
                            icono = Icons.Rounded.FactCheck, tono = TonoEdu.AZUL, titulo = "Asistencia",
                            dato = when {
                                resumen == null -> "…"
                                pct == null -> "Sin registros"
                                else -> "$pct% mes"
                            },
                            tonoDato = when {
                                pct == null -> TonoEdu.GRIS
                                pct >= 90 -> TonoEdu.VERDE
                                pct >= 75 -> TonoEdu.AMARILLO
                                else -> TonoEdu.CORAL
                            },
                            modifier = Modifier.weight(1f),
                            onClick = onAsistencias
                        )
                        val sinLeer = resumen?.sinLeer ?: 0
                        TarjetaAcceso(
                            icono = Icons.Rounded.Campaign, tono = TonoEdu.CORAL, titulo = "Comunicados",
                            dato = when {
                                resumen == null -> "…"
                                sinLeer > 0 -> "$sinLeer sin leer"
                                else -> "Al día"
                            },
                            tonoDato = if (sinLeer > 0) TonoEdu.CORAL else TonoEdu.VERDE,
                            datoSolido = sinLeer > 0,
                            modifier = Modifier.weight(1f),
                            onClick = onComunicados
                        )
                        val pendientes = resumen?.pendientes ?: 0
                        TarjetaAcceso(
                            icono = Icons.Rounded.Assignment, tono = TonoEdu.AMARILLO, titulo = "Agenda",
                            dato = when {
                                resumen == null -> "…"
                                pendientes == 1 -> "1 pendiente"
                                pendientes > 1 -> "$pendientes pendientes"
                                else -> "Al día"
                            },
                            tonoDato = if (pendientes > 0) TonoEdu.AMARILLO else TonoEdu.VERDE,
                            modifier = Modifier.weight(1f),
                            onClick = onAgenda
                        )
                    }

                    if (resumen == null) {
                        TarjetaCargando("Cargando las novedades de $nombreHijo…")
                    } else {
                        // ---------- AVISO DE FALTA ----------
                        val faltas = resumen.asistenciaHoy.filter { it.estado == "F" }
                        if (faltas.isNotEmpty()) AvisoFalta(nombreHijo, faltas)

                        // ---------- HOY ----------
                        TituloSeccionEdu("Hoy")
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
                                            "Verás aquí la asistencia cuando el docente la tome",
                                            fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp,
                                            color = EduTextoSecundario
                                        )
                                    }
                                }
                            }
                        } else {
                            TarjetaAsistenciaHoy(resumen.asistenciaHoy)
                        }

                        // ---------- NOVEDADES ----------
                        TituloSeccionEdu("Novedades")
                        if (resumen.novedades.isEmpty()) {
                            TarjetaEdu(relleno = PaddingValues(horizontal = 16.dp, vertical = 18.dp)) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconoCirculoEdu(Icons.Rounded.WbSunny, TonoEdu.VERDE, tamano = 64.dp)
                                    Text(
                                        "Todo al día", fontFamily = Nunito, fontWeight = FontWeight.Black,
                                        fontSize = 17.sp, color = EduTexto
                                    )
                                    Text(
                                        "No hay comunicados nuevos ni tareas pendientes de $nombreHijo. " +
                                                "Te avisaremos cuando llegue algo.",
                                        fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp,
                                        lineHeight = 19.sp, color = EduTextoSecundario, textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            resumen.novedades.forEach { n ->
                                when (n.tipo) {
                                    TipoNovedad.COMUNICADO -> TarjetaComunicado(n) { onAbrirNovedad(n) }
                                    TipoNovedad.TAREA -> TarjetaTarea(n) { onAbrirNovedad(n) }
                                    TipoNovedad.EVALUACION -> TarjetaEvaluacion(n) { onAbrirNovedad(n) }
                                }
                            }
                            Text(
                                "Estás al día con las novedades de $nombreHijo",
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp,
                                color = EduTextoSecundario, textAlign = TextAlign.Center
                            )
                        }
                    }
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

// Acceso a un módulo: ícono, nombre y un dato rápido
@Composable
private fun TarjetaAcceso(
    icono: ImageVector,
    tono: TonoEdu,
    titulo: String,
    dato: String,
    tonoDato: TonoEdu,
    modifier: Modifier = Modifier,
    datoSolido: Boolean = false,
    onClick: () -> Unit
) {
    TarjetaEdu(modifier = modifier, relleno = PaddingValues(horizontal = 6.dp, vertical = 12.dp), onClick = onClick) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconoCirculoEdu(icono, tono, tamano = 42.dp)
            Text(
                titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp,
                color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            EtiquetaPequena(dato, tonoDato, solido = datoSolido)
        }
    }
}

// Chip pequeño (dato de los accesos y estado de las tarjetas)
@Composable
private fun EtiquetaPequena(texto: String, tono: TonoEdu, solido: Boolean = false) {
    val c = coloresDeTono(tono)
    Text(
        texto,
        modifier = Modifier
            .background(if (solido) c.solido else c.fondo, RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 3.dp),
        fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 11.5.sp,
        color = if (solido) Color.White else c.texto, maxLines = 1
    )
}

// Etiqueta del tipo de tarjeta: COMUNICADO, TAREA, EVALUACIÓN, ASISTENCIA DE HOY
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

// Botón pequeño blanco "Leer" (acción secundaria)
@Composable
private fun BotonLeer(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(Color.White)
            .border(2.dp, EduBorde, RoundedCornerShape(13.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(Icons.Rounded.Visibility, contentDescription = null, tint = EduEtiqueta, modifier = Modifier.size(17.dp))
        Text("Leer", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.5.sp, color = EduEtiqueta)
    }
}

@Composable
private fun TarjetaComunicado(n: NovedadPadreItem, onClick: () -> Unit) {
    TarjetaEdu(relleno = PaddingValues(horizontal = 15.dp, vertical = 13.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EtiquetaTipo("COMUNICADO", Icons.Rounded.Campaign, TonoEdu.CORAL)
            Spacer(modifier = Modifier.weight(1f))
            if (!n.leido) EtiquetaPequena("Nuevo", TonoEdu.CORAL, solido = true)
            else EtiquetaPequena("Leído", TonoEdu.GRIS)
        }
        Text(
            n.titulo, modifier = Modifier.padding(top = 8.dp), fontFamily = Nunito, fontWeight = FontWeight.Black,
            fontSize = 16.sp, color = EduTexto, maxLines = 2, overflow = TextOverflow.Ellipsis
        )
        if (n.detalle.isNotBlank()) {
            Text(
                n.detalle, modifier = Modifier.padding(top = 3.dp), fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, lineHeight = 19.sp, color = EduEtiqueta,
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            listOf(n.docente, n.curso, fechaEnvio(n.fecha)).filter { it.isNotBlank() }.joinToString(" · "),
            modifier = Modifier.padding(top = 6.dp), fontFamily = Nunito, fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp, color = EduTextoSecundario, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (n.adjunto.isNotBlank()) {
                Icon(Icons.Rounded.AttachFile, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(16.dp))
                Text(
                    n.adjunto, modifier = Modifier.weight(1f).padding(start = 4.dp, end = 8.dp), fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = EduTextoSecundario,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            BotonLeer(onClick)
        }
    }
}

@Composable
private fun TarjetaTarea(n: NovedadPadreItem, onClick: () -> Unit) {
    val dias = diasHasta(n.fecha)
    val pendiente = n.estado == "Pendiente"
    val urgente = pendiente && dias != null && dias <= 1
    TarjetaEdu(relleno = PaddingValues(horizontal = 15.dp, vertical = 13.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EtiquetaTipo("TAREA", Icons.Rounded.Assignment, TonoEdu.AMARILLO)
            Spacer(modifier = Modifier.weight(1f))
            when (n.estado) {
                "Entregada" -> EtiquetaPequena("Entregó", TonoEdu.VERDE)
                "No entregada" -> EtiquetaPequena("No entregó", TonoEdu.CORAL)
                else -> EtiquetaPequena("Pendiente", TonoEdu.AMARILLO)
            }
        }
        Text(
            n.titulo, modifier = Modifier.padding(top = 8.dp), fontFamily = Nunito, fontWeight = FontWeight.Black,
            fontSize = 16.sp, color = EduTexto, maxLines = 2, overflow = TextOverflow.Ellipsis
        )
        Text(
            listOf(n.curso, n.docente).filter { it.isNotBlank() }.joinToString(" · "),
            modifier = Modifier.padding(top = 2.dp), fontFamily = Nunito, fontWeight = FontWeight.Bold,
            fontSize = 13.sp, color = EduTextoSecundario, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        val textoFecha = when {
            n.fecha.isBlank() -> "Sin fecha límite"
            !pendiente -> "Fecha límite: ${fechaCortaDia(n.fecha)}"
            dias == 0L -> "Vence hoy"
            dias == 1L -> "Vence mañana"
            else -> "Vence el ${fechaCortaDia(n.fecha)}"
        }
        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            val color = if (urgente) EduCoralOscuro else EduTextoSecundario
            Icon(Icons.Rounded.Schedule, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Text(
                textoFecha, fontFamily = Nunito, fontWeight = if (urgente) FontWeight.Black else FontWeight.ExtraBold,
                fontSize = 13.sp, color = color
            )
        }
    }
}

@Composable
private fun TarjetaEvaluacion(n: NovedadPadreItem, onClick: () -> Unit) {
    val dias = diasHasta(n.fecha)
    TarjetaEdu(relleno = PaddingValues(horizontal = 15.dp, vertical = 13.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EtiquetaTipo("EVALUACIÓN", Icons.Rounded.Event, TonoEdu.AZUL)
            Spacer(modifier = Modifier.weight(1f))
            EtiquetaPequena(
                when (dias) {
                    null -> "Próxima"
                    0L -> "Hoy"
                    1L -> "Mañana"
                    else -> "En $dias días"
                },
                if (dias != null && dias <= 1) TonoEdu.AMARILLO else TonoEdu.AZUL
            )
        }
        Text(
            n.titulo, modifier = Modifier.padding(top = 8.dp), fontFamily = Nunito, fontWeight = FontWeight.Black,
            fontSize = 16.sp, color = EduTexto, maxLines = 2, overflow = TextOverflow.Ellipsis
        )
        Text(
            listOf(n.curso, n.docente).filter { it.isNotBlank() }.joinToString(" · "),
            modifier = Modifier.padding(top = 2.dp), fontFamily = Nunito, fontWeight = FontWeight.Bold,
            fontSize = 13.sp, color = EduTextoSecundario, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        if (n.fecha.isNotBlank()) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(16.dp))
                Text(
                    fechaLarga(n.fecha), fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp, color = EduTextoSecundario
                )
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

// Días que faltan desde hoy ("yyyy-MM-dd"); null si la fecha no es válida
private fun diasHasta(fecha: String): Long? = try {
    ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(fecha.take(10)))
} catch (e: Exception) { null }

// "2026-10-23" -> "vie 23 oct"
private fun fechaCortaDia(fecha: String): String = try {
    LocalDate.parse(fecha.take(10)).format(DateTimeFormatter.ofPattern("EEE d MMM", LOCALE_PE)).replace(".", "")
} catch (e: Exception) { fecha }

// "2026-10-26" -> "Lunes 26 de octubre"
private fun fechaLarga(fecha: String): String = try {
    LocalDate.parse(fecha.take(10)).format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", LOCALE_PE))
        .replaceFirstChar { it.uppercase() }
} catch (e: Exception) { fecha }

// Fecha de envío del comunicado: "hoy 09:40 a. m.", "ayer" o "vie 16 oct"
private fun fechaEnvio(fechaHora: String): String = try {
    val f = LocalDateTime.parse(fechaHora)
    when (ChronoUnit.DAYS.between(f.toLocalDate(), LocalDate.now())) {
        0L -> "hoy " + f.format(DateTimeFormatter.ofPattern("hh:mm a", LOCALE_PE))
        1L -> "ayer"
        else -> f.format(DateTimeFormatter.ofPattern("EEE d MMM", LOCALE_PE)).replace(".", "")
    }
} catch (e: Exception) { "" }

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

// Ítem de la barra anterior (se conserva por compatibilidad; la barra nueva usa BarraInferiorEdu)
@Composable
fun NavBarItem(
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
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = label,
            fontFamily = Nunito,
            fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            color = if (activo) EduconnectBlue else TextSecondary
        )
    }
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
                porcentajeMes = 96, sinLeer = 2, pendientes = 1,
                asistenciaHoy = listOf(
                    AsistenciaHoyItem("Ciencia y Tecnología", "08:05 a. m.", "A", "Prof. Hector Reyna"),
                    AsistenciaHoyItem("Comunicación", "10:02 a. m.", "T")
                ),
                novedades = listOf(
                    NovedadPadreItem(
                        1, TipoNovedad.COMUNICADO, "Reunión de padres este viernes",
                        "Los esperamos el viernes 23 a las 6:00 p. m. en el aula de 2° A.",
                        "Ciencia y Tecnología", "Prof. Hector Reyna", hoy.atTime(9, 40).toString() + ":00",
                        leido = false, adjunto = "citacion.pdf"
                    ),
                    NovedadPadreItem(
                        2, TipoNovedad.TAREA, "Maqueta de ecosistemas", curso = "Ciencia y Tecnología",
                        fecha = hoy.plusDays(1).toString(), estado = "Pendiente"
                    ),
                    NovedadPadreItem(
                        3, TipoNovedad.EVALUACION, "Evaluación de fracciones", curso = "Matemáticas",
                        fecha = hoy.plusDays(7).toString()
                    )
                )
            )
        )
    }
}
