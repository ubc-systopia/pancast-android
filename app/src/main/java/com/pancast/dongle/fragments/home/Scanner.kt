package com.pancast.dongle.fragments.home

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.os.Build
import androidx.annotation.RequiresApi
import com.pancast.dongle.utilities.*

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
            // need to notify user that bluetooth needs to be enabled
        }
    }

    fun stopScan() {

    }

    // want to be able to start scan
    // want to be able to stop scan
    // want to decode data
    // want to be able to store scanned data


    val mScanCallback: ScanCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result == null || result.scanRecord == null) return
            val data = result.scanRecord!!.bytes
            if (data.size < 30) {
                // data is too small
                return
            }
            val truncatedData = data.copyOfRange(0, 30)
            if (isPancastData(truncatedData)) {
                val rearrangedPayload = rearrangeData(truncatedData)
//                Log.d("TELEMETRY", "Encounter received")
//                val rssi = result.rssi.toString()
//                Log.d("TELEMETRY", "Signal strength: $rssi")
                handler.logEncounter(rearrangedPayload)
            }
        }
    }




}