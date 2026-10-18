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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.NaranjaMedia
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.RojoError
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite
import com.educonnectapp.ui.theme.VerdeOk

private val FondoCampoClave = Color(0xFFE6F1FB)

// Misma regla que el registro y Recuperar contraseña: 8 a 72 caracteres, con letras y números
private fun cumpleLongitud(p: String) = p.length in 8..72
private fun tieneLetra(p: String) = p.any { it.isLetter() }
private fun tieneNumero(p: String) = p.any { it.isDigit() }
private fun claveValida(p: String) = cumpleLongitud(p) && tieneLetra(p) && tieneNumero(p)

// 0 = vacía, 1 = débil, 2 = media, 3 = fuerte
private fun nivelSeguridad(p: String): Int {
    if (p.isEmpty()) return 0
    var puntos = 0
    if (p.length >= 8) puntos++
    if (p.length >= 12) puntos++
    if (tieneLetra(p) && tieneNumero(p)) puntos++
    if (p.any { it.isUpperCase() } && p.any { it.isLowerCase() }) puntos++
    if (p.any { !it.isLetterOrDigit() }) puntos++
    return when {
        !claveValida(p) || puntos <= 2 -> 1
        puntos == 3 -> 2
        else -> 3
    }
}

@Composable
fun CambiarContrasenaScreen(
    onBack: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onGuardar: (actual: String, nueva: String) -> Unit = { _, _ -> },
    // Estado (lo controla MainActivity)
    procesando: Boolean = false,
    exito: Boolean = false,
    alerta: AlertaDatos? = null,
    onCerrarExito: () -> Unit = {},      // cierra la sesión y va al Login
    onCerrarAlerta: () -> Unit = {},
    barraInferior: @Composable () -> Unit = {}
) {
    var actual by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    val coinciden = confirmar.isNotEmpty() && confirmar == nueva
    val distintaDeActual = nueva.isNotEmpty() && nueva != actual
    val formularioValido = actual.isNotEmpty() && claveValida(nueva) && coinciden && distintaDeActual

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
                IconButton(onClick = onBack, modifier = Modifier.size(35.dp).offset(x = (-5).dp)) {
                    Image(painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver", modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(text = "Cambiar contraseña", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextWhite)
                    Text(text = "Protege el acceso a tu cuenta", fontFamily = Roboto,
                        fontSize = 16.sp, color = TextWhite.copy(alpha = 0.85f))
                }
            }
            IconButton(onClick = onNotificaciones) {
                Image(painter = painterResource(id = R.drawable.notification_white),
                    contentDescription = "Notificaciones", modifier = Modifier.size(35.dp))
            }
        }

        // CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "CONTRASEÑA", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                fontSize = 16.sp, color = EduconnectBlue, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CampoClave(etiqueta = "Contraseña actual", valor = actual, onCambio = { actual = it })

                CampoClave(
                    etiqueta = "Nueva contraseña",
                    valor = nueva,
                    onCambio = { nueva = it },
                    error = if (nueva.isNotEmpty() && actual.isNotEmpty() && !distintaDeActual)
                        "Debe ser distinta a la contraseña actual" else null
                )
                IndicadorSeguridad(nivelSeguridad(nueva))

                // Requisitos con check verde al cumplirse
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Requisito("Mínimo 8 caracteres", cumpleLongitud(nueva))
                    Requisito("Al menos una letra", tieneLetra(nueva))
                    Requisito("Al menos un número", tieneNumero(nueva))
                }

                CampoClave(
                    etiqueta = "Confirmar nueva contraseña",
                    valor = confirmar,
                    onCambio = { confirmar = it },
                    error = if (confirmar.isNotEmpty() && !coinciden) "Las contraseñas no coinciden" else null,
                    ok = coinciden
                )
            }

            Text(
                text = "Al actualizarla se cerrará tu sesión y deberás ingresar con la nueva contraseña.",
                fontFamily = Roboto, fontSize = 14.sp, color = TextSecondary,
                lineHeight = 19.sp, modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { mostrarConfirmacion = true },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOrange,
                    disabledContainerColor = BorderLight
                )
            ) {
                Text(text = "Actualizar contraseña", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = if (formularioValido) TextWhite else TextSecondary)
            }

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundWhite)
            ) {
                Text(text = "Cancelar", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        barraInferior()
    }

    // ===================== POPUPS (PopupsEdu.kt) =====================
    if (mostrarConfirmacion) {
        PopupConfirmacionEdu(
            titulo = "¿Cambiar tu contraseña?",
            mensaje = "Se cerrará tu sesión y deberás ingresar con la nueva contraseña.",
            textoConfirmar = "Cambiar",
            onCancelar = { mostrarConfirmacion = false },
            onConfirmar = {
                mostrarConfirmacion = false
                onGuardar(actual, nueva)
            }
        )
    }

    if (procesando) PopupProcesandoEdu(titulo = "Actualizando contraseña…")

    if (exito) {
        PopupExitoEdu(
            titulo = "¡Contraseña actualizada!",
            mensaje = "Por seguridad se cerrará tu sesión. Ingresa con tu nueva contraseña.",
            onCerrar = onCerrarExito
        )
    }

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

// Campo de contraseña con ojo para mostrar/ocultar
@Composable
private fun CampoClave(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    error: String? = null,
    ok: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }
    Column {
        Text(text = etiqueta, fontFamily = Roboto, fontSize = 15.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = { if (it.length <= 72) onCambio(it) },
            singleLine = true,
            isError = error != null,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (ok) Icon(Icons.Filled.CheckCircle, contentDescription = null,
                        tint = VerdeOk, modifier = Modifier.size(20.dp))
                    IconButton(onClick = { visible = !visible }) {
                        Icon(
                            imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (visible) "Ocultar" else "Mostrar",
                            tint = TextSecondary
                        )
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EduconnectBlue,
                unfocusedBorderColor = if (ok) VerdeOk else BorderLight,
                errorBorderColor = RojoError,
                focusedTextColor = TextBlue,
                unfocusedTextColor = TextBlue,
                errorTextColor = TextBlue,
                focusedContainerColor = FondoCampoClave,
                unfocusedContainerColor = FondoCampoClave,
                errorContainerColor = FondoCampoClave
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(text = error, fontFamily = Roboto, fontSize = 13.sp, color = RojoError,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp))
        }
    }
}

// Barra de 3 segmentos: Débil (rojo), Media (naranja), Fuerte (verde)
@Composable
private fun IndicadorSeguridad(nivel: Int) {
    if (nivel == 0) return
    val (texto, color) = when (nivel) {
        1 -> "Débil" to RojoError
        2 -> "Media" to NaranjaMedia
        else -> "Fuerte" to VerdeOk
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(3) { i ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .background(if (i < nivel) color else BorderLight, RoundedCornerShape(3.dp))
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "Seguridad: $texto", fontFamily = Roboto, fontWeight = FontWeight.Bold,
            fontSize = 13.sp, color = color)
    }
}

@Composable
private fun Requisito(texto: String, cumple: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(
            imageVector = if (cumple) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (cumple) VerdeOk else TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Text(text = texto, fontFamily = Roboto, fontSize = 14.sp,
            color = if (cumple) VerdeOk else TextSecondary)
    }
}

@Preview(showBackground = true)
@Composable
fun CambiarContrasenaPreview() {
    EduConnectAppTheme { CambiarContrasenaScreen() }
}
