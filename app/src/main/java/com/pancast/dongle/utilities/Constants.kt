package com.pancast.dongle.utilities

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
}

enum class RequestType {
    RISK_TYPE,
    EPI_TYPE
}