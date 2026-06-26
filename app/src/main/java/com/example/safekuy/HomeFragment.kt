package com.example.safekuy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safekuy.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var calendarAdapter: CalendarAdapter
    private var _view: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _view = inflater.inflate(R.layout.fragment_home, container, false)
        return _view!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(requireActivity())[TransactionViewModel::class.java]

        setupCalendar()
        setupRecyclerView()
        setupHeader()
        setupQuickAiInput()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModel.setSelectedDate(currentDate)
        calendarAdapter.setData(currentDate)
    }

    private fun setupQuickAiInput() {
        val etQuickAiInput = _view?.findViewById<android.widget.EditText>(R.id.etQuickAiInput)
        val btnQuickSendAi = _view?.findViewById<android.widget.Button>(R.id.btnQuickSendAi)
        
        btnQuickSendAi?.setOnClickListener {
            val input = etQuickAiInput?.text.toString()
            if (input.isBlank()) {
                Toast.makeText(context, "Masukkan teks transaksi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addAiTransaction(input)
        }
    }

    private fun setupHeader() {
        val btnToday = _view?.findViewById<TextView>(R.id.btnToday)
        val tvMonthYear = _view?.findViewById<TextView>(R.id.tvMonthYear)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedDate.collectLatest { date ->
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                btnToday?.visibility = if (date == today) View.GONE else View.VISIBLE

                try {
                    val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
                    tvMonthYear?.text = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(d!!)
                } catch (e: Exception) {
                    tvMonthYear?.text = date
                }
            }
        }

        btnToday?.setOnClickListener {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            viewModel.setSelectedDate(today)
            calendarAdapter.setData(today)
            _view?.findViewById<RecyclerView>(R.id.rvCalendar)?.scrollToPosition(calendarAdapter.getSelectedPosition())
        }
    }

    private fun setupCalendar() {
        val rvCalendar = _view?.findViewById<RecyclerView>(R.id.rvCalendar) ?: return
        calendarAdapter = CalendarAdapter { selectedDate ->
            viewModel.setSelectedDate(selectedDate)
            calendarAdapter.setData(selectedDate)
        }
        
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCalendar.layoutManager = layoutManager
        rvCalendar.adapter = calendarAdapter
        
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        calendarAdapter.setData(today)
        
        // Scroll to center
        rvCalendar.post {
            layoutManager.scrollToPositionWithOffset(calendarAdapter.getSelectedPosition(), rvCalendar.width / 2 - 100)
        }
    }

    private fun setupRecyclerView() {
        val rvTransactions = _view?.findViewById<RecyclerView>(R.id.rvTransactions) ?: return
        transactionAdapter = TransactionAdapter { transaction ->
            viewModel.deleteTransaction(transaction)
            Toast.makeText(requireContext(), "Transaksi dihapus", Toast.LENGTH_SHORT).show()
        }
        rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        rvTransactions.adapter = transactionAdapter
        rvTransactions.isNestedScrollingEnabled = false
    }

    private fun observeViewModel() {
        val tvSaldo = _view?.findViewById<TextView>(R.id.tvSaldo)
        val tvTotalPemasukan = _view?.findViewById<TextView>(R.id.tvTotalPemasukan)
        val tvTotalPengeluaran = _view?.findViewById<TextView>(R.id.tvTotalPengeluaran)
        val progressBar = _view?.findViewById<ProgressBar>(R.id.progressBar)
        val llEmpty = _view?.findViewById<LinearLayout>(R.id.llEmpty)
        val progressQuickAi = _view?.findViewById<ProgressBar>(R.id.progressQuickAi)
        val btnQuickSendAi = _view?.findViewById<android.widget.Button>(R.id.btnQuickSendAi)
        val etQuickAiInput = _view?.findViewById<android.widget.EditText>(R.id.etQuickAiInput)
        val formatRp = NumberFormat.getNumberInstance(Locale("id", "ID"))

        var currentPemasukan = 0.0
        var currentPengeluaran = 0.0

        fun updateSaldo() {
            val saldo = currentPemasukan - currentPengeluaran
            tvSaldo?.text = "Rp ${formatRp.format(saldo)}"
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.transactions.collectLatest { list ->
                        transactionAdapter.submitList(list)
                        llEmpty?.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.totalPemasukan.collectLatest { total ->
                        currentPemasukan = total
                        tvTotalPemasukan?.text = "Rp ${formatRp.format(total)}"
                        updateSaldo()
                    }
                }
                launch {
                    viewModel.totalPengeluaran.collectLatest { total ->
                        currentPengeluaran = total
                        tvTotalPengeluaran?.text = "Rp ${formatRp.format(total)}"
                        updateSaldo()
                    }
                }
                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
                        progressQuickAi?.visibility = if (isLoading) View.VISIBLE else View.GONE
                        btnQuickSendAi?.isEnabled = !isLoading
                        btnQuickSendAi?.text = if (isLoading) "" else "Proses dengan AI"
                        
                        // Clear input if loading finished (simplistic check)
                        if (!isLoading) {
                            etQuickAiInput?.text?.clear()
                        }
                    }
                }
                launch {
                    viewModel.error.collectLatest { errorMsg ->
                        errorMsg?.let {
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            viewModel.clearError()
                        }
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
