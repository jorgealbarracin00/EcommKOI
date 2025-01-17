package com.example.ecommkoi

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PurchaseHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var purchaseHistoryAdapter: PurchaseHistoryAdapter
    private var loggedInUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_history)

        // Get logged-in user ID
        loggedInUserId = intent.getIntExtra("userId", -1)
        if (loggedInUserId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPurchaseHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load purchase history
        loadPurchaseHistory()
    }

    private fun loadPurchaseHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(applicationContext)
            val userDao = database.userDao()

            // ✅ Fetch & Sort Orders
            val pastOrders = userDao.getOrdersByUserLastSixMonths(loggedInUserId)
            val sortedOrders = pastOrders.sortedByDescending { it.orderDate } // Explicitly sort by date

            withContext(Dispatchers.Main) {
                if (sortedOrders.isEmpty()) {
                    Toast.makeText(this@PurchaseHistoryActivity, "No purchases in the last 6 months.", Toast.LENGTH_SHORT).show()
                } else {
                    purchaseHistoryAdapter = PurchaseHistoryAdapter(sortedOrders)
                    recyclerView.adapter = purchaseHistoryAdapter
                    purchaseHistoryAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}