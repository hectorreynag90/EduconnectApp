package com.educonnectapp.ui.screens.docente.asistencia

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.ContadorEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Alumno cuya asistencia se cambió al actualizar (estados "A", "T" o "F")
data class CambioAsistenciaItem(
    val alumno: String,
    val estadoAnterior: String,
    val estadoNuevo: String
)

private fun tonoLetra(estado: String): TonoEdu = when (estado) {
    "A" -> TonoEdu.VERDE
    "T" -> TonoEdu.AMARILLO
    "F" -> TonoEdu.CORAL
    else -> TonoEdu.GRIS
}

private fun textoEstado(estado: String): String = when (estado) {
    "A" -> "Asistió"
    "T" -> "Tardanza"
    "F" -> "Faltó"
    else -> "–"
}

// =====================================================================
// CONFIRMACIÓN DE ASISTENCIA (rediseño 2026)
// Check turquesa · padres notificados · datos del registro · contadores · cambios (si se actualizó)
// =====================================================================

@Composable
fun ConfirmacionAsistenciaScreen(
    docenteNombre: String,
    curso: String,
    grado: String,
    seccion: String,
    totalPresentes: Int,
    totalAusentes: Int,
    totalTardanzas: Int = 0,
    fecha: String = "",                                   // "yyyy-MM-dd" de la hoja (servidor)
    esActualizacion: Boolean = false,                     // true si se editó una asistencia ya registrada
    cambios: List<CambioAsistenciaItem> = emptyList(),    // solo en actualización
    onNuevaAsistencia: () -> Unit = {},
    onVerHistorial: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    // Al actualizar solo se notifica a los padres de los alumnos que cambiaron
    val totalNotificados = if (esActualizacion) cambios.size
    else totalPresentes + totalAusentes + totalTardanzas

    // Fecha y hora de Perú (aunque el celular tenga otra zona horaria)
    val zonaPeru = remember { TimeZone.getTimeZone("America/Lima") }
    val fechaDisplay = remember(fecha) {
        val formato = SimpleDateFormat("EEEE d 'de' MMMM yyyy", Locale("es", "PE")).apply { timeZone = zonaPeru }
        val dia = try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = zonaPeru }.parse(fecha)
        } catch (e: Exception) { null } ?: Date()
        formato.format(dia).replaceFirstChar { it.uppercase() }
    }
    val horaDisplay = remember {
        SimpleDateFormat("hh:mm a", Locale("es", "PE")).apply { timeZone = zonaPeru }.format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA con X para cerrar
        CabeceraEdu(
            titulo = if (esActualizacion) "Asistencia actualizada" else "Asistencia registrada",
            subtitulo = "$curso · $grado $seccion",
            colorFondo = ColorModulo.Asistencia,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Close,
                    descripcion = "Cerrar",
                    onClick = onClose,
                    colorCabecera = ColorModulo.Asistencia
                )
            }
        )

        // CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // CHECK TURQUESA CON HALO
            Box(
                modifier = Modifier.padding(top = 6.dp).size(96.dp).background(EduTealClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(72.dp).background(EduTeal, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(42.dp))
                }
            }
            Text(
                if (esActualizacion) "¡Asistencia actualizada!" else "¡Asistencia registrada!",
                fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 23.sp,
                color = EduTealOscuro, textAlign = TextAlign.Center
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipEdu(
                    "$totalNotificados " + (if (totalNotificados == 1) "padre notificado" else "padres notificados"),
                    TonoEdu.VERDE, icono = Icons.Rounded.Groups
                )
                ChipEdu(horaDisplay, TonoEdu.GRIS, icono = Icons.Rounded.Schedule)
            }

            // DATOS DEL REGISTRO
            TarjetaEdu(relleno = PaddingValues(16.dp)) {
                TituloSeccionEdu("Datos del registro")
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilaDato(Icons.Rounded.CalendarToday, "Fecha", fechaDisplay)
                    FilaDato(Icons.Rounded.Person, "Docente", docenteNombre)
                    FilaDato(Icons.Rounded.MenuBook, "Curso", curso)
                    FilaDato(Icons.Rounded.Groups, "Grado y sección", "$grado $seccion")
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ContadorEdu(totalPresentes, "Asistió", TonoEdu.VERDE, Modifier.weight(1f))
                    ContadorEdu(totalTardanzas, "Tardanza", TonoEdu.AMARILLO, Modifier.weight(1f))
                    ContadorEdu(totalAusentes, "Faltó", TonoEdu.CORAL, Modifier.weight(1f))
                }
            }

            // ALUMNOS ACTUALIZADOS (solo en actualización): estado anterior → nuevo
            if (esActualizacion && cambios.isNotEmpty()) {
                TarjetaEdu(relleno = PaddingValues(16.dp)) {
                    TituloSeccionEdu("Alumnos actualizados")
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        cambios.forEach { c ->
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    c.alumno, fontFamily = Nunito, fontWeight = FontWeight.Black,
                                    fontSize = 14.5.sp, color = EduTexto
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ChipEdu(textoEstado(c.estadoAnterior), tonoLetra(c.estadoAnterior))
                                    Icon(
                                        Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = "cambia a",
                                        tint = EduTextoSecundario, modifier = Modifier.size(16.dp)
                                    )
                                    ChipEdu(textoEstado(c.estadoNuevo), tonoLetra(c.estadoNuevo), solido = true)
                                }
                            }
                        }
                    }
                }
            }
        }

        // BOTONES FIJOS ABAJO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BotonPrincipalEdu(
                texto = "Tomar otra asistencia",
                tipo = TipoBotonEdu.AVANZAR,
                icono = Icons.Rounded.FactCheck,
                onClick = onNuevaAsistencia
            )
            BotonSecundarioEdu(texto = "Ver historial", icono = Icons.Rounded.History, onClick = onVerHistorial)
        }
    }
}

// Fila de dato con ícono: "Fecha        Lunes 19 de octubre 2026"
@Composable
private fun FilaDato(icono: ImageVector, etiqueta: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(20.dp))
        Text(
            etiqueta, modifier = Modifier.padding(start = 8.dp).width(118.dp),
            fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EduTextoSecundario
        )
        Text(
            valor, modifier = Modifier.weight(1f), textAlign = TextAlign.End,
            fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp, color = EduEtiqueta
        )
    }
}

// FILA DE DATO (diseño anterior). Se conserva porque la usan otras pantallas de confirmación.
@Composable
fun FilaRegistro(
    iconRes: Int,
    label: String,
    valor: String,
    colorValor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = label,
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextSecondary
            )
        }
        Text(
            text = valor,
            fontFamily = Roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = colorValor,
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ConfirmacionAsistenciaPreview() {
    EduConnectAppTheme {
        ConfirmacionAsistenciaScreen(
            docenteNombre = "Hector Alexander Reyna Gomez",
            curso = "Ciencia y Tecnología",
            grado = "1er Grado",
            seccion = "A",
            totalPresentes = 21,
            totalAusentes = 2,
            totalTardanzas = 1,
            esActualizacion = true,
            cambios = listOf(
                CambioAsistenciaItem("Sanchez Vega, Pedro", "F", "T"),
                CambioAsistenciaItem("Mendoza Cruz, Diego", "A", "F")
            )
        )
    }
}
