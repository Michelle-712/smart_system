package com.example.smartsystem.data

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.smartsystem.model.Sale
import com.example.smartsystem.model.SaleItem
import com.example.smartsystem.model.StockMovement
import com.example.smartsystem.model.StockAlert
import com.example.smartsystem.model.StockPrediction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

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
                var totalItemsCount = 0
                items.forEach { item ->
                    totalItemsCount += item.quantity
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
                updateDailySummary(totalAmount, totalItemsCount)
                
                Toast.makeText(context, "Sale and items recorded", Toast.LENGTH_SHORT).show()
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
        
        // After every movement, check for low stock
        checkLowStockAlert(productId)
    }

    private fun checkLowStockAlert(productId: String) {
        val alertsRef = database.getReference("StockAlerts")
        // Logic to fetch stock and create alerts would go here
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
