package com.educonnectapp.ui.screens.docente.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupExitoEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.ResumenAsignacionEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
import com.educonnectapp.ui.screens.docente.inicio.BottomNavBarDocente
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// Error del servidor al registrar o quitar la asignación (lo arma MainActivity y se muestra como popup)
data class AlertaAsignacion(val titulo: String, val mensaje: String)

// =====================================================================
// AGREGAR ASIGNACIÓN (rediseño 2026)
// Cabecera fija azul oscuro · grado y sección con pastillas · cursos en tarjetas ·
// resumen + Cancelar / Agregar fijos abajo
// =====================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgregarAsignacionScreen(
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    gradosDisp: List<String> = emptyList(),
    seccionesDisp: Map<String, List<String>> = emptyMap(),
    cursosDisp: List<String> = emptyList(),
    onGuardar: (grado: String, seccion: String, curso: String) -> Unit = { _, _, _ -> },
    onCancelar: () -> Unit = {},
    // Estado del registro (lo controla MainActivity)
    procesando: Boolean = false,
    exito: Boolean = false,
    alerta: AlertaAsignacion? = null,
    onCerrarExito: () -> Unit = {},
    onCerrarAlerta: () -> Unit = {}
) {
    var grado by remember { mutableStateOf("") }
    var seccion by remember { mutableStateOf("") }
    var curso by remember { mutableStateOf("") }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    val secciones = if (grado.isNotEmpty()) seccionesDisp[grado] ?: emptyList() else emptyList()
    val completa = grado.isNotEmpty() && seccion.isNotEmpty() && curso.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Agregar asignación",
            subtitulo = "Elige grado, sección y curso",
            onVolver = onBack,
            colorFondo = ColorModulo.Perfil,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Notifications,
                    descripcion = "Notificaciones",
                    onClick = onNotificaciones,
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
            // GRADO
            TituloSeccionEdu("Grado")
            if (gradosDisp.isEmpty()) {
                MensajeVacioAsignacion("Cargando grados...")
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    gradosDisp.forEach { g ->
                        PastillaAsignacion(g, g == grado) { grado = g; seccion = "" }
                    }
                }
            }

            // SECCIÓN (aparece al elegir el grado)
            if (grado.isNotEmpty()) {
                TituloSeccionEdu("Sección", modifier = Modifier.padding(top = 6.dp))
                if (secciones.isEmpty()) {
                    MensajeVacioAsignacion("Este grado aún no tiene secciones registradas.")
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        secciones.forEach { s -> PastillaAsignacion("Sección $s", s == seccion) { seccion = s } }
                    }
                }
            }

            // CURSO
            TituloSeccionEdu("Curso", modifier = Modifier.padding(top = 6.dp))
            if (cursosDisp.isEmpty()) MensajeVacioAsignacion("Cargando cursos...")
            cursosDisp.forEach { c ->
                val elegido = c == curso
                val forma = RoundedCornerShape(18.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(forma)
                        .background(if (elegido) EduTealClaro else Color.White)
                        .border(if (elegido) 2.dp else 1.5.dp, if (elegido) EduTeal else EduBorde, forma)
                        .clickable { curso = c }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        c, modifier = Modifier.weight(1f), fontFamily = Nunito,
                        fontWeight = if (elegido) FontWeight.Black else FontWeight.Bold, fontSize = 15.5.sp,
                        color = if (elegido) EduTealOscuro else EduTexto
                    )
                    if (elegido) {
                        Box(
                            modifier = Modifier.size(26.dp).background(EduTeal, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // ZONA FIJA: resumen + botones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (completa) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(18.dp))
                        .border(1.5.dp, EduBorde, RoundedCornerShape(18.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconoCirculoEdu(Icons.Rounded.School, TonoEdu.VERDE, tamano = 40.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("$grado $seccion · $curso", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.5.sp, color = EduTexto)
                        Text("Se agregará a tus clases", fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduEtiqueta)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BotonSecundarioEdu(texto = "Cancelar", modifier = Modifier.weight(1f), onClick = onCancelar)
                BotonPrincipalEdu(
                    texto = "Agregar",
                    tipo = TipoBotonEdu.GUARDAR,
                    icono = Icons.Rounded.Add,
                    modifier = Modifier.weight(1f),
                    habilitado = completa && !procesando,
                    onClick = { if (completa) mostrarConfirmacion = true }
                )
            }
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
    if (mostrarConfirmacion) {
        PopupConfirmacionEdu(
            titulo = "¿Agregar asignación?",
            mensaje = "Se agregará a tus clases la siguiente asignación:",
            textoConfirmar = "Agregar",
            colorConfirmar = TipoBotonEdu.GUARDAR.fondo,
            icono = Icons.Rounded.School,
            onCancelar = { mostrarConfirmacion = false },
            onConfirmar = {
                mostrarConfirmacion = false
                onGuardar(grado, seccion, curso)
            }
        ) {
            ResumenAsignacionEdu(grado, seccion, curso)
        }
    }

    if (procesando) PopupProcesandoEdu(titulo = "Registrando asignación…")

    if (exito) {
        PopupExitoEdu(
            titulo = "¡Asignación agregada!",
            detalle = "$grado $seccion\n$curso",
            mensaje = "Ya aparece en tus clases.",
            onCerrar = onCerrarExito
        )
    }

    // Error (por ejemplo, asignación ya registrada): el docente se queda en la pantalla
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

// Pastilla seleccionable (grado o sección): turquesa sólido cuando está elegida
@Composable
private fun PastillaAsignacion(texto: String, elegida: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(50)
    Text(
        texto,
        modifier = Modifier
            .clip(forma)
            .background(if (elegida) EduTeal else Color.White)
            .border(1.5.dp, if (elegida) EduTeal else EduBorde, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        fontFamily = Nunito, fontWeight = if (elegida) FontWeight.Black else FontWeight.Bold,
        fontSize = 14.5.sp, color = if (elegida) Color.White else EduEtiqueta
    )
}

@Composable
private fun MensajeVacioAsignacion(texto: String) {
    TarjetaEdu {
        Text(texto, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = EduTextoSecundario)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun AgregarAsignacionPreview() {
    EduConnectAppTheme {
        AgregarAsignacionScreen(
            gradosDisp = listOf("1er Grado", "2do Grado", "3er Grado"),
            seccionesDisp = mapOf("3er Grado" to listOf("A", "B")),
            cursosDisp = listOf("Matemáticas", "Comunicación", "Ciencia y Tecnología")
        )
    }
}
