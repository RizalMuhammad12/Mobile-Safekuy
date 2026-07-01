package com.example.safekuy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.safekuy.data.AppDatabase
import com.example.safekuy.data.DailySplit
import com.example.safekuy.data.SplitCategoryItem
import com.example.safekuy.data.SplitDao
import com.example.safekuy.databinding.ActivitySplitHistoryBinding
import com.example.safekuy.databinding.ItemSplitHistoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class SplitHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplitHistoryBinding
    private lateinit var dao: SplitDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplitHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.toolbar.setNavigationOnClickListener { finish() }

        dao = AppDatabase.getDatabase(this).splitDao()
        val adapter = HistoryAdapter(dao) { split ->
            showEditDialog(split)
        }
        binding.rvHistory.adapter = adapter

        lifecycleScope.launch {
            dao.getAllSplits().collectLatest { splits ->
                adapter.submitList(splits)
            }
        }
    }

    private fun showEditDialog(split: DailySplit) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_edit_history, null)
        dialog.setContentView(view)
        
        val tvDateLabel = view.findViewById<TextView>(R.id.tvDateLabel)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val rvEditCategories = view.findViewById<RecyclerView>(R.id.rvEditCategories)
        
        val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        tvDateLabel.text = "${dateFormat.format(Date(split.date))} - ${formatRp.format(split.income).replace("Rp", "Rp ")}"

        // Adapter for dialog
        val workingItems = mutableListOf<SplitCategoryItem>()
        val categoryAdapter = CategoryAdapter { item, isSaved, storage ->
            val index = workingItems.indexOfFirst { it.id == item.id }
            if (index != -1) {
                workingItems[index] = item.copy(isSaved = isSaved, storageLocation = storage)
            }
        }
        rvEditCategories.adapter = categoryAdapter
        
        lifecycleScope.launch(Dispatchers.IO) {
            val items = dao.getItemsForSplit(split.id)
            workingItems.addAll(items)
            withContext(Dispatchers.Main) {
                categoryAdapter.submitList(workingItems.toList())
            }
        }

        btnSave.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                workingItems.forEach { item ->
                    dao.updateCategoryItem(item)
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SplitHistoryActivity, "Riwayat diperbarui", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }

        btnDelete.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Hapus Riwayat")
                .setMessage("Yakin ingin menghapus riwayat pembagian ini? Semua data pembagian di dalamnya akan hilang.")
                .setPositiveButton("Hapus") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        dao.deleteDailySplit(split.id)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SplitHistoryActivity, "Riwayat dihapus", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        dialog.show()
    }
}

class HistoryAdapter(
    private val dao: SplitDao,
    private val onItemClick: (DailySplit) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var items = listOf<DailySplit>()

    fun submitList(newItems: List<DailySplit>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSplitHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemSplitHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(items[adapterPosition])
                }
            }
        }

        fun bind(split: DailySplit) {
            val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            
            binding.tvDate.text = dateFormat.format(Date(split.date))
            binding.tvTotalIncome.text = "Pendapatan: ${formatRp.format(split.income).replace("Rp", "Rp ")}"
            
            kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                val categoryItems = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    dao.getItemsForSplit(split.id)
                }
                
                val summaryBuilder = java.lang.StringBuilder()
                categoryItems.forEach { item ->
                    val checkMark = if(item.isSaved) "✅" else "⬜"
                    summaryBuilder.append("${item.name}: ${formatRp.format(item.amount).replace("Rp", "Rp ")} (${item.storageLocation}) $checkMark\n")
                }
                
                binding.tvSplitSummary.text = summaryBuilder.toString().trimEnd()
            }
            
            if (split.note.isNullOrEmpty()) {
                binding.tvNote.visibility = android.view.View.GONE
            } else {
                binding.tvNote.visibility = android.view.View.VISIBLE
                binding.tvNote.text = "Catatan: ${split.note}"
            }
        }
    }
}
