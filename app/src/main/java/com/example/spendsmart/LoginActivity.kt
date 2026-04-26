package com.example.spendsmart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText // In your DB this is 'username'
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignUp: TextView
    private lateinit var dbHelper: DatabaseHelper // CRITICAL: Added this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignUp = findViewById(R.id.tvSignUp)

        btnLogin.setOnClickListener {
            val username = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Inside your login success block (where you start MainActivity)
            val emailEntered = etEmail.text.toString()

// Optional: Clean the name (e.g., "john@gmail.com" becomes "John")
            val cleanName = emailEntered.split("@")[0]

            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString("userName", cleanName)
            editor.apply()


            startActivity(Intent(this, MainActivity::class.java))

            // Input Handling: Prevention of empty field crashes
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Database Logic: Calling the helper function
            if (loginUser(username, password)) {
                Toast.makeText(this, "Welcome back, $username!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // UI Feedback: Excellent error handling
                etEmail.error = "Invalid username or password"
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    //  Database query to verify user
    private fun loginUser(user: String, pass: String): Boolean {
        return try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                arrayOf(user, pass)
            )
            val exists = cursor.count > 0
            cursor.close()
            exists
        } catch (e: Exception) {
            false // Safe return if table doesn't exist yet
        }
    }
}