package com.example.ecommkoi

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.ecommkoi.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the start button and set an OnClickListener
        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            // Navigate to HomeActivity
            startActivity(Intent(this, HomeActivity::class.java))
        }

        // Initialize the Room database
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "ecommkoi-database"
        ).allowMainThreadQueries() // Allow main thread queries for simplicity
            .build()

        // Example: Insert a user (only for testing)
        val dao = database.userDao()
        dao.insertUser(User(name = "John Doe", email = "john@example.com", password = "password"))
    }
}