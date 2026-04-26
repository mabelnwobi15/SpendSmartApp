package com.example.spendsmart

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvCurrentRange: TextView
    private var filterStart = "1900-01-01"
    private var filterEnd = "2100-12-31"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        dbHelper = DatabaseHelper(this)
        rvHistory = findViewById(R.id.rvHistory)
        tvCurrentRange = findViewById(R.id.tvCurrentRange)
        rvHistory.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.btnFilterDate).setOnClickListener { showRangePicker() }
        loadExpenses()
    }

    private fun showRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker().build()
        builder.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            filterStart = sdf.format(Date(selection.first))
            filterEnd = sdf.format(Date(selection.second))
            tvCurrentRange.text = "Range: $filterStart to $filterEnd"
            loadExpenses()
        }
        builder.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun loadExpenses() {
        val list = mutableListOf<Expense>()
        val db = dbHelper.readableDatabase
        val query = "SELECT e.id, e.amount, c.name, e.date, e.receipt_uri, e.time FROM expenses e JOIN categories c ON e.category_id = c.id WHERE e.date BETWEEN ? AND ? ORDER BY e.date DESC"
        val cursor = db.rawQuery(query, arrayOf(filterStart, filterEnd))

        if (cursor.moveToFirst()) {
            do {
                val expense = Expense(
                    cursor.getInt(0),
                    cursor.getDouble(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
                )
                list.add(expense)
            } while (cursor.moveToNext())
        }
        cursor.close()

        rvHistory.adapter = ExpenseAdapter(list) { selectedExpense ->
            openReceiptMenu(selectedExpense)
        }
    }

    private fun openReceiptMenu(expense: Expense) {
        val options = arrayOf("View Actual Slip", "Share/Export File")

        AlertDialog.Builder(this)
            .setTitle("Receipt Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { // Show the In-App Slip
                        showReceiptSlip(
                            expense.amount.toString(),
                            expense.categoryName,
                            expense.date,
                            expense.time ?: "N/A",
                            expense.receiptUri
                        )
                    }
                    1 -> { // External Share
                        if (expense.receiptUri.isNullOrEmpty()) {
                            Toast.makeText(this, "No file attached to share", Toast.LENGTH_SHORT).show()
                        } else {
                            shareFile(expense.receiptUri!!)
                        }
                    }
                }
            }
            .show()
    }

    private fun showReceiptSlip(amount: String, category: String, date: String, time: String, uriString: String?) {
        val dialog = android.app.Dialog(this)
        dialog.setContentView(R.layout.dialog_receipt_slip)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvSlipAmount = dialog.findViewById<TextView>(R.id.tvSlipAmount)
        val tvSlipCategory = dialog.findViewById<TextView>(R.id.tvSlipCategory)
        val tvSlipDate = dialog.findViewById<TextView>(R.id.tvSlipDate)
        val tvSlipTime = dialog.findViewById<TextView>(R.id.tvSlipTime)
        val ivSlipImage = dialog.findViewById<ImageView>(R.id.ivSlipImage)
        val cvImageContainer = dialog.findViewById<View>(R.id.cvImageContainer)
        val btnClose = dialog.findViewById<Button>(R.id.btnExitSlip)

        tvSlipAmount.text = "R $amount"
        tvSlipCategory.text = category
        tvSlipDate.text = "DATE: $date"
        tvSlipTime.text = "TIME: $time"

        if (!uriString.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(uriString)
                ivSlipImage.setImageURI(uri)
                cvImageContainer.visibility = View.VISIBLE
            } catch (e: Exception) {
                cvImageContainer.visibility = View.GONE
            }
        } else {
            cvImageContainer.visibility = View.GONE
        }

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun shareFile(uriString: String) {
        val uri = Uri.parse(uriString)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = if (uriString.contains(".pdf")) "application/pdf" else "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Receipt"))
    }
}
