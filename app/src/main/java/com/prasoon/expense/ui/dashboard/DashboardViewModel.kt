package com.prasoon.expense.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.prasoon.expense.data.local.ExpenseRepository
import com.prasoon.expense.model.Category
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private var categoryList = ArrayList<Category>()
    private val _pieDataSet = MutableLiveData<PieDataSet>()
    val pieDataSet = _pieDataSet
    private val _barDataSet = MutableLiveData<BarDataSet?>()
    val barDataSet = _barDataSet

    fun onFragmentLoaded() {
        viewModelScope.launch {
            _pieDataSet.value = expenseRepository.fetchCategory()?.let {
                categoryList = it as ArrayList<Category>
                setPieData(categoryList)
            }
        }
    }

    private fun setPieData(categoryList: ArrayList<Category>): PieDataSet {
        val entries: ArrayList<PieEntry> = ArrayList()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        categoryList.forEach { category ->
            entries.add(
                PieEntry(
                    category.totalExpense.toFloat(),
                    category.name
                )
            )
        }
        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 1f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f

        // add a lot of colors
        val colors: ArrayList<Int> = ArrayList()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors
        return dataSet
    }

    fun showBarGraphPressed() {
        _barDataSet.value = setBarData(categoryList)
    }

    private fun setBarData(categoryList: ArrayList<Category>): BarDataSet? {
        return null
    }

}