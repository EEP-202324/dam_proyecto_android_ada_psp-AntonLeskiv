package com.android.tickets_android

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tickets_android.ui.screens.admin.AdminScreen
import com.android.tickets_android.ui.screens.auth.LoginScreen
import com.android.tickets_android.ui.screens.auth.RegisterScreen
import com.android.tickets_android.ui.screens.Screen
import com.android.tickets_android.ui.screens.user.UserScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable(Screen.LOGIN) { LoginScreen(navController) }
        composable(Screen.REGISTER) { RegisterScreen(navController) }
        composable(Screen.USER) { UserScreen(navController) }
        composable(Screen.ADMIN) { AdminScreen(navController) }
    }
}