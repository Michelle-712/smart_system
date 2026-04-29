package com.example.smartsystem.ui.theme.Screen.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.smartsystem.data.ProductViewModel
import com.example.smartsystem.model.Product
import com.example.smartsystem.navigation.MyBottomBar
import com.example.smartsystem.navigation.ROUTE_SELL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(navController: NavHostController, productViewModel: ProductViewModel) {
    val context = LocalContext.current
    val products = remember { mutableStateListOf<Product>() }
    
    var showRestockDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var restockQuantity by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        productViewModel.getProducts(products)
    }

    if (showRestockDialog && selectedProduct != null) {
        AlertDialog(
            onDismissRequest = { 
                showRestockDialog = false 
                selectedProduct = null
                restockQuantity = ""
            },
            title = { Text(text = "Restock ${selectedProduct?.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "Current Stock: ${selectedProduct?.quantity} units", color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = restockQuantity,
                        onValueChange = { restockQuantity = it },
                        label = { Text("Quantity to add") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = restockQuantity.toIntOrNull()
                        if (qty != null && qty > 0) {
                            productViewModel.restockProduct(selectedProduct!!.productId, qty, context)
                            showRestockDialog = false
                            selectedProduct = null
                            restockQuantity = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C))
                ) {
                    Text("Add Stock", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showRestockDialog = false 
                    selectedProduct = null
                    restockQuantity = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { MyBottomBar(navController, 3) } 
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Header text instead of search bar to maintain balance if needed, or just the buttons
                Text(
                    text = "Stock Levels",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                if (products.isEmpty()) {
                    Button(
                        onClick = { productViewModel.addSampleProducts(context) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Add Samples")
                    }
                }

                IconButton(
                    onClick = { navController.navigate(ROUTE_SELL) },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Inventory is empty. Click + to add products.", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(products) { product ->
                        InventoryItemCard(product) {
                            selectedProduct = product
                            showRestockDialog = true
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(product: Product, onClick: () -> Unit) {
    val (status, statusBg, statusText) = when {
        product.quantity <= 2 -> Triple("Critical", Color(0xFFFFEBEE), Color(0xFFD32F2F))
        product.quantity <= 6 -> Triple("Low", Color(0xFFFFF3E0), Color(0xFFEF6C00))
        else -> Triple("OK", Color(0xFFE8F5E9), Color(0xFF2E7D32))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "${product.quantity} units",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Surface(
                color = statusBg,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
