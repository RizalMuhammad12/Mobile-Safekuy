package com.example.safekuy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.safekuy.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplitViewModel(application: Application) : AndroidViewModel(application) {

    private val splitDao: SplitDao = AppDatabase.getDatabase(application).splitDao()

    private val _currentSplit = MutableLiveData<DailySplit?>()
    val currentSplit: LiveData<DailySplit?> = _currentSplit
    
    private val _currentItems = MutableLiveData<List<SplitCategoryItem>>()
    val currentItems: LiveData<List<SplitCategoryItem>> = _currentItems
    
    val templates = splitDao.getAllTemplates()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (splitDao.getTemplateCount() == 0) {
                // Insert defaults
                splitDao.insertTemplate(SplitCategoryTemplate(name = "Cicilan", percentage = 39))
                splitDao.insertTemplate(SplitCategoryTemplate(name = "Bensin", percentage = 20))
                splitDao.insertTemplate(SplitCategoryTemplate(name = "Tabungan", percentage = 25))
                splitDao.insertTemplate(SplitCategoryTemplate(name = "Dana Cadangan", percentage = 16))
            }
        }
    }

    fun calculateAndSaveSplit(income: Double, date: Long, note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTemplates = templates.firstOrNull() ?: return@launch
            
            val newSplit = DailySplit(
                date = date,
                income = income,
                note = note
            )
            val splitId = splitDao.insertSplit(newSplit)
            val insertedSplit = newSplit.copy(id = splitId)
            
            val itemsToInsert = currentTemplates.map { template ->
                SplitCategoryItem(
                    dailySplitId = splitId,
                    name = template.name,
                    amount = income * template.percentage / 100.0,
                    isSaved = false,
                    storageLocation = "Tunai"
                )
            }
            
            splitDao.insertCategoryItems(itemsToInsert)
            val insertedItems = splitDao.getItemsForSplit(splitId)
            
            withContext(Dispatchers.Main) {
                _currentSplit.value = insertedSplit
                _currentItems.value = insertedItems
            }
        }
    }

    fun updateCategoryState(
        item: SplitCategoryItem,
        isSaved: Boolean,
        storageLocation: String
    ) {
        val updatedItem = item.copy(isSaved = isSaved, storageLocation = storageLocation)
        
        viewModelScope.launch(Dispatchers.IO) {
            splitDao.updateCategoryItem(updatedItem)
            
            val split = _currentSplit.value ?: return@launch
            val updatedItems = splitDao.getItemsForSplit(split.id)
            withContext(Dispatchers.Main) {
                _currentItems.value = updatedItems
            }
        }
    }
    
    // For settings management
    fun saveNewTemplate(name: String, percentage: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            splitDao.insertTemplate(SplitCategoryTemplate(name = name, percentage = percentage))
        }
    }
    
    fun updateTemplate(template: SplitCategoryTemplate) {
        viewModelScope.launch(Dispatchers.IO) {
            splitDao.updateTemplate(template)
        }
    }
    
    fun deleteTemplate(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            splitDao.deleteTemplate(id)
        }
    }
}
