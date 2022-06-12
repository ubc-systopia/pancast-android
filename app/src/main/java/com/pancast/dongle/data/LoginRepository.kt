package com.pancast.dongle.data

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val logindataSource: LoginDataSource, private val logDao: LoginDao) {

    var dataSource: LoginDataSource = logindataSource
    var loginDao: LoginDao = logDao

    fun addEntry(user: LoggedInUser) {
        loginDao.insert(user)
    }

    fun getEntryName(displayName: String): LoggedInUser {
        return loginDao.getEntryName(displayName)
    }

    fun getEntryDevKey(userId: String): LoggedInUser {
        return loginDao.getEntryDevKey(userId)
    }

    fun getNumEntriesDB(): Int {
        return loginDao.numEntries()
    }

    fun clearUserDB() {
        loginDao.deleteAll()
    }

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        // user = getEntry(devKey)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun register(userId: String, displayName: String): String {
        val result = dataSource.register()
        return result
    }
}