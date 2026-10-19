package com.educonnectapp.ui.screens.padre.perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupExitoEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
import com.educonnectapp.ui.screens.padre.inicio.BottomNavBarPadre
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.FondoRojo
import com.educonnectapp.ui.theme.NaranjaMedia
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.RojoError
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite

private val FondoAzulHijos = Color(0xFFE6F1FB)

// Error al quitar o asociar un hijo (lo arma MainActivity y se muestra como popup)
data class AlertaHijo(val titulo: String, val mensaje: String)

// Gestionar hijos: lista con el botón para quitar (desasociar) cada uno
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
            .background(BackgroundLight)
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduconnectBlue)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(35.dp).offset(x = (-5).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(text = "Mis hijos", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextWhite)
                    Text(text = "Estudiantes asociados a tu cuenta", fontFamily = Roboto,
                        fontSize = 16.sp, color = TextWhite.copy(alpha = 0.85f))
                }
            }
            IconButton(onClick = onNotificaciones) {
                Image(
                    painter = painterResource(id = R.drawable.notification_white),
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(35.dp)
                )
            }
        }

        // CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (hijos.isEmpty()) "SIN HIJOS ASOCIADOS" else "HIJOS ASOCIADOS (${hijos.size})",
                fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                color = EduconnectBlue, modifier = Modifier.padding(start = 4.dp)
            )

            if (hijos.isEmpty()) {
                Text(
                    text = "Aún no tienes hijos asociados. Usa \"Asociar hijo\" con el código que te dio la institución.",
                    fontFamily = Roboto, fontSize = 15.sp, color = TextSecondary,
                    lineHeight = 20.sp, modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            hijos.forEach { hijo ->
                FilaHijo(hijo = hijo, onQuitar = { porQuitar = hijo })
            }

            // Aviso: qué pasa al quitar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoAzulHijos, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Info, contentDescription = null,
                    tint = EduconnectBlue, modifier = Modifier.size(20.dp))
                Text(
                    text = "Al quitar a un hijo dejarás de recibir sus asistencias, comunicados y tareas. " +
                            "Su historial se conserva y puedes volver a asociarlo con su código.",
                    fontFamily = Roboto, fontSize = 14.sp, color = TextBlue, lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÓN ASOCIAR
            Button(
                onClick = onAsociar,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
            ) {
                Image(painter = painterResource(id = R.drawable.useradd_white),
                    contentDescription = null, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Asociar hijo", fontFamily = Roboto,
                    fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextWhite)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // NAVBAR
        BottomNavBarPadre(
            onInicio = onHomePadre,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Perfil"
        )
    }

    // ===================== POPUPS (PopupsEdu.kt) =====================
    // 1. Confirmación (en rojo)
    porQuitar?.let { h ->
        PopupConfirmacionEdu(
            titulo = "¿Quitar a tu hijo?",
            mensaje = "Dejarás de recibir sus notificaciones. Podrás volver a asociarlo con su código.",
            textoConfirmar = "Quitar",
            colorConfirmar = RojoError,
            onCancelar = { porQuitar = null },
            onConfirmar = {
                porQuitar = null
                onQuitar(h)
            }
        ) {
            ResumenHijoEdu(h, colorBorde = RojoError)
        }
    }

    // 2. Procesando
    if (procesando) PopupProcesandoEdu(titulo = "Quitando hijo…")

    // 3. Éxito: se cierra solo a los 5 segundos o con la X (se queda en la lista)
    quitado?.let { h ->
        PopupExitoEdu(
            titulo = "¡Hijo desasociado con éxito!",
            detalle = "${h.nombres} ${h.apellidos}\n${h.gradoNombre} · Sec. ${h.seccionNombre}",
            mensaje = "Ya no aparece en tu perfil.",
            onCerrar = onCerrarExito
        )
    }

    // Error
    alerta?.let {
        PopupAlertaEdu(
            icono = Icons.Filled.Warning,
            color = NaranjaMedia,
            titulo = it.titulo,
            mensaje = it.mensaje,
            onCerrar = onCerrarAlerta
        )
    }
}

// Tarjeta con el resumen de un hijo (iniciales, nombre, grado, sección y código).
// También la usa AsociarHijoScreen en su confirmación.
@Composable
fun ResumenHijoEdu(hijo: HijoAsociado, colorBorde: Color = EduconnectBlue) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoAzulHijos, RoundedCornerShape(12.dp))
            .border(1.dp, colorBorde, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InicialesHijo(hijo, fondo = BackgroundWhite)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "${hijo.nombres} ${hijo.apellidos}", fontFamily = Roboto,
                fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EduconnectBlue)
            Text(text = "${hijo.gradoNombre} · Sec. ${hijo.seccionNombre}", fontFamily = Roboto,
                fontSize = 14.sp, color = TextPrimary)
            Text(text = "Código: ${hijo.codigoEstudiante}", fontFamily = Roboto,
                fontSize = 14.sp, color = TextPrimary)
        }
    }
}

@Composable
private fun InicialesHijo(hijo: HijoAsociado, fondo: Color = FondoAzulHijos) {
    val iniciales = "${hijo.nombres.firstOrNull() ?: ""}${hijo.apellidos.firstOrNull() ?: ""}".uppercase()
    Box(
        modifier = Modifier.size(44.dp).background(fondo, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = iniciales, fontFamily = Roboto, fontWeight = FontWeight.Bold,
            fontSize = 18.sp, color = EduconnectBlue)
    }
}

// Un hijo: iniciales, nombre, grado · sección, código y botón quitar
@Composable
private fun FilaHijo(hijo: HijoAsociado, onQuitar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(14.dp))
            .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
            .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InicialesHijo(hijo)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "${hijo.nombres} ${hijo.apellidos}", fontFamily = Roboto,
                fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextBlue)
            Text(text = "${hijo.gradoNombre} · Sec. ${hijo.seccionNombre}", fontFamily = Roboto,
                fontSize = 15.sp, color = TextPrimary)
            Text(text = "Código: ${hijo.codigoEstudiante}", fontFamily = Roboto,
                fontSize = 13.sp, color = TextSecondary)
        }
        IconButton(onClick = onQuitar) {
            Box(
                modifier = Modifier.size(38.dp).background(FondoRojo, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.PersonRemove, contentDescription = "Quitar hijo",
                    tint = RojoError, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MisHijosPreview() {
    EduConnectAppTheme {
        MisHijosScreen(
            hijos = listOf(
                HijoAsociado(1L, "Angela", "Perez Garcia", "2do", "A", "PMU0006"),
                HijoAsociado(2L, "Pedro", "Sanchez Vega", "1er", "A", "PMU0001")
            )
        )
    }
}
