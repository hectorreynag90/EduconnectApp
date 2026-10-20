package com.educonnectapp.ui.screens.acceso

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.CampoTextoEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// Error del login. tipo: "CORREO" (no registrado) | "CONTRASENA" (incorrecta) | "VACIOS" | "OTRO".
// Clase normal (no data class): cada intento fallido es un error nuevo y vuelve a mostrar el popup.
class ErrorLogin(val tipo: String, val mensaje: String = "")

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit,
    onGoToRegister: () -> Unit,
    isLoading: Boolean = false,
    errorLogin: ErrorLogin? = null,          // error del último intento (lo arma MainActivity)
    onOlvidePassword: () -> Unit = {},       // "¿Olvidaste tu contraseña?"
    onVolver: (() -> Unit)? = null           // opcional: flecha para volver a la bienvenida
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Campos en rojo y popup del error. Se quita el rojo al escribir en el campo.
    var correoEnRojo by remember { mutableStateOf(false) }
    var passwordEnRojo by remember { mutableStateOf(false) }
    var errorPopup by remember { mutableStateOf<ErrorLogin?>(null) }

    fun mostrarError(error: ErrorLogin) {
        correoEnRojo = error.tipo == "CORREO" || (error.tipo == "VACIOS" && email.isBlank())
        passwordEnRojo = error.tipo == "CONTRASENA" || (error.tipo == "VACIOS" && password.isBlank())
        errorPopup = error
    }
    LaunchedEffect(errorLogin) { errorLogin?.let { mostrarError(it) } }

    // POPUP DE ERROR (PopupAlertaEdu está en RecuperarContrasenaScreen.kt, mismo paquete)
    errorPopup?.let { error ->
        val cerrar = { errorPopup = null }
        when (error.tipo) {
            "CORREO" -> PopupAlertaEdu(
                Icons.Rounded.Email, EduCoral, "Correo incorrecto",
                error.mensaje.ifBlank { "El correo ingresado no está registrado en EduConnect. Verifica que esté bien escrito." },
                onCerrar = cerrar
            )
            "CONTRASENA" -> PopupAlertaEdu(
                Icons.Rounded.Key, EduCoral, "Contraseña incorrecta",
                "La contraseña no es correcta. Inténtalo nuevamente o usa \"¿Olvidaste tu contraseña?\".",
                onCerrar = cerrar
            )
            "VACIOS" -> PopupAlertaEdu(
                Icons.Rounded.Warning, EduAmarillo, "Faltan datos",
                "Ingresa tu correo y contraseña para iniciar sesión.",
                onCerrar = cerrar
            )
            else -> PopupAlertaEdu(
                Icons.Rounded.Warning, EduAmarillo, "No se pudo iniciar sesión",
                error.mensaje.ifBlank { "Inténtalo nuevamente en unos minutos." },
                onCerrar = cerrar
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        // CABECERA (color del módulo: azul oscuro)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorModulo.Login, RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 70.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (onVolver != null) {
                BotonIconoCabeceraEdu(
                    icono = Icons.AutoMirrored.Rounded.ArrowBack,
                    descripcion = "Volver",
                    onClick = onVolver,
                    colorCabecera = ColorModulo.Login
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_educonnect),
                        contentDescription = "Logo EduConnect",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
            Text(
                text = "¡Bienvenido!",
                modifier = Modifier.padding(top = 6.dp),
                fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 30.sp, color = Color.White
            )
            Text(
                text = "Ingresa con tu correo y contraseña.",
                fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(30.dp))

            //ChipCabeceraEdu("")
        }

        // TARJETA BLANCA CON EL FORMULARIO (sube 48 dp sobre la cabecera)
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .offset(y = (-48).dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp, shape = RoundedCornerShape(28.dp),
                    ambientColor = ColorModulo.Login.copy(alpha = 0.25f), spotColor = ColorModulo.Login.copy(alpha = 0.25f)
                )
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CampoTextoEdu(
                valor = email,
                onValorCambia = { nuevo -> email = nuevo.trim(); correoEnRojo = false },
                etiqueta = "Correo electrónico",
                placeholder = "Ingresa tu correo",
                icono = Icons.Rounded.Email,
                tipoTeclado = KeyboardType.Email,
                enError = correoEnRojo
            )
            CampoTextoEdu(
                valor = password,
                onValorCambia = { nuevo -> password = nuevo; passwordEnRojo = false },
                etiqueta = "Contraseña",
                placeholder = "Ingresa tu contraseña",
                icono = Icons.Rounded.Lock,
                esContrasena = true,
                accionTeclado = ImeAction.Done,
                enError = passwordEnRojo
            )

            // ¿OLVIDASTE TU CONTRASEÑA?
            Text(
                text = "¿Olvidaste tu contraseña?",
                modifier = Modifier
                    .align(Alignment.End)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onOlvidePassword)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.5.sp, color = EduTealOscuro
            )

            // BOTÓN INGRESAR
            BotonPrincipalEdu(
                texto = "Ingresar",
                onClick = {
                    if (email.isBlank() || password.isBlank()) mostrarError(ErrorLogin("VACIOS"))
                    else onLogin(email, password)
                },
                tipo = TipoBotonEdu.AVANZAR,
                icono = Icons.AutoMirrored.Rounded.Login,
                cargando = isLoading,
                textoCargando = "Ingresando"
            )
        }

        // ¿NO TIENES CUENTA? REGÍSTRATE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes cuenta? ",
                fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EduTextoSecundario
            )
            Text(
                text = "Regístrate",
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onGoToRegister)
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTealOscuro
            )
        }

        Text(
            text = "Al ingresar aceptas nuestros Términos de uso y Política de privacidad",
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-20).dp)
                .padding(horizontal = 36.dp)
                .navigationBarsPadding(),
            fontFamily = Nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 17.sp,
            color = EduTextoSecundario,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun LoginScreenReview() {
    EduConnectAppTheme {
        LoginScreen(
            onLogin = { _, _ -> },
            onGoToRegister = {}
        )
    }
}
