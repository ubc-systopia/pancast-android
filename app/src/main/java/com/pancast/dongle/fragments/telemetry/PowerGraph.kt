package com.pancast.dongle.fragments.telemetry

import android.util.Log
import android.view.View
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.pancast.dongle.R

class PowerGraph(viewGraph: GraphView) {
    var data: MutableList<Double> = mutableListOf()
    var series: LineGraphSeries<DataPoint> = LineGraphSeries()
    private val plot: GraphView = viewGraph

    init {
        plot.viewport.isScalable = true
        plot.viewport.setScalableY(true)
        plot.viewport.isScrollable = true
        plot.viewport.setScrollableY(true)

        plot.viewport.setMaxY(0.0)
        plot.viewport.setMinY(-128.0)
        plot.viewport.setMinX(0.0)
        plot.viewport.setMaxX(100.0)
        plot.viewport.isYAxisBoundsManual = true;
        plot.viewport.isXAxisBoundsManual = true;

        plot.addSeries(series)
    }

    fun createPlot() {
        val size = data.size
        if (size != 0) {
            series.appendData(DataPoint(size - 1.0, data[size - 1]), true, 1000)
        }
        plot.removeAllSeries()
        plot.addSeries(series)
//        series = LineGraphSeries()
//        for (i in data.indices) {
//            try {
//                val x: Double = i.toDouble()
//                val y: Double = data[i]
//                series.appendData(DataPoint(x, y), true, 1000)
//            } catch (e: Exception) {
//                Log.e("TELEMETRY", "Plotting failed")
//            }
//        }
    }

    companion object {
        @Volatile
        var INSTANCE: PowerGraph? = null

        fun getPowerGraph(v: View): PowerGraph {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val tempGraph: GraphView = v.findViewById(R.id.powerGraph)
                val instance = PowerGraph(tempGraph)
                INSTANCE = instance
                return instance
            }
        }

        fun updateGraph(data: Double) {
            val graph = INSTANCE
            if (graph != null) {
                synchronized(this) {
                    graph.data.add(data)
                    graph.createPlot()
                }
            }
        }
    }


}