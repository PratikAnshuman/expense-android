package com.prasoon.expense.utils

import android.Manifest
import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.prasoon.expense.R
import com.prasoon.expense.model.Category
import com.prasoon.expense.ui.PermissionsView
import java.text.Format
import java.text.NumberFormat
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
    imm?.hideSoftInputFromWindow(this.windowToken, 0)
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
    return if (DateUtils.isToday(this)) {
        val format = SimpleDateFormat("HH:mm", Locale.ROOT)
        format.format(date)
    } else {
        val format = SimpleDateFormat("dd MMM HH:mm", Locale.ROOT)
        format.format(date)
    }
}

fun Calendar.getMonth(): String {
    return this.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: "January"
}

fun getTodayStartTime(): Long {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

fun TextView.setAmount(f: Double) {
    val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
    this.text = format.format(f)
}

fun <T> LiveData<T>.event() = this.map { Event(it) }

/** Uses `Transformations.map` on a LiveData */
fun <X, Y> LiveData<X>.map(body: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, body)
}

fun PermissionsView.Permission.getPermission(): String {
    return when (this) {
        PermissionsView.Permission.READ_SMS -> Manifest.permission.READ_SMS
    }
}

fun Category.getIcon(): Int {
    return when (this.name) {
        "transport" -> R.drawable.ic_baseline_commute_24
        "groceries" -> R.drawable.ic_baseline_local_grocery_store_24
        "bills" -> R.drawable.ic_baseline_monetization_on_24
        "entertainment" -> R.drawable.ic_baseline_movie_filter_24
        "shopping" -> R.drawable.ic_baseline_shopping_basket_24
        "general" -> R.drawable.ic_baseline_store_24
        else -> R.drawable.ic_baseline_store_24
    }
}
