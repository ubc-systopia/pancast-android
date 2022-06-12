package com.pancast.dongle.data

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _devId = MutableLiveData<String>()
    var userDevId: LiveData<String> = _devId

    private var loginRepository: LoginRepository

    init {
//        loginRepository.dataSource = dSource
        val loginDao = PancastDatabase.getDatabase(application).loginDao()
        val loginDataSource = LoginDataSource()
        loginRepository = LoginRepository(loginDataSource, loginDao)
    }

    fun addEntry(userId: String, displayName: String, result: String) {
        val user = LoggedInUser(userId, displayName, result)
        var t: Thread = thread(start = true) {
            loginRepository.addEntry(user)
        }
        t.join()
    }

    fun getEntry(displayName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.getEntry(displayName)
        }
    }

    fun deleteUserDB() {
        val t: Thread = thread(start=true) {
            val count = loginRepository.getNumEntriesDB()
            loginRepository.clearUserDB()
            Log.e("[H]", "deleted " + count.toString() + " entries")
        }
        t.join()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun register(userId: String, displayName: String): String {
        // get device ID from backend
        val result = loginRepository.register(userId, displayName)
        // insert mapping <devKey, PanCastUUID, result> into loginDao DB of the phone
        addEntry(userId, displayName, result)
        // update in-memory mutable state
        _devId.postValue(result)
        userDevId = MutableLiveData(result)

        return result
    }
}