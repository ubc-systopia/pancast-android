package com.pancast.dongle.utilities

import android.Manifest

object Constants {
    const val WEB_PROTOCOL = "https"
    const val BACKEND_ADDR = "pancast.cs.ubc.ca"
    const val BACKEND_PORT = "443"
    const val ENCOUNTER_TIME_TRESHOLD = 5
    const val LOCATION_FINE_PERM = Manifest.permission.ACCESS_FINE_LOCATION
}

enum class RequestType {
    RISK_TYPE,
    EPI_TYPE
}