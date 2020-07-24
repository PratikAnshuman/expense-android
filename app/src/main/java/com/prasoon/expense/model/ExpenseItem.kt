package com.prasoon.expense.model

import androidx.room.*

@Entity(
    tableName = "expense_item",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("categoryId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class ExpenseItem(
    @PrimaryKey
    @ColumnInfo val id: Long,
    @ColumnInfo val amount: Double,
    @ColumnInfo val note: String,
    @ColumnInfo(index = true)
    val categoryId: Long
)