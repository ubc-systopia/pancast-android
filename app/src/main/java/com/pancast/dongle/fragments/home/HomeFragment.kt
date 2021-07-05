package com.pancast.dongle.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.pancast.dongle.R

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

        return view
    }
}