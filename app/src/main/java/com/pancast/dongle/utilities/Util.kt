package com.pancast.dongle.utilities

import com.pancast.dongle.utilities.Constants.MILLISECONDS_IN_SECOND
import com.pancast.dongle.utilities.Constants.SECONDS_IN_MINUTE
import java.text.SimpleDateFormat
import java.util.*

val MaxBroadcastSize = 30

fun getMinutesSinceLinuxEpoch(): Long {
    val currentTime: Long = System.currentTimeMillis() / (MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE)
    return currentTime
}

fun minutesIntoTime(minutes: Int): String {
    val millisecondsSinceEpoch = minutes.toLong() * MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE
    val format = SimpleDateFormat("yyyy-MM-dd, hh:mm", Locale.CANADA)
    val date = Date(millisecondsSinceEpoch)
    return format.format(date)
}

fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

