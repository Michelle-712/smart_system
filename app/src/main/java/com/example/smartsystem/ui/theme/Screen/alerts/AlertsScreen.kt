package com.example.smartsystem.ui.theme.Screen.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.smartsystem.data.ProductViewModel
import com.example.smartsystem.model.StockAlert
import com.example.smartsystem.model.StockPrediction
import com.example.smartsystem.navigation.MyBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(navController: NavHostController, productViewModel: ProductViewModel) {
    val predictions = remember { mutableStateListOf<StockPrediction>() }
    val alerts = remember { mutableStateListOf<StockAlert>() }

    LaunchedEffect(Unit) {
        productViewModel.getStockPredictions(predictions)
        productViewModel.getAlerts(alerts)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stock Alerts & Predictions", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { MyBottomBar(navController, 4) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(16.dp)
        ) {
            LazyColumn {
                if (alerts.isNotEmpty()) {
                    item {
                        Text("Immediate Attention", fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(alerts) { alert ->
                        StockAlertItem(alert)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                item {
                    Text("Inventory Run-out Predictions", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(predictions) { pred ->
                    PredictionItem(pred)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun StockAlertItem(alert: StockAlert) {
    val color = if (alert.severity == "Critical") Color(0xFFD32F2F) else Color(0xFFEF6C00)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(alert.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "${alert.severity.uppercase()} STOCK",
                    color = color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${alert.currentStock} left",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PredictionItem(pred: StockPrediction) {
    val progress = (pred.daysRemaining / 10f).coerceIn(0f, 1f)
    val color = when {
        pred.daysRemaining <= 2 -> Color(0xFFD32F2F) // Critical
        pred.daysRemaining <= 5 -> Color(0xFFEF6C00) // Low
        else -> Color(0xFF2E7D32) // OK
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(pred.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "${pred.daysRemaining} days left",
                    color = color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = color,
                trackColor = Color(0xFFE0E0E0),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}
