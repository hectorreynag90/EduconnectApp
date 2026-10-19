package com.educonnectapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.educonnectapp.R
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import kotlinx.coroutines.delay

// =====================================================================
// POPUPS COMUNES DE EDUCONNECT (rediseño 2026)
// Flujo: confirmación -> procesando (círculo de carga) -> éxito (check turquesa, X, 5 s)
// Los usan: AgregarAsignacionScreen, MisAsignacionesScreen, EditarDatosScreen, etc.
// Las alertas de error usan PopupAlertaEdu (RecuperarContrasenaScreen.kt).
// =====================================================================

// Segundos que permanece visible el popup de éxito antes de cerrarse solo
const val SEGUNDOS_POPUP_EXITO = 5

private val FormaPopup = RoundedCornerShape(28.dp)

// 1. Confirmación: ícono opcional, título, mensaje, contenido opcional (resumen) y botones Cancelar / Confirmar
// colorConfirmar: usar el color del tipo de acción, p. ej. TipoBotonEdu.ELIMINAR.fondo para "Quitar"
@Composable
fun PopupConfirmacionEdu(
    titulo: String,
    mensaje: String,
    textoConfirmar: String = "Confirmar",
    colorConfirmar: Color = TipoBotonEdu.GUARDAR.fondo,
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit,
    icono: ImageVector? = null,
    contenido: @Composable ColumnScope.() -> Unit = {}
) {
    Dialog(onDismissRequest = onCancelar) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, FormaPopup)
                .padding(horizontal = 22.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icono != null) {
                Box(
                    modifier = Modifier.size(72.dp).background(colorConfirmar.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icono, contentDescription = null,
                        tint = if (colorConfirmar == EduAmarillo) EduTexto else colorConfirmar,
                        modifier = Modifier.size(34.dp))
                }
            }
            Text(text = titulo, fontFamily = Nunito, fontWeight = FontWeight.Black,
                fontSize = 21.sp, color = EduTexto, textAlign = TextAlign.Center)
            Text(text = mensaje, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                color = EduEtiqueta, textAlign = TextAlign.Center, lineHeight = 21.sp)
            contenido()
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduFondo),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        "Cancelar", fontFamily = Nunito, fontWeight = FontWeight.Black,
                        fontSize = 15.sp, color = EduEtiqueta,
                        textAlign = TextAlign.Center, maxLines = 2, lineHeight = 17.sp
                    )
                }
                Button(
                    onClick = onConfirmar,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorConfirmar),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        textoConfirmar, fontFamily = Nunito, fontWeight = FontWeight.Black,
                        fontSize = 15.sp, color = textoSobre(colorConfirmar),
                        textAlign = TextAlign.Center, maxLines = 2, lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

// 2. Procesando: círculo de carga (no se puede cerrar mientras el servidor responde)
@Composable
fun PopupProcesandoEdu(
    titulo: String,
    mensaje: String = "Espera un momento, por favor."
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, FormaPopup)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = EduTeal,
                trackColor = EduTealClaro,
                strokeWidth = 6.dp,
                modifier = Modifier.size(64.dp)
            )
            Text(text = titulo, fontFamily = Nunito, fontWeight = FontWeight.Black,
                fontSize = 19.sp, color = EduTexto, textAlign = TextAlign.Center)
            Text(text = mensaje, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                color = EduTextoSecundario, textAlign = TextAlign.Center)
        }
    }
}

// 3. Éxito: check turquesa arriba, X en la esquina y cierre automático a los 5 segundos
@Composable
fun PopupExitoEdu(
    titulo: String,
    detalle: String? = null,
    mensaje: String = "",
    onCerrar: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(SEGUNDOS_POPUP_EXITO * 1000L)
        onCerrar()
    }
    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, FormaPopup)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Check dentro de un círculo turquesa con halo claro
                Box(
                    modifier = Modifier.size(96.dp).background(EduTealClaro, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size(72.dp).background(EduTeal, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(42.dp))
                    }
                }
                Text(text = titulo, fontFamily = Nunito, fontWeight = FontWeight.Black,
                    fontSize = 21.sp, color = EduTealOscuro, textAlign = TextAlign.Center)
                if (!detalle.isNullOrBlank()) {
                    Text(text = detalle, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp, color = EduTexto,
                        textAlign = TextAlign.Center, lineHeight = 21.sp)
                }
                if (mensaje.isNotBlank()) {
                    Text(text = mensaje, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                        color = EduEtiqueta, textAlign = TextAlign.Center, lineHeight = 21.sp)
                }
                Text(text = "Se cierra solo en $SEGUNDOS_POPUP_EXITO s", fontFamily = Nunito,
                    fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario)
            }
            // X para cerrar
            IconButton(
                onClick = onCerrar,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(40.dp)
            ) {
                Box(
                    modifier = Modifier.size(34.dp).background(EduFondo, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Cerrar",
                        tint = EduTextoSecundario, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// Tarjeta con el resumen de una asignación (grado · sección y curso)
@Composable
fun ResumenAsignacionEdu(
    grado: String,
    seccion: String,
    curso: String,
    colorBorde: Color = EduTeal
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(EduTealClaro, RoundedCornerShape(16.dp))
            .border(1.5.dp, colorBorde, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(42.dp)
                .background(EduTeal, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.teacher_white),
                contentDescription = null, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "$grado · Sec. $seccion", fontFamily = Nunito,
                fontWeight = FontWeight.Black, fontSize = 17.sp, color = EduTealOscuro)
            Text(text = curso, fontFamily = Nunito, fontWeight = FontWeight.Bold,
                fontSize = 15.sp, color = EduTexto)
        }
    }
}
