package com.example.ecommkoi

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailActivity : AppCompatActivity() {
    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Initialize views
        val productImage = findViewById<ImageView>(R.id.productImage)
        val productName = findViewById<TextView>(R.id.productName)
        val productDescription = findViewById<TextView>(R.id.productDescription)
        val productPrice = findViewById<TextView>(R.id.productPrice)
        val quantityText = findViewById<TextView>(R.id.quantityText)
        val decreaseQuantity = findViewById<Button>(R.id.decreaseQuantity)
        val increaseQuantity = findViewById<Button>(R.id.increaseQuantity)
        val btnAddToCart = findViewById<Button>(R.id.btnAddToCart)

        // Get product details and userId from intent
        val productId = intent.getIntExtra("productId", -1)
        val userId = intent.getIntExtra("userId", -1)
        val name = intent.getStringExtra("productName") ?: "Unknown Product"
        val description = intent.getStringExtra("productDescription") ?: "No Description"
        val price = intent.getDoubleExtra("productPrice", 0.0)
        val imageResId = intent.getIntExtra("productImageResId", 0)

        // Validate userId
        if (userId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            finish() // Exit the activity if userId is invalid
            return
        }

        // Set product details
        productImage.setImageResource(imageResId)
        productName.text = name
        productDescription.text = description
        productPrice.text = "$$price"

        // Handle quantity increase
        increaseQuantity.setOnClickListener {
            quantity++
            quantityText.text = quantity.toString()
        }

        // Handle quantity decrease
        decreaseQuantity.setOnClickListener {
            if (quantity > 1) {
                quantity--
                quantityText.text = quantity.toString()
            }
        }

        // Initialize Room database
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "ecommkoi-database"
        ).build()
        val dao = database.userDao()

        // Handle Add to Cart button
        btnAddToCart.setOnClickListener {
            if (productId == -1) {
                Toast.makeText(this, "Error: Product not found.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val totalPrice = quantity * price // Calculate total price
            val order = Order(userId = userId, productId = productId, quantity = quantity, totalPrice = totalPrice)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    dao.insertOrder(order)
                    Log.d("DatabaseDebug", "Inserted Order: $order")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ProductDetailActivity,
                            "Added $quantity of $name to the cart. Total: $$totalPrice",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish() // Close the detail page
                    }
                } catch (e: Exception) {
                    Log.e("DatabaseDebug", "Error inserting order: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ProductDetailActivity,
                            "Failed to add item to cart. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}