package com.prasoon.expense.data.local

import android.content.Context
import android.util.Log
import com.prasoon.expense.data.local.dao.CategoryDao
import com.prasoon.expense.data.local.dao.ExpenseDao
import com.prasoon.expense.model.Category
import com.prasoon.expense.model.ExpenseItem


class ExpenseRepository(context: Context) {

    var database: ExpenseAppDatabase = getDatabase(context)

    private var categoryDao: CategoryDao = database.categoryDao()
    private var expenseDao: ExpenseDao = database.expenseDao()

    suspend fun saveCategory(category: Category) {
        val insertId = categoryDao.insertCategory(category)
        Log.d("insertId : ", insertId.toString())
    }

    suspend fun fetchCategory(): List<Category>? {
        return categoryDao.getAllCategory()
    }

    suspend fun deleteCategory(id: Long) {
        return categoryDao.deleteByUserId(id)
    }

    suspend fun updateTotalExpense(id: Long, total: Double) {
        return categoryDao.updateTotalById(id, total)
    }

    suspend fun fetchTotalExpense(id: Long): Double {
        return categoryDao.getTotalById(id)
    }

    suspend fun saveExpense(expense: ExpenseItem) {
        val insertId = expenseDao.insertExpense(expense)
        Log.d("insertId : ", insertId.toString())
    }

    suspend fun fetchExpense(categoryId: Long): List<ExpenseItem>? {
        return expenseDao.getAllExpense(categoryId)
    }
}