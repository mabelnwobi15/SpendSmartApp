package com.example.spendsmart

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.Calendar

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var actCategory: AutoCompleteTextView
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var ivReceiptPreview: ImageView
    private lateinit var dbHelper: DatabaseHelper
    private var categoryIds = mutableMapOf<String, Int>()
    private var currentReceiptUri: String? = null

    // Gallery Picker with Persistable Permission
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {}
            currentReceiptUri = it.toString()
            ivReceiptPreview.setImageURI(it)
            ivReceiptPreview.visibility = View.VISIBLE
        }
    }

    // Camera Launcher
    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            bitmap?.let {
                val path = MediaStore.Images.Media.insertImage(contentResolver, it, "Receipt_${System.currentTimeMillis()}", null)
                currentReceiptUri = path
                ivReceiptPreview.setImageBitmap(it)
                ivReceiptPreview.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        dbHelper = DatabaseHelper(this)
        etAmount = findViewById(R.id.etAmount)
        actCategory = findViewById(R.id.actCategory)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        ivReceiptPreview = findViewById(R.id.ivReceiptPreview)

        loadCategories()

        findViewById<ImageButton>(R.id.btnPickDate).setOnClickListener { showDatePicker() }
        findViewById<ImageButton>(R.id.btnPickTime).setOnClickListener { showTimePicker() }

        // Logic for the 3 Buttons
        findViewById<Button>(R.id.btnCaptureReceipt).setOnClickListener {
            takePhotoLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        findViewById<Button>(R.id.btnUploadReceipt).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        findViewById<Button>(R.id.btnViewReceiptHistory).setOnClickListener {
            // Navigate to whatever screen shows your full list
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<Button>(R.id.btnGeneratePDF).setOnClickListener {
            val amt = etAmount.text.toString()
            if (amt.isNotEmpty()) {
                currentReceiptUri = saveAsPdf(amt, actCategory.text.toString(), etDate.text.toString())
                ivReceiptPreview.setImageResource(android.R.drawable.ic_menu_save)
                ivReceiptPreview.visibility = View.VISIBLE
                Toast.makeText(this, "PDF Generated!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnSaveExpense).setOnClickListener { saveToDb() }
    }

    private fun saveToDb() {
        val amt = etAmount.text.toString()
        val cat = actCategory.text.toString()
        if (amt.isEmpty() || !categoryIds.containsKey(cat)) return

        val db = dbHelper.writableDatabase
        val v = ContentValues().apply {
            put("amount", amt.toDouble())
            put("category_id", categoryIds[cat])
            put("date", etDate.text.toString())
            put("time", etTime.text.toString())
            put("receipt_uri", currentReceiptUri)
        }
        db.insert("expenses", null, v)
        Toast.makeText(this, "Expense Saved!", Toast.LENGTH_SHORT).show()

        // Redirect to History after saving
        val intent = Intent(this, HistoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Clears the backstack
        startActivity(intent)
        finish()
    }

    private fun saveAsPdf(amount: String, category: String, date: String): String? {
        val pdf = PdfDocument()
        val info = PdfDocument.PageInfo.Builder(300, 400, 1).create()
        val page = pdf.startPage(info)
        val canvas = page.canvas
        val paint = Paint()
        paint.textSize = 14f
        canvas.drawText("SpendSmart Receipt", 20f, 40f, paint)
        canvas.drawText("Amount: R$amount", 20f, 80f, paint)
        canvas.drawText("Category: $category", 20f, 110f, paint)
        pdf.finishPage(page)

        val name = "Receipt_${System.currentTimeMillis()}.pdf"
        var uriStr: String? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val cv = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv)
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { pdf.writeTo(it) }
                    uriStr = it.toString()
                }
            }
        } catch (e: Exception) {} finally { pdf.close() }
        return uriStr
    }

    private fun loadCategories() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, name FROM categories", null)
        val names = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            names.add(name)
            categoryIds[name] = id
        }
        cursor.close()
        actCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, names))
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d -> etDate.setText("$y-${m+1}-$d") }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker() {
        val c = Calendar.getInstance()
        TimePickerDialog(this, { _, h, m -> etTime.setText("$h:$m") }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
    }
}