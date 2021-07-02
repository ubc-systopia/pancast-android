package com.example.bluetooth_sample.data

import androidx.room.Entity

@Entity(primaryKeys = ["ephemeralID", "beaconID", "beaconTime"])
data class Entry(
    // ephID, beaconID, locationID, beaconTime, deviceTime
    val ephemeralID: String,
    val beaconID: Int,
    val locationID: Long,
    val beaconTime: Int,
    val dongleTime: Int
)