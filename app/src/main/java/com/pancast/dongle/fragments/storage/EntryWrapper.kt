package com.pancast.dongle.fragments.storage

import com.pancast.dongle.data.Entry

class EntryWrapper(val entry: Entry, var isChecked: Boolean) {
    fun switchState() {
        isChecked = !isChecked
    }
}