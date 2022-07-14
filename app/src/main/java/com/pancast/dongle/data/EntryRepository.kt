package com.pancast.dongle.data

import androidx.lifecycle.LiveData

class EntryRepository(private val entryDao: EntryDao) {

    var entries: LiveData<List<Entry>> = entryDao.getAll(0)

    fun addEntry(entry: Entry) {
        entryDao.insert(entry)
    }

    fun updateEntry(entry: Entry) {
        entryDao.update(entry.ephemeralID, entry.beaconTimeInterval, entry.dongleTimeInterval, entry.rssi)
    }

    fun getAllEntries(): List<Entry> {
        return entryDao.getAllSynchronously(0)
    }

    fun getEntry(ephID: String): Entry {
        return entryDao.getEntry(ephID)
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