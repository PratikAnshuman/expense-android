package com.prasoon.expense.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Budget(
    @PrimaryKey
    val id: Long,
    @ColumnInfo val budgetAmount: Double,
    @ColumnInfo val expenseAmount: Double,
    @ColumnInfo val monthName: String
) {
    @Ignore
    val balanceAmount: Double = budgetAmount - expenseAmount
}