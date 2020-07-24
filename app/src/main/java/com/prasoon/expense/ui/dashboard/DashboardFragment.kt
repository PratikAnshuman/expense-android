package com.prasoon.expense.ui.dashboard

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.prasoon.expense.ExpenseViewModelFactory
import com.prasoon.expense.R
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
//    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ExpenseViewModelFactory(activity!!.application).create(DashboardViewModel::class.java)
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.graph_options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        dashboardViewModel.onFragmentLoaded()

        dashboardViewModel.pieDataSet.observe(viewLifecycleOwner, Observer {
            setPieData(it)
        })

        dashboardViewModel.barDataSet.observe(viewLifecycleOwner, Observer {
            pieChart.visibility = View.GONE
            barChart.getDescription().setEnabled(false)

//            barChart.setOnChartValueSelectedListener(this)

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
            xl.position = XAxisPosition.BOTTOM
            xl.typeface = Typeface.SANS_SERIF
            setData()

//            val mv = XYMarkerView(this, xAxisFormatter)
//            mv.setChartView(chart) // For bounds control
//
//            chart.setMarker(mv) // Set the marker to the chart


        })
    }

    private fun setData() {
        val NoOfEmp = ArrayList<BarEntry>()

        NoOfEmp.add(BarEntry(2008f,945f))
        NoOfEmp.add(BarEntry(2009f,1040f))
        NoOfEmp.add(BarEntry(2010f,1133f))
        NoOfEmp.add(BarEntry(2011f,1240f))
        NoOfEmp.add(BarEntry(2012f,1369f))
        NoOfEmp.add(BarEntry(2013f,1487f))
        NoOfEmp.add(BarEntry(2014f,1501f))
        NoOfEmp.add(BarEntry(2015f,1645f))
        NoOfEmp.add(BarEntry(2016f,1578f))
        NoOfEmp.add(BarEntry(2017f,1695f))

        val year = ArrayList<Any>()

        year.add("2008")
        year.add("2009")
        year.add("2010")
        year.add("2011")
        year.add("2012")
        year.add("2013")
        year.add("2014")
        year.add("2015")
        year.add("2016")
        year.add("2017")

        val bardataset = BarDataSet(NoOfEmp, "No Of Employee")
        barChart.animateY(5000)
        val data = BarData(bardataset)
        bardataset.setColors(*ColorTemplate.COLORFUL_COLORS)
        barChart.setData(data)

    }

    private fun setPieData(dataSet: PieDataSet) {

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(16f)
        data.setValueTextColor(Color.BLACK)
        data.setValueTypeface(Typeface.SANS_SERIF)
        pieChart.data = data

        // undo all highlights
        pieChart.highlightValues(null)
        pieChart.invalidate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.bar -> dashboardViewModel.showBarGraphPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}