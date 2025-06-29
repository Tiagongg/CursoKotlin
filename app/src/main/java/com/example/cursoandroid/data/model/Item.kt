package com.example.cursoandroid.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad de producto/item para la base de datos Room
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Identificador único del item
    val title: String, // Título del producto
    val description: String, // Descripción del producto
    val category: String, // Categoría del producto
    val price: Double = 0.0, // Precio
    val imageUrl: String? = null, // URL de la imagen (opcional)
    val latitude: Double = 0.0, // Latitud de la ubicación
    val longitude: Double = 0.0, // Longitud de la ubicación
    val userId: Long = 1, // ID del usuario que publicó el item
    val createdAt: Long = System.currentTimeMillis() // Fecha de creación
)

// Enumeración de categorías posibles para los items
enum class ItemCategory(val displayName: String) {
    TECHNOLOGY("Tecnología"),
    FOOD("Comida"),
    TRAVEL("Viajes"),
    SPORTS("Deportes"),
    ART("Arte"),
    OTHER("Otros")
} 