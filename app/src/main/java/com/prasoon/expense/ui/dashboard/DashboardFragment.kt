package com.prasoon.expense.ui.dashboard

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.prasoon.expense.MainActivity
import com.prasoon.expense.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_dashboard.*

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.graph_options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).toolbar.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        dashboardViewModel.onFragmentLoaded()

        dashboardViewModel.pieDataSet.observe(viewLifecycleOwner, Observer {
            pieChart.visibility = View.VISIBLE
            barChart.visibility = View.GONE

            setPieData(it)
        })

        dashboardViewModel.centreText.observe(viewLifecycleOwner, Observer {
            pieChart.centerText = it

        })

        dashboardViewModel.barDataSet.observe(viewLifecycleOwner, Observer {
            barChart.visibility = View.VISIBLE
            pieChart.visibility = View.GONE

            setBarData(it)
        })
    }

    private fun setBarData(barDataSet: BarDataSet) {

        barChart.animateY(5000)
        val data = BarData(barDataSet)
        barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        barChart.data = data

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
            R.id.pie -> dashboardViewModel.showPieChartPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}