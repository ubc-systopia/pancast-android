package com.example.bluetooth_sample.requests

import com.example.bluetooth_sample.data.Entry
import com.example.bluetooth_sample.utilities.Constants
import com.example.bluetooth_sample.utilities.RequestType
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

class RequestsHandler {
    fun uploadData(data: List<Entry>, type: RequestType): String? {
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
        client.newCall(request).execute().use { response -> return response.body?.string() }
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
        client.newCall(request).execute().use { response -> return response.body?.bytes() }
    }

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
                            "}", en.ephemeralID, en.dongleTime, en.beaconTime, en.beaconID, en.locationID
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