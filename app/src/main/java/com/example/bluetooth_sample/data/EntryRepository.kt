package com.example.bluetooth_sample.data

import com.example.bluetooth_sample.getMinutesSinceLinuxEpoch

class EntryRepository(private val entryDao: EntryDao) {

    fun addEntry(entry: Entry) {
        entryDao.insert(entry)
    }

    fun deleteEntries() {
        val currTime: Long = 0
        entryDao.delete(currTime)
    }

    fun getEntries(): List<Entry> {
        val currTime: Long = 0
        val entries = entryDao.getAll(currTime)
        return entries
    }
}