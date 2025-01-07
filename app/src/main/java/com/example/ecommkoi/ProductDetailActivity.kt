package com.example.ecommkoi

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Get product details from intent
        val productId = intent.getIntExtra("productId", -1)
        val productName = intent.getStringExtra("productName") ?: "Unknown Product"
        val productDescription = intent.getStringExtra("productDescription") ?: "No Description"
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val productImageResId = intent.getIntExtra("productImageResId", 0)

        // Set up UI elements
        findViewById<TextView>(R.id.productName).text = productName
        findViewById<TextView>(R.id.productDescription).text = productDescription
        findViewById<TextView>(R.id.productPrice).text = "$$productPrice"
        findViewById<ImageView>(R.id.productImage).setImageResource(productImageResId)
    }
}