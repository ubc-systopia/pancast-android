package com.pancast.dongle.fragments.telemetry

import android.view.View
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.pancast.dongle.R

class PowerGraph(viewGraph: GraphView) {
    var data: MutableList<Triple<Double, Int, Int>> = mutableListOf()
    var averageSeries: LineGraphSeries<DataPoint> = LineGraphSeries()
    var minSeries: LineGraphSeries<DataPoint> = LineGraphSeries()
    var maxSeries: LineGraphSeries<DataPoint> = LineGraphSeries()
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

        plot.addSeries(averageSeries)
        plot.addSeries(minSeries)
        plot.addSeries(maxSeries)
    }

    fun createPlot() {
        val size = data.size
        if (size != 0) {
            averageSeries.appendData(DataPoint(size - 1.0, data[size - 1].first), true, 1000)
            minSeries.appendData(DataPoint(size - 1.0, data[size - 1].second.toDouble()), true, 1000)
            maxSeries.appendData(DataPoint(size - 1.0, data[size - 1].third.toDouble()), true, 1000)
        }
        plot.removeAllSeries()
        plot.addSeries(averageSeries)
        plot.addSeries(minSeries)
        plot.addSeries(maxSeries)
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

        fun updateGraph(data: Triple<Double, Int, Int>) {
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