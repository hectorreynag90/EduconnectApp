package com.educonnectapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.theme.EduAmarillo
import com.educonnectapp.ui.theme.EduAmarilloClaro
import com.educonnectapp.ui.theme.EduAmarilloOscuro
import com.educonnectapp.ui.theme.EduAzulOscuro
import com.educonnectapp.ui.theme.EduAzulOscuroClaro
import com.educonnectapp.ui.theme.EduBorde
import com.educonnectapp.ui.theme.EduCampo
import com.educonnectapp.ui.theme.EduCoral
import com.educonnectapp.ui.theme.EduCoralClaro
import com.educonnectapp.ui.theme.EduCoralOscuro
import com.educonnectapp.ui.theme.EduEtiqueta
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealClaro
import com.educonnectapp.ui.theme.EduTealOscuro
import com.educonnectapp.ui.theme.EduTexto
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// =====================================================================
// COMPONENTES DEL REDISEÑO 2026 (Prototipo B)
// Piezas visuales que se repiten en todas las pantallas. Solo UI:
// no llaman al ApiClient ni navegan; reciben datos y callbacks.
// =====================================================================

// ---------- Tonos de estado ----------
// VERDE = Asistió / Entregado / Registrada · AMARILLO = Tardanza / Pendiente
// CORAL = Faltó / No entregado / error · AZUL = Evaluación · GRIS = neutro
enum class TonoEdu { VERDE, AMARILLO, CORAL, AZUL, GRIS }

data class ColoresTono(val fondo: Color, val texto: Color, val solido: Color)

fun coloresDeTono(tono: TonoEdu): ColoresTono = when (tono) {
    TonoEdu.VERDE -> ColoresTono(EduTealClaro, EduTealOscuro, EduTeal)
    TonoEdu.AMARILLO -> ColoresTono(EduAmarilloClaro, EduAmarilloOscuro, EduAmarillo)
    TonoEdu.CORAL -> ColoresTono(EduCoralClaro, EduCoralOscuro, EduCoral)
    TonoEdu.AZUL -> ColoresTono(EduAzulOscuroClaro, EduAzulOscuro, EduAzulOscuro)
    TonoEdu.GRIS -> ColoresTono(EduAzulOscuroClaro, EduTextoSecundario, EduTextoSecundario)
}

// ---------- Color de cabecera por módulo ----------
// Las cabeceras varían entre los 4 colores de la paleta. Para cambiar el color de un
// módulo, se cambia solo aquí.
object ColorModulo {
    val Bienvenida = EduTeal
    val Login = EduAzulOscuro
    val Registro = EduAmarillo
    val Recuperar = EduCoral
    val Inicio = EduTeal
    val Notificaciones = EduCoral
    val Alumnos = EduTeal
    val Asistencia = EduAzulOscuro
    val Comunicados = EduCoral
    val Publicaciones = EduAmarillo
    val Perfil = EduAzulOscuro
}

// Colores de texto e íconos según el fondo de la cabecera.
// Sobre amarillo se usa texto oscuro (el blanco no se lee); sobre los demás, blanco.
data class EstiloCabecera(
    val titulo: Color,
    val subtitulo: Color,
    val icono: Color,
    val fondoBoton: Color
)

fun estiloCabecera(fondo: Color): EstiloCabecera =
    if (fondo == EduAmarillo) EstiloCabecera(
        titulo = EduTexto,
        subtitulo = EduEtiqueta,
        icono = EduTexto,
        fondoBoton = EduTexto.copy(alpha = 0.10f)
    ) else EstiloCabecera(
        titulo = Color.White,
        subtitulo = Color.White.copy(alpha = 0.85f),
        icono = Color.White,
        fondoBoton = Color.White.copy(alpha = 0.2f)
    )

// ---------- Cabecera de color con esquinas inferiores redondeadas ----------
@Composable
fun CabeceraEdu(
    titulo: String,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    onVolver: (() -> Unit)? = null,
    colorFondo: Color = EduTeal,
    espacioInferior: Dp = 22.dp,
    acciones: @Composable RowScope.() -> Unit = {},
    contenidoInferior: @Composable ColumnScope.() -> Unit = {}
) {
    val estilo = estiloCabecera(colorFondo)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorFondo, RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .statusBarsPadding()
            .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = espacioInferior)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (onVolver != null) {
                BotonIconoCabeceraEdu(
                    icono = Icons.AutoMirrored.Rounded.ArrowBack,
                    descripcion = "Volver",
                    onClick = onVolver,
                    colorCabecera = colorFondo
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo, fontFamily = Nunito, fontWeight = FontWeight.Black,
                    fontSize = 23.sp, lineHeight = 27.sp, color = estilo.titulo
                )
                if (!subtitulo.isNullOrBlank()) {
                    Text(
                        text = subtitulo, fontFamily = Nunito, fontWeight = FontWeight.Bold,
                        fontSize = 14.sp, color = estilo.subtitulo
                    )
                }
            }
            acciones()
        }
        contenidoInferior()
    }
}

// Botón cuadrado translúcido para la cabecera (volver, campana…). conPunto = aviso sin leer
// colorCabecera: color de fondo de la cabecera, para elegir ícono blanco u oscuro
@Composable
fun BotonIconoCabeceraEdu(
    icono: ImageVector,
    descripcion: String,
    onClick: () -> Unit,
    conPunto: Boolean = false,
    colorCabecera: Color = EduTeal
) {
    val estilo = estiloCabecera(colorCabecera)
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(estilo.fondoBoton)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = descripcion, tint = estilo.icono, modifier = Modifier.size(24.dp))
        if (conPunto) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 9.dp, end = 10.dp)
                    .size(10.dp)
                    .background(EduCoral, CircleShape)
            )
        }
    }
}

// Chip translúcido sobre la cabecera (fecha, colegio…)
@Composable
fun ChipCabeceraEdu(texto: String, modifier: Modifier = Modifier, colorCabecera: Color = EduTeal) {
    val estilo = estiloCabecera(colorCabecera)
    Text(
        text = texto,
        modifier = modifier
            .background(estilo.fondoBoton, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp,
        color = estilo.titulo, maxLines = 1
    )
}

// ---------- Botones ----------
// Un color fijo por TIPO DE ACCIÓN, igual en todas las pantallas:
//   AVANZAR    (amarillo)    Ingresar, Iniciar sesión, Enviar, Publicar, Verificar, Ir al login, Entendido
//   GUARDAR    (turquesa)    Guardar, Crear cuenta, + Crear, Agregar, Asociar, Confirmar
//   ACTUALIZAR (azul oscuro) Actualizar, Editar
//   ELIMINAR   (coral)       Eliminar, Quitar, Cerrar sesión
// Volver, Cancelar y Ver historial usan BotonSecundarioEdu (blanco con borde gris).
enum class TipoBotonEdu(val fondo: Color, val texto: Color) {
    AVANZAR(EduAmarillo, EduTexto),
    GUARDAR(EduTeal, Color.White),
    ACTUALIZAR(EduAzulOscuro, Color.White),
    ELIMINAR(EduCoral, Color.White)
}

// Color de texto/ícono legible sobre un fondo de la paleta (oscuro sobre amarillo, blanco en los demás)
fun textoSobre(fondo: Color): Color = if (fondo == EduAmarillo) EduTexto else Color.White

// Botón principal: su color sale del tipo de acción (por defecto AVANZAR)
@Composable
fun BotonPrincipalEdu(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tipo: TipoBotonEdu = TipoBotonEdu.AVANZAR,
    icono: ImageVector? = null,
    cargando: Boolean = false,
    textoCargando: String = texto,
    habilitado: Boolean = true,
    colorFondo: Color = tipo.fondo,
    colorTexto: Color = tipo.texto
) {
    Button(
        onClick = { if (!cargando) onClick() },
        enabled = habilitado,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFondo,
            contentColor = colorTexto,
            disabledContainerColor = colorFondo.copy(alpha = 0.45f),
            disabledContentColor = colorTexto.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(color = colorTexto, strokeWidth = 3.dp, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(textoCargando, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp)
        } else {
            if (icono != null) {
                Icon(icono, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(texto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp)
        }
    }
}

// Botón blanco con borde gris (acciones secundarias: "Volver", "Cancelar", "Ver historial")
@Composable
fun BotonSecundarioEdu(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    colorPrincipal: Color = EduBorde,
    colorTexto: Color = EduEtiqueta
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(2.dp, colorPrincipal),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = colorTexto)
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(texto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp)
    }
}

// Botón turquesa claro (alternativa suave; ya no se usa en Bienvenida, se conserva por compatibilidad)
@Composable
fun BotonClaroEdu(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null
) {
    BotonPrincipalEdu(
        texto = texto, onClick = onClick, modifier = modifier, icono = icono,
        colorFondo = EduTealClaro, colorTexto = EduTealOscuro
    )
}

// Botón flotante "+ Crear" (esquina inferior derecha, sobre la barra inferior). Tipo GUARDAR (turquesa)
@Composable
fun BotonCrearEdu(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    texto: String = "Crear"
) {
    val forma = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .height(56.dp)
            .shadow(10.dp, forma, ambientColor = TipoBotonEdu.GUARDAR.fondo, spotColor = TipoBotonEdu.GUARDAR.fondo)
            .clip(forma)
            .background(TipoBotonEdu.GUARDAR.fondo)
            .clickable(onClick = onClick)
            .padding(start = 18.dp, end = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Rounded.Add, contentDescription = null, tint = TipoBotonEdu.GUARDAR.texto, modifier = Modifier.size(24.dp))
        Text(texto, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 17.sp, color = TipoBotonEdu.GUARDAR.texto)
    }
}

// ---------- Campo de texto con etiqueta arriba ----------
@Composable
fun CampoTextoEdu(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    icono: ImageVector? = null,
    esContrasena: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    accionTeclado: ImeAction = ImeAction.Next,
    enError: Boolean = false,
    mensajeAyuda: String? = null,
    bloqueado: Boolean = false,
    lineas: Int = 1
) {
    var visible by remember { mutableStateOf(false) }
    var enfocado by remember { mutableStateOf(false) }
    val colorIcono = when {
        enError -> EduCoralOscuro
        enfocado -> EduTeal
        else -> EduTextoSecundario
    }

    val placeholderComp: (@Composable () -> Unit)? = if (placeholder.isEmpty()) null else {
        {
            Text(placeholder, fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp, color = EduTextoSecundario)
        }
    }
    val iconoInicio: (@Composable () -> Unit)? = if (icono == null) null else {
        { Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(22.dp)) }
    }
    val iconoFinal: (@Composable () -> Unit)? = when {
        bloqueado -> {
            { Icon(Icons.Rounded.Lock, contentDescription = "No editable", tint = EduTextoSecundario, modifier = Modifier.size(20.dp)) }
        }
        esContrasena -> {
            {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = colorIcono
                    )
                }
            }
        }
        else -> null
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = etiqueta, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
            color = if (enError) EduCoralOscuro else EduEtiqueta
        )
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambia,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (lineas == 1) 56.dp else (24 * lineas + 32).dp)
                .onFocusChanged { enfocado = it.isFocused },
            enabled = !bloqueado,
            singleLine = lineas == 1,
            minLines = lineas,
            textStyle = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EduTexto),
            placeholder = placeholderComp,
            leadingIcon = iconoInicio,
            trailingIcon = iconoFinal,
            visualTransformation = if (esContrasena && !visible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (esContrasena) KeyboardType.Password else tipoTeclado,
                imeAction = accionTeclado
            ),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (enError) EduCoralOscuro else EduTeal,
                unfocusedBorderColor = if (enError) EduCoralOscuro else EduBorde,
                disabledBorderColor = EduBorde,
                focusedContainerColor = if (enError) EduCoralClaro else Color.White,
                unfocusedContainerColor = if (enError) EduCoralClaro else EduCampo,
                disabledContainerColor = EduAzulOscuroClaro,
                focusedTextColor = EduTexto,
                unfocusedTextColor = EduTexto,
                disabledTextColor = EduTextoSecundario,
                cursorColor = EduTeal
            )
        )
        if (!mensajeAyuda.isNullOrBlank()) {
            Text(
                text = mensajeAyuda, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                color = if (enError) EduCoralOscuro else EduTextoSecundario
            )
        }
    }
}

// ---------- Chip de estado (Asistió, Tardanza, Faltó, Entregado…) ----------
@Composable
fun ChipEdu(
    texto: String,
    tono: TonoEdu,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    solido: Boolean = false
) {
    val c = coloresDeTono(tono)
    val fondo = if (solido) c.solido else c.fondo
    val colorTexto = when {
        !solido -> c.texto
        tono == TonoEdu.AMARILLO -> EduTexto
        else -> Color.White
    }
    Row(
        modifier = modifier
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 11.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, tint = colorTexto, modifier = Modifier.size(15.dp))
        }
        Text(
            text = texto, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp,
            color = colorTexto, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}

// ---------- Tarjeta blanca ----------
@Composable
fun TarjetaEdu(
    modifier: Modifier = Modifier,
    relleno: PaddingValues = PaddingValues(16.dp),
    colorFondo: Color = Color.White,
    onClick: (() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(22.dp)
    val conSombra = colorFondo == Color.White
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (conSombra) 3.dp else 0.dp, shape = forma,
                ambientColor = EduTexto.copy(alpha = 0.10f), spotColor = EduTexto.copy(alpha = 0.10f)
            )
            .clip(forma)
            .background(colorFondo)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(relleno),
        content = contenido
    )
}

// Título de sección en mayúsculas ("MIS CLASES DE HOY") con acciones opcionales a la derecha
@Composable
fun TituloSeccionEdu(
    texto: String,
    modifier: Modifier = Modifier,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = texto.uppercase(), modifier = Modifier.weight(1f),
            fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 13.5.sp,
            letterSpacing = 0.8.sp, color = EduTextoSecundario
        )
        acciones()
    }
}

// Ícono dentro de un círculo de color (listas, tarjetas, popups)
@Composable
fun IconoCirculoEdu(
    icono: ImageVector,
    tono: TonoEdu,
    modifier: Modifier = Modifier,
    tamano: Dp = 44.dp,
    solido: Boolean = false
) {
    val c = coloresDeTono(tono)
    val tintIcono = when {
        !solido -> c.texto
        tono == TonoEdu.AMARILLO -> EduTexto
        else -> Color.White
    }
    Box(
        modifier = modifier.size(tamano).background(if (solido) c.solido else c.fondo, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = null, tint = tintIcono, modifier = Modifier.size(tamano * 0.5f))
    }
}

// Contador grande con etiqueta (4 Asistió · 1 Tardanza · 1 Faltó)
@Composable
fun ContadorEdu(
    numero: Int,
    etiqueta: String,
    tono: TonoEdu,
    modifier: Modifier = Modifier
) {
    val c = coloresDeTono(tono)
    Column(
        modifier = modifier
            .background(c.fondo, RoundedCornerShape(18.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("$numero", fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 24.sp, color = c.texto)
        Text(
            etiqueta, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 12.5.sp,
            color = c.texto, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}

// Barra de progreso redondeada (ej. "Visto por 18 de 24 padres")
@Composable
fun BarraProgresoEdu(
    progreso: Float,
    modifier: Modifier = Modifier,
    color: Color = EduTeal
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(9.dp)
            .clip(RoundedCornerShape(50))
            .background(EduAzulOscuroClaro)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progreso.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}

// ---------- Barra inferior ----------
data class ItemNavEdu(val etiqueta: String, val icono: ImageVector)

enum class TabDocente { INICIO, ALUMNOS, AVISOS, PERFIL }

private val ItemsNavDocente: List<ItemNavEdu>
    get() = listOf(
        ItemNavEdu("Inicio", Icons.Rounded.Home),
        ItemNavEdu("Alumnos", Icons.Rounded.Groups),
        ItemNavEdu("Avisos", Icons.Rounded.Campaign),
        ItemNavEdu("Perfil", Icons.Rounded.Person)
    )

@Composable
fun BarraInferiorEdu(
    items: List<ItemNavEdu>,
    seleccionado: Int,
    onSeleccionar: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp)
            .background(Color.White)
            .navigationBarsPadding()
            .height(74.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { i, item ->
            val activo = i == seleccionado
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSeleccionar(i) }
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(62.dp)
                        .height(32.dp)
                        .background(if (activo) EduTealClaro else Color.Transparent, RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        item.icono, contentDescription = null,
                        tint = if (activo) EduTealOscuro else EduTextoSecundario,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = item.etiqueta, fontFamily = Nunito,
                    fontWeight = if (activo) FontWeight.Black else FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = if (activo) EduTealOscuro else EduTextoSecundario
                )
            }
        }
    }
}

// Barra inferior del docente: Inicio · Alumnos · Avisos · Perfil
@Composable
fun BarraInferiorDocenteEdu(
    seleccionada: TabDocente,
    onSeleccionar: (TabDocente) -> Unit
) {
    BarraInferiorEdu(
        items = ItemsNavDocente,
        seleccionado = seleccionada.ordinal,
        onSeleccionar = { onSeleccionar(TabDocente.entries[it]) }
    )
}
