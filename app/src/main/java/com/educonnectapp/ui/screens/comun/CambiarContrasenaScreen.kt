package com.educonnectapp.ui.screens.comun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupExitoEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAmarilloClaro
import com.educonnectapp.ui.theme.EduAmarilloOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduCampo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduCoralClaro
import com.educonnectapp.ui.theme.EduCoralOscuro
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

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

// =====================================================================
// CAMBIAR CONTRASEÑA (rediseño 2026) · lo usan docente y padre
// Cabecera fija azul oscuro · 3 campos con ojo · barra de seguridad · requisitos · Cancelar / Actualizar
// =====================================================================

@OptIn(ExperimentalLayoutApi::class)
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
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Cambiar contraseña",
            subtitulo = "Protege el acceso a tu cuenta",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoClaveEdu(etiqueta = "Contraseña actual", valor = actual, onCambio = { actual = it })

            CampoClaveEdu(
                etiqueta = "Nueva contraseña",
                valor = nueva,
                onCambio = { nueva = it },
                error = if (nueva.isNotEmpty() && actual.isNotEmpty() && !distintaDeActual)
                    "Debe ser distinta a la contraseña actual" else null
            )
            IndicadorSeguridad(nivelSeguridad(nueva))

            // Requisitos: se marcan en turquesa al cumplirse
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Requisito("8+ caracteres", cumpleLongitud(nueva))
                Requisito("Una letra", tieneLetra(nueva))
                Requisito("Un número", tieneNumero(nueva))
            }

            CampoClaveEdu(
                etiqueta = "Confirmar nueva contraseña",
                valor = confirmar,
                onCambio = { confirmar = it },
                error = if (confirmar.isNotEmpty() && !coinciden) "Las contraseñas no coinciden" else null,
                ok = coinciden
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EduAmarilloClaro, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = EduAmarilloOscuro, modifier = Modifier.size(22.dp))
                Text(
                    "Al actualizarla se cerrará tu sesión y deberás ingresar con la nueva contraseña.",
                    modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp, lineHeight = 18.sp, color = EduAmarilloOscuro
                )
            }
        }

        // BOTONES FIJOS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduFondo)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BotonSecundarioEdu(texto = "Cancelar", modifier = Modifier.weight(1f), onClick = onBack)
            BotonPrincipalEdu(
                texto = "Actualizar",
                tipo = TipoBotonEdu.ACTUALIZAR,
                icono = Icons.Rounded.Sync,
                modifier = Modifier.weight(1f),
                habilitado = formularioValido && !procesando,
                onClick = { mostrarConfirmacion = true }
            )
        }

        barraInferior()
    }

    // ===================== POPUPS =====================
    if (mostrarConfirmacion) {
        PopupConfirmacionEdu(
            titulo = "¿Cambiar tu contraseña?",
            mensaje = "Se cerrará tu sesión y deberás ingresar con la nueva contraseña.",
            textoConfirmar = "Cambiar",
            colorConfirmar = TipoBotonEdu.ACTUALIZAR.fondo,
            icono = Icons.Rounded.Key,
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
            icono = Icons.Rounded.Warning,
            color = EduAmarillo,
            titulo = it.titulo,
            mensaje = it.mensaje,
            onCerrar = onCerrarAlerta
        )
    }
}

// Campo de contraseña: vacío gris; con texto blanco y borde turquesa; error granate; ojo para mostrar
@Composable
private fun CampoClaveEdu(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    error: String? = null,
    ok: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }
    val lleno = valor.isNotEmpty()
    val forma = RoundedCornerShape(16.dp)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(etiqueta, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = EduEtiqueta)
        BasicTextField(
            value = valor,
            onValueChange = { if (it.length <= 72) onCambio(it) },
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EduTexto),
            cursorBrush = SolidColor(EduTeal),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 54.dp)
                        .clip(forma)
                        .background(
                            when {
                                error != null -> EduCoralClaro
                                lleno -> Color.White
                                else -> EduCampo
                            }
                        )
                        .border(
                            if (lleno) 2.dp else 1.5.dp,
                            when {
                                error != null -> EduCoralOscuro
                                lleno -> EduTeal
                                else -> EduBorde
                            },
                            forma
                        )
                        .padding(start = 14.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f).padding(vertical = 14.dp)) { inner() }
                    if (ok) Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = EduTeal, modifier = Modifier.size(20.dp))
                    IconButton(onClick = { visible = !visible }) {
                        Icon(
                            if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = if (visible) "Ocultar" else "Mostrar",
                            tint = EduTextoSecundario
                        )
                    }
                }
            }
        )
        if (error != null) {
            Text(error, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduCoralOscuro)
        }
    }
}

// Barra de 3 segmentos: Débil (granate), Media (amarillo), Fuerte (turquesa)
@Composable
private fun IndicadorSeguridad(nivel: Int) {
    if (nivel == 0) return
    val (texto, color, colorTexto) = when (nivel) {
        1 -> Triple("Débil", EduCoral, EduCoralOscuro)
        2 -> Triple("Media", EduAmarillo, EduAmarilloOscuro)
        else -> Triple("Fuerte", EduTeal, EduTealOscuro)
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(3) { i ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(7.dp)
                    .background(if (i < nivel) color else EduAzulOscuroClaro, RoundedCornerShape(50))
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(texto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.sp, color = colorTexto)
    }
}

@Composable
private fun Requisito(texto: String, cumple: Boolean) {
    ChipEdu(texto, if (cumple) TonoEdu.VERDE else TonoEdu.GRIS, icono = if (cumple) Icons.Rounded.Check else null)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun CambiarContrasenaPreview() {
    EduConnectAppTheme { CambiarContrasenaScreen() }
}
