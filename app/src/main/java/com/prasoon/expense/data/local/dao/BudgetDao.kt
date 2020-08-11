package com.prasoon.expense.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prasoon.expense.model.Budget

@Dao
interface BudgetDao {

    @Query("UPDATE budget SET budgetAmount = :amount WHERE id = :id")
    suspend fun updateBudget(id: Long, amount: Double)

    @Query("UPDATE budget SET expenseAmount = :amount WHERE id = :id")
    suspend fun updateBudgetExpense(id: Long, amount: Double)

    @Query("UPDATE budget SET lastSyncTime = :time WHERE id = :id")
    suspend fun updateBudgetLastSync(id: Long, time: Long)

    @Query("SELECT * FROM budget ORDER BY id DESC")
    suspend fun getBudget(): List<Budget>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget): Long

}