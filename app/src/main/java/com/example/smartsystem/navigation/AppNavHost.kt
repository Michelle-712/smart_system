package com.example.smartsystem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartsystem.ui.theme.Screen.dashboard.DashboardScreen
import com.example.smartsystem.ui.theme.Screen.login.LoginScreen
import com.example.smartsystem.ui.theme.Screen.register.RegisterScreen
import androidx.compose.material3.Text

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(),
               startDestination: String = ROUTE_LOGIN) {

    NavHost(navController = navController, startDestination = startDestination) {
        composable(ROUTE_REGISTER) { RegisterScreen(navController) }
        composable(ROUTE_LOGIN) { LoginScreen(navController) }
        composable(ROUTE_DASHBOARD) { DashboardScreen(navController) }
        
        // Placeholder composables for new routes
        composable(ROUTE_SALES) { Text("Sales Screen") }
        composable(ROUTE_REPORTS) { Text("Reports Screen") }
        composable(ROUTE_STOCK) { Text("Stock Screen") }
        composable(ROUTE_ALERTS) { Text("Alerts Screen") }
    }
}
