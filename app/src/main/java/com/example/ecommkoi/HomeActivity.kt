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

        // ✅ Retrieve userId from intent
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
                    Product(1, "Cute couple", "Handmade stuffed toy", 28.0, R.drawable.toy1),
                    Product(2, "Fairy", "Handcrafted stuffed toy", 40.0, R.drawable.toy2),
                    Product(3, "Elephant", "Handcrafted stuffed toy", 30.0, R.drawable.toy3),
                    Product(4, "Lion", "Handcrafted stuffed toy", 60.0, R.drawable.toy4),
                    Product(5, "Dragon", "Handcrafted stuffed toy", 80.0, R.drawable.toy5),
                    Product(6, "Giraffe", "Handcrafted wooden toy", 40.0, R.drawable.toy6),
                    Product(7, "Hedgehog", "Handcrafted wooden toy", 32.0, R.drawable.toy7),
                    Product(8, "Unicorn", "Handcrafted wooden toy", 34.0, R.drawable.toy8),
                    Product(9, "Fox", "Handcrafted crochet toy", 62.0, R.drawable.toy9),
                    Product(10, "Foxy", "Handcrafted crochet toy", 18.0, R.drawable.toy10)
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
            showCategoryFilterDialog() // ✅ Implemented method for category filtering
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

    /** Show Category Filter Dialog **/
    private fun showCategoryFilterDialog() {
        val categories = products.map { it.description }.distinct() // ✅ Extract unique descriptions

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Filter by Category")
        builder.setItems(categories.toTypedArray()) { _, which ->
            val selectedCategory = categories[which]
            filterProductsByCategory(selectedCategory)
        }
        builder.show()
    }

    /** Filter Products by Selected Category **/
    private fun filterProductsByCategory(category: String) {
        val filteredProducts = products.filter { it.description == category } // ✅ Filter locally

        updateRecyclerView(filteredProducts)
        Toast.makeText(this, "Filtered by: $category", Toast.LENGTH_SHORT).show()
    }

    /** Handle Menu Options **/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                val intent = Intent(this, CartActivity::class.java).apply {
                    putExtra("userId", loggedInUserId) // ✅ Ensure userId is passed
                }
                startActivity(intent)
                true
            }
            R.id.action_purchase_history -> {
                val intent = Intent(this, PurchaseHistoryActivity::class.java).apply {
                    putExtra("userId", loggedInUserId) // ✅ Ensure userId is passed
                }
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToPurchaseHistory() {
        val intent = Intent(this, PurchaseHistoryActivity::class.java).apply {
            putExtra("userId", loggedInUserId)
        }
        startActivity(intent)
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