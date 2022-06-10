package com.pancast.dongle.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.pancast.dongle.utilities.Constants.devKey
import kotlin.concurrent.thread

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

    fun getEntry(displayName: String): LoggedInUser {
        return loginDao.getEntry(displayName)
    }

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        // user = getEntry(devKey)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun register(): String {
        val result = dataSource.register()
//        Log.w("[H]", "LoginRepo devId: " + result)

        val user = LoggedInUser("PanCast", devKey, result)
        var t: Thread = thread(start=true) { addEntry(user) }
        t.join()
        return result
    }
}