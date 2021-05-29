package com.blogspot.atifsoftwares.bluetoothexample

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.nio.charset.Charset
import java.util.*
import androidx.fragment.app.Fragment
import java.lang.reflect.Field


class MainActivity : AppCompatActivity() {
    var mStatusBlueTv: TextView? = null
    var mPairedTv: TextView? = null
    var mBlueIv: ImageView? = null
    var mOnBtn: Button? = null
    var mOffBtn: Button? = null
    var mDiscoverBtn: Button? = null
    var mPairedBtn: Button? = null
    var mAdvertisementBtn: Button? = null
    var mAdvertisementBtn2: Button? = null
    var mEnableGPSBtn: Button? = null
    var mDisableGPSBtn: Button? = null
    var mBlueAdapter: BluetoothAdapter? = null
    var mAdvertiser: BluetoothLeAdvertiser? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null
    private val mHandler: Handler = Handler()
    val LOCATION_FINE_PERM = Manifest.permission.ACCESS_FINE_LOCATION
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")

    val mScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result == null || result.device == null || TextUtils.isEmpty(result.device.name)) return
            if (result.device.name == "安娜的兔子大军") {
                Log.d("ADV_LOG", result.toString())
            }
//            Log.d("ADV_LOG", result.toString())
//            super.onScanResult(callbackType, result)
//            if (result == null || result.device == null || TextUtils.isEmpty(result.device.name)) return
//            val builder = StringBuilder(result.device.name)
//            builder.append("\n").append(
//                String(
//                    result.scanRecord!!.getServiceData(
//                        result.scanRecord!!.serviceUuids[0]
//                    )!!, Charset.forName("UTF-8")
//                )
//            )
//            Log.d("ADV_LOG", builder.toString())
        }

        override fun onBatchScanResults(results: List<ScanResult?>?) {
            showToast("onBatchScanResults")
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            showToast("onScanFailed")
            Log.e("BLE", "Discovery onScanFailed: $errorCode")
            super.onScanFailed(errorCode)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mStatusBlueTv = findViewById(R.id.statusBluetoothTv)
        mPairedTv = findViewById(R.id.pairedTv)
        mBlueIv = findViewById(R.id.bluetoothIv)
        mOnBtn = findViewById(R.id.onBtn)
        mOffBtn = findViewById(R.id.offBtn)
        mDiscoverBtn = findViewById(R.id.discoverableBtn)
        mPairedBtn = findViewById(R.id.pairedBtn)
        mAdvertisementBtn = findViewById(R.id.advertisementBtn)
        mAdvertisementBtn2 = findViewById(R.id.advertisementBtn2)
        mEnableGPSBtn = findViewById(R.id.enableGPSBtn)
        mDisableGPSBtn = findViewById(R.id.disableGPSBtn)

        mBlueAdapter = BluetoothAdapter.getDefaultAdapter()

        //check if bluetooth is available or not
        if (mBlueAdapter == null) {
            mStatusBlueTv?.setText("Bluetooth is not available")
        } else {
            mStatusBlueTv?.setText("Bluetooth is available")
            mAdvertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
            mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        }

        //set image according to bluetooth status(on/off)
        if (mBlueAdapter?.isEnabled() == true) {
            mBlueIv?.setImageResource(R.drawable.ic_action_on)
        } else {
            mBlueIv?.setImageResource(R.drawable.ic_action_off)
        }

        //on btn click
        mOnBtn?.setOnClickListener(View.OnClickListener {
            enableBluetooth()
        })
        //discover bluetooth btn click
        mDiscoverBtn?.setOnClickListener(View.OnClickListener {
            discoverBluetoothDevices()
        })
        //off btn click
        mOffBtn?.setOnClickListener(View.OnClickListener {
            disableBluetooth()
        })
        //get paired devices btn click
        mPairedBtn?.setOnClickListener(View.OnClickListener {
            getPairedDevices()
        })

        // start broadcasting advertisements
        mAdvertisementBtn?.setOnClickListener(View.OnClickListener {
            handleBroadcastingAdvertisements()
        })

        // start listening for advertisements
        mAdvertisementBtn2?.setOnClickListener(View.OnClickListener {
            handleScanningForAdvertisements()
        })

        // enable GPS
        mEnableGPSBtn?.setOnClickListener {
            showToast("enabling GPS...")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                makeLocationRequest()
            }

        }

        mDisableGPSBtn?.setOnClickListener {
            showToast("disabling GPS...")
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun makeLocationRequest() = requestPermissions(
        arrayOf(LOCATION_FINE_PERM),
        101
    )

    private fun discoverBluetoothDevices() {
        if (!mBlueAdapter?.isDiscovering()!!) {
            showToast("Making Your Device Discoverable")
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            startActivityForResult(intent, REQUEST_DISCOVER_BT)
        }
    }

    private fun enableBluetooth() {
        if (!mBlueAdapter?.isEnabled()!!) {
            showToast("Turning On Bluetooth...")
            //intent to on bluetooth
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_ENABLE_BT)
        } else {
            showToast("Bluetooth is already on")
        }
    }

    private fun disableBluetooth() {
        if (mBlueAdapter?.isEnabled() == true) {
            mBlueAdapter!!.disable()
            showToast("Turning Bluetooth Off")
            mBlueIv?.setImageResource(R.drawable.ic_action_off)
        } else {
            showToast("Bluetooth is already off")
        }
    }

    private fun getPairedDevices() {
        if (mBlueAdapter?.isEnabled() == true) {
            mPairedTv?.setText("Paired Devices")
            val devices = mBlueAdapter?.getBondedDevices()
            if (devices != null) {
                for (device in devices) {
                    mPairedTv?.append(
                        """
                                            Device: ${device.name}, $device
                                            """.trimIndent()
                    )
                }
            }
        } else {
            //bluetooth is off so can't get paired devices
            showToast("Turn on bluetooth to get paired devices")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun handleBroadcastingAdvertisements() {
        if (mBlueAdapter?.isEnabled() == true) {
            Log.d("DEBUG", "advertisement starting")
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build()
            Log.d("DEBUG", "settings initialized")
            val pUuid = ParcelUuid(UUID.fromString("47c6beb5-8a97-4019-9263-3d9009c2d852"))
            Log.d("DEBUG", "UUID initialized")
            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .addServiceData(pUuid, "Data".toByteArray(Charset.forName("UTF-8")))
                .build()
            Log.d("DEBUG", "data initialized")
            val advertisingCallback: AdvertiseCallback = object : AdvertiseCallback() {
                override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                    super.onStartSuccess(settingsInEffect)
                }

                override fun onStartFailure(errorCode: Int) {
                    Log.e("BLE", "Advertising onStartFailure: $errorCode")
                    super.onStartFailure(errorCode)
                }
            }
            Log.d("DEBUG", "callback initialized")
            mAdvertiser?.startAdvertising(settings, data, advertisingCallback)
            Log.d("DEBUG", "advertising initialized")
        } else {
            showToast("Turn on bluetooth to advertise")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun handleScanningForAdvertisements() {
        if (mBlueAdapter?.isEnabled() == true) {
            showToast("Getting advertisements...")
//            val filter: ScanFilter = ScanFilter.Builder().setServiceUuid("0xFD6F").build()
            val filters: List<ScanFilter> = listOf(ScanFilter.Builder().build())
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
            mBluetoothLeScanner?.startScan(filters, settings, mScanCallback)
        } else {
            showToast("Turn on bluetooth to receive advertisements")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT -> if (resultCode == RESULT_OK) {
                //bluetooth is on
                mBlueIv!!.setImageResource(R.drawable.ic_action_on)
                showToast("Bluetooth is on")
            } else {
                //user denied to turn bluetooth on
                showToast(requestCode.toString())
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //toast message function
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 0
        private const val REQUEST_DISCOVER_BT = 1
    }
}