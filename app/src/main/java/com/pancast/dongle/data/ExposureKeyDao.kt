package com.pancast.dongle.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pancast.dongle.utilities.Constants

@Dao
interface ExposureKeyDao {
    // get all keys that are less than 14 days old
    @Query("SELECT * FROM exposure_keys WHERE (:currTime) - time < ${Constants.MINUTES_IN_WINDOW}")
    fun getAll(currTime: Long): List<ExposureKey>

    // insert new key
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(exposureKey: ExposureKey)
}