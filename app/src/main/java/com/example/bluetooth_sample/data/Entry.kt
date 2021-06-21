package com.example.bluetooth_sample.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Entry(
    // ephID, beaconID, locationID, beaconTime, deviceTime
    @PrimaryKey val ephemeralID: String,
    @PrimaryKey val beaconID: Int?,
    @PrimaryKey val locationID: String?,
    val beaconTime: Int?,
    val dongleTime: Int?
)