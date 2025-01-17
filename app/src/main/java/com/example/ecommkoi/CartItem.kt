package com.example.ecommkoi

data class CartItem(
    val orderId: Int,
    val quantity: Int,
    val productName: String,
    val productPrice: Double,
    val productImage: Int,
    val productId: Int // ✅ Ensure this exists

)