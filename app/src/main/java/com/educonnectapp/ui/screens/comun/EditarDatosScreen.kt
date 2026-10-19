package com.educonnectapp.ui.screens.comun

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupExitoEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
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

private val FondoCampoEditable = Color(0xFFE6F1FB)
private val FondoCampoBloqueado = Color(0xFFF1F3F5)

// Error al guardar los datos (lo arma MainActivity y se muestra como popup)
data class AlertaDatos(val titulo: String, val mensaje: String)

// Une espacios repetidos: "  Hector   Reyna " -> "Hector Reyna"
private fun normalizarNombre(nombre: String) = nombre.trim().replace(Regex("\\s+"), " ")

// Edición de datos personales: solo nombre y teléfono (DNI y correo identifican la cuenta)
@Composable
fun EditarDatosScreen(
    nombreActual: String,
    telefonoActual: String,
    dni: String,
    email: String,
    onBack: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onGuardar: (nombre: String, telefono: String) -> Unit = { _, _ -> },
    // Estado del guardado (lo controla MainActivity)
    procesando: Boolean = false,
    exito: Boolean = false,
    alerta: AlertaDatos? = null,
    onCerrarExito: () -> Unit = {},
    onCerrarAlerta: () -> Unit = {},
    // Barra inferior: la de docente o la de padre
    barraInferior: @Composable () -> Unit = {}
) {
    var nombre by remember { mutableStateOf(nombreActual) }
    var telefono by remember { mutableStateOf(telefonoActual) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var errorLocal by remember { mutableStateOf<AlertaDatos?>(null) }

    val nombreNuevo = normalizarNombre(nombre)
    val cambioNombre = nombreNuevo != nombreActual
    val cambioTelefono = telefono != telefonoActual
    val hayCambios = cambioNombre || cambioTelefono

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
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(35.dp).offset(x = (-5).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(text = "Editar datos", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextWhite)
                    Text(text = "Actualiza tu nombre y teléfono", fontFamily = Roboto,
                        fontSize = 16.sp, color = TextWhite.copy(alpha = 0.85f))
                }
            }
            IconButton(onClick = onNotificaciones) {
                Image(
                    painter = painterResource(id = R.drawable.notification_white),
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(35.dp)
                )
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
            Text(text = "DATOS PERSONALES", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                fontSize = 16.sp, color = EduconnectBlue, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // NOMBRE (editable)
                CampoEditable(
                    etiqueta = "Nombre completo",
                    valor = nombre,
                    onCambio = { if (it.length <= 150) nombre = it },
                    teclado = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    error = if (nombre.isBlank()) "Ingresa tu nombre completo" else null
                )
                // TELÉFONO (editable, solo números, 9 dígitos)
                CampoEditable(
                    etiqueta = "Teléfono",
                    valor = telefono,
                    onCambio = { nuevo -> telefono = nuevo.filter { it.isDigit() }.take(9) },
                    teclado = KeyboardOptions(keyboardType = KeyboardType.Number),
                    error = if (telefono.length != 9) "El teléfono debe tener 9 dígitos (${telefono.length}/9)" else null
                )
                // DNI y CORREO (solo lectura)
                CampoBloqueado(etiqueta = "DNI", valor = dni)
                CampoBloqueado(etiqueta = "Correo", valor = email)
            }

            Text(
                text = "El DNI y el correo identifican tu cuenta y no se pueden modificar.",
                fontFamily = Roboto, fontSize = 14.sp, color = TextSecondary,
                lineHeight = 19.sp, modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN ACTUALIZAR: valida y abre la confirmación
            Button(
                onClick = {
                    errorLocal = when {
                        nombreNuevo.isEmpty() -> AlertaDatos("Falta el nombre", "Ingresa tu nombre completo.")
                        nombreNuevo.length < 3 -> AlertaDatos("Nombre muy corto", "Ingresa tu nombre completo.")
                        telefono.length != 9 -> AlertaDatos("Teléfono incompleto", "El teléfono debe tener 9 dígitos.")
                        else -> null
                    }
                    if (errorLocal == null) mostrarConfirmacion = true
                },
                enabled = hayCambios,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOrange,
                    disabledContainerColor = BorderLight
                )
            ) {
                Text(text = "Actualizar", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = if (hayCambios) TextWhite else TextSecondary)
            }

            // BOTÓN CANCELAR
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
    // 1. Confirmación: muestra solo los campos que cambiaron (antes -> ahora)
    if (mostrarConfirmacion) {
        PopupConfirmacionEdu(
            titulo = "¿Actualizar tus datos?",
            mensaje = "Revisa que los datos nuevos sean correctos:",
            onCancelar = { mostrarConfirmacion = false },
            onConfirmar = {
                mostrarConfirmacion = false
                onGuardar(nombreNuevo, telefono)
            }
        ) {
            if (cambioNombre) CambioDato("Nombre completo", nombreActual, nombreNuevo)
            if (cambioTelefono) CambioDato("Teléfono", telefonoActual, telefono)
        }
    }

    // 2. Procesando
    if (procesando) PopupProcesandoEdu(titulo = "Guardando datos…")

    // 3. Éxito: se cierra solo a los 5 segundos o con la X y vuelve al perfil
    if (exito) {
        PopupExitoEdu(
            titulo = "¡Datos actualizados con éxito!",
            mensaje = "Tus datos ya aparecen actualizados en tu perfil.",
            onCerrar = onCerrarExito
        )
    }

    // Errores: validación local o respuesta del servidor
    (errorLocal ?: alerta)?.let {
        PopupAlertaEdu(
            icono = Icons.Filled.Warning,
            color = NaranjaMedia,
            titulo = it.titulo,
            mensaje = it.mensaje,
            onCerrar = {
                if (errorLocal != null) errorLocal = null else onCerrarAlerta()
            }
        )
    }
}

// Campo de texto editable con etiqueta y mensaje de error debajo
@Composable
private fun CampoEditable(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    teclado: KeyboardOptions,
    error: String?
) {
    Column {
        Text(text = etiqueta, fontFamily = Roboto, fontSize = 15.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            singleLine = true,
            keyboardOptions = teclado,
            isError = error != null,
            shape = RoundedCornerShape(10.dp),
            textStyle = TextStyle(
                fontFamily = Roboto, fontSize = 17.sp, fontWeight = FontWeight.Medium
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EduconnectBlue,
                unfocusedBorderColor = BorderLight,
                errorBorderColor = RojoError,
                focusedTextColor = TextBlue,
                unfocusedTextColor = TextBlue,
                errorTextColor = TextBlue,
                focusedContainerColor = FondoCampoEditable,
                unfocusedContainerColor = FondoCampoEditable,
                errorContainerColor = FondoCampoEditable
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(text = error, fontFamily = Roboto, fontSize = 13.sp, color = RojoError,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp))
        }
    }
}

// Campo de solo lectura con candado (DNI y correo)
@Composable
private fun CampoBloqueado(etiqueta: String, valor: String) {
    Column {
        Text(text = etiqueta, fontFamily = Roboto, fontSize = 15.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FondoCampoBloqueado, RoundedCornerShape(10.dp))
                .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = valor.ifBlank { "No registrado" }, fontFamily = Roboto, fontSize = 17.sp,
                color = TextSecondary, modifier = Modifier.weight(1f))
            Icon(Icons.Filled.Lock, contentDescription = "No editable",
                tint = TextSecondary, modifier = Modifier.size(18.dp))
        }
    }
}

// Fila de la confirmación: etiqueta, valor anterior tachado y valor nuevo
@Composable
private fun CambioDato(etiqueta: String, antes: String, ahora: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoCampoEditable, RoundedCornerShape(12.dp))
            .border(1.dp, EduconnectBlue, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(text = etiqueta, fontFamily = Roboto, fontSize = 13.sp, color = TextSecondary)
        Text(
            text = antes.ifBlank { "Sin registrar" },
            fontFamily = Roboto, fontSize = 15.sp, color = TextSecondary,
            textDecoration = TextDecoration.LineThrough
        )
        Box(modifier = Modifier.height(2.dp))
        Text(text = ahora, fontFamily = Roboto, fontWeight = FontWeight.Bold,
            fontSize = 17.sp, color = EduconnectBlue)
    }
}

@Preview(showBackground = true)
@Composable
fun EditarDatosPreview() {
    EduConnectAppTheme {
        EditarDatosScreen(
            nombreActual = "Hector Alexander",
            telefonoActual = "969601897",
            dni = "18181815",
            email = "docente@correo.com"
        )
    }
}
