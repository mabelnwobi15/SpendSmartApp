package com.example.spendsmart

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TrendsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trends)
        dbHelper = DatabaseHelper(this)

        findViewById<Button>(R.id.btnViewLatestReceipt).setOnClickListener {
            openLatestReceipt()
        }
    }

    private fun openLatestReceipt() {
        val db = dbHelper.readableDatabase
        // Get the latest URI from the database
        val cursor = db.rawQuery("SELECT receipt_uri FROM expenses WHERE receipt_uri IS NOT NULL ORDER BY id DESC LIMIT 1", null)

        if (cursor.moveToFirst()) {
            val uriStr = cursor.getString(0)
            cursor.close()
            if (!uriStr.isNullOrEmpty()) {
                openFileFromUri(uriStr)
            }
        } else {
            cursor.close()
            Toast.makeText(this, "No receipts found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFileFromUri(uriString: String) {
        try {
            val uri = Uri.parse(uriString)
            val intent = Intent(Intent.ACTION_VIEW)

            // Determine the MIME type so Android suggests the right apps (Drive, Gallery, etc.)
            val mimeType = when {
                uriString.contains(".pdf", ignoreCase = true) -> "application/pdf"
                uriString.contains(".jpg", ignoreCase = true) ||
                        uriString.contains(".png", ignoreCase = true) ||
                        uriString.contains(".jpeg", ignoreCase = true) -> "image/*"
                else -> "*/*"
            }

            intent.setDataAndType(uri, mimeType)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val chooser = Intent.createChooser(intent, "SpendSmart: Open Receipt with...")
            startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}