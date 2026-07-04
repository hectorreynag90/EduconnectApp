package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.*

data class SeccionComunicadoItem(
    val seccionId: Long,
    val gradoNombre: String,
    val seccionNombre: String,
    val cursoNombre: String,
    val cantidadAlumnos: Int,
    val cantidadComunicados: Int
)

@Composable
fun SeleccionarComunicadoScreen(
    secciones: List<SeccionComunicadoItem> = emptyList(),
    onBack: () -> Unit = {},
    onHomeDocente: () -> Unit = {},
    onAlumnos: () -> Unit = {},
    onAvisos: () -> Unit = {},
    onPerfilDocente: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onSeleccionarSeccion: (seccionId: Long, gradoNombre: String, seccionNombre: String, cursoNombre: String) -> Unit = { _, _, _, _ -> }
) {
    val fechaActual = remember {
        java.text.SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", java.util.Locale("es", "PE"))
            .format(java.util.Date()).replaceFirstChar { it.uppercase() }
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(32.dp)
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
                        text = "Elegir sección",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "Lista de secciones asignado",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = TextWhite.copy(alpha = 0.8f)
                    )
                    Text(
                        text = fechaActual,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = TextWhite.copy(alpha = 0.7f)
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

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = "¿DE QUÉ SECCIÓN DESEAS VER LOS COMUNICADOS?",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(secciones) { item ->
                    SeccionComunicadoCard(
                        item = item,
                        onClick = {
                            onSeleccionarSeccion(
                                item.seccionId,
                                item.gradoNombre,
                                item.seccionNombre,
                                item.cursoNombre
                            )
                        }
                    )
                }
            }
        }

        BottomNavBarDocente(
            onInicio = onHomeDocente,
            onAlumnos = onAlumnos,
            onAvisos = onAvisos,
            onPerfil = onPerfilDocente,
            itemActivo = "Avisos"
        )
    }
}

@Composable
fun SeccionComunicadoCard(
    item: SeccionComunicadoItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite, RoundedCornerShape(20.dp))
            .border(1.5.dp, BorderBlue, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(AccentOrange, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.users_white),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "${item.gradoNombre} Sec. ${item.seccionNombre}",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextBlue
            )
            Text(
                text = "${item.cantidadAlumnos} alumnos - ${item.cursoNombre}",
                fontFamily = Roboto,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .background(EduconnectBlue.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${item.cantidadComunicados} comunicados",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = EduconnectBlue
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.next_gray),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SeleccionarComunicadoPreview() {
    EduConnectAppTheme {
        SeleccionarComunicadoScreen(
            secciones = listOf(
                SeccionComunicadoItem(1L, "2do", "A", "Matemáticas", 28, 12),
                SeccionComunicadoItem(2L, "3er", "B", "Matemáticas", 30, 8),
                SeccionComunicadoItem(3L, "4to", "C", "Matemáticas", 25, 5)
            )
        )
    }
}
