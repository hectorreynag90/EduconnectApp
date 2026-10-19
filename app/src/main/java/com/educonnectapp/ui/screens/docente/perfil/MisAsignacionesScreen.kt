package com.educonnectapp.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
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
import com.educonnectapp.ui.components.ResumenAsignacionEdu
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.NaranjaMedia
import com.educonnectapp.ui.theme.RojoError
import com.educonnectapp.ui.theme.FondoRojo
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite

private val FondoAzulMis = Color(0xFFE6F1FB)

// Asignación que se muestra en la lista (la arma MainActivity desde AsignacionResponse)
data class AsignacionGestion(
    val id: Long,
    val grado: String,
    val seccion: String,
    val curso: String,
    val alumnos: Long
)

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
                    Text(text = "Mis asignaciones", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextWhite)
                    Text(text = "Grados, secciones y cursos a tu cargo", fontFamily = Roboto,
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
                text = if (asignaciones.isEmpty()) "SIN ASIGNACIONES"
                else "ASIGNACIONES (${asignaciones.size})",
                fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                color = EduconnectBlue, modifier = Modifier.padding(start = 4.dp)
            )

            if (asignaciones.isEmpty()) {
                Text(
                    text = "Aún no tienes grados ni cursos asignados. Usa \"Agregar asignación\" para empezar.",
                    fontFamily = Roboto, fontSize = 15.sp, color = TextSecondary,
                    lineHeight = 20.sp, modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            asignaciones.forEach { a ->
                FilaAsignacion(asignacion = a, onEliminar = { porEliminar = a })
            }

            // Aviso: el historial se conserva
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoAzulMis, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Info, contentDescription = null,
                    tint = EduconnectBlue, modifier = Modifier.size(20.dp))
                Text(
                    text = "Al eliminar una asignación dejarás de verla en la app. Las asistencias, " +
                            "comunicados y tareas ya registrados se conservan.",
                    fontFamily = Roboto, fontSize = 14.sp, color = TextBlue, lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÓN AGREGAR
            Button(
                onClick = onAgregar,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
            ) {
                Image(painter = painterResource(id = R.drawable.add_white),
                    contentDescription = null, modifier = Modifier.size(25.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Agregar asignación", fontFamily = Roboto,
                    fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextWhite)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // NAVBAR
        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfil,
            itemActivo = "Perfil"
        )
    }

    // ===================== POPUPS (PopupsEdu.kt) =====================
    // 1. Confirmación
    porEliminar?.let { a ->
        PopupConfirmacionEdu(
            titulo = "¿Eliminar asignación?",
            mensaje = "Dejarás de tener a cargo esta asignación. El historial registrado se conserva.",
            textoConfirmar = "Eliminar",
            colorConfirmar = RojoError,
            onCancelar = { porEliminar = null },
            onConfirmar = {
                porEliminar = null
                onEliminar(a)
            }
        ) {
            ResumenAsignacionEdu(a.grado, a.seccion, a.curso, colorBorde = RojoError)
        }
    }

    // 2. Procesando
    if (procesando) PopupProcesandoEdu(titulo = "Eliminando asignación…")

    // 3. Éxito: se cierra solo a los 5 segundos o con la X (se queda en la lista)
    eliminada?.let { a ->
        PopupExitoEdu(
            titulo = "¡Asignación eliminada con éxito!",
            detalle = "${a.grado} · Sec. ${a.seccion}\n${a.curso}",
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

// Una asignación: grado · sección, curso, alumnos y botón eliminar
@Composable
private fun FilaAsignacion(asignacion: AsignacionGestion, onEliminar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(14.dp))
            .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
            .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(44.dp)
                .background(EduconnectBlue, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.teacher_white),
                contentDescription = null, modifier = Modifier.size(24.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "${asignacion.grado} · Sec. ${asignacion.seccion}", fontFamily = Roboto,
                fontWeight = FontWeight.Bold, fontSize = 17.sp, color = EduconnectBlue)
            Text(text = asignacion.curso, fontFamily = Roboto,
                fontWeight = FontWeight.Medium, fontSize = 15.sp, color = TextPrimary)
            Text(
                text = if (asignacion.alumnos == 1L) "1 alumno" else "${asignacion.alumnos} alumnos",
                fontFamily = Roboto, fontSize = 13.sp, color = TextSecondary
            )
        }
        IconButton(onClick = onEliminar) {
            Box(
                modifier = Modifier.size(38.dp).background(FondoRojo, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar asignación",
                    tint = RojoError, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MisAsignacionesPreview() {
    EduConnectAppTheme {
        MisAsignacionesScreen(
            asignaciones = listOf(
                AsignacionGestion(1, "1er Grado", "A", "Ciencia y Tecnología", 5),
                AsignacionGestion(2, "2do Grado", "A", "Religión", 1)
            )
        )
    }
}
