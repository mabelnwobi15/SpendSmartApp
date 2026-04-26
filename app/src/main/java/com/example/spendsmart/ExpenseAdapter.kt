package com.example.spendsmart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val expenses: List<Expense>,
    private val onExpenseClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(android.R.id.text1)
        val tvDetails: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = expenses[position]
        holder.tvAmount.text = "R${item.amount} - ${item.categoryName}"

        val receiptStatus = if (item.receiptUri != null) " [View Receipt 📷]" else ""
        holder.tvDetails.text = "${item.date}$receiptStatus"

        holder.itemView.setOnClickListener {
            onExpenseClick(item)
        }
    }

    override fun getItemCount() = expenses.size
}
