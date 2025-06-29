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

// ViewModel encargado de la lógica de autenticación y registro de usuarios
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = UserRepository(db.userDao())
    }

    // Inicia sesión con email y contraseña. Actualiza el estado según el resultado.
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Validación básica
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    _authState.value = AuthState.Error("Email inválido")
                    return@launch
                }
                if (password.length < 6) {
                    _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
                    return@launch
                }
                val user = repository.authenticateUser(email, password)
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Error("Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    // Registra un nuevo usuario. Actualiza el estado según el resultado.
    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Validación básica
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    _authState.value = AuthState.Error("Email inválido")
                    return@launch
                }
                if (password.length < 6) {
                    _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
                    return@launch
                }
                if (name.isBlank()) {
                    _authState.value = AuthState.Error("El nombre no puede estar vacío")
                    return@launch
                }
                val result = repository.registerUser(email, password, name)
                result.fold(
                    onSuccess = { userId ->
                        val user = User(id = userId, email = email, password = password, name = name)
                        _currentUser.value = user
                        _authState.value = AuthState.Success(user)
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Error(exception.message ?: "Error en el registro")
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error en el registro")
            }
        }
    }

    // Cierra la sesión del usuario actual
    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Initial
    }

    // Limpia el estado de error si existe
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Initial
        }
    }
}

// Estados posibles de la autenticación
sealed class AuthState {
    object Initial : AuthState() // Estado inicial
    object Loading : AuthState() // Estado de carga
    data class Success(val user: User) : AuthState() // Autenticación exitosa
    data class Error(val message: String) : AuthState() // Error en autenticación o registro
} 