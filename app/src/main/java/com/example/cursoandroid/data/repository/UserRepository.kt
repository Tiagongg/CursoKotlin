package com.example.cursoandroid.data.repository

import com.example.cursoandroid.data.dao.UserDao
import com.example.cursoandroid.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {
    fun getUserById(userId: Long): Flow<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun authenticateUser(email: String, password: String): User? {
        return userDao.authenticateUser(email, password)
    }

    suspend fun registerUser(email: String, password: String, name: String): Result<Long> {
        return try {
            val user = User(
                id = 0,
                email = email,
                password = password,
                name = name
            )
            val userId = insertUser(user)
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }
} 