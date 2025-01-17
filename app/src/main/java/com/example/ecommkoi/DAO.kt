package com.example.ecommkoi

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDAO {

    // ✅ User-related operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    fun getUser(email: String, password: String): User?

    // ✅ Cart-related operations
    @Query("""
        SELECT o.id AS orderId, o.quantity, p.name AS productName, p.price AS productPrice, p.imageResId AS productImage
        FROM orders o
        INNER JOIN products p ON o.productId = p.id
        WHERE o.userId = :userId
    """)
    fun getCartItemsForUser(userId: Int): List<CartItem>

    // ✅ Product-related operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(products: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: Product)

    @Query("SELECT * FROM products")
    fun getAllProducts(): List<Product>

    // ✅ Order-related operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(order: Order)

    @Query("DELETE FROM orders WHERE id = :orderId")
    fun deleteOrder(orderId: Int)

    @Query("DELETE FROM orders WHERE userId = :userId")
    fun deleteOrdersByUser(userId: Int)

    // ✅ Fix: Use correct timestamp conversion for the last 6 months
    @Query("""
    SELECT * FROM orders
    WHERE userId = :userId
    AND orderDate >= strftime('%s', 'now', '-6 months') * 1000
    ORDER BY orderDate DESC
""")
    fun getOrdersByUserLastSixMonths(userId: Int): List<Order>

    @Query("UPDATE orders SET status = 'completed' WHERE userId = :userId")
    fun markOrdersAsCompleted(userId: Int)

    @Query("""
        SELECT * FROM orders 
        WHERE userId = :userId 
        AND status = 'completed' 
        AND orderDate >= (:currentTime - (6 * 30 * 24 * 60 * 60 * 1000))
    """)
    fun getPurchaseHistory(userId: Int, currentTime: Long): List<Order>
}