package com.example.smartsystem.ui.theme.Screen.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smartsystem.data.ProductViewModel
import com.example.smartsystem.model.DailySummary
import com.example.smartsystem.navigation.MyBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavHostController, productViewModel: ProductViewModel) {
    var selectedTab by remember { mutableStateOf("Week") }
    val dailySummary = remember { mutableStateOf<DailySummary?>(null) }

    LaunchedEffect(Unit) {
        productViewModel.getDailySummary(dailySummary)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { MyBottomBar(navController, 2) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Tab Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Day", "Week", "Month").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .padding(horizontal = 2.dp),
                        onClick = { selectedTab = tab },
                        color = if (isSelected) Color(0xFF0D47A1) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = tab,
                                color = if (isSelected) Color.White else Color.Black,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sales this $selectedTab", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Bar Chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val heights = listOf(0.4f, 0.2f, 0.6f, 0.8f, 0.5f, 0.9f, 0.3f)
                        val days = listOf("M", "T", "W", "T", "F", "S", "S")
                        heights.forEachIndexed { index, h ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .fillMaxHeight(h)
                                        .background(Color(0xFF42A5F5), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                )
                                Text(days[index], fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Top products", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))

            TopProductItem("Milk 500ml", 0.82f, "82%")
            TopProductItem("Sugar 1kg", 0.61f, "61%")
            TopProductItem("Bread loaf", 0.48f, "48%")
        }
    }
}

@Composable
fun TopProductItem(name: String, progress: Float, percentage: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, modifier = Modifier.width(100.dp), fontSize = 14.sp)
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
            color = Color(0xFF2E7D32),
            trackColor = Color(0xFFE0E0E0)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(percentage, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
