package com.pancast.dongle.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.pancast.dongle.R
import java.lang.Error

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val mListBtn: Button = view.findViewById(R.id.listBtn)
        mListBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_storageFragment)
        }

        val handler = EntryHandler(this)
        val scanner = Scanner(handler)

        val mStartBtn: Button = view.findViewById(R.id.startBtn)
        mStartBtn.setOnClickListener {
            try {
                scanner.startScan()
            } catch (e: Exception) {
                Toast.makeText(this.context, e.toString(), Toast.LENGTH_LONG).show()
            }
        }

        val mStopBtn: Button = view.findViewById(R.id.stopBtn)
        mStopBtn.setOnClickListener {
            try {
                scanner.stopScan()
            } catch (e: Exception) {
                Toast.makeText(this.context, e.toString(), Toast.LENGTH_LONG).show()
            }
        }
        return view
    }
}