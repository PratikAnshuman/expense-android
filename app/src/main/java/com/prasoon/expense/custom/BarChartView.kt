package com.prasoon.expense.custom

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class BarChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BarChart(context, attrs, defStyleAttr), OnChartValueSelectedListener {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        barChart.getDescription().setEnabled(false)

        barChart.setOnChartValueSelectedListener(this)

        barChart.setDrawGridBackground(false)

        barChart.setTouchEnabled(true)

        // enable scaling and dragging

        // enable scaling and dragging
        barChart.setDragEnabled(true)
        barChart.setScaleEnabled(true)

        barChart.setMaxVisibleValueCount(200)
        barChart.setPinchZoom(true)


        val l: Legend = barChart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.typeface = Typeface.SANS_SERIF

        val yl: YAxis = barChart.getAxisLeft()
        yl.typeface = Typeface.SANS_SERIF
        yl.spaceTop = 30f
        yl.spaceBottom = 30f
        yl.setDrawZeroLine(false)

        barChart.getAxisRight().setEnabled(false)

        val xl: XAxis = barChart.getXAxis()
        xl.position = XAxis.XAxisPosition.BOTTOM
        xl.typeface = Typeface.SANS_SERIF
//        setData()

//            val mv = XYMarkerView(this, xAxisFormatter)
//            mv.setChartView(chart) // For bounds control
//
//            chart.setMarker(mv) // Set the marker to the chart

    }

    override fun onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e == null)
            return;
        if (h != null) {
            Log.i(
                "VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex()
            )
        };

    }

}