package com.example.bluetooth_sample

import android.Manifest
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
import com.blogspot.atifsoftwares.bluetoothexample.R
import com.example.bluetooth_sample.data.Entry
import com.example.bluetooth_sample.data.EntryViewModel
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.*

class BluetoothActivity : AppCompatActivity() {
    val ENCOUNTER_TIME_TRESHOLD: Long = 5
    // initialize cache
    private var mEphemeralIDCache: MutableMap<ByteArray, Long> = mutableMapOf()
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
    private var mBlueAdapter: BluetoothAdapter? = null
    private var mAdvertiser: BluetoothLeAdvertiser? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null
    private val LOCATION_FINE_PERM = Manifest.permission.ACCESS_FINE_LOCATION
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")

    val mScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result == null || result.scanRecord == null) return
            if (result.scanRecord!!.serviceUuids != null) {
                val uuid: ParcelUuid = result.scanRecord!!.serviceUuids[0]
                val pancastUUID: ParcelUuid = ParcelUuid.fromString("00002222-0000-1000-8000-00805f9b34fb")
                if (uuid == pancastUUID) { // need to replace with 2222 for testing...
                    Log.d("TELEMETRY", "Encounter received")
                    showToast("Received broadcast")
                    val payload = result.scanRecord!!.bytes
                    if (payload.size != 31) {
                        Log.d("TELEMETRY", "Bad payload size")
                    } else {
                        val payloadWithoutLength = payload.copyOfRange(1, 31)
                        val rssi = result.rssi.toString()
                        Log.d("TELEMETRY", "Signal strength: $rssi")
//                        logEncounter(payloadWithoutLength)
                    }
                }
            }
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

        // start listening for advertisements
        mLogAdvertisementBtn?.setOnClickListener{
            handleScanningForAdvertisements()
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun handleScanningForAdvertisements() {
        if (mBlueAdapter?.isEnabled == true) {
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

    fun logEncounter(input: ByteArray) {
        // need some form of expiry mechanism for old ephemeral IDs within the map. cron job to remove
        // old entries?
        val decoded: DecodedData = decodeData(input)
        if (!mEphemeralIDCache.containsKey(decoded.ephemeralID)) {
            mEphemeralIDCache[decoded.ephemeralID] = getMinutesSinceLinuxEpoch()
        } else {
            val oldTime = mEphemeralIDCache[decoded.ephemeralID]
            val newTime = getMinutesSinceLinuxEpoch()
            if (newTime - oldTime!! >= ENCOUNTER_TIME_TRESHOLD) {
                val entry = Entry(decoded.ephemeralID, decoded.beaconID, decoded.locationID, decoded.beaconTime, oldTime.toInt())
                mEntryViewModel.addEntry(entry)
                mEphemeralIDCache[decoded.ephemeralID] = newTime
            }
        }
    }
    // should deserve its own class file
    data class DecodedData
    constructor(val beaconTime: Int, val beaconID: Int, val locationID: Long, val ephemeralID: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DecodedData

            if (beaconTime != other.beaconTime) return false
            if (beaconID != other.beaconID) return false
            if (locationID != other.locationID) return false
            if (!ephemeralID.contentEquals(other.ephemeralID)) return false

            return true
        }

        @ExperimentalUnsignedTypes
        override fun hashCode(): Int {
            var result = beaconTime.hashCode()
            result = 31 * result + beaconID.hashCode()
            result = 31 * result + locationID.hashCode()
            result = 31 * result + ephemeralID.contentHashCode()
            return result
        }
    }

    private fun decodeData(d: ByteArray): DecodedData {
        if (d.size != 30) {
            throw IllegalArgumentException("size is not correct")
        }
//        val beaconTime = d.copyOfRange(0, 4).getUInt32()
        val beaconTime = ByteBuffer.wrap(d.copyOfRange(0, 4)).order(ByteOrder.LITTLE_ENDIAN).int
        val beaconID = ByteBuffer.wrap(d.copyOfRange(4, 8)).order(ByteOrder.LITTLE_ENDIAN).int
        val locationID = ByteBuffer.wrap(d.copyOfRange(8, 16)).order(ByteOrder.LITTLE_ENDIAN).long
//        val beaconID = d.copyOfRange(4, 8).getUInt32()
//        val locationID = d.copyOfRange(8, 16).getUInt64()
        val ephemeralID: ByteArray = d.copyOfRange(16, 30)
        return DecodedData(beaconTime, beaconID, locationID, ephemeralID)
    }

    //toast message function
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 0
    }
}