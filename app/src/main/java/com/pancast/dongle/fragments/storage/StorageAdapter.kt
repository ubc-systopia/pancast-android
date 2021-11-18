package com.pancast.dongle.fragments.storage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pancast.dongle.R
import com.pancast.dongle.data.Entry
import com.pancast.dongle.utilities.minutesIntoDateTime
import com.pancast.dongle.utilities.minutesIntoTime

class StorageAdapter: RecyclerView.Adapter<StorageAdapter.EntryViewHolder>() {

    private var entryList = emptyList<EntryWrapper>()

    class EntryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        return EntryViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.individual_entry, parent, false))
    }

    override fun onBindViewHolder(holder: EntryViewHolder, pos: Int) {
        val currentItem = entryList[pos]
        holder.itemView.findViewById<TextView>(R.id.item_eph_id).text = currentItem.entry.ephemeralID
        holder.itemView.findViewById<TextView>(R.id.item_beacon_id).text = currentItem.entry.beaconID.toString()
        holder.itemView.findViewById<TextView>(R.id.item_date).text = minutesIntoDateTime(currentItem.entry.dongleTime)
        holder.itemView.findViewById<TextView>(R.id.item_location_id).text = currentItem.entry.locationID.toString()
        holder.itemView.findViewById<TextView>(R.id.item_rssi).text = currentItem.entry.rssi.toString()
        val checkBox = holder.itemView.findViewById<CheckBox>(R.id.excludeCheckBox)
        checkBox.setOnClickListener {
            currentItem.switchState()
        }
    }

    fun changeState(entries: List<Entry>) {
        val oldList = this.entryList
        this.entryList = entries.map{EntryWrapper(it, false)}
        for (oldEntry in oldList) {
            for (i in this.entryList.indices) {
                if (entryList[i].entry == oldEntry.entry) {
                    entryList[i].isChecked = oldEntry.isChecked
                }
            }
        }
        notifyDataSetChanged()
    }

    fun getState(): List<Entry> {
        return entryList.filter{!it.isChecked}.map{ it.entry }
    }

    fun getTrueState(): List<Entry> {
        return entryList.filter { it.isChecked }.map { it.entry }
    }
    override fun getItemCount(): Int {
        return entryList.size
    }
}