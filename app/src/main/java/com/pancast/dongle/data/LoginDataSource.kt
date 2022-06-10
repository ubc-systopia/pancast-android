package com.pancast.dongle.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.pancast.dongle.requests.RequestsHandler
import java.io.IOException
import java.lang.Exception
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import org.json.JSONObject

import okhttp3.internal.toHexString

@Serializable
data class DeviceRegistrationModel(val ServerKey: String, val DeviceID: Int, val Clock: Int,
val Secret: String, val OTPs: ByteArray, val LocationID: String)

@OptIn(ExperimentalSerializationApi::class)
fun readJson(){
    val deserialized = Json.decodeFromString<DeviceRegistrationModel>(
        """{"ServerKey" : "Hallo", "DeviceID": 286326790, "Clock": 27579205, "Secret": "uvuMT=", "OTPs":[], "LocationID": 7004003849336993379}"""
    )
}

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    @RequiresApi(Build.VERSION_CODES.O)
    fun register(): String {
        try {
            val result = RequestsHandler().registerDevice()
            Log.e("[H]", "register query response: " + result)
            val jsonobj = JSONObject(result)
            val devId: Int = jsonobj.optInt("DeviceID")
//            Log.w("[H]", "JSONObject devId: " + devId.toHexString())
            return devId.toHexString()
        } catch (e: Exception) {
            val errMsg: String = "Failed to register device!"
            return IOException(errMsg, e).toString()
        }
    }
}