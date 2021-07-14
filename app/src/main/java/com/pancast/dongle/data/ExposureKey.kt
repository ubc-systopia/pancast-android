package com.pancast.dongle.data

import androidx.room.Entity

@Entity(tableName = "exposure_keys", primaryKeys = ["rollingProximityIdentifier"])
data class ExposureKey(
    // ephID, beaconID, locationID, beaconTime, deviceTime
    val rollingProximityIdentifier: String,
    val associatedEncryptedMetadata: String,
    val time: Int,
    val rssi: Int
)