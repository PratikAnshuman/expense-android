package com.prasoon.expense.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_item")
data class Category(
    @PrimaryKey
    @ColumnInfo val id: Long,
    @ColumnInfo val name: String,
    @ColumnInfo val totalExpense: Double
)
