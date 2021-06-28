package com.example.bluetooth_sample

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }