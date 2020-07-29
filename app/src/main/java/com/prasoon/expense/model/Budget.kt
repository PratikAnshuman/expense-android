package com.prasoon.expense.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Budget(
    @PrimaryKey
    val id: Long,
    @ColumnInfo val budgetAmount: Double,
    @ColumnInfo val expenseAmount: Double,
    @ColumnInfo val monthName: String,
    @ColumnInfo val balanceAmount: Double
)