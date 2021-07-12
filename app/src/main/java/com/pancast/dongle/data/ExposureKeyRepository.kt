package com.pancast.dongle.data

class ExposureKeyRepository(private val exposureKeyDao: ExposureKeyDao) {

    fun addExposureKey(exposureKey: ExposureKey) {
        exposureKeyDao.insert(exposureKey)
    }

    fun getAllEntries(): List<ExposureKey> {
        return exposureKeyDao.getAll()
    }
}