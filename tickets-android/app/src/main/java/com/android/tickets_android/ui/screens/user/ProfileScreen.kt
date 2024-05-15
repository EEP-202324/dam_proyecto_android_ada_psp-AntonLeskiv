package com.android.tickets_android.ui.screens.user

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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.android.tickets_android.R
import com.android.tickets_android.api.UserService
import com.android.tickets_android.model.User
import com.android.tickets_android.model.UserManager
import com.android.tickets_android.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UserProfileScreen() {
    val userService = RetrofitClient.instance.create(UserService::class.java)
    var userFirstName by remember { mutableStateOf("") }
    var userLastName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }


    fun loadProfile(userId: Long) {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userService.getUserById(userId).execute()
                Log.i("UserProfileScreen", "Response: ${response.body()}")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!
                        userFirstName = user.firstName ?: ""
                        userLastName = user.lastName ?: ""
                        userEmail = user.email ?: ""
                    } else {
                        errorMessage = response.message()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = e.message
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadProfile(UserManager.userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Perfil de Usuario", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userFirstName,
            onValueChange = { userFirstName = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.dark_blue),
                focusedLabelColor = colorResource(id = R.color.dark_blue),
                unfocusedLabelColor = Color.Gray,
                unfocusedBorderColor = colorResource(id = R.color.blue),
                cursorColor = colorResource(id = R.color.blue)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = userLastName,
            onValueChange = { userLastName = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.dark_blue),
                focusedLabelColor = colorResource(id = R.color.dark_blue),
                unfocusedLabelColor = Color.Gray,
                unfocusedBorderColor = colorResource(id = R.color.blue),
                cursorColor = colorResource(id = R.color.blue)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.dark_blue),
                focusedLabelColor = colorResource(id = R.color.dark_blue),
                unfocusedLabelColor = Color.Gray,
                unfocusedBorderColor = colorResource(id = R.color.blue),
                cursorColor = colorResource(id = R.color.blue)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val updateResponse = userService.updateUser(
                            UserManager.userId,
                            User(
                                firstName = userFirstName,
                                lastName = userLastName,
                                email = userEmail,
                            )
                        ).execute()
                        withContext(Dispatchers.Main) {
                            if (updateResponse.isSuccessful) {
                                successMessage = "Perfil actualizado con éxito"
                                errorMessage = null
                            } else {
                                errorMessage = updateResponse.message()
                                successMessage = null
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            errorMessage = e.message
                            successMessage = null
                        }
                    } finally {
                        withContext(Dispatchers.Main) {
                            isLoading = false
                        }
                    }
                }
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.blue)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier)
            } else {
                Text("Actualizar")
            }
        }

        if (successMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = successMessage ?: "",
                color = Color.Green,
                style = MaterialTheme.typography.body2
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

