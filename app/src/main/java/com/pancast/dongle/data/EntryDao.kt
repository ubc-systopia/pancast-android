package com.pancast.dongle.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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

    @Query("SELECT * FROM entry WHERE ephemeralID = :ephID")
    fun getEntry(ephID: String): Entry

    // insert new entry
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entry: Entry)

    @Query("UPDATE entry SET " +
            "beaconTimeInterval = :beaconTimeInterval, dongleTimeInterval = :dongleTimeInterval, " +
            "numObservations = numObservations + 1, rssi = rssi + :rssi " +
            "WHERE ephemeralID = :ephID")
    fun update(ephID: String, beaconTimeInterval: Int, dongleTimeInterval: Int, rssi: Int)

    // delete all entries older than 14 days old
    @Query("DELETE FROM entry WHERE (:currTime) - dongleTime >= $MINUTES_IN_WINDOW")
    fun delete(currTime: Long)

    // delete all entries
    @Query("DELETE FROM entry")
    fun deleteAll()

    // delete entry with matching ephID
    @Query("DELETE FROM entry WHERE ephemeralID = :ephID AND dongleTime = :dongleTime" +
            " AND rssi = :rssi")
    fun deleteOneEntry(ephID: String, dongleTime: Int, rssi: Int)
}