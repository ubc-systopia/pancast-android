package com.example.bluetooth_sample.data

import androidx.room.Entity

@Entity(primaryKeys = ["ephemeralID", "beaconID"])
data class Entry constructor(
    // ephID, beaconID, locationID, beaconTime, deviceTime
    val ephemeralID: ByteArray,
    val beaconID: Int,
    val locationID: Long,
    val beaconTime: Int,
    val dongleTime: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Entry

        if (!ephemeralID.contentEquals(other.ephemeralID)) return false
        if (beaconID != other.beaconID) return false
        if (locationID != other.locationID) return false
        if (beaconTime != other.beaconTime) return false
        if (dongleTime != other.dongleTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ephemeralID.contentHashCode()
        result = 31 * result + beaconID.hashCode()
        result = 31 * result + locationID.hashCode()
        result = 31 * result + beaconTime.hashCode()
        result = 31 * result + (dongleTime ?: 0)
        return result
    }
}