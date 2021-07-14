package com.pancast.dongle.fragments.home.handlers

interface PacketHandler {
    fun isOfType(payload: ByteArray): Boolean

    fun handlePayload(payload: ByteArray, rssi: Int)

}