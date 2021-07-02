package com.example.bluetooth_sample.requests

import android.annotation.SuppressLint
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.X509Certificate


internal class NaiveTrustManager : X509TrustManager {
    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
        return arrayOf()
    }

    @SuppressLint("TrustAllX509TrustManager")
    override fun checkClientTrusted(
        chain: Array<out java.security.cert.X509Certificate>?,
        authType: String?
    ) {
    }

    @SuppressLint("TrustAllX509TrustManager")
    override fun checkServerTrusted(
        chain: Array<out java.security.cert.X509Certificate>?,
        authType: String?
    ) {
    }
}