package com.prasoon.expense.ui.category

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prasoon.expense.data.local.ExpenseRepository
import com.prasoon.expense.model.Category
import kotlinx.coroutines.launch

private const val TAG = "CategoryViewModel"

class CategoryViewModel(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _showToast = MutableLiveData<String>()
    val showToast = _showToast

    private val _showKeyboard = MutableLiveData<Boolean>()
    val showKeyboard = _showKeyboard

    private val _showAlert = MutableLiveData<Boolean>()
    val showAlert = _showAlert
    fun addCategoryPressed() {
        _showAlert.value = true
    }

    private val _showAddCategoryError = MutableLiveData<String>()
    val showAddCategoryError = _showAddCategoryError
    fun confirmCategoryPressed(categoryName: String) {
        if (categoryName.isEmpty()) {
            _showAddCategoryError.value = "Name Can't Be Empty"
            return
        }
        viewModelScope.launch {
            expenseRepository.saveCategory(Category(System.currentTimeMillis(), categoryName, 0.0))
            _showToast.value = "category added successfully"
            _showKeyboard.value = false
            _showAlert.value = false
            updateCategoryList()
        }
    }

    fun cancelAlertPressed() {
        _showKeyboard.value = false
        _showAlert.value = false
    }

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

    fun onDeleteCategoryPressed(it: Long) {
        viewModelScope.launch {
            expenseRepository.deleteCategory(it)
            _showToast.value = "expense deleted successfully"
            updateCategoryList()
        }
    }


    private val _navigateToCategoryExpenseList = MutableLiveData<Category>()
    val navigateToCategoryExpenseList = _navigateToCategoryExpenseList
    fun onCategoryNameClicked(it: Category) {
        _navigateToCategoryExpenseList.value = it
    }

}