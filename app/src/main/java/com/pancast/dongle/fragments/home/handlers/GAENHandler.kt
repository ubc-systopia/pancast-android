package com.pancast.dongle.fragments.home.handlers

import android.content.Context
import com.pancast.dongle.data.ExposureKey
import com.pancast.dongle.data.ExposureKeyRepository
import com.pancast.dongle.data.PancastDatabase
import com.pancast.dongle.utilities.toHexString
import com.pancast.dongle.utilities.getMinutesSinceLinuxEpoch
import kotlin.concurrent.thread

class GAENHandler(ctx: Context): PacketHandler {
    private val mExposureKeyDao = PancastDatabase.getDatabase(ctx).exposureKeyDao()
    private val mExposureKeyRepository = ExposureKeyRepository(mExposureKeyDao)


    override fun isOfType(payload: ByteArray): Boolean {
        return getUUID(payload)
    }

    override fun handlePayload(payload: ByteArray, rssi: Int) {
        val rollingProximityIdentifier = payload.copyOfRange(11, 27)
        val associatedEncryptedMetadata = payload.copyOfRange(27, 31)
        val rpiAsHexString = rollingProximityIdentifier.toHexString()
        val aemAsHexString = associatedEncryptedMetadata.toHexString()
        val key = ExposureKey(rpiAsHexString, aemAsHexString, getMinutesSinceLinuxEpoch().toInt(), rssi)
        thread(start = true){
            mExposureKeyRepository.addExposureKey(key)
        }
    }

    private fun getUUID(payload: ByteArray): Boolean {
        // I still don't understand why the service identifier is duplicated :/
        // did I do something wrong?
        // for now it'll serve as extra validation that this is the packet we want
        val uuid1 = payload.copyOfRange(5, 7)
        val uuid2 = payload.copyOfRange(9, 11)
        return uuid1.toHexString() == "6ffd" && uuid2.toHexString() == "6ffd"
    }

    companion object {
        @Volatile
        private var INSTANCE: GAENHandler? = null

        fun getGaenHandler(ctx: Context): GAENHandler {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = GAENHandler(ctx)
                INSTANCE = instance
                return instance
            }
        }
    }
}