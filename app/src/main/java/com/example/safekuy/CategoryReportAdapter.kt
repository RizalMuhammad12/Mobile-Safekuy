package com.example.safekuy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safekuy.data.CategorySummary
import java.text.NumberFormat
import java.util.Locale

class CategoryReportAdapter(private var categories: List<CategorySummary>) :
    RecyclerView.Adapter<CategoryReportAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvEmoji)
        val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
        val tvTransactionCount: TextView = view.findViewById(R.id.tvTransactionCount)
        val tvTotalAmount: TextView = view.findViewById(R.id.tvTotalAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = categories[position]
        holder.tvEmoji.text = item.emoji
        holder.tvCategoryName.text = item.category
        holder.tvTransactionCount.text = "${item.transactionCount} Transaksi"

        val formatRp = NumberFormat.getNumberInstance(Locale("id", "ID"))
        holder.tvTotalAmount.text = "Rp ${formatRp.format(item.totalAmount)}"
    }

    override fun getItemCount(): Int = categories.size

    fun updateData(newCategories: List<CategorySummary>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}
