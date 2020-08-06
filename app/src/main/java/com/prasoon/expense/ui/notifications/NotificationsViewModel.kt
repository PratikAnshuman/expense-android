package com.prasoon.expense.ui.notifications

import android.database.Cursor
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prasoon.expense.model.Sms
import com.prasoon.expense.utils.getTodayStartTime
import java.util.*

class NotificationsViewModel @ViewModelInject constructor() : ViewModel() {

    private val _notificationsList = MutableLiveData<List<Sms>>()
    val notificationsList: LiveData<List<Sms>>
        get() = _notificationsList

    private val _createMessageCursor = MutableLiveData<String>()
    val createMessageCursor: LiveData<String>
        get() = _createMessageCursor

    fun onPermissionGranted() {
        _createMessageCursor.value = "date>=" + getTodayStartTime()
    }

    private val _checkPermission = MutableLiveData<Unit>()
    val checkPermission: LiveData<Unit>
        get() = _checkPermission

    fun onViewCreated() {
        _checkPermission.value = Unit
    }

    fun onCursorCreated(cursor: Cursor?) {
        val smsList = ArrayList<Sms>()

        cursor?.let {
            if (it.moveToFirst()) { // must check the result to prevent exception
                do {
                    val body = it.getString(it.getColumnIndexOrThrow("body"))
                    if ((body.contains("debited") || body.contains("Paid")) && body.contains("Rs.")) {
                        val address = it.getString(it.getColumnIndexOrThrow("address"))
                        val time = it.getString(it.getColumnIndexOrThrow("date"))
                        val sms = Sms(address, time, body)
                        smsList.add(sms)
                    }
                } while (it.moveToNext())
            }
        }
        cursor?.close()
        _notificationsList.value = smsList
    }

}