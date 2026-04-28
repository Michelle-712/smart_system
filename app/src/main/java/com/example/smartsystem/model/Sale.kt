package com.example.smartsystem.model

data class Sale(
    val saleId: String = "",
    val userId: String = "",
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "",
    val status: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
