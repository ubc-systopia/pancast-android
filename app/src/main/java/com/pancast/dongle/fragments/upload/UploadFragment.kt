package com.pancast.dongle.fragments.upload

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.pancast.dongle.R
import com.pancast.dongle.data.Entry
import com.pancast.dongle.data.EntryViewModel
import com.pancast.dongle.fragments.storage.EntryWrapper
import com.pancast.dongle.requests.RequestsHandler
import com.pancast.dongle.utilities.RequestType
import com.pancast.dongle.utilities.showAlertDialog
import java.lang.Exception
import kotlin.concurrent.thread

class UploadFragment : Fragment() {
    val data: UploadFragmentArgs by navArgs()

    private lateinit var mEntryViewModel: EntryViewModel
    private lateinit var entryData: Array<Entry>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mHandler = Handler()
        val view = inflater.inflate(R.layout.fragment_upload, container, false)
        mEntryViewModel = ViewModelProvider(this).get(EntryViewModel::class.java)

        // initialize entryData from arguments passed from storage
        entryData = data.dataToUpload

        val mUploadRiskBtn: Button = view.findViewById(R.id.uploadRiskBtn)
        mUploadRiskBtn.setOnClickListener {
            thread(start = true) {
                try {
                    val dataToUpload = entryData.toList()
                    RequestsHandler().uploadData(dataToUpload, RequestType.RISK_TYPE)
                    mHandler.post {
                        showAlertDialog(
                            requireContext(),
                            "Success!",
                            "Request was successfully sent"
                        )
                    }
                } catch (e: Exception) {
                    mHandler.post {
                        showAlertDialog(
                            requireContext(),
                            "Failure!",
                            e.localizedMessage!!
                        )
                    }
                }
            }
        }

        val mUploadEpiBtn: Button = view.findViewById(R.id.uploadEpiBtn)
        mUploadEpiBtn.setOnClickListener {
            thread(start = true) {
                try {
                    val dataToUpload = entryData.toList()
                    RequestsHandler().uploadData(dataToUpload, RequestType.EPI_TYPE)
                } catch (e: Exception) {
                    // handle
                }
            }
        }
        return view
    }
}