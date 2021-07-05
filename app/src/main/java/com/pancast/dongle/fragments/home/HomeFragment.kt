package com.pancast.dongle.fragments.home

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pancast.dongle.R
import com.pancast.dongle.cuckoo.CuckooFilter
import com.pancast.dongle.data.EntryViewModel
import com.pancast.dongle.decodeHex
import com.pancast.dongle.requests.RequestsHandler
import com.pancast.dongle.utilities.Constants.LOCATION_FINE_PERM
import com.pancast.dongle.utilities.showAlertDialog
import java.lang.Error
import java.util.jar.Manifest
import kotlin.concurrent.thread

class HomeFragment : Fragment() {

    private lateinit var checkLocationPermission: ActivityResultLauncher<Array<String>>
    private lateinit var mEntryViewModel: EntryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // for showing alert dialogs from within threads
        val mHandler = Handler()

        mEntryViewModel = ViewModelProvider(this).get(EntryViewModel::class.java)

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

        val handler = EntryHandler(this)
        val scanner = Scanner(handler)

        val mStartBtn: Button = view.findViewById(R.id.startBtn)
        mStartBtn.setOnClickListener {
            try {
                permissionHandler()
                scanner.startScan()
            } catch (e: Exception) {
                showAlertDialog(requireContext(), "Error", "Scan could not be started")
            }
        }

        val mStopBtn: Button = view.findViewById(R.id.stopBtn)
        mStopBtn.setOnClickListener {
            try {
                scanner.stopScan()
            } catch (e: Exception) {
                showAlertDialog(requireContext(), "Error", "Scan could not be stopped")
            }
        }

        val mCheckExposureBtn: Button = view.findViewById(R.id.checkExposureBtn)
        mCheckExposureBtn.setOnClickListener {
            thread(start = true) {
                val riskBroadcast = RequestsHandler().downloadRiskBroadcast()
                if (riskBroadcast != null) {
                    val cf = CuckooFilter(riskBroadcast)
                    val entries = mEntryViewModel.repository.getAllEntries()
                    for (entry in entries) {
                        val result = cf.lookupItem(entry.ephemeralID.decodeHex())
                        if (result) {
                            mHandler.post {
                                showAlertDialog(
                                    requireContext(),
                                    "Exposure",
                                    "You may have been exposed"
                                )
                            }
                        }
                    }
                }
            }

        }

        return view
    }

    private fun permissionHandler() {
        val hasFineLocationPermission = ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
        if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED && hasCoarseLocationPermission == PackageManager.PERMISSION_DENIED) {
            checkLocationPermission.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }
}