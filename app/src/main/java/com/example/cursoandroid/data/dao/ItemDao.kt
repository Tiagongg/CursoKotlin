package com.example.cursoandroid.data.dao

import androidx.room.*
import com.example.cursoandroid.data.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE userId = :userId ORDER BY createdAt DESC")
    fun getItemsByUser(userId: Long): Flow<List<Item>>
    
    @Query("SELECT * FROM items WHERE userId = :userId AND category = :category ORDER BY createdAt DESC")
    fun getItemsByUserAndCategory(userId: Long, category: String): Flow<List<Item>>
    
    @Query("SELECT * FROM items WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getItemsByUserSync(userId: Long): List<Item>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long
    
    @Update
    suspend fun updateItem(item: Item)
    
    @Delete
    suspend fun deleteItem(item: Item)
    
    @Query("SELECT DISTINCT category FROM items WHERE userId = :userId")
    fun getCategoriesByUser(userId: Long): Flow<List<String>>

    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun getItemById(itemId: Long): Item?
} 