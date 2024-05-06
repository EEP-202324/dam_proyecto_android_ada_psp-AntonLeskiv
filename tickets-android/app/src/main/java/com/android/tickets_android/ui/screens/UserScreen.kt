package com.android.tickets_android.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

enum class UserTabItem(val title: String, val icon: ImageVector) {
    Events("Eventos", Icons.Filled.Event),
    Tickets("Entradas", Icons.Filled.Difference)
}

@Composable
fun UserScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(UserTabItem.Events) }

    Scaffold(
        bottomBar = {
            BottomNavigation {
                UserTabItem.values().forEach { item ->
                    BottomNavigationItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = item == selectedTab,
                        onClick = { selectedTab = item }
                    )
                }
            }
        }
    ) { innerPadding ->
        // El contenido principal de la pantalla se coloca aquí
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                UserTabItem.Events -> UserEventsScreen()
                UserTabItem.Tickets -> UserTicketsScreen()
            }
        }
    }
}

@Composable
fun UserEventsScreen() {
    // Contenido de la pantalla Eventos
    Text("Pantalla de Eventos de Usuario")
}

@Composable
fun UserTicketsScreen() {
    // Contenido de la pantalla Entradas
    Text("Pantalla de Entradas de Usuario")
}