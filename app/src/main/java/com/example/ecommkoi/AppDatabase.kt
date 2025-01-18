package com.example.ecommkoi

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Order::class, Product::class], version = 9) // ✅ Increment version if needed
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ecommkoi-database"
                )
                    .fallbackToDestructiveMigration() // Wipes database if schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}