package com.pancast.dongle

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.pancast.dongle.utilities.Constants.SERVICE_CHANNEL_ID
import com.pancast.dongle.utilities.getMinutesSinceLinuxEpoch

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initChannel()
    }

    private fun initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                "Ongoing Scan Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(serviceChannel)
        }

    }

    companion object {
        val APP_START_TIME = getMinutesSinceLinuxEpoch()
    }
}