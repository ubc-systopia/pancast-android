package com.pancast.dongle.requests

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.pancast.dongle.data.Entry
import com.pancast.dongle.gaen.computeHMACAuthenticationString
import com.pancast.dongle.utilities.decodeHex
import com.pancast.dongle.utilities.Constants
import com.pancast.dongle.utilities.Constants.MCC_CODE
import com.pancast.dongle.utilities.RequestType
import com.pancast.dongle.utilities.toHexString
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.SecureRandom
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

class RequestsHandler {
    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadData(data: List<Entry>, type: RequestType): String {
        val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
        val myTrustManagerArray: Array<TrustManager> = arrayOf(NaiveTrustManager())
        sslContext.init(null, myTrustManagerArray, SecureRandom())
        val trustManager = NaiveTrustManager()
        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
            .hostnameVerifier { _, _ -> true }
            .build()
        val url = "${Constants.WEB_PROTOCOL}://${Constants.BACKEND_ADDR}:${Constants.BACKEND_PORT}/upload"
        val contentType: MediaType = "application/json; charset=utf-8".toMediaType()
        val body: String = constructUploadRequestBody(data, type)
        val reqBody: RequestBody = body.toRequestBody(contentType)
        val request: Request = Request.Builder()
            .url(url)
            .post(reqBody)
            .build()
//        val blen = body.length
//        Log.w("R1", "$url, $contentType, $blen")
//        Log.w("R2", "$body")
        val response = client.newCall(request).execute()
        return if (response.body != null) {
            response.body!!.string()
        } else {
            ""
        }
    }

    fun downloadRiskBroadcast(): ByteArray? {
        val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
        val myTrustManagerArray: Array<TrustManager> = arrayOf(NaiveTrustManager())
        sslContext.init(null, myTrustManagerArray, SecureRandom())
        val trustManager = NaiveTrustManager()
        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
            .hostnameVerifier { _, _ -> true }
            .build()
        val url = "${Constants.WEB_PROTOCOL}://${Constants.BACKEND_ADDR}:${Constants.BACKEND_PORT}/update"
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        Log.w("B", "$request")
        client.newCall(request).execute().use { response -> return response.body?.bytes() }
    }

    fun downloadGaenRiskBroadcast(): ByteArray? {
        val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
        val myTrustManagerArray: Array<TrustManager> = arrayOf(NaiveTrustManager())
        sslContext.init(null, myTrustManagerArray, SecureRandom())
        val trustManager = NaiveTrustManager()
        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
            .hostnameVerifier { _, _ -> true }
            .build()
        val authenticationString = computeHMACAuthenticationString().toHexString()
        val url = "${Constants.GAEN_WEB_PROTOCOL}://${Constants.GAEN_BACKEND_ADDR}:" +
                "${Constants.GAEN_BACKEND_PORT}/retrieve/${MCC_CODE}/00000/${authenticationString}"
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        client.newCall(request).execute().use { response -> return response.body?.bytes() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun constructUploadRequestBody(data: List<Entry>, type: RequestType): String {
        val bld = StringBuilder()
        bld.append("{\"Entries\": [")

        for (i in data.indices) {
            val en: Entry = data[i]
            bld.append(
                java.lang.String.format(
                    "{" +
                            "\"EphemeralID\": \"%s\"," +
                            "\"DongleClock\": %s," +
                            "\"BeaconClock\": %s," +
                            "\"BeaconId\":    %s," +
                            "\"LocationID\":  %s" +
                    "}",
                    en.ephemeralID,
                    //Base64.getEncoder().encodeToString(en.ephemeralID.decodeHex()),
                    en.dongleTime, en.beaconTime, en.beaconID, en.locationID
                    /* TODO: upload beaconTimeInterval, dongleTimeInterval, and rssi value too */
                )
            )
            if (i < data.size - 1) {
                bld.append(',')
            }
        }
        when (type) {
            RequestType.RISK_TYPE -> bld.append("], \"Type\": 0 }")
            RequestType.EPI_TYPE -> bld.append("], \"Type\": 1 }")
        }
        return bld.toString()
    }
}