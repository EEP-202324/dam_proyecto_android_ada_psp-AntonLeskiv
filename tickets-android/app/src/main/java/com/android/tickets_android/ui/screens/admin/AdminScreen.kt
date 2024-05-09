package com.android.tickets_android.ui.screens.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

enum class AdminTabItem(val title: String, val icon: ImageVector) {
    Events("Eventos", Icons.Filled.Event),
}

@Composable
fun AdminScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(AdminTabItem.Events) }

    Scaffold(
        bottomBar = {
            BottomNavigation {
                AdminTabItem.values().forEach { item ->
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
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                AdminTabItem.Events -> AdminEventScreen()
            }
        }
    }
}