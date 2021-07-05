package com.pancast.dongle.fragments.upload

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.pancast.dongle.R

class UploadFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_upload, container, false)

        val mUploadRiskBtn: Button = view.findViewById(R.id.uploadRiskBtn)
        mUploadRiskBtn.setOnClickListener {
            //
        }

        val mUploadEpiBtn: Button = view.findViewById(R.id.uploadEpiBtn)
        mUploadEpiBtn.setOnClickListener {
            //
        }

        return view
    }
}