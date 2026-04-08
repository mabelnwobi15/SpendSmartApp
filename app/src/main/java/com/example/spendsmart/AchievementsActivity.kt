package com.example.spendsmart

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AchievementsActivity : AppCompatActivity() {

    private lateinit var tvPoints: TextView
    private lateinit var tvRank: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        tvPoints = findViewById(R.id.tvPoints)
        tvRank = findViewById(R.id.tvRank)
        progressBar = findViewById(R.id.progressBar)

        val points = 320
        tvPoints.text = "Points: $points"
        tvRank.text = "Rank: Saver"
        progressBar.max = 500
        progressBar.progress = points

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_rewards

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_add -> {
                    startActivity(Intent(this, AddExpenseActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_trends -> {
                    startActivity(Intent(this, TrendsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_rewards -> true

                R.id.nav_categories -> {
                    startActivity(Intent(this, CategoriesActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
        }
    }
}