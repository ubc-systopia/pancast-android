package com.pancast.dongle.data

import android.app.Application
import android.os.Build
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

    fun addEntry(user: LoggedInUser) {
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.addEntry(user)
        }
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
    fun register(): String {
        val result = loginRepository.register()
//        Log.w("[H]", "LoginViewModel devId: " + result)

        _devId.postValue(result)
        userDevId = MutableLiveData<String>(result)
        return result
    }
}