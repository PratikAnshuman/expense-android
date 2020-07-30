package com.prasoon.expense.data.local

import android.content.Context
import android.util.Log
import com.prasoon.expense.data.local.dao.BudgetDao
import com.prasoon.expense.data.local.dao.CategoryDao
import com.prasoon.expense.data.local.dao.ExpenseDao
import com.prasoon.expense.model.Budget
import com.prasoon.expense.model.Category
import com.prasoon.expense.model.ExpenseItem
import com.prasoon.expense.utils.getMonth
import java.lang.IndexOutOfBoundsException
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.util.*


class ExpenseRepository(context: Context) {

    private val database: ExpenseAppDatabase = getDatabase(context)

    private var categoryDao: CategoryDao = database.categoryDao()
    private var expenseDao: ExpenseDao = database.expenseDao()
    private var budgetDao: BudgetDao = database.budgetDao()

    suspend fun saveCategory(category: Category) {
        val insertId = categoryDao.insertCategory(category)
        Log.d("insertId : ", insertId.toString())
    }

    suspend fun updateCategory(name: String, id: Long) {
        categoryDao.updateCategory(id, name)
    }

    suspend fun fetchCategory(): List<Category>? {
        return categoryDao.getAllCategory()
    }

    suspend fun deleteCategory(id: Long) {
        return categoryDao.deleteByUserId(id)
    }

    suspend fun updateCategoryExpense(id: Long, total: Double) {
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

    private suspend fun saveBudget(budget: Budget) {
        val insertId = budgetDao.insertBudget(budget)
        Log.d("insertId : ", insertId.toString())
    }

    suspend fun fetchBudget(): Budget {
        return try {
            budgetDao.getBudget()[0]
        } catch (e: IndexOutOfBoundsException) {
            val budget = Budget(
                System.currentTimeMillis(),
                0.0,
                0.0,
                Calendar.getInstance().getMonth()
            )
            saveBudget(budget)
            budget
        }
    }

    suspend fun updateBudget(amount: Double, id: Long) {
        budgetDao.updateBudget(id, amount)
    }

    suspend fun updateBudgetExpense(it: Double) {
        val budget = fetchBudget()
        budgetDao.updateBudgetExpense(
            budget.id,
            budget.expenseAmount + it
        )
    }

    suspend fun updateExpense(amount: Double, note: String, expenseId: Long) {
        expenseDao.updateExpenseById(amount, note, expenseId)
    }

}