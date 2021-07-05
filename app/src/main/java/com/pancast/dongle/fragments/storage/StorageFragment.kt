package com.pancast.dongle.fragments.storage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.pancast.dongle.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StorageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_storage, container, false)
        val mUploadBtn: Button = view.findViewById(R.id.uploadBtn)
        mUploadBtn.setOnClickListener {
            findNavController().navigate(R.id.action_storageFragment_to_uploadFragment)
        }
        return view
    }
}