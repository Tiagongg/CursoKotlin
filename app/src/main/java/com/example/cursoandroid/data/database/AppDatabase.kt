package com.example.cursoandroid.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.cursoandroid.data.dao.ItemDao
import com.example.cursoandroid.data.dao.UserDao
import com.example.cursoandroid.data.model.Item
import com.example.cursoandroid.data.model.User

@Database(
    entities = [User::class, Item::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "curso_android_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 