package com.prasoon.expense.custom

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class PieChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PieChart(context, attrs, defStyleAttr), OnChartValueSelectedListener {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5F, 10F, 5F, 5F)

        pieChart.dragDecelerationFrictionCoef = 0.95f

        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)

        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        pieChart.holeRadius = 60f
        pieChart.transparentCircleRadius = 62f

        pieChart.setDrawCenterText(true)

        pieChart.rotationAngle = 0F
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true

        // add a selection listener
        pieChart.setOnChartValueSelectedListener(this)

        pieChart.animateY(1400, Easing.EaseInOutQuad)

        val l: Legend = pieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.textSize = 14f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling

        // entry label styling
//        chart.setEntryLabelColor(Color.BLACK)
//        chart.setEntryLabelTypeface(Typeface.SANS_SERIF)
//        chart.setEntryLabelTextSize(12f)
        pieChart.setDrawEntryLabels(false)
    }

    override fun onNothingSelected() {
        Log.i("PieChart", "nothing selected")
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e == null)
            return
        if (h != null) {
            Log.i(
                "VAL SELECTED",
                "Value: " + e.y + ", index: " + h.x
                        + ", DataSet index: " + h.dataSetIndex
            )
        }

    }

}