package com.prasoon.expense

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prasoon.expense.data.local.ExpenseRepository
import com.prasoon.expense.ui.category.CategoryViewModel
import com.prasoon.expense.ui.home.HomeViewModel
import com.prasoon.expense.ui.dashboard.DashboardViewModel
import com.prasoon.expense.ui.expense.ExpenseListViewModel

class ExpenseViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = ExpenseRepository(application)
        @Suppress("UNCHECKED_CAST")
        when (modelClass.simpleName) {
            HomeViewModel::class.simpleName -> return HomeViewModel(repository) as T
            ExpenseListViewModel::class.simpleName -> return ExpenseListViewModel(repository) as T
            DashboardViewModel::class.simpleName -> return DashboardViewModel(repository) as T
            CategoryViewModel::class.simpleName -> return CategoryViewModel(repository) as T
        }
        return modelClass.newInstance()

    }

}