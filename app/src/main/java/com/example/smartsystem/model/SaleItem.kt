package com.example.smartsystem.model

data class SaleItem(
    val itemId: String = "",
    val saleId: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val subtotal: Double = quantity * unitPrice
)
