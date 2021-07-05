package com.pancast.dongle.utilities

import android.Manifest

object Constants {
    const val WEB_PROTOCOL = "https"
    const val BACKEND_ADDR = "pancast.cs.ubc.ca"
    const val BACKEND_PORT = "443"
    const val ENCOUNTER_TIME_THRESHOLD = 5
    const val LOCATION_FINE_PERM = Manifest.permission.ACCESS_FINE_LOCATION
    const val MILLISECONDS_IN_SECOND = 1000
    const val SECONDS_IN_MINUTE = 60
}

enum class RequestType {
    RISK_TYPE,
    EPI_TYPE
}