package com.example.spendsmart

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AchievementsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        val tvPoints = findViewById<TextView>(R.id.tvPoints)
        val tvRank = findViewById<TextView>(R.id.tvRank)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // LOGIC: In a real app, 'points' would come from your Database count
        val points = 320
        tvPoints.text = "$points pts"

        // Dynamic Rank Logic
        val rankName = when {
            points > 400 -> "Master Saver 🏆"
            points > 200 -> "Smart Saver 💰"
            else -> "Beginner 🌱"
        }
        tvRank.text = "Rank: $rankName"

        progressBar.max = 500
        progressBar.progress = points

        // Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_rewards
        bottomNav.setOnItemSelectedListener {
            if (it.itemId == R.id.nav_rewards) return@setOnItemSelectedListener true
            val intent = when (it.itemId) {
                R.id.nav_home -> Intent(this, MainActivity::class.java)
                R.id.nav_add -> Intent(this, AddExpenseActivity::class.java)
                R.id.nav_trends -> Intent(this, TrendsActivity::class.java)
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