package com.pancast.dongle.fragments.home

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.os.Build
import androidx.annotation.RequiresApi
import java.lang.Exception

class Scanner(handler: EntryHandler) {
    private val mBlueAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val mBluetoothLeScanner: BluetoothLeScanner = mBlueAdapter.bluetoothLeScanner

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startScan() {
        if (mBlueAdapter.isEnabled) {
            val filters: List<ScanFilter> = listOf(ScanFilter.Builder().build())
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
            mBluetoothLeScanner.startScan(filters, settings, mScanCallback)
        } else {
            throw Exception("Bluetooth is not enabled")
        }
    }

    fun stopScan() {
        if (mBlueAdapter.isDiscovering) {
            mBluetoothLeScanner.stopScan(mScanCallback)
        } else {
            throw Exception("Scan is not ongoing")
        }
    }

    private val mScanCallback: ScanCallback = object: ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result == null || result.scanRecord == null) return
            val data = result.scanRecord!!.bytes
            handler.handlePayload(data)
        }
    }
}