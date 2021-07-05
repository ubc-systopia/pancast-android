package com.pancast.dongle.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// partly based off of Stevdza-San's ROOM database tutorial on YT
// remember to increment the version whenever we change the schema

// uses the singleton pattern
@Database(entities = [Entry::class], version = 2, exportSchema = false)
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
                    .fallbackToDestructiveMigration() // if we change the schema, all data will be lost
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}


