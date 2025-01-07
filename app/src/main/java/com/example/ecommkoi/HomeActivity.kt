package com.example.ecommkoi

import ProductAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Product list
        val products = listOf(
            Product(1, "Toy 1", "Handmade wooden toy", 25.0, R.drawable.toy1),
            Product(2, "Toy 2", "Handcrafted stuffed toy", 30.0, R.drawable.toy2),
            Product(3, "Toy 3", "Artisanal painted toy", 20.0, R.drawable.toy3)
        )

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Adapter with click listener
        productAdapter = ProductAdapter(products) { product ->
            navigateToProductDetail(product)
        }
        recyclerView.adapter = productAdapter

        // Set up SearchView
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredProducts = products.filter {
                    it.name.contains(newText ?: "", ignoreCase = true)
                }
                updateRecyclerView(filteredProducts)
                return true
            }
        })
    }

    // Navigate to ProductDetailActivity with product details
    private fun navigateToProductDetail(product: Product) {
        val intent = Intent(this, ProductDetailActivity::class.java).apply {
            putExtra("productId", product.id)
            putExtra("productName", product.name)
            putExtra("productDescription", product.description)
            putExtra("productPrice", product.price)
            putExtra("productImageResId", product.imageResId)
        }
        startActivity(intent)
    }

    // Update RecyclerView with filtered products
    private fun updateRecyclerView(filteredProducts: List<Product>) {
        productAdapter = ProductAdapter(filteredProducts) { product ->
            navigateToProductDetail(product)
        }
        recyclerView.adapter = productAdapter
    }
}