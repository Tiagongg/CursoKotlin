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

// ViewModel encargado de la lógica de gestión del perfil de usuario
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Initial)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _currentUserProfile = MutableStateFlow<User?>(null)
    val currentUserProfile: StateFlow<User?> = _currentUserProfile.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = UserRepository(db.userDao())
    }

    // Carga el perfil del usuario por ID
    fun loadUserProfile(userId: Long) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                repository.getUserById(userId).collect { user ->
                    _currentUserProfile.value = user
                    if (user != null) {
                        _profileState.value = ProfileState.Success
                    } else {
                        _profileState.value = ProfileState.Error("Usuario no encontrado")
                    }
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Error al cargar perfil")
            }
        }
    }

    // Actualiza el perfil del usuario
    fun updateProfile(
        userId: Long,
        name: String,
        lastName: String,
        age: Int,
        profileImageUrl: String?
    ) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                // Validaciones básicas
                if (name.isBlank()) {
                    _profileState.value = ProfileState.Error("El nombre no puede estar vacío")
                    return@launch
                }
                if (lastName.isBlank()) {
                    _profileState.value = ProfileState.Error("El apellido no puede estar vacío")
                    return@launch
                }
                if (age < 0 || age > 150) {
                    _profileState.value = ProfileState.Error("Edad debe estar entre 0 y 150 años")
                    return@launch
                }

                val result = repository.updateUserProfile(userId, name, lastName, age, profileImageUrl)
                result.fold(
                    onSuccess = {
                        _profileState.value = ProfileState.UpdateSuccess
                        // Recargar el perfil actualizado
                        loadUserProfile(userId)
                    },
                    onFailure = { exception ->
                        _profileState.value = ProfileState.Error(exception.message ?: "Error al actualizar perfil")
                    }
                )
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Error al actualizar perfil")
            }
        }
    }

    // Limpia el estado de error si existe
    fun clearError() {
        if (_profileState.value is ProfileState.Error) {
            _profileState.value = ProfileState.Initial
        }
    }

    // Limpia el estado de éxito si existe
    fun clearSuccess() {
        if (_profileState.value is ProfileState.UpdateSuccess) {
            _profileState.value = ProfileState.Success
        }
    }
}

// Estados posibles del perfil
sealed class ProfileState {
    object Initial : ProfileState() // Estado inicial
    object Loading : ProfileState() // Estado de carga
    object Success : ProfileState() // Perfil cargado exitosamente
    object UpdateSuccess : ProfileState() // Perfil actualizado exitosamente
    data class Error(val message: String) : ProfileState() // Error en operación
} 