package com.example.cursoandroid.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cursoandroid.data.database.AppDatabase
import com.example.cursoandroid.data.model.Item
import com.example.cursoandroid.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class ItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ItemRepository
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val filteredItems: StateFlow<List<Item>> = combine(_items, _selectedCategory) { items, category ->
        if (category != null && category != "Todos") {
            items.filter { it.category == category }
        } else {
            items
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ItemRepository(db.itemDao())
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getItemsByUser(1L).collectLatest { itemsList ->
                    _items.value = itemsList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar los items"
                _isLoading.value = false
            }
        }
    }

    fun addItem(item: Item) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.insertItem(item)
                _isLoading.value = false
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al agregar el item"
                _isLoading.value = false
            }
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateItem(item)
                _isLoading.value = false
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al actualizar el item"
                _isLoading.value = false
            }
        }
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val item = _items.value.find { it.id == itemId }
                if (item != null) {
                    repository.deleteItem(item)
                }
                _isLoading.value = false
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al eliminar el item"
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun clearError() {
        _error.value = null
    }
} 