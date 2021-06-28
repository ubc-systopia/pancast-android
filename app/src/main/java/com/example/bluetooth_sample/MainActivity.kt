package com.example.bluetooth_sample

import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.bluetoothexample.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("STARTUP", "MainActivity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bluetoothActivityBtn: Button = findViewById(R.id.bluetoothActivityBtn)

        bluetoothActivityBtn.setOnClickListener {
            val intent = Intent(this, BluetoothActivity::class.java)
            startActivity(intent)
        }
    }
}