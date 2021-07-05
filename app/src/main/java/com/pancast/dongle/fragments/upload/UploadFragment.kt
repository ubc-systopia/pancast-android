package com.pancast.dongle.fragments.upload

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.pancast.dongle.R
import com.pancast.dongle.data.EntryViewModel
import com.pancast.dongle.fragments.upload.requests.RequestsHandler
import com.pancast.dongle.utilities.RequestType
import java.lang.Exception
import kotlin.concurrent.thread

class UploadFragment : Fragment() {

    private lateinit var mEntryViewModel: EntryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_upload, container, false)
        mEntryViewModel = ViewModelProvider(this).get(EntryViewModel::class.java)

        val mUploadRiskBtn: Button = view.findViewById(R.id.uploadRiskBtn)
        mUploadRiskBtn.setOnClickListener {
            thread(start = true) {
                try {
                    val dataToUpload = mEntryViewModel.repository.getAllEntries()
                    RequestsHandler().uploadData(dataToUpload, RequestType.RISK_TYPE)
                } catch (e: Exception) {
                    // handle
                }
            }
        }

        val mUploadEpiBtn: Button = view.findViewById(R.id.uploadEpiBtn)
        mUploadEpiBtn.setOnClickListener {
            thread(start = true) {
                try {
                    val dataToUpload = mEntryViewModel.repository.getAllEntries()
                    RequestsHandler().uploadData(dataToUpload, RequestType.EPI_TYPE)
                } catch (e: Exception) {
                    // handle
                }
            }
        }
        return view
    }
}