package com.prasoon.expense.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prasoon.expense.data.local.ExpenseRepository
import com.prasoon.expense.model.Category
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _categoryList = MutableLiveData<ArrayList<Category>>()
    val categoryList = _categoryList
    fun onFragmentLoaded() {
        updateCategoryList()
    }

    private fun updateCategoryList() {
        viewModelScope.launch {
            expenseRepository.fetchCategory()?.let {
                _categoryList.value = it as ArrayList<Category>
            }
        }
    }

    private val _navigateToCategoryExpenseList = MutableLiveData<Category>()
    val navigateToCategoryExpenseList = _navigateToCategoryExpenseList
    fun onCategoryNameClicked(it: Category) {
        _navigateToCategoryExpenseList.value = it
    }

}