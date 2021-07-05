package com.pancast.dongle.fragments.home

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.lang.Exception

class Scanner(handler: EntryHandler) {
    private val mBlueAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val mBluetoothLeScanner: BluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
    private val mScanCallback: ScanCallback = object: ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result == null || result.scanRecord == null) return
            val data = result.scanRecord!!.bytes
            if (handler.isPancastPayload(data)) {
                Log.d("TELEMETRY", "RSSI: " + result.rssi.toString())
                Log.d("TELEMETRY", "TxPower: " + result.txPower.toString())
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


}