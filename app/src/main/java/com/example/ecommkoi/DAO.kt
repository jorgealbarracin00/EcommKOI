package com.example.ecommkoi

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DAO {
    @Insert
    fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    fun getUser(email: String, password: String): User?

    @Insert
    fun insertProduct(product: Product)

    @Query("SELECT * FROM products")
    fun getAllProducts(): List<Product>

    @Insert
    fun insertOrder(order: Order)

    @Query("SELECT * FROM orders WHERE userId = :userId")
    fun getOrdersByUser(userId: Int): List<Order>
}