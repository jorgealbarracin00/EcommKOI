package com.example.ecommkoi

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val orderId: Int = 0,  // ✅ Ensure Primary Key exists
    val userId: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val productPrice: Double,
    val totalPrice: Double,
    val orderDate: Long,
    val status: String,  // ✅ "pending" or "completed"
    val orderSessionId: Long,
    val productImage: Int
)