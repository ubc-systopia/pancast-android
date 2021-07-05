package com.pancast.dongle.data

import androidx.lifecycle.LiveData

class EntryRepository(private val entryDao: EntryDao) {

    val entries: LiveData<List<Entry>> = entryDao.getAll(0)

    fun addEntry(entry: Entry) {
        entryDao.insert(entry)
    }

    fun deleteEntries() {
        val currTime: Long = 0
        entryDao.delete(currTime)
    }
}