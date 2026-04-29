package com.example.smartsystem.ui.theme.Screen.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.smartsystem.data.AuthViewModel
import com.example.smartsystem.data.ProductViewModel
import com.example.smartsystem.model.DailySummary
import com.example.smartsystem.navigation.MyBottomBar
import com.example.smartsystem.navigation.ROUTE_SELL


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController){
    val authViewModel: AuthViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    val dailySummary = remember { mutableStateOf<DailySummary?>(null) }

    LaunchedEffect(Unit) {
        productViewModel.getDailySummary(dailySummary)
    }

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
        bottomBar = { MyBottomBar(navController, 0) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            StatCard(
                title = "Today's Sales",
                value = "Ksh ${dailySummary.value?.totalSales ?: 0.0}"
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatCard(
                title = "Items Sold Today",
                value = "${dailySummary.value?.totalItemsSold ?: 0}"
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatCard(title = "Low Stock Items", value = "0", color = Color.Red)

            Spacer(modifier = Modifier.height(24.dp))


            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActionCard(title = "Sell Products", modifier = Modifier.weight(1f)) {
                    navController.navigate(ROUTE_SELL)
                }
                ActionCard(title = "Inventory", modifier = Modifier.weight(1f)) {
                    // TODO: Navigate to Inventory
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActionCard(title = "Reports", modifier = Modifier.weight(1f)) {
                    // TODO: Navigate to Reports
                }
                ActionCard(title = "Prediction", modifier = Modifier.weight(1f)) {
                    // TODO: Navigate to Prediction
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color = Color.Black) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun ActionCard(title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Cyan.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview(){
    DashboardScreen(rememberNavController())
}
