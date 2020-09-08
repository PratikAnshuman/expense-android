package com.prasoon.expense.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prasoon.expense.data.local.ExpenseRepository
import com.prasoon.expense.model.Budget
import com.prasoon.expense.model.Category
import kotlinx.coroutines.launch
import androidx.hilt.lifecycle.ViewModelInject
import com.prasoon.expense.model.Sms
import com.prasoon.expense.utils.event
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.NumberFormatException

private const val TAG = "HomeViewModel"

class HomeViewModel @ViewModelInject constructor(
    @ApplicationContext val application: Context,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _showToast = MutableLiveData<String>()
    val showToast = _showToast.event()

    private val _budgetAmount = MutableLiveData<String>()
    val budgetAmount = _budgetAmount.event()

    private val _currentBudget = MutableLiveData<Budget>()
    val currentBudget = _currentBudget.event()

    private val _showKeyboard = MutableLiveData<Boolean>()
    val showKeyboard = _showKeyboard.event()

    private val _showEmptyAnimation = MutableLiveData<Boolean>()
    val showEmptyAnimation = _showEmptyAnimation.event()

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog = _showDialog.event()
    fun onBudgetPressed() {
        _showDialog.value = true
    }

    private val _categoryList = MutableLiveData<ArrayList<Category>>()
    val categoryList = _categoryList.event()
    fun onFragmentLoaded() {
        fetchCurrentBudget()
        updateCategoryList()
    }

    private val _notificationCount = MutableLiveData<Int>()
    val notificationCount = _notificationCount

    private fun updateNotificationBadge() {
        showNotificationCount()
        if (ContextCompat.checkSelfPermission(application, Manifest.permission.READ_SMS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            viewModelScope.launch {
                loadNotification(expenseRepository.fetchBudget().lastSyncTime)
            }
        }
    }

    private fun loadNotification(lastSyncTime: Long) {
        val smsList = ArrayList<Sms>()
        val selection = "date>=$lastSyncTime"
        val cursor: Cursor? =
            application.contentResolver?.query(
                Uri.parse("content://sms/inbox"),
                null,
                selection,
                null,
                null
            )
        cursor?.let {
            if (it.moveToFirst()) { // must check the result to prevent exception
                do {
                    val body = it.getString(it.getColumnIndexOrThrow("body"))
                    if (
                        (body.contains("debited") || body.contains("Paid") || body.contains("sent")) &&
                        (body.contains("Rs.") || body.contains("INR"))
                    ) {
                        val address = it.getString(it.getColumnIndexOrThrow("address"))
                        val time = it.getString(it.getColumnIndexOrThrow("date"))
                        val sms = Sms(System.currentTimeMillis(), address, time, body)
                        smsList.add(sms)
                    }
                } while (it.moveToNext())
            }
        }
        cursor?.close()
        if (smsList.isNotEmpty()) {
            Log.i(TAG, smsList.size.toString())
            updateNotificationCount(smsList.size)
            viewModelScope.launch {
                expenseRepository.saveNotifications(smsList)
            }
        }
        updateLastSyncTime()
    }

    private fun updateNotificationCount(size: Int) {
        currentBudget.value?.peekContent()?.let {

            viewModelScope.launch {
                expenseRepository.updateBudgetNotificationCount(
                    it.notificationCount + size,
                    it.id
                )
                showNotificationCount()
            }
        }
    }

    private fun showNotificationCount() {
        viewModelScope.launch {
            expenseRepository.fetchBudgetNotificationCount().let {
                if (it > 0) {
                    _notificationCount.value = it
                }
            }
        }
    }

    private fun updateLastSyncTime() {
        viewModelScope.launch {
            expenseRepository.updateLastSync()
        }
    }

    private fun fetchCurrentBudget() {
        viewModelScope.launch {
            expenseRepository.fetchBudget().let {
                _currentBudget.value = it
                updateNotificationBadge()
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
    val showUpdateBudgetError = _showUpdateBudgetError.event()
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
                        currentBudget.value!!.peekContent().id
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
