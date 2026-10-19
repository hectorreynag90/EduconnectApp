package com.educonnectapp.ui.screens.docente.publicaciones

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.IconoCirculoEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.docente.comunicados.SeccionDestinatario
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduCampo
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// =====================================================================
// PIEZAS COMUNES DEL MÓDULO PUBLICACIONES (rediseño 2026)
// Campos con estilo "lleno" (fondo blanco y borde turquesa, igual que "Para"),
// hoja para elegir la clase y hoja "¿Qué quieres publicar?"
// =====================================================================

private val FormaCampo = RoundedCornerShape(16.dp)

// "1.2 MB" / "850 KB"
internal fun tamanoLegiblePub(bytes: Long?): String = when {
    bytes == null -> ""
    bytes >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
    else -> "${(bytes / 1024).coerceAtLeast(1)} KB"
}

// "2026-10-23" -> "Viernes 23 de octubre"
internal fun fechaLargaPub(fecha: String): String = try {
    val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha.take(10))
    SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "PE")).format(d!!).replaceFirstChar { it.uppercase() }
} catch (e: Exception) { fecha }

// "2026-10-23" -> "vie 23 oct"
internal fun fechaCortaPub(fecha: String): String = try {
    val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha.take(10))
    SimpleDateFormat("EEE d MMM", Locale("es", "PE")).format(d!!).replace(".", "")
} catch (e: Exception) { fecha }

internal fun hoyPub(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(java.util.Date())

// Etiqueta de un campo
@Composable
internal fun EtiquetaCampoPub(texto: String) {
    Text(texto, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = EduEtiqueta)
}

// Campo de texto: vacío = gris claro con borde gris; con texto = fondo blanco y borde turquesa (como "Para")
@Composable
internal fun CampoTextoPub(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    placeholder: String,
    lineas: Int = 1,
    ayuda: String? = null
) {
    val lleno = valor.isNotBlank()
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EtiquetaCampoPub(etiqueta)
        BasicTextField(
            value = valor,
            onValueChange = onValorCambia,
            singleLine = lineas == 1,
            minLines = lineas,
            textStyle = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EduTexto, lineHeight = 22.sp),
            cursorBrush = SolidColor(EduTeal),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = if (lineas == 1) 54.dp else (22 * lineas + 28).dp)
                        .clip(FormaCampo)
                        .background(if (lleno) Color.White else EduCampo)
                        .border(if (lleno) 2.dp else 1.5.dp, if (lleno) EduTeal else EduBorde, FormaCampo)
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    contentAlignment = if (lineas == 1) Alignment.CenterStart else Alignment.TopStart
                ) {
                    if (valor.isEmpty()) {
                        Text(
                            placeholder, fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp, color = EduTextoSecundario
                        )
                    }
                    inner()
                }
            }
        )
        if (!ayuda.isNullOrBlank()) {
            Text(ayuda, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario)
        }
    }
}

// Campo de fecha (abre el calendario desde hoy). fecha = "yyyy-MM-dd" o "".
@Composable
internal fun CampoFechaPub(etiqueta: String, fecha: String, onFecha: (String) -> Unit) {
    val context = LocalContext.current
    val lleno = fecha.isNotBlank()
    fun abrir() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, anio, mes, dia -> onFecha(String.format(Locale.US, "%04d-%02d-%02d", anio, mes + 1, dia)) },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).apply { datePicker.minDate = System.currentTimeMillis() - 1000 }.show()
    }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EtiquetaCampoPub(etiqueta)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(FormaCampo)
                .background(if (lleno) Color.White else EduCampo)
                .border(if (lleno) 2.dp else 1.5.dp, if (lleno) EduTeal else EduBorde, FormaCampo)
                .clickable { abrir() }
                .padding(horizontal = 14.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Rounded.CalendarMonth, contentDescription = null,
                tint = if (lleno) EduTeal else EduTextoSecundario, modifier = Modifier.size(20.dp)
            )
            Text(
                if (lleno) fechaLargaPub(fecha) else "Seleccionar fecha",
                modifier = Modifier.weight(1f), fontFamily = Nunito,
                fontWeight = if (lleno) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 15.5.sp, color = if (lleno) EduTexto else EduTextoSecundario
            )
            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(22.dp))
        }
    }
}

// Campo "Para": clase elegida y cuántos alumnos; abre la hoja de clases
@Composable
internal fun CampoParaPub(destino: SeccionDestinatario?, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EtiquetaCampoPub("Para")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(FormaCampo)
                .background(if (destino != null) Color.White else EduCampo)
                .border(if (destino != null) 2.dp else 1.5.dp, if (destino != null) EduTeal else EduBorde, FormaCampo)
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = if (destino != null) 10.dp else 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (destino != null) {
                IconoCirculoEdu(Icons.Rounded.Groups, TonoEdu.VERDE, tamano = 36.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${destino.gradoNombre} ${destino.seccionNombre} · ${destino.cursoNombre}",
                        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTexto
                    )
                    Text(
                        "${destino.totalPadres} alumnos · se avisa a sus padres", fontFamily = Nunito,
                        fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario
                    )
                }
            } else {
                Icon(Icons.Rounded.Groups, contentDescription = null, tint = EduTextoSecundario, modifier = Modifier.size(22.dp))
                Text(
                    "Elige la clase", modifier = Modifier.weight(1f), fontFamily = Nunito,
                    fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = EduTextoSecundario
                )
            }
            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "Elegir clase", tint = EduTextoSecundario, modifier = Modifier.size(24.dp))
        }
    }
}

// Hoja inferior para elegir la clase (agrupada por grado)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HojaElegirClasePub(
    clases: List<SeccionDestinatario>,
    seleccionada: SeccionDestinatario?,
    onElegir: (SeccionDestinatario) -> Unit,
    onCerrar: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconoCirculoEdu(Icons.Rounded.Groups, TonoEdu.VERDE)
                Column {
                    Text("¿Para qué clase?", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 20.sp, color = EduTexto)
                    Text(
                        "Se avisa a los padres de sus alumnos", fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp, color = EduTextoSecundario
                    )
                }
            }
            if (clases.isEmpty()) {
                Text(
                    "Aún no tienes clases asignadas. Agrégalas desde tu perfil.",
                    fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = EduTextoSecundario
                )
            }
            clases
                .sortedWith(compareBy({ it.gradoId }, { it.cursoNombre }, { it.seccionNombre }))
                .groupBy { it.gradoNombre }
                .forEach { (grado, lista) ->
                    Text(
                        grado.uppercase(), modifier = Modifier.padding(start = 2.dp, top = 4.dp),
                        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 12.5.sp,
                        letterSpacing = 0.6.sp, color = EduTextoSecundario.copy(alpha = 0.8f)
                    )
                    lista.forEach { dest ->
                        val activa = dest.seccionId == seleccionada?.seccionId && dest.cursoId == seleccionada?.cursoId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(FormaCampo)
                                .background(if (activa) EduTealClaro else Color.White)
                                .border(if (activa) 2.dp else 1.5.dp, if (activa) EduTeal else EduBorde, FormaCampo)
                                .clickable { onElegir(dest) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(22.dp).border(2.dp, if (activa) EduTeal else EduBorde, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (activa) Box(modifier = Modifier.size(11.dp).background(EduTeal, CircleShape))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(dest.cursoNombre, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTexto)
                                Text(
                                    "Sección ${dest.seccionNombre} · ${dest.totalPadres} alumnos", fontFamily = Nunito,
                                    fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario
                                )
                            }
                        }
                    }
                }
        }
    }
}

// Hoja "¿Qué quieres publicar?": Tarea o Aviso de evaluación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HojaCrearPublicacion(onTarea: () -> Unit, onEvaluacion: () -> Unit, onCerrar: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("¿Qué quieres publicar?", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 20.sp, color = EduTexto)
            OpcionCrearPub(
                Icons.AutoMirrored.Rounded.Assignment, TonoEdu.AZUL, "Tarea",
                "Con fecha límite y adjunto. Registras quién entregó."
            ) { onCerrar(); onTarea() }
            OpcionCrearPub(
                Icons.Rounded.Quiz, TonoEdu.AMARILLO, "Aviso de evaluación",
                "Fecha y temario para que los padres preparen al alumno."
            ) { onCerrar(); onEvaluacion() }
        }
    }
}

@Composable
private fun OpcionCrearPub(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    tono: TonoEdu,
    titulo: String,
    detalle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, EduBorde, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        IconoCirculoEdu(icono, tono, tamano = 50.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.5.sp, color = EduTexto)
            Text(detalle, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = EduTextoSecundario)
        }
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null,
            tint = EduTextoSecundario, modifier = Modifier.size(24.dp)
        )
    }
}
