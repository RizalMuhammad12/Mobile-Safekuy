package com.example.safekuy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.safekuy.data.AppDatabase
import com.example.safekuy.data.SplitCategoryTemplate
import com.example.safekuy.databinding.ActivitySplitSettingsBinding
import com.example.safekuy.databinding.ItemSplitSettingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplitSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplitSettingsBinding
    private lateinit var adapter: SettingsAdapter
    
    // Using a mutable list as our working copy
    private val workingTemplates = mutableListOf<SplitCategoryTemplate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplitSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = SettingsAdapter(workingTemplates, this::validateTotal)
        binding.rvSettings.adapter = adapter

        val dao = AppDatabase.getDatabase(this).splitDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val existing = dao.getAllTemplates().firstOrNull() ?: emptyList()
            withContext(Dispatchers.Main) {
                workingTemplates.addAll(existing)
                adapter.notifyDataSetChanged()
                validateTotal()
            }
        }

        binding.btnAdd.setOnClickListener {
            workingTemplates.add(SplitCategoryTemplate(name = "", percentage = 0))
            adapter.notifyItemInserted(workingTemplates.size - 1)
            binding.rvSettings.scrollToPosition(workingTemplates.size - 1)
            validateTotal()
        }

        binding.btnSave.setOnClickListener {
            val total = workingTemplates.sumOf { it.percentage }
            if (total == 100) {
                // Ensure no empty names
                if (workingTemplates.any { it.name.trim().isEmpty() }) {
                    Toast.makeText(this, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
                lifecycleScope.launch(Dispatchers.IO) {
                    val existing = dao.getAllTemplates().firstOrNull() ?: emptyList()
                    existing.forEach { dao.deleteTemplate(it.id) }
                    
                    workingTemplates.forEach { 
                        dao.insertTemplate(SplitCategoryTemplate(name = it.name.trim(), percentage = it.percentage))
                    }
                    
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SplitSettingsActivity, "Pengaturan disimpan", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Total persentase harus 100%", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateTotal() {
        val total = workingTemplates.sumOf { it.percentage }
        binding.tvTotalLabel.text = "$total%"
        if (total == 100) {
            binding.tvTotalLabel.setTextColor(getColor(R.color.emerald_600))
            binding.btnSave.isEnabled = true
        } else {
            binding.tvTotalLabel.setTextColor(getColor(R.color.red_500))
            binding.btnSave.isEnabled = false
        }
    }
}

class SettingsAdapter(
    private val templates: MutableList<SplitCategoryTemplate>,
    private val onDataChanged: () -> Unit
) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSplitSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = templates.size

    inner class ViewHolder(private val binding: ItemSplitSettingBinding) : RecyclerView.ViewHolder(binding.root) {
        
        private var isBinding = false

        fun bind(position: Int) {
            isBinding = true
            val item = templates[position]
            binding.etCategoryName.setText(item.name)
            binding.etPercentage.setText(if (item.percentage == 0 && item.name.isEmpty()) "" else item.percentage.toString())
            isBinding = false

            // Remove old listeners to avoid multiple attachments during recycle
            binding.etCategoryName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    templates[adapterPosition] = templates[adapterPosition].copy(name = binding.etCategoryName.text.toString())
                }
            }

            binding.etPercentage.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val pct = binding.etPercentage.text.toString().toIntOrNull() ?: 0
                    templates[adapterPosition] = templates[adapterPosition].copy(percentage = pct)
                    onDataChanged()
                }
            }
            
            // TextWatchers for immediate validation update
            binding.etPercentage.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!isBinding && adapterPosition != RecyclerView.NO_POSITION) {
                        val pct = s?.toString()?.toIntOrNull() ?: 0
                        templates[adapterPosition] = templates[adapterPosition].copy(percentage = pct)
                        onDataChanged()
                    }
                }
            })
            
            binding.etCategoryName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!isBinding && adapterPosition != RecyclerView.NO_POSITION) {
                        templates[adapterPosition] = templates[adapterPosition].copy(name = s?.toString() ?: "")
                    }
                }
            })

            binding.btnDelete.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    templates.removeAt(pos)
                    notifyItemRemoved(pos)
                    notifyItemRangeChanged(pos, templates.size)
                    onDataChanged()
                }
            }
        }
    }
}
