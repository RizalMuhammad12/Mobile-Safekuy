package com.example.safekuy

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.safekuy.data.AppDatabase
import com.example.safekuy.databinding.ActivitySplitStatsBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class SplitStatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplitStatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplitStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Configure Chart
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setUsePercentValues(false)
        binding.pieChart.isDrawHoleEnabled = true
        binding.pieChart.setHoleColor(Color.WHITE)
        binding.pieChart.legend.isEnabled = true
        binding.pieChart.legend.isWordWrapEnabled = true

        val dao = AppDatabase.getDatabase(this).splitDao()
        
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        val startTimestamp = calendar.timeInMillis
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        val endTimestamp = calendar.timeInMillis

        lifecycleScope.launch {
            val totalIncome = dao.getTotalIncome(startTimestamp, endTimestamp).firstOrNull() ?: 0.0
            val stats = dao.getCategoryStats(startTimestamp, endTimestamp).firstOrNull() ?: emptyList()

            val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            binding.tvTotalIncome.text = formatRp.format(totalIncome).replace("Rp", "Rp ")

            val entries = ArrayList<PieEntry>()
            stats.forEach { stat ->
                if (stat.total > 0) {
                    entries.add(PieEntry(stat.total.toFloat(), stat.name))
                }
            }

            if (entries.isNotEmpty()) {
                val dataSet = PieDataSet(entries, "")
                
                // Generate colors dynamically or use a preset list
                val colors = listOf(
                    getColor(R.color.indigo_500),
                    getColor(R.color.emerald_500),
                    getColor(R.color.yellow_300),
                    getColor(R.color.purple_600),
                    getColor(R.color.red_500),
                    getColor(R.color.slate_500)
                )
                dataSet.colors = colors
                dataSet.valueTextSize = 12f
                dataSet.valueTextColor = Color.WHITE

                val data = PieData(dataSet)
                binding.pieChart.data = data
                binding.pieChart.invalidate()
            }
        }
    }
}
