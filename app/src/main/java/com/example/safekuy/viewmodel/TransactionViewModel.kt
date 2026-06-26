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

    private val _selectedDate = MutableStateFlow(getCurrentDate())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(transactionDao)
        loadDataForSelectedDate()
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
        loadDataForSelectedDate()
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
    }

    fun addManualTransaction(type: String, amount: Double, note: String) {
        viewModelScope.launch {
            val trx = Transaction(
                type = type,
                amount = amount,
                note = note,
                date = _selectedDate.value,
                emoji = if (type == "pemasukan") "📈" else "💸"
            )
            repository.insertManual(trx)
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
