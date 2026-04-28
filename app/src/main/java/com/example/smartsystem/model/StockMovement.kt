package com.example.smartsystem.model

data class StockMovement(
    val movementId: String = "",
    val productId: String = "",
    val quantity: Int = 0, // Positive for addition, negative for deduction
    val type: String = "", // "Restock", "Sale", "Damage", "Return"
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
