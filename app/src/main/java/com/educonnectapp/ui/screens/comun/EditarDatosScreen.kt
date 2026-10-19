package com.educonnectapp.ui.screens.comun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.ManageAccounts
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Sync
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.components.BotonIconoCabeceraEdu
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.BotonSecundarioEdu
import com.educonnectapp.ui.components.CabeceraEdu
import com.educonnectapp.ui.components.ColorModulo
import com.educonnectapp.ui.components.PopupConfirmacionEdu
import com.educonnectapp.ui.components.PopupExitoEdu
import com.educonnectapp.ui.components.PopupProcesandoEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduCampo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoralClaro
import com.educonnectapp.ui.theme.EduCoralOscuro
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// Error al guardar los datos (lo arma MainActivity y se muestra como popup)
data class AlertaDatos(val titulo: String, val mensaje: String)

// Une espacios repetidos: "  Hector   Reyna " -> "Hector Reyna"
private fun normalizarNombre(nombre: String) = nombre.trim().replace(Regex("\\s+"), " ")

// =====================================================================
// EDITAR DATOS (rediseño 2026) · lo usan docente y padre
// Solo nombre y teléfono (DNI y correo identifican la cuenta).
// Cabecera fija azul oscuro · campos blancos con borde turquesa · Cancelar / Actualizar
// =====================================================================

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
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Editar datos",
            subtitulo = "Actualiza tu nombre y teléfono",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CampoDatoEdu(
                etiqueta = "Nombre completo",
                valor = nombre,
                onCambio = { if (it.length <= 150) nombre = it },
                teclado = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                error = if (nombre.isBlank()) "Ingresa tu nombre completo" else null
            )
            CampoDatoEdu(
                etiqueta = "Teléfono",
                valor = telefono,
                onCambio = { nuevo -> telefono = nuevo.filter { it.isDigit() }.take(9) },
                teclado = KeyboardOptions(keyboardType = KeyboardType.Number),
                error = if (telefono.length != 9) "El teléfono debe tener 9 dígitos (${telefono.length}/9)" else null,
                ayuda = "9 dígitos"
            )
            CampoBloqueadoEdu(etiqueta = "DNI", valor = dni)
            CampoBloqueadoEdu(etiqueta = "Correo", valor = email)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EduAzulOscuroClaro, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.Lock, contentDescription = null, tint = EduAzulOscuro, modifier = Modifier.size(20.dp))
                Text(
                    "El DNI y el correo identifican tu cuenta y no se pueden modificar.",
                    modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp, lineHeight = 18.sp, color = EduEtiqueta
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
                habilitado = hayCambios && !procesando,
                onClick = {
                    errorLocal = when {
                        nombreNuevo.isEmpty() -> AlertaDatos("Falta el nombre", "Ingresa tu nombre completo.")
                        nombreNuevo.length < 3 -> AlertaDatos("Nombre muy corto", "Ingresa tu nombre completo.")
                        telefono.length != 9 -> AlertaDatos("Teléfono incompleto", "El teléfono debe tener 9 dígitos.")
                        else -> null
                    }
                    if (errorLocal == null) mostrarConfirmacion = true
                }
            )
        }

        barraInferior()
    }

    // ===================== POPUPS =====================
    // 1. Confirmación: muestra solo los campos que cambiaron (antes -> ahora)
    if (mostrarConfirmacion) {
        PopupConfirmacionEdu(
            titulo = "¿Actualizar tus datos?",
            mensaje = "Revisa que los datos nuevos sean correctos:",
            textoConfirmar = "Actualizar",
            colorConfirmar = TipoBotonEdu.ACTUALIZAR.fondo,
            icono = Icons.Rounded.ManageAccounts,
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
            titulo = "¡Datos actualizados!",
            mensaje = "Tus datos ya aparecen actualizados en tu perfil.",
            onCerrar = onCerrarExito
        )
    }

    // Errores: validación local o respuesta del servidor
    (errorLocal ?: alerta)?.let {
        PopupAlertaEdu(
            icono = Icons.Rounded.Warning,
            color = EduAmarillo,
            titulo = it.titulo,
            mensaje = it.mensaje,
            onCerrar = { if (errorLocal != null) errorLocal = null else onCerrarAlerta() }
        )
    }
}

// Campo editable: vacío = gris claro; con texto = fondo blanco y borde turquesa; con error = granate
@Composable
internal fun CampoDatoEdu(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    teclado: KeyboardOptions,
    error: String?,
    ayuda: String? = null
) {
    val lleno = valor.isNotBlank()
    val forma = RoundedCornerShape(16.dp)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(etiqueta, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = EduEtiqueta)
        BasicTextField(
            value = valor,
            onValueChange = onCambio,
            singleLine = true,
            keyboardOptions = teclado,
            textStyle = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EduTexto),
            cursorBrush = SolidColor(EduTeal),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 54.dp)
                        .clip(forma)
                        .background(
                            when {
                                error != null && lleno -> EduCoralClaro
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
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) { inner() }
            }
        )
        when {
            error != null -> Text(error, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduCoralOscuro)
            !ayuda.isNullOrBlank() -> Text(ayuda, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario)
        }
    }
}

// Campo de solo lectura con candado (DNI y correo)
@Composable
private fun CampoBloqueadoEdu(etiqueta: String, valor: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(etiqueta, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = EduEtiqueta)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduAzulOscuroClaro, RoundedCornerShape(16.dp))
                .border(1.5.dp, EduBorde, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                valor.ifBlank { "No registrado" }, modifier = Modifier.weight(1f), fontFamily = Nunito,
                fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EduTextoSecundario
            )
            Icon(Icons.Rounded.Lock, contentDescription = "No editable", tint = EduTextoSecundario, modifier = Modifier.size(18.dp))
        }
    }
}

// Fila de la confirmación: etiqueta, valor anterior tachado y valor nuevo
@Composable
private fun CambioDato(etiqueta: String, antes: String, ahora: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(EduTealClaro, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Text(etiqueta, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EduTextoSecundario)
        Text(
            antes.ifBlank { "Sin registrar" }, fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
            fontSize = 14.5.sp, color = EduTextoSecundario, textDecoration = TextDecoration.LineThrough
        )
        Text(ahora, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 16.sp, color = EduTexto)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun EditarDatosPreview() {
    EduConnectAppTheme {
        EditarDatosScreen(
            nombreActual = "Hector Reyna Gomez",
            telefonoActual = "987654321",
            dni = "47829301",
            email = "docente@correo.com"
        )
    }
}
