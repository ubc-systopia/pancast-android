package com.pancast.dongle.data

import com.pancast.dongle.utilities.getMinutesSinceLinuxEpoch

class ExposureKeyRepository(private val exposureKeyDao: ExposureKeyDao) {

    fun addExposureKey(exposureKey: ExposureKey) {
        exposureKeyDao.insert(exposureKey)
    }

    fun getAllEntries(): List<ExposureKey> {
        return exposureKeyDao.getAll(getMinutesSinceLinuxEpoch())
    }
}