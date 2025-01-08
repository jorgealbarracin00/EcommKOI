package com.example.ecommkoi

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int, // Remove autoGenerate
    val name: String,
    val description: String,
    val price: Double,
    val imageResId: Int
)