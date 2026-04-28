package com.example.smartsystem.data

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.smartsystem.navigation.ROUTE_DASHBOARD
import com.example.smartsystem.navigation.ROUTE_LOGIN

class AuthViewModel : ViewModel() {
    fun signup(username:String, email:String, phoneNumber:String, password:String, navController:NavController, context: Context) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        // Add your registration logic here (e.g., Firebase)
        navController.navigate(ROUTE_DASHBOARD)
    }

    fun login(email:String, password:String, navController:NavController, context: Context) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        // Add your login logic here
        navController.navigate(ROUTE_DASHBOARD)
    }

    fun logout(navController:NavController) {
        // Add your logout logic here
        navController.navigate(ROUTE_LOGIN)
    }
}
