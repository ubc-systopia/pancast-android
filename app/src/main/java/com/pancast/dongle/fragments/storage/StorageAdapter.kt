package com.pancast.dongle.fragments.storage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pancast.dongle.R
import com.pancast.dongle.data.Entry
import com.pancast.dongle.utilities.minutesIntoTime

class StorageAdapter: RecyclerView.Adapter<StorageAdapter.EntryViewHolder>() {

    private var entryList = emptyList<Entry>()

    class EntryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        return EntryViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.individual_entry, parent, false))
    }

    override fun onBindViewHolder(holder: EntryViewHolder, pos: Int) {
        val currentItem = entryList[pos]
        holder.itemView.findViewById<TextView>(R.id.item_eph_id).text = currentItem.ephemeralID
        holder.itemView.findViewById<TextView>(R.id.item_beacon_id).text = currentItem.beaconID.toString()
        holder.itemView.findViewById<TextView>(R.id.item_date).text = minutesIntoTime(currentItem.dongleTime)
    }

    fun changeState(entries: List<Entry>) {
        this.entryList = entries
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}