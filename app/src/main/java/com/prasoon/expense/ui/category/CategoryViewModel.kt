package com.prasoon.expense.ui.category

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prasoon.expense.data.local.ExpenseRepository
import com.prasoon.expense.model.Category
import com.prasoon.expense.utils.event
import kotlinx.coroutines.launch

//private const val TAG = "CategoryViewModel"

class CategoryViewModel @ViewModelInject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _showEditDialog = MutableLiveData<Category>()
    val showEditDialog = _showEditDialog.event()

    private val _showToast = MutableLiveData<String>()
    val showToast = _showToast.event()

    private val _showKeyboard = MutableLiveData<Boolean>()
    val showKeyboard = _showKeyboard.event()

    private val _showEmptyAnimation = MutableLiveData<Boolean>()
    val showEmptyAnimation = _showEmptyAnimation.event()

    private val _showAlert = MutableLiveData<Boolean>()
    val showAlert = _showAlert.event()
    fun onAddCategory() {
        _showAlert.value = true
    }

    private val _showAddCategoryError = MutableLiveData<String>()
    val showAddCategoryError = _showAddCategoryError.event()
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

    fun onCancelDialog() {
        _categoryList.value = _categoryList.value
        _showKeyboard.value = false
        _showAlert.value = false
    }

    private val _categoryList = MutableLiveData<ArrayList<Category>>()
    val categoryList = _categoryList.event()
    fun onFragmentLoaded() {
        updateCategoryList()
    }

    private fun updateCategoryList() {
        viewModelScope.launch {
            expenseRepository.fetchCategory()?.let {
                _categoryList.value = it as ArrayList<Category>
                _showEmptyAnimation.value = it.isEmpty()
            }
        }
    }

    fun onEditCategoryPressed(it: Category) {
        _showEditDialog.value = it
    }

    private val _navigateToCategoryExpenseList = MutableLiveData<Category>()
    val navigateToCategoryExpenseList = _navigateToCategoryExpenseList.event()

    private val _navigateToNotification = MutableLiveData<Pair<Long, Long>>()
    val navigateToNotification = _navigateToNotification.event()

    fun onCategoryNameClicked(it: Category) {
        if (_chooseCategoryForNotification) {
            _navigateToNotification.value = Pair(it.id, _notificationId)
        } else {
            _navigateToCategoryExpenseList.value = it
        }
    }

    fun onDeleteCategory(category: Category) {
        viewModelScope.launch {
            expenseRepository.deleteCategory(category.id)
            viewModelScope.launch {
                expenseRepository.updateBudgetExpense(-1 * category.totalExpense)
            }
            _showToast.value = "category deleted successfully"
            onCancelDialog()
            updateCategoryList()
        }
    }

    fun onUpdateCategory(name: String, id: Long) {
        if (name.isEmpty()) {
            _showAddCategoryError.value = "Name Can't Be Empty"
            return
        }
        viewModelScope.launch {
            expenseRepository.updateCategory(name, id)
            _showToast.value = "category updated successfully"
            onCancelDialog()
            updateCategoryList()
        }
    }

    private var _chooseCategoryForNotification = false
    private var _notificationId = -1L
    fun isFromNotification(notificationId: Long) {
        _chooseCategoryForNotification = true
        _notificationId = notificationId
    }

}