package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import com.educonnectapp.R
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.BorderMedium
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.EduconnectBlueMedium
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite

// Colores de los popups
private val VerdeExito      = Color(0xFF1B7F3B)   // check verde
private val VerdeOscuro     = Color(0xFF1B5E20)   // texto "Cuenta registrada"
private val RojoAlerta      = Color(0xFFE53935)
private val NaranjaAlerta   = Color(0xFFF59E0B)

// Tiempo que se muestra el popup de cuenta registrada antes de ir al inicio de sesión
private const val SEGUNDOS_EXITO = 5

// Alerta que devuelve el servidor al registrar (la arma MainActivity con la respuesta del backend)
// tipo: "CORREO" (correo ya registrado) | "DNI" (DNI ya registrado) | "ERROR" (cualquier otro)
data class AlertaRegistro(val tipo: String, val mensaje: String = "")

// Alerta que se muestra en el popup (validación local o del servidor)
private data class Alerta(val icono: ImageVector, val color: Color, val titulo: String, val mensaje: String)

private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

@Composable
fun RegisterScreen(
    onRegister: (nombre: String, dni: String, telefono: String, email: String, password: String, rol: String) -> Unit,
    onBack: () -> Unit,
    registrando: Boolean = false,                 // true mientras el backend crea la cuenta
    alertaServidor: AlertaRegistro? = null,       // correo/DNI repetido u otro error del backend
    registroExitoso: Boolean = false,             // true cuando el backend creó la cuenta
    onCerrarAlerta: () -> Unit = {},
    onIrALogin: () -> Unit = {}                   // se llama al cerrar el popup de cuenta registrada
) {
    var nombre by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf("Docente") }

    // Alerta de validación local (antes de llamar al backend)
    var alertaLocal by remember { mutableStateOf<Alerta?>(null) }

    // Validación al presionar "Crear Cuenta". Devuelve la alerta a mostrar, o null si todo está bien.
    fun validar(): Alerta? = when {
        nombre.isBlank() || dni.isBlank() || telefono.isBlank() || email.isBlank() || password.isEmpty() -> Alerta(
            Icons.Filled.Warning, NaranjaAlerta,
            "Faltan datos",
            "Completa todos los campos para crear tu cuenta."
        )
        dni.length != 8 -> Alerta(
            Icons.Filled.Badge, NaranjaAlerta,
            "DNI incompleto",
            "El DNI debe tener 8 dígitos."
        )
        telefono.length != 9 -> Alerta(
            Icons.Filled.Phone, NaranjaAlerta,
            "Teléfono incompleto",
            "El teléfono debe tener 9 dígitos."
        )
        !REGEX_CORREO.matches(email.trim()) -> Alerta(
            Icons.Filled.Email, NaranjaAlerta,
            "Correo no válido",
            "Ingresa un correo electrónico válido, por ejemplo: nombre@correo.com"
        )
        password.length < 8 || !password.any { it.isLetter() } || !password.any { it.isDigit() } -> Alerta(
            Icons.Filled.Key, RojoAlerta,
            "Contraseña no válida",
            "La contraseña ingresada no cumple con los requisitos mínimos. " +
                    "Ingresa una contraseña de 8 caracteres o más, con letras y números."
        )
        else -> null
    }

    // Alerta del servidor -> popup
    val alertaDelServidor = alertaServidor?.let {
        when (it.tipo) {
            "CORREO" -> Alerta(
                Icons.Filled.Email, RojoAlerta,
                "Correo ya registrado",
                "El correo \"${email.trim()}\" ya está registrado en EduConnect. Ingresa un correo diferente."
            )
            "DNI" -> Alerta(
                Icons.Filled.Badge, RojoAlerta,
                "DNI ya registrado",
                "El DNI $dni ya está registrado en EduConnect. Verifica el número ingresado."
            )
            else -> Alerta(
                Icons.Filled.Warning, RojoAlerta,
                "No se pudo crear la cuenta",
                it.mensaje.ifBlank { "Inténtalo nuevamente en unos minutos." }
            )
        }
    }

    // POPUP DE ALERTA (local o del servidor)
    (alertaLocal ?: alertaDelServidor)?.let { alerta ->
        PopupAlertaRegistro(
            alerta = alerta,
            onCerrar = {
                if (alertaLocal != null) alertaLocal = null else onCerrarAlerta()
            }
        )
    }

    // POPUP CUENTA REGISTRADA: se cierra solo a los 5 segundos y lleva al inicio de sesión
    if (registroExitoso) {
        LaunchedEffect(Unit) {
            delay(SEGUNDOS_EXITO * 1000L)
            onIrALogin()
        }
        PopupCuentaRegistrada(rol = rolSeleccionado, onCerrar = onIrALogin)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // HEADER AZUL
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduconnectBlue)
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(start = 2.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically)
            {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(35.dp).offset(x = (10).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Box(modifier = Modifier.size(60.dp).background(
                color = EduconnectBlueMedium, shape = RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Filled.PersonAdd, contentDescription = null,
                    tint = TextWhite, modifier = Modifier.size(45.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Crear cuenta",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextWhite)

            Text(text = "$rolSeleccionado - EduConnect",
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = TextWhite)

            Spacer(modifier = Modifier.height(6.dp))
        }

        // BODY BLANCO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite)
                .padding(horizontal = 20.dp,
                    vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // SELECTOR ROL
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { rolSeleccionado = "Docente" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (rolSeleccionado == "Docente") EduconnectBlue else BorderLight
                    )
                ) {
                    Text(text = "Docente",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = if (rolSeleccionado == "Docente") TextWhite else TextSecondary)
                }
                Button(
                    onClick = { rolSeleccionado = "Padre" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (rolSeleccionado == "Padre") EduconnectBlue else BorderLight
                    )
                ) {
                    Text(text = "Padre",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = if (rolSeleccionado == "Padre") TextWhite else TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // NOMBRE
            Text(text = "NOMBRE COMPLETO",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextBlue,
                letterSpacing = 1.sp
            )
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                placeholder = {
                    Text(
                        text = "Ingrese nombre completo",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = Roboto
                    ) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.user_circle_darkgray),
                        contentDescription = null, modifier = Modifier.size(25.dp)
                    )},
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderMedium,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = EduconnectBlue),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                singleLine = true
            )

            //DNI Y TELÉFONO
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                //DNI: solo números, máximo 8
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "DNI",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextBlue,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = dni,
                        onValueChange = { nuevo -> dni = nuevo.filter { it.isDigit() }.take(8) },
                        placeholder = {
                            Text(
                                text = "00000000",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontFamily = Roboto
                            )},
                        leadingIcon = {
                            Image(painter = painterResource(id = R.drawable.dni_darkgray),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(10.dp),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EduconnectBlue,
                            unfocusedBorderColor = BorderMedium,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = EduconnectBlue),
                        modifier = Modifier.fillMaxWidth().height(50.dp), singleLine = true
                    )
                }

                // TELÉFONO: solo números, máximo 9
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TELÉFONO",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextBlue,
                        letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp)
                    )
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { nuevo -> telefono = nuevo.filter { it.isDigit() }.take(9) },
                        placeholder = {
                            Text(
                                text = "999999999",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontFamily = Roboto)},
                        leadingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.phone_darkgray),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EduconnectBlue,
                            unfocusedBorderColor = BorderMedium,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = EduconnectBlue),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        singleLine = true
                    )
                }
            }

            // CORREO
            Text(
                text = "CORREO ELECTRÓNICO",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextBlue,
                letterSpacing = 1.sp)
            OutlinedTextField(
                value = email, onValueChange = { email = it.trim() },
                placeholder = {
                    Text(
                        text = "Ingrese correo",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = Roboto
                    ) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.mail_darkgray),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderMedium,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = EduconnectBlue),
                modifier = Modifier.fillMaxWidth().height(50.dp), singleLine = true
            )

            // CONTRASEÑA
            Text(
                text = "CONTRASEÑA",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextBlue,
                letterSpacing = 1.sp)
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                placeholder = {
                    Text(
                        text = "Mínimo 8 caracteres",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = Roboto
                    )},
                leadingIcon = {
                    Image(painter = painterResource(id = R.drawable.closed_darkgray),
                        contentDescription = null, modifier = Modifier.size(25.dp)) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Image(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.eye_darkgray
                                else R.drawable.eyeoff_darkgray),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderMedium,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = EduconnectBlue),
                modifier = Modifier.fillMaxWidth().height(50.dp), singleLine = true
            )

            // AVISO SEGÚN ROL
            Row(modifier = Modifier.fillMaxWidth()
                .background(color = BackgroundLight, shape = RoundedCornerShape(10.dp))
                .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(id = R.drawable.alerta_darkgray),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (rolSeleccionado == "Docente")
                        "Podrás asignar grado, sección y curso desde tu perfil."
                    else
                        "Podrás asociar a tus hijos desde tu perfil.",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    lineHeight = 15.sp
                )
            }

            // BOTÓN CREAR CUENTA: valida y, si todo está bien, envía al backend
            Button(
                onClick = {
                    val alerta = validar()
                    if (alerta != null) alertaLocal = alerta
                    else onRegister(nombre.trim(), dni, telefono, email.trim(), password, rolSeleccionado)
                },
                enabled = !registrando && !registroExitoso,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOrange,
                    disabledContainerColor = AccentOrange.copy(alpha = 0.6f)
                )
            ) {
                if (registrando) {
                    CircularProgressIndicator(
                        color = TextWhite,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "Creando cuenta...",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = TextWhite)
                } else {
                    Icon(
                        imageVector = Icons.Filled.PersonAdd, contentDescription = null,
                        tint = TextWhite, modifier = Modifier.size(28.dp))
                    Spacer(
                        modifier = Modifier.size(8.dp))
                    Text(
                        text = "Crear Cuenta",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = TextWhite)
                }
            }

            // ¿YA TIENES CUENTA?
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    fontFamily = Roboto,
                    fontSize = 15.sp,
                    color = TextSecondary
                )
                TextButton(onClick = onBack,
                    contentPadding = PaddingValues(0.dp)) {
                    Text(
                        text = "Inicia sesión",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = TextBlue
                    )
                }
            }
        }
    }
}

// Popup de alerta: ícono en círculo de color, título, mensaje y botón "Entendido"
@Composable
private fun PopupAlertaRegistro(alerta: Alerta, onCerrar: () -> Unit) {
    Dialog(onDismissRequest = onCerrar) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite, RoundedCornerShape(20.dp))
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(alerta.color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = alerta.icono,
                    contentDescription = null,
                    tint = alerta.color,
                    modifier = Modifier.size(38.dp)
                )
            }
            Text(
                text = alerta.titulo,
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextBlue,
                textAlign = TextAlign.Center
            )
            Text(
                text = alerta.mensaje,
                fontFamily = Roboto,
                fontSize = 15.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onCerrar,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(23.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EduconnectBlue)
            ) {
                Text(
                    text = "Entendido",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextWhite
                )
            }
        }
    }
}

// Popup de éxito: check verde arriba y "¡Cuenta registrada!" en verde oscuro. Se cierra solo.
@Composable
private fun PopupCuentaRegistrada(rol: String, onCerrar: () -> Unit) {
    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite, RoundedCornerShape(20.dp))
                .clickable { onCerrar() }
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = VerdeExito,
                modifier = Modifier.size(84.dp)
            )
            Text(
                text = "¡Cuenta registrada!",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = VerdeOscuro,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tu cuenta de ${rol.lowercase()} fue creada correctamente. " +
                        "Ahora inicia sesión con tu correo y contraseña.",
                fontFamily = Roboto,
                fontSize = 15.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    EduConnectAppTheme {
        RegisterScreen(
            onRegister = { _, _, _, _, _, _ -> },
            onBack = {}
        )
    }
}
