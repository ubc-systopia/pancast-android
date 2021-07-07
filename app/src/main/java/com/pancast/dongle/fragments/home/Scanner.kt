package com.pancast.dongle.fragments.home

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.pancast.dongle.data.EntryDatabase
import java.lang.Exception



class Scanner(handler: EntryHandler) {
    private var max = -127
    private var min = 127
    private val mBlueAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val mBluetoothLeScanner: BluetoothLeScanner =
        BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
    private val mScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result == null || result.scanRecord == null) return
            val data = result.scanRecord!!.bytes
            if (handler.isPancastPayload(data)) {
                if (result.rssi > max) {
                    max = result.rssi
                }
                if (result.rssi < min) {
                    min = result.rssi
                }
                Log.d("TELEMETRY", "BEGIN")
                Log.d("TELEMETRY", "Max is $max")
                Log.d("TELEMETRY", "Min is $min")
                handler.handlePayload(data)
            }
            // maybe add more handlers for different types of packets
        }
    }

    fun startScan() {
        if (mBlueAdapter.isEnabled) {
            val filters: List<ScanFilter> = listOf(ScanFilter.Builder().build())
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build()
            mBluetoothLeScanner.startScan(filters, settings, mScanCallback)
        } else {
            throw Exception("Bluetooth is not enabled")
        }
    }

    fun stopScan() {
        mBluetoothLeScanner.stopScan(mScanCallback)
    }

    companion object {
        @Volatile
        private var INSTANCE: Scanner? = null

        fun getScanner(ctx: Context): Scanner {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val handler = EntryHandler(ctx)
                val instance = Scanner(handler)
                INSTANCE = instance
                return instance
            }
        }
    }

}