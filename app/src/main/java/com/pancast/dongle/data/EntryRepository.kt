package com.pancast.dongle.data

import androidx.lifecycle.LiveData

class EntryRepository(private val entryDao: EntryDao) {

    val entries: LiveData<List<Entry>> = entryDao.getAll(0)

    fun addEntry(entry: Entry) {
        entryDao.insert(entry)
    }

    fun getAllEntries(): List<Entry> {
        return entryDao.getAllSynchronously(0)
    }

    fun getNumEntries(ephID: String): Int {
        return entryDao.getNumEntries(ephID)
    }

    fun deleteEntries() {
        val currTime: Long = 0
        entryDao.delete(currTime)
    }

    fun deleteAllEntries() {
        entryDao.deleteAll()
    }

    fun deleteOneEntry(ephID: String, dongleTime: Int, rssi: Int) {
        entryDao.deleteOneEntry(ephID, dongleTime, rssi)
        entries = entryDao.getAll(0)
    }
}