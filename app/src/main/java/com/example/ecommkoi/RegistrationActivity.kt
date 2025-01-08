package com.example.ecommkoi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val nameField = findViewById<EditText>(R.id.editName)
        val emailField = findViewById<EditText>(R.id.editEmail)
        val passwordField = findViewById<EditText>(R.id.editPassword)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        val database = AppDatabase.getDatabase(applicationContext)
        val userDao = database.userDao()

        registerButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val user = User(name = name, email = email, password = password)
                userDao.insertUser(user)
                runOnUiThread {
                    Toast.makeText(this@RegistrationActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}