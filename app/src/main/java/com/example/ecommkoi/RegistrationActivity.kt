package com.example.ecommkoi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // UI Components
        val nameField = findViewById<EditText>(R.id.editName)
        val emailField = findViewById<EditText>(R.id.editEmail)
        val passwordField = findViewById<EditText>(R.id.editPassword)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        // Initialize database
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.dao()

        // Register Button Listener
        registerButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (validateInput(name, email, password)) {
                // Check if email is already registered
                if (dao.getUser(email, password) != null) {
                    showToast("This email is already registered.")
                } else {
                    // Save user to the database
                    dao.insertUser(User(name = name, email = email, password = password))
                    showToast("Registration Successful!")

                    // Navigate to LoginActivity
                    navigateToLogin()
                }
            }
        }
    }

    // Validate user input
    private fun validateInput(name: String, email: String, password: String): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields.")
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

    // Navigate to LoginActivity
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close RegistrationActivity
    }

    // Show toast messages
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}