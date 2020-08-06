package com.prasoon.expense.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.prasoon.expense.data.local.dao.BudgetDao
import com.prasoon.expense.data.local.dao.CategoryDao
import com.prasoon.expense.data.local.dao.ExpenseDao
import com.prasoon.expense.model.Budget
import com.prasoon.expense.model.Category
import com.prasoon.expense.model.ExpenseItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


@Database(
    entities = [Category::class, ExpenseItem::class, Budget::class],
    version = 1,
    exportSchema = false
)
abstract class ExpenseAppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao
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
                .addCallback(rdc)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}

var rdc: RoomDatabase.Callback = object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        val defaultCategories: List<String> =
            arrayListOf("transport", "groceries", "bills", "entertainment", "shopping", "general")
        val categoryDao = INSTANCE.categoryDao()

        GlobalScope.launch {
            defaultCategories.forEach { name ->
                val cat = Category(System.currentTimeMillis(), name, 0.0)
                categoryDao.insertCategory(cat)
            }
        }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        // do something every time database is open
    }
}

