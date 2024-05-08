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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController) {
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
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.h1,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
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
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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
                performLogin(email, password, navController)
            },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text(
                stringResource(R.string.login_button),
                style = TextStyle(fontSize = 18.sp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(
            onClick = { navController.navigate(Screen.REGISTER) }
        ) {
            Text(
                text = stringResource(R.string.go_to_register),
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

// Función para hacer el inicio de sesión
fun performLogin(email: String, password: String, navController: NavController) {
    val authService = RetrofitClient.instance.create(AuthenticationService::class.java)
    val credentials = mapOf("email" to email, "password" to password)
    authService.login(credentials).enqueue(object : Callback<AuthenticationResponse> {

        override fun onResponse(
            call: Call<AuthenticationResponse>,
            response: Response<AuthenticationResponse>
        ) {
            if (response.isSuccessful && response.body()?.success == true) {
                Log.i("Login", "Inicio de sesión exitoso: ${response.body()}")
                var role = response.body()?.role
                var userId = response.body()?.userId
                if (userId != null) {
                    UserManager.userId = userId
                }
                Log.i("Register", "id: $userId, role: $role")
                if (role == "ADMIN") {
                    navController.navigate(Screen.ADMIN)
                } else if (role == "USER") {
                    navController.navigate(Screen.USER)
                }
            } else {
                Log.i("Login", "Inicio de sesión fallido: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
            Log.e("Login", "Error en la red o el servidor: ${t.message}")
        }
    })
}