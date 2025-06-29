package com.example.cursoandroid.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val price: Double = 0.0,
    val imageUrl: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val userId: Long = 1,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ItemCategory(val displayName: String) {
    TECHNOLOGY("Tecnología"),
    FOOD("Comida"),
    TRAVEL("Viajes"),
    SPORTS("Deportes"),
    ART("Arte"),
    OTHER("Otros")
} 