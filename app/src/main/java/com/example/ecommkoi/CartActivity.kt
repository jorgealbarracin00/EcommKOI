package com.example.ecommkoi

import android.os.Bundle
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
    private var loggedInUserId: Int = -1

    // Reference to the DAO
    private val dao by lazy { AppDatabase.getDatabase(applicationContext).userDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Retrieve the logged-in user's ID
        loggedInUserId = intent.getIntExtra("userId", -1)
        if (loggedInUserId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize RecyclerView
        cartRecyclerView = findViewById(R.id.rvCartItems)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)

        // Attach an empty adapter initially to avoid the error
        cartAdapter = CartAdapter(emptyList())
        cartRecyclerView.adapter = cartAdapter

        // Load cart items asynchronously
        loadCartItems()
    }

    private fun loadCartItems() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch cart items for the logged-in user
                val cartItems = dao.getCartItemsForUser(loggedInUserId)

                withContext(Dispatchers.Main) {
                    if (cartItems.isEmpty()) {
                        Toast.makeText(this@CartActivity, "Your cart is empty.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Update adapter with cart items
                        cartAdapter = CartAdapter(cartItems)
                        cartRecyclerView.adapter = cartAdapter
                    }

                    // Calculate and display total price
                    val totalPriceTextView = findViewById<TextView>(R.id.tvTotalPrice)
                    val totalPrice = cartItems.sumOf { it.quantity * it.productPrice }
                    totalPriceTextView.text = "Total: $$totalPrice"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CartActivity, "Error loading cart items.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}