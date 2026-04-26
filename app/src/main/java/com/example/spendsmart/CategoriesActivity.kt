package com.example.spendsmart

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CategoriesActivity : AppCompatActivity() {

    private lateinit var etCategoryName: EditText
    private lateinit var etMaxGoal: EditText
    private lateinit var btnAddCategory: Button
    private lateinit var listViewCategories: ListView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ArrayAdapter<String>
    private val categoryList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        dbHelper = DatabaseHelper(this)
        etCategoryName = findViewById(R.id.etCategoryName)
        etMaxGoal = findViewById(R.id.etCategoryBudget)
        btnAddCategory = findViewById(R.id.btnAddCategory)
        listViewCategories = findViewById(R.id.listViewCategories)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryList)
        listViewCategories.adapter = adapter

        loadCategoriesFromDB()

        btnAddCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            val budgetStr = etMaxGoal.text.toString().trim()

            if (name.isNotEmpty() && budgetStr.isNotEmpty()) {
                val budget = budgetStr.toDoubleOrNull() ?: 0.0
                saveCategoryToDB(name, budget)
                etCategoryName.text.clear()
                etMaxGoal.text.clear()
            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        setupNavigation()
    }



    private fun saveCategoryToDB(name: String, budget: Double) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("max_goal", budget)
            put("min_goal", 0.0)
        }
        val result = db.insert("categories", null, values)
        if (result != -1L) {
            Toast.makeText(this, "Category Saved", Toast.LENGTH_SHORT).show()
            loadCategoriesFromDB()
        }
    }

    private fun loadCategoriesFromDB() {
        categoryList.clear()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM categories", null)
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val budget = cursor.getDouble(cursor.getColumnIndexOrThrow("max_goal"))
                categoryList.add("$name - Limit: R$budget")
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_categories
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, MainActivity::class.java)); true }
                R.id.nav_add -> { startActivity(Intent(this, AddExpenseActivity::class.java)); true }
                else -> true
            }
        }
    }
}