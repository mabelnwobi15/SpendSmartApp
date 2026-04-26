package com.example.spendsmart

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "SpendSmart.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        // Users Table
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT)")

        // Categories Table
        db.execSQL("CREATE TABLE categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, min_goal REAL, max_goal REAL)")

        // Expenses Table
        db.execSQL("""
            CREATE TABLE expenses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                amount REAL,
                date TEXT,
                time TEXT,
                category_id INTEGER,
                receipt_uri TEXT,
                FOREIGN KEY(category_id) REFERENCES categories(id)
            )
        """.trimIndent())

        // Pre-populate default categories
        val defaults = arrayOf("Groceries", "Transport", "Entertainment", "Utilities")
        for (cat in defaults) {
            val values = ContentValues().apply {
                put("name", cat)
                put("min_goal", 0.0)
                put("max_goal", 1000.0)
            }
            db.insert("categories", null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        db.execSQL("DROP TABLE IF EXISTS expenses")
        db.execSQL("DROP TABLE IF EXISTS categories")
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }
}