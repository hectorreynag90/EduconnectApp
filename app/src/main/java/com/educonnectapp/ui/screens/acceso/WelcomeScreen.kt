package com.educonnectapp.ui.screens.acceso

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.components.BotonPrincipalEdu
import com.educonnectapp.ui.components.ChipCabeceraEdu
import com.educonnectapp.ui.components.TipoBotonEdu
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduTeal
import com.educonnectapp.ui.theme.EduTealCirculo1
import com.educonnectapp.ui.theme.EduTealCirculo2
import com.educonnectapp.ui.theme.EduTealTextoCabecera
import com.educonnectapp.ui.theme.EduTextoSecundario
import com.educonnectapp.ui.theme.Nunito

// Pantalla de bienvenida (rediseño 2026): fondo turquesa con logo y hoja blanca con los botones
@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EduTeal)
            .clipToBounds()
    ) {
        // Círculos decorativos de fondo
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 170.dp, y = (-150).dp)
                .size(420.dp)
                .background(EduTealCirculo1, CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-170).dp, y = 60.dp)
                .size(300.dp)
                .background(EduTealCirculo2, CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Logo, nombre y lema
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(Color.White, RoundedCornerShape(42.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_educonnect),
                        contentDescription = "Logo EduConnect",
                        modifier = Modifier.size(108.dp)
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                Text(
                    text = "EduConnect",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Black,
                    fontSize = 40.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Conectamos docentes y padres de familia en tiempo real",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    lineHeight = 24.sp,
                    color = EduTealTextoCabecera,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipCabeceraEdu("Asistencia")
                    ChipCabeceraEdu("Comunicados")
                    ChipCabeceraEdu("Tareas")
                }
            }

            // Hoja blanca inferior con los botones
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .navigationBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BotonPrincipalEdu(
                    texto = "Iniciar sesión",
                    onClick = onLoginClick,
                    tipo = TipoBotonEdu.AVANZAR,
                    icono = Icons.AutoMirrored.Rounded.Login
                )
                BotonPrincipalEdu(
                    texto = "Crear cuenta",
                    onClick = onRegisterClick,
                    tipo = TipoBotonEdu.GUARDAR,
                    icono = Icons.Rounded.PersonAdd
                )
                /*Text(
                    text = "",
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = EduTextoSecundario,
                    textAlign = TextAlign.Center
                )*/
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun WelcomeScreenPreview() {
    EduConnectAppTheme {
        WelcomeScreen()
    }
}
