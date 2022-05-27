package com.pancast.dongle.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.android.parcel.Parcelize

@Entity(primaryKeys = ["ephemeralID", "beaconID", "beaconTime"])
//@Entity(primaryKeys = ["ephemeralID"])
@Parcelize
data class Entry(
    // ephID, beaconID, locationID, beaconTime, deviceTime
    val ephemeralID: String,
    val beaconID: Int,
    val locationID: Long,
    val beaconTime: Int,
    var beaconTimeInterval: Int,
    val dongleTime: Int,
    var dongleTimeInterval: Int,
    val rssi: Int
): Parcelable

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database : SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE entry ADD COLUMN rssi INTEGER NOT NULL DEFAULT -200")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database : SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE entry " +
                "ADD COLUMN beaconTimeInterval INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE entry " +
                "ADD COLUMN dongleTimeInterval INTEGER NOT NULL DEFAULT 0")
    }
}