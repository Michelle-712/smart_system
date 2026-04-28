package com.example.smartsystem.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun MyBottomBar(navController: NavHostController, selectedIndex: Int) {
    NavigationBar(containerColor = Color.Cyan) {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { navController.navigate(ROUTE_DASHBOARD) },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { navController.navigate(ROUTE_SALES) },
            icon = { Icon(Icons.Default.ShoppingCart, null) },
            label = { Text("Sales") }
        )
        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { navController.navigate(ROUTE_REPORTS) },
            icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
            label = { Text("Reports") }
        )
        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = { navController.navigate(ROUTE_STOCK) },
            icon = { Icon(Icons.Default.Info, null) },
            label = { Text("Stock") }
        )
        NavigationBarItem(
            selected = selectedIndex == 4,
            onClick = { navController.navigate(ROUTE_ALERTS) },
            icon = { Icon(Icons.Default.Warning, null) },
            label = { Text("Alerts") }
        )
    }
}
