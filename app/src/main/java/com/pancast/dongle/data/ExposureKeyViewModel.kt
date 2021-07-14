package com.pancast.dongle.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExposureKeyViewModel(application: Application): AndroidViewModel(application) {
    private val repository: ExposureKeyRepository

    init {
        val entryDao = PancastDatabase.getDatabase(application).exposureKeyDao()
        repository = ExposureKeyRepository(entryDao)
    }

    fun addExposureKey(key: ExposureKey) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addExposureKey(key)
        }
    }
}