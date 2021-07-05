package com.pancast.dongle

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.pancast.dongle.data.Entry
import com.pancast.dongle.data.EntryViewModel
import com.pancast.dongle.utilities.Constants.ENCOUNTER_TIME_THRESHOLD
import com.pancast.dongle.utilities.*
import com.pancast.dongle.utilities.Constants.LOCATION_FINE_PERM
import java.nio.charset.Charset
import java.util.*

class BluetoothActivity : AppCompatActivity() {
    // initialize cache
    private var mEphemeralIDCache: MutableMap<String, Long> = mutableMapOf()
    private lateinit var mEntryViewModel: EntryViewModel

    private var mStatusBlueTv: TextView? = null
    private var mPairedTv: TextView? = null
    private var mBlueIv: ImageView? = null
    private var mOnBtn: Button? = null
    private var mOffBtn: Button? = null
    private var mAdvertisementBtn: Button? = null
    private var mLogAdvertisementBtn: Button? = null
    private var mEnableGPSBtn: Button? = null
    private var mDisableGPSBtn: Button? = null
    private var mDumpDatabaseBtn: Button? = null
    private var mInsertDataBtn: Button? = null
    private var mBlueAdapter: BluetoothAdapter? = null
    private var mAdvertiser: BluetoothLeAdvertiser? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("STARTUP", "BluetoothActivity")
        mEntryViewModel = ViewModelProvider(this).get(EntryViewModel::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        mStatusBlueTv = findViewById(R.id.statusBluetoothTv)
        mPairedTv = findViewById(R.id.pairedTv)
        mBlueIv = findViewById(R.id.bluetoothIv)
        mOnBtn = findViewById(R.id.onBtn)
        mOffBtn = findViewById(R.id.offBtn)
        mAdvertisementBtn = findViewById(R.id.advertisementBtn)
        mLogAdvertisementBtn = findViewById(R.id.logAdvertisementBtn)
        mEnableGPSBtn = findViewById(R.id.enableGPSBtn)
        mDisableGPSBtn = findViewById(R.id.disableGPSBtn)
        mDumpDatabaseBtn = findViewById(R.id.dumpDatabaseBtn)
        mInsertDataBtn = findViewById(R.id.insertDataBtn)

        mBlueAdapter = BluetoothAdapter.getDefaultAdapter()

        // setting up action bar for returning to parent activity
        val actionBar = supportActionBar
        actionBar!!.title = "Bluetooth Activity"
        actionBar.setDisplayHomeAsUpEnabled(true)

        //check if bluetooth is available or not
        if (mBlueAdapter == null) {
            mStatusBlueTv?.text = "Bluetooth is not available"
        } else {
            mStatusBlueTv?.text = "Bluetooth is available"
            mAdvertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
            mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
        }

        //set image according to bluetooth status(on/off)
        if (mBlueAdapter?.isEnabled == true) {
            mBlueIv?.setImageResource(R.drawable.ic_action_on)
        } else {
            mBlueIv?.setImageResource(R.drawable.ic_action_off)
        }

        //on btn click
        mOnBtn?.setOnClickListener {
            enableBluetooth()
        }
        //off btn click
        mOffBtn?.setOnClickListener{
            disableBluetooth()
        }

        // start broadcasting advertisements
        mAdvertisementBtn?.setOnClickListener{
            handleBroadcastingAdvertisements()
        }

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

        mInsertDataBtn?.setOnClickListener{
            val ephID = "0102030405060708090a0b0c0d"
            val beaconID = 1
            val locationID: Long = 1
            val beaconTime = 1
            val dongleTime = 1
            val entry = Entry(ephID, beaconID, locationID, beaconTime, dongleTime)
            mEntryViewModel.addEntry(entry)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun makeLocationRequest() = requestPermissions(
        arrayOf(LOCATION_FINE_PERM),
        101
    )

    private fun enableBluetooth() {
        if (!mBlueAdapter?.isEnabled!!) {
            showToast("Turning On Bluetooth...")
            //intent to on bluetooth
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_ENABLE_BT)
        } else {
            showToast("Bluetooth is already on")
        }
    }

    private fun disableBluetooth() {
        if (mBlueAdapter?.isEnabled == true) {
            mBlueAdapter!!.disable()
            showToast("Turning Bluetooth Off")
            mBlueIv?.setImageResource(R.drawable.ic_action_off)
        } else {
            showToast("Bluetooth is already off")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun handleBroadcastingAdvertisements() {
        if (mBlueAdapter?.isEnabled == true) {
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
    }
}