package com.pancast.dongle.utilities

import com.pancast.dongle.cuckoo.FINGERPRINT_SIZE_IN_BYTES
import java.nio.ByteBuffer

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

fun fingerprintToBytes(x: ULong): ByteArray {
    val buffer = ByteBuffer.allocate(FINGERPRINT_SIZE_IN_BYTES.toInt())
    buffer.putInt(x.toInt())
    // reversed because fingerprints are represented in big endian within the filter
    return buffer.array().reversedArray()
}

fun String.decodeHex(): ByteArray {
    require(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}