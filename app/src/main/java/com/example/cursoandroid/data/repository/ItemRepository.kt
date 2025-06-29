package com.example.cursoandroid.data.repository

import com.example.cursoandroid.data.dao.ItemDao
import com.example.cursoandroid.data.model.Item
import kotlinx.coroutines.flow.Flow

class ItemRepository(
    private val itemDao: ItemDao
) {
    fun getItemsByUser(userId: Long): Flow<List<Item>> {
        return itemDao.getItemsByUser(userId)
    }
    
    fun getItemsByUserAndCategory(userId: Long, category: String): Flow<List<Item>> {
        return itemDao.getItemsByUserAndCategory(userId, category)
    }
    
    suspend fun getItemsByUserSync(userId: Long): List<Item> {
        return itemDao.getItemsByUserSync(userId)
    }
    
    suspend fun insertItem(item: Item): Long {
        return itemDao.insertItem(item)
    }
    
    suspend fun updateItem(item: Item) {
        itemDao.updateItem(item)
    }
    
    suspend fun deleteItem(item: Item) {
        itemDao.deleteItem(item)
    }
    
    fun getCategoriesByUser(userId: Long): Flow<List<String>> {
        return itemDao.getCategoriesByUser(userId)
    }

    suspend fun getItemById(itemId: Long): Item? {
        return itemDao.getItemById(itemId)
    }
} 