package com.example.ecommkoi

data class CartItem(
    val orderId: Int,       // ✅ Must match `id AS orderId` in SQL
    val userId: Int,        // ✅ Needed to track the user's cart
    val productId: Int,     // ✅ Unique product ID
    val productName: String,
    val productPrice: Double,
    val quantity: Int,
    val totalPrice: Double, // ✅ Add total price
    val orderDate: Long,    // ✅ Add order timestamp
    val status: String,     // ✅ Needed for distinguishing cart vs purchase
    val orderSessionId: Long, // ✅ To group items in a session
    val productImage: Int
)