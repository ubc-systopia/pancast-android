package com.pancast.dongle

import com.pancast.dongle.cuckoo.CuckooFilter
import com.pancast.dongle.utilities.byteArrayOfInts
import org.junit.Test
import org.junit.Assert.*
import java.io.File

class CuckooTest {
    @Test
    fun cuckoo_lookup_in_empty_filter() {
        val data = byteArrayOfInts(0x10,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00)
        val item = byteArrayOfInts(0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00)
        val cuckooFilter = CuckooFilter(data)
        val outcome = cuckooFilter.lookupItem(item)
        assertEquals(false, outcome)
    }

    @Test
    fun cuckoo_lookup_multiple_items() {
        val testArray = File("src/test/java/com/example/bluetooth_sample/testData/testCuckooFilter.bin").readBytes()
        val ephIDs = mutableListOf<ByteArray>()
        ephIDs.add("00d17c102c46b0d".toByteArray())
        ephIDs.add("04ad5f4414ab19f".toByteArray())
        ephIDs.add("0c788e58b0b9b94".toByteArray())
        ephIDs.add("174063dc93a8354".toByteArray())
        ephIDs.add("19c4945cac1f5d6".toByteArray())
        ephIDs.add("1dad8ea6111f7db".toByteArray())
        ephIDs.add("4a10c16f3637b5f".toByteArray())
        ephIDs.add("5f8465db3b720c8".toByteArray())
        ephIDs.add("6a7f72f9571481b".toByteArray())
        ephIDs.add("6b3e950b5210610".toByteArray())

        val falseEphIDs = mutableListOf<ByteArray>()
        falseEphIDs.add("000000000000000".toByteArray())
        falseEphIDs.add("000000000000001".toByteArray())
        falseEphIDs.add("000000000000002".toByteArray())
        falseEphIDs.add("000000000000003".toByteArray())
        falseEphIDs.add("000000000000004".toByteArray())
        falseEphIDs.add("000000000000005".toByteArray())

        val cuckooFilter = CuckooFilter(testArray)
        ephIDs.forEach {
            assertEquals(true, cuckooFilter.lookupItem(it))
        }
        falseEphIDs.forEach {
            assertEquals(false, cuckooFilter.lookupItem(it))
        }
    }
}