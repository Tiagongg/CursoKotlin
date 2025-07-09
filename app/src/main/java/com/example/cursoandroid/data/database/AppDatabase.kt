package com.example.cursoandroid.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.example.cursoandroid.data.dao.ItemDao
import com.example.cursoandroid.data.dao.UserDao
import com.example.cursoandroid.data.model.Item
import com.example.cursoandroid.data.model.User

@Database(
    entities = [User::class, Item::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migración de la versión 1 a la versión 2 para agregar campos de perfil
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE users ADD COLUMN lastName TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE users ADD COLUMN age INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE users ADD COLUMN profileImageUrl TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "curso_android_database"
                )
                .addMigrations(MIGRATION_1_2)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Insertar usuarios de prueba
                        db.execSQL("""
                            INSERT INTO users (email, password, name, lastName, age) VALUES 
                            ('usuario1@test.com', '123456', 'Juan', 'Pérez', 25),
                            ('usuario2@test.com', '123456', 'María', 'García', 30),
                            ('usuario3@test.com', '123456', 'Carlos', 'López', 28)
                        """)
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 