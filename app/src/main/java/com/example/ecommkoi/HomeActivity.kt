package com.example.ecommkoi

import ProductAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        // Retrieve logged-in user ID
        loggedInUserId = intent.getIntExtra("userId", -1)
        if (loggedInUserId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set up Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Load Products from Database
        loadProducts()

        // Set up Floating Action Button (FAB) for filter options
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            openFilterOptions()
        }
    }

    /** Load Products from Database **/
    private fun loadProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(applicationContext)
            val dao = database.userDao()

            Log.d("HomeActivity", "Fetching products from database...")
            val existingProducts = dao.getAllProducts()

            if (existingProducts.isEmpty()) {
                Log.d("HomeActivity", "No products found. Inserting default products...")
                val productsToInsert = listOf(
                    Product(1, "Toy 1", "Handmade wooden toy", 25.0, R.drawable.toy1),
                    Product(2, "Toy 2", "Handcrafted stuffed toy", 30.0, R.drawable.toy2),
                    Product(3, "Toy 3", "Artisanal painted toy", 20.0, R.drawable.toy3)
                )
                dao.insertProducts(productsToInsert)
                products = productsToInsert
            } else {
                products = existingProducts
            }

            Log.d("HomeActivity", "Products loaded: ${products.size}")

            withContext(Dispatchers.Main) {
                setupRecyclerView()
            }
        }
    }

    /** Set up RecyclerView with the Product List **/
    private fun setupRecyclerView() {
        if (products.isEmpty()) {
            Toast.makeText(this, "No products available.", Toast.LENGTH_SHORT).show()
            return
        }

        productAdapter = ProductAdapter(products) { product ->
            navigateToProductDetail(product)
        }
        recyclerView.adapter = productAdapter
    }

    /** Open Bottom Sheet for Filtering Options **/
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

    /** Handle Menu Options **/
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

    /** Navigate to Cart **/
    private fun navigateToCart() {
        val intent = Intent(this, CartActivity::class.java).apply {
            putExtra("userId", loggedInUserId)
        }
        startActivity(intent)
    }

    /** Update RecyclerView with Filtered Products **/
    private fun updateRecyclerView(filteredProducts: List<Product>) {
        productAdapter = ProductAdapter(filteredProducts) { product ->
            navigateToProductDetail(product)
        }
        recyclerView.adapter = productAdapter
    }

    /** Navigate to Product Detail Page **/
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