package com.example.safekuy

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.safekuy.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {

    private val viewModel: TransactionViewModel by viewModels()
    private var selectedType = "pengeluaran"
    private var selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        setupToggle()
        setupForm()
        observeViewModel()
    }

    private fun setupToggle() {
        val btnPengeluaran = findViewById<TextView>(R.id.btnPengeluaran)
        val btnPemasukan = findViewById<TextView>(R.id.btnPemasukan)

        btnPengeluaran.setOnClickListener {
            selectedType = "pengeluaran"
            btnPengeluaran.setBackgroundResource(R.drawable.bg_saldo_card)
            btnPengeluaran.elevation = 2f
            btnPengeluaran.setTextColor(resources.getColor(R.color.red_500, theme))
            btnPemasukan.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            btnPemasukan.elevation = 0f
            btnPemasukan.setTextColor(resources.getColor(R.color.slate_500, theme))
        }

        btnPemasukan.setOnClickListener {
            selectedType = "pemasukan"
            btnPemasukan.setBackgroundResource(R.drawable.bg_saldo_card)
            btnPemasukan.elevation = 2f
            btnPemasukan.setTextColor(resources.getColor(R.color.emerald_500, theme))
            btnPengeluaran.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            btnPengeluaran.elevation = 0f
            btnPengeluaran.setTextColor(resources.getColor(R.color.slate_500, theme))
        }
    }

    private fun setupForm() {
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etNote = findViewById<EditText>(R.id.etNote)

        btnBack.setOnClickListener { finish() }

        // Display current date
        updateDateDisplay()

        tvDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d)
                updateDateDisplay()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnSubmit.setOnClickListener {
            val amountText = etAmount.text.toString()
            val note = etNote.text.toString()

            if (amountText.isBlank()) {
                Toast.makeText(this, "Masukkan nominal!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Nominal tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.setSelectedDate(selectedDate)
            viewModel.addManualTransaction(selectedType, amount, note.ifEmpty { selectedType.replaceFirstChar { it.uppercase() } })
            Toast.makeText(this, "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.error.collectLatest { errorMsg ->
                        errorMsg?.let {
                            Toast.makeText(this@AddTransactionActivity, it, Toast.LENGTH_LONG).show()
                            viewModel.clearError()
                        }
                    }
                }
            }
        }
    }

    private fun updateDateDisplay() {
        val tvDate = findViewById<TextView>(R.id.tvDate)
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val date = inputFormat.parse(selectedDate)
            tvDate.text = if (date != null) outputFormat.format(date) else selectedDate
        } catch (e: Exception) {
            tvDate.text = selectedDate
        }
    }
}
