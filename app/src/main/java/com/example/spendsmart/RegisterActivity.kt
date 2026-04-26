package com.example.spendsmart

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLogin: TextView

    // Initialize DatabaseHelper
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLogin = findViewById(R.id.tvLogin)

        btnSignUp.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // 1. Input Validation
            if (email.isEmpty()) {
                etEmail.error = "Enter your email/username"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 8) {
                etPassword.error = "Password must be at least 8 characters"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                etConfirmPassword.error = "Passwords do not match"
                etConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            // 2. Call the Database Registration Function
            registerUser(email, password)
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser(user: String, pass: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", user)
            put("password", pass)
        }

        try {
            // Using SQLite insert with conflict resolution or checking result
            val success = db.insert("users", null, values)

            if (success != -1L) {
                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()

                // Navigate to Login Activity after successful registration
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // If insert returns -1, it usually means the username (UNIQUE) exists
                Toast.makeText(this, "Username/Email already exists", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}