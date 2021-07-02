package com.example.bluetooth_sample.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

const val MINUTES_IN_WINDOW = 20160

@Dao
interface EntryDao {
    // get all entries that are less than 14 days old
    @Query("SELECT * FROM entry WHERE (:currTime) - dongleTime < $MINUTES_IN_WINDOW")
    fun getAll(currTime: Long): List<Entry>

    // insert new entry
    @Insert
    fun insert(entry: Entry)

    // delete all entries older than 14 days old
    @Query("DELETE FROM entry WHERE (:currTime) - dongleTime >= $MINUTES_IN_WINDOW")
    fun delete(currTime: Long)
}