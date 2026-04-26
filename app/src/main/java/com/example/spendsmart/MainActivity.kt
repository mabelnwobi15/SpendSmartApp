package com.example.spendsmart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Catch the Name from SharedPreferences
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedName = prefs.getString("userName", "User") ?: "User"

        // Final Polish: Capitalize the name
        val displayName = savedName.replaceFirstChar { it.uppercase() }

        // 2. Dynamic Greeting Logic
        val tvTitle = findViewById<TextView>(R.id.tvDashboardTitle)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val (greeting, icon) = when(hour) {
            in 0..11 -> "Good Morning" to "☀️"
            in 12..16 -> "Good Afternoon" to "☕"
            else -> "Good Evening" to "🌙"
        }

        // Display: "Good Morning, John! ☀️"
        tvTitle.text = "$greeting, $displayName! $icon"

        // 3. Dynamic Widgets Logic
        val tvTip = findViewById<TextView>(R.id.tvDailyTip)
        val tips = listOf(
            "💡 Tip: Tracking daily saves you 15% more.",
            "💡 Tip: Review your 'Entertainment' category today.",
            "💡 Tip: You're close to your 'Saver' badge!"
        )
        tvTip.text = tips.random()

        // 4. Animation for the Dashboard Card
        val progressCard = findViewById<CardView>(R.id.progressCard)
        progressCard.alpha = 0f
        progressCard.translationY = 40f
        progressCard.animate().alpha(1f).translationY(0f).setDuration(700).start()

        // 5. Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener {
            if (it.itemId == R.id.nav_home) return@setOnItemSelectedListener true
            val intent = when (it.itemId) {
                R.id.nav_add -> Intent(this, AddExpenseActivity::class.java)
                R.id.nav_trends -> Intent(this, TrendsActivity::class.java)
                R.id.nav_rewards -> Intent(this, AchievementsActivity::class.java)
                R.id.nav_categories -> Intent(this, CategoriesActivity::class.java)
                else -> null
            }
            intent?.let { i ->
                startActivity(i)
                overridePendingTransition(0, 0)
                finish()
            }
            true
        }
    }
}