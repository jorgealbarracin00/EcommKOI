package com.example.ecommkoi

import ProductAdapter
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private var products: List<Product> = listOf() // Initialize with an empty list
    private var loggedInUserId: Int = -1 // Store the logged-in user's ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Retrieve the logged-in user's ID from the intent
        loggedInUserId = intent.getIntExtra("userId", -1)
        if (loggedInUserId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            finish() // Exit if userId is invalid
            return
        }

        // Set up Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize products asynchronously
        initializeProducts()

        // Set up FAB for filter options
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            openFilterOptions()
        }
    }

    private fun initializeProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(applicationContext)
            val dao = database.userDao()

            // Check if products are already in the database
            if (dao.getAllProducts().isEmpty()) {
                val productsToInsert = listOf(
                    Product(1, "Toy 1", "Handmade wooden toy", 25.0, R.drawable.toy1),
                    Product(2, "Toy 2", "Handcrafted stuffed toy", 30.0, R.drawable.toy2),
                    Product(3, "Toy 3", "Artisanal painted toy", 20.0, R.drawable.toy3)
                )
                dao.insertProducts(productsToInsert)
            }

            // Fetch products from the database
            products = dao.getAllProducts()

            // Update the RecyclerView on the main thread
            withContext(Dispatchers.Main) {
                setupRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(products) { product ->
            navigateToProductDetail(product)
        }
        recyclerView.adapter = productAdapter
    }

    private fun openFilterOptions() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_filter_options, null)

        view.findViewById<Button>(R.id.btnFilterByPrice).setOnClickListener {
            val filteredProducts = products.sortedBy { it.price }
            updateRecyclerView(filteredProducts)
            Toast.makeText(this, "Filtered by Price", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }

        view.findViewById<Button>(R.id.btnFilterByCategory).setOnClickListener {
            Toast.makeText(this, "Filtered by Category", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }

        view.findViewById<Button>(R.id.btnClearFilters).setOnClickListener {
            updateRecyclerView(products)
            Toast.makeText(this, "Filters cleared", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                navigateToCart()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToCart() {
        val intent = Intent(this, CartActivity::class.java).apply {
            putExtra("userId", loggedInUserId)
        }
        startActivity(intent)
    }

    private fun updateRecyclerView(filteredProducts: List<Product>) {
        productAdapter = ProductAdapter(filteredProducts) { product ->
            navigateToProductDetail(product)
        }
        recyclerView.adapter = productAdapter
    }

    private fun navigateToProductDetail(product: Product) {
        val intent = Intent(this, ProductDetailActivity::class.java).apply {
            putExtra("productId", product.id)
            putExtra("productName", product.name)
            putExtra("productDescription", product.description)
            putExtra("productPrice", product.price)
            putExtra("productImageResId", product.imageResId)
            putExtra("userId", loggedInUserId)
        }
        startActivity(intent)
    }
}