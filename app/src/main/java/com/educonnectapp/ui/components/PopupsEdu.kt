package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.educonnectapp.R
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite
import com.educonnectapp.ui.theme.VerdeOk
import com.educonnectapp.ui.theme.VerdeTexto
import kotlinx.coroutines.delay

// =====================================================================
// POPUPS COMUNES DE EDUCONNECT
// Flujo: confirmación -> procesando (círculo de carga) -> éxito (check verde, X, 5 s)
// Los usan: AgregarAsignacionScreen, MisAsignacionesScreen y EditarDatosScreen.
// Las alertas de error usan PopupAlertaEdu (RecuperarContrasenaScreen.kt).
// =====================================================================

// Segundos que permanece visible el popup de éxito antes de cerrarse solo
const val SEGUNDOS_POPUP_EXITO = 5

private val FondoAzulResumen = Color(0xFFE6F1FB)

// 1. Confirmación: título, mensaje, contenido opcional (resumen) y botones Cancelar / Confirmar
@Composable
fun PopupConfirmacionEdu(
    titulo: String,
    mensaje: String,
    textoConfirmar: String = "Confirmar",
    colorConfirmar: Color = AccentOrange,
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit,
    contenido: @Composable ColumnScope.() -> Unit = {}
) {
    Dialog(onDismissRequest = onCancelar) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = titulo, fontFamily = Roboto, fontWeight = FontWeight.Bold,
                fontSize = 21.sp, color = TextBlue, textAlign = TextAlign.Center)
            Text(text = mensaje, fontFamily = Roboto, fontSize = 15.sp, color = TextPrimary,
                textAlign = TextAlign.Center, lineHeight = 20.sp)
            contenido()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(23.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BackgroundLight)
                ) {
                    Text("Cancelar", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, color = TextSecondary)
                }
                Button(
                    onClick = onConfirmar,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(23.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorConfirmar)
                ) {
                    Text(textoConfirmar, fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, color = TextWhite)
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
                .background(BackgroundWhite, RoundedCornerShape(20.dp))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = EduconnectBlue,
                strokeWidth = 5.dp,
                modifier = Modifier.size(64.dp)
            )
            Text(text = titulo, fontFamily = Roboto, fontWeight = FontWeight.Bold,
                fontSize = 19.sp, color = TextBlue, textAlign = TextAlign.Center)
            Text(text = mensaje, fontFamily = Roboto, fontSize = 15.sp,
                color = TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

// 3. Éxito: check verde arriba, X en la esquina y cierre automático a los 5 segundos
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
                .background(BackgroundWhite, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = VerdeOk,
                    modifier = Modifier.size(84.dp)
                )
                Text(text = titulo, fontFamily = Roboto, fontWeight = FontWeight.Bold,
                    fontSize = 21.sp, color = VerdeTexto, textAlign = TextAlign.Center)
                if (!detalle.isNullOrBlank()) {
                    Text(text = detalle, fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, color = EduconnectBlue,
                        textAlign = TextAlign.Center, lineHeight = 21.sp)
                }
                if (mensaje.isNotBlank()) {
                    Text(text = mensaje, fontFamily = Roboto, fontSize = 15.sp,
                        color = TextPrimary, textAlign = TextAlign.Center, lineHeight = 20.sp)
                }
            }
            // X para cerrar
            IconButton(
                onClick = onCerrar,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(36.dp)
            ) {
                Box(
                    modifier = Modifier.size(28.dp).background(BackgroundLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar",
                        tint = TextSecondary, modifier = Modifier.size(18.dp))
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
    colorBorde: Color = EduconnectBlue
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoAzulResumen, RoundedCornerShape(12.dp))
            .border(1.dp, colorBorde, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp)
                .background(EduconnectBlue, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.teacher_white),
                contentDescription = null, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "$grado · Sec. $seccion", fontFamily = Roboto,
                fontWeight = FontWeight.Bold, fontSize = 17.sp, color = EduconnectBlue)
            Text(text = curso, fontFamily = Roboto, fontSize = 15.sp, color = TextBlue)
        }
    }
}
