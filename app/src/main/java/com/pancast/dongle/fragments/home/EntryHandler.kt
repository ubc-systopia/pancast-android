package com.pancast.dongle.fragments.home

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.pancast.dongle.data.Entry
import com.pancast.dongle.data.EntryViewModel
import com.pancast.dongle.toHexString
import com.pancast.dongle.utilities.Constants
import com.pancast.dongle.utilities.MaxBroadcastSize
import com.pancast.dongle.utilities.byteArrayOfInts
import com.pancast.dongle.utilities.getMinutesSinceLinuxEpoch
import java.nio.ByteBuffer
import java.nio.ByteOrder

class EntryHandler(homeFragment: HomeFragment) {
    val ephemeralIDCache: MutableMap<String, Long> = mutableMapOf()
    val mEntryViewModel: EntryViewModel = ViewModelProvider(homeFragment).get(EntryViewModel::class.java)

    fun logEncounter(input: ByteArray) {
        // need some form of expiry mechanism for old ephemeral IDs within the map. cron job to remove
        // old entries from the cache?
        val decoded: DecodedData = decodeData(input)
        if (!ephemeralIDCache.containsKey(decoded.ephemeralID.toHexString())) {
            ephemeralIDCache[decoded.ephemeralID.toHexString()] = getMinutesSinceLinuxEpoch()
        } else {
            val oldTime = ephemeralIDCache[decoded.ephemeralID.toHexString()]
            val newTime = getMinutesSinceLinuxEpoch()
            if (newTime - oldTime!! >= Constants.ENCOUNTER_TIME_TRESHOLD) {
                Log.d("DATA", "Entry added")
                val entry = Entry(decoded.ephemeralID.toHexString(), decoded.beaconID, decoded.locationID, decoded.beaconTime, oldTime.toInt())
                mEntryViewModel.addEntry(entry)
                ephemeralIDCache[decoded.ephemeralID.toHexString()] = newTime
            }
        }
    }
}

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
    val beaconTime = ByteBuffer.wrap(d.copyOfRange(0, 4)).order(ByteOrder.LITTLE_ENDIAN).int
    val beaconID = ByteBuffer.wrap(d.copyOfRange(4, 8)).order(ByteOrder.LITTLE_ENDIAN).int
    val locationID = ByteBuffer.wrap(d.copyOfRange(8, 16)).order(ByteOrder.LITTLE_ENDIAN).long
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