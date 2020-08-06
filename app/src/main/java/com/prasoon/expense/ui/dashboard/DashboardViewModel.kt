package com.prasoon.expense.ui.dashboard

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.prasoon.expense.R
import com.prasoon.expense.data.local.ExpenseRepository
import com.prasoon.expense.model.Category
import kotlinx.coroutines.launch


class DashboardViewModel @ViewModelInject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private var categoryList = ArrayList<Category>()

    private val _pieDataSet = MutableLiveData<PieDataSet>()
    val pieDataSet = _pieDataSet

    private val _centreText = MutableLiveData<SpannableString>()
    val centreText = _centreText

    private val _barDataSet = MutableLiveData<BarDataSet>()
    val barDataSet = _barDataSet

    fun onFragmentLoaded() {
        viewModelScope.launch {
            expenseRepository.fetchCategory()?.let {
                categoryList = it as ArrayList<Category>
                showPieChartPressed()
            }
        }
    }

    private fun setPieData(categoryList: ArrayList<Category>) {
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
        _pieDataSet.value = dataSet
    }

    fun showBarGraphPressed() {
        setBarData(categoryList)
    }

    fun showPieChartPressed() {
        generateCenterSpannableText()
        setPieData(categoryList)
    }

    private fun generateCenterSpannableText() {
        viewModelScope.launch {
            _centreText.value =
                expenseRepository.fetchBudget().let { budget ->
                    val s = SpannableString(
                        budget.monthName
                                + "\n"
                                + "\u20B9"
                                + budget.expenseAmount
                    )
                        .let {
                            it.apply {
                                setSpan(
                                    RelativeSizeSpan(1.7f),
                                    0,
                                    budget.monthName.length,
                                    0
                                )
                                setSpan(StyleSpan(Typeface.NORMAL), 0, budget.monthName.length, 0)
                                setSpan(
                                    ForegroundColorSpan(Color.GRAY),
                                    0,
                                    budget.monthName.length,
                                    0
                                )
                                setSpan(RelativeSizeSpan(2f), budget.monthName.length, it.length, 0)
                                setSpan(
                                    StyleSpan(Typeface.NORMAL),
                                    budget.monthName.length,
                                    it.length,
                                    0
                                )
                                setSpan(
                                    ForegroundColorSpan(Color.BLACK),
                                    budget.monthName.length,
                                    it.length,
                                    0
                                )
                            }
                        }
                    s
                }
        }
    }

    private fun setBarData(categoryList: ArrayList<Category>) {
        val entries = ArrayList<BarEntry>()
        var x = 0f
        categoryList.forEach { category ->
            entries.add(
                BarEntry(
                    x++,
                    category.totalExpense.toFloat()
                )
            )

            _barDataSet.value = BarDataSet(entries, "No Of Employee")
        }

    }
}