package com.example.smartsystem.data

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.smartsystem.model.Product
import com.example.smartsystem.model.Sale
import com.example.smartsystem.model.SaleItem
import com.example.smartsystem.model.StockMovement
import com.example.smartsystem.model.StockAlert
import com.example.smartsystem.model.StockPrediction
import com.example.smartsystem.model.DailySummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    
    // Temporary cart for the current sale
    val cartItems = mutableStateListOf<SaleItem>()

    fun addProduct(name: String, description: String, price: Double, quantity: Int, category: String, context: Context): String? {
        val productRef = database.getReference("Products")
        val productId = productRef.push().key ?: return null
        val product = Product(productId, name, description, price, quantity, category)

        productRef.child(productId).setValue(product).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logStockMovement(productId, quantity, "Initial Stock")
                Toast.makeText(context, "Product added to system", Toast.LENGTH_SHORT).show()
            }
        }
        return productId
    }

    fun addSampleProducts(context: Context) {
        val samples = listOf(
            Product("", "Milk 500ml", "Dairy product", 120.0, 42, "Dairy"),
            Product("", "Bread loaf", "White bread", 65.0, 6, "Bakery"),
            Product("", "Sugar 1kg", "Fine sugar", 360.0, 2, "Groceries"),
            Product("", "Cooking oil", "Vegetable oil", 450.0, 18, "Groceries")
        )
        
        samples.forEach { p ->
            addProduct(p.name, p.description, p.price, p.quantity, p.category, context)
        }
    }

    fun addToCart(productId: String, productName: String, quantity: Int, price: Double) {
        val newItem = SaleItem(
            productId = productId,
            productName = productName,
            quantity = quantity,
            unitPrice = price
        )
        cartItems.add(newItem)
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getProducts(products: SnapshotStateList<Product>) {
        val productRef = database.getReference("Products")
        productRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                products.clear()
                for (snap in snapshot.children) {
                    val product = snap.getValue(Product::class.java)
                    if (product != null) {
                        products.add(product)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun viewSales(sales: SnapshotStateList<Sale>): SnapshotStateList<Sale> {
        val salesRef = database.getReference("Sales")
        salesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sales.clear()
                for (snap in snapshot.children) {
                    val sale = snap.getValue(Sale::class.java)
                    if (sale != null) {
                        sales.add(sale)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        return sales
    }

    fun addSale(
        totalAmount: Double,
        paymentMethod: String,
        status: String,
        items: List<SaleItem>,
        context: Context
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val salesRef = database.getReference("Sales")
        val saleItemsRef = database.getReference("SaleItems")
        
        val saleId = salesRef.push().key ?: return

        val sale = Sale(
            saleId = saleId,
            userId = userId,
            totalAmount = totalAmount,
            paymentMethod = paymentMethod,
            status = status,
            timestamp = System.currentTimeMillis()
        )

        // 1. Save Sale
        salesRef.child(saleId).setValue(sale).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 2. Save Sale Items and 3. Log Stock Movement
                items.forEach { item ->
                    val itemId = saleItemsRef.push().key ?: ""
                    val itemWithIds = item.copy(itemId = itemId, saleId = saleId)
                    saleItemsRef.child(itemId).setValue(itemWithIds)

                    // Log stock reduction for the sale
                    logStockMovement(
                        productId = item.productId,
                        quantity = -item.quantity,
                        type = "Sale",
                        userId = userId
                    )
                }
                
                // 4. Update Daily Summary
                updateDailySummary(totalAmount, items.sumOf { it.quantity })
                
                clearCart()
                Toast.makeText(context, "Checkout Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateDailySummary(amount: Double, itemsCount: Int) {
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val summaryRef = database.getReference("DailySummary").child(dateKey)
        
        summaryRef.child("date").setValue(dateKey)
        summaryRef.child("totalSales").run { setValue(ServerValue.increment(amount)) }
        summaryRef.child("totalTransactions").run { setValue(ServerValue.increment(1)) }
        summaryRef.child("totalItemsSold").run { setValue(ServerValue.increment(itemsCount.toLong())) }
    }

    fun getDailySummary(summary: MutableState<DailySummary?>) {
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val summaryRef = database.getReference("DailySummary").child(dateKey)
        
        summaryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(DailySummary::class.java)
                summary.value = data
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun logStockMovement(
        productId: String,
        quantity: Int,
        type: String,
        userId: String? = null
    ) {
        val currentUserId = userId ?: auth.currentUser?.uid ?: "System"
        val stockMovementsRef = database.getReference("StockMovements")
        val movementId = stockMovementsRef.push().key ?: return

        val movement = StockMovement(
            movementId = movementId,
            productId = productId,
            quantity = quantity,
            type = type,
            userId = currentUserId,
            timestamp = System.currentTimeMillis()
        )

        stockMovementsRef.child(movementId).setValue(movement)
        
        // Update product quantity in Products table
        val productRef = database.getReference("Products").child(productId).child("quantity")
        productRef.run { setValue(ServerValue.increment(quantity.toLong())) }

        checkLowStockAlert(productId)
    }

    private fun checkLowStockAlert(productId: String) {
        val productRef = database.getReference("Products").child(productId)
        productRef.get().addOnSuccessListener { snapshot ->
            val product = snapshot.getValue(Product::class.java) ?: return@addOnSuccessListener
            
            // Threshold for low stock (e.g., less than 10)
            if (product.quantity <= 10) {
                val alertsRef = database.getReference("StockAlerts")
                val alertId = productId // Use productId as alertId to avoid duplicates for same product
                
                val severity = if (product.quantity <= 3) "Critical" else "Low"
                
                val alert = StockAlert(
                    alertId = alertId,
                    productId = productId,
                    productName = product.name,
                    currentStock = product.quantity,
                    severity = severity,
                    isResolved = false,
                    timestamp = System.currentTimeMillis()
                )
                alertsRef.child(alertId).setValue(alert)
            } else {
                database.getReference("StockAlerts").child(productId).removeValue()
            }
        }
    }

    fun getAlerts(alerts: SnapshotStateList<StockAlert>) {
        val alertsRef = database.getReference("StockAlerts")
        alertsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                alerts.clear()
                for (snap in snapshot.children) {
                    val alert = snap.getValue(StockAlert::class.java)
                    if (alert != null && !alert.isResolved) {
                        alerts.add(alert)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getStockPredictions(predictions: SnapshotStateList<StockPrediction>) {
        val prodRef = database.getReference("Products")
        prodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                predictions.clear()
                for (snap in snapshot.children) {
                    val p = snap.getValue(Product::class.java)
                    if (p != null) {
                        // Simple logic: if stock is 10 and they sell 2 a day, they have 5 days left
                        val daysLeft = if (p.quantity > 0) (p.quantity / 2) + 1 else 0
                        predictions.add(StockPrediction(p.productId, p.productId, 2.0, daysLeft))
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun updateAlertStatus(alertId: String, isResolved: Boolean) {
        database.getReference("StockAlerts").child(alertId).child("resolved").setValue(isResolved)
    }

    fun savePrediction(productId: String, avgSales: Double, daysLeft: Int) {
        val predictionRef = database.getReference("StockPredictions")
        val prediction = StockPrediction(
            predictionId = productId,
            productId = productId,
            averageDailySales = avgSales,
            daysRemaining = daysLeft
        )
        predictionRef.child(productId).setValue(prediction)
    }

    fun restockProduct(productId: String, quantity: Int, context: Context) {
        logStockMovement(productId, quantity, "Restock")
        Toast.makeText(context, "Restock recorded", Toast.LENGTH_SHORT).show()
    }
}
