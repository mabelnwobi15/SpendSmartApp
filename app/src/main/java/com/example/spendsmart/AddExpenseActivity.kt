package com.example.spendsmart

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var actCategory: AutoCompleteTextView
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnUploadReceipt: Button
    private lateinit var btnSaveExpense: Button
    private lateinit var btnPickDate: ImageButton
    private lateinit var btnPickTime: ImageButton

    private val categories = listOf(
        "Groceries",
        "Transport",
        "Utilities",
        "Entertainment",
        "Health",
        "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        etAmount = findViewById(R.id.etAmount)
        actCategory = findViewById(R.id.actCategory)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        etDescription = findViewById(R.id.etDescription)
        btnUploadReceipt = findViewById(R.id.btnUploadReceipt)
        btnSaveExpense = findViewById(R.id.btnSaveExpense)
        btnPickDate = findViewById(R.id.btnPickDate)
        btnPickTime = findViewById(R.id.btnPickTime)

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        actCategory.setAdapter(categoryAdapter)

        btnPickDate.setOnClickListener { showDatePicker() }
        btnPickTime.setOnClickListener { showTimePicker() }

        btnUploadReceipt.setOnClickListener {
            Toast.makeText(this, "Receipt upload clicked", Toast.LENGTH_SHORT).show()
        }

        btnSaveExpense.setOnClickListener {
            val amount = etAmount.text.toString().trim()
            val category = actCategory.text.toString().trim()
            val date = etDate.text.toString().trim()
            val time = etTime.text.toString().trim()

            if (amount.isEmpty()) {
                etAmount.error = "Enter amount"
                etAmount.requestFocus()
                return@setOnClickListener
            }

            if (category.isEmpty()) {
                actCategory.error = "Select category"
                actCategory.requestFocus()
                return@setOnClickListener
            }

            if (date.isEmpty()) {
                etDate.error = "Select date"
                etDate.requestFocus()
                return@setOnClickListener
            }

            if (time.isEmpty()) {
                etTime.error = "Select time"
                etTime.requestFocus()
                return@setOnClickListener
            }

            Toast.makeText(this, "Expense saved successfully", Toast.LENGTH_SHORT).show()

            etAmount.text.clear()
            actCategory.text.clear()
            etDate.text.clear()
            etTime.text.clear()
            etDescription.text.clear()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_add

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_add -> true

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

                R.id.nav_categories -> {
                    startActivity(Intent(this, CategoriesActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                etDate.setText("$day/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hour, minute ->
                etTime.setText(String.format("%02d:%02d", hour, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }
}