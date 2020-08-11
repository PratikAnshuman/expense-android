package com.prasoon.expense.ui.expense

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prasoon.expense.data.local.ExpenseRepository
import com.prasoon.expense.model.ExpenseItem
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class ExpenseListViewModel @ViewModelInject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private var categoryId: Long = 0

    private val _showToast = MutableLiveData<String>()
    val showToast = _showToast

    private val _showKeyboard = MutableLiveData<Boolean>()
    val showKeyboard = _showKeyboard

    private val _showEditExpenseDialog = MutableLiveData<ExpenseItem>()
    val showEditExpenseDialog = _showEditExpenseDialog

    private val _showEmptyAnimation = MutableLiveData<Boolean>()
    val showEmptyAnimation = _showEmptyAnimation

    private val _expenseItemList = MutableLiveData<ArrayList<ExpenseItem>>()
    val expenseItemList = _expenseItemList
    fun onFragmentLoaded(categoryId: Long) {
        this.categoryId = categoryId
        updateExpenseList()
    }

    private val _showAlert = MutableLiveData<Boolean>()
    val showAlert = _showAlert
    fun onAddExpensePressed() {
        _showAlert.value = true
    }

    fun cancelAlertPressed() {
        _expenseItemList.value = _expenseItemList.value
        _showKeyboard.value = false
        _showAlert.value = false
    }

    private val _showAddExpenseError = MutableLiveData<String>()
    val showAddExpenseError = _showAddExpenseError

    fun confirmExpensePressed(amount: String, note: String) {
        if (amount.isEmpty()) {
            _showAddExpenseError.value = "Amount Can't Be Empty"
            return
        }
        try {
            amount.toDouble().let {

                viewModelScope.launch {
                    expenseRepository.saveExpense(
                        ExpenseItem(
                            System.currentTimeMillis(),
                            it,
                            note,
                            categoryId
                        )
                    )
                    viewModelScope.launch {
                        expenseRepository.updateCategoryExpense(
                            categoryId,
                            it
                        )
                    }
                    viewModelScope.launch {
                        expenseRepository.updateBudgetExpense(it)
                    }
                    _showToast.value = "expense added successfully"
                    _showKeyboard.value = false
                    _showAlert.value = false
                    updateExpenseList()
                }
            }
        } catch (exception: NumberFormatException) {
            _showAddExpenseError.value = "Amount Must Be Numeric only"
        }
    }

    private fun updateExpenseList() {
        viewModelScope.launch {
            expenseRepository.fetchExpense(categoryId)?.let {
                _expenseItemList.value = it as ArrayList<ExpenseItem>
                _showEmptyAnimation.value = it.isEmpty()
            }
        }
    }

    fun onEditExpense(it: ExpenseItem) {
        _showEditExpenseDialog.value = it
    }

    fun updateExpensePressed(amount: String, note: String, id: Long, oldAmount: Double) {
        if (amount.isEmpty()) {
            _showAddExpenseError.value = "Amount Can't Be Empty"
            return
        }
        try {
            amount.toDouble().let {

                viewModelScope.launch {
                    expenseRepository.updateExpense(it, note, id)
                    viewModelScope.launch {
                        expenseRepository.updateCategoryExpense(
                            categoryId,
                            it - oldAmount
                        )
                    }
                    viewModelScope.launch {
                        expenseRepository.updateBudgetExpense(it - oldAmount)
                    }
                    _showToast.value = "expense updated successfully"
                    _showKeyboard.value = false
                    _showAlert.value = false
                    updateExpenseList()
                }
            }
        } catch (exception: NumberFormatException) {
            _showAddExpenseError.value = "Amount Must Be Numeric only"
        }
    }

    fun onDeleteExpense(expenseItem: ExpenseItem) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseItem.id)
            viewModelScope.launch {
                expenseRepository.updateCategoryExpense(categoryId, -1 * expenseItem.amount)
            }
            viewModelScope.launch {
                expenseRepository.updateBudgetExpense(expenseItem.amount * -1)
            }
            _showToast.value = "expense updated successfully"
            _showKeyboard.value = false
            _showAlert.value = false
            updateExpenseList()
        }
    }

}