package com.example.safekuy

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.safekuy.data.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private val onDelete: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val flEmoji: FrameLayout = itemView.findViewById(R.id.flEmoji)
        private val tvEmoji: TextView = itemView.findViewById(R.id.tvEmoji)
        private val tvNote: TextView = itemView.findViewById(R.id.tvNote)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val btnDelete: TextView = itemView.findViewById(R.id.btnDelete)
        private val btnEdit: TextView = itemView.findViewById(R.id.btnEdit)
        private val llActions: LinearLayout = itemView.findViewById(R.id.llActions)

        fun bind(transaction: Transaction) {
            tvEmoji.text = transaction.emoji
            tvNote.text = transaction.note.ifEmpty { 
                transaction.type.replaceFirstChar { it.uppercase() } 
            }

            // Format date
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                val date = inputFormat.parse(transaction.date)
                val formattedDate = if (date != null) outputFormat.format(date) else transaction.date
                tvDate.text = "$formattedDate • ${transaction.type.replaceFirstChar { it.uppercase() }}"
            } catch (e: Exception) {
                tvDate.text = "${transaction.date} • ${transaction.type.replaceFirstChar { it.uppercase() }}"
            }

            // Format amount
            val formatRp = NumberFormat.getNumberInstance(Locale("id", "ID"))
            val formattedAmount = formatRp.format(transaction.amount)

            if (transaction.type.lowercase() == "pemasukan") {
                tvAmount.text = "+Rp $formattedAmount"
                tvAmount.setTextColor(Color.parseColor("#10B981"))
                flEmoji.setBackgroundResource(R.drawable.bg_emoji_pemasukan)
            } else {
                tvAmount.text = "-Rp $formattedAmount"
                tvAmount.setTextColor(Color.parseColor("#1E293B"))
                flEmoji.setBackgroundResource(R.drawable.bg_emoji_pengeluaran)
            }

            btnDelete.setOnClickListener { onDelete(transaction) }
            
            // Edit not implemented for now, hide it
            btnEdit.visibility = View.GONE
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}
