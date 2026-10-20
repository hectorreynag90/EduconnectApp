package com.educonnectapp.ui.screens.padre.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Verified
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
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
import com.educonnectapp.ui.components.TarjetaEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.components.TituloSeccionEdu
import com.educonnectapp.ui.components.TonoEdu
import com.educonnectapp.ui.screens.acceso.PopupAlertaEdu
import com.educonnectapp.ui.screens.padre.inicio.BottomNavBarPadre
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduCampo
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduCoralOscuro
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduFondo
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// Resultado de la búsqueda por código de estudiante
data class AlumnoEncontrado(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val codigoEstudiante: String
)

// =====================================================================
// ASOCIAR HIJO (rediseño 2026)
// Cabecera fija azul oscuro · código con "Buscar" dentro del campo · estudiante encontrado ·
// Cancelar + Asociar fijos · confirmación -> procesando -> éxito
// =====================================================================

@Composable
fun AsociarHijoScreen(
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},             // Inicio del padre (se conserva el nombre)
    onAgenda: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onBuscar: (codigo: String) -> Unit = {},
    alumnoEncontrado: AlumnoEncontrado? = null,
    mensajeError: String = "",
    onConfirmar: (AlumnoEncontrado) -> Unit = {},   // se llama después de confirmar en el popup
    onCancelar: () -> Unit = {},
    // Estado de la asociación (lo controla MainActivity)
    procesando: Boolean = false,
    exito: Boolean = false,
    alerta: AlertaHijo? = null,
    onCerrarExito: () -> Unit = {},
    onCerrarAlerta: () -> Unit = {},
) {
    var codigo by remember { mutableStateOf("") }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    // El alumno se guarda al confirmar para mostrarlo en el popup de éxito
    var alumnoAsociado by remember { mutableStateOf<AlumnoEncontrado?>(null) }
    val buscar = { if (codigo.isNotBlank()) onBuscar(codigo.trim()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EduFondo)
    ) {
        // CABECERA FIJA
        CabeceraEdu(
            titulo = "Asociar hijo",
            subtitulo = "Ingresa el código del estudiante",
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
            // ---------- CÓDIGO ----------
            CampoCodigo(
                valor = codigo,
                onCambio = { codigo = it.uppercase().filter { c -> !c.isWhitespace() } },
                error = mensajeError,
                onBuscar = buscar
            )

            // ---------- ESTUDIANTE ENCONTRADO ----------
            if (alumnoEncontrado != null) {
                TituloSeccionEdu("Estudiante encontrado")
                val forma = RoundedCornerShape(22.dp)
                TarjetaEdu(
                    modifier = Modifier.border(2.dp, EduTeal, forma),
                    relleno = PaddingValues(horizontal = 15.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InicialesHijoPerfil(alumnoEncontrado.aHijo(), EduTealClaro, tamano = 52)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${alumnoEncontrado.nombres} ${alumnoEncontrado.apellidos}".trim(), fontFamily = Nunito,
                                fontWeight = FontWeight.Black, fontSize = 16.5.sp, color = EduTexto,
                                maxLines = 2, overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "${alumnoEncontrado.gradoNombre} · Sección ${alumnoEncontrado.seccionNombre}",
                                fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduTextoSecundario
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ChipEdu("Estudiante verificado", TonoEdu.VERDE, icono = Icons.Rounded.Verified)
                        ChipEdu("Código ${alumnoEncontrado.codigoEstudiante}", TonoEdu.AZUL)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EduAzulOscuroClaro, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = EduAzulOscuro, modifier = Modifier.size(21.dp))
                    Text(
                        "Al asociarlo recibirás sus asistencias, comunicados y tareas.",
                        modifier = Modifier.weight(1f), fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp, lineHeight = 18.sp, color = EduEtiqueta
                    )
                }
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
            BotonSecundarioEdu(texto = "Cancelar", onClick = onCancelar, modifier = Modifier.weight(1f))
            BotonPrincipalEdu(
                texto = "Asociar",
                tipo = TipoBotonEdu.GUARDAR,
                icono = Icons.Rounded.PersonAdd,
                habilitado = alumnoEncontrado != null,
                onClick = { mostrarConfirmacion = true },
                modifier = Modifier.weight(1f)
            )
        }

        BottomNavBarPadre(
            onInicio = onHomeDocente,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Perfil",
        )
    }

    // ===================== POPUPS (PopupsEdu.kt) =====================
    // 1. Confirmación con los datos del alumno (turquesa: acción de guardar)
    if (mostrarConfirmacion && alumnoEncontrado != null) {
        PopupConfirmacionEdu(
            titulo = "¿Asociar a tu hijo?",
            mensaje = "Recibirás sus asistencias, comunicados y tareas:",
            textoConfirmar = "Asociar",
            colorConfirmar = TipoBotonEdu.GUARDAR.fondo,
            icono = Icons.Rounded.PersonAdd,
            onCancelar = { mostrarConfirmacion = false },
            onConfirmar = {
                mostrarConfirmacion = false
                alumnoAsociado = alumnoEncontrado
                onConfirmar(alumnoEncontrado)
            }
        ) {
            ResumenHijoEdu(alumnoEncontrado.aHijo(), colorBorde = EduTeal)
        }
    }

    // 2. Procesando
    if (procesando) PopupProcesandoEdu(titulo = "Asociando hijo…")

    // 3. Éxito: se cierra solo a los 5 segundos o con la X
    if (exito) {
        val a = alumnoAsociado
        PopupExitoEdu(
            titulo = "¡Hijo asociado!",
            detalle = a?.let { "${it.nombres} ${it.apellidos}\n${it.gradoNombre} ${it.seccionNombre}" },
            mensaje = "Ya aparece en tu perfil.",
            onCerrar = onCerrarExito
        )
    }

    // Error (por ejemplo, ya estaba asociado)
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

// Campo del código: gris vacío · blanco con borde turquesa al escribir · granate con error.
// El botón "Buscar" (amarillo, avanzar) va dentro del campo.
@Composable
private fun CampoCodigo(valor: String, onCambio: (String) -> Unit, error: String, onBuscar: () -> Unit) {
    val hayError = error.isNotBlank()
    val lleno = valor.isNotBlank()
    val forma = RoundedCornerShape(16.dp)
    Column {
        Text(
            "Código de estudiante", modifier = Modifier.padding(bottom = 6.dp), fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = EduEtiqueta
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(forma)
                .background(if (lleno || hayError) Color.White else EduCampo)
                .border(
                    if (lleno || hayError) 2.dp else 1.5.dp,
                    when {
                        hayError -> EduCoral
                        lleno -> EduTeal
                        else -> EduBorde
                    },
                    forma
                )
                .padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Rounded.Key, contentDescription = null, modifier = Modifier.size(20.dp),
                tint = when {
                    hayError -> EduCoralOscuro
                    lleno -> EduTealOscuro
                    else -> EduTextoSecundario
                }
            )
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (valor.isEmpty()) {
                    Text("Ej. PMU0001", fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = EduTextoSecundario)
                }
                BasicTextField(
                    value = valor,
                    onValueChange = onCambio,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp,
                        letterSpacing = 1.5.sp, color = EduTexto
                    ),
                    cursorBrush = SolidColor(EduTeal),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(onSearch = { onBuscar() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // Botón "Buscar" (amarillo = avanzar)
            Row(
                modifier = Modifier
                    .height(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (lleno) EduAmarillo else EduAmarillo.copy(alpha = 0.45f))
                    .clickable(enabled = lleno, onClick = onBuscar)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Rounded.Search, contentDescription = null, tint = EduTexto, modifier = Modifier.size(17.dp))
                Text("Buscar", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 14.sp, color = EduTexto)
            }
        }
        Text(
            if (hayError) error else "Lo encuentras en la ficha que te entregó el colegio",
            modifier = Modifier.padding(top = 6.dp, start = 2.dp), fontFamily = Nunito,
            fontWeight = if (hayError) FontWeight.ExtraBold else FontWeight.Bold, fontSize = 12.5.sp,
            color = if (hayError) EduCoralOscuro else EduTextoSecundario
        )
    }
}

private fun AlumnoEncontrado.aHijo() = HijoAsociado(
    id = id, nombres = nombres, apellidos = apellidos,
    gradoNombre = gradoNombre, seccionNombre = seccionNombre, codigoEstudiante = codigoEstudiante
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun AsociarHijoPreview() {
    EduConnectAppTheme {
        AsociarHijoScreen(
            alumnoEncontrado = AlumnoEncontrado(
                id = 1L,
                nombres = "Pedro",
                apellidos = "Perez Garcia",
                gradoNombre = "1er Grado",
                seccionNombre = "A",
                codigoEstudiante = "PMU0001"
            ),
        )
    }
}
