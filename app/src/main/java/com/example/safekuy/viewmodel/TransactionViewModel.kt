package com.example.safekuy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.safekuy.data.AppDatabase
import com.example.safekuy.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _totalPemasukan = MutableStateFlow(0.0)
    val totalPemasukan: StateFlow<Double> = _totalPemasukan.asStateFlow()

    private val _totalPengeluaran = MutableStateFlow(0.0)
    val totalPengeluaran: StateFlow<Double> = _totalPengeluaran.asStateFlow()

    private val _categorySummaries = MutableStateFlow<List<com.example.safekuy.data.CategorySummary>>(emptyList())
    val categorySummaries: StateFlow<List<com.example.safekuy.data.CategorySummary>> = _categorySummaries.asStateFlow()

    private val _selectedDate = MutableStateFlow(getCurrentDate())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedMonth = MutableStateFlow(getCurrentMonth())
    val selectedMonth: StateFlow<String> = _selectedMonth.asStateFlow()

    private val _monthlyTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val monthlyTransactions: StateFlow<List<Transaction>> = _monthlyTransactions.asStateFlow()

    private val _monthlyTotalPemasukan = MutableStateFlow(0.0)
    val monthlyTotalPemasukan: StateFlow<Double> = _monthlyTotalPemasukan.asStateFlow()

    private val _monthlyTotalPengeluaran = MutableStateFlow(0.0)
    val monthlyTotalPengeluaran: StateFlow<Double> = _monthlyTotalPengeluaran.asStateFlow()

    private val _monthlyCategorySummaries = MutableStateFlow<List<com.example.safekuy.data.CategorySummary>>(emptyList())
    val monthlyCategorySummaries: StateFlow<List<com.example.safekuy.data.CategorySummary>> = _monthlyCategorySummaries.asStateFlow()

    init {
        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(transactionDao)
        loadDataForSelectedDate()
        loadDataForSelectedMonth()
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getCurrentMonth(): String {
        return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
        loadDataForSelectedDate()
    }

    fun setSelectedMonth(month: String) {
        _selectedMonth.value = month
        loadDataForSelectedMonth()
    }

    private fun loadDataForSelectedDate() {
        val date = _selectedDate.value
        viewModelScope.launch {
            repository.getTransactionsByDate(date).collectLatest { list ->
                _transactions.value = list
            }
        }
        viewModelScope.launch {
            repository.getTotalPemasukan(date).collectLatest { total ->
                _totalPemasukan.value = total ?: 0.0
            }
        }
        viewModelScope.launch {
            repository.getTotalPengeluaran(date).collectLatest { total ->
                _totalPengeluaran.value = total ?: 0.0
            }
        }
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(getApplication())
            db.transactionDao().getCategorySummariesByDate(date).collectLatest { list ->
                _categorySummaries.value = list
            }
        }
    }
    
    private fun loadDataForSelectedMonth() {
        val month = _selectedMonth.value
        viewModelScope.launch {
            repository.getTransactionsByMonth(month).collectLatest { list ->
                _monthlyTransactions.value = list
            }
        }
        viewModelScope.launch {
            repository.getTotalPemasukanByMonth(month).collectLatest { total ->
                _monthlyTotalPemasukan.value = total ?: 0.0
            }
        }
        viewModelScope.launch {
            repository.getTotalPengeluaranByMonth(month).collectLatest { total ->
                _monthlyTotalPengeluaran.value = total ?: 0.0
            }
        }
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(getApplication())
            db.transactionDao().getCategorySummariesByMonth("$month%").collectLatest { list ->
                _monthlyCategorySummaries.value = list
            }
        }
    }

    fun addManualTransaction(type: String, amount: Double, note: String, categoryInput: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                var finalCategory = categoryInput
                var finalEmoji = if (type == "pemasukan") "📈" else "💸"

                if (finalCategory.isBlank()) {
                    // Opsi 3: Gunakan AI untuk menebak kategori jika kosong
                    val generativeModel = com.google.ai.client.generativeai.GenerativeModel(
                        modelName = "gemini-2.5-flash",
                        apiKey = com.example.safekuy.BuildConfig.API_KEY,
                        generationConfig = com.google.ai.client.generativeai.type.generationConfig {
                            responseMimeType = "application/json"
                        }
                    )
                    val prompt = """
                        Tebak "category" singkat (1-2 kata, misal: Makanan, Transportasi, Hiburan, Tagihan, Belanja, Gaji, dll) dan satu karakter "emoji" untuk transaksi $type berikut dengan catatan: "$note".
                        Format JSON:
                        {
                            "category": "<kategori>",
                            "emoji": "<emoji>"
                        }
                    """.trimIndent()
                    
                    try {
                        val response = generativeModel.generateContent(prompt)
                        val jsonObject = org.json.JSONObject(response.text?.trim() ?: "{}")
                        finalCategory = jsonObject.optString("category", "Lainnya")
                        finalEmoji = jsonObject.optString("emoji", finalEmoji)
                    } catch (e: Exception) {
                        finalCategory = "Lainnya"
                    }
                }

                val trx = com.example.safekuy.data.Transaction(
                    type = type,
                    category = finalCategory,
                    amount = amount,
                    note = note,
                    date = _selectedDate.value,
                    emoji = finalEmoji
                )
                repository.insertManual(trx)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
    
    fun deleteAllTransactions() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun addAiTransaction(text: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.insertViaAi(text, _selectedDate.value)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
