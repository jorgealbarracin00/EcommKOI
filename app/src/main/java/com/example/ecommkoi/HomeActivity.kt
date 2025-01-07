package com.example.ecommkoi

import ProductAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var products: List<Product> // Store the original product list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Product list
        products = listOf(
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

        // Set up FAB for filter options
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            openFilterOptions()
        }
    }

    // Open filter options using a BottomSheetDialog
    private fun openFilterOptions() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_filter_options, null)

        // Set up filter option buttons
        view.findViewById<Button>(R.id.btnFilterByPrice).setOnClickListener {
            val filteredProducts = products.sortedBy { it.price }
            updateRecyclerView(filteredProducts)
            Toast.makeText(this, "Filtered by Price", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }

        view.findViewById<Button>(R.id.btnFilterByCategory).setOnClickListener {
            // Example: Filter logic for category (add categories to your Product model if needed)
            Toast.makeText(this, "Filtered by Category", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }

        view.findViewById<Button>(R.id.btnClearFilters).setOnClickListener {
            updateRecyclerView(products) // Reset to original product list
            Toast.makeText(this, "Filters cleared", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
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