package com.example.smartsystem.data

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.smartsystem.model.UserModel
import com.example.smartsystem.navigation.ROUTE_DASHBOARD
import com.example.smartsystem.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    fun signup(username:String, email:String, phoneNumber:String, password:String, navController:NavController, context: Context) {
        if (email.isBlank() || password.isBlank() || username.isBlank() || phoneNumber.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: ""
                val user = UserModel(username, email, phoneNumber, userId)
                
                database.getReference("Users").child(userId).setValue(user).addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                        navController.navigate(ROUTE_DASHBOARD) {
                            popUpTo(0)
                        }
                    } else {
                        Toast.makeText(context, "Database Error: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login(email:String, password:String, navController:NavController, context: Context) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                navController.navigate(ROUTE_DASHBOARD) {
                    popUpTo(0)
                }
            } else {
                Toast.makeText(context, "Login Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun logout(navController:NavController) {
        auth.signOut()
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(0)
        }
    }
}
