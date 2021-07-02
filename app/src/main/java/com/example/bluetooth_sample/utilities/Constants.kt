package com.example.bluetooth_sample.utilities

object Constants {
    const val RISK_ENTRIES = 1
    const val EPI_ENTRIES = 2
    const val WEB_PROTOCOL = "https"
    const val BACKEND_ADDR = "pancast.cs.ubc.ca"
    const val BACKEND_PORT = "443"
}

enum class RequestType {
    RISK_TYPE,
    EPI_TYPE
}