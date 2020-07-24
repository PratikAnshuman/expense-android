package com.prasoon.expense.data.local

import android.content.Context
import androidx.room.*
import com.prasoon.expense.data.local.dao.CategoryDao
import com.prasoon.expense.data.local.dao.ExpenseDao
import com.prasoon.expense.model.Category
import com.prasoon.expense.model.ExpenseItem


@Database(entities = [Category::class,ExpenseItem::class], version = 2, exportSchema = false)
abstract class ExpenseAppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

}

private lateinit var INSTANCE: ExpenseAppDatabase

fun getDatabase(context: Context): ExpenseAppDatabase {
    synchronized(ExpenseAppDatabase::class) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                .databaseBuilder(
                    context.applicationContext,
                    ExpenseAppDatabase::class.java,
                    "expense_db"
                )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}

