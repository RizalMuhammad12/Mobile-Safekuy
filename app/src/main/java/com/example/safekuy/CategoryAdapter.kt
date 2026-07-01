package com.example.safekuy

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.safekuy.data.SplitCategoryItem
import com.example.safekuy.databinding.ItemSplitCategoryBinding
import java.text.NumberFormat
import java.util.*

class CategoryAdapter(
    private val onStateChange: (SplitCategoryItem, Boolean, String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var items = listOf<SplitCategoryItem>()
    private val storageOptions = arrayOf("Tunai", "Dana", "GoPay", "SeaBank", "Bank BCA", "Bank BRI", "Bank Mandiri", "Bank BNI", "OVO", "ShopeePay", "Lainnya")

    fun submitList(newItems: List<SplitCategoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSplitCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemSplitCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SplitCategoryItem) {
            val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            binding.tvCategoryName.text = item.name
            binding.tvAmount.text = formatRp.format(item.amount).replace("Rp", "Rp ")
            
            binding.ivCategoryIcon.setImageResource(android.R.drawable.ic_menu_myplaces)
            
            binding.cbSaved.setOnCheckedChangeListener(null)
            binding.cbSaved.isChecked = item.isSaved
            
            updateBadge(item.isSaved)

            val spinnerAdapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                storageOptions
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerLocation.adapter = spinnerAdapter
            
            val selectionIndex = storageOptions.indexOf(item.storageLocation)
            if (selectionIndex >= 0) {
                binding.spinnerLocation.setSelection(selectionIndex)
            }

            binding.cbSaved.setOnCheckedChangeListener { _, isChecked ->
                updateBadge(isChecked)
                val location = binding.spinnerLocation.selectedItem.toString()
                onStateChange(item, isChecked, location)
            }
            
            // To capture spinner changes without checking the checkbox again
            binding.spinnerLocation.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, pos: Int, id: Long) {
                    val location = storageOptions[pos]
                    if (location != item.storageLocation) {
                         onStateChange(item, binding.cbSaved.isChecked, location)
                    }
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
        }
        
        private fun updateBadge(isSaved: Boolean) {
            if (isSaved) {
                binding.tvStatusBadge.text = "Sudah Disimpan"
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_check)
                binding.tvStatusBadge.setTextColor(binding.root.context.getColor(R.color.emerald_600))
            } else {
                binding.tvStatusBadge.text = "Belum Disimpan"
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_uncheck)
                binding.tvStatusBadge.setTextColor(binding.root.context.getColor(R.color.slate_700))
            }
        }
    }
}
