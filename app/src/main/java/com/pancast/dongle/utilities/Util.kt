package com.pancast.dongle.utilities

import java.nio.ByteBuffer
import java.nio.ByteOrder

val MaxBroadcastSize = 30

fun getMinutesSinceLinuxEpoch(): Long {
    val millisecondsInSecond = 1000
    val secondsInMinute = 60
    val currentTime: Long = System.currentTimeMillis() / (millisecondsInSecond * secondsInMinute)
    return currentTime
}

fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

