package com.educonnectapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.AccentOrangeLight
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextOrange
import com.educonnectapp.ui.theme.TextWhite

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        EduconnectBlue,
                        EduconnectBlue.copy(alpha = 0.85f)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        //LOGO
        Image(
            painter = painterResource(id = R.drawable.logo_educonnect),
            contentDescription = "Logo EduConnect",
            modifier = Modifier.size(170.dp)
        )

        Spacer(modifier = Modifier.height(44.dp))

        // NOMBRE
        Text(
            text = "EduConnect",
            fontFamily = Roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 45.sp,
            color = TextWhite
        )

        Spacer(modifier = Modifier.height(10.dp))

        //SUBTÍTULO
        Text(
            text = "\"Conectamos docentes y\npadres de familia en tiempo real\"",
            fontFamily = Roboto,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Italic,
            fontSize = 22.sp,
            color = TextWhite.copy(alpha = 0.90f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(160.dp))

        // BOTÓN BIENVENIDO
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .height(70.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentOrange
            )
        ) {
            Text(
                text = "Bienvenido",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 35.sp,
                color = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onRegisterClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "¿No tienes cuenta?  ",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextWhite
            )
            Text(
                text = "Regístrate",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = AccentOrangeLight
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    EduConnectAppTheme {
        WelcomeScreen()
    }
}


