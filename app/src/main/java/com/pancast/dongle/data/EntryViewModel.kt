package com.pancast.dongle.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EntryViewModel(application: Application): AndroidViewModel(application) {
    val repository: EntryRepository
    val entries: LiveData<List<Entry>>

    init {
        val entryDao = PancastDatabase.getDatabase(application).entryDao()
        repository = EntryRepository(entryDao)
        entries = repository.entries
    }

    fun addEntry(entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEntry(entry)
        }
    }

    fun deleteOldEntries() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEntries()
        }
    }

    fun deleteAllEntries() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllEntries()
        }
    }

    fun deleteOneEntry(ephID: String, dongleTime: Int, rssi: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteOneEntry(ephID, dongleTime, rssi)
        }
    }
}