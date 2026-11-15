package com.findmycar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.findmycar.data.local.dao.ParkedCarDao

@Database(entities = [ParkedCarEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun parkedCarDao(): ParkedCarDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "find_my_car_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
