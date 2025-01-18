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
    SELECT orderId, userId, productId, productName, productPrice, quantity, totalPrice, orderDate, status, orderSessionId, productImage 
    FROM orders 
    WHERE userId = :userId 
    AND status = 'pending'
""")
    fun getCartItemsForUser(userId: Int): List<CartItem>

    @Query("DELETE FROM orders WHERE userId = :userId AND status = 'pending'")
    fun clearCart(userId: Int): Int // ✅ Now deletes only pending orders

    // ✅ Product-related operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(products: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: Product)

    @Query("SELECT * FROM products")
    fun getAllProducts(): List<Product>

    // ✅ Order-related operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrders(orders: List<Order>) // ✅ Fix: Use batch insert

    @Query("DELETE FROM orders WHERE orderId = :orderId")
    fun deleteOrder(orderId: Int)

    @Query("DELETE FROM orders WHERE userId = :userId")
    fun deleteOrdersByUser(userId: Int)

    // ✅ Fix: Get orders from the last 6 months using UNIX timestamp
    @Query("""
    SELECT orderId, userId, productId, productName, quantity, productPrice, totalPrice, orderDate, status, orderSessionId, productImage 
    FROM orders
    WHERE userId = :userId 
    AND orderDate >= strftime('%s', 'now', '-6 months') * 1000
    ORDER BY orderSessionId DESC, orderDate DESC
""")
    fun getOrdersByUserLastSixMonths(userId: Int): List<Order>

    @Query("UPDATE orders SET status = 'completed' WHERE userId = :userId")
    fun markOrdersAsCompleted(userId: Int)

    // ✅ Fix: Use a direct time calculation instead of `strftime()`
    @Query("SELECT * FROM orders WHERE userId = :userId AND status = 'completed' ORDER BY orderDate DESC")
    fun getPurchaseHistory(userId: Int): List<Order>

    @Query("SELECT * FROM orders WHERE orderSessionId = :sessionId")
    fun getOrdersBySession(sessionId: Long): List<Order> // ✅ Fix: Explicitly use `Long`

    @Query("SELECT * FROM orders WHERE userId = :userId AND status = 'completed' ORDER BY orderDate DESC")
    fun getDistinctOrdersByUser(userId: Int): List<Order>

    @Query("SELECT * FROM orders")
    fun getAllOrders(): List<Order> // ✅ Fix: Fetch all orders

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(order: Order)

}