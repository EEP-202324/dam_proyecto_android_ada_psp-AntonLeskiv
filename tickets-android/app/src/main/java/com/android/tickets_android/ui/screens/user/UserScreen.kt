package com.android.tickets_android.ui.screens.user

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavHostController
import com.android.tickets_android.R

enum class UserTabItem(val title: String, val icon: ImageVector) {
    Events("Eventos", Icons.Filled.Event),
    Tickets("Entradas", Icons.Filled.Difference),
    Profile("Perfil", Icons.Filled.Person)
}

@Composable
fun UserScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(UserTabItem.Events) }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = colorResource(id = R.color.light_blue)
            ) {
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
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                UserTabItem.Events -> UserEventScreen()
                UserTabItem.Tickets -> UserTicketScreen()
                UserTabItem.Profile -> UserProfileScreen()
            }
        }
    }
}