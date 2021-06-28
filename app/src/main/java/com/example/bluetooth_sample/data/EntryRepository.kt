package com.example.bluetooth_sample.data

class EntryRepository(private val entryDao: EntryDao) {

    suspend fun addEntry(entry: Entry) {
        entryDao.insert(entry)
    }

    suspend fun deleteEntries() {
        val currTime: Int = 0
        entryDao.delete(currTime)
    }

    suspend fun getEntries() {
        val currTime: Int = 0
        entryDao.getAll(currTime)
    }
}