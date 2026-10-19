package com.educonnectapp.ui.screens.acceso

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.MarkEmailRead
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.CampoTextoEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.components.coloresDeTono
import com.educonnectapp.ui.theme.EduAmarillo
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
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito
import kotlinx.coroutines.delay

// Rediseño 2026: cabecera coral (ColorModulo.Recuperar) + tarjeta blanca, igual que el Login y el Registro

private val REGEX_CORREO_REC = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

// ============================== MODELOS ==============================

// Alerta del servidor que se muestra como popup.
// tipo: "CORREO_NO_REGISTRADO" | "CODIGO_VENCIDO" | "SOLICITUD_VENCIDA" | "CONTRASENA" | "ERROR"
class AlertaRecuperacion(val tipo: String, val mensaje: String = "")

// ============================== POPUP COMÚN ==============================

// Popup de alerta de EduConnect: ícono en círculo de color con halo, título, mensaje y botón "Entendido"
// (tipo AVANZAR: amarillo).
// También lo usan LoginScreen, RegisterScreen y otras pantallas.
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
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(horizontal = 22.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(92.dp).background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(68.dp).background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icono, contentDescription = null,
                        tint = if (color == EduAmarillo) EduTexto else Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
            Text(
                text = titulo, fontFamily = Nunito, fontWeight = FontWeight.Black,
                fontSize = 21.sp, color = EduTexto, textAlign = TextAlign.Center
            )
            Text(
                text = mensaje, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                color = EduEtiqueta, textAlign = TextAlign.Center, lineHeight = 21.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onCerrar,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TipoBotonEdu.AVANZAR.fondo),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(textoBoton, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TipoBotonEdu.AVANZAR.texto)
            }
        }
    }
}

// Popup a partir de una alerta del servidor
@Composable
private fun PopupRecuperacion(alerta: AlertaRecuperacion, onCerrar: () -> Unit) {
    when (alerta.tipo) {
        "CORREO_NO_REGISTRADO" -> PopupAlertaEdu(
            Icons.Rounded.Email, EduCoral, "Correo no registrado",
            "El correo ingresado no está registrado en EduConnect. Verifica que esté bien escrito.", onCerrar = onCerrar
        )
        "CODIGO_VENCIDO" -> PopupAlertaEdu(
            Icons.Rounded.Schedule, EduCoral, "Código no válido",
            alerta.mensaje.ifBlank { "El código expiró. Presiona Reenviar para recibir uno nuevo." }, onCerrar = onCerrar
        )
        "SOLICITUD_VENCIDA" -> PopupAlertaEdu(
            Icons.Rounded.Schedule, EduCoral, "Solicitud vencida",
            alerta.mensaje.ifBlank { "Vuelve a solicitar un código de verificación." }, onCerrar = onCerrar
        )
        "CONTRASENA" -> PopupAlertaEdu(
            Icons.Rounded.Key, EduCoral, "Contraseña no válida", alerta.mensaje, onCerrar = onCerrar
        )
        else -> PopupAlertaEdu(
            Icons.Rounded.Warning, EduAmarillo, "Aviso",
            alerta.mensaje.ifBlank { "Inténtalo nuevamente en unos minutos." }, onCerrar = onCerrar
        )
    }
}

// ============================== PIEZAS COMUNES ==============================

// Estructura de las 4 pantallas: cabecera coral + tarjeta blanca que sube sobre ella
@Composable
private fun PantallaRecuperacion(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    onVolver: (() -> Unit)? = null,
    checkEnSubtitulo: Boolean = false,   // check en círculo blanco a la izquierda del subtítulo
    detalle: String? = null,
    iconoConCheck: Boolean = false,      // candado con check (pantalla ¡Listo!)
    debajoDeTarjeta: @Composable ColumnScope.() -> Unit = {},
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        // CABECERA CORAL
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorModulo.Recuperar, RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 36.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp)) {
                if (onVolver != null) {
                    BotonIconoCabeceraEdu(
                        icono = Icons.AutoMirrored.Rounded.ArrowBack,
                        descripcion = "Volver",
                        onClick = onVolver,
                        colorCabecera = ColorModulo.Recuperar
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier.size(92.dp).background(Color.White.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (iconoConCheck) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
                            Box(
                                modifier = Modifier.size(24.dp).background(EduAmarillo, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Check, contentDescription = null, tint = EduTexto, modifier = Modifier.size(16.dp))
                            }
                        }
                    } else {
                        Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(46.dp))
                    }
                }
                Text(
                    text = titulo, modifier = Modifier.padding(top = 4.dp),
                    fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 27.sp,
                    color = Color.White, textAlign = TextAlign.Center
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (checkEnSubtitulo) {
                        Box(
                            modifier = Modifier.size(22.dp).background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = EduCoralOscuro, modifier = Modifier.size(15.dp))
                        }
                    }
                    Text(
                        text = subtitulo, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                        lineHeight = 22.sp, color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center
                    )
                }
                detalle?.let {
                    Text(
                        text = it, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center
                    )
                }
            }
        }

        // TARJETA BLANCA (sube 48 dp sobre la cabecera)
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .offset(y = (-48).dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp, shape = RoundedCornerShape(28.dp),
                    ambientColor = ColorModulo.Recuperar.copy(alpha = 0.25f), spotColor = ColorModulo.Recuperar.copy(alpha = 0.25f)
                )
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = contenido
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-36).dp)
                .padding(horizontal = 18.dp)
                .navigationBarsPadding(),
            content = debajoDeTarjeta
        )
    }
}

// Banner con ícono (VERDE = éxito, CORAL = error, AMARILLO = aviso)
@Composable
private fun Banner(icono: ImageVector, texto: String, tono: TonoEdu) {
    val c = coloresDeTono(tono)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(c.fondo, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icono, contentDescription = null, tint = c.texto, modifier = Modifier.size(26.dp))
        Text(texto, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = c.texto, lineHeight = 19.sp)
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
        PopupAlertaEdu(Icons.Rounded.Email, EduAmarillo, "Correo no válido", it, onCerrar = { alertaLocal = null })
    }
    alerta?.let { PopupRecuperacion(it, onCerrarAlerta) }

    PantallaRecuperacion(
        titulo = "Recuperar contraseña",
        subtitulo = "Ingresa tu correo y te enviaremos un código de verificación de 6 dígitos.",
        icono = Icons.Rounded.LockOpen,
        onVolver = onVolverLogin
    ) {
        CampoTextoEdu(
            valor = email,
            onValorCambia = { email = it.trim() },
            etiqueta = "Correo electrónico",
            placeholder = "nombre@correo.com",
            icono = Icons.Rounded.Email,
            tipoTeclado = KeyboardType.Email,
            accionTeclado = ImeAction.Done
        )

        BotonPrincipalEdu(
            texto = "Enviar código",
            tipo = TipoBotonEdu.AVANZAR,
            icono = Icons.AutoMirrored.Rounded.Send,
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

        BotonSecundarioEdu(
            texto = "Volver al login",
            icono = Icons.AutoMirrored.Rounded.ArrowBack,
            onClick = onVolverLogin
        )
    }
}

// ============================== 2) INGRESAR CÓDIGO ==============================

@Composable
fun VerificarCodigoScreen(
    email: String = "",
    expiraEnMillis: Long = 0L,              // momento en que vence el código (System.currentTimeMillis)
    reenviarDesdeMillis: Long = 0L,         // momento desde el que se puede reenviar
    verificando: Boolean = false,
    reenviando: Boolean = false,
    errorCodigo: String? = null,            // "Código incorrecto..." (casillas en coral)
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

    PantallaRecuperacion(
        titulo = if (hayError) "Verificar código" else "Código enviado",
        subtitulo = if (hayError) "Código de 6 dígitos" else "Revisa tu correo e ingresa el código de verificación.",
        icono = Icons.Rounded.MarkEmailRead,
        onVolver = onBack,
        debajoDeTarjeta = {
            // ¿NO LO RECIBISTE? REENVIAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "¿No lo recibiste? ", fontFamily = Nunito, fontWeight = FontWeight.Bold,
                    fontSize = 15.sp, color = EduTextoSecundario
                )
                val puedeReenviar = segundosReenvio == 0L && !reenviando
                Text(
                    text = when {
                        reenviando -> "Reenviando..."
                        segundosReenvio > 0 -> "Reenviar en $segundosReenvio s"
                        else -> "Reenviar"
                    },
                    fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp,
                    color = if (puedeReenviar) EduTealOscuro else EduTextoSecundario,
                    textDecoration = if (puedeReenviar) TextDecoration.Underline else TextDecoration.None,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = puedeReenviar) { onReenviar() }
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                )
            }
        }
    ) {
        when {
            hayError -> Banner(Icons.Rounded.ErrorOutline, errorCodigo!!, TonoEdu.CORAL)
            vencido -> Banner(Icons.Rounded.Schedule, "El código expiró. Presiona Reenviar para recibir uno nuevo.", TonoEdu.CORAL)
            else -> Banner(
                Icons.Rounded.CheckCircle,
                "Código enviado a $email.\nRevisa tu bandeja de entrada (o spam).",
                TonoEdu.VERDE
            )
        }

        Text(
            text = "Ingresa el código de 6 dígitos",
            fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
            color = if (hayError) EduCoralOscuro else EduEtiqueta,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
        )

        // 6 casillas: un solo campo (teclado numérico) dibujado como 6 cajas
        BasicTextField(
            value = codigo,
            onValueChange = { nuevo ->
                codigo = nuevo.filter { it.isDigit() }.take(6)
                if (hayError) onLimpiarError()       // al escribir se quita el coral
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
                                .height(58.dp)
                                .background(
                                    when {
                                        hayError -> EduCoralClaro
                                        actual -> Color.White
                                        else -> EduCampo
                                    },
                                    RoundedCornerShape(15.dp)
                                )
                                .border(
                                    width = if (actual || hayError) 2.dp else 1.5.dp,
                                    color = when {
                                        hayError -> EduCoralOscuro
                                        actual -> EduTeal
                                        else -> EduBorde
                                    },
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                digito, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 26.sp,
                                color = if (hayError) EduCoralOscuro else EduTexto
                            )
                        }
                    }
                }
            }
        )

        // EXPIRA EN
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Expira en:", fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                color = EduTextoSecundario, modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier
                    .background(EduCoralClaro, RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Rounded.Schedule, contentDescription = null, tint = EduCoralOscuro, modifier = Modifier.size(18.dp))
                Text(
                    if (vencido) "Vencido" else mmss(segundosRestantes),
                    fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 15.sp, color = EduCoralOscuro
                )
            }
        }

        BotonPrincipalEdu(
            texto = "Verificar código",
            tipo = TipoBotonEdu.AVANZAR,
            icono = Icons.Rounded.CheckCircle,
            cargando = verificando,
            textoCargando = "Verificando...",
            habilitado = codigo.length == 6 && !vencido,
            onClick = { onVerificar(codigo) }
        )
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
        PopupAlertaEdu(Icons.Rounded.Key, EduCoral, titulo, mensaje, onCerrar = { alertaLocal = null })
    }
    alerta?.let { PopupRecuperacion(it, onCerrarAlerta) }

    val nuevaValida = cumpleRegla(nueva)
    val coinciden = confirmar.isNotEmpty() && confirmar == nueva
    val puntos = puntajeSeguridad(nueva)
    val (nivel, colorNivel) = when {
        nueva.isEmpty() -> "" to EduTextoSecundario
        puntos <= 1 || !nuevaValida -> "Débil" to EduCoral
        puntos <= 3 -> "Media" to EduAmarillo
        else -> "Fuerte" to EduTeal
    }

    PantallaRecuperacion(
        titulo = "Nueva contraseña",
        subtitulo = "Código verificado",
        checkEnSubtitulo = true,
        detalle = "Crea una contraseña nueva",
        icono = Icons.Rounded.Lock,
        onVolver = onBack
    ) {
        CampoContrasena(
            etiqueta = "Nueva contraseña",
            valor = nueva,
            onCambio = { nueva = it },
            visible = verNueva,
            valido = nuevaValida,
            placeholder = "Mínimo 8 caracteres, letras y números",
            trailing = {
                IconButton(onClick = { verNueva = !verNueva }) {
                    Icon(
                        if (verNueva) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = if (verNueva) "Ocultar" else "Mostrar", tint = EduTextoSecundario
                    )
                }
            }
        )

        // BARRA DE SEGURIDAD (4 segmentos) + requisitos
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(4) { i ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .background(
                                if (nueva.isNotEmpty() && i < puntos.coerceAtLeast(1)) colorNivel else EduAzulOscuroClaro,
                                RoundedCornerShape(50)
                            )
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Seguridad", fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                    color = EduTextoSecundario, modifier = Modifier.weight(1f)
                )
                Text(
                    nivel, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp,
                    color = if (colorNivel == EduAmarillo) EduTexto else colorNivel
                )
            }
            RequisitosContrasenaEdu(nueva)   // está en RegisterScreen.kt (mismo paquete)
        }

        CampoContrasena(
            etiqueta = "Confirmar contraseña",
            valor = confirmar,
            onCambio = { confirmar = it },
            visible = verConfirmar,
            valido = coinciden && nuevaValida,
            placeholder = "Repite la contraseña",
            trailing = {
                if (coinciden && nuevaValida) {
                    Icon(
                        Icons.Rounded.CheckCircle, contentDescription = "Coinciden", tint = EduTeal,
                        modifier = Modifier.padding(end = 12.dp).size(26.dp)
                    )
                } else {
                    IconButton(onClick = { verConfirmar = !verConfirmar }) {
                        Icon(
                            if (verConfirmar) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = if (verConfirmar) "Ocultar" else "Mostrar", tint = EduTextoSecundario
                        )
                    }
                }
            }
        )

        BotonPrincipalEdu(
            texto = "Actualizar contraseña",
            tipo = TipoBotonEdu.ACTUALIZAR,
            icono = Icons.Rounded.Lock,
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

// Campo de contraseña con etiqueta: borde y fondo turquesa suave cuando cumple la regla
@Composable
private fun CampoContrasena(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    visible: Boolean,
    valido: Boolean,
    placeholder: String,
    trailing: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            etiqueta, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
            color = if (valido) EduTealOscuro else EduEtiqueta
        )
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            placeholder = {
                Text(placeholder, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = EduTextoSecundario)
            },
            leadingIcon = {
                Icon(Icons.Rounded.Lock, contentDescription = null, tint = if (valido) EduTeal else EduTextoSecundario)
            },
            trailingIcon = trailing,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            textStyle = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EduTexto),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EduTeal,
                unfocusedBorderColor = if (valido) EduTeal else EduBorde,
                focusedContainerColor = if (valido) EduTealClaro else Color.White,
                unfocusedContainerColor = if (valido) EduTealClaro else EduCampo,
                focusedTextColor = EduTexto,
                unfocusedTextColor = EduTexto,
                cursorColor = EduTeal
            ),
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
        )
    }
}

// ============================== 4) ¡LISTO! ==============================

@Composable
fun ContrasenaActualizadaScreen(onIrLogin: () -> Unit = {}) {
    BackHandler { onIrLogin() }

    PantallaRecuperacion(
        titulo = "¡Listo!",
        subtitulo = "Tu cuenta está protegida",
        icono = Icons.Rounded.Lock,
        iconoConCheck = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Check turquesa con halo (mismo estilo que PopupExitoEdu)
            Box(
                modifier = Modifier.size(96.dp).background(EduTealClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(72.dp).background(EduTeal, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(42.dp))
                }
            }
            Text(
                "Contraseña actualizada correctamente.",
                fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 21.sp,
                color = EduTealOscuro, textAlign = TextAlign.Center, lineHeight = 27.sp
            )
            Text(
                "Ya puedes iniciar sesión con tu nueva contraseña.",
                fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 15.5.sp,
                color = EduTextoSecundario, textAlign = TextAlign.Center
            )
        }
        BotonPrincipalEdu(
            texto = "Ir al login",
            tipo = TipoBotonEdu.AVANZAR,
            icono = Icons.AutoMirrored.Rounded.Login,
            onClick = onIrLogin
        )
    }
}

// ============================== PREVIEWS ==============================

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun RecuperarCorreoPreview() {
    EduConnectAppTheme { RecuperarCorreoScreen(emailInicial = "luis.perez@gmail.com") }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
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

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun NuevaContrasenaPreview() {
    EduConnectAppTheme { NuevaContrasenaScreen() }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ContrasenaActualizadaPreview() {
    EduConnectAppTheme { ContrasenaActualizadaScreen() }
}
