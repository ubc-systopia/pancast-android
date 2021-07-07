package com.pancast.dongle.fragments.home

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.pancast.dongle.MainActivity
import com.pancast.dongle.utilities.Constants.SERVICE_CHANNEL_ID

class BleScannerService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        // don't need anything for this?
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val scanner = Scanner.getScanner(applicationContext)
        scanner.startScan()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notificationChannel = NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setContentTitle("Pancast Dongle")
            .setContentText("Collecting ephemeral IDs from nearby beacons...")
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notificationChannel)
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        val scanner = Scanner.getScanner(applicationContext)
        scanner.stopScan()
        super.onDestroy()
    }

    companion object {
        fun startService(ctx: Context) {
            val startIntent = Intent(ctx, BleScannerService::class.java)
            ContextCompat.startForegroundService(ctx, startIntent)
        }

        fun stopService(ctx: Context) {
            val startIntent = Intent(ctx, BleScannerService::class.java)
            ctx.stopService(startIntent)
        }
    }
}