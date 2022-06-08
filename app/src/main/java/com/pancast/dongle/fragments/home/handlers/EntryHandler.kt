package com.pancast.dongle.fragments.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pancast.dongle.MainApplication.Companion.APP_START_TIME
import com.pancast.dongle.data.*
import com.pancast.dongle.fragments.home.handlers.PacketHandler
import com.pancast.dongle.utilities.*
import com.pancast.dongle.utilities.Constants.MaxBroadcastSize
import okhttp3.internal.toHexString
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.concurrent.thread

class EntryHandler(ctx: Context): PacketHandler {
//    private val ephemeralIDCache: MutableMap<String, Long> = mutableMapOf()
    private val mEntryDao: EntryDao = PancastDatabase.getDatabase(ctx).entryDao()
    private val mEntryRepository = EntryRepository(mEntryDao)

    // telemetry
    var count: MutableLiveData<Int> = MutableLiveData(0)
    var encounter: MutableLiveData<Int> = MutableLiveData(0)
    var avgRssi: MutableLiveData<Double> = MutableLiveData(0.0)

    override fun handlePayload(payload: ByteArray, rssi: Int) {
        val truncatedData = payload.copyOfRange(0, MaxBroadcastSize)
        val rearrangedPayload = rearrangeData(truncatedData)
        logEncounter(rearrangedPayload, rssi)
    }

    override fun isOfType(payload: ByteArray): Boolean {
        return if (payload.size < 30) {
            false
        } else {
            isPancastData(payload)
        }
    }

    private fun logEncounter(input: ByteArray, rssi: Int) {
        count.value = count.value?.plus(1)
        if (mEntryRepository.entries.value?.size == 0)
            encounter.value = encounter.value?.plus(0)
        else
            encounter.value = mEntryRepository.entries.value?.size?.let {
                encounter.value?.plus(mEntryRepository.entries.value!!.size)
            }

        avgRssi.value = avgRssi.value?.plus(rssi)

        // need some form of expiry mechanism for old ephemeral IDs within the map. cron job to remove
        // old entries from the cache?
        val decoded: DecodedData = decodeData(input)
        val relTime = (getMinutesSinceLinuxEpoch().toInt() - APP_START_TIME.toInt())
        val relMin = minutesIntoTime(relTime)
        val bIdHex = decoded.beaconID.toHexString()

        thread(start=true) {
            val numEntries = mEntryRepository.getNumEntries(decoded.ephemeralID.toHexString())

            if (numEntries == 0) {
                val entry = Entry(decoded.ephemeralID.toHexString(), decoded.beaconID,
                    decoded.locationID, decoded.beaconTime, 1,
                    getMinutesSinceLinuxEpoch().toInt(), 1, rssi)
                mEntryRepository.addEntry(entry)
            } else if (numEntries == 1) {
                val entry = mEntryRepository.getEntry(decoded.ephemeralID.toHexString())
                val oldBeaconTime = entry.beaconTime
                val oldDongleTime = entry.dongleTime
                val newBeaconTime = decoded.beaconTime
                val newDongleTime = getMinutesSinceLinuxEpoch().toInt()
                entry.beaconTimeInterval = newBeaconTime - oldBeaconTime + 1
                entry.dongleTimeInterval = newDongleTime - oldDongleTime
                mEntryRepository.updateEntry(entry)
            } else {
                // we should not be reaching here, maybe raise an exception
                Log.w("SC", "[$relMin] DUPLICATE EPHIDs: " + numEntries +
                        " for" + decoded.ephemeralID.toHexString() +
                        " 0x${bIdHex} ${decoded.locationID} ${decoded.beaconTime} $rssi")
            }
        }

        /*
        if (!ephemeralIDCache.containsKey(decoded.ephemeralID.toHexString())) {
            ephemeralIDCache[decoded.ephemeralID.toHexString()] = getMinutesSinceLinuxEpoch()
        } else {
            val oldTime = ephemeralIDCache[decoded.ephemeralID.toHexString()]
            val newTime = getMinutesSinceLinuxEpoch()
            if (newTime - oldTime!! >= Constants.ENCOUNTER_TIME_THRESHOLD) {
                /* // == use only for debugging ===
                val relTime = (oldTime - APP_START_TIME).toInt()
                val relMin = minutesIntoTime(relTime)
                val bIdHex = Integer.toString(decoded.beaconID, 16)
                Log.w("SC", "[$relMin] " + decoded.ephemeralID.toHexString() +
                        " 0x${bIdHex} ${decoded.locationID} ${decoded.beaconTime} $rssi")
                */

                val entry = Entry(decoded.ephemeralID.toHexString(), decoded.beaconID,
                    decoded.locationID, decoded.beaconTime, oldTime.toInt(), rssi)
                thread(start=true) {
                    mEntryRepository.addEntry(entry)
                }
                ephemeralIDCache[decoded.ephemeralID.toHexString()] = newTime
            }
        }
         */
    }

    companion object {
        @Volatile
        private var INSTANCE: EntryHandler? = null

        fun getEntryHandler(ctx: Context): EntryHandler {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = EntryHandler(ctx)
                INSTANCE = instance
                return instance
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
    if (d.size != MaxBroadcastSize) {
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