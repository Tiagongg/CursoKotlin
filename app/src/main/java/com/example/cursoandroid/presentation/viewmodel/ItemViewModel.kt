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

// ViewModel encargado de la lógica de gestión de items/productos
class ItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ItemRepository
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Lista reactiva de items filtrados por categoría
    val filteredItems: StateFlow<List<Item>> = combine(_items, _selectedCategory, _searchQuery) { items, category, query ->
        items.filter { item ->
            val matchesCategory = category == null || category == "Todos" || item.category == category
            val matchesSearch = query.isEmpty() || item.title.contains(query, ignoreCase = true) || 
                               item.description.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ItemRepository(db.itemDao())
        // Al inicializar, carga todos los items (para la vista general)
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllItems().collectLatest { itemsList ->
                    _items.value = itemsList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar los items"
                _isLoading.value = false
            }
        }
    }
    
    // Obtiene items del usuario específico
    fun getItemsByUser(userId: Long): StateFlow<List<Item>> {
        val userItems = MutableStateFlow<List<Item>>(emptyList())
        viewModelScope.launch {
            repository.getItemsByUser(userId).collectLatest { itemsList ->
                userItems.value = itemsList
            }
        }
        return userItems.asStateFlow()
    }

    // Agrega un nuevo item a la base de datos
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

    // Actualiza un item existente
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

    // Elimina un item por su ID
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

    // Cambia la categoría seleccionada para el filtro
    fun filterByCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun searchItems(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    // Limpia el estado de error
    fun clearError() {
        _error.value = null
    }
    
    // Obtiene un item por ID
    suspend fun getItemById(itemId: Long): Item? {
        return repository.getItemById(itemId)
    }
} 