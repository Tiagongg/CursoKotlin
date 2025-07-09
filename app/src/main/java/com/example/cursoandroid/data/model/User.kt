package com.example.cursoandroid.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad de usuario para la base de datos Room
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Identificador único del usuario
    val email: String, // Correo electrónico del usuario
    val password: String, // Contraseña (en un caso real debería estar hasheada)
    val name: String, // Nombre del usuario
    val lastName: String = "", // Apellido del usuario
    val age: Int = 0, // Edad del usuario
    val profileImageUrl: String? = null, // URL de la imagen de perfil (opcional)
    val createdAt: Long = System.currentTimeMillis() // Fecha de creación
) 