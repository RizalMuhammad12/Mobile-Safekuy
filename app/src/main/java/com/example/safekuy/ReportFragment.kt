package com.example.safekuy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

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
