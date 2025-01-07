package com.example.ecommkoi

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class, Product::class, Order::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Define migration logic (optional, for preserving data)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example: Add new column or modify schema here
                // database.execSQL("ALTER TABLE users ADD COLUMN age INTEGER DEFAULT 0 NOT NULL")
            }
        }

        // Get the database instance
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ecommkoi-database"
                )
                    .fallbackToDestructiveMigration() // Use this if data preservation is unnecessary
                    // .addMigrations(MIGRATION_1_2) // Uncomment and modify if preserving data
                    .allowMainThreadQueries() // Remove in production for better performance
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}