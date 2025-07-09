package com.example.cursoandroid.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cursoandroid.data.database.AppDatabase
import com.example.cursoandroid.data.model.User
import com.example.cursoandroid.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel encargado de la gestión de datos del usuario
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = UserRepository(db.userDao())
    }

    // Carga el usuario por ID
    fun loadUserById(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = repository.getUserByIdSync(userId)
                _currentUser.value = user
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar el usuario"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Actualiza el usuario actual
    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    // Limpia el estado de error
    fun clearError() {
        _error.value = null
    }
} 