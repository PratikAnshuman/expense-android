package com.prasoon.expense.utils

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


fun View.showKeyboard() {
    this.requestFocus()
    val imm: InputMethodManager? =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.hideKeyboard() {
    Log.d("hide keyboard", "called")
    if (this.isFocused) {
        this.clearFocus()
    }
    val imm: InputMethodManager? =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(this.windowToken, 0);
}

fun Fragment.showToast(message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

fun Long.convertLongToTime(): String {
    val date = Date(this)
    if (DateUtils.isToday(this)) {
        val format = SimpleDateFormat("HH:mm")
        return format.format(date)
    } else {
        val format = SimpleDateFormat("dd MMM HH:mm")
        return format.format(date)
    }
}

fun Calendar.getMonth(): String {
    return this.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: "January"

}
