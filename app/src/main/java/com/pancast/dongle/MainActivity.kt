package com.pancast.dongle

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBarWithNavController(findNavController(R.id.fragmentContainerView))
    }

    // enables back button
    override fun onSupportNavigateUp(): Boolean {
        findNavController(R.id.fragmentContainerView).navigateUp()
        return super.onSupportNavigateUp()
    }
}