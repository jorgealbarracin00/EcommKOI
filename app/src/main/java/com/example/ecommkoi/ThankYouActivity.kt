package com.example.ecommkoi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ThankYouActivity : AppCompatActivity() {

    private var loggedInUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you)

        // Retrieve userId from intent
        loggedInUserId = intent.getIntExtra("userId", -1)

        // Set Thank You image and message
        val imageView = findViewById<ImageView>(R.id.ivThankYou)
        imageView.setImageResource(R.drawable.ic_thankyou) // Ensure you have thank_you.png in res/drawable

        val textView = findViewById<TextView>(R.id.tvThankYouMessage)
        textView.text = "Thank you for your order!"

        // Redirect to Home after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra("userId", loggedInUserId) // Pass userId to HomeActivity
            }
            startActivity(intent)
            finish()
        }, 3000) // 3-second delay
    }
}