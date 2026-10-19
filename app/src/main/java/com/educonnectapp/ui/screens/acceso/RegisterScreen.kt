package com.educonnectapp.ui.screens.acceso

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.CampoTextoEdu
import com.educonnectapp.ui.components.ChipEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.PopupExitoEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAmarilloOscuro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduCampo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

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
    var rolSeleccionado by remember { mutableStateOf("Docente") }

    // Alerta de validación local (antes de llamar al backend)
    var alertaLocal by remember { mutableStateOf<Alerta?>(null) }

    // Validación al presionar "Crear cuenta". Devuelve la alerta a mostrar, o null si todo está bien.
    fun validar(): Alerta? = when {
        nombre.isBlank() || dni.isBlank() || telefono.isBlank() || email.isBlank() || password.isEmpty() -> Alerta(
            Icons.Rounded.Warning, EduAmarillo,
            "Faltan datos",
            "Completa todos los campos para crear tu cuenta."
        )
        dni.length != 8 -> Alerta(
            Icons.Rounded.Badge, EduAmarillo,
            "DNI incompleto",
            "El DNI debe tener 8 dígitos."
        )
        telefono.length != 9 -> Alerta(
            Icons.Rounded.Phone, EduAmarillo,
            "Teléfono incompleto",
            "El teléfono debe tener 9 dígitos."
        )
        !REGEX_CORREO.matches(email.trim()) -> Alerta(
            Icons.Rounded.Email, EduAmarillo,
            "Correo no válido",
            "Ingresa un correo electrónico válido, por ejemplo: nombre@correo.com"
        )
        password.length < 8 || !password.any { it.isLetter() } || !password.any { it.isDigit() } -> Alerta(
            Icons.Rounded.Key, EduCoral,
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
                Icons.Rounded.Email, EduCoral,
                "Correo ya registrado",
                "El correo \"${email.trim()}\" ya está registrado en EduConnect. Ingresa un correo diferente."
            )
            "DNI" -> Alerta(
                Icons.Rounded.Badge, EduCoral,
                "DNI ya registrado",
                "El DNI $dni ya está registrado en EduConnect. Verifica el número ingresado."
            )
            else -> Alerta(
                Icons.Rounded.Warning, EduCoral,
                "No se pudo crear la cuenta",
                it.mensaje.ifBlank { "Inténtalo nuevamente en unos minutos." }
            )
        }
    }

    // POPUP DE ALERTA (local o del servidor). PopupAlertaEdu está en RecuperarContrasenaScreen.kt
    (alertaLocal ?: alertaDelServidor)?.let { alerta ->
        PopupAlertaEdu(
            alerta.icono, alerta.color, alerta.titulo, alerta.mensaje,
            onCerrar = { if (alertaLocal != null) alertaLocal = null else onCerrarAlerta() }
        )
    }

    // POPUP CUENTA REGISTRADA: PopupExitoEdu se cierra solo a los 5 s y lleva al inicio de sesión
    if (registroExitoso) {
        PopupExitoEdu(
            titulo = "¡Cuenta registrada!",
            mensaje = "Tu cuenta de ${rolSeleccionado.lowercase()} fue creada correctamente. " +
                    "Ahora inicia sesión con tu correo y contraseña.",
            onCerrar = onIrALogin
        )
    }

    // Alto real de la cabecera: el contenido desplazable empieza 44 dp antes de su final,
    // así la tarjeta se monta sobre el color y la cabecera queda FIJA al hacer scroll.
    val densidad = LocalDensity.current
    var altoCabecera by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA (color del módulo: amarillo, con texto oscuro)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { altoCabecera = with(densidad) { it.height.toDp() } }
                .background(ColorModulo.Registro, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 62.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BotonIconoCabeceraEdu(
                    icono = Icons.AutoMirrored.Rounded.ArrowBack,
                    descripcion = "Volver",
                    onClick = onBack,
                    colorCabecera = ColorModulo.Registro
                )
                Column {
                    Text(
                        text = "Crear cuenta",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Black,
                        fontSize = 26.sp,
                        color = EduTexto
                    )
                    Text(
                        text = "Completa tus datos para empezar.",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = EduEtiqueta
                    )
                }
            }
        }

        // CONTENIDO DESPLAZABLE: solo esta parte se mueve al hacer scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = (altoCabecera - 44.dp).coerceAtLeast(0.dp))
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            // TARJETA BLANCA CON EL FORMULARIO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp, shape = RoundedCornerShape(28.dp),
                        ambientColor = EduAmarilloOscuro.copy(alpha = 0.25f), spotColor = EduAmarilloOscuro.copy(alpha = 0.25f)
                    )
                    .background(Color.White, RoundedCornerShape(28.dp))
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // SELECTOR DE ROL (la misma pantalla sirve para docente, padre y director)
                SelectorRol(rolSeleccionado = rolSeleccionado, onSeleccionar = { rolSeleccionado = it })

                // NOMBRE
                CampoTextoEdu(
                    valor = nombre,
                    onValorCambia = { nombre = it },
                    etiqueta = "Nombre completo",
                    placeholder = "Ingresa tu nombre completo",
                    icono = Icons.Rounded.Person
                )

                // DNI Y TELÉFONO: solo números (8 y 9 dígitos)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CampoTextoEdu(
                        valor = dni,
                        onValorCambia = { nuevo -> dni = nuevo.filter { it.isDigit() }.take(8) },
                        etiqueta = "DNI",
                        placeholder = "00000000",
                        icono = Icons.Rounded.Badge,
                        tipoTeclado = KeyboardType.NumberPassword,
                        modifier = Modifier.weight(1f)
                    )
                    CampoTextoEdu(
                        valor = telefono,
                        onValorCambia = { nuevo -> telefono = nuevo.filter { it.isDigit() }.take(9) },
                        etiqueta = "Teléfono",
                        placeholder = "999999999",
                        icono = Icons.Rounded.Phone,
                        tipoTeclado = KeyboardType.NumberPassword,
                        modifier = Modifier.weight(1f)
                    )
                }

                // CORREO
                CampoTextoEdu(
                    valor = email,
                    onValorCambia = { email = it.trim() },
                    etiqueta = "Correo electrónico",
                    placeholder = "nombre@correo.com",
                    icono = Icons.Rounded.Email,
                    tipoTeclado = KeyboardType.Email
                )

                // CONTRASEÑA + requisitos que se marcan al cumplirse
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CampoTextoEdu(
                        valor = password,
                        onValorCambia = { password = it },
                        etiqueta = "Contraseña",
                        placeholder = "Mínimo 8 caracteres",
                        icono = Icons.Rounded.Lock,
                        esContrasena = true,
                        accionTeclado = ImeAction.Done
                    )
                    RequisitosContrasenaEdu(password)
                }

                // AVISO SEGÚN ROL
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EduTealClaro, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = EduTealOscuro, modifier = Modifier.size(20.dp))
                    Text(
                        text = when (rolSeleccionado) {
                            "Docente" -> "Podrás asignar grado, sección y curso desde tu perfil."
                            "Padre" -> "Podrás asociar a tus hijos desde tu perfil."
                            else -> "Recibirás los avisos y las correcciones de asistencia de la institución."
                        },
                        fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp,
                        lineHeight = 18.sp, color = EduEtiqueta
                    )
                }

                // BOTÓN CREAR CUENTA: valida y, si todo está bien, envía al backend
                BotonPrincipalEdu(
                    texto = "Crear cuenta",
                    onClick = {
                        val alerta = validar()
                        if (alerta != null) alertaLocal = alerta
                        else onRegister(nombre.trim(), dni, telefono, email.trim(), password, rolSeleccionado)
                    },
                    tipo = TipoBotonEdu.GUARDAR,
                    icono = Icons.Rounded.PersonAdd,
                    cargando = registrando,
                    textoCargando = "Creando cuenta...",
                    habilitado = !registroExitoso
                )

                // ¿YA TIENES CUENTA? (dentro de la tarjeta para ahorrar espacio)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EduTextoSecundario
                    )
                    Text(
                        text = "Inicia sesión",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onBack)
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduTealOscuro
                    )
                }
            }
        }
    }
}

// Selector de rol con tres tarjetas: Docente / Padre / Director.
// Guarda "Docente", "Padre" o "Director" (el texto que se envía al backend).
private data class OpcionRol(val valor: String, val texto: String, val icono: ImageVector)

private val OPCIONES_ROL = listOf(
    OpcionRol("Docente", "Docente", Icons.Rounded.School),
    OpcionRol("Padre", "Padre", Icons.Rounded.FamilyRestroom),
    OpcionRol("Director", "Director", Icons.Rounded.AdminPanelSettings)
)

@Composable
private fun SelectorRol(rolSeleccionado: String, onSeleccionar: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "¿Quién eres?",
            fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = EduEtiqueta
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OPCIONES_ROL.forEach { opcion ->
                val activo = rolSeleccionado == opcion.valor
                val forma = RoundedCornerShape(14.dp)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(forma)
                        .background(if (activo) EduTealClaro else EduCampo)
                        .border(if (activo) 2.dp else 1.5.dp, if (activo) EduTeal else EduBorde, forma)
                        .clickable { onSeleccionar(opcion.valor) }
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
                ) {
                    Icon(
                        opcion.icono, contentDescription = null,
                        tint = if (activo) EduTeal else EduTextoSecundario,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = opcion.texto,
                        fontFamily = Nunito,
                        fontWeight = if (activo) FontWeight.Black else FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = if (activo) EduTealOscuro else EduTextoSecundario,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// Chips con los requisitos de la contraseña: se ponen en verde con check al cumplirse.
// Público para reutilizarlo en Recuperar y Cambiar contraseña.
@Composable
fun RequisitosContrasenaEdu(password: String, modifier: Modifier = Modifier) {
    val reglas = listOf(
        "8 caracteres" to (password.length >= 8),
        "Letras" to password.any { it.isLetter() },
        "Números" to password.any { it.isDigit() }
    )
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        reglas.forEach { (texto, cumple) ->
            ChipEdu(
                texto = texto,
                tono = if (cumple) TonoEdu.VERDE else TonoEdu.GRIS,
                icono = if (cumple) Icons.Rounded.Check else null
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun RegisterScreenPreview() {
    EduConnectAppTheme {
        RegisterScreen(
            onRegister = { _, _, _, _, _, _ -> },
            onBack = {}
        )
    }
}
