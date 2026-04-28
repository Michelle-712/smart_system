package com.example.smartsystem.model

data class StockPrediction(
    val predictionId: String = "",
    val productId: String = "",
    val averageDailySales: Double = 0.0,
    val daysRemaining: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
