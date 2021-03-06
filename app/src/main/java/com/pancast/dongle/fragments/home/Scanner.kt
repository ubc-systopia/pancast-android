package com.pancast.dongle.fragments.home

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.pancast.dongle.fragments.home.EntryHandler.Companion.getEntryHandler
import com.pancast.dongle.fragments.home.handlers.GAENHandler
import com.pancast.dongle.fragments.home.handlers.GAENHandler.Companion.getGaenHandler
import com.pancast.dongle.fragments.telemetry.PowerGraph
import com.pancast.dongle.utilities.toHexString


class Scanner(entryHandler: EntryHandler, gaenHandler: GAENHandler) {
    // telemetry fields
    private var lastScansBuffer: MutableList<Int> = mutableListOf()
    private var lastScansMax: Int = -128
    private var lastScansMin: Int = 127
    private val numScansBeforeLogging: Int = 50

    private val mBlueAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val mBluetoothLeScanner: BluetoothLeScanner =
        mBlueAdapter.bluetoothLeScanner
    private val mScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result == null || result.scanRecord == null) return
            val data = result.scanRecord!!.bytes

            if (entryHandler.isOfType(data)) {
                // BEGIN TELEMETRY
//                Log.w("SR", "len: " + data.size + ", dev: " + result.device.address.toString())
                lastScansBuffer.add(result.rssi)
                if (result.rssi < lastScansMin) {
                    lastScansMin = result.rssi
                }
                if (result.rssi > lastScansMax) {
                    lastScansMax = result.rssi
                }
                if (lastScansBuffer.size >= numScansBeforeLogging) {
                    val dataTuple = Triple(lastScansBuffer.average(), lastScansMin, lastScansMax)
                    PowerGraph.updateGraph(dataTuple)
                    lastScansBuffer = mutableListOf()
                    lastScansMin = 127
                    lastScansMax = -128
                }
                // END TELEMETRY
                entryHandler.handlePayload(data, result.rssi)
            } else if (gaenHandler.isOfType(data)) {
                gaenHandler.handlePayload(data, result.rssi)
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
                val entryHandler = getEntryHandler(ctx)
                val gaenHandler = getGaenHandler(ctx)
                val instance = Scanner(entryHandler, gaenHandler)
                INSTANCE = instance
                return instance
            }
        }
    }

}