package com.pancast.dongle.fragments.storage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pancast.dongle.R
import com.pancast.dongle.data.EntryViewModel

class StorageFragment : Fragment() {

    private lateinit var mEntryViewModel: EntryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_storage, container, false)
        // navigates to the upload screen
        val mUploadBtn: Button = view.findViewById(R.id.uploadBtn)

        val adapter = StorageAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mEntryViewModel = ViewModelProvider(this).get(EntryViewModel::class.java)
        mEntryViewModel.entries.observe(viewLifecycleOwner, {
            adapter.changeState(it)
        })

        mUploadBtn.setOnClickListener {
            val action = StorageFragmentDirections.actionStorageFragmentToUploadFragment(adapter.getState()
                .toTypedArray())
            findNavController().navigate(action)
//            findNavController().navigate(R.id.action_storageFragment_to_uploadFragment)
        }

        val mDeleteBtn: Button = view.findViewById(R.id.deleteBtn)
        mDeleteBtn.setOnClickListener {
//            var checkedList = adapter.getTrueState()
//            if (checkedList.size == 0 || checkedList.size == adapter.itemCount) {
            mEntryViewModel.deleteAllEntries()
//            } else {
//                for (e in checkedList) {
//                    mEntryViewModel.deleteOneEntry(e.ephemeralID, e.dongleTime, e.rssi)
//                }
//            }
        }

        val mDeleteBtnHist: Button = view.findViewById(R.id.deleteBtnHist)
        mDeleteBtnHist.setOnClickListener {
            mEntryViewModel.deleteOldEntries()
        }

        return view
    }
}