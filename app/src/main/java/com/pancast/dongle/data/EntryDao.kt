package com.pancast.dongle.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pancast.dongle.utilities.Constants.MINUTES_IN_WINDOW

@Dao
interface EntryDao {
    // get all entries that are less than 14 days old
    @Query("SELECT * FROM entry WHERE (:currTime) - dongleTime < $MINUTES_IN_WINDOW")
    fun getAll(currTime: Long): LiveData<List<Entry>>

    @Query("SELECT * FROM entry WHERE (:currTime) - dongleTime < $MINUTES_IN_WINDOW")
    fun getAllSynchronously(currTime: Long): List<Entry>

    @Query("SELECT COUNT(*) FROM entry WHERE ephemeralID = :ephID")
    fun getNumEntries(ephID: String): Int

    // insert new entry
    @Insert
    fun insert(entry: Entry)

    // delete all entries older than 14 days old
    @Query("DELETE FROM entry WHERE (:currTime) - dongleTime >= $MINUTES_IN_WINDOW")
    fun delete(currTime: Long)
}