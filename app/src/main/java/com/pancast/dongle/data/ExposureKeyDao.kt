package com.pancast.dongle.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExposureKeyDao {
    // get all entries that are less than 14 days old
    @Query("SELECT * FROM exposure_keys")
    fun getAll(): List<ExposureKey>

    // insert new entry
    @Insert
    fun insert(exposureKey: ExposureKey)
}