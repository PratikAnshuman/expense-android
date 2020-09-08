package com.prasoon.expense.data.local

import android.util.Log
import com.prasoon.expense.data.local.dao.BudgetDao
import com.prasoon.expense.data.local.dao.CategoryDao
import com.prasoon.expense.data.local.dao.ExpenseDao
import com.prasoon.expense.data.local.dao.NotificationDao
import com.prasoon.expense.model.*
import com.prasoon.expense.utils.getMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IndexOutOfBoundsException
import java.util.*
import javax.inject.Inject


class ExpenseRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao,
    private val notificationDao: NotificationDao
) {

    suspend fun saveCategory(category: Category) =
        withContext(Dispatchers.IO) {
            categoryDao.insertCategory(category)
        }

    suspend fun updateCategory(name: String, id: Long) =
        withContext(Dispatchers.IO) {
            categoryDao.updateCategory(id, name)
        }

    suspend fun fetchCategory(): List<Category>? =
        withContext(Dispatchers.IO) {
            return@withContext categoryDao.getAllCategory()
        }

    suspend fun deleteCategory(id: Long) =
        withContext(Dispatchers.IO) {
            return@withContext categoryDao.deleteByUserId(id)
        }

    suspend fun updateCategoryExpense(id: Long, amount: Double) =
        withContext(Dispatchers.IO) {
            val catExpense = fetchTotalExpense(id)
            return@withContext categoryDao.updateTotalById(id, catExpense + amount)
        }

    private suspend fun fetchTotalExpense(id: Long): Double =
        withContext(Dispatchers.IO) {
            return@withContext categoryDao.getTotalById(id)
        }

    suspend fun saveExpense(expense: ExpenseItem) =
        withContext(Dispatchers.IO) {
            expenseDao.insertExpense(expense)
        }

    suspend fun fetchExpense(categoryId: Long): List<ExpenseItem>? =
        withContext(Dispatchers.IO) {
            return@withContext expenseDao.getAllExpense(categoryId)
        }

    private suspend fun saveBudget(budget: Budget) =
        withContext(Dispatchers.IO) {
            budgetDao.insertBudget(budget)
        }

    suspend fun fetchBudget(): Budget =
        withContext(Dispatchers.IO) {
            return@withContext try {
                budgetDao.getBudget()[0]
            } catch (e: IndexOutOfBoundsException) {
                val budget = Budget(
                    System.currentTimeMillis(),
                    0.0,
                    0.0,
                    Calendar.getInstance().getMonth(),
                    System.currentTimeMillis(),
                    0
                )
                saveBudget(budget)
                budget
            }
        }

    suspend fun updateBudget(amount: Double, id: Long) =
        withContext(Dispatchers.IO) {
            budgetDao.updateBudget(id, amount)
        }

    suspend fun updateBudgetExpense(it: Double) =
        withContext(Dispatchers.IO) {
            val budget = fetchBudget()
            budgetDao.updateBudgetExpense(
                budget.id,
                budget.expenseAmount + it
            )
        }

    suspend fun updateExpense(amount: Double, note: String, expenseId: Long) =
        withContext(Dispatchers.IO) {
            expenseDao.updateExpenseById(amount, note, expenseId)
        }

    suspend fun deleteExpense(id: Long) =
        withContext(Dispatchers.IO) {
            expenseDao.deleteExpenseById(id)
        }

    suspend fun updateLastSync() =
        withContext(Dispatchers.IO) {
            budgetDao.updateBudgetLastSync(fetchBudget().id, System.currentTimeMillis())
        }

    suspend fun fetchNotifications(): List<Sms> =
        withContext(Dispatchers.IO) {
            return@withContext notificationDao.getNotifications()
        }

    suspend fun saveNotifications(smsList: List<Sms>) =
        withContext(Dispatchers.IO) {
            notificationDao.insertNotificationList(smsList)
        }

    suspend fun fetchBudgetNotificationCount(): Int =
        withContext(Dispatchers.IO) {
            val count = fetchBudget().notificationCount
            Log.i("notification", count.toString())
            return@withContext count
        }

    suspend fun updateBudgetNotificationCount(count: Int, id: Long) =
        withContext(Dispatchers.IO) {
            budgetDao.updateBudgetNotificationCount(id, count)
        }

    suspend fun getNotificationById(id: Long) =
        withContext(Dispatchers.IO) {
            return@withContext notificationDao.getNotificationById(id)
        }

    suspend fun deleteNotification(notificationId: Long) {
        withContext(Dispatchers.IO) {
            notificationDao.deleteNotificationById(notificationId)
        }
    }
}