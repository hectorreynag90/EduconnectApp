package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class PublicacionHistorialItem(
    val id: Long,
    val tipo: String,              // "Tarea" | "Evaluación"
    val titulo: String,
    val cursoNombre: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val fechaEntrega: String,      // "yyyy-MM-dd" (puede venir vacía)
    val fechaPublicacion: String,  // "yyyy-MM-dd"
    val totalAlumnos: Int,
    val calificados: Int,
    val lecturas: Int,
    val estado: String = "ACTIVA"  // "ACTIVA" | "CERRADA" | "ANULADA"
)

@Composable
fun HistorialPublicacionesScreen(
    publicaciones: List<PublicacionHistorialItem> = emptyList(),
    cargando: Boolean = false,
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onVerDetalle: (PublicacionHistorialItem) -> Unit = {}
) {
    var tabSeleccionado by remember { mutableStateOf("Todas") }

    val fechaActual = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
            .format(Date()).replaceFirstChar { it.uppercase() }
    }

    val filtradas = publicaciones.filter {
        when (tabSeleccionado) {
            "Tareas" -> it.tipo == "Tarea"
            "Evaluaciones" -> it.tipo == "Evaluación"
            else -> true
        }
    }

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
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
                    Text(
                        text = "Historial Publicaciones",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "${publicaciones.size} publicaciones",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.85f)
                    )
                    Text(
                        text = fechaActual,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = TextWhite.copy(alpha = 0.7f)
                    )
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // TABS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("Todas", "Tareas", "Evaluaciones").forEachIndexed { index, tab ->
                    val activo = tabSeleccionado == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (activo) EduconnectBlue else Color.White)
                            .border(1.5.dp, if (activo) EduconnectBlue else BorderBlue, RoundedCornerShape(20.dp))
                            .clickable { tabSeleccionado = tab }
                            .padding(horizontal = 22.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = if (activo) TextWhite else TextPrimary
                        )
                    }
                    if (index < 2) Spacer(modifier = Modifier.width(8.dp))
                }
            }

            when {
                cargando && publicaciones.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EduconnectBlue)
                    }
                }
                filtradas.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BackgroundWhite, RoundedCornerShape(16.dp))
                            .border(1.dp, BorderBlue, RoundedCornerShape(16.dp))
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay publicaciones",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filtradas, key = { it.id }) { item ->
                            PublicacionHistorialCard(item = item, onClick = { onVerDetalle(item) })
                        }
                    }
                }
            }
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Inicio"
        )
    }
}

// "2026-10-08" -> "Jue 08 oct"
private fun fechaCortaHistorial(fecha: String): String = try {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    SimpleDateFormat("EEE dd MMM", Locale("es", "PE"))
        .format(sdf.parse(fecha)!!).replaceFirstChar { it.uppercase() }
} catch (e: Exception) { fecha }

@Composable
fun PublicacionHistorialCard(item: PublicacionHistorialItem, onClick: () -> Unit) {
    val esEvaluacion = item.tipo == "Evaluación"
    val anulada = item.estado == "ANULADA"
    val completo = item.totalAlumnos > 0 && item.calificados >= item.totalAlumnos

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(16.dp))
            .border(1.5.dp, if (anulada) BorderMedium else BorderBlue, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(if (esEvaluacion) EduconnectBlue else EduconnectBlueMedium, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = if (esEvaluacion) R.drawable.exam_white else R.drawable.task_white),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.titulo,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = if (anulada) TextSecondary else TextBlue,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.cursoNombre} · ${item.gradoNombre} Sec. ${item.seccionNombre}",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
            Image(
                painter = painterResource(id = R.drawable.next_gray),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fecha límite / de la evaluación
            Text(
                text = (if (esEvaluacion) "Evaluación: " else "Entrega: ") +
                        (if (item.fechaEntrega.isNotEmpty()) fechaCortaHistorial(item.fechaEntrega) else "—"),
                fontFamily = Roboto,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextOrange,
                modifier = Modifier.weight(1f)
            )
            if (item.estado != "ACTIVA") {
                ChipHistorial(
                    texto = if (anulada) "Anulada" else "Cerrada",
                    color = if (anulada) StatusErrorRed else TextSecondary
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Solo las tareas son entregables; las evaluaciones son avisos
            if (esEvaluacion) {
                ChipHistorial(texto = "Aviso", color = TextSecondary)
            } else {
                ChipHistorial(
                    texto = "${item.calificados}/${item.totalAlumnos} registrados",
                    color = if (completo) StatusGreen else AccentOrange
                )
            }
            ChipHistorial(texto = "${item.lecturas} lecturas", color = EduconnectBlue)
        }
    }
}

@Composable
private fun ChipHistorial(texto: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.10f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = texto,
            fontFamily = Roboto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistorialPublicacionesPreview() {
    EduConnectAppTheme {
        HistorialPublicacionesScreen(
            publicaciones = listOf(
                PublicacionHistorialItem(1L, "Tarea", "Ejercicios Cap. 5 - Fracciones", "Matemáticas", "1er", "A", "2026-10-08", "2026-10-02", 28, 10, 20),
                PublicacionHistorialItem(2L, "Evaluación", "Práctica calificada 2", "Comunicación", "1er", "A", "2026-10-07", "2026-10-02", 28, 28, 25),
                PublicacionHistorialItem(3L, "Tarea", "Lectura Cap. 8", "Ciencias", "2do", "B", "2026-10-01", "2026-09-28", 30, 0, 5, "ANULADA")
            )
        )
    }
}
