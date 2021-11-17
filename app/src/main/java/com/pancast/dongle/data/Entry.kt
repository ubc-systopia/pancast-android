package com.pancast.dongle.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.android.parcel.Parcelize

@Entity(primaryKeys = ["ephemeralID", "beaconID", "beaconTime"])
@Parcelize
data class Entry(
    // ephID, beaconID, locationID, beaconTime, deviceTime
    val ephemeralID: String,
    val beaconID: Int,
    val locationID: Long,
    val beaconTime: Int,
    val dongleTime: Int,
    val rssi: Int
): Parcelable

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database : SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE entry ADD COLUMN rssi INTEGER NOT NULL DEFAULT -200")
    }
}