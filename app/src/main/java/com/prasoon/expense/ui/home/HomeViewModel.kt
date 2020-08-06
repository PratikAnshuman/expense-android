package com.prasoon.expense.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prasoon.expense.data.local.ExpenseRepository
import com.prasoon.expense.model.Budget
import com.prasoon.expense.model.Category
import kotlinx.coroutines.launch
import androidx.hilt.lifecycle.ViewModelInject
import com.prasoon.expense.utils.event
import java.lang.NumberFormatException

private const val TAG = "HomeViewModel"

class HomeViewModel @ViewModelInject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _showToast = MutableLiveData<String>()
    val showToast = _showToast

    private val _budgetAmount = MutableLiveData<String>()
    val budgetAmount = _budgetAmount

    private val _currentBudget = MutableLiveData<Budget>()
    val currentBudget: LiveData<Budget>
        get() = _currentBudget

    private val _showKeyboard = MutableLiveData<Boolean>()
    val showKeyboard = _showKeyboard

    private val _showEmptyAnimation = MutableLiveData<Boolean>()
    val showEmptyAnimation = _showEmptyAnimation

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog = _showDialog
    fun onSetBudget() {
        _showDialog.value = true
    }

    private val _categoryList = MutableLiveData<ArrayList<Category>>()
    val categoryList = _categoryList
    fun onFragmentLoaded() {
        fetchCurrentBudget()
        updateCategoryList()
    }

    private fun fetchCurrentBudget() {
        viewModelScope.launch {
            expenseRepository.fetchBudget().let {
                _currentBudget.value = it
            }
        }
    }

    private fun updateCategoryList() {
        viewModelScope.launch {
            expenseRepository.fetchCategory()?.let {
                _categoryList.value = it as ArrayList<Category>
                Log.i(TAG, it.toString())
                _showEmptyAnimation.value = it.isEmpty()
            }
        }
    }

    private val _navigateToCategoryExpenseList = MutableLiveData<Category>()
    val navigateToCategoryExpenseList = _navigateToCategoryExpenseList.event()
    fun onCategoryNameClicked(it: Category) {
        _navigateToCategoryExpenseList.value = it
    }

    fun onCancelDialog() {
        _showKeyboard.value = false
        _showDialog.value = false
    }

    private val _showUpdateBudgetError = MutableLiveData<String>()
    val showUpdateBudgetError = _showUpdateBudgetError
    fun onUpdateBudget(amount: String) {
        if (amount.isEmpty()) {
            _showUpdateBudgetError.value = "Budget Amount Can't Be Empty"
            return
        }
        try {
            amount.toDouble().let {

                viewModelScope.launch {
                    expenseRepository.updateBudget(
                        it,
                        currentBudget.value!!.id
                    )
                    _showToast.value = "budget updated successfully"
                    _showKeyboard.value = false
                    _showDialog.value = false
                    fetchCurrentBudget()
                }
            }
        } catch (e: NumberFormatException) {
            _showUpdateBudgetError.value = "Amount Must Be Numeric only"
        }
    }
}
