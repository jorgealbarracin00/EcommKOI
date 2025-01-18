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

    private lateinit var purchaseRecyclerView: RecyclerView
    private lateinit var purchaseAdapter: PurchaseHistoryAdapter
    private var loggedInUserId: Int = -1
    private var purchaseHistory: MutableList<Order> = mutableListOf() // ✅ Fix: Ensure this list exists

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_history)

        // Retrieve logged-in user ID
        loggedInUserId = intent.getIntExtra("userId", -1)
        if (loggedInUserId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ✅ Initialize RecyclerView
        purchaseRecyclerView = findViewById(R.id.rvPurchaseHistory)
        purchaseRecyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ Initialize Adapter
        purchaseAdapter = PurchaseHistoryAdapter(purchaseHistory)
        purchaseRecyclerView.adapter = purchaseAdapter

        // Load purchase history
        loadPurchaseHistory()
    }

    private fun loadPurchaseHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(applicationContext)
            val dao = database.userDao()

            Log.d("PurchaseHistoryDebug", "Fetching purchase history for userId: $loggedInUserId")

            // ✅ Fetch orders again with logs
            val orders = dao.getDistinctOrdersByUser(loggedInUserId)
            Log.d("PurchaseHistoryDebug", "Purchase history returned ${orders.size} items")

            withContext(Dispatchers.Main) {
                if (orders.isEmpty()) {
                    Toast.makeText(this@PurchaseHistoryActivity, "No past orders found.", Toast.LENGTH_SHORT).show()
                }
                purchaseHistory.clear()
                purchaseHistory.addAll(orders)
                purchaseAdapter.notifyDataSetChanged()
            }
        }
    }
}