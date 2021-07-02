package com.example.bluetooth_sample

import com.example.bluetooth_sample.data.Entry
import com.example.bluetooth_sample.utilities.byteArrayOfInts
import com.example.bluetooth_sample.utilities.decodeData
import com.example.bluetooth_sample.utilities.getMinutesSinceLinuxEpoch
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
    fun test_decode() {
        val testArray: ByteArray = byteArrayOfInts(
        0x01,0x00,0x00,0x00, // beacon time
        0x01,0x00,0x00,0x00, // beacon ID
        0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00, // location ID
        0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E // 14 byte ephemeral ID
        )

        val output = decodeData(testArray)
        assertEquals(1, output.beaconTime)
        assertEquals(1, output.beaconID)
        assertEquals(1, output.locationID)
        assertEquals(byteArrayOfInts(0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E).toHexString(), output.ephemeralID.toHexString())
    }

    @Test
    fun test_insert_entry() {
        val ephID = byteArrayOfInts(0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E)
        val beaconID = 1
        val locationID: Long = 1
        val beaconTime = 1
        val dongleTime = 1
        val entry = Entry(ephID, beaconID, locationID, beaconTime, dongleTime)
    }
}