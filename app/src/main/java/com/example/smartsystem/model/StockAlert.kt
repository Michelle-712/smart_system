package com.example.smartsystem.model

data class StockAlert(
    val alertId: String = "",
    val productId: String = "",
    val productName: String = "",
    val currentStock: Int = 0,
    val severity: String = "", // "Low", "Critical"
    val isResolved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
