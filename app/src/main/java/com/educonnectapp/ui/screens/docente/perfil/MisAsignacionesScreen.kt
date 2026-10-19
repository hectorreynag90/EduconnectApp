package com.educonnectapp.ui.screens.docente.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupExitoEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.ResumenAsignacionEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduCoralClaro
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// Asignación que se muestra en la lista (la arma MainActivity desde AsignacionResponse)
data class AsignacionGestion(
    val id: Long,
    val grado: String,
    val seccion: String,
    val curso: String,
    val alumnos: Long
)

// =====================================================================
// MIS ASIGNACIONES (rediseño 2026)
// Cabecera fija azul oscuro · clases agrupadas por grado con botón quitar · "Agregar asignación"
// =====================================================================

@Composable
fun MisAsignacionesScreen(
    asignaciones: List<AsignacionGestion> = emptyList(),
    onBack: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onAgregar: () -> Unit = {},
    onEliminar: (AsignacionGestion) -> Unit = {},
    // Estado de la eliminación (lo controla MainActivity)
    procesando: Boolean = false,
    eliminada: AsignacionGestion? = null,
    alerta: AlertaAsignacion? = null,
    onCerrarExito: () -> Unit = {},
    onCerrarAlerta: () -> Unit = {}
) {
    var porEliminar by remember { mutableStateOf<AsignacionGestion?>(null) }
    val porGrado = asignaciones
        .sortedWith(compareBy({ it.grado }, { it.curso }, { it.seccion }))
        .groupBy { it.grado }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Mis asignaciones",
            subtitulo = "Grados, secciones y cursos a tu cargo",
            onVolver = onBack,
            colorFondo = ColorModulo.Perfil,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
                    colorCabecera = ColorModulo.Perfil
                )
            },
            contenidoInferior = {
                ChipCabeceraEdu(
                    if (asignaciones.size == 1) "1 clase" else "${asignaciones.size} clases",
                    modifier = Modifier.padding(top = 14.dp),
                    colorCabecera = ColorModulo.Perfil
                )
            }
        )

        // CONTENIDO DESPLAZABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (asignaciones.isEmpty()) {
                TarjetaEdu {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconoCirculoEdu(Icons.Rounded.School, TonoEdu.GRIS)
                        Text(
                            "Aún no tienes grados ni cursos asignados. Usa \"Agregar asignación\" para empezar.",
                            modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp, color = EduTextoSecundario
                        )
                    }
                }
            }

            porGrado.forEach { (grado, lista) ->
                Text(
                    grado.uppercase(), modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                    fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.5.sp,
                    letterSpacing = 0.6.sp, color = EduTextoSecundario.copy(alpha = 0.8f)
                )
                lista.forEach { a -> TarjetaAsignacion(a, onQuitar = { porEliminar = a }) }
            }

            // AVISO: el historial se conserva
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .background(EduTealClaro, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = EduTealOscuro, modifier = Modifier.size(22.dp))
                Text(
                    "Al quitar una clase dejarás de verla en la app. Las asistencias, comunicados y tareas " +
                            "registrados se conservan.",
                    modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp, lineHeight = 18.sp, color = EduEtiqueta
                )
            }
        }

        // BOTÓN FIJO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            BotonPrincipalEdu(
                texto = "Agregar asignación",
                tipo = TipoBotonEdu.GUARDAR,
                icono = Icons.Rounded.Add,
                onClick = onAgregar
            )
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfil,
            itemActivo = "Perfil"
        )
    }

    // ===================== POPUPS =====================
    // 1. Confirmación (granate: acción de quitar)
    porEliminar?.let { a ->
        PopupConfirmacionEdu(
            titulo = "¿Quitar esta clase?",
            mensaje = "Dejarás de tenerla a cargo. El historial registrado se conserva.",
            textoConfirmar = "Quitar",
            colorConfirmar = TipoBotonEdu.ELIMINAR.fondo,
            icono = Icons.Rounded.DeleteOutline,
            onCancelar = { porEliminar = null },
            onConfirmar = {
                porEliminar = null
                onEliminar(a)
            }
        ) {
            ResumenAsignacionEdu(a.grado, a.seccion, a.curso, colorBorde = EduCoral)
        }
    }

    // 2. Procesando
    if (procesando) PopupProcesandoEdu(titulo = "Quitando asignación…")

    // 3. Éxito (se cierra solo a los 5 s o con la X)
    eliminada?.let { a ->
        PopupExitoEdu(
            titulo = "¡Asignación quitada!",
            detalle = "${a.grado} ${a.seccion}\n${a.curso}",
            mensaje = "Ya no aparece en tu perfil.",
            onCerrar = onCerrarExito
        )
    }

    // Error
    alerta?.let {
        PopupAlertaEdu(
            icono = Icons.Rounded.Warning,
            color = EduAmarillo,
            titulo = it.titulo,
            mensaje = it.mensaje,
            onCerrar = onCerrarAlerta
        )
    }
}

// Una clase: curso, sección · alumnos y botón para quitar
@Composable
private fun TarjetaAsignacion(a: AsignacionGestion, onQuitar: () -> Unit) {
    TarjetaEdu(relleno = PaddingValues(horizontal = 14.dp, vertical = 12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconoCirculoEdu(Icons.Rounded.School, TonoEdu.AZUL)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    a.curso, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.5.sp,
                    color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Sección ${a.seccion} · " + if (a.alumnos == 1L) "1 alumno" else "${a.alumnos} alumnos",
                    fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EduCoralClaro)
                    .clickable(onClick = onQuitar),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.DeleteOutline, contentDescription = "Quitar asignación", tint = EduCoral, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun MisAsignacionesPreview() {
    EduConnectAppTheme {
        MisAsignacionesScreen(
            asignaciones = listOf(
                AsignacionGestion(1, "1er Grado", "A", "Ciencia y Tecnología", 5),
                AsignacionGestion(2, "2do Grado", "A", "Ciencia y Tecnología", 5),
                AsignacionGestion(3, "2do Grado", "B", "Matemáticas", 24)
            )
        )
    }
}
