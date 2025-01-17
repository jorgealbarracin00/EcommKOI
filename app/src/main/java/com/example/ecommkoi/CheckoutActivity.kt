package com.example.ecommkoi

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

                // ✅ Instead of deleting, mark orders as "completed"
                dao.markOrdersAsCompleted(loggedInUserId)
                Log.d("CheckoutActivity", "Orders marked as completed.")

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CheckoutActivity, "Order placed successfully!", Toast.LENGTH_LONG).show()

                    // Navigate to ThankYouActivity
                    val intent = Intent(this@CheckoutActivity, ThankYouActivity::class.java)
                    intent.putExtra("userId", loggedInUserId)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                Log.e("CheckoutActivity", "Error placing order: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CheckoutActivity, "Error placing order.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }}