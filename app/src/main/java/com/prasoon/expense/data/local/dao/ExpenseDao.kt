package com.prasoon.expense.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prasoon.expense.model.ExpenseItem

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseItem): Long

    @Query("SELECT * FROM expense_item WHERE categoryId = :categoryId ORDER BY id DESC")
    suspend fun getAllExpense(categoryId: Long): List<ExpenseItem>

    @Query("DELETE FROM expense_item WHERE id = :id")
    suspend fun deleteByUserId(id: Long)
}