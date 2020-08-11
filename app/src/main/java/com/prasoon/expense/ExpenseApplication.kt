package com.prasoon.expense

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Singleton

@HiltAndroidApp
@Singleton
class ExpenseApplication : Application() {
}
