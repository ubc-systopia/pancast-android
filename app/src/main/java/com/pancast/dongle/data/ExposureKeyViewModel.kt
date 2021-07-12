package com.pancast.dongle.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExposureKeyViewModel(application: Application): AndroidViewModel(application) {
    val repository: Exposure
    val entries: LiveData<List<ExposureKey>>

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
}