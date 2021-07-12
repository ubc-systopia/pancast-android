package com.pancast.dongle.fragments.storage

import com.pancast.dongle.data.Entry
import java.io.Serializable

class EntryWrapper(val entry: Entry, var isChecked: Boolean): Serializable {
    fun switchState() {
        isChecked = !isChecked
    }
}