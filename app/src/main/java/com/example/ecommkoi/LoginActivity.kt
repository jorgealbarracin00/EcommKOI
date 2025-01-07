package com.example.ecommkoi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // UI Components
        val emailField = findViewById<EditText>(R.id.editEmail)
        val passwordField = findViewById<EditText>(R.id.editPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signUpTextView = findViewById<TextView>(R.id.tvSignUp)

        // Initialize Room database
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.dao()

        // Login Button Listener
        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (validateInput(email, password)) {
                val user = dao.getUser(email, password)
                if (user != null) {
                    navigateToHome()
                } else {
                    showToast("Invalid credentials. Please try again.")
                }
            }
        }

        // Sign-up TextView Listener
        signUpTextView.setOnClickListener {
            navigateToRegistration()
        }
    }

    // Validate user input
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill in all fields.")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address.")
            return false
        }
        if (password.length < 6) {
            showToast("Password must be at least 6 characters long.")
            return false
        }
        return true
    }

    // Navigate to HomeActivity
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity
    }

    // Navigate to RegistrationActivity
    private fun navigateToRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    // Show toast messages
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}