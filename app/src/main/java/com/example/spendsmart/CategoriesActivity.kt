package com.example.spendsmart

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CategoriesActivity : AppCompatActivity() {

    private lateinit var etCategoryName: EditText
    private lateinit var etCategoryBudget: EditText
    private lateinit var btnAddCategory: Button
    private lateinit var listViewCategories: ListView

    private val categoryList = mutableListOf(
        "Groceries - Budget: R2000",
        "Transport - Budget: R1500",
        "Utilities - Budget: R1000"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        etCategoryName = findViewById(R.id.etCategoryName)
        etCategoryBudget = findViewById(R.id.etCategoryBudget)
        btnAddCategory = findViewById(R.id.btnAddCategory)
        listViewCategories = findViewById(R.id.listViewCategories)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryList)
        listViewCategories.adapter = adapter

        btnAddCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            val budget = etCategoryBudget.text.toString().trim()

            if (name.isNotEmpty() && budget.isNotEmpty()) {
                categoryList.add("$name - Budget: R$budget")
                adapter.notifyDataSetChanged()
                etCategoryName.text.clear()
                etCategoryBudget.text.clear()
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_categories

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

                R.id.nav_rewards -> {
                    startActivity(Intent(this, AchievementsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_categories -> true

                else -> false
            }
        }
    }
}