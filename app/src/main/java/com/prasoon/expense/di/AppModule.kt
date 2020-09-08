package com.prasoon.expense.di

import android.app.Application
import android.content.Context
import com.prasoon.expense.data.local.dao.BudgetDao
import com.prasoon.expense.data.local.dao.CategoryDao
import com.prasoon.expense.data.local.dao.ExpenseDao
import com.prasoon.expense.data.local.dao.NotificationDao
import com.prasoon.expense.data.local.getDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class) // Installs in the generate ApplicationComponent.
object AppModule {

    @Provides
    fun provideCategoryDao(@ApplicationContext context: Context): CategoryDao {
        return getDatabase(context).categoryDao()
    }

    @Provides
    fun provideExpenseDao(@ApplicationContext context: Context): ExpenseDao {
        return getDatabase(context).expenseDao()
    }

    @Provides
    fun provideBudgetDao(@ApplicationContext context: Context): BudgetDao {
        return getDatabase(context).budgetDao()
    }

    @Provides
    fun provideNotificationDao(@ApplicationContext context: Context): NotificationDao {
        return getDatabase(context).notificationDao()
    }
}