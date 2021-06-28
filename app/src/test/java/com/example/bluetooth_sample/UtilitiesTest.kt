package com.example.bluetooth_sample

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UtilitiesTest {
    @Test
    fun timestamp_is_correct() {
        val currentTime = getMinutesSinceLinuxEpoch()
        assertNotEquals(0, currentTime)
    }

    @Test
    fun test_log_encounter() {
        val testArray: ByteArray = byteArrayOfInts(
        0x1,0x0,0x0,0x0,
        0x1,0x0,0x0,0x0,
        0x1,0x0,0x0,0x0,0x0,0x0,0x0,0x0,
        0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9,0xA,0xB,0xC,0xD,0xE
        )
        // call logEncounter
    }
}