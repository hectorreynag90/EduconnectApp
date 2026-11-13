package com.educonnectapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.Nunito

// Botón "Historial" para la cabecera de los módulos del docente (Asistencia, Comunicados, Publicaciones).
// Pastilla blanca con el texto en el color de la cabecera (oscuro sobre amarillo).
@Composable
fun BotonHistorialEdu(
    onClick: () -> Unit,
    colorCabecera: Color,
    modifier: Modifier = Modifier
) {
    val colorTexto = if (colorCabecera == EduAmarillo) EduTexto else colorCabecera
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(start = 12.dp, end = 14.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(Icons.Rounded.History, contentDescription = null, tint = colorTexto, modifier = Modifier.size(18.dp))
        Text(
            "Historial", fontFamily = Nunito, fontWeight = FontWeight.Black,
            fontSize = 14.sp, color = colorTexto, maxLines = 1
        )
    }
}
