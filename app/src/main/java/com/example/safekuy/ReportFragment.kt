package com.example.safekuy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class ReportFragment : Fragment() {

    private var _view: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view = inflater.inflate(R.layout.fragment_report, container, false)
        return _view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val viewModel = androidx.lifecycle.ViewModelProvider(requireActivity())[com.example.safekuy.viewmodel.TransactionViewModel::class.java]
        
        val tvTotalPemasukan = view.findViewById<android.widget.TextView>(R.id.tvTotalPemasukanReport)
        val tvTotalPengeluaran = view.findViewById<android.widget.TextView>(R.id.tvTotalPengeluaranReport)
        val llReportContent = view.findViewById<android.widget.LinearLayout>(R.id.llReportContent)
        val llReportEmpty = view.findViewById<android.widget.LinearLayout>(R.id.llReportEmpty)
        val rvCategoryReport = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvCategoryReport)
        val pieChart = view.findViewById<PieChart>(R.id.pieChart)
        
        val categoryAdapter = CategoryReportAdapter(emptyList())
        rvCategoryReport.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        rvCategoryReport.adapter = categoryAdapter
        
        // Setup PieChart
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleAlpha(0)
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)
        
        val formatRp = java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID"))

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            launch {
                viewModel.totalPemasukan.collect { total ->
                    tvTotalPemasukan?.text = "Rp ${formatRp.format(total)}"
                }
            }
            launch {
                viewModel.totalPengeluaran.collect { total ->
                    tvTotalPengeluaran?.text = "Rp ${formatRp.format(total)}"
                }
            }
            launch {
                viewModel.categorySummaries.collect { summaries ->
                    categoryAdapter.updateData(summaries)
                    
                    // Update PieChart
                    if (summaries.isNotEmpty()) {
                        val entries = ArrayList<PieEntry>()
                        for (summary in summaries) {
                            // Cuma tampilkan di pie chart jika nominal > 0
                            if (summary.totalAmount > 0) {
                                entries.add(PieEntry(summary.totalAmount.toFloat(), summary.category))
                            }
                        }
                        
                        val dataSet = PieDataSet(entries, "Kategori")
                        
                        // Buat daftar warna
                        val colors = ArrayList<Int>()
                        for (c in ColorTemplate.MATERIAL_COLORS) colors.add(c)
                        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
                        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
                        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
                        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
                        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
                        
                        dataSet.colors = colors
                        dataSet.valueTextColor = Color.WHITE
                        dataSet.valueTextSize = 12f
                        
                        val data = PieData(dataSet)
                        pieChart.data = data
                        pieChart.invalidate() // refresh
                        pieChart.visibility = View.VISIBLE
                    } else {
                        pieChart.visibility = View.GONE
                    }
                }
            }
            launch {
                viewModel.transactions.collect { list ->
                    if (list.isEmpty()) {
                        llReportContent?.visibility = View.GONE
                        llReportEmpty?.visibility = View.VISIBLE
                    } else {
                        llReportContent?.visibility = View.VISIBLE
                        llReportEmpty?.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _view = null
    }
}
