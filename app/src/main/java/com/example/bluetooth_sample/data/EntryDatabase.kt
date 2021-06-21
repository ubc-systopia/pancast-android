package com.example.bluetooth_sample.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// partly based off of Stevdza-San's ROOM database tutorial on YT
@Database(entities = [Entry::class], version = 1)
abstract class EntryDatabase(): RoomDatabase() {
    abstract fun entryDao(): EntryDao

    companion object {
        @Volatile
        private var INSTANCE: EntryDatabase? = null

        fun getDatabase(context: Context): EntryDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EntryDatabase::class.java, "pancast-android")
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}


