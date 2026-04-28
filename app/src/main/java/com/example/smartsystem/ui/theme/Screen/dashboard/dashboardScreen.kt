package com.example.smartsystem.ui.theme.Screen.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.smartsystem.data.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController){
    val authViewModel: AuthViewModel = viewModel()
    var selectedItem by remember { mutableIntStateOf(0)}
    Scaffold (
        topBar = { TopAppBar(
            title = { Text(
                text = "Welcome Admin",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold)},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Cyan,
                titleContentColor = Color.Black,),
            actions = {
                IconButton(onClick = {
                    authViewModel.logout(navController)
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                }
            }
        ) },
        bottomBar = { NavigationBar(containerColor = Color.Cyan) {
            NavigationBarItem(
                selected = selectedItem == 0,
                onClick = { selectedItem = 0 },
                icon = { Icon(Icons.Default.Home, null) },
                label = { Text("Home") }
            )
            NavigationBarItem(
                selected = selectedItem == 1,
                onClick = { selectedItem = 1 },
                icon = { Icon(Icons.Default.Settings, null) },
                label = { Text("Settings") }
            )

            NavigationBarItem(
                selected = selectedItem == 2,
                onClick = { selectedItem = 2 },
                icon = { Icon(Icons.Default.Person, null) },
                label = { Text("Profile") }
            )
        } }
    ) { innerPadding ->
        Text(text = "Dashboard Content", modifier = Modifier.padding(innerPadding))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview(){
    DashboardScreen(rememberNavController())
}
