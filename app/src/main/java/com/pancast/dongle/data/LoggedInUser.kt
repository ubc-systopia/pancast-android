package com.pancast.dongle.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.*
import kotlinx.android.parcel.Parcelize

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Entity(primaryKeys = ["displayName"])
@Parcelize
data class LoggedInUser(
    val userId: String,
    val displayName: String,
    val devId: String
): Parcelable

@Dao
interface LoginDao {
    @Query("SELECT * FROM LoggedInUser WHERE displayName = :displayName")
    fun getEntry(displayName: String): LoggedInUser

    // insert new entry
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entry: LoggedInUser)

}