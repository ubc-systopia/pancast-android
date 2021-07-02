package com.example.bluetooth_sample

import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.ByteBuffer

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

fun longToBytes(x: Long): ByteArray {
    val buffer = ByteBuffer.allocate(8)
    buffer.putLong(x)
    return buffer.array()
}