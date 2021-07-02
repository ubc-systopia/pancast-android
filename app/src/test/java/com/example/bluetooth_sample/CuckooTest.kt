package com.example.bluetooth_sample

import com.example.bluetooth_sample.cuckoo.CuckooFilter
import com.example.bluetooth_sample.utilities.byteArrayOfInts
import org.junit.Test
import org.junit.Assert.*

class CuckooTest {
    @Test
    fun cuckoo_lookup_in_empty_filter() {
        val data = byteArrayOf()
        val item = byteArrayOfInts(0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00)
        val cuckooFilter = CuckooFilter(data)
        val outcome = cuckooFilter.lookupItem(item)
        assertEquals(false, outcome)
    }
}