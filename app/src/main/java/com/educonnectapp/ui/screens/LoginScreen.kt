package com.educonnectapp.ui.screens

import android.R.attr.singleLine
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextWhite
import com.educonnectapp.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderBlue
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextOrange
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.focus.onFocusChanged

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit,
    onGoToRegister: () -> Unit,
    isLoading: Boolean = false
)
{
    var email by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize() // ocupa toda la pantalla
    ) {
        //Header Azul
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduconnectBlue)// fondo azul primario
                .statusBarsPadding()
                .padding(vertical = 20.dp), //
            horizontalAlignment = Alignment.CenterHorizontally, //
            verticalArrangement = Arrangement.Center            //
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            //Logo EduConnect
            Image(
                painter = painterResource(id = R.drawable.logo_educonnect),
                contentDescription = "Logo EduConnect",
                modifier = Modifier.size(100.dp)
            )

            // Nombre Aplicativo
            Text(
                text = "EduConnect",
                fontFamily = Roboto,          // fuente Roboto
                fontWeight = FontWeight.Bold, // negrita
                fontSize = 28.sp,             // tamaño 24
                color = TextWhite             // color blanco
            )

            Spacer(modifier = Modifier.height(6.dp)) // espacio de 6dp entre textos

            // Slogan de la app en cursiva
            Text(
                text = "\"Conectamos docentes y padres en tiempo real\"",
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Italic, // cursiva
                fontSize = 15.sp,
                color = TextWhite
            )
        }

        //Body Blanco
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .background(BackgroundWhite)
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .imePadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = BorderBlue,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "I.E. Pedro Mercedes Ureña - Trujillo",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Bienvenido",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextBlue
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "CORREO:",
                fontFamily = Roboto,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = TextBlue,
                letterSpacing = 1.sp
            )

            // INPUTBOX CORREO
            OutlinedTextField(
                value = email,
                onValueChange = { nuevo -> email = nuevo },
                placeholder = {
                    Text(
                        text = "Ingrese su correo",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = Roboto
                    )
                },
                // ícono de email a la izquierda
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.mail_darkgray),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                            if (emailFocused) EduconnectBlue else TextSecondary
                        )
                    )
                },

                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderLight,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = EduconnectBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { emailFocused = it.isFocused },
                singleLine = true
            )

            Text(
                text = "CONTRASEÑA",
                fontFamily = Roboto,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = TextBlue,
                letterSpacing = 1.sp
            )

            OutlinedTextField(
                value = password,
                onValueChange = { nuevo -> password = nuevo },
                visualTransformation = if (passwordVisible)
                    androidx.compose.ui.text.input.VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                placeholder = {
                    Text(
                        text = "Ingrese contraseña",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = Roboto,
                        modifier = Modifier.fillMaxWidth(),

                    )
                },
                //
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.closed_darkgray),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                            if (passwordFocused) EduconnectBlue else TextSecondary
                        )
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Image(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.eye_darkgray
                                else R.drawable.eyeoff_darkgray
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp),
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                if (passwordFocused) EduconnectBlue else TextSecondary
                            )
                        )
                    }
                },

                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderLight,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = EduconnectBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .onFocusChanged { passwordFocused = it.isFocused },
                singleLine = true
            )
            //OLVIDASTE CONTRASEÑA
            TextButton(
                onClick = onGoToRegister,
                modifier = Modifier.align(Alignment.End)
            )
            {
                Text(
                    text = "¿Olvidaste tu Contraseña?",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = TextBlue
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // BOTÓN INICIAR SESIÓN
            Button(
                onClick = { if (!isLoading) onLogin(email, password) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(25.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOrange,
                    disabledContainerColor = AccentOrange
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = TextWhite,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Text(text = "Iniciando sesión...", fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = TextWhite)
                } else {

                    Image(painter = painterResource(id = R.drawable.login_white),
                        contentDescription = null, modifier = Modifier.size(58.dp))
                    Text(text = "Iniciar sesión", fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = TextWhite)
                }
            }

            TextButton(
                onClick = onGoToRegister,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "¿No tienes cuenta?, Registrate",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = TextOrange,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ///Spacer(modifier = Modifier.height(2.dp))

            TextButton(
                onClick = { /* acción */ },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Al ingresar aceptas nuestros Términos de uso y Política de privacidad",
                    fontFamily = Roboto,
                    fontSize = 14.sp,
                    color = TextBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenReview(){
    EduConnectAppTheme {
        LoginScreen(
            onLogin = {email, password ->},
            onGoToRegister = {}
        )
    }
}