package com.prasoon.expense.ui.notifications

import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prasoon.expense.data.local.ExpenseRepository
import com.prasoon.expense.model.Sms
import com.prasoon.expense.utils.event
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

//private const val TAG = "NotificationsViewModel"

class NotificationsViewModel @ViewModelInject constructor(
    @ApplicationContext val application: Context,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _messageList = MutableLiveData<List<Sms>>()
    val messageList = _messageList.event()

    fun onPermissionGranted() {
        showNotificationList()
//        viewModelScope.launch {
//            loadNotification(expenseRepository.fetchBudget().lastSyncTime)
//        }
    }

    private val _checkPermission = MutableLiveData<Unit>()
    val checkPermission = _checkPermission.event()

    fun onViewCreated() {
        showNotificationList()
        updateNotificationCount()
        _checkPermission.value = Unit
    }

    private fun updateNotificationCount() {
        viewModelScope.launch {
            expenseRepository.updateBudgetNotificationCount(
                0,
                expenseRepository.fetchBudget().id
            )
        }
    }

    private fun showNotificationList() {
        viewModelScope.launch {
            _messageList.value = expenseRepository.fetchNotifications()
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
            viewModelScope.launch {
                expenseRepository.saveNotifications(smsList)
                showNotificationList()
            }
        }
        updateLastSyncTime()
    }

    private fun updateLastSyncTime() {
        viewModelScope.launch {
            expenseRepository.updateLastSync()
        }
    }
}
