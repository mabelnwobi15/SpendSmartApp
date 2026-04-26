package com.example.spendsmart

data class Expense(
    val id: Int,
    val amount: Double,
    val categoryName: String,
    val date: String,
    val receiptUri: String?,
    val time: String?
)
