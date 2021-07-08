package com.pancast.dongle.fragments.telemetry

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.PointsGraphSeries
import com.pancast.dongle.R
import com.pancast.dongle.fragments.home.EntryHandler

class TelemetryFragment : Fragment() {
    private lateinit var graph: PowerGraph

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_telemetry, container, false)
        graph = PowerGraph.getPowerGraph(view)
        graph.createPlot()

        val entryHandler = EntryHandler.getEntryHandler(requireContext())
        val countView: TextView = view.findViewById(R.id.numCount)
        val countObserver = Observer<Int> {
            countView.text = it.toString()
        }
        entryHandler.count.observe(viewLifecycleOwner, countObserver)
        return view
    }
}