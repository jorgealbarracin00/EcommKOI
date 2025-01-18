package com.example.ecommkoi

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val productId: Int,
    val productName: String,  // ✅ Add this field
    val quantity: Int,
    val totalPrice: Double,
    val orderDate: Long = System.currentTimeMillis(), // Store timestamp
    val status: String = "pending",
    val orderSessionId: Long // New: Group orders together
// Default status is pending


)