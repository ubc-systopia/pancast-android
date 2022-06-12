package com.pancast.dongle.fragments.home

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pancast.dongle.R
import com.pancast.dongle.cuckoo.CuckooFilter
import com.pancast.dongle.data.*
import com.pancast.dongle.utilities.decodeHex
import com.pancast.dongle.fragments.home.BleScannerService.Companion.startScanningService
import com.pancast.dongle.fragments.home.BleScannerService.Companion.stopScanningService
import com.pancast.dongle.gaen.PacketParser
import com.pancast.dongle.gaen.getRPIsFromTEK
import com.pancast.dongle.requests.RequestsHandler
import com.pancast.dongle.data.LoginViewModel
import com.pancast.dongle.utilities.Constants.PanCastUUID
import com.pancast.dongle.utilities.Constants.devKey
import com.pancast.dongle.utilities.showAlertDialog
import kotlin.concurrent.thread

var dbUserDevId: String = "foo"
var dbUserEntry: LoggedInUser ?= null

class HomeFragment : Fragment() {

    private lateinit var checkLocationPermission: ActivityResultLauncher<Array<String>>
    private lateinit var mEntryViewModel: EntryViewModel
    private lateinit var exposureKeyDao: ExposureKeyDao
    private lateinit var exposureKeyRepository: ExposureKeyRepository
    private lateinit var loginDataSource: LoginDataSource
    private lateinit var loginDao: LoginDao
    private lateinit var loginRepository: LoginRepository
    private lateinit var loginViewModel: LoginViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // for showing alert dialogs from within threads
        val mHandler = Handler()

        mEntryViewModel = ViewModelProvider(this).get(EntryViewModel::class.java)
        exposureKeyDao = PancastDatabase.getDatabase(requireContext()).exposureKeyDao()
        exposureKeyRepository = ExposureKeyRepository(exposureKeyDao)

        loginDataSource = LoginDataSource()
        loginDao = PancastDatabase.getDatabase(requireContext()).loginDao()
        loginRepository = LoginRepository(loginDataSource, loginDao)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        var t: Thread = thread(start=true) {
            dbUserEntry = loginDao.getEntryDevKey(devKey)
            Log.d("[H]", "devKey: " + devKey + ", #DB entries: " +
                loginRepository.getNumEntriesDB() + ", dbUserEntry: " + dbUserEntry?.devId)
            if (dbUserEntry == null) {
                dbUserDevId = loginViewModel.register()
            } else {
                dbUserDevId = dbUserEntry!!.devId
            }
        }
        t.join()

        var regID: TextView = view.findViewById<TextView>(R.id.registrationID)
        var regText: String = "Android ID:  " + PanCastUUID + "\nDevice Key: " + devKey +
                "\nDevice ID:    " + dbUserDevId
        regID.setText(regText)

        checkLocationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == false &&
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == false) {
                // denied
            }
        }

        val mListBtn: Button = view.findViewById(R.id.listBtn)
        mListBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_storageFragment)
        }

        val mTelemetryBtn: Button = view.findViewById(R.id.viewTelemetryBtn)
        mTelemetryBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_telemetryFragment)
        }

        val mStartBtn: Button = view.findViewById(R.id.startBtn)
        mStartBtn.setOnClickListener {
            try {
                locationPermissionHandler()
                startScanningService(requireContext())
            } catch (e: Exception) {
                val msg = e.localizedMessage!!
                showAlertDialog(requireContext(), "Error", msg)
            }
        }

        val mStopBtn: Button = view.findViewById(R.id.stopBtn)
        mStopBtn.setOnClickListener {
            try {
                stopScanningService(requireContext())
            } catch (e: Exception) {
                val msg = e.localizedMessage!!
                showAlertDialog(requireContext(), "Error", msg)
            }
        }

        val mCheckExposureBtn: Button = view.findViewById(R.id.checkExposureBtn)
        mCheckExposureBtn.setOnClickListener {
            thread(start = true) {
                checkRiskBroadcast(mHandler)
            }
        }

        val mCheckGaenExposureButton: Button = view.findViewById(R.id.checkGaenExposureBtn)
        mCheckGaenExposureButton.setOnClickListener {
            thread(start = true) {
                checkGaenRiskBroadcast(mHandler)
            }
        }

        return view
    }

    private fun checkGaenRiskBroadcast(mHandler: Handler) {
        try {
            val riskBroadcast = RequestsHandler().downloadGaenRiskBroadcast()
            if (riskBroadcast != null) {
                val parsedData = PacketParser(riskBroadcast)
                val encounteredKeys = exposureKeyRepository.getAllEntries().map{it.rollingProximityIdentifier.decodeHex()}
                for (entry in parsedData.tekExport.keysList) {
                    val key = entry.keyData.toByteArray()
                    val startInterval = entry.rollingStartIntervalNumber
                    val listOfRPIs = getRPIsFromTEK(key, startInterval)
                    for (rpi in listOfRPIs) {
                        if (encounteredKeys.contains(rpi)) {
                            mHandler.post {
                                showAlertDialog(
                                    requireContext(),
                                    "Exposure: $rpi",
                                    "You may have been exposed to an infected beacon or user."
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            showErrorMessage(e, mHandler)
        }
    }

    private fun checkRiskBroadcast(mHandler: Handler) {
        try {
            val riskBroadcast = RequestsHandler().downloadRiskBroadcast()
            if (riskBroadcast != null) {
                val cf = CuckooFilter(riskBroadcast)
                val entries = mEntryViewModel.repository.getAllEntries()
                val numEntries = entries.size
                val rbLen = riskBroadcast.size
                Log.w("CF", "download size: $rbLen, #entries: $numEntries")
                var numMatchEntries = 0
                var numMatchBeacons = 0
                var matchBeacons: MutableList<Long> = mutableListOf()
                for (entry in entries) {
                    val entryEphID =
                        entry.ephemeralID + "00" // because current ephIDs are 14 bytes, append extra null byte
                    val result = cf.lookupItem(entryEphID.decodeHex())
                    if (!result) {
                        continue
                    }
                    numMatchEntries += 1
                    if (matchBeacons.contains(entry.locationID) == false) {
                        matchBeacons += entry.locationID
                    }
                }
                if (numMatchEntries > 0) {
                    numMatchBeacons = matchBeacons.size
                    mHandler.post {
                        showAlertDialog(requireContext(), "Exposure: ",
                            "You may have been infected. You have encountered " +
                            "$numMatchBeacons infected beacons and exposed $numMatchEntries times."
                        )
                    }
                }
            }
        } catch (e: Exception) {
            showErrorMessage(e, mHandler)
        }
    }

    private fun locationPermissionHandler() {
        val hasFineLocationPermission = ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
        if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED && hasCoarseLocationPermission == PackageManager.PERMISSION_DENIED) {
            checkLocationPermission.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun showErrorMessage(e: Exception, mHandler: Handler) {
        if (e.localizedMessage != null) {
            val msg = e.localizedMessage!!
            mHandler.post {
                showAlertDialog(
                    requireContext(),
                    "Error",
                    msg
                )
            }
        } else {
            mHandler.post {
                showAlertDialog(
                    requireContext(),
                    "Error",
                    "Unknown exception"
                )
            }
        }
    }
}