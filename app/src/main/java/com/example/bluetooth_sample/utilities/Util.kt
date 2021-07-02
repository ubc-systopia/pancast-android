package com.example.bluetooth_sample.utilities

import java.nio.ByteBuffer
import java.nio.ByteOrder

val MaxBroadcastSize = 30

fun getMinutesSinceLinuxEpoch(): Long {
    val millisecondsInSecond = 1000
    val secondsInMinute = 60
    val currentTime: Long = System.currentTimeMillis() / (millisecondsInSecond * secondsInMinute)
    return currentTime
}

fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

data class DecodedData
constructor(val beaconTime: Int, val beaconID: Int, val locationID: Long, val ephemeralID: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DecodedData

        if (beaconTime != other.beaconTime) return false
        if (beaconID != other.beaconID) return false
        if (locationID != other.locationID) return false
        if (!ephemeralID.contentEquals(other.ephemeralID)) return false

        return true
    }

    @ExperimentalUnsignedTypes
    override fun hashCode(): Int {
        var result = beaconTime.hashCode()
        result = 31 * result + beaconID.hashCode()
        result = 31 * result + locationID.hashCode()
        result = 31 * result + ephemeralID.contentHashCode()
        return result
    }
}

fun decodeData(d: ByteArray): DecodedData {
    if (d.size != 30) {
        throw IllegalArgumentException("size is not correct")
    }
//        val beaconTime = d.copyOfRange(0, 4).getUInt32()
    val beaconTime = ByteBuffer.wrap(d.copyOfRange(0, 4)).order(ByteOrder.LITTLE_ENDIAN).int
    val beaconID = ByteBuffer.wrap(d.copyOfRange(4, 8)).order(ByteOrder.LITTLE_ENDIAN).int
    val locationID = ByteBuffer.wrap(d.copyOfRange(8, 16)).order(ByteOrder.LITTLE_ENDIAN).long
//        val beaconID = d.copyOfRange(4, 8).getUInt32()
//        val locationID = d.copyOfRange(8, 16).getUInt64()
    val ephemeralID: ByteArray = d.copyOfRange(16, 30)
    return DecodedData(beaconTime, beaconID, locationID, ephemeralID)
}

fun isPancastData(d: ByteArray): Boolean {
    val uuid = d.copyOfRange(6, 8)
    val pancastUUID = byteArrayOfInts(0x22, 0x22)
    return uuid.contentEquals(pancastUUID)
}

fun rearrangeData(d: ByteArray): ByteArray {
    val dataCopy = d.copyOf()
    dataCopy[0] = d[1]
    dataCopy[1] = d[MaxBroadcastSize - 1]
    return dataCopy
}