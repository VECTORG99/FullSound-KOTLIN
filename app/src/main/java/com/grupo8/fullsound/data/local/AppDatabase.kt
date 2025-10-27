package com.grupo8.fullsound.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.grupo8.fullsound.data.models.User
import com.grupo8.fullsound.data.models.Beat
import com.grupo8.fullsound.data.models.CarritoItem

@Database(
    entities = [User::class, Beat::class, CarritoItem::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun beatDao(): BeatDao
    abstract fun carritoDao(): CarritoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fullsound_database"
                )
                    .fallbackToDestructiveMigration() // Destruye y recrea la BD si cambia el esquema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}