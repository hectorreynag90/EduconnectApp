package com.educonnectapp.ui.screens.padre.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.PersonRemove
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
import androidx.compose.ui.graphics.Color
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
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
import com.educonnectapp.ui.screens.padre.inicio.BottomNavBarPadre
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduCoralClaro
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// Error al quitar o asociar un hijo (lo arma MainActivity y se muestra como popup)
data class AlertaHijo(val titulo: String, val mensaje: String)

// =====================================================================
// MIS HIJOS (rediseño 2026) · mismo diseño que "Mis asignaciones" del docente
// Cabecera fija azul oscuro · un hijo por tarjeta con botón quitar · "Asociar hijo" fijo
// =====================================================================

@Composable
fun MisHijosScreen(
    hijos: List<HijoAsociado> = emptyList(),
    onBack: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onHomePadre: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onAsociar: () -> Unit = {},
    onQuitar: (HijoAsociado) -> Unit = {},
    // Estado de la desasociación (lo controla MainActivity)
    procesando: Boolean = false,
    quitado: HijoAsociado? = null,
    alerta: AlertaHijo? = null,
    onCerrarExito: () -> Unit = {},
    onCerrarAlerta: () -> Unit = {}
) {
    var porQuitar by remember { mutableStateOf<HijoAsociado?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Mis hijos",
            subtitulo = "Estudiantes asociados a tu cuenta",
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
                    when (hijos.size) { 0 -> "Sin hijos"; 1 -> "1 hijo"; else -> "${hijos.size} hijos" },
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
            TituloSeccionEdu("Hijos asociados")
            if (hijos.isEmpty()) {
                TarjetaEdu {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconoCirculoEdu(Icons.Rounded.Groups, TonoEdu.GRIS)
                        Text(
                            "Aún no tienes hijos asociados. Usa \"Asociar hijo\" con el código que te dio el colegio.",
                            modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp, color = EduTextoSecundario
                        )
                    }
                }
            }
            hijos.forEachIndexed { i, h ->
                TarjetaHijo(h, if (i % 2 == 0) EduAmarillo else EduTealClaro, onQuitar = { porQuitar = h })
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
                    "Al quitar a un hijo dejarás de recibir sus asistencias, comunicados y tareas. " +
                            "Su historial se conserva y puedes volver a asociarlo con su código.",
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
                texto = "Asociar hijo",
                tipo = TipoBotonEdu.GUARDAR,
                icono = Icons.Rounded.PersonAdd,
                onClick = onAsociar
            )
        }

        BottomNavBarPadre(
            onInicio = onHomePadre,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Perfil"
        )
    }

    // ===================== POPUPS =====================
    // 1. Confirmación (granate: acción de quitar)
    porQuitar?.let { h ->
        PopupConfirmacionEdu(
            titulo = "¿Quitar a tu hijo?",
            mensaje = "Dejarás de recibir sus notificaciones. Podrás volver a asociarlo con su código.",
            textoConfirmar = "Quitar",
            colorConfirmar = TipoBotonEdu.ELIMINAR.fondo,
            icono = Icons.Rounded.PersonRemove,
            onCancelar = { porQuitar = null },
            onConfirmar = {
                porQuitar = null
                onQuitar(h)
            }
        ) {
            ResumenHijoEdu(h, colorBorde = EduCoral)
        }
    }

    // 2. Procesando
    if (procesando) PopupProcesandoEdu(titulo = "Quitando hijo…")

    // 3. Éxito: se cierra solo a los 5 segundos o con la X (se queda en la lista)
    quitado?.let { h ->
        PopupExitoEdu(
            titulo = "¡Hijo desasociado!",
            detalle = "${h.nombres} ${h.apellidos}\n${h.gradoNombre} ${h.seccionNombre}",
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

// Tarjeta con el resumen de un hijo (iniciales, nombre, grado y código) para los popups.
// Fondo claro del color del borde: granate al quitar, turquesa al asociar.
@Composable
fun ResumenHijoEdu(hijo: HijoAsociado, colorBorde: Color = EduTeal) {
    val fondo = if (colorBorde == EduCoral) EduCoralClaro else EduTealClaro
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(fondo, RoundedCornerShape(16.dp))
            .border(1.5.dp, colorBorde, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InicialesHijoPerfil(hijo, Color.White, tamano = 40)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "${hijo.nombres} ${hijo.apellidos}".trim(), fontFamily = Nunito, fontWeight = FontWeight.Black,
                fontSize = 15.sp, color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                "${hijo.gradoNombre} ${hijo.seccionNombre} · Código ${hijo.codigoEstudiante}",
                fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
            )
        }
    }
}

// Un hijo: iniciales, nombre, grado · código y botón quitar
@Composable
private fun TarjetaHijo(h: HijoAsociado, colorAvatar: Color, onQuitar: () -> Unit) {
    TarjetaEdu(relleno = PaddingValues(horizontal = 14.dp, vertical = 12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InicialesHijoPerfil(h, colorAvatar, tamano = 46)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${h.nombres} ${h.apellidos}".trim(), fontFamily = Nunito, fontWeight = FontWeight.Black,
                    fontSize = 15.5.sp, color = EduTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${h.gradoNombre} ${h.seccionNombre} · Código ${h.codigoEstudiante}",
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
                Icon(Icons.Rounded.PersonRemove, contentDescription = "Quitar hijo", tint = EduCoral, modifier = Modifier.size(21.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun MisHijosPreview() {
    EduConnectAppTheme {
        MisHijosScreen(
            hijos = listOf(
                HijoAsociado(1L, "Angela", "Perez Garcia", "2do Grado", "A", "PMU0006"),
                HijoAsociado(2L, "Pedro", "Perez Garcia", "1er Grado", "A", "PMU0001")
            )
        )
    }
}
