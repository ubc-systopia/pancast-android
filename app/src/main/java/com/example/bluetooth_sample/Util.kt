package com.example.bluetooth_sample

fun getMinutesSinceLinuxEpoch(): Long {
    val millisecondsInSecond = 1000
    val secondsInMinute = 60
    val currentTime: Long = System.currentTimeMillis() / (millisecondsInSecond * secondsInMinute)
    return currentTime
    // do what function name asks
}

//@ExperimentalUnsignedTypes
//fun ByteArray.getUInt32() =
//    ((this[0].toUInt() and 0xFFu) shl 24) or
//            ((this[1].toUInt() and 0xFFu) shl 16) or
//            ((this[2].toUInt() and 0xFFu) shl 8) or
//            (this[3].toUInt() and 0xFFu)
//
//@ExperimentalUnsignedTypes
//fun ByteArray.getUInt64() =
//    ((this[0].toULong() and 0xFFFFu) shl 56) or
//            ((this[1].toULong() and 0xFFFFu) shl 48) or
//            ((this[2].toULong() and 0xFFFFu) shl 40) or
//            ((this[3].toULong() and 0xFFFFu) shl 32) or
//            ((this[4].toULong() and 0xFFFFu) shl 24) or
//            ((this[5].toULong() and 0xFFFFu) shl 16) or
//            ((this[6].toULong() and 0xFFFFu) shl 8) or
//            ((this[7].toULong() and 0xFFFFu) shl 0)

fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }
