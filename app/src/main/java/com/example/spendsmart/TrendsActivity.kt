package com.example.spendsmart

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView

class TrendsActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var tvWeeklyTotal: TextView
    private lateinit var tvHighestDay: TextView
    private lateinit var tvLowestDay: TextView
    private lateinit var tvSmartTip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trends)

        barChart = findViewById(R.id.barChart)
        tvWeeklyTotal = findViewById(R.id.tvWeeklyTotal)
        tvHighestDay = findViewById(R.id.tvHighestDay)
        tvLowestDay = findViewById(R.id.tvLowestDay)
        tvSmartTip = findViewById(R.id.tvSmartTip)

        setupBarChart()
        setupInsights()
        setupBottomNavigation()
    }

    private fun setupBarChart() {
        val entries = arrayListOf(
            BarEntry(0f, 120f),
            BarEntry(1f, 250f),
            BarEntry(2f, 300f),
            BarEntry(3f, 180f),
            BarEntry(4f, 800f),
            BarEntry(5f, 450f),
            BarEntry(6f, 250f)
        )

        val labels = arrayListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        val dataSet = BarDataSet(entries, "Weekly Spending")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.labelCount = labels.size

        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = false
        
        barChart.invalidate()
    }

    private fun setupInsights() {
        tvWeeklyTotal.text = "Weekly Total: R2,350"
        tvHighestDay.text = "Highest Spending: Friday (R800)"
        tvLowestDay.text = "Lowest Spending: Monday (R120)"
        tvSmartTip.text = "Smart Tip: Reduce takeout spending to stay within budget."
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_trends

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddExpenseActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_trends -> true
                R.id.nav_rewards -> {
                    startActivity(Intent(this, AchievementsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_categories -> {
                    startActivity(Intent(this, CategoriesActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}