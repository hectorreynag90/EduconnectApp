package com.educonnectapp.ui.screens.docente.publicaciones

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
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoralOscuro
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// =====================================================================
// PUBLICACIÓN CONFIRMADA (rediseño 2026)
// Pantalla informativa: cabecera amarilla con X a la derecha (sin campana) ·
// check turquesa · datos · "Nueva tarea / Nuevo aviso" y "Ver publicaciones de la clase"
// =====================================================================

@Composable
fun ConfirmacionPublicacionScreen(
    tipo: String = "Tarea",
    titulo: String = "",
    gradoNombre: String = "",
    seccionNombre: String = "",
    cursoNombre: String = "",
    totalNotificados: Int = 0,
    fechaEntrega: String = "",
    hora: String = "",
    onNuevaPublicacion: () -> Unit = {},
    onVerHistorial: () -> Unit = {},      // "Ver publicaciones de la clase"
    onVolver: () -> Unit = {},            // la X
    // ----- Rediseño 2026 -----
    adjunto: String = ""                  // nombre del adjunto ("" = sin adjunto)
) {
    val esTarea = tipo == "Tarea"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA: solo la X (pantalla informativa)
        CabeceraEdu(
            titulo = if (esTarea) "Tarea publicada" else "Aviso publicado",
            subtitulo = "Ya se notificó a los padres",
            colorFondo = ColorModulo.Publicaciones,
            acciones = {
                BotonIconoCabeceraEdu(
                    icono = Icons.Rounded.Close,
                    descripcion = "Cerrar",
                    onClick = onVolver,
                    colorCabecera = ColorModulo.Publicaciones
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
                if (esTarea) "¡Tarea publicada!" else "¡Aviso de evaluación publicado!",
                fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 22.sp,
                color = EduTealOscuro, textAlign = TextAlign.Center
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipEdu(
                    if (totalNotificados == 1) "1 padre notificado" else "$totalNotificados padres notificados",
                    TonoEdu.VERDE
                )
                if (hora.isNotBlank()) ChipEdu(hora, TonoEdu.AZUL)
            }

            Spacer(modifier = Modifier.height(4.dp))
            TarjetaEdu(relleno = PaddingValues(horizontal = 16.dp, vertical = 14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconoCirculoEdu(
                        if (esTarea) Icons.AutoMirrored.Rounded.Assignment else Icons.Rounded.Quiz,
                        if (esTarea) TonoEdu.AZUL else TonoEdu.AMARILLO, tamano = 38.dp
                    )
                    Text(
                        titulo, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Black,
                        fontSize = 16.sp, color = EduTexto, maxLines = 3, overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                FilaDatoPub(Icons.Rounded.Groups, "Para", "$gradoNombre $seccionNombre".trim())
                FilaDatoPub(Icons.Rounded.MenuBook, "Curso", cursoNombre)
                FilaDatoPub(
                    Icons.Rounded.CalendarToday,
                    if (esTarea) "Fecha límite" else "Evaluación",
                    if (fechaEntrega.isNotBlank()) fechaLargaPub(fechaEntrega) else "—",
                    colorValor = EduCoralOscuro
                )
                if (adjunto.isNotBlank()) FilaDatoPub(Icons.Rounded.AttachFile, "Adjunto", adjunto)
            }
        }

        // BOTONES FIJOS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BotonPrincipalEdu(
                texto = if (esTarea) "Nueva tarea" else "Nuevo aviso de evaluación",
                tipo = TipoBotonEdu.GUARDAR,
                icono = Icons.Rounded.Add,
                onClick = onNuevaPublicacion
            )
            BotonSecundarioEdu(
                texto = "Ver publicaciones de la clase",
                icono = Icons.Rounded.Visibility,
                onClick = onVerHistorial
            )
        }
    }
}

// Fila "ícono · etiqueta · valor"
@Composable
private fun FilaDatoPub(icono: ImageVector, etiqueta: String, valor: String, colorValor: Color = EduTexto) {
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
            etiqueta, modifier = Modifier.width(92.dp), fontFamily = Nunito, fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp, color = EduTextoSecundario
        )
        Text(
            valor, modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.Black,
            fontSize = 14.5.sp, color = colorValor, maxLines = 2, overflow = TextOverflow.Ellipsis
        )
    }
}

// Se conserva por compatibilidad (pantallas anteriores la usaban); ya no se usa en el rediseño
@Composable
fun DetalleRow(label: String, valor: String, colorValor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontFamily = Nunito, fontSize = 14.sp, color = EduEtiqueta)
        Text(valor, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = colorValor)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ConfirmacionPublicacionPreview() {
    EduConnectAppTheme {
        ConfirmacionPublicacionScreen(
            tipo = "Tarea",
            titulo = "Ejercicios cap. 5 · Fracciones",
            gradoNombre = "1er Grado",
            seccionNombre = "A",
            cursoNombre = "Ciencia y Tecnología",
            totalNotificados = 5,
            fechaEntrega = "2026-10-23",
            hora = "10:40 a. m.",
            adjunto = "practica_fracciones.pdf"
        )
    }
}
