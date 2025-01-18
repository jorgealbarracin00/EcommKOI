package com.example.ecommkoi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var totalPriceTextView: TextView
    private var loggedInUserId: Int = -1
    private var cartItems: MutableList<CartItem> = mutableListOf() // Mutable list for updates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Retrieve logged-in user ID
        loggedInUserId = intent.getIntExtra("userId", -1)
        if (loggedInUserId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize UI components
        cartRecyclerView = findViewById(R.id.rvCartItems)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        totalPriceTextView = findViewById(R.id.tvTotalPrice)

        // Attach an empty adapter initially
        cartAdapter = CartAdapter(cartItems) { cartItem -> removeItemFromCart(cartItem) }
        cartRecyclerView.adapter = cartAdapter

        // Load cart items
        loadCartItems()

        // Handle Checkout button
        val btnCheckout = findViewById<Button>(R.id.btnCheckout)
        btnCheckout.setOnClickListener {
            checkoutCart()
        }
    }

    private fun loadCartItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(applicationContext)
            val dao = database.userDao()

            // ✅ Log when fetching cart items
            Log.d("CartDebug", "Fetching cart items for userId: $loggedInUserId")
            val items = dao.getCartItemsForUser(loggedInUserId)
            Log.d("CartDebug", "Cart query returned ${items.size} items")

            withContext(Dispatchers.Main) {
                if (items.isEmpty()) {
                    Toast.makeText(this@CartActivity, "Your cart is empty.", Toast.LENGTH_SHORT).show()
                } else {
                    cartItems.clear()
                    cartItems.addAll(items)
                    cartAdapter.notifyDataSetChanged() // Refresh RecyclerView
                }

                // ✅ Calculate & Update Total Price
                updateTotalPrice()
            }
        }
    }

    private fun removeItemFromCart(cartItem: CartItem) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(applicationContext)
                database.userDao().deleteOrder(cartItem.orderId)

                withContext(Dispatchers.Main) {
                    cartItems.remove(cartItem)
                    cartAdapter.notifyDataSetChanged() // Ensure RecyclerView updates

                    updateTotalPrice() // Recalculate total price
                    Toast.makeText(this@CartActivity, "${cartItem.productName} removed.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CartActivity, "Error removing item.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateTotalPrice() {
        val totalPrice = cartItems.sumOf { it.quantity * it.productPrice }
        totalPriceTextView.text = "Total: $%.2f".format(totalPrice)
        Log.d("CartDebug", "Updated Total Price: $$totalPrice")
    }

    private fun checkoutCart() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty. Add items before checkout.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, CheckoutActivity::class.java).apply {
            putExtra("userId", loggedInUserId) // Pass user ID to CheckoutActivity
        }
        startActivity(intent)
    }
}