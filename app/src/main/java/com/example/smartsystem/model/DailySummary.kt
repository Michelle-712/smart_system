package com.example.smartsystem.model

data class DailySummary(
    val date: String = "", // Format: YYYY-MM-DD
    val totalSales: Double = 0.0,
    val totalTransactions: Int = 0,
    val totalItemsSold: Int = 0
)
