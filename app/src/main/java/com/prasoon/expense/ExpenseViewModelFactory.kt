package com.prasoon.expense

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prasoon.expense.data.local.ExpenseRepository
import com.prasoon.expense.ui.category.CategoryViewModel
import com.prasoon.expense.ui.dashboard.DashboardViewModel
import com.prasoon.expense.ui.expense.ExpenseListViewModel

class ExpenseViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = ExpenseRepository(application)
        @Suppress("UNCHECKED_CAST")
        when (modelClass.simpleName) {
            CategoryViewModel::class.simpleName -> return CategoryViewModel(repository) as T
            ExpenseListViewModel::class.simpleName -> return ExpenseListViewModel(repository) as T
            DashboardViewModel::class.simpleName -> return DashboardViewModel(repository) as T
        }
        return modelClass.newInstance()

    }

}