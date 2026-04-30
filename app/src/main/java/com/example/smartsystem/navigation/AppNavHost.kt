package com.example.smartsystem.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartsystem.data.ProductViewModel
import com.example.smartsystem.ui.theme.Screen.dashboard.DashboardScreen
import com.example.smartsystem.ui.theme.Screen.inventory.AddProductScreen
import com.example.smartsystem.ui.theme.Screen.inventory.InventoryScreen
import com.example.smartsystem.ui.theme.Screen.login.LoginScreen
import com.example.smartsystem.ui.theme.Screen.register.RegisterScreen
import com.example.smartsystem.ui.theme.Screen.sales.SalesScreen
import com.example.smartsystem.ui.theme.Screen.sales.SellProductScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(),
               startDestination: String = ROUTE_LOGIN) {

    // Instantiate the ViewModel here so it's shared across all destinations in this NavHost
    val productViewModel: ProductViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(ROUTE_REGISTER) { RegisterScreen(navController) }
        composable(ROUTE_LOGIN) { LoginScreen(navController) }
        composable(ROUTE_DASHBOARD) { DashboardScreen(navController) }
        
        composable(ROUTE_SALES) { 
            SalesScreen(navController, productViewModel) 
        }
        
        composable(ROUTE_SELL) { 
            SellProductScreen(navController, productViewModel) 
        }

        composable(ROUTE_REPORTS) { /* TODO */ }
        composable(ROUTE_STOCK) { 
            InventoryScreen(navController, productViewModel) 
        }
        composable(ROUTE_ALERTS) { /* TODO */ }
        
        composable(ROUTE_ADD_PRODUCT) {
            AddProductScreen(navController, productViewModel)
        }
    }
}
