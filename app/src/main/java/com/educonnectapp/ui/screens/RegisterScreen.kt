package com.educonnectapp.ui.screens

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educonnectapp.R
import com.educonnectapp.ui.theme.AccentOrange
import com.educonnectapp.ui.theme.BackgroundLight
import com.educonnectapp.ui.theme.BackgroundWhite
import com.educonnectapp.ui.theme.BorderLight
import com.educonnectapp.ui.theme.BorderMedium
import com.educonnectapp.ui.theme.EduConnectAppTheme
import com.educonnectapp.ui.theme.EduconnectBlue
import com.educonnectapp.ui.theme.EduconnectBlueMedium
import com.educonnectapp.ui.theme.Roboto
import com.educonnectapp.ui.theme.TextBlue
import com.educonnectapp.ui.theme.TextPrimary
import com.educonnectapp.ui.theme.TextSecondary
import com.educonnectapp.ui.theme.TextWhite

@Composable
fun RegisterScreen(
    onRegister: (nombre: String, dni: String, telefono: String, email: String, password: String, rol: String) -> Unit,
    onBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf("Docente") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // HEADER AZUL
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduconnectBlue)
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(start = 2.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically)
            {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(35.dp).offset(x = (10).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrowleft_white),
                        contentDescription = "Volver",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Box(modifier = Modifier.size(60.dp).background(
                color = EduconnectBlueMedium, shape = RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Filled.PersonAdd, contentDescription = null,
                    tint = TextWhite, modifier = Modifier.size(45.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Crear cuenta",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextWhite)

            Text(text = "$rolSeleccionado - EduConnect",
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = TextWhite)

            Spacer(modifier = Modifier.height(6.dp))
        }

        // BODY BLANCO
        Column(
            modifier = Modifier.fillMaxWidth().background(BackgroundWhite)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // SELECTOR ROL
            Row(modifier = Modifier.fillMaxWidth().padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(
                    onClick = { rolSeleccionado = "Docente" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (rolSeleccionado == "Docente") EduconnectBlue else BorderLight
                    )
                ) {
                    Text(text = "Docente",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = if (rolSeleccionado == "Docente") TextWhite else TextSecondary)
                }
                Button(
                    onClick = { rolSeleccionado = "Padre" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (rolSeleccionado == "Padre") EduconnectBlue else BorderLight
                    )
                ) {
                    Text(text = "Padre",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = if (rolSeleccionado == "Padre") TextWhite else TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // NOMBRE
            Text(text = "NOMBRE COMPLETO",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextBlue,
                letterSpacing = 1.sp
            )
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                placeholder = {
                    Text(
                        text = "Ingrese nombre completo",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = Roboto
                    ) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.user_circle_darkgray),
                    contentDescription = null, modifier = Modifier.size(25.dp)
                    )},
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderMedium,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = EduconnectBlue),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                singleLine = true
            )

            //DNI Y TELÉFONO
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                //DNI
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "DNI", fontFamily = Roboto, fontWeight = FontWeight.Bold,
                        fontSize = 13.sp, color = TextBlue, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = dni,
                        onValueChange = { if (it.length <= 8) dni = it },
                        placeholder = {
                            Text(
                                text = "00000000",
                                color = TextSecondary,
                                fontSize = 14.sp, fontFamily = Roboto)},
                        leadingIcon = {
                                Image(painter = painterResource(id = R.drawable.dni_darkgray),
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp))
                                      },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EduconnectBlue,
                            unfocusedBorderColor = BorderMedium,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = EduconnectBlue),
                        modifier = Modifier.fillMaxWidth().height(50.dp), singleLine = true
                    )
                }

                // TELÉFONO
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TELÉFONO",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextBlue,
                        letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp)
                    )
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { if (it.length <= 9) telefono = it },
                        placeholder = {
                            Text(
                                text = "999999999",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontFamily = Roboto)},
                        leadingIcon = {
                            Image(painter = painterResource(id = R.drawable.phone_darkgray),
                            contentDescription = null, modifier = Modifier.size(22.dp))},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EduconnectBlue,
                            unfocusedBorderColor = BorderMedium,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = EduconnectBlue),
                        modifier = Modifier.fillMaxWidth().height(50.dp), singleLine = true
                    )
                }
            }

            // CORREO
            Text(
                text = "CORREO ELECTRÓNICO",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextBlue,
                letterSpacing = 1.sp)
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                placeholder = {
                    Text(text = "Ingrese correo",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = Roboto) },
                leadingIcon = {
                    Image(painter = painterResource(id = R.drawable.mail_darkgray),
                    contentDescription = null, modifier = Modifier.size(25.dp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderMedium,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = EduconnectBlue),
                modifier = Modifier.fillMaxWidth().height(50.dp), singleLine = true
            )

            // CONTRASEÑA
            Text(
                text = "CONTRASEÑA",
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextBlue,
                letterSpacing = 1.sp)
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                placeholder = {
                    Text(
                        text = "Mínimo 8 caracteres",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = Roboto)},
                leadingIcon = {
                    Image(painter = painterResource(id = R.drawable.closed_darkgray),
                    contentDescription = null, modifier = Modifier.size(25.dp)) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Image(painter = painterResource(
                            id = if (passwordVisible) R.drawable.eye_darkgray
                            else R.drawable.eyeoff_darkgray),
                            contentDescription = null, modifier = Modifier.size(25.dp))
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduconnectBlue,
                    unfocusedBorderColor = BorderMedium,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = EduconnectBlue),
                modifier = Modifier.fillMaxWidth().height(50.dp), singleLine = true
            )

            // AVISO SEGÚN ROL
            Row(modifier = Modifier.fillMaxWidth()
                .background(color = BackgroundLight, shape = RoundedCornerShape(10.dp))
                .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.Top) {
                Image(painter = painterResource(id = R.drawable.alerta_darkgray),
                    contentDescription = null, modifier = Modifier.size(20.dp))
                Text(
                    text = if (rolSeleccionado == "Docente")
                        "Podrás asignar grado, sección y curso desde tu perfil."
                    else
                        "Podrás asociar a tus hijos desde tu perfil.",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    lineHeight = 15.sp
                )
            }

            // BOTÓN CREAR CUENTA
            Button(
                onClick = { onRegister(nombre, dni, telefono, email, password, rolSeleccionado) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
            ) {
                Icon(
                    imageVector = Icons.Filled.PersonAdd, contentDescription = null,
                    tint = TextWhite, modifier = Modifier.size(28.dp))
                Spacer(
                    modifier = Modifier.size(8.dp))
                Text(
                    text = "Crear Cuenta",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = TextWhite)
            }

            // ¿YA TIENES CUENTA?
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    fontFamily = Roboto,
                    fontSize = 15.sp,
                    color = TextSecondary
                )
                TextButton(onClick = onBack,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                    Text(
                        text = "Inicia sesión",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = TextBlue
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    EduConnectAppTheme {
        RegisterScreen(
            onRegister = { _, _, _, _, _, _ -> },
            onBack = {}
        )
    }
}

