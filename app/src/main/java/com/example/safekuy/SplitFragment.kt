package com.example.safekuy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.safekuy.data.DailySplit
import com.example.safekuy.data.SplitCategoryItem
import com.example.safekuy.databinding.FragmentSplitBinding
import com.example.safekuy.databinding.ItemSplitCategoryBinding
import com.example.safekuy.viewmodel.SplitViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class SplitFragment : Fragment() {

    private var _binding: FragmentSplitBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: SplitViewModel
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(SplitViewModel::class.java)

        // Set default date to today
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etDate.setText(dateFormat.format(Date()))

        categoryAdapter = CategoryAdapter { item, isSaved, storage ->
            viewModel.updateCategoryState(item, isSaved, storage)
        }
        binding.rvSplitCategories.adapter = categoryAdapter

        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            try {
                val date = dateFormat.parse(binding.etDate.text.toString())
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {}
            
            android.app.DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.etDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnCalculate.setOnClickListener {
            val incomeStr = binding.etIncome.text.toString()
            if (incomeStr.isEmpty()) {
                Toast.makeText(requireContext(), "Masukkan pendapatan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val income = incomeStr.toDoubleOrNull() ?: 0.0
            var date = System.currentTimeMillis()
            try {
                val parsedDate = dateFormat.parse(binding.etDate.text.toString())
                if (parsedDate != null) date = parsedDate.time
            } catch (e: Exception) {}
            
            val note = binding.etNote.text.toString()

            viewModel.calculateAndSaveSplit(income, date, note)
            
            // Show results with slide up/fade in animation
            if (binding.layoutResults.visibility == View.GONE) {
                binding.layoutResults.visibility = View.VISIBLE
                binding.layoutResults.alpha = 0f
                binding.layoutResults.translationY = 50f
                binding.layoutResults.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .start()
            }
        }
        
        viewModel.currentItems.observe(viewLifecycleOwner) { items ->
            if (items != null) {
                categoryAdapter.submitList(items)
            }
        }
        
        binding.btnHistory.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), SplitHistoryActivity::class.java))
        }
        
        binding.btnStats.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), SplitStatsActivity::class.java))
        }
        
        binding.btnSettings.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), SplitSettingsActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
