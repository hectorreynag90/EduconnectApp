package com.educonnectapp.ui.screens.docente.comunicados

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.text.SimpleDateFormat
import java.util.Locale

// Verde de las pantallas anteriores. Ya no se usa aquí; se conserva por si otra
// pantalla aún no rediseñada lo importa. Se elimina al terminar el rediseño.
val ConfirmGreen = Color(0xFF00B050)

// "2026-10-19" -> "Lunes 19 de octubre"
private fun fechaConfirmacion(fecha: String): String = try {
    val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha.take(10))
    SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "PE")).format(d!!).replaceFirstChar { it.uppercase() }
} catch (e: Exception) { fecha }

// =====================================================================
// COMUNICADO ENVIADO (rediseño 2026)
// Pantalla informativa: cabecera granate con X a la derecha (sin campana) ·
// check turquesa · datos del envío · "Nuevo comunicado" / "Ver comunicados de la clase"
// =====================================================================

@Composable
fun ConfirmacionComunicadoScreen(
    docenteNombre: String,
    curso: String,
    asunto: String,
    destinatario: String,
    totalNotificados: Int,
    hora: String,
    fecha: String,
    onNuevoComunicado: () -> Unit = {},
    onVerHistorial: () -> Unit = {},     // "Ver comunicados de la clase"
    onClose: () -> Unit = {},
    // ----- Rediseño 2026 -----
    adjunto: String = ""                 // nombre del archivo adjunto ("" = sin adjunto)
) {
    val fechaTexto = remember(fecha) { fechaConfirmacion(fecha) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA: solo la X para cerrar (pantalla informativa)
        CabeceraEdu(
            titulo = "Comunicado enviado",
            subtitulo = "Ya se notificó a los padres",
            colorFondo = ColorModulo.Comunicados,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Close,
                    descripcion = "Cerrar",
                    onClick = onClose,
                    colorCabecera = ColorModulo.Comunicados
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Check turquesa con halo
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier.size(96.dp).background(EduTealClaro, CircleShape),
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
                "¡Comunicado enviado!", fontFamily = Nunito, fontWeight = FontWeight.Black,
                fontSize = 22.sp, color = EduTealOscuro, textAlign = TextAlign.Center
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipEdu(
                    if (totalNotificados == 1) "1 padre notificado" else "$totalNotificados padres notificados",
                    TonoEdu.VERDE
                )
                if (hora.isNotBlank()) ChipEdu(hora, TonoEdu.AZUL)
            }

            // DATOS DEL ENVÍO
            Spacer(modifier = Modifier.height(4.dp))
            TarjetaEdu(relleno = PaddingValues(horizontal = 16.dp, vertical = 14.dp)) {
                Text(
                    asunto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.sp,
                    color = EduTexto, maxLines = 3, overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                FilaDatoComunicado(Icons.Rounded.Groups, "Para", destinatario.replace(" Sec. ", " "))
                FilaDatoComunicado(Icons.Rounded.MenuBook, "Curso", curso.ifBlank { "General" })
                FilaDatoComunicado(Icons.Rounded.CalendarToday, "Fecha", fechaTexto)
                if (adjunto.isNotBlank()) FilaDatoComunicado(Icons.Rounded.AttachFile, "Adjunto", adjunto)
                if (docenteNombre.isNotBlank()) FilaDatoComunicado(Icons.Rounded.Person, "Docente", docenteNombre)
            }
        }

        // BOTONES FIJOS ABAJO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BotonPrincipalEdu(
                texto = "Nuevo comunicado",
                tipo = TipoBotonEdu.GUARDAR,
                icono = Icons.Rounded.Add,
                onClick = onNuevoComunicado
            )
            BotonSecundarioEdu(
                texto = "Ver comunicados de la clase",
                icono = Icons.Rounded.Visibility,
                onClick = onVerHistorial
            )
        }
    }
}

// Fila "ícono · etiqueta · valor" de los datos del envío
@Composable
private fun FilaDatoComunicado(icono: ImageVector, etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(34.dp).background(EduAzulOscuroClaro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(17.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            etiqueta, modifier = Modifier.width(76.dp), fontFamily = Nunito, fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp, color = EduTextoSecundario
        )
        Text(
            valor, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Black,
            fontSize = 14.5.sp, color = EduTexto, maxLines = 2, overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ConfirmacionComunicadoPreview() {
    EduConnectAppTheme {
        ConfirmacionComunicadoScreen(
            docenteNombre = "Hector Reyna Gomez",
            curso = "Ciencia y Tecnología",
            asunto = "Reunión de padres · viernes 23",
            destinatario = "1er Grado Sec. A",
            totalNotificados = 5,
            hora = "10:24 a. m.",
            fecha = "2026-10-19",
            adjunto = "agenda_reunion.pdf"
        )
    }
}
