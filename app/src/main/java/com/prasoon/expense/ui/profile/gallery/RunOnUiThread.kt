package com.prasoon.expense.ui.profile.gallery

import android.content.Context
import android.util.Log
import org.jetbrains.anko.runOnUiThread


class RunOnUiThread(var context: Context?) {
    fun safely(dothis: () -> Unit) {
        context?.runOnUiThread {
            try {
                dothis.invoke()
            } catch (e: Exception) {
                Log.e("runOnUiThread", e.toString())
                e.printStackTrace()
            }
        }
    }
}