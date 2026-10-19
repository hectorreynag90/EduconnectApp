package com.educonnectapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.educonnectapp.ui.theme.*
import kotlinx.coroutines.delay

// Colores: theme/Color.kt (sección "Recuperar contraseña")

private val REGEX_CORREO_REC = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

// ============================== MODELOS ==============================

// Alerta del servidor que se muestra como popup.
// tipo: "CORREO_NO_REGISTRADO" | "CODIGO_VENCIDO" | "SOLICITUD_VENCIDA" | "CONTRASENA" | "ERROR"
class AlertaRecuperacion(val tipo: String, val mensaje: String = "")

// ============================== POPUP COMÚN ==============================

// Popup de alerta de EduConnect: ícono en círculo de color, título, mensaje y botón "Entendido".
// También lo usa LoginScreen.
@Composable
fun PopupAlertaEdu(
    icono: ImageVector,
    color: Color,
    titulo: String,
    mensaje: String,
    textoBoton: String = "Entendido",
    onCerrar: () -> Unit
) {
    Dialog(onDismissRequest = onCerrar) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(68.dp).background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(38.dp))
            }
            Text(
                text = titulo, fontFamily = Roboto, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, color = TextBlue, textAlign = TextAlign.Center
            )
            Text(
                text = mensaje, fontFamily = Roboto, fontSize = 15.sp, color = TextPrimary,
                textAlign = TextAlign.Center, lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onCerrar,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(23.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EduconnectBlue)
            ) {
                Text(textoBoton, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextWhite)
            }
        }
    }
}

// Popup a partir de una alerta del servidor
@Composable
private fun PopupRecuperacion(alerta: AlertaRecuperacion, onCerrar: () -> Unit) {
    when (alerta.tipo) {
        "CORREO_NO_REGISTRADO" -> PopupAlertaEdu(
            Icons.Filled.Email, RojoError, "Correo no registrado",
            "El correo ingresado no está registrado en EduConnect. Verifica que esté bien escrito.", onCerrar = onCerrar
        )
        "CODIGO_VENCIDO" -> PopupAlertaEdu(
            Icons.Filled.Schedule, RojoError, "Código no válido",
            alerta.mensaje.ifBlank { "El código expiró. Presiona Reenviar para recibir uno nuevo." }, onCerrar = onCerrar
        )
        "SOLICITUD_VENCIDA" -> PopupAlertaEdu(
            Icons.Filled.Schedule, RojoError, "Solicitud vencida",
            alerta.mensaje.ifBlank { "Vuelve a solicitar un código de verificación." }, onCerrar = onCerrar
        )
        "CONTRASENA" -> PopupAlertaEdu(
            Icons.Filled.Key, RojoError, "Contraseña no válida", alerta.mensaje, onCerrar = onCerrar
        )
        else -> PopupAlertaEdu(
            Icons.Filled.Warning, NaranjaMedia, "Aviso",
            alerta.mensaje.ifBlank { "Inténtalo nuevamente en unos minutos." }, onCerrar = onCerrar
        )
    }
}

// ============================== PIEZAS COMUNES ==============================

// Encabezado azul oscuro con ícono en círculo, título y subtítulos
@Composable
private fun EncabezadoRecuperacion(
    modifier: Modifier = Modifier,       // alto del encabezado (cada pantalla le da su proporción)
    titulo: String,
    subtitulo: String,
    detalle: String? = null,
    checkEnSubtitulo: Boolean = false,   // check verde a la izquierda del subtítulo
    icono: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AzulOscuro)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Box(
            modifier = Modifier.size(96.dp).background(Color.White.copy(alpha = 0.18f), CircleShape),
            contentAlignment = Alignment.Center
        ) { icono() }
        Spacer(modifier = Modifier.height(4.dp))
        Text(titulo, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextWhite, textAlign = TextAlign.Center)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (checkEnSubtitulo) {
                Icon(
                    Icons.Filled.CheckCircle, contentDescription = null, tint = VerdeOk,
                    modifier = Modifier.size(22.dp).background(TextWhite, CircleShape)
                )
            }
            Text(subtitulo, fontFamily = Roboto, fontSize = 18.sp, color = TextWhite.copy(alpha = 0.9f), textAlign = TextAlign.Center)
        }
        detalle?.let {
            Text(it, fontFamily = Roboto, fontSize = 16.sp, color = TextWhite.copy(alpha = 0.8f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun IconoEncabezado(icono: ImageVector) {
    Icon(icono, contentDescription = null, tint = TextWhite, modifier = Modifier.size(70.dp))
}

// Botón principal de color sólido (azul oscuro por defecto); muestra un indicador mientras carga
@Composable
private fun BotonPrincipal(
    texto: String,
    icono: ImageVector?,
    onClick: () -> Unit,
    cargando: Boolean = false,
    textoCargando: String = "Procesando...",
    habilitado: Boolean = true,
    color: Color = AzulOscuro
) {
    val activo = habilitado && !cargando

    Spacer(modifier = Modifier.height(8.dp))
    Spacer(modifier = Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(if (activo || cargando) color else BorderLight)
            .clickable(enabled = activo) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (cargando) {
                CircularProgressIndicator(color = TextWhite, strokeWidth = 2.5.dp, modifier = Modifier.size(22.dp))
                Text(textoCargando, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 19.sp, color = TextWhite)
            } else {
                icono?.let { Icon(it, contentDescription = null, tint = if (activo) TextWhite else TextSecondary, modifier = Modifier.size(28.dp)) }
                Text(texto, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = if (activo) TextWhite else TextSecondary)
            }
        }
    }
}

@Composable
private fun Etiqueta(texto: String, color: Color = TextBlue) {
    Text(texto, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = color, letterSpacing = 0.5.sp)
}

// Banner con ícono (verde = éxito, rojo = error, naranja = aviso)
@Composable
private fun Banner(icono: ImageVector, texto: String, color: Color, fondo: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(fondo, RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        Text(texto, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = color, lineHeight = 18.sp)
    }
}

private fun mmss(segundos: Long): String = "%02d:%02d".format(segundos / 60, segundos % 60)

// ============================== 1) INGRESAR CORREO ==============================

@Composable
fun RecuperarCorreoScreen(
    emailInicial: String = "",
    enviando: Boolean = false,
    alerta: AlertaRecuperacion? = null,
    onEnviar: (email: String) -> Unit = {},
    onCerrarAlerta: () -> Unit = {},
    onVolverLogin: () -> Unit = {}
) {
    var email by remember { mutableStateOf(emailInicial) }
    var alertaLocal by remember { mutableStateOf<String?>(null) }   // mensaje de validación local

    BackHandler { onVolverLogin() }

    alertaLocal?.let {
        PopupAlertaEdu(Icons.Filled.Email, NaranjaMedia, "Correo no válido", it, onCerrar = { alertaLocal = null })
    }
    alerta?.let { PopupRecuperacion(it, onCerrarAlerta) }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {
        EncabezadoRecuperacion(
            modifier = Modifier.weight(0.45f),
            titulo = "Recuperar Contraseña",
            subtitulo = "Ingresa tu correo y te enviaremos un código de verificación de 6 dígitos."
        ) { IconoEncabezado(Icons.Filled.LockOpen) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f)   // el cuerpo ocupa el resto de la pantalla
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 22.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Etiqueta("CORREO ELECTRÓNICO")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoCampoAzul, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderBlue, RoundedCornerShape(12.dp))
                    .padding(start = 14.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Filled.Email, contentDescription = null, tint = AzulOscuro, modifier = Modifier.size(28.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.trim() },
                    placeholder = { Text("nombre@correo.com", color = TextSecondary, fontSize = 15.sp, fontFamily = Roboto) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EduconnectBlue,
                        unfocusedBorderColor = BorderMedium,
                        focusedContainerColor = BackgroundWhite,
                        unfocusedContainerColor = BackgroundWhite,
                        focusedTextColor = TextBlue,
                        unfocusedTextColor = TextBlue,
                        cursorColor = EduconnectBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            BotonPrincipal(
                texto = "Enviar código",
                icono = Icons.AutoMirrored.Filled.Send,
                cargando = enviando,
                textoCargando = "Enviando...",
                onClick = {
                    when {
                        email.isBlank() -> alertaLocal = "Ingresa tu correo electrónico."
                        !REGEX_CORREO_REC.matches(email) -> alertaLocal = "Ingresa un correo válido, por ejemplo: nombre@correo.com"
                        else -> onEnviar(email)
                    }
                }
            )

            // VOLVER AL LOGIN: naranja con letras blancas
            Button(
                onClick = onVolverLogin,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextWhite)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Volver al Login", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 19.sp, color = TextWhite)
            }
        }
    }
}

// 2) INGRESAR CÓDIGO

@Composable
fun VerificarCodigoScreen(
    email: String = "",
    expiraEnMillis: Long = 0L,              // momento en que vence el código (System.currentTimeMillis)
    reenviarDesdeMillis: Long = 0L,         // momento desde el que se puede reenviar
    verificando: Boolean = false,
    reenviando: Boolean = false,
    errorCodigo: String? = null,            // "Código incorrecto..." (casillas en rojo)
    alerta: AlertaRecuperacion? = null,
    onVerificar: (codigo: String) -> Unit = {},
    onReenviar: () -> Unit = {},
    onLimpiarError: () -> Unit = {},
    onCerrarAlerta: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var codigo by remember { mutableStateOf("") }
    // Al recibir un código nuevo (reenvío) se limpian las casillas
    LaunchedEffect(expiraEnMillis) { codigo = "" }

    // Reloj de la pantalla (cuenta regresiva)
    var ahora by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            ahora = System.currentTimeMillis()
            delay(1000)
        }
    }
    val segundosRestantes = ((expiraEnMillis - ahora) / 1000).coerceAtLeast(0)
    val vencido = expiraEnMillis > 0 && segundosRestantes == 0L
    val segundosReenvio = ((reenviarDesdeMillis - ahora) / 1000).coerceAtLeast(0)
    val hayError = errorCodigo != null

    BackHandler { onBack() }
    alerta?.let { PopupRecuperacion(it, onCerrarAlerta) }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {
        EncabezadoRecuperacion(
            modifier = Modifier.weight(0.36f),
            titulo = if (hayError) "Verificar código" else "Código enviado",
            subtitulo = if (hayError) "Código de 6 dígitos" else "Revisa tu correo e ingresa el código de verificación"
        ) { IconoEncabezado(Icons.Filled.MarkEmailRead) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.64f)   // el cuerpo ocupa el resto de la pantalla
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 22.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                hayError -> Banner(Icons.Filled.ErrorOutline, errorCodigo!!, RojoError, FondoRojo)
                vencido -> Banner(Icons.Filled.Schedule, "El código expiró. Presiona Reenviar para recibir uno nuevo.", RojoError, FondoRojo)
                else -> Banner(
                    Icons.Outlined.CheckCircle,
                    "Código enviado a $email.\nRevisa tu bandeja de entrada (o spam).",
                    VerdeTexto, FondoVerde
                )
            }

            Text(
                text = "INGRESA EL CÓDIGO DE 6 DÍGITOS",
                fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                color = if (hayError) RojoError else TextBlue,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
            )

            // 6 casillas: un solo campo (teclado numérico) dibujado como 6 cajas
            BasicTextField(
                value = codigo,
                onValueChange = { nuevo ->
                    codigo = nuevo.filter { it.isDigit() }.take(6)
                    if (hayError) onLimpiarError()       // al escribir se quita el rojo
                },
                enabled = !vencido && !verificando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                decorationBox = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        repeat(6) { i ->
                            val digito = codigo.getOrNull(i)?.toString() ?: ""
                            val actual = i == codigo.length && !vencido
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(62.dp)
                                    .background(if (hayError) FondoRojo else FondoCampoAzul, RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (actual) 2.dp else 1.dp,
                                        color = when {
                                            hayError -> RojoError
                                            actual -> EduconnectBlue
                                            else -> BorderBlue
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    digito, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 30.sp,
                                    color = if (hayError) RojoError else AzulOscuro
                                )
                            }
                        }
                    }
                }
            )

            // EXPIRA EN
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Expira en:", fontFamily = Roboto, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .background(FondoRojo, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = RojoError, modifier = Modifier.size(18.dp))
                    Text(
                        if (vencido) "Vencido" else mmss(segundosRestantes),
                        fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = RojoError
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            BotonPrincipal(
                texto = "Verificar código",
                icono = Icons.Filled.CheckCircle,
                cargando = verificando,
                textoCargando = "Verificando...",
                habilitado = codigo.length == 6 && !vencido,
                onClick = { onVerificar(codigo) }
            )

            // ¿NO LO RECIBISTE? REENVIAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿No lo recibiste? ", fontFamily = Roboto, fontSize = 15.sp, color = TextSecondary)
                val puedeReenviar = segundosReenvio == 0L && !reenviando
                Text(
                    text = when {
                        reenviando -> "Reenviando..."
                        segundosReenvio > 0 -> "Reenviar en ${segundosReenvio} s"
                        else -> "Reenviar"
                    },
                    fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    color = if (puedeReenviar) EduconnectBlue else TextSecondary,
                    textDecoration = if (puedeReenviar) TextDecoration.Underline else TextDecoration.None,
                    modifier = Modifier.clickable(enabled = puedeReenviar) { onReenviar() }.padding(4.dp)
                )
            }
        }
    }
}

// ============================== 3) NUEVA CONTRASEÑA ==============================

// 0 a 4 puntos: longitud >= 8, letras y números, mayúscula y minúscula, símbolo o >= 12 caracteres
private fun puntajeSeguridad(p: String): Int {
    if (p.isEmpty()) return 0
    var puntos = 0
    if (p.length >= 8) puntos++
    if (p.any { it.isLetter() } && p.any { it.isDigit() }) puntos++
    if (p.any { it.isUpperCase() } && p.any { it.isLowerCase() }) puntos++
    if (p.any { !it.isLetterOrDigit() } || p.length >= 12) puntos++
    return puntos
}

private fun cumpleRegla(p: String) = p.length in 8..72 && p.any { it.isLetter() } && p.any { it.isDigit() }

@Composable
fun NuevaContrasenaScreen(
    guardando: Boolean = false,
    alerta: AlertaRecuperacion? = null,
    onActualizar: (nuevaPassword: String) -> Unit = {},
    onCerrarAlerta: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var nueva by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verNueva by remember { mutableStateOf(false) }
    var verConfirmar by remember { mutableStateOf(false) }
    var alertaLocal by remember { mutableStateOf<Pair<String, String>?>(null) }   // título, mensaje

    BackHandler { onBack() }
    alertaLocal?.let { (titulo, mensaje) ->
        PopupAlertaEdu(Icons.Filled.Key, RojoError, titulo, mensaje, onCerrar = { alertaLocal = null })
    }
    alerta?.let { PopupRecuperacion(it, onCerrarAlerta) }

    val nuevaValida = cumpleRegla(nueva)
    val coinciden = confirmar.isNotEmpty() && confirmar == nueva
    val puntos = puntajeSeguridad(nueva)
    val (nivel, colorNivel) = when {
        nueva.isEmpty() -> "" to TextSecondary
        puntos <= 1 || !nuevaValida -> "Débil" to RojoError
        puntos <= 3 -> "Media" to NaranjaMedia
        else -> "Fuerte" to VerdeOk
    }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {
        EncabezadoRecuperacion(
            modifier = Modifier.weight(0.36f),
            titulo = "Nueva contraseña",
            subtitulo = "Código verificado",
            checkEnSubtitulo = true,
            detalle = "Crea una contraseña nueva"
        ) { IconoEncabezado(Icons.Filled.Lock) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.64f)   // el cuerpo ocupa el resto de la pantalla
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 22.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Etiqueta("NUEVA CONTRASEÑA")
            CampoContrasena(
                valor = nueva,
                onCambio = { nueva = it },
                visible = verNueva,
                valido = nuevaValida,
                placeholder = "Mínimo 8 caracteres, letras y números",
                trailing = {
                    IconButton(onClick = { verNueva = !verNueva }) {
                        Icon(
                            if (verNueva) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (verNueva) "Ocultar" else "Mostrar", tint = TextSecondary
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))
            Etiqueta("CONFIRMAR CONTRASEÑA")
            CampoContrasena(
                valor = confirmar,
                onCambio = { confirmar = it },
                visible = verConfirmar,
                valido = coinciden && nuevaValida,
                placeholder = "Repite la contraseña",
                trailing = {
                    if (coinciden && nuevaValida) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = "Coinciden", tint = VerdeOk, modifier = Modifier.padding(end = 12.dp).size(28.dp))
                    } else {
                        IconButton(onClick = { verConfirmar = !verConfirmar }) {
                            Icon(
                                if (verConfirmar) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (verConfirmar) "Ocultar" else "Mostrar", tint = TextSecondary
                            )
                        }
                    }
                }
            )

            // BARRA DE SEGURIDAD
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Seguridad", fontFamily = Roboto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                Text(nivel, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colorNivel)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(BorderLight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (nueva.isEmpty()) 0f else (puntos.coerceAtLeast(1) / 4f))
                        .fillMaxHeight()
                        .background(colorNivel)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            BotonPrincipal(
                texto = "Actualizar contraseña",
                icono = Icons.Filled.Lock,
                cargando = guardando,
                textoCargando = "Actualizando...",
                onClick = {
                    when {
                        !nuevaValida -> alertaLocal = "Contraseña no válida" to
                                "La contraseña ingresada no cumple con los requisitos mínimos. " +
                                "Ingresa una contraseña de 8 caracteres o más, con letras y números."
                        confirmar != nueva -> alertaLocal = "Las contraseñas no coinciden" to
                                "Escribe la misma contraseña en ambos campos."
                        else -> onActualizar(nueva)
                    }
                }
            )
        }
    }
}

// Campo de contraseña: fondo verde suave cuando cumple la regla
@Composable
private fun CampoContrasena(
    valor: String,
    onCambio: (String) -> Unit,
    visible: Boolean,
    valido: Boolean,
    placeholder: String,
    trailing: @Composable () -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        placeholder = { Text(placeholder, color = TextSecondary, fontSize = 14.sp, fontFamily = Roboto) },
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = if (valido) VerdeOk else TextSecondary) },
        trailingIcon = trailing,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (valido) VerdeOk else EduconnectBlue,
            unfocusedBorderColor = if (valido) VerdeOk else BorderMedium,
            focusedContainerColor = if (valido) FondoVerde else BackgroundWhite,
            unfocusedContainerColor = if (valido) FondoVerde else BackgroundWhite,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = EduconnectBlue
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

// ============================== 4) ¡LISTO! ==============================

@Composable
fun ContrasenaActualizadaScreen(onIrLogin: () -> Unit = {}) {
    BackHandler { onIrLogin() }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {
        EncabezadoRecuperacion(modifier = Modifier.weight(0.45f), titulo = "¡Listo!", subtitulo = "Tu cuenta está protegida") {
            // Candado con check
            Box(contentAlignment = Alignment.BottomEnd) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = TextWhite, modifier = Modifier.size(54.dp))
                Icon(
                    Icons.Filled.CheckCircle, contentDescription = null, tint = VerdeOk,
                    modifier = Modifier.size(24.dp).background(TextWhite, CircleShape)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f)   // el cuerpo ocupa el resto de la pantalla
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = VerdeOk, modifier = Modifier.size(96.dp))
            Text(
                "Contraseña actualizada correctamente.",
                fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 21.sp,
                color = TextBlue, textAlign = TextAlign.Center
            )
            Text(
                "Ya puedes iniciar sesión con tu nueva contraseña.",
                fontFamily = Roboto, fontSize = 16.sp, color = TextSecondary, textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            BotonPrincipal(
                texto = "Ir al login",
                icono = Icons.AutoMirrored.Filled.Login,
                onClick = onIrLogin
            )
        }
    }
}

// ============================== PREVIEWS ==============================

@Preview(showBackground = true)
@Composable
fun RecuperarCorreoPreview() {
    EduConnectAppTheme { RecuperarCorreoScreen(emailInicial = "luis.perez@gmail.com") }
}

@Preview(showBackground = true)
@Composable
fun VerificarCodigoPreview() {
    EduConnectAppTheme {
        VerificarCodigoScreen(
            email = "luis.perez@gmail.com",
            expiraEnMillis = System.currentTimeMillis() + 522_000,
            errorCodigo = "Código incorrecto. Verifica e intenta nuevamente (te quedan 4 intentos)."
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NuevaContrasenaPreview() {
    EduConnectAppTheme { NuevaContrasenaScreen() }
}

@Preview(showBackground = true)
@Composable
fun ContrasenaActualizadaPreview() {
    EduConnectAppTheme { ContrasenaActualizadaScreen() }
}
