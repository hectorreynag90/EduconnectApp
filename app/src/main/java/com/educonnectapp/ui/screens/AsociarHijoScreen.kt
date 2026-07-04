package com.educonnectapp.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundRedLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.BorderRed
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.StatusGreen
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextRed
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BackgroundGreenLight3 = Color(0xFFEAF3DE)
private val BackgroundOrangeLight4 = Color(0xFFFFF0ED)
private val GreenText3 = Color(0xFF3B6D11)
private val BlueLight3 = Color(0xFFE6F1FB)

// Data class para resultado de búsqueda
data class AlumnoEncontrado(
    val id: Long,
    val nombres: String,
    val apellidos: String,
    val gradoNombre: String,
    val seccionNombre: String,
    val codigoEstudiante: String
)

@Composable
fun AsociarHijoScreen(
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAgenda: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfil: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onBuscar: (codigo: String) -> Unit = {},
    alumnoEncontrado: AlumnoEncontrado? = null,
    mensajeError: String = "",
    onConfirmar: (AlumnoEncontrado) -> Unit = {},
    onCancelar: () -> Unit = {},
) {
    var codigo by remember { mutableStateOf("") }

    val iniciales = alumnoEncontrado?.let {
        "${it.nombres.firstOrNull() ?: ""}${it.apellidos.firstOrNull() ?: ""}".uppercase()
    } ?: ""

    // obtiene la fecha actual del dispositivo en español
    val fechaActual = remember {
        val sdf = SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
        sdf.format(Date())
            .replaceFirstChar { it.uppercase() } // pone la primera letra en mayúscula
    }

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
                    modifier = Modifier
                        .size(35.dp)
                        .offset(x = (-5).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Asociar Hijo",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "Ingresa el código del estudiante",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.85f)
                    )
                    Text(
                        text = fechaActual, // fecha actual
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = TextWhite
                    )
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

        Spacer(modifier = Modifier.height(15.dp))

        // CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // CAMPO CÓDIGO
            Text(
                text = "CÓDIGO DE ESTUDIANTE",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = EduconnectBlue,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundWhite, RoundedCornerShape(14.dp))
                    .border(0.5.dp, BorderBlue, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Ingresa el código brindado por la institución",
                    fontFamily = Roboto, fontSize = 14.sp, color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it.uppercase() },
                    placeholder = {
                        Text(
                            text = "Ej. PMU0001", color = TextSecondary,
                            fontSize = 16.sp, fontFamily = Roboto
                        )
                    },

                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.key_student_blue),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EduconnectBlue,
                        unfocusedBorderColor = BorderLight,
                        focusedTextColor = TextBlue,
                        unfocusedTextColor = TextBlue,
                        cursorColor = EduconnectBlue,
                        focusedContainerColor = BlueLight3,
                        unfocusedContainerColor = BlueLight3
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // BOTÓN BUSCAR
            Button(
                onClick = { if (codigo.isNotBlank()) onBuscar(codigo) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (codigo.isNotBlank()) EduconnectBlue else BorderLight
                ),
                enabled = codigo.isNotBlank()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search_white),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Buscar Código",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (codigo.isNotBlank()) TextWhite else TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ERROR
            if (mensajeError.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFCEBEB), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFE53935), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.close_red),
                        contentDescription = null, modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = mensajeError, fontFamily = Roboto,
                        fontSize = 13.sp, color = Color(0xFFE53935)
                    )
                }
            }

            // RESULTADO ENCONTRADO
            if (alumnoEncontrado != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ESTUDIANTE ENCONTRADO",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = EduconnectBlue,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                // Card resultado
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundWhite, RoundedCornerShape(14.dp))
                        .border(2.dp, EduconnectBlue, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(BlueLight3),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = iniciales, fontFamily = Roboto,
                                fontWeight = FontWeight.Bold, fontSize = 22.sp,
                                color = EduconnectBlue
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${alumnoEncontrado.nombres} ${alumnoEncontrado.apellidos}",
                                fontFamily = Roboto, fontWeight = FontWeight.Bold,
                                fontSize = 17.sp, color = EduconnectBlue
                            )
                            Text(
                                text = "${alumnoEncontrado.gradoNombre} · Sec. ${alumnoEncontrado.seccionNombre}",
                                fontFamily = Roboto, fontSize = 15.sp, color = TextPrimary
                            )
                            Text(
                                text = "Código: ${alumnoEncontrado.codigoEstudiante}",
                                fontFamily = Roboto, fontSize = 15.sp, color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(BackgroundGreenLight3, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "✓ Estudiante verificado",
                                    fontFamily = Roboto, fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp, color = GreenText3
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Confirmación
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundGreenLight3, RoundedCornerShape(10.dp))
                        .border(1.dp, StatusGreen, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.check_green),
                        contentDescription = null, modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "¿Confirmas asociar a ${alumnoEncontrado.nombres} ${alumnoEncontrado.apellidos} a tu cargo?",
                        fontFamily = Roboto, fontSize = 16.sp,
                        color = GreenText3, lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // BOTÓN CONFIRMAR
                Button(
                    onClick = { onConfirmar(alumnoEncontrado) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.useradd_white),
                        contentDescription = null, modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirmar y asociar", fontFamily = Roboto,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(1.dp))

            // BOTÓN CANCELAR
            Button(
                onClick = onCancelar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundRedLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderRed)
            ) {
                Text(
                    text = "Cancelar", fontFamily = Roboto,
                    fontWeight = FontWeight.Normal, fontSize = 20.sp, color = TextRed
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
        //NAVBAR INFERIOR
        BottomNavBarPadre(
            onInicio = onHomeDocente,
            onAvisos = onAvisos,
            onAgenda = onAgenda,
            onPerfil = onPerfil,
            itemActivo = "Perfil",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AsociarHijoPreview() {
    EduConnectAppTheme {
        AsociarHijoScreen(
            alumnoEncontrado = AlumnoEncontrado(
                id = 1L,
                nombres = "Pedro",
                apellidos = "Sanchez Vega",
                gradoNombre = "1er Grado",
                seccionNombre = "A",
                codigoEstudiante = "PMU0001"
            ),
        )
    }
}