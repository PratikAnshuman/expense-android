package com.prasoon.expense.custom

import android.R
import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import kotlinx.android.synthetic.main.chart_marker_view.view.*


class PieChartMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val tvContent: TextView

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry, highlight: Highlight?) {
        tvContent.text = "" + e.data // set the entry-value as the display text
    }

    // this will center the marker-view horizontally
    val xOffset: Int
        get() =// this will center the marker-view horizontally
            -(width / 2)

    // this will cause the marker-view to be above the selected value
    val yOffset: Int
        get() =// this will cause the marker-view to be above the selected value
            -height

    init {
        // this markerview only displays a textview
        tvContent = tvContentMarker
    }
}