package com.pancast.dongle.utilities

import android.app.AlertDialog
import android.content.Context
import com.pancast.dongle.R
import com.pancast.dongle.utilities.Constants.MILLISECONDS_IN_SECOND
import com.pancast.dongle.utilities.Constants.SECONDS_IN_MINUTE
import java.text.SimpleDateFormat
import java.util.*


const val MaxBroadcastSize = 30

fun getMinutesSinceLinuxEpoch(): Long {
    return System.currentTimeMillis() / (MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE)
}

fun minutesIntoTime(minutes: Int): String {
    val millisecondsSinceEpoch = minutes.toLong() * MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE
    val format = SimpleDateFormat("yyyy-MM-dd, hh:mm", Locale.CANADA)
    val date = Date(millisecondsSinceEpoch)
    return format.format(date)
}

fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

fun showAlertDialog(ctx: Context, title: String, description: String) {
    AlertDialog.Builder(ctx)
        .setTitle(title)
        .setMessage(description) // Specifying a listener allows you to take an action before dismissing the dialog.
        // The dialog is automatically dismissed when a dialog button is clicked.
        .setPositiveButton(
            R.string.yes
        ) { _, _ -> }
        .setNegativeButton(R.string.no, null)
        .setIcon(R.drawable.ic_dialog_alert)
        .show()
}
