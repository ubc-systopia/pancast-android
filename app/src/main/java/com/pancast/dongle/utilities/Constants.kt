package com.pancast.dongle.utilities

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.provider.Settings
import android.util.Log
import com.pancast.dongle.utilities.Constants.locationStringLen
import java.util.*

object Constants {
    const val WEB_PROTOCOL = "https"
    const val BACKEND_ADDR = "pancast.cs.ubc.ca"
    const val BACKEND_PORT = "443"
    const val ENCOUNTER_TIME_THRESHOLD = 1
    const val LOG_MIN_WAIT = 15
    const val MaxBroadcastSize = 31
    const val MILLISECONDS_IN_SECOND = 1000
    const val SECONDS_IN_MINUTE = 60
    const val MINUTES_IN_WINDOW = 20160
    const val SERVICE_CHANNEL_ID: String = "SCAN_ONGOING_CHANNEL"

    const val GAEN_WEB_PROTOCOL = "http"
    const val GAEN_BACKEND_ADDR = "10.0.0.32"
    const val GAEN_BACKEND_PORT = "8001"
    const val MCC_CODE = "302"
    // my inner scream:
    const val HMAC_KEY = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    const val locationStringLen = 16
    @SuppressLint("HardwareIds")
    val PanCastUUID = BluetoothAdapter.getDefaultAdapter().address
    val devKey = getRandomString(PanCastUUID)
}

enum class RequestType {
    RISK_TYPE,
    EPI_TYPE
}

private val ALLOWED_CHARACTERS = "0123456789abcdef"

fun getRandomString(inputSeed: String): String {
    val hash =  Objects.hashCode(inputSeed)
    Log.d("[H]", "input[" + inputSeed.length + "]: " + inputSeed + ", h1:" + hash.toString())
    val random = Random(hash.toLong())
    val sb = StringBuilder(locationStringLen)
    for (i in 0 until locationStringLen)
        sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
    return sb.toString()
}