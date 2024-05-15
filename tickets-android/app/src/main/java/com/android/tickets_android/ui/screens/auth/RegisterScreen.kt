package com.android.tickets_android.ui.screens.auth


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.tickets_android.R
import com.android.tickets_android.api.AuthenticationService
import com.android.tickets_android.model.AuthenticationResponse
import com.android.tickets_android.model.UserManager
import com.android.tickets_android.network.RetrofitClient
import com.android.tickets_android.ui.screens.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegisterScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    Text(
        text = "IFEMA TICKETS",
        fontSize = 50.sp,
        modifier = Modifier
            .padding(horizontal = 40.dp)
            .padding(top = 80.dp)
            .fillMaxWidth(),
        style = MaterialTheme.typography.body2,
        textAlign = TextAlign.Center
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.register),
            style = MaterialTheme.typography.h1,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text(stringResource(R.string.name)) },
            leadingIcon = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = stringResource(R.string.name)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.dark_blue),
                focusedLabelColor = colorResource(id = R.color.dark_blue),
                unfocusedLabelColor = Color.Gray,
                unfocusedBorderColor = colorResource(id = R.color.blue),
                cursorColor = colorResource(id = R.color.blue)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text(stringResource(R.string.lastname)) },
            leadingIcon = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = stringResource(R.string.lastname)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.dark_blue),
                focusedLabelColor = colorResource(id = R.color.dark_blue),
                unfocusedLabelColor = Color.Gray,
                unfocusedBorderColor = colorResource(id = R.color.blue),
                cursorColor = colorResource(id = R.color.blue)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            leadingIcon = {
                Icon(
                    Icons.Filled.Email,
                    contentDescription = stringResource(R.string.email)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.dark_blue),
                focusedLabelColor = colorResource(id = R.color.dark_blue),
                unfocusedLabelColor = Color.Gray,
                unfocusedBorderColor = colorResource(id = R.color.blue),
                cursorColor = colorResource(id = R.color.blue)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            leadingIcon = {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = stringResource(R.string.password)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.dark_blue),
                focusedLabelColor = colorResource(id = R.color.dark_blue),
                unfocusedLabelColor = Color.Gray,
                unfocusedBorderColor = colorResource(id = R.color.blue),
                cursorColor = colorResource(id = R.color.blue)
            ),
            trailingIcon = {
                val image = if (passwordVisibility)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        imageVector = image,
                        contentDescription = stringResource(R.string.hide_or_show_password)
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                performRegister(firstName, lastName, email, password, navController)
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.blue)
            ),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text(
                stringResource(R.string.register_button),
                style = TextStyle(fontSize = 18.sp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(
            onClick = { navController.navigate(Screen.LOGIN) }
        ) {
            Text(
                text = stringResource(R.string.go_to_login),
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

// Función para registrar un usuario
fun performRegister(
    firstName: String,
    lastName: String,
    email: String,
    password: String,
    navController: NavController
) {
    val authService = RetrofitClient.instance.create(AuthenticationService::class.java)
    val userData = mapOf(
        "firstName" to firstName,
        "lastName" to lastName,
        "email" to email,
        "password" to password
    )
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = authService.register(userData).execute()

            // Cambiar al contexto principal para manejar la UI
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Log.i("Login", "Inicio de sesión exitoso: ${response.body()}")

                    val role = response.body()?.role
                    val userId = response.body()?.userId
                    if (userId != null) {
                        UserManager.userId = userId
                    }
                    Log.i("Register", "id: $userId, role: $role")

                    when (role) {
                        "ADMIN" -> navController.navigate(Screen.ADMIN)
                        "USER" -> navController.navigate(Screen.USER)
                        else -> Log.i("Login", "Rol desconocido: $role")
                    }
                } else {
                    Log.i("Login", "Inicio de sesión fallido: ${response.errorBody()?.string()}")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.e("Login", "Error en la red o el servidor: ${e.message}")
            }
        }
    }
}
