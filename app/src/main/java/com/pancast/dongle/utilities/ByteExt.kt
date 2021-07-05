package com.pancast.dongle

import com.pancast.dongle.cuckoo.FINGERPRINT_SIZE_IN_BYTES
import java.nio.ByteBuffer

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

fun uLongToBytes(x: ULong): ByteArray {
    val buffer = ByteBuffer.allocate(8)
    buffer.putLong(x.toLong())
    return buffer.array()
}

fun fingerprintToBytes(x: ULong): ByteArray {
    val buffer = ByteBuffer.allocate(FINGERPRINT_SIZE_IN_BYTES.toInt())
    buffer.putInt(x.toInt())
    // reversed because fingerprints are represented in big endian within the filter
    return buffer.array().reversedArray()
}