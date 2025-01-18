package com.example.ecommkoi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckoutActivity : AppCompatActivity() {

    private lateinit var etAddress: EditText
    private lateinit var rgPaymentOptions: RadioGroup
    private lateinit var btnPlaceOrder: Button
    private lateinit var creditCardLayout: LinearLayout
    private lateinit var paypalLayout: LinearLayout
    private lateinit var cashLayout: LinearLayout
    private var loggedInUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Retrieve logged-in user ID
        loggedInUserId = intent.getIntExtra("userId", -1)
        if (loggedInUserId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        etAddress = findViewById(R.id.etAddress)
        rgPaymentOptions = findViewById(R.id.rgPaymentOptions)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        // Mock Payment Details
        creditCardLayout = findViewById(R.id.creditCardDetailsLayout)
        paypalLayout = findViewById(R.id.paypalDetailsLayout)
        cashLayout = findViewById(R.id.cashDetailsLayout)

        // Handle Payment Selection
        rgPaymentOptions.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCreditCard -> {
                    creditCardLayout.visibility = View.VISIBLE
                    paypalLayout.visibility = View.GONE
                    cashLayout.visibility = View.GONE
                }
                R.id.rbPayPal -> {
                    creditCardLayout.visibility = View.GONE
                    paypalLayout.visibility = View.VISIBLE
                    cashLayout.visibility = View.GONE
                }
                R.id.rbCashOnDelivery -> {
                    creditCardLayout.visibility = View.GONE
                    paypalLayout.visibility = View.GONE
                    cashLayout.visibility = View.VISIBLE
                }
            }
        }

        // Handle Place Order button click
        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }
    }

    private fun placeOrder() {
        val address = etAddress.text.toString().trim()
        val selectedPaymentMethod = when (rgPaymentOptions.checkedRadioButtonId) {
            R.id.rbCreditCard -> "Credit Card"
            R.id.rbPayPal -> "PayPal"
            R.id.rbCashOnDelivery -> "Cash on Delivery"
            else -> null
        }

        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter your address.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedPaymentMethod == null) {
            Toast.makeText(this, "Please select a payment method.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(applicationContext)
                val dao = database.userDao()

                // ✅ Retrieve cart items before checkout
                val cartItems = dao.getCartItemsForUser(loggedInUserId)

                if (cartItems.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CheckoutActivity, "Cart is empty.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val orderSessionId = System.currentTimeMillis()
                Log.d("CheckoutDebug", "Order session ID: $orderSessionId")

                // ✅ Convert cart items into orders
                val orders = cartItems.map { cartItem ->
                    Order(
                        userId = loggedInUserId,
                        productId = cartItem.productId,
                        productName = cartItem.productName,
                        quantity = cartItem.quantity,
                        productPrice = cartItem.productPrice, // ✅ Ensure productPrice is passed!
                        productImage = cartItem.productImage, // ✅ Added productImage here!
                        totalPrice = cartItem.quantity * cartItem.productPrice,
                        orderDate = System.currentTimeMillis(),
                        status = "completed",
                        orderSessionId = orderSessionId
                    )
                }

                Log.d("CheckoutDebug", "Total orders to insert: ${orders.size}")

                // ✅ Insert all orders once
                dao.insertOrders(orders)

                Log.d("DatabaseDebug", "Fetching all orders after checkout...")
                val allOrders = dao.getOrdersByUserLastSixMonths(loggedInUserId)
                Log.d("DatabaseDebug", "Total orders in database: ${allOrders.size}")

                val newOrders = dao.getOrdersBySession(orderSessionId) // ✅ Ensure this is properly retrieved

                for (order in newOrders) {
                    Log.d("CheckoutDebug", "Inserted Order: ${order.productName}, Status: ${order.status}")
                }

                // ✅ Remove all cart items after checkout
                Log.d("CheckoutDebug", "Deleting all cart items for user: $loggedInUserId")
                dao.clearCart(loggedInUserId)

                // ✅ Ensure cart is empty
                val remainingCartItems = dao.getCartItemsForUser(loggedInUserId)
                Log.d("CheckoutDebug", "Cart after deletion: ${remainingCartItems.size}")

                // ✅ Notify CartActivity to refresh UI
                withContext(Dispatchers.Main) {
                    sendBroadcast(Intent("com.example.ecommkoi.CLEAR_CART"))
                    Toast.makeText(this@CheckoutActivity, "Order placed successfully!", Toast.LENGTH_LONG).show()

                    // ✅ Redirect to Thank You Page
                    val intent = Intent(this@CheckoutActivity, ThankYouActivity::class.java).apply {
                        putExtra("userId", loggedInUserId)
                    }
                    startActivity(intent)
                    finish()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CheckoutActivity, "Error placing order: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}