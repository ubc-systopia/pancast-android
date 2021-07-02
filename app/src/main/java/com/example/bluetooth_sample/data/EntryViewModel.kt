package com.example.bluetooth_sample.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EntryViewModel(application: Application): AndroidViewModel(application) {
    private val repository: EntryRepository

    init {
        val entryDao = EntryDatabase.getDatabase(application).entryDao()
        repository = EntryRepository(entryDao)
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

    fun getAllEntries() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("DATA", repository.getEntries().toString())
        }
    }
}