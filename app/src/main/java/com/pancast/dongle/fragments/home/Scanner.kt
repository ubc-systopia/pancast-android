package com.pancast.dongle.fragments.home

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pancast.dongle.fragments.home.EntryHandler.Companion.getEntryHandler
import com.pancast.dongle.fragments.telemetry.PowerGraph
import java.lang.Exception



class Scanner(handler: EntryHandler) {
    // telemetry fields
    private var lastScansBuffer: MutableList<Int> = mutableListOf()
    private val numScansBeforeLogging: Int = 10

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
                // BEGIN TELEMETRY
                lastScansBuffer.add(result.rssi)
                if (lastScansBuffer.size >= numScansBeforeLogging) {
                    PowerGraph.updateGraph(lastScansBuffer.average())
                    lastScansBuffer = mutableListOf()
                }
                // END TELEMETRY
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
                val handler = getEntryHandler(ctx)
                val instance = Scanner(handler)
                INSTANCE = instance
                return instance
            }
        }
    }

}