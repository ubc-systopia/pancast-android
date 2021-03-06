package com.pancast.dongle.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// partly based off of Stevdza-San's ROOM database tutorial on YT
// remember to increment the version whenever we change the schema

// uses the singleton pattern
@Database(entities = [Entry::class, ExposureKey::class, LoggedInUser::class], version = 8)
abstract class PancastDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun exposureKeyDao(): ExposureKeyDao
    abstract fun loginDao(): LoginDao

    companion object {
        @Volatile
        private var INSTANCE: PancastDatabase? = null

        fun getDatabase(context: Context): PancastDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PancastDatabase::class.java, "pancast-android")
                    //.fallbackToDestructiveMigration() // if we change the schema, all data will be lost
                    .addMigrations(MIGRATION_7_8)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}